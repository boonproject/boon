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

import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.sort.Sort;
import org.boon.sort.SortType;
import org.boon.tests.model.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


import static org.junit.Assert.assertEquals;

public class SortTest {

    List<Employee> list;

    @Before
    public void setUp() throws Exception {
        list = Lists.list(
                Employee.employee( "zzz", "LastA", "120", "5.29.1970:00:00:01", 100 ),
                Employee.employee( "zbbb", "bbb", "124", "5.29.1960:00:00:00", 200 ),
                Employee.employee( "zbbb", "aaa", "123", "5.29.1970:00:00:01", 100 ),
                Employee.employee( "bababa", "LastB", "125", "5.29.1960:00:00:00", 200 ),
                Employee.employee( "BAbaba", "LastB", "126", "5.29.1960:00:00:00", 200 )

        );


    }

    @Test
    public void simpleSort() throws Exception {
        Sort sort = new Sort( "firstName", SortType.ASCENDING );
        sort.sort( list );
        List<String> firstNames = BeanUtils.idxList( Typ.string, list, "firstName" );
        assertEquals( "bababa", firstNames.get( 0 ) );
        assertEquals( "BAbaba", firstNames.get( 1 ) );
        assertEquals( "zbbb", firstNames.get( 2 ) );
        assertEquals( "zbbb", firstNames.get( 3 ) );
        assertEquals( "zzz", firstNames.get( 4 ) );

    }

    @Test
    public void compoundSort() throws Exception {
        Sort sort = new Sort( "firstName", SortType.ASCENDING );
        sort.then( "lastName" );
        sort.sort( list );
        List<String> firstNames = BeanUtils.idxList( Typ.string, list, "firstName" );
        List<String> lastNames = BeanUtils.idxList( Typ.string, list, "lastName" );

        assertEquals( "bababa", firstNames.get( 0 ) );
        assertEquals( "BAbaba", firstNames.get( 1 ) );
        assertEquals( "zbbb", firstNames.get( 2 ) );
        assertEquals( "zbbb", firstNames.get( 3 ) );
        assertEquals( "zzz", firstNames.get( 4 ) );

    }

}
