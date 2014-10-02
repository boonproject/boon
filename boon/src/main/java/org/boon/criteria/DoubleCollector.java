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

import org.boon.collections.DoubleList;
import org.boon.collections.FloatList;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;



/**
 * Collects primitive values from  Data repo results.
 * It avoids the creation of many wrapper objects.
 * It also provides a wicked fast version of List<PRIMITIVE> which can do
 * mean, median, standard deviation, sum, etc. over the returned results.
 */
public class DoubleCollector extends Selector {

    /**
     * Factory for int collector.
     * @param propertyName name of property to collect
     * @return new values
     */
    public static DoubleCollector intCollector(String propertyName) {
        return new DoubleCollector(propertyName);
    }

    private DoubleList list;

    public DoubleCollector(String fieldName) {
        super(fieldName);
    }

    @Override
    public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
        double value;
        if (path) {
            value = BeanUtils.idxDouble(item, this.name);
        } else {
            value = fields.get(name).getDouble(item);
        }
        list.add( value );
    }

    @Override
    public void handleStart(Collection<?> results) {
        list = new DoubleList(results.size());


    }

    @Override
    public void handleComplete(List<Map<String, Object>> rows) {

    }

    public DoubleList list() {
        return list;
    }
}

