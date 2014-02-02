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

import java.util.HashMap;
import java.util.Map;
import ws.rocket.config.section.SectionValueException;
import ws.rocket.config.section.value.ValueConverter;

/**
 * A section reader that expects a key-value pair (separated by first equal-sign) per line. By default, the key and
 * value are expected to be <code>String</code>s, however, the target types can also be predefined so that data errors
 * could be discovered quicker.
 *
 * @author Martti Tamm
 */
public final class ValueMapSection extends NonBlankLineReader {

  private Map<Object, Object> props = new HashMap<Object, Object>();

  private final Class<?> keyType;

  private final Class<?> valueType;

  /**
   * Creates a new instance where keys and values will be treated as <code>String</code>s.
   */
  public ValueMapSection() {
    this(String.class, String.class);
  }

  /**
   * Creates a new instance where keys will be treated as <code>keyType</code> and values as <code>valueType</code>.
   * 
   * @param keyType Target runtime type for map keys.
   * @param valueType Target runtime type for map values.
   */
  public ValueMapSection(Class<?> keyType, Class<?> valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public void reset() {
    this.props = new HashMap<Object, Object>();
  }

  @Override
  protected void readNonBlankLine(String line, ValueConverter converter) throws SectionValueException {
    int splitAt = line.indexOf('=');
    boolean valid = false;

    if (splitAt > 0) {
      Object key = readKey(line, splitAt, converter);

      if (key != null) {
        valid = true;
        if (!this.props.containsKey(key)) {
          this.props.put(key, readValue(line, splitAt, converter));
        } else {
          throw new SectionValueException("Property '" + key
              + " is specified more than once, but only first value was registered", true);
        }
      }
    }

    if (!valid) {
      throw new SectionValueException("Bad property line format: '" + line + "' (should be: key = value)");
    }
  }

  private Object readKey(String line, int uptoPos, ValueConverter converter) throws SectionValueException {
    String value = line.substring(0, uptoPos).trim();
    return value.length() == 0 ? null : converter.convert(value, this.keyType);
  }

  private Object readValue(String line, int afterPos, ValueConverter converter) throws SectionValueException {
    String value = line.substring(afterPos + 1).trim();
    return value.length() == 0 ? null : converter.convert(value, this.valueType);
  }

  @Override
  public Map<Object, Object> getResult() {
    return this.props;
  }

  @Override
  public Class<?> getValueType() {
    return this.keyType;
  }

  /**
   * Returns <code>java.util.Map.class</code>.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Class<?> getCollectionType() {
    return Map.class;
  }

}
