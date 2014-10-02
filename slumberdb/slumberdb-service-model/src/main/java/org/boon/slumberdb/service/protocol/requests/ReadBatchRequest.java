package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Lists;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 9/2/14.
 */
public class ReadBatchRequest extends BaseDataStoreRequest {

    protected String clientId;
    protected List<String> keys;


    public ReadBatchRequest(long id, String clientId, String... keys) {
        super(id, Action.BATCH_READ);
        this.clientId = clientId;
        this.keys = Lists.list(keys);
    }

    private ReadBatchRequest() {

    }

    public ReadBatchRequest(long messageId, String clientId, Collection<String> keys) {
        super(messageId, Action.BATCH_READ);
        this.keys = Lists.list(keys);
        this.clientId = clientId;

    }

    public ReadBatchRequest(ReadBatchRequest batchRequest, Collection<String> keys) {
        super(batchRequest.messageId(), batchRequest.action());
        this.keys = Lists.list(keys);
        this.clientId = batchRequest.clientId();

    }

    public static ReadBatchRequest parse(Action action, Map<String, String> message) {
        ReadBatchRequest request = new ReadBatchRequest();
        request.action = action;

        parsePreamble(message, request);

        String recordDelim = getRecordDelim(message);

        String strKeys = message.get(ProtocolConstants.KEYS_KEY);

        String[] keyArray = strKeys.split(recordDelim);
        request.keys = Lists.list(keyArray);

        return request;
    }

    public static ReadBatchRequest parse(String message) {
        ReadBatchRequest request = new ReadBatchRequest();

        final String[] split = parsePreamble(message, request);


        String strKeys = split[ProtocolConstants.Version1.BatchRead.KEYS];
        String[] keyArray = StringScanner.split(strKeys, ProtocolConstants.SUB_DELIM);
        request.keys = Lists.list(keyArray);

        return request;
    }

    public String clientId() {
        return clientId;
    }

    public String key() {
        throw new UnsupportedOperationException();
    }

    @Override
    void key(String key) {

    }

    public int count() {
        return keys.size();
    }

    public List<String> keys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
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

    }

    @Override
    void updateTimeStamp(long ts) {

    }

    @Override
    void createTimeStamp(long ts) {

    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {
    }

    @Override
    public String formTextRequest() {

        String strKeys = Str.joinCollection(ProtocolConstants.SUB_DELIM, keys);

        return Str.join(ProtocolConstants.DELIMITER,
                ProtocolConstants.VERSION_1,        //             0  PROTOCOL VERSION
                action.verb(),                      //             1  ACTION
                "",                                 //             2  AUTH_TOKEN
                "",                                 //             3  HEADER
                "",                                 //             4  RESERVED
                clientId,                           //             5  CLIENT ID
                "" + messageId,                     //             6  MESSAGE_ID
                strKeys                             //             7  KEYS
        );

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReadBatchRequest)) return false;
        if (!super.equals(o)) return false;

        ReadBatchRequest that = (ReadBatchRequest) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (keys != null ? !keys.equals(that.keys) : that.keys != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (keys != null ? keys.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReadBatchRequest{" +
                "clientId='" + clientId + '\'' +
                ", keys=" + keys +
                "} " + super.toString();
    }

}
