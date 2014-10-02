package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.results.Response;

/**
 * Created by Richard on 6/27/14.
 */
public interface DataOutputQueue {

    void put(Response result);

    Response poll();


    Response take();

}
