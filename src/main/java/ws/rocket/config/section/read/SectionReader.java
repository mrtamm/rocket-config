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

package ws.rocket.config.section.read;

import ws.rocket.config.section.SectionValueException;
import ws.rocket.config.section.value.ValueConverter;

/**
 * Section reader interprets the content of a section line-by-line.
 *
 * @author Martti Tamm
 */
public interface SectionReader {

  /**
   * Resets the inner state so that the reader could be reused.
   */
  void reset();

  /**
   * Reads a new line of section data.
   * 
   * @param line A non-blank section data line.
   * @param converter A value converter to convert data to target type.
   * @throws SectionValueException When the line data is not some how correct for this reader.
   */
  void readLine(String line, ValueConverter converter) throws SectionValueException;

  /**
   * To be called after section data has been read: provides a collection of parsed data.
   * 
   * @return A collection.
   */
  Object getResult();

  /**
   * Provides the class name of the returned result collection. For example, <code>java.util.List.class</code>. This
   * type should be general, not the precise implementation, unless that's an important hint to section writer.
   * 
   * @return The result collection type as class.
   */
  Class<?> getCollectionType();

  /**
   * Provides the class name of the main value in the returned collection.
   * <p>
   * The main value type in the collection (it is used to determine array component type). For a map, it's the type of
   * the map keys (to check if they are Strings).
   * 
   * @return A value type as class.
   */
  Class<?> getValueType();
}
