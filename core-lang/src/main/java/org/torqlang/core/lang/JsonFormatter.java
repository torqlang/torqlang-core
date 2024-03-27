/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.EscapeChar;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JsonFormatter {

    public static final JsonFormatter SINGLETON = new JsonFormatter();

    private JsonFormatter() {
    }

    public final String format(Object jsonValue) {
        try (StringWriter sw = new StringWriter()) {
            format(jsonValue, sw);
            return sw.toString();
        } catch (RuntimeException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new IllegalStateException(exc);
        }
    }

    private void quote(String source, StringWriter sw) {
        sw.append('"');
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            //noinspection UnnecessaryUnicodeEscape
            if (c < '\u0020') {
                EscapeChar.apply(c, sw);
            } else {
                if (c == '\\') {
                    sw.write("\\\\");
                } else if (c == '"') {
                    sw.write("\\\"");
                } else {
                    sw.write(c);
                }
            }
        }
        sw.write('"');
    }

    private void format(Object jsonValue, StringWriter sw) {
        if (jsonValue instanceof String jsonString) {
            quote(jsonString, sw);
        } else if (jsonValue instanceof Number jsonNumber) {
            formatNumber(jsonNumber, sw);
        } else if (jsonValue instanceof Boolean jsonBoolean) {
            sw.write(Boolean.toString(jsonBoolean));
        } else if (jsonValue instanceof Map<?, ?> jsonMap) {
            formatObject(jsonMap, sw);
        } else if (jsonValue instanceof List<?> jsonList) {
            formatArray(jsonList, sw);
        } else if (jsonValue instanceof LocalDate jsonDate) {
            formatUsingToString(jsonDate, sw);
        } else if (jsonValue == null || jsonValue == JsonNull.SINGLETON) {
            sw.write("null");
        } else {
            throw new IllegalArgumentException("Invalid JSON request: " + jsonValue);
        }
    }

    private void formatArray(List<?> jsonArray, StringWriter sw) {
        sw.write('[');
        Iterator<?> i = jsonArray.iterator();
        while (i.hasNext()) {
            format(i.next(), sw);
            if (i.hasNext()) {
                sw.write(',');
            }
        }
        sw.write(']');
    }

    private void formatDouble(double n, StringWriter sw) {
        String s = String.format("%.16f", n);
        // Trim trailing zeros
        int stop = s.length();
        while (stop > 3) {
            int next = stop - 1;
            if (s.charAt(next) != '0' || s.charAt(next - 1) == '.') {
                break;
            }
            stop = next;
        }
        sw.append(s.substring(0, stop));
    }

    private void formatLong(long n, StringWriter sw) {
        sw.append(String.format("%d", n));
    }

    private void formatNumber(Number n, StringWriter sw) {
        if (n instanceof Double || n instanceof Float) {
            formatDouble(n.doubleValue(), sw);
        } else if (n instanceof BigDecimal bigDecimal) {
            formatUsingToString(bigDecimal, sw);
        } else {
            formatLong(n.longValue(), sw);
        }
    }

    private void formatObject(Map<?, ?> jsonObject, StringWriter sw) {
        sw.write('{');
        Iterator<? extends Map.Entry<?, ?>> i = jsonObject.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<?, ?> e = i.next();
            String k = (String) e.getKey();
            quote(k, sw);
            sw.write(':');
            format(e.getValue(), sw);
            if (i.hasNext()) {
                sw.write(',');
            }
        }
        sw.write('}');
    }

    private void formatUsingToString(Object obj, StringWriter sw) {
        quote(obj.toString(), sw);
    }

}
