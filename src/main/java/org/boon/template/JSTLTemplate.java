package org.boon.template;

import org.boon.*;
import org.boon.collections.LazyMap;
import org.boon.core.Conversions;
import org.boon.core.reflection.BeanUtils;
import org.boon.expression.BasicContext;
import org.boon.primitive.CharBuf;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputl;
import static org.boon.Exceptions.die;
import static org.boon.Str.slc;
import static org.boon.json.JsonFactory.fromJson;

/**
 * Created by Richard on 9/15/14.
 */
public class JSTLTemplate implements Template {



    private JSTLTemplate parentTemplate;

    private JSTLCoreParser parser;


    private String template;

    CharBuf _buf = CharBuf.create(16);
    private BasicContext _context;

    private void output(Object object) {
        _buf.add(object);
    }
    private void output(JSTLCoreParser.Token token) {
        _buf.add(template, token.start(), token.stop());
    }


    @Override
    public String replace(String template, Object context) {

        initContext(context);
        parser = new JSTLCoreParser();
        this.template = template;
        parser.parse(template);
        _buf.readForRecycle();

        Iterator<JSTLCoreParser.Token> tokens = parser.getTokenList().iterator();

        while (tokens.hasNext()) {
            final JSTLCoreParser.Token token = tokens.next();
            processToken(tokens, token);
        }

        return _buf.toString();
    }

    private String textFromToken(JSTLCoreParser.Token token) {
        return template.substring(token.start(), token.stop());
    }


    protected void initContext(final Object root) {


        this._context = new BasicContext(root);



    }


    private void handleCommand(String template, JSTLCoreParser.Token commandToken, Iterator<JSTLCoreParser.Token> tokens) {

        this.template = template;

        final String commandText = textFromToken(commandToken);

        int index = StringScanner.findWhiteSpace(commandText);

        String command = Str.endSliceOf(commandText, index);

        String args = Str.sliceOf(commandText, index).trim();


        Map<String, String> params = parseArguments(args);


        JSTLCoreParser.Token bodyToken = tokens.next();




        int bodyTokenStop;
        if (bodyToken.type() == JSTLCoreParser.TokenTypes.COMMAND_BODY) {
            bodyTokenStop = bodyToken.stop();
        }else {
            bodyTokenStop = -1;
            die("BODY TOKEN NOT FOUND", commandText, commandToken);
        }


        List <JSTLCoreParser.Token> commandTokens = new ArrayList<>();


        while (tokens.hasNext()) {
            final JSTLCoreParser.Token next = tokens.next();
            commandTokens.add(next);
            if (next.stop() == bodyTokenStop) {
                break;
            }

        }

        dispatchCommand(command, params, commandTokens);

    }

    private Map<String, String> parseArguments(String args) {
        int index;
        final char[] chars = args.toCharArray();

        boolean collectName=true;

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        Map<String, String> params  = new LinkedHashMap<>();

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
                    params.put(name.toString(), value.toString());
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
        return params;
    }

    private void dispatchCommand(
            String command,
            Map<String, String> params,
            List<JSTLCoreParser.Token> commandTokens
    ) {
        switch (command) {
            case "if":
                handleIf(params, commandTokens);
                break;
            case "for":
            case "forEach":
            case "loop":
                handleLoop(params, commandTokens);

        }

    }

    private void handleIf(
                          Map<String, String> params,
                          List<JSTLCoreParser.Token> commandTokens
                          ) {


        boolean display;

        final String test = params.get("test");
        if (test.startsWith("$")) {
            Object o = _context.lookup(test);

            display = Conversions.toBoolean(o);
        } else {
            display = Conversions.toBoolean(test);
        }


        if (display) {

            processCommandBodyTokens(commandTokens);

        }

    }

    private void processCommandBodyTokens(List<JSTLCoreParser.Token> commandTokens) {
        final Iterator<JSTLCoreParser.Token> commandTokenIterators
                = commandTokens.iterator();
        while (commandTokenIterators.hasNext()) {

            JSTLCoreParser.Token token = commandTokenIterators.next();
            processToken(commandTokenIterators, token);

        }
    }

    private void processToken(Iterator<JSTLCoreParser.Token> tokens, JSTLCoreParser.Token token) {
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


    private void handleLoop(Map<String, String> params,  List<JSTLCoreParser.Token> commandTokens
    ) {

        List<Object> items = Conversions.toList(_context.lookup(params.get("items")));

        String var = params.get("var");

        if (var == null) {
            var = "item";
        } else if (var.startsWith("$")) {
            var = Conversions.toString(_context.lookupWithDefault(var, "item"));

        }

        Map<String, Object> values = new LazyMap();

        int index = 0;

        _context.pushContext(values);

        for (Object item : items) {

            values.put(var, item);

            values.put("index", index);

            processCommandBodyTokens(commandTokens);
            index++;
        }

        _context.removeLastContext();



    }


    public void displayTokens() {

        for (JSTLCoreParser.Token token : parser.getTokenList()) {
            puts ("token", token, textFromToken(token));
        }
    }

}
