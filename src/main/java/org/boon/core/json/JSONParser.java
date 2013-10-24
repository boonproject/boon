package org.boon.core.json;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts an input JSON String into Java objects works with Reader or String
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 * */
public class JSONParser  {

    private char[] charArray;
    private int __index;
    private int line;
    private int lastLineStart;
    private char ch;
    private final boolean debug = true; // just used to debug if their are


    private  JSONParser() {

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


    private final boolean safe() {

        return __index < charArray.length;
    }

    private final boolean hasMore() throws Exception {
        return __index + 1 <= charArray.length;
    }

    private final char currentChar() throws Exception {

        if (safe()) {
            return ch = charArray[__index];
        }
        return ch;

    }

    private final char nextChar() throws Exception {
        if (hasMore()) {
            __index++;
            return this.currentChar();
        }
        return ch;

    }

    private void reset() {
        charArray = null;
        __index = 0;
        line = 0;
        lastLineStart = 0;
        ch = (char) 0;

    }



    @SuppressWarnings("unchecked")
    private Object decode(char[] cs) {
        charArray = cs;
        Object root=null;
        try {
            root = decodeValue();
        } catch (Exception e) {
            JSONException.handleException(e);
        }
        return  root;
    }



    private Object decode(String cs) {
        charArray = cs.toCharArray();
        Object root=null;
        try {
            root = decodeValue();
        } catch (Exception e) {
            JSONException.handleException(e);
        }
        return root;
    }


    @SuppressWarnings("nls")
    private void debug() {
        if (debug) {
            PrintStream out = System.out;
            out.println("line number " + line); //$NON-NLS-1$

            int lineCount = __index - lastLineStart;

            out.println(new String(charArray, lastLineStart, __index));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lineCount; i++) {
                if (lineCount - 1 == i) {
                    builder.append('^');
                } else {
                    builder.append('.');
                }
            }
            System.out.println(builder.toString());
        }
    }

    private void skipWhiteSpace() throws Exception {

        // if (fine) System.out.println("skipWhiteSpace");

        int tooMuchWhiteSpace = 20;
        int count = 0;

        while (hasMore()) {
            count++;
            if (count > tooMuchWhiteSpace)
                break;
            char c = this.currentChar();
            // if (fine) System.out.printf("skipWhiteSpace c='%s' \n", c);
            if (c == '\n') {
                line++;
                lastLineStart = __index;
                this.nextChar();
                continue;
            } else if (c == '\r') {
                line++;
                if (hasMore()) {
                    c = this.nextChar();
                    if (c != '\n') {
                        lastLineStart = __index;
                        break;
                    }
                }
                lastLineStart = __index;
                this.nextChar();
                continue;
            } else if (Character.isWhitespace(c)) {
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

        Map<String, Object> map = new HashMap<String, Object>();
        do {

            skipWhiteSpace();

            char c = this.currentChar();

            if (c == '"') {
                String key = decodeKeyName();
                skipWhiteSpace();
                c = this.currentChar();
                if (c != ':') {
                    debug();
                    throw new IllegalStateException(
                            "expecting ':' but got current char " + c
                                    + " line " + line
                                    + " index " + __index); //$NON-NLS-1$
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
                    debug();
                    throw new IllegalStateException(
                            "expecting '}' or ',' but got current char " + c
                                    + " line " + line
                                    + " index " + __index); //$NON-NLS-1$
                }
            }
            if (c == '}') {
                this.nextChar();
                break;
            } else if (c == ',') {
                this.nextChar();
                continue;
            } else {
                debug();
                throw new IllegalStateException(
                        "expecting '}' or ',' but got current char " + c
                                + " line " + line
                                + " index " + __index); //$NON-NLS-1$
            }
        } while (this.hasMore());
        return map;
    }

    private Object decodeValue() throws Exception {
        Object value = null;

        while (hasMore()) {
            char c = this.currentChar();
            if (c == '"') {
                value = decodeString();
                break;
            } else if (c == 't' || c == 'f') {
                value = decodeBoolean();
                break;
            } else if (c == 'n') {
                value = decodeNull();
                break;
            } else if (c == '[') {
                value = decodeJsonArray();
                break;
            } else if (c == '{') {
                value = decodeJsonObject();
                break;
            } else if (c == '-' || Character.isDigit(c)) {
                value = decodeNumber();
                break;
            }
            this.nextChar();
        }
        skipWhiteSpace();
        return value;
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
            debug();
            throw new IllegalStateException(
                    "expecting number char but got current char " + c
                            + " line " + line
                            + " index " + __index); //$NON-NLS-1$

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
            debug();
            throw new IllegalStateException(
                    "expecting to decode a number but got value of " + svalue
                            + " line " + line
                            + " index " + __index);

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
        return JSONStringParser.decode( charArray, start, to );
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
        List<Object> list = new ArrayList<Object>();

        int arrayIndex = 0;

        do {
            skipWhiteSpace();
            char c = this.currentChar();
            list.add(decodeValue());
            arrayIndex++;
            skipWhiteSpace();
            c = this.currentChar();
            if (!(c == ',' || c == ']')) {
                debug();
                throw new IllegalStateException(
                        "expecting a ',' of a ']' but got the current character of  " + c
                                + " line " + line
                                + " index " + __index + " on array index of " + arrayIndex);
            }
            if (c == ']') {
                this.nextChar();
                break;
            }
        } while (this.hasMore());
        return list;
    }


}