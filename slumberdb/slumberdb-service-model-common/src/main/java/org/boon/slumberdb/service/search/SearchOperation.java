package org.boon.slumberdb.service.search;

/**
 * @author JD
 */
public enum SearchOperation {
    AND {
        public boolean op(boolean a, boolean b) {
            return a && b;
        }
    },
    OR {
        public boolean op(boolean a, boolean b) {
            return a | b;
        }
    };


}
