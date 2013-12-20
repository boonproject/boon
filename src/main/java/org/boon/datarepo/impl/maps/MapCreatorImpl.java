package org.boon.datarepo.impl.maps;

import org.boon.datarepo.spi.MapCreator;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

public class MapCreatorImpl implements MapCreator {
    @Override
    public NavigableMap createNavigableMap ( Class<?> keyType ) {
        if ( keyType == String.class ) {
            return new JavaUtilNavigableMap ();
        } else {
            return new JavaUtilNavigableMap ();
        }
    }

    @Override
    public NavigableMap createNavigableMap ( Class<?> keyType, Comparator collator ) {

        if ( keyType == String.class ) {
            return new JavaUtilNavigableMap ( collator );
        } else {
            return new JavaUtilNavigableMap ();
        }
    }

    @Override
<<<<<<< HEAD
    public Map createMap ( Class<?> keyType ) {
=======
    public Map createMap( Class<?> keyType ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new JavaUtilMap ();
    }
}
