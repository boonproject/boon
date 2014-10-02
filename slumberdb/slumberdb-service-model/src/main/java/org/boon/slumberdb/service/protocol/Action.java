package org.boon.slumberdb.service.protocol;

/**
 * Created by Richard on 9/2/14.
 */
public enum Action {


    QUERY(ProtocolConstants.QUERY_VERB, ActionResponse.BATCH_RESPONSE),
    QUERY_FOR_KEYS(ProtocolConstants.QUERY_FOR_KEYS_VERB, ActionResponse.BATCH_RESPONSE),
    GET_STATS(ProtocolConstants.GET_STATS_VERB, ActionResponse.STATS),
    CLEAR_STATS(ProtocolConstants.CLEAR_STATS_VERB, ActionResponse.NONE),
    REMOVE(ProtocolConstants.REMOVE_VERB, ActionResponse.NONE),
    REMOVE_SOURCE(ProtocolConstants.REMOVE_SOURCE_VERB, ActionResponse.NONE),
    GET(ProtocolConstants.GET_VERB, ActionResponse.GET_RESPONSE),
    GET_MEM(ProtocolConstants.GET_MEM_VERB, ActionResponse.GET_RESPONSE),
    GET_LOCAL_DB(ProtocolConstants.GET_LOCAL_DB_VERB, ActionResponse.GET_RESPONSE),
    BATCH_READ(ProtocolConstants.BATCH_READ_VERB, ActionResponse.BATCH_RESPONSE),
    SET(ProtocolConstants.SET_VERB, ActionResponse.NONE),
    SET_BROADCAST(ProtocolConstants.SET_BROADCAST_VERB, ActionResponse.BROADCAST),
    SET_IF_NOT_EXIST(ProtocolConstants.SET_IF_NOT_EXISTS_VERB, ActionResponse.NONE),
    SET_BATCH(ProtocolConstants.SET_BATCH_VERB, ActionResponse.NONE),
    SET_BATCH_IF_NOT_EXISTS(ProtocolConstants.SET_BATCH_IF_NOT_EXISTS_VERB, ActionResponse.NONE),
    SET_SOURCE(ProtocolConstants.SET_SOURCE_VERB, ActionResponse.NONE),
    GET_SOURCE(ProtocolConstants.GET_SOURCE_VERB, ActionResponse.GET_RESPONSE),
    SET_BATCH_INTERNAL(ProtocolConstants.SET_INTERNAL_BATCH_VERB, ActionResponse.NONE),
    SET_INTERNAL(ProtocolConstants.SET_INTERNAL_VERB, ActionResponse.NONE),
    SEARCH(ProtocolConstants.SEARCH_VERB, ActionResponse.BATCH_RESPONSE),
    NONE(ProtocolConstants.NONE, ActionResponse.NONE),
    METHOD_CALL(ProtocolConstants.METHOD_CALL_VERB, ActionResponse.GET_RESPONSE),

    PING(ProtocolConstants.PING_VERB, ActionResponse.NONE);
    private final String verb;


    private final String contains;

    private final ActionResponse response;


    Action(String verb, ActionResponse response) {
        this.verb = verb;
        this.response = response;
        this.contains = ProtocolConstants.DELIMITER_STR + verb + ProtocolConstants.DELIMITER;
        ProtocolConstants.actionMap.put(verb, this);
    }

    public String verb() {
        return verb;
    }

    public String contains() {
        return contains;
    }

    public ActionResponse response() {
        return response;
    }
}
