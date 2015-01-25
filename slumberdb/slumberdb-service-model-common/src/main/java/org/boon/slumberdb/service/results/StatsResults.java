package org.boon.slumberdb.service.results;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ActionResponse;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.requests.StatsRequest;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;

import java.util.Map;

import static org.boon.slumberdb.service.protocol.ProtocolConstants.DELIMITER;

/**
 * Created by Richard on 9/9/14.
 */
public class StatsResults extends Stat {
    private static ThreadLocal<JsonSerializer> jsonSerializerThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<JsonParserAndMapper> jsonMappingParser = new ThreadLocal<>();
    private final StatsRequest request;
    private Map<String, StatCount> results;

    public StatsResults(StatsRequest request) {
        this.request = request;
    }

    private static StatsResults fromJson(String text) {
        if (jsonMappingParser.get() == null) {
            jsonMappingParser.set(new JsonParserFactory().create());
        }

        return jsonMappingParser.get().parse(StatsResults.class, text);
    }

    public static StatsResults fromTextMessage(String text) {


        final String[] split = Str.split(text, DELIMITER);
        String body = split[5];

        final StatsResults statsResults = fromJson(body);
        return statsResults;


    }

    public StatsRequest request() {
        return request;
    }

    public Map<String, StatCount> getResults() {
        return results;
    }

    public void setResults(Map<String, StatCount> results) {
        this.results = results;
    }

    private String toJson() {
        if (jsonSerializerThreadLocal.get() == null) {
            jsonSerializerThreadLocal.set(new JsonSerializerFactory().create());
        }

        return jsonSerializerThreadLocal.get().serialize(this).toString();
    }

    public String toTextMessage() {

        return Str.joinObjects(ProtocolConstants.GROUP_DELIM,
                ActionResponse.STATS.responseHeader(), //0
                request.clientId(), //1
                request.messageId(), //2
                source == null ? DataStoreSource.ALL : source, //3
                request.action() == null ? Action.GET_STATS : request.action(), //4
                toJson());//5
    }
}
