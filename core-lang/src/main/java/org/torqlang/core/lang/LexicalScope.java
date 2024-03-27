/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.IdentDef;
import org.torqlang.core.klvm.LocalStmt;
import org.torqlang.core.klvm.SeqStmt;
import org.torqlang.core.klvm.Stmt;
import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

public final class LexicalScope {
    private final List<IdentDef> identDefs = new ArrayList<>();
    private final List<Stmt> stmts = new ArrayList<>();
    private Stmt stmt;

    public LexicalScope() {
    }

    public final void addIdentDef(IdentDef identDef) {
        identDefs.add(identDef);
    }

    public final void addStmt(Stmt stmt) {
        stmts.add(stmt);
    }

    final Stmt build() {
        if (stmt != null) {
            throw new IllegalStateException("Already built error");
        }
        if (stmts.isEmpty()) {
            throw new IllegalStateException("Empty scope");
        }
        if (identDefs.isEmpty()) {
            if (stmts.size() == 1) {
                stmt = stmts.get(0);
            } else {
                stmt = new SeqStmt(stmts, SourceSpan.adjoin(stmts));
            }
        } else {
            SeqStmt body = new SeqStmt(stmts, SourceSpan.adjoin(stmts));
            stmt = new LocalStmt(identDefs, body, SourceSpan.adjoin(stmts));
        }
        return stmt;
    }

}
