/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.FormatterState;
import org.torqlang.core.util.NeedsImpl;

import java.io.StringWriter;
import java.util.*;

/*
 * Format kernel values as kernel "source" (we do not have a formal grammar for the kernel language at this time).
 *
 * Statements are functions of Stack, Environment, and Memory. Statements that contain record and procedure
 * expressions actually contain definitions, such as RecDef and ProcDef, that drive the creation of values inside
 * memory. The KernelFormatter class is designed to handle the particulars of formatting values, such as circular
 * references.
 *
 * We can format kernel values from different roots:
 *     Compiled statements
 *         Includes statements, identifiers, definitions, and complete values
 *     Kernel memory values
 *         Includes values created by computed statements, such as closures created by CreateProcStmt and
 *         records created by CreateRecStmt
 *     Machine state
 *         Includes the stack and environment
 *         Includes kernel memory variables (see above)
 *         Includes compiled statements (see above)
 */
public final class KernelFormatter implements KernelVisitor<FormatterState, Void> {

    public static final KernelFormatter SINGLETON = new KernelFormatter();

    private static final String $ADD = "$add";
    private static final String $BIND = "$bind";
    private static final String $CREATE_ACTOR_CFGTR = "$create_actor_cfgtr";
    private static final String $CREATE_PROC = "$create_proc";
    private static final String $CREATE_REC = "$create_rec";
    private static final String $CREATE_TUPLE = "$create_tuple";
    private static final String $DIV = "$div";
    private static final String $EQ = "$eq";
    private static final String $GE = "$ge";
    private static final String $GET = "$get";
    private static final String $GT = "$gt";
    private static final String $JUMP_CATCH = "$jump_catch";
    private static final String $JUMP_THROW = "$jump_throw";
    private static final String $LE = "$le";
    private static final String $LT = "$lt";
    private static final String $MOD = "$mod";
    private static final String $MULT = "$mult";
    private static final String $NE = "$ne";
    private static final String $NEGATE = "$negate";
    private static final String $NOT = "$not";
    private static final String $SET = "$set";
    private static final String $SUB = "$sub";
    private static final String $SELECT = "$select";
    private static final String $SELECT_APPLY = "$select_apply";

    public final String format(Kernel kernel) {
        try (StringWriter sw = new StringWriter()) {
            FormatterState state = new FormatterState(sw);
            kernel.accept(this, state);
            state.flush();
            return sw.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void formatBinaryStmt(String oper, CompleteOrIdent a, CompleteOrIdent b, Ident x, FormatterState state) throws Exception {
        state.write(oper);
        state.write('(');
        a.accept(this, state.inline());
        state.write(", ");
        b.accept(this, state.inline());
        state.write(", ");
        x.accept(this, state.inline());
        state.write(')');
    }

    private void formatBindStmt(Kernel a, Kernel x, FormatterState state) throws Exception {
        state.write($BIND);
        state.write('(');
        a.accept(this, state.inline());
        state.write(", ");
        x.accept(this, state.inline());
        state.write(')');
    }

    @Override
    public final Void visitActStmt(ActStmt stmt, FormatterState state) throws Exception {
        state.write("$act");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.stmt.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitActorCfg(ActorCfg value, FormatterState state) throws Exception {
        state.write("// ActorCfg:");
        state.writeNewLineAndIndent();
        List<Complete> args = value.args();
        if (args.isEmpty()) {
            state.write("// args.size() == 0");
            state.writeNewLineAndIndent();
        }
        for (int i = 0; i < args.size(); i++) {
            Complete arg = args.get(i);
            state.write("// arg[");
            state.write(Integer.toString(i));
            state.write("]: ");
            arg.accept(this, state.inline());
            state.writeNewLineAndIndent();
        }
        value.handlersCtor().accept(this, state);
        return null;
    }

    @Override
    public final Void visitActorCfgtr(ActorCfgtr value, FormatterState state) throws Exception {
        return visitClosure(value.handlersCtor(), state);
    }

    @Override
    public final Void visitAddStmt(AddStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($ADD, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitApplyProcStmt(ApplyStmt stmt, FormatterState state) throws Exception {
        stmt.x.accept(this, state.inline());
        state.write('(');
        for (int i = 0; i < stmt.ys.size(); i++) {
            if (i > 0) {
                state.write(',');
                state.write(FormatterState.SPACE);
            }
            CompleteOrIdent y = stmt.ys.get(i);
            y.accept(this, state.inline());
        }
        state.write(')');
        return null;
    }

    @Override
    public final Void visitBindCompleteToCompleteStmt(BindCompleteToCompleteStmt stmt, FormatterState state) throws Exception {
        formatBindStmt(stmt.a, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitBindCompleteToIdentStmt(BindCompleteToIdentStmt stmt, FormatterState state) throws Exception {
        formatBindStmt(stmt.a, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitBindCompleteToValueOrVarStmt(BindCompleteToValueOrVarStmt stmt, FormatterState state) throws Exception {
        formatBindStmt(stmt.a, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitBindIdentToIdentStmt(BindIdentToIdentStmt stmt, FormatterState state) throws Exception {
        formatBindStmt(stmt.a, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitBool(Bool kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitCaseElseStmt(CaseElseStmt stmt, FormatterState state) throws Exception {
        state.write("case ");
        stmt.x.accept(this, state.inline());
        state.write(" of ");
        stmt.valueOrPtn.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.consequent.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("else");
        nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.alternate.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitCaseStmt(CaseStmt stmt, FormatterState state) throws Exception {
        state.write("case ");
        stmt.x.accept(this, state.inline());
        state.write(" of ");
        stmt.valueOrPtn.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.consequent.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitCatchStmt(CatchStmt stmt, FormatterState state) throws Exception {
        state.write("catch ");
        stmt.arg.accept(this, state.inline());
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.caseStmt.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public Void visitChar(Char kernel, FormatterState state) throws Exception {
        state.write('&');
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitClosure(Closure value, FormatterState state) throws Exception {
        if (value.capturedEnv().shallowSize() > 0) {
            visitEnv(value.capturedEnv(), state);
            state.writeNewLineAndIndent();
        }
        visitProcDef(value.procDef(), state);
        return null;
    }

    @Override
    public final Void visitCreateActorCfgtrStmt(CreateActorCfgtrStmt stmt, FormatterState state) throws Exception {
        state.write($CREATE_ACTOR_CFGTR);
        state.write('(');
        visitProcDef(stmt.procDef, state);
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitCreateProcStmt(CreateProcStmt stmt, FormatterState state) throws Exception {
        state.write($CREATE_PROC);
        state.write('(');
        visitProcDef(stmt.procDef, state);
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitCreateRecStmt(CreateRecStmt stmt, FormatterState state) throws Exception {
        state.write($CREATE_REC);
        state.write('(');
        stmt.recDef.accept(this, state.inline());
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitCreateTupleStmt(CreateTupleStmt stmt, FormatterState state) throws Exception {
        state.write($CREATE_TUPLE);
        state.write('(');
        stmt.tupleDef.accept(this, state.inline());
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitDec128(Dec128 kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        state.write('m');
        return null;
    }

    @Override
    public final Void visitDisentailsStmt(DisentailsStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($NE, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitDivideStmt(DivideStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($DIV, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitEntailsStmt(EntailsStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($EQ, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public Void visitEnv(Env env, FormatterState state) throws Exception {
        String envFormatted = env.formatValue();
        if (!envFormatted.isBlank()) {
            String[] lines = envFormatted.split("\n");
            for (int i = 0; i < lines.length; i++) {
                state.write("// ");
                state.write(lines[i]);
                if (i + 1 < lines.length) {
                    state.writeNewLineAndIndent();
                }
            }
        }
        return null;
    }

    @Override
    public final Void visitEof(Eof kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitFailedValue(FailedValue kernel, FormatterState state) throws Exception {
        state.write("FailedValue(error=");
        if (kernel.error() == null) {
            state.write("null");
        } else {
            kernel.error().accept(this, state.inline());
        }
        state.write(')');
        return null;
    }

    @Override
    public Void visitFieldDef(FieldDef kernel, FormatterState state) throws Exception {
        kernel.feature.accept(this, state.inline());
        state.write(": ");
        kernel.value.accept(this, state.inline());
        return null;
    }

    @Override
    public Void visitFieldPtn(FieldPtn kernel, FormatterState state) throws Exception {
        kernel.feature.accept(this, state.inline());
        state.write(": ");
        kernel.value.accept(this, state.inline());
        return null;
    }

    @Override
    public Void visitFlt32(Flt32 kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        state.write('f');
        return null;
    }

    @Override
    public Void visitFlt64(Flt64 kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitGetCellValueStmt(GetCellValueStmt stmt, FormatterState state) throws Exception {
        state.write($GET);
        state.write('(');
        stmt.cell.accept(this, state.inline());
        state.write(", ");
        stmt.target.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitGreaterThanOrEqualToStmt(GreaterThanOrEqualToStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($GE, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitGreaterThanStmt(GreaterThanStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($GT, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitIdent(Ident kernel, FormatterState state) throws Exception {
        if (Ident.isSimpleName(kernel.name)) {
            state.write(kernel.name);
        } else {
            state.write(Ident.quote(kernel.name));
        }
        return null;
    }

    @Override
    public final Void visitIdentDef(IdentDef kernel, FormatterState state) throws Exception {
        kernel.ident.accept(this, state.inline());
        if (kernel.value != null) {
            state.write(" = ");
            kernel.value.accept(this, state.inline());
        }
        return null;
    }

    @Override
    public Void visitIdentPtn(IdentPtn kernel, FormatterState state) throws Exception {
        if (kernel.escaped) {
            state.write('~');
        }
        kernel.ident.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitIfElseStmt(IfElseStmt stmt, FormatterState state) throws Exception {
        state.write("if ");
        stmt.x.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.consequent.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("else");
        nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.alternate.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitIfStmt(IfStmt stmt, FormatterState state) throws Exception {
        state.write("if ");
        stmt.x.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.consequent.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public Void visitInt32(Int32 kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public Void visitInt64(Int64 kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        state.write('L');
        return null;
    }

    @Override
    public Void visitJumpCatchStmt(JumpCatchStmt kernel, FormatterState state) throws Exception {
        state.write($JUMP_CATCH);
        state.write('(');
        state.write("" + kernel.id);
        state.write(')');
        return null;
    }

    @Override
    public Void visitJumpThrowStmt(JumpThrowStmt kernel, FormatterState state) throws Exception {
        state.write($JUMP_THROW);
        state.write('(');
        state.write("" + kernel.id);
        state.write(')');
        return null;
    }

    @Override
    public final Void visitLessThanOrEqualToStmt(LessThanOrEqualToStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($LE, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitLessThanStmt(LessThanStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($LT, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitLocalStmt(LocalStmt stmt, FormatterState state) throws Exception {
        state.write("local ");
        for (int i = 0; i < stmt.xs.size(); i++) {
            if (i > 0) {
                state.write(',');
                state.write(FormatterState.SPACE);
            }
            IdentDef id = stmt.xs.get(i);
            id.ident.accept(this, state.inline());
            if (id.value != null) {
                state.write(" = ");
                id.value.accept(this, state.inline());
            }
        }
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitModuloStmt(ModuloStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($MOD, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitMultiplyStmt(MultiplyStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($MULT, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitNegateStmt(NegateStmt stmt, FormatterState state) throws Exception {
        state.write($NEGATE);
        state.write('(');
        stmt.a.accept(this, state.inline());
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitNotStmt(NotStmt stmt, FormatterState state) throws Exception {
        state.write($NOT);
        stmt.a.accept(this, state.inline());
        state.write(", ");
        stmt.x.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitNothing(Nothing kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitObj(Obj kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitOpaqueValue(OpaqueValue kernel, FormatterState state) throws Exception {
        state.write(kernel.getClass().getName());
        return null;
    }

    @Override
    public final Void visitProc(Proc kernel, FormatterState state) throws Exception {
        state.write(kernel.getClass().getName());
        return null;
    }

    @Override
    public final Void visitProcDef(ProcDef kernel, FormatterState state) throws Exception {
        state.write("proc (");
        for (int i = 0; i < kernel.xs.size(); i++) {
            if (i > 0) {
                state.write(',');
                state.write(FormatterState.SPACE);
            }
            kernel.xs.get(i).accept(this, state.inline());
        }
        state.write(") in");
        List<Ident> freeIdents = new ArrayList<>(kernel.freeIdents);
        freeIdents.sort(Comparator.comparing(a -> a.name));
        Iterator<Ident> freeIdentsIter = freeIdents.iterator();
        if (freeIdentsIter.hasNext()) {
            state.write(" // free vars: ");
        }
        while (freeIdentsIter.hasNext()) {
            Ident ident = freeIdentsIter.next();
            ident.accept(this, state.inline());
            if (freeIdentsIter.hasNext()) {
                state.write(", ");
            }
        }
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        kernel.stmt.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public Void visitRec(Rec kernel, FormatterState state) throws Exception {
        if (kernel instanceof Tuple tuple) {
            visitTuple(tuple, null, state);
        } else {
            visitRec(kernel, null, state);
        }
        return null;
    }

    private void visitRec(Rec kernel, IdentityHashMap<Rec, Object> memos, FormatterState state) throws Exception {
        if (memos == null) {
            memos = new IdentityHashMap<>();
        }
        memos.put(kernel, Value.PRESENT);
        if (kernel.label() == Nothing.SINGLETON) {
            state.write('{');
        } else {
            kernel.label().accept(this, state.inline());
            state.write("#{");
        }
        Collection<Var> undeterminedVars = kernel.sweepUndeterminedVars();
        if (!undeterminedVars.isEmpty()) {
            for (Var v : undeterminedVars) {
                state.write(v.formatValue());
            }
        } else {
            for (int i = 0; i < kernel.fieldCount(); i++) {
                if (i > 0) {
                    state.write(", ");
                }
                kernel.featureAt(i).accept(this, state.inline());
                state.write(": ");
                visitRecValue(kernel.valueAt(i).resolveValueOrVar(), memos, state);
            }
        }
        state.write('}');
    }

    @Override
    public Void visitRecDef(RecDef kernel, FormatterState state) throws Exception {
        if (kernel.label.equals(Rec.DEFAULT_LABEL)) {
            state.write('{');
        } else {
            kernel.label.accept(this, state.inline());
            state.write("#{");
        }
        for (int i = 0; i < kernel.fieldCount(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            FieldDef fd = kernel.fieldDefAtIndex(i);
            fd.accept(this, state.inline());
        }
        state.write('}');
        return null;
    }

    @Override
    public final Void visitRecPtn(RecPtn kernel, FormatterState state) throws Exception {
        if (kernel.label().equals(Rec.DEFAULT_LABEL)) {
            state.write('{');
        } else {
            kernel.label().accept(this, state.inline());
            state.write("#{");
        }
        for (int i = 0; i < kernel.fieldCount(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            FieldPtn fp = kernel.fields().get(i);
            fp.accept(this, state.inline());
            if (i + 1 == kernel.fieldCount() && kernel.partialArity()) {
                state.write(", ...");
            }
        }
        state.write('}');
        return null;
    }

    private void visitRecValue(ValueOrVar value, IdentityHashMap<Rec, Object> memos, FormatterState state) throws Exception {
        if (value instanceof Rec recValue) {
            if (memos.containsKey(recValue)) {
                state.write("<<$circular " + Kernel.toSystemString(recValue) + ">>");
            } else {
                if (recValue instanceof Tuple tupleValue) {
                    visitTuple(tupleValue, memos, state.inline());
                } else {
                    visitRec(recValue, memos, state.inline());
                }
            }
        } else {
            value.accept(this, state.inline());
        }
    }

    @Override
    public Void visitResolvedFieldPtn(ResolvedFieldPtn kernel, FormatterState state) throws Exception {
        kernel.feature.accept(this, state.inline());
        state.write(": ");
        kernel.value.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitResolvedIdentPtn(ResolvedIdentPtn kernel, FormatterState state) throws Exception {
        kernel.ident.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitResolvedRecPtn(ResolvedRecPtn kernel, FormatterState state) throws Exception {
        kernel.label.accept(this, state.inline());
        state.write("#{");
        for (int i = 0; i < kernel.fieldCount(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            ResolvedFieldPtn fp = kernel.fields.get(i);
            fp.accept(this, state.inline());
            if (i + 1 == kernel.fieldCount() && kernel.partialArity) {
                state.write(", ...");
            }
        }
        state.write('}');
        return null;
    }

    @Override
    public final Void visitSelectAndApplyStmt(SelectAndApplyStmt stmt, FormatterState state) throws Exception {
        state.write($SELECT_APPLY);
        state.write('(');
        stmt.rec.accept(this, state.inline());
        state.write(", [");
        for (int i = 0; i < stmt.path.size(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            FeatureOrIdent f = stmt.path.get(i);
            f.accept(this, state.inline());
        }
        state.write(']');
        if (!stmt.args.isEmpty()) {
            state.write(", ");
            for (int i = 0; i < stmt.args.size(); i++) {
                if (i > 0) {
                    state.write(',');
                    state.write(FormatterState.SPACE);
                }
                CompleteOrIdent y = stmt.args.get(i);
                y.accept(this, state.inline());
            }
        }
        state.write(')');
        return null;
    }

    @Override
    public final Void visitSelectStmt(SelectStmt stmt, FormatterState state) throws Exception {
        state.write($SELECT);
        state.write('(');
        stmt.rec.accept(this, state.inline());
        state.write(", ");
        stmt.feature.accept(this, state.inline());
        state.write(", ");
        stmt.target.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitSeqStmt(SeqStmt stmt, FormatterState state) throws Exception {
        for (StmtList.Entry current = stmt.seq.firstEntry(); current != null; current = current.next()) {
            if (current.prev() != null) {
                state.writeNewLineAndIndent();
            }
            current.stmt().accept(this, state);
        }
        return null;
    }

    @Override
    public final Void visitSetCellValueStmt(SetCellValueStmt stmt, FormatterState state) throws Exception {
        state.write($SET);
        state.write('(');
        stmt.cell.accept(this, state.inline());
        state.write(", ");
        stmt.value.accept(this, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitSkipStmt(SkipStmt stmt, FormatterState state) throws Exception {
        state.write("skip");
        return null;
    }

    @Override
    public Void visitStack(Stack kernel, FormatterState state) throws Exception {
        for (Stack s = kernel; s != null; s = s.next) {
            state.write(Kernel.toSystemString(s.stmt));
            if (s.next != null) {
                state.writeNewLineAndIndent();
            }
        }
        return null;
    }

    @Override
    public final Void visitStr(Str kernel, FormatterState state) throws Exception {
        state.write(Str.quote(kernel.value, '\''));
        return null;
    }

    @Override
    public final Void visitSubtractStmt(SubtractStmt stmt, FormatterState state) throws Exception {
        formatBinaryStmt($SUB, stmt.a, stmt.b, stmt.x, state);
        return null;
    }

    @Override
    public final Void visitThrowStmt(ThrowStmt stmt, FormatterState state) throws Exception {
        state.write("throw ");
        stmt.error.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitToken(Token kernel, FormatterState state) throws Exception {
        state.write(kernel.formatValue());
        return null;
    }

    @Override
    public final Void visitTryStmt(TryStmt stmt, FormatterState state) throws Exception {
        // try
        //     ....
        //     ....
        state.write("try");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        stmt.body.accept(this, nextLevelState);
        // catch <ident>
        //     ....
        //     ....
        // end
        state.writeNewLineAndIndent();
        stmt.catchStmt.accept(this, state);
        return null;
    }

    private void visitTuple(Tuple tuple, IdentityHashMap<Rec, Object> memos, FormatterState state) throws Exception {
        if (memos == null) {
            memos = new IdentityHashMap<>();
        }
        memos.put(tuple, Value.PRESENT);
        if (tuple.label() == Nothing.SINGLETON) {
            state.write('[');
        } else {
            tuple.label().accept(this, state.inline());
            state.write("#[");
        }
        // Force the record to resolve available values
        tuple.checkDetermined();
        for (int i = 0; i < tuple.fieldCount(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            visitRecValue(tuple.valueAt(i).resolveValueOrVar(), memos, state);
        }
        state.write(']');
    }

    @Override
    public Void visitTupleDef(TupleDef kernel, FormatterState state) throws Exception {
        if (kernel.label.equals(Rec.DEFAULT_LABEL)) {
            state.write('[');
        } else {
            kernel.label.accept(this, state.inline());
            state.write("#[");
        }
        for (int i = 0; i < kernel.valueCount(); i++) {
            if (i > 0) {
                state.write(", ");
            }
            ValueDef vd = kernel.valueDefAtIndex(i);
            vd.value.accept(this, state.inline());
        }
        state.write(']');
        return null;
    }

    @Override
    public final Void visitValueDef(ValueDef kernel, FormatterState state) {
        throw new NeedsImpl();
    }

    @Override
    public final Void visitVar(Var var, FormatterState state) throws Exception {
        state.write(var.formatValue());
        return null;
    }

    @Override
    public final Void visitVarSet(VarSet varSet, FormatterState state) throws Exception {
        state.write(varSet.formatValue());
        return null;
    }

}
