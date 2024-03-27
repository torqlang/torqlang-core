/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

public class NeedsImpl extends RuntimeException {

    public NeedsImpl() {
        super("This method needs an implementation");
    }

    public NeedsImpl(String message) {
        super(message);
    }

    public NeedsImpl(String message, Throwable cause) {
        super(message, cause);
    }

    public NeedsImpl(Throwable cause) {
        super(cause);
    }

}
