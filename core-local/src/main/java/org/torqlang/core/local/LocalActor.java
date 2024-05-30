/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.Stack;
import org.torqlang.core.klvm.*;
import org.torqlang.core.util.NeedsImpl;

import java.util.*;
import java.util.stream.Collectors;

import static org.torqlang.core.local.OnMessageResult.FINISHED;
import static org.torqlang.core.local.OnMessageResult.NOT_FINISHED;
import static org.torqlang.core.util.ListTools.nullSafeCopyOf;
import static org.torqlang.core.util.SourceSpan.emptySourceSpan;

/*
 * Envelope Insertion -- An envelope is placed at the end of the mailbox queue unless the envelope is higher priority
 * than its predecessor. If the envelope is higher priority than its predecessor, their positions in the queue are
 * swapped. This repeats until the envelope is no longer higher priority than its predecessor.
 *
 * Wait State -- An actor is a single threaded kernel machine. The wait-state is implemented as a single field
 * holding the barrier value that suspended the machine. A non-null wait-state indicates we are waiting on a Response.
 * Otherwise, a null wait-state indicates we are waiting on a Notify or Request.
 *
 * Notify and Request messages are computation requests. Response messages affect the machines state, and Control
 * messages affect the actor lifecycle.
 *
 * Priority 0: Control message are the highest priority. Examples of control messages are Resume, Stop, and Debug.
 * Priority 1: Response messages are higher priority than request messages because the actor may be waiting on an
 *             unbound variable fulfilled by the response.
 * Priority 2: Request and notify message have the same priority, they are requesting that the actor perform a
 *             computation.
 *
 */
final class LocalActor extends AbstractActor {

    private static final Env ROOT_ENV = createRootEnv();

    private final ActorSystem system;
    private final IdentityHashMap<Var, List<ChildVar>> triggers = new IdentityHashMap<>();

    private boolean trace;
    private Machine machine;
    private EnvEntry askHandlerEntry;
    private EnvEntry tellHandlerEntry;
    private Envelope activeRequest;
    private Object waitState;
    private int childCount;
    private FailedValue failedValue; // We are halted if not null

    private List<Envelope> selectableResponses = Collections.emptyList();
    private List<Envelope> suspendedResponses = Collections.emptyList();

    LocalActor(Address address, ActorSystem system, boolean trace) {
        super(address, system.createMailbox(), system.executor(), system.createLogger());
        this.system = system;
        this.trace = trace;
        if (trace) {
            logInfo("Created");
        }
    }

    private static Env createRootEnv() {
        List<EnvEntry> bindings = List.of(
            new EnvEntry(Ident.$ACT, new Var((CompleteProc) LocalActor::onCallbackToAct)),
            new EnvEntry(Ident.$IMPORT, new Var((CompleteProc) LocalActor::onCallbackToImport)),
            new EnvEntry(Ident.$RESPOND, new Var((CompleteProc) LocalActor::onCallbackToRespondFromAsk)),
            new EnvEntry(Ident.$SELF, new Var((CompleteProc) LocalActor::onCallbackToSelf)),
            new EnvEntry(Ident.$SPAWN, new Var((CompleteProc) LocalActor::onCallbackToSpawn))
        );
        return Env.create(bindings);
    }

    private static boolean nullSafeIsControl(Envelope envelope) {
        return envelope != null && envelope.isControl();
    }

    private static boolean nullSafeIsResponse(Envelope envelope) {
        return envelope != null && envelope.isResponse();
    }

    private static void onCallbackToAct(List<CompleteOrIdent> ys, Env env, Machine machine) {
        LocalActor owner = machine.owner();
        owner.performCallbackToAct(ys, env, machine);
    }

    static void onCallbackToActorAt(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        LocalActor owner = machine.owner();
        if (ys.size() != 2) {
            throw new InvalidArgCountError(2, ys, "LocalActor.onCallbackForActorAt");
        }
        Str addressStr = (Str) ys.get(0).resolveValue(env);
        Address address = Address.create(addressStr.value);
        ActorRefObj actorRefObj = owner.system.actorAt(address);
        ys.get(1).resolveValueOrVar(env).bindToValue(actorRefObj, null);
    }

    /*
     * Imports must be a type of `Complete`
     */
    static void onCallbackToImport(List<CompleteOrIdent> ys, Env env, Machine machine)
        throws WaitException
    {
        LocalActor owner = machine.owner();
        if (ys.size() != 2) {
            throw new InvalidArgCountError(2, ys, "LocalActor.onCallbackToImport");
        }
        Value qualifierRes = ys.get(0).resolveValue(env);
        if (!(qualifierRes instanceof Str qualifierStr)) {
            throw new IllegalArgumentException("Not a Str: " + qualifierRes);
        }
        String qualifier = qualifierStr.value;
        CompleteRec moduleRec = owner.system.moduleAt(qualifier);
        Value namesRes = ys.get(1).resolveValue(env);
        if (!(namesRes instanceof CompleteTuple namesTuple)) {
            throw new IllegalArgumentException("Not a CompleteTuple: " + namesRes);
        }
        for (int i = 0; i < namesTuple.fieldCount(); i++) {
            Str nameStr;
            Str aliasStr;
            Value nameValue = namesTuple.valueAt(i);
            if (nameValue instanceof CompleteTuple nameTuple) {
                nameStr = (Str) nameTuple.valueAt(0);
                aliasStr = (Str) nameTuple.valueAt(1);
            } else {
                nameStr = (Str) nameValue;
                aliasStr = nameStr;
            }
            Complete component = moduleRec.findValue(nameStr);
            if (component == null) {
                throw new IllegalArgumentException("Component not found: " + nameStr);
            }
            Ident aliasIdent = Ident.create(aliasStr.value);
            env.get(aliasIdent).bindToValue(component, null);
        }
    }

    private static void onCallbackToRespondFromAsk(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        LocalActor owner = machine.owner();
        owner.sendResponse(ys, env, machine);
        // We are at the end of an ask-handler and have completed the request
        owner.activeRequest = null;
    }

    static void onCallbackToRespondFromProc(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        LocalActor owner = machine.owner();
        owner.sendResponse(ys, env, machine);
    }

    private static void onCallbackToSelf(List<CompleteOrIdent> ys, Env env, Machine machine) {
        LocalActor owner = machine.owner();
        owner.performCallbackToSelf(ys, env, machine);
    }

    private static void onCallbackToSpawn(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        LocalActor owner = machine.owner();
        owner.performCallbackToSpawn(ys, env, machine);
    }

    static Env rootEnv() {
        return ROOT_ENV;
    }

    private void addParentVarDependency(Var triggerVar, Var parentVar, Var childVar, LocalActor child) {
        if (trace) {
            logInfo("Adding bind dependency on var " + triggerVar + " to synchronize parent var " +
                parentVar + " with child var: " + childVar + " at child " + child.address());
        }
        List<ChildVar> childVars = triggers.get(triggerVar);
        if (childVars == null) {
            childVars = new ArrayList<>();
            triggers.put(triggerVar, childVars);
            triggerVar.setBindCallback(this::onParentVarBound);
        }
        childVars.add(new ChildVar(parentVar, childVar, child));
    }

    private void bindResponseValue(Envelope envelope) throws WaitException {

        // If the response is a typical request-response value, simply bind it.
        // Note that if the response is a FailedValue, it is bound here silently.

        if (envelope.requestId() instanceof ValueOrVarRef valueOrVarRef) {
            ValueOrVar responseTarget = valueOrVarRef.valueOrVar;
            Complete responseValue = (Complete) envelope.message();
            responseTarget.bindToValue(responseValue, null);
            return;
        }

        // Otherwise, the response is just one in a possible stream of values.

        StreamObjRef streamObjRef = (StreamObjRef) envelope.requestId();
        StreamObj streamObj = streamObjRef.streamObj;

        // Unlike a typical request-response, we need to check for a FailedValue
        // and bind it explicitly.

        if (envelope.message() instanceof FailedValue childFailedValue) {
            if (trace) {
                logInfo("Binding FailedValue to tail Var of stream " + streamObj.tail.element);
            }
            streamObj.tail.element.bindToValue(childFailedValue, null);
            streamObj.appendUnboundTail();
            return;
        }

        CompleteRec messageRec = (CompleteRec) envelope.message();

        // Although inefficient, it's legal for a publisher to return an empty batch of values, and
        // in that case, we have nothing to bind and nothing else to do.
        if (messageRec.fieldCount() == 0) {
            return;
        }

        // An 'eof' response must have a 'more' feature.
        if (messageRec.label().equals(Eof.SINGLETON)) {
            Bool more = (Bool) messageRec.valueAt(0);
            if (more.value) {
                streamObj.fetchNextFromPublisher();
            } else {
                if (trace) {
                    logInfo("Binding 'eof' to stream tail variable");
                }
                streamObj.tail.element.bindToValue(Eof.SINGLETON, null);
            }
            return;
        }

        // At this point, we know we have a tuple of values. We must bind the first
        // value to the current tail Var. The remaining values are appended to the
        // stream.
        CompleteTuple values = (CompleteTuple) messageRec;
        Complete responseValue = values.valueAt(0);
        if (trace) {
            logInfo("Binding first tuple value to stream tail variable: " + responseValue);
        }
        streamObj.tail.element.bindToValue(responseValue, null);
        streamObj.appendRemainingResponseValues(values);
    }

    private ComputeAdvice computeTimeSlice() {
        // Compute only returns a halt in response to two conditions:
        //     1. Compute touched a FailedValue
        //         (a) The halt contains the touched (remote) FailedValue but the local stack
        //     2. Compute threw an exception that was not caught
        //         (a) An indirect throw can originate when:
        //             1. A native Java program throws a NativeThrow
        //                 (a) The resulting NativeThrowError contains an 'error' value
        //                 (b) A 'throw error' statement is pushed and run
        //                 (c) The halt will contain an error and the native throw error
        //             2. Compute catches a generic Throwable
        //                 (a) The throwable is rerun as a 'throw error#{name: _, ...}' statement
        //                 (b) The halt will contain a NativeError
        //         (b) The halt contains the uncaught throw and local stack
        // If we are in the middle of an active request (activeRequest != null), we
        // must convert the halt into a FailedValue.
        //     1. If compute touched a FailedValue
        //         (a) Create a new FailedValue with the given FailedValue as its cause
        //     2. If compute threw an exception that was not caught
        //         (a) Create a FailedValue with an error and native cause
        //         (b) Native error should be "error#{name: _, message: _, ...}"
        waitState = null;
        if (trace) {
            logInfo("Computing");
        }
        ComputeAdvice advice = machine.compute(10_000);
        if (advice.isWait()) {
            ComputeWait computeWait = (ComputeWait) advice;
            if (trace) {
                String idents = machine.stack().env.collectIdents((Var) computeWait.barrier).
                    stream().map(Ident::toString).collect(Collectors.joining(", ", "[", "]"));
                String label = "Waiting on " + computeWait.barrier + " with identifiers " + idents;
                String message = "Waiting on a variable\n" + machine.stack().stmt.formatWithMessage(label, 5, 1, 2);
                logInfo(message);
            }
            waitState = computeWait.barrier;
        } else if (advice.isPreempt()) {
            send(Resume.SINGLETON);
        } else if (advice.isHalt()) {
            throw new MachineHaltError((ComputeHalt) advice);
        }
        return advice;
    }

    private ComputeAdvice computeTimeSliceUsingHandler(Value value, EnvEntry handlerEntry) {
        if (trace) {
            logInfo("Processing request message " + value);
        }
        if (machine.stack() != null) {
            throw new IllegalStateException("Previous computation is not ended");
        }
        EnvEntry messageEntry = new EnvEntry(Ident.$NEXT, new Var(value));
        Env computeEnv = Env.create(Env.emptyEnv(), handlerEntry, messageEntry);
        ApplyStmt computeStmt = new ApplyStmt(Ident.$HANDLER, Collections.singletonList(Ident.$NEXT), emptySourceSpan());
        machine.pushStackEntry(computeStmt, computeEnv);
        return computeTimeSlice();
    }

    final void configure(ActorCfg actorCfg) {
        send(Envelope.createControlNotify(new Configure(actorCfg)));
    }

    @Override
    protected final boolean isExecutable(Mailbox mailbox) {
        if (waitState != null) {
            Envelope next = mailbox.peekNext();
            return nullSafeIsResponse(next) || !selectableResponses.isEmpty() || nullSafeIsControl(next);
        }
        return !mailbox.isEmpty();
    }

    private void logRespondingWithValue(Complete value) {
        logInfo("Responding to " + activeRequest.requester().address() + " target " +
            activeRequest.requestId() + " with " + value);
    }

    private LocalAddress nextChildAddress() {
        childCount++;
        return LocalAddress.create((LocalAddress) address(), Integer.toString(childCount));
    }

    private OnMessageResult onActRequest(Envelope envelope) {
        activeRequest = envelope;
        Act act = (Act) envelope.message();
        Env actEnv = Env.create(ROOT_ENV, act.input);
        actEnv = actEnv.add(new EnvEntry(act.target, new Var()));
        machine = new Machine(LocalActor.this, new Stack(act.seq, actEnv, null));
        computeTimeSlice();
        return NOT_FINISHED;
    }

    private OnMessageResult onConfigure(Envelope envelope) {
        if (trace) {
            logInfo("Configuring");
        }

        // Extract the actor configuration from the incoming Configure message
        Configure configure = (Configure) envelope.message();
        ActorCfg actorCfg = configure.actorCfg;

        // Create the kernel machine and necessary environment to construct the handlers
        machine = new Machine(LocalActor.this, null);
        List<EnvEntry> envEntries = new ArrayList<>();
        EnvEntry handlersEntry = new EnvEntry(Ident.$HANDLERS, new Var());
        envEntries.add(handlersEntry);

        // Build a list of arguments for the handlers constructor
        List<Complete> args = actorCfg.args();
        List<CompleteOrIdent> argIdents = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            Ident argIdent = Ident.createSystemArgIdent(i);
            argIdents.add(argIdent);
            envEntries.add(new EnvEntry(argIdent, new Var(args.get(i))));
        }
        argIdents.add(Ident.$HANDLERS);

        // Compute the handlers
        Var constructorVar = new Var(actorCfg.handlersCtor());
        envEntries.add(new EnvEntry(Ident.$HANDLERS_CTOR, constructorVar));
        Env configEnv = Env.create(ROOT_ENV, envEntries);
        ApplyStmt computeStmt = new ApplyStmt(Ident.$HANDLERS_CTOR, argIdents, emptySourceSpan());
        machine.pushStackEntry(computeStmt, configEnv);
        ComputeAdvice advice = computeTimeSlice();
        if (advice != ComputeEnd.SINGLETON) {
            throw new IllegalStateException("Did not compute handlers");
        }
        if (!(handlersEntry.var.valueOrVarSet() instanceof Tuple handlers)) {
            throw new IllegalStateException("Handlers is not a Tuple");
        }

        // Save the `ask` handlers and `tell` handlers separately
        askHandlerEntry = new EnvEntry(Ident.$HANDLER, new Var((Value) handlers.valueAt(0)));
        tellHandlerEntry = new EnvEntry(Ident.$HANDLER, new Var((Value) handlers.valueAt(1)));

        return NOT_FINISHED;
    }

    private OnMessageResult onControl(Envelope envelope) {
        if (envelope == Resume.SINGLETON) {
            return onResume();
        }
        if (envelope.isResponse()) {
            throw new IllegalArgumentException("Invalid control response");
        }
        if (envelope.message() instanceof SyncVar syncVar) {
            return onSyncVar(syncVar);
        }
        if (envelope.message() instanceof Act) {
            return onActRequest(envelope);
        }
        if (envelope.message() instanceof Configure) {
            return onConfigure(envelope);
        }
        if (envelope.message() == Stop.SINGLETON) {
            return onStop(envelope);
        }
        throw new IllegalArgumentException("Invalid control message: " + envelope);
    }

    @Override
    protected final OnMessageResult onMessage(Envelope[] next) {
        // It's possible to be executable with zero incoming response messages because we have a collection of
        // selectableResponses and optionally a collection of suspendedResponses.
        if (next.length == 0 || next[0].isResponse()) {
            List<Envelope> waitingResponses = new ArrayList<>();
            List<Envelope> allResponses = new ArrayList<>(next.length + selectableResponses.size());
            Collections.addAll(allResponses, next);
            allResponses.addAll(selectableResponses);
            allResponses.addAll(suspendedResponses);
            for (Envelope envelope : allResponses) {
                try {
                    if (trace) {
                        logInfo("Received response for target: " + envelope.requestId() +
                            " with value: " + envelope.message());
                    }
                    bindResponseValue(envelope);
                } catch (WaitException exc) {
                    waitingResponses.add(envelope);
                }
            }
            if (waitingResponses.size() == allResponses.size()) {
                // All responses failed to bind. Therefore, we leave waitState as-is and none of the responses are
                // selectable until we are able to bind a new response.
                suspendedResponses = waitingResponses;
                selectableResponses = Collections.emptyList();
                return NOT_FINISHED;
            }
            // Otherwise, we were able to bind some responses. We will now move waitingResponses to selectableResponses
            // and remain executable. It may take multiple passes to bind all responses because of responses depending
            // on other responses to complete.
            suspendedResponses = Collections.emptyList();
            selectableResponses = waitingResponses;
            if (trace) {
                logInfo("Resuming computation after binding response values");
            }
            computeTimeSlice();
            return NOT_FINISHED;
        } else {
            if (next.length != 1) {
                throw new IllegalArgumentException("Not a single envelope");
            }
            Envelope only = next[0];
            if (only.isControl()) {
                return onControl(only);
            }
            if (only.isNotify()) {
                computeTimeSliceUsingHandler((Value) only.message(), tellHandlerEntry);
                return NOT_FINISHED;
            }
            // We know we have a request
            activeRequest = only;
            computeTimeSliceUsingHandler((Value) only.message(), askHandlerEntry);
            return NOT_FINISHED;
        }
    }

    /*
     * An original mapping may begin as P -> (P, P') specifying that binding P synchronizes P and P'. However,
     * if P is bound to a partial record {feature: X}, as an example, then the mapping P -> (P, P') is replaced with
     * a new mapping of X -> (P, P') specifying that binding field value X synchronizes P with P'. This process is
     * iterative in cases where P is bound to a compound partial record, such as {name: X, address: Y}. Two possible
     * binding sequences in that case are P -> (P, P'), X -> (P, P'), Y -> (P, P'); and P -> (P, P'), Y -> (P, P'),
     * X -> (P, P'). If all of P's components are bound before P itself is bound, then P is a complete value. If some
     * but not all of P's components are bound before P itself, then the bound components are not present in the
     * possible binding sequences.
     */
    private void onParentVarBound(Var triggerVar, Value value) {
        if (trace) {
            logInfo("Trigger fired on var " + triggerVar + " with value: " + value);
        }
        List<ChildVar> childVars = triggers.remove(triggerVar);
        if (childVars != null) {
            for (ChildVar childVar : childVars) {
                Complete parentComplete;
                try {
                    // Resolve parent var as a complete value
                    parentComplete = childVar.parentVar.resolveValueOrVar().checkComplete();
                } catch (WaitVarException wx) {
                    if (trace) {
                        logInfo("Cannot synchronize parent var " + childVar.parentVar +
                            " because we are waiting to bind " + wx.barrier());
                    }
                    // The parentVar is not yet complete. Therefore, we need to create a new trigger to try again
                    // when the next part of parentVar is completed.
                    Var nextTriggerVar = wx.barrier();
                    List<ChildVar> nextChildVars = triggers.get(nextTriggerVar);
                    if (nextChildVars == null) {
                        nextChildVars = childVars;
                        triggers.put(nextTriggerVar, nextChildVars);
                        nextTriggerVar.setBindCallback(this::onParentVarBound);
                    } else {
                        nextChildVars.addAll(childVars);
                    }
                    return;
                }
                if (trace) {
                    logInfo("Synchronizing from parent var " + childVar.parentVar +
                        " to child var " + childVar.childVar + " at actor: " + childVar.child.address() +
                        " with value: " + parentComplete);
                }
                childVar.child.send(Envelope.createControlNotify(new SyncVar(childVar.childVar, parentComplete)));
            }
        }
    }

    protected final void onReceivedAfterFailed(Envelope envelope) {
        if (envelope.isRequest()) {
            envelope.requester().send(Envelope.createResponse(failedValue, envelope.requestId()));
        } else {
            super.onReceivedAfterFailed(envelope);
        }
    }

    private OnMessageResult onResume() {
        if (trace) {
            logInfo("Resuming computation");
        }
        computeTimeSlice();
        return NOT_FINISHED;
    }

    private OnMessageResult onStop(Envelope envelope) {
        if (envelope.requester() != null) {
            envelope.requester().send(Envelope.createControlResponse(Stop.SINGLETON, envelope.requestId()));
        }
        return FINISHED;
    }

    /*
     * SyncVar synchronizes a free variable by passing a completed value from the parent to the child. Because the
     * free child variable is unbound (not an undetermined record), we should never trigger a WaitException.
     */
    private OnMessageResult onSyncVar(SyncVar syncVar) {
        if (trace) {
            logInfo("Synchronizing var: " + syncVar.var + " with value: " + syncVar.value);
        }
        try {
            syncVar.var.bindToValue(syncVar.value, null);
        } catch (WaitException exc) {
            throw new IllegalStateException("Received WaitException binding a SyncVar message");
        }
        computeTimeSlice();
        return NOT_FINISHED;
    }

    @Override
    protected final void onUnhandledError(Mailbox mailbox, Throwable throwable) {
        // CREATE FAILED VALUE
        if (throwable instanceof MachineHaltError machineHaltError) {
            ComputeHalt computeHalt = machineHaltError.computeHalt();
            if (computeHalt.touchedFailedValue != null) {
                // This means the halt occurred in another actor and the machine threw a FailedValueError
                failedValue = new FailedValue(address().toString(), computeHalt.touchedFailedValue.error(),
                    computeHalt.current, computeHalt.touchedFailedValue, computeHalt.nativeCause);
            } else {
                // This means the halt occurred in this actor and the machine threw an UncaughtThrowError
                failedValue = new FailedValue(address().toString(), computeHalt.uncaughtThrow,
                    computeHalt.current, null, computeHalt.nativeCause);
            }
        } else {
            failedValue = FailedValue.create(address().toString(), machine.stack(), throwable);
        }
        // RESPOND TO ACTIVE REQUEST
        if (activeRequest != null) {
            if (trace) {
                logRespondingWithValue(failedValue);
            }
            activeRequest.requester().send(Envelope.createResponse(failedValue, activeRequest.requestId()));
        } else {
            String errorText = "Actor halted\n" + failedValue.toDetailsString();
            logError(errorText);
        }
        // EMPTY THE MAILBOX WHILE RESPONDING TO REQUESTS
        while (!mailbox.isEmpty()) {
            Envelope next = mailbox.removeNext();
            if (next.isRequest()) {
                next.requester().send(Envelope.createResponse(failedValue, next.requestId()));
            }
        }
    }

    private void performCallbackToAct(List<CompleteOrIdent> ys, Env env, Machine machine) {

        LocalActor child = new LocalActor(nextChildAddress(), system, trace);

        ActStmt actStmt = (ActStmt) machine.current().stmt;

        HashSet<Ident> lexicallyFree = new HashSet<>();
        actStmt.captureLexicallyFree(new HashSet<>(), lexicallyFree);

        List<EnvEntry> childInput = new ArrayList<>();
        for (Ident freeIdent : lexicallyFree) {
            if (ROOT_ENV.contains(freeIdent) || freeIdent.equals(actStmt.target)) {
                continue;
            }
            Var parentVar = env.get(freeIdent);
            ValueOrVar valueOrVar = parentVar.resolveValueOrVar();
            Var childVar;
            if (valueOrVar instanceof Var) {
                childVar = new Var();
                addParentVarDependency(parentVar, parentVar, childVar, child);
            } else {
                try {
                    childVar = new Var(valueOrVar.checkComplete());
                } catch (WaitVarException wx) {
                    childVar = new Var();
                    addParentVarDependency(wx.barrier(), parentVar, childVar, child);
                }
            }
            childInput.add(new EnvEntry(freeIdent, childVar));
        }

        ArrayList<Stmt> stmtList = new ArrayList<>(2);
        stmtList.add(actStmt.stmt);
        stmtList.add(new ApplyStmt(Ident.$RESPOND, List.of(actStmt.target), actStmt.sourceSpan.toSourceSpanEnd()));
        SeqStmt seq = new SeqStmt(stmtList, actStmt.sourceSpan);
        ValueOrVar responseTarget = actStmt.target.resolveValueOrVar(env);
        Act act = new Act(seq, actStmt.target, childInput);
        child.send(Envelope.createControlRequest(act, LocalActor.this, new ValueOrVarRef(responseTarget)));
    }

    private void performCallbackToSelf(List<CompleteOrIdent> ys, Env env, Machine machine) {
        throw new NeedsImpl();
    }

    private void performCallbackToSpawn(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != 2) {
            throw new InvalidArgCountError(2, ys, "LocalActor.onCallbackToSpawn");
        }
        // Let's resolve the target early in case there is a variable error. We do not want to spawn an actor only to
        // find out that the variable is not found.
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        Value config = ys.get(0).resolveValue(env);
        ActorRefObj childRefObj;
        if (config instanceof ActorCfg actorCfg) {
            childRefObj = spawnActorCfg(actorCfg);
        } else {
            childRefObj = spawnNativeActorCfg((NativeActorCfg) config);
        }
        target.bindToValue(childRefObj, null);
    }

    protected final Envelope[] selectNext(Mailbox mailbox) {
        Envelope first = mailbox.removeNext();
        if (first == null) {
            // Although there are no messages in the mailbox, we are executable because we contain selectable
            // responses. Therefore, return an empty batch of envelopes. (see isExecutable)
            return new Envelope[0];
        }
        if (!first.isResponse()) {
            return new Envelope[]{first};
        }
        ArrayList<Envelope> responses = new ArrayList<>();
        responses.add(first);
        Envelope nextEnvelope = mailbox.peekNext();
        while (nextEnvelope != null && nextEnvelope.isResponse()) {
            responses.add(mailbox.removeNext());
            nextEnvelope = mailbox.peekNext();
        }
        return responses.toArray(new Envelope[0]);
    }

    private void sendResponse(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitVarException {
        if (ys.size() != 1) {
            throw new InvalidArgCountError(1, ys, "LocalActor.sendResponse");
        }
        Value candidateValue = ys.get(0).resolveValue(env);
        // Check complete accomplishes the following:
        //   1. If candidate value is not completable, throw a CannotConvertToComplete error
        //   2. If candidate value is complete, we can progress and the value will be sent
        //   2. If candidate value is partial, a WaitException is thrown
        Complete responseValue = candidateValue.checkComplete();
        // Check for the subtle case where a 'respond' expression simply returns the result of an 'ask'
        // expression. For example, consider the following 'query' handler that returns the result of a call
        // to 'OrderDaoRef.ask(findOrder#{orderId: Id})':
        //     respond query#{orderId: Id::Str} in
        //         OrderDaoRef.ask(findOrder#{orderId: Id})
        //     end
        // Instead of simply returning the child FailedValue, we want to return a FailedValue chain that includes
        // the parent.
        if (responseValue instanceof FailedValue childFailedValue) {
            responseValue = new FailedValue(address().toString(), childFailedValue.error(),
                machine.current(), childFailedValue, null);
        }
        if (trace) {
            logRespondingWithValue(responseValue);
        }
        activeRequest.requester().send(Envelope.createResponse(responseValue, activeRequest.requestId()));
    }

    private ActorRefObj spawnActorCfg(ActorCfg parentCfg) throws WaitException {

        /*
            (The following was copied, see `Value.java` for more information.)

            There is one exception to all of the above. When an actor spawns another actor, we delay verifying the
            actor configuration for as long as possible to opportunistically increase concurrency. During the spawn
            callback, we dynamically verify that the `ActorCfg` is effectively complete by verifying that its free
            variables are complete.
         */

        // Map each captured environment entry from the parent environment to the child environment.

        Closure parentHandlersCtor = parentCfg.handlersCtor();
        Env parentCapturedEnv = parentHandlersCtor.capturedEnv();
        HashMap<Ident, Var> childCapturedEnvMap = new HashMap<>();
        for (EnvEntry parentEntry : parentCapturedEnv) {
            // We can skip free variables that reference root env entries because the root env is static (and Complete)
            // and in many cases, its entries simply callback to the invoking actor.
            if (ROOT_ENV.contains(parentEntry.ident)) {
                continue;
            }
            // INVARIANT: All other free vars must be complete values because we only communicate complete and
            // immutable values across actor boundaries.
            ValueOrVar parentValueOrVar = parentEntry.var.resolveValueOrVar();
            // CRITICAL: If 'checkComplete()' throws a WaitException, the KLVM will suspend and then retry this entire
            // method once the unbound var becomes bound.
            childCapturedEnvMap.put(parentEntry.ident, new Var(parentValueOrVar.checkComplete()));
        }
        Env childCapturedEnv = Env.create(ROOT_ENV, childCapturedEnvMap);

        // We now have complete values (we are past the point of a potential WaitException) and are ready to create and
        // spawn the child actor.

        Closure childHandlersCtor = new Closure(parentHandlersCtor.procDef(), childCapturedEnv);
        ActorCfg childConfig = new ActorCfg(parentCfg.args(), childHandlersCtor);
        Configure configure = new Configure(childConfig);
        LocalActor childActor = new LocalActor(nextChildAddress(), system, trace);

        childActor.send(Envelope.createControlNotify(configure));

        return new ActorRefObj(childActor);
    }

    private ActorRefObj spawnNativeActorCfg(NativeActorCfg nativeActorCfg) {
        ActorRef actorRef = nativeActorCfg.spawn(nextChildAddress(), system, trace);
        return new ActorRefObj(actorRef);
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + address() + ")";
    }

    private static final class Act {
        private final SeqStmt seq;
        private final Ident target;
        private final List<EnvEntry> input;

        private Act(SeqStmt seq, Ident target, List<EnvEntry> input) {
            this.seq = seq;
            this.target = target;
            this.input = nullSafeCopyOf(input);
        }
    }

    private static final class ChildVar {
        private final Var parentVar;
        private final Var childVar;
        private final LocalActor child;

        private ChildVar(Var parentVar, Var childVar, LocalActor child) {
            this.parentVar = parentVar;
            this.childVar = childVar;
            this.child = child;
        }
    }

    private static final class Configure {
        private final ActorCfg actorCfg;

        private Configure(ActorCfg actorCfg) {
            this.actorCfg = actorCfg;
        }
    }

    private static final class Resume implements Envelope {
        private static final Resume SINGLETON = new Resume();

        private Resume() {
        }

        @Override
        public final boolean isControl() {
            return true;
        }

        @Override
        public final Object message() {
            return Nothing.SINGLETON;
        }

        @Override
        public final Object requestId() {
            return null;
        }

        @Override
        public final ActorRef requester() {
            return null;
        }
    }

    static final class StreamCls implements CompleteObj {
        static final StreamCls SINGLETON = new StreamCls();

        private static final CompleteProc STREAM_CLS_NEW = StreamCls::clsNew;

        private StreamCls() {
        }

        private static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
            final int expectedCount = 3;
            if (ys.size() != expectedCount) {
                throw new InvalidArgCountError(expectedCount, ys, "LocalActor.Stream.new");
            }
            ActorRefObj publisher = (ActorRefObj) ys.get(0).resolveValue(env);
            Complete requestMessage = (Complete) ys.get(1).resolveValue(env);
            StreamObj streamObj = new StreamObj(machine.owner(), publisher, requestMessage);
            ValueOrVar target = ys.get(2).resolveValueOrVar(env);
            target.bindToValue(streamObj, null);
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return STREAM_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

    private static final class StreamEntry {

        private final ValueOrVar element;
        private StreamEntry nextEntry;

        private StreamEntry() {
            this.element = new Var();
            this.nextEntry = null;
        }

        private StreamEntry(Complete element) {
            this.element = element;
            this.nextEntry = null;
        }

        private void setNextEntry(StreamEntry nextEntry) {
            if (this.nextEntry != null) {
                throw new IllegalStateException("Next entry is already set");
            }
            this.nextEntry = nextEntry;
        }

    }

    private static class StreamIter implements ValueIter {

        private final LocalActor localActor;
        private final StreamObj streamObj;

        private boolean waiting = false;

        private StreamIter(LocalActor localActor, StreamObj streamObj) {
            this.streamObj = streamObj;
            this.localActor = localActor;
        }

        @Override
        public void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {

            if (localActor.trace) {
                localActor.logInfo("StreamIter performing an iteration");
            }

            if (ys.size() != VALUE_ITER_ARG_COUNT) {
                throw new InvalidArgCountError(VALUE_ITER_ARG_COUNT, ys, "LocalActor.StreamIter()");
            }

            ValueOrVar headValueOrVar = streamObj.head.element.resolveValueOrVar();

            if (waiting) {
                if (headValueOrVar instanceof Var var) {
                    if (localActor.trace) {
                        localActor.logInfo("StreamIter cannot iterate because we are already waiting, throwing a WaitVarException");
                    }
                    throw new WaitVarException(var);
                }
                waiting = false;
                streamObj.head = streamObj.head.nextEntry;
                headValueOrVar = streamObj.head.element.resolveValueOrVar();
            }

            if (headValueOrVar instanceof Var var) {
                if (localActor.trace) {
                    localActor.logInfo("StreamIter binding unbound stream head " + var + " to identifier " + ys.get(0));
                }
                ValueOrVar y = ys.get(0).resolveValueOrVar(env);
                var.bindToValueOrVar(y, null);
                waiting = true;
                return;
            }

            Complete headValue = (Complete) headValueOrVar;
            ValueOrVar y = ys.get(0).resolveValueOrVar(env);
            if (localActor.trace) {
                localActor.logInfo("StreamIter binding next value " + headValue + " to iterator variable " + y);
            }
            y.bindToValue(headValue, null);
            if (headValue != Eof.SINGLETON) {
                streamObj.head = streamObj.head.nextEntry;
            }
        }

    }

    private static final class StreamObj implements Obj, ValueIterSource {
        private final LocalActor localActor;
        private final ActorRefObj publisher;
        private final RequestId requestId;
        private final Complete requestMessage;
        private final StreamIter streamIter;

        private StreamEntry head = new StreamEntry();
        private StreamEntry tail = head;

        private StreamObj(LocalActor localActor, ActorRefObj publisher, Complete requestMessage) {
            this.localActor = localActor;
            this.publisher = publisher;
            this.requestId = new StreamObjRef(this);
            this.requestMessage = requestMessage;
            this.streamIter = new StreamIter(localActor, this);
            fetchNextFromPublisher();
        }

        private void appendRemainingResponseValues(CompleteTuple values) {
            for (int i = 1; i < values.fieldCount(); i++) {
                Complete appendValue = values.valueAt(i);
                if (localActor.trace) {
                    localActor.logInfo("StreamObj appending response to stream tail: " + appendValue);
                }
                StreamEntry newTail = new StreamEntry(appendValue);
                tail.setNextEntry(newTail);
                tail = newTail;
            }
            appendUnboundTail();
        }

        private void appendUnboundTail() {
            StreamEntry unboundTail = new StreamEntry();
            tail.setNextEntry(unboundTail);
            tail = unboundTail;
        }

        private void fetchNextFromPublisher() {
            if (localActor.trace) {
                localActor.logInfo("StreamObj sending request " + requestMessage + " to " + publisher.referent().address());
            }
            publisher.referent().send(Envelope.createRequest(requestMessage, localActor, requestId));
            if (localActor.trace) {
                localActor.logInfo("StreamObj request " + requestMessage + " sent to " + publisher.referent().address());
            }
        }

        @Override
        public final ValueOrVar select(Feature feature) {
            throw new NeedsImpl();
        }

        @Override
        public final ValueOrVar valueIter() {
            return streamIter;
        }
    }

    private static final class StreamObjRef extends OpaqueValue implements RequestId {
        private final StreamObj streamObj;

        private StreamObjRef(StreamObj streamObj) {
            this.streamObj = streamObj;
        }
    }

    private static final class SyncVar {
        private final Var var;
        private final Complete value;

        private SyncVar(Var var, Complete value) {
            this.var = var;
            this.value = value;
        }
    }

}
