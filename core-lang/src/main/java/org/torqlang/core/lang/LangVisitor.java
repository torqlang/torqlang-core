/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

public interface LangVisitor<T, R> {

    R visitActExpr(ActExpr lang, T state) throws Exception;

    R visitActorExpr(ActorExpr lang, T state) throws Exception;

    R visitActorSntc(ActorSntc lang, T state) throws Exception;

    R visitAndExpr(AndExpr lang, T state) throws Exception;

    R visitApplyLang(ApplyLang lang, T state) throws Exception;

    R visitAskSntc(AskSntc lang, T state) throws Exception;

    R visitBeginLang(BeginLang lang, T state) throws Exception;

    R visitBoolAsExpr(BoolAsExpr lang, T state) throws Exception;

    R visitBoolAsPat(BoolAsPat lang, T state) throws Exception;

    R visitBreakSntc(BreakSntc lang, T state) throws Exception;

    R visitCaseClause(CaseClause lang, T state) throws Exception;

    R visitCaseLang(CaseLang lang, T state) throws Exception;

    R visitCatchClause(CatchClause lang, T state) throws Exception;

    R visitCharAsExpr(CharAsExpr lang, T state) throws Exception;

    R visitContinueSntc(ContinueSntc lang, T state) throws Exception;

    R visitDec128AsExpr(Dec128AsExpr lang, T state) throws Exception;

    R visitDotSelectExpr(DotSelectExpr lang, T state) throws Exception;

    R visitEofAsExpr(EofAsExpr lang, T state) throws Exception;

    R visitEofAsPat(EofAsPat lang, T state) throws Exception;

    R visitFieldExpr(FieldExpr lang, T state) throws Exception;

    R visitFieldPat(FieldPat lang, T state) throws Exception;

    R visitFltAsExpr(FltAsExpr lang, T state) throws Exception;

    R visitForSntc(ForSntc lang, T state) throws Exception;

    R visitFuncExpr(FuncExpr lang, T state) throws Exception;

    R visitFuncSntc(FuncSntc lang, T state) throws Exception;

    R visitGroupExpr(GroupExpr lang, T state) throws Exception;

    R visitIdentAsExpr(IdentAsExpr lang, T state) throws Exception;

    R visitIdentAsPat(IdentAsPat lang, T state) throws Exception;

    R visitIdentVarDecl(IdentVarDecl lang, T state) throws Exception;

    R visitIfClause(IfClause lang, T state) throws Exception;

    R visitIfLang(IfLang lang, T state) throws Exception;

    R visitImportSntc(ImportSntc lang, T state) throws Exception;

    R visitIndexSelectExpr(IndexSelectExpr lang, T state) throws Exception;

    R visitInitVarDecl(InitVarDecl lang, T state) throws Exception;

    R visitIntAsExpr(IntAsExpr lang, T state) throws Exception;

    R visitIntAsPat(IntAsPat lang, T state) throws Exception;

    R visitLocalLang(LocalLang lang, T state) throws Exception;

    R visitNothingAsExpr(NothingAsExpr lang, T state) throws Exception;

    R visitNothingAsPat(NothingAsPat lang, T state) throws Exception;

    R visitOrExpr(OrExpr lang, T state) throws Exception;

    R visitProcExpr(ProcExpr lang, T state) throws Exception;

    R visitProcSntc(ProcSntc lang, T state) throws Exception;

    R visitProductExpr(ProductExpr lang, T state) throws Exception;

    R visitRecExpr(RecExpr lang, T state) throws Exception;

    R visitRecPat(RecPat lang, T state) throws Exception;

    R visitRelationalExpr(RelationalExpr lang, T state) throws Exception;

    R visitRespondSntc(RespondSntc lang, T state) throws Exception;

    R visitReturnSntc(ReturnSntc lang, T state) throws Exception;

    R visitSelectAndApplyLang(SelectAndApplyLang lang, T state) throws Exception;

    R visitSeqLang(SeqLang lang, T state) throws Exception;

    R visitSetCellValueSntc(SetCellValueSntc lang, T state) throws Exception;

    R visitSkipSntc(SkipSntc lang, T state) throws Exception;

    R visitSpawnExpr(SpawnExpr lang, T state) throws Exception;

    R visitStrAsExpr(StrAsExpr lang, T state) throws Exception;

    R visitStrAsPat(StrAsPat lang, T state) throws Exception;

    R visitSumExpr(SumExpr lang, T state) throws Exception;

    R visitTellSntc(TellSntc lang, T state) throws Exception;

    R visitThrowLang(ThrowLang lang, T state) throws Exception;

    R visitTryLang(TryLang lang, T state) throws Exception;

    R visitTupleExpr(TupleExpr lang, T state) throws Exception;

    R visitTuplePat(TuplePat lang, T state) throws Exception;

    R visitTypeAnno(TypeAnno lang, T state) throws Exception;

    R visitUnaryExpr(UnaryExpr lang, T state) throws Exception;

    R visitUnifySntc(UnifySntc lang, T state) throws Exception;

    R visitVarSntc(VarSntc lang, T state) throws Exception;

    R visitWhileSntc(WhileSntc lang, T state) throws Exception;
}
