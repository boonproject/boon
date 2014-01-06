package org.boon.json.serializers.impl;

import org.boon.cache.Cache;
import org.boon.cache.CacheType;
import org.boon.cache.SimpleCache;
import org.boon.core.Dates;
import org.boon.core.reflection.FastStringUtils;
import org.boon.json.serializers.DateSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by rick on 1/4/14.
 */
public class JsonDateSerializer implements DateSerializer {

    private final Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
    private final Cache<Object, String> dateCache = new SimpleCache<>(200, CacheType.LRU);


    @Override
    public final void serializeDate( JsonSerializerInternal jsonSerializer, Date date, CharBuf builder ) {
        String string = dateCache.get ( date );
        if ( string == null) {
            CharBuf buf =  CharBuf.create ( Dates.JSON_TIME_LENGTH );
            Dates.jsonDateChars ( calendar, date, buf );
            string = buf.toString();
            dateCache.put ( date, string );

        }
        builder.addChars ( FastStringUtils.toCharArray( string ) );
    }
}
