package org.boon.core.value;

import org.boon.core.Value;

import java.util.Map;

import static org.boon.Exceptions.die;

/** This holds a mapping from value key to value value to mazimize lazyness.
 *
 */
public class MapItemValue implements Map.Entry<String, Value> {

    final Value name;
    final Value value;

    private String key = null;

    private static final boolean internKeys = Boolean.parseBoolean( System.getProperty( "org.boon.json.implementation.internKeys", "false" ) );


    public MapItemValue( Value name, Value value ) {
        this.name = name;
        this.value = value;

    }

    @Override
    public String getKey() {
        if ( key == null ) {
            if ( internKeys ) {
                key = name.toString();
            } else {
                key = name.toString().intern();
            }
        }
        return key;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public Value setValue( Value value ) {
        die( "not that kind of Entry" );
        return null;
    }

}
