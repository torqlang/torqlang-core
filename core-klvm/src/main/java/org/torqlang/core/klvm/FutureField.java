/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

@SuppressWarnings("ClassCanBeRecord")
public class FutureField implements Field {

    public final Var feature;
    public final ValueOrVar value;

    public FutureField(Var feature, ValueOrVar value) {
        this.feature = feature;
        this.value = value;
    }

    @Override
    public final Var feature() {
        return feature;
    }

    @Override
    public final ValueOrVar value() {
        return value;
    }

}
