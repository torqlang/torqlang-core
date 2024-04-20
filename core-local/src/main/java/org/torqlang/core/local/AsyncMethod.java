/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ValueTools;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class AsyncMethod implements CompleteProc {

    private final MethodHandle methodHandle;
    private final Executor executor;

    private List<Object> nativeArgs;
    private RequestId requestId;

    public AsyncMethod(MethodHandle methodHandle, Executor executor) {
        this.methodHandle = methodHandle;
        this.executor = executor;
    }

    public AsyncMethod(MethodHandle methodHandle) {
        this(methodHandle, ForkJoinPool.commonPool());
    }

    @Override
    public void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {

        MethodType type = methodHandle.type();
        int nativeArgCount = type.parameterCount();
        if (!type.returnType().equals(Void.TYPE)) {
            nativeArgCount++;
        }
        if (ys.size() != nativeArgCount) {
            throw new InvalidArgCountError(nativeArgCount, ys, this);
        }

        nativeArgs = new ArrayList<>(ys.size());
        for (int i = 0; i < type.parameterCount(); i++) {
            CompleteOrIdent y = ys.get(i);
            Value value = y.resolveValue(env);
            Complete complete = value.checkComplete();
            Object nativeArg = ValueTools.toNativeValue(complete);
            nativeArgs.add(nativeArg);
        }

        // Is this a Proc or a Func? If a Func, prepare to send a response.
        if (type.parameterCount() < ys.size()) {
            ValueOrVar responseTarget = ys.get(ys.size() - 1).resolveValueOrVar(env);
            requestId = new ValueOrVarRef(responseTarget);
        }

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(this::supplyAsync, executor);
        future.thenAccept(result -> {
            if (requestId != null) {
                Complete kernelValue = ValueTools.toKernelValue(result);
                ActorRef requester = machine.owner();
                requester.send(ActorSystem.createResponse(kernelValue, requestId));
            }
        });
    }

    private Object supplyAsync() {
        try {
            return methodHandle.invokeWithArguments(nativeArgs);
        } catch (Throwable exc) {
            return FailedValue.create(getClass().getName(), exc);
        }
    }

}
