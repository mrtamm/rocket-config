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
import ws.rocket.config.reader.StreamWriter;

/**
 * An advanced section data writer that determines the property type, and creates an instance of it by calling the
 * constructor with as many parameters as given to this class. For this purpose, parameter names are required.
 * <p>
 * This writer supports only those readers that collect data into a map where keys are <code>String</code>s. The keys
 * must match property names. When property names are given, the map keys that do not match any property name, will be
 * ignored.
 *
 * @author Martti Tamm
 */
public final class BeanConstructPropertyWriter implements SectionWriter {

  private final String[] paramNames;

  /**
   * Creates a new multi-property writer. When property names are not defined, all the properties are attempted to write
   * to for which there exists a map key with non-null value.
   * 
   * @param paramNames Array of parameter names of target property type. Their count determines the constructor to call.
   *                   The values for constructor parameters will be taken from the map by corresponding parameter name.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public BeanConstructPropertyWriter(String... paramNames) {
    this.paramNames = paramNames;
  }

  @Override
  public void validate(Class<?> collectionType, Class<?> mainValueType, BeanValidator validator) {
    BeanValidator.validateMap(collectionType, mainValueType, Arrays.toString(this.paramNames));

    if (this.paramNames.length > 0 && validator.getBeanType().getConstructors(this.paramNames.length).isEmpty()) {
      validator.requireEmptyConstructor();

      for (String propName : this.paramNames) {
        validator.requireSetterMethod(propName);
      }
    }
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void write(BeanWriter<?> writer, Object values, Class<?> valueType) {
    writer.reconstruct(this.paramNames, (Map<String, String>) values);
  }

  @Override
  public void describeTo(StreamWriter out, Class<?> collectionType, Class<?> valueType) {
    out.nameValues(this.paramNames);
  }

}
