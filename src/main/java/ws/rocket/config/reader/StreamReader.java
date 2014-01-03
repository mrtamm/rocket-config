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
 * The stream reader takes care of reading input line-by-line, skipping all blank lines and comments.
 * <p>
 * White-space rules: blank lines, white-space before or after symbols on a line are ignored.
 * <p>
 * Comments: everything on a line that starts with symbol # is ignored until the end of line.
 * <p>
 * Stream closing: although this class does its best effort to close the used stream, users of this class must also try
 * to close the stream, too.
 * <p>
 * Encoding: this reader does not encode/decode the data read.
 *
 * @author Martti Tamm
 */
public final class StreamReader {

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
   * Attempts to read next non-blank line. When data is found, white-space around the returned line is guaranteed to be
   * removed. When no more lines are found, <code>null</code> will be returned (even if called multiple times).
   * 
   * @return The read line (non-blank) or <code>null</code>.
   * @throws IOException When the underlying stream reports problems.
   */
  public String readLine() throws IOException {
    return this.endOfStream ? null : readNonEmptyLine();
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

  private String readNonEmptyLine() throws IOException {
    StringBuilder buffer = new StringBuilder();

    // Repeat until a line is read or stream end is reached.
    do {
      this.line++;
      int nextChar = this.input.read();
      boolean comment = false;

      // Repeat reading a line until the end of line or end of file.
      // Preceding whitespace and comments are not read.
      while (nextChar != '\n' && nextChar != -1) {
        if (nextChar == '#') {
          comment = true;
        } else if (nextChar == -1) {
          this.input.close();
          break;
        } else if (!comment && !(Character.isWhitespace(nextChar) && buffer.length() == 0)) {
          buffer.appendCodePoint(nextChar);
        }

        nextChar = this.input.read();
      }

      this.endOfStream = nextChar == -1;

    } while (!this.endOfStream && buffer.length() == 0);

    return buffer.length() > 0 ? bufferToString(buffer) : null;
  }

  private static String bufferToString(StringBuilder buffer) {
    for (int i = buffer.length() - 1; i >= 0 && Character.isWhitespace(buffer.codePointAt(i)); i--) {
      buffer.deleteCharAt(i);
    }
    return buffer.toString();
  }

}
