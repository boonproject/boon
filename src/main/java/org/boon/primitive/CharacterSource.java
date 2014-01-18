package org.boon.primitive;

/** */
public interface CharacterSource {

    /** Skip white space. */
    void skipWhiteSpace();

    /** returns the next character moving the file pointer or index to the next location. */
    int nextChar();
    /** returns the current character without changing the IO pointer or index. */
    int currentChar();
    /** Checks to see if there is a next character. */
    boolean hasChar();
    /** Useful for finding constants in a string like true, false, etc.  */
    boolean consumeIfMatch( char[] match );
    /** This is mostly for debugging and testing. */
    int location();

    /** Combines the operations of nextChar and hasChar.
     *  Characters is -1 if not found which signifies end of file.
     *  This might be preferable to avoid two method calls.
     **/
    int safeNextChar();

    /**
     * Used to find strings and their ilk
     * Finds the next non-escaped char
     * @param ch character to find
     * @param esc escape character to avoid next char if escaped
     * @return list of chars until this is found.
     */
    char[] findNextChar( int ch, int esc );

    boolean hadEscape();

    /** Reads a number from the character source. */
    char[] readNumber(  );

}
