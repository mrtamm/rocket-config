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

import java.io.PrintStream;
import java.util.Date;

/**
 * A helper class to uniformly write out the configuration model and expected values.
 *
 * @author Martti Tamm
 */
public final class StreamWriter {

  /**
   * Initializes a writer and writes out the standard header.
   * <p>
   * Note: the header consists of comment-lines describing the configuration file, usually the target bean type. This
   * header might alter from release to release.
   *
   * @param out            The stream where to write.
   * @param configBeanType The target configuration bean type; may be null.
   * @return The created writer.
   */
  public static StreamWriter init(PrintStream out, Class<?> configBeanType) {
    StreamWriter writer = new StreamWriter(out);
    writer.comment("Generated on " + new Date());
    if (configBeanType == null) {
      writer.comment("Target bean type is unspecified");
    } else {
      writer.comment("Target bean: " + configBeanType.getName());
    }
    return writer;
  }

  private final PrintStream out;

  /**
   * Creates a new instance.
   * <p>
   * This constructor does not update the stream. To use a standard head, please refer to
   * {@link #init(java.io.PrintStream, java.lang.Class)}.
   *
   * @param out The stream where to write.
   * @see #init(java.io.PrintStream, java.lang.Class)
   */
  public StreamWriter(PrintStream out) {
    if (out == null) {
      throw new NullPointerException("Stream to write to is null");
    }
    this.out = out;
  }

  /**
   * Writes out the provided comment. The argument does not need to contain comment sign.
   *
   * @param comment The text to be shown after the comment symbol.
   * @return This writer.
   */
  public StreamWriter comment(String comment) {
    return line("# " + comment);
  }

  /**
   * Writes out a standard section name line with given section name.
   *
   * @param name The section name without square brackets.
   * @return This writer.
   */
  public StreamWriter section(String name) {
    return line(null).line("[" + name + "]");
  }

  /**
   * Writes out a comment line describing the expected name-value pairs.
   *
   * @param names The setting names (may be empty or null).
   * @return This writer.
   */
  public StreamWriter nameValues(String[] names) {
    if (names == null || names.length == 0) {
      comment("key = value pairs");
    } else {
      for (String name : names) {
        line(name + " = ");
      }
    }
    return this;
  }

  /**
   * Writes out a comment line describing the expected values.
   *
   * @param valueType The expected value type (required).
   * @return This writer.
   */
  public StreamWriter values(Class<?> valueType) {
    return comment("Values of " + valueType.getName());
  }

  /**
   * Writes a line with given text. Using custom line breaks in the provided text is not checked, nor recommended.
   * <p>
   * The line break depends on the stream setup that this object uses.
   *
   * @param line The text to write on the line. May be null.
   * @return This writer.
   */
  public StreamWriter line(String line) {
    if (line == null) {
      this.out.println();
    } else {
      this.out.println(line);
    }
    return this;
  }

}
