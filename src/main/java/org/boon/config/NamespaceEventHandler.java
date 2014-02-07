package org.boon.config;

import org.boon.core.Value;
import org.boon.json.JsonParserEvents;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;


/**
 * Created by Richard on 2/5/14.
 */
public class NamespaceEventHandler implements JsonParserEvents {


    private String namespace;

    private List<String> include;
    boolean continueParse = true;

    boolean inMeta = false;

    public List<String> include() {
        return include == null ? java.util.Collections.EMPTY_LIST : include;
    }

    public NamespaceEventHandler( String namespace ) {
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

        if ( inMeta && name!=null && name.toString().equals( "namespace" )
                && field instanceof CharSequence && !field.toString().equals( namespace )) {

             return continueParse=false;
        }

        if (inMeta && name!=null && name.toString().equals( "META" ) ) {

            Map<String, Object> meta = toMap( field );
            if (meta.containsKey( "include" )) {
              include = toList( meta.get( "include" ) );
              puts ("include", include);
            }
            inMeta = false;
        }

        return continueParse;
    }

    private List<String> toList( Object include ) {
        if (include instanceof Value )  {
            Value value = ( Value ) include;
            return (List) value.toValue();
        } else {
            return (List) include;
        }

    }

    private Map<String, Object> toMap( Object field ) {
        if (field instanceof Value )  {
            Value value = ( Value ) field;
            return (Map) value.toValue();
        } else {
            return (Map) field;
        }
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
