package org.boon.datarepo.spi;

import org.boon.datarepo.ResultSet;
import org.boon.criteria.Criteria;

import java.util.List;

public interface ResultSetInternal<T> extends ResultSet<T> {

    void addResults(List<T> results);

    void filterAndPrune(Criteria criteria);

    void andResults();

    int lastSize();
}
