package org.boon.datarepo.spi;

import org.boon.criteria.Criteria;
import org.boon.datarepo.ResultSet;

import java.util.List;

public interface ResultSetInternal<T> extends ResultSet<T> {

    void addResults ( List<T> results );

    void filterAndPrune ( Criteria criteria );

<<<<<<< HEAD
    void andResults ();

    int lastSize ();
=======
    void andResults();

    int lastSize();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
}
