package org.boon.collections;

import junit.framework.TestCase;
import org.junit.Test;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 2/18/14.
 */
public class IntListTest extends TestCase {

    @Test
    public void test() {
        IntList list = new IntList();
        list.addInt(1);
        list.addInt(2);
        list.addInt(3);

        if (list.getInt(0) != 1) die();


        if (list.getInt(1) != 2) die();


        if (list.getInt(2) != 3) die();


        if (list.size() != 3) die();


        if (list.sum() != 6) die();


        list.addArray(2, 3, 5);


        if (list.sum() != 16) die();

        if (list.size() != 6) die();


    }


}
