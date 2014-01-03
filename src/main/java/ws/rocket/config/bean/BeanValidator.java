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

import java.util.Map;

/**
 * Provides checks to be performed on a target type, which is expected to be bean. When a check fails, a
 * <code>ModelException</code> will be raised together with message describing the failed check.
 *
 * @see ModelException
 * @author Martti Tamm
 */
public final class BeanValidator {

  private final BeanType<?> type;

  /**
   * Creates a new instance of validator for given bean type.
   * 
   * @param beanType Targeted bean type.
   */
  public BeanValidator(BeanType<?> beanType) {
    this.type = beanType;
  }

  /**
   * Checks that the underlying type has a default constructor.
   */
  public void requireEmptyConstructor() {
    if (this.type.getConstructors(0).isEmpty()) {
      throw new ModelException("Class '" + this.type + "' does not have public default constructor");
    }
  }

  /**
   * Checks that the underlying type has at least one setter method for given property.
   * 
   * @param property The bean property name to check (required).
   */
  public void requireSetterMethod(String property) {
    if (this.type.getSetterMethods(property).isEmpty()) {
      throw new ModelException("Property '" + property + "' is not writable or does not exist in "
          + this.type.toString());
    }
  }

  /**
   * Checks that the underlying type has at least one setter method for given property that accepts a parameter that is
   * either of <code>collectionType</code> or an array of <code>arrayCompType</code>.
   * 
   * @param property The bean property name to check (required).
   * @param collectionType The collection type to check (required).
   * @param arrayCompType The array component type to check (required).
   */
  public void requireProperty(String property, Class<?> collectionType, Class<?> arrayCompType) {
    if (this.type.getSetterMethod(property, collectionType) == null
            && this.type.getSetterMethodWithArray(property, arrayCompType) == null) {

      StringBuilder msg = new StringBuilder(100);
      msg.append("Property '").append(property).append("' (").append(collectionType.getName());

      if (arrayCompType != null) {
        msg.append(" or ").append(arrayCompType.getName()).append("[]");
      }

      msg.append(") is not writable or does not exist in ").append(this.type.toString());

      throw new ModelException(msg.toString());
    }
  }

  /**
   * Checks that the underlying type has a writable <code>property</code> with a type that has a constructor with
   * exactly <code>constrParamCount</code> parameters.
   * 
   * @param property The bean property name to check (required).
   * @param constrParamCount The amount of parameters required for the class constructor.
   */
  public void requirePropertyValueConstructor(String property, int constrParamCount) {
    Class<?> propType = this.type.getPropertyType(property, constrParamCount);
    if (propType == null) {
        throw new ModelException("Did not find accessible constructors with " + constrParamCount
                + " parameter(s) to create an instance of the object for setting it to property " + property + ".");
    }
  }

  /**
   * This method does not check bean, but the parameters, instead: the collection type must be
   * <code>java.util.Map.class</code> and the main value type must be <code>String</code>. This check is mostly used by
   * bean writers that accept only a map collection, and perform some sort of bean mapping with the map keys.
   * 
   * @param collectionType The collection type to check (required).
   * @param mainValueType The main collection value type to check (required).
   * @param propertyName A free-form string describing the property names this check applies to. Used in error message.
   */
  public static void validateMap(Class<?> collectionType, Class<?> mainValueType, String propertyName) {
    if (collectionType != Map.class) {
        throw new ModelException("Map section is required for setting value to property " + propertyName + ".");
    }
    if (mainValueType != String.class) {
        throw new ModelException("Map keys must be String for setting value to property " + propertyName + ".");
    }
  }

}
