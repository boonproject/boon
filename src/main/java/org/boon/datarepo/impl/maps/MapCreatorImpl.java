package org.boon.datarepo.impl.maps;

import org.boon.datarepo.spi.MapCreator;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

public class MapCreatorImpl implements MapCreator {
    @Override
    public NavigableMap createNavigableMap(Class<?> keyType) {
        if (keyType == String.class) {
            return new JavaUtilNavigableMap();
        } else {
            return new JavaUtilNavigableMap();
        }
    }

    @Override
    public NavigableMap createNavigableMap(Class<?> keyType, Comparator collator) {

        if (keyType == String.class) {
            return new JavaUtilNavigableMap(collator);
        } else {
            return new JavaUtilNavigableMap();
        }
    }

    @Override
    public Map createMap(Class<?> keyType) {
        return new JavaUtilMap();
    }
}
