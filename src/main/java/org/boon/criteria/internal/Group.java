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

package org.boon.criteria.internal;

import org.boon.Lists;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.ObjectFilter;
import org.boon.primitive.CharBuf;

import java.util.*;

import static org.boon.Lists.atIndex;

public abstract class Group extends Criteria {

    protected List<Criteria> expressions;


    private Grouping grouping = Grouping.AND;




    public Group( Grouping grouping, Criteria... expressions ) {
        this.grouping = grouping;
        this.expressions = Lists.list(expressions);

    }



    public Group( Grouping grouping, Class<?> cls, List<?> list ) {
        this.grouping = grouping;

        ArrayList<Criteria> criteriaArrayList = new ArrayList();
        List<List<?>> lists = (List<List<?>>)list;
        for (List args : lists) {
            args = new ArrayList(args);
            args.add(1, cls);

            Object o = atIndex(args, -1);
            if (! (o instanceof List) ) {
                atIndex(args, -1, Collections.singletonList(o));
            }
            Criteria criteria = (Criteria) Invoker.invokeFromObject(ObjectFilter.class, "createCriteriaFromClass", args);
            criteriaArrayList.add(criteria);
        }
        this.expressions = criteriaArrayList;

    }


    public Grouping getGrouping() {
        return grouping;
    }


    public List<Criteria> getExpressions() {
        return expressions;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (expressions != null ? !expressions.equals(group.expressions) : group.expressions != null) return false;
        if (grouping != group.grouping) return false;

        return true;
    }




    public static class And extends Group {

        public And( Criteria... expressions ) {
            super( Grouping.AND, expressions );
        }

        public And( Class<?> cls,  List<?> list ) {
            super( Grouping.AND, cls,  list );
        }

        @Override
        public boolean test(  Object owner ) {
            for ( Criteria c : expressions ) {
                if ( !c.test( owner ) ) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Or extends Group {

        public Or( Criteria... expressions ) {
            super( Grouping.OR, expressions );
        }

        public Or( Class<?> cls,  List<?> list ) {
            super( Grouping.OR, cls,  list );
        }


        @Override
        public boolean test( Object owner ) {
            for ( Criteria c : expressions ) {
                if ( c.test( owner ) ) {
                    return true;
                }
            }
            return false;
        }
    }

}
