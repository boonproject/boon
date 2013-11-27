package org.boon.primitive;


public class CharBuf {
    protected int capacity = 16;
    protected int location = 0;

    protected char[] buffer;


    public static CharBuf createExact( final int capacity ) {
        return new CharBuf ( capacity ) {
            public CharBuf add( char[] chars ) {
                Chr._idx ( buffer, location, chars );
                location += chars.length;
                return this;
            }
        };
    }

    public static CharBuf create( int capacity ) {
        return new CharBuf ( capacity );
    }

    protected CharBuf( int capacity ) {
        this.capacity = capacity;
        init ( );
    }


    protected CharBuf( ) {
        init ( );
    }

    public void init( ) {
        buffer = new char[capacity];
    }

    public CharBuf add( String str ) {
        add ( str.toCharArray ( ) );
        return this;
    }


    public CharBuf add( int i ) {
        add ( "" + i );
        return this;
    }



    public CharBuf addChar( byte i ) {
        add ((char) i );
        return this;
    }


    public CharBuf addChar( short i ) {
        add ((char) i );
        return this;
    }


    public CharBuf addChar( char c ) {
        add (c );
        return this;
    }

    public CharBuf addLine( String str ) {
        add ( str.toCharArray ( ) );
        add ( '\n' );
        return this;
    }


    public CharBuf add( char[] chars ) {
        if ( chars.length + location < capacity ) {
            Chr._idx ( buffer, location, chars );
        } else {
            buffer = Chr.grow ( buffer, buffer.length * 2 + chars.length );
            Chr._idx ( buffer, location, chars );
            capacity = buffer.length;
        }
        location += chars.length;
        return this;
    }


    public void add( char ch ) {
        if ( 1 + location < capacity ) {
            Chr.idx ( buffer, location, ch );
        } else {
            buffer = Chr.grow ( buffer );
            Chr.idx ( buffer, location, ch );
            capacity = buffer.length;
        }
        location += 1;
    }

    public String toString( ) {
        return new String ( buffer, 0, location );
    }

    public int len( ) {
        return location;
    }

}
