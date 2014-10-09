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

package org.boon.etcd;

import org.boon.Boon;

import java.util.List;

/**
 * Created by rhightower on 10/8/14.
 */
public class Node {
    private final String key;
    private final String value;
    private final long createdIndex;
    private final long modifiedIndex;
    private final long ttl;
    private final List<Node> nodes;

    public Node(final String key,
         final String value,
         final long createdIndex,
         final long modifiedIndex,
         final long ttl,
         final List<Node> nodes) {
        this.key = key;
        this.value = value;
        this.createdIndex = createdIndex;
        this.modifiedIndex = modifiedIndex;
        this.ttl = ttl;
        this.nodes = nodes;
    }

    public String key() {
        return key;
    }

    public long getCreatedIndex() {
        return createdIndex;
    }

    public long getModifiedIndex() {
        return modifiedIndex;
    }

    public long getTtl() {
        return ttl;
    }

    public String getValue() {
        return value;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return Boon.toPrettyJson(this);
    }
}
