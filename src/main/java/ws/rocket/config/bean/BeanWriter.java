/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.rocket.config.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ws.rocket.config.Messages;
import ws.rocket.config.section.SectionValueException;
import ws.rocket.config.section.value.ValueConverter;

/**
 * Takes care of updating the target bean instance. The bean instance is created together with the writer and can be
 * accessed via {@link #getBean()}.
 *
 * @param <T> The main bean type handled by this bean handler (see #getBean()).
 * 
 * @author Martti Tamm
 */
public final class BeanWriter<T> {

  /**
   * Creates a new instance of bean and returns a writer for updating it.
   * 
   * @param <T> The created bean type.
   * @param beanType Bean type information.
   * @param valueConverter Converter for non-String property values.
   * @param msgs Messages container for logging errors.
   * @return The created bean writer or <code>null</code> when bean instance could not be created (check messages then).
   */
  public static <T> BeanWriter<T> create(BeanType<T> beanType, ValueConverter valueConverter, Messages msgs) {
    BeanWriter<T> handler = null;
    Class<T> type = beanType.getBeanClass();
    try {
      handler = new BeanWriter<T>(type.newInstance(), beanType, valueConverter, msgs);
    } catch (Exception e) {
      msgs.addError("Could not create instance of " + type.getName() + " using default constructor: " + e);
    }
    return handler;
  }

  private final T bean;

  private final BeanType type;

  private final Messages msgs;

  private final ValueConverter converter;

  private BeanWriter(T bean, BeanType beanType, ValueConverter valueConverter, Messages msgs) {
    this.bean = bean;
    this.type = beanType;
    this.converter = valueConverter;
    this.msgs = msgs;
  }

  /**
   * Provides the bean instance that this writer is working with.
   * 
   * @return A bean instance.
   */
  public T getBean() {
    return this.bean;
  }

  /**
   * Provides the value converter used by this writer.
   * 
   * @return A value converter.
   */
  public ValueConverter getConverter() {
    return this.converter;
  }

  /**
   * Writes a <code>value</code> to the <code>property</code> of the target bean. Null values won't be written.
   * <p>
   * For a map or list value, the setter must accept <code>java.util.Map</code> or <code>java.util.List</code>
   * correspondingly. (Therefore, subtypes of these interfaces are not allowed.)
   * <p>
   * In case of a list value together with <code>mainValueType</code>, the setter may also accept an array of
   * <code>mainValueType</code> (however, this method looks for <code>List</code> parameter first).
   * <p>
   * If the value is <code>String</code> and no setter for that type is not found, this method also attempts to convert
   * the string value to another parameter type that is available for this property.
   * <p>
   * Writing errors will be logged to the messages container.
   * 
   * @param property The target property name (required).
   * @param value The value to write.
   * @param mainValueType (Optional) The collection value type to determine required array component type.
   */
  public void setProperty(String property, Object value, Class<?> mainValueType) {
    if (value == null) {
      return;
    }

    Method m = null;

    if (value instanceof Map) {
      m = this.type.getSetterMethod(property, Map.class);

    } else if (value instanceof List) {
      m = this.type.getSetterMethod(property, List.class);

      // Fallback to array:
      if (m == null && mainValueType != null) {
        m = this.type.getSetterMethodWithArray(property, mainValueType);
        List<?> list = (List<?>) value;
        value = list.toArray((Object[]) Array.newInstance(mainValueType, list.size()));
      }
    } else {
      List<Method> setters = this.type.getSetterMethods(property);

      // First attempt exact parameter type match
      for (Method method : setters) {
        if (method.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
          m = method;
          break;
        }
      }

      for (Method method : setters) {
        if (m == null && value instanceof String) {
          Class<?> paramType = method.getParameterTypes()[0];
          try {
            value = this.converter.convert((String) value, paramType);
            m = method;
            break;
          } catch (SectionValueException e) {
            this.msgs.addWarning("Tried to convert value '" + value + "' to " + paramType + " but failed: "
                + e.getMessage());
          }
        }
      }
    }

    if (m == null) {
      if (value instanceof List && mainValueType != null) {
        addError("Property '" + property + "' (List or " + mainValueType.getName() + "[]) is not writable");
      } else if (value instanceof String) {
        addError("Property '" + property + "' (" + value.getClass().getName()
            + ") is not writable or conversion to another parameter type failed");
      } else {
        addError("Property '" + property + "' (" + value.getClass().getName() + ") is not writable");
      }
    } else {
      try {
        m.invoke(this.bean, value);
      } catch (Exception e) {
        addError("Could not call method " + m + ": " + e.getMessage());
      }
    }
  }

  /**
   * Constructs an instance of given <code>type</code> by calling its constructor that has as many parameters as is the
   * length of the array <code>paramName</code>. When such constructor is found, the parameters will be evaluated by
   * replacing parameter names with corresponding values from the <code>values</code> map.
   * <p>
   * This method avoids exceptions, and tries to log all problems in the messages container.
   * 
   * @param type The class to instantiate.
   * @param paramNames The constructor parameter names to resolve into values from the map.
   * @param values A map containing (all or some) values for the constructor.
   * @return The created instance or null when the class could not be instantiated.
   */
  public Object construct(Class<?> type, String[] paramNames, Map<String, String> values) {
    Object result = null;
    List<Constructor> constructors = BeanType.getConstructors(type, paramNames.length);

    for (Constructor constr : constructors) {
      try {
        result = attempConstruct(constr, paramNames, values);
        if (result != null) {
          break;
        }
      } catch (SectionValueException e) {
        this.msgs.addWarning("Tried to create an instance of class by calling " + constr
            + " but failed to convert a value to target type: " + e);
      }
    }

    if (result == null) {
      if (constructors.isEmpty()) {
        addError("Did not find accessible constructors to create instance of " + type.getName()
            + " using parameters " + Arrays.toString(paramNames));
      } else {
        addError("Found " + constructors.size()
            + " accessible constructors but still failed to create instance of " + type.getName()
            + " using parameters " + Arrays.toString(paramNames));
      }
    }

    return result;
  }

  private Object attempConstruct(Constructor constr, String[] paramNames, Map<String, String> values)
      throws SectionValueException {

    try {
      if (constr.isAccessible()) {
        Object[] params = resolveParams(constr.getParameterTypes(), paramNames, values);
        return params != null ? constr.newInstance(params) : null;
      }
    } catch (InstantiationException ex) {
      throw new SectionValueException(ex.toString());
    } catch (IllegalAccessException ex) {
      throw new SectionValueException(ex.toString());
    } catch (IllegalArgumentException ex) {
      throw new SectionValueException(ex.toString());
    } catch (InvocationTargetException ex) {
      throw new SectionValueException(ex.toString());
    }
    return null;
  }

  private Object[] resolveParams(Class[] paramTypes, String[] paramNames, Map<String, String> values)
      throws SectionValueException {

    Object[] result = null;

    if (paramTypes.length == paramNames.length) {
      result = new Object[paramNames.length];

      for (int i = 0; i < paramNames.length; i++) {
        result[i] = this.converter.convert(values.get(paramNames[i]), paramTypes[i]);
      }
    }

    return result;
  }

  private void addError(String msg) {
    this.msgs.addError(msg);
  }
}
