package org.boon.slumberdb.service.search;

import java.util.ArrayList;
import java.util.List;

/**
 * JD
 */
public class SearchContext {
    private List<SearchCriterion> criteria;
    private int limit;
    private int offset;


    public SearchContext(List<SearchCriterion> criteria, int limit, int offset) {
        this.criteria = criteria;
        this.limit = limit;
        this.offset = offset;
    }

    public List<SearchCriterion> getCriteria() {
        if (this.criteria == null) {
            this.criteria = new ArrayList<>();
        }
        return criteria;
    }

    public void setCriteria(List<SearchCriterion> criteria) {
        this.criteria = criteria;
    }

    public void addCriterion(SearchCriterion criterion) {
        if (this.criteria == null) {
            this.criteria = new ArrayList<>();
        }
        this.criteria.add(criterion);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
