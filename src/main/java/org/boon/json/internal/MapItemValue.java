package org.boon.json.internal;

import java.util.Map;

import static org.boon.Exceptions.die;

public class MapItemValue implements Map.Entry<String, Value> {

    Value name;
    Value value;

    private String key = null;

    private static final boolean internKeys = Boolean.parseBoolean ( System.getProperty ( "org.boon.json.implementation.internKeys", "true" ) );


<<<<<<< HEAD
    public MapItemValue ( Value name, Value value ) {
=======
    public MapItemValue( Value name, Value value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.name = name;
        this.value = value;

    }

    @Override
<<<<<<< HEAD
    public String getKey () {
=======
    public String getKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( key == null ) {
            if ( internKeys ) {
                key = name.toString ();
            } else {
                key = name.toString ().intern ();
            }
        }
        return key;
    }

    @Override
    public Value getValue () {
        return value;
    }

    @Override
<<<<<<< HEAD
    public Value setValue ( Value value ) {
=======
    public Value setValue( Value value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        die ( "not that kind of Entry" );
        return null;
    }

    public Value name () {
        return name;
    }

    public void name ( Value name ) {
        this.name = name;
    }

    public void value ( Value value ) {
        this.value = value;
    }
}
