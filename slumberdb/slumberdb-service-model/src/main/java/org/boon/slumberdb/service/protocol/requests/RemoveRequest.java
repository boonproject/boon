package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.Map;

/**
 * Created by Richard on 9/4/14.
 */
public class RemoveRequest extends BaseDataStoreRequest {


    protected String clientId;
    protected DataStoreSource source;
    private ObjectId objectId = new ObjectId();


    public RemoveRequest(long id, String clientId, String key) {
        super(id, Action.REMOVE);
        this.clientId = clientId;
        objectId.key(key);
        this.source = DataStoreSource.ALL;

    }

    public RemoveRequest(Action action, long id, String clientId, String key) {
        super(id, action);
        objectId.key(key);
        this.clientId = clientId;

        this.source = DataStoreSource.ALL;
    }

    public RemoveRequest(Action action, long id, String clientId, long version, long createTimeStamp, long updateTimeStamp, String key) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;

        this.source = DataStoreSource.ALL;
    }

    public RemoveRequest(DataStoreSource source, long id, String clientId, String key) {
        super(id, Action.REMOVE);
        this.clientId = clientId;
        objectId.key(key);
        this.source = source;
    }

    public RemoveRequest(DataStoreSource source, Action action, long id, String clientId, String key) {
        super(id, action);
        objectId.key(key);
        this.clientId = clientId;
        this.source = source;

    }

    public RemoveRequest(DataStoreSource source, Action action, long id, String clientId, long version, long createTimeStamp, long updateTimeStamp, String key) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;
        this.source = source;

    }

    private RemoveRequest() {

    }

    public static RemoveRequest parse(Action action, Map<String, String> message) {
        RemoveRequest request = new RemoveRequest();

        request.action = action;

        parsePreamble(message, request);


        parseObjectIdInfo(request, message);


        if (action == Action.REMOVE_SOURCE) {
            String src = message.get(ProtocolConstants.SOURCE_KEY);
            if (!Str.isEmpty(src)) {

                request.source = Enum.valueOf(DataStoreSource.class, src);
            } else {
                request.source = DataStoreSource.MEMORY;
            }
        }


        return request;
    }

    public static RemoveRequest parse(String message) {
        RemoveRequest request = new RemoveRequest();

        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER);

        String version = parsePreamble(request, split);

        parseObjectIdInfo(version, request, split);

        parseSourceInfo(request, split);


        return request;
    }

    public static String createTextMessage(DataStoreSource source, Action action, long messageId, String clientId, String key) {
        RemoveRequest request = new RemoveRequest(source, action, messageId, clientId, 0, 0, 0, key);
        return request.formTextRequest();

    }

    public String payload() {
        return "REMOVE";
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
                source == null ? "" : source.toString(),                  //             10 SOURCE
                objectId.key()                      //             11 key
        );


    }

    public DataStoreSource source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoveRequest)) return false;
        if (!super.equals(o)) return false;

        RemoveRequest that = (RemoveRequest) o;

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
        return "RemoveRequest{" +
                "clientId='" + clientId + '\'' +
                ", objectId=" + objectId +
                "} " + super.toString();
    }

    public int count() {
        return 1;
    }

}
