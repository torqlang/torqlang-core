/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.NeedsImpl;

public final class WaitAnyException extends WaitException {

    public WaitAnyException() {
    }

    @Override
    public final Object barrier() {
        throw new NeedsImpl();
    }

}
