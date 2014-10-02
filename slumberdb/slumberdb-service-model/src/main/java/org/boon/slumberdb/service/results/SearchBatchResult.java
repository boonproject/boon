package org.boon.slumberdb.service.results;


import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Boon;

import java.util.Map;

/**
 * @author JD
 */
public class SearchBatchResult extends BatchResult {
    public SearchBatchResult(long messageId, String clientId, DataStoreSource source, Map<String, String> results) {
        super(messageId, clientId, source, results);
    }

    @Override
    public String toTextMessage() {

        return Boon.toJson(results);
    }
}
