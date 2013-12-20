package com.examples.security.model;

public class Subject {

    public final String name;


    public Subject () {
        name = "ROOT";
    }


    public Subject ( String name ) {
        this.name = name;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Subject ) ) return false;

        Subject subject = ( Subject ) o;

        if ( name != null ? !name.equals ( subject.name ) : subject.name != null ) return false;

        return true;
    }

    @Override
    public int hashCode () {
        return name != null ? name.hashCode () : 0;
    }
}
