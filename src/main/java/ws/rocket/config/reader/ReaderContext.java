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

package ws.rocket.config.reader;

import java.io.IOException;
import java.io.InputStream;
import ws.rocket.config.ConfigException;
import ws.rocket.config.Messages;
import ws.rocket.config.bean.BeanContext;
import ws.rocket.config.bean.BeanWriter;
import ws.rocket.config.section.SectionValueException;

/**
 * Provides a helpful context binding all the needed components to simplify stream parsing process. Mainly this class
 * provides methods to remember the current section, to provide enhanced logging methods, and access to current stream
 * reader and target bean writer.
 *
 * @author Martti Tamm
 * @param <T> Target bean type.
 */
public final class ReaderContext<T> {

  private final StreamReader reader;

  private final BeanWriter<T> beanHandler;

  private final Messages msgs;

  private String sectionName;

  /**
   * Creates a new reader context.
   * 
   * @param beanFactory The target bean handler.
   * @param input The stream to parse (fails if null).
   * @throws ConfigException When there are problems with stream or configuration file syntax/data.
   */
  public ReaderContext(BeanContext<T> beanFactory, InputStream input) throws ConfigException {
    this.msgs = new Messages();

    if (input == null) {
      this.msgs.addError("Configuration input stream is null");
    }

    this.beanHandler = beanFactory.createWriter(this.msgs);

    checkErrors();

    this.reader = new StreamReader(input);
  }

  /**
   * Reads configuration file until a section declaration is reached. After this method completes, the stream can be
   * used to read the contents of the section.
   * 
   * @return Current reader context.
   * @throws IOException When underlying stream has problems.
   */
  public ReaderContext<T> toNextSection() throws IOException {
    String name;
    do {
      name =  this.reader.readLine();
    } while (name != null && (name.charAt(0) != '[' || name.charAt(name.length() - 1) != ']'));
    return inSection(name);
  }

  /**
   * Provides the section name (<strong>without</strong> square brackets) or null when no section declaration has been
   * read or when end of stream is reached.
   * 
   * @return The current section name or <code>null</code>.
   */
  public String inSection() {
    return this.sectionName;
  }

  /**
   * Updates the current section name with the given name (<strong>with</strong> square brackets). Usually the
   * <code>name</code> parameter corresponds to a line that begins with a square bracket. Therefore the parameter value
   * may also be <code>null</code>.
   * 
   * @param name The line containing section name. May be <code>null</code>.
   * @return Current reader context.
   */
  public ReaderContext<T> inSection(String name) {
    if (name == null) {
      this.sectionName = null;
    } else if (name.charAt(0) != '[' || name.charAt(name.length() - 1) != ']') {
      error("Bad section name format: " + name);
    } else {
      this.sectionName = name.substring(1, name.length() - 1).trim();
    }
    return this;
  }

  /**
   * Logs an error message. This method will also add information about current section and line number to the message.
   * 
   * @param msg The message to log.
   * @return Current reader context.
   */
  public ReaderContext<T> error(String msg) {
    this.msgs.addError(enrichMsg(msg));
    return this;
  }

  /**
   * Logs a warning message. This method will also add information about current section and line number to the message.
   * 
   * @param msg The message to log.
   * @return Current reader context.
   */
  public ReaderContext<T> warn(String msg) {
    this.msgs.addWarning(enrichMsg(msg));
    return this;
  }

  /**
   * Logs a value conversion exception. This method will also add information about current section and line number to
   * the message.
   * 
   * @param e The exception to log.
   * @return Current reader context.
   */
  public ReaderContext<T> log(SectionValueException e) {
    if (e.isWarning()) {
      warn(e.getMessage());
    } else {
      error(e.getMessage());
    }
    return this;
  }

  /**
   * Logs an exception regarding the underlying stream.
   * 
   * @param when A description when this message occurred.
   * @param e The exception to log.
   * @return Current reader context.
   */
  public ReaderContext<T> log(String when, IOException e) {
    this.error(when + ": " + e.getMessage());
    return this;
  }

  /**
   * Checks that there are no error messages logged. If there is any, this method will raise a
   * <code>ConfigException</code> to view the logged messages.
   * 
   * @return Current reader context.
   * @throws ConfigException When there are problems with the configuration file.
   */
  public ReaderContext<T> checkErrors() throws ConfigException {
    if (this.msgs.hasErrors()) {
      throw new ConfigException(this.msgs);
    }
    return this;
  }

  /**
   * Provides access to the current configuration file stream reader.
   * 
   * @return A stream reader.
   */
  public StreamReader getStreamReader() {
    return this.reader;
  }

  /**
   * Provides access to the current target bean writer.
   * 
   * @return A bean writer.
   */
  public BeanWriter<T> getBeanWriter() {
    return this.beanHandler;
  }

  private String enrichMsg(String msg) {
    StringBuilder sb = new StringBuilder(msg.length() + 40);
    sb.append('[');
    if (this.sectionName != null) {
      sb.append(this.sectionName);
    }
    sb.append(':').append(this.reader.getLineNumber()).append("]: ").append(msg);
    return sb.toString();
  }
}
