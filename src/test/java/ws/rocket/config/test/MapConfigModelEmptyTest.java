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

import java.io.ByteArrayInputStream;
import java.util.Map;
import org.testng.annotations.Test;

import ws.rocket.config.ConfigException;
import ws.rocket.config.MapConfigModel;
import ws.rocket.config.Messages;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Tests the minimum: a configuration with no sections, both model construction and parsing.
 * 
 * @author Martti Tamm
 */
public final class MapConfigModelEmptyTest {

  /**
   * Creates a configuration model with no sections. Afterwards parses an empty stream and verifies that an instance
   * of configuration bean is created.
   */
  @Test
  public void testModel() {
    MapConfigModel<Object> model = MapConfigModel.expect(Object.class);
    verifyModel(model);
    verifyParse(model);
  }

  private void verifyModel(MapConfigModel<Object> model) {
    assertNotNull(model, "Model object");
    assertSame(model.getConfigBeanType(), Object.class, "Model bean type.");
  }

  private void verifyParse(MapConfigModel<Object> model) {
    try {
      Map<String, Object> data = model.parse(new ByteArrayInputStream(new byte[0]));

      assertNotNull(data, "Configuration object");
      assertTrue(data.isEmpty(), "Configuration object should be empty");

    } catch (ConfigException e) {
      Messages msgs = e.getMessages();
      msgs.writeTo(System.out);

      assertNotNull(msgs, "Messages must be (always) defined");
      assertFalse(msgs.hasWarnings(), "No warnings expected (should be correct model).");
      assertFalse(msgs.hasErrors(), "No errors expected (should be correct model).");
    }
  }

}
