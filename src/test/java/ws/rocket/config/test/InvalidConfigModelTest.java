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

import org.testng.Assert;
import org.testng.annotations.Test;
import ws.rocket.config.ConfigModel;
import ws.rocket.config.bean.ModelException;
import ws.rocket.config.test.data.ConfigTestModel;

/**
 * Attempts construction of configuration model with bad parameters. This test case demonstrates the expected exception
 * types for different situations.
 *
 * @author Martti Tamm
 */
public final class InvalidConfigModelTest {

  /**
   * When configuration bean type is null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Given configuration bean type is a null reference"
  )
  public void testModelTypeNull() {
    ConfigModel.expect(null);
  }

  /**
   * When configuration bean type has no default constructor.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Class 'java\\.lang\\.Integer' does not have public default constructor"
  )
  public void testModelTypeNoDefaultConstructor() {
    ConfigModel.expect(Integer.class);
  }

  /**
   * When value converter is explicitly null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Given value converter is a null reference"
  )
  public void testValueConverterNull() {
    ConfigModel.expect(Object.class, null);
  }

  /**
   * When section name is null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Got a null reference for a section name"
  )
  public void testSectionNameNull() {
    ConfigModel.expect(Object.class).section(null);
  }

  /**
   * When section name string is blank.
   */
  @Test(
      expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Section name is empty"
  )
  public void testSectionNameBlank() {
    ConfigModel.expect(Object.class).section("  ");
  }

  /**
   * When section name is surrounded by whitespace. Gets trimmed, no exception raised.
   */
  @Test
  public void testSectionNameWhitespace() {
    ConfigModel<ConfigTestModel> model = ConfigModel.expect(ConfigTestModel.class)
        .section("  some name  ").ofMap().storeInBeanProps().ready();
    Assert.assertEquals(model.getSections()[0].getName(), "some name");
  }

  /**
   * When section name string contains '['.
   */
  @Test(
      expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Section name must not contain square brackets"
  )
  public void testSectionNameSquareBracketOpen() {
    ConfigModel.expect(Object.class).section("some[name");
  }

  /**
   * When section name string contains ']'.
   */
  @Test(
      expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Section name must not contain square brackets"
  )
  public void testSectionNameSquareBracketClose() {
    ConfigModel.expect(Object.class).section("some]name");
  }

  /**
   * When section name is already defined.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Section with name \\[some-name\\] is already defined\\."
  )
  public void testSectionNameDuplicate() {
    ConfigModel.expect(ConfigTestModel.class)
        .section("some-name").ofMap().storeIn("handlers")
        .section(" some-name ");
  }

  /**
   * When configuration bean destination property is null.
   */
  @Test(
      expectedExceptions = NullPointerException.class,
      expectedExceptionsMessageRegExp = "Got a null reference instead of a bean property name"
  )
  public void testTargetPropertyNull() {
    ConfigModel.expect(Object.class).section("test").ofList().storeIn(null);
  }

  /**
   * When configuration bean destination property string contains whitespace.
   */
  @Test(
      expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Property name contains whitespace\\."
  )
  public void testTargetPropertyWhitespace() {
    ConfigModel.expect(Object.class).section("test").ofList().storeIn(" test ");
  }

  /**
   * When configuration bean does not have that destination property (with parameter type either List or array).
   * Testing the <code>storeIn(String)</code> method.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Property 'noSuchProperty' \\(java\\.util\\.List or "
          + "java\\.lang\\.String\\[\\]\\) is not writable or does not exist in java\\.lang\\.Object"
  )
  public void testTargetPropertyNotExist() {
    ConfigModel.expect(Object.class).section("test").ofList().storeIn("noSuchProperty");
  }

  /**
   * When configuration bean does not have that destination property (with parameter type Map). Testing the
   * <code>storeIn(String)</code> method.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Property 'description' \\(java\\.util\\.Map\\) is not writable or does not "
          + "exist in ws\\.rocket\\.config\\.test\\.data\\.ConfigTestModel"
  )
  public void testTargetPropertyNotMap() {
    ConfigModel.expect(ConfigTestModel.class).section("test").ofMap().storeIn("description");
  }

  /**
   * When configuration bean does not have that destination property (with any parameter type). Testing the
   * <code>storeInBeanProps(String...)</code> method.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Property 'noSuchProperty' is not writable or does not exist in "
          + "java\\.lang\\.Object"
  )
  public void testTargetPropertyNotExistMulti() {
    ConfigModel.expect(Object.class).section("test").ofMap().storeInBeanProps("noSuchProperty");
  }

  /**
   * When configuration bean property value type has not a constructor with given amount of parameters.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Did not find accessible constructors with 1 parameter\\(s\\) to create an "
          + "instance of the object for setting it to property readOnly\\."
  )
  public void testTargetPropertyValueConstrParamCount() {
    ConfigModel.expect(Object.class).section("test").ofMap().storeInBeanOf("readOnly", "1");
  }

  /**
   * When configuration bean destination property has a different array type.
   */
  @Test(
      expectedExceptions = ModelException.class,
      expectedExceptionsMessageRegExp = "Property 'interceptorsArray' \\(java\\.util\\.List or "
          + "java\\.lang\\.String\\[\\]\\) is not writable or does not exist in "
          + "ws\\.rocket\\.config\\.test\\.data\\.ConfigTestModel"
  )
  public void testTargetPropertyWrongArrayType() {
    ConfigModel.expect(ConfigTestModel.class).section("test").ofList().storeIn("interceptorsArray");
  }

}
