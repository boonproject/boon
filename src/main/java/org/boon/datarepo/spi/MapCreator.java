package org.boon.datarepo.spi;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

public interface MapCreator {
    NavigableMap createNavigableMap(Class<?> keyType);

    NavigableMap createNavigableMap(Class<?> keyType, Comparator comparator);

    Map createMap(Class<?> keyType);

}
