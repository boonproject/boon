package org.boon.template;

import org.boon.*;
import org.boon.core.Conversions;
import org.boon.core.reflection.BeanUtils;
import org.boon.primitive.CharBuf;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputl;
import static org.boon.Exceptions.die;
import static org.boon.core.reflection.BeanUtils.findProperty;
import static org.boon.json.JsonFactory.fromJson;

/**
 * Created by Richard on 9/15/14.
 */
public class JSTLTemplate implements Template {


    private Object context;

    private JSTLTemplate parentTemplate;

    @Override
    public String replace(String template, Object context) {

        initContext(context);
        CharBuf buf = CharBuf.create(template.length());
        JSTLCoreParser parser = new JSTLCoreParser();
        parser.parse(template);

        Iterator<JSTLCoreParser.Token> tokens = parser.getTokenList().iterator();

        while (tokens.hasNext()) {
            final JSTLCoreParser.Token token = tokens.next();
            switch (token.type()) {
                case TEXT:
                    buf.add(template, token.start(), token.stop());
                    break;
                case EXPRESSION:
                    String path = textFromToken(template, token);
                    buf.add(lookup(path));
                    break;
                case COMMAND:
                    handleCommand(buf, template, token, tokens);
                    break;

            }
        }

        return buf.toString();
    }

    private String textFromToken(String template, JSTLCoreParser.Token token) {
        return template.substring(token.start(), token.stop());
    }


    protected void initContext(Object context) {
        if (context instanceof CharSequence) {
            try {
                this.context = fromJson(context.toString());
            } catch (Exception ex) {
                this.context = context;
            }
        } else {
            this.context = context;
        }
    }


    /**
     * Lookup an object and use its name as the default value if not found.
     *
     * @param objectName
     * @return
     */
    public Object lookup(String objectName) {
        return lookup(objectName, objectName);
    }

    /**
     * Lookup an object and supply a default value.
     * @param objectName
     * @param defaultValue
     * @return
     */
    public Object lookup(String objectName, String defaultValue) {


        Object value =  findProperty(context, objectName);
        if (value == null) {
            if (parentTemplate!=null) {
                value = parentTemplate.lookup(objectName);
            }
        }
        return value == null ? defaultValue : value;
    }


    private void handleCommand(CharBuf buf, String template, JSTLCoreParser.Token token, Iterator<JSTLCoreParser.Token> tokens) {

        final String commandText = textFromToken(template, token);

        int index = StringScanner.findWhiteSpace(commandText);

        String command = Str.endSliceOf(commandText, index);

        String args = Str.sliceOf(commandText, index).trim();


        final char[] chars = args.toCharArray();

        boolean collectName=true;

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        Map<String, Object> params  = new LinkedHashMap<>();

        for (index=0; index < chars.length; index++) {
            char c = chars[index];

            switch (c) {

                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    continue;

                case '=':
                    collectName=false;
                    index++; //skip quote
                    continue;


                case '\"':
                case '\'':
                    collectName=true;
                    index++;

                    String v = value.toString().trim();
                    Object o = v;
                    if (v.startsWith("${") && v.endsWith("}")) {
                        String path = Str.slc(v, 2, -1);
                        o = lookup(path);
                    }
                    params.put(name.toString(), o);
                    name = new StringBuilder();
                    value = new StringBuilder();
                    continue;


                default:
                    if (collectName) {
                        name.append(c);
                    }else {
                        value.append(c);
                    }

            }
        }

        handleCommand(buf, template, command, params, tokens);

    }

    private void handleCommand(CharBuf buf, String template, String command, Map<String, Object> params, Iterator<JSTLCoreParser.Token> tokens) {
        switch (command) {
            case "if":
                handleIf(buf, template, params, tokens);
                break;

        }

    }

    private void handleIf(CharBuf buf, String template, Map<String, Object> params, Iterator<JSTLCoreParser.Token> tokens) {

        boolean display = Conversions.toBoolean(params.get("test"));

        JSTLCoreParser.Token token = tokens.next();

        int stop;
        if (token.type() == JSTLCoreParser.TokenTypes.COMMAND_BODY) {
            stop = token.stop();
        }else {
            stop = -1;
            die();
        }

        while (tokens.hasNext()) {

            token = tokens.next();
            if (display) {
                switch (token.type()) {
                    case TEXT:
                        buf.add(template, token.start(), token.stop());
                        break;
                    case EXPRESSION:
                        String path = textFromToken(template, token);
                        buf.add(lookup(path));
                        break;
                    case COMMAND:
                        handleCommand(buf, template, token, tokens);
                        break;


                }
            }
            if (token.stop() == stop) {
                break;
            }

        }

    }
}
