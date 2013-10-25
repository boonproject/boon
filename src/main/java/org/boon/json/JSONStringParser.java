package org.boon.json;

public class JSONStringParser {

    public static String decode(String string) {

        char[] cs = string.toCharArray();
        return decode(cs, 0, cs.length);
    }

    public static String decode(char[] chars) {

        return decode(chars, 0, chars.length);
    }

    public static String decode(char[] chars, int start, int to) {

        final char[] cs = chars;
        final int length = cs.length;

        if (cs[start] == '"') {
            start ++;
        }

        StringBuilder builder = new StringBuilder(cs.length);
        for (int index = start; index < to; index++) {
            char c = cs[index];
            if (c == '\\') {
                if (index < cs.length) {
                    index++;
                    c = cs[index];
                    if (c == 'n') {
                        builder.append("\n");
                    } else if (c == '/') {
                        builder.append("/");
                    } else if (c == '"') {
                        builder.append('"');
                    } else if (c == 'f') {
                        builder.append("\f");
                    } else if (c == 't') {
                        builder.append("\t");
                    } else if (c == '\\') {
                        builder.append("\\");
                    } else if (c == 'b') {
                        builder.append("\b");
                    } else if (c == 'r') {
                        builder.append("\r");
                    } else if (c == 'u') {
                        if (index + 4 < cs.length) {
                            String hex = new String(cs, index + 1, index + 5);
                            char unicode = (char) Integer.parseInt(hex, 16);
                            builder.append(unicode);
                            index += 4;
                        }
                    }
                }
            } else {
                builder.append(c);
            }
        }
        return builder.toString();

    }

}