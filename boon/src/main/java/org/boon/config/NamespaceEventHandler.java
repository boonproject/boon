/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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


    private final MetaConfigEvents events;
    private String namespace;

    private List<String> include;
    boolean continueParse = true;

    boolean inMeta = false;

    public List<String> include() {
        return include == null ? java.util.Collections.EMPTY_LIST : include;
    }

    public NamespaceEventHandler( String namespace, MetaConfigEvents events ) {
        this.namespace = namespace;
        this.events = events;
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
    public boolean objectField( int index, Map<String, Object> map, CharSequence name, Object value ) {

        if ( inMeta && name!=null && name.toString().equals( "namespace" )
                && value instanceof CharSequence && !value.toString().equals( namespace )) {

             return continueParse=false;
        }

        if (name!=null && name.toString().equals( "META" ) ) {

            Map<String, Object> meta = toMap( value );
            if (meta.containsKey( "include" )) {
              include = toList( meta.get( "include" ) );
              puts ("include", include);
            }
            inMeta = false;
            if (events!=null) {
                continueParse = events.parsedMeta( meta );
            }
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
