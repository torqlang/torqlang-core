/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

final class BasicCompleteRec extends AbstractCompleteRec {

    private BasicCompleteRec() {
    }

    private BasicCompleteRec(Literal label, CompleteField[] completeFields) {
        restore(label, completeFields);
    }

    static CompleteRec createPrivatelyForKlvm(Literal label, List<CompleteField> completeFields) {
        return new BasicCompleteRec(label, completeFields.toArray(new CompleteField[0]));
    }

    static BasicCompleteRec instanceForRestore() {
        return new BasicCompleteRec();
    }

    @Override
    public final int unificationPriority() {
        return UnificationPriority.COMPLETE_REC;
    }

}
