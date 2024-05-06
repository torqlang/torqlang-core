/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ActorExpr;
import org.torqlang.core.lang.ActorSntc;

import java.util.List;

public interface ActorBuilderConfigured {
    ActorCfg actorCfg();

    ActorExpr actorExpr();

    Ident actorIdent();

    Rec actorRec();

    ActorSntc actorSntc();

    Address address();

    List<? extends CompleteOrIdent> args();

    ActorCfg config();

    Stmt createActorRecStmt();

    String source();

    ActorBuilderSpawned spawn() throws Exception;
}
