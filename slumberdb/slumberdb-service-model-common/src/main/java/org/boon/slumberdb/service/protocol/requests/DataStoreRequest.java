package org.boon.slumberdb.service.protocol.requests;


import org.boon.Str;
import org.boon.StringScanner;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;

import java.util.Map;

import static org.boon.Exceptions.die;

public abstract class DataStoreRequest {

    protected long messageId;


    protected Action action;


    protected DataStoreRequest(long id, Action action) {
        this.messageId = id;
        this.action = action;

    }

    DataStoreRequest() {
    }

    protected static String validateProtocolVersion(String[] split) {
        String version = split[ProtocolConstants.PROTOCOL_VERSION_POSITON];
        if (version.equals(ProtocolConstants.VERSION_1)) {
            return version;
        }

        Action action = Action.getInstance(split[ProtocolConstants.ACTION_POSITION]);
        if (action != null) {
            switch (action) {
                case SET_BATCH:
                    if (version.equals(ProtocolConstants.VERSION_2)) {
                        return version;
                    }
            }
        }

        die("Unable to parse Data Store Request message", split);
        return null; // never gets here since "die" throws
    }

    protected static void parseAction(String version, DataStoreRequest request, String[] split) {
        String actionKey = split[ProtocolConstants.Version1.Preamble.ACTION_POSITION];
        request.action = ProtocolConstants.actionMap.get(actionKey);
    }

    protected static void parseClientId(String version, DataStoreRequest request, String[] split) {
        String clientId = split[ProtocolConstants.Version1.Preamble.CLIENT_ID_POSITION];
        request.clientId(clientId);
    }

    protected static void parseMessageId(String version, DataStoreRequest request, String[] split) {
        String sMessageId = split[ProtocolConstants.Version1.Preamble.MESSAGE_ID_POSITION];
        request.messageId = Long.parseLong(sMessageId);
    }

    protected static String parsePreamble(DataStoreRequest request, String message) {
        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER, ProtocolConstants.Version1.PREAMBLE_SIZE);

        return parsePreamble(request, split);
    }

    protected static void parsePreamble(Map<String, String> message, DataStoreRequest request) {

        request.clientId(message.get(ProtocolConstants.CLIENT_ID_MAP_KEY));
        request.messageId = Long.parseLong(message.get(ProtocolConstants.MESSAGE_ID_MAP_KEY));


    }

    protected static String getGroupDelim(Map<String, String> message) {
        String delim = message.get(ProtocolConstants.GROUP_DELIM_KEY);

        return delim == null ? ProtocolConstants.HUMAN_GROUP_DELIM : delim;
    }

    protected static String getRecordDelim(Map<String, String> message) {
        String delim = message.get(ProtocolConstants.RECORD_DELIM_KEY);

        return delim == null ? ProtocolConstants.HUMAN_RECORD_DELIM : delim;
    }

    protected static String getUnitDelim(Map<String, String> message) {
        String delim = message.get(ProtocolConstants.RECORD_DELIM_KEY);

        return delim == null ? ProtocolConstants.HUMAN_UNIT_DELIM : delim;
    }

    protected static String parsePreamble(DataStoreRequest request, String[] split) {
        String protocolVersion = validateProtocolVersion(split);
        parseAction(protocolVersion, request, split);
        parseClientId(protocolVersion, request, split);
        parseMessageId(protocolVersion, request, split);
        return protocolVersion;
    }

    protected static void parseObjectIdInfo(String protocolVersion, DataStoreRequest request, String[] split) {

        parseKey(protocolVersion, request, split);
        parseObjectVersion(protocolVersion, request, split);
        parseUpdateTimeStamp(protocolVersion, request, split);
        parseCreateTimeStamp(protocolVersion, request, split);
    }

    protected static void parseObjectIdInfo(DataStoreRequest request, Map<String, String> message) {

        request.key(message.get(ProtocolConstants.KEY_KEY));


        String value = message.get(ProtocolConstants.OBJECT_VERSION_KEY);
        if (!Str.isEmpty(value)) {
            request.objectVersion(Long.parseLong(value));
        }

        value = message.get(ProtocolConstants.UPDATE_TIME_KEY);
        if (!Str.isEmpty(value)) {
            request.updateTimeStamp(Long.parseLong(value));
        }


        value = message.get(ProtocolConstants.CREATE_TIME_KEY);
        if (!Str.isEmpty(value)) {
            request.createTimeStamp(Long.parseLong(value));
        }

    }

    private static void parseKey(String protocolVersion, DataStoreRequest request, String[] split) {
        String value = split[ProtocolConstants.Version1.ObjectVersion.KEY_POSITION];
        request.key(value);
    }

    protected static void parseObjectVersion(String protocolVersion, DataStoreRequest request, String[] split) {
        String value = split[ProtocolConstants.Version1.ObjectVersion.OBJECT_VERSION_POSITION];

        if (!Str.isEmpty(value)) {
            request.objectVersion(Long.parseLong(value));
        }
    }

    protected static void parseUpdateTimeStamp(String protocolVersion, DataStoreRequest request, String[] split) {
        String value = split[ProtocolConstants.Version1.ObjectVersion.UPDATE_TIME_STAMP_POSITION];
        if (!Str.isEmpty(value)) {
            request.updateTimeStamp(Long.parseLong(value));
        }
    }

    protected static void parseCreateTimeStamp(String protocolVersion, DataStoreRequest request, String[] split) {
        String value = split[ProtocolConstants.Version1.ObjectVersion.CREATE_TIME_STAMP_POSITION];
        if (!Str.isEmpty(value)) {
            request.createTimeStamp(Long.parseLong(value));
        }
    }

    protected static String[] parsePreamble(String message, DataStoreRequest request) {
        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER, ProtocolConstants.Version1.PREAMBLE_SIZE);

        parsePreamble(request, split);
        return split;
    }

    protected static void parseSourceInfo(DataStoreRequest request, String[] split) {
        String strSource = split[ProtocolConstants.Version1.SetGet.SOURCE_POSITION];

        if (!Str.isEmpty(strSource)) {
            request.setSource(Enum.valueOf(DataStoreSource.class, strSource));
        }
    }

    public long messageId() {
        return messageId;
    }

    public Action action() {
        return action;
    }

    public abstract String clientId();

    public abstract String key();

    abstract void key(String key);

    public abstract String payload();

    public abstract String formTextRequest();

    abstract void clientId(String clientId);

    abstract void objectVersion(long objectVersion);

    abstract void updateTimeStamp(long ts);

    abstract void createTimeStamp(long ts);

    protected abstract void setSource(DataStoreSource dataStoreSource);


    public DataStoreSource source() {
        return DataStoreSource.NONE;
    }


    public int count() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStoreRequest)) return false;

        DataStoreRequest that = (DataStoreRequest) o;

        if (messageId != that.messageId) return false;
        if (action != that.action) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataStoreRequest{" +
                "messageId=" + messageId +
                ", action=" + action +
                '}';
    }
}
