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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for a Java bean type introspection.
 *
 * @param <T> The bean type.
 * @author Martti Tamm
 */
public final class BeanType<T> {

  private final Class<T> type;

  /**
   * Creates a new instance for working with given class. This class does not strictly check whether the target type is
   * a class, an abstract class, enumeration, or an interface.
   * 
   * @param type The targeted bean type.
   */
  public BeanType(Class<T> type) {
    if (type == null) {
      throw new NullPointerException("Got null for bean type");
    }
    this.type = type;
  }

  /**
   * Provides the class of the underlying bean.
   * 
   * @return The class used.
   */
  public Class<T> getBeanClass() {
    return this.type;
  }

  /**
   * Looks up a setter method for <code>property</code> to accept a parameter of given type.
   * 
   * @param property The Java bean property name.
   * @param paramType The required parameter type for setter method.
   * @return The found setter method or <code>null</code>.
   */
  public Method getSetterMethod(String property, Class<?> paramType) {
    String methodName = getSetterMethodName(property);
    Method method;

    try {
      method = this.type.getMethod(methodName, paramType);
    } catch (NoSuchMethodException e) {
      method = null;
    } catch (SecurityException e) {
      method = null;
    }

    return method;
  }

  /**
   * Looks up a setter method for <code>property</code> to accept an array of given type as its parameter.
   * 
   * @param property The Java bean property name.
   * @param arrayCompType The required array component type.
   * @return The found setter method or <code>null</code>.
   */
  public Method getSetterMethodWithArray(String property, Class<?> arrayCompType) {
    Method result = null;

    for (Method method : getSetterMethods(property)) {
      Class<?> paramType = method.getParameterTypes()[0];

      if (paramType.isArray() && arrayCompType.equals(paramType.getComponentType())) {
        result = method;
        break;
      }
    }

    return result;
  }

  /**
   * Looks up all setter methods for <code>property</code> that accept exactly one parameter.
   * 
   * @param property The Java bean property name.
   * @return A list with found setter methods.
   */
  public List<Method> getSetterMethods(String property) {
    String methodName = getSetterMethodName(property);
    List<Method> result = new ArrayList<Method>(2);

    for (Method method : this.type.getMethods()) {
      if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
        result.add(method);
      }
    }

    return result;
  }

  /**
   * Looks up a Java type for given <code>property</code> that has a constructor with
   * <code>typeConstrParamCount</code> parameters.
   * 
   * @param property The Java bean property name.
   * @param typeConstrParamCount The required amount of parameters for property type constructor.
   * @return The first found property type that has a constructor with required amount of parameters.
   */
  public Class<?> getPropertyType(String property, int typeConstrParamCount) {
    List<Method> methods = getSetterMethods(property);
    Class<?> result = null;

    for (Method method : methods) {
      Class<?> paramType = method.getParameterTypes()[0];

      if (!getConstructors(paramType, typeConstrParamCount).isEmpty()) {
        result = paramType;
        break;
      }
    }

    return result;
  }

  /**
   * Looks up all constructors on current type that have <code>paramCount</code> parameters.
   * 
   * @param paramCount The required amount of parameters.
   * @return A list containing all found constructors.
   */
  public List<Constructor> getConstructors(int paramCount) {
    return getConstructors(this.type, paramCount);
  }

  /**
   * Looks up all constructors of given <code>type</code> that have <code>paramCount</code> parameters.
   * 
   * @param type The class type.
   * @param paramCount The required amount of parameters.
   * @return A list containing all found constructors.
   */
  public static List<Constructor> getConstructors(Class<?> type, int paramCount) {
    List<Constructor> result = new ArrayList<Constructor>(type.getConstructors().length);
    for (Constructor constr : type.getConstructors()) {
      if (constr.getParameterTypes().length == paramCount) {
        result.add(constr);
      }
    }
    return result;
  }

  /**
   * Returns the bean class full name.
   * <p>
   * {@inheritDoc}
   * 
   * @return The bean class full name.
   */
  @Override
  public String toString() {
    return this.type.getName();
  }

  private static String getSetterMethodName(String property) {
    if (property == null) {
      throw new NullPointerException("Got null reference instead of bean property name.");
    }

    String methodName = property;
    if (!Character.isUpperCase(property.charAt(0))) {
      methodName = Character.toUpperCase(property.charAt(0)) + property.substring(1);
    }
    methodName = "set" + methodName;
    return methodName;
  }

}
