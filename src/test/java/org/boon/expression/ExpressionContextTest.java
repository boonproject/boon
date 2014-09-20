package org.boon.expression;

import org.boon.Lists;
import org.boon.Maps;
import org.junit.Before;
import org.junit.Test;

import static org.boon.Boon.equalsOrDie;

/**
 * Created by Richard on 9/19/14.
 */
public class ExpressionContextTest {


    BoonExpressionContext context;

    @Before
    public void setup() {


        BoonExpressionContext child = new BoonExpressionContext(
                Maps.map(
                        "name", "child-application",
                        "session", Maps.map(
                                "name", "child-session",
                                "request", Maps.map("name", "child-request",
                                        "fruit", Lists.list("peaches", "strawberries")
                                )
                        ),
                        "SOME KEY", "SOME VALUE"
                )
        );
        context = new BoonExpressionContext(
                child,
                Maps.map("name", "parent-application",
                        "session", Maps.map(
                                "name", "parent-session",
                                "request", Maps.map("name", "request-parent",
                                        "fruit", Lists.list("apple", "oranges")
                                )
                        )
                )

        );


    }

    @Test public void nestedFunctions() {
        equalsOrDie("child-request", context.lookup("$fn:lower($fn:upper(session.request.name))"));

    }


    @Test public void searchParent() {

        equalsOrDie("parent-application", context.lookup("../name"));
    }

    @Test public void callFunctionOnParent() {
        equalsOrDie("request-parent", context.lookup("..fn:lower(fn:upper(session.request.name))"));

    }

    @Test
    public void main() {

        equalsOrDie("child-application", context.lookup("name") );

        equalsOrDie("parent-application", context.lookup("../name"));
        equalsOrDie("parent-application", context.lookup("..name"));

        equalsOrDie("child-session", context.lookup("session.name"));
        equalsOrDie("parent-session", context.lookup("../session.name"));

        equalsOrDie("child-request", context.lookup("session/request/name"));
        equalsOrDie("request-parent", context.lookup("..session/request/name"));


        equalsOrDie("SOME VALUE", context.lookup("SOME KEY"));

        equalsOrDie("CHILD-REQUEST", context.lookup("$fn:upper(session.request.name)"));
        equalsOrDie("child-request", context.lookup("$fn:lower($fn:upper(session.request.name))"));

        equalsOrDie("CHILD-REQUEST", context.lookup("fn:upper(session.request.name)"));
        equalsOrDie("child-request", context.lookup("fn:lower(fn:upper(session.request.name))"));

        equalsOrDie("CHILD-REQUEST", context.lookup("{{fn:upper(session.request.name)}}"));

        equalsOrDie("child-request", context.lookup("fn:lower(fn:upper(session/request/name))"));


        equalsOrDie("request-parent", context.lookup("..fn:lower(fn:upper(session.request.name))"));


        equalsOrDie("strawberries", context.lookup("session.request.fruit.1"));


        equalsOrDie("oranges", context.lookup("../session.request.fruit.1"));

//        puts(context.findProperty("session.request.name"));
//
//
//        puts(context.findProperty("session.request.fruit"));
//
//
//
//
//        puts(context.findProperty("session/request/fruit/1"));
//
//
//
//
//        puts(context.lookup("this.name"));
//
//
//        puts(context.lookup(".name"));
    }
}
