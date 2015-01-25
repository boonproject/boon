package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.Map;

/**
 * Created by Richard on 9/2/14.
 */
public class GetRequest extends BaseDataStoreRequest {

    protected String clientId;
    protected DataStoreSource source;
    private ObjectId objectId = new ObjectId();

    public GetRequest(long id, String clientId, String key) {
        super(id, Action.GET);
        this.clientId = clientId;
        objectId.key(key);
        this.source = DataStoreSource.ALL;

    }

    public GetRequest(Action action, long id, String clientId, String key) {
        super(id, action);
        objectId.key(key);
        this.clientId = clientId;

        this.source = DataStoreSource.ALL;
    }

    public GetRequest(Action action, long id, String clientId, long version, long createTimeStamp, long updateTimeStamp, String key) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;

        this.source = DataStoreSource.ALL;
    }

    public GetRequest(DataStoreSource source, long id, String clientId, String key) {
        super(id, Action.GET);
        this.clientId = clientId;
        objectId.key(key);
        this.source = source;
    }

    public GetRequest(DataStoreSource source, Action action, long id, String clientId, String key) {
        super(id, action);
        objectId.key(key);
        this.clientId = clientId;
        this.source = source;

    }

    public GetRequest(DataStoreSource source, Action action, long id, String clientId, long version, long createTimeStamp, long updateTimeStamp, String key) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;
        this.source = source;

    }

    private GetRequest() {

    }

    public static GetRequest parse(Action action, Map<String, String> message) {
        GetRequest request = new GetRequest();

        request.action = action;

        parsePreamble(message, request);


        parseObjectIdInfo(request, message);


        if (action == Action.GET_SOURCE) {
            String src = message.get(ProtocolConstants.SOURCE_KEY);
            if (!Str.isEmpty(src)) {

                request.source = Enum.valueOf(DataStoreSource.class, src);
            } else {
                request.source = DataStoreSource.MEMORY;
            }
        } else if (action == Action.GET_LOCAL_DB) {
            request.source = DataStoreSource.LOCAL_DB;
        } else if (action == Action.GET_MEM) {
            request.source = DataStoreSource.MEMORY;
        }


        return request;
    }

    public static GetRequest parse(String message) {
        GetRequest request = new GetRequest();

        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER);

        String version = parsePreamble(request, split);

        parseObjectIdInfo(version, request, split);

        parseSourceInfo(request, split);

        if (request.action == Action.GET_LOCAL_DB) {
            request.source = DataStoreSource.LOCAL_DB;
        } else if (request.action == Action.GET_MEM) {
            request.source = DataStoreSource.MEMORY;
        }


        return request;
    }

    public static String createTextMessage(DataStoreSource source, Action action, long messageId, String clientId, String key) {
        GetRequest request = new GetRequest(source, action, messageId, clientId, 0, 0, 0, key);
        return request.formTextRequest();

    }

    public String clientId() {
        return clientId;
    }

    public String key() {
        return objectId.key();
    }

    @Override
    void key(String key) {
        objectId.key(key);
    }

    @Override
    public String payload() {
        throw new UnsupportedOperationException();
    }

    @Override
    void clientId(String clientId) {

        this.clientId = clientId;
    }

    @Override
    void objectVersion(long objectVersion) {

        this.objectId.version(objectVersion);

    }

    @Override
    void updateTimeStamp(long ts) {

        this.objectId.updateTimeStamp(ts);
    }

    @Override
    void createTimeStamp(long ts) {
        this.objectId.createTimeStamp(ts);
    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {
        this.source = dataStoreSource;
    }

    @Override
    public String formTextRequest() {
        return Str.join(ProtocolConstants.DELIMITER,
                ProtocolConstants.VERSION_1,        //             0  PROTOCOL VERSION
                action.verb(),                      //             1  ACTION
                "",                                 //             2  AUTH_TOKEN
                "",                                 //             3  HEADER
                "",                                 //             4  RESERVED
                clientId,                           //             5  CLIENT ID
                "" + messageId,                     //             6  MESSAGE_ID
                "" + objectId.version(),            //             7  VERSION
                "" + objectId.updateTimeStamp(),    //             8  UPDATE_TIMESTAMP
                "" + objectId.createTimeStamp(),    //             9  CREATE_TIMESTAMP
                source == null ? "" : source.toString(), //       10 SOURCE
                objectId.key()                      //             11 key
        );


    }

    public int count() {
        return 1;
    }

    public DataStoreSource source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetRequest)) return false;
        if (!super.equals(o)) return false;

        GetRequest that = (GetRequest) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GetRequest{" +
                "clientId='" + clientId + '\'' +
                ", objectId=" + objectId +
                "} " + super.toString();
    }


}

