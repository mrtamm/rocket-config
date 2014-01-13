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

package ws.rocket.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import ws.rocket.config.bean.BeanContext;
import ws.rocket.config.reader.ReaderContext;
import ws.rocket.config.section.Section;
import ws.rocket.config.section.read.ValueMapSection;
import ws.rocket.config.section.value.DefaultConverter;
import ws.rocket.config.section.value.ValueConverter;
import ws.rocket.config.section.write.BeanConstructPropertyWriter;

/**
 * A configuration model that has only one section (type) but can parse a file with multiple section, excepting them all
 * to have same section format. This model, when parsed, returns an unordered map, where read section names are stored
 * as map keys, and section data is stored as a map value (per map key) in an instance of the target bean type.
 * 
 * @param <T> The target type that will hold the read configuration of each section.
 * 
 * @author Martti Tamm
 */
public final class MapConfigModel<T> {

  /**
   * The factory method for constructing a new map-based model for given configuration bean type. The returned
   * configuration model allows unlimited amount of sections where section name is used as map key, and the section
   * data is used to create a new instance of configuration bean (per section).
   *
   * @param <T> The type of the configuration bean.
   * @param confInstanceType The type of the configuration bean.
   * @param propNames Optional array of allowed property names. When empty, all properties are attempted.
   * @return The created configuration model.
   */
  public static <T> MapConfigModel<T> expect(Class<T> confInstanceType, String... propNames) {
    return expect(confInstanceType, new DefaultConverter(), propNames);
  }

  /**
   * The factory method for constructing a new map-based model for given configuration bean type. The returned
   * configuration model allows unlimited amount of sections where section name is used as map key, and the section
   * data is used to create a new instance of configuration bean (per section).
   *
   * @param <T> The type of the configuration bean.
   * @param confInstanceType The type of the configuration bean.
   * @param converter A custom value converter to use for <code>String</code> to runtime type value conversions.
   * @param propNames Optional array of allowed property names. When empty, all properties are attempted.
   * @return The created configuration model.
   */
  public static <T> MapConfigModel<T> expect(Class<T> confInstanceType, ValueConverter converter,
      String... propNames) {
    if (confInstanceType == null) {
      throw new NullPointerException("Given configuration bean type is a null reference");
    } else if (converter == null) {
      throw new NullPointerException("Given value converter is a null reference");
    }

    validatePropName(propNames);

    BeanContext<T> beanFactory = new BeanContext<T>(confInstanceType, converter);
    Section section = new Section("*", new ValueMapSection(), new BeanConstructPropertyWriter(propNames));
    section.validate(beanFactory.getValidator());

    return new MapConfigModel<T>(beanFactory, section);
  }

  private final BeanContext<T> beanFactory;

  private final Section section;

  private MapConfigModel(BeanContext<T> beanFactory, Section section) {
    this.beanFactory = beanFactory;
    this.section = section;
  }

  /**
   * Informative method: provides the bean type that will be returned after conversion.
   * 
   * @return Configuration bean type.
   */
  public Class<T> getConfigBeanType() {
    return this.beanFactory.getBeanType();
  }

  /**
   * Parses the input stream expecting sections with configuration settings. Each section will be parsed as defined in
   * this model. Serious conflicts in stream can eventually trigger ConfigException, while minor conflicts (bad setting
   * lines) might just be logged as warnings.
   * <p>
   * The order of sections in the configuration file will not be preserved in the returned map.
   * <p>
   * Each section is parsed as rows of key-value pairs, and each section triggers creation of the target bean with the
   * read data.
   * <p>
   * At first, it attempts to call a constructor with as many constructor parameters as defined in section writer (a
   * parameter value will be read from the map where parameter name corresponds to map key). When this approach fails,
   * it attempts to initialize the bean using default constructor, and set the required (when parameter names are given
   * to writer) or all (otherwise) properties of the bean.
   * <p>
   * Failing to initialize bean or failing to set a declared property will raise ConfigException.
   * 
   * @param input Configuration stream. When null then ConfigException will be raised.
   * @return A new instance of configuration object with data set as defined in the stream.
   * @throws ConfigException Contains error and possibly also warning messages from parsing the stream.
   */
  public Map<String, T> parse(InputStream input) throws ConfigException {
    ReaderContext<T> ctx = new ReaderContext<T>(this.beanFactory, false, input);
    Map<String, T> result = new HashMap<String, T>();

    try {
      String key = ctx.toNextSection().inSection();

      while (key != null) {
        if (result.get(key) != null) {
          ctx.error("Section is already defined.").toNextSection();
        } else {
          this.section.parse(ctx);
          result.put(key, ctx.getBeanWriter().getBean());
        }

        key = ctx.inSection();
      }

    } catch (IOException e) {
      ctx.log("While processing configuration stream", e);

    } finally {
      try {
        input.close();
      } catch (IOException e) {
        ctx.log("While closing configuration stream", e);
      }
    }

    ctx.checkErrors();
    return result;
  }

  private static void validatePropName(String... propNames) {
    if (propNames == null) {
      throw new NullPointerException("Got a null reference for an array of bean property names");
    }
    for (String propName : propNames) {
      if (propName == null) {
        throw new NullPointerException("Got a null reference instead of a bean property name");
      } else if (propName.length() == 0) {
        throw new IllegalArgumentException("Got an empty string for a property name");
      } else if (propName.indexOf(' ') >= 0 || propName.indexOf('\t') >= 0 || propName.indexOf('\n') >= 0) {
        throw new IllegalArgumentException("Property name contains whitespace.");
      }
    }
  }

}
