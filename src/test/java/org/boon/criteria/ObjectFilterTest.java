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

package org.boon.criteria;

import static org.boon.Exceptions.die;
import static org.boon.Maps.*;
import static org.boon.Lists.*;
import static org.boon.criteria.ObjectFilter.*;
import static org.boon.criteria.ObjectFilter.matches;

import org.boon.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ObjectFilterTest {

    @Test
    public void test() {

        Map<String, Object> map = map("name", (Object) "Rick", "salary", 1);

        boolean ok = true;

        ok &= matches( map, eq("name", "Rick"), eq("salary", 1) ) || die();

        ok &= matches( map, eq("name", "Rick"), gt("salary", 0) ) || die();

        ok &= matches( map, eq("name", "Rick"), gte( "salary", 0 ) ) || die();

        ok &= !matches( map, eq("name", "Rick"), lt( "salary", 0 ) ) || die();

        ok &= !matches( map, not( eq("name", "Rick") ), lt( "salary", 1 ) ) || die();

    }



    @Test
    public void testList() {

        Map<String, Object> prototype = map("name", (Object) "Rick", "salary", 1);

        List<Map<String, Object>> list = list( copy( prototype ), copy( prototype ), copy( prototype ) );

        prototype.put( "salary", 100 );
        add( list,  copy( prototype ), copy( prototype ), copy( prototype ) );



        boolean ok = true;

        ok &= filter( list, eq("name", "Rick"), gte( "salary", 0 ) ).size() == 6 || die();

        ok &= filter( list, eq("name", "Rick"), gte( "salary", 50 ) ).size() == 3 || die();

    }



}
