/**
 * Copyright 2013 Rick Hightower
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boon.datarepo;


import org.boon.criteria.Criteria;
import org.boon.criteria.Update;

import java.util.List;

/**
 * Repo, A Repo is like a DAO object or a Repository object.
 *
 * @param <KEY>
 * @param <ITEM> //SearchableCollection<KEY, ITEM>,
 */
public interface Repo<KEY, ITEM> extends ObjectEditor<KEY, ITEM>, SearchableCollection<KEY, ITEM> {


    void updateByFilter(String property, Object value, Criteria... expressions);

    void updateByFilterUsingValue(String property, String value, Criteria... expressions);

    void updateByFilter(String property, int value, Criteria... expressions);

    void updateByFilter(String property, long value, Criteria... expressions);

    void updateByFilter(String property, char value, Criteria... expressions);

    void updateByFilter(String property, short value, Criteria... expressions);

    void updateByFilter(String property, byte value, Criteria... expressions);

    void updateByFilter(String property, float value, Criteria... expressions);

    void updateByFilter(String property, double value, Criteria... expressions);

    void updateByFilter(List<Update> values, Criteria... expressions);


}
