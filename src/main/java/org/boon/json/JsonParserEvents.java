package org.boon.json;

import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2/5/14.
 */
public interface JsonParserEvents {

    boolean  objectStart(int index);
    boolean  objectEnd(int index, Map<String, Object> object);

    boolean  objectFieldName(int index, Map<String, Object> map, CharSequence name);
    boolean  objectField(int index, Map<String, Object> map, CharSequence name, Object field);

    boolean  arrayStart(int index);
    boolean  arrayEnd(int index, List<Object> list);
    boolean  arrayItem(int index, List<Object> list, Object item);


    boolean  number(int startIndex, int endIndex, Number number);
    boolean  string(int startIndex, int endIndex, CharSequence string);

    boolean  bool( int endIndex, boolean value);
    boolean  nullValue( int endIndex);

}
