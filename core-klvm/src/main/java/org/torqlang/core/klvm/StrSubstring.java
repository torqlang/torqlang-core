/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

final class StrSubstring implements ObjProc<Str> {

    static final StrSubstring SINGLETON = new StrSubstring();

    private StrSubstring() {
    }

    @Override
    public final void apply(Str obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        int argCount = ys.size();
        if (argCount < 2 || argCount > 3) {
            throw new InvalidArgCountError(2, 3, ys, this);
        }
        Int64 beginIndex = (Int64) ys.get(0).resolveValue(env);
        Str subStr;
        ValueOrVar target;
        if (argCount == 2) {
            subStr = Str.of(obj.value.substring(beginIndex.intValue()));
            target = ys.get(1).resolveValueOrVar(env);
        } else {
            Int64 endIndex = (Int64) ys.get(1).resolveValue(env);
            subStr = Str.of(obj.value.substring(beginIndex.intValue(), endIndex.intValue()));
            target = ys.get(2).resolveValueOrVar(env);
        }
        target.bindToValueOrVar(subStr, null);
    }

}
