/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface UnificationPriority {
    int JAVA_OBJECT = 500;
    int COMPLETE_TUPLE = 400;
    int COMPLETE_REC = 300;
    int PARTIAL_TUPLE = 200;
    int PARTIAL_REC = 100;
}
