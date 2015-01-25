package org.boon.slumberdb.service.client;

/**
 * Created by Richard on 7/3/14.
 */
public interface DataStoreClientProvider {


    DataStoreClient get(String context);

    DataStoreClient get();


}
