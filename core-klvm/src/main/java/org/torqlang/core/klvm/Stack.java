/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public class Stack implements Kernel {

    public final Stmt stmt;
    public final Env env;
    public final Stack next;
    public final int size;

    public Stack(Stmt stmt, Env env, Stack next) {
        this.stmt = stmt;
        this.env = env;
        this.next = next;
        this.size = next == null ? 1 : next.size + 1;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitStack(this, state);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
