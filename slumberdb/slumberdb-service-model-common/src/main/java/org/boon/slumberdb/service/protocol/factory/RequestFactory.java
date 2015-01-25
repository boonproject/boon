package org.boon.slumberdb.service.protocol.factory;

import org.boon.slumberdb.service.protocol.requests.DataStoreRequest;

/**
 * Created by Richard on 9/2/14.
 */
public interface RequestFactory<BUFFER, R extends DataStoreRequest> {

    R createRequest(BUFFER in);
}
