/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public interface StreamClientResponse {
    Address address();

    Queue<Envelope> awaitEof(long timeout, TimeUnit unit) throws Exception;

    Queue<Envelope> mailbox();
}
