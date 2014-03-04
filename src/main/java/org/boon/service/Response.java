package org.boon.service;

import org.boon.core.Conversions;

import java.util.Map;

/**
 * Created by Richard on 3/3/14.
 */
public class Response {

    private final int status; //200 Ok, 500 error, etc. may not be HTTP could be some other scheme, but most likely HTTP codes
    private final Object headers; //could be map or list or object or JSON string
    private final Object statusMessage; //Could be "OK" or the message from a java exception
    private final Object payload;
    private final Class<? extends Enum> enumStatusClass;


    public Response(int status, Object headers, Object statusMessage, Object payload) {
        this.status = status;
        this.headers = headers;
        this.statusMessage = statusMessage;
        this.payload = payload;
        enumStatusClass = null;
    }


    public Response(int status, Object headers, Object statusMessage, Object payload, Class<? extends Enum> enumStatusClass) {
        this.status = status;
        this.headers = headers;
        this.statusMessage = statusMessage;
        this.payload = payload;
        this.enumStatusClass = enumStatusClass;
    }


    public int status() {
        return status;
    }


    public <E extends Enum> E  statusEnum(Class<E> enumClass) {
        return Conversions.toEnum(enumClass, status);
    }


    public Enum  statusEnum() {
        return Conversions.toEnum(this.enumStatusClass, status);
    }

    public Object headers() {
        return headers;
    }


    public Map<String, Object> headerMap() {
        return Conversions.toMap(headers);
    }

    public Object statusMessage() {
        return statusMessage;
    }


    public String statusMessageAsString() {
        return Conversions.toString(statusMessage);
    }


    public Object payload() {
        return payload;
    }


    public String payloadAsString() {
        return Conversions.toString(payload);
    }

    public static Response response(int status, Map headers, String statusMessage, String payload) {
        return new Response(status, headers, statusMessage, payload);
    }
}
