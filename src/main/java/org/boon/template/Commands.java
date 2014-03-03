package org.boon.template;

import org.boon.core.Conversions;

/**
 * Created by Richard on 2/27/14.
 */
public enum Commands {
    EACH,
    IF,
    WITH,
    UNLESS,
    LOG, //MISSING
    LENGTH, //like if but checks to see if a list has a certain length NOT DONE
    FUNCTION, //calls a function if return type is not void, outputs to screen, hmmmm.... NOT DONE
    INCLUDE, //INCLUDE ANOTHER TEMPLATE has a name gets mapped in.. used like function if found in namespace WILL BE LIKE FREEMARKER style
    UNKNOWN;

    /*
    TODO Think about tiles inheritance structure

    templates can have JSON snippet definition associated with the template
    <definition name="myapp.homepage.body" template="/layouts/three_rows.jsp">
        <put-attribute name="one" value="/tiles/headlines.jsp" />
        <put-attribute name="two" value="/tiles/topics.jsp" />
        <put-attribute name="one" value="/tiles/comments.jsp" />
    </definition>

    Becomes
    {
        "definitions": [
                { "name":"myapp.homepage.body",
                "template":"three_row",
                 children : [
                    {name:one, template:"topics"}
                 ]},
                { "name":"myapp.backend.body",
                 "ex
                "template":"three_row",
                 children : [
                    {name:one, template:"topics"}
                 ]},

        ]
    }

     */

    /**
     *
     * @param value
     * @return
     */
    public static Commands command(String value) {
       return Conversions.toEnum(Commands.class, value.toUpperCase(), UNKNOWN);
    }
}
