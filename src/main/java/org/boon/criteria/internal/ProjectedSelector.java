package org.boon.criteria.internal;


import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccess;

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
        return new Selector( join( '.', "max", fieldName ) ) {
            Comparable max;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                Comparable value = ( Comparable ) fields.get( fieldName ).getValue( item );

                if ( max == null ) {
                    max = value;
                }

                if ( value.compareTo( max ) > 0 ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                max = null;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, max );
                }
            }
        };
    }


    public static Selector min( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
            Comparable min;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                Comparable value = ( Comparable ) fields.get( fieldName ).getValue( item );

                if ( min == null ) {
                    min = value;
                }

                if ( value.compareTo( min ) < 0 ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                min = null;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, min );
                }
            }
        };
    }


    public static Selector sum( final String fieldName ) {
        return new Selector( join( '.', "sum", fieldName ) ) {
            long sum = 0;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {

                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.intgr ) {
                    int value = field.getInt( item );
                    sum += value;
                } else {
                    Integer ovalue = toInt( field.getValue( item ) );
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, sum );
                }
            }
        };
    }

    public static Selector sumFloat( final String fieldName ) {
        return new Selector( join( '.', "sum", fieldName ) ) {
            double sum = 0;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {

                FieldAccess field = fields.get( fieldName );
                if ( field.type() == Typ.flt ) {
                    float value = field.getFloat( item );
                    sum += value;
                } else {
                    Float ovalue = toFloat( field.getValue( item ) );
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, sum );
                }

            }
        };
    }


    public static Selector maxInt( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
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
            public void handleStart( List<? extends Object> results ) {
                max = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, max );
                }
            }
        };
    }

    public static Selector maxLong( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
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
            public void handleStart( List<? extends Object> results ) {
                max = Long.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, max );
                }

            }
        };
    }

    public static Selector minInt( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
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
            public void handleStart( List<? extends Object> results ) {
                min = Integer.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, min );
                }

            }
        };
    }

    public static Selector minLong( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
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
            public void handleStart( List<? extends Object> results ) {
                min = Long.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, min );
                }

            }
        };
    }

    public static Selector maxFloat( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
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
            public void handleStart( List<? extends Object> results ) {
                max = Float.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, max );
                }
            }
        };
    }

    public static Selector minFloat( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {

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
            public void handleStart( List<? extends Object> results ) {
                min = Float.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( this.name, min );
                }

            }
        };
    }

    public static Selector maxDouble( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
            double max = Double.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                double value = fields.get( fieldName ).getDouble( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                max = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, max );
                }
            }
        };
    }

    public static Selector minDouble( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
            double min = Double.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                double value = fields.get( fieldName ).getDouble( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                min = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, min );
                }
            }
        };
    }

    public static Selector minShort( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
            short min = Short.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                short value = fields.get( fieldName ).getShort( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                min = Short.MAX_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, min );
                }
            }
        };
    }

    public static Selector maxShort( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
            short max = Short.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                short value = fields.get( fieldName ).getShort( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                max = Short.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, max );
                }
            }
        };
    }

    public static Selector maxByte( final String fieldName ) {
        return new Selector( join( '.', "max", fieldName ) ) {
            byte max = Byte.MIN_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                byte value = fields.get( fieldName ).getByte( item );
                if ( value > max ) {
                    max = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                max = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, max );
                }
            }
        };
    }


    public static Selector minByte( final String fieldName ) {
        return new Selector( join( '.', "min", fieldName ) ) {
            byte min = Byte.MAX_VALUE;

            @Override
            public void handleRow( int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields ) {
                byte value = fields.get( fieldName ).getByte( item );
                if ( value < min ) {
                    min = value;
                }
            }

            @Override
            public void handleStart( List<? extends Object> results ) {
                min = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete( List<Map<String, Object>> rows ) {
                if ( rows.size() > 0 ) {
                    rows.get( 0 ).put( name, min );
                }
            }
        };
    }

}
