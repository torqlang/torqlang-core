/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

final class BasicCompleteTuple extends AbstractCompleteTuple {

    private BasicCompleteTuple() {
    }

    private BasicCompleteTuple(Literal label, Complete[] values) {
        restore(label, values);
    }

    static CompleteTuple createPrivatelyForKlvm(Literal label, Complete[] values) {
        return new BasicCompleteTuple(label, values);
    }

    static BasicCompleteTuple instanceForRestore() {
        return new BasicCompleteTuple();
    }

    @Override
    public final int unificationPriority() {
        return UnificationPriority.COMPLETE_TUPLE;
    }

}
