package org.boon.slumberdb.service.protocol.factory;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.Maps;

import java.util.Map;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/9/14.
 */
public class ByTextFactory {


    /**
     * Created by Richard on 9/2/14.
     */
    public static class BatchReadRequestFastTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return ReadBatchRequest.parse(message);
        }
    }

    /**
     * Created by Richard on 9/3/14.
     */
    public static class BatchSetRequestByTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return BatchSetRequest.parse(message);
        }
    }


    /**
     * Created by Richard on 9/2/14.
     */
    public static class FastTextProtocolFactory implements RequestFactory<String, DataStoreRequest> {


        private Map<Action, RequestFactory> mapping = Maps.map(
                (Action) Action.BATCH_READ, (RequestFactory) new BatchReadRequestFastTextFactory(),
                Action.GET, new GetRequestFromTextFactory(),
                Action.GET_LOCAL_DB, new GetRequestFromTextFactory(),
                Action.GET_MEM, new GetRequestFromTextFactory(),
                Action.GET_SOURCE, new GetRequestFromTextFactory(),
                Action.SET, new SetRequestFromTextFactory(),
                Action.SET_SOURCE, new SetRequestFromTextFactory(),
                Action.SET_BROADCAST, new SetRequestFromTextFactory(),
                Action.SET_IF_NOT_EXIST, new SetRequestFromTextFactory(),
                Action.SET_BATCH, new BatchSetRequestByTextFactory(),
                Action.SET_BATCH_IF_NOT_EXISTS, new BatchSetRequestByTextFactory(),
                Action.QUERY, new QueryByTextFactory(),
                Action.QUERY_FOR_KEYS, new QueryByTextFactory(),
                Action.REMOVE, new RemoveByTextFactory(),
                Action.REMOVE_SOURCE, new RemoveByTextFactory(),
                Action.GET_STATS, new GetStatsByTextFactory(),
                Action.CLEAR_STATS, new GetStatsByTextFactory(),
                Action.METHOD_CALL, new MethodCallFactory()
        );


        @Override
        public DataStoreRequest createRequest(String message) {

            Action action = PreambleOfRequest.action(message);
            RequestFactory factory = mapping.get(action);

            if (factory == null) {
                die("Factory for message not found", action, "\n\n", "MESSAGE\n", message, "END MESSAGE");
            }
            return (DataStoreRequest) factory.createRequest(message);
        }
    }

    /**
     */
    public static class MethodCallFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return MethodCall.parse(message);
        }
    }

    /**
     * Created by Richard on 9/2/14.
     */
    public static class GetRequestFromTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return GetRequest.parse(message);

        }
    }


    /**
     * Created by Richard on 9/8/14.
     */
    public static class QueryByTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {

            return QueryRequest.parse(message);
        }
    }


    /**
     * Created by Richard on 9/8/14.
     */
    public static class GetStatsByTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return StatsRequest.parse(message);
        }

    }


    /**
     * Created by Richard on 9/8/14.
     */
    public static class RemoveByTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {

            return RemoveRequest.parse(message);
        }
    }

    /**
     * Created by Richard on 9/2/14.
     */
    public static class SetRequestFromTextFactory implements RequestFactory<String, DataStoreRequest> {
        @Override
        public DataStoreRequest createRequest(String message) {
            return SetRequest.parse(message);
        }
    }
}
