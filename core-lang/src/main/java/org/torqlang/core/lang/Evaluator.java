/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Note that as we transition forward in the process, we gain properties and loose methods.
 *
 * State transitions
 * =================
 *             (begin)          INIT
 * INIT        setSource        READY
 * INIT        setSntcOrExpr    PARSED
 * READY       parse            PARSED
 * PARSED      generate         GENERATED
 * GENERATED   perform          PERFORMED
 * PERFORMED   (end)
 *
 * Properties and Methods
 * ======================
 *
 * INIT
 *   properties: (none)
 *   methods:    setRootEnv, setExprIdent, setMaxTime, addVar, setSource, setSntcOrExpr
 * READY
 *   properties: rootEnv, exprIdent, maxTime, source
 *   methods:    parse, generate, perform
 * PARSED
 *   properties: rootEnv, exprIdent, maxTime, source, sntcOrExpr
 *   methods:    generate, perform
 * GENERATED
 *   properties: rootEnv, exprIdent, maxTime, source, sntcOrExpr, kernel
 *   methods:    perform
 * PERFORMED
 *   properties: rootEnv, env, exprIdent, maxTime, source, sntcOrExpr, kernel
 *   methods:    (none)
 */
public final class Evaluator implements EvaluatorInit, EvaluatorReady, EvaluatorParsed,
    EvaluatorGenerated, EvaluatorPerformed
{
    private final List<EnvEntry> envEntries;

    private State state;

    private Env env;
    private Env rootEnv;
    private Ident exprIdent;
    private String source;
    private SntcOrExpr sntcOrExpr;
    private Kernel kernel;
    private long maxTime;

    private Evaluator() {
        rootEnv = Env.emptyEnv();
        maxTime = 10_000;
        envEntries = new ArrayList<>();
        state = State.INIT;
    }

    public static EvaluatorInit builder() {
        return new Evaluator();
    }

    @Override
    public final EvaluatorInit addVar(Ident ident) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot addVar at state: " + state);
        }
        envEntries.add(new EnvEntry(ident, new Var()));
        return this;
    }

    @Override
    public final EvaluatorInit addVar(Ident ident, Var var) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot addVar at state: " + state);
        }
        envEntries.add(new EnvEntry(ident, var));
        return this;
    }

    @Override
    public final Env env() {
        return env;
    }

    @Override
    public Ident exprIdent() {
        return exprIdent;
    }

    @Override
    public final EvaluatorGenerated generate() throws Exception {
        if (state == State.READY) {
            parse();
        }
        if (state != State.PARSED) {
            throw new IllegalStateException("Cannot generate at state: " + state);
        }
        Generator g = new Generator();
        if (exprIdent != null) {
            kernel = g.acceptExpr(sntcOrExpr, exprIdent);
        } else {
            kernel = g.acceptSntc(sntcOrExpr);
        }
        state = State.GENERATED;
        return this;
    }

    @Override
    public final Kernel kernel() {
        return kernel;
    }

    @Override
    public final long maxTime() {
        return maxTime;
    }

    @Override
    public final EvaluatorParsed parse() throws Exception {
        if (state != State.READY) {
            throw new IllegalStateException("Cannot parse at state: " + state);
        }
        Parser p = new Parser(source);
        sntcOrExpr = p.parse();
        state = State.PARSED;
        return this;
    }

    @Override
    public final EvaluatorPerformed perform() throws Exception {
        if (state == State.READY) {
            parse();
        }
        if (state == State.PARSED) {
            generate();
        }
        if (state != State.GENERATED) {
            throw new IllegalStateException("Cannot perform at state: " + state);
        }
        env = Env.create(rootEnv, envEntries);
        Stack stack = new Stack((Stmt) kernel, env, null);
        Machine machine = new Machine(stack);
        Machine.compute(machine, maxTime);
        state = State.PERFORMED;
        return this;
    }

    @Override
    public final Env rootEnv() {
        return rootEnv;
    }

    @Override
    public final EvaluatorInit setExprIdent(Ident exprIdent) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setExprIdent at state: " + state);
        }
        this.exprIdent = exprIdent;
        return this;
    }

    @Override
    public final Evaluator setMaxTime(long maxTime) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setMaxTime at state: " + state);
        }
        this.maxTime = maxTime;
        return this;
    }

    @Override
    public final EvaluatorInit setRootEnv(Env rootEnv) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setRootEnv at state: " + state);
        }
        this.rootEnv = rootEnv;
        return this;
    }

    @Override
    public final EvaluatorReady setSntcOrExpr(SntcOrExpr sntcOrExpr) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setSntcOrExpr at state: " + state);
        }
        this.sntcOrExpr = sntcOrExpr;
        state = State.READY;
        return this;
    }

    @Override
    public final EvaluatorReady setSource(String source) {
        if (state != State.INIT) {
            throw new IllegalStateException("Cannot setSource at state: " + state);
        }
        this.source = source;
        state = State.READY;
        return this;
    }

    @Override
    public final SntcOrExpr sntcOrExpr() {
        return sntcOrExpr;
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final Var varAtName(String name) {
        return env.get(Ident.createPrivately(name));
    }

    private enum State {
        INIT,
        READY,
        PARSED,
        GENERATED,
        PERFORMED
    }

}
