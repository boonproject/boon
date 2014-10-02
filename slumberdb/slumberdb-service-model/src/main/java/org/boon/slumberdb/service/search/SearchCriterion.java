package org.boon.slumberdb.service.search;

/**
 * @author JD
 */
public class SearchCriterion {
    private String criterion;
    private SearchType type;
    private SearchOperation op;

    public SearchCriterion(String criterion, SearchType type, SearchOperation op) {
        this.criterion = criterion;
        this.type = type;
        this.op = op;
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public SearchOperation getOp() {
        return op;
    }

    public void setOp(SearchOperation op) {
        this.op = op;
    }
}
