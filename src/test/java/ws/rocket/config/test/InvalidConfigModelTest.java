/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rocket.config.test;

import org.testng.annotations.Test;
import ws.rocket.config.ConfigModel;
import ws.rocket.config.bean.ModelException;

/**
 *
 * @author Martti Tamm
 */
public class InvalidConfigModelTest {

  @Test(expectedExceptions = ModelException.class)
  public void testModelInit() {
    ConfigModel.expect(Object.class)
      .section("main").ofMap().storeInBeanProps("name", "version", "author")
      .ready();
  }
}
