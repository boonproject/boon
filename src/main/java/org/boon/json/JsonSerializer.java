package org.boon.json;

import org.boon.primitive.CharBuf;

/**
 * Created by rick on 12/26/13.
 */
public interface JsonSerializer {

    CharBuf serialize( Object obj );


}
