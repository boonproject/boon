package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Lists;
import org.boon.Str;
import org.boon.StringScanner;

import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/15/14.
 */
public class MethodCall extends DataStoreRequest {

    private final String clientId;
    private final String object;


    private final String method;
    private final boolean prettyPrint;
    private List<String> arguments;


    public MethodCall(String clientId, long messageId, String object, String method, boolean prettyPrint, String... arguments) {
        super(messageId, Action.METHOD_CALL);
        this.clientId = clientId;
        this.object = object;
        this.method = method;

        this.arguments = Lists.list(arguments);
        this.prettyPrint = prettyPrint;
    }

    public static MethodCall parse(String message) {

        PreambleOfRequest preamble = new PreambleOfRequest();


        final String[] messageParts = StringScanner.split(message, ProtocolConstants.DELIMITER);

        parsePreamble(preamble, messageParts);

        final String clientId = messageParts[ProtocolConstants.Version1.Preamble.CLIENT_ID_POSITION];

        final String objectName = messageParts[7];

        final String methodName = messageParts[8];


        final boolean prettyPrint = messageParts[9].charAt(0) == 't';

        if (messageParts.length == 11) {

            final String args = messageParts[10];


            final String[] argList = StringScanner.split(args, ProtocolConstants.RECORD_DELIM);


            return new MethodCall(clientId, preamble.messageId(), objectName, methodName, prettyPrint, argList);
        } else {

            return new MethodCall(clientId, preamble.messageId(), objectName, methodName, prettyPrint);
        }
    }

    public static MethodCall parse(Action action, Map<String, String> message) {
        PreambleOfRequest preamble = new PreambleOfRequest();


        parsePreamble(message, preamble);


        final String methodName = message.get("method");

        final String objectName = message.get("object");

        final String args = message.get("arguments");

        final String clientId = message.get(ProtocolConstants.CLIENT_ID_MAP_KEY);


        final String[] split = StringScanner.split(args, ProtocolConstants.HUMAN_RECORD_DELIM.charAt(0));

        return new MethodCall(clientId, preamble.messageId(), objectName, methodName, true, split);
    }

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public String key() {
        die();
        return null;
    }

    @Override
    void key(String key) {
        die();
    }

    @Override
    public String payload() {
        die();
        return null;
    }

    @Override
    void clientId(String clientId) {

        die();
    }

    @Override
    void objectVersion(long objectVersion) {
        die();
    }

    @Override
    void updateTimeStamp(long ts) {
        die();
    }

    @Override
    void createTimeStamp(long ts) {
        die();
    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {

        die();

    }

    @Override
    public String formTextRequest() {
        return Str.joinObjects(ProtocolConstants.DELIMITER,
                ProtocolConstants.VERSION_1,        //             0  PROTOCOL VERSION
                action.verb(),                      //             1  ACTION
                "",                                 //             2  AUTH_TOKEN
                "",                                 //             3  HEADER
                "",                                 //             4  RESERVED
                clientId,                           //             5  CLIENT ID
                messageId,                          //             6  MESSAGE_ID
                object,                             //             7  OBJECT NAME
                method,                             //             8  METHOD NAME
                prettyPrint ? 't' : 'f',                             //             9  Pretty print

                Str.joinCollection(ProtocolConstants.RECORD_DELIM, arguments) //             10  ARGUMENTS
        );


    }

    public String object() {
        return object;
    }

    public String method() {
        return method;
    }

    public List<String> arguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodCall)) return false;
        if (!super.equals(o)) return false;

        MethodCall that = (MethodCall) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodCall{" +
                "clientId='" + clientId + '\'' +
                ", object='" + object + '\'' +
                ", method='" + method + '\'' +
                ", arguments=" + arguments +
                "} " + super.toString();
    }


    public boolean isPrettyPrint() {
        return prettyPrint;
    }

}
