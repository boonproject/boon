package org.boon.slumberdb.stores.queue;

import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.stores.DataOutputQueue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Richard on 6/27/14.
 */
public class MapOutputQueue implements DataOutputQueue {

    public static Map<String, String> map = new ConcurrentHashMap<>();

    @Override
    public void put(Response result) {
        if (result instanceof SingleResult) {
            SingleResult dataItem = (SingleResult) result;
            map.put(dataItem.key(), dataItem.getValue() == null ? "NULL" : dataItem.getValue());
        } else if (result instanceof BatchResult) {

            BatchResult batchResult = (BatchResult) result;
            final Map<String, String> results = batchResult.getResults();
            final Set<Map.Entry<String, String>> entries = results.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                map.put(entry.getKey(), entry.getValue() == null ? "NULL" : entry.getValue());

            }
        }


    }

    @Override
    public Response poll() {
        return null;
    }

    @Override
    public Response take() {
        return null;
    }

}
