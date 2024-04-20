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
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskSingleAct {

    @Test
    public void test() throws Exception {
        String source = """
            actor SingleAct() in
                handle ask 'perform' in
                    act 1 end
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $respond
                    local $v0, $v3 in
                        $create_proc(proc ($m) in // free vars: $act, $respond
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v1)
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v2 in
                                        $act
                                            $bind(1, $v2)
                                        end
                                        $respond($v2)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v4 in
                                $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': $m}, $v4)
                                throw $v4
                            end
                        end, $v3)
                        $create_tuple('handlers'#[$v0, $v3], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('SingleAct'#{'cfg': $actor_cfgtr}, SingleAct)
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
