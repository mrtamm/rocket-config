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

package ws.rocket.config.section.value;

import ws.rocket.config.section.SectionValueException;

/**
 * A simple value converter: parsing string to create instance of target type. Converters must be stateless but are not
 * required to be thread-safe.
 *
 * @author Martti Tamm
 */
public interface ValueConverter {

  /**
   * Converts given value to target type. When conversion fails, it should be logged in <code>msgs</code>.
   * <p>
   * Normally, a converter should not be called to convert a null or empty string. However, implementations may handle
   * that by returning null and/or logging a warning.
   *
   * @param value The source value.
   * @param targetType The target type as class (required). When an interface or abstract class, the source value should
   * be considered a full name of a class to be instantiated that must be implement or extend this target type.
   * @return The converted value. May be a null.
   * @throws ws.rocket.config.section.SectionValueException When the value has problems that blocked the conversion.
   */
  Object convert(String value, Class<?> targetType) throws SectionValueException;

}
