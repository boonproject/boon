package org.boon.slumberdb.service.results;

import org.boon.slumberdb.service.protocol.ActionResponse;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.primitive.CharBuf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 7/8/14.
 */
public class BatchResult extends Result {

    private static ThreadLocal<CharBuf> charBufThreadLocal = new ThreadLocal<>();
    protected Map<String, String> results;

    public BatchResult() {
    }

    public BatchResult(long messageId, String clientId, DataStoreSource source, Map<String, String> results) {
        super(messageId, clientId, source);
        this.results = results;
    }

    public BatchResult(BatchResult batchResult, Map map) {
        this.messageId = batchResult.messageId;
        this.clientId = batchResult.clientId;
        this.source = batchResult.source;
        this.results = map;
    }

    public static BatchResult fromTextMessage(String text) {

        BatchResult dataItem = new BatchResult();

        if (!text.startsWith(ActionResponse.BATCH_RESPONSE.responseHeader())) {
            die("Unable to parser batch result", text);
        }

        final String[] split = Str.split(text, ProtocolConstants.DELIMITER);
        dataItem.messageId = Long.parseLong(split[1]);
        dataItem.clientId = split[2];
        dataItem.source = Enum.valueOf(DataStoreSource.class, split[3]);
        int size = Integer.parseInt(split[4]);

        dataItem.results = new LinkedHashMap<>(size);

        Map<String, String> results = dataItem.results;

        int index = 5;
        for (int sizeIndex = 0; sizeIndex < size; sizeIndex++) {
            String key = split[index];
            index++;
            String value = split[index];
            index++;
            results.put(key, value);
        }
        return dataItem;

    }

    @Override
    public String toTextMessage() {

        CharBuf charBuf = charBufThreadLocal.get();

        if (charBuf == null) {
            charBuf = CharBuf.create(2_000);
            charBufThreadLocal.set(charBuf);
        }

        charBuf.add(ProtocolConstants.BATCH_RESPONSE); //action
        charBuf.add(ProtocolConstants.DELIMITER);
        charBuf.add(messageId);      //MESSAGE ID
        charBuf.add(ProtocolConstants.DELIMITER);
        charBuf.add(clientId);       //client messageId
        charBuf.add(ProtocolConstants.DELIMITER);
        charBuf.add(source);         // SOURCE
        charBuf.add(ProtocolConstants.DELIMITER);
        charBuf.add(results.size()); //size

        final Set<Map.Entry<String, String>> entries = results.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            charBuf.add(ProtocolConstants.DELIMITER);
            charBuf.add(entry.getKey());
            charBuf.add(ProtocolConstants.DELIMITER);
            charBuf.add(entry.getValue().toString());
        }
        String message = charBuf.toStringAndRecycle();

        return message;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "results=" + results +
                "} " + super.toString();
    }

    public Map<String, String> getResults() {
        return results;
    }
}
