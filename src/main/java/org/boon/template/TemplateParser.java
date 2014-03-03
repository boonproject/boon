package org.boon.template;

/**
 * @author Rick Hightower
 *
 * This supports handlebar templates, freemarker-like template and JSTL-like templates.
 *
 * <pre>
 *     {{#if foo}}
 *
 *          {{foo}} //Escaped
 *
 *          {{{foo}}} //Unescaped
 *     {{/if foo}}
 *
 *     {{#with foo}}
 *
 *           {{foo}}   //Escaped
 *           {{{foo}}} /Unescaped
 *
 *     {{/with}}
 *
 *     {{#each fruits}}
 *          {{this}}
 *     {{/each}}
 * </pre>
 *
 * see http://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm
 * see http://www.tutorialspoint.com/jsp/jstl_core_foreach_tag.htm
 * see http://docs.oracle.com/javaee/5/jstl/1.1/docs/tlddocs/c/forEach.html
 * <pre>
 *      <c:if test="foo">
 *
 *          ${fn:escapeXml(foo)} //Escaped
 *          ${foo} //Unescaped
 *
 *     </c:if>
 *
 *     <c:with item="foo">
 *
 *          ${fn:escapeXml(foo)} //Escaped
 *          ${foo} //Unescaped
 *
 *     </c:with>
 *
 *     <c:forEach items="fruits">
 *
 *          ${item}
 *
 *     </c:forEach>
 *
 *     <c:forEach var="window" items="${windows}">
 *                 <c:out value="${window}"/>
 *     </c:forEach>
 *
 * </pre>
 *
 * Freemarker like
 * <pre>
 *      <#if foo>
 *
 *          ${fn:escapeXml(foo)} //Escaped
 *          ${foo} //Unescaped
 *
 *     </#if>
 *
 *     <#with foo>
 *
 *          ${fn:escapeXml(foo)} //Escaped
 *          ${foo} //Unescaped
 *
 *     </#with>
 *
 *     <#list fruits as fruit>
 *
 *          ${fruit}
 *
 *     </#list>
 *
 *
 *     <#list fruits>
 *
 *          ${item}
 *
 *     </#list>
 *
 *
 * </pre>
 *
 * Freemarker has an expression language for if and such.
 * There are no plans to add a full expression langauge to this template.
 * The same goes for JSTL-like. Simple boolean expressions and calls to functions but no logic.
 *
 * Velocity-like
 * <pre>
 *          <ul>
 *            #foreach( $product in $allProducts )
 *                 <li>$product</li>
 *           #end
 *          </ul>
 *
 *          #if( $foo )
 *              <strong>Velocity!</strong>
 *          #end
 * </pre>
 *
 * Velocity and Freemarker have if and else, else if.
 * Handlebars has if / else. Handlebars also has unless.
 * JSTL just has if.
 *
 * Velocity, JSTL and Freemarker have full expression languages.
 * Handlebars does not.
 *
 * The plan (currently) is for this templating to have no expressions.
 * You have property paths that are either true or false only, and you can call functions
 * that return true or false. Or rather true-y or falsey (null = false, empty list = false, etc.).
 * Python style true or false which is more ore less what handlebars/mustache do.
 * So this will be a stupid template, i.e., no logic other than true/false.
 *
 * Start of block characters
 * <pre>
 *     Freemarker-like
 *
 *     <#if  = '<#'
 *
 *     Velocity-like
 *
 *     #if = '#'
 *
 *     JSTL-like
 *
 *     <c:if = '<c:if'
 *
 *     Mustache
 *     {{#if = '{{'
 *
 * </pre>
 *
 *
 * END of start block character
 * <pre>
 *     Freemarker-like
 *
 *     <#if blah>   = '>'
 *              ^
 *              |
 *
 *     Velocity-like
 *
 *     #if<SPACE>  = ' '
 *           ^
 *           |
 *
 *     JSTL-like
 *
 *     <c:if test="foo">  = '>'
 *                     ^
 *                     |
 *
 *     Mustache
 *     {{#if    }  = '>'
 *              ^
 *              |
 *
 *
 * </pre>
 *
 * This parser merely delivers up the stuff between start and stop.
 * Then there are handlers to further divide up the strings as the syntax varies a lot of how
 * expression of {{if}}, {{each}}, and {{with}} are handled.
 *
 * Start of expression characters
 * <pre>
 *
 *     Freemarker-like
 *
 *     ${
 *
 *     Velocity-like
 *
 *     $
 *
 *     JSTL-like
 *
 *     ${
 *
 *     Mustache/Handlebar
 *     {{
 *
 *
 *     Mustache/Handlebar
 *     {{{
 *
 * </pre>
 *
 *
 * End of expression characters
 * <pre>
 *
 *     Freemarker-like
 *
 *     }
 *
 *     Velocity-like
 *
 *     <SPACE>
 *
 *     JSTL-like
 *
 *     }
 *
 *     Mustache/Handlebar
 *     }}
 *
 *
 *     Mustache/Handlebar (NOT HANDLED BY PARSER, HANDLED BY LOOKUP)
 *     }}}
 *
 * </pre>
 *
 * Rather than treat mustache as having two expressions (tried that), I will treat it as one.
 * Then the lookup mechanism will handle the other case.
 *
 * Velocity allows $vice and ${vice}maniac forms. Rather than handling two forms.
 * All parsers treat $vice as an expression. So that $foo is always an expression.
 * This would mean for mustache you would have $vice or {{vice}}maniac.
 * The caveat being that $expression handlng is a flag so you can turn it off for handlebars by default.
 *
 * One of the goals is to have the ability to write scripts 100% compatible with Handlebars.
 *
 * There is not a similar goal with Velocity, JSTL, and Freemarker.
 * You can only ever write scripts that are similar to JSTL not true JSTL scripts.
 * You can only ever write scripts that are similar to Velocity not true Velocity scripts.
 * You can only ever write scripts that are similar to Freemarker not true Freemarker scripts.
 * The goal is the ability to write scripts that are 100% Handlebar compatible and can be rendered by browser or Java.
 *
 * The goal is for BoonTemplate is to be a superset of Handlebars and always only a subset of JSTL,
 * Velocity and Freemarker.
 *
 * Order of importance:
 * <ol>
 *     <li><Handlebar compliance</li>,
 *     <li>JSTL style support</li>,
 *     <li>Freemarker style support</li>
 *     <li> and then Velocity style support</li>
 * </ol>
 *
 * Handlebars has the advantage of the expression, and commands start with the same character, which
 * will help with parsing.
 *
 * <code><pre>
 *
 *     if (sameStart) {
 *         look for start char of command and expression
 *     } else {
 *         look for start char of expression or command  #NOTE this takes longer
 *     }
 * </pre></code>
 *
 * Also we need to support comments.
 *
 * <pre>
 *     Velocity like multiline
 *     ##.
 *     .##
 *
 *     Freemarker like
 *     <#--
 *     -->
 *
 *     Handlebar like
 *     {{!
 *     }}
 *
 *     JSTL like (I made this one up JSTL does not have comments JSP does. :)
 *     <c:comment
 *     >
 * </pre>
 *
 * Input:
 * char[]
 *
 * Output array of tokens in IndexOverlay style
 * <pre>
 *
 * Array item: TemplateToken (Block or Text or Expression or Comment, startIndex, stopIndex)
 *
 * </pre>
 *
 * There is no logic in this parser. Just an array of token positions.
 * It is up to BoonTemplate on how to interpret those tokens.
 */
public class TemplateParser {


}
