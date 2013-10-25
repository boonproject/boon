package org.boon.core.json;

import org.boon.core.primitive.CharBuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.core.json.JSONParserState.*;

/**
 * Converts an input JSON String into Java objects works with Reader or String
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 */
public class JSONParser {

    private char[] charArray;
    private int __index;
    private int line;
    private int lastLineStart;
    private char __currentChar;
    private char __lastChar;

    private final boolean debug = false; // just used to debug if their are
    private Map<String, Object> lastObject;
    private List<Object> lastList;
    private JSONParserState state;
    private JSONParserState lastState;

    private JSONParser() {

    }

    public static Object decodeObject(String cs) {
        JSONParser p = new JSONParser();
        return p.decode(cs);

    }


    public static Map<String, Object> decodeMap(String cs) {
        JSONParser p = new JSONParser();
        return (Map<String, Object>) p.decode(cs);
    }

    public static <T> List<T> decodeList(Class<T> type, String cs) {
        JSONParser p = new JSONParser();
        return (List<T>) p.decode(cs);
    }


    public static Number decodeNumber(char[] cs) {
        JSONParser p = new JSONParser();
        return (Number) p.decode(cs);
    }


    @SuppressWarnings("unchecked")
    private Object decode(char[] cs) {
        charArray = cs;
        Object root = null;
        try {
            root = decodeValue();
        } catch (Exception e) {
            JSONException.handleException(e);
        }
        return root;
    }


    private Object decode(String cs) {
        charArray = cs.toCharArray();
        Object root = null;
        try {
            root = decodeValue();
        } catch (Exception e) {
            JSONException.handleException(e);
        }
        return root;
    }


    private final boolean safe() {

        return __index < charArray.length;
    }

    private final boolean hasMore() throws Exception {
        return __index + 1 < charArray.length;
    }

    private final char currentChar() throws Exception {

        if (safe()) {
            return __currentChar = charArray[__index];
        }
        return __currentChar;

    }

    private final char nextChar() throws Exception {

        try {
            if (hasMore()) {
                __lastChar = __currentChar;
                __index++;
                return __currentChar = charArray[__index];
            }
            return __currentChar;

        } catch (Exception ex) {
            throw new RuntimeException ( exceptionDetails("failure in next "+
            ex.getLocalizedMessage()), ex);

        }
    }

    private String exceptionDetails(String message) {
        CharBuf buf = CharBuf.create(255);

        buf.addLine(message);

        buf.addLine("");
        buf.addLine("The last character read was " + __lastChar);
        buf.addLine("The current character read is " + __currentChar);


        if (lastObject != null) {
            buf.addLine("The last object read was");
            buf.addLine("------------------------");
            buf.addLine(lastObject.toString());
            buf.addLine("------------------------");
        }

        if (lastList != null) {
            buf.addLine("The last array read was");
            buf.addLine("------------------------");
            buf.addLine(lastList.toString());
            buf.addLine("------------------------");
        }


        buf.addLine(message);
        buf.addLine("line number " + line);
        buf.addLine("index number " + __index);

        int lineLocationCount = __index - lastLineStart;

        buf.addLine(new String(charArray, lastLineStart, __index));
        for (int i = 0; i < lineLocationCount; i++) {
            if (lineLocationCount - 1 == i) {
                buf.add('^');
            } else {
                buf.add('.');
            }
        }
        return buf.toString();
    }

    private void skipWhiteSpace() throws Exception {

        int count = 0;

        while (hasMore()) {
            count++;

            currentChar();
            if (__currentChar == '\n') {
                line++;
                lastLineStart = __index;
                this.nextChar();
                continue;
            } else if (__currentChar == '\r') {
                line++;
                if (hasMore()) {
                    this.nextChar();
                    if (__currentChar != '\n') {
                        lastLineStart = __index;
                        break;
                    }
                }
                lastLineStart = __index;
                this.nextChar();
                continue;
            } else if (Character.isWhitespace(__currentChar)) {
                this.nextChar();
                continue;
            } else {
                break;
            }
        }

    }

    private Object decodeJsonObject() throws Exception {
        if (debug)
            System.out.println("decodeJsonObject enter"); //$NON-NLS-1$

        if (this.currentChar() == '{' && this.hasMore())
            this.nextChar();

        Map<String, Object> map = new LinkedHashMap<>();

        this.lastObject = map;

        do {

            skipWhiteSpace();

            char c = this.currentChar();

            if (c == '"') {
                String key = decodeKeyName();
                skipWhiteSpace();
                c = this.currentChar();
                if (c != ':') {

                    complain("expecting current character to be '" + c + "'\n");
                }
                c = this.nextChar(); // skip past ':'
                skipWhiteSpace();
                Object value = decodeValue();

                if (debug)
                    System.out
                            .printf("key:%s value:%s", key, value); //$NON-NLS-1$
                skipWhiteSpace();

                map.put(key, value);

                c = this.currentChar();
                if (!(c == '}' || c == ',')) {
                    complain("expecting '}' or ',' but got current char " + c);
                }
            }
            if (c == '}') {
                this.nextChar();
                break;
            } else if (c == ',') {
                this.nextChar();
                continue;
            } else {
                complain(
                        "expecting '}' or ',' but got current char " + c);

            }
        } while (this.hasMore());
        return map;
    }

    private void complain(String complaint) {
        throw new JSONException(exceptionDetails(complaint));
    }

    boolean lastValueJSONNull = false;

    private boolean wasJsonNull() {
        boolean was = lastValueJSONNull;
        lastValueJSONNull = false;
        return was;
    }

    private Object decodeValue() throws Exception {
        Object value = null;

        do  {
            char c = this.currentChar();
            if (c == '"') {
                value = decodeString();
                break;
            } else if (c == 't' || c == 'f') {
                value = decodeBoolean();
                break;
            } else if (c == 'n') {
                value = decodeNull();

                lastValueJSONNull = true;
                break;
            } else if (c == '[') {
                setState(START_LIST);
                value = decodeJsonArray();
                setState(END_LIST);
                break;
            } else if (c == '{') {
                setState(START_OBJECT);
                value = decodeJsonObject();
                setState(END_OBJECT);
                break;

            } else if (c == '-' || Character.isDigit(c)) {
                value = decodeNumber();
                break;
            }
            this.nextChar();
        } while (hasMore());
        skipWhiteSpace();
        return value;
    }

    private void setState(JSONParserState state) {
        this.lastState = this.state;
        this.state = state;
    }

    private Object decodeNumber() throws Exception {
        StringBuilder builder = new StringBuilder();

        boolean doubleFloat = false;
        do {
            char c = this.currentChar();
            if (Character.isWhitespace(c) || c == ',' || c == '}' || c == ']') {
                break;
            }
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E'
                    || c == '+' || c == '-') {
                if (c == '.' || c == 'e' || c == 'E') {
                    doubleFloat = true;
                }
                builder.append(c);
                this.nextChar();
                continue;
            }
            complain("expecting number char but got current char " + c);

        } while (this.hasMore());

        String svalue = builder.toString();
        Object value = null;
        try {
            if (doubleFloat) {
                value = Double.parseDouble(svalue);
            } else {
                value = Integer.parseInt(svalue);
            }
        } catch (Exception ex) {
            try {
                value = Long.parseLong(svalue);
            } catch (Exception ex2) {
                complain("expecting to decode a number but got value of "+ svalue);
            }

        }

        return value;

    }

    private int index() {
        return __index;
    }

    private Object decodeBoolean() throws Exception {
        StringBuilder builder = new StringBuilder();
        do {
            char c = this.currentChar();
            if (Character.isWhitespace(c) || c == ',' || c == '}') {
                break;
            }
            builder.append(c);
            this.nextChar();
        } while (hasMore());
        return Boolean.parseBoolean(builder.toString());
    }

    private Object decodeNull() throws Exception {
        StringBuilder builder = new StringBuilder();
        do {
            char c = this.currentChar();
            if (Character.isWhitespace(c) || c == ',' || c == '}') {
                break;
            }
            builder.append(c);
            this.nextChar();
        } while (hasMore());
        return null;
    }

    private Object decodeString() throws Exception {
        String value = null;

        int startIndex = index();
        do {
            char c = this.nextChar();
            if (c == '"') {
                break;
            }
            if (c == '\\' && (c = this.nextChar()) == '"') {
                continue;
            }

        } while (hasMore());

        value = encodeString(startIndex, index());
        this.nextChar(); // skip other quote

        return value;
    }

    private String encodeString(int start, int to) throws Exception {
        return JSONStringParser.decode(charArray, start, to);
    }

    private String decodeKeyName() throws Exception {

        StringBuilder builder = new StringBuilder();
        do {
            char c = this.nextChar();
            if (c == '"') {
                break;
            }
            builder.append(c);
        } while (hasMore());

        Object value = builder.toString();
        this.nextChar(); // skip other quote

        return (String) value;
    }

    private Object decodeJsonArray() throws Exception {
        if (this.currentChar() == '[' && hasMore())
            this.nextChar();
        skipWhiteSpace();
        List<Object> list = new ArrayList<>();
        this.lastList = list;

        int arrayIndex = 0;

        do {
            skipWhiteSpace();

            Object arrayItem = decodeValue();
            boolean wasNull = wasJsonNull();

            if (arrayItem == null && wasNull) {
                list.add(null); //JSON null detected
            } else if (arrayItem == null) {
                //do nothing
            } else {
                list.add(arrayItem);
            }

            arrayIndex++;
            skipWhiteSpace();
            char c = this.currentChar();
            if (!(c == ',' || c == ']')) {
                complain(
                        String.format("expecting a ',' of a ']', " +
                        " but got the current character of  %s " +
                        " on array index of %s \n", c, arrayIndex)
                );
            }
            if (c == ']') {
                this.nextChar();
                break;
            }
        } while (this.hasMore());
        return list;
    }


}