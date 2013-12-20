package org.boon.datarepo.modification;


public abstract class ModificationEvent<KEY, ITEM> {

    public static final String ROOT_PROPERTY = "ROOT";

    private KEY key;
    private ITEM item;
    private String property = ROOT_PROPERTY;

    private ModificationType type;

<<<<<<< HEAD
    public ModificationEvent () {
=======
    public ModificationEvent() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    }

    public ModificationEvent ( KEY k, ITEM i, ModificationType t, String p ) {
        key = k;
        item = i;
        type = t;
        if ( p != null ) {
            this.property = p;
        }
    }

<<<<<<< HEAD
    public ITEM getItem () {
        return item;
    }

    public KEY getKey () {
=======
    public ITEM getItem() {
        return item;
    }

    public KEY getKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return key;
    }


<<<<<<< HEAD
    public abstract boolean booleanValue ();

    public abstract int intValue ();

    public abstract short shortValue ();

    public abstract char charValue ();

    public abstract byte byteValue ();

    public abstract long longValue ();

    public abstract float floatValue ();

    public abstract double doubleValue ();

    public abstract Object objectValue ();

    public abstract String value ();

    @Override
    public String toString () {
=======
    public abstract boolean booleanValue();

    public abstract int intValue();

    public abstract short shortValue();

    public abstract char charValue();

    public abstract byte byteValue();

    public abstract long longValue();

    public abstract float floatValue();

    public abstract double doubleValue();

    public abstract Object objectValue();

    public abstract String value();

    @Override
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return "ModificationEvent{" +
                "key=" + key +
                ", item=" + item +
                ", property='" + property + '\'' +
                ", type=" + type +
                '}';
    }

    static class ModficationEventImpl<KEY, ITEM> extends ModificationEvent<KEY, ITEM> {


<<<<<<< HEAD
        public ModficationEventImpl () {
=======
        public ModficationEventImpl() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        }

        public ModficationEventImpl ( KEY k, ITEM i, ModificationType t, String p ) {
            super ( k, i, t, p );
        }

        @Override
<<<<<<< HEAD
        public boolean booleanValue () {
=======
        public boolean booleanValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public int intValue () {
=======
        public int intValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public short shortValue () {
=======
        public short shortValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public char charValue () {
=======
        public char charValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public byte byteValue () {
=======
        public byte byteValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public long longValue () {
=======
        public long longValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }


        @Override
<<<<<<< HEAD
        public float floatValue () {
=======
        public float floatValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public double doubleValue () {
=======
        public double doubleValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public String value () {
=======
        public String value() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

        @Override
<<<<<<< HEAD
        public Object objectValue () {
=======
        public Object objectValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            throw new UnsupportedOperationException ( "not supported" );
        }

    }


    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item,
            final String property, final boolean value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            boolean v = value;

<<<<<<< HEAD
            public boolean booleanValue () {
=======
            public boolean booleanValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final Object value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            Object v = value;

<<<<<<< HEAD
            public Object objectValue () {
=======
            public Object objectValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final String value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            String v = value;

<<<<<<< HEAD
            public String value () {
=======
            public String value() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification ( final ModificationType type,
                                                                                final KEY key, final ITEM item, final String property, final byte value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            byte v = value;

<<<<<<< HEAD
            public byte byteValue () {
=======
            public byte byteValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final short value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            short v = value;

<<<<<<< HEAD
            public short shortValue () {
=======
            public short shortValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final int value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            int v = value;

<<<<<<< HEAD
            public int intValue () {
=======
            public int intValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final long value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            long v = value;

<<<<<<< HEAD
            public long longValue () {
=======
            public long longValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final float value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            float v = value;

<<<<<<< HEAD
            public float floatValue () {
=======
            public float floatValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification (
            final ModificationType type, final KEY key, final ITEM item, final String property, final double value ) {
        return new ModficationEventImpl ( key, item, type, property ) {
            double v = value;

<<<<<<< HEAD
            public double doubleValue () {
=======
            public double doubleValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return v;
            }
        };
    }

}
