/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Comparator;

public final class FeatureProviderComparator implements Comparator<FeatureProvider> {

    public static final FeatureProviderComparator SINGLETON = new FeatureProviderComparator();

    private FeatureProviderComparator() {
    }

    public static FeatureProviderComparator comparator() {
        return SINGLETON;
    }

    @Override
    public final int compare(FeatureProvider o1, FeatureProvider o2) {
        return FeatureComparator.SINGLETON.compare(o1.feature(), o2.feature());
    }

}
