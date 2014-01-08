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

package ws.rocket.config.test.data;

/**
 * Class for an immutable (read-only) data object used in tests.
 *
 * @author Martti Tamm
 */
public final class ReadOnlyModel {

  private final int code;

  private final String text;

  private final boolean enabled;

  /**
   * Creates a new instance that will contain given data.
   * 
   * @param code Any integer.
   * @param text Any text.
   * @param enabled Any boolean.
   */
  public ReadOnlyModel(int code, String text, boolean enabled) {
    this.code = code;
    this.text = text;
    this.enabled = enabled;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public int getCode() {
    return this.code;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public String getText() {
    return this.text;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public boolean isEnabled() {
    return this.enabled;
  }

}
