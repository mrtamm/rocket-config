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

package ws.rocket.config.section;

import java.io.IOException;
import ws.rocket.config.reader.ReaderContext;
import ws.rocket.config.reader.StreamReader;
import ws.rocket.config.bean.BeanValidator;
import ws.rocket.config.section.read.SectionReader;
import ws.rocket.config.section.write.SectionWriter;

/**
 * Section is a block of configuration file. This class contains information about section: its name, a class for
 * reading the data, a class for delivering the read data to target configuration bean.
 *
 * @author Martti Tamm
 */
public final class Section {

  private final String name;

  private final SectionReader reader;

  private final SectionWriter writer;

  /**
   * Creates a new instance with all the required parameters.
   * 
   * @param name The section name.
   * @param reader An object taking care of reading section lines.
   * @param writer An object for writing the read data to target bean.
   */
  public Section(String name, SectionReader reader, SectionWriter writer) {
    this.name = name;
    this.reader = reader;
    this.writer = writer;
  }

  /**
   * Provides the section name.
   * 
   * @return The section name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Provides the section reader.
   * 
   * @return The section reader.
   */
  public SectionReader getReader() {
    return this.reader;
  }

  /**
   * Provides the section writer.
   * 
   * @return The section writer.
   */
  public SectionWriter getWriter() {
    return this.writer;
  }

  /**
   * Validates that the writer supports the collection type returned by reader, and the target properties (if exist) are
   * also writable.
   * 
   * @param validator  The bean validation helper to use for testing bean properties.
   */
  public void validate(BeanValidator validator) {
    Class<?> collectionType = this.reader.getCollectionType();
    Class<?> valueType = this.reader.getValueType();
    this.writer.validate(collectionType, valueType, validator);
  }

  /**
   * Reads and parses a configuration section until next section or end of file.
   * 
   * @param context The parsing context.
   * @throws IOException An exception from the underlying stream.
   */
  public void parse(ReaderContext<?> context) throws IOException {
    StreamReader stream = context.getStreamReader();
    String line = null;

    while (!stream.isEndOfStream()) {
      line = stream.readLine();

      if (line == null || line.charAt(0) == '[') {
        break;
      }

      try {
        this.reader.readLine(line, context.getBeanWriter().getConverter());
      } catch (SectionValueException e) {
        context.log(e);
      }
    }

    this.writer.write(context.getBeanWriter(), this.reader.getResult(), this.reader.getValueType());
    this.reader.reset();

    context.inSection(line);
  }

}
