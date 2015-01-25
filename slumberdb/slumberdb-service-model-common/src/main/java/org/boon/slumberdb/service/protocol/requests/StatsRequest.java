package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.Map;

/**
 * Created by Richard on 9/8/14.
 */
public class StatsRequest extends BaseDataStoreRequest {

    protected String clientId;
    protected DataStoreSource source;

    public StatsRequest(long id, String clientId) {
        super(id, Action.GET_STATS);
        this.clientId = clientId;
        this.source = DataStoreSource.ALL;

    }

    public StatsRequest(DataStoreSource source, Action action, long id, String clientId) {
        super(id, action);
        this.clientId = clientId;
        this.source = source;
    }


    private StatsRequest() {

    }

    public static StatsRequest parse(Action action, Map<String, String> message) {
        StatsRequest request = new StatsRequest();

        request.action = action;

        parsePreamble(message, request);


        if (action == Action.GET_SOURCE) {
            String src = message.get(ProtocolConstants.SOURCE_KEY);
            if (!Str.isEmpty(src)) {

                request.source = Enum.valueOf(DataStoreSource.class, src);
            } else {
                request.source = DataStoreSource.MEMORY;
            }
        }


        return request;
    }

    public static StatsRequest parse(String message) {
        StatsRequest request = new StatsRequest();

        final String[] split = StringScanner.split(message, ProtocolConstants.DELIMITER);

        parsePreamble(request, split);

        parseSourceInfo(request, split);


        return request;
    }

    public static String createTextMessage(DataStoreSource source, Action action, long messageId, String clientId) {
        StatsRequest request = new StatsRequest(source, action, messageId, clientId);
        return request.formTextRequest();

    }

    public String clientId() {
        return clientId;
    }

    @Override
    void clientId(String clientId) {

        this.clientId = clientId;
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
                "",                                 //             7  VERSION
                "",                                 //             8  UPDATE_TIMESTAMP
                "",                                 //             9  CREATE_TIMESTAMP
                source == null ? DataStoreSource.ALL.toString() : source.toString()                  //             10 SOURCE
        );


    }

    public DataStoreSource source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsRequest)) return false;
        if (!super.equals(o)) return false;

        StatsRequest that = (StatsRequest) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StatsRequest{" +
                "clientId='" + clientId + '\'' +
                "} " + super.toString();
    }


}
