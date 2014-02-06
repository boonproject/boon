package org.boon.di;


public interface Context extends Module {
    Context add( Module module );

    Context remove( Module module );

    Context addFirst( Module module );

    Iterable<Module> children();

    void resolveProperties( Object o );

    void debug(  );
}