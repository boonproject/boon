/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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