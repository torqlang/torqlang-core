/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.Envelope;

/**
 * A simple and minimal interface to the concept of a Mailbox.
 */
public interface Mailbox {

    void add(Envelope envelope);

    boolean isEmpty();

    /**
     * Return the next message or null if mailbox is empty.
     */
    Envelope peekNext();

    /**
     * Remove and return the next message or null if mailbox is empty.
     */
    Envelope removeNext();

    int size();
}
