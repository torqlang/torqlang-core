/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class TokenMod {

    public static final Ident TOKEN_IDENT = Ident.create("Token");
    public static final CompleteObj TOKEN_CLS = TokenCls.SINGLETON;

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 1;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Token.new");
        }
        Token token = new Token();
        ValueOrVar target = ys.get(0).resolveValueOrVar(env);
        target.bindToValue(token, null);
    }

    static final class TokenCls implements CompleteObj {

        private static final TokenCls SINGLETON = new TokenCls();
        private static final CompleteProc TOKEN_CLS_NEW = TokenMod::clsNew;

        private TokenCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return TOKEN_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
