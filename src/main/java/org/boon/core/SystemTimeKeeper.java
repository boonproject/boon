package org.boon.core;

import org.boon.core.timer.TimeKeeper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by rick on 12/20/13.
 */
public class SystemTimeKeeper implements TimeKeeper {

    private static final AtomicLong time = new AtomicLong();
    private static final AtomicBoolean started = new AtomicBoolean();

    private static ScheduledExecutorService executorService;

    @Override
    public long time() {
        return time.get();

    }

    public static void start() {

        if ( !started.get() ) {
            executorService = Executors.newSingleThreadScheduledExecutor();

            executorService.scheduleAtFixedRate( new Runnable() {
                @Override
                public void run() {
                    time.set( System.nanoTime() / 1_000_000 );
                }
            }, 5, 5, TimeUnit.MILLISECONDS );
            started.set( true );
        }
    }
}
