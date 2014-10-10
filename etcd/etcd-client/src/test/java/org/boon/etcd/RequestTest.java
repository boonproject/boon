package org.boon.etcd;

import org.junit.Before;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

public class RequestTest {

    Request request;

    boolean ok;

    @Before
    public void before() {
        request = new Request();
        request.host("localhost").port(8080).key("mykey");
    }

    @Test
    public void testDelete() {

        request.dir(true).methodDELETE().recursive(true);


        puts(request.toString());

        String control = "http://localhost:8080::DELETE/v2/keys/mykey\n" +
                "REQUEST_BODY\n" +
                "\trecursive=true&dir=true";

        ok = control.equals(request.toString()) || die();
    }



    @Test
    public void testGet() {

        request.dir(true).methodGET().recursive(true).wait(true).prevExist(true).prevIndex(99).prevValue("prevValue").value("value");


        puts(request.toString());

        String control = "http://localhost:8080/v2/keys/mykey?" +
                "prevValue=prevValue&value=value&prevIndex=99&wait=true&recursive=true&prevExist=true&dir=true";

        ok = control.equals(request.toString()) || die();
    }
}