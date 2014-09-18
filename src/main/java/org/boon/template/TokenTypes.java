package org.boon.template;

import org.boon.core.reflection.FastStringUtils;

/**
* Created by Richard on 9/17/14.
*/
public enum TokenTypes {
    COMMAND(""),
    COMMAND_BODY(""),
    EXPRESSION(""),
    COMMAND_START("<c:"),

    COMMAND_END_START(">"),
    COMMAND_START_END("</c:"),
    EXPRESSION_START("${"),
    EXPRESSION_END("}"),
    TEXT("");

    TokenTypes(String str) {
        this.chars = FastStringUtils.toCharArray(str);
    }

    char[] chars;
}
