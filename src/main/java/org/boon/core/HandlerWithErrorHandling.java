package org.boon.core;

public interface HandlerWithErrorHandling<T> extends Handler<T> {

    Handler<Throwable> errorHandler();
}
