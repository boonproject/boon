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

import java.util.Collection;


/**
 * Decorates a collection.
 *
 * Adds SearchableCollection to a collection.
 *
 * <p>
 *
 * </p>
 * @see org.boon.datarepo.Collections.QList
 * @see org.boon.datarepo.Collections.QSet
 * @see Collections
 */
public interface CollectionDecorator {
    SearchableCollection searchCollection();

    Collection collection();
}
