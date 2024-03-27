/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.io.StringWriter;

/*
 * Control characters:
 *   C0 codes: \u0000 to \u001F
 *   DEL code: \u007F
 *   C1 codes: \u0080 to \u009F
 *
 * Common escape sequences:
 *   \r (carriage return CR, Unicode \u000d)
 *   \n (linefeed LF, Unicode \u000a)
 *   \t (horizontal tab HT, Unicode \u0009)
 *   \f (form feed FF, Unicode \u000c)
 *   \b (backspace BS, Unicode \u0008)
 */
public final class EscapeChar {

    public static void apply(char c, StringBuilder sb) {
        if (c == '\r') {
            sb.append("\\r");
        } else if (c == '\n') {
            sb.append("\\n");
        } else if (c == '\t') {
            sb.append("\\t");
        } else if (c == '\f') {
            sb.append("\\f");
        } else if (c == '\b') {
            sb.append("\\b");
        } else {
            sb.append("\\u");
            StringTools.appendHexString(c, sb);
        }
    }

    public static void apply(char c, StringWriter sw) {
        if (c == '\r') {
            sw.write("\\r");
        } else if (c == '\n') {
            sw.write("\\n");
        } else if (c == '\t') {
            sw.write("\\t");
        } else if (c == '\f') {
            sw.write("\\f");
        } else if (c == '\b') {
            sw.write("\\b");
        } else {
            sw.write("\\u");
            StringTools.appendHexString(c, sw);
        }
    }

}
