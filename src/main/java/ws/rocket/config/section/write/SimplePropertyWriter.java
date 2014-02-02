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

import java.util.Map;
import ws.rocket.config.bean.BeanValidator;
import ws.rocket.config.bean.BeanWriter;
import ws.rocket.config.reader.StreamWriter;

/**
 * A section data writer that will store the received collection in a property of the target bean.
 *
 * @author Martti Tamm
 */
public final class SimplePropertyWriter implements SectionWriter {

  private final String propertyName;

  /**
   * Creates a new simple writer instance.
   *
   * @param propertyName The property name of the target bean.
   */
  public SimplePropertyWriter(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public void validate(Class<?> collectionType, Class<?> mainValueType, BeanValidator validator) {
    validator.requireProperty(this.propertyName, collectionType, mainValueType);
  }

  @Override
  public void write(BeanWriter<?> writer, Object values, Class<?> valueType) {
    writer.setProperty(this.propertyName, values, valueType);
  }

  @Override
  public void describeTo(StreamWriter out, Class<?> collectionType, Class<?> valueType) {
    if (collectionType == Map.class) {
      out.nameValues(null);
    } else {
      out.values(valueType);
    }
  }

}
