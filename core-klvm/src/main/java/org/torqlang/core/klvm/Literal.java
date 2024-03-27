/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public interface Literal extends Obj, Feature, LiteralOrVar, LiteralOrIdent, LiteralOrIdentPtn {

    @Override
    default Literal bindToValue(Value value, Set<Memo> memos) {
        if (!this.equals(value)) {
            throw new UnificationError(this, value);
        }
        return this;
    }

    @Override
    default ValueOrVar select(Feature feature) {
        throw new FeatureNotFoundError(this, feature);
    }

}
