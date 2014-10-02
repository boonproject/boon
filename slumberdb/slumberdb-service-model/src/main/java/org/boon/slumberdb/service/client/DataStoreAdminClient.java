package org.boon.slumberdb.service.client;

/**
 * Created by Richard on 9/3/14.
 */
public interface DataStoreAdminClient {

    void turnOnRequestLogging();

    void turnOnMetricsTracking();

    void turnOnSendLogsToClient();
}
