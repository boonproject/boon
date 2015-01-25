package org.boon.slumberdb.service.protocol;

/**
 * Created by Richard on 9/2/14.
 */
public enum ActionResponse {


    STATS(ProtocolConstants.STATS),
    GET_RESPONSE(ProtocolConstants.RESPONSE),
    BROADCAST(ProtocolConstants.BROADCAST),
    BATCH_RESPONSE(ProtocolConstants.BATCH_RESPONSE),
    NONE("");

    final String responseHeader;
    final String startWith;

    ActionResponse(String responseHeader) {
        this.responseHeader = responseHeader;
        this.startWith = responseHeader + ProtocolConstants.DELIMITER;

    }

    public String responseHeader() {
        return responseHeader;
    }

    public String startsWith() {
        return startWith;
    }

}
