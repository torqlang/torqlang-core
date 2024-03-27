/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Address;
import org.torqlang.core.klvm.CompleteOrIdent;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Stmt;
import org.torqlang.core.lang.ActorExpr;
import org.torqlang.core.lang.ActorSntc;

import java.util.List;

public interface ActorBuilderGenerated {
    ActorExpr actorExpr();

    Ident actorIdent();

    ActorSntc actorSntc();

    Address address();

    List<? extends CompleteOrIdent> args();

    ActorBuilderConstructed createActorRec() throws Exception;

    Stmt createActorRecStmt();

    String source();

    ActorRef spawn() throws Exception;
}
