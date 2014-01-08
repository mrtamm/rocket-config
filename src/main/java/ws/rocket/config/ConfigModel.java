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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import ws.rocket.config.reader.ReaderContext;
import ws.rocket.config.bean.BeanContext;
import ws.rocket.config.section.Section;
import ws.rocket.config.section.read.SectionReader;
import ws.rocket.config.section.read.ValueListSection;
import ws.rocket.config.section.read.ValueMapSection;
import ws.rocket.config.section.value.DefaultConverter;
import ws.rocket.config.section.value.ValueConverter;
import ws.rocket.config.section.write.BeanConstructorWriter;
import ws.rocket.config.section.write.MultiPropertyWriter;
import ws.rocket.config.section.write.SectionWriter;
import ws.rocket.config.section.write.SimplePropertyWriter;

/**
 * Configuration model holds the description of what structure a configuration file may have, how to parse it, and
 * where to write the configuration settings/values (in a bean object).
 * <p>
 * The configuration files consist of sections with name (in square brackets) and configuration lines per section. A
 * <code>SectionReader</code> attempts to interpret each non-blank line in corresponding section, tries to convert it
 * into desired runtime type and stores it in its buffer. Typically a section settings line either contains a full value
 * or a key-value pair (separated by equals-sign). Therefore, the values of a section are stored in a list or map (in
 * the reader object).
 * <p>
 * Once a section ends in the configuration file, the parsed data is immediately stored in the configuration bean. This
 * is handled by <code>SectionWriter</code> that uses <code>BeanWriter</code> for writing data to target bean.
 * <p>
 * All in all, configuration consists of sections with unique names. Their order must match in files (although the file
 * may omit some of them). Besides name, each section also has a section reader (interprets lines) and a section writer
 * (knows where to store collected information in the configuration data bean).
 * <p>
 * Configuration model class serves three purposes:
 * <ol>
 * <li>describes the expected configuration format</li>
 * <li>enables elementary validation of the constructed model</li>
 * <li>enables configuration file parsing</li>
 * </ol>
 *
 * @author Martti Tamm
 * 
 * @param <T> The target type that will hold the read configuration.
 */
public final class ConfigModel<T> {

  /**
   * The factory method for constructing a new model for given configuration bean type.
   * 
   * @param <T> The type of the configuration bean.
   * @param confInstanceType The type of the configuration bean.
   * @return The builder to continue with describing the sections.
   */
  public static <T> ConfigModelBuilder<T> expect(Class<T> confInstanceType) {
    return new ConfigModelBuilder<T>(confInstanceType, new DefaultConverter());
  }

  /**
   * The factory method for constructing a new model for given configuration bean type.
   * 
   * @param <T> The type of the configuration bean.
   * @param confInstanceType The type of the configuration bean.
   * @param converter A custom value converter to use for <code>String</code> to runtime type value conversions.
   * @return The builder to continue with describing the sections.
   */
  public static <T> ConfigModelBuilder<T> expect(Class<T> confInstanceType, ValueConverter converter) {
    return new ConfigModelBuilder<T>(confInstanceType, converter);
  }

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
  public static <T> ConfigModel<Map> expectMapOf(Class<T> confInstanceType, String... propNames) {
    return new ConfigModelBuilder<Map>(Map.class, new DefaultConverter()).section("*",
        new ValueMapSection(String.class, confInstanceType), null).ready();
  }

  private final BeanContext<T> beanFactory;

  private final Section[] sections;

  private ConfigModel(BeanContext<T> beanFactory, Section[] section) {
    this.beanFactory = beanFactory;
    this.sections = section;
  }

  /**
   * Informative method: provides access to the configuration sections of this model.
   * 
   * @return An array of configuration sections.
   */
  public Section[] getSections() {
    return Arrays.copyOf(this.sections, this.sections.length);
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
   * The sections in the configuration stream must be ordered the same way as in this model, however, sections in the
   * stream may omit some of those in the model. Duplicate sections or bad section names will raise ConfigException.
   * 
   * @param input Configuration stream. When null then ConfigException will be raised.
   * @return A new instance of configuration object with data set as defined in the stream.
   * @throws ConfigException Contains error and possibly also warning messages from parsing the stream.
   */
  public T parse(InputStream input) throws ConfigException {
    ReaderContext<T> ctx = new ReaderContext<T>(this.beanFactory, input);

    try {
      ctx.toNextSection();

      List<Section> expectedSections = new ArrayList<Section>(Arrays.asList(this.sections));

      while (ctx.inSection() != null) {
        boolean parsed = false;

        for (int i = 0; i < expectedSections.size(); i++) {
          if (expectedSections.get(i).getName().equals(ctx.inSection())) {
            expectedSections.get(i).parse(ctx);
            expectedSections.subList(0, i + 1).clear();
            parsed = true;
            break;
          }
        }

        if (!parsed) {
          ctx.error("Section was not parsed - it's name did not match, is in wrong order, or a duplicate");
          ctx.toNextSection();
        }
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

    return ctx.checkErrors().getBeanWriter().getBean();
  }

  /**
   * Configuration model builder defines the language for describing a configuration file sections and their content
   * parsing methods.
   * 
   * @param <T> The type of the configuration bean.
   */
  public static final class ConfigModelBuilder<T> {

    private final BeanContext<T> beanFactory;

    private final List<Section> sections = new ArrayList<Section>();

    private final FactoryImpl<T> factory = new FactoryImpl<T>(this);

    /**
     * Creates a new instance of model builder for given configuration bean type.
     * 
     * @param type The class of the configuration bean (for creating an instance of it during parsing).
     * @param valueConverter The value converter to use to convert <code>String</code> values to runtime types.
     */
    public ConfigModelBuilder(Class<T> type, ValueConverter valueConverter) {
      if (type == null) {
        throw new NullPointerException("Given configuration bean type is a null reference");
      } else if (valueConverter == null) {
        throw new NullPointerException("Given value converter is a null reference");
      }
      this.beanFactory = new BeanContext<T>(type, valueConverter);
    }

    /**
     * Adds a section definition with all the necessary handlers. This is recommended to use when custom section reader
     * and writer is used. Otherwise, the #section(String) method is recommended, as it is more readable and safer.
     * <p>
     * A <code>ModelException</code> will be raised when there is a data conflict between the reader or writer, or the
     * writer cannot write to destination bean
      * 
     * @param name Section name. Must not be empty and already used.
     * @param reader Section reader instance.
     * @param writer Section writer instance.
     * @return This model builder.
     */
    public ConfigModelBuilder<T> section(String name, SectionReader reader, SectionWriter writer) {
      validateSectionName(name);
      Section section = new Section(name.trim(), reader, writer);
      section.validate(this.beanFactory.getValidator());
      this.sections.add(section);
      return this;
    }

    /**
     * Starts a new section description block with given section name.
     * 
     * @param sectionName Section name. Must not be empty and already used.
     * @return A factory instance for choosing a reader for this section.
     */
    public ReaderFactory<T> section(String sectionName) {
      validateSectionName(sectionName);
      return this.factory.section(sectionName);
    }

    /**
     * Finalizes configuration model using the previously added sections.
     * 
     * @return A new configuration model instance with defined sections.
     */
    public ConfigModel<T> ready() {
      return new ConfigModel<T>(this.beanFactory, this.sections.toArray(new Section[this.sections.size()]));
    }

    private void validateSectionName(String name) {
      if (name == null) {
        throw new NullPointerException("Got a null reference for a section name");
      } else if (name.trim().length() == 0) {
        throw new IllegalArgumentException("Section name is empty");
      }
    }

    private final class FactoryImpl<T> implements ReaderFactory<T>, BeanWriter<T> {

      private final ConfigModelBuilder<T> builder;

      private String currentSection;

      private SectionReader currentReader;

      private FactoryImpl(ConfigModelBuilder<T> builder) {
        this.builder = builder;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ValueWriter<T> ofList() {
        return updateReader(new ValueListSection());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ValueWriter<T> ofList(Class<?> type) {
        return updateReader(new ValueListSection(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public BeanWriter<T> ofMap() {
        return updateReader(new ValueMapSection());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ValueWriter<T> ofMap(Class<?> valueType) {
        return ofMap(String.class, valueType);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ValueWriter<T> ofMap(Class<?> keyType, Class<?> valueType) {
        return updateReader(new ValueMapSection(keyType, valueType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ConfigModelBuilder<T> storeInBeanProps(String... propertyNames) {
        validatePropName(propertyNames);
        return updateWriter(new MultiPropertyWriter(propertyNames));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ConfigModelBuilder<T> storeInBeanOf(String propertyName, String... paramNames) {
        validatePropName(propertyName);
        return updateWriter(new BeanConstructorWriter(propertyName, paramNames));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ConfigModelBuilder<T> storeIn(String propertyName) {
        validatePropName(propertyName);
        return updateWriter(new SimplePropertyWriter(propertyName));
      }

      private ReaderFactory<T> section(String name) {
        if (this.currentSection != null) {
          throw new RuntimeException("Starting new configuration section [" + name
                  + "] while previous section [" + this.currentSection + "] is not completed.");
        }
        this.currentSection = name;
        return this;
      }

      private FactoryImpl<T> updateReader(SectionReader reader) {
        if (this.currentReader != null) {
          throw new RuntimeException("Cannot update reader when it's already set (in section ["
              + this.currentReader + "]).");
        }
        this.currentReader = reader;
        return this;
      }

      private ConfigModelBuilder<T> updateWriter(SectionWriter writer) {
        if (this.currentSection == null) {
          throw new RuntimeException("Cannot update writer when section name is not set.");
        } else if (this.currentReader == null) {
          throw new RuntimeException("Cannot set writer when section reader is not set (in section ["
                  + currentSection + "]).");
        }

        this.builder.section(this.currentSection, this.currentReader, writer);

        this.currentSection = null;
        this.currentReader = null;
        return this.builder;
      }

      private void validatePropName(String... propNames) {
        if (propNames == null) {
          throw new RuntimeException("Got a null reference for an array of bean property names");
        }
        for (String propName : propNames) {
          if (propName == null) {
            throw new RuntimeException("Got a null reference instead of a bean property name");
          } else if (propName.length() == 0) {
            throw new IllegalArgumentException("Got an empty string for a property name");
          } else if (propName.indexOf(' ') >= 0 || propName.indexOf('\t') >= 0 || propName.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("Property name contains whitespace.");
          }
        }
      }

    }

    /**
     * Factory for defining common section readers. This factory is for convenience and is optional.
     * 
     * @param <T> The type of the configuration bean.
     */
    public interface ReaderFactory<T> {

      /**
       * Defines that section data is a <code>String</code> value per line to be collected into
       * <code>java.util.List</code>.
       * 
       * @return A factory instance for defining section writer.
       */
      ValueWriter<T> ofList();

      /**
       * Defines that section data is a value (of given type) per line to be collected into
       * <code>java.util.List</code>.
       * 
       * @param type The target runtime type for list values.
       * @return A factory instance for defining section writer.
       */
      ValueWriter<T> ofList(Class<?> type);

      /**
       * Defines that section data is a key-value pair (separated by equal-sign) per line to be collected into
       * <code>java.util.Map</code>. Both key and value are expected to be <code>String</code>s.
       * 
       * @return A factory instance for defining section writer.
       */
      BeanWriter<T> ofMap();

      /**
       * Defines that section data is a key-value pair (separated by equal-sign) per line to be collected into
       * <code>java.util.Map</code>. The map key is expected to be <code>String</code>. The map value is expected to be
       * of <code>valueType</code>.
       * 
       * @param valueType The target runtime type for map values.
       * @return A factory instance for defining section writer.
       */
      ValueWriter<T> ofMap(Class<?> valueType);

      /**
       * Defines that section data is a key-value pair (separated by equal-sign) per line to be collected into
       * <code>java.util.Map</code>. The map key is expected to be of <code>keyType</code>. The map value is expected to
       * be of <code>valueType</code>.
       * 
       * @param keyType The target runtime type for map keys.
       * @param valueType The target runtime type for map values.
       * @return A factory instance for defining section writer.
       */
      ValueWriter<T> ofMap(Class<?> keyType, Class<?> valueType);
    }

    /**
     * Section writer factory contract to use when the section reader supports at least writing the gathered section
     * data to a configuration bean property.
     * 
     * @param <T> The type of the configuration bean.
     */
    public interface ValueWriter<T> {

      /**
       * The value (containing data from configuration section) will be written to given bean property. When the section
       * is stored as <code>List</code> it may get converted to array (when required by the target property type).
       * However, for section data stored as <code>Map</code> the property must also accept a <code>Map</code>.
       * 
       * @param propertyName The property name of the main configuration object.
       * @return Current configuration builder.
      */
      ConfigModelBuilder<T> storeIn(String propertyName);
    }

    /**
     * Section writer factory contract to use when the section reader passes on a <code>Map</code> collection. This
     * enables writing the gathered section data to multiple properties of a configuration bean, or the property type
     * can be an object type that takes in the section data as constructor arguments.
     * 
     * @param <T> The type of the configuration bean.
     */
    public interface BeanWriter<T> extends ValueWriter<T> {

      /**
       * The value as Map (containing data from configuration section) will be written to given bean properties. The
       * bean property names are expected to be contained in the map, or they will be skipped. Therefore, the bean
       * property names and keys in the configuration section must be identical.
       * <p>
       * The property names array may remain empty to let configuration handler attempt writing to bean properties by
       * using all the keys in the map (failures will be logged as warnings). It is more risky but sometimes justified.
       * <p>
       * <strong>Note: this kind of writing configuration settings only works when the Map contains keys and values as
       * String objects. The keys must be String to match with bean property names. The values must be String so that
       * value converter could convert them to target type, when necessary.</strong>
       * 
       * @param propertyNames The property names (also the map keys) of the bean to write to. May be an empty array.
       * @return Current configuration builder.
       */
      ConfigModelBuilder<T> storeInBeanProps(String... propertyNames);

      /**
       * The value as Map (containing data from configuration section) will be written to given bean property, which is
       * a custom type that needs to be created by calling its constructor with given parameter names. The property name
       * must therefore refer to a setter method with a class type (not an interface nor an abstract class!) as
       * parameter. The type must have preferably exactly one such constructor that takes the same amount of arguments
       * as is the length of <code>paramNames</code> array.
       * <p>
       * When resolving the values of these constructor arguments, the parameter names are looked up from the map of
       * section configuration and converted to target type, which is the constructor argument type in the same position
       * as the parameter name. However, when parameter names array is empty, the empty constructor will be called and
       * the data read from the section will be ignored.
       * <p>
       * <strong>Note: this kind of writing configuration settings only works when the Map contains keys and values as
       * String objects. The keys must be String to match with parameter names. The values must be String so that value
       * converter could convert them to target type, when necessary.</strong>
       * 
       * @param propertyName The property name of the main configuration object.
       * @param paramNames The class constructor parameter names (also the map keys) for creating the value object for
       * setting the property of the main configuration object. May be an empty array.
       * @return Current configuration builder.
       */
      ConfigModelBuilder<T> storeInBeanOf(String propertyName, String... paramNames);
    }

  }

}
