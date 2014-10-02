package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.search.BaseSearchHandler;
import org.boon.slumberdb.service.search.SearchCriterion;
import org.boon.slumberdb.service.search.SearchHandler;
import org.boon.slumberdb.service.search.SearchType;
import org.boon.slumberdb.stores.DataStoreSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author JD
 */
public class SearchRequest extends DataStoreRequest {
    protected String clientId;
    protected DataStoreSource source;
    private ObjectId objectId = new ObjectId();
    private SearchHandler handler;
    private List<SearchCriterion> criteria;
    private int offset;
    private int limit;

    public SearchRequest() {
    }

    ;

    public static SearchRequest parse(Action action, Map<String, String> message) {
        SearchRequest request = new SearchRequest();

        request.action = action;

        parsePreamble(message, request);
        parseObjectIdInfo(request, message);


        request.offset = Integer.parseInt(message.get(ProtocolConstants.Search.OFFSET_KEY));
        request.limit = setLimit(message.get(ProtocolConstants.Search.LIMIT_KEY));
        request.criteria = setCriterion(message.get(ProtocolConstants.Search.CRITERIA_KEY));
        request.handler = setHandler(message.get(ProtocolConstants.Search.HANDLER_KEY));


        return request;
    }

    private static int setLimit(String limit) {
        if (null == limit || limit.equals("")) {
            return ProtocolConstants.Search.LIMIT_VALUE;
        }
        return Integer.parseInt(limit);
    }

    private static List<SearchCriterion> setCriterion(String criterion) {
        List<SearchCriterion> criteria = new ArrayList<>();
        criteria.add(new SearchCriterion(criterion, SearchType.KEY, null));
        criteria.add(new SearchCriterion(criterion, SearchType.VALUE, null));

        return criteria;
    }

    private static SearchHandler setHandler(String className) {
        SearchHandler handler;

        try {
            handler = (SearchHandler) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            handler = new BaseSearchHandler();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            handler = new BaseSearchHandler();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            handler = new BaseSearchHandler();
            e.printStackTrace();
        }
        return handler;
    }

    public SearchHandler handler() {
        return handler;
    }

    public void handler(SearchHandler handler) {
        this.handler = handler;
    }

    public int limit() {
        return limit;
    }

    public void limit(int limit) {
        this.limit = limit;
    }

    public int offset() {
        return offset;
    }

    public void offset(int offset) {
        this.offset = offset;
    }

    public List<SearchCriterion> criteria() {
        return criteria;
    }

    public void criteria(List<SearchCriterion> criteria) {
        this.criteria = criteria;
    }

    @Override
    public String clientId() {
        return this.clientId;
    }

    @Override
    public String key() {
        return "search";
    }

    @Override
    void key(String key) {

    }

    @Override
    public String payload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String formTextRequest() {
        return null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SearchRequest that = (SearchRequest) o;

        if (offset != that.offset) return false;
        if (!clientId.equals(that.clientId)) return false;
        if (!criteria.equals(that.criteria)) return false;
        if (!handler.equals(that.handler)) return false;
        if (!objectId.equals(that.objectId)) return false;
        if (source != that.source) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + objectId.hashCode();
        result = 31 * result + criteria.hashCode();
        result = 31 * result + handler.hashCode();
        result = 31 * result + offset;
        result = 31 * result + clientId.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "objectId=" + objectId +
                ", criteria='" + criteria + '\'' +
                ", handler='" + handler + '\'' +
                ", offset=" + offset +
                ", clientId='" + clientId + '\'' +
                ", source=" + source +
                '}';
    }
}
