package org.boon.etcd;

import org.boon.Boon;

/**
 * Created by rhightower on 10/8/14.
 */
public class Error {

    private final int errorCode;
    private final String cause;
    private final String message;
    private final long index;

    public Error(int errorCode, String cause, String message, long index) {
        this.errorCode = errorCode;
        this.cause = cause;
        this.message = message;
        this.index = index;
    }

    public int errorCode() {
        return errorCode;
    }

    public String cause() {
        return cause;
    }

    public String message() {
        return message;
    }

    public long index() {
        return index;
    }

    @Override
    public String toString() {
        return Boon.toPrettyJson(this);
    }
}
