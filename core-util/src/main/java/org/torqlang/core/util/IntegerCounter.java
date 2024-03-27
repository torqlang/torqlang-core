/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

public class IntegerCounter {

    private int value;

    public IntegerCounter(int initialValue) {
        value = initialValue;
    }

    public void add(int delta) {
        value += delta;
    }

    public int addAndGet(int delta) {
        value += delta;
        return value;
    }

    public int get() {
        return value;
    }

    public int getAndAdd(int delta) {
        int answer = value;
        value += delta;
        return answer;
    }

}
