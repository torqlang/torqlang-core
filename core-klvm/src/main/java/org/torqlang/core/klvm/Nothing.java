/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class Nothing implements Literal {

    public static final String NOTHING_NAME = "nothing";
    public static final Nothing SINGLETON = new Nothing();

    private Nothing() {
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitNothing(this, state);
    }

    @Override
    public final String appendToString(String string) {
        return string + NOTHING_NAME;
    }

    @Override
    public final String formatValue() {
        return NOTHING_NAME;
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final Object toNativeValue() {
        return null;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

}
