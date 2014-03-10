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

package org.boon.criteria;


import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.Lists.list;
import static org.boon.Boon.joinBy;

public abstract class Selector {
    protected String name;

    public Selector() {
    }

    public Selector( String n ) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public static List<Selector> selects( Selector... selects ) {
        return list( selects );
    }

    public static Selector select( final String name ) {
        return new Selector( name ) {
            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                row.put( this.name, fields.get( this.name ).getValue( item ) );
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector toStr( final String name ) {
        return new Selector( name + ".toString()" ) {
            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                Object selected = fields.get( this.name ).getValue( item );
                row.put( this.name, selected == null ? "" : selected.toString() );
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector toStr() {
        return new Selector( "toString()" ) {
            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                row.put( this.name, item.toString() );
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector select( final String... path ) {
        return new Selector( joinBy( '.', path ) ) {
            int index = 0;

            @Override
            public void handleRow( int rowNum, Map<String, Object> row,
                                   Object item, Map<String, FieldAccess> fields ) {

                Object o = BeanUtils.getPropByPath( item, path );


                row.put( this.name, o );
            }


            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector toStr( final String... path ) {
        return new Selector( joinBy( '.', path ) + ".toString()" ) {
            int index = 0;

            @Override
            public void handleRow( int rowNum, Map<String, Object> row,
                                   Object item, Map<String, FieldAccess> fields ) {

                Object o = BeanUtils.getPropByPath( item, path );


                row.put( this.name, o == null ? "" : o.toString() );
            }


            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector selectPropPath( final String... path ) {
        return new Selector( joinBy( '.', path ) ) {
            @Override
            public void handleRow( int rowNum, Map<String, Object> row,
                                   Object item, Map<String, FieldAccess> fields ) {

                Object o = BeanUtils.getPropByPath( item, path );

                row.put( this.name, o );
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }

    public static Selector rowId() {

        return new Selector( "rowId" ) {
            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                row.put( name, index );
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
            }
        };
    }


    public static <ITEM> List<Map<String, Object>> performSelection( List<Selector> selectors, List<ITEM> results, Map<String, FieldAccess> fields ) {
        List<Map<String, Object>> rows = new ArrayList<>( results.size() );


        for ( Selector s : selectors ) {
            s.handleStart( results );
        }


        int index = 0;
        for ( ITEM item : results ) {
            Map<String, Object> row = new LinkedHashMap<>();
            for ( Selector s : selectors ) {
                s.handleRow( index, row, item, fields );
            }
            index++;
            rows.add( row );
        }

        for ( Selector s : selectors ) {
            s.handleComplete( rows );
        }

        return rows;
    }


    public abstract void handleRow( int index, Map<String, Object> row,
                                    Object item,
                                    Map<String, FieldAccess> fields );

    public abstract void handleStart( List<? extends Object> results );

    public abstract void handleComplete( List<Map<String, Object>> rows );
}
