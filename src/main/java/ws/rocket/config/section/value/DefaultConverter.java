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

package ws.rocket.config.section.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ws.rocket.config.section.SectionValueException;

/**
 * Built-in support for value conversions from <code>String</code> to target runtime types.
 * 
 * <table style="border: 1px solid;">
 *   <tr>
 *     <th>Target type</th>
 *     <th>Source interpretation</th>
 *   </tr>
 *   <tr>
 *     <td><code>long<br>java.lang.Long</code></td>
 *     <td><code>Long.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>int<br>java.lang.Integer</code></td>
 *     <td><code>Integer.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>short<br>java.lang.Short</code></td>
 *     <td><code>Short.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>double<br>java.lang.Double</code></td>
 *     <td><code>Double.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>float<br>java.lang.Float</code></td>
 *     <td><code>Float.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>char<br>java.lang.Character</code></td>
 *     <td>First character of <em>source</em></td>
 *   </tr>
 *   <tr>
 *     <td><code>byte<br>java.lang.Byte</code></td>
 *     <td><code>Byte.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>boolean<br>java.lang.Boolean</code></td>
 *     <td><code>Boolean.valueOf(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.math.BigDecimal</code></td>
 *     <td><code>new BigDecimal(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.math.BigInteger</code></td>
 *     <td><code>new BigInteger(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.net.URI</code></td>
 *     <td><code>URI.create(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.net.URL</code></td>
 *     <td><code>new URL(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.lang.Class</code></td>
 *     <td><code>Class.forName(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.lang.Package</code></td>
 *     <td><code>Package.getPackage(<em>source</em>)</code></td>
 *   </tr>
 *   <tr>
 *     <td><code>java.util.Date</code></td>
 *     <td>
 *       Date value parsed using either of these formats:
 *       <br><em>yyyy</em>-<em>mo</em>-<em>dd</em>T<em>h24</em>:<em>mi</em>:<em>ss</em>
 *       <br><em>yyyy</em>-<em>mo</em>-<em>dd</em>
 *       <br>T<em>h24</em>:<em>mi</em>:<em>ss</em>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td><em>any other type</em></td>
 *     <td>an instance returned by calling the default constructor of <code>Class.forName(<em>source</em>)</code></td>
 *   </tr>
 * </table>
 *
 * @author Martti Tamm
 */
public final class DefaultConverter implements ValueConverter {

  private final SimpleDateFormat fmtDateTime = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

  private final SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");

  private final SimpleDateFormat fmtTime = new SimpleDateFormat("'T'hh:mm:ss");

  @Override
  public Object convert(String value, Class<?> targetType) throws SectionValueException {
    if (value == null || targetType == String.class) {
      return value;
    } else if (targetType == Long.class || targetType == long.class) {
      return Long.valueOf(value);
    } else if (targetType == Integer.class || targetType == int.class) {
      return Integer.valueOf(value);
    } else if (targetType == Short.class || targetType == short.class) {
      return Short.valueOf(value);
    } else if (targetType == Double.class || targetType == double.class) {
      return Double.valueOf(value);
    } else if (targetType == Float.class || targetType == float.class) {
      return Float.valueOf(value);
    } else if (targetType == Character.class || targetType == char.class) {
      return Character.valueOf(value.length() > 0 ? value.charAt(0) : '\0');
    } else if (targetType == Byte.class || targetType == byte.class) {
      return Byte.valueOf(value);
    } else if (targetType == Boolean.class || targetType == boolean.class) {
      return Boolean.valueOf(value);
    } else if (targetType == BigDecimal.class) {
      return new BigDecimal(value);
    } else if (targetType == BigInteger.class) {
      return new BigInteger(value);
    } else if (targetType == URI.class) {
      return URI.create(value);
    } else if (targetType.isEnum()) {
      Object result = null;
      for (Object enumVal : targetType.getEnumConstants()) {
        if (value.equals(enumVal.toString())) {
          result = enumVal;
        }
      }
      if (result == null) {
        throw new SectionValueException("There is no enum constant for '" + value + "' in " + targetType);
      }
      return result;
    } else if (targetType == URL.class) {
      try {
        return new URL(value);
      } catch (MalformedURLException e) {
        throw new SectionValueException("Could not parse URL from '" + value + "': " + e.getMessage());
      }
    } else if (targetType == Package.class) {
      return Package.getPackage(value);

    } else if (targetType == Date.class) {
      try {
        if (value.charAt(0) == 'T') {
          return this.fmtTime.parse(value);
        } else if (value.length() <= 10) {
          return this.fmtDate.parse(value);
        } else {
          return this.fmtDateTime.parse(value);
        }
      } catch (ParseException e) {
        throw new SectionValueException("Could not parse Date from '" + value + "': " + e.getMessage() + " at "
            + e.getErrorOffset());
      }
    }

    try {
      Class<?> type = Class.forName(value);

      if (targetType == Class.class) {
        return type;
      } else if (!targetType.isAssignableFrom(type)) {
        throw new SectionValueException(type + "' cannot be assigned to required type: " + targetType);
      }

      return type.newInstance();
    } catch (ClassNotFoundException e) {
      throw new SectionValueException("Cannot find and load class '" + value + "'");
    } catch (InstantiationException e) {
      throw new SectionValueException("Cannot instantiate class '" + value + "' using default constructor: " + e);
    } catch (IllegalAccessException e) {
      throw new SectionValueException("Cannot instantiate class '" + value + "' using default constructor: " + e);
    }
  }

}
