package org.boon.json.implementation;

import org.boon.json.JsonParser;
import org.boon.primitive.CharBuf;
import sun.nio.cs.Surrogate;


import static org.boon.Exceptions.die;

public class JsonUTF8Parser extends JsonBaseByteArrayParser implements JsonParser {












    //  [C2..DF] [80..BF]
    private static boolean isMalformed2 ( int b1, int b2 ) {
        return ( b1 & 0x1e ) == 0x0 || ( b2 & 0xc0 ) != 0x80;
    }

    //  [E0]     [A0..BF] [80..BF]
    //  [E1..EF] [80..BF] [80..BF]
    private static boolean isMalformed3 ( int b1, int b2, int b3 ) {
        return ( b1 == ( byte ) 0xe0 && ( b2 & 0xe0 ) == 0x80 ) ||
                ( b2 & 0xc0 ) != 0x80 || ( b3 & 0xc0 ) != 0x80;
    }

    //  [F0]     [90..BF] [80..BF] [80..BF]
    //  [F1..F3] [80..BF] [80..BF] [80..BF]
    //  [F4]     [80..8F] [80..BF] [80..BF]
    //  only check 80-be range here, the [0xf0,0x80...] and [0xf4,0x90-...]
    //  will be checked by Surrogate.neededFor(uc)
    private static boolean isMalformed4 ( int b2, int b3, int b4 ) {
        return ( b2 & 0xc0 ) != 0x80 || ( b3 & 0xc0 ) != 0x80 ||
                ( b4 & 0xc0 ) != 0x80;
    }



    private final void utf8MultiByte (final int b1, final CharBuf builder) {

        boolean ok = true;

        if ( ( b1 >> 5 ) == -2 ) {
            int b2;

            ok = hasMore () || die ( "unable to parse 2 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];

            if ( isMalformed2 ( b1, b2 ) ) {
                builder.addChar ( '#' );
            } else {
                builder.addChar ( ( ( b1 << 6 ) ^ b2 ) ^ 0x0f80 );
            }
        } else if ( ( b1 >> 4 ) == -2 ) {
            int b2;
            int b3;

            ok = hasMore () || die ( "unable to parse 3 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 3 byte utf 8 - b3" );
            __index++;
            b3 = this.charArray[ __index ];

            if ( isMalformed3 ( b1, b2, b3 ) ) {
                builder.addChar ( '#' );
            } else {
                builder.addChar ( ( ( b1 << 12 ) ^ ( b2 << 6 ) ^ b3 ) ^ 0x1f80 );
            }
        } else if ( ( b1 >> 3 ) == -2 ) {
            int b2;
            int b3;
            int b4;

            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b3" );
            __index++;
            b3 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b4" );
            __index++;
            b4 = this.charArray[ __index ];

            int uc = ( ( b1 & 0x07 ) << 18 ) |
                    ( ( b2 & 0x3f ) << 12 ) |
                    ( ( b3 & 0x3f ) << 6 ) |
                    ( b4 & 0x3f );

            if ( isMalformed4 ( b2, b3, b4 ) && !Surrogate.neededFor ( uc ) ) {
                builder.addChar ( '#' );
            } else {

                final char high = Surrogate.high ( uc );
                final char low = Surrogate.low ( uc );

                builder.addChar ( high );
                builder.addChar ( low );

            }
        }

    }



    protected final  void addChar() {
        if ( __currentChar >= 0 ) {
            builder.addChar ( __currentChar );
        } else {
            utf8MultiByte ( __currentChar, builder );
        }
    }








}

