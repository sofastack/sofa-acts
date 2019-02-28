/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.test.acts.utils;

/**
 * Properties Parse Utils
 *
 */
import org.apache.commons.lang.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    private static final char[] hexDigit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String convert2String(Properties properties, boolean unicode) {
        if (properties == null) {
            return null;
        } else if (properties.isEmpty()) {
            return "";
        } else {
            StringWriter writer = new StringWriter();

            try {
                store(properties, writer, unicode);
            } catch (IOException var4) {
                return null;
            }

            return writer.toString();
        }
    }

    public static Properties restoreFromString(String string) {
        Properties properties = new Properties();
        if (StringUtils.isBlank(string)) {
            return properties;
        } else {
            try {
                properties.load(new ByteArrayInputStream(string.getBytes()));
            } catch (Exception var3) {
                ;
            }

            return properties;
        }
    }

    public static Properties restoreFromString(String string, String encoding) {
        Properties properties = new Properties();
        if (StringUtils.isBlank(string)) {
            return properties;
        } else {
            ByteArrayInputStream in = new ByteArrayInputStream(string.getBytes());

            try {
                load(properties, new InputStreamReader(in, encoding));
            } catch (Exception var5) {
                ;
            }

            return properties;
        }
    }

    public static String convert2String(Map<String, String> map, boolean unicode) {
        return convert2String(toProperties(map), unicode);
    }

    public static Map<String, String> restoreMap(String str) {
        return toMap(restoreFromString(str));
    }

    public static Properties toProperties(Map<String, String> map) {
        Properties properties = new Properties();
        if (map == null) {
            return properties;
        } else {
            Iterator var3 = map.keySet().iterator();

            while (var3.hasNext()) {
                String key = (String) var3.next();
                properties.setProperty(key, (String) map.get(key));
            }

            return properties;
        }
    }

    public static Map<String, String> toMap(Properties properties) {
        HashMap map = new HashMap();
        if (properties == null) {
            return map;
        } else {
            Iterator var3 = properties.keySet().iterator();

            while (var3.hasNext()) {
                Object key = var3.next();
                map.put((String) key, properties.getProperty((String) key));
            }

            return map;
        }
    }

    public static void store(Properties properties, OutputStream out, String encoding)
                                                                                      throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
        store(properties, writer, (String) null, (Date) null, true);
    }

    public static void store(Properties properties, OutputStream out, String header, Date date,
                             String encoding, boolean unicodes) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
        store(properties, writer, header, date, unicodes);
    }

    public static void store(Properties properties, OutputStream out, String encoding,
                             boolean unicodes) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
        store(properties, writer, (String) null, (Date) null, unicodes);
    }

    public static void store(Properties properties, Writer writer, boolean unicodes)
                                                                                    throws IOException {
        store(properties, writer, (String) null, (Date) null, unicodes);
    }

    public static void store(Properties properties, Writer writer, String header, Date date,
                             boolean unicodes) throws IOException {
        BufferedWriter awriter = new BufferedWriter(writer);
        if (header != null) {
            writeln(awriter, "#" + header);
        }

        if (date != null) {
            writeln(awriter, "#" + date.toString());
        }

        String val;
        String key1;
        for (Iterator var7 = properties.keySet().iterator(); var7.hasNext(); writeln(awriter, key1
                                                                                              + "="
                                                                                              + val)) {
            Object key = var7.next();
            val = properties.getProperty((String) key);
            key1 = saveConvert((String) key, true);
            if (unicodes) {
                val = saveConvert(val, false);
            }
        }

        awriter.flush();
    }

    public static void load(Properties properties, InputStream inStream, String encoding)
                                                                                         throws IOException {
        InputStreamReader reader = new InputStreamReader(inStream, encoding);
        load(properties, reader);
    }

    public static void load(Properties properties, Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);

        while (true) {
            String line;
            int len;
            int keyStart;
            char firstChar;
            do {
                do {
                    do {
                        do {
                            line = in.readLine();
                            if (line == null) {
                                return;
                            }
                        } while (line.length() <= 0);

                        len = line.length();

                        for (keyStart = 0; keyStart < len
                                           && " \t\r\n\f".indexOf(line.charAt(keyStart)) != -1; ++keyStart) {
                            ;
                        }
                    } while (keyStart == len);

                    firstChar = line.charAt(keyStart);
                } while (firstChar == 35);
            } while (firstChar == 33);

            while (continueLine(line)) {
                String separatorIndex = in.readLine();
                if (separatorIndex == null) {
                    separatorIndex = "";
                }

                String valueIndex = line.substring(0, len - 1);

                int key;
                for (key = 0; key < separatorIndex.length()
                              && " \t\r\n\f".indexOf(separatorIndex.charAt(key)) != -1; ++key) {
                    ;
                }

                separatorIndex = separatorIndex.substring(key, separatorIndex.length());
                line = new String(valueIndex + separatorIndex);
                len = line.length();
            }

            int var11;
            for (var11 = keyStart; var11 < len; ++var11) {
                char var12 = line.charAt(var11);
                if (var12 == 92) {
                    ++var11;
                } else if ("=: \t\r\n\f".indexOf(var12) != -1) {
                    break;
                }
            }

            int var13;
            for (var13 = var11; var13 < len && " \t\r\n\f".indexOf(line.charAt(var13)) != -1; ++var13) {
                ;
            }

            if (var13 < len && "=:".indexOf(line.charAt(var13)) != -1) {
                ++var13;
            }

            while (var13 < len && " \t\r\n\f".indexOf(line.charAt(var13)) != -1) {
                ++var13;
            }

            String var14 = line.substring(keyStart, var11);
            String value = var11 < len ? line.substring(var13, len) : "";
            var14 = loadConvert(var14);
            value = loadConvert(value);
            properties.put(var14, value);
        }
    }

    private static boolean continueLine(String line) {
        int slashCount = 0;

        for (int index = line.length() - 1; index >= 0 && line.charAt(index--) == 92; ++slashCount) {
            ;
        }

        return slashCount % 2 == 1;
    }

    private static String loadConvert(String theString) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        int x = 0;

        while (true) {
            while (true) {
                while (x < len) {
                    char aChar = theString.charAt(x++);
                    if (aChar == 92) {
                        aChar = theString.charAt(x++);
                        if (aChar == 117) {
                            int value = 0;

                            for (int i = 0; i < 4; ++i) {
                                aChar = theString.charAt(x++);
                                switch (aChar) {
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                        value = (value << 4) + aChar - 48;
                                        break;
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F':
                                        value = (value << 4) + 10 + aChar - 65;
                                        break;
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f':
                                        value = (value << 4) + 10 + aChar - 97;
                                        break;
                                    case ':':
                                    case ';':
                                    case '<':
                                    case '=':
                                    case '>':
                                    case '?':
                                    case '@':
                                    case 'G':
                                    case 'H':
                                    case 'I':
                                    case 'J':
                                    case 'K':
                                    case 'L':
                                    case 'M':
                                    case 'N':
                                    case 'O':
                                    case 'P':
                                    case 'Q':
                                    case 'R':
                                    case 'S':
                                    case 'T':
                                    case 'U':
                                    case 'V':
                                    case 'W':
                                    case 'X':
                                    case 'Y':
                                    case 'Z':
                                    case '[':
                                    case '\\':
                                    case ']':
                                    case '^':
                                    case '_':
                                    case '`':
                                    default:
                                        throw new IllegalArgumentException(
                                            "Malformed \\uxxxx encoding.");
                                }
                            }

                            outBuffer.append((char) value);
                        } else {
                            if (aChar == 116) {
                                aChar = 9;
                            } else if (aChar == 114) {
                                aChar = 13;
                            } else if (aChar == 110) {
                                aChar = 10;
                            } else if (aChar == 102) {
                                aChar = 12;
                            }

                            outBuffer.append(aChar);
                        }
                    } else {
                        outBuffer.append(aChar);
                    }
                }

                return outBuffer.toString();
            }
        }
    }

    private static void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

    private static String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len * 2);

        for (int x = 0; x < len; ++x) {
            char aChar = theString.charAt(x);
            switch (aChar) {
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }

                    outBuffer.append(' ');
                    break;
                case '\\':
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    break;
                default:
                    if (aChar >= 32 && aChar <= 126) {
                        if ("=: \t\r\n\f#!".indexOf(aChar) != -1) {
                            outBuffer.append('\\');
                        }

                        outBuffer.append(aChar);
                    } else {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex(aChar >> 12 & 15));
                        outBuffer.append(toHex(aChar >> 8 & 15));
                        outBuffer.append(toHex(aChar >> 4 & 15));
                        outBuffer.append(toHex(aChar & 15));
                    }
            }
        }

        return outBuffer.toString();
    }

    private static char toHex(int nibble) {
        return hexDigit[nibble & 15];
    }
}