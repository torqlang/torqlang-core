/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class FeatureNotFoundError extends MachineError {
    public final Composite composite;
    public final Feature feature;

    public FeatureNotFoundError(Composite composite, Feature feature) {
        super("Feature not found");
        this.composite = composite;
        this.feature = feature;
    }
}
