/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskSingleAct {

    @Test
    public void test() throws Exception {
        String source = """
            actor SingleAct() in
                ask 'perform' in
                    act 1 end
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $act, $respond
                    $create_proc(proc ($m) in // free vars: $act, $respond
                        local $else in
                            $create_proc(proc () in // free vars: $m
                                local $v0 in
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                    throw $v0
                                end
                            end, $else)
                            case $m of 'perform' then
                                local $v1 in
                                    $act
                                        $bind(1, $v1)
                                    end
                                    $respond($v1)
                                end
                            else
                                $else()
                            end
                        end
                    end, $r)
                end, $actor_cfg_ctor)
                $create_rec('SingleAct'#{'cfg': $actor_cfg_ctor}, SingleAct)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("SingleActClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.I32_1, response);
    }

}
