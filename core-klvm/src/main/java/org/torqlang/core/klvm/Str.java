/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.EscapeChar;

import java.util.Set;

public final class Str implements Literal {

    private static final ObjProcTable<Str> objProcTable = ObjProcTable.<Str>builder()
        .addEntry(Str.of("substring"), StrPack::objSubstring)
        .build();

    public final String value;

    private Str(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public static Str of(String value) {
        return new Str(value);
    }

    public static String quote(String value, char delimiter) {
        StringBuilder sb = new StringBuilder(value.length() * 2 + 2);
        quote(value, delimiter, sb);
        return sb.toString();
    }

    public static void quote(String source, char delimiter, StringBuilder sb) {
        sb.append(delimiter);
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            //noinspection UnnecessaryUnicodeEscape
            if (c < '\u0020') {
                EscapeChar.apply(c, sb);
            } else {
                if (c == '\\') {
                    sb.append("\\\\");
                } else if (c == '\'') {
                    sb.append("\\'");
                } else if (c == '"') {
                    sb.append("\\\"");
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append(delimiter);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitStr(this, state);
    }

    @Override
    public final Str add(Value addend) {
        return Str.of(addend.appendToString(this.value));
    }

    @Override
    public final String appendToString(String string) {
        return string + value;
    }

    @Override
    public final int compareValueTo(Value right) {
        if (!(right instanceof Str s)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_STR);
        }
        return value.compareTo(s.value);
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        return this.equals(operand);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Str that = (Str) other;
        return value.equals(that.value);
    }

    @Override
    public final String formatValue() {
        return value;
    }

    @Override
    public final Bool greaterThan(Value right) {
        if (!(right instanceof Str s)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_STR);
        }
        return Bool.of(value.compareTo(s.value) > 0);
    }

    @Override
    public final Bool greaterThanOrEqualTo(Value right) {
        if (!(right instanceof Str s)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_STR);
        }
        return Bool.of(value.compareTo(s.value) >= 0);
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final Bool lessThan(Value right) {
        if (!(right instanceof Str s)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_STR);
        }
        return Bool.of(value.compareTo(s.value) < 0);
    }

    @Override
    public final Bool lessThanOrEqualTo(Value right) {
        if (!(right instanceof Str s)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_STR);
        }
        return Bool.of(value.compareTo(s.value) <= 0);
    }

    @Override
    public final Proc select(Feature feature) {
        return objProcTable.selectAndBind(this, feature);
    }

    @Override
    public final String toNativeValue() {
        return value;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

}
