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
    private int ch = -2;

    private boolean foundEscape;


    private char[] readBuf;

    private int index;

    private int length;

    private int location;

    boolean more = true;
    private boolean done = false;

    public ReaderCharacterSource( final Reader reader, final int readAheadSize ) {
        this.reader = reader;
        this.readBuf =  new char[readAheadSize];
        this.readAheadSize = readAheadSize;
    }

    public ReaderCharacterSource( final Reader reader ) {
        this.reader = reader;
        this.readAheadSize = 10;
        this.readBuf =  new char[10];
    }

    public ReaderCharacterSource( final String string ) {
        this(new StringReader ( string ));
    }


    private void ensureBuffer() {
        if (index >= length && !done) {
            try {
                length = reader.read ( readBuf );
                index = 0;
                if (length == -1) {
                    ch = -1;
                    length = 0;
                    more = false;
                    done = true;
                } else {
                    if (length<readBuf.length) {
                        done = true;
                        more = true;
                    }
                }
            } catch ( IOException e ) {
                Exceptions.handle ( e );
            }
        } else if (done && index >=length) {
            more = false;
        }else {
            more = true;
        }
    }

    @Override
    public final int nextChar() {
        ensureBuffer();
        return ch = readBuf[index++];
    }

    @Override
    public  final int currentChar() {
        ensureBuffer();
        return readBuf[index];
    }

    @Override
    public  final boolean hasChar() {
            ensureBuffer();
            return more;
    }

    @Override
    public  final boolean consumeIfMatch( char[] match ) {

        ensureBuffer();

        char [] _chars = readBuf;
        int i=0;
        int idx = --index;
        boolean ok = true;

        if ( idx + match.length > length ) {
            for (; idx < length; i++, idx++) {
                ok &=  ( match[i] == _chars[idx] );
                if (!ok) break;
            }
            if (ok) {
                index = idx;
                ensureBuffer();
                idx = 0;
            }
        }

        for (; i < match.length; i++, idx++) {
                ok &=  ( match[i] == _chars[idx] );
                if (!ok) break;
        }

        if ( ok ) {
            index = idx;
            return true;
        } else {
            return false;
        }

    }

    @Override
    public final  int location() {
        return die(int.class, "not supported");
    }

    public final int safeNextChar() {
        ensureBuffer();
        return index + 1 < readBuf.length ? readBuf[index++] : -1;
    }


    private final char[] EMPTY_CHARS = new char[0];


    @Override
    public char[] findNextChar( int match, int esc ) {
            ensureBuffer();

            int idx = index;
            int start = index;

            char[] _chars = readBuf;

            foundEscape=false;

            boolean foundEnd = false;
            char [] results ;


                for (; idx < length; idx++) {
                    ch  = _chars[idx];
                    if ( ch == match || ch == esc ) {
                        if ( ch == match ) {
                            foundEnd = true;

                            break;
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


                start = start == 0 ? 0 : start -1;
                if (idx == 0 ) {
                     results = EMPTY_CHARS;
                }   else {
                    results =  Arrays.copyOfRange ( _chars, start, idx );
                }
                index = idx;


                if (foundEnd) {
                    index++;
                    if (index < length) {
                        ch = _chars[index ];
                    }
                    return results;
                } else {

                    if (index >= length) {
                        ensureBuffer();
                        char results2[] = findNextChar(match, esc);
                        return Chr.add(results, results2);
                    } else {
                        return die (char[].class, "Unable to find close char " + (char)match + " " + new String(results));
                    }
                }


    }

    @Override
    public boolean hadEscape() {
        return foundEscape;
    }


    @Override
    public void skipWhiteSpace() {
        ensureBuffer();
        index = CharScanner.skipWhiteSpace( readBuf, index );
        if (index >= length && more) {

            ensureBuffer();

            skipWhiteSpace();
        }
    }







    public char[] readNumber(  ) {
         ensureBuffer();

        char [] results =  CharScanner.readNumber( readBuf, index);
        index += results.length;

        if (index >= length) {
            ensureBuffer();
            if (length!=0) {
                char results2[] = CharScanner.readNumber( readBuf, index);
                return Chr.add(results, results2);
            } else  {
                return results;
            }
        } else {
            return results;
        }

    }

}
