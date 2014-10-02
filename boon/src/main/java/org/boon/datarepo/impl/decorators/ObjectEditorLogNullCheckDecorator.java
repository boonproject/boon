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

package org.boon.datarepo.impl.decorators;


import org.boon.criteria.Update;
import org.boon.datarepo.ObjectEditor;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.boon.Exceptions.requireNonNull;


public class ObjectEditorLogNullCheckDecorator<KEY, ITEM> extends ObjectEditorDecoratorBase<KEY, ITEM> {

    Logger logger = Logger.getLogger( ObjectEditorLogNullCheckDecorator.class.getName() );
    Level level = Level.FINER;


    private boolean debug = false;

    void log( final String msg, final Object... items ) {

        if ( debug ) {
            System.out.printf( msg, items );
        }
        String message = String.format( msg, items );
        logger.log( level, message );

    }


    public void setLevel( Level level ) {
        this.level = level;
    }

    public void setDebug( boolean debug ) {
        this.debug = debug;
    }


    public void setLogger( Logger logger ) {
        this.logger = logger;
    }

    public ObjectEditorLogNullCheckDecorator() {


    }

    public ObjectEditorLogNullCheckDecorator( ObjectEditor oe ) {
        super( oe );

    }


    @Override
    public void put( ITEM item ) {
        requireNonNull( item, "item cannot be null" );
        log( "put (item=%s)", item );
        super.put( item );
    }


    @Override
    public boolean add( ITEM item ) {
        requireNonNull( item, "item cannot be null" );
        log( "addObject (item=%s)", item );
        return super.add( item );
    }

    @Override
    public ITEM get( KEY key ) {
        requireNonNull( key, "key cannot be null" );
        log( "get (key=%s)", key );

        return super.get( key );
    }


    @Override
    public void modify( ITEM item ) {
        requireNonNull( item, "item cannot be null" );
        log( "modify (item=%s)", item );

        super.modify( item );
    }

    @Override
    public void modify( ITEM item, String property, Object value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modifyByValue( ITEM item, String property, String value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modifyByValue( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, int value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, long value ) {

        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, char value ) {

        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, short value ) {

        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, byte value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, float value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, String property, double value ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );

        log( "modify (item=%s, property=%s, set=%s)", item, property, value );

        super.modify( item, property, value );
    }

    @Override
    public void modify( ITEM item, Update... values ) {
        requireNonNull( item, "item cannot be null" );
        requireNonNull( values, "value cannot be null" );

        log( "modify (item=%s, property=%s, update=%s)", item, values );

        super.modify( item, values );
    }

    @Override
    public void update( KEY key, String property, Object value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void updateByValue( KEY key, String property, String value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "updateByValue (key=%s, property=%s, set=%s)", key, property, value );

        super.updateByValue( key, property, value );
    }

    @Override
    public void update( KEY key, String property, int value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );
        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, long value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, char value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, short value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, byte value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, float value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, String property, double value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "update (key=%s, property=%s, set=%s)", key, property, value );

        super.update( key, property, value );
    }

    @Override
    public void update( KEY key, Update... values ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( values, "values cannot be null" );

        log( "update (key=%s, update=%s)", key, values );

        super.update( key, values );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, Object compare, Object value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, int compare, int value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, long compare, long value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, char compare, char value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, short compare, short value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, byte compare, byte value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, float compare, float value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndUpdate( KEY key, String property, double compare, double value ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        requireNonNull( value, "value cannot be null" );
        log( "compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value );

        return super.compareAndUpdate( key, property, compare, value );
    }

    @Override
    public boolean compareAndIncrement( KEY key, String property, int compare ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        log( "compareAndIncrement (key=%s, property=%s, compare=%s)", key, property, compare );

        return super.compareAndIncrement( key, property, compare );
    }

    @Override
    public boolean compareAndIncrement( KEY key, String property, long compare ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        log( "compareAndIncrement (key=%s, property=%s, compare=%s)", key, property, compare );

        return super.compareAndIncrement( key, property, compare );
    }

    @Override
    public boolean compareAndIncrement( KEY key, String property, short compare ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        log( "compareAndIncrement (key=%s, property=%s, set=%s)", key, property, compare );

        return super.compareAndIncrement( key, property, compare );
    }

    @Override
    public boolean compareAndIncrement( KEY key, String property, byte compare ) {
        requireNonNull( key, "key cannot be null" );
        requireNonNull( property, "property cannot be null" );
        requireNonNull( compare, "compare cannot be null" );
        log( "compareAndIncrement (key=%s, property=%s, set=%s)", key, property, compare );

        return super.compareAndIncrement( key, property, compare );
    }
}
