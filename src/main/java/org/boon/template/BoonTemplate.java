package org.boon.template;

import org.boon.Str;
import org.boon.StringScanner;
import org.boon.collections.LazyMap;
import org.boon.core.Conversions;
import org.boon.core.reflection.BeanUtils;
import org.boon.expression.ExpressionContext;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;
import org.boon.template.support.LoopTagStatus;
import org.boon.template.support.Token;
import org.boon.template.support.TokenTypes;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/15/14.
 */
public class BoonTemplate implements Template {


    CharBuf _buf = CharBuf.create(16);
    private BoonTemplate parentTemplate;
    private BoonCoreTemplateParser parser;
    private String template;
    private ExpressionContext _context;

    private void output(Object object) {
        _buf.add(object);
    }

    private void output(Token token) {
        _buf.add(template, token.start(), token.stop());
    }


    @Override
    public String replace(String template, Object... context) {

        initContext(context);
        parser = new BoonCoreTemplateParser();
        this.template = template;
        parser.parse(template);
        _buf.readForRecycle();

        Iterator<Token> tokens = parser.getTokenList().iterator();

        while (tokens.hasNext()) {
            final Token token = tokens.next();
            processToken(tokens, token);
        }

        return _buf.toString();
    }

    private String textFromToken(Token token) {
        return template.substring(token.start(), token.stop());
    }


    protected void initContext(final Object... root) {


        this._context = new ExpressionContext(root);


    }


    private Token handleCommand(String template, Token commandToken, Iterator<Token> tokens) {

        this.template = template;

        final String commandText = textFromToken(commandToken);

        int index = StringScanner.findWhiteSpace(commandText);

        String command = Str.endSliceOf(commandText, index);

        String args = Str.sliceOf(commandText, index).trim();


        Map<String, String> params = parseArguments(args);


        Token bodyToken = tokens.next();


        int bodyTokenStop;
        if (bodyToken.type() == TokenTypes.COMMAND_BODY) {
            bodyTokenStop = bodyToken.stop();
        } else {
            bodyTokenStop = -1;

        }


        List<Token> commandTokens = new ArrayList<>();


        if (bodyToken.start()!=bodyToken.stop()) {
            while (tokens.hasNext()) {
                final Token next = tokens.next();
                commandTokens.add(next);
                if (next.stop() == bodyTokenStop) {
                    break;
                }

            }
        }

        dispatchCommand(command, params, commandTokens);

        return null;

    }

    private Map<String, String> parseArguments(String args) {
        int index;
        final char[] chars = args.toCharArray();

        boolean collectName = true;

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        Map<String, String> params = new LinkedHashMap<>();

        for (index = 0; index < chars.length; index++) {
            char c = chars[index];

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
                        } else {
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
                        index++; //skip quote
                    }
                    continue;


                case '\"':
                case '\'':
                    collectName = true;
                    index++;
                    params.put(name.toString(), value.toString());
                    name = new StringBuilder();
                    value = new StringBuilder();
                    continue;


                default:
                    if (collectName) {
                        name.append(c);
                    } else {
                        value.append(c);
                    }

            }
        }
        return params;
    }

    private void dispatchCommand(
            String command,
            Map<String, String> params,
            List<Token> commandTokens
    ) {
        switch (command) {
            case "if":
                handleIf(params, commandTokens);
                break;
            case "set":
            case "let":
            case "var":
            case "define":
            case "def":
                handleSet(params, commandTokens);
                break;
            case "for":
            case "forEach":
            case "loop":
                handleLoop(params, commandTokens);

        }

    }

    private void processCommandBodyTokens(List<Token> commandTokens) {
        final Iterator<Token> commandTokenIterators
                = commandTokens.iterator();
        while (commandTokenIterators.hasNext()) {

            Token token = commandTokenIterators.next();
            processToken(commandTokenIterators, token);

        }
    }

    private void processToken(Iterator<Token> tokens, Token token) {
        switch (token.type()) {
            case TEXT:
                output(token);
                break;
            case EXPRESSION:
                String path = textFromToken(token);
                output(_context.lookup(path));
                break;
            case COMMAND:
                handleCommand(template, token, tokens);

                break;


        }
    }


    private String getStringParam(String param, Map<String, String> params, String defaultValue) {

        String value = params.get(param);
        if (value == null) {
            return defaultValue;
        } else if (value.startsWith("$")) {
            return Conversions.toString(_context.lookupWithDefault(value, defaultValue));

        }
        return Conversions.toString(value);
    }

    private int getIntParam(String param, Map<String, String> params, int defaultValue) {

        String value = params.get(param);
        if (value == null) {
            return defaultValue;
        } else if (value.startsWith("$")) {
            return Conversions.toInt(_context.lookupWithDefault(value, defaultValue));

        }
        return Conversions.toInt(value);
    }

    public void displayTokens() {

        for (Token token : parser.getTokenList()) {
            puts("token", token, textFromToken(token));
        }
    }


    private void handleLoop(Map<String, String> params, List<Token> commandTokens
    ) {

        List<Object> items = Conversions.toList(_context.lookup(params.get("items")));

        String var = getStringParam("var", params, "item");
        String varStatus = getStringParam("varStatus", params, "status");


        int begin = getIntParam("begin", params, -1);
        int end = getIntParam("end", params, -1);
        int step = getIntParam("step", params, -1);

        LoopTagStatus status = new LoopTagStatus();

        status.setCount(items.size());

        if (end != -1) {
            status.setEnd(end);
        } else {
            end = items.size();
        }


        if (begin != -1) {
            status.setBegin(begin);
        } else {
            begin = 0;
        }


        if (step != -1) {
            status.setStep(step);
        } else {
            step = 1;
        }


        Map<String, Object> values = new LazyMap();

        int index = 0;

        _context.pushContext(values);

        for (Object item : items) {

            if (index >= begin && index < end && index % step == 0) {

                values.put(var, item);
                values.put(varStatus, status);

                values.put("index", index);

                status.setIndex(index);

                processCommandBodyTokens(commandTokens);
            }
            index++;
        }

        _context.removeLastContext();


    }


    private void handleSet(Map<String, String> params, List<Token> commandTokens) {


        String var = getStringParam("var", params, "");
        String propertyPath = getStringParam("property", params, "");
        String valueExpression = params.get("value");
        String targetExpression = params.get("target");


        Object value = _context.lookup(valueExpression);

        Object bean = _context.lookup(targetExpression);

        if (!Str.isEmpty(propertyPath) && bean!=null ) {
            BeanUtils.idx(bean, propertyPath, value);
        }

        if (!Str.isEmpty(var)) {
            _context.put(var, value);
        }

    }

    private void handleIf(Map<String, String> params,
            List<Token> commandTokens ) {


        boolean display;

        String var = getStringParam("var", params, "__none");

        final String test = params.get("test");
        if (test.startsWith("$")) {
            Object o = _context.lookup(test);

            display = Conversions.toBoolean(o);
        } else {
            display = Conversions.toBoolean(test);
        }


        if (!var.equals("__none")) {
            Map<String, Object> values = new LazyMap();


            _context.pushContext(values);
        }


        if (display) {

            processCommandBodyTokens(commandTokens);

        }


        if (!var.equals("__none")) {


            _context.removeLastContext();
        }

    }


}
