/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.template;

import org.boon.Str;
import org.boon.core.reflection.FastStringUtils;
import org.boon.primitive.CharScanner;
import org.boon.template.support.Token;
import org.boon.template.support.TokenTypes;

import java.util.ArrayList;
import java.util.List;

import static org.boon.Boon.putl;
import static org.boon.Boon.puts;

public class BoonModernTemplateParser implements TemplateParser{


    char charArray[];

    int index;
    int ch;


    private List<Token> tokenList = new ArrayList<>();



    public void TemplateParser() {

    }

    public void parse(String string) {

        this.charArray = FastStringUtils.toCharArray(string);
        this.index = 0;

        tokenList.clear();


        processLoop();
    }

    @Override
    public List<Token> getTokenList() {

        return this.tokenList;
    }


    @Override
    public void displayTokens(String template) {

        for (Token token : this.getTokenList()) {
            puts("token", token, Str.slc(template, token.start(), token.stop()));
        }
    }

    private void processLoop() {

        int startIndex;

        while (true) {

            startIndex = index;

            index = CharScanner.findChars(TokenTypes.EXPRESSION_START.handleBarStyle(), index, charArray);

            /* If you found something than add a text token up to the point where you found something. */
            if (index != -1) {

                if (startIndex!=index) {
                    tokenList.add(Token.text(startIndex, index));
                }

                index += TokenTypes.EXPRESSION_START.handleBarStyle().length;

                if (!handleCommandOrExpression()) break;
            } else {
                /* If you did not find anything, then just have the text token consume the rest of the buffer. */

                if (startIndex!=charArray.length) {
                    tokenList.add(Token.text(startIndex, charArray.length));
                }
                break;
            }

            if (index > charArray.length || index == -1) {
                break;
            }
        }
    }

    private boolean handleCommandOrExpression() {


        if (index > charArray.length) {
            return false;
        }

        ch = charArray[index];

        if (ch == '#') {
            return handleCommand();
        } else if (ch=='!') {
            return handleComment();
        }
        else {
            return handleExpression();
        }

    }

    private boolean handleComment() {
        index++;
        int foundIndex = CharScanner.findChars(
                TokenTypes.EXPRESSION_END.handleBarStyle(), index, charArray);

        if (foundIndex==-1) {
            return false;
        }

        index = foundIndex + TokenTypes.EXPRESSION_END.handleBarStyle().length;
        return true;
    }

    private boolean handleExpression() {

        int startIndex = index;
        index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), index, charArray);
        if (index > 0) {
            this.tokenList.add(Token.expression(startIndex, index));
            index += TokenTypes.EXPRESSION_END.handleBarStyle().length;

            if ( index<charArray.length) {
                char c = charArray[index];
                if (c == '}') {
                    index++;
                }
            }

            return true;
        }
        return false;
    }

    private boolean handleCommand() {



        int startIndex = index + 1;
        index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), index, charArray);
        if (index == -1) {
            return false;
        }

        this.tokenList.add(Token.commandStart(startIndex, index));


        index += TokenTypes.EXPRESSION_END.handleBarStyle().length;


        Token commandBody = Token.commandBody(index, -1);
        tokenList.add(commandBody);

        while (true) {

            startIndex = index;

            index = CharScanner.findChars(TokenTypes.EXPRESSION_START.handleBarStyle(), index, charArray);


            /* If you found something than add a text token up to the point where you found something. */
            if (index == -1) {
                return false;
            }

            /* Add a text token up to the found expression or command. */
            if (startIndex != index) {
                tokenList.add(Token.text(startIndex, index));
            }
            index += TokenTypes.EXPRESSION_START.handleBarStyle().length;


            if (index >= charArray.length) {
                return false;
            }

            ch = charArray[index];

            if (ch == '#') {
                if (!handleCommand()) break; //nested command
            } else if (ch == '/') {
                commandBody.stop(index - 2);


                index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), index, charArray);

                if (index < 0) return false;
                index += TokenTypes.EXPRESSION_END.handleBarStyle().length;
                break;

            } else {

                if (!handleExpression()) break;
            }

        }


        return true;


    }


    private Token textToken(Token text) {
        if (text != null) {
            text.stop(index);
            if (text.start() != text.stop()) {
                this.tokenList.add(text);
            }
            text = null;
        }
        return text;
    }



}


/**
 * Historical
 * @author Rick Hightower
 *         <p/>
 *         This supports handlebar templates, freemarker-like jstl and JSTL-like templates.
 *         <p/>
 *         <pre>
 *             {{#if foo}}
 *
 *                  {{foo}} //Escaped
 *
 *                  {{{foo}}} //Unescaped
 *             {{/if foo}}
 *
 *             {{#with foo}}
 *
 *                   {{foo}}   //Escaped
 *                   {{{foo}}} /Unescaped
 *
 *             {{/with}}
 *
 *             {{#each fruits}}
 *                  {{this}}
 *             {{/each}}
 *         </pre>
 *         <p/>
 *         see http://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm
 *         see http://www.tutorialspoint.com/jsp/jstl_core_foreach_tag.htm
 *         see http://docs.oracle.com/javaee/5/jstl/1.1/docs/tlddocs/c/forEach.html
 *         <pre>
 *              <c:if test="foo">
 *
 *                  ${fn:escapeXml(foo)} //Escaped
 *                  ${foo} //Unescaped
 *
 *             </c:if>
 *
 *             <c:with item="foo">
 *
 *                  ${fn:escapeXml(foo)} //Escaped
 *                  ${foo} //Unescaped
 *
 *             </c:with>
 *
 *             <c:forEach items="fruits">
 *
 *                  ${item}
 *
 *             </c:forEach>
 *
 *             <c:forEach var="window" items="${windows}">
 *                         <c:out value="${window}"/>
 *             </c:forEach>
 *
 *         </pre>
 *         <p/>
 *         Freemarker like
 *         <pre>
 *              <#if foo>
 *
 *                  ${fn:escapeXml(foo)} //Escaped
 *                  ${foo} //Unescaped
 *
 *             </#if>
 *
 *             <#with foo>
 *
 *                  ${fn:escapeXml(foo)} //Escaped
 *                  ${foo} //Unescaped
 *
 *             </#with>
 *
 *             <#list fruits as fruit>
 *
 *                  ${fruit}
 *
 *             </#list>
 *
 *
 *             <#list fruits>
 *
 *                  ${item}
 *
 *             </#list>
 *
 *
 *         </pre>
 *         <p/>
 *         Freemarker has an expression language for if and such.
 *         There are no plans to add a full expression langauge to this jstl.
 *         The same goes for JSTL-like. Simple boolean expressions and calls to functions but no logic.
 *         <p/>
 *         Velocity-like
 *         <pre>
 *                  <ul>
 *                    #foreach( $product in $allProducts )
 *                         <li>$product</li>
 *                   #end
 *                  </ul>
 *
 *                  #if( $foo )
 *                      <strong>Velocity!</strong>
 *                  #end
 *         </pre>
 *         <p/>
 *         Velocity and Freemarker have if and else, else if.
 *         Handlebars has if / else. Handlebars also has unless.
 *         JSTL just has if.
 *         <p/>
 *         Velocity, JSTL and Freemarker have full expression languages.
 *         Handlebars does not.
 *         <p/>
 *         The plan (currently) is for this templating to have no expressions.
 *         You have property paths that are either true or false only, and you can call functions
 *         that return true or false. Or rather true-y or falsey (null = false, empty list = false, etc.).
 *         Python style true or false which is more ore less what handlebars/mustache do.
 *         So this will be a stupid jstl, i.e., no logic other than true/false.
 *         <p/>
 *         Start of block characters
 *         <pre>
 *             Freemarker-like
 *
 *             <#if  = '<#'
 *
 *             Velocity-like
 *
 *             #if = '#'
 *
 *             JSTL-like
 *
 *             <c:if = '<c:if'
 *
 *             Mustache
 *             {{#if = '{{'
 *
 *         </pre>
 *         <p/>
 *         <p/>
 *         END of start block character
 *         <pre>
 *             Freemarker-like
 *
 *             <#if blah>   = '>'
 *                      ^
 *                      |
 *
 *             Velocity-like
 *
 *             #if<SPACE>  = ' '
 *                   ^
 *                   |
 *
 *             JSTL-like
 *
 *             <c:if test="foo">  = '>'
 *                             ^
 *                             |
 *
 *             Mustache
 *             {{#if    }  = '>'
 *                      ^
 *                      |
 *
 *
 *         </pre>
 *         <p/>
 *         This parser merely delivers up the stuff between start and stop.
 *         Then there are handlers to further divide up the strings as the syntax varies a lot of how
 *         expression of {{if}}, {{each}}, and {{with}} are handled.
 *         <p/>
 *         Start of expression characters
 *         <pre>
 *
 *             Freemarker-like
 *
 *             ${
 *
 *             Velocity-like
 *
 *             $
 *
 *             JSTL-like
 *
 *             ${
 *
 *             Mustache/Handlebar
 *             {{
 *
 *
 *             Mustache/Handlebar
 *             {{{
 *
 *         </pre>
 *         <p/>
 *         <p/>
 *         End of expression characters
 *         <pre>
 *
 *             Freemarker-like
 *
 *             }
 *
 *             Velocity-like
 *
 *             <SPACE>
 *
 *             JSTL-like
 *
 *             }
 *
 *             Mustache/Handlebar
 *             }}
 *
 *
 *             Mustache/Handlebar (NOT HANDLED BY PARSER, HANDLED BY LOOKUP)
 *             }}}
 *
 *         </pre>
 *         <p/>
 *         Rather than treat mustache as having two expressions (tried that), I will treat it as one.
 *         Then the lookupWithDefault mechanism will handle the other case.
 *         <p/>
 *         Velocity allows $vice and ${vice}maniac forms. Rather than handling two forms.
 *         All parsers treat $vice as an expression. So that $foo is always an expression.
 *         This would mean for mustache you would have $vice or {{vice}}maniac.
 *         The caveat being that $expression handlng is a flag so you can turn it off for handlebars by default.
 *         <p/>
 *         One of the goals is to have the ability to write scripts 100% compatible with Handlebars.
 *         <p/>
 *         There is not a similar goal with Velocity, JSTL, and Freemarker.
 *         You can only ever write scripts that are similar to JSTL not true JSTL scripts.
 *         You can only ever write scripts that are similar to Velocity not true Velocity scripts.
 *         You can only ever write scripts that are similar to Freemarker not true Freemarker scripts.
 *         The goal is the ability to write scripts that are 100% Handlebar compatible and can be rendered by browser or Java.
 *         <p/>
 *         The goal is for BoonTemplate is to be a superset of Handlebars and always only a subset of JSTL,
 *         Velocity and Freemarker.
 *         <p/>
 *         Order of importance:
 *         <ol>
 *         <li><Handlebar compliance</li>,
 *         <li>JSTL style support</li>,
 *         <li>Freemarker style support</li>
 *         <li> and then Velocity style support</li>
 *         </ol>
 *         <p/>
 *         Handlebars has the advantage of the expression, and commands start with the same character, which
 *         will help with parsing.
 *         <p/>
 *         <code><pre>
 *         <p/>
 *             if (sameStart) {
 *                 look for start char of command and expression
 *             } else {
 *                 look for start char of expression or command  #NOTE this takes longer
 *             }
 *         </pre></code>
 *         <p/>
 *         Also we need to support comments.
 *         <p/>
 *         <pre>
 *             Velocity like multiline
 *             ##.
 *             .##
 *
 *             Freemarker like
 *             <#--
 *             -->
 *
 *             Handlebar like
 *             {{!
 *             }}
 *
 *             JSTL like (I made this one up JSTL does not have comments JSP does. :)
 *             <c:comment
 *             >
 *         </pre>
 *         <p/>
 *         Input:
 *         char[]
 *         <p/>
 *         Output array of tokens in IndexOverlay style
 *         <pre>
 *
 *         Array item: TemplateToken (Block or Text or Expression or Comment, startIndex, stopIndex)
 *
 *         </pre>
 *         <p/>
 *         There is no logic in this parser. Just an array of token positions.
 *         It is up to BoonTemplate on how to interpret those tokens.
 */
