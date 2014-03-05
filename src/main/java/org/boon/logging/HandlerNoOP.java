package org.boon.logging;

import org.boon.core.Handler;

/**
 * Created by Richard on 3/5/14.
 */
public final class HandlerNoOP implements Handler<LogRecord> {
    @Override
    public final void handle(LogRecord event) {
    }
}
