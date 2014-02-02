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

import java.util.ArrayList;
import java.util.List;
import ws.rocket.config.section.SectionValueException;
import ws.rocket.config.section.value.ValueConverter;

/**
 * A section reader that expects a value per line. By default, a value is expected to be a <code>String</code>, however,
 * the target type can also be predefined so that data errors could be discovered quicker.
 * 
 * @author Martti Tamm
 */
public final class ValueListSection extends NonBlankLineReader {

  private List<Object> rows = new ArrayList<Object>();

  private final Class<?> valueType;

  /**
   * Creates a new instance where rows will be treated as <code>String</code>s.
   */
  public ValueListSection() {
    this(String.class);
  }

  /**
   * Creates a new instance where rows will be treated as defined by <code>valueType</code>.
   * 
   * @param valueType Target runtime type for row values.
   */
  public ValueListSection(Class<?> valueType) {
    this.valueType = valueType;
  }

  @Override
  public void reset() {
    this.rows = new ArrayList<Object>();
  }

  @Override
  protected void readNonBlankLine(String line, ValueConverter converter) throws SectionValueException {
    Object value = converter.convert(line, this.valueType);
    if (!this.rows.contains(value)) {
      this.rows.add(value);
    } else {
      throw new SectionValueException("Value '" + line
          + " is specified more than once, but it was added only once", true);
    }
  }

  @Override
  public List<Object> getResult() {
    return this.rows;
  }

  @Override
  public Class<?> getValueType() {
    return this.valueType;
  }

  /**
   * Returns <code>java.util.List.class</code>.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Class<?> getCollectionType() {
    return List.class;
  }

}
