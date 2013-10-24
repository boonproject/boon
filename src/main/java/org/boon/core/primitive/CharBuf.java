package org.boon.core.primitive;



public class CharBuf {
    protected int capacity = 16;
    protected int location = 0;

    protected char [] buffer;


    public static CharBuf createExact( final int capacity) {
          return new CharBuf(capacity) {
              public void add(char[] chars) {
                      Chr._idx( buffer, location, chars );
                      location += chars.length;
             }
         };
    }

    public static CharBuf create( int capacity) {
        return new CharBuf(capacity);
    }

    protected CharBuf(int capacity)  {
         this.capacity = capacity;
         init();
    }


    protected CharBuf()  {
        init();
    }

    public void init() {
        buffer = new char[capacity];
    }

    public void add(String str) {
        add( str.toCharArray() );
    }


    public void add(char[] chars) {
        if (chars.length + location < capacity) {
            Chr._idx( buffer, location, chars );
        } else {
            buffer = Chr.grow(buffer);
            Chr._idx( buffer, location, chars );
            capacity = buffer.length;
        }
        location += chars.length;
    }


    public void add(char ch) {
        if (1 + location < capacity) {
            Chr.idx( buffer, location, ch );
        } else {
            buffer = Chr.grow(buffer);
            Chr.idx( buffer, location, ch );
            capacity = buffer.length;
        }
        location += 1;
    }

    public String toString() {
        return new String(buffer, 0, location);
    }

}
