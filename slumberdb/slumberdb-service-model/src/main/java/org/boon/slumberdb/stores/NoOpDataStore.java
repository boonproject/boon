package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.protocol.requests.*;


/**
 * Created by Richard on 6/27/14.
 */
public class NoOpDataStore implements DataStore {
    @Override
    public void clearStats() {

    }

    @Override
    public void sendStats(long now) {

    }


    @Override
    public void set(SetRequest request) {

    }


    @Override
    public void get(GetRequest getRequest) {

    }

    @Override
    public void search(SearchRequest searchRequest) {

    }

    @Override
    public void batchRead(ReadBatchRequest request) {

    }

    @Override
    public void remove(RemoveRequest removeRequest) {

    }

    @Override
    public void addAll(BatchSetRequest batchSetRequest) {

    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
