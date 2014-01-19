package org.boon.primitive;

import org.boon.Exceptions;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import static org.boon.Exceptions.die;

public class ReaderCharacterSource implements CharacterSource {


    private final Reader reader;
    private final int readAheadSize;
    private int ch;

    private boolean foundEscape;


    private final char[] readBuf;

    public ReaderCharacterSource( final Reader reader, final int readAheadSize ) {
        this.reader = reader;
        this.readBuf =  new char[readAheadSize];
        this.readAheadSize = readAheadSize;
    }

    public ReaderCharacterSource( final Reader reader ) {
        this.reader = reader;
        this.readAheadSize = 1_000;
        this.readBuf =  new char[1_000];
    }

    public ReaderCharacterSource( final String string ) {
        this(new StringReader ( string ));
    }


    @Override
    public final int nextChar() {
        try {
            reader.mark ( 1 );
            ch = reader.read();
            if ( ch==-1 ) {
                die("There are no more characters to consume");
            }
            return ch;
        } catch ( IOException e ) {
            return Exceptions.handle(int.class, e);
        }
    }

    @Override
    public  final int currentChar() {
        /** Added this to make the behavior the same between char[] and reader. */
        return ch != -2 ? ch : nextChar();
    }

    @Override
    public  final boolean hasChar() {
        try {
            reader.mark ( 1 );
            int i = reader.read();
            boolean canRead = i!=-1;
            reader.reset();
            return canRead;
        } catch ( IOException e ) {
            return Exceptions.handle(boolean.class, e);
        }
    }

    @Override
    public  final boolean consumeIfMatch( char[] match ) {

        this.ch = -2;
        char[] chars = new char[ match.length ];

        try {

            reader.reset (  );
            reader.mark ( match.length );
            reader.read ( chars );
            if (chars.length!=match.length) {
                reader.reset();
                return false;
            }
        } catch ( IOException e ) {
            return Exceptions.handle(boolean.class, e);
        }

        boolean ok = Chr.equalsNoNullCheck ( match, chars  );
        if ( ok ) {
            return true;
        } else {
            try {
                reader.reset();
            } catch ( IOException e ) {
                return Exceptions.handle(boolean.class, e);
            }
            return false;
        }
    }

    @Override
    public final  int location() {
        return die(int.class, "not supported");
    }

    @Override
    public final int safeNextChar() {
        try {
            return ch = reader.read (  );
        } catch ( IOException e ) {
            return Exceptions.handle ( int.class, e );
        }
    }


    private final char[] EMPTY_CHARS = new char[0];

    @Override
    public char[] findNextChar( int match, int esc ) {


        try {
            reader.reset();
            reader.mark ( this.readAheadSize );
            reader.read ( readBuf );

            int idx = 0;
            char[] _chars = readBuf;
            int ch;
            this.ch = -2;
            foundEscape=false;

            for (; idx < _chars.length; idx++) {
                ch  = _chars[idx];
                if ( ch == match || ch == esc ) {
                    if ( ch == match ) {
                        /* Go back to the mark. */
                        reader.reset();
                        /* Copy the part of the buffer that we do need. */
                        char [] results =  Arrays.copyOfRange ( _chars, 0, idx );
                        /* Consume the part of the buffer that we did  use from the reader. */
                        reader.read ( readBuf, 0, idx + 1 );
                        return results;
                    } else if ( ch == esc ) {
                        foundEscape=true;
                        /** if we are dealing with an escape then see if the escaped char is a match
                         *  if so, skip it.
                         */
                        if ( idx + 1 < _chars.length) {
                            idx++;
                        }
                    }
                }
            }

            reader.reset();
            return EMPTY_CHARS;


        } catch ( IOException e ) {
            return Exceptions.handle ( char[].class, e );
        }
    }

    @Override
    public boolean hadEscape() {
        return foundEscape;
    }


    @Override
    public void skipWhiteSpace() {

        try {
            reader.mark ( this.readAheadSize );
            reader.read ( readBuf );

            this.ch = -2;

            int skipped = CharScanner.skipWhiteSpaceFast ( readBuf );

            reader.reset();

            if (skipped > 0) {
                reader.read ( readBuf, 0, skipped );
            }

        } catch ( IOException e ) {
             Exceptions.handle (  e );
        }

    }







    public char[] readNumber(  ) {


        try {

            reader.reset();

            reader.mark ( this.readAheadSize );
            int count = reader.read ( readBuf );

            char[] _chars = readBuf;
            this.ch = -2;

            char [] results =  CharScanner.readNumber( readBuf, 0, count);


            reader.reset();

            if (results.length > 0) {
                reader.read ( readBuf, 0, results.length );
            }
            this.safeNextChar();
            return results;


        } catch ( IOException e ) {
            return Exceptions.handle ( char[].class, e );
        }

    }

}
