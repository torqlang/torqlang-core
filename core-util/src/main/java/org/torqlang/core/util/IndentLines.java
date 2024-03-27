/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.util.Arrays;

public final class IndentLines {

    public static String apply(String source, int indentLevel) {
        char[] indentChars = new char[indentLevel];
        Arrays.fill(indentChars, ' ');
        String indent = new String(indentChars);
        StringBuilder answer = new StringBuilder();
        if (source.length() > 0) {
            answer.append(indent);
        }
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '\n') {
                answer.append('\n');
                answer.append(indent);
            } else {
                answer.append(c);
            }
        }
        return answer.toString();
    }

}
