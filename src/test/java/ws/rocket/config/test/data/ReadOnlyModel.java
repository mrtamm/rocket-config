/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ws.rocket.config.test.data;

/**
 *
 * @author martti
 */
public class ReadOnlyModel {

  private final int code;

  private final String text;

  private final boolean enabled;

  public ReadOnlyModel(int code, String text, boolean enabled) {
    this.code = code;
    this.text = text;
    this.enabled = enabled;
  }

  public int getCode() {
    return this.code;
  }

  public String getText() {
    return this.text;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

}
