/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ws.rocket.config.test;

import java.util.List;
import ws.rocket.config.ConfigModel;
import ws.rocket.config.Messages;
import ws.rocket.config.test.data.ConfigTestModel;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import ws.rocket.config.ConfigException;
import ws.rocket.config.test.data.filter.Phase1Filter;
import ws.rocket.config.test.data.filter.Phase2Filter;
import ws.rocket.config.test.data.filter.TestFilter;
import ws.rocket.config.test.data.handler.Phase1Handler;
import ws.rocket.config.test.data.handler.Phase2Handler;
import ws.rocket.config.test.data.handler.TestHandler;

/**
 *
 * @author martti
 */
public class ConfigTestModelTest {
 
  @Test
  public void testModelParse() {
    final ConfigModel<ConfigTestModel> model = ConfigModel.expect(ConfigTestModel.class)
        .section("main").ofMap().storeInBeanProps()
        .section("read-only").ofMap().storeInBeanOf("readOnly", "code", "text", "enabled")
        .section("handlers").ofMap(TestHandler.class).storeIn("handlers")
        .section("filters").ofList(TestFilter.class).storeIn("interceptors")
        .section("filters-array").ofList(TestFilter.class).storeIn("interceptorsArray")
        .ready();

    try {
      ConfigTestModel config = model.parse(ConfigTestModel.class.getResourceAsStream("/config-test.conf"));

      validateGeneralSection(config);
      validateReadOnlySection(config);
      validateHandlerSection(config);
      validateFilterSection(config);
      validateFilterArraySection(config);

    } catch (ConfigException e) {
      Messages msgs = e.getMessages();
      msgs.writeTo(System.out);

      assertNotNull(msgs, "Messages must be (always) defined");
      assertFalse(msgs.hasWarnings(), "No warnings (should be correct model).");
      assertFalse(msgs.hasErrors(), "No errors (should be correct model).");
    }

  }

  private void validateGeneralSection(ConfigTestModel config) {
    assertEquals(config.getDescription(), "This is a test");
    assertEquals(config.getClazz(), String.class);
    assertEquals(config.getAmount(), 123456789);
    assertEquals(config.getPort(), 12345);
    assertEquals(config.getPopulation(), 7000000000L);
    assertEquals(config.getPrice(), 9.99f);
    assertEquals(config.getRatio(), 100000.123);
    assertEquals(config.isEnabled(), true);
    assertEquals(config.getIndex(), 127);
    assertEquals(config.getYes(), 'y');
  }

  private void validateReadOnlySection(ConfigTestModel config) {
    assertNotNull(config.getReadOnly());
    assertEquals(config.getReadOnly().getCode(), 404);
    assertEquals(config.getReadOnly().getText(), "Page Not Found");
    assertTrue(config.getReadOnly().isEnabled());
  }

  private void validateHandlerSection(ConfigTestModel config) {
    assertEquals(config.getHandlers().size(), 4);
    assertEquals(config.getHandlers().get("step1").getClass(), Phase2Handler.class);
    assertEquals(config.getHandlers().get("step2").getClass(), Phase1Handler.class);
    assertEquals(config.getHandlers().get("step3").getClass(), Phase1Handler.class);
    assertEquals(config.getHandlers().get("step4").getClass(), Phase2Handler.class);
  }

  private void validateFilterSection(ConfigTestModel config) {
    List<TestFilter> values = config.getInterceptors();

    assertEquals(values.size(), 4);
    assertEquals(values.get(0).getClass(), Phase1Filter.class);
    assertEquals(values.get(1).getClass(), Phase2Filter.class);
    assertEquals(values.get(2).getClass(), Phase1Filter.class);
    assertEquals(values.get(3).getClass(), Phase2Filter.class);
  }

  private void validateFilterArraySection(ConfigTestModel config) {
    TestFilter[] values = config.getInterceptorsArray();

    assertEquals(values.length, 4);
    assertEquals(values[0].getClass(), Phase2Filter.class);
    assertEquals(values[1].getClass(), Phase1Filter.class);
    assertEquals(values[2].getClass(), Phase2Filter.class);
    assertEquals(values[3].getClass(), Phase1Filter.class);
  }

}
