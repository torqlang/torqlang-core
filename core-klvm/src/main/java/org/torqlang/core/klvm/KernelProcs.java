/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public class KernelProcs {

    public static final Ident ASSERT_BOUND_IDENT = Ident.create("assert_bound");
    public static final Ident IS_BOUND_IDENT = Ident.create("is_bound");
    public static final Ident IS_DET_IDENT = Ident.create("is_det");
    private static final int ASSERT_BOUND_ARG_COUNT = 1;
    public static final CompleteProc ASSERT_BOUND_PROC = KernelProcs::assertBound;
    private static final int IS_BOUND_ARG_COUNT = 2;
    public static final CompleteProc IS_BOUND_PROC = KernelProcs::isBound;
    private static final int IS_DET_ARG_COUNT = 2;
    public static final CompleteProc IS_DET_PROC = KernelProcs::isDet;

    static void assertBound(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != ASSERT_BOUND_ARG_COUNT) {
            throw new InvalidArgCountError(ASSERT_BOUND_ARG_COUNT, ys, "assert_bound");
        }
        CompleteOrIdent y = ys.get(0);
        ValueOrVar yRes = y.resolveValueOrVar(env);
        if (yRes instanceof Var yVar) {
            throw new NotBoundError(yVar, machine.current().stmt);
        }
    }

    static void isBound(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != IS_BOUND_ARG_COUNT) {
            throw new InvalidArgCountError(IS_BOUND_ARG_COUNT, ys, "is_bound");
        }
        Bool bound = (ys.get(0).resolveValueOrVar(env) instanceof Value) ? Bool.TRUE : Bool.FALSE;
        ys.get(1).resolveValueOrVar(env).bindToValue(bound, null);
    }

    static void isDet(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != IS_DET_ARG_COUNT) {
            throw new InvalidArgCountError(IS_DET_ARG_COUNT, ys, "is_det");
        }
        CompleteOrIdent y0 = ys.get(0);
        ValueOrVar valueOrVar = y0.resolveValueOrVar(env);
        Bool det;
        if (valueOrVar instanceof Value) {
            if (valueOrVar instanceof Rec rec) {
                det = rec.sweepUndeterminedVars().isEmpty() ? Bool.TRUE : Bool.FALSE;
            } else {
                det = Bool.TRUE;
            }
        } else {
            det = Bool.FALSE;
        }
        CompleteOrIdent y1 = ys.get(1);
        y1.resolveValueOrVar(env).bindToValue(det, null);
    }

}
