/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Kernel {

    static String toSystemString(Kernel kernel) {
        return kernel.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(kernel));
    }

    <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception;

    default String toKernelString() {
        return KernelFormatter.SINGLETON.format(this);
    }

}
