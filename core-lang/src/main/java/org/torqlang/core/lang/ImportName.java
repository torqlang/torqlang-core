/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Str;

public final class ImportName {
    public final Str name;
    public final Str alias;

    public ImportName(Str name, Str alias) {
        this.name = name;
        this.alias = alias;
    }

    public ImportName(Str name) {
        this.name = name;
        this.alias = null;
    }
}
