package org.boon.slumberdb.service.protocol;

import org.boon.Str;
import org.boon.primitive.CharBuf;
import org.boon.slumberdb.service.protocol.factory.ByMapFactories;
import org.boon.slumberdb.service.protocol.factory.ByTextFactory;
import org.boon.slumberdb.service.protocol.factory.RequestFactory;
import org.boon.slumberdb.service.protocol.requests.DataStoreRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.StringScanner.split;

/**
 * Created by Richard on 7/1/14.
 */
public class ProtocolConstants {

    public static final int PROTOCOL_VERSION_POSITON = 0;       //What version of the protocol are we getting?
    public static final int ACTION_POSITION = 1;                //What do they want?

    public static final char DELIMITER = (char) 0x1d;
    public static final String DELIMITER_STR = new String(new char[]{DELIMITER});
    public static final char SUB_DELIM = (char) 0x1e;
    public static final String SUB_DELIM_STR = new String(new char[]{SUB_DELIM});
    public static final char GROUP_DELIM = (char) 0x1d;
    public static final char RECORD_DELIM = (char) 0x1e;
    public static final char UNIT_DELIM = (char) 0x1f;
    public static final String VERSION_1 = "a";
    public static final String VERSION_2 = "b";
    public static final String GET_MASK = VERSION_1 + DELIMITER_STR + "get";
    public static final String SET_MASK = VERSION_1 + DELIMITER_STR + "set";
    public static final String REMOVE_MASK = VERSION_1 + DELIMITER_STR + "remove";
    public static final String CLEAR_MASK = VERSION_1 + DELIMITER_STR + "clear";
    public static final String ACTION_MAP_KEY = "action";
    public static final String CLIENT_ID_MAP_KEY = "clientId";
    public static final String MESSAGE_ID_MAP_KEY = "messageId";
    public static final String KEYS_KEY = "keys";
    public static final String RECORD_DELIM_KEY = "record";
    public static final String GROUP_DELIM_KEY = "group";
    public static final String UNIT_DELIM_KEY = "unit";
    public static final String HUMAN_RECORD_DELIM = "~";
    public static final String HUMAN_GROUP_DELIM = "|";
    public static final String HUMAN_UNIT_DELIM = "@";
    public static final String VALUES_KEY = "values";
    public static final String KEY_KEY = "key";
    public static final String VALUE_KEY = "value";
    public static final String OBJECT_VERSION_KEY = "version";
    public static final String CREATE_TIME_KEY = "createTime";
    public static final String UPDATE_TIME_KEY = "updateTime";
    public static final String SOURCE_KEY = "source";
    public static final String REMOVE_VERB = "remove";
    public static final String REMOVE_SOURCE_VERB = "removeFromSrc";
    public static final String CLEAR_STATS_VERB = "clearStats";
    public static final String QUERY_VERB = "query";
    public static final String QUERY_FOR_KEYS_VERB = "queryForKeys";
    public static final String SET_INTERNAL_VERB = "setInternal";
    public static final String NONE = "none";
    public static final String METHOD_CALL_VERB = "methodCall";
    public static final String SET_INTERNAL_BATCH_VERB = "setInternalBatch";
    public static final String SEARCH_VERB = "search";
    public static final int DEFAULT_PORT = 10100;
    public static final String DEFAULT_URI = "/services/data/store";
    public static final String DEFAULT_WEBSOCKET_URI = DEFAULT_URI;
    public static final String DEFAULT_REST_URI = DEFAULT_URI;
    public static final String GET_VERB = "get";
    public static final String GET_MEM_VERB = "getMem";
    public static final String GET_LOCAL_DB_VERB = "getLocalDB";
    public static final String BATCH_READ_VERB = "getBatch";
    public static final String GET_SOURCE_VERB = "getFromSrc";
    public static final String SET_VERB = "set";
    public static final String SET_VERB_STARTS = VERSION_1 + DELIMITER_STR + SET_VERB + DELIMITER_STR;
    public static final String SET_BATCH_VERB = "setBatch";
    public static final String SET_SOURCE_VERB = "setToSrc";
    public static final String SET_BATCH_IF_NOT_EXISTS_VERB = "setBatchIfNotExists";
    public static final String SET_BROADCAST_VERB = "setBroadcast";
    public static final String SET_IF_NOT_EXISTS_VERB = "setIfNotExists";
    public static final String PING_VERB = "ping";
    public static final String PING_VERB_STARTS = VERSION_1 + DELIMITER_STR + PING_VERB + DELIMITER_STR;
    public static final String RESPONSE = "response";
    public static final String STATS = "stats";
    public static final String BATCH_RESPONSE = "batchResponse";
    public static final String BROADCAST = "broadcast";
    public static String GET_STATS_VERB = "getStats";
    public static int DELIMITER_COUNT_IN_RESPONSE = 2;

    public static Map<String, Action> actionMap = new ConcurrentHashMap<>();

    public static String prettyPrintMessageWithLinesTabs(String message) {

        String[] groups;
        String[] records;
        CharBuf charBuf = CharBuf.create(255);

        groups = split(message, DELIMITER);

        int groupNumber = 0;

        int recordNumber = 0;

        for (String group : groups) {
            records = split(group, SUB_DELIM);

            if (records.length > 1) {
                recordNumber = 0;

                charBuf.add("\tgrp ").add(Str.lpad("" + groupNumber, 3, ' ')).add('\t').add('\t').add("start").add('\n');
                for (String record : records) {
                    charBuf.add("\t\trec ").add(Str.lpad("" + recordNumber, 3, ' ')).add('\t').add('\t').add(record).add('\n');
                    recordNumber++;
                }
                charBuf.add("\tgrp ").add(Str.lpad("" + groupNumber, 3, ' ')).add('\t').add('\t').add("stop").add('\n');
            } else {

                charBuf.add("\tgrp ").add(Str.lpad("" + groupNumber, 3, ' ')).add('\t').add('\t').add(group).add('\n');
            }
            groupNumber++;
        }

        return charBuf.toString();

    }

    public static String prettyPrintMessage(String textData) {
        //return textData.replaceAll(DELIMITER_STR, "<<0x1d>>").replaceAll(SUB_DELIM_STR, "<<0x1e>>");
        return textData.replaceAll(DELIMITER_STR, " | ").replaceAll(SUB_DELIM_STR, " - ");
    }

    public static RequestFactory<String, DataStoreRequest> textProtocolFactory() {
        return new ByTextFactory.FastTextProtocolFactory();
    }

    public static RequestFactory<Map<String, String>, DataStoreRequest> mapProtocolFactory() {
        return new ByMapFactories.MapProtocolFactory();
    }

    public static class Version1 {
        public static final char DELIMITER = (char) 0x1d;

        public static final String DELIMITER_STR = new String(new char[]{DELIMITER});

        public static final int PREAMBLE_SIZE = 7;

        public static class Preamble {
            public static final int PROTOCOL_VERSION_POSITON = 0;       //What version of the protocol are we getting?
            public static final int ACTION_POSITION = 1;                //What do they want?
            public static final int AUTH_TOKEN_POSITION = 2;            //Credentials or token that allows us to communicate.
            public static final int HEADER_POSITON = 3;                 //Headers
            public static final int RESERVED = 4;
            public static final int CLIENT_ID_POSITION = 5;             //Who are they?
            public static final int MESSAGE_ID_POSITION = 6;            //Uniquely identifies a message for this client conversation
        }

        public static class ObjectVersion {
            public static final int OBJECT_VERSION_POSITION = 7;        //KEY OF THE VALUE TO GET.
            public static final int UPDATE_TIME_STAMP_POSITION = 8;     //KEY OF THE VALUE TO GET.
            public static final int CREATE_TIME_STAMP_POSITION = 9;     //CREATE TIME_STAMP OF THE VALUE.
            public static final int KEY_POSITION = 11;                  //KEY OF THE VALUE TO GET/SET.
        }

        public static class SetGet {
            public static final int SOURCE_POSITION = 10;               //DATA SOURCE
        }

        public static class Set {
            public static final int PAYLOAD_POSITION = 12;              //KEY OF THE VALUE TO GET/SET.
        }

        public static class BatchRead {
            public static final int KEYS = 7;                   //KEYS TO read
        }

        public static class BatchSet {
            public static final int KEYS = 7;                   //KEYS TO read
            public static final int VALUES = 8;                 //VALUE TO read
        }
    }

    public static class Version2 extends Version1 {
        public static class BatchSet {
            public static final int SOURCE_POSITION = 7;
            public static final int KEYS = 8;                   //KEYS TO read
            public static final int VALUES = 9;                 //VALUE TO read
        }
    }

    public static class Search {

        public static final String CRITERIA_KEY = "criteria";
        public static final String OFFSET_KEY = "offset";
        public static final String LIMIT_KEY = "limit";
        public static final int LIMIT_VALUE = 5000;
        public static final String HANDLER_KEY = "handler";

    }


}
