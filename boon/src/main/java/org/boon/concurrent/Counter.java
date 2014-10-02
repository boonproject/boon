package org.boon.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Richard on 7/2/14.
 */
public class Counter {


    private AtomicLong count = new AtomicLong();
    long _count = 0;


    public void increment() {
        long __count = _count;

        if (__count >= 100) {
            count.addAndGet(__count);
            __count = 0;
        }

        __count++;

        _count = __count;
    }

    public long incrementAndGet() {
        long __count = _count;

        if (__count >= 100) {
            count.addAndGet(__count);
            __count = 0;
        }

        __count++;

        _count = __count;
        return _count;
    }

    public void add(int size) {
        long __count = _count;



        __count += size;

        if (__count >= 1000) {
            count.addAndGet(__count);
            __count = 0;
        }

        _count = __count;

    }


    public long addAndGet(int size) {
        long __count = _count;



        __count += size;

        if (__count >= 1000) {
            count.addAndGet(__count);
            __count = 0;
        }

        _count = __count;

        return _count;

    }

    public synchronized void reset() {
        _count=0;
        count.set(0);
    }

    public synchronized long get() {


        count.addAndGet(_count);
        _count=0;
        return count.get();
    }


    @Override
    public synchronized String toString() {
        return "" + get();
    }
}
