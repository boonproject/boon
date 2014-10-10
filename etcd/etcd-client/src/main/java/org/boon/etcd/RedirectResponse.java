package org.boon.etcd;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by rhightower on 10/9/14.
 */
public class RedirectResponse extends Response {
    URI location;

    public RedirectResponse(String location) {
        super("REDIRECT", 307, (Error) null);
        try {
            this.location = new URI(location);
        } catch (URISyntaxException e) {
            this.location = null;
        }
    }


    public URI location() {
        return location;
    }


    public boolean successful() {
        return false;
    }


}
