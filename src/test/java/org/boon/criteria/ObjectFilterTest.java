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

import static org.boon.Boon.*;
import static org.boon.Exceptions.die;
import static org.boon.Maps.*;
import static org.boon.Lists.*;
import static org.boon.criteria.ObjectFilter.*;
import static org.boon.criteria.ObjectFilter.matches;

import org.boon.Lists;
import org.boon.core.Type;
import org.boon.core.reflection.Invoker;
import org.boon.criteria.internal.Criteria;
import org.boon.criteria.internal.Group;
import org.boon.criteria.internal.Operator;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ObjectFilterTest {


    @Test
    public void testCreateCriteriaFromJson() {

        List<Integer> list = Lists.list(1, 2);
        Criterion foo = (Criterion) ObjectFilter.createCriteria("foo", Operator.EQUAL, Type.INT, list);

        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("foo", foo.getName());

        foo = (Criterion) Invoker.invokeFromList(ObjectFilter.class, "createCriteria", Lists.list("foo", Operator.EQUAL, Type.INT, list));

        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));

        List<Operator> operators = Lists.list(Operator.LESS_THAN, Operator.EQUAL, Operator.NOT_EQUAL,
                Operator.GREATER_THAN, Operator.BETWEEN, Operator.IN,
                Operator.NOT_IN, Operator.CONTAINS, Operator.ENDS_WITH, Operator.STARTS_WITH,
                Operator.NOT_CONTAINS);


        List<Type> types = Lists.list(Type.INT, Type.DOUBLE, Type.OBJECT,
                Type.FLOAT, Type.SHORT, Type.BYTE,
                Type.LONG);


        for (Operator op : operators) {
            for (Type type : types) {
                List<Object> args = Lists.list("foo", op, type, list);

                foo = (Criterion) Invoker.invokeFromList(ObjectFilter.class,
                        "createCriteria", args);

                puts ("Now working with", op, type);

                equalsOrDie(op, foo.getOperator());


                equalsOrDie("foo", foo.getName());


            }
        }

        String json = "[\"foo\", \"EQUAL\", \"INT\", [1,2,3]]";
        Object object = fromJson(json);

        puts (object);

        foo = (Criterion) Invoker.invokeFromObject(ObjectFilter.class,
                "createCriteria", object );



        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("foo", foo.getName());

        foo = (Criterion) ObjectFilter.createCriteriaFromClass("foo",
                org.boon.criteria.MyClass.class, Operator.EQUAL,list);



        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("foo", foo.getName());


        foo = (Criterion) ObjectFilter.createCriteriaFromClass("bar.baz",
                org.boon.criteria.MyClass.class, Operator.EQUAL, list);



        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("bar.baz", foo.getName());



        json = "[\"bar.baz\", \"org.boon.criteria.MyClass\", \"EQUAL\",  [1,2,3]]";


        object = fromJson(json);

        puts(object);

        foo = (Criterion) Invoker.invokeFromObject(ObjectFilter.class,
                "createCriteriaFromClass", object );



        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("bar.baz", foo.getName());


        json = "[\"bar.baz\", \"org.boon.criteria.MyClass\", \"EQUAL\",  [1,2,3]]";


        List listJson = Lists.list("an example", "org.boon.criteria.MyClass", "AND",
                Lists.list(
                        Lists.list("bar.baz",
                         "EQUAL", Lists.list(1,2,3)
                        ),
                        Lists.list("bar.baz",
                                "EQUAL", Lists.list(1,2,3)
                        )
                )

        );

        json = toJson(listJson);
        puts(json);


        object = fromJson(json);

        Group and = (Group) Invoker.invokeFromObject(ObjectFilter.class,
                "createCriteriaFromClass", object );

        puts(foo);

        foo = (Criterion) and.getExpressions().get(0);


        equalsOrDie(Operator.EQUAL, foo.getOperator());

        equalsOrDie(foo.values[0], list.get(0));


        equalsOrDie("bar.baz", foo.getName());


    }
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
