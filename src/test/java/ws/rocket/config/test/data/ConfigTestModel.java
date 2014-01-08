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

package ws.rocket.config.test.data;

import java.util.List;
import java.util.Map;
import ws.rocket.config.test.data.filter.TestFilter;
import ws.rocket.config.test.data.handler.TestHandler;

/**
 * This is a simple configuration data bean used in tests.
 * <p>
 * This bean has no custom initial values for bean properties.
 *
 * @author Martti Tamm
 */
public final class ConfigTestModel {

  private Class<?> clazz;

  private String description;

  private int amount;

  private short port;

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

  /**
   * Creates a new instance. No extra checks nor work during construction.
   */
  public ConfigTestModel() {
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public Class<?> getClazz() {
    return this.clazz;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param clazz The value for this bean property.
   */
  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param description The value for this bean property.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public int getAmount() {
    return this.amount;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param amount The value for this bean property.
   */
  public void setAmount(int amount) {
    this.amount = amount;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public short getPort() {
    return this.port;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param port The value for this bean property.
   */
  public void setPort(short port) {
    this.port = port;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public long getPopulation() {
    return this.population;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param population The value for this bean property.
   */
  public void setPopulation(long population) {
    this.population = population;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public float getPrice() {
    return this.price;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param price The value for this bean property.
   */
  public void setPrice(float price) {
    this.price = price;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public double getRatio() {
    return this.ratio;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param ratio The value for this bean property.
   */
  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param enabled The value for this bean property.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public byte getIndex() {
    return this.index;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param index The value for this bean property.
   */
  public void setIndex(byte index) {
    this.index = index;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public char getYes() {
    return this.yes;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param yes The value for this bean property.
   */
  public void setYes(char yes) {
    this.yes = yes;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public ReadOnlyModel getReadOnly() {
    return this.readOnly;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param readOnly The value for this bean property.
   */
  public void setReadOnly(ReadOnlyModel readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public Map<String, TestHandler> getHandlers() {
    return this.handlers;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param handlers The value for this bean property.
   */
  public void setHandlers(Map<String, TestHandler> handlers) {
    this.handlers = handlers;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public List<TestFilter> getInterceptors() {
    return this.interceptors;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param interceptors The value for this bean property.
   */
  public void setInterceptors(List<TestFilter> interceptors) {
    this.interceptors = interceptors;
  }

  /**
   * Provides the value of the named property.
   * 
   * @return The value of this bean property.
   */
  public TestFilter[] getInterceptorsArray() {
    return this.interceptorsArray;
  }

  /**
   * Assigns a value to the named property.
   * 
   * @param interceptorsArray The value for this bean property.
   */
  public void setInterceptorsArray(TestFilter[] interceptorsArray) {
    this.interceptorsArray = interceptorsArray;
  }

}
