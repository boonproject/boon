package org.boon.slumberdb.service.protocol;

import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.Lists;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/3/14.
 */
public class ParseProtocolTest {

    boolean ok;


    @Test
    public void parseUnparseGet() throws Exception {

        GetRequest getRequest = new GetRequest(Action.GET, 1L, "clientId123", 2L, 3L, 4L, "foo");
        puts(getRequest);

        puts(getRequest.formTextRequest().replace(ProtocolConstants.DELIMITER, '\n'));


        GetRequest getRequestAfter = GetRequest.parse(getRequest.formTextRequest());

        puts(getRequestAfter);

        ok = getRequestAfter.equals(getRequest) || die();


    }


    @Test
    public void parseUnparseGetMem() throws Exception {

        GetRequest getRequest = new GetRequest(Action.GET_MEM, 1L, "clientId123", 2L, 3L, 4L, "foo");
        puts(getRequest);

        puts(getRequest.formTextRequest().replace(ProtocolConstants.DELIMITER, '\n'));


        GetRequest getRequestAfter = GetRequest.parse(getRequest.formTextRequest());

        puts(getRequestAfter);

        ok = getRequestAfter.equals(getRequest) || die();


    }


    @Test
    public void parsePing() throws Exception {

        PingRequest ping = PingRequest.SINGLETON;

        puts(ping.formTextRequest());

        puts(PingRequest.PING);

        ok = PingRequest.isPing(ping.formTextRequest()) || die();

    }

    @Test
    public void parseUnparseSet() throws Exception {

        SetRequest getRequest = new SetRequest(Action.SET_BROADCAST, 1L, "clientId123", 2L, 3L, 4L, "foo", "bar");
        puts(getRequest);

        puts(getRequest.formTextRequest().replace(ProtocolConstants.DELIMITER, '\n'));


        SetRequest getRequestAfter = SetRequest.parse(getRequest.formTextRequest());

        puts(getRequestAfter);

        ok = getRequestAfter.equals(getRequest) || die();


    }


    @Test
    public void parseUnparseBatch() throws Exception {

        ReadBatchRequest request = new ReadBatchRequest(1L, "PE-instance1", "key1", "key2", "key3");
        puts(request);

        puts(request.formTextRequest().replace(ProtocolConstants.DELIMITER, '\n'));


        ReadBatchRequest requestAfter = ReadBatchRequest.parse(request.formTextRequest());

        puts(requestAfter);

        ok = requestAfter.equals(request) || die();


    }


    @Test
    public void parseBatchSet() throws Exception {

        BatchSetRequest request = new BatchSetRequest(1L, "PE-instance1", Lists.list("key1", "key2", "key3"),
                Lists.list("v1", "v2", "v3"));
        puts("BEFORE", request);


        String textRequest = request.formTextRequest();

        puts("CONVERTED TO TEXT", ProtocolConstants.prettyPrintMessageWithLinesTabs(textRequest));

        BatchSetRequest requestAfter = BatchSetRequest.parse(textRequest);

        puts(requestAfter);

        ok = requestAfter.equals(request) || die();


    }

}
