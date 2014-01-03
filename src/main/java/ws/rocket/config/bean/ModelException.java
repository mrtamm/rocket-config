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

package ws.rocket.config.bean;

/**
 * Indicates a problem the configuration model being constructed.
 *
 * @author Martti Tamm
 */
public class ModelException extends IllegalArgumentException {

  /**
   * Creates a new instance.
   * 
   * @param msg The message describing the problem.
   */
  public ModelException(String msg) {
    super(msg);
  }

}
