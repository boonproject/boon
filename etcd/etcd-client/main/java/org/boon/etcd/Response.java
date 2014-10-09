package org.boon.etcd;

import org.boon.Boon;

/**
 * Created by rhightower on 10/8/14.
 */
public class Response {
    private final String action;
    private final Node node;
    private final Error error;

    private  int responseCode;
    private boolean created;


    public Response(String action, int responseCode, Node node) {
        this.action = action;
        this.node = node;
        this.error = null;

        this.responseCode = responseCode;
    }


    public Response(String action, int responseCode, Error error) {
        this.action = action;
        this.node = null;
        this.error = error;

        this.responseCode = responseCode;
    }



    public boolean wasError() {
        return error !=null;
    }
    public String action() {
        return action;
    }

    public Node node() {
        return node;
    }


    public int responseCode() {
        return responseCode;
    }


    @Override
    public String toString() {
        return Boon.toPrettyJson(this);
    }

    public void setHttpStatusCode(int httpStatusCode) {

        responseCode = httpStatusCode;
    }

    public void setCreated() {
        this.created = true;
    }
}
