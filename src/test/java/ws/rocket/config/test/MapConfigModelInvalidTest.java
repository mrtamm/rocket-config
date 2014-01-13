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
import ws.rocket.config.MapConfigModel;
import ws.rocket.config.bean.ModelException;
import ws.rocket.config.section.value.ValueConverter;

/**
 * Attempts construction of map-based configuration model with bad parameters. This test case demonstrates the expected
 * exception types for different situations.
 *
 * @author Martti Tamm
 */
public final class MapConfigModelInvalidTest {

  /**
   * When configuration bean type is null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Given configuration bean type is a null reference"
  )
  public void testModelTypeNull() {
    MapConfigModel.expect(null);
  }

  /**
   * When value converter is explicitly null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Given value converter is a null reference"
  )
  public void testValueConverterNull() {
    MapConfigModel.expect(Object.class, (ValueConverter) null);
  }

  /**
   * When configuration bean destination property is null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Got a null reference instead of a bean property name"
  )
  public void testTargetPropertyNull() {
    MapConfigModel.expect(Object.class, (String) null);
  }

  /**
   * When configuration bean destination property string contains whitespace.
   */
  @Test(
      expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Property name contains whitespace\\."
  )
  public void testTargetPropertyWhitespace() {
    MapConfigModel.expect(Object.class, " test ");
  }

  /**
   * When configuration bean does not have that destination property (with parameter type either List or array). Testing
   * the <code>storeIn(String)</code> method.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Property 'noSuchProperty' is not writable or does not exist in "
          + "java\\.lang\\.Object"
  )
  public void testTargetPropertyNotExist() {
    MapConfigModel.expect(Object.class, "noSuchProperty");
  }

}
