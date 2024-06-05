/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;
import org.torqlang.core.util.NeedsImpl;
import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

/*
 * Generator transforms sentences and expression into kernel statements using the visitor pattern.
 *
 * Node Processing
 * ===============
 *
 * BASIC PROCESSING:
 *
 * -- A node generates its kernel statements into the given target scope.
 * -- If a node traverses other nodes, it creates and offers a child scope when it traverses them.
 *
 * EXPRESSION NODES:
 *
 * -- A child node always returns a value or identifier so that the caller can use the value or identifier as an
 *    argument.
 * -- If a child node is a sub-expression, the parent node may offer an identifier. If offered an identifier, the child
 *    node binds its result to the offered identifier and clears the offering by calling
 *    `LocalTarget.clearOfferedIdent()`. If not offered an identifier, the child node creates an identifier and adds it
 *    to the target scope. Finally, the child node returns either the offered identifier or created identifier.
 * -- If a child node is an identifier or value and is offered an identifier, it binds itself to the offered
 *    identifier and returns either the offered identifier or itself.
 *
 * SENTENCE NODES:
 * -- Sentence nodes are not offered identifiers because they are not "values".
 * -- Body sequences are visited such that only the last entry in the sequence can be offered an identifier. If the
 *    body is an expression, it is offered an identifier. Otherwise, no identifier is offered.
 *
 * Ident and Value Expression Processing
 * =====================================
 *
 * Node processing for values and idents follow the same processing pattern. If no ident is offered, simply return the
 * contained value or ident to be used as an argument by the caller. Otherwise, bind the value or ident to the offered
 * ident and return the offered ident to be used as an argument by the caller.
 *
 * -- visitBoolAsExpr
 * -- visitCharAsExpr
 * -- visitDec128AsExpr
 * -- visitEofAsExpr
 * -- visitFltAsExpr
 * -- visitIdentAsExpr
 * -- visitIntAsExpr
 * -- visitNothingAsExpr
 * -- visitStrAsExpr
 */
public final class Generator implements LangVisitor<LocalTarget, CompleteOrIdent> {

    public static final int BREAK_ID = 1;
    public static final int CONTINUE_ID = 2;
    public static final int RETURN_ID = 3;

    public static final String ASK_NOT_HANDLED_ERROR_NAME = "org.torqlang.core.lang.AskNotHandledError";
    public static final String ASK_NOT_HANDLED_ERROR_MESSAGE = """
        Actor could not match request message with an 'ask' handler.""";
    public static final String TELL_NOT_HANDLED_ERROR_NAME = "org.torqlang.core.lang.TellNotHandledError";
    public static final String TELL_NOT_HANDLED_ERROR_MESSAGE = """
        Actor could not match notify message with a 'tell' handler.""";

    private int nextSystemAnonymousSuffix = 0;
    private int nextSystemVarSuffix = 0;

    private static IdentAsPat assertIdentAsPatNotEscaped(Pat pat) {
        if (pat instanceof IdentAsPat identAsPat) {
            if (identAsPat.escaped) {
                throw new InvalidEscapeError(identAsPat);
            }
        } else {
            throw new NotIdentError(pat);
        }
        return identAsPat;
    }

    private static void compileFormalArgsToIdents(List<Pat> formalArgs, List<Ident> formalIdents) {
        for (Pat arg : formalArgs) {
            if (arg instanceof IdentAsPat identAsPat) {
                if (identAsPat.escaped) {
                    throw new InvalidEscapeError(arg);
                }
                formalIdents.add(identAsPat.ident);
            } else {
                throw new NotIdentError(arg);
            }
        }
    }

    private static SeqLang createElseUnhandledSeq(ActorLang lang, Ident errorIdent, String errorName,
                                                  String errorMessage, RecExpr errorDetails, SourceSpan endOfActorSpan)
    {
        VarSntc errorVar = new VarSntc(
            List.of(
                new IdentVarDecl(new IdentAsPat(errorIdent, false, endOfActorSpan), endOfActorSpan)
            ),
            endOfActorSpan
        );
        RecExpr errorExpr = new RecExpr(
            new StrAsExpr(Str.of("error"), endOfActorSpan),
            List.of(
                new FieldExpr(
                    new StrAsExpr(Str.of("name"), endOfActorSpan),
                    new StrAsExpr(Str.of(errorName), endOfActorSpan),
                    endOfActorSpan
                ),
                new FieldExpr(
                    new StrAsExpr(Str.of("message"), endOfActorSpan),
                    new StrAsExpr(Str.of(errorMessage), endOfActorSpan),
                    endOfActorSpan
                ),
                new FieldExpr(
                    new StrAsExpr(Str.of("details"), endOfActorSpan),
                    errorDetails,
                    endOfActorSpan
                )
            ),
            endOfActorSpan
        );
        UnifySntc errorBind = new UnifySntc(new IdentAsExpr(errorIdent, endOfActorSpan), errorExpr, endOfActorSpan);
        ThrowLang errorThrow = new ThrowLang(new IdentAsExpr(errorIdent, endOfActorSpan), endOfActorSpan);
        return new SeqLang(List.of(errorVar, errorBind, errorThrow), lang);
    }

    public final Stmt acceptExpr(SntcOrExpr sntcOrExpr, Ident exprIdent) throws Exception {
        LocalTarget target = LocalTarget.createExprTargetForRoot(exprIdent);
        sntcOrExpr.accept(this, target);
        return target.build();
    }

    private Ident acceptOfferedIdentOrNextSystemVarIdent(LocalTarget target) {
        Ident varIdent;
        if (target.offeredIdent() != null) {
            varIdent = target.offeredIdent();
            target.acceptOfferedIdent();
        } else {
            varIdent = allocateNextSystemVarIdent();
            target.addIdentDef(new IdentDef(varIdent));
        }
        return varIdent;
    }

    private Ident acceptOfferedIdentOrNull(LocalTarget target) {
        Ident varIdent = target.offeredIdent();
        if (varIdent == null) {
            return null;
        }
        target.acceptOfferedIdent();
        return varIdent;
    }

    public final Stmt acceptSntc(SntcOrExpr sntcOrExpr) throws Exception {
        LocalTarget target = LocalTarget.createSntcTargetForRoot();
        sntcOrExpr.accept(this, target);
        return target.build();
    }

    final Ident allocateNextSystemAnonymousIdent() {
        int next = nextSystemAnonymousSuffix;
        nextSystemAnonymousSuffix++;
        return Ident.createSystemAnonymousIdent(next);
    }

    final Ident allocateNextSystemVarIdent() {
        int next = nextSystemVarSuffix;
        nextSystemVarSuffix++;
        return Ident.createSystemVarIdent(next);
    }

    private Stmt buildCaseStmts(CompleteOrIdent arg, ValueOrPtn valueOrPtn, List<CompiledPat.ChildPtn> childPtns,
                                int childPtnNext, MatchClause matchClause, boolean elseNeeded, SourceSpan elseSpan,
                                Ident exprIdent, LocalTarget target) throws Exception
    {
        // BUILD CASE BODY

        LocalTarget caseBodyTarget;
        if (exprIdent != null) {
            caseBodyTarget = target.asExprTargetWithNewScope(exprIdent);
        } else {
            caseBodyTarget = target.asSntcTargetWithNewScope();
        }
        Stmt caseBodyStmt;
        if (childPtnNext < childPtns.size()) {
            CompiledPat.ChildPtn nextChild = childPtns.get(childPtnNext);
            caseBodyStmt = buildCaseStmts(nextChild.arg, nextChild.recPtn, childPtns,
                childPtnNext + 1, matchClause, elseNeeded, elseSpan, exprIdent, caseBodyTarget);
        } else {
            if (matchClause.guard != null) {
                buildMatchClauseWithGuard(matchClause, elseNeeded, elseSpan, exprIdent, caseBodyTarget);
            } else {
                matchClause.accept(this, caseBodyTarget);
            }
            caseBodyStmt = caseBodyTarget.build();
        }

        // CREATE AND RETURN CASE STMT

        Stmt caseStmt;
        if (elseNeeded) {
            Stmt applyElseStmt;
            if (exprIdent != null) {
                applyElseStmt = new ApplyStmt(Ident.$ELSE, List.of(exprIdent), elseSpan);
            } else {
                applyElseStmt = new ApplyStmt(Ident.$ELSE, List.of(), elseSpan);
            }
            caseStmt = new CaseElseStmt(arg, valueOrPtn, caseBodyStmt, applyElseStmt, matchClause.body);
        } else {
            caseStmt = new CaseStmt(arg, valueOrPtn, caseBodyStmt, matchClause.body);
        }
        return caseStmt;
    }

    private void buildMatchClauseWithGuard(MatchClause matchClause, boolean elseNeeded, SourceSpan elseSpan, Ident exprIdent,
                                           LocalTarget caseBodyTarget)
        throws Exception
    {
        // BUILD IF BODY STMT

        SntcOrExpr guard = matchClause.guard;
        Ident guardIdent = allocateNextSystemVarIdent();
        caseBodyTarget.addIdentDef(new IdentDef(guardIdent));
        LocalTarget guardTarget = caseBodyTarget.asExprTargetWithSameScope(guardIdent);
        guard.accept(this, guardTarget);
        LocalTarget ifBodyTarget;
        if (exprIdent != null) {
            ifBodyTarget = caseBodyTarget.asExprTargetWithNewScope(exprIdent);
        } else {
            ifBodyTarget = caseBodyTarget.asSntcTargetWithNewScope();
        }
        matchClause.accept(this, ifBodyTarget);
        Stmt ifBodyStmt = ifBodyTarget.build();

        // CREATE IF STMT

        Stmt ifStmt;
        if (elseNeeded) {
            Stmt applyElseStmt;
            if (exprIdent != null) {
                applyElseStmt = new ApplyStmt(Ident.$ELSE, List.of(exprIdent), elseSpan);
            } else {
                applyElseStmt = new ApplyStmt(Ident.$ELSE, List.of(), elseSpan);
            }
            ifStmt = new IfElseStmt(guardIdent, ifBodyStmt, applyElseStmt, guard);
        } else {
            ifStmt = new IfStmt(guardIdent, ifBodyStmt, guard);
        }
        caseBodyTarget.addStmt(ifStmt);
    }

    @SuppressWarnings("unchecked")
    private void createHandlersProc(ActorLang lang, Ident targetIdent, List<? extends MatchClause> handlers,
                                    String notHandledErrorName, String notHandledErrorMessage,
                                    RecExpr notHandledErrorDetails, LocalTarget target)
        throws Exception
    {
        SourceSpan endOfActorSpan = lang.toSourceSpanEnd();
        LocalTarget handlerBodyTarget = target.asSntcTargetWithNewScope();
        // If there are no handlers, generate kernel statements to throw an error
        if (handlers.isEmpty()) {
            Ident errorIdent = allocateNextSystemVarIdent();
            SeqLang unhandledSeq = createElseUnhandledSeq(lang, errorIdent, notHandledErrorName,
                notHandledErrorMessage, notHandledErrorDetails, endOfActorSpan);
            unhandledSeq.accept(this, handlerBodyTarget);
            Stmt handlerThrowStmt = handlerBodyTarget.build();
            target.addStmt(new CreateProcStmt(targetIdent, new ProcDef(List.of(Ident.$M),
                handlerThrowStmt, lang), lang));
        } else {
            // Synthesize an "else" to throw an error if `$m` is not matched
            Ident errorIdent = allocateNextSystemVarIdent();
            SeqLang elseUnhandledSeq = createElseUnhandledSeq(lang, errorIdent, notHandledErrorName,
                notHandledErrorMessage, notHandledErrorDetails, endOfActorSpan);
            // Generate match logic using case statements
            visitMatchClauses(Ident.$M, handlers.get(0), (List<MatchClause>) handlers, 1,
                elseUnhandledSeq, null, handlerBodyTarget);
            // Add a jump-catch if `return` was used during an `ask`
            if (handlerBodyTarget.isReturnUsed()) {
                handlerBodyTarget.addStmt(new JumpCatchStmt(RETURN_ID, endOfActorSpan));
            }
            Stmt handlerCaseStmt = handlerBodyTarget.build();
            target.addStmt(new CreateProcStmt(targetIdent, new ProcDef(List.of(Ident.$M),
                handlerCaseStmt, lang), lang));
        }
    }

    private ProcDef createProcDef(List<Pat> formalArgs, Ident returnArg, List<SntcOrExpr> bodyList, SourceSpan sourceSpan)
        throws Exception
    {
        List<Ident> xs = new ArrayList<>(formalArgs.size() + 1);
        compileFormalArgsToIdents(formalArgs, xs);
        LocalTarget bodyTarget;
        if (returnArg != null) {
            xs.add(returnArg);
            bodyTarget = LocalTarget.createExprTargetForFuncBody(returnArg);
        } else {
            bodyTarget = LocalTarget.createSntcTargetForProcBody();
        }
        visitBodyList(bodyList, bodyTarget);
        if (bodyTarget.isReturnUsed()) {
            bodyTarget.addStmt(new JumpCatchStmt(RETURN_ID, sourceSpan.toSourceSpanEnd()));
        }
        Stmt bodyStmt = bodyTarget.build();
        return new ProcDef(xs, bodyStmt, sourceSpan);
    }

    final Ident toIdentOrNextAnonymousIdent(Ident ident) {
        return ident.isAnonymous() ? allocateNextSystemAnonymousIdent() : ident;
    }

    @Override
    public final CompleteOrIdent visitActExpr(ActExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope(exprIdent);
        lang.seq.accept(this, childTarget);
        Stmt actBodyStmt = childTarget.build();
        target.addStmt(new ActStmt(actBodyStmt, exprIdent, lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitActorExpr(ActorExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        visitActorLang(exprIdent, lang, target);
        return exprIdent;
    }

    private void visitActorLang(Ident exprIdent, ActorLang lang, LocalTarget target) throws Exception {

        SourceSpan endOfActorSpan = lang.toSourceSpanEnd();

        target.addIdentDef(new IdentDef(Ident.$ACTOR_CFGTR));
        LocalTarget childTarget = target.asSntcTargetWithNewScope();

        // --- Build the configurator
        List<Pat> formalArgs = lang.formalArgs;
        List<Ident> xs = new ArrayList<>(formalArgs.size() + 1);
        compileFormalArgsToIdents(formalArgs, xs);
        xs.add(Ident.$R);
        // Initializer
        LocalTarget actorBodyTarget = childTarget.asSntcTargetWithNewScope();
        for (SntcOrExpr next : lang.initializer()) {
            next.accept(this, actorBodyTarget);
        }
        // Ask handlers
        Ident askProcIdent = allocateNextSystemVarIdent();
        actorBodyTarget.addIdentDef(new IdentDef(askProcIdent));
        RecExpr askErrorDetails = new RecExpr(
            List.of(
                new FieldExpr(
                    new StrAsExpr(Str.of("request"), endOfActorSpan),
                    new IdentAsExpr(Ident.$M, endOfActorSpan),
                    endOfActorSpan
                )
            ),
            endOfActorSpan
        );
        createHandlersProc(lang, askProcIdent, lang.askHandlers(), ASK_NOT_HANDLED_ERROR_NAME, ASK_NOT_HANDLED_ERROR_MESSAGE, askErrorDetails, actorBodyTarget);
        // Tell handlers
        Ident tellProcIdent = allocateNextSystemVarIdent();
        actorBodyTarget.addIdentDef(new IdentDef(tellProcIdent));
        RecExpr tellErrorDetails = new RecExpr(
            List.of(
                new FieldExpr(
                    new StrAsExpr(Str.of("notify"), endOfActorSpan),
                    new IdentAsExpr(Ident.$M, endOfActorSpan),
                    endOfActorSpan
                )
            ),
            endOfActorSpan
        );
        createHandlersProc(lang, tellProcIdent, lang.tellHandlers(), TELL_NOT_HANDLED_ERROR_NAME, TELL_NOT_HANDLED_ERROR_MESSAGE, tellErrorDetails, actorBodyTarget);
        // Create and bind handlers tuple to result
        List<ValueDef> handlers = List.of(new ValueDef(askProcIdent, endOfActorSpan),
            new ValueDef(tellProcIdent, endOfActorSpan));
        TupleDef handlersDef = new TupleDef(Str.of("handlers"), handlers, endOfActorSpan);
        actorBodyTarget.addStmt(new CreateTupleStmt(Ident.$R, handlersDef, endOfActorSpan));
        // --- Build body containing initializer, ask handlers, and tell handlers
        Stmt bodyStmt = actorBodyTarget.build();
        ProcDef actorCfgtrDef = new ProcDef(xs, bodyStmt, lang);
        childTarget.addStmt(new CreateActorCfgtrStmt(Ident.$ACTOR_CFGTR, actorCfgtrDef, lang));

        // Build the actor record
        FieldDef configDef = new FieldDef(Str.of("cfg"), Ident.$ACTOR_CFGTR, endOfActorSpan);
        RecDef actorRecDef = new RecDef(Str.of(exprIdent.name), List.of(configDef), endOfActorSpan);
        childTarget.addStmt(new CreateRecStmt(exprIdent, actorRecDef, endOfActorSpan));

        target.addStmt(childTarget.build());
    }

    @Override
    public final CompleteOrIdent visitActorSntc(ActorSntc lang, LocalTarget target) throws Exception {
        target.addIdentDef(new IdentDef(lang.name()));
        visitActorLang(lang.name, lang, target);
        return null;
    }

    @Override
    public final CompleteOrIdent visitAndExpr(AndExpr lang, LocalTarget target) throws Exception {

        // Translate:
        //     z = k > 5 && k < 11
        //
        // Into:
        //     local $v0 in
        //        $gt(k, 5, $v0)
        //        if $v0 then
        //            $lt(k, 11, z)
        //        else
        //            $bind(false, z)
        //        end
        //    end

        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);

        LocalTarget rightTarget = target.asExprTargetWithNewScope(exprIdent);
        CompleteOrIdent arg2Bool = lang.arg2.accept(this, rightTarget);
        if (rightTarget.offeredIdent() != null) {
            rightTarget.addStmt(BindStmt.create(exprIdent, arg2Bool, lang.arg2));
        }
        Stmt arg2Stmt = rightTarget.build();

        LocalTarget leftTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg1Bool = lang.arg1.accept(this, leftTarget);
        BindStmt arg1False = BindStmt.create(exprIdent, Bool.FALSE, lang.arg1);
        leftTarget.addStmt(new IfElseStmt(arg1Bool, arg2Stmt, arg1False, lang));

        target.addStmt(leftTarget.build());

        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitApplyLang(ApplyLang lang, LocalTarget target) throws Exception {
        Ident exprIdent = target.isExprTarget() ? acceptOfferedIdentOrNextSystemVarIdent(target) : null;
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent proc = lang.proc.accept(this, childTarget);
        List<CompleteOrIdent> ys = new ArrayList<>();
        for (SntcOrExpr arg : lang.args) {
            ys.add(arg.accept(this, childTarget));
        }
        if (exprIdent != null) {
            ys.add(exprIdent);
        }
        childTarget.addStmt(new ApplyStmt(proc, ys, lang));
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitAskSntc(AskSntc lang, LocalTarget target) throws Exception {
        Ident exprIdent = allocateNextSystemVarIdent();
        LocalTarget askTarget = target.asAskTargetWithNewScope(exprIdent);
        askTarget.addIdentDef(new IdentDef(exprIdent));
        lang.body.accept(this, askTarget);
        askTarget.addStmt(new ApplyStmt(Ident.$RESPOND, List.of(exprIdent), lang.toSourceSpanEnd()));
        target.addStmt(askTarget.build());
        return null;
    }

    @Override
    public final CompleteOrIdent visitBeginLang(BeginLang lang, LocalTarget target) throws Exception {
        return visitBodyList(lang.body.list, target);
    }

    private CompleteOrIdent visitBodyList(List<SntcOrExpr> bodyList, LocalTarget target) throws Exception {
        int sizeMinusOne = bodyList.size() - 1;
        // Do not offer intermediate nodes a target identifier
        LocalTarget sntcTarget = target.asSntcTargetWithSameScope();
        for (int i = 0; i < sizeMinusOne; i++) {
            SntcOrExpr next = bodyList.get(i);
            next.accept(this, sntcTarget);
        }
        SntcOrExpr last = bodyList.get(sizeMinusOne);
        // Only offer the last node the target identifier (if one exists)
        return last.accept(this, target);
    }

    @Override
    public final CompleteOrIdent visitBoolAsExpr(BoolAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.bool;
        }
        target.addStmt(BindStmt.create(exprIdent, lang.bool, lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitBoolAsPat(BoolAsPat lang, LocalTarget target) {
        throw new IllegalStateException("BoolAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitBreakSntc(BreakSntc lang, LocalTarget target) {
        if (!target.isBreakAllowed()) {
            throw new BreakNotAllowedError(lang);
        }
        target.setBreakUsed();
        target.addStmt(new JumpThrowStmt(BREAK_ID, lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitCaseClause(CaseClause lang, LocalTarget target) throws Exception {
        lang.body.accept(this, target);
        return null;
    }

    @Override
    public final CompleteOrIdent visitCaseLang(CaseLang lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        CompleteOrIdent arg = lang.arg.accept(this, target);
        visitMatchClauses(arg, lang.caseClause, lang.altCaseClauses, 0, lang.elseSeq, exprIdent, target);
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitCatchClause(CatchClause lang, LocalTarget target) throws Exception {
        lang.body.accept(this, target);
        return null;
    }

    @Override
    public final CompleteOrIdent visitCharAsExpr(CharAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.value();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.value(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitContinueSntc(ContinueSntc lang, LocalTarget target) {
        if (!target.isContinueAllowed()) {
            throw new ContinueNotAllowedError(lang);
        }
        target.setContinueUsed();
        target.addStmt(new JumpThrowStmt(CONTINUE_ID, lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitDec128AsExpr(Dec128AsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.dec128();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.dec128(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitDotSelectExpr(DotSelectExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent rec = lang.recExpr.accept(this, childTarget);
        FeatureOrIdent feature;
        if (lang.featureExpr instanceof IdentAsExpr identAsExpr) {
            feature = Str.of(identAsExpr.ident.name);
        } else {
            feature = (FeatureOrIdent) lang.featureExpr.accept(this, childTarget);
        }
        childTarget.addStmt(new SelectStmt(rec, feature, exprIdent, lang));
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitEofAsExpr(EofAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.value();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.value(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitEofAsPat(EofAsPat lang, LocalTarget target) {
        throw new IllegalStateException("EofAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitFieldExpr(FieldExpr lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitFieldPat(FieldPat lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitFltAsExpr(FltAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.flt64();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.flt64(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitForSntc(ForSntc lang, LocalTarget target) throws Exception {

        LocalTarget childTarget = target.asSntcTargetWithNewScope();

        // ITER

        childTarget.addIdentDef(new IdentDef(Ident.$ITER));
        LocalTarget iterTarget = childTarget.asExprTargetWithSameScope(Ident.$ITER);
        lang.iter.accept(this, iterTarget);

        // FOR

        childTarget.addIdentDef(new IdentDef(Ident.$FOR));
        LocalTarget forTarget = childTarget.asSntcTargetWithNewScope();
        IdentAsPat forNextAsPat = assertIdentAsPatNotEscaped(lang.pat);
        Ident forNext = forNextAsPat.ident;
        forTarget.addIdentDef(new IdentDef(forNext));
        forTarget.addStmt(new ApplyStmt(Ident.$ITER, List.of(forNext), lang.iter));
        Ident forBool = allocateNextSystemVarIdent();
        forTarget.addIdentDef(new IdentDef(forBool));
        forTarget.addStmt(new DisentailsStmt(forNext, Eof.SINGLETON, forBool, lang.iter));
        LocalTarget forBodyTarget = childTarget.asSntcTargetForLoopBodyWithNewScope();
        lang.body.accept(this, forBodyTarget);
        if (forBodyTarget.isContinueUsed()) {
            forBodyTarget.addStmt(new JumpCatchStmt(CONTINUE_ID, lang.body.toSourceSpanEnd()));
        }
        forBodyTarget.addStmt(new ApplyStmt(Ident.$FOR, List.of(), lang.body.toSourceSpanEnd()));
        forTarget.addStmt(new IfStmt(forBool, forBodyTarget.build(), lang.body));
        Stmt forStmt = forTarget.build();
        childTarget.addStmt(new CreateProcStmt(
            Ident.$FOR,
            new ProcDef(List.of(), forStmt, lang.body),
            lang.body));

        // FIRST INVOCATION

        childTarget.addStmt(new ApplyStmt(Ident.$FOR, List.of(), lang.body.toSourceSpanEnd()));
        if (forBodyTarget.isBreakUsed()) {
            childTarget.addStmt(new JumpCatchStmt(BREAK_ID, lang.body.toSourceSpanEnd()));
        }

        target.addStmt(childTarget.build());
        return null;
    }

    @Override
    public final CompleteOrIdent visitFuncExpr(FuncExpr lang, LocalTarget target) throws Exception {
        Ident funcIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        ProcDef procDef = createProcDef(lang.formalArgs, Ident.$R, lang.body.list, lang);
        target.addStmt(new CreateProcStmt(funcIdent, procDef, lang));
        return funcIdent;
    }

    @Override
    public final CompleteOrIdent visitFuncSntc(FuncSntc lang, LocalTarget target) throws Exception {
        target.addIdentDef(new IdentDef(lang.name()));
        ProcDef procDef = createProcDef(lang.formalArgs, Ident.$R, lang.body.list, lang);
        target.addStmt(new CreateProcStmt(lang.name(), procDef, lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitGroupExpr(GroupExpr lang, LocalTarget target) throws Exception {
        return lang.expr.accept(this, target);
    }

    @Override
    public final CompleteOrIdent visitIdentAsExpr(IdentAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.ident;
        }
        target.addStmt(BindStmt.create(exprIdent, lang.ident, lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitIdentAsPat(IdentAsPat lang, LocalTarget target) {
        throw new IllegalStateException("IdentAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitIdentVarDecl(IdentVarDecl lang, LocalTarget target) {
        IdentAsPat identAsPat = lang.identAsPat;
        if (identAsPat.escaped) {
            throw new InvalidEscapeError(identAsPat);
        }
        target.addIdentDef(new IdentDef(identAsPat.ident));
        return null;
    }

    @Override
    public final CompleteOrIdent visitIfClause(IfClause lang, LocalTarget target) {
        throw new IllegalStateException("IfClause visited directly");
    }

    @Override
    public final CompleteOrIdent visitIfLang(IfLang lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        visitIfLangRecursively(lang.ifClause, lang.altIfClauses, 0, lang.elseSeq, exprIdent, target);
        return exprIdent;
    }

    private void visitIfLangRecursively(IfClause ifClause,
                                        List<IfClause> altIfClauses,
                                        int altIfClauseNext,
                                        SeqLang elseSeq,
                                        Ident exprIdent,
                                        LocalTarget target) throws Exception
    {
        LocalTarget boolTarget = target.asExprTargetWithSameScope();
        CompleteOrIdent boolIdent = ifClause.condition.accept(this, boolTarget);
        LocalTarget conTarget;
        if (exprIdent != null) {
            conTarget = target.asExprTargetWithNewScope(exprIdent);
        } else {
            conTarget = target.asSntcTargetWithNewScope();
        }
        ifClause.body.accept(this, conTarget);
        Stmt conStmt = conTarget.build();
        Stmt altStmt = null;
        if (altIfClauseNext < altIfClauses.size()) {
            LocalTarget altTarget;
            if (exprIdent != null) {
                altTarget = target.asExprTargetWithNewScope(exprIdent);
            } else {
                altTarget = target.asSntcTargetWithNewScope();
            }
            visitIfLangRecursively(altIfClauses.get(altIfClauseNext), altIfClauses, altIfClauseNext + 1,
                elseSeq, exprIdent, altTarget);
            altStmt = altTarget.build();
        } else if (elseSeq != null) {
            LocalTarget altTarget;
            if (exprIdent != null) {
                altTarget = target.asExprTargetWithNewScope(exprIdent);
            } else {
                altTarget = target.asSntcTargetWithNewScope();
            }
            elseSeq.accept(this, altTarget);
            altStmt = altTarget.build();
        }
        if (altStmt != null) {
            target.addStmt(new IfElseStmt(boolIdent, conStmt, altStmt, ifClause));
        } else {
            target.addStmt(new IfStmt(boolIdent, conStmt, ifClause));
        }
    }

    @Override
    public final CompleteOrIdent visitImportSntc(ImportSntc lang, LocalTarget target) {
        LocalTarget childTarget = target.asSntcTargetWithNewScope();
        List<CompleteOrIdent> ys = new ArrayList<>();
        ys.add(lang.qualifier);
        CompleteTupleBuilder builder = Rec.completeTupleBuilder();
        // Imported names are added to the parent scope, not the child scope
        for (ImportName in : lang.names) {
            if (in.alias != null) {
                target.addIdentDef(new IdentDef(Ident.create(in.alias.value)));
                builder.addValue(Rec.completeTupleBuilder()
                    .addValue(in.name)
                    .addValue(in.alias)
                    .build());
            } else {
                target.addIdentDef(new IdentDef(Ident.create(in.name.value)));
                builder.addValue(in.name);
            }
        }
        ys.add(builder.build());
        childTarget.addStmt(new ApplyStmt(Ident.$IMPORT, ys, lang));
        target.addStmt(childTarget.build());
        return null;
    }

    @Override
    public final CompleteOrIdent visitIndexSelectExpr(IndexSelectExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent rec = lang.recExpr.accept(this, childTarget);
        FeatureOrIdent feature = (FeatureOrIdent) lang.featureExpr.accept(this, childTarget);
        childTarget.addStmt(new SelectStmt(rec, feature, exprIdent, lang));
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitInitVarDecl(InitVarDecl lang, LocalTarget target) throws Exception {
        // Currently, we only allow identifiers on the left side of an initialization. Allowing a pattern on the left
        // side would be syntactic sugar for a limited case statement. Instead, using a full-featured case statement
        // allows the programmer to explicitly handle mismatches.
        IdentAsPat identAsPat = assertIdentAsPatNotEscaped(lang.varPat);
        // Optimize simple value assignments
        if (lang.valueExpr instanceof ValueAsExpr valueAsExpr) {
            target.addIdentDef(new IdentDef(identAsPat.ident, valueAsExpr.value()));
        } else {
            target.addIdentDef(new IdentDef(identAsPat.ident));
            LocalTarget rightSideTarget = target.asExprTargetWithSameScope(identAsPat.ident);
            lang.valueExpr.accept(this, rightSideTarget);
        }
        return null;
    }

    @Override
    public final CompleteOrIdent visitIntAsExpr(IntAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.int64();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.int64(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitIntAsPat(IntAsPat lang, LocalTarget target) {
        throw new IllegalStateException("IntAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitLocalLang(LocalLang lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        LocalTarget childTarget;
        if (exprIdent != null) {
            childTarget = target.asExprTargetWithNewScope(exprIdent);
        } else {
            childTarget = target.asSntcTargetWithNewScope();
        }
        for (VarDecl d : lang.varDecls) {
            d.accept(this, childTarget);
        }
        visitBodyList(lang.body.list, childTarget);
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    private <T extends MatchClause> void visitMatchClauses(CompleteOrIdent arg,
                                                           T matchClause,
                                                           List<T> altMatchClauses,
                                                           int altMatchClauseNext,
                                                           SeqLang elseSeq,
                                                           Ident exprIdent,
                                                           LocalTarget target) throws Exception
    {
        //////////////////////////////////////////////////////
        // TODO: FIX SOURCE RANGES -- THEY ARE CERTAINLY WRONG
        //////////////////////////////////////////////////////

        LocalTarget childTarget;
        if (exprIdent != null) {
            childTarget = target.asExprTargetWithNewScope(exprIdent);
        } else {
            childTarget = target.asSntcTargetWithNewScope();
        }

        // CREATE ELSE PROC

        boolean elseNeeded = altMatchClauseNext < altMatchClauses.size() || elseSeq != null;
        if (elseNeeded) {
            childTarget.addIdentDef(new IdentDef(Ident.$ELSE));
            LocalTarget elseOrAltTarget;
            if (exprIdent != null) {
                elseOrAltTarget = childTarget.asExprTargetWithNewScope(Ident.$R);
            } else {
                elseOrAltTarget = childTarget.asSntcTargetWithNewScope();
            }
            if (altMatchClauseNext < altMatchClauses.size()) {
                visitMatchClauses(arg, altMatchClauses.get(altMatchClauseNext), altMatchClauses,
                    altMatchClauseNext + 1, elseSeq, Ident.$R, elseOrAltTarget);
            } else {
                elseSeq.accept(this, elseOrAltTarget);
            }
            Stmt elseStmt = elseOrAltTarget.build();
            List<Ident> elseFormalArgs;
            if (exprIdent != null) {
                elseFormalArgs = List.of(Ident.$R);
            } else {
                elseFormalArgs = List.of();
            }
            childTarget.addStmt(new CreateProcStmt(
                Ident.$ELSE, new ProcDef(elseFormalArgs, elseStmt, elseStmt),
                elseStmt));
        }

        // COMPILE PATTERN

        CompiledPat cp = new CompiledPat(matchClause.pat, this);
        cp.compile();

        // CREATE CASE STMT

        childTarget.addStmt(buildCaseStmts(arg, cp.root(), cp.children(), 0, matchClause,
            elseNeeded, elseSeq, exprIdent, childTarget));

        target.addStmt(childTarget.build());
    }

    @Override
    public final CompleteOrIdent visitNothingAsExpr(NothingAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.value();
        }
        target.addStmt(BindStmt.create(exprIdent, lang.value(), lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitNothingAsPat(NothingAsPat lang, LocalTarget target) {
        throw new IllegalStateException("NothingAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitOrExpr(OrExpr lang, LocalTarget target) throws Exception {

        // Translate:
        //     z = k < 5 || k > 11
        //
        // Into:
        //     local $v0 in
        //        $lt(k, 5, $v0)
        //        if $v0 then
        //            $bind(true, z)
        //        else
        //            $gt(k, 11, z)
        //        end
        //    end

        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);

        LocalTarget rightTarget = target.asExprTargetWithNewScope(exprIdent);
        CompleteOrIdent arg2Bool = lang.arg2.accept(this, rightTarget);
        if (rightTarget.offeredIdent() != null) {
            rightTarget.addStmt(BindStmt.create(exprIdent, arg2Bool, lang.arg2));
        }
        Stmt arg2Stmt = rightTarget.build();

        LocalTarget leftTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg1Bool = lang.arg1.accept(this, leftTarget);
        BindStmt arg1True = BindStmt.create(exprIdent, Bool.TRUE, lang.arg1);
        leftTarget.addStmt(new IfElseStmt(arg1Bool, arg1True, arg2Stmt, lang));

        target.addStmt(leftTarget.build());

        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitProcExpr(ProcExpr lang, LocalTarget target) throws Exception {
        Ident procIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        ProcDef procDef = createProcDef(lang.formalArgs, null, lang.body.list, lang);
        target.addStmt(new CreateProcStmt(procIdent, procDef, lang));
        return procIdent;
    }

    @Override
    public final CompleteOrIdent visitProcSntc(ProcSntc lang, LocalTarget target) throws Exception {
        target.addIdentDef(new IdentDef(lang.name()));
        ProcDef procDef = createProcDef(lang.formalArgs, null, lang.body.list, lang);
        target.addStmt(new CreateProcStmt(lang.name(), procDef, lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitProductExpr(ProductExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg1 = lang.arg1.accept(this, childTarget);
        CompleteOrIdent arg2 = lang.arg2.accept(this, childTarget);
        Stmt productStmt;
        if (lang.oper == ProductOper.MULTIPLY) {
            productStmt = new MultiplyStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == ProductOper.DIVIDE) {
            productStmt = new DivideStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == ProductOper.MODULO) {
            productStmt = new ModuloStmt(arg1, arg2, exprIdent, lang);
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Product operator not recognized");
        }
        childTarget.addStmt(productStmt);
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitRecExpr(RecExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteRec completeRec = lang.checkComplete();
        if (completeRec != null) {
            childTarget.addStmt(BindStmt.create(exprIdent, completeRec, lang));
        } else {
            LocalTarget recTarget = childTarget.asExprTargetWithNewScope();
            LiteralOrIdent label;
            if (lang.label() == null) {
                label = Rec.DEFAULT_LABEL;
            } else {
                label = (LiteralOrIdent) lang.label().accept(this, recTarget);
            }
            List<FieldDef> fieldDefs = new ArrayList<>(lang.fields().size());
            for (FieldExpr f : lang.fields()) {
                FeatureOrIdent feature = (FeatureOrIdent) f.feature.accept(this, recTarget);
                CompleteOrIdent value = f.value.accept(this, recTarget);
                fieldDefs.add(new FieldDef(feature, value, f));
            }
            RecDef recDef = new RecDef(label, fieldDefs, lang);
            recTarget.addStmt(new CreateRecStmt(exprIdent, recDef, lang));
            childTarget.addStmt(recTarget.build());
        }
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitRecPat(RecPat lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitRelationalExpr(RelationalExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg1 = lang.arg1.accept(this, childTarget);
        CompleteOrIdent arg2 = lang.arg2.accept(this, childTarget);
        Stmt relStmt;
        if (lang.oper == RelationalOper.EQUAL_TO) {
            relStmt = new EntailsStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == RelationalOper.NOT_EQUAL_TO) {
            relStmt = new DisentailsStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == RelationalOper.LESS_THAN) {
            relStmt = new LessThanStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == RelationalOper.LESS_THAN_OR_EQUAL_TO) {
            relStmt = new LessThanOrEqualToStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == RelationalOper.GREATER_THAN) {
            relStmt = new GreaterThanStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == RelationalOper.GREATER_THAN_OR_EQUAL_TO) {
            relStmt = new GreaterThanOrEqualToStmt(arg1, arg2, exprIdent, lang);
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Relational operator not recognized");
        }
        childTarget.addStmt(relStmt);
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitRespondSntc(RespondSntc lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitReturnSntc(ReturnSntc lang, LocalTarget target) throws Exception {
        if (!target.isReturnAllowed()) {
            throw new ReturnNotAllowedError(lang);
        }
        if (lang.value != null) {
            lang.value.accept(this, target.asExprTargetWithSameScope(Ident.$R));
        }
        target.setReturnUsed();
        target.addStmt(new JumpThrowStmt(RETURN_ID, lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitSelectAndApplyLang(SelectAndApplyLang lang, LocalTarget target)
        throws Exception
    {
        Ident exprIdent = target.isExprTarget() ? acceptOfferedIdentOrNextSystemVarIdent(target) : null;
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent rec = null;
        List<FeatureOrIdent> path = new ArrayList<>();
        SelectExpr selectExpr = lang.selectExpr;
        while (selectExpr != null) {
            FeatureOrIdent nestedFeature;
            if ((selectExpr instanceof DotSelectExpr) && (selectExpr.featureExpr instanceof IdentAsExpr identAsExpr)) {
                nestedFeature = Str.of(identAsExpr.ident.name);
            } else {
                nestedFeature = (FeatureOrIdent) selectExpr.featureExpr.accept(this, childTarget);
            }
            path.add(0, nestedFeature);
            if (selectExpr.recExpr instanceof SelectExpr nextSelectExpr) {
                selectExpr = nextSelectExpr;
            } else {
                rec = selectExpr.recExpr.accept(this, childTarget);
                selectExpr = null;
            }
        }
        List<CompleteOrIdent> ys = new ArrayList<>();
        for (int i = 0; i < lang.args.size(); i++) {
            ys.add(lang.args.get(i).accept(this, childTarget));
        }
        if (exprIdent != null) {
            ys.add(exprIdent);
        }
        childTarget.addStmt(new SelectAndApplyStmt(rec, path, ys, lang));
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitSeqLang(SeqLang lang, LocalTarget target) throws Exception {
        return visitBodyList(lang.list, target);
    }

    @Override
    public final CompleteOrIdent visitSetCellValueSntc(SetCellValueSntc lang, LocalTarget target) throws Exception {
        LocalTarget childTarget = target.asSntcTargetWithNewScope();
        CompleteOrIdent leftSide = lang.leftSide.accept(this, childTarget);
        if (!(leftSide instanceof Ident leftSideIdent)) {
            throw new NotIdentError(lang.leftSide);
        }
        LocalTarget rightSideTarget = childTarget.asExprTargetWithSameScope();
        CompleteOrIdent rightSide = lang.rightSide.accept(this, rightSideTarget);
        childTarget.addStmt(new SetCellValueStmt(leftSideIdent, rightSide, lang));
        target.addStmt(childTarget.build());
        return null;
    }

    @Override
    public final CompleteOrIdent visitSkipSntc(SkipSntc lang, LocalTarget target) {
        target.addStmt(new SkipStmt(lang));
        return null;
    }

    @Override
    public final CompleteOrIdent visitSpawnExpr(SpawnExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = target.isExprTarget() ? acceptOfferedIdentOrNextSystemVarIdent(target) : null;
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        List<CompleteOrIdent> ys = new ArrayList<>();
        for (SntcOrExpr arg : lang.args) {
            ys.add(arg.accept(this, childTarget));
        }
        if (exprIdent != null) {
            ys.add(exprIdent);
        }
        childTarget.addStmt(new ApplyStmt(Ident.$SPAWN, ys, lang));
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitStrAsExpr(StrAsExpr lang, LocalTarget target) {
        Ident exprIdent = acceptOfferedIdentOrNull(target);
        if (exprIdent == null) {
            return lang.str;
        }
        target.addStmt(BindStmt.create(exprIdent, lang.str, lang));
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitStrAsPat(StrAsPat lang, LocalTarget target) {
        throw new IllegalStateException("StrAsPat visited directly");
    }

    @Override
    public final CompleteOrIdent visitSumExpr(SumExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg1 = lang.arg1.accept(this, childTarget);
        CompleteOrIdent arg2 = lang.arg2.accept(this, childTarget);
        Stmt sumStmt;
        if (lang.oper == SumOper.ADD) {
            sumStmt = new AddStmt(arg1, arg2, exprIdent, lang);
        } else if (lang.oper == SumOper.SUBTRACT) {
            sumStmt = new SubtractStmt(arg1, arg2, exprIdent, lang);
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Sum operator not recognized");
        }
        childTarget.addStmt(sumStmt);
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitTellSntc(TellSntc lang, LocalTarget target) throws Exception {
        lang.body.accept(this, target);
        return null;
    }

    @Override
    public final CompleteOrIdent visitThrowLang(ThrowLang lang, LocalTarget target) throws Exception {
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg = lang.arg.accept(this, childTarget);
        childTarget.addStmt(new ThrowStmt(arg, lang));
        target.addStmt(childTarget.build());
        return null;
    }

    @Override
    public final CompleteOrIdent visitTryLang(TryLang lang, LocalTarget target) throws Exception {

        Ident exprIdent = acceptOfferedIdentOrNull(target);

        LocalTarget childTarget;
        if (exprIdent != null) {
            childTarget = target.asExprTargetWithNewScope(exprIdent);
        } else {
            childTarget = target.asSntcTargetWithNewScope();
        }

        //////////////////////////////////////////////////////
        // TODO: FIX SOURCE RANGES -- THEY ARE CERTAINLY WRONG
        //////////////////////////////////////////////////////

        // FINALLY

        SeqLang finallySeq = lang.finallySeq;
        if (finallySeq != null) {
            childTarget.addIdentDef(new IdentDef(Ident.$FINALLY));
            LocalTarget finallyTarget = LocalTarget.createSntcTargetForFinally();
            finallySeq.accept(this, finallyTarget);
            Stmt finallyStmt = finallyTarget.build();
            childTarget.addStmt(new CreateProcStmt(Ident.$FINALLY, new ProcDef(List.of(), finallyStmt, finallySeq), finallySeq));
        }

        // TRY/CATCH

        LocalTarget tryBodyTarget;
        if (exprIdent != null) {
            tryBodyTarget = childTarget.asExprTargetWithNewScope(exprIdent);
        } else {
            tryBodyTarget = childTarget.asSntcTargetWithNewScope();
        }
        lang.body.accept(this, tryBodyTarget);
        Stmt tryBodyStmt = tryBodyTarget.build();
        Ident catchIdent = allocateNextSystemVarIdent();
        LocalTarget catchBodyTarget;
        if (exprIdent != null) {
            catchBodyTarget = childTarget.asExprTargetWithNewScope(exprIdent);
        } else {
            catchBodyTarget = childTarget.asSntcTargetWithNewScope();
        }
        SourceSpan endOfTrySpan = lang.toSourceSpanEnd();
        ApplyLang applyFinallyLang = new ApplyLang(new IdentAsExpr(Ident.$FINALLY, endOfTrySpan),
            List.of(), endOfTrySpan);
        ThrowLang throwAgainLang = new ThrowLang(new IdentAsExpr(catchIdent, endOfTrySpan), endOfTrySpan);
        SeqLang elseSeq;
        if (finallySeq != null) {
            elseSeq = new SeqLang(List.of(applyFinallyLang, throwAgainLang), endOfTrySpan);
        } else {
            elseSeq = new SeqLang(List.of(throwAgainLang), endOfTrySpan);
        }
        visitMatchClauses(catchIdent, lang.catchClauses.get(0), lang.catchClauses, 1,
            elseSeq, exprIdent, catchBodyTarget);
        Stmt catchBodyStmt = catchBodyTarget.build();
        SourceSpan catchSpan = SourceSpan.adjoin(lang.catchClauses);
        childTarget.addStmt(new TryStmt(tryBodyStmt, new CatchStmt(catchIdent, catchBodyStmt, catchSpan), catchSpan));

        // APPLY FINALLY

        if (finallySeq != null) {
            childTarget.addStmt(new ApplyStmt(Ident.$FINALLY, List.of(), finallySeq));
        }

        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitTupleExpr(TupleExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteTuple completeTuple = lang.checkComplete();
        if (completeTuple != null) {
            childTarget.addStmt(BindStmt.create(exprIdent, completeTuple, lang));
        } else {
            LocalTarget recTarget = childTarget.asExprTargetWithNewScope();
            LiteralOrIdent label;
            if (lang.label() == null) {
                label = Rec.DEFAULT_LABEL;
            } else {
                label = (LiteralOrIdent) lang.label().accept(this, recTarget);
            }
            List<ValueDef> valueDefs = new ArrayList<>(lang.values().size());
            for (SntcOrExpr v : lang.values()) {
                CompleteOrIdent value = v.accept(this, recTarget);
                valueDefs.add(new ValueDef(value, v));
            }
            TupleDef tupleDef = new TupleDef(label, valueDefs, lang);
            recTarget.addStmt(new CreateTupleStmt(exprIdent, tupleDef, lang));
            childTarget.addStmt(recTarget.build());
        }
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitTuplePat(TuplePat lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitTypeAnno(TypeAnno lang, LocalTarget target) {
        throw new NeedsImpl();
    }

    @Override
    public final CompleteOrIdent visitUnaryExpr(UnaryExpr lang, LocalTarget target) throws Exception {
        Ident exprIdent = acceptOfferedIdentOrNextSystemVarIdent(target);
        LocalTarget childTarget = target.asExprTargetWithNewScope();
        CompleteOrIdent arg = lang.arg.accept(this, childTarget);
        Stmt unaryStmt;
        if (lang.oper == UnaryOper.NOT) {
            unaryStmt = new NotStmt(arg, exprIdent, lang);
        } else if (lang.oper == UnaryOper.ACCESS) {
            if (!(arg instanceof Ident arg1Ident)) {
                throw new NotIdentError(lang.arg);
            }
            unaryStmt = new GetCellValueStmt(arg1Ident, exprIdent, lang);
        } else if (lang.oper == UnaryOper.NEGATE) {
            unaryStmt = new NegateStmt(arg, exprIdent, lang);
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Unary operator not recognized");
        }
        childTarget.addStmt(unaryStmt);
        target.addStmt(childTarget.build());
        return exprIdent;
    }

    @Override
    public final CompleteOrIdent visitUnifySntc(UnifySntc lang, LocalTarget target) throws Exception {
        CompleteOrIdent leftSide = lang.leftSide.accept(this, target);
        if (leftSide instanceof Ident leftSideIdent) {
            LocalTarget rightSideTarget = target.asExprTargetWithSameScope(leftSideIdent);
            CompleteOrIdent rightSide = lang.rightSide.accept(this, rightSideTarget);
            if (rightSideTarget.offeredIdent() != null) {
                // The offered identifier was not consumed, so we must add a bind statement here
                target.addStmt(BindStmt.create(leftSide, rightSide, lang));
            }
        } else {
            CompleteOrIdent rightSide = lang.rightSide.accept(this, target);
            target.addStmt(BindStmt.create(leftSide, rightSide, lang));
        }
        return null;
    }

    @Override
    public final CompleteOrIdent visitVarSntc(VarSntc lang, LocalTarget target) throws Exception {
        for (VarDecl next : lang.varDecls) {
            next.accept(this, target);
        }
        return null;
    }

    @Override
    public final CompleteOrIdent visitWhileSntc(WhileSntc lang, LocalTarget target) throws Exception {

        LocalTarget childTarget = target.asSntcTargetWithNewScope();

        // GUARD

        childTarget.addIdentDef(new IdentDef(Ident.$GUARD));
        LocalTarget guardTarget = childTarget.asExprTargetWithNewScope(Ident.$R);
        lang.cond.accept(this, guardTarget);
        Stmt guardStmt = guardTarget.build();
        childTarget.addStmt(new CreateProcStmt(
            Ident.$GUARD,
            new ProcDef(List.of(Ident.$R), guardStmt, lang.cond),
            lang.cond));

        // WHILE

        childTarget.addIdentDef(new IdentDef(Ident.$WHILE));
        LocalTarget whileTarget = childTarget.asSntcTargetWithNewScope();
        Ident whileBool = allocateNextSystemVarIdent();
        whileTarget.addIdentDef(new IdentDef(whileBool));
        whileTarget.addStmt(new ApplyStmt(Ident.$GUARD, List.of(whileBool), lang.cond));
        LocalTarget whileBodyTarget = childTarget.asSntcTargetForLoopBodyWithNewScope();
        lang.body.accept(this, whileBodyTarget);
        if (whileBodyTarget.isContinueUsed()) {
            whileBodyTarget.addStmt(new JumpCatchStmt(CONTINUE_ID, lang.body.toSourceSpanEnd()));
        }
        whileBodyTarget.addStmt(new ApplyStmt(Ident.$WHILE, List.of(), lang.body.toSourceSpanEnd()));
        whileTarget.addStmt(new IfStmt(whileBool, whileBodyTarget.build(), lang.body));
        Stmt whileStmt = whileTarget.build();
        childTarget.addStmt(new CreateProcStmt(
            Ident.$WHILE,
            new ProcDef(List.of(), whileStmt, lang.body),
            lang.body));

        // FIRST INVOCATION

        childTarget.addStmt(new ApplyStmt(Ident.$WHILE, List.of(), lang.body.toSourceSpanEnd()));
        if (whileBodyTarget.isBreakUsed()) {
            childTarget.addStmt(new JumpCatchStmt(BREAK_ID, lang.body.toSourceSpanEnd()));
        }

        target.addStmt(childTarget.build());
        return null;
    }

}
