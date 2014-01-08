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

package ws.rocket.config.test;

import org.testng.annotations.Test;
import ws.rocket.config.ConfigModel;
import ws.rocket.config.bean.ModelException;

/**
 * Attempts construction of configuration model with bad parametes.
 *
 * @author Martti Tamm
 */
public class InvalidConfigModelTest {

  @Test(expectedExceptions = NullPointerException.class)
  public void testModelNull() {
    ConfigModel.expect(null);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testValueConverterNull() {
    ConfigModel.expect(Object.class, null);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testSectionNameNull() {
    ConfigModel.expect(Object.class).section(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testSectionNameBlank() {
    ConfigModel.expect(Object.class).section("  ");
  }

  @Test(expectedExceptions = ModelException.class)
  public void testPropertyNotExists() {
    ConfigModel.expect(Object.class)
      .section("main").ofMap().storeIn("name")
      .ready();
  }
}
