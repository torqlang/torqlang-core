/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.MachineError;

public class ModuleNotFoundError extends MachineError {
    public static final String MODULE_NOT_FOUND = "Module not found";
    public final String path;

    public ModuleNotFoundError(String path) {
        super(MODULE_NOT_FOUND);
        this.path = path;
    }
}
