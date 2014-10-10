package org.boon.etcd;

import org.boon.Exceptions;
import org.boon.Str;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by rhightower on 10/9/14.
 */
public class Request {

    private final static String URI_STUB = "/v2/keys/";

    private final static String UTF_8 = StandardCharsets.UTF_8.displayName();

    private boolean wait;
    private long waitIndex;
    private boolean recursive;
    private boolean sorted;
    private String key;
    private long ttl;
    private boolean dir;

    private boolean consistent;


    private String value;
    private String method = "GET";


    private String host;
    private int port;

    private boolean prevExist;
    private String prevValue;
    private long prevIndex;
    private boolean emptyTTL;


    public static Request request () {
        return new Request();
    }


    public boolean isWait() {
        return wait;
    }

    public Request wait(boolean wait) {
        this.wait = wait;
        return this;
    }

    public long waitIndex() {
        return waitIndex;
    }

    public Request waitIndex(long waitIndex) {
        this.waitIndex = waitIndex;
        return this;
    }

    public boolean recursive() {
        return recursive;
    }

    public Request recursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }



    public boolean consistent() {
        return consistent;
    }

    public Request consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public boolean sorted() {
        return sorted;
    }

    public Request sorted(boolean sorted) {
        this.sorted = sorted;
        return this;
    }

    public String key() {
        return key;
    }

    public Request key(String key) {
        this.key = key;
        return this;
    }

    public long ttl() {
        return ttl;
    }

    public Request ttl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    public long prevIndex() {
        return prevIndex;
    }

    public Request prevIndex(long prevIndex) {
        this.prevIndex = prevIndex;
        return this;
    }

    public boolean prevExist() {
        return prevExist;
    }

    public Request prevExist(boolean prevExist) {
        this.prevExist = prevExist;
        return this;
    }

    public boolean dir() {
        return dir;
    }

    public Request dir(boolean dir) {
        this.dir = dir;
        return this;
    }

    public String value() {
        return value;
    }

    public Request value(String value) {
        this.value = value;
        return this;
    }

    public String prevValue() {
        return prevValue;
    }

    public Request prevValue(String prevValue) {
        this.prevValue = prevValue;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Request methodGET() {
        this.method = "GET";
        return this;
    }

    public Request methodPUT() {
        this.method = "PUT";
        return this;
    }


    public Request methodPOST() {
        this.method = "POST";
        return this;
    }

    public Request methodDELETE() {
        this.method = "DELETE";
        return this;
    }

    public String uri() {

        StringBuilder builder = new StringBuilder(80);
        builder.append(URI_STUB).append(key);

        if (this.method.equals("GET")) {

            builder.append("?");

            paramBody(builder);
        }

        return  builder.toString();
    }


    public String paramBody() {
        StringBuilder builder = new StringBuilder(80);
        paramBody(builder);
        return builder.toString();
    }

    private void paramBody(StringBuilder builder) {

        boolean first = true;

        if (!Str.isEmpty(prevValue)) {
            builder.append("prevValue=").append(encode(prevValue));
            first=false;
        }

        if (!Str.isEmpty(value)) {
            if (!first) builder.append("&");
            builder.append("value=").append(encode(value));
            first=false;
        }


        if ( ttl > 0 ) {
            if (!first) builder.append("&");
            builder.append("ttl=").append(ttl);
            first=false;
        }


        if ( waitIndex > 0 ) {
            if (!first) builder.append("&");
            builder.append("waitIndex=").append(waitIndex);
            first=false;
        }

        if ( prevIndex > 0 ) {
            if (!first) builder.append("&");
            builder.append("prevIndex=").append(prevIndex);
            first=false;
        }

        if ( wait ) {
            if (!first) builder.append("&");
            builder.append("wait=true");
            first=false;
        }

        if ( recursive ) {
            if (!first) builder.append("&");
            builder.append("recursive=true");
            first=false;
        }

        if ( sorted ) {
            if (!first) builder.append("&");
            builder.append("sorted=true");
            first=false;
        }


        if ( emptyTTL ) {
            if (!first) builder.append("&");
            builder.append("ttl=");
            first=false;
        }

        if ( prevExist ) {
            if (!first) builder.append("&");
            builder.append("prevExist=true");
            first=false;
        }

        if ( dir ) {
            if (!first) builder.append("&");
            builder.append("dir=true");
            first=false;
        }


        if ( consistent ) {
            if (!first) builder.append("&");
            builder.append("consistent=true");
            first=false;
        }




    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            Exceptions.handle(e);
            return "";
        }
    }

    public String host() {
        return host;
    }

    public Request host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public Request port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public String toString() {
        if (method.equals("GET")) {
            return Str.add("http://", host, ":" + port, uri());
        } else {
            return Str.add("http://",host, ":" + port, "::", method, uri(), "\nREQUEST_BODY\n\t", paramBody());
        }
    }

    public Request emptyTTL() {
        this.emptyTTL = true;

        return this;
    }
}
