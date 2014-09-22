package org.boon.template;

import org.boon.Boon;
import org.boon.IO;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.collections.LazyMap;
import org.boon.core.Conversions;
import org.boon.core.reflection.BeanUtils;
import org.boon.expression.BoonExpressionContext;
import org.boon.expression.ExpressionContext;
import org.boon.primitive.CharBuf;
import org.boon.template.support.LoopTagStatus;
import org.boon.template.support.Token;
import org.boon.template.support.TokenTypes;

import java.util.*;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/15/14.
 */
public class BoonTemplate implements Template {


    private CharBuf _buf = CharBuf.create(16);
    private BoonTemplate parentTemplate;
    private TemplateParser parser;
    private String template;
    private final ExpressionContext _context;
    private BoonCommandArgumentParser commandArgumentParser = new BoonCommandArgumentParser();

    private void output(Object object) {
        _buf.add(object);
    }

    private void output(Token token) {
        _buf.add(template, token.start(), token.stop());
    }


    public BoonTemplate() {

        parser = new BoonCoreTemplateParser();

        _context = new BoonExpressionContext();

    }


    public BoonTemplate(TemplateParser templateParser) {

        parser = templateParser;
        _context = new BoonExpressionContext();

    }

    public BoonTemplate(ExpressionContext context, TemplateParser templateParser) {

        parser = templateParser;
        _context = context;

    }


    @Override
    public String replace(String template, Object... context) {

        initContext(context);
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


    private String include(String template) {

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

    @Override
    public String replaceFromResource(String resource, Object... context) {
        final String template = Boon.resource(resource);
        return replace(template, context);
    }

    @Override
    public String replaceFromFile(String resource, Object... context) {
        final String template = IO.read(resource);
        return replace(template, context);

    }

    @Override
    public String replaceFromURI(String resource, Object... context) {

        final String template = IO.readResource(resource);
        return replace(template, context);

    }

    public String inlcudeFromURI(String resource) {

        final String template = IO.readResource(resource);
        return include(template);

    }

    private String textFromToken(Token token) {
        return template.substring(token.start(), token.stop());
    }


    protected void initContext(final Object... root) {

        _context.initContext(root);

    }


    private Token handleCommand(String template, Token commandToken, Iterator<Token> tokens) {

        this.template = template;

        final String commandText = textFromToken(commandToken);

        int index = StringScanner.findWhiteSpace(commandText);

        String command = Str.endSliceOf(commandText, index);

        String args = Str.sliceOf(commandText, index);


        Map<String, Object> params = commandArgumentParser.parseArguments(args);


        Token bodyToken = tokens.next();


        int bodyTokenStop;
        if (bodyToken.type() == TokenTypes.COMMAND_BODY) {
            bodyTokenStop = bodyToken.stop();
        } else {
            bodyTokenStop = -1;

        }


        List<Token> commandTokens = new ArrayList<>();


        if (bodyToken.start() != bodyToken.stop()) {
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


    private void dispatchCommand(
            String command,
            Map<String, Object> params,
            List<Token> commandTokens
    ) {
        switch (command) {
            case "if":
                handleIf(params, commandTokens, true);
                break;

            case "unless":
                handleIf(params, commandTokens, false);
                break;

            case "set":
            case "let":
            case "var":
            case "define":
            case "def":
            case "assign":
                handleSet(params, commandTokens);
                break;


            case "insert":
            case "include":
            case "import":
            case "template":
                handleInclude(params, commandTokens);

            case "list":
            case "for":
            case "forEach":
            case "foreach":
            case "loop":
            case "each":
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


    private String getStringParam(String param, Map<String, Object> params, String defaultValue) {

        String value = Str.toString(params.get(param));
        if (Str.isEmpty(value)) {
            return defaultValue;
        } else if (value.startsWith("$")) {
            return Conversions.toString(_context.lookupWithDefault(value, defaultValue));

        }
        return value;
    }

    private int getIntParam(String param, Map<String, Object> params, int defaultValue) {

        Object value = params.get(param);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof CharSequence && value.toString().startsWith("$")) {
            return Conversions.toInt(_context.lookupWithDefault(value.toString(), defaultValue));

        }
        return Conversions.toInt(value);
    }

    public void displayTokens() {

        for (Token token : parser.getTokenList()) {
            puts("token", token, textFromToken(token));
        }
    }


    private void handleLoop(Map<String, Object> params, List<Token> commandTokens) {


        String itemsName = Str.toString(params.get("items"));
        Object object = null;
        Collection<Object> items;


        if (!Boon.isEmpty(itemsName)) {
            object = _context.lookup(itemsName);
        }

        if (Boon.isEmpty(object)) {
            object = params.get("varargs");

        }

        if (Boon.isStringArray(object)) {
            String[] array = (String[]) object;

            List holder = new ArrayList();

            for (int index = 0; index < array.length; index++) {
                if (Str.isEmpty(array[index])) {
                    continue;
                }
                object = _context.lookup(array[index]);


                holder.addAll(Conversions.toCollection(object));

            }

            object = holder;

        }


        items = Conversions.toCollection(object);


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


                status.setIndex(index);

                values.put("@first", status.isFirst());

                values.put("@last", status.isLast());


                values.put("index", index);
                values.put("@index", index);

                if (item instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry) item;
                    values.put("@key", entry.getKey());
                    values.put("@value", entry.getValue());
                    values.put("this", entry.getValue());

                } else {

                    values.put("this", item);

                }
                processCommandBodyTokens(commandTokens);
            }
            index++;
        }

        _context.removeLastContext();


    }


    private void handleSet(Map<String, Object> params, List<Token> commandTokens) {


        String var = getStringParam("var", params, "");
        String propertyPath = getStringParam("property", params, "");
        String valueExpression = Str.toString(params.get("value"));
        String targetExpression = Str.toString(params.get("target"));


        Object value = _context.lookup(valueExpression);

        Object bean = _context.lookup(targetExpression);

        if (!Str.isEmpty(propertyPath) && bean != null) {
            BeanUtils.idx(bean, propertyPath, value);
        }

        if (!Str.isEmpty(var)) {
            _context.put(var, value);
        }

    }


    private void handleInclude(Map<String, Object> params, List<Token> commandTokens) {


        Map<String, Object> newContext = new LazyMap(params.size());

        for(Map.Entry<String, Object> entry : params.entrySet()) {
            newContext.put(entry.getKey(), _context.lookupWithDefault(
                    entry.getValue().toString(),
                    entry.getValue()));
        }

        final Object otype = params.get("type");
        String type;

        if (!Boon.isEmpty(otype)) {
            type = otype.toString();
        } else {
            type="";
        }

        TemplateParser includeParser;

        switch (type) {
            case "":
                try {
                    includeParser = parser.getClass().newInstance();
                } catch (Exception e) {
                    includeParser = new BoonCoreTemplateParser();
                }
                break;
            case "boon":
            case "jsp":
            case "jstl":
                includeParser = new BoonCoreTemplateParser();
                break;

            case "modern":
            case "mustache":
            case "handlebar":
            case "handlebars":
                includeParser = new BoonModernTemplateParser();
                break;

            default:
                includeParser = new BoonCoreTemplateParser();

        }

        BoonTemplate template = new BoonTemplate(_context, includeParser);
        template.parentTemplate = this;
        _context.pushContext(newContext);


        final String resource = (String) params.get("resource");

        if (resource.contains(":")) {
            output(template.inlcudeFromURI(resource));
        } else {
            output(template.includeFromResource(resource));
        }

        _context.removeLastContext();


    }

    private String includeFromResource(String resource) {
        final String template = Boon.resource(resource);
        return include(template);
    }

    private void handleIf(Map<String, Object> params, List<Token> commandTokens,
                          boolean normal) {

        boolean display = false;

        String var = getStringParam("var", params, "__none");

        if (params.containsKey("test")) {
            final String test = Str.toString(params.get("test"));
            if (!Str.isEmpty(test)) {

                if ("true".equals(test)) {
                    display = true;
                } else {
                    Object o = _context.lookupWithDefault(test, "");

                    display = Conversions.toBoolean(o);
                }
            }
        } else {
            final Object varargsObject = params.get("varargs");

            if (Boon.isStringArray(varargsObject)) {

                String[] array = (String[]) varargsObject;

                if (array.length > 0) {
                    display = true; //for now
                }
                for (int index = 0; index < array.length; index++) {

                    if (!Str.isEmpty(array[index])) {
                        Object o = _context.lookup(array[index]);
                        if (!Conversions.toBoolean(o)) {
                            display = false;
                            break;
                        }
                    }
                }

            } else if (varargsObject instanceof Collection) {
                Collection varargs = (Collection) varargsObject;


                if (varargs.size() > 0) {
                    display = true; //for now
                }


                for (Object arg : varargs) {

                    Object value = _context.lookup(arg.toString());
                    if (!Conversions.toBoolean(value)) {
                        display = false;
                        break;
                    }
                }
            }
        }

        display = display && normal;

        if (!var.equals("__none")) {

            _context.put(var, display);

        }


        if (display) {

            processCommandBodyTokens(commandTokens);

        }


    }


    public static Template template() {

        return new BoonTemplate(new BoonModernTemplateParser());

    }

    public static Template jstl() {

        return new BoonTemplate(new BoonCoreTemplateParser());

    }

    public void addFunctions(Class<?> functions) {

        _context.addFunctions(functions);
    }

    public void addFunctions(String prefix, Class<?> functions) {

        _context.addFunctions(prefix, functions);
    }

}