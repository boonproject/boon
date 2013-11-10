package org.boon.primitive;

import java.util.Objects;

public class ByteBuf {

    protected int capacity = 16;


    protected int length = 0;

    protected byte [] buffer;


    public static ByteBuf createExact( final int capacity) {
        return new ByteBuf(capacity) {
            public void add(byte[] chars) {
                Byt._idx( buffer, length, chars );
                length += chars.length;
            }
        };
    }

    public static ByteBuf create( int capacity) {
        return new ByteBuf(capacity);
    }

    public static ByteBuf create( byte [] buffer) {
        ByteBuf buf = new ByteBuf(buffer.length);
        buf.buffer = buffer;
        return buf;
    }

    protected ByteBuf(int capacity)  {
        this.capacity = capacity;
        init();
    }


    protected ByteBuf()  {
        init();
    }

    private void init() {
        buffer = new byte[capacity];
    }


    public void add(String str) {
            this.add( Byt.bytes(str) );

    }


    public void add(int value) {

        if (4 + length < capacity) {
            Byt.intTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer, buffer.length * 2 + 4 );
            capacity = buffer.length;

            Byt.intTo(buffer, length, value);
        }

        length += 4;


    }


    public void add(float value) {

        if (4 + length < capacity) {
            Byt.floatTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 4 );
            capacity = buffer.length;

            Byt.floatTo(buffer, length, value);
        }

        length += 4;


    }



    public void add(char value) {

        if (2 + length < capacity) {
            Byt.charTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 2 );
            capacity = buffer.length;

            Byt.charTo(buffer, length, value);
        }

        length += 2;


    }



    public void add(short value) {

        if (2 + length < capacity) {
            Byt.shortTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 2 );
            capacity = buffer.length;

            Byt.shortTo(buffer, length, value);
        }

        length += 2;


    }

    public void addByte(int value) {
         this.add ( (byte) value );
    }

    public void add(byte value) {

        if (1 + length < capacity) {
            Byt.idx(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.idx(buffer, length, value);
        }

        length += 1;


    }

    public void add(long value) {

        if (8 + length < capacity) {
            Byt.longTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 8 );
            capacity = buffer.length;

            Byt.longTo(buffer, length, value);
        }

        length += 8;

    }

    public void addUnsignedInt(long value) {

        if (4 + length < capacity) {
            Byt.unsignedIntTo ( buffer, length, value );
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 4 );
            capacity = buffer.length;

            Byt.unsignedIntTo ( buffer, length, value );
        }

        length += 4;

    }

    public void add(double value) {

        if (8 + length < capacity) {
            Byt.doubleTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + 8 );
            capacity = buffer.length;

            Byt.doubleTo(buffer, length, value);
        }

        length += 8;

    }


    public void add(byte[] array) {
        if (array.length + length < capacity) {
            Byt._idx( buffer, length, array );
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + array.length );
            capacity = buffer.length;

            Byt._idx(buffer, length, array);

        }
        length += array.length;
    }


    public void add(byte[] array, final int length) {
        if (array.length + length < capacity) {
            Byt._idx( buffer, length, array, length );
        } else {
            buffer = Byt.grow(buffer,  buffer.length * 2 + length );
            capacity = buffer.length;

            Byt._idx(buffer, length, array, length);

        }
        this.length += length;
    }

    public byte[] readAndReset () {
        byte [] bytes = this.buffer;
        this.buffer = null;
        return bytes;
    }

    public byte[] readForRecycle () {
        this.length = 0;
        return  this.buffer;
    }

    public int len() {
        return length;
    }

    public void addUrlEncodedByteArray ( byte[] value ) {



        final byte[] encoded = new byte [2];

        for (int index = 0; index < value.length; index++) {
            int i = value[index];

            if ( i >= 'a' && i <= 'z' ) {
                this.addByte ( i );
            } else if ( i >= 'A' && i <= 'Z' ) {
                this.addByte ( i );
            } else if ( i >= '0' && i <= '9' ) {
                this.addByte ( i );
            } else if ( i == '_' || i == '-' || i == '.' || i == '*') {
                this.addByte ( i );
            } else if ( i == ' ') {
                this.addByte ( '+' );
            } else {
                encodeByteIntoTwoAsciiCharBytes(i, encoded);
                this.addByte ( '%' );
                this.addByte ( encoded [0] );
                this.addByte ( encoded [1] );
            }

        }
    }



    /**
     * Encodes a single nibble.
     *
     * @param decoded the nibble to encode.
     *
     * @return the encoded half octet.
     */
    protected static int encodeNibbleToHexAsciiCharByte(final int decoded) {

        switch (decoded) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08:
            case 0x09:
                return decoded + 0x30; // 0x30('0') - 0x39('9')
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return decoded + 0x57; // 0x41('a') - 0x46('f')
            default:
                throw new IllegalArgumentException("illegal half: " + decoded);
        }
    }


    /**
     * Encodes a single octet into two nibbles.
     *
     * @param decoded the octet to encode.
     * @param encoded the array to which each encoded nibbles are written.
     */
    protected static void encodeByteIntoTwoAsciiCharBytes(final int decoded, final byte[] encoded) {

        if (encoded == null) {
            throw new IllegalArgumentException("null encoded");
        }

        if (encoded.length < 2) {
            // not required
            throw new IllegalArgumentException(
                    "encoded.length(" + encoded.length + ") < 2");
        }

        encoded[0] = (byte) encodeNibbleToHexAsciiCharByte((decoded >> 4) & 0x0F);
        encoded[1] = (byte) encodeNibbleToHexAsciiCharByte(decoded & 0x0F);
    }

}
