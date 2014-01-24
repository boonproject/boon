package org.boon;

public interface Handler<E> {

    void handle(E event);
}
