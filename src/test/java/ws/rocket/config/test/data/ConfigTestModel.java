/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ws.rocket.config.test.data;

import java.util.List;
import java.util.Map;
import ws.rocket.config.test.data.filter.TestFilter;
import ws.rocket.config.test.data.handler.TestHandler;

/**
 *
 * @author martti
 */
public class ConfigTestModel {

  private Class<?> clazz;

  private String description;

  private int amount;

  private long population;

  private float price;

  private double ratio;

  private boolean enabled;

  private byte index;

  private char yes;

  private ReadOnlyModel readOnly;

  private Map<String, TestHandler> handlers;

  private List<TestFilter> interceptors;

  private TestFilter[] interceptorsArray;

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public long getPopulation() {
    return population;
  }

  public void setPopulation(long population) {
    this.population = population;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public double getRatio() {
    return ratio;
  }

  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public byte getIndex() {
    return index;
  }

  public void setIndex(byte index) {
    this.index = index;
  }

  public char getYes() {
    return yes;
  }

  public void setYes(char yes) {
    this.yes = yes;
  }

  public ReadOnlyModel getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(ReadOnlyModel readOnly) {
    this.readOnly = readOnly;
  }

  public Map<String, TestHandler> getHandlers() {
    return handlers;
  }

  public void setHandlers(Map<String, TestHandler> handlers) {
    this.handlers = handlers;
  }

  public List<TestFilter> getInterceptors() {
    return interceptors;
  }

  public void setInterceptors(List<TestFilter> interceptors) {
    this.interceptors = interceptors;
  }

  public TestFilter[] getInterceptorsArray() {
    return interceptorsArray;
  }

  public void setInterceptorsArray(TestFilter[] interceptorsArray) {
    this.interceptorsArray = interceptorsArray;
  }

}
