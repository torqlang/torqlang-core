/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.Stack;
import org.torqlang.core.util.ErrorWithSourceSpan;
import org.torqlang.core.util.SourceSpan;

public class SelfRefAskError extends ErrorWithSourceSpan {

    private final SourceSpan sourceSpan;

    public SelfRefAskError(Stack current) {
        this.sourceSpan = current.stmt;
    }

    @Override
    public SourceSpan sourceSpan() {
        return sourceSpan;
    }

}
