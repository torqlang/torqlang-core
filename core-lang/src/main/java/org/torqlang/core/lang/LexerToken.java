/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

import java.util.NoSuchElementException;

/**
 * Tokens that contain numeric values or quoted literals are marked as invalid when their content is invalid.
 * Other tokens, such as character symbols, are never marked as invalid.
 */
public final class LexerToken implements SourceSpan {

    private final LexerTokenType type;
    private final String source;
    private final int begin;
    private final int end;

    private int hash = 0;

    public LexerToken(LexerTokenType type, String source, int begin, int end) {
        this.type = type;
        this.source = source;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public final int begin() {
        return begin;
    }

    @Override
    public final int end() {
        return end;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LexerToken otherToken = (LexerToken) other;
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

    public final boolean isChar() {
        return type == LexerTokenType.CHAR_TOKEN;
    }

    public final boolean isComment() {
        return type == LexerTokenType.COMMENT_TOKEN;
    }

    public final boolean isComment(String text) {
        return type == LexerTokenType.COMMENT_TOKEN && substringEquals(text);
    }

    public final boolean isContextualKeyword(String word) {
        return type == LexerTokenType.IDENT_TOKEN &&
            SymbolsAndKeywords.isContextualKeyword(source, begin, end) &&
            substringEquals(word);
    }

    public final boolean isDec() {
        return type == LexerTokenType.DEC_TOKEN;
    }

    public final boolean isDec(String value) {
        return type == LexerTokenType.DEC_TOKEN && substringEquals(value);
    }

    public final boolean isEof() {
        return type == LexerTokenType.EOF_TOKEN;
    }

    public final boolean isFlt() {
        return type == LexerTokenType.FLT_TOKEN;
    }

    public final boolean isFlt(String value) {
        return type == LexerTokenType.FLT_TOKEN && substringEquals(value);
    }

    public final boolean isIdent() {
        return type == LexerTokenType.IDENT_TOKEN;
    }

    public final boolean isIdent(String ident) {
        return type == LexerTokenType.IDENT_TOKEN && substringEquals(ident);
    }

    public final boolean isInt(String value) {
        return type == LexerTokenType.INT_TOKEN && substringEquals(value);
    }

    public final boolean isInt() {
        return type == LexerTokenType.INT_TOKEN;
    }

    public final boolean isKeyword() {
        return type == LexerTokenType.KEYWORD_TOKEN;
    }

    public final boolean isKeyword(String keyword) {
        return type == LexerTokenType.KEYWORD_TOKEN && substringEquals(keyword);
    }

    public final boolean isOneCharSymbol() {
        return type == LexerTokenType.ONE_CHAR_TOKEN;
    }

    public final boolean isOneCharSymbol(char symbol) {
        return type == LexerTokenType.ONE_CHAR_TOKEN && firstChar() == symbol;
    }

    public final boolean isStr() {
        return type == LexerTokenType.STR_TOKEN;
    }

    public final boolean isStr(String value) {
        return type == LexerTokenType.STR_TOKEN && substringEquals(value);
    }

    public final boolean isThreeCharSymbol() {
        return type == LexerTokenType.THREE_CHAR_TOKEN;
    }

    public final boolean isThreeCharSymbol(String symbol) {
        return type == LexerTokenType.THREE_CHAR_TOKEN && substringEquals(symbol);
    }

    public final boolean isTwoCharSymbol() {
        return type == LexerTokenType.TWO_CHAR_TOKEN;
    }

    public final boolean isTwoCharSymbol(String symbol) {
        return type == LexerTokenType.TWO_CHAR_TOKEN && substringEquals(symbol);
    }

    public final int length() {
        return end - begin;
    }

    @Override
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

    @Override
    public final LexerToken toSourceSpanBegin() {
        return this;
    }

    @Override
    public final LexerToken toSourceSpanEnd() {
        return this;
    }

    public final String toString() {
        return "[" + begin + ", " + end + "] " + type + ": " + substring();
    }

    public final LexerTokenType type() {
        return type;
    }

}