package org.boon.slumberdb.service.client;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreFactory {

    private static DataStoreClientProvider clientSupplier;

    public static void registerClientSupplier(DataStoreClientProvider aClientFactory) {
        clientSupplier = aClientFactory;
    }

    public static DataStoreClient createClient(String contextName, Object credential) {
        return clientSupplier.get(contextName);
    }
}
