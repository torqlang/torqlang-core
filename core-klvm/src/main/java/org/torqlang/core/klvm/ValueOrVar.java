/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public interface ValueOrVar extends Kernel {

    /*
     * Variable-variable binding [CTM p. 63]
     * Value creation [CTM p. 63]
     */
    Value bindToValue(Value value, Set<Memo> memos) throws WaitVarException;

    ValueOrVar bindToValueOrVar(ValueOrVar valueOrVar, Set<Memo> memos) throws WaitVarException;

    ValueOrVar bindToVar(Var var, Set<Memo> memos) throws WaitVarException;

    Complete checkComplete() throws WaitVarException;

    boolean entails(Value operand, Set<Memo> memos) throws WaitVarException;

    boolean entailsValueOrIdent(ValueOrIdent operand, Env env) throws WaitVarException;

    boolean entailsValueOrVar(ValueOrVar operand, Set<Memo> memos) throws WaitVarException;

    boolean entailsVar(Var operand, Set<Memo> memos) throws WaitVarException;

    Value resolveValue() throws WaitVarException;

    ValueOrVar resolveValueOrVar();

}
