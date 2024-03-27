/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class DuplicateFeatureError extends MachineError {
    public final Rec rec;
    public final Feature feature;

    public DuplicateFeatureError(Rec rec, Feature feature) {
        super("Duplicate feature");
        this.rec = rec;
        this.feature = feature;
    }
}
