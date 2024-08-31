/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.ActorCfg;
import org.torqlang.core.klvm.CompleteOrIdent;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.lang.ActorSntc;

import java.util.List;

public interface ActorBuilderInit {

    ActorImage actorImage(String source) throws Exception;

    ActorBuilderConfigured configure(String source) throws Exception;

    ActorBuilderConstructed construct(String source) throws Exception;

    ActorBuilderConfigured setActorCfg(ActorCfg actorCfg);

    ActorBuilderConstructed setActorRec(Rec actorRec);

    ActorBuilderParsed setActorSntc(ActorSntc actorSntc);

    ActorBuilderInit setAddress(Address address);

    ActorBuilderInit setArgs(List<? extends CompleteOrIdent> args);

    ActorBuilderReady setSource(String source);

    ActorBuilderInit setSystem(ActorSystem system);

    ActorBuilderInit setTrace(boolean trace);

    ActorBuilderSpawned spawn(ActorCfg actorCfg) throws Exception;

    ActorBuilderSpawned spawn(Rec actorRec) throws Exception;

    ActorBuilderSpawned spawn(String source) throws Exception;
}
