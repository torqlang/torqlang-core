/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.PartialField;
import org.torqlang.core.klvm.Rec;

public final class Actor {

    public static ActorBuilderInit builder() {
        return new ActorBuilder();
    }

    public static CompleteRec compileForImport(String source) throws Exception {
        Rec actorRec = builder()
                .setSource(source)
                .createActorRec()
                .actorRec();
        actorRec.checkDetermined();
        PartialField actorField = (PartialField) actorRec.fieldAt(0);
        return Rec.completeRecBuilder()
                .addField(actorField.feature, actorField.value.checkComplete())
                .build();
    }

}
