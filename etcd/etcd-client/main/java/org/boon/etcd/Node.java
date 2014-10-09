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
