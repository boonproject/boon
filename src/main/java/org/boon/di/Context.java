package org.boon.di;


public interface Context extends Module {


    Object invoke( String objectName, String methodName, Object args);


    Object invokeOverload( String objectName, String methodName, Object args);



    Object invokeFromJson( String objectName, String methodName, String args);


    Object invokeOverloadFromJson( String objectName, String methodName, String args);

    Context add( Module module );

    Context remove( Module module );

    Context addFirst( Module module );

    Iterable<Module> children();

    void resolveProperties( Object o );

    void debug(  );
}