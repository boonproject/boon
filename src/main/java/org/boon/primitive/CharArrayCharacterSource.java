package org.boon.primitive;

import org.boon.core.reflection.FastStringUtils;

import java.util.Arrays;

public class CharArrayCharacterSource implements CharacterSource {

    private char[] chars;
    private int index=0;
    private boolean foundEscape;





    public CharArrayCharacterSource ( char[] chars ) {
        this.chars = chars;
    }


    public CharArrayCharacterSource ( String string ) {
        this.chars = FastStringUtils.toCharArray ( string );
    }


    @Override
    public final int nextChar() {
        return chars[index++];
    }

    public final int safeNextChar() {
        return index + 1 < chars.length ? chars[index++] : -1;
    }


    private final char[] EMPTY_CHARS = new char[0];

    @Override
    public final char[] findNextChar( final int match, final int esc ) {
        int idx = index;
        int startIndex = idx;
        foundEscape = false;
        char[] _chars = chars;
        int ch;

        for (; idx < _chars.length; idx++) {
             ch  = _chars[idx];
             if ( ch == match || ch == esc ) {
                 if ( ch == match ) {
                     /** If you have found the next char, then return a copy of the buffer range.*/
                     index = idx+1;
                     return  Arrays.copyOfRange ( _chars, startIndex, idx );
                 } else if ( ch == esc ) {
                     foundEscape = true;
                     /** if we are dealing with an escape then see if the escaped char is a match
                      *  if so, skip it.
                       */
                    if ( idx + 1 < _chars.length) {
                         idx++;
                    }
                 }
             }
        }

       index = idx;
       return EMPTY_CHARS;
    }

    @Override
    public boolean hadEscape() {
        return foundEscape;
    }

    public char[] readNumber(  ) {
        char [] results =  CharScanner.readNumber( chars, index);
        index += results.length;
        return results;
    }

    @Override
    public final int currentChar() {
        return chars[index];
    }

    @Override
    public final boolean hasChar() {
        return  index + 1 < chars.length;
    }


    @Override
    public final boolean consumeIfMatch( char[] match ) {

        int idx = index;
        char[] _chars = chars;

        boolean ok = true;

        for (int i=0; i < match.length; i++, idx++) {
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
    public final int location() {
        return index;
    }




    @Override
    public void skipWhiteSpace() {
        index = CharScanner.skipWhiteSpace( chars, index );
    }



}
