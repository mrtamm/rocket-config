/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ws.rocket.config.test;

import org.testng.annotations.Test;
import ws.rocket.config.ConfigModel;

import static org.testng.Assert.*;

/**
 *
 * @author Martti Tamm
 */
public class EmptyConfigModelTest {

  @Test
  public void testModel() {
    ConfigModel<Object> model = ConfigModel.expect(Object.class).ready();

    assertNotNull(model, "Model object");

    assertSame(model.getConfigBeanType(), Object.class, "Model bean type.");

    assertNotNull(model.getSections(), "Model sections array");
    assertEquals(model.getSections().length, 0, "Model sections count");
  }

}
