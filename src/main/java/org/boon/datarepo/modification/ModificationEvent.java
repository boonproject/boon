package org.boon.datarepo.modification;


public abstract class ModificationEvent<KEY, ITEM> {

    public static final String ROOT_PROPERTY = "ROOT";

    private KEY key;
    private ITEM item;
    private String property = ROOT_PROPERTY;

    private ModificationType type;

    public ModificationEvent() {

    }

    public ModificationEvent(KEY k, ITEM i, ModificationType t, String p) {
        key = k;
        item = i;
        type = t;
        if (p != null) {
            this.property = p;
        }
    }

    public ITEM getItem() {
        return item;
    }

    public KEY getKey() {
        return key;
    }


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
        return "ModificationEvent{" +
                "key=" + key +
                ", item=" + item +
                ", property='" + property + '\'' +
                ", type=" + type +
                '}';
    }

    static class ModficationEventImpl<KEY, ITEM> extends ModificationEvent<KEY, ITEM> {


        public ModficationEventImpl() {

        }

        public ModficationEventImpl(KEY k, ITEM i, ModificationType t, String p) {
            super(k, i, t, p);
        }

        @Override
        public boolean booleanValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public int intValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public short shortValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public char charValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public byte byteValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public long longValue() {
            throw new UnsupportedOperationException("not supported");
        }


        @Override
        public float floatValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public double doubleValue() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public String value() {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public Object objectValue() {
            throw new UnsupportedOperationException("not supported");
        }

    }


    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item,
            final String property, final boolean value) {
        return new ModficationEventImpl(key, item, type, property) {
            boolean v = value;

            public boolean booleanValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final Object value) {
        return new ModficationEventImpl(key, item, type, property) {
            Object v = value;

            public Object objectValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final String value) {
        return new ModficationEventImpl(key, item, type, property) {
            String v = value;

            public String value() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(final ModificationType type,
                                                                              final KEY key, final ITEM item, final String property, final byte value) {
        return new ModficationEventImpl(key, item, type, property) {
            byte v = value;

            public byte byteValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final short value) {
        return new ModficationEventImpl(key, item, type, property) {
            short v = value;

            public short shortValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final int value) {
        return new ModficationEventImpl(key, item, type, property) {
            int v = value;

            public int intValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final long value) {
        return new ModficationEventImpl(key, item, type, property) {
            long v = value;

            public long longValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final float value) {
        return new ModficationEventImpl(key, item, type, property) {
            float v = value;

            public float floatValue() {
                return v;
            }
        };
    }

    public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(
            final ModificationType type, final KEY key, final ITEM item, final String property, final double value) {
        return new ModficationEventImpl(key, item, type, property) {
            double v = value;

            public double doubleValue() {
                return v;
            }
        };
    }

}
