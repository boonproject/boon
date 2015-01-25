package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.Map;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/2/14.
 */
public class SetRequest extends BaseDataStoreRequest {

    protected String clientId;

    protected String payload;
    protected DataStoreSource source;
    private ObjectId objectId = new ObjectId();

    public SetRequest(long id, String clientId, String key, String payload) {


        super(id, Action.SET);

        objectId.key(key);

        this.clientId = clientId;
        this.payload = payload;
        this.source = DataStoreSource.ALL;
    }

    public SetRequest(Action action, long id, String clientId, String key, String payload) {


        super(id, action);

        objectId.key(key);

        this.clientId = clientId;
        this.payload = payload;
        this.source = DataStoreSource.ALL;
    }

    public SetRequest(DataStoreSource source, long id, String clientId, String key, String payload) {


        super(id, Action.SET);

        objectId.key(key);

        this.clientId = clientId;
        this.payload = payload;
        this.source = source;
    }

    public SetRequest(Action action, long id, String clientId, long version, long createTimeStamp, long updateTimeStamp, String key, String payload) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;
        this.payload = payload;
        this.source = DataStoreSource.ALL;

    }

    public SetRequest(DataStoreSource source, Action action,
                      long id, String clientId, long version,
                      long createTimeStamp, long updateTimeStamp,
                      String key, String payload) {
        super(id, action);
        objectId.key(key);
        objectId.version(version);
        objectId.createTimeStamp(createTimeStamp);
        objectId.updateTimeStamp(updateTimeStamp);
        this.clientId = clientId;
        this.payload = payload;
        this.source = source;

    }


    private SetRequest() {
        this.source = DataStoreSource.ALL;

    }

    public SetRequest(DataStoreSource source, Action action, SingleResult result) {
        super(result.messageId(), action);
        this.setSource(source);

        objectId.key(result.key());
        objectId.version(0);
        objectId.createTimeStamp(0);
        objectId.updateTimeStamp(0);
        payload = result.payload();

    }

    public static SetRequest parse(Action action, Map<String, String> message) {
        SetRequest request = new SetRequest();
        request.action = action;

        parsePreamble(message, request);


        parseObjectIdInfo(request, message);

        if (action == Action.SET_SOURCE) {
            String src = message.get(ProtocolConstants.SOURCE_KEY);
            if (!Str.isEmpty(src)) {

                request.source = Enum.valueOf(DataStoreSource.class, src);
            } else {
                request.source = DataStoreSource.MEMORY;
            }
        }

        request.payload = message.get(ProtocolConstants.VALUE_KEY);


        return request;
    }

    public static SetRequest parse(String message) {
        SetRequest request = new SetRequest();

        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER);

        String protocolVersion = parsePreamble(request, split);

        parseObjectIdInfo(protocolVersion, request, split);

        request.payload = split[ProtocolConstants.Version1.Set.PAYLOAD_POSITION];

        String strSource = split[ProtocolConstants.Version1.SetGet.SOURCE_POSITION];

        if (!Str.isEmpty(strSource)) {
            request.source = Enum.valueOf(DataStoreSource.class, strSource);
        }


        return request;
    }

    public static String createTextMessage(Action action, long messageId, DataStoreSource source, String clientId, String key, String value) {
        SetRequest request = new SetRequest(source, action, messageId, clientId, 0, 0, 0, key, value);
        return request.formTextRequest();
    }

    public static void main(String... args) {
        puts(System.currentTimeMillis());
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

    public String payload() {
        return payload;
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
                objectId.key(),                     //             11 key
                payload);                           //             12 PayLoad
    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {
        this.source = dataStoreSource;
    }

    @Override
    public String toString() {
        return "SetRequest{" +
                "clientId='" + clientId + '\'' +
                ", objectId='" + objectId.toString() + '\'' +
                ", payload='" + payload + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SetRequest)) return false;
        if (!super.equals(o)) return false;

        SetRequest that = (SetRequest) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        return result;
    }

    public DataStoreSource source() {
        return source;
    }

    public int count() {
        return 1;
    }

}
