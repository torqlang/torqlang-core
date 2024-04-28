/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;
import org.torqlang.core.util.IntegerCounter;
import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.core.lang.MessageText.*;
import static org.torqlang.core.lang.SymbolsAndKeywords.*;
import static org.torqlang.core.util.ListTools.last;

public final class Parser {

    private final Lexer lexer;

    private LexerToken currentToken;

    public Parser(String source) {
        this.lexer = new Lexer(source);
    }

    public static Lang parse(String source) {
        Parser p = new Parser(source);
        return p.parse();
    }

    private static SourceSpan sourceSpanForSeq(List<? extends SntcOrExpr> seq) {
        return seq.get(0).adjoin(last(seq));
    }

    private static String unquoteString(String source, int begin, int end) {
        begin = begin + 1;
        end = end - 1;
        StringBuilder sb = new StringBuilder((end - begin) * 2);
        int i = begin;
        while (i < end) {
            char c1 = source.charAt(i);
            if (c1 == '\\') {
                char c2 = source.charAt(i + 1);
                if (c2 != 'u') {
                    if (c2 == 'r') {
                        c1 = '\r';
                    } else if (c2 == 'n') {
                        c1 = '\n';
                    } else if (c2 == 't') {
                        c1 = '\t';
                    } else if (c2 == 'f') {
                        c1 = '\f';
                    } else if (c2 == 'b') {
                        c1 = '\b';
                    } else if (c2 == '\\') {
                        c1 = '\\';
                    } else if (c2 == '\'') {
                        c1 = '\'';
                    } else if (c2 == '"') {
                        c1 = '"';
                    } else {
                        throw new IllegalArgumentException("Invalid escape sequence: " + c1 + c2);
                    }
                    sb.append(c1);
                    i += 2;
                } else {
                    int code = Integer.parseInt("" + source.charAt(i + 2) + source.charAt(i + 3) +
                        source.charAt(i + 4) + source.charAt(i + 5), 16);
                    sb.append(Character.toChars(code));
                    i += 6;
                }
            } else {
                sb.append(c1);
                i++;
            }
        }
        return sb.toString();
    }

    private LexerToken acceptEndToken() {
        LexerToken current = currentToken;
        if (!current.isKeyword(END_VALUE)) {
            throw new ParserError(END_EXPECTED, current);
        }
        nextToken();
        return current;
    }

    private void assertCurrentAtKeyword(String message, String keywordValue) {
        if (!currentToken.isKeyword(keywordValue)) {
            throw new ParserError(message, currentToken);
        }
    }

    private boolean includesLineBreakBetween(SourceSpan first, LexerToken second) {
        String source = first.source();
        int start = first.end() - 1;
        int stop = Math.min(second.begin(), source.length());
        for (int i = start; i < stop; i++) {
            if (source.charAt(i) == '\n') {
                return true;
            }
        }
        return false;
    }

    public final boolean isEof() {
        return currentToken.isEof();
    }

    private UnaryOper negateOrNotOperFor(LexerToken operToken) {
        if (operToken.isOneCharSymbol()) {
            char firstOperChar = operToken.firstChar();
            if (firstOperChar == SUBTRACT_OPER_CHAR) {
                return UnaryOper.NEGATE;
            }
            if (firstOperChar == NOT_OPER_CHAR) {
                return UnaryOper.NOT;
            }
        }
        return null;
    }

    private LexerToken nextToken() {
        currentToken = lexer.nextToken(true);
        return currentToken;
    }

    public final SntcOrExpr parse() {
        nextToken();
        SntcOrExpr answer = parseSntcOrExpr();
        if (!currentToken.isEof()) {
            throw new ParserError(UNEXPECTED_TOKEN, currentToken);
        }
        return answer;
    }

    private SntcOrExpr parseAccess() {
        SntcOrExpr construct = parseConstruct();
        if (construct != null) {
            return construct;
        }
        LexerToken operToken = currentToken;
        if (!operToken.isOneCharSymbol(ACCESS_CELL_VALUE_OPER_CHAR)) {
            return null;
        }
        nextToken(); // accept '@' token
        SntcOrExpr right = parseAccess();
        if (right == null) {
            throw new ParserError(EXPR_EXPECTED, currentToken);
        }
        return new UnaryExpr(UnaryOper.ACCESS, right, operToken.adjoin(right));
    }

    private ActExpr parseAct() {
        LexerToken actToken = currentToken;
        nextToken(); // accept 'act' token
        SeqLang seq = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new ActExpr(seq, actToken.adjoin(endToken));
    }

    private ActorLang parseActor() {
        LexerToken actorToken = currentToken;
        Ident name = null;
        LexerToken current = nextToken();
        if (current.isIdent()) {
            name = Ident.create(current.substring());
            current = nextToken(); // accept IDENT
        }
        if (!current.isOneCharSymbol(L_PAREN_CHAR)) {
            throw new ParserError(L_PAREN_EXPECTED, current);
        }
        nextToken(); // accept '(' token
        List<Pat> formalArgs = parsePatList();
        current = currentToken;
        if (!current.isOneCharSymbol(R_PAREN_CHAR)) {
            throw new ParserError(R_PAREN_EXPECTED, current);
        }
        nextToken(); // accept ')' token
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        List<SntcOrExpr> body = new ArrayList<>();
        while (true) {
            if (currentToken.isContextualKeyword(HANDLE_VALUE)) {
                LexerToken handleToken = currentToken;
                nextToken(); // accept 'handle' token
                if (currentToken.isContextualKeyword(ASK_VALUE)) {
                    body.add(parseAsk(handleToken));
                } else if (currentToken.isContextualKeyword(TELL_VALUE)) {
                    body.add(parseTell(handleToken));
                } else {
                    throw new ParserError(ASK_OR_TELL_EXPECTED, currentToken);
                }
            } else {
                SntcOrExpr next = parseSntcOrExpr();
                if (next != null) {
                    body.add(next);
                } else {
                    break;
                }
            }
        }
        LexerToken endToken = acceptEndToken();
        if (name != null) {
            return new ActorSntc(name, formalArgs, body, actorToken.adjoin(endToken));
        } else {
            return new ActorExpr(formalArgs, body, actorToken.adjoin(endToken));
        }
    }

    private SntcOrExpr parseAnd() {
        SntcOrExpr answer = parseRelational();
        LexerToken operToken = currentToken;
        while (operToken.isTwoCharSymbol(AND_OPER)) {
            nextToken(); // accept '&&' token
            SntcOrExpr right = parseRelational();
            if (right == null) {
                throw new ParserError(EXPR_EXPECTED, currentToken);
            }
            answer = new AndExpr(answer, right, answer.adjoin(right));
            operToken = currentToken;
        }
        return answer;
    }

    private List<SntcOrExpr> parseArgList() {
        List<SntcOrExpr> args = new ArrayList<>();
        SntcOrExpr arg = parseSntcOrExpr();
        while (arg != null) {
            args.add(arg);
            LexerToken current = currentToken;
            if (!current.isOneCharSymbol(COMMA_CHAR)) {
                break;
            }
            nextToken(); // accept ',' token
            arg = parseSntcOrExpr();
        }
        return args;
    }

    private AskSntc parseAsk(LexerToken handleToken) {
        LexerToken current = nextToken(); // accept 'ask' token
        Pat pat = parsePat();
        if (pat == null) {
            throw new ParserError(PATTERN_EXPECTED, current);
        }
        TypeAnno responseType = parseReturnTypeAnno();
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new AskSntc(pat, body, responseType, handleToken.adjoin(endToken));
    }

    private SntcOrExpr parseAssign() {
        SntcOrExpr left = parseOr();
        LexerToken operToken = currentToken;
        if (operToken.isOneCharSymbol()) {
            if (operToken.firstChar() == UNIFY_OPER_CHAR) {
                nextToken(); // accept '=' token
                SntcOrExpr right = parseOr();
                if (right == null) {
                    throw new ParserError(EXPR_EXPECTED, currentToken);
                }
                return new UnifySntc(left, right, left.adjoin(right));
            }
        } else if (operToken.isTwoCharSymbol()) {
            if (operToken.substringEquals(ASSIGN_CELL_VALUE_OPER)) {
                nextToken(); // accept ':=' token
                SntcOrExpr right = parseOr();
                if (right == null) {
                    throw new ParserError(EXPR_EXPECTED, currentToken);
                }
                return new SetCellValueSntc(left, right, left.adjoin(right));
            }
        }
        return left;
    }

    private BeginLang parseBegin() {
        LexerToken beginToken = currentToken;
        nextToken(); // accept 'begin' token
        SeqLang seq = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new BeginLang(seq, beginToken.adjoin(endToken));
    }

    private CaseLang parseCase() {
        LexerToken caseToken = currentToken;
        LexerToken current = nextToken(); // accept 'case' token
        SntcOrExpr arg = parseSntcOrExpr();
        if (arg == null) {
            throw new ParserError(EXPR_EXPECTED, current);
        }
        assertCurrentAtKeyword(OF_EXPECTED, OF_VALUE);
        CaseClause caseClause = parseCaseClause();
        List<CaseClause> altCaseClauses = new ArrayList<>();
        current = currentToken;
        while (current.isKeyword(OF_VALUE)) {
            CaseClause altCaseClause = parseCaseClause();
            altCaseClauses.add(altCaseClause);
            current = currentToken;
        }
        SeqLang elseSeq = null;
        if (current.isKeyword(ELSE_VALUE)) {
            nextToken(); // accept 'else' token
            elseSeq = parseSeq();
        }
        LexerToken endToken = acceptEndToken();
        return new CaseLang(arg, caseClause, altCaseClauses, elseSeq, caseToken.adjoin(endToken));
    }

    private CaseClause parseCaseClause() {
        LexerToken ofToken = currentToken;
        LexerToken current = nextToken(); // accept 'of' token
        Pat pat = parsePat();
        if (pat == null) {
            throw new ParserError(PATTERN_EXPECTED, current);
        }
        SntcOrExpr guard = null;
        current = currentToken;
        if (current.isKeyword(WHEN_VALUE)) {
            nextToken(); // accept 'when'
            guard = parseSntcOrExpr();
        }
        assertCurrentAtKeyword(THEN_EXPECTED, THEN_VALUE);
        nextToken(); // accept THEN
        SeqLang body = parseSeq();
        return new CaseClause(pat, guard, body, ofToken.adjoin(body));
    }

    private SntcOrExpr parseConstruct() {
        Expr valueOrIdentAsExpr = parseValueOrIdentAsExpr();
        if (valueOrIdentAsExpr != null) {
            return valueOrIdentAsExpr;
        }
        LexerToken current = currentToken;
        if (current.isEof()) {
            return null;
        }
        if (current.isOneCharSymbol()) {
            char firstChar = current.firstChar();
            if (firstChar == L_PAREN_CHAR) {
                return parseGroup();
            }
        }
        if (current.isKeyword()) {
            if (current.substringEquals(VAR_VALUE)) {
                nextToken(); // accept 'var' token
                List<VarDecl> varDecls = parseVarDecls();
                return new VarSntc(varDecls, current.adjoin(last(varDecls)));
            }
            if (current.substringEquals(IF_VALUE)) {
                return parseIf();
            }
            if (current.substringEquals(FOR_VALUE)) {
                return parseFor();
            }
            if (current.substringEquals(WHILE_VALUE)) {
                return parseWhile();
            }
            if (current.substringEquals(CASE_VALUE)) {
                return parseCase();
            }
            if (current.substringEquals(FUNC_VALUE)) {
                return parseFunc();
            }
            if (current.substringEquals(PROC_VALUE)) {
                return parseProc();
            }
            if (current.substringEquals(ACT_VALUE)) {
                return parseAct();
            }
            if (current.substringEquals(SPAWN_VALUE)) {
                return parseSpawn();
            }
            if (current.substringEquals(ACTOR_VALUE)) {
                return parseActor();
            }
            if (current.substringEquals(IMPORT_VALUE)) {
                return parseImport();
            }
            if (current.substringEquals(BEGIN_VALUE)) {
                return parseBegin();
            }
            if (current.substringEquals(LOCAL_VALUE)) {
                return parseLocal();
            }
            if (current.substringEquals(THROW_VALUE)) {
                return parseThrow();
            }
            if (current.substringEquals(TRY_VALUE)) {
                return parseTry();
            }
            if (current.substringEquals(SELF_VALUE)) {
                nextToken(); // accept 'self' token
                return new IdentAsExpr(Ident.$SELF, current);
            }
            if (current.substringEquals(BREAK_VALUE)) {
                nextToken(); // accept 'break' token
                return new BreakSntc(current);
            }
            if (current.substringEquals(CONTINUE_VALUE)) {
                nextToken(); // accept 'continue' token
                return new ContinueSntc(current);
            }
            if (current.substringEquals(RETURN_VALUE)) {
                return parseReturn();
            }
            if (current.substringEquals(SKIP_VALUE)) {
                nextToken(); // accept 'skip' token
                return new SkipSntc(current);
            }
        }
        return null;
    }

    private FieldExpr parseFieldExpr() {
        SntcOrExpr featureExpr = parseSntcOrExpr();
        if (featureExpr == null) {
            return null;
        }
        LexerToken current = nextToken(); // accept ':'
        SntcOrExpr valueExpr = parseSntcOrExpr();
        if (valueExpr == null) {
            throw new ParserError(INVALID_VALUE_EXPR, current);
        }
        return new FieldExpr(featureExpr, valueExpr, featureExpr.adjoin(valueExpr));
    }

    private FieldPat parseFieldPat(IntegerCounter nextImpliedFeature) {
        Pat featureOrValuePat = parsePat();
        if (featureOrValuePat == null) {
            return null;
        }
        FeaturePat featurePat;
        Pat valuePat;
        LexerToken current = currentToken;
        if (current.isOneCharSymbol(COLON_CHAR)) {
            current = nextToken(); // accept ':'
            if (!(featureOrValuePat instanceof FeaturePat featurePatParsed)) {
                throw new ParserError(INVALID_FEATURE_PAT, featureOrValuePat);
            }
            featurePat = featurePatParsed;
            valuePat = parsePat();
            if (valuePat == null) {
                throw new ParserError(INVALID_VALUE_PAT, current);
            }
        } else {
            int nextFeature = nextImpliedFeature.getAndAdd(1);
            featurePat = new IntAsPat(Int32.of(nextFeature), featureOrValuePat.toSourceSpanBegin());
            valuePat = featureOrValuePat;
        }
        return new FieldPat(featurePat, valuePat, featurePat.adjoin(valuePat));
    }

    private ForSntc parseFor() {
        LexerToken forToken = currentToken;
        LexerToken current = nextToken(); // accept 'for' token
        Pat pat = parsePat();
        if (pat == null) {
            throw new ParserError(PATTERN_EXPECTED, current);
        }
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SntcOrExpr iter = parseSntcOrExpr();
        current = currentToken;
        if (iter == null) {
            throw new ParserError(EXPR_EXPECTED, current);
        }
        if (!current.isKeyword(DO_VALUE)) {
            throw new ParserError(DO_EXPECTED, current);
        }
        nextToken(); // accept 'do' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new ForSntc(pat, iter, body, forToken.adjoin(endToken));
    }

    private FuncLang parseFunc() {
        LexerToken funcToken = currentToken;
        Ident name = null;
        LexerToken current = nextToken(); // accept 'func' token
        if (current.isIdent()) {
            name = Ident.create(current.substring());
            current = nextToken(); // accept IDENT
        }
        if (!current.isOneCharSymbol(L_PAREN_CHAR)) {
            throw new ParserError(L_PAREN_EXPECTED, current);
        }
        nextToken(); // accept '(' token
        List<Pat> formalArgs = parsePatList();
        current = currentToken;
        if (!current.isOneCharSymbol(R_PAREN_CHAR)) {
            throw new ParserError(R_PAREN_EXPECTED, current);
        }
        nextToken(); // accept ')' token
        TypeAnno returnType = parseReturnTypeAnno();
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        if (name != null) {
            return new FuncSntc(name, formalArgs, returnType, body, funcToken.adjoin(endToken));
        } else {
            return new FuncExpr(formalArgs, returnType, body, funcToken.adjoin(endToken));
        }
    }

    private SntcOrExpr parseGroup() {
        LexerToken groupBegin = currentToken;
        nextToken(); // accept '(' token
        SeqLang seq = parseSeq();
        LexerToken groupEnd = currentToken;
        if (!groupEnd.isOneCharSymbol(R_PAREN_CHAR)) {
            throw new ParserError(R_PAREN_EXPECTED, groupEnd);
        }
        nextToken(); // accept ')' token
        return new GroupExpr(seq, groupBegin.adjoin(groupEnd));
    }

    private IfLang parseIf() {
        IfClause ifClause = parseIfClause();
        LexerToken current = currentToken;
        List<IfClause> altIfClauses = new ArrayList<>();
        while (current.isKeyword(ELSEIF_VALUE)) {
            IfClause altIfClause = parseIfClause();
            altIfClauses.add(altIfClause);
            current = currentToken;
        }
        SeqLang elseSeq = null;
        if (current.isKeyword(ELSE_VALUE)) {
            nextToken(); // accept 'else' token
            elseSeq = parseSeq();
        }
        LexerToken endToken = acceptEndToken();
        return new IfLang(ifClause, altIfClauses, elseSeq, ifClause.adjoin(endToken));
    }

    private IfClause parseIfClause() {
        LexerToken ifOrElseIfToken = currentToken;
        LexerToken current = nextToken(); // accept 'if' or 'elseif' token
        SntcOrExpr condition = parseSntcOrExpr();
        if (condition == null) {
            throw new ParserError(EXPR_EXPECTED, current);
        }
        assertCurrentAtKeyword(THEN_EXPECTED, THEN_VALUE);
        nextToken(); // accept THEN
        SeqLang body = parseSeq();
        return new IfClause(condition, body, ifOrElseIfToken.adjoin(body));
    }

    private ImportSntc parseImport() {
        LexerToken importToken = currentToken;
        LexerToken current = nextToken(); // accept 'import' token
        if (!current.isIdent()) {
            throw new ParserError(IDENT_EXPECTED, current);
        }
        StringBuilder qualifier = new StringBuilder();
        List<Str> selections = new ArrayList<>();
        LexerToken previous = current;
        current = nextToken();
        // Parse qualifier
        while (current.isOneCharSymbol(DOT_OPER_CHAR) || current.isOneCharSymbol(L_BRACKET_CHAR)) {
            if (!qualifier.isEmpty()) {
                qualifier.append('.');
            }
            qualifier.append(previous.substring());
            if (current.isOneCharSymbol(L_BRACKET_CHAR)) {
                break;
            }
            current = nextToken();
            if (!current.isIdent()) {
                throw new ParserError(IDENT_EXPECTED, current);
            }
            previous = current;
            current = nextToken();
        }
        // Current token is either the '[' or one past the single selection
        if (current.isOneCharSymbol(L_BRACKET_CHAR)) {
            current = nextToken(); // accept '['
            while (current.isIdent()) {
                selections.add(Str.of(current.substring()));
                current = nextToken(); // accept Ident
                if (current.isOneCharSymbol(COMMA_CHAR)) {
                    current = nextToken();
                }
            }
            if (!current.isOneCharSymbol(R_BRACKET_CHAR)) {
                throw new ParserError(R_BRACKET_EXPECTED, current);
            }
            current = nextToken(); // accept ']'
        } else {
            selections.add(Str.of(previous.substring()));
        }
        // Current token is now one past the import expression
        return new ImportSntc(Str.of(qualifier.toString()), selections, importToken.adjoin(current));
    }

    private LocalLang parseLocal() {
        LexerToken localToken = currentToken;
        nextToken(); // accept 'local' token
        List<VarDecl> varDecls = parseVarDecls();
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new LocalLang(varDecls, body, localToken.adjoin(endToken));
    }

    private SntcOrExpr parseOr() {
        SntcOrExpr answer = parseAnd();
        LexerToken operToken = currentToken;
        while (operToken.isTwoCharSymbol(OR_OPER)) {
            nextToken(); // accept '||' token
            SntcOrExpr right = parseAnd();
            if (right == null) {
                throw new ParserError(EXPR_EXPECTED, currentToken);
            }
            answer = new OrExpr(answer, right, answer.adjoin(right));
            operToken = currentToken;
        }
        return answer;
    }

    private Pat parsePat() {
        LexerToken current = currentToken;
        if (current.isOneCharSymbol()) {
            if (current.firstCharEquals(L_BRACE_CHAR)) {
                return parseRecPat(null);
            }
            if (current.firstCharEquals(L_BRACKET_CHAR)) {
                return parseTuplePat(null);
            }
            if (current.firstCharEquals(IDENT_ESC_CHAR)) {
                LexerToken next = nextToken(); // accept '~'
                if (!next.isIdent()) {
                    throw new ParserError(IDENT_EXPECTED, next);
                }
                Ident ident = tokenToIdent(next);
                IdentAsPat identAsPat = new IdentAsPat(ident, true, current.adjoin(next));
                next = nextToken(); // accept IDENT
                if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                    return parseRecOrTuplePat(identAsPat);
                }
                return identAsPat;
            }
            return null;
        }
        if (current.isStr()) {
            LexerToken next = nextToken(); // accept Str token
            String substring = unquoteString(current.source(), current.begin(), current.end());
            StrAsPat strAsPat = new StrAsPat(Str.of(substring), current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTuplePat(strAsPat);
            }
            return strAsPat;
        }
        if (current.isIdent()) {
            LexerToken next = nextToken(); // accept Ident token
            Ident ident = tokenToIdent(current);
            if (!next.isTwoCharSymbol(TYPE_OPER)) {
                return new IdentAsPat(ident, false, current);
            }
            next = nextToken(); // accept '::' and get IDENT
            if (!next.isIdent()) {
                throw new ParserError(IDENT_EXPECTED, next);
            }
            nextToken(); // accept type Ident
            Ident identType = tokenToIdent(next);
            return new IdentAsPat(ident, false,
                new TypeAnno(identType, next), current.adjoin(next));
        }
        if (current.isInt()) {
            nextToken(); // accept Int token
            String symbolText = current.substring();
            return new IntAsPat(symbolText, current);
        }
        if (current.substringEquals(TRUE_VALUE)) {
            LexerToken next = nextToken();  // accept 'true' token
            BoolAsPat boolAsPat = new BoolAsPat(Bool.TRUE, current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTuplePat(boolAsPat);
            }
            return boolAsPat;
        }
        if (current.substringEquals(FALSE_VALUE)) {
            LexerToken next = nextToken();  // accept 'false' token
            BoolAsPat boolAsPat = new BoolAsPat(Bool.FALSE, current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTuplePat(boolAsPat);
            }
            return boolAsPat;
        }
        if (current.substringEquals(NOTHING_VALUE)) {
            LexerToken next = nextToken(); // accept 'nothing' token
            NothingAsPat nothingAsPat = new NothingAsPat(current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTuplePat(nothingAsPat);
            }
            return nothingAsPat;
        }
        if (current.substringEquals(EOF_VALUE)) {
            LexerToken next = nextToken(); // accept 'eof' token
            EofAsPat eofAsPat = new EofAsPat(current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTuplePat(eofAsPat);
            }
            return eofAsPat;
        }
        return null;
    }

    private List<Pat> parsePatList() {
        List<Pat> args = new ArrayList<>();
        Pat arg = parsePat();
        if (arg != null) {
            args.add(arg);
        }
        LexerToken current = currentToken;
        while (current.isOneCharSymbol(COMMA_CHAR)) {
            nextToken(); // accept ',' token
            arg = parsePat();
            current = currentToken;
            if (arg == null) {
                throw new ParserError(PATTERN_EXPECTED, current);
            }
            args.add(arg);
        }
        return args;
    }

    private ProcLang parseProc() {
        LexerToken procToken = currentToken;
        Ident name = null;
        LexerToken current = nextToken(); // accept 'proc' token
        if (current.isIdent()) {
            name = Ident.create(current.substring());
            current = nextToken(); // accept IDENT
        }
        if (!current.isOneCharSymbol(L_PAREN_CHAR)) {
            throw new ParserError(L_PAREN_EXPECTED, current);
        }
        nextToken(); // accept '(' token
        List<Pat> formalArgs = parsePatList();
        current = currentToken;
        if (!current.isOneCharSymbol(R_PAREN_CHAR)) {
            throw new ParserError(R_PAREN_EXPECTED, current);
        }
        nextToken(); // accept ')' token
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        if (name != null) {
            return new ProcSntc(name, formalArgs, body, procToken.adjoin(endToken));
        } else {
            return new ProcExpr(formalArgs, body, procToken.adjoin(endToken));
        }
    }

    private SntcOrExpr parseProduct() {
        SntcOrExpr answer = parseUnary();
        LexerToken operToken = currentToken;
        ProductOper productOper = productOperFor(operToken);
        while (productOper != null) {
            nextToken(); // accept '*', '/' or '%' token
            SntcOrExpr right = parseUnary();
            if (right == null) {
                throw new ParserError(EXPR_EXPECTED, currentToken);
            }
            answer = new ProductExpr(answer, productOper, right, answer.adjoin(right));
            operToken = currentToken;
            productOper = productOperFor(operToken);
        }
        return answer;
    }

    private RecExpr parseRecExpr(Expr label) {
        LexerToken recToken = currentToken;
        nextToken(); // accept '{' token
        List<FieldExpr> fieldExprs = new ArrayList<>();
        FieldExpr fieldExpr = parseFieldExpr();
        if (fieldExpr != null) {
            fieldExprs.add(fieldExpr);
        }
        LexerToken current = currentToken;
        while (current.isOneCharSymbol(COMMA_CHAR)) {
            nextToken(); // accept ',' token
            fieldExpr = parseFieldExpr();
            current = currentToken;
            if (fieldExpr != null) {
                fieldExprs.add(fieldExpr);
            } else {
                throw new ParserError(FIELD_EXPECTED, current);
            }
        }
        if (!current.isOneCharSymbol(R_BRACE_CHAR)) {
            throw new ParserError(R_BRACE_EXPECTED, current);
        }
        nextToken(); // accept '}' token
        SourceSpan recSpan = label == null ? recToken.adjoin(current) : label.adjoin(current);
        return new RecExpr(label, fieldExprs, recSpan);
    }

    private Expr parseRecOrTupleExpr(LabelExpr label) {
        LexerToken current = nextToken(); // accept '#' token
        if (current.isOneCharSymbol()) {
            if (current.firstCharEquals(L_BRACE_CHAR)) {
                return parseRecExpr(label);
            } else if (current.firstCharEquals(L_BRACKET_CHAR)) {
                return parseTupleExpr(label);
            }
        }
        throw new ParserError(L_BRACE_OR_L_BRACKET_EXPECTED, current);
    }

    private Pat parseRecOrTuplePat(LabelPat label) {
        LexerToken current = nextToken(); // accept '#' token
        if (current.isOneCharSymbol()) {
            if (current.firstCharEquals(L_BRACE_CHAR)) {
                return parseRecPat(label);
            } else if (current.firstCharEquals(L_BRACKET_CHAR)) {
                return parseTuplePat(label);
            }
        }
        throw new ParserError(L_BRACE_OR_L_BRACKET_EXPECTED, current);
    }

    private RecPat parseRecPat(LabelPat label) {
        LexerToken recToken = currentToken;
        nextToken(); // accept '{' token
        IntegerCounter nextImpliedFeature = new IntegerCounter(0);
        boolean partialArity = false;
        List<FieldPat> fieldPats = new ArrayList<>();
        FieldPat fieldPat = parseFieldPat(nextImpliedFeature);
        if (fieldPat != null) {
            fieldPats.add(fieldPat);
        }
        LexerToken current = currentToken;
        while (current.isOneCharSymbol(COMMA_CHAR)) {
            nextToken(); // accept ',' token
            fieldPat = parseFieldPat(nextImpliedFeature);
            current = currentToken;
            if (fieldPat != null) {
                fieldPats.add(fieldPat);
            } else if (current.isThreeCharSymbol(PARTIAL_ARITY_OPER)) {
                current = nextToken(); // accept '...' token
                partialArity = true;
            } else {
                throw new ParserError(FIELD_EXPECTED, current);
            }
        }
        if (!current.isOneCharSymbol(R_BRACE_CHAR)) {
            throw new ParserError(R_BRACE_EXPECTED, current);
        }
        nextToken(); // accept '}' token
        SourceSpan recSpan = label == null ? recToken.adjoin(current) : label.adjoin(current);
        return new RecPat(label, fieldPats, partialArity, recSpan);
    }

    private SntcOrExpr parseRelational() {
        SntcOrExpr answer = parseSum();
        LexerToken operToken = currentToken;
        RelationalOper relationalOper = relationalOperFor(operToken);
        while (relationalOper != null) {
            nextToken(); // accept '==', '!=', '<', '<=', '>' or '>=' token
            SntcOrExpr right = parseSum();
            if (right == null) {
                throw new ParserError(EXPR_EXPECTED, currentToken);
            }
            answer = new RelationalExpr(answer, relationalOper, right, answer.adjoin(right));
            operToken = currentToken;
            relationalOper = relationalOperFor(operToken);
        }
        return answer;
    }

    private ReturnSntc parseReturn() {
        LexerToken returnToken = currentToken;
        nextToken(); // accept 'return' token
        SntcOrExpr expr = parseSntcOrExpr();
        if (expr == null) {
            return new ReturnSntc(null, returnToken);
        }
        return new ReturnSntc(expr, returnToken.adjoin(expr));
    }

    private TypeAnno parseReturnTypeAnno() {
        LexerToken current = currentToken;
        TypeAnno typeAnno = null;
        if (current.isTwoCharSymbol(RETURN_TYPE_OPER)) {
            LexerToken typeOper = current;
            current = nextToken(); // accept '->' and get IDENT
            if (!current.isIdent()) {
                throw new ParserError(IDENT_EXPECTED, current);
            }
            String identText = current.substring();
            Ident typeIdent = Ident.create(identText);
            typeAnno = new TypeAnno(typeIdent, typeOper.adjoin(current));
            nextToken(); // accept Ident and position for next
        }
        return typeAnno;
    }

    /*
     * Because parenthesis '(...)' and brackets '[...]' are also used for grouping and tuple literals, respectively,
     * we must disambiguate their usage. When parenthesis or brackets are applied to a left-side operand, then at
     * least the opening symbol must appear on the same line as the operand. For example, 'aVariable(arg)[feat]' cannot
     * be broken such that the '(' or '[' appear first on the next line.
     */
    private SntcOrExpr parseSelectOrApply() {
        SntcOrExpr answer = parseAccess();
        if (answer == null) {
            return null;
        }
        LexerToken operToken = currentToken;
        SelectOrApply selectOrApply = selectOrApplyFor(answer.toSourceSpanEnd(), operToken);
        while (selectOrApply != null) {
            nextToken(); // accept '.', '[', or '(' token
            if (selectOrApply == SelectOrApply.DOT) {
                SntcOrExpr right = parseAccess();
                if (right == null) {
                    throw new ParserError(SELECTOR_EXPECTED, currentToken);
                }
                answer = new DotSelectExpr(answer, right, answer.adjoin(right));
            } else if (selectOrApply == SelectOrApply.INDEX) {
                SntcOrExpr featureExpr = parseSntcOrExpr();
                if (featureExpr == null) {
                    throw new ParserError(EXPR_EXPECTED, currentToken);
                }
                LexerToken current = currentToken;
                if (!current.isOneCharSymbol(R_BRACKET_CHAR)) {
                    throw new ParserError(R_BRACKET_EXPECTED, current);
                }
                nextToken(); // accept ']' token
                answer = new IndexSelectExpr(answer, featureExpr, answer.adjoin(current));
            } else {
                List<SntcOrExpr> args = parseArgList();
                LexerToken current = currentToken;
                if (!current.isOneCharSymbol(R_PAREN_CHAR)) {
                    throw new ParserError(R_PAREN_EXPECTED, current);
                }
                nextToken(); // accept ')' token
                if (answer instanceof SelectExpr selectExpr) {
                    answer = new SelectAndApplyLang(selectExpr, args, answer.adjoin(current));
                } else {
                    answer = new ApplyLang(answer, args, answer.adjoin(current));
                }
            }
            operToken = currentToken;
            selectOrApply = selectOrApplyFor(answer.toSourceSpanEnd(), operToken);
        }
        return answer;
    }

    /*
     * Parse a sequence of sentences and/or expressions until we reach a terminating token. Some examples of
     * terminating tokens are END_OF_FILE, END, ELSE, and ELSEIF.
     */
    private SeqLang parseSeq() {
        List<SntcOrExpr> list = new ArrayList<>();
        SntcOrExpr next = parseSntcOrExpr();
        while (next != null) {
            list.add(next);
            next = parseSntcOrExpr();
        }
        if (list.isEmpty()) {
            // If list is empty, no tokens were accepted
            LexerToken unrecognizedToken = currentToken;
            throw new ParserError(STMT_OR_EXPR_EXPECTED, unrecognizedToken);
        }
        return new SeqLang(list, sourceSpanForSeq(list));
    }

    private SntcOrExpr parseSntcOrExpr() {
        SntcOrExpr assign = parseAssign();
        while (currentToken.isOneCharSymbol(SEMICOLON_CHAR)) {
            nextToken();
        }
        return assign;
    }

    private SpawnExpr parseSpawn() {
        LexerToken spawnToken = currentToken;
        LexerToken current = nextToken(); // accept 'spawn' token
        if (!current.isOneCharSymbol(L_PAREN_CHAR)) {
            throw new ParserError(L_PAREN_EXPECTED, current);
        }
        nextToken(); // accept '(' token
        List<SntcOrExpr> args = parseArgList();
        current = currentToken;
        if (!current.isOneCharSymbol(R_PAREN_CHAR)) {
            throw new ParserError(R_PAREN_EXPECTED, current);
        }
        nextToken(); // accept ')' token
        return new SpawnExpr(args, spawnToken.adjoin(current));
    }

    private SntcOrExpr parseSum() {
        SntcOrExpr answer = parseProduct();
        LexerToken operToken = currentToken;
        SumOper sumOper = sumOperFor(operToken);
        while (sumOper != null) {
            nextToken(); // accept '+' or '-' token
            SntcOrExpr right = parseProduct();
            if (right == null) {
                throw new ParserError(EXPR_EXPECTED, currentToken);
            }
            answer = new SumExpr(answer, sumOper, right, answer.adjoin(right));
            operToken = currentToken;
            sumOper = sumOperFor(operToken);
        }
        return answer;
    }

    private TellSntc parseTell(LexerToken handleToken) {
        LexerToken current = nextToken(); // accept 'tell' token
        Pat pat = parsePat();
        if (pat == null) {
            throw new ParserError(PATTERN_EXPECTED, current);
        }
        assertCurrentAtKeyword(IN_EXPECTED, IN_VALUE);
        nextToken(); // accept 'in' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new TellSntc(pat, body, handleToken.adjoin(endToken));
    }

    private ThrowLang parseThrow() {
        LexerToken throwToken = currentToken;
        nextToken(); // accept 'throw' token
        SntcOrExpr expr = parseSntcOrExpr();
        if (expr == null) {
            throw new ParserError(EXPR_EXPECTED, currentToken);
        }
        return new ThrowLang(expr, throwToken.adjoin(expr));
    }

    private TryLang parseTry() {
        LexerToken tryToken = currentToken;
        nextToken(); // accept 'try' token
        SeqLang seq = parseSeq();
        LexerToken current = currentToken;
        List<CatchClause> catchClauses = new ArrayList<>();
        while (current.isKeyword(CATCH_VALUE)) {
            LexerToken catchToken = current;
            nextToken(); // accept 'catch' token
            Pat pat = parsePat();
            if (pat == null) {
                throw new ParserError(PATTERN_EXPECTED, current);
            }
            assertCurrentAtKeyword(THEN_EXPECTED, THEN_VALUE);
            nextToken(); // accept 'then' token
            SeqLang catchSeq = parseSeq();
            catchClauses.add(new CatchClause(pat, catchSeq, catchToken.adjoin(catchSeq)));
            current = currentToken;
        }
        SeqLang finallySntc = null;
        if (current.isKeyword(FINALLY_VALUE)) {
            nextToken(); // accept 'finally' token
            finallySntc = parseSeq();
        }
        LexerToken endToken = acceptEndToken();
        if (catchClauses.isEmpty() && finallySntc == null) {
            throw new ParserError(CATCH_OR_FINALLY_EXPECTED, endToken);
        }
        return new TryLang(seq, catchClauses, finallySntc, tryToken.adjoin(endToken));
    }

    private TupleExpr parseTupleExpr(Expr label) {
        // tupleValue: '[' (expr (',' expr)*)? ']';
        LexerToken tupleToken = currentToken;
        nextToken(); // accept '[' token
        List<SntcOrExpr> valueExprs = parseArgList();
        LexerToken current = currentToken;
        if (!current.isOneCharSymbol(R_BRACKET_CHAR)) {
            throw new ParserError(R_BRACKET_EXPECTED, current);
        }
        nextToken(); // accept ']' token
        SourceSpan tupleSpan = label == null ? tupleToken.adjoin(current) : label.adjoin(current);
        return new TupleExpr(label, valueExprs, tupleSpan);
    }

    private TuplePat parseTuplePat(LabelPat label) {
        LexerToken tupleToken = currentToken;
        nextToken(); // accept '[' token
        boolean partialArity = false;
        List<Pat> valuePats = new ArrayList<>();
        Pat pat = parsePat();
        if (pat != null) {
            valuePats.add(pat);
        }
        LexerToken current = currentToken;
        while (current.isOneCharSymbol(COMMA_CHAR)) {
            nextToken(); // accept ',' token
            pat = parsePat();
            current = currentToken;
            if (pat != null) {
                valuePats.add(pat);
            } else if (current.isThreeCharSymbol(PARTIAL_ARITY_OPER)) {
                current = nextToken(); // accept '...' token
                partialArity = true;
            } else {
                throw new ParserError(PATTERN_EXPECTED, current);
            }
        }
        if (!current.isOneCharSymbol(R_BRACKET_CHAR)) {
            throw new ParserError(R_BRACKET_EXPECTED, current);
        }
        nextToken(); // accept ']' token
        SourceSpan tupleSpan = label == null ? tupleToken.adjoin(current) : label.adjoin(current);
        return new TuplePat(label, valuePats, partialArity, tupleSpan);
    }

    private SntcOrExpr parseUnary() {
        SntcOrExpr selectOrApply = parseSelectOrApply();
        if (selectOrApply != null) {
            return selectOrApply;
        }
        LexerToken operToken = currentToken;
        UnaryOper unaryOper = negateOrNotOperFor(operToken);
        if (unaryOper == null) {
            return null;
        }
        nextToken(); // accept '-' or '!' token
        SntcOrExpr right = parseUnary();
        if (right == null) {
            throw new ParserError(EXPR_EXPECTED, currentToken);
        }
        return new UnaryExpr(unaryOper, right, operToken.adjoin(right));
    }

    private Expr parseValueOrIdentAsExpr() {
        LexerToken current = currentToken;
        if (current.isOneCharSymbol()) {
            if (current.firstCharEquals(L_BRACE_CHAR)) {
                return parseRecExpr(null);
            }
            if (current.firstCharEquals(L_BRACKET_CHAR)) {
                return parseTupleExpr(null);
            }
            return null;
        }
        if (current.isStr()) {
            LexerToken next = nextToken(); // accept STR token
            String substring = unquoteString(current.source(), current.begin(), current.end());
            StrAsExpr strAsExpr = new StrAsExpr(Str.of(substring), current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTupleExpr(strAsExpr);
            }
            return strAsExpr;
        }
        if (current.isIdent()) {
            LexerToken next = nextToken(); // accept IDENT token
            Ident ident = tokenToIdent(current);
            IdentAsExpr identAsExpr = new IdentAsExpr(ident, current);
            if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                return parseRecOrTupleExpr(identAsExpr);
            }
            return identAsExpr;
        }
        if (current.isInt()) {
            nextToken(); // accept Int token
            String symbolText = current.substring();
            return new IntAsExpr(symbolText, current);
        }
        if (current.isFlt()) {
            nextToken();  // accept Flt token
            String symbolText = current.substring();
            return new FltAsExpr(symbolText, current);
        }
        if (current.isDec()) {
            nextToken();  // accept Dec token
            String symbolText = current.substring();
            return new Dec128AsExpr(symbolText, current);
        }
        if (current.isKeyword()) {
            if (current.substringEquals(TRUE_VALUE)) {
                LexerToken next = nextToken();  // accept 'true' token
                BoolAsExpr boolAsExpr = new BoolAsExpr(Bool.TRUE, current);
                if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                    return parseRecOrTupleExpr(boolAsExpr);
                }
                return boolAsExpr;
            }
            if (current.substringEquals(FALSE_VALUE)) {
                LexerToken next = nextToken();  // accept 'false' token
                BoolAsExpr boolAsExpr = new BoolAsExpr(Bool.FALSE, current);
                if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                    return parseRecOrTupleExpr(boolAsExpr);
                }
                return boolAsExpr;
            }
            if (current.substringEquals(NOTHING_VALUE)) {
                LexerToken next = nextToken(); // accept 'nothing' token
                NothingAsExpr nothingAsExpr = new NothingAsExpr(current);
                if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                    return parseRecOrTupleExpr(nothingAsExpr);
                }
                return nothingAsExpr;
            }
            if (current.substringEquals(EOF_VALUE)) {
                LexerToken next = nextToken(); // accept 'EOF' token
                EofAsExpr eofAsExpr = new EofAsExpr(current);
                if (next.isOneCharSymbol(HASH_TAG_CHAR)) {
                    return parseRecOrTupleExpr(eofAsExpr);
                }
                return eofAsExpr;
            }
        }
        if (current.isChar()) {
            // TODO: Try to move this code to a new Char.decode() method, like we did
            //       for Int32, Int64, and Dec128
            nextToken(); // accept character token
            char firstCharValue = current.substringCharAt(1); // first char past '&'
            if (current.length() == 2) {
                return new CharAsExpr(Char.of(firstCharValue), current);
            }
            if (firstCharValue != '\\') {
                throw new ParserError(INVALID_CHARACTER_LITERAL, current);
            }
            if (current.length() == 3) {
                char secondCharValue = current.substringCharAt(2);
                if (secondCharValue == 't') {
                    return new CharAsExpr(Char.of('\t'), current);
                }
                if (secondCharValue == 'b') {
                    return new CharAsExpr(Char.of('\b'), current);
                }
                if (secondCharValue == 'n') {
                    return new CharAsExpr(Char.of('\n'), current);
                }
                if (secondCharValue == 'r') {
                    return new CharAsExpr(Char.of('\r'), current);
                }
                if (secondCharValue == 'f') {
                    return new CharAsExpr(Char.of('\f'), current);
                }
            }
            if (current.length() == 7) {
                char secondCharValue = current.substringCharAt(2);
                if (secondCharValue == 'u') {
                    char charValue = (char) Integer.parseInt(current.substring().substring(3), 16);
                    return new CharAsExpr(Char.of(charValue), current);
                }
            }
            throw new ParserError(INVALID_CHARACTER_LITERAL, current);
        }
        return null;
    }

    private VarDecl parseVarDecl() {
        LexerToken current = currentToken;
        Pat pat = parsePat();
        if (pat == null) {
            throw new ParserError(PATTERN_EXPECTED, current);
        }
        current = currentToken;
        if (current.isOneCharSymbol(UNIFY_OPER_CHAR)) {
            nextToken(); // accept '=' token
            SntcOrExpr expr = parseSntcOrExpr();
            if (expr == null) {
                throw new ParserError(EXPR_EXPECTED, current);
            }
            return new InitVarDecl(pat, expr, pat.adjoin(expr));
        }
        if (pat instanceof IdentAsPat identAsPat) {
            return new IdentVarDecl(identAsPat, identAsPat);
        }
        throw new ParserError(INVALID_VAR_DECL, pat);
    }

    private List<VarDecl> parseVarDecls() {
        List<VarDecl> varDecls = new ArrayList<>();
        VarDecl varDecl = parseVarDecl();
        while (true) {
            varDecls.add(varDecl);
            LexerToken current = currentToken;
            if (!current.isOneCharSymbol(COMMA_CHAR)) {
                break;
            }
            nextToken(); // accept ',' token
            varDecl = parseVarDecl();
        }
        return varDecls;
    }

    private WhileSntc parseWhile() {
        LexerToken whileToken = currentToken;
        nextToken(); // accept 'while' token
        SntcOrExpr cond = parseSntcOrExpr();
        LexerToken current = currentToken;
        if (cond == null) {
            throw new ParserError(EXPR_EXPECTED, current);
        }
        if (!current.isKeyword(DO_VALUE)) {
            throw new ParserError(DO_EXPECTED, current);
        }
        nextToken(); // accept 'do' token
        SeqLang body = parseSeq();
        LexerToken endToken = acceptEndToken();
        return new WhileSntc(cond, body, whileToken.adjoin(endToken));
    }

    private ProductOper productOperFor(LexerToken operToken) {
        if (operToken.isOneCharSymbol()) {
            char firstOperChar = operToken.firstChar();
            if (firstOperChar == MULTIPLY_OPER_CHAR) {
                return ProductOper.MULTIPLY;
            }
            if (firstOperChar == DIVIDE_OPER_CHAR) {
                return ProductOper.DIVIDE;
            }
            if (firstOperChar == MODULO_OPER_CHAR) {
                return ProductOper.MODULO;
            }
        }
        return null;
    }

    private RelationalOper relationalOperFor(LexerToken operToken) {
        if (operToken.isOneCharSymbol()) {
            char firstOperChar = operToken.firstChar();
            if (firstOperChar == LESS_THAN_OPER_CHAR) {
                return RelationalOper.LESS_THAN;
            }
            if (firstOperChar == GREATER_THAN_OPER_CHAR) {
                return RelationalOper.GREATER_THAN;
            }
        } else if (operToken.isTwoCharSymbol()) {
            if (operToken.substringEquals(EQUAL_TO_OPER)) {
                return RelationalOper.EQUAL_TO;
            }
            if (operToken.substringEquals(NOT_EQUAL_TO_OPER)) {
                return RelationalOper.NOT_EQUAL_TO;
            }
            if (operToken.substringEquals(LESS_THAN_OR_EQUAL_TO_OPER)) {
                return RelationalOper.LESS_THAN_OR_EQUAL_TO;
            }
            if (operToken.substringEquals(GREATER_THAN_OR_EQUAL_TO_OPER)) {
                return RelationalOper.GREATER_THAN_OR_EQUAL_TO;
            }
        }
        return null;
    }

    private SelectOrApply selectOrApplyFor(SourceSpan operand, LexerToken operToken) {
        if (operToken.isOneCharSymbol()) {
            char firstOperChar = operToken.firstChar();
            if (firstOperChar == DOT_OPER_CHAR) {
                return SelectOrApply.DOT;
            }
            if (firstOperChar == L_BRACKET_CHAR) {
                if (includesLineBreakBetween(operand, operToken)) {
                    return null;
                }
                return SelectOrApply.INDEX;
            }
            if (firstOperChar == L_PAREN_CHAR) {
                if (includesLineBreakBetween(operand, operToken)) {
                    return null;
                }
                return SelectOrApply.APPLY;
            }
        }
        return null;
    }

    public final String source() {
        return lexer.source();
    }

    private SumOper sumOperFor(LexerToken operToken) {
        if (operToken.isOneCharSymbol()) {
            char firstOperChar = operToken.firstChar();
            if (firstOperChar == ADD_OPER_CHAR) {
                return SumOper.ADD;
            }
            if (firstOperChar == SUBTRACT_OPER_CHAR) {
                return SumOper.SUBTRACT;
            }
        }
        return null;
    }

    private Ident tokenToIdent(LexerToken token) {
        if (token.firstChar() != '`') {
            return Ident.create(token.substring());
        }
        int begin = token.begin() + 1;
        int end = token.end() - 1;
        String source = token.source();
        StringBuilder sb = new StringBuilder((end - begin) * 2);
        int i = begin;
        while (i < end) {
            char c1 = source.charAt(i);
            if (c1 == '\\') {
                char c2 = source.charAt(i + 1);
                if (c2 != 'u') {
                    if (c2 == 'r') {
                        c1 = '\r';
                    } else if (c2 == 'n') {
                        c1 = '\n';
                    } else if (c2 == 't') {
                        c1 = '\t';
                    } else if (c2 == 'f') {
                        c1 = '\f';
                    } else if (c2 == 'b') {
                        c1 = '\b';
                    } else if (c2 == '\\') {
                        c1 = '\\';
                    } else if (c2 == '\'') {
                        c1 = '\'';
                    } else {
                        throw new IllegalArgumentException("Invalid escape sequence: " + c1 + c2);
                    }
                    sb.append(c1);
                    i += 2;
                } else {
                    int code = Integer.parseInt("" + source.charAt(i + 2) + source.charAt(i + 3) +
                        source.charAt(i + 4) + source.charAt(i + 5), 16);
                    sb.append(Character.toChars(code));
                    i += 6;
                }
            } else {
                sb.append(c1);
                i++;
            }
        }
        return Ident.create(sb.toString());
    }

    private enum SelectOrApply {
        DOT,
        INDEX,
        APPLY
    }

}
