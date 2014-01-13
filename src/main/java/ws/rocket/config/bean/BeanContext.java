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

package ws.rocket.config.bean;

import ws.rocket.config.Messages;
import ws.rocket.config.section.value.ValueConverter;

/**
 * Provides access to bean validation, value conversion, and bean value writing functionality for a bean type.
 *
 * @author Martti Tamm
 * @param <T> The bean type.
 */
public final class BeanContext<T> {

  private final BeanType<T> beanType;

  private final ValueConverter valueConverter;

  private final BeanValidator validator;

  /**
   * Creates a new bean context for given type. The value converter instance will be used for converting
   * <code>String</code> values to runtime types used by target bean.
   * 
   * @param type The target configuration bean type.
   * @param valueConverter A converter to use for bean property value conversion from <code>String</code>.
   */
  public BeanContext(Class<T> type, ValueConverter valueConverter) {
    this.beanType = new BeanType<T>(type);
    this.valueConverter = valueConverter;
    this.validator = new BeanValidator(this.beanType);
  }

  /**
   * Provides the current bean type.
   * 
   * @return The bean type.
   */
  public Class<T> getBeanType() {
    return this.beanType.getBeanClass();
  }

  /**
   * Creates a new bean and its writer instances for writing values to that new bean.
   * 
   * @param messages A message container to use for logging errors and warnings.
   * @return The created writer.
   */
  public BeanWriter<T> createWithBean(Messages messages) {
    return BeanWriter.createWithBean(this.beanType, this.valueConverter, messages);
  }

  /**
   * Creates a new bean writer without creating a bean instance yet. Useful, when a section writer has control over
   * initializing the bean.
   * 
   * @param messages A message container to use for logging errors and warnings.
   * @return The created writer.
   */
  public BeanWriter<T> createWriter(Messages messages) {
    return BeanWriter.create(this.beanType, this.valueConverter, messages);
  }

  /**
   * Provides validation functionality for the underlying bean.
   * 
   * @return A validator instance.
   */
  public BeanValidator getValidator() {
    return this.validator;
  }

  /**
   * Provides value conversion functionality used for the underlying bean.
   * 
   * @return A value converter instance.
   */
  public ValueConverter getValueConverter() {
    return this.valueConverter;
  }

}
