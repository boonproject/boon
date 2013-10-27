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

import org.boon.predicates.Function;

import java.util.List;

/**
 * Creates a simple lookup index (like a hash map).
 *
 * @param <KEY>  The key
 * @param <ITEM> The item
 */
public interface LookupIndex<KEY, ITEM> extends Bag<ITEM> {
    ITEM get(KEY key);

    void setKeyGetter(Function<ITEM, KEY> keyGetter);

    List<ITEM> getAll(KEY key);

    boolean deleteByKey(KEY key);

    boolean isPrimaryKeyOnly();

    void setInputKeyTransformer(Function<Object, KEY> func);

    void setBucketSize(int size);

    void init();


}
