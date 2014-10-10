package org.boon.etcd.exceptions;

/**
 * Created by rhightower on 10/8/14.
 */
public class EtcdException extends RuntimeException{

    public EtcdException() {
    }

    public EtcdException(String message) {
        super(message);
    }

    public EtcdException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdException(Throwable cause) {
        super(cause);
    }

    public EtcdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
