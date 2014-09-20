package org.boon.template;

import org.boon.Str;
import org.boon.StringScanner;
import org.boon.collections.LazyMap;
import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.boon.primitive.CharScanner;

import java.util.Map;

/**
 * Created by Richard on 9/18/14.
 */
public class BoonCommandArgumentParser {

    final JsonParserAndMapper jsonParser = new JsonParserFactory().lax().create();

    public Map<String, Object> parseArguments(final String args) {
        int index;


        final char[] chars = FastStringUtils.toCharArray(args);

        index = CharScanner.skipWhiteSpace(chars);

        char c = ' ';

        if (index != chars.length) {
            c = chars[index];
        }


        if (c == '{') {
            return jsonParser.parseMap(chars);
        } else if (c == '[') {
            LazyMap map = new LazyMap(1);
            map.put("varargs", jsonParser.parse(chars));
            return map;
        }

        boolean collectName = true;

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        Map<String, Object> params = new LazyMap();

        params.put("commandArgs", args);

        char closeQuoteToMatch = '\"';

        for (index = 0; index < chars.length; index++) {
            c = chars[index];

            switch (c) {

                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    continue;

                case '=':
                    collectName = false;
                    if (chars[index + 1] != '\'' && chars[index + 1] != '\"') {
                        int start = index + 1;
                        int end = -1;
                        if (chars[index + 1] == '$' && chars[index + 2] == '{') {

                            end = CharScanner.findChar('}', start, chars);
                            if (end != -1) end++;
                        } else if (chars[index + 1] == '{' && chars[index + 2] == '{') {

                            end = CharScanner.findChar('}', start, chars);
                            if (end != -1) end+=2;
                        }
                        else {
                            end = CharScanner.findWhiteSpace(start, chars);
                        }
                        if (end == -1) {
                            end = chars.length;
                        }
                        String v = Str.slc(args, start, end);
                        params.put(name.toString(), v);
                        index += v.length();
                        name = new StringBuilder();

                        collectName = true;
                        value = new StringBuilder();

                    } else {

                        closeQuoteToMatch = chars[index+1];
                        index++; //skip quote
                    }
                    continue;


                case '\"':
                case '\'':

                    if (c == closeQuoteToMatch) {
                        collectName = true;
                        index++;
                        params.put(name.toString(), value.toString());
                        name = new StringBuilder();
                        value = new StringBuilder();
                        continue;
                    }


                default:
                    if (collectName) {
                        name.append(c);
                    } else {
                        value.append(c);
                    }

            }
        }

        if (params.size()==1) {



            params.put("varargs", StringScanner.splitByChars(args, ' ', '\t', '\n'));
        }
        return params;
    }

}
