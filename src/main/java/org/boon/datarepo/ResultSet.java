package org.boon.datarepo;

import org.boon.criteria.Criteria;
import org.boon.criteria.Selector;
import org.boon.criteria.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResultSet<T> extends Iterable<T> {

    ResultSet expectOne();

    ResultSet expectMany();

    ResultSet expectNone();

    ResultSet expectOneOrMany();

    ResultSet removeDuplication();

    ResultSet sort(Sort sort);

    Collection<T> filter(Criteria criteria);

    ResultSet<List<Map<String, Object>>> select(Selector... selectors);

    int[] selectInts(Selector selector);

    float[] selectFloats(Selector selector);

    short[] selectShorts(Selector selector);

    double[] selectDoubles(Selector selector);

    byte[] selectBytes(Selector selector);

    char[] selectChars(Selector selector);

    Object[] selectObjects(Selector selector);

    <OBJ> OBJ[] selectObjects(Class<OBJ> cls, Selector selector);

    <OBJ> ResultSet<OBJ> selectObjectsAsResultSet(Class<OBJ> cls, Selector selector);


    Collection<T> asCollection();

    String asJSONString();

    List<Map<String, Object>> asListOfMaps();

    List<T> asList();

    Set<T> asSet();

    List<PlanStep> queryPlan();

    T firstItem();

    Map<String, Object> firstMap();

    String firstJSON();

    int firstInt(Selector selector);

    float firstFloat(Selector selector);

    short firstShort(Selector selector);

    double firstDouble(Selector selector);

    byte firstByte(Selector selector);

    char firstChar(Selector selector);

    Object firstObject(Selector selector);

    <OBJ> OBJ firstObject(Class<OBJ> cls, Selector selector);


    List<T> paginate(int start, int size);

    List<Map<String, Object>> paginateMaps(int start, int size);

    String paginateJSON(int start, int size);

    //Size can vary if you allow duplication.
    //The size can change after removeDuplication.
    int size();


}
