package org.boon.core;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/* This timer is used for caches and such
 * It is a default timer.
 * It should never be used direct.
 * There should always be a fall back.
 * By default this will reset the time every 100 times it is called to the latest
 * System.nanoTime
 * At most 1 thread will incur the cost of calling nanoTime about every 100 invocations.
 *
 */
public class TimeKeeperBasic implements TimeKeeper {


    private final AtomicInteger callEveryNowAndThen = new AtomicInteger ();
    private final AtomicLong time = new AtomicLong ();
    private final int TIME_KEEPER_FREQUENCY = Integer.parseInt ( System.getProperty ( "org.boon.timekeeper.frequency", "100" ) );

    private final ReentrantLock lock = new ReentrantLock ();


    @Override
    public final long time () {

        long limit = callEveryNowAndThen.incrementAndGet ();
        if ( limit > TIME_KEEPER_FREQUENCY ) {
            callEveryNowAndThen.set ( 0 );

            boolean locked = lock.tryLock (); //make sure two or more threads are not calling nanoTime.
            if ( locked ) {
                try {
                    //I don't want more than one thread calling nanoTime
                    long time = System.nanoTime () / 1_000_000;
                    this.time.set ( time );

                } finally {
                    lock.unlock ();
                }
            }

        }
        return time.get ();
    }

}
