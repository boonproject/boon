package org.boon;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/14/14.
 */
public class BoonTest extends TestCase {

    @Test
    public void test() {

        final List<String> abc = Lists.list("abc", "123");
        puts(Boon.toPrettyJson(abc));

        final String json = Boon.toPrettyJson(abc);

        final Object o = Boon.fromJson(json);

        Boon.equalsOrDie("lists are equal", o, abc);

    }
}
