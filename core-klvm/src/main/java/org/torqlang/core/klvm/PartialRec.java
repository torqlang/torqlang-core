/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.ArrayList;
import java.util.List;

public interface PartialRec extends Partial, Rec {

    static PartialRec create(LiteralOrVar label, List<FutureField> futureFields, List<PartialField> partialFields) {
        return BasicPartialRec.createPrivatelyForKlvm(label, new ArrayList<>(futureFields), new ArrayList<>(partialFields));
    }

    /**
     * Count of fields that do not yet have a determined feature
     */
    int futureFieldCount();

    Var futureLabel();

    /**
     * Equal to fieldCount() + futureFieldCount()
     */
    int totalFieldCount();
}
