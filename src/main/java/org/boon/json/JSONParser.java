package org.boon.json;

import org.boon.primitive.CharBuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.json.ParserState.*;

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

    private Map<String, Object> lastObject;
    private List<Object> lastList;
    private ParserState state = START;
    private ParserState lastState = START;

    private JSONParser() {

    }

    public static Object parse(String cs) {
        JSONParser p = new JSONParser();
        return p.decode(cs);

    }


    public static Map<String, Object> parseMap(String cs) {
        JSONParser p = new JSONParser();
        return (Map<String, Object>) p.decode(cs);
    }

    public static <T> List<T> parseList(Class<T> type, String cs) {
        JSONParser p = new JSONParser();
        return (List<T>) p.decode(cs);
    }


    public static Number parseNumber(char[] cs) {
        JSONParser p = new JSONParser();
        return (Number) p.decode(cs);
    }


    @SuppressWarnings("unchecked")
    private Object decode(char[] cs) {
        charArray = cs;
        Object root = null;
        root = decodeValue();
        return root;
    }


    private Object decode(String cs) {
        charArray = cs.toCharArray();
        Object root = null;
        root = decodeValue();
        return root;
    }


    private final boolean safe() {

        return __index < charArray.length;
    }

    private final boolean hasMore() {
        return __index + 1 < charArray.length;
    }

    private final boolean hasMorePlus() {
        return __index < charArray.length;
    }

    private final char _currentChar() {

        if (safe()) {
            return __currentChar = charArray[__index];
        }
        return __currentChar;

    }

    private final char nextChar() {

        try {
            if (hasMore()) {
                __lastChar = __currentChar;
                __index++;
                return __currentChar = charArray[__index];
            }
            return __currentChar;

        } catch (Exception ex) {
            throw new RuntimeException(exceptionDetails("failure in next " +
                    ex.getLocalizedMessage()), ex);

        }
    }

    private final char nextCharIfSafe() {

        try {
            if (hasMore()) {
                __lastChar = __currentChar;
                __index++;
                return __currentChar = charArray[__index];
            } else if (__index + 1 == charArray.length) {
                __lastChar = __currentChar;
                return __currentChar = charArray[charArray.length-1];
            }
            return __currentChar;

        } catch (Exception ex) {
            throw new RuntimeException(exceptionDetails("failure in next " +
                    ex.getLocalizedMessage()), ex);

        }
    }

    private String exceptionDetails(String message) {
        CharBuf buf = CharBuf.create(255);

        buf.addLine(message);

        buf.add(state.toString()).addLine(" is CURRENT STATE");
        buf.add(lastState.toString()).addLine(" is LAST STATE");

        buf.addLine("");
        buf.addLine("The last character read was " + charDescription(__lastChar));
        buf.addLine("The current character read is " + charDescription(__currentChar));


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

        try {
            buf.addLine(new String(charArray, lastLineStart, __index));
        } catch (Exception ex) {

            try {
                int index = (__index - 20 < 0) ? 0 : __index - 20;

                buf.addLine(new String(charArray, index, __index));
            } catch (Exception ex2) {
                buf.addLine(new String(charArray, 0, charArray.length));
            }
        }
        for (int i = 0; i < lineLocationCount; i++) {
            if (lineLocationCount - 1 == i) {
                buf.add('^');
            } else {
                buf.add('.');
            }
        }
        return buf.toString();
    }

    private void skipWhiteSpace() {
        this._currentChar();


        while (hasMore()) {

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
            } else if (__currentChar == ' ' || __currentChar == '\t' || __currentChar == '\b' || __currentChar == '\f') {
                this.nextChar();
                continue;
            } else {
                break;
            }
        }

    }

    private Object decodeJsonObject() {

        if (__currentChar == '{' && this.hasMore())
            this.nextChar();

        Map<String, Object> map = new LinkedHashMap<>();

        this.lastObject = map;

        do {

            skipWhiteSpace();


            if (__currentChar == '"') {
                String key = decodeKeyName();
                skipWhiteSpace();

                if (__currentChar != ':') {

                    complain("expecting current character to be " + charDescription(__currentChar) + "\n");
                }
                this.nextChar(); // skip past ':'
                skipWhiteSpace();
                Object value = decodeValue();

                skipWhiteSpace();

                map.put(key, value);

                if (!(__currentChar == '}' || __currentChar == ',')) {
                    complain("expecting '}' or ',' but got current char " + charDescription(__currentChar));
                }
            }
            if (__currentChar == '}') {
                this.nextChar();
                break;
            } else if (__currentChar == ',') {
                this.nextChar();
                continue;
            } else {
                complain(
                        "expecting '}' or ',' but got current char " + charDescription(__currentChar));

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

    private Object decodeValue() {
        Object value = null;


        do {
            skipWhiteSpace();
            char c = this.__currentChar;
            if (c == '"') {
                value = decodeString();
                break;
            } else if (c == 't' || c == 'f') {
                setState(START_BOOLEAN);
                value = decodeBoolean();
                setState(END_BOOLEAN);
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
            } else {
                if (__index + 1 >= charArray.length) {
                    break;
                } else {
                    throw new JSONException(exceptionDetails("Unable to determine the " +
                            "current character, it is not a string, number, array, or object"));
                }
            }
        } while (hasMore());
        skipWhiteSpace();
        return value;
    }

    private void setState(ParserState state) {
        this.lastState = this.state;
        this.state = state;
    }

    private Object decodeNumber() {
        CharBuf buf = CharBuf.create(16);

        boolean doubleFloat = false;

        int index;

        for ( index = __index; index < charArray.length; index++ ) {
           __currentChar = charArray [index];
            char c = __currentChar;
            /*
                Numbers are odd, if you see anything that is nto a number
                then we are done with the number.
                Look for space, tab, comma, curly bracket, and bracket.
             */
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ',' || c == '}' || c == ']') {
                __index = index + 1;
                break;
            }

            if (
                    c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
                            c == '5' || c == '6' || c == '7' || c == '8' || c == '9'
                            || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {

                if (c == '.' || c == 'e' || c == 'E') {
                    doubleFloat = true;
                }
                buf.add(c);
                continue;
            }
            complain("expecting number char but got current char " + charDescription(c));
        }

        __index = index;
        skipWhiteSpace();

        String svalue = buf.toString();
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
                complain("expecting to decode a number but got value of " + svalue);
            }

        }

        return value;

    }

    private int index() {
        return __index;
    }

    private Object decodeBoolean() {
        if (__currentChar == 't') {
            nextChar();
            nextChar();
            if (__lastChar == 'r' && __currentChar == 'u') {
                nextChar();
                if (__currentChar == 'e') {
                    nextChar();
                    skipWhiteSpace();
                    return true;
                } else {
                    throw new JSONException(exceptionDetails("boolean parse for true"));

                }

            } else {
                throw new JSONException(exceptionDetails("boolean parse for true"));

            }
        } else if (__currentChar == 'f') {
            nextChar();
            nextChar();
            if (__lastChar == 'a' && __currentChar == 'l') {
                nextChar();
                nextChar();
                if (__lastChar == 's' && __currentChar == 'e') {
                    nextChar();
                    skipWhiteSpace();
                    return true;
                } else {
                    throw new JSONException(exceptionDetails("boolean parse for false"));

                }

            } else {
                throw new JSONException(exceptionDetails("boolean parse for false"));

            }
        } else {
            throw new JSONException(exceptionDetails("boolean parse"));

        }


    }

    private Object decodeNull() {
        if (__currentChar == 'n') {
            nextChar();
            nextChar();
            if (__lastChar == 'u' && __currentChar == 'l') {
                nextChar();
                if (__currentChar == 'l') {
                    nextChar();
                    skipWhiteSpace();
                    return null;
                } else {
                    throw new JSONException(exceptionDetails("null parse"));

                }

            } else {
                throw new JSONException(exceptionDetails("null parse"));

            }
        } else {
            throw new JSONException(exceptionDetails("null parse"));

        }

    }

    private Object decodeString() {
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

    private String encodeString(int start, int to) {
        return JSONStringParser.decode(charArray, start, to);
    }

    private String decodeKeyName() {

        CharBuf builder = CharBuf.create(12);
        do {
            char c = this.nextChar();
            if (c == '"') {
                break;
            }
            builder.add( c );
        } while (hasMore());

        Object value = builder.toString();
        this.nextChar(); // skip other quote

        return (String) value;
    }

    private Object decodeJsonArray() {
        if (__currentChar == '[') {
            this.nextChar();
        }

        skipWhiteSpace();

        List<Object> list = new ArrayList<>();
        this.lastList = list;

        /* the listStream might be empty  */
        if (__currentChar == ']') {
            this.nextChar();
            return list;
        }


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

            char c = __currentChar;

            if (c == ',') {
                this.nextChar();
                continue;
            } else if (c == ']') {
                this.nextChar();
                break;
            } else {
                String charString = charDescription(c);

                complain(
                        String.format("expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex)
                );

            }
        } while (this.hasMore());
        return list;
    }

    private String charDescription(char c) {
        String charString;
        if (c == ' ') {
            charString = "[SPACE]";
        } else if (c == '\t') {
            charString = "[TAB]";

        } else if (c == '\n') {
            charString = "[NEWLINE]";

        } else {
            charString = "'" + c + "'";
        }

        charString = charString + " with an int value of " + ((int) c);
        return charString;
    }


}