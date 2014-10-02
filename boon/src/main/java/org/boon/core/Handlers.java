package org.boon.core;

/**
 * Created by Richard on 10/1/14.
 */
public class Handlers {

    public static <T>  Handler<T> handler(final Handler<T> handler, final Handler<Throwable> errorHandler) {
        return new HandlerWithErrorHandling<T>() {

            @Override
            public Handler<Throwable> errorHandler() {
                return errorHandler;
            }

            @Override
            public void handle(T event) {
                handler.handle(event);
            }
        };
    }


}
