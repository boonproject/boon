package org.boon.template;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.core.Conversions;

import static java.util.Arrays.copyOfRange;


import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Lists.list;
import static org.boon.Lists.sliceOf;
import static org.boon.Maps.map;
import static org.boon.core.reflection.BeanUtils.*;
import static org.boon.core.reflection.FastStringUtils.noCopyStringFromChars;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.primitive.CharScanner.findChar;
import static org.boon.primitive.CharScanner.findChars;
import static org.boon.primitive.CharScanner.findString;

/**
 * Created by Richard on 2/27/14.
 *
 * TODO
 * Improve error handling and tests.
 * Improve toJson, fromJson support
 *
 * TODO add this:
 *
 * Helpers receive the current context as the this context of the function.
 <ul>
 {{#each items}}
 <li>{{agree_button}}</li>
 {{/each}}
 </ul>
 when using this context and helpers:
using functions

 TODO
 add factory or make template more factory like, add helper.

 TODO
 implement handlebar templates
 Template comments with {{! }} or {{!-- --}}.

 TODO
 handle different forms of this

 Handlebars also allows for name conflict resolution between helpers and data fields via a this reference:
 <p>{{./name}} or {{this/name}} or {{this.name}}</p>
 Any of the above would cause the name field on the current context to be used rather than a helper of the same name.

 TODO improve path support
 Nested handlebars paths can also include ../ segments, which evaluate their paths against a parent context.
 <h1>Comments</h1>

 <div id="comments">
 {{#each comments}}
 <h2><a href="/posts/{{../permalink}}#{{id}}">{{title}}</a></h2>
 <div>{{body}}</div>
 {{/each}}
 </div>

 * <pre>
 * TODO add this
 * When looping through items in each, you can optionally reference the current loop index via {{@index}}
 {{#each array}}
 {{@index}}: {{this}}
 {{/each}}
 Additionally for object iteration, {{@key}} references the current key name:
 {{#each object}}
 {{@key}}: {{this}}
 {{/each}}
 </pre>
 <p>
 The first and last steps of iteration are noted via the @first and @last
 variables then iterating over an array. When iterating over an object only the @first is available.
 </p>
 <p>
 TODO
 If you find a "function" (Provider or Function), call it instead of evaluating it and converting it toString.
 Function gets passed context as argument to apply.

 TODO loading templates by name

 template.setPrefix("/templates");
 template.setSuffix(".html");

 always search classpath first

 TODO
 https://github.com/jknack/handlebars.java

 block and partial

 Block and partial helpers work together to provide you Template Inheritance.

 Usage:

 {{#block "title"}}
 ...
 {{/block}}
 context: A string literal which define the region's name.

 Usage:

 {{#partial "title"}}
 ...
 {{/partial}}
 context: A string literal which define the region's name.

 //

 he uses embedded for include {{embedded "user"}}
 I think I will stick with include but support embedded

 TODO
 //i18n ??? maybe

 Might implement some of these

 //https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java
 I have some too. :) //lower, upper, rpad, etc. https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java



 TODO implement default value

 TODO
 https://github.com/elving/swag
 </p>
 */
public class BoonTemplate {

    /** Set the start of expression demarcation. As in {{Hello}}
     *  {{Hello}}
     *  ^
     *  |
     *  |
     * */
    char[] expressionStart = "{{".toCharArray();

    /** Sets how the beginning of the end block start should look .
     *
     *  {{if}}
     *
     *  {{/if}}
     *  ^
     *  |
     *  |
     * */
    String endBlockStart = "{{/";

    /**
     * Three {{{Text in here will not get escaped so you can have <ol/> }}}
     *
     * BoonTemplate does not always support HTML output or rather Boon template does not only support
     * HTML so you have to enable HTML escaping for {{HTML ESCAPED}} and {{{HTML UNESCAPED TO WORK}}}
     * By default everything is unescaped.
     */
    char[] unescapedExpressionStart = "{{{".toCharArray();
    char[] unescapedExpressionEnd = "}}}".toCharArray();

    /**
     * It detects if the escaped and unescaped start with the same char.
     */
    final boolean sameStart;

    /**
     * The end of an end block.
     *
     *
     *  {{#if}}
     *
     *  {{/if}}
     *       ^
     *       |
     *       |
     *
     */
    String endBlockEnd = "}}";

    /**
     * The end of an end expression.
     *
     *
     *  {{helloMom}}
     *           ^
     *           |
     *           |
     *
     */
    char[] expressionEnd = "}}".toCharArray();

    /**
     * First char used to search strings quickly for expressions or commands.
     */
    char expressionStart1stChar = expressionStart[0];

    /**
     * First char used to search strings quickly for expressions or commands.
     */
    char unescapedExpressionStartChar = unescapedExpressionStart[0];

    /**
     * Command marker.
     *
     * {{#if}}
     *  ^
     *  |
     */
    String commandMarker = "#";

    /**
     * I was not sure how to handle this so I punted.
     */
    String elseBlock = "{{else}}";

    //State
    /** Current line number. */
    int lineIndex;
    /** List of lines we are processing. */
    char[][] lines;
    /** Context object that we are getting expressions from. */
    Object context;

    /** Whether we support escaped mode or not for HTML output. */
    boolean escaped;

    /** Commands that we support. A command is more or less like a tag handler in JSP. */
    protected Map<String, Command> commandMap;


    /** Functions can be used anywhere where expressions can be used. */
    protected Map<String, MethodAccess> functionMap;


    /** Lookup a command.
     *
     * If the commandMap is null and this template has a parent template then
     * it will look for the command in the parent template.
     *
     * Also if a command is not found in the this template's commandMap then it will be searched
     * in the parent commandMap.
     *
     * This is how {{#if}}, {{#with}}, and {{#each}} use commands from their parent templates.
     * Every block is its own template with a parent template.
     * */
    protected Command command(String cmdStr) {

        /* if the command is null user the parent templates command map if present. */
        if (this.commandMap == null) {

            if ( parentTemplate != null) {
                return parentTemplate.command(cmdStr);
            }
            return null;
        } else {

            Command command = this.commandMap.get(cmdStr);
            if (command ==  null) {

                if ( parentTemplate != null) {
                    command = parentTemplate.command(cmdStr);
                }
            }
            if (command == null && !command.equals("missingCommand")) {
                command = command("missingCommand");

                if (command!=null) {
                    //Set the command name if the object supports it.
                    idx(command, "commandName", cmdStr);
                }
            }

            return command;
        }


    }

    public BoonTemplate(char[] expressionStart, char[] expressionEnd, Object functions) {
        this.expressionStart = expressionStart;
        this.expressionEnd = expressionEnd;
        this.expressionStart1stChar = expressionStart[0];

        this.unescapedExpressionStartChar = unescapedExpressionStart[0];

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

        if (functions!=null) {
            extractFunctions(functions, false);
        }
    }


    private void extractFunctions(Object functions, boolean all) {
        commandMap = new HashMap<>();
        functionMap = new HashMap<>();

        ClassMeta<?> classMeta = ClassMeta.classMetaEither(functions);

        Iterable<MethodAccess> methods = classMeta.methods();

        for (MethodAccess methodAccess : methods) {
            functionMap.put(methodAccess.name(), methodAccess.methodAccess().bind(functions));
        }

        if (!all) {
            for (MethodAccess methodAccess : methods) {
                if (methodAccess.respondsTo(CharBuf.class, String.class, CharSequence.class, Object.class)) {
                    commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
                } else if (methodAccess.respondsTo(String.class, CharSequence.class, Object.class)) {
                    commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
                }
            }


        } else {
            for (MethodAccess methodAccess : methods) {
                commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
            }
        }
    }

    public BoonTemplate() {

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

    }

    public static BoonTemplate template() {
        return new BoonTemplate();
    }


    public static BoonTemplate jstl() {
        return template("${", "}");
    }


    public static BoonTemplate template(String expStart, String expEnd) {
        return new BoonTemplate(expStart.toCharArray(), expEnd.toCharArray(), null);
    }


    public static BoonTemplate template(char[] expStart, char[] expEnd) {
        return new BoonTemplate(expStart, expEnd, null);
    }


    public static BoonTemplate template(char[] expStart, char[] expEnd, Object functions) {
        return new BoonTemplate(expStart, expEnd, functions);
    }


    public static BoonTemplate templateWithFunctions(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplate();
        boonTemplate.extractFunctions(functions, false);
        return boonTemplate;
    }


    public static BoonTemplate templateWithDynamicFunctions(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplate();
        boonTemplate.extractFunctions(functions, true);
        return boonTemplate;

    }


    public CharSequence replace (CharSequence template, Object context) {

        initContext(context);
        char[] chars = FastStringUtils.toCharArray(template);
        CharBuf output = CharBuf.create(template.length());

        lines = CharScanner.splitLines(chars);

        for ( lineIndex = 0; lineIndex < lines.length; lineIndex++ ) {
            char [] line = lines[lineIndex];
            //puts("LINE", new String(line));

            if (lineHasCommand()) {
                processLineCommand(output, line); //for JSTL like commands
            }

            int index = findExpression(line);
            if (index == -1) {
                output.add(line);
            } else {

                output.add(copyOfRange(line, 0, index));
                processCommandOrExpression(output, line, index);
            }
        }

        return output;
    }

    private void initContext(Object context) {
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


    private int findExpression(char[] line) {

        return findExpressionFromIndex(line, 0);
    }

    private int findExpressionFromIndex(char[] line, int startIndex) {

        if (sameStart) {
            if (findChar(expressionStart1stChar, startIndex, line) == -1) {
                return -1;
            }
        }

        int indexEscaped = findChars(expressionStart, startIndex, line);
        int indexUnEscaped =  findChars(unescapedExpressionStart, startIndex, line);


        if (indexUnEscaped != -1) {
            this.escaped = false;
            return indexUnEscaped;
        }

        if (indexEscaped != -1) {
            this.escaped = true;
            return indexEscaped;
        }


        return -1;

    }

    /** These two are here so I can add JSTL style support later. */
    protected boolean lineHasCommand() {
        return false;
    }

    protected void processLineCommand(CharBuf output, char[] line ){

    }


    protected String escape(String charSequence ){

        if (sameStart && charSequence.charAt(0) == '{' ) {
            charSequence = Str.sliceOf(charSequence, 1);
        }
        if (escaped) {
            return charSequence; //No op
        } else {
            return charSequence;
        }
    }

    private void processCommandOrExpression(CharBuf output, char[] line, int index) {
        int startIndex;

        char [] expressionStart;
        char [] expressionEnd;

        if (escaped ) {

            expressionStart= this.expressionStart;
            expressionEnd = this.expressionEnd;
        } else {
            expressionStart= this.unescapedExpressionStart;
            expressionEnd = this.unescapedExpressionEnd;
        }

        startIndex = index + expressionStart.length;

        index = findChars(expressionEnd, startIndex, line);

        if (index == -1) {
            output.add(line);
            return;
        }

        String command = noCopyStringFromChars( copyOfRange(line, startIndex, index) );


        index = index + expressionEnd.length;

        int lineNumber = this.lineIndex;
        processCommand(index, output, command); //processCommand can advance line number so be careful



        int findIndex = findExpressionFromIndex(line, index);


        if (lineNumber == lineIndex) {
            if (findIndex == -1) {
                output.add(copyOfRange(line, index, line.length));
            } else {

                output.add(copyOfRange(line, index, findIndex));

                processCommandOrExpression(output, line, findIndex);
            }
        }
    }



    private void processCommand(int index, CharBuf output, String command ) {

        if (!command.startsWith(commandMarker)) {
            handleExpression(output, command);
        } else {
            handleCommand(index, output, command);
        }
    }

    private void handleCommand(int index, CharBuf output, String command) {
        String cmd = Str.slc(command, commandMarker.length(), command.indexOf(' '));
        String arguments = Str.slc(command, commandMarker.length() + cmd.length() +1 );
        CharSequence block;
        CharSequence[] blocks;
        String endOfBlock;
        endOfBlock = Str.add(endBlockStart, cmd, endBlockEnd);


        switch (Commands.command(cmd)) {

            case UNLESS:
                blocks = readBlocks(index, elseBlock, endOfBlock);
                processUnless(output, arguments, blocks);
                break;


            case LENGTH:
                blocks = readBlocks(index, elseBlock, endOfBlock);
                processLength(output, arguments, blocks);
                break;

            case IF:
                blocks = readBlocks(index, elseBlock, endOfBlock);
                processIf(output, arguments, blocks);
                break;

            case EACH:
                block = readBlock( index, endOfBlock );
                processEach(output, arguments, block);
                break;

            case WITH:
                block = readBlock( index, endOfBlock );
                processWith(output, arguments, block);
                break;

            default:

                block = readBlock(index,  endOfBlock );
                Command commandObject = commandMap.get(cmd);
                if (commandObject!=null) {
                        commandObject.processCommand(output, arguments, block, context);
                }

          }
    }


    private void handleExpression(CharBuf output, String command) {

        if (command.contains("(")) {
            handleFunction(output, command);
        } else {
            Object object;
            object = lookup(command);
            String str = Str.toString(object, command);
            output.add( escape(str) );
        }
    }


    private void handleFunction(CharBuf output, String command) {

        String[] split = StringScanner.splitByChars(command, '(', ')');
        String methodName = split[0];
        MethodAccess method = this.functionMap.get(methodName);
        if (method==null) {
            return;
        }

        String arguments = split[1];
        Object args = getObjectFromArguments(arguments);

        Object out = Invoker.invokeMethodFromObjectArg(method.bound(), method, args);

        if ( out != null ) {
            output.add(out.toString());
        }

    }


    private Object lookup(String objectName) {
        return lookup(objectName, objectName);
    }

    private Object lookup(String objectName, String defaultValue) {
        Object value =  findProperty(context, objectName);
        if (value == null) {
            if (parentTemplate!=null) {
                value = parentTemplate.lookup(objectName);
            }
        }
        return value == null ? defaultValue : value;
    }

    private void processEach(CharBuf output, String arguments, CharSequence block) {


        Object object = getObjectFromArguments(arguments);


        if (object instanceof Map) {
            eachMapProperty(output, block, object);
        } else {

            eachListItem(output, block, object);
        }

    }

    private void eachListItem(CharBuf output, CharSequence block, Object object) {
        Iterator iterator = Conversions.iterator(object);
        int len = Boon.len(object);
        int index = 0;
        CharSequence blockOutput;

        Map<String, Object> map = map("@length", len, "@array", object);


        while (iterator.hasNext()) {

            Object item = iterator.next();
            map.put("@index", index );
            map.put("@first", index == 0 );
            map.put("@last",  index == len-1 );
            map.put("@even",  index % 2 == 0 );
            map.put("@odd",   index % 2 != 0 );
            map.put("this",   item );
            map.put("item",   item );
            blockOutput = template(expressionStart, expressionEnd)
                    .replace(block, list(item, map, context));
            output.add(blockOutput);
            index++;
        }
    }

    private void eachMapProperty(CharBuf output, CharSequence block, Object object) {
        Map<Object, Object> objectMap = (Map<Object, Object>) object;
        Set<Map.Entry<Object, Object>> entries = objectMap.entrySet();
        int len = objectMap.size();
        int index = 0;
        CharSequence blockOutput;


        Map <String, Object> map = map("@length", len, "@array", object);


        for (Map.Entry<Object, Object> entry : entries) {
            map.put("@index", index );
            map.put("@first", index == 0 );
            map.put("@last",  index == len-1 );
            map.put("@even",  index % 2 == 0 );
            map.put("@odd",   index % 2 != 0 );
            map.put("@value",  entry.getValue() );
            map.put("@this",  entry.getValue() );
            map.put("@key",  entry.getKey() );
            map.put("this",  entry.getValue() );

            blockOutput = template(expressionStart, expressionEnd)
                    .replace(block, list(entry.getValue(), map, context));
            output.add(blockOutput);



            index++;
        }
    }


    private void processWith(CharBuf output, String arguments, CharSequence block) {

        Object object = getObjectFromArguments(arguments);

        CharSequence blockOutput = template(this.expressionStart, this.expressionEnd)
                    .replace(block, list(map("@this", object, "this", object), context));
        output.add(blockOutput);
    }



    private void processIf(CharBuf output, String arguments, CharSequence[] blocks) {
        doProcessIf(output, arguments, blocks, false);
    }

    private void processUnless(CharBuf output, String arguments, CharSequence[] blocks) {
        doProcessIf(output, arguments, blocks, true);
    }

    private void processLength(CharBuf output, String arguments, CharSequence[] blocks) {
        Object object = getObjectFromArguments(arguments);

    }


    private void doProcessIf(CharBuf output, String arguments, CharSequence[] blocks, boolean negate) {

        boolean test = true;
        Object oTest;

        if (arguments.startsWith("[") || arguments.startsWith("\"") || arguments.startsWith("{")) {
            arguments = createJSTL().replace(arguments, context).toString();

            oTest = fromJson(arguments.replace('\'', '"'));
            test = Conversions.toBoolean(oTest);
        } else if(arguments.contains(" ")) {
            arguments = createJSTL().replace(arguments, context).toString();
            String[] strings = Str.splitBySpace(arguments);
            List list = Lists.list(strings);

            for (String string : strings) {
                oTest = lookup(string);
                if (test) test = Conversions.toBoolean(oTest);
                if (oTest != null) {
                    list.add(oTest);

                } else {
                    list.add(string);
                }
             }
            oTest = list;

        }
        else  {
            oTest = lookup(arguments, null);
            test = Conversions.toBoolean(oTest);
        }

        CharSequence ifBody = blocks[0];
        CharSequence elseBody = blocks.length == 2 ? blocks[1] : null;

        if (negate) {
            test = !test;
        }

        if (test) {
            CharSequence blockOutput = createTemplate().replace(ifBody, list(map("test", oTest, "this", oTest), context));
            output.add(blockOutput);
        } else {
            if (elseBody!=null) {
                CharSequence blockOutput = createTemplate().replace(elseBody, list(map("test", oTest, "this", oTest), context));
                output.add(blockOutput);
            }
        }
    }

    BoonTemplate parentTemplate;

    private BoonTemplate createTemplate() {
        BoonTemplate boonTemplate = template(this.expressionStart, this.expressionEnd);
        boonTemplate.parentTemplate = this;
        boonTemplate.elseBlock = this.elseBlock;
        boonTemplate.endBlockEnd = this.endBlockEnd;
        boonTemplate.endBlockStart = this.endBlockStart;
        boonTemplate.commandMarker = this.commandMarker;
        boonTemplate.expressionEnd = this.expressionEnd;
        boonTemplate.expressionStart = this.expressionStart;
        boonTemplate.unescapedExpressionStart = this.unescapedExpressionEnd;
        boonTemplate.unescapedExpressionEnd = this.unescapedExpressionEnd;


        return boonTemplate;
    }


    private CharSequence[] readBlocks(int startLine, String elseBlock, String endBlock2) {
        CharBuf buf = CharBuf.create(80);



        if (readBlockFindFirstLine(startLine, endBlock2, buf)) return new CharSequence[]{buf};

        CharBuf buf1 = null;
        CharBuf buf2 = null;
        int index;

        for (; lineIndex<lines.length; lineIndex++) {

            index = findString(elseBlock, lines[lineIndex]);
            if (index != -1) {
                buf1 = buf;
                buf = CharBuf.create(80);
                continue;
            }

            index = findString(endBlock2, lines[lineIndex]);
            if (index != -1) {

                if (buf1 != null) {
                   buf2 = buf;
                };
                break;
            }


            buf.add(lines[lineIndex]);


        }


        if (buf2 == null) {
            return new CharBuf[]{buf};
        } else {
            return new CharBuf[]{buf1, buf2};
        }

    }


    private CharSequence readBlock(int startIndexOfFirstLine, String endBlock) {
        CharBuf buf = CharBuf.create(80);

        if (readBlockFindFirstLine(startIndexOfFirstLine, endBlock, buf)) return buf;

        for (; lineIndex<lines.length; lineIndex++) {

            int index = findString(endBlock, lines[lineIndex]);
            if (index != -1) {
                return buf;
            } else {
                buf.add(lines[lineIndex]);
            }
        }

        return buf;
    }

    private boolean readBlockFindFirstLine(int startIndexOfFirstLine, String endBlock, CharBuf buf) {
        char[] line = lines[lineIndex];

        int endIndexOfFirstLineCommandBody = findString(endBlock, lines[lineIndex]);

        if (endIndexOfFirstLineCommandBody == -1) {
            line = Chr.sliceOf(line, startIndexOfFirstLine); //FIX ME.. can't use sliceOf... have to do some real logic here.
        } else {

            line = Chr.sliceOf(line, startIndexOfFirstLine, endIndexOfFirstLineCommandBody);

            buf.add(line);
            lineIndex++;
            return true;
        }

        //buf.add(line); //DIRTY HACK.. you have to fix the line index above
        lineIndex++;
        return false;
    }


    private Object getObjectFromArguments(String arguments) {
        Object object;
        if (arguments.startsWith("[") || arguments.startsWith("\"") || arguments.startsWith("{")) {
            arguments = createJSTL().replace(arguments, context).toString();

            object = fromJson(arguments.replace('\'', '"'));
        } else if(arguments.contains(" ")) {
            arguments = createJSTL().replace(arguments, context).toString();
            String[] strings = Str.splitBySpace(arguments);
            List list = Lists.list(strings);

            for (String string : strings) {
                object = lookup(string);
                if (object != null) {
                    list.add(object);
                } else {
                    list.add(string);
                }
            }
            object = list;

        }
        else  {
            object = lookup(arguments);
        }
        return object;
    }

    private BoonTemplate createJSTL() {
        BoonTemplate jstl = jstl();
        jstl.parentTemplate = this;
        return jstl;
    }


}
