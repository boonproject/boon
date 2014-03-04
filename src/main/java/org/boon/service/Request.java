package org.boon.service;


/**
 * @author Rick Hightower
 * Generic request object for request response handling over websocket, http, mq, etc.
 */
public class Request {



    final String method; //could be GET, POST, or loadUser
    final Object headers; //could be map or list or object
    final Object params; //could be map or list or object
    final Object payload;  //Could be JSON payload, XML or Java objects or buffer
    final String path; //could be URL, URI, or some sort of address

    final long correlationId; //used to match requests with responses.

    public Request(String method, Object headers, Object params, Object payload, String path) {
        this.method = method;
        this.params = params;
        this.headers = headers;
        this.payload = payload;
        this.path = path;
        this.correlationId = -1; //-1 means none
    }


}
