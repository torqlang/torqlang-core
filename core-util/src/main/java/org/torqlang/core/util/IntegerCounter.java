/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

public final class IntegerCounter {

    private int value;

    public IntegerCounter(int initialValue) {
        value = initialValue;
    }

    public final void add(int delta) {
        value += delta;
    }

    public final int addAndGet(int delta) {
        value += delta;
        return value;
    }

    public final int get() {
        return value;
    }

    public final int getAndAdd(int delta) {
        int answer = value;
        value += delta;
        return answer;
    }

}
