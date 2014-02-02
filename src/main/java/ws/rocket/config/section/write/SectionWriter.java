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

import ws.rocket.config.bean.BeanValidator;
import ws.rocket.config.bean.BeanWriter;
import ws.rocket.config.reader.StreamWriter;

/**
 * A section writer determines how the data collected by section reader is to be written to target beam. It's the bridge
 * between section reader and bean writer.
 * <p>
 * Due to the binding role, section writers should implement validation to verify that supported collection type is
 * returned by section readers, and that the targeted properties in the bean exists (and are with correct type).
 *
 * @author Martti Tamm
 */
public interface SectionWriter {

  /**
   * Performs simple checks to verify that this writer accepts the collection and main value types from the reader, and
   * that the targeted properties are writable. When a problem is detected, it's recommended to throw a ModelException.
   *
   * @param collectionType The collection type used by the reader.
   * @param mainValueType  The main value type in the collection. For a map, it's the type of the map keys.
   * @param validator      A helper for validating the bean properties.
   */
  void validate(Class<?> collectionType, Class<?> mainValueType, BeanValidator validator);

  /**
   * Writes given values to target bean using given writer.
   *
   * @param writer        A helper for writing to the bean properties.
   * @param values        A collection with collected values.
   * @param mainValueType The main value type in the collection (it is used to determine array component type). For a
   *                      map, it's the type of the map keys (to check if they are Strings).
   */
  void write(BeanWriter<?> writer, Object values, Class<?> mainValueType);

  /**
   * Enables the section writer to describe the expected section data.
   *
   * @param out            An object where to write the information.
   * @param collectionType The collection type used by the reader.
   * @param valueType      The main value type in the collection (it is used to determine array component type). For a
   *                       map, it's the type of the map keys (to check if they are Strings).
   */
  void describeTo(StreamWriter out, Class<?> collectionType, Class<?> valueType);

}
