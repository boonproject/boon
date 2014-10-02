package org.boon.slumberdb.stores.log;

import org.boon.slumberdb.service.protocol.requests.SetRequest;
import org.boon.core.Sys;
import org.boon.primitive.Chr;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 6/27/14.
 */
public class AsyncFileWriterDataStoreBenchMark {

    static ScheduledFuture<?> postReaderFuture;


    static ScheduledExecutorService scheduledExecutorService;


    static AsyncFileWriterDataStore postReceiver;


    static int hitsPerSecondSimulation = 1_000;
    static int howManyTimes = 20;
    static AtomicInteger times = new AtomicInteger();

    public static void main(String... args) throws Exception {

        File file = new File("/opt/org/userdatatracker/");

        file.mkdirs();
        run(args);

        System.exit(0);
    }

    public static void run(String... args) throws Exception {

        CollectorManager.MONITOR_INTERVAL_SECONDS = 1;

        if (args.length == 1) {
            hitsPerSecondSimulation = Integer.parseInt(args[0].replace("_", ""));
        } else if (args.length == 2) {
            hitsPerSecondSimulation = Integer.parseInt(args[0].replace("_", ""));
            howManyTimes = Integer.parseInt(args[1].replace("_", ""));

        }

        scheduledExecutorService = Executors.newScheduledThreadPool(6);
        postReceiver = new AsyncFileWriterDataStore();
        postReceiver.start();

        postReaderFuture =
                scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        readerSimulator(postReceiver);
                    }
                }, 0, 500, TimeUnit.MILLISECONDS);


        int timer = 0;
        while (times.get() < howManyTimes) {
            Sys.sleep(1_000);
            timer++;
            if (timer >= howManyTimes) {
                break;
            }
        }
        postReaderFuture.cancel(true);
        postReceiver.stop();


    }

    public static void readerSimulator(final AsyncFileWriterDataStore postReceiver) {

        puts("times", times, "of", howManyTimes, " requests per second of ", hitsPerSecondSimulation);
        times.incrementAndGet();

        String bigString = new String(Chr.multiply('A', 1_450_000));


        for (int index = 0; index < hitsPerSecondSimulation; index++) {

            postReceiver.set(new SetRequest(0, "client1", "FOOBAR", bigString + index));

            Thread.yield();


        }

    }
}