/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.Address;
import org.torqlang.core.klvm.CompleteOrIdent;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.lang.ActorExpr;
import org.torqlang.core.lang.ActorSntc;

import java.util.List;

public interface ActorBuilderRewritten {
    ActorExpr actorExpr();

    Ident actorIdent();

    ActorSntc actorSntc();

    Address address();

    List<? extends CompleteOrIdent> args();

    ActorBuilderConfigured configure() throws Exception;

    ActorBuilderConstructed construct() throws Exception;

    ActorBuilderGenerated generate() throws Exception;

    String source();

    ActorBuilderSpawned spawn() throws Exception;
}
