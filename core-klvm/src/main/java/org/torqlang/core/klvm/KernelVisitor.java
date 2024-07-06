/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface KernelVisitor<T, R> {

    R visitActStmt(ActStmt kernel, T state) throws Exception;

    R visitActorCfg(ActorCfg kernel, T state) throws Exception;

    R visitActorCfgtr(ActorCfgtr kernel, T state) throws Exception;

    R visitAddStmt(AddStmt kernel, T state) throws Exception;

    R visitApplyProcStmt(ApplyStmt kernel, T state) throws Exception;

    R visitBindCompleteToCompleteStmt(BindCompleteToCompleteStmt kernel, T state) throws Exception;

    R visitBindCompleteToIdentStmt(BindCompleteToIdentStmt kernel, T state) throws Exception;

    R visitBindCompleteToValueOrVarStmt(BindCompleteToValueOrVarStmt kernel, T state) throws Exception;

    R visitBindIdentToIdentStmt(BindIdentToIdentStmt kernel, T state) throws Exception;

    R visitBool(Bool kernel, T state) throws Exception;

    R visitCaseElseStmt(CaseElseStmt kernel, T state) throws Exception;

    R visitCaseStmt(CaseStmt kernel, T state) throws Exception;

    R visitCatchStmt(CatchStmt kernel, T state) throws Exception;

    R visitChar(Char kernel, T state) throws Exception;

    R visitClosure(Closure kernel, T state) throws Exception;

    R visitCreateActorCfgtrStmt(CreateActorCfgtrStmt kernel, T state) throws Exception;

    R visitCreateProcStmt(CreateProcStmt kernel, T state) throws Exception;

    R visitCreateRecStmt(CreateRecStmt kernel, T state) throws Exception;

    R visitCreateTupleStmt(CreateTupleStmt kernel, T state) throws Exception;

    R visitDec128(Dec128 kernel, T state) throws Exception;

    R visitDisentailsStmt(DisentailsStmt kernel, T state) throws Exception;

    R visitDivideStmt(DivideStmt kernel, T state) throws Exception;

    R visitEntailsStmt(EntailsStmt kernel, T state) throws Exception;

    R visitEnv(Env kernel, T state) throws Exception;

    R visitEof(Eof kernel, T state) throws Exception;

    R visitFailedValue(FailedValue kernel, T state) throws Exception;

    R visitFieldDef(FieldDef kernel, T state) throws Exception;

    R visitFieldPtn(FieldPtn kernel, T state) throws Exception;

    R visitFlt32(Flt32 kernel, T state) throws Exception;

    R visitFlt64(Flt64 kernel, T state) throws Exception;

    R visitGetCellValueStmt(GetCellValueStmt kernel, T state) throws Exception;

    R visitGreaterThanOrEqualToStmt(GreaterThanOrEqualToStmt kernel, T state) throws Exception;

    R visitGreaterThanStmt(GreaterThanStmt kernel, T state) throws Exception;

    R visitIdent(Ident kernel, T state) throws Exception;

    R visitIdentDef(IdentDef kernel, T state) throws Exception;

    R visitIdentPtn(IdentPtn kernel, T state) throws Exception;

    R visitIfElseStmt(IfElseStmt kernel, T state) throws Exception;

    R visitIfStmt(IfStmt kernel, T state) throws Exception;

    R visitInt32(Int32 kernel, T state) throws Exception;

    R visitInt64(Int64 kernel, T state) throws Exception;

    R visitJumpCatchStmt(JumpCatchStmt kernel, T state) throws Exception;

    R visitJumpThrowStmt(JumpThrowStmt kernel, T state) throws Exception;

    R visitLessThanOrEqualToStmt(LessThanOrEqualToStmt kernel, T state) throws Exception;

    R visitLessThanStmt(LessThanStmt kernel, T state) throws Exception;

    R visitLocalStmt(LocalStmt kernel, T state) throws Exception;

    R visitModuloStmt(ModuloStmt kernel, T state) throws Exception;

    R visitMultiplyStmt(MultiplyStmt kernel, T state) throws Exception;

    R visitNegateStmt(NegateStmt kernel, T state) throws Exception;

    R visitNotStmt(NotStmt kernel, T state) throws Exception;

    R visitNull(Null kernel, T state) throws Exception;

    R visitObj(Obj kernel, T state) throws Exception;

    R visitOpaqueValue(OpaqueValue kernel, T state) throws Exception;

    R visitProc(Proc kernel, T state) throws Exception;

    R visitProcDef(ProcDef kernel, T state) throws Exception;

    R visitRec(Rec kernel, T state) throws Exception;

    R visitRecDef(RecDef kernel, T state) throws Exception;

    R visitRecPtn(RecPtn kernel, T state) throws Exception;

    R visitResolvedFieldPtn(ResolvedFieldPtn kernel, T state) throws Exception;

    R visitResolvedIdentPtn(ResolvedIdentPtn kernel, T state) throws Exception;

    R visitResolvedRecPtn(ResolvedRecPtn kernel, T state) throws Exception;

    R visitSelectAndApplyStmt(SelectAndApplyStmt kernel, T state) throws Exception;

    R visitSelectStmt(SelectStmt kernel, T state) throws Exception;

    R visitSeqStmt(SeqStmt kernel, T state) throws Exception;

    R visitSetCellValueStmt(SetCellValueStmt kernel, T state) throws Exception;

    R visitSkipStmt(SkipStmt kernel, T state) throws Exception;

    R visitStack(Stack kernel, T state) throws Exception;

    R visitStr(Str kernel, T state) throws Exception;

    R visitSubtractStmt(SubtractStmt kernel, T state) throws Exception;

    R visitThrowStmt(ThrowStmt kernel, T state) throws Exception;

    R visitToken(Token kernel, T state) throws Exception;

    R visitTryStmt(TryStmt kernel, T state) throws Exception;

    R visitTupleDef(TupleDef kernel, T state) throws Exception;

    R visitValueDef(ValueDef kernel, T state) throws Exception;

    R visitVar(Var kernel, T state) throws Exception;

    R visitVarSet(VarSet kernel, T state) throws Exception;
}
