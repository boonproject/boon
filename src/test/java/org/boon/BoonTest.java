package org.boon;

import junit.framework.TestCase;
import org.junit.Test;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/14/14.
 */
public class BoonTest extends TestCase {

    @Test
    public void test() {

        puts(Boon.toPrettyJson(Lists.list("abc", "123")));
    }
}
