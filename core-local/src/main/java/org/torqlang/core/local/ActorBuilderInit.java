/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.Address;
import org.torqlang.core.klvm.CompleteOrIdent;
import org.torqlang.core.lang.ActorSntc;

import java.util.List;

public interface ActorBuilderInit {
    ActorBuilderParsed setActorSntc(ActorSntc actorSntc);

    ActorBuilderInit setAddress(Address address);

    ActorBuilderInit setArgs(List<? extends CompleteOrIdent> args);

    ActorBuilderReady setSource(String source);

    ActorBuilderInit setTrace(boolean trace);
}
