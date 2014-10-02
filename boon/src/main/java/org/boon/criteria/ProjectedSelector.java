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


import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.boon.Lists.list;
import static org.boon.Str.join;
import static org.boon.core.Conversions.*;

public abstract class ProjectedSelector extends Selector {

    public static List<ProjectedSelector> projections( ProjectedSelector... projections ) {
        return list( projections );
    }


    public static Selector max( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            Comparable max;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                Comparable value = ( Comparable ) this.getPropertyValue(item, fields);
                if ( max == null ) {
                    max = value;
                }

                if ( value.compareTo( max ) > 0 ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = null;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, max );
                }
            }
        };
    }


    public static Selector min( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            Comparable min;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                Comparable value = ( Comparable ) this.getPropertyValue(item, fields);

                if ( min == null ) {
                    min = value;
                }

                if ( value.compareTo( min ) < 0 ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = null;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, min );
                }
            }
        };
    }


    public static Selector sum( final String fieldName ) {
        return new Selector( fieldName, join( '.', "sum", fieldName ) ) {
            long sum = 0;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {

                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.intgr ) {
                    int value = field.getInt( item );
                    sum += value;
                } else {

                    Comparable value = ( Comparable ) this.getPropertyValue(item, fields);

                    Integer ovalue = toInt( value );
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart( Collection<?> results ) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, sum );
                }
            }
        };
    }

    public static Selector sumFloat( final String fieldName ) {
        return new Selector( fieldName, join( '.', "sum", fieldName ) ) {
            double sum = 0;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {

                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.flt ) {
                    float value = field.getFloat( item );
                    sum += value;
                } else {
                    Comparable value = ( Comparable ) this.getPropertyValue(item, fields);

                    Float ovalue = toFloat( value );
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart( Collection<?> results ) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, sum );
                }

            }
        };
    }


    public static Selector maxInt( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            int max = Integer.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.intgr ) {
                    int value = field.getInt( item );
                    if ( value > max ) {
                        max = value;
                    }
                } else {
                    Integer ovalue = toInt( field.getValue( item ) );
                    if ( ovalue > max ) {
                        max = ovalue;
                    }

                }


            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, max );
                }
            }
        };
    }

    public static Selector maxLong( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            long max = Long.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.lng ) {
                    long value = field.getLong( item );
                    if ( value > max ) {
                        max = value;
                    }
                } else {
                    Long ovalue = toLong( field.getValue( item ) );
                    if ( ovalue > max ) {
                        max = ovalue;
                    }

                }


            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Long.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, max );
                }

            }
        };
    }

    public static Selector minInt( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            int min = Integer.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.intgr ) {
                    int value = field.getInt( item );
                    if ( value < min ) {
                        min = value;
                    }
                } else {
                    Integer ovalue = toInt( field.getValue( item ) );
                    if ( ovalue < min ) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Integer.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, min );
                }

            }
        };
    }

    public static Selector minLong( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            long min = Long.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.lng ) {
                    long value = field.getLong( item );
                    if ( value < min ) {
                        min = value;
                    }
                } else {
                    Long ovalue = toLong( field.getValue( item ) );
                    if ( ovalue < min ) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Long.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, min );
                }

            }
        };
    }

    public static Selector maxFloat( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            float max = Float.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.flt ) {
                    float value = field.getFloat( item );
                    if ( value > max ) {
                        max = value;
                    }
                } else {
                    Float ovalue = toFloat( field.getValue( item ) );
                    if ( ovalue > max ) {
                        max = ovalue;
                    }

                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Float.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, max );
                }
            }
        };
    }

    public static Selector minFloat( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {

            float min = Float.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                FieldAccess field = fields.get( fieldName );

                if ( field.type() == Typ.flt ) {
                    float value = field.getFloat( item );
                    if ( value > min ) {
                        min = value;
                    }
                } else {
                    Float ovalue = toFloat( field.getValue( item ) );
                    if ( ovalue > min ) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Float.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.alias, min );
                }

            }
        };
    }

    public static Selector maxDouble( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            double max = Double.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                double value = fields.get( fieldName ).getDouble( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, max );
                }
            }
        };
    }

    public static Selector minDouble( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            double min = Double.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                double value = fields.get( fieldName ).getDouble( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, min );
                }
            }
        };
    }

    public static Selector minShort( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            short min = Short.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                short value = fields.get( fieldName ).getShort( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Short.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, min );
                }
            }
        };
    }

    public static Selector maxShort( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            short max = Short.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                short value = fields.get( fieldName ).getShort( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Short.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, max );
                }
            }
        };
    }

    public static Selector maxByte( final String fieldName ) {
        return new Selector( fieldName, join( '.', "max", fieldName ) ) {
            byte max = Byte.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                byte value = fields.get( fieldName ).getByte( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                max = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, max );
                }
            }
        };
    }


    public static Selector minByte( final String fieldName ) {
        return new Selector( fieldName, join( '.', "min", fieldName ) ) {
            byte min = Byte.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                byte value = fields.get( fieldName ).getByte( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( Collection<?> results ) {
                min = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( alias, min );
                }
            }
        };
    }

}
