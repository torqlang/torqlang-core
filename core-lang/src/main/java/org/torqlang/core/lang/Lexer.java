/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import static org.torqlang.core.lang.MessageText.*;
import static org.torqlang.core.lang.SymbolsAndKeywords.*;

public final class Lexer {

    private final String source;

    private int charPos = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public final int charPos() {
        return charPos;
    }

    private boolean isDelimiterAt(int index) {
        return isOneCharSymbolAt(index) || isTwoCharSymbolAt(index) || isThreeCharSymbolAt(index);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    private boolean isOneCharSymbolAt(int index) {
        return index < source.length() && isOneCharSymbol(source.charAt(index));
    }

    private boolean isThreeCharSymbolAt(int index) {
        int index2 = index + 1;
        int index3 = index + 2;
        return index3 < source.length() &&
            isThreeCharSymbol(source.charAt(index), source.charAt(index2), source.charAt(index3));
    }

    private boolean isTwoCharSymbolAt(int index) {
        int index2 = index + 1;
        return index2 < source.length() &&
            isTwoCharSymbol(source.charAt(index), source.charAt(index2));
    }

    /*
     * // WS : [ \r\n\t\f\b]+ -> skip;
     */
    private boolean isWhiteSpace(char c) {
        //noinspection UnnecessaryUnicodeEscape
        if (c > '\u0020') {
            return false;
        }
        if (c == ' ') {
            return true;
        }
        if (c == '\r') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\t') {
            return true;
        }
        if (c == '\f') {
            return true;
        }
        return c == '\b';
    }

    private boolean isWhiteSpaceAt(int index) {
        return index < source.length() && isWhiteSpace(source.charAt(index));
    }

    public final LexerToken nextToken() {
        return nextToken(true);
    }

    public final LexerToken nextToken(boolean skipComments) {
        LexerToken answer;
        do {
            skipWhitespace();
            answer = parseToken();
        } while (skipComments && answer.type() == LexerTokenType.COMMENT_TOKEN);
        return answer;
    }

    /*
     * Precondition: charPos references a '"'
     */
    private LexerToken parseDoubleQuotedString() {
        int start = charPos;
        charPos++;
        while (charPos < source.length()) {
            if (source.charAt(charPos) == '"') {
                if (source.charAt(charPos - 1) != '\\') {
                    break;
                }
            }
            charPos++;
        }
        if (charPos == source.length()) {
            LexerToken invalidToken = new LexerToken(LexerTokenType.STR_TOKEN, source, start, charPos);
            throw new LexerError(invalidToken, STR_IS_MISSING_CLOSING_DOUBLE_QUOTE);
        }
        charPos++;
        return new LexerToken(LexerTokenType.STR_TOKEN, source, start, charPos);
    }

    /*
     * Precondition: charPos references the character following the character escape sequence "&\"
     * Postcondition: charPos references the first character past the full escape sequence
     */
    private void parseEscSeq() {
        // Verify there is a character following the escape '\\'
        if (charPos == source.length()) {
            LexerToken invalidToken = new LexerToken(LexerTokenType.CHAR_TOKEN, source, charPos - 1, charPos);
            throw new LexerError(invalidToken, INVALID_CHARACTER_LITERAL);
        }
        char escapedChar = source.charAt(charPos);
        // Accept the character following the escape '\\'
        charPos++;
        if (escapedChar == 'u') {
            // We need a start position in case of an error
            int start = charPos - 2;
            for (int i = 0; i < 4; i++) {
                if (charPos == source.length()) {
                    LexerToken invalidToken = new LexerToken(LexerTokenType.CHAR_TOKEN, source, start, charPos);
                    throw new LexerError(invalidToken, INVALID_UNICODE_LITERAL);
                }
                char nextHex = source.charAt(charPos);
                charPos++;  // accept escaped character
                if (!isHexDigit(nextHex)) {
                    LexerToken invalidToken = new LexerToken(LexerTokenType.CHAR_TOKEN, source, start, charPos);
                    throw new LexerError(invalidToken, INVALID_UNICODE_LITERAL);
                }
            }
        }
    }

    /*
     * Precondition: start is beginning of digits and stop is 'e' or 'E' of a scientific number
     * such as "1.234E+9".
     */
    private LexerToken parseExponentSuffix(int start, int stop) {
        stop++; // accept 'e' or 'E'
        if (stop == source.length()) {
            charPos = stop;
            LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
            throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
        }
        char c = source.charAt(stop);
        if (c == '-' || c == '+') {
            stop++; // accept sign '-' or '+'
            if (stop == source.length()) {
                charPos = stop;
                LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
                throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
            }
            c = source.charAt(stop);
        }
        if (!isDigit(c)) {
            charPos = stop;
            LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
            throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
        }
        while (++stop < source.length()) {
            if (!isDigit(source.charAt(stop))) {
                break;
            }
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
        }
        c = source.charAt(stop);
        stop++; // accept first character following digits
        charPos = stop; // charPos is now one past suffix
        if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
            return new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
        }
        // We have a nonsensical continuation of what looked like a floating point literal
        LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
        throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
    }

    /*
     * Precondition: start is beginning of number and stop is first digit of a fractional part
     */
    private LexerToken parseFractionalPart(int start, int stop) {
        // Accept first digit [0-9] and continue accepting digits until a non-digit is found
        while (++stop < source.length()) {
            if (!isDigit(source.charAt(stop))) {
                break;
            }
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
        }
        char c = source.charAt(stop);
        if (c == 'e' || c == 'E') {
            return parseExponentSuffix(start, stop);
        }
        LexerTokenType tokenType;
        stop++;
        if (c == 'f' || c == 'F') {
            tokenType = LexerTokenType.FLT_TOKEN;
        } else if (c == 'd' || c == 'D') {
            tokenType = LexerTokenType.FLT_TOKEN;
        } else if (c == 'm' || c == 'M') {
            tokenType = LexerTokenType.DEC_TOKEN;
        } else {
            LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, stop);
            throw new LexerError(invalidToken, "Floating point suffix must be one of [fFdDmM]");
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new LexerToken(tokenType, source, start, stop);
        }
        // We have a nonsensical continuation of what looked like a floating point literal
        charPos = stop + 1; // accept the nonsensical char as part of the value
        LexerToken invalidToken = new LexerToken(tokenType, source, start, charPos);
        if (tokenType == LexerTokenType.DEC_TOKEN) {
            throw new LexerError(invalidToken, INVALID_DECIMAL_NUMBER);
        }
        throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
    }

    /*
     * Precondition: charPos references the start of '0x' or '0X'
     */
    private LexerToken parseHexInteger() {
        int start = charPos;
        charPos = charPos + 2; // accept '0x' or '0X'
        while (charPos < source.length() && isHexDigit(source.charAt(charPos))) {
            charPos++;
        }
        int hexCount = charPos - start - 2;
        if (hexCount == 0) {
            LexerToken invalidToken = new LexerToken(LexerTokenType.INT_TOKEN, source, start, charPos);
            throw new LexerError(invalidToken, INVALID_HEXADECIMAL_NUMBER);
        }
        return parseIntegerSuffix(start, charPos);
    }

    /*
     * Precondition: start is beginning of digits and stop is one past string of digits
     */
    private LexerToken parseIntegerSuffix(int start, int stop) {
        if (stop == source.length()) {
            // End of File
            charPos = stop;
            return new LexerToken(LexerTokenType.INT_TOKEN, source, start, stop);
        }
        char c = source.charAt(stop);
        LexerTokenType tokenType;
        if (c == 'l' || c == 'L') {
            stop++;
            tokenType = LexerTokenType.INT_TOKEN;
        } else if (c == 'm' || c == 'M') {
            stop++;
            tokenType = LexerTokenType.DEC_TOKEN;
        } else {
            tokenType = LexerTokenType.INT_TOKEN;
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new LexerToken(tokenType, source, start, charPos);
        } else {
            // We have a nonsensical continuation of what looked like an integer literal
            charPos = stop + 1;
            LexerToken invalidToken = new LexerToken(LexerTokenType.INT_TOKEN, source, start, charPos);
            throw new LexerError(invalidToken, INVALID_INTEGER);
        }
    }

    /*
     * Precondition: charPos references the start of a keyword or identifier
     */
    private LexerToken parseKeywordOrIdent() {
        int start = charPos;
        int stop = charPos;
        while (stop < source.length() && !isWhiteSpaceAt(stop) && !isDelimiterAt(stop)) {
            stop++;
        }
        charPos = stop;
        if (SymbolsAndKeywords.isKeyword(source, start, stop)) {
            return new LexerToken(LexerTokenType.KEYWORD_TOKEN, source, start, stop);
        }
        char c = source.charAt(start);
        if (Character.isUpperCase(c) || Character.isLowerCase(c) || c == '_') {
            return new LexerToken(LexerTokenType.IDENT_TOKEN, source, start, stop);
        }
        LexerToken invalidToken = new LexerToken(LexerTokenType.UNKNOWN_TOKEN, source, start, stop);
        throw new LexerError(invalidToken, INVALID_TOKEN);
    }

    private LexerToken parseNonHexNumber() {
        int start = charPos;
        int stop = start + 1;
        while (stop < source.length() && isDigit(source.charAt(stop))) {
            stop++;
        }
        if (stop < source.length() && source.charAt(stop) == '.') {
            stop++; // accept the '.' char
            if (stop == source.length()) {
                // We have a nonsensical token at end-of-file: digits followed by a period
                charPos = stop;
                LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, charPos);
                throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
            }
            char c = source.charAt(stop);
            if (!isDigit(c)) {
                // We have a nonsensical token: digits and a period followed by a non-digit
                charPos = stop + 1; // accept the invalid character
                LexerToken invalidToken = new LexerToken(LexerTokenType.FLT_TOKEN, source, start, charPos);
                throw new LexerError(invalidToken, INVALID_FLOATING_POINT_NUMBER);
            }
            return parseFractionalPart(start, stop);
        }
        return parseIntegerSuffix(start, stop);
    }

    /*
     * Precondition: charPos references a digit
     */
    public final LexerToken parseNumber() {
        int start = charPos;
        int stop = charPos + 1;
        if (stop == source.length()) {
            // Accept a single digit integer token as the last token in the stream
            charPos = stop;
            return new LexerToken(LexerTokenType.INT_TOKEN, source, start, stop);
        }
        char digit = source.charAt(start);
        if (digit == '0') {
            char possibleXChar = source.charAt(stop);
            if (possibleXChar == 'x' || possibleXChar == 'X') {
                return parseHexInteger();
            }
        }
        return parseNonHexNumber();
    }

    /*
     * Precondition: charPos references a '`'
     */
    private LexerToken parseQuotedIdentifier() {
        int start = charPos;
        charPos++;
        while (charPos < source.length()) {
            if (source.charAt(charPos) == '`') {
                if (source.charAt(charPos - 1) != '\\') {
                    break;
                }
            }
            charPos++;
        }
        if (charPos == source.length()) {
            LexerToken invalidToken = new LexerToken(LexerTokenType.STR_TOKEN, source, start, charPos);
            throw new LexerError(invalidToken, IDENT_IS_MISSING_CLOSING_BACKTICK);
        }
        charPos++;
        return new LexerToken(LexerTokenType.IDENT_TOKEN, source, start, charPos);
    }

    /*
     * Precondition: charPos references a '\''
     */
    private LexerToken parseSingleQuotedString() {
        int start = charPos;
        charPos++;
        while (charPos < source.length()) {
            if (source.charAt(charPos) == '\'') {
                if (source.charAt(charPos - 1) != '\\') {
                    break;
                }
            }
            charPos++;
        }
        if (charPos == source.length()) {
            LexerToken invalidToken = new LexerToken(LexerTokenType.STR_TOKEN, source, start, charPos);
            throw new LexerError(invalidToken, STR_IS_MISSING_CLOSING_SINGLE_QUOTE);
        }
        charPos++;
        return new LexerToken(LexerTokenType.STR_TOKEN, source, start, charPos);
    }

    private LexerToken parseToken() {
        if (charPos == source.length()) {
            return new LexerToken(LexerTokenType.EOF_TOKEN, source, charPos, charPos);
        }
        char c = source.charAt(charPos);
        if (isDigit(c)) {
            return parseNumber();
        }
        if (c == '\'') {
            return parseSingleQuotedString();
        }
        if (c == '"') {
            return parseDoubleQuotedString();
        }
        if (c == '`') {
            return parseQuotedIdentifier();
        }
        int start = charPos;
        if (c == '/') {
            int c2i = start + 1;
            if (c2i < source.length()) {
                char c2 = source.charAt(c2i);
                if (c2 == '/') {
                    charPos = c2i + 1; // accept '//' string
                    while (charPos < source.length() && source.charAt(charPos) != '\n') {
                        charPos++;
                    }
                    return new LexerToken(LexerTokenType.COMMENT_TOKEN, source, start, charPos);
                }
                if (c2 == '*') {
                    charPos = c2i + 1; // accept '/*' string
                    while (charPos < source.length()) {
                        c = source.charAt(charPos);
                        charPos++; // accept a possible '*' char
                        if (c == '*' && charPos < source.length()) {
                            c2 = source.charAt(charPos);
                            charPos++; // accept a possible '/' char
                            if (c2 == '/') {
                                break;
                            }
                        }
                    }
                    // charPos is one past the block comment
                    if (source.charAt(charPos - 2) == '*' && source.charAt(charPos - 1) == '/') {
                        return new LexerToken(LexerTokenType.COMMENT_TOKEN, source, start, charPos);
                    }
                    LexerToken invalidToken = new LexerToken(LexerTokenType.COMMENT_TOKEN, source, start, charPos);
                    throw new LexerError(invalidToken, COMMENT_IS_MISSING_CLOSING_SEQUENCE);
                }
            }
        }
        if (isThreeCharSymbolAt(charPos)) {
            charPos += 3;
            return new LexerToken(LexerTokenType.THREE_CHAR_TOKEN, source, start, charPos);
        }
        if (isTwoCharSymbolAt(charPos)) {
            charPos += 2;
            return new LexerToken(LexerTokenType.TWO_CHAR_TOKEN, source, start, charPos);
        }
        if (isOneCharSymbolAt(charPos)) {
            charPos += 1;
            return new LexerToken(LexerTokenType.ONE_CHAR_TOKEN, source, start, charPos);
        }
        if (c == '&') {
            charPos += 1; // accept '&'
            if (charPos == source.length()) {
                LexerToken invalidToken = new LexerToken(LexerTokenType.CHAR_TOKEN, source, start, charPos);
                throw new LexerError(invalidToken, INVALID_CHARACTER_LITERAL);
            }
            char c2 = source.charAt(charPos);
            charPos += 1;  // accept character (either escape or actual character)
            if (c2 == '\\') {
                parseEscSeq();
            }
            return new LexerToken(LexerTokenType.CHAR_TOKEN, source, start, charPos);
        }
        return parseKeywordOrIdent();
    }

    public final void skipWhitespace() {
        while (charPos < source.length() && isWhiteSpace(source.charAt(charPos))) {
            charPos++;
        }
    }

    public final String source() {
        return source;
    }

}
