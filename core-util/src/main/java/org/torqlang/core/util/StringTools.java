/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.io.StringWriter;

public final class StringTools {

    public static void appendWithPadLeft(String arg, char pad, int width, StringBuilder sb) {
        int deficit = width - arg.length();
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < deficit; i++) {
            sb.append(pad);
        }
        sb.append(arg);
    }

    public static void appendWithPadLeft(String arg, char pad, int width, StringWriter sb) {
        int deficit = width - arg.length();
        for (int i = 0; i < deficit; i++) {
            sb.write(pad);
        }
        sb.write(arg);
    }

    public static void appendWithPadRight(String arg, char pad, int width, StringBuilder sb) {
        sb.append(arg);
        int deficit = width - arg.length();
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < deficit; i++) {
            sb.append(pad);
        }
    }

    public static void appendWithPadRight(String arg, char pad, int width, StringWriter sw) {
        sw.write(arg);
        int deficit = width - arg.length();
        for (int i = 0; i < deficit; i++) {
            sw.append(pad);
        }
    }

    /*
     * Append the given character as a four character hex string.
     */
    public static void appendHexString(char c, StringBuilder sb) {
        String hex = Integer.toHexString(c);
        appendWithPadLeft(hex, '0', 4, sb);
    }

    public static void appendHexString(char c, StringWriter sw) {
        String hex = Integer.toHexString(c);
        appendWithPadLeft(hex, '0', 4, sw);
    }

}
