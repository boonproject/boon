package org.boon.json;

public class JsonException extends RuntimeException {

    static void handleException ( Exception ex ) {
        throw new JsonException ( ex );
    }

    public JsonException () {
    }

    public JsonException ( String message, Throwable cause ) {
        super ( message, cause );
    }

    public JsonException ( String message ) {
        super ( message );
    }

    public JsonException ( Throwable cause ) {
        super ( cause );
    }

}