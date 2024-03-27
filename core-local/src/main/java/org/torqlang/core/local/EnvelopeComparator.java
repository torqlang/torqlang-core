/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.Envelope;

import java.util.Comparator;

public final class EnvelopeComparator implements Comparator<Envelope> {

    public static final EnvelopeComparator SINGLETON = new EnvelopeComparator();

    private EnvelopeComparator() {
    }

    @Override
    public final int compare(Envelope o1, Envelope o2) {
        return priority(o1) - priority(o2);
    }

    public final int priority(Envelope envelope) {
        int answer = 4;
        if (envelope.isControl()) {
            answer -= 2;
        }
        if (envelope.isResponse()) {
            answer -= 1;
        }
        return answer;
    }

}
