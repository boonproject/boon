package org.boon.template.support;

import org.boon.core.reflection.FastStringUtils;

/**
 * Created by Richard on 9/17/14.
 */
public enum TokenTypes {
    COMMAND(""),
    COMMAND_BODY(""),
    EXPRESSION(""),
    COMMAND_START("<c:","{{#"),

    COMMAND_END_START(">","{{/"),
    COMMAND_START_TAG_END("/>"),

    COMMAND_START_END("</c:"),
    EXPRESSION_START("${", "{{"),
    EXPRESSION_END("}", "}}"),
    TEXT("");
    private char[] JSTL_STYLE;

    private char[] HANDLE_BAR_STYLE;

    TokenTypes(String jstl) {
        this.JSTL_STYLE = jstl.toCharArray();
    }


    TokenTypes(String jstl, String handlebar) {
        this.JSTL_STYLE = jstl.toCharArray();
        this.HANDLE_BAR_STYLE = handlebar.toCharArray();
    }



    TokenTypes() {

        this.JSTL_STYLE = "".toCharArray();
        this.HANDLE_BAR_STYLE = "".toCharArray();


    }

    public char[] jstlStyle() {
        return JSTL_STYLE;
    }

    public char[] handleBarStyle() {
        return HANDLE_BAR_STYLE;
    }
}
