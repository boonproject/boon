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

package org.boon.criteria.internal;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.util.Arrays;
import java.util.Map;

public abstract class Group extends Criteria {

    protected Criteria[] expressions;

    private final int hashCode;
    private String toString;

    private Grouping grouping = Grouping.AND;


    //TODO there is an opportunity to optimize this so Group holds on to fields for subgroups.
    @Override
    public void prepareForGroupTest( Map<String, FieldAccess> fields, Object owner ) {

    }

    @Override
    public void cleanAfterGroupTest() {

    }

    public Group( Grouping grouping, Criteria... expressions ) {
        this.grouping = grouping;
        this.expressions = expressions;
        hashCode = doHashCode();

    }

    private int doHashCode() {
        int result = expressions != null ? Arrays.hashCode( expressions ) : 0;
        result = 31 * result + ( grouping != null ? grouping.hashCode() : 0 );
        return result;

    }

    private String doToString() {

        if ( toString == null ) {


            CharBuf builder = CharBuf.create( 80 );
            builder.add( "{" );
            builder.add( "\"expressions\":" );
            builder.add( Arrays.toString( expressions ) );
            builder.add( ", \"grouping\":" );
            builder.add( String.valueOf( grouping ) );
            builder.add( '}' );
            toString = builder.toString();
        }
        return toString;

    }

    public Grouping getGrouping() {
        return grouping;
    }


    public Criteria[] getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Group group = ( Group ) o;

        if ( !Arrays.equals( expressions, group.expressions ) ) return false;
        if ( grouping != group.grouping ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return doToString();
    }

    public static class And extends Group {

        public And( Criteria... expressions ) {
            super( Grouping.AND, expressions );
        }


        @Override
        public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
            for ( Criteria c : expressions ) {
                c.prepareForGroupTest( fields, owner );
                if ( !c.test( owner ) ) {
                    return false;
                }
                c.cleanAfterGroupTest();
            }
            return true;
        }
    }

    public static class Or extends Group {

        public Or( Criteria... expressions ) {
            super( Grouping.OR, expressions );
        }

        @Override
        public void prepareForGroupTest( Map<String, FieldAccess> fields, Object owner ) {

        }

        @Override
        public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
            for ( Criteria c : expressions ) {
                c.prepareForGroupTest( fields, owner );
                if ( c.test( owner ) ) {
                    return true;
                }
                c.cleanAfterGroupTest();
            }
            return false;
        }
    }

}
