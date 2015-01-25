package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.Boon;
import org.junit.Test;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/15/14.
 */
public class MethodCallTest {

    @Test
    public void testFromTextNoArgs() {

        MethodCall call = new MethodCall("me", 1L, "admin", "jmxStatus", true);

        String text = call.formTextRequest();

        puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(text));


        final MethodCall call2 = MethodCall.parse(text);

        Boon.equalsOrDie("Calls should be the same", call.method(), call2.method());

        Boon.equalsOrDie("Calls should be the same", call.clientId(), call2.clientId());

        Boon.equalsOrDie("Calls should be the same", call.object(), call2.object());


        Boon.equalsOrDie("Calls should be the same", call.arguments().size(), call2.arguments().size());

        Boon.equalsOrDie("Calls should be the same", call, call2);

    }


    @Test
    public void testFromTextManyArgs() {

        MethodCall call = new MethodCall("me", 1L, "admin", "jmxStatus", true, "1", "boolean", "2");

        String text = call.formTextRequest();

        puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(text));


        final MethodCall call2 = MethodCall.parse(text);

        Boon.equalsOrDie("Calls should be the same", call.method(), call2.method());

        Boon.equalsOrDie("Calls should be the same", call.clientId(), call2.clientId());

        Boon.equalsOrDie("Calls should be the same", call.object(), call2.object());


        Boon.equalsOrDie("Calls should be the same", call.arguments().size(), call2.arguments().size());

        Boon.equalsOrDie("Calls should be the same", call, call2);

    }
}
