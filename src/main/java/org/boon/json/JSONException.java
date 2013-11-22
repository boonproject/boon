package org.boon.json;

public class JSONException extends RuntimeException {

    static void handleException( Exception ex ) {
        throw new JSONException ( ex );
    }

    public JSONException( ) {
    }

    public JSONException( String message, Throwable cause ) {
        super ( message, cause );
    }

    public JSONException( String message ) {
        super ( message );
    }

    public JSONException( Throwable cause ) {
        super ( cause );
    }

}