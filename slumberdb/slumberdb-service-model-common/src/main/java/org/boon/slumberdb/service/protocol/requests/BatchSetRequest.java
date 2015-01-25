package org.boon.slumberdb.service.protocol.requests;

import org.boon.Lists;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.collections.LazyMap;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;

import java.util.*;

/**
 * Created by Richard on 9/3/14.
 */
public class BatchSetRequest extends BaseDataStoreRequest {

    protected String clientId;
    protected DataStoreSource source;
    protected List<String> keys;
    protected List<String> values;

    public BatchSetRequest() {}

    public BatchSetRequest(BatchSetRequest request, Collection<String> keys, Collection<String> values) {
        this(null, request.action(), request.messageId(), request.clientId(), keys, values);
    }

    public BatchSetRequest(long messageId, String clientId, Collection<String> keys, Collection<String> values) {
        this(null, Action.SET_BATCH, messageId, clientId, keys, values);
    }

    public BatchSetRequest(Action action, long messageId, String clientId, Collection<String> keys, Collection<String> values) {
        this(null, action, messageId, clientId, keys, values);
    }

    public BatchSetRequest(DataStoreSource source, long messageId, String clientId, Collection<String> keys, Collection<String> values) {
        this(source, Action.SET_BATCH, messageId, clientId, keys, values);
    }

    public BatchSetRequest(DataStoreSource source, Action action, long messageId, String clientId, Collection<String> keys, Collection<String> values) {
        super(messageId, action);
        this.source = source;
        this.clientId = clientId;
        this.keys = Lists.list(keys);
        this.values = Lists.list(values);
    }

    public BatchSetRequest(long messageId, String clientId, Map<String, String> map) {
        this(null, Action.SET_BATCH, messageId, clientId, map);
    }

    public BatchSetRequest(Action action, long messageId, String clientId, Map<String, String> map) {
        this(null, action, messageId, clientId, map);
    }

    public BatchSetRequest(DataStoreSource source, Action action, long messageId, String clientId, Map<String, String> map) {
        super(messageId, action);
        this.source = source;
        this.clientId = clientId;
        if (map instanceof LinkedHashMap) {
            this.keys = Lists.list(map.keySet());
            this.values = Lists.list(map.values());
        }
        else {
            final Set<Map.Entry<String, String>> entries = map.entrySet();
            this.keys = new ArrayList<>(map.size());
            this.values = new ArrayList<>(map.size());

            for (Map.Entry<String, String> entry : entries) {
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
        }
    }

    public static BatchSetRequest parse(String message) {
        BatchSetRequest request = new BatchSetRequest();

        int keysIndex = ProtocolConstants.Version1.BatchSet.KEYS;
        int valuesIndex = ProtocolConstants.Version1.BatchSet.VALUES;

        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER, ProtocolConstants.Version2.BatchSet.VALUES);

        String protocolVersion = parsePreamble(request, split);
        if (protocolVersion.equals(ProtocolConstants.VERSION_2)) {
            keysIndex = ProtocolConstants.Version2.BatchSet.KEYS;
            valuesIndex = ProtocolConstants.Version2.BatchSet.VALUES;
            String src = split[ProtocolConstants.Version2.BatchSet.SOURCE_POSITION];
            if (!Str.isEmpty(src)) {
                request.source = Enum.valueOf(DataStoreSource.class, src);
            }
        }

        String strKeys = split[keysIndex];
        String[] keyArray = StringScanner.split(strKeys, ProtocolConstants.SUB_DELIM);

        String strValues = split[valuesIndex];
        String[] values = StringScanner.split(strValues, ProtocolConstants.SUB_DELIM);

        request.keys = Lists.list(keyArray);
        request.values = Lists.list(values);

        return request;
    }

    public static BatchSetRequest parse(Action action, Map<String, String> message) {
        BatchSetRequest request = new BatchSetRequest();
        request.action = action;

        parsePreamble(message, request);

        String recordDelim = getRecordDelim(message);

        String src = message.get(ProtocolConstants.SOURCE_KEY);
        if (!Str.isEmpty(src)) {
            request.source = Enum.valueOf(DataStoreSource.class, src);
        }

        String strKeys = message.get(ProtocolConstants.KEYS_KEY);
        String[] keyArray = strKeys.split(recordDelim);
        request.keys = Lists.list(keyArray);

        String strValues = message.get(ProtocolConstants.VALUES_KEY);
        String[] valueArray = strValues.split(recordDelim);
        request.values = Lists.list(valueArray);

        return request;
    }

    public String clientId() {
        return clientId;
    }

    public String key() {
        throw new UnsupportedOperationException();
    }

    public List<String> keys() {
        return keys;
    }

    public List<String> values() {
        return values;
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
    public String formTextRequest() {

        String strKeys = Str.joinCollection(ProtocolConstants.SUB_DELIM, keys);

        String strValues = Str.joinCollection(ProtocolConstants.SUB_DELIM, values);

        if (source == null) {
            return Str.join(ProtocolConstants.DELIMITER,
                    ProtocolConstants.VERSION_1,             //        0  PROTOCOL VERSION
                    action.verb(),                           //        1  ACTION
                    "",                                      //        2  AUTH_TOKEN
                    "",                                      //        3  HEADER
                    "",                                      //        4  RESERVED
                    clientId,                                //        5  CLIENT ID
                    "" + messageId,                          //        6  MESSAGE_ID
                    strKeys,                                 //        7  KEYS
                    strValues                                //        8  VALUES

            );
        }

        return Str.join(ProtocolConstants.DELIMITER,
                ProtocolConstants.VERSION_2,           //        0  PROTOCOL VERSION
                action.verb(),                           //        1  ACTION
                "",                                      //        2  AUTH_TOKEN
                "",                                      //        3  HEADER
                "",                                      //        4  RESERVED
                clientId,                                //        5  CLIENT ID
                "" + messageId,                          //        6  MESSAGE_ID
                source == null ? "" : source.toString(), //        7  SOURCE
                strKeys,                                 //        8  KEYS
                strValues                                //        9  VALUES

        );

    }

    public Map<String, String> payloadAsMap() {

        final LazyMap lazyMap = new LazyMap(this.keys, this.values, true);

        return (Map<String, String>) (Object) lazyMap;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchSetRequest)) return false;
        if (!super.equals(o)) return false;

        BatchSetRequest that = (BatchSetRequest) o;

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


    public int count() {
        return keys.size();
    }

    @Override
    protected void setSource(DataStoreSource source) {
        this.source = source;
    }

    @Override
    public DataStoreSource source() {
        return source;
    }
}
