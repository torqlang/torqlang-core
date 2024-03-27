/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.OpaqueValue;
import org.torqlang.core.klvm.ValueOrVar;

/**
 * A local-only reference to a ValueOrVar
 */
final class ValueOrVarRef extends OpaqueValue implements RequestId {
    final ValueOrVar valueOrVar;

    ValueOrVarRef(ValueOrVar valueOrVar) {
        this.valueOrVar = valueOrVar;
    }

    public final String toString() {
        return "ValueOrVarRef(" + valueOrVar + ")";
    }

}
