package org.boon.tests;

import org.junit.Before;
import org.junit.Test;

public class RepoBuilderTest {
    RepoDefaultTest test;


    @Before
    public void setup() {
        test = new RepoDefaultTest();

    }

    public void runAll() throws Exception {

        test.testAdd();
        test.testGet();

        test.testEasyFilter();
        test.testHarderFilter();
        test.testModify();
        test.testFilterLogicalOperators2();
        test.testUpdateByKeyUsingValues();
        test.testIndexedLookup();

    }


    @Test
    public void testCreateFromBuilderNestedIndex() throws Exception {

        test.repo = TestHelper.createFromBuilderNestedIndex();
        runAll();

    }

    @Test
    public void testWithTransformAndCollation() throws Exception {

        test.repo = TestHelper.createFromBuilderWithTransformAndCollation();
        runAll();

    }

    @Test
    public void testNormal() throws Exception {

        test.repo = TestHelper.createFromBuilder();
        runAll();

    }

    @Test
    public void testNoIndexes() throws Exception {

        test.repo = TestHelper.createWithNoIndexes();
        runAll();

    }

    @Test
    public void testNormalLogAndClone() throws Exception {

        test.repo = TestHelper.createFromBuilderLogAndClone();
        runAll();

    }

    @Test
    public void testNoReflect() throws Exception {

        test.repo = TestHelper.createBuilderNoReflection();
        runAll();

    }

    @Test
    public void testWithProps() throws Exception {

        test.repo = TestHelper.createFromBuilderUsingPropertyAccess();
        runAll();

    }


    @Test
    public void testWithEvents() throws Exception {

        test.repo = TestHelper.createFromBuilderEvents();
        runAll();

    }

}