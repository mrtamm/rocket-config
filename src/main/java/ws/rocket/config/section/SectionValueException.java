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

/**
 * Exception for bad section values.
 *
 * @author Martti Tamm
 */
public final class SectionValueException extends Exception {

  private final boolean warning;

  /**
   * Creates a new exception with given error message.
   * 
   * @param msg Error message (required).
   */
  public SectionValueException(String msg) {
    this(msg, false);
  }

  /**
   * Creates a new exception with given error/warning message.
   * 
   * @param msg Error message (required).
   * @param warning A boolean, when true, the message is to considered a non-fatal error (warning), otherwise the
   *                message indicates a fatal error that might block further actions on a section.
   */
  public SectionValueException(String msg, boolean warning) {
    super(msg);
    this.warning = warning;
  }

  /**
   * Reports whether this exception is fatal (error) or non-fatal (warning).
   * 
   * @return A Boolean that is true for warnings.
   */
  public boolean isWarning() {
    return this.warning;
  }

}
