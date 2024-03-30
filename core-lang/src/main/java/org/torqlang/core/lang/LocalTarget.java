/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.IdentDef;
import org.torqlang.core.klvm.Stmt;

/*
 * When entering a new method (function or procedure) body:
 *
 * -- We allocate a new `JumpFlags` with `return` as an allowed jump operation
 * -- The same `return` flag is shared as we enter loop bodies
 *
 * When entering a new loop body:
 *
 * -- Allocate a `JumpFlags` with `break` and `continue` allowed, but carry forward the shared `return` flag.
 */
public final class LocalTarget {

    public static final int ALLOWED = -1;
    public static final int NOT_ALLOWED = 0;
    public static final int USED = 1;

    private final LocalTargetType type;
    private final JumpFlags jumpFlags;
    private final LexicalScope scope;

    private Ident offeredIdent;

    private LocalTarget(LocalTargetType type, Ident offeredIdent, JumpFlags jumpFlags, LexicalScope scope) {
        this.type = type;
        this.offeredIdent = offeredIdent;
        this.jumpFlags = jumpFlags;
        this.scope = scope;
    }

    public static LocalTarget createExprTargetForFuncBody(Ident offeredIdent) {
        return new LocalTarget(LocalTargetType.EXPR, offeredIdent, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(ALLOWED)), new LexicalScope());
    }

    public static LocalTarget createExprTargetForRoot(Ident exprIdent) {
        return new LocalTarget(LocalTargetType.EXPR, exprIdent, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(NOT_ALLOWED)), new LexicalScope());
    }

    public static LocalTarget createSntcTargetForFinally() {
        return new LocalTarget(LocalTargetType.SNTC, null, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(NOT_ALLOWED)), new LexicalScope());
    }

    public static LocalTarget createSntcTargetForProcBody() {
        return new LocalTarget(LocalTargetType.SNTC, null, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(ALLOWED)), new LexicalScope());
    }

    public static LocalTarget createSntcTargetForRoot() {
        return new LocalTarget(LocalTargetType.SNTC, null, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(NOT_ALLOWED)), new LexicalScope());
    }

    public void acceptOfferedIdent() {
        this.offeredIdent = null;
    }

    public final void addIdentDef(IdentDef identDef) {
        scope.addIdentDef(identDef);
    }

    public final void addStmt(Stmt stmt) {
        scope.addStmt(stmt);
    }

    public final LocalTarget asAskTargetWithNewScope(Ident exprIdent) {
        if (exprIdent == null) {
            throw new NullPointerException("exprIdent");
        }
        return new LocalTarget(LocalTargetType.EXPR, exprIdent, new JumpFlags(NOT_ALLOWED, NOT_ALLOWED,
            new SharedFlag(ALLOWED)), new LexicalScope());
    }

    /*
     * expr | null-ident | same-flags | new-scope
     *
     * This method is used by binary and unary expressions to create intermediate results that bind to new synthetic
     * identifiers. Subsequently, the intermediate identifiers are used as arguments in binary or unary expressions.
     *   - visitAndExpr
     *   - visitOrExpr
     *   - visitProductExpr
     *   - visitRelationalExpr
     *   - visitSumExpr
     *   - visitUnaryExpr
     */
    public LocalTarget asExprTargetWithNewScope() {
        return new LocalTarget(LocalTargetType.EXPR, null, jumpFlags, new LexicalScope());
    }

    /*
     * expr | ident | same-flags | new-scope
     *
     * This method is used by Lang statements that may be an Expr or Sntc:
     *   - buildCaseStmtRecursively: used when CaseLang is actually a CaseExpr
     *   - visitAndExpr: used to build right-side of `&&` expression
     *   - visitIfLangRecursively: used when IfLang is actually an IfExpr
     *   - visitLocalLang: used when LocalLang is actually a LocalExpr
     *   - visitMatchClauseRecursively: used when match case is an Expr
     *   - visitOrExpr: used to build right-side of `||` expression
     *   - visitTryLang: used when TryLang is actually a TryExpr
     *   - visitWhileSntc: used to produce an intermediate guard boolean
     */
    public final LocalTarget asExprTargetWithNewScope(Ident exprIdent) {
        if (exprIdent == null) {
            throw new NullPointerException("exprIdent");
        }
        return new LocalTarget(LocalTargetType.EXPR, exprIdent, jumpFlags, new LexicalScope());
    }

    public LocalTarget asExprTargetWithSameScope() {
        return new LocalTarget(LocalTargetType.EXPR, null, jumpFlags, scope);
    }

    /*
     * expr | pass-ident | same-flags | same-scope
     *
     *  This method is used by sentences to bind intermediate expressions to special identifiers:
     *   - visitForSntc: used to generate and bind an iterator at $Iter
     *   - visitInitVarDecl: used to generate initialization for a newly declared identifier
     *   - visitReturnSntc: used to generate and bind a return value at $r
     *   - visitUnifySntc: used to generate and bind a right-side expression to left-side identifier
     */
    public final LocalTarget asExprTargetWithSameScope(Ident exprIdent) {
        if (exprIdent == null) {
            throw new NullPointerException("exprIdent");
        }
        return new LocalTarget(LocalTargetType.EXPR, exprIdent, jumpFlags, scope);
    }

    /*
     * sntc | null-ident | (ALLOWED, ALLOWED, same) | new-scope
     *
     * This method is used by sentences to structure loop bodies with jump flags:
     *   - visitForSntc: used to allow `break` and `continue` sentences
     *   - visitWhileSntc: used to allow `break` and `continue` sentences
     */
    public final LocalTarget asSntcTargetForLoopBodyWithNewScope() {
        return new LocalTarget(LocalTargetType.SNTC, null,
            new JumpFlags(ALLOWED, ALLOWED, jumpFlags.returnFlag), new LexicalScope());
    }

    /*
     * sntc | null-ident | same-flags | new-scope
     *
     * This method is used by sentences to create new lexical scopes:
     *   - buildCaseStmtRecursively: used when CaseLang is actually a CaseSntc
     *   - visitForSntc
     *   - buildIfLangRecursively: used when IfLang is actually an IfSntc
     *   - visitLocalLang: used when LocalLang is actually a LocalSntc
     *   - visitMatchClauseRecursively: used when match case is a Sntc
     *   - visitSelectAndApplyLang: used to create intermediate results along "select path"
     *   - visitSetCellValueSntc
     *   - visitTryLang: used when TryLang is actually a TrySntc
     *   - visitWhileSntc
     */
    public final LocalTarget asSntcTargetWithNewScope() {
        return new LocalTarget(LocalTargetType.SNTC, null, jumpFlags, new LexicalScope());
    }

    /*
     * sntc | null-ident | same-flags | same-scope
     *
     * This method is used by `visitBodyList` while visiting all but the last entry of the list.
     */
    public final LocalTarget asSntcTargetWithSameScope() {
        return new LocalTarget(LocalTargetType.SNTC, null, jumpFlags, scope);
    }

    public final int breakFlag() {
        return jumpFlags.breakFlag;
    }

    public final Stmt build() {
        return scope.build();
    }

    public final int continueFlag() {
        return jumpFlags.continueFlag;
    }

    public final boolean isBreakAllowed() {
        return jumpFlags.breakFlag == ALLOWED || jumpFlags.breakFlag == USED;
    }

    public final boolean isBreakUsed() {
        return jumpFlags.breakFlag == USED;
    }

    public final boolean isContinueAllowed() {
        return jumpFlags.continueFlag == ALLOWED || jumpFlags.continueFlag == USED;
    }

    public final boolean isContinueUsed() {
        return jumpFlags.continueFlag == USED;
    }

    public final boolean isExprTarget() {
        return type == LocalTargetType.EXPR;
    }

    public final boolean isReturnAllowed() {
        return jumpFlags.returnFlag.value == ALLOWED || jumpFlags.returnFlag.value == USED;
    }

    public final boolean isReturnUsed() {
        return jumpFlags.returnFlag.value == USED;
    }

    public final boolean isSntcTarget() {
        return type == LocalTargetType.SNTC;
    }

    public final Ident offeredIdent() {
        return offeredIdent;
    }

    public final int returnFlag() {
        return jumpFlags.returnFlag.value;
    }

    public final LexicalScope scope() {
        return scope;
    }

    public final void setBreakUsed() {
        jumpFlags.breakFlag = Math.abs(jumpFlags.breakFlag);
    }

    public final void setContinueUsed() {
        jumpFlags.continueFlag = Math.abs(jumpFlags.continueFlag);
    }

    public final void setReturnUsed() {
        jumpFlags.returnFlag.value = Math.abs(jumpFlags.returnFlag.value);
    }

    private static enum LocalTargetType {
        EXPR,
        SNTC
    }

    /*
     *  0 = Operation not allowed
     * -1 = Operation is allowed but not used
     *  1 = Operation is allowed and used
     */
    private static class JumpFlags {
        private final SharedFlag returnFlag;
        private int breakFlag;
        private int continueFlag;

        private JumpFlags(int breakFlag, int continueFlag, SharedFlag returnFlag) {
            this.breakFlag = breakFlag;
            this.continueFlag = continueFlag;
            this.returnFlag = returnFlag;
        }
    }

    private static class SharedFlag {
        private int value;

        private SharedFlag(int value) {
            this.value = value;
        }
    }

}
