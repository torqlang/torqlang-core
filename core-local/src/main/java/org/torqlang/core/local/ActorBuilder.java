/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ActorExpr;
import org.torqlang.core.lang.ActorSntc;
import org.torqlang.core.lang.Generator;
import org.torqlang.core.lang.Parser;
import org.torqlang.core.util.ListTools;
import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Note that as we transition forward in the process, we gain properties and loose methods.
 *
 * State transitions
 * =================
 *             (begin)          INIT
 * INIT        setSource        READY
 * INIT        setActorSntc     PARSED
 * READY       parse            PARSED
 * PARSED      rewrite          REWRITTEN      rewrite   --> actorExpr
 * REWRITTEN   generate         GENERATED      generate  --> createActorRecStmt
 * GENERATED   construct        CONSTRUCTED    construct --> actorRec
 * CONSTRUCTED configure        CONFIGURED     configure --> actorCfg
 * CONFIGURED  spawn            SPAWNED
 * SPAWNED     (end)
 *
 * Properties and Methods
 * ======================
 * INIT
 *   properties: (none)
 *   methods:    setAddress, setArgs, setTrace, setSystem, setSource, setActorSntc, setActorCfg
 * READY
 *   properties: source
 *   methods:    parse, rewrite, generate, construct, configure, spawn
 * PARSED
 *   properties: source, actorSntc
 *   methods:    rewrite, generate, construct, configure, spawn
 * REWRITTEN
 *   properties: source, actorSntc, actorIdent, actorExpr
 *   methods:    generate, construct, configure, spawn
 * GENERATED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt
 *   methods:    construct, configure, spawn
 * CONSTRUCTED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt, actorRec
 *   methods:    configure, spawn
 * CONFIGURED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt, actorRec, actorCfg
 *   methods:    spawn
 * SPAWNED
 *   properties: source, actorSntc, actorIdent, actorExpr, createActorRecStmt, actorRec, actorCfg, actorRef
 *   methods:    (none)
 *
 * Not shown above are the properties system, address, args, and trace, which are available after INIT.
 */
public final class ActorBuilder implements ActorBuilderInit, ActorBuilderReady, ActorBuilderParsed,
    ActorBuilderRewritten, ActorBuilderGenerated, ActorBuilderConstructed, ActorBuilderConfigured, ActorBuilderSpawned
{
    private static final Str CFG = Str.of("cfg");
    private static final int TIME_SLICE_10_000 = 10_000;

    private State state;

    private Address address;
    private ActorImage actorImage;
    private ActorSystem system;
    private String source;
    private boolean trace;
    private ActorExpr actorExpr;
    private ActorSntc actorSntc;
    private Ident actorIdent;
    private Stmt createActorRecStmt;
    private Rec actorRec;
    private List<? extends CompleteOrIdent> args = List.of();
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
    public final ActorImage actorImage() {
        if (actorImage == null) {
            checkAddress();
            Object response;
            try {
                response = RequestClient.builder()
                    .setAddress(address)
                    .send(actorRef(), CaptureImage.SINGLETON)
                    .awaitResponse(10, TimeUnit.MILLISECONDS);
                if (response instanceof FailedValue failedValue) {
                    throw new IllegalStateException(failedValue.toString());
                }
            } catch (Exception exc) {
                throw new IllegalStateException(exc);
            }
            actorImage = (ActorImage) response;
        }
        return actorImage;
    }

    @Override
    public final ActorImage actorImage(String source) throws Exception {
        setSource(source);
        spawn();
        return actorImage;
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

    private void checkAddress() {
        if (address == null) {
            address = Address.UNDEFINED;
        }
    }

    private void checkSystem() {
        if (system == null) {
            system = ActorSystem.defaultSystem();
        }
    }

    @Override
    public final ActorCfg config() {
        return actorCfg;
    }

    @Override
    public final ActorBuilderConfigured configure() throws Exception {
        parseRewriteGenerateConstruct();
        if (state != State.CONSTRUCTED) {
            throw new IllegalStateException("Cannot spawn at state: " + state);
        }
        // The actor record will contain values (not vars). Therefore, we can access the ActorCfgtr directly.
        ActorCfgtr actorCfgtr = (ActorCfgtr) actorRec.findValue(CFG);
        Env env = Env.create(LocalActor.rootEnv(),
            List.of(
                new EnvEntry(Ident.$ACTOR_CFGTR, new Var(actorCfgtr)),
                new EnvEntry(Ident.$R, new Var())
            )
        );
        List<CompleteOrIdent> argsWithTarget = ListTools.append(CompleteOrIdent.class, args, Ident.$R);
        List<Stmt> localStmts = new ArrayList<>();
        localStmts.add(new ApplyStmt(Ident.$ACTOR_CFGTR, argsWithTarget, SourceSpan.emptySourceSpan()));
        SeqStmt seqStmt = new SeqStmt(localStmts, SourceSpan.emptySourceSpan());
        Stack stack = new Stack(seqStmt, env, null);
        Machine.compute(stack, TIME_SLICE_10_000);
        try {
            actorCfg = (ActorCfg) env.get(Ident.$R).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        state = State.CONFIGURED;
        return this;
    }

    @Override
    public final ActorBuilderConfigured configure(String source) throws Exception {
        setSource(source);
        return configure();
    }

    @Override
    public final ActorBuilderConstructed construct() throws Exception {
        parseRewriteGenerate();
        if (state != State.GENERATED) {
            throw new IllegalStateException("Cannot createActorRec at state: " + state);
        }
        Env env = Env.create(LocalActor.rootEnv(), new EnvEntry(actorIdent, new Var()));
        Stack stack = new Stack(createActorRecStmt, env, null);
        Machine.compute(stack, TIME_SLICE_10_000);
        try {
            actorRec = (Rec) env.get(actorIdent).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        state = State.CONSTRUCTED;
        return this;
    }

    @Override
    public final ActorBuilderConstructed construct(String source) throws Exception {
        setSource(source);
        return construct();
    }

    @Override
    public final Stmt createActorRecStmt() {
        return createActorRecStmt;
    }

    @Override
    public final ActorBuilderGenerated generate() throws Exception {
        parseRewrite();
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

    private void parseRewrite() {
        if (state == State.READY) {
            parse();
        }
        if (state == State.PARSED) {
            rewrite();
        }
    }

    private void parseRewriteGenerate() throws Exception {
        parseRewrite();
        if (state == State.REWRITTEN) {
            generate();
        }
    }

    private void parseRewriteGenerateConstruct() throws Exception {
        parseRewriteGenerate();
        if (state == State.GENERATED) {
            construct();
        }
    }

    private void parseRewriteGenerateConstructConfigure() throws Exception {
        parseRewriteGenerateConstruct();
        if (state == State.CONSTRUCTED) {
            configure();
        }
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
    public final ActorBuilderConfigured setActorCfg(ActorCfg actorCfg) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setActorSntc at state: " + state);
        }
        this.actorCfg = actorCfg;
        state = State.CONFIGURED;
        return this;
    }

    @Override
    public final ActorBuilderConstructed setActorRec(Rec actorRec) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setActorRec at state: " + state);
        }
        this.actorRec = actorRec;
        state = State.CONSTRUCTED;
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
    public final ActorBuilderInit setSystem(ActorSystem system) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setSystem at state: " + state);
        }
        this.system = system;
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
    public final ActorBuilderSpawned spawn(ActorCfg actorCfg) throws Exception {
        setActorCfg(actorCfg);
        return spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(Rec actorRec) throws Exception {
        setActorRec(actorRec);
        return spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(String source) throws Exception {
        setSource(source);
        return spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn() throws Exception {
        parseRewriteGenerateConstructConfigure();
        if (state != State.CONFIGURED) {
            throw new IllegalStateException("Cannot spawn at state: " + state);
        }
        checkAddress();
        checkSystem();
        localActor = new LocalActor(address, system, trace);
        localActor.configure(actorCfg);
        state = State.SPAWNED;
        return this;
    }

    public final ActorSystem system() {
        return system;
    }

    private enum State {
        INIT,
        READY,
        PARSED,
        REWRITTEN,
        GENERATED,
        CONSTRUCTED,
        CONFIGURED,
        SPAWNED
    }

}
