package org.boon.datarepo.impl;

import org.boon.core.reflection.Conversions;
import org.boon.datarepo.DataRepoException;
import org.boon.datarepo.PlanStep;
import org.boon.datarepo.ResultSet;
import org.boon.criteria.Criteria;
import org.boon.criteria.QueryFactory;
import org.boon.criteria.Selector;
import org.boon.criteria.Sort;
import org.boon.datarepo.spi.ResultSetInternal;


import java.lang.reflect.Array;
import java.util.*;


import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;
import static org.boon.core.reflection.Reflection.toMap;

import static org.boon.Lists.list;

public class ResultSetImpl<T> implements ResultSetInternal<T> {

    private List<T> results;
    private List<List<T>> allResults;
    private int totalSize;

    private List<T> lastList;

    private Map<String, FieldAccess> fields;


    public ResultSetImpl(Map<String, FieldAccess> fields) {
        this.fields = fields;
        this.allResults = new ArrayList<>();
    }


    public ResultSetImpl(List<T> results, Map<String, FieldAccess> fields) {
        this.fields = fields;
        this.allResults = new ArrayList<>();
        this.addResults(results);
    }

    public ResultSetImpl(List<T> results) {
        if (results.size() > 0) {
            this.fields = Reflection.getPropertyFieldAccessMap(results.get(0).getClass());
        } else {
            this.fields = Collections.EMPTY_MAP;
        }
        this.allResults = new ArrayList<>();
        this.addResults(results);
    }

    private void prepareResults() {
        if (results == null && allResults.size() == 1) {
            results = allResults.get(0);
        } else if (results == null) {

            results = new ArrayList<>(totalSize);

            for (List<T> list : allResults) {
                for (T item : list) {
                    results.add(item);
                }
            }
        }
        allResults.clear();
        totalSize = 0;
    }


    public void addResults(List<T> results) {
        lastList = results;
        totalSize += results.size();
        allResults.add(results);
    }

    @Override
    public ResultSet expectOne() {
        prepareResults();
        if (results.size() == 0) {
            throw new DataRepoException("Expected one result, no results");
        } else if (results.size() > 1) {
            throw new DataRepoException("Expected one result, but have many");
        }
        return this;
    }

    @Override
    public ResultSet expectMany() {
        prepareResults();

        if (results.size() <= 1) {
            throw new DataRepoException("Expected many");
        }
        return this;
    }

    @Override
    public ResultSet expectNone() {
        prepareResults();

        if (results.size() != 0) {
            throw new DataRepoException("Expected none");
        }
        return this;
    }

    @Override
    public ResultSet expectOneOrMany() {
        prepareResults();

        if (results.size() >= 1) {
            throw new DataRepoException("Expected one or many");
        }
        return this;
    }

    @Override
    public ResultSet removeDuplication() {
        prepareResults();
        results = new ArrayList(asSet());
        return this;
    }

    @Override
    public ResultSet sort(Sort sort) {
        prepareResults();
        sort.sort(results);
        return this;
    }

    @Override
    public Collection<T> filter(Criteria criteria) {
        prepareResults();
        return QueryFactory.filter(results, criteria);
    }

    @Override
    public void filterAndPrune(Criteria criteria) {
        prepareResults();
        this.results = QueryFactory.filter(results, criteria);
    }


    @Override
    public ResultSet<List<Map<String, Object>>> select(Selector... selectors) {
        prepareResults();
        return new ResultSetImpl(
                Selector.performSelection(
                        Arrays.asList(selectors), results, fields),
                fields);

    }

    @Override
    public int[] selectInts(Selector selector) {
        prepareResults();

        int[] values = new int[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toInt(map.get(keyName));
        }
        return values;
    }

    @Override
    public float[] selectFloats(Selector selector) {
        prepareResults();

        float[] values = new float[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toFloat(map.get(keyName));
        }
        return values;
    }

    @Override
    public short[] selectShorts(Selector selector) {
        prepareResults();

        short[] values = new short[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toShort(map.get(keyName));
        }
        return values;
    }

    @Override
    public double[] selectDoubles(Selector selector) {
        prepareResults();

        double[] values = new double[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toDouble(map.get(keyName));
        }
        return values;
    }

    @Override
    public byte[] selectBytes(Selector selector) {
        prepareResults();

        byte[] values = new byte[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toByte(map.get(keyName));
        }
        return values;
    }

    @Override
    public char[] selectChars(Selector selector) {
        prepareResults();

        char[] values = new char[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toChar(map.get(keyName));
        }
        return values;
    }

    @Override
    public Object[] selectObjects(Selector selector) {
        prepareResults();

        Object[] values = new Object[results.size()];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = map.get(keyName);
        }
        return values;
    }

    @Override
    public <OBJ> OBJ[] selectObjects(Class<OBJ> cls, Selector selector) {
        prepareResults();

        Object values = Array.newInstance(cls, results.size());

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < results.size(); index++) {
            Map<String, Object> map = maps.get(index);
            Reflection.idx(values, index, map.get(keyName));
        }
        return (OBJ[]) values;
    }


    @Override
    public <OBJ> ResultSet<OBJ> selectObjectsAsResultSet(Class<OBJ> cls, Selector selector) {
        prepareResults();

        Object values = Array.newInstance(cls, results.size());

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < results.size(); index++) {
            Map<String, Object> map = maps.get(index);
            Reflection.idx(values, index, map.get(keyName));
        }
        OBJ[] array = (OBJ[]) values;
        List list = new ArrayList(Arrays.asList(array));
        return new ResultSetImpl(list);
    }

    @Override
    public Collection<T> asCollection() {
        prepareResults();

        return results;
    }

    @Override
    public String asJSONString() {
        prepareResults();

        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public List<Map<String, Object>> asListOfMaps() {
        prepareResults();


        List<Map<String, Object>> items = new ArrayList<>(results.size());
        for (T item : results) {
            items.add(toMap(item));
        }
        return items;

    }

    @Override
    public List<T> asList() {
        prepareResults();

        return results;
    }

    @Override
    public Set<T> asSet() {
        prepareResults();

        return new HashSet(results);
    }

    @Override
    public List<PlanStep> queryPlan() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public T firstItem() {
        prepareResults();

        return results.get(0);
    }

    @Override
    public Map<String, Object> firstMap() {
        prepareResults();
        return toMap(this.firstItem());
    }

    @Override
    public String firstJSON() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public int firstInt(Selector selector) {
        prepareResults();

        int[] values = new int[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length && index < maps.size(); index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toInt(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[0];


    }

    @Override
    public float firstFloat(Selector selector) {
        prepareResults();

        float[] values = new float[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toFloat(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public short firstShort(Selector selector) {
        prepareResults();

        short[] values = new short[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toShort(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public double firstDouble(Selector selector) {
        prepareResults();

        double[] values = new double[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toDouble(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public byte firstByte(Selector selector) {
        prepareResults();

        byte[] values = new byte[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toByte(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public char firstChar(Selector selector) {
        prepareResults();

        char[] values = new char[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = Conversions.toChar(map.get(keyName));
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public Object firstObject(Selector selector) {
        prepareResults();

        Object[] values = new Object[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = map.get(keyName);
            if (index == 1) {
                break;
            }
        }
        return values[1];
    }

    @Override
    public <OBJ> OBJ firstObject(Class<OBJ> cls, Selector selector) {
        prepareResults();

        Object[] values = new Object[1];

        List<Map<String, Object>> maps = Selector.performSelection(list(selector), results, fields);

        String keyName = selector.getName();
        for (int index = 0; index < values.length; index++) {
            Map<String, Object> map = maps.get(index);
            values[index] = map.get(keyName);
            if (index == 1) {
                break;
            }
        }
        return (OBJ) values[1];
    }

    @Override
    public List<T> paginate(int start, int size) {
        prepareResults();

        return results.subList(start, start + size);
    }

    @Override
    public List<Map<String, Object>> paginateMaps(int start, int size) {
        prepareResults();

        List<Map<String, Object>> mapResults = new ArrayList<>();
        List<T> list = this.paginate(start, size);

        for (T item : list) {
            mapResults.add(toMap(item));
        }

        return mapResults;
    }

    @Override
    public String paginateJSON(int start, int size) {
        prepareResults();

        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public int size() {
        if (results != null) {
            return this.results.size();
        } else {
            return totalSize;
        }
    }

    @Override
    public Iterator<T> iterator() {
        prepareResults();
        return this.results.iterator();
    }

    @Override
    public void andResults() {
        if (allResults.size() == 0) {
            return;
        }

        if (allResults.size() == 1) {
            prepareResults();
            return;
        }

        boolean foundEmpty = false;

        for (List<T> list : allResults) {
            if (list.size() == 0) {
                foundEmpty = true;
                break;
            }
        }

        if (foundEmpty) {
            results = Collections.EMPTY_LIST;
            allResults.clear();
            totalSize = 0;
            return;
        }


        List<T> shortestList = null;
        int min = Integer.MAX_VALUE;
        for (List<T> list : allResults) {
            int size = list.size();
            if (size < min) {
                min = size;
                shortestList = list;
            }
        }
        if (shortestList == null) {
            return;
        }

        allResults.remove(shortestList);
        Set set = new HashSet(shortestList);
        for (List<T> list : allResults) {
            set.retainAll(list);
        }

        results = new ArrayList(set);
        allResults.clear();
        totalSize = 0;

    }

    @Override
    public int lastSize() {
        if (lastList == null) {
            return 0;
        } else {
            return lastList.size();
        }

    }


}
