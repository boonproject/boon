package org.boon.core;

/* Generic event handler.
*  @author Rick Hightower
*/
public interface Handler<E> {

    void handle(E event);
}
