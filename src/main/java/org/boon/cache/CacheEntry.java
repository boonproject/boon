package org.boon.cache;

class CacheEntry<KEY, VALUE> implements Comparable<CacheEntry> {
    int readCount;
    VALUE value;
    KEY key;


    @Override
    public int compareTo ( CacheEntry other ) {
        if (readCount > other.readCount)  {
            return 1;
        }
        else if (readCount < other.readCount) {
            return -1;
        }
        else if (readCount == other.readCount) {
            return 0;
        }

        return 0;
    }


}
