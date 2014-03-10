/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.json.implementation;

import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonException;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {





    public static String decode( String string ) {
        if ( !string.contains( "\\" ) ) {
            return string;
        }
        char[] cs = FastStringUtils.toCharArray( string );
        return decode( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode( chars, 0, chars.length );
    }


    public static String decode( char[] chars, int start, int to ) {

        if ( !Chr.contains( chars, '\\', start, to - start ) ) {
            return new String( chars, start, to - start );
        }
        return decodeForSure( chars, start, to );
    }


    public static String decodeForSure( CharSequence cs, int start, int to ) {
        if ( cs instanceof String ) {
            return decodeForSure( ( String ) cs, start, to );
        } else {
            return decode( cs.subSequence( start, to ).toString() );
        }

    }


    public static String decodeForSure( String string, int start, int to ) {
        char[] cs = FastStringUtils.toCharArray( string );
        return decodeForSure( cs, start, to );
    }

    public static String decodeForSure( char[] chars ) {
        return decodeForSure( chars, 0, chars.length );
    }

    public static String decodeForSure( char[] chars, int start, int to ) {

        CharBuf builder = CharBuf.create( to - start );
        builder.decodeJsonString(chars, start, to);
        return builder.toString();

    }

    public static String decodeForSure( byte[] bytes, int start, int to ) {
        CharBuf builder = CharBuf.create( to - start );
        builder.decodeJsonString(bytes, start, to);
        return builder.toString();
    }

    public static String decodeForSureAscii( byte[] bytes, int start, int to ) {
        CharBuf builder = CharBuf.create( to - start );
        builder.decodeJsonStringAscii(bytes, start, to);
        return builder.toString();
    }
}