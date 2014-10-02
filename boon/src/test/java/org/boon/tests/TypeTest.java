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

package org.boon.tests;

import org.boon.criteria.ObjectFilter;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.boon.datarepo.modification.ModificationEvent;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.tests.model.ClassForTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypeTest {
    Repo<String, ClassForTest> repo;

    @Before
    public void setUp() throws Exception {
        repo = Repos.builder().primaryKey( "strings" )
                .searchIndex( "ints" ).searchIndex( "floats" )
                .searchIndex( "doubles" ).searchIndex( "shorts" )
                .searchIndex( "chars" ).searchIndex( "longs" )
                .searchIndex( "chars" ).searchIndex( "bytes" )
                .events( new ModificationListener() {
                    @Override
                    public void modification( ModificationEvent event ) {
                        System.out.println( event );
                    }
                } )
                .build( String.class, ClassForTest.class );


        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );


    }

    public void setUp2() throws Exception {
        repo = Repos.builder().primaryKey( "strings" ).usePropertyForAccess( true )
                .searchIndex( "ints" ).searchIndex( "floats" )
                .searchIndex( "doubles" ).searchIndex( "shorts" )
                .searchIndex( "chars" ).searchIndex( "longs" )
                .searchIndex( "chars" ).searchIndex( "bytes" )
                .events( new ModificationListener() {
                    @Override
                    public void modification( ModificationEvent event ) {
                        System.out.println( event );
                    }
                } )
                .build( String.class, ClassForTest.class );


        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );
    }

    public void setUp3() throws Exception {
        repo = Repos.builder().primaryKey( "strings" ).useUnsafe( false )
                .searchIndex( "ints" ).searchIndex( "floats" )
                .searchIndex( "doubles" ).searchIndex( "shorts" )
                .searchIndex( "chars" ).searchIndex( "longs" )
                .searchIndex( "chars" ).searchIndex( "bytes" )
                .events( new ModificationListener() {
                    @Override
                    public void modification( ModificationEvent event ) {
                        System.out.println( event );
                    }
                } )
                .build( String.class, ClassForTest.class );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

    }

    public void setUp4() throws Exception {
        repo = Repos.builder().primaryKey( "strings" ).useUnsafe( false )
                .events( new ModificationListener() {
                    @Override
                    public void modification( ModificationEvent event ) {
                        System.out.println( event );
                    }
                } )
                .build( String.class, ClassForTest.class );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

        repo.add( new ClassForTest() );

    }


    @Test
    public void testQuery3() throws Exception {
        setUp4();
        testQuery();
    }

    @Test
    public void testQuery2() throws Exception {
        setUp3();
        testQuery();
    }

    @Test
    public void testQuery1() throws Exception {
        setUp2();
        testQuery();
    }

    @Test
    public void testQuery() throws Exception {
        repo.query( ObjectFilter.eq( "ints", 1 ), ObjectFilter.eq( "floats", 1.1f ),
                ObjectFilter.eq( "doubles", 1.1 ), ObjectFilter.eq( "shorts", ( short ) 1 ),
                ObjectFilter.eq( "chars", 'c' ), ObjectFilter.eq( "longs", 1L ),
                ObjectFilter.eq( "bytes", ( byte ) 1 ) );

        repo.query( ObjectFilter.gt( "ints", 1 ), ObjectFilter.gt( "floats", 1.1f ),
                ObjectFilter.gt( "doubles", 1.1 ), ObjectFilter.gt( "shorts", ( short ) 1 ),
                ObjectFilter.gt( "chars", 'c' ), ObjectFilter.gt( "longs", 1L ),
                ObjectFilter.gt( "bytes", ( byte ) 1 ) );

        repo.query( ObjectFilter.lt( "ints", 1 ), ObjectFilter.lt( "floats", 1.1f ),
                ObjectFilter.lt( "doubles", 1.1 ), ObjectFilter.lt( "shorts", ( short ) 1 ),
                ObjectFilter.lt( "chars", 'c' ), ObjectFilter.lt( "longs", 1L ),
                ObjectFilter.lt( "bytes", ( byte ) 1 ) );


        repo.query( ObjectFilter.gte( "ints", 1 ), ObjectFilter.gte( "floats", 1.1f ),
                ObjectFilter.gte( "doubles", 1.1 ), ObjectFilter.gte( "shorts", ( short ) 1 ),
                ObjectFilter.gte( "chars", 'c' ), ObjectFilter.gte( "longs", 1L ),
                ObjectFilter.gte( "bytes", ( byte ) 1 ) );

        repo.query( ObjectFilter.lte( "ints", 1 ), ObjectFilter.lte( "floats", 1.1f ),
                ObjectFilter.lte( "doubles", 1.1 ), ObjectFilter.lte( "shorts", ( short ) 1 ),
                ObjectFilter.lte( "chars", 'c' ), ObjectFilter.lte( "longs", 1L ),
                ObjectFilter.lte( "bytes", ( byte ) 1 ) );

        repo.query( ObjectFilter.in( "ints", 1, 2, 3 ), ObjectFilter.in( "floats", 1.1f, 1.2f ),
                ObjectFilter.in( "doubles", 1.1, 2.2 ), ObjectFilter.in( "shorts", ( short ) 1, ( short ) 2, ( short ) 3 ),
                ObjectFilter.in( "chars", 'a', 'b', 'c' ), ObjectFilter.in( "longs", 1L, 2L, 3L ),
                ObjectFilter.in( "bytes", ( byte ) 1, ( byte ) 2, ( byte ) 3 ) );

        repo.query( ObjectFilter.between( "ints", 1, 2 ), ObjectFilter.between( "floats", 1.1f, 1.2f ),
                ObjectFilter.between( "doubles", 1.1, 2.2 ), ObjectFilter.between( "shorts", ( short ) 1, ( short ) 2 ),
                ObjectFilter.between( "chars", 'a', 'b' ), ObjectFilter.between( "longs", 1L, 2L ),
                ObjectFilter.between( "bytes", ( byte ) 1, ( byte ) 2 ) );

        repo.query( ObjectFilter.notIn( "ints", 1, 2, 3 ), ObjectFilter.notIn( "floats", 1.1f, 1.2f ),
                ObjectFilter.notIn( "doubles", 1.1, 2.2 ), ObjectFilter.notIn( "shorts", 1, 2, 3 ),
                ObjectFilter.notIn( "chars", 'a', 'b', 'c' ), ObjectFilter.notIn( "longs", 1L, 2L, 3L ),
                ObjectFilter.notIn( "bytes", 1, 2, 3 ) );

        repo.query( ObjectFilter.startsWith( "strings", "foo" ) );

        repo.query( ObjectFilter.endsWith( "strings", "foo" ) );

    }

    @Test
    public void testMods() throws Exception {
        ClassForTest forTest = new ClassForTest();
        String key = "AAAA";
        forTest.setStrings( key );
        repo.add( forTest );

        repo.modify( forTest, "ints", 1 );
        int i = repo.getInt( forTest, "ints" );
        assertEquals( 1, i );

        repo.modify( forTest, "shorts", ( short ) 1 );
        short s = repo.getShort( forTest, "shorts" );
        assertEquals( 1, s );

        repo.modify( forTest, "bytes", ( byte ) 1 );
        byte b = repo.getByte( forTest, "bytes" );
        assertEquals( 1, b );

        repo.modify( forTest, "longs", 1l );
        long l = repo.getLong( forTest, "longs" );
        assertEquals( 1, l );


        repo.modify( forTest, "doubles", 1.0 );
        double d = repo.getDouble( forTest, "doubles" );
        assertEquals( 1, d, 0.1d );

        repo.modify( forTest, "floats", 1.0f );
        float f = repo.getFloat( forTest, "floats" );
        assertEquals( 1, f, 0.1f );

        repo.modify( forTest, "chars", 'a' );
        char c1 = repo.getChar( forTest, "chars" );
        assertEquals( 'a', c1 );


        repo.update( key, "ints", 2 );
        i = repo.readInt( key, "ints" );
        assertEquals( 2, i );

        repo.update( key, "shorts", ( short ) 2 );
        s = repo.readShort( key, "shorts" );
        assertEquals( 2, s );

        repo.update( key, "bytes", ( byte ) 2 );
        b = repo.readByte( key, "bytes" );
        assertEquals( 2, b );

        repo.update( key, "longs", 2l );
        l = repo.readLong( key, "longs" );
        assertEquals( 2, l );


        repo.update( key, "doubles", 2.0 );
        d = repo.readDouble( key, "doubles" );
        assertEquals( 2, d, 0.1d );

        repo.update( key, "floats", 2.0f );
        f = repo.readFloat( key, "floats" );
        assertEquals( 2, f, 0.1f );

        repo.update( key, "chars", 'b' );
        c1 = repo.readChar( key, "chars" );
        assertEquals( 'b', c1 );

        //--------------

        repo.compareAndUpdate( key, "ints", 2, 3 );
        i = repo.readInt( key, "ints" );
        assertEquals( 3, i );

        repo.compareAndUpdate( key, "shorts", ( short ) 2, ( short ) 3 );
        s = repo.readShort( key, "shorts" );
        assertEquals( 3, s );

        repo.compareAndUpdate( key, "bytes", ( byte ) 2, ( byte ) 3 );
        b = repo.readByte( key, "bytes" );
        assertEquals( 3, b );

        repo.compareAndUpdate( key, "longs", 2l, 3l );
        l = repo.readLong( key, "longs" );
        assertEquals( 3, l );


        repo.compareAndUpdate( key, "doubles", 2.0, 3.0 );
        d = repo.readDouble( key, "doubles" );
        assertEquals( 3, d, 0.1d );

        repo.compareAndUpdate( key, "floats", 2.0f, 3.0f );
        f = repo.readFloat( key, "floats" );
        assertEquals( 3, f, 0.1f );

        repo.compareAndUpdate( key, "chars", 'b', 'c' );
        c1 = repo.readChar( key, "chars" );
        assertEquals( 'c', c1 );

        //------------------

        repo.compareAndIncrement( key, "ints", 3 );
        i = repo.readInt( key, "ints" );
        assertEquals( 4, i );

        repo.compareAndIncrement( key, "shorts", ( short ) 3 );
        s = repo.readShort( key, "shorts" );
        assertEquals( 4, s );

        repo.compareAndIncrement( key, "bytes", ( byte ) 3 );
        b = repo.readByte( key, "bytes" );
        assertEquals( 4, b );

        repo.compareAndIncrement( key, "longs", 3l );
        l = repo.readLong( key, "longs" );
        assertEquals( 4, l );


    }
}
