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

/**
 * The stream reader takes care of reading input line-by-line.
 * <p>
 * Comments: everything on a line that starts with symbol # is ignored until the end of line. The symbol can be escaped
 * with backward-slash (e.g.: \#)
 * <p>
 * Stream closing: although this class does its best effort to close the used stream, users of this class must also try
 * to close the stream, too.
 * <p>
 * Encoding: this reader does not encode/decode the data read.
 *
 * @author Martti Tamm
 */
public final class StreamReader {

  private final StringBuilder buffer = new StringBuilder();

  private final InputStream input;

  private int line;

  private boolean endOfStream;

  /**
   * Creates a new instance of stream that parses the given input stream. The given stream must not be null.
   *
   * @param input The stream to parse.
   */
  public StreamReader(InputStream input) {
    if (input == null) {
      throw new NullPointerException("Given input stream is a null reference");
    }
    this.input = input;
  }

  /**
   * Attempts to read next line. The line will be returned as-is, except that a comment, when present, will be removed
   * together with comment symbol. Also line breaks won't be included in return values. When no more lines are found,
   * <code>null</code> will be returned (even if called multiple times).
   *
   * @return The read line or <code>null</code>.
   * @throws IOException When the underlying stream reports problems.
   */
  public String readLine() throws IOException {
    if (this.endOfStream) {
      return null;
    }

    this.line++;

    boolean comment = false;
    boolean escapeCharBefore = false;
    int nextChar = this.input.read();

    // Repeat reading a line until the end of line or end of file.
    // Comments will be skipped.
    while (nextChar != '\n' && nextChar != -1) {

      if (nextChar == -1) {
        this.input.close();
        break;

      } else if (Character.getType(nextChar) == Character.LINE_SEPARATOR) {
        if (nextChar != '\r') {
          break;
        }

      } else if (comment) {
        comment = true; // Do nothing; shields from the rest.

      } else if (nextChar == '#') {
        if (escapeCharBefore) {
          this.buffer.setLength(this.buffer.length() - 1);
          this.buffer.appendCodePoint(nextChar);
        } else {
          comment = true;
        }

      } else {
        this.buffer.appendCodePoint(nextChar);
      }

      escapeCharBefore = nextChar == '\\';
      nextChar = this.input.read();
    }

    this.endOfStream = nextChar == -1;

    String result = null;
    if (!this.endOfStream || this.buffer.length() > 0) {
      result = this.buffer.toString();
    }

    this.buffer.setLength(0);
    return result;
  }

  /**
   * Provides the line number of the last returned line. When nothing is read yet, zero is returned. In case of empty
   * file, the line number after reading is one.
   *
   * @return The line number of last returned line.
   */
  public int getLineNumber() {
    return this.line;
  }

  /**
   * Reports whether the underlying stream is known to be consumed or not. This method does not have any effect on the
   * stream.
   *
   * @return A Boolean that is <code>true</code> when stream is consumed.
   */
  public boolean isEndOfStream() {
    return this.endOfStream;
  }

}
