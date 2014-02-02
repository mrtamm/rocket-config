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
 * Section reader abstraction that skips blank lines and removes surrounding whitespace from line data.
 *
 * @author Martti Tamm
 */
public abstract class NonBlankLineReader implements SectionReader {

  @Override
  public final void readLine(String line, ValueConverter converter) throws SectionValueException {
    String value = line.trim();

    if (value.length() > 0) {
      readNonBlankLine(value, converter);
    }
  }

  /**
   * Reads a new non-blank line of section data.
   * 
   * @param line A section data line (with comments and surrounding whitespace removed).
   * @param converter A value converter to convert data to target type.
   * @throws SectionValueException When the line data is not some how correct for this reader.
   */
  protected abstract void readNonBlankLine(String line, ValueConverter converter) throws SectionValueException;

}
