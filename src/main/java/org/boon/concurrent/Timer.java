package org.boon.concurrent;

import org.boon.Logger;
import org.boon.core.Sys;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.boon.Boon.configurableLogger;


public class Timer {




    private Logger logger = configurableLogger(Timer.class);

    private AtomicLong time = new AtomicLong(System.nanoTime() / 1_000_000);


    ScheduledExecutorService monitor;

    ScheduledFuture<?> future;

    private static AtomicReference<Timer> timeHolder = new AtomicReference<>();

    public static Timer timer() {
        if (timeHolder.get()==null) {

            if (timeHolder.compareAndSet(timeHolder.get(), new Timer())) {
                timeHolder.get().start();
            }

        }
        return timeHolder.get();
    }


    public void stop() {
        future.cancel(true);
        monitor.shutdownNow();
        monitor = null;
    }


    private void start() {



        if (monitor == null)
            monitor = Executors.newScheduledThreadPool(1,
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable runnable) {
                            Thread thread = new Thread(runnable);
                            thread.setPriority(Thread.MAX_PRIORITY);
                            thread.setName("Timer OutputQueue Manager");
                            return thread;
                        }
                    }
            );


        future = monitor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    manageTimer();
                } catch (Exception ex) {
                    logger.error(ex, "can't manage timeHolder");
                }
            }
        }, 50, 50, TimeUnit.MILLISECONDS);




    }

    private void manageTimer() {

        int count=0;
        while (true) {

            count++;
            Sys.sleep(5);
            time.addAndGet(5);

            if (count > 100) {
                time.set(System.nanoTime() / 1_000_000);
                count = 0;
            }

        }
    }


    public long time() {
        return time.get();
    }


    public long now() {
        return time.get();
    }


}