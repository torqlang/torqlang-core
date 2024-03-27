/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.GetStackTrace;

public final class NativeError extends AbstractCompleteRec {

    public static final Str LABEL = Str.of("error");
    public static final Str MESSAGE = Str.of("message");
    public static final Str NAME = Str.of("name");
    public static final Str STACK_TRACE = Str.of("stackTrace");

    private final Throwable throwable;

    public NativeError(Throwable throwable) {
        this.throwable = throwable;
        Str messageValue = Str.of(throwable.getMessage() != null ? throwable.getMessage() : "");
        Str nameValue = Str.of(throwable.getClass().getName());
        Str stackTraceValue = Str.of(GetStackTrace.apply(throwable, true));
        restore(LABEL, new CompleteField[]{
            new CompleteField(MESSAGE, messageValue),
            new CompleteField(NAME, nameValue),
            new CompleteField(STACK_TRACE, stackTraceValue)
        });
    }

    public final Throwable throwable() {
        return throwable;
    }

    @Override
    public final int unificationPriority() {
        return UnificationPriority.JAVA_OBJECT;
    }

}
