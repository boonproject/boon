package org.boon.json;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 2/5/14.
 */
public class JsonCreatorEventHandler implements JsonParserEvents {


    private String namespace;
    boolean continueParse = true;

    boolean inMeta = false;

    public JsonCreatorEventHandler( String namespace ) {
        this.namespace = namespace;
    }

    @Override
    public boolean objectStart( int index ) {
        return continueParse;
    }

    @Override
    public boolean objectEnd( int index, Map<String, Object> object ) {

        return continueParse;
    }

    @Override
    public boolean objectFieldName( int index, Map<String, Object> map, CharSequence name ) {
        if (name!=null && name.toString().equals( "META" ) ) {
            inMeta = true;
        }
        return continueParse;
    }

    @Override
    public boolean objectField( int index, Map<String, Object> map, CharSequence name, Object field ) {
        puts (index, name, field);

        if ( inMeta && name!=null && name.toString().equals( "namespace" )
                && field instanceof CharSequence && !field.toString().equals( namespace )) {

             return continueParse=false;
        }

        if (inMeta && name!=null && name.toString().equals( "META" ) ) {
            inMeta = false;
        }

        return continueParse;
    }

    @Override
    public boolean arrayStart( int index ) {
        return continueParse;
    }

    @Override
    public boolean arrayEnd( int index, List<Object> list ) {
        return continueParse;
    }

    @Override
    public boolean arrayItem( int index, List<Object> list, Object item ) {
        return continueParse;
    }

    @Override
    public boolean number( int startIndex, int endIndex, Number number ) {
        return continueParse;
    }

    @Override
    public boolean string( int startIndex, int endIndex, CharSequence string ) {
        return continueParse;
    }

    @Override
    public boolean bool( int endIndex, boolean value ) {
        return continueParse;
    }

    @Override
    public boolean nullValue( int endIndex ) {
        return continueParse;
    }
}
