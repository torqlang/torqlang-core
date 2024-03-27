/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Envelope;

import java.util.ArrayList;
import java.util.List;

final class LocalEnvelope implements Envelope {

    private final boolean isControl;
    private final Object message;
    private final ActorRef requester;
    private final Object requestId;

    LocalEnvelope(boolean isControl, Object message, ActorRef requester, Object requestId) {
        this.isControl = isControl;
        this.message = message;
        this.requester = requester;
        this.requestId = requestId;
    }

    @Override
    public final boolean isControl() {
        return isControl;
    }

    @Override
    public final Object message() {
        return message;
    }

    @Override
    public final Object requestId() {
        return requestId;
    }

    @Override
    public final ActorRef requester() {
        return requester;
    }

    @Override
    public final String toString() {
        List<String> fields = new ArrayList<>(4);
        if (isControl) {
            fields.add("isControl=true");
        }
        if (requestId != null) {
            fields.add("requestId=" + requestId);
        }
        if (requester != null) {
            fields.add("requester=" + requester);
        }
        fields.add("message=" + message);
        return "DefaultEnvelope(" + String.join(",", fields) + ")";
    }

}
