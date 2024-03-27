/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Feature extends Comparable<Feature>, Complete, FeatureOrVar, FeatureOrIdent, FeatureOrIdentPtn {

    default int compareTo(Feature other) {
        return FeatureComparator.SINGLETON.compare(this, other);
    }

}
