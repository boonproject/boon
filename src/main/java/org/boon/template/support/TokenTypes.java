package org.boon.template.support;

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
    COMMAND_START_TAG_END("/>"),

    COMMAND_START_END("</c:"),
    EXPRESSION_START("${"),
    EXPRESSION_END("}"),
    TEXT("");
    private char[] JSTL_STYLE;

    TokenTypes(String str) {
        this.JSTL_STYLE = FastStringUtils.toCharArray(str);
    }

    public char[] jstlStyle() {
        return JSTL_STYLE;
    }
}
