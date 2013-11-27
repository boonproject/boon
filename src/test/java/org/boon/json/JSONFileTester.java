package org.boon.json;

import org.boon.IO;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

public class JSONFileTester {


    public static void main (String... args) {



        final List<String> list = IO.listByExt ( "files", ".json" );

        for ( String file : list ) {


            puts ( "testing", file );

            final Map<String,Object> map =  JsonParser.parseMap ( IO.read ( file ) );
            final Map<String,Object> map2 = JsonLazyEncodeParser.parseMap ( IO.read ( file ) );
            final Map<String,Object> map3 = JsonAsciiParser.parseMap ( IO.read ( file ) );

            boolean ok = true;

            ok |=  map2.equals ( map3 ) || die( "maps not equal");
            puts ( map );
        }


    }
}
