package org.boon.slumberdb.service.search;

import org.boon.slumberdb.service.protocol.requests.SearchRequest;
import org.boon.slumberdb.KeyValueStore;

import java.util.Map;

/**
 * @author JD
 */
public interface SearchHandler {
    Map<String, String> handle(KeyValueStore<String, String> store, SearchRequest request);
}
