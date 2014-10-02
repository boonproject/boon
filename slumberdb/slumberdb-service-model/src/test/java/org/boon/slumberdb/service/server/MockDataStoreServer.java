package org.boon.slumberdb.service.server;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/15/14.
 */
public class MockDataStoreServer extends DataStoreServer {
    @Override
    protected void sendToAllClients(String reply) {
        puts("sendToAllClients", reply);
    }

    @Override
    protected void sendMessageToClientId(String clientId, String reply) {
        puts("sendMessageToClientId", clientId, reply);

    }

    @Override
    public boolean clientExistsStill(String clientId) {
        return true;
    }

    @Override
    protected void registerOutputHandler(String clientId, Object commChannel) {
        puts("sendMessageToClientId", clientId, commChannel);

    }
}
