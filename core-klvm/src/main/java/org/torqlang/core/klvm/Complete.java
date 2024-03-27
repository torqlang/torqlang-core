/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Complete extends Value, CompleteOrIdent {

    @Override
    default Complete checkComplete() {
        return this;
    }

    @Override
    default Var toVar(Env env) {
        return new Var(this);
    }

    default Object toNativeValue() {
        throw new IllegalArgumentException("Cannot convert to native value: " + getClass().getName());
    }

}
