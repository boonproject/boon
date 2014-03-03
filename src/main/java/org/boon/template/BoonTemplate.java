package org.boon.template;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.core.Conversions;

import static java.util.Arrays.copyOfRange;


import org.boon.core.Function;
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
import static org.boon.Maps.map;
import static org.boon.core.reflection.BeanUtils.*;
import static org.boon.core.reflection.FastStringUtils.noCopyStringFromChars;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.primitive.CharScanner.*;

/**
 * Created by Richard on 2/27/14.

 TODO
 finish making properties configurable

 TODO
 add a way to add a single command or function

 TODO
 make command object's properties exposed to the template... ? (Maybe)

 TODO
 implement handlebar templates
 Template comments with {{! }} or {{!-- --}}.

 TODO
 handle different forms of this
 Handlebars also allows for name conflict resolution between helpers and data fields via a this reference:
 <p> {{this.name}}</p>
 Any of the above would cause the name field on the current context to be used rather than a helper of the same name.

 <p>
 TODO
 If you find a "function" (Provider or Function), call it instead of evaluating it and converting it toString.
 Function gets passed context as argument to apply.

 TODO
 Need ability to add Function<> interface implementations as well.


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

 NOTE need a way to invoke templates from a template
 //

 TODO implement include
 he uses embedded for include {{embedded "user"}}
 I think I will stick with include but support embedded

 //https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java
 I have some too. :) //lower, upper, rpad, etc. https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java


 TODO implement all standard JSTL functions

 <pre>
 fn:contains()	Tests if an input string contains the specified substring.
 fn:containsIgnoreCase()	Tests if an input string contains the specified substring in a case insensitive way.
 fn:endsWith()	Tests if an input string ends with the specified suffix.
 fn:escapeXml()	Escapes characters that could be interpreted as XML markup.
 fn:indexOf()	Returns the index withing a string of the first occurrence of a specified substring.
 fn:join()	Joins all elements of an array into a string.
 fn:length()	Returns the number of items in a collection, or the number of characters in a string.
 fn:replace()	Returns a string resulting from replacing in an input string all occurrences with a given string.
 fn:split()	Splits a string into an array of substrings.
 fn:startsWith()	Tests if an input string starts with the specified prefix.
 fn:substring()	Returns a subset of a string.
 fn:substringAfter()	Returns a subset of a string following a specific substring.
 fn:substringBefore()	Returns a subset of a string before a specific substring.
 fn:toLowerCase()	Converts all of the characters of a string to lower case.
 fn:toUpperCase()	Converts all of the characters of a string to upper case.
 fn:trim()	Removes white spaces from both ends of a string.
 </pre>
 TODO look at functions here
 https://github.com/elving/swag
 </p>
 */
public abstract class BoonTemplate {

    /** These two are here so I can add JSTL style support later for <c:if and <c:each, etc. */
    protected abstract boolean lineHasCommand();


    /** These two are here so I can add JSTL style support later for <c:if and <c:each, etc. */
    protected abstract boolean processLineCommand(CharBuf output, char[] line );

    static class BoonTemplateMustacheLike extends BoonTemplate {

        BoonTemplateMustacheLike() {
        }

        BoonTemplateMustacheLike(char[] expressionStart, char[] expressionEnd, Object functions) {
            super(expressionStart, expressionEnd, functions);
        }

        @Override
        protected final boolean lineHasCommand() {
            return false;
        }

        @Override
        protected final boolean processLineCommand(CharBuf output, char[] line) {
            return true;
        }
    }


    private static char[] startOfJSTLCommand = "<c:".toCharArray();


    private static char[] ifCommand = "if".toCharArray();

    private static char[] ifCommandTestAttribute = "test".toCharArray();

    private static char[] forEachCommand = "forEach".toCharArray();


    private static char[] forEachCommandItemsAttribute = "items".toCharArray();

    static class BoonTemplateJSTLLike extends BoonTemplate {

        BoonTemplateJSTLLike () {
            this.expressionStart = FastStringUtils.toCharArray("${");
            this.expressionEnd = FastStringUtils.toCharArray("}");
            this.unescapedExpressionStart = FastStringUtils.toCharArray("${fn:escapeXml(");
            this.unescapedExpressionEnd = FastStringUtils.toCharArray("}");
            this.expressionStart1stChar = '$';
            this.unescapedExpressionStartChar = '$';



        }

        @Override
        protected final boolean lineHasCommand() {
            return true;
        }

        @Override
        protected final boolean processLineCommand(CharBuf output, char[] line) {
            int index = 0;
            int indexOfIf = 0;
            int indexOfEach = 0;
            CharSequence block;


            /** See if the line has <c: */
            if ( (index = (findChars(startOfJSTLCommand, line) )) != -1 ) {

                /** See if if follows <c: as in <c:if */
                if ( (indexOfIf = findChars(ifCommand, index, line)) != -1 ) {

                    index = findChars(ifCommandTestAttribute, indexOfIf, line);

                    if (index == -1) {
                        return false;
                    }

                    index = findChar('=', index + ifCommandTestAttribute.length, line);

                    if (index == -1) {
                        return false;
                    }

                    index = findChar('"', index, line);


                    if (index == -1) {
                        return false;
                    }

                    int endIndex = CharScanner.findEndQuote(line, index+1);


                    String arguments = FastStringUtils.noCopyStringFromChars(Arrays.copyOfRange(line, index+1, endIndex));
                    block = readBlock(index, "</c:if>");

                    processIf(output, arguments, new CharSequence[]{block});

                    return true;

                }else if ( (indexOfEach = findChars(forEachCommand, index, line)) != -1 )  {


                        index = findChars(forEachCommandItemsAttribute, indexOfEach, line);

                        if (index == -1) {
                            return false;
                        }

                        index = findChar('=', index + forEachCommandItemsAttribute.length, line);

                        if (index == -1) {
                            return false;
                        }

                        index = findChar('"', index, line);


                        if (index == -1) {
                            return false;
                        }

                        int endIndex = CharScanner.findEndQuote(line, index+1);


                        String arguments = FastStringUtils.noCopyStringFromChars(Arrays.copyOfRange(line, index+1, endIndex));


                        index = findChar('>', index, line);
                        block = readBlock(index, "</c:forEach>");

                        processEach(output, arguments, block);

                        return true;


                }
            }

            return false;
        }
    }


    protected boolean strictChecking;

    /** Set the start of expression demarcation. As in {{Hello}}
     *  {{Hello}}
     *  ^
     *  |
     *  |
     * */
    protected char[] expressionStart = "{{".toCharArray();

    /** Sets how the beginning of the end block start should look .
     *
     *  {{if}}
     *
     *  {{/if}}
     *  ^
     *  |
     *  |
     * */
    protected String endBlockStart = "{{/";

    /**
     * Three {{{Text in here will not get escaped so you can have <ol/> }}}
     *
     * BoonTemplate does not always support HTML output or rather Boon template does not only support
     * HTML so you have to enable HTML escaping for {{HTML ESCAPED}} and {{{HTML UNESCAPED TO WORK}}}
     * By default everything is unescaped.
     */
    protected char[] unescapedExpressionStart = "{{{".toCharArray();
    protected char[] unescapedExpressionEnd = "}}}".toCharArray();

    /**
     * It detects if the escaped and unescaped start with the same char.
     */
    protected final boolean sameStart;

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
    protected String endBlockEnd = "}}";

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
    protected char[] expressionEnd = "}}".toCharArray();

    /**
     * First char used to search strings quickly for expressions or commands.
     */
    protected char expressionStart1stChar = expressionStart[0];

    /**
     * First char used to search strings quickly for expressions or commands.
     */
    protected char unescapedExpressionStartChar = unescapedExpressionStart[0];

    /**
     * Command marker.
     *
     * {{#if}}
     *  ^
     *  |
     */
    protected String commandMarker = "#";

    /**
     * I was not sure how to handle this so I punted.
     */
    protected String elseBlock = "{{else}}";

    //State
    /** Current line number. */
    protected int lineIndex;
    /** List of lines we are processing. */
    protected char[][] lines;
    /** Context object that we are getting expressions from. */
    protected Object context;

    /** Whether we support escaped mode or not for HTML output. */
    boolean escaped;

    /** Commands that we support. A command is more or less like a tag handler in JSP. */
    protected Map<String, Command> commandMap;


    /** Functions can be used anywhere where expressions can be used. */
    protected Map<String, MethodAccess> methodMap;


    /** Functions can be used anywhere where expressions can be used. */
    protected Map<String, Function> functionMap;


    /**
     * Plug-able String escaping. e.g., you can plug in an HTML string escaper if you want.
     */
    Function<String, String> stringEscaper = null;



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

    public boolean strictChecking() {
        return strictChecking;
    }

    public BoonTemplate strictChecking(boolean strictChecking) {
        this.strictChecking = strictChecking;
        return this;
    }

    /** Adds functions from class (static methods) or instance (regular methods)).
     * Every method in this object becomes a command handler.
     * */
    public BoonTemplate addFunctions(Object object) {
        extractFunctions(object, true);
        return this;
    }


    /** Adds function with a give name.
     * */
    public BoonTemplate addFunction(String name, Function function) {

        if  (this.functionMap == null) {
            this.functionMap = new HashMap<>();
        }

        functionMap.put(name, function);
        return this;
    }


    /** Adds function with a give name.
     * */
    public BoonTemplate addFunction(String methodName, Object functions) {

        if  (this.methodMap == null) {
            this.methodMap = new HashMap<>();
        }


        ClassMeta<?> classMeta = ClassMeta.classMetaEither(functions);
        MethodAccess method = classMeta.method(methodName);


        return this;
    }

    /** Adds functions from class (static methods) or instance (regular methods)).
     * Every method in this object becomes a command handler.
     *
     /*
     *  functionMap()
     *  commandMap()
     *  methodMap()
     *  lineIndex()
     *  line()
     *  context()
     *  parentTemplate()
     *  getThis()
     */
    public BoonTemplate addTemplateAsFunctions() {

        this.addFunction("functionMap", this);
        this.addFunction("commandMap", this);
        this.addFunction("methodMap", this);
        this.addFunction("lineIndex", this);
        this.addFunction("line", this);
        this.addFunction("parentTemplate", this);
        this.addFunction("context", this);
        this.addFunction("getThis", this);

        return this;
    }


    /** Adds commandHandlers from class (static methods) or instance (regular methods)).
     * Every method in this object becomes a command handler.
     **/
    public BoonTemplate addCommandHandlers(Object object) {
        extractFunctions(object, false);
        return this;
    }

    /**
     *
     * @return returns the start of the expression, ie., the '{{' of {{name}}
     */
    public String expressionStart() {
        return FastStringUtils.noCopyStringFromChars(expressionStart);
    }

    /**
     *
     * @param v sets the '{{' of {{name}} and the '{{' of '{{#if}}
     * @return template
     */
    public BoonTemplate expressionStart(String v) {
        this.expressionStart = FastStringUtils.toCharArray(v);
        return this;

    }

    /**
     *
     * @return returns the end of the block start, i.e., the '}}' of {{#if}}
     */
    public String endBlockStart() {
        return endBlockStart;
    }


    /**
     *
     /**
     *
     * sets the end of the block start, i.e., the '}}' of {{#if}}
     *
     * @param v
     * @return
     */
    public BoonTemplate endBlockStart(String v) {
        this.endBlockStart = v;
        return this;
    }

    /**
     * Returns the start of an unescaped expression, i.e., "{{{"
     * @return
     */
    public String unescapedExpressionStart() {
        return  FastStringUtils.noCopyStringFromChars(unescapedExpressionStart);
    }


    /**
     * Sets the start of an unescaped expression, i.e., "{{{"
     * @param v
     * @return
     */
    public BoonTemplate unescapedExpressionStart(String v) {
        this.unescapedExpressionStart = FastStringUtils.toCharArray(v);
        return this;
    }


    /**
     * Returns the end of an unescaped expression, i.e., the "}}}" of value.
     *
     * @return
     */
    public String unescapedExpressionEnd() {

        return  FastStringUtils.noCopyStringFromChars(unescapedExpressionEnd);
    }

    /**
     * Sets the end of an unescaped expression, i.e., the "}}}" of value.
    */
    public BoonTemplate unescapedExpressionEnd(String v) {
        this.unescapedExpressionEnd = FastStringUtils.toCharArray(v);
        return this;
    }


    /**
     * returns the '}}' or {{/if}}
     * @return
     */
    public String endBlockEnd() {
        return endBlockEnd;
    }




    /**
     * returns the '}}' or {{/if}}
     * @return
     */
    public BoonTemplate endBlockEnd(String v) {
        this.endBlockEnd = v;
        return this;
    }


    /**
     * returns the '}}' of {{value}}
     * @return
     */
    public String expressionEnd() {
        return  FastStringUtils.noCopyStringFromChars(expressionEnd);
    }


    /**
     * Sets the '}}' of {{value}}
     * @return
     */
    public BoonTemplate expressionEnd(String v) {
        this.expressionEnd = FastStringUtils.toCharArray(v);
        return this;

    }


    /**
     * Sets the '#' of {{#if}}
     * @return
     */
    public String commandMarker() {
        return commandMarker;
    }


    /**
     * Sets else block marker in total. "{{else}}"
     * @return
     */
    public String elseBlock() {
        return elseBlock;
    }

    /** Returns the current line index */
    public int lineIndex() {
        return lineIndex;
    }




    /** Returns the current line. */
    public String line() {
        return FastStringUtils.noCopyStringFromChars(lines[lineIndex]);
    }


    /** Returns the current context. */
    public Object context() {
        return context;
    }


    /** Returns the current context. */
    public Object getThis() {
        return context;
    }


    /** returns the current command map. */
    public Map<String, Command> commandMap() {
        return commandMap;
    }

    /** returns the current function map. */
    public Map<String, MethodAccess> functionMap() {
        return methodMap;
    }


    public BoonTemplate parentTemplate() {
        return parentTemplate;
    }

    private void extractFunctions(Object functions, boolean all) {

        if (commandMap == null) {
            commandMap = new HashMap<>();

        }

        if (methodMap == null) {
            methodMap = new HashMap<>();
        }

        ClassMeta<?> classMeta = ClassMeta.classMetaEither(functions);

        Iterable<MethodAccess> methods = classMeta.methods();

        for (MethodAccess methodAccess : methods) {
            methodMap.put(methodAccess.name(), methodAccess.methodAccess().bind(functions));
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



        if (!all) {
            for (MethodAccess methodAccess : methods) {
                if (methodAccess.respondsTo(CharBuf.class, String.class, CharSequence.class, Object.class)) {
                    commandMap.put(Str.add(classMeta.name(), ".", methodAccess.name()), new InvokeCommand(functions, methodAccess));
                } else if (methodAccess.respondsTo(String.class, CharSequence.class, Object.class)) {
                    commandMap.put(Str.add(classMeta.name(), ".", methodAccess.name()), new InvokeCommand(functions, methodAccess));
                }
            }


        } else {
            for (MethodAccess methodAccess : methods) {
                commandMap.put(Str.add(classMeta.name(), ".", methodAccess.name()), new InvokeCommand(functions, methodAccess));
            }
        }
    }

    /**
     * Constructor
     */
    public BoonTemplate() {

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

    }

    /**
     * Creates a new template.
     * @return
     */
    public static BoonTemplate template() {
        return new BoonTemplateMustacheLike();
    }


    /**
     * Creates a new JSTL template.
     * @return
     */
    public static BoonTemplate jstl() {
        return new BoonTemplateJSTLLike();
    }


    /**
     * Creates a new template and allows you to define the start expression and the end expression.
     * @param expStart
     * @param expEnd
     * @return
     */
    public static BoonTemplate template(String expStart, String expEnd) {
        return new BoonTemplateMustacheLike(expStart.toCharArray(), expEnd.toCharArray(), null);
    }


    /**
     * Creates a new template and allows you to define the start expression and the end expression.
     * @param expStart
     * @param expEnd
     * @return
     */
    private static BoonTemplate template(char[] expStart, char[] expEnd) {
        return new BoonTemplateMustacheLike(expStart, expEnd, null);
    }


    /**
     * Creates a new template and allows you to define the start expression and the end expression.
     * @param expStart
     * @param expEnd
     * @return
     */
    public static BoonTemplate template(char[] expStart, char[] expEnd, Object functions) {
        return new BoonTemplateMustacheLike(expStart, expEnd, functions);
    }


    /**
     * BoonTemplate with functions
     * @param functions
     * @return
     */
    public static BoonTemplate templateWithCommandHandlers(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplateMustacheLike();
        boonTemplate.extractFunctions(functions, true);
        return boonTemplate;
    }


    /**
     * Template with functions
     * @param functions
     * @return
     */
    public static BoonTemplate templateWithFunctions(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplateMustacheLike();
        boonTemplate.extractFunctions(functions, true);
        return boonTemplate;

    }


    /**
     * Replace template with properties from context.
     * The context can be a Java Bean, a hash map or a list of JavaBeans or HashMaps.
     * The search order of the objects is determine by their order in the list.
     *
     * @param template the template (a string or char sequence that constitutes the template)
     * @param context instance, map or list of maps/instances or a JSON string which can be a list or a list of objects(maps)
     * @return char sequence of applied results of context to sequence.
     */
    public CharSequence replace (CharSequence template, Object context) {

        initContext(context);
        char[] chars = FastStringUtils.toCharArray(template);
        CharBuf output = CharBuf.create(template.length());

        lines = CharScanner.splitLines(chars);

        for ( lineIndex = 0; lineIndex < lines.length; lineIndex++ ) {
            char [] line = lines[lineIndex];
            //puts("LINE", new String(line));

            if (lineHasCommand()) {
                if (processLineCommand(output, line) ) continue;
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


    /**
     * Initialize the context object.
     * @param context
     */
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


    /** Searches the line for an expression. */
    private int findExpression(char[] line) {

        return findExpressionFromIndex(line, 0);
    }

    /** Searches from the startIndex for expressions. */
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


    /** Handles string escaping. */
    protected String escape(String charSequence ){

        if (sameStart && charSequence.charAt(0) == '{' ) {
            charSequence = Str.sliceOf(charSequence, 1);
        }
        if (escaped && stringEscaper!=null) {
            return stringEscaper.apply(charSequence);
        } else {
            return charSequence;
        }
    }

    /**
     * Pluggable String escaper
     * @return
     */
    public Function<String, String> stringEscaper() {
        return stringEscaper;
    }

    public BoonTemplate setStringEscaper(Function<String, String> stringEscaper) {
        this.stringEscaper = stringEscaper;
        return this;
    }


    /**
     * Processes a command or an expression.
     *
     * Commands are like {{#if}}
     * An Expression is like {{Rick}}
     * @param output
     * @param line
     * @param index
     */
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


    /**
     * Processes a command or an expression.
     * @param index
     * @param output
     * @param command
     */
    private void processCommand(int index, CharBuf output, String command ) {

        if (!command.startsWith(commandMarker)) {
            handleExpressionOrFunction(output, command);
        } else {
            handleCommand(index, output, command);
        }
    }

    /**
     * Handle a command like {{#if}}, {{#each}}, {{#myCommand}}
     * @param index
     * @param output
     * @param command
     */
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


    /**
     * Handles an expression like {{name}} or function call like {{doIt( 1, 2, ${name} ) }}.
     * You can override this and change the way that expressions and function calls are handled.
     * @param output output char buffer
     * @param command name of the command.
     */
    protected void handleExpressionOrFunction(CharBuf output, String command) {

        if (command.contains("(")) {
            handleFunction(output, command);
        } else {
            handleExpression(output, command);
        }
    }

    /**
     * Handle an expression
     * @param output
     * @param command
     */
    protected void handleExpression(CharBuf output, String command) {

        Object object;
        object = lookup(command);
        String str = Str.toString(object, command);
        output.add( escape(str) );
    }


    /**
     * Handles an expression that results in a function call.
     * You can override this and change the way that expressions are handled.
     * @param output output char buffer
     * @param command name of the command.
     */
    private void handleFunction(CharBuf output, String command) {

        String[] split = StringScanner.splitByChars(command, '(', ')');
        String methodName = split[0];
        MethodAccess method = this.methodMap.get(methodName);
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

    /**
     * Handles the for each operation, i.e., handles the {{#each}} command.
     * You can override this.
     *
     * @param output
     * @param arguments
     * @param block
     */
    protected void processEach(CharBuf output, String arguments, CharSequence block) {


        Object object = getObjectFromArguments(arguments);


        if (object instanceof Map) {
            eachMapProperty(output, block, object);
        } else {

            eachListItem(output, block, object);
        }

    }

    /**
     * Handles the for each case in the case of collections and arrays.
     * You can override this.
     *
     * @param output the output buffer
     * @param block the template block for the each
     * @param object the actual List or Array like object
     */
    protected void eachListItem(CharBuf output, CharSequence block, Object object) {
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

    /**
     * Handles the for each operation fo Maps.
     * You can override this.
     *
     * @param output the output buffer
     * @param block the template block for the each
     * @param object the output object, i.e., the Map or map like thing.
     */
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


    /**
     * Handles the {{#with}}. You can override this.
     *
     * @param output the output buffer
     * @param arguments arguments to the with handler
     * @param block the actual template block
     */
    protected void processWith(CharBuf output, String arguments, CharSequence block) {

        Object object = getObjectFromArguments(arguments);

        CharSequence blockOutput = template(this.expressionStart, this.expressionEnd)
                    .replace(block, list(map("@this", object, "this", object), context));
        output.add(blockOutput);
    }



    /**
     * Handles the {{#if}} command. You can override this.
     *
     * @param output the output buffer
     * @param arguments arguments to the with handler
     * @param blocks the actual template blocks, the first item is the if, the second item is the else if present
     */
    protected void processIf(CharBuf output, String arguments, CharSequence[] blocks) {
        doProcessIf(output, arguments, blocks, false);
    }


    /**
     * Handles the {{#unless}} command. You can override this.
     *
     * @param output the output buffer
     * @param arguments arguments to the with handler
     * @param blocks the actual template blocks, the first item is the if, the second item is the else if present
     */
    private void processUnless(CharBuf output, String arguments, CharSequence[] blocks) {
        doProcessIf(output, arguments, blocks, true);
    }

    /**
     * Handles the {{length}} command. You can override this.
     * @param output the output buffer
     * @param arguments arguments to the length handler.
     * @param blocks the actual template blocks, the first item is the if, the second item is the else if present
     */
    private void processLength(CharBuf output, String arguments, CharSequence[] blocks) {

        String[] strings = Str.splitBySpace(arguments);
        if (strings.length > 1) {
            String len = strings[0];

            int length = (int) getObjectFromArguments( len );

            Collection collection = (Collection) getObjectFromArguments(Str.join(' ', org.boon.Arrays.sliceOf(strings, 1)));

            //NOT DONE TODO

        }
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

        BoonTemplate boonTemplate;

        if (this instanceof BoonTemplateMustacheLike) {
            boonTemplate = template(this.expressionStart, this.expressionEnd);
        } else if (this instanceof BoonTemplateJSTLLike) {
            boonTemplate = jstl();
        } else {
            boonTemplate = template(this.expressionStart, this.expressionEnd);
        }
        boonTemplate.parentTemplate = this;
        boonTemplate.elseBlock = this.elseBlock;
        boonTemplate.endBlockEnd = this.endBlockEnd;
        boonTemplate.endBlockStart = this.endBlockStart;
        boonTemplate.commandMarker = this.commandMarker;
        boonTemplate.expressionEnd = this.expressionEnd;
        boonTemplate.expressionStart = this.expressionStart;
        boonTemplate.unescapedExpressionStart = this.unescapedExpressionStart;
        boonTemplate.unescapedExpressionEnd = this.unescapedExpressionEnd;


        return boonTemplate;
    }


    /**
     * reads a block that has an else and an if.
     * @param startLine
     * @param elseBlock
     * @param endBlock
     * @return
     */
    protected CharSequence[] readBlocks(int startLine, String elseBlock, String endBlock) {
        CharBuf buf = CharBuf.create(80);



        if (readBlockFindFirstLine(startLine, endBlock, buf)) return new CharSequence[]{buf};

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

            index = findString(endBlock, lines[lineIndex]);
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


    /**
     * Reads the guts of a block so
     *
     * Given this if block:
     *
     * {{#if}}
     *
     *   THIS TEXT WILL BE PART OF THE BLOCK
     *   EVERYTHING IN THE IF, INCLUDING SPACES and \n
     *
     * {{/if}}
     *
     *
     * @param startIndexOfFirstLine
     * @param endBlock
     * @return
     */
    protected CharSequence readBlock(int startIndexOfFirstLine, String endBlock) {
        CharBuf buf = CharBuf.create(80);

        if (readBlockFindFirstLine(startIndexOfFirstLine, endBlock, buf)) return buf;

        for (; lineIndex<lines.length; lineIndex++) {

            int index = findString(endBlock, lines[lineIndex]);
            String line = line();

            if (index != -1) {
                return buf;
            } else {
                buf.add(lines[lineIndex]);
            }
        }

        return buf;
    }

    /**
     * Internal helper method.
     * @param startIndexOfFirstLine
     * @param endBlock
     * @param buf
     * @return
     */
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


    /** This method parses the arguments for {{with}}, {{line}}, and {{each}}.
     *
     * It does not do the ones for {{if}} but the one for {{if}} works the same way.
     * If arguments are JSON, then the JSON object is parsed.
     * JSON is a special type where we use single quotes or double quotes, other than
     * that it is plain JSON.
     * If arguments are not JSON but has " ", then the arguments are split using a String split function.
     *
     * If the arguments contain a ${foo}, then foo is replaced with the foo property out of the context.
     */
    protected Object getObjectFromArguments(String arguments) {
        Object object;

        /**
         * If arguments starts with '[' or '{' or '"'  or "'" then we think it JSON.
         */
        if (arguments.startsWith("[") || arguments.startsWith("\"") || arguments.startsWith("{") || arguments.startsWith("'")) {
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

    /**
     * Create a JSTL style template.. internally.
     * @return
     */
    private BoonTemplate createJSTL() {
        BoonTemplate jstl = jstl();
        jstl.parentTemplate = this;
        return jstl;
    }


}
