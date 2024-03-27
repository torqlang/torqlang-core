/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Address;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ActorExpr;
import org.torqlang.core.lang.ActorSntc;
import org.torqlang.core.lang.Generator;
import org.torqlang.core.lang.Parser;
import org.torqlang.core.util.ListTools;
import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.core.local.ActorSystem.*;

/*
 * Note that as we transition forward in the process, we gain properties and loose methods.
 *
 * State transitions
 * =================
 *             (begin)          INIT
 * INIT        setSource        READY
 * INIT        setActorSntc     PARSED
 * READY       parse            PARSED
 * PARSED      rewrite          REWRITTEN
 * REWRITTEN   generate         GENERATED
 * GENERATED   createActorRec   CONSTRUCTED
 * CONSTRUCTED spawn            SPAWNED
 * SPAWNED     (end)
 *
 * Properties and Methods
 * ======================
 * INIT
 *   properties: (none)
 *   methods:    setActorSntc, setSource, setAddress, setArgs, setTrace
 * READY
 *   properties: source
 *   methods:    parse, rewrite, generate, createActorRec, spawn
 * PARSED
 *   properties: source, actorSntc
 *   methods:    rewrite, generate, createActorRec, spawn
 * REWRITTEN
 *   properties: source, actorSntc, actorIdent, actorExpr
 *   methods:    generate, createActorRec, spawn
 * GENERATED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt
 *   methods:    createActorRec, spawn
 * CONSTRUCTED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt, actorRec
 *   methods:    spawn
 *  SPAWNED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt, actorRec, actorRef
 *   methods:    (none)
 *
 * Not shown above are the properties address, args, and trace, which are available after INIT. They do not cause a
 * state change, so they are not mentioned.
 */
public final class ActorBuilder implements ActorBuilderInit, ActorBuilderReady, ActorBuilderParsed,
    ActorBuilderRewritten, ActorBuilderGenerated, ActorBuilderConstructed, ActorBuilderSpawned
{
    private static final Str CFG = Str.of("cfg");
    private static final int TIME_SLICE_1000 = 10_000;

    private State state;

    private Address address;
    private String source;
    private boolean trace;
    private ActorExpr actorExpr;
    private ActorSntc actorSntc;
    private Ident actorIdent;
    private Stmt createActorRecStmt;
    private Rec actorRec;
    private List<CompleteOrIdent> args = List.of();
    private ActorCfg actorCfg;
    private LocalActor localActor;

    ActorBuilder() {
        state = State.INIT;
    }

    @Override
    public final ActorCfg actorCfg() {
        return actorCfg;
    }

    @Override
    public final ActorExpr actorExpr() {
        return actorExpr;
    }

    @Override
    public final Ident actorIdent() {
        return actorIdent;
    }

    @Override
    public final Rec actorRec() {
        return actorRec;
    }

    @Override
    public final ActorRef actorRef() {
        return localActor;
    }

    @Override
    public final ActorSntc actorSntc() {
        return actorSntc;
    }

    @Override
    public final Address address() {
        return address;
    }

    @Override
    public final List<? extends CompleteOrIdent> args() {
        return args;
    }

    @Override
    public final ActorBuilderConstructed createActorRec() throws Exception {
        if (state == State.READY) {
            parse();
        }
        if (state == State.PARSED) {
            rewrite();
        }
        if (state == State.REWRITTEN) {
            generate();
        }
        if (state != State.GENERATED) {
            throw new IllegalStateException("Cannot createActorRec at state: " + state);
        }
        Env env = Env.create(LocalActor.rootEnv(), new EnvEntry(actorIdent, new Var()));
        Stack stack = new Stack(createActorRecStmt, env, null);
        Machine.compute(new Machine(stack), TIME_SLICE_1000);
        try {
            actorRec = (Rec) env.get(actorIdent).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        state = State.CONSTRUCTED;
        return this;
    }

    @Override
    public final Stmt createActorRecStmt() {
        return createActorRecStmt;
    }

    @Override
    public final ActorBuilderGenerated generate() throws Exception {
        if (state == State.READY) {
            parse();
        }
        if (state == State.PARSED) {
            rewrite();
        }
        if (state != State.REWRITTEN) {
            throw new IllegalStateException("Cannot generate at state: " + state);
        }
        Generator g = new Generator();
        createActorRecStmt = g.acceptExpr(actorExpr, actorIdent);
        state = State.GENERATED;
        return this;
    }

    @Override
    public final ActorBuilderParsed parse() {
        if (state != State.READY) {
            throw new IllegalStateException("Cannot parse at state: " + state);
        }
        Parser p = new Parser(source);
        actorSntc = (ActorSntc) p.parse();
        state = State.PARSED;
        return this;
    }

    @Override
    public final ActorBuilderRewritten rewrite() {
        /*
            Rewrite:
                actor HelloWorld () in
                    ask 'hello' in
                        'Hello, World!'
                    end
                end
            As:
                HelloWorld = actor () in
                    ask 'hello' in
                        'Hello, World!'
                    end
                end
            Also, capture the actor name as an identifier.
         */
        if (state == State.READY) {
            parse();
        }
        if (state != State.PARSED) {
            throw new IllegalStateException("Cannot rewrite at state: " + state);
        }
        actorIdent = actorSntc.name;
        actorExpr = new ActorExpr(actorSntc.formalArgs, actorSntc.body, actorSntc);
        state = State.REWRITTEN;
        return this;
    }

    @Override
    public final ActorBuilderParsed setActorSntc(ActorSntc actorSntc) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setActorSntc at state: " + state);
        }
        this.actorSntc = actorSntc;
        state = State.PARSED;
        return this;
    }

    @Override
    public final ActorBuilderInit setAddress(Address address) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setAddress at state: " + state);
        }
        this.address = address;
        return this;
    }

    @Override
    public final ActorBuilderInit setArgs(List<? extends CompleteOrIdent> args) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setArgs at state: " + state);
        }
        if (args == null) {
            throw new NullPointerException("args");
        }
        this.args = List.copyOf(args);
        return this;
    }

    @Override
    public final ActorBuilderReady setSource(String source) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setSource at state: " + state);
        }
        this.source = source;
        state = State.READY;
        return this;
    }

    @Override
    public final ActorBuilderInit setTrace(boolean trace) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setTrace at state: " + state);
        }
        this.trace = trace;
        return this;
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final ActorRef spawn() throws Exception {
        if (state == State.READY) {
            parse();
        }
        if (state == State.PARSED) {
            rewrite();
        }
        if (state == State.REWRITTEN) {
            generate();
        }
        if (state == State.GENERATED) {
            createActorRec();
        }
        if (state != State.CONSTRUCTED) {
            throw new IllegalStateException("Cannot spawn at state: " + state);
        }
        // The actor record will contain resolved fields. Therefore, we can access the ActorCfgCtor directly.
        ActorCfgCtor actorCfgCtor = (ActorCfgCtor) actorRec.findValue(CFG);
        Env env = Env.create(LocalActor.rootEnv(),
            List.of(
                new EnvEntry(Ident.ACTOR_CFG_CTOR, new Var(actorCfgCtor)),
                new EnvEntry(Ident.RESULT, new Var())
            )
        );
        List<CompleteOrIdent> argsWithTarget = ListTools.append(CompleteOrIdent.class, args, Ident.RESULT);
        List<Stmt> localStmts = new ArrayList<>();
        localStmts.add(new ApplyStmt(Ident.ACTOR_CFG_CTOR, argsWithTarget, SourceSpan.emptySourceSpan()));
        SeqStmt seqStmt = new SeqStmt(localStmts, SourceSpan.emptySourceSpan());
        Stack stack = new Stack(seqStmt, env, null);
        Machine.compute(new Machine(stack), TIME_SLICE_1000);
        try {
            actorCfg = (ActorCfg) env.get(Ident.RESULT).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        localActor = new LocalActor(address, createMailbox(), computationExecutor(), createLogger(), trace);
        localActor.configure(actorCfg);
        state = State.SPAWNED;
        return localActor;
    }

    private enum State {
        INIT,
        READY,
        PARSED,
        REWRITTEN,
        GENERATED,
        CONSTRUCTED,
        SPAWNED
    }

}
