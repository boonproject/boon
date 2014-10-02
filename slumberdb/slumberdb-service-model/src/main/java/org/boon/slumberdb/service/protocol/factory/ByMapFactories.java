package org.boon.slumberdb.service.protocol.factory;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.Maps;

import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Maps.entry;

/**
 * Created by Richard on 9/9/14.
 */
public class ByMapFactories {


    /**
     * Created by Richard on 9/2/14.
     */
    public static class MapProtocolFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private Map<Action, RequestFactory> mapping = Maps.mapByEntries(
                entry(
                        Action.BATCH_READ,
                        (RequestFactory) new BatchReadRequestFromMapFactory(Action.BATCH_READ)),
                entry(
                        Action.GET,
                        (RequestFactory) new GetRequestFromMapFactory(Action.GET)),
                entry(
                        Action.GET_LOCAL_DB,
                        (RequestFactory) new GetRequestFromMapFactory(Action.GET_LOCAL_DB)),
                entry(
                        Action.GET_MEM,
                        (RequestFactory) new GetRequestFromMapFactory(Action.GET_MEM)),
                entry(
                        Action.GET_SOURCE,
                        (RequestFactory) new GetRequestFromMapFactory(Action.GET_SOURCE)),
                entry(
                        Action.SET,
                        (RequestFactory) new SetRequestFromMapFactory(Action.SET)),
                entry(
                        Action.SET_SOURCE,
                        (RequestFactory) new SetRequestFromMapFactory(Action.SET_SOURCE)),
                entry(
                        Action.SET_BROADCAST,
                        (RequestFactory) new SetRequestFromMapFactory(Action.SET_BROADCAST)),
                entry(
                        Action.SET_IF_NOT_EXIST,
                        (RequestFactory) new SetRequestFromMapFactory(Action.SET_IF_NOT_EXIST)),
                entry(
                        Action.SET_BATCH,
                        (RequestFactory) new BatchSetRequestFromMapFactory(Action.SET_BATCH)),
                entry(
                        Action.SET_BATCH_IF_NOT_EXISTS,
                        (RequestFactory) new BatchSetRequestFromMapFactory(Action.SET_BATCH_IF_NOT_EXISTS)),
                entry(
                        Action.QUERY_FOR_KEYS,
                        (RequestFactory) new QueryByMapFactory(Action.QUERY_FOR_KEYS)),
                entry(
                        Action.QUERY,
                        (RequestFactory) new QueryByMapFactory(Action.QUERY)),
                entry(
                        Action.REMOVE,
                        (RequestFactory) new RemoveByMapFactory(Action.REMOVE)

                ),
                entry(
                        Action.REMOVE_SOURCE,
                        (RequestFactory) new RemoveByMapFactory(Action.REMOVE_SOURCE)

                ),
                entry(
                        Action.SEARCH,
                        (RequestFactory) new SearchRequestFromMapFactory(Action.SEARCH)

                ),
                entry(
                        Action.GET_STATS,
                        (RequestFactory) new GetStatsFromMapFactory(Action.GET_STATS)

                ),
                entry(
                        Action.METHOD_CALL,
                        (RequestFactory) new MethodCallFromMapFactory(Action.METHOD_CALL)

                )
        );


        public static Action action(Map<String, String> message) {

            String actionKey = message.get(ProtocolConstants.ACTION_MAP_KEY);
            return ProtocolConstants.actionMap.get(actionKey);
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            Action action = action(message);
            RequestFactory factory = mapping.get(action);

            if (factory == null) {
                die("Factory for message not found for MAP", action, "\n\n", "MESSAGE\n", message, "END MESSAGE");
            }
            return factory.createRequest(message);
        }
    }


    public static class MethodCallFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private final Action action;

        public MethodCallFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            return MethodCall.parse(action, message);

        }
    }

    /**
     * Created by Richard on 9/5/14.
     */
    public static class BatchSetRequestFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private final Action action;

        public BatchSetRequestFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            return BatchSetRequest.parse(action, message);

        }
    }

    /**
     * Created by Richard on 9/5/14.
     */
    public static class BatchReadRequestFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {

        private final Action action;

        public BatchReadRequestFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {
            return ReadBatchRequest.parse(action, message);
        }
    }

    /**
     * Created by Richard on 9/5/14.
     */
    public static class GetRequestFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private final Action action;

        public GetRequestFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            return GetRequest.parse(action, message);

        }
    }

    /**
     * Created by Richard on 9/8/14.
     */
    public static class GetStatsFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private final Action action;

        public GetStatsFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            return StatsRequest.parse(action, message);

        }
    }

    /**
     * Created by Richard on 9/8/14.
     */
    public static class QueryByMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {

        private final Action action;

        public QueryByMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {
            return QueryRequest.parse(action, message);


        }
    }

    /**
     * Created by Richard on 9/8/14.
     */
    public static class RemoveByMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {

        private final Action action;

        public RemoveByMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {
            return RemoveRequest.parse(action, message);


        }
    }

    /**
     * Created by Richard on 9/5/14.
     */
    public static class SetRequestFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {


        private final Action action;

        public SetRequestFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {

            return SetRequest.parse(action, message);

        }
    }

    /**
     * Created by JD on 9/8/14.
     */
    public static class SearchRequestFromMapFactory implements RequestFactory<Map<String, String>, DataStoreRequest> {

        private final Action action;

        public SearchRequestFromMapFactory(Action action) {
            this.action = action;
        }

        @Override
        public DataStoreRequest createRequest(Map<String, String> message) {
            return SearchRequest.parse(action, message);
        }
    }
}
