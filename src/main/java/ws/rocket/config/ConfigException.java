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

/**
 * Indicates an error or errors that caused the configuration parse process to halt.
 *
 * @author Martti Tamm
 */
public final class ConfigException extends Exception {

  private final Messages msgs;

  /**
   * Creates a new instance with error messages that caused the configuration load to fail.
   * 
   * @param messages The messages from the configuration load procedure. Must not be null.
   */
  public ConfigException(Messages messages) {
    if (messages == null) {
      throw new NullPointerException("Given messages is a null reference");
    }
    this.msgs = messages;
  }

  /**
   * Provides the messages that caused the configuration load to fail.
   * 
   * @return The messages object.
   */
  public Messages getMessages() {
    return this.msgs;
  }

  @Override
  public String toString() {
    return "Configuration had " + this.msgs.getErrorCount() + " error(s) and "
        + this.msgs.getWarningCount() + " warnings.";
  }

}
