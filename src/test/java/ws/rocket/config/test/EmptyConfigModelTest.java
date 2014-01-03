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

  private final ConfigModel<Object> model = ConfigModel.expect(Object.class).ready();

  @Test
  public void testModelNotNull() {
    assertNotNull(this.model, "Model object");
  }

  @Test
  public void testModelBeanType() {
    assertSame(this.model.getConfigBeanType(), Object.class, "Model bean type.");
  }

  @Test
  public void testModelSectionsEmpty() {
    assertNotNull(this.model.getSections(), "Model sections array");
    assertEquals(this.model.getSections().length, 0, "Model sections count");
  }

}
