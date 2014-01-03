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

package ws.rocket.config.section.write;

import java.util.Arrays;
import java.util.Map;
import ws.rocket.config.bean.BeanValidator;
import ws.rocket.config.bean.BeanWriter;

/**
 * A section data writer that writes each value to corresponding property of target bean. This writer supports only
 * those readers that collect data into a map where keys are <code>String</code>s. The keys must match property names.
 * When property names are given, the map keys that do not match any property name, will be ignored.
 *
 * @author Martti Tamm
 */
public final class MultiPropertyWriter implements SectionWriter {

  private final String[] propertyNames;

  /**
   * Creates a new multi-property writer. When property names are not defined, all the properties are attempted to write
   * to for which there exists a map key with non-null value.
   * 
   * @param propertyNames Optional array of property names defining writable properties.
   */
  public MultiPropertyWriter(String... propertyNames) {
    this.propertyNames = propertyNames;
  }

  @Override
  public void validate(Class<?> collectionType, Class<?> mainValueType, BeanValidator validator) {
    validator.validateMap(collectionType, mainValueType, Arrays.toString(this.propertyNames));

    for (String propertyName : this.propertyNames) {
      validator.requireSetterMethod(propertyName);
    }
  }

  @Override
  public void write(BeanWriter<?> writer, Object values, Class<?> valueType) {
    Map map = (Map) values;

    if (this.propertyNames.length > 0) {
      for (String propertyName : this.propertyNames) {
        Object value = map.get(propertyName);
        if (value != null) {
          writer.setProperty(propertyName, value, null);
        }
      }
    } else if (valueType == String.class) {
      for (Object propertyName : map.keySet()) {
        Object value = map.get((String) propertyName);
        if (value != null) {
          writer.setProperty((String) propertyName, value, null);
        }
      }
    }
  }

}
