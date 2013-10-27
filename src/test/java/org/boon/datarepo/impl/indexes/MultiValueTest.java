package org.boon.datarepo.impl.indexes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.boon.Lists.list;

public class MultiValueTest {

    private MultiValue mv;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

        MultiValue mv = MultiValue.add(null, "Rick", 3);
        Assert.assertEquals("Rick", mv.getValue());

    }

    @Test
    public void testMany() throws Exception {

        List<String> strings = list("Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13");


        for (String item : strings) {
            mv = MultiValue.add(mv, item, 3);

        }

        Assert.assertEquals("Rick", mv.getValue());
        Assert.assertEquals(13, mv.size());


        for (Object item : mv.getValues()) {
            junit.framework.Assert.assertNotNull(item);
        }

    }

    @Test
    public void testManyThenAddTo() throws Exception {

        List<String> strings = list("Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13");


        for (String item : strings) {
            mv = MultiValue.add(mv, item, 3);

        }

        List<String> results = list("Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13");

        mv.addTo(results);

        for (Object item : mv.getValues()) {
            junit.framework.Assert.assertNotNull(item);
        }


    }

    @Test
    public void testManyUseAddTo() throws Exception {

        MultiValue mv = MultiValue.add(null, "Rick", 3);

        Assert.assertEquals("Rick", mv.getValue());

        List<String> results = list("Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13");

        mv.addTo(results);

//        results.forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });
//
//        mv.getValues().forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });
//
//        results.forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });

    }

}
