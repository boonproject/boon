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

import org.boon.Str;
import org.boon.core.reflection.FastStringUtils;
import org.boon.primitive.CharScanner;
import org.boon.template.support.Token;
import org.boon.template.support.TokenTypes;

import java.util.ArrayList;
import java.util.List;

import static org.boon.Boon.putl;
import static org.boon.Boon.puts;


/**
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
public class BoonCoreTemplateParser implements TemplateParser {


    char charArray[];

    int index;
    char ch;


    private List<Token> tokenList = new ArrayList<>();


    @Override
    public void parse(String string) {

        this.charArray = FastStringUtils.toCharArray(string);
        this.index = 0;

        tokenList.clear();

        processLoop();
    }

    private void processLoop() {


        Token text = Token.text(index, -1);


        for (; index < charArray.length; index++) {
            ch = charArray[index];

            if (ch == '<') {
                if (CharScanner.matchChars(TokenTypes.COMMAND_START.jstlStyle(), index, this.charArray)) {


                    text = textToken(text);

                    index += TokenTypes.COMMAND_START.jstlStyle().length;
                    handleCommand();
                }
            }


            else if (ch == '$') {


                char ch1 = charArray[index + 1];
                if (ch1 == '{') {
                    if (CharScanner.matchChars(TokenTypes.EXPRESSION_START.jstlStyle(), index, this.charArray)) {


                        text = textToken(text);

                        index += TokenTypes.EXPRESSION_START.jstlStyle().length;
                        handleCurlyExpression();
                        text = Token.text(index, -1);
                        index--;


                    }
                } else {


                    text = textToken(text);
                    index++;
                    handleExpression(null);
                    text = Token.text(index, -1);
                    index--;


                }

            } else {
                if (text == null) {

                    text = Token.text(index, -1);
                }
            }
        }

        if (text != null) {
            text.stop(charArray.length);
            this.tokenList.add(text);
        }
    }

    private void handleCurlyExpression() {

        int startIndex = index;
        index = CharScanner.findChars(TokenTypes.EXPRESSION_END.jstlStyle(), index, charArray);
        if (index > 0) {
            this.tokenList.add(Token.expression(startIndex, index));
            index += TokenTypes.EXPRESSION_END.jstlStyle().length;

        }
    }

    private void handleExpression(String term) {

        int startIndex = index;
        index = CharScanner.findWhiteSpace(index, charArray);


        if (term != null) {
            if (index == -1) {
                index = startIndex;
                index = CharScanner.findChars(term.toCharArray(), index, charArray);
            }
        }

        if (index == -1) {
            index = charArray.length;
        }
        this.tokenList.add(Token.expression(startIndex, index));
    }


    private void handleCommand() {


        int startIndex = index;
        boolean noBody = false;
        index = CharScanner.findChars(TokenTypes.COMMAND_END_START.jstlStyle(), index, charArray);
        if (index == -1 ) {
            return;
        }

        int foundIndex = CharScanner.findChars(TokenTypes.COMMAND_START_TAG_END.jstlStyle(), index-1, charArray);
        if (foundIndex!=-1) {
            noBody = true;
        }

        if (noBody) {
            index--;

        }

        //Add this command start to the token list.
        this.tokenList.add(Token.commandStart(startIndex, index));


        index += TokenTypes.COMMAND_END_START.jstlStyle().length;


        if (noBody) {

            tokenList.add(Token.commandBody(index, index));

            return;
        }


        Token commandBody = Token.commandBody(index, index);


        tokenList.add(commandBody);



        Token text = Token.text(index, -1);






        for (; index< charArray.length; index++) {
            ch = charArray[index];

            if (ch=='<') {

                if (CharScanner.matchChars(TokenTypes.COMMAND_START.jstlStyle(), index, this.charArray)) {


                    text = textToken(text);

                    index+= TokenTypes.COMMAND_START.jstlStyle().length;
                    handleCommand();

                } else if (CharScanner.matchChars(TokenTypes.COMMAND_START_END.jstlStyle(), index, this.charArray)) {



                    text = textToken(text);

                    commandBody.stop(index);
                    index++;
                    index = CharScanner.findChar('>', index, charArray);
                    break;

                }

            } else if (ch=='$') {

                char ch1 = charArray[index+1];
                if (ch1 == '{') {
                    if (CharScanner.matchChars(TokenTypes.EXPRESSION_START.jstlStyle(), index, this.charArray)) {


                        text = textToken(text);

                        index += TokenTypes.EXPRESSION_START.jstlStyle().length;
                        handleCurlyExpression();
                        text = Token.text(index, -1);
                        index--;


                    }
                } else {
                    text = textToken(text);

                    index++;
                    handleExpression("</");
                    text = Token.text(index, -1);
                    index--;
                }

            } else {
                if (text == null) {

                    text = Token.text(index, -1);
                }
            }

        }


        if (commandBody.stop() == -1) {
            commandBody.stop(index);
        }
        if (text!=null) {
            text.stop(charArray.length);
            this.tokenList.add( text );
        }



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

    @Override
    public List<Token> getTokenList() {
        return tokenList;
    }


    @Override
    public void displayTokens(String template) {

        for (Token token : this.getTokenList()) {
            puts("token", token, Str.slc(template, token.start(), token.stop()));
        }
    }


}

