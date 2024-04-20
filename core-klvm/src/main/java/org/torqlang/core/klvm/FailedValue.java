/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.GetStackTrace;
import org.torqlang.core.util.IndentLines;
import org.torqlang.core.util.SourceSpan;

import java.util.Objects;
import java.util.Set;

/*
 * Any attempt to use a FailedValue throws an exception, including testing if it is bound (resolving). This ensures
 * that all possible executions that consume failed values as input will fail. Because accessing a FailedValue throws
 * an exception, programmers can isolate and compensate for component failures. [CTM p. 328]
 *
 * The general contract for handling a FailedValue:
 *     When an unhandled error (including native errors) is caught by an actor:
 *         Create a new FailedValue
 *         If there is an active request:
 *             Respond with the new FailedValue
 *         Else:
 *             Log the new FailedValue details string
 *         Halt the current actor
 *     When a FailedValueError is caught it means a FailedValue was touched:
 *         Create a new FailedValue with the caught FailedValue as the cause
 *         If there is an active request:
 *             Respond with the new FailedValue
 *         Else:
 *             Log the new FailedValue details string
 *         Halt the current actor
 *     When a FailedValue is created:
 *         Capture Torqlang error value, Torqlang stack, Java stack
 *     When a response is received, and the response is a FailedValue:
 *         Bind the FailedValue to the response target, which will throw
 *         a FailedValueError when the target is touched
 *     When a FailedValue is touched, throw a FailedValueError
 */
public final class FailedValue implements Value, Complete {

    private static final String CAUSED_BY_COLON = "Caused by:";
    private static final String FAILED_VALUE_ERROR_COLON = "FailedValue error:";
    private static final String INDENT = "    "; // 4 spaces

    private final String owner;
    private final long threadId;
    private final String threadName;
    private final Complete error;
    private final Stack stack;
    private final FailedValue torqCause;
    private final Throwable nativeCause;

    public FailedValue(String owner, Complete error, Stack stack, FailedValue torqCause, Throwable nativeCause) {
        this.owner = owner;
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.error = error;
        this.stack = stack;
        this.torqCause = torqCause;
        this.nativeCause = nativeCause;
    }

    public static FailedValue create(String owner, Throwable throwable) {
        return create(owner, null, throwable);
    }

    public static FailedValue create(String owner, Stack stack, Throwable throwable) {
        Complete error;
        String n = throwable.getClass().getName();
        String m = throwable.getMessage();
        if (m == null) {
            error = Str.of(n);
        } else {
            int maxErrorLen = 100;
            if (m.length() > maxErrorLen) {
                m = m.substring(0, maxErrorLen) + "...";
            }
            error = Str.of(n + ": " + m);
        }
        return new FailedValue(owner, error, stack, null, throwable);
    }

    public static String formatDetailsString(String heading, String owner, long threadId, String threadName, Complete error, Stack current, Throwable nativeCause) {
        StringBuilder sb = new StringBuilder();
        formatDetailsString(heading, owner, threadId, threadName, error, current, nativeCause, sb);
        return sb.toString();
    }

    public static void formatDetailsString(String heading, String owner, long threadId, String threadName, Complete error, Stack current, Throwable nativeCause, StringBuilder sb) {
        sb.append(heading);
        sb.append('\n');
        sb.append(INDENT + "Owner: ").append(owner);
        sb.append('\n');
        sb.append(INDENT + "Thread id: ").append(threadId);
        sb.append('\n');
        sb.append(INDENT + "Thread name: ").append(threadName);
        sb.append('\n');
        sb.append(INDENT + "Error: ").append(error);
        if (current != null) {
            sb.append('\n');
            sb.append(INDENT + "Source context: ");
            sb.append('\n');
            SourceSpan sourceSpan = current.stmt;
            String inlineMessage = Objects.toString(error);
            String formatted = sourceSpan.formatWithMessage(inlineMessage, 4, 5, 5);
            sb.append(IndentLines.apply(formatted, INDENT.length() * 2));
        }
        if (nativeCause != null) {
            sb.append('\n');
            sb.append(INDENT + "Native cause with stack trace: ");
            sb.append('\n');
            String stackTrace = GetStackTrace.apply(nativeCause, true);
            sb.append(IndentLines.apply(stackTrace, INDENT.length() * 2));
        }
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitFailedValue(this, state);
    }

    @Override
    public Value add(Value addend) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value bindToValue(Value value, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value bindToValueOrVar(ValueOrVar valueOrVar, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value bindToVar(Var var, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrResolvedPtn caseNonRecOfThis(Value nonRecValue, Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrResolvedPtn caseOf(ValueOrPtn valueOrPtn, Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrResolvedPtn caseRecOfThis(Rec rec, Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value checkNotFailedValue() {
        throw new FailedValueError(this);
    }

    @Override
    public final Env deconstruct(ValueOrResolvedPtn valueOrResolvedPtn, Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final Bool disentails(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value divide(Value divisor) {
        throw new FailedValueError(this);
    }

    @Override
    public final Bool entails(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final boolean entailsRec(Rec operand, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final boolean entailsValueOrIdent(ValueOrIdent operand, Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final boolean entailsValueOrVar(ValueOrVar operand, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    @Override
    public final boolean entailsVar(Var operand, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

    public final Complete error() {
        return error;
    }

    @Override
    public final Bool greaterThan(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final Bool greaterThanOrEqualTo(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final Bool lessThan(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final Bool lessThanOrEqualTo(Value operand) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value modulo(Value divisor) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value multiply(Value multiplicand) {
        throw new FailedValueError(this);
    }

    public final Throwable nativeCause() {
        return nativeCause;
    }

    @Override
    public final Value negate() {
        throw new FailedValueError(this);
    }

    @Override
    public final Value not() {
        throw new FailedValueError(this);
    }

    @Override
    public final Value resolveValue(Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final Value resolveValue() {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrIdent resolveValueOrIdent(Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrVar resolveValueOrVar(Env env) {
        throw new FailedValueError(this);
    }

    @Override
    public final ValueOrVar resolveValueOrVar() {
        throw new FailedValueError(this);
    }

    public final Stack stack() {
        return stack;
    }

    @Override
    public final Value subtract(Value subtrahend) {
        throw new FailedValueError(this);
    }

    public final long threadId() {
        return threadId;
    }

    public final String threadName() {
        return threadName;
    }

    public final String toDetailsString() {
        StringBuilder sb = new StringBuilder();
        toDetailsString(FAILED_VALUE_ERROR_COLON, sb);
        return sb.toString();
    }

    private void toDetailsString(String heading, StringBuilder sb) {
        formatDetailsString(heading, owner, threadId, threadName, error, stack, nativeCause, sb);
        if (torqCause != null) {
            sb.append('\n');
            torqCause.toDetailsString(CAUSED_BY_COLON, sb);
        }
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    @Override
    public final Rec unifyRecs(Rec rec, Set<Memo> memos) {
        throw new FailedValueError(this);
    }

}
