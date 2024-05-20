/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $respond
                    local $v0, $v4 in
                        $create_proc(proc ($m) in // free vars: $act, $respond
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        local $v2 in
                                            $create_rec({'request': $m}, $v2)
                                            $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'details': $v2}, $v1)
                                        end
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v3 in
                                        $act
                                            $bind(1, $v3)
                                        end
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v5 in
                                local $v6 in
                                    $create_rec({'notify': $m}, $v6)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v6}, $v5)
                                end
                                throw $v5
                            end
                        end, $v4)
                        $create_tuple('handlers'#[$v0, $v4], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('SingleAct'#{'cfg': $actor_cfgtr}, SingleAct)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("SingleActClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.I32_1, response);
    }

}
