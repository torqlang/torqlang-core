/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;
import java.util.Set;

/*
 * A native procedure must be designed to wait on unbound inputs. When an unbound input is needed, the procedure must
 * suspend itself by throwing a WaitException. Later, when the unbound input becomes bound, the procedure must resume
 * itself.
 *
 * INVARIANT: A native procedure CAN ONLY be a function of its given parameters. A native procedure CANNOT interact
 * with objects not found in its given parameter set. A native procedure CANNOT create threads or block execution any
 * way whatsoever.
 */
public interface Proc extends Value {

    @Override
    default <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitProc(this, state);
    }

    void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException;

    @Override
    default Proc bindToValue(Value value, Set<Memo> memos) {
        if (this != value) {
            throw new UnificationError(this, value);
        }
        return this;
    }

    @Override
    default boolean entailsRec(Rec operand, Set<Memo> memos) {
        return false;
    }

}
