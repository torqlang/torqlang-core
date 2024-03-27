/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

public abstract class ErrorWithSourceSpan extends RuntimeException implements SourceSpanProvider {

    public ErrorWithSourceSpan() {
        super();
    }

    public ErrorWithSourceSpan(String message) {
        super(message);
    }

    public ErrorWithSourceSpan(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorWithSourceSpan(Throwable cause) {
        super(cause);
    }

    public static void runWithPrintError(SourceRoutine routine) throws Exception {
        try {
            routine.apply();
        } catch (ErrorWithSourceSpan exc) {
            exc.printError();
            throw exc;
        }
    }

    public final String formatWithSource(int lineNrWidth, int showBefore, int showAfter) {
        return sourceSpan() != null ?
            sourceSpan().formatWithMessage(getMessage(), lineNrWidth, showBefore, showAfter) :
            this.toString();
    }

    public final void printError() {
        System.err.println("==== BEGIN ====");
        System.err.println(formatWithSource(4, 5, 5));
        System.err.println("==== END ====");
    }

    public interface SourceRoutine {
        void apply() throws Exception;
    }

}
