/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Flt32;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.util.FormatterState;
import org.torqlang.core.util.NeedsImpl;

import java.io.StringWriter;
import java.util.List;

public final class LangFormatter implements LangVisitor<FormatterState, Void> {

    public static final LangFormatter SINGLETON = new LangFormatter();

    public final String format(Lang lang) {
        try (StringWriter sw = new StringWriter()) {
            FormatterState state = new FormatterState(sw);
            lang.accept(this, state);
            state.flush();
            return sw.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void formatBinaryExpr(String oper, SntcOrExpr arg1, SntcOrExpr arg2, FormatterState state) throws Exception {
        arg1.accept(this, state.inline());
        state.write(FormatterState.SPACE);
        state.write(oper);
        state.write(FormatterState.SPACE);
        arg2.accept(this, state.inline());
    }

    @Override
    public final Void visitActExpr(ActExpr lang, FormatterState state) throws Exception {
        state.write("act");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.seq.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitActorExpr(ActorExpr lang, FormatterState state) throws Exception {
        state.write("actor ");
        visitActorLang(lang, state);
        return null;
    }

    private void visitActorLang(ActorLang lang, FormatterState state) throws Exception {
        state.write('(');
        visitFormalArgs(lang.formalArgs, state.inline());
        state.write(") in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        visitSeqList(lang.body, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
    }

    @Override
    public final Void visitActorSntc(ActorSntc lang, FormatterState state) throws Exception {
        state.write("actor ");
        state.write(lang.name.formatValue());
        visitActorLang(lang, state);
        return null;
    }

    private void visitActualArgs(List<SntcOrExpr> list, FormatterState state) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            SntcOrExpr next = list.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
        }
    }

    @Override
    public final Void visitAndExpr(AndExpr lang, FormatterState state) throws Exception {
        formatBinaryExpr(SymbolsAndKeywords.AND_OPER, lang.arg1, lang.arg2, state);
        return null;
    }

    @Override
    public final Void visitApplyLang(ApplyLang lang, FormatterState state) throws Exception {
        lang.proc.accept(this, state.inline());
        state.write('(');
        visitActualArgs(lang.args, state);
        state.write(')');
        return null;
    }

    @Override
    public final Void visitAskSntc(AskSntc lang, FormatterState state) throws Exception {
        state.write("handle ask ");
        lang.pat.accept(this, state.inline());
        if (lang.responseType != null) {
            state.write(" -> ");
            lang.responseType.accept(this, state.inline());
        }
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitBeginLang(BeginLang lang, FormatterState state) throws Exception {
        state.write("begin");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitBoolAsExpr(BoolAsExpr lang, FormatterState state) throws Exception {
        state.write(lang.bool.formatValue());
        return null;
    }

    @Override
    public final Void visitBoolAsPat(BoolAsPat lang, FormatterState state) throws Exception {
        state.write(lang.bool.formatValue());
        return null;
    }

    @Override
    public final Void visitBreakSntc(BreakSntc lang, FormatterState state) throws Exception {
        state.write("break");
        return null;
    }

    @Override
    public final Void visitCaseClause(CaseClause lang, FormatterState state) throws Exception {
        state.write("of ");
        lang.pat.accept(this, state.inline());
        if (lang.guard != null) {
            state.write(" when ");
            lang.guard.accept(this, state.inline());
        }
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        return null;
    }

    @Override
    public final Void visitCaseLang(CaseLang lang, FormatterState state) throws Exception {
        state.write("case ");
        lang.arg.accept(this, state.inline());
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.caseClause.accept(this, nextLevelState);
        for (CaseClause altCaseClause : lang.altCaseClauses) {
            nextLevelState.writeNewLineAndIndent();
            altCaseClause.accept(this, nextLevelState);
        }
        if (lang.elseSeq != null) {
            nextLevelState.writeAfterNewLineAndIdent("else");
            FormatterState thirdLevelState = nextLevelState.nextLevel();
            thirdLevelState.writeNewLineAndIndent();
            lang.elseSeq.accept(this, thirdLevelState);
        }
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitCatchClause(CatchClause lang, FormatterState state) throws Exception {
        state.write("catch ");
        lang.pat.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        return null;
    }

    @Override
    public final Void visitCharAsExpr(CharAsExpr lang, FormatterState state) throws Exception {
        state.write('&');
        state.write(lang.charNum().formatValue());
        return null;
    }

    @Override
    public final Void visitContinueSntc(ContinueSntc lang, FormatterState state) throws Exception {
        state.write("continue");
        return null;
    }

    @Override
    public final Void visitDec128AsExpr(Dec128AsExpr lang, FormatterState state) throws Exception {
        state.write(lang.dec128().formatValue());
        state.write('m');
        return null;
    }

    @Override
    public final Void visitDotSelectExpr(DotSelectExpr lang, FormatterState state) throws Exception {
        lang.recExpr.accept(this, state.inline());
        state.write('.');
        lang.featureExpr.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitEofAsExpr(EofAsExpr lang, FormatterState state) throws Exception {
        state.write(lang.value().formatValue());
        return null;
    }

    @Override
    public final Void visitEofAsPat(EofAsPat lang, FormatterState state) throws Exception {
        state.write(lang.value().formatValue());
        return null;
    }

    @Override
    public final Void visitFieldExpr(FieldExpr lang, FormatterState state) throws Exception {
        lang.feature.accept(this, state);
        state.write(": ");
        lang.value.accept(this, state);
        return null;
    }

    @Override
    public final Void visitFieldPat(FieldPat lang, FormatterState state) throws Exception {
        lang.feature.accept(this, state);
        state.write(": ");
        lang.value.accept(this, state);
        return null;
    }

    @Override
    public final Void visitFltAsExpr(FltAsExpr lang, FormatterState state) throws Exception {
        state.write(lang.flt64().formatValue());
        if (lang.flt64() instanceof Flt32) {
            state.write('f');
        }
        return null;
    }

    @Override
    public final Void visitForSntc(ForSntc lang, FormatterState state) throws Exception {
        state.write("for ");
        lang.pat.accept(this, state.inline());
        state.write(" in ");
        lang.iter.accept(this, state.inline());
        state.write(" do");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    private void visitFormalArgs(List<Pat> formalArgs, FormatterState state) throws Exception {
        for (int i = 0; i < formalArgs.size(); i++) {
            Pat next = formalArgs.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
        }
    }

    @Override
    public final Void visitFuncExpr(FuncExpr lang, FormatterState state) throws Exception {
        state.write("func ");
        visitFuncLang(lang, state);
        return null;
    }

    private void visitFuncLang(FuncLang lang, FormatterState state) throws Exception {
        state.write('(');
        visitFormalArgs(lang.formalArgs, state.inline());
        state.write(')');
        if (lang.returnType != null) {
            state.write(" -> ");
            lang.returnType.accept(this, state.inline());
        }
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        visitSeqList(lang.body.list, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
    }

    @Override
    public final Void visitFuncSntc(FuncSntc lang, FormatterState state) throws Exception {
        state.write("func ");
        state.write(lang.name().name);
        visitFuncLang(lang, state);
        return null;
    }

    @Override
    public final Void visitGroupExpr(GroupExpr lang, FormatterState state) throws Exception {
        state.write('(');
        lang.expr.accept(this, state);
        state.write(')');
        return null;
    }

    private void visitIdent(Ident ident, FormatterState state) throws Exception {
        if (Ident.isSimpleName(ident.name)) {
            state.write(ident.name);
        } else {
            state.write(Ident.quote(ident.name));
        }
    }

    @Override
    public final Void visitIdentAsExpr(IdentAsExpr lang, FormatterState state) throws Exception {
        visitIdent(lang.ident, state);
        return null;
    }

    @Override
    public final Void visitIdentAsPat(IdentAsPat lang, FormatterState state) throws Exception {
        if (lang.escaped) {
            state.write('~');
        }
        visitIdent(lang.ident, state);
        if (lang.typeAnno != null) {
            state.write(SymbolsAndKeywords.TYPE_OPER);
            lang.typeAnno.accept(this, state.inline());
        }
        return null;
    }

    @Override
    public final Void visitIdentVarDecl(IdentVarDecl lang, FormatterState state) throws Exception {
        lang.identAsPat.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitIfClause(IfClause lang, FormatterState state) throws Exception {
        lang.condition.accept(this, state.inline());
        state.write(" then");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        return null;
    }

    @Override
    public final Void visitIfLang(IfLang lang, FormatterState state) throws Exception {
        state.write("if ");
        lang.ifClause.accept(this, state);
        for (IfClause altIfClause : lang.altIfClauses) {
            state.writeAfterNewLineAndIdent("elseif ");
            altIfClause.accept(this, state);
        }
        if (lang.elseSeq != null) {
            state.writeAfterNewLineAndIdent("else");
            FormatterState nextLevelState = state.nextLevel();
            nextLevelState.writeNewLineAndIndent();
            lang.elseSeq.accept(this, nextLevelState);
        }
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitImportSntc(ImportSntc lang, FormatterState state) throws Exception {
        Str q = lang.qualifier;
        List<ImportName> ins = lang.names;
        state.write("import ");
        state.write(q.value);
        if (ins.size() == 1 && ins.get(0).alias == null) {
            if (!q.value.isEmpty()) {
                state.write('.');
            }
            state.write(ins.get(0).name.value);
        } else {
            state.write('[');
            for (int i = 0; i < ins.size(); i++) {
                ImportName in = ins.get(i);
                if (i > 0) {
                    state.write(", ");
                }
                state.write(in.name.value);
                if (in.alias != null) {
                    state.write(" as ");
                    state.write(in.alias.value);
                }
            }
            state.write(']');
        }
        return null;
    }

    @Override
    public final Void visitIndexSelectExpr(IndexSelectExpr lang, FormatterState state) throws Exception {
        lang.recExpr.accept(this, state.inline());
        state.write('[');
        lang.featureExpr.accept(this, state.inline());
        state.write(']');
        return null;
    }

    @Override
    public final Void visitInitVarDecl(InitVarDecl lang, FormatterState state) throws Exception {
        lang.varPat.accept(this, state.inline());
        state.write(" = ");
        lang.valueExpr.accept(this, state.inline());
        return null;
    }

    @Override
    public final Void visitIntAsExpr(IntAsExpr lang, FormatterState state) throws Exception {
        state.write(lang.int64().formatValue());
        if (!(lang.int64() instanceof Int32)) {
            state.write('L');
        }
        return null;
    }

    @Override
    public final Void visitIntAsPat(IntAsPat lang, FormatterState state) throws Exception {
        state.write(lang.int64().formatValue());
        if (!(lang.int64() instanceof Int32)) {
            state.write('L');
        }
        return null;
    }

    @Override
    public final Void visitLocalLang(LocalLang lang, FormatterState state) throws Exception {
        state.write("local ");
        visitVarDecls(lang.varDecls, state);
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitNothingAsExpr(NothingAsExpr lang, FormatterState state) throws Exception {
        state.write(lang.value().formatValue());
        return null;
    }

    @Override
    public final Void visitNothingAsPat(NothingAsPat lang, FormatterState state) throws Exception {
        state.write(lang.value().formatValue());
        return null;
    }

    @Override
    public final Void visitOrExpr(OrExpr lang, FormatterState state) throws Exception {
        formatBinaryExpr(SymbolsAndKeywords.OR_OPER, lang.arg1, lang.arg2, state);
        return null;
    }

    @Override
    public final Void visitProcExpr(ProcExpr lang, FormatterState state) throws Exception {
        state.write("proc ");
        visitProcLang(lang, state);
        return null;
    }

    private void visitProcLang(ProcLang lang, FormatterState state) throws Exception {
        state.write('(');
        visitFormalArgs(lang.formalArgs, state.inline());
        state.write(") in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        visitSeqList(lang.body.list, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
    }

    @Override
    public final Void visitProcSntc(ProcSntc lang, FormatterState state) throws Exception {
        state.write("proc ");
        state.write(lang.name().name);
        visitProcLang(lang, state);
        return null;
    }

    @Override
    public final Void visitProductExpr(ProductExpr lang, FormatterState state) throws Exception {
        formatBinaryExpr(lang.oper.symbol(), lang.arg1, lang.arg2, state);
        return null;
    }

    @Override
    public final Void visitRecExpr(RecExpr lang, FormatterState state) throws Exception {
        if (lang.label() != null) {
            lang.label().accept(this, state.inline());
            state.write('#');
        }
        state.write('{');
        List<FieldExpr> list = lang.fields();
        for (int i = 0; i < list.size(); i++) {
            FieldExpr next = list.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
        }
        state.write('}');
        return null;
    }

    @Override
    public final Void visitRecPat(RecPat lang, FormatterState state) throws Exception {
        if (lang.label() != null) {
            lang.label().accept(this, state.inline());
            state.write('#');
        }
        state.write('{');
        List<FieldPat> list = lang.fields();
        for (int i = 0; i < list.size(); i++) {
            FieldPat next = list.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
            if (lang.partialArity() && i + 1 == list.size()) {
                state.write(", ...");
            }
        }
        state.write('}');
        return null;
    }

    @Override
    public final Void visitRelationalExpr(RelationalExpr lang, FormatterState state) throws Exception {
        formatBinaryExpr(lang.oper.symbol(), lang.arg1, lang.arg2, state);
        return null;
    }

    @Override
    public final Void visitRespondSntc(RespondSntc lang, FormatterState state) {
        throw new NeedsImpl();
    }

    @Override
    public final Void visitReturnSntc(ReturnSntc lang, FormatterState state) throws Exception {
        state.write("return");
        if (lang.value != null) {
            state.write(' ');
            lang.value.accept(this, state.inline());
        }
        return null;
    }

    @Override
    public final Void visitSelectAndApplyLang(SelectAndApplyLang lang, FormatterState state) throws Exception {
        lang.selectExpr.accept(this, state);
        state.write('(');
        visitActualArgs(lang.args, state);
        state.write(')');
        return null;
    }

    @Override
    public final Void visitSeqLang(SeqLang lang, FormatterState state) throws Exception {
        visitSeqList(lang.list, state);
        return null;
    }

    private void visitSeqList(List<SntcOrExpr> list, FormatterState state) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            SntcOrExpr next = list.get(i);
            if (i > 0) {
                if (state.level() == FormatterState.INLINE_VALUE) {
                    SntcOrExpr prev = list.get(i - 1);
                    if (prev instanceof GroupExpr || (next instanceof UnaryExpr unaryExpr && unaryExpr.oper == UnaryOper.NEGATE)) {
                        state.write(';');
                    }
                }
                state.writeNewLineAndIndent();
            }
            next.accept(this, state);
        }
    }

    @Override
    public final Void visitSetCellValueSntc(SetCellValueSntc lang, FormatterState state) throws Exception {
        lang.leftSide.accept(this, state);
        state.write(" := ");
        lang.rightSide.accept(this, state);
        return null;
    }

    @Override
    public final Void visitSkipSntc(SkipSntc lang, FormatterState state) throws Exception {
        state.write("skip");
        return null;
    }

    @Override
    public final Void visitSpawnExpr(SpawnExpr lang, FormatterState state) throws Exception {
        state.write("spawn(");
        visitActualArgs(lang.args, state.inline());
        state.write(')');
        return null;
    }

    @Override
    public final Void visitStrAsExpr(StrAsExpr lang, FormatterState state) throws Exception {
        state.write(Str.quote(lang.str.value, '\''));
        return null;
    }

    @Override
    public final Void visitStrAsPat(StrAsPat lang, FormatterState state) throws Exception {
        state.write(Str.quote(lang.str.value, '\''));
        return null;
    }

    @Override
    public final Void visitSumExpr(SumExpr lang, FormatterState state) throws Exception {
        formatBinaryExpr(lang.oper.symbol(), lang.arg1, lang.arg2, state);
        return null;
    }

    @Override
    public final Void visitTellSntc(TellSntc lang, FormatterState state) throws Exception {
        state.write("handle tell ");
        lang.pat.accept(this, state.inline());
        state.write(" in");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitThrowLang(ThrowLang lang, FormatterState state) throws Exception {
        state.write("throw ");
        lang.arg.accept(this, state.nextLevel());
        return null;
    }

    @Override
    public final Void visitTryLang(TryLang lang, FormatterState state) throws Exception {
        state.write("try");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        for (CatchClause catchClause : lang.catchClauses) {
            state.writeNewLineAndIndent();
            catchClause.accept(this, state);
        }
        if (lang.finallySeq != null) {
            state.writeAfterNewLineAndIdent("finally");
            nextLevelState.writeNewLineAndIndent();
            lang.finallySeq.accept(this, nextLevelState);
        }
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

    @Override
    public final Void visitTupleExpr(TupleExpr lang, FormatterState state) throws Exception {
        if (lang.label() != null) {
            lang.label().accept(this, state.inline());
            state.write('#');
        }
        state.write("[");
        List<SntcOrExpr> list = lang.values();
        for (int i = 0; i < list.size(); i++) {
            SntcOrExpr next = list.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
        }
        state.write(']');
        return null;
    }

    @Override
    public final Void visitTuplePat(TuplePat lang, FormatterState state) throws Exception {
        if (lang.label() != null) {
            lang.label().accept(this, state.inline());
            state.write('#');
        }
        state.write("[");
        List<Pat> list = lang.values();
        for (int i = 0; i < list.size(); i++) {
            Pat next = list.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
            if (lang.partialArity() && i + 1 == list.size()) {
                state.write(", ...");
            }
        }
        state.write(']');
        return null;
    }

    @Override
    public final Void visitTypeAnno(TypeAnno lang, FormatterState state) throws Exception {
        visitIdent(lang.ident, state);
        return null;
    }

    @Override
    public final Void visitUnaryExpr(UnaryExpr lang, FormatterState state) throws Exception {
        state.write(lang.oper.symbol());
        lang.arg.accept(this, state);
        return null;
    }

    @Override
    public final Void visitUnifySntc(UnifySntc lang, FormatterState state) throws Exception {
        lang.leftSide.accept(this, state.inline());
        state.write(" = ");
        lang.rightSide.accept(this, state);
        return null;
    }

    private void visitVarDecls(List<VarDecl> varDecls, FormatterState state) throws Exception {
        for (int i = 0; i < varDecls.size(); i++) {
            VarDecl next = varDecls.get(i);
            if (i > 0) {
                state.write(", ");
            }
            next.accept(this, state.inline());
        }
    }

    @Override
    public final Void visitVarSntc(VarSntc lang, FormatterState state) throws Exception {
        state.write("var ");
        visitVarDecls(lang.varDecls, state);
        return null;
    }

    @Override
    public final Void visitWhileSntc(WhileSntc lang, FormatterState state) throws Exception {
        state.write("while ");
        lang.cond.accept(this, state.inline());
        state.write(" do");
        FormatterState nextLevelState = state.nextLevel();
        nextLevelState.writeNewLineAndIndent();
        lang.body.accept(this, nextLevelState);
        state.writeAfterNewLineAndIdent("end");
        return null;
    }

}
