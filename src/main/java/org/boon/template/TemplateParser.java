package org.boon.template;

import org.boon.template.support.Token;

import java.util.List;

/**
 * Created by Richard on 9/18/14.
 */
public interface TemplateParser {
    void parse(String string);

    List<Token> getTokenList();

    void displayTokens(String template);

}
