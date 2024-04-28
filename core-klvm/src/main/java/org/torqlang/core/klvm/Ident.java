/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.EscapeChar;

import java.util.Set;

public final class Ident implements Decl, LiteralOrIdent {

    public static final Ident $ACT = new Ident("$act");
    public static final Ident $ACTOR_CFGTR = new Ident("$actor_cfgtr");
    public static final Ident $ELSE = new Ident("$else");
    public static final Ident $FINALLY = new Ident("$finally");
    public static final Ident $FOR = new Ident("$for");
    public static final Ident $GUARD = new Ident("$guard");
    public static final Ident $HANDLER = new Ident("$handler");
    public static final Ident $HANDLERS = new Ident("$handlers");
    public static final Ident $HANDLERS_CTOR = new Ident("$handlers_ctor");
    public static final Ident $IMPORT = new Ident("$import");
    public static final Ident $ITER = new Ident("$iter");
    public static final Ident $M = new Ident("$m");
    public static final Ident $NEXT = new Ident("$next");
    public static final Ident $RESPOND = new Ident("$respond");
    public static final Ident $R = new Ident("$r");
    public static final Ident $SELF = new Ident("$self");
    public static final Ident $SPAWN = new Ident("$spawn");
    public static final Ident $WHILE = new Ident("$while");

    private static final String $_ = "$_";
    private static final String $A = "$a";
    private static final String $V = "$v";

    private static final char UPPER_CASE_A = 'A';
    private static final char UPPER_CASE_Z = 'Z';
    private static final char LOWER_CASE_A = 'a';
    private static final char LOWER_CASE_Z = 'z';
    private static final char DIGIT_ZERO = '0';
    private static final char DIGIT_NINE = '9';

    public final String name;

    private Ident(String name) {
        this.name = name;
    }

    /*
     * This is a convenience method. If the given identifier is not in the knownBound set, add it to the lexicallyFree
     * set.
     *
     * ident            the identifier being evaluated as either bound or free
     * knownBound       the identifiers known so far to be bound in the closure
     * lexicallyFree    the free identifiers captured so far in the closure
     */
    public static void captureLexicallyFree(Ident ident, Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        if (!knownBound.contains(ident)) {
            lexicallyFree.add(ident);
        }
    }

    public static Ident create(String name) {
        if (name.charAt(0) == '$') {
            throw new IllegalArgumentException(KlvmMessageText.USER_IDENTIFIERS_CANNOT_BEGIN_WITH_A_DOLLAR_SIGN);
        }
        return new Ident(name);
    }

    public static Ident createPrivately(String name) {
        return new Ident(name);
    }

    public static Ident createSystemAnonymousIdent(int suffix) {
        return new Ident($_ + suffix);
    }

    public static Ident createSystemArgIdent(int suffix) {
        return new Ident($A + suffix);
    }

    public static Ident createSystemVarIdent(int suffix) {
        return new Ident($V + suffix);
    }

    public static boolean isAlphaNumericOrUnderscore(char c) {
        if (c >= LOWER_CASE_A && c <= LOWER_CASE_Z) {
            return true;
        }
        if (c >= UPPER_CASE_A && c <= UPPER_CASE_Z) {
            return true;
        }
        if (c >= DIGIT_ZERO && c <= DIGIT_NINE) {
            return true;
        }
        return c == '_';
    }

    public static boolean isSimpleName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if (!isAlphaNumericOrUnderscore(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String quote(String value) {
        StringBuilder sb = new StringBuilder(value.length() * 2 + 2);
        quote(value, sb);
        return sb.toString();
    }

    public static void quote(String source, StringBuilder sb) {
        sb.append('`');
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            //noinspection UnnecessaryUnicodeEscape
            if (c < '\u0020') {
                EscapeChar.apply(c, sb);
            } else {
                if (c == '\\') {
                    sb.append("\\\\");
                } else if (c == '`') {
                    sb.append("\\`");
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('`');
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitIdent(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        throw new IllegalStateException(KlvmMessageText.IDENT_ALONE_CANNOT_DETERMINE_WHETHER_IT_IS_BOUND_OR_FREE);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Ident that = (Ident) other;
        return name.equals(that.name);
    }

    public final String formatValue() {
        return name;
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    public final boolean isAnonymous() {
        return name.equals("_");
    }

    public final boolean isSystem() {
        return name.charAt(0) == '$';
    }

    @Override
    public final Value resolveValue(Env env) throws WaitVarException {
        ValueOrVar valueOrVar = env.get(this);
        if (valueOrVar == null) {
            throw new IdentNotFoundError(this, null);
        }
        return valueOrVar.resolveValue();
    }

    @Override
    public final ValueOrVar resolveValueOrVar(Env env) {
        ValueOrVar valueOrVar = env.get(this);
        if (valueOrVar == null) {
            throw new IdentNotFoundError(this, null);
        }
        return valueOrVar.resolveValueOrVar();
    }

    @Override
    public final String toString() {
        return formatValue();
    }

    @Override
    public final Var toVar(Env env) {
        Var var = env.get(this);
        if (var == null) {
            throw new IdentNotFoundError(this, null);
        }
        return var;
    }

}
