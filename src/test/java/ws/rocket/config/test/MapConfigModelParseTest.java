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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;
import ws.rocket.config.ConfigException;
import ws.rocket.config.MapConfigModel;
import ws.rocket.config.Messages;
import ws.rocket.config.test.data.ConfigTestModel;
import ws.rocket.config.test.data.ReadOnlyModel;

/**
 * Tests all the features to verify that they work when model is correct and configuration file conforms. This test
 * relies on external configuration files <em>/map-constructor-test.conf</em> and <em>/map-setter-test</em>.
 *
 * @author Martti Tamm
 */
public final class MapConfigModelParseTest {

  /**
   * Creates a model, parses configuration file (<em>/map-constructor-test.conf</em>), and validates the parsed data.
   */
  @Test
  public void testModelConstructorParse() {
    final MapConfigModel<ReadOnlyModel> model = MapConfigModel
            .expect(ReadOnlyModel.class, "code", "text", "enabled");

    verifyToString(model);

    try {
      Map<String, ReadOnlyModel> config = model.parse(
              ConfigTestModel.class.getResourceAsStream("/map-constructor-test.conf"));

      assertNotNull(model, "Parsed map (model) must never be null");
      assertEquals(config.size(), 3, "Expecting 3 items in map");

      validateSection(config.get("normal"), 404, "Page Not Found", true);
      validateSection(config.get("reverse-order"), 401, "Not authorized", false);
      validateSection(config.get("partial"), 200, null, true);

    } catch (ConfigException e) {
      Messages msgs = e.getMessages();
      msgs.writeTo(System.out);

      assertNotNull(msgs, "Messages must be (always) defined");
      assertFalse(msgs.hasWarnings(), "No warnings expected (should be correct model).");
      assertFalse(msgs.hasErrors(), "No errors expected (should be correct model).");
    }
  }

  /**
   * Creates a model, parses configuration file (<em>/map-setter-test.conf</em>), and validates the parsed data.
   */
  @Test
  public void testModelSetterParse() {
    final MapConfigModel<ConfigTestModel> model = MapConfigModel.expect(ConfigTestModel.class);

    verifyToString(model);

    try {
      Map<String, ConfigTestModel> config = model.parse(
              ConfigTestModel.class.getResourceAsStream("/map-setter-test.conf"));

      assertNotNull(model, "Parsed map (model) must never be null");
      assertEquals(config.size(), 3, "Expecting 3 items in map");

      validateSection(config.get("normal"), 1, String.class, 1024);
      validateSection(config.get("reverse-order"), 2, Set.class, -1025);
      validateSection(config.get("partial"), 3, null, 0);

    } catch (ConfigException e) {
      Messages msgs = e.getMessages();
      msgs.writeTo(System.out);

      assertNotNull(msgs, "Messages must be (always) defined");
      assertFalse(msgs.hasWarnings(), "No warnings expected (should be correct model).");
      assertFalse(msgs.hasErrors(), "No errors expected (should be correct model).");
    }
  }

  private void verifyToString(MapConfigModel<?> model) {
    System.out.println(model.toString());
  }

  private void validateSection(ReadOnlyModel config, int code, String text, boolean enabled) {
    assertEquals(config.getCode(), code);
    assertEquals(config.getText(), text);
    assertEquals(config.isEnabled(), enabled);
  }

  private void validateSection(ConfigTestModel config, int index, Class<?> clazz, long population) {
    assertEquals(config.getClazz(), clazz);
    assertEquals(config.getIndex(), index);
    assertEquals(config.getPopulation(), population);
  }

}
