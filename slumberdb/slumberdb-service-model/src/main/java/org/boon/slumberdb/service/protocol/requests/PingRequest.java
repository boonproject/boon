package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.Str;
import org.boon.concurrent.Timer;

/**
 * Created by Richard on 9/3/14.
 */
public class PingRequest extends BaseDataStoreRequest {

    public static final PingRequest SINGLETON = new PingRequest();

    public static String PING = ProtocolConstants.VERSION_1 + ProtocolConstants.DELIMITER_STR + Action.PING.verb();

    public static boolean isPing(String message) {
        return message.startsWith(PING);
    }

    @Override
    public String formTextRequest() {
        return Str.join(ProtocolConstants.DELIMITER,
                ProtocolConstants.VERSION_1,        //             0  PROTOCOL VERSION
                Action.PING.verb(),              //             1  ACTION
                "" + Timer.timer().time()
        );
    }


}
