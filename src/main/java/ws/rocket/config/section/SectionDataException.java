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

package ws.rocket.config.section;

import java.util.ArrayList;
import java.util.List;

/**
 * An exception used for gathering multiple section value exceptions.
 * 
 * @author Martti Tamm
 */
public final class SectionDataException extends Exception {

  private final List<SectionValueException> valueExceptions = new ArrayList<SectionValueException>();

  /**
   * Creates a new instance and adds given value exception to the list of all gathered value exceptions.
   * 
   * @param e A value exception. Null values will be ignored.
   */
  public SectionDataException(SectionValueException e) {
    super("One or more values in section were not correct");
    append(e);
  }

  /**
   * Appends given value exception to the list of all gathered value exceptions.
   * 
   * @param e A value exception. Null values will be ignored.
   */
  public void append(SectionValueException e) {
    if (e != null) {
      this.valueExceptions.add(e);
    }
  }

  /**
   * Provides the list of all gathered value exceptions.
   * 
   * @return The list of value exceptions.
   */
  public List<SectionValueException> getValueExceptions() {
    return this.valueExceptions;
  }

  /**
   * Reports whether there is any value exception in this class.
   * 
   * @return A Boolean that is true when at least one value exception is contained.
   */
  public boolean hasProblems() {
    return !this.valueExceptions.isEmpty();
  }

}
