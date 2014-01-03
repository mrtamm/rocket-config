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

package ws.rocket.config;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple container for all the error and warning messages generated during parsing and model validation. Error
 * message should be considered critical, while warning messages can be ignored or not tolerated.
 *
 * @author Martti Tamm
 */
public final class Messages {

  private final List<String> errors = new ArrayList<String>();

  private final List<String> warnings = new ArrayList<String>();

  /**
   * Adds a new error message to this container.
   *
   * @param msg The message to add.
   */
  public void addError(String msg) {
    if (msg != null && msg.trim().length() > 0) {
      this.errors.add(msg);
    }
  }

  /**
   * Adds a new warning message to this container.
   *
   * @param msg The message to add.
   */
  public void addWarning(String msg) {
    if (msg != null && msg.trim().length() > 0) {
      this.warnings.add(msg);
    }
  }

  /**
   * Provides all contained error messages as an array.
   *
   * @return An array containing all error messages.
   */
  public String[] getErrors() {
    return this.errors.toArray(new String[this.errors.size()]);
  }

  /**
   * Provides all contained warning messages as an array.
   *
   * @return An array containing all warning messages.
   */
  public String[] getWarnings() {
    return this.warnings.toArray(new String[this.warnings.size()]);
  }

  /**
   * Reports current error messages count.
   *
   * @return The count of error messages.
   */
  public int getErrorCount() {
    return this.errors.size();
  }

  /**
   * Reports current warning messages count.
   *
   * @return The count of warning messages.
   */
  public int getWarningCount() {
    return this.warnings.size();
  }

  /**
   * Reports whether there are any error messages contained by this object.
   *
   * @return A Boolean that is true when at least one error message exists..
   */
  public boolean hasErrors() {
    return !this.errors.isEmpty();
  }

  /**
   * Reports whether there are any warning messages contained by this object.
   *
   * @return A Boolean that is true when at least one warning message exists..
   */
  public boolean hasWarnings() {
    return !this.warnings.isEmpty();
  }

  /**
   * Clears all messages contained by this object.
   */
  public void clear() {
    this.errors.clear();
    this.warnings.clear();
  }

  /**
   * Writes all contained messages to given stream. When there are no messages, nothing will be written.
   * <p>
   * The output will be human-readable. If there are errors, they will be listed first.
   *
   * @param ps The stream to write to. When it's null and there are messages to be written, this method will fail with a
   * NullPointerException.
   */
  public void writeTo(PrintStream ps) {
    if (ps != null && (hasErrors() || hasWarnings())) {
      ps.println("=========================");
      if (hasErrors()) {
        write(ps, "ERRORS", this.errors);
      }
      if (hasWarnings()) {
        write(ps, "WARNINGS", this.warnings);
      }
      ps.flush();
    }
  }

  @Override
  public String toString() {
    String result = "[No messages]";
    if (hasErrors() || hasWarnings()) {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
      writeTo(new PrintStream(buffer));
      result = buffer.toString();
    }
    return result;
  }

  private void write(PrintStream ps, String name, List<String> rows) {
    ps.print(name);
    ps.print(" (");
    ps.print(rows.size());
    ps.println("):");

    int i = 1;
    for (String msg : rows) {
      ps.print(i++);
      ps.print(". ");
      ps.println(msg);
    }
  }

}
