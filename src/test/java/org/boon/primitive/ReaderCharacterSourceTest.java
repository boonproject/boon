package org.boon.primitive;

import org.junit.Before;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

public class ReaderCharacterSourceTest {


    ReaderCharacterSource source;
    String testString = "abc";

    @Before
    public void setup() {
        source = new ReaderCharacterSource ( testString  );
    }



    @Test
    public void basicCurrentChar() {
        int i = source.currentChar();
        boolean ok = i  == 'a' || die( "" + (char) i);

        i = source.currentChar();
        ok = i  == 'a' || die( "" + (char) i);

    }

    @Test
    public void safeNext() {
        int i = source.safeNextChar();
        boolean ok = i  == 'a' || die( "" + (char) i);

        i = source.nextChar();
        ok = i  == 'b' || die( "" + (char) i);

        i = source.nextChar();
        ok = i  == 'c' || die( "" + (char) i);


        i = source.safeNextChar ();
        ok = i  == -1 || die( "" +  i);

        try {
            i = source.nextChar();
            die();
        } catch ( RuntimeException aiobe ) {

        }
    }


    @Test
    public void hasNextTest() {
        StringBuilder builder = new StringBuilder(  );
        while (source.hasChar()) {
            builder.append ( source.nextChar () );
        }

        boolean ok = builder.toString().equals( testString );
    }

    @Test public void consumeIfMatchTest() {

        String testString = "abc true abc";
        source = new ReaderCharacterSource ( testString );
        boolean found = false;

        loop:
        while (source.hasChar()) {
            int i = source.nextChar ();
            switch ( i ) {
                case 't':
                    found = true;
                    break loop;
            }
        }

        boolean ok = found || die("not found");
        ok |= source.consumeIfMatch ( "true".toCharArray() ) || die();
        ok |= source.safeNextChar () == ' ' || die("" + (char)source.safeNextChar ());

    }

    @Test public void consumeIfMatchNotMatchingTest() {

        String testString = "abc train abc";
        source = new ReaderCharacterSource ( testString );
        boolean found = false;

        loop:
        while (source.hasChar()) {
            int i = source.nextChar ();
            switch ( i ) {
                case 't':
                    found = true;
                    break loop;
            }
        }

        boolean ok = found || die("not found");
        ok |= !source.consumeIfMatch ( "true".toCharArray() ) || die();
        ok |= source.currentChar() == 't' || die("" + (char)source.currentChar());

    }

    @Test public void findStringWithFindNextChar() {

        String testString = "abc \"train\" abc";
        source = new ReaderCharacterSource ( testString );
        boolean found = false;

        loop:
        while (source.hasChar()) {
            int i = source.nextChar();
            puts ("current char", (char)i);
            switch ( i ) {
                case '"':
                    found = true;
                    break loop;
            }
        }

        boolean ok = found || die("not found");
        source.nextChar();
        char [] chars = source.findNextChar ( '"', '\\' ) ;

        puts (new String(chars));
        ok &= Chr.equals( chars, "train".toCharArray ()) || die(new String(chars));

        ok |= source.currentChar () == ' ' || die("" + (char)source.currentChar());

    }


    @Test public void findStringWithFindNextCharWithEscape() {

        String testString = "abc \"train \\b\" abc";
        source = new ReaderCharacterSource ( testString );
        boolean found = false;

        loop:
        while (source.hasChar()) {
            int i = source.currentChar();
            switch ( i ) {
                case '"':
                    found = true;
                    break loop;
            }
            source.nextChar();
        }

        boolean ok = found || die("not found");
        source.nextChar();
        char [] chars = source.findNextChar ( '"', '\\' ) ;

        ok &= Chr.equals( chars, "train \\b".toCharArray ()) || die(new String(chars));

        ok |= source.currentChar () == ' ' || die("" + (char)source.currentChar());

    }

    @Test public void findStringWithFindNextCharWithEscapeOfQuote() {

        String testString = "abc \"train \\\"\" abc0123456789";
        source = new ReaderCharacterSource( testString );
        boolean found = false;

        loop:
        while (source.hasChar()) {
            int i = source.currentChar();
            switch ( i ) {
                case '"':
                    found = true;
                    break loop;
            }
            source.nextChar();
        }

        boolean ok = found || die("not found");
        source.nextChar();
        char [] chars = source.findNextChar ( '"', '\\' ) ;

        ok &= Chr.equals( chars, "train \\\"".toCharArray ()) || die(new String(chars));
        ok |= source.currentChar () == ' ' || die("" + (char)source.currentChar());

    }


    @Test public void skipWhiteSpace() {

        String testString = "a   b c";
        source = new ReaderCharacterSource( testString );


        boolean ok = source.nextChar() == 'a' || die("" + (char)source.currentChar());

        source.nextChar();
        source.skipWhiteSpace();

        ok &= source.nextChar() == 'b' || die("$$" + (char)source.currentChar() + "$$");
        source.nextChar();
        source.skipWhiteSpace();

        ok &= source.nextChar() == 'c' || die("" + (char)source.currentChar());

        source.skipWhiteSpace();


    }


    @Test public void readNumberTest() {

        String testString = "123";
        source = new ReaderCharacterSource ( testString );

        char [] numberChars = source.readNumber();
        boolean ok = Chr.equals ( "123".toCharArray (), numberChars ) || die( new String(numberChars) ) ;

    }


    @Test public void readNumberTest2() {

        String testString = "123 456";
        source = new ReaderCharacterSource( testString );

        char [] numberChars = source.readNumber();
        boolean ok = Chr.equals ( "123".toCharArray (), numberChars ) || die( new String(numberChars) ) ;


        source.skipWhiteSpace();

        numberChars = source.readNumber();
        ok = Chr.equals ( "456".toCharArray (), numberChars ) || die( new String(numberChars) ) ;

    }



    @Test
    public void readNumberTest3() {


        String testString = "123,456,abc";
        source = new ReaderCharacterSource( testString );

        source.nextChar();

        if (source.currentChar() == '1') {
            char [] numberChars = source.readNumber();
            boolean ok = Chr.equals ( "123".toCharArray (), numberChars ) || die( new String(numberChars) ) ;

        } else {
            die();
        }


        if (source.nextChar() == ',') {

            if (source.nextChar() == '4') {
                char[] numberChars = source.readNumber();
                boolean ok = Chr.equals ( "456".toCharArray (), numberChars ) || die( new String(numberChars) ) ;
            } else {
                die("" + (char)source.currentChar());
            }

        } else {
            die("" + (char)source.currentChar());
        }



    }


}
