/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.NoSuchElementException;

public final class JsonLexerToken {

    private final JsonLexerTokenType type;
    private final String source;
    private final int begin;
    private final int end;

    private int hash = 0;

    public JsonLexerToken(JsonLexerTokenType type, String source, int begin, int end) {
        this.type = type;
        this.source = source;
        this.begin = begin;
        this.end = end;
    }

    public final int begin() {
        return begin;
    }

    public final int end() {
        return end;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        JsonLexerToken otherToken = (JsonLexerToken) other;
        if (type != otherToken.type) return false;
        if (length() != otherToken.length()) return false;
        int otherIndex = otherToken.begin;
        for (int i = begin; i < end; i++) {
            if (source.charAt(i) != otherToken.source.charAt(otherIndex)) {
                return false;
            }
            otherIndex++;
        }
        return true;
    }

    public final char firstChar() {
        if (begin == source.length()) {
            throw new NoSuchElementException(Integer.toString(begin));
        }
        return source.charAt(begin);
    }

    public final boolean firstCharEquals(char c) {
        return firstChar() == c;
    }

    @Override
    public final int hashCode() {
        if (hash != 0) {
            return hash;
        }
        int h = 1;
        for (int i = begin; i < end; i++) {
            char c = source.charAt(i);
            h = 31 * h + c;
        }
        hash = h == 0 ? -1 : h; // do not allow a zero hash
        return hash;
    }

    public final boolean isColonChar() {
        return type == JsonLexerTokenType.DELIMITER && source.charAt(begin) == ':';
    }

    public final boolean isCommaChar() {
        return type == JsonLexerTokenType.DELIMITER && source.charAt(begin) == ',';
    }

    public final boolean isRightBraceChar() {
        return type == JsonLexerTokenType.DELIMITER && source.charAt(begin) == '}';
    }

    public final boolean isRightBracketChar() {
        return type == JsonLexerTokenType.DELIMITER && source.charAt(begin) == ']';
    }

    public final int length() {
        return end - begin;
    }

    public final String source() {
        return source;
    }

    public final String substring() {
        return source.substring(begin, end);
    }

    public final char substringCharAt(int index) {
        int sourceIndex = begin + index;
        if (sourceIndex >= source.length()) {
            throw new NoSuchElementException(Integer.toString(sourceIndex));
        }
        return source.charAt(sourceIndex);
    }

    public final boolean substringEquals(String value) {
        if (length() != value.length()) {
            return false;
        }
        int x = 0;
        for (int i = begin; i < end; i++) {
            if (source.charAt(i) != value.charAt(x)) {
                return false;
            }
            x++;
        }
        return true;
    }

    public final String toString() {
        return "[" + begin + ", " + end + "] " + type + ": " + substring();
    }

    public final JsonLexerTokenType type() {
        return type;
    }

}
