package org.boon.slumberdb.stores.log;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.IO;
import org.boon.core.Sys;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.*;

/**
 * Created by Richard on 6/27/14.
 */
public class CollectorManager implements TimeAware {

    /**
     * Force flush if outputDataQueue is empty after this many mili-seconds.
     */
    private final static long FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS
            = Sys.sysProp(
            "NFL.USER_DATA_TRACKER_SERVER.FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS", 40);

    private final static boolean RECYCLE_BUFFER
            = Sys.sysProp(
            "NFL.USER_DATA_TRACKER_SERVER.RECYCLE_BUFFER", true);
    /**
     * Periodic force flush. We can turn off periodic flushing and allow the OS
     * to decide best time to sync to disk for speed.
     * (Not much difference in speed on OSX).
     */
    private final static boolean PERIODIC_FORCE_FLUSH
            = Sys.sysProp(
            "NFL.USER_DATA_TRACKER_SERVER.PERIODIC_FORCE_FLUSH", true);
    /**
     * Determines if we should see if the writer is busy before batching up a lot of
     * data. Turning this off helps with throughput at the expense of data safety.
     */
    private final static boolean TRANSFER_QUEUE_WRITER_WAITING_CHECK
            = Sys.sysProp(
            "NFL.USER_DATA_TRACKER_SERVER.TRANSFER_QUEUE_WRITER_WAITING_CHECK", true);
    /**
     * How often should we report status?
     */
    protected static int MONITOR_INTERVAL_SECONDS
            = Sys.sysProp(
            "NFL.USER_DATA_TRACKER_SERVER.MONITOR_INTERVAL_SECONDS", 600);
    /**
     * Byte Buffers that are done are put back on this outputDataQueue.
     */
    private final TransferQueue<ByteBuffer> recycleChannel = new LinkedTransferQueue<>();
    /**
     * Byte buffers that have been received from HTTP post or Event Bus, but not written to disk.
     */
    private final TransferQueue<ByteBuffer> inputChannel = new LinkedTransferQueue<>();
    /**
     * BatchFileWriter is used to write out batches of data at once.
     */
    private final BatchFileWriter writer = new BatchFileWriter();
    int monitorCount = 0;
    /**
     * Main thread scheduler.
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * How many times have we done a flush.
     */
    private AtomicLong numberOfFlushesTotal = new AtomicLong();
    /**
     * current time, which we get every 20 mili-seconds.
     */
    private AtomicLong time = new AtomicLong();
    /**
     * Request main thread to stop.
     */
    private AtomicBoolean stop = new AtomicBoolean();
    /**
     * Holds the writerFuture for shutdown.
     */
    private ScheduledFuture<?> writerFuture;
    /**
     * The last time we forced a sync to disk.
     */
    private long lastFlushTime = 0;
    /**
     * Request main thread to stop.
     */
    private AtomicBoolean recoverMode = new AtomicBoolean();
    /**
     * Health monitor Future.
     */
    private ScheduledFuture<?> monitorFuture;
    /**
     * 20 ms timer to reduce expensive calls to System.nanoSecond.
     */
    private ScheduledFuture<?> tickTock;
    private DataStoreConfig config;

    //Create with only six threads max.
    public CollectorManager() {

        scheduledExecutorService = Executors.newScheduledThreadPool(6);
    }

    public CollectorManager(ScheduledExecutorService service) {
        scheduledExecutorService = service;
    }

    /**
     * This gets called by the http post handler or event bus handler.
     */
    public final void offer(ByteBuffer batch) {
        batch.flip();

//        try {
//
//                inputChannel.transfer(batch);
//        } catch (InterruptedException ie) {
//            Thread.interrupted();
        inputChannel.offer(batch);
//        }

    }

    /**
     * This gets called from the http post handler  or event bus handler.
     *
     * @param size the size of the buffer that you would like.
     * @return
     */
    public final ByteBuffer allocateBuffer(int size) {

        if (RECYCLE_BUFFER) {
            ByteBuffer spentBuffer = recycleChannel.poll();
            if (spentBuffer == null) {
                spentBuffer = ByteBuffer.allocateDirect(size);
            }
            spentBuffer.clear();
            return spentBuffer;
        } else {
            return ByteBuffer.allocateDirect(size);
        }
    }

    /**
     * This checks to see if the output outputDataQueue is waiting.
     * We don't want the output outputDataQueue to wait, but we also
     * don't want it to thread sync too much either.
     */
    public final boolean isWriterWaiting() {
        // This call causes us to lose about 5% write throughput
        // it has the advantage of reducing loss of buffered input data
        // in the very rare occurrence of an outage.
        return TRANSFER_QUEUE_WRITER_WAITING_CHECK &&
                inputChannel.hasWaitingConsumer();
    }

    /**
     * This is the main processing loop for the batch writer processing.
     */
    private void processWrites() {

        while (true) {
            try {

                manageInputWriterChannel();


            } catch (InterruptedException e) {

                if (determineIfWeShouldExit()) {
                    break;
                }
            }

        }

    }

    /**
     * See if it is time to stop
     * We have been interrupted. Should we ignore it or break out of the loop.
     *
     * @return
     */
    private boolean determineIfWeShouldExit() {
        boolean shouldStop = stop.get();
        if (!shouldStop) {
            Thread.interrupted();
        } else {
            System.out.println("Exiting processing loop as requested");
            return true;
        }
        return false;
    }

    /**
     * Queue and batch writer main logic.
     * This is where the magic happens.
     *
     * @throws InterruptedException
     */
    private void manageInputWriterChannel() throws InterruptedException {


        try {

            ByteBuffer dataToWriteToFile;
            dataToWriteToFile = inputChannel.poll();  //no wait

            //If it is null, it means the inputChannel is empty and we need to flush.
            if (dataToWriteToFile == null) {
                queueEmptyMaybeFlush();
                dataToWriteToFile = inputChannel.poll();
            }


            //If it is still null, this means that we need to wait
            //for more items to show up in the inputChannel.
            if (dataToWriteToFile == null) {
                dataToWriteToFile = waitForNextDataToWrite();
            }

            //We have to check for null again because we could have been interrupted.
            if (dataToWriteToFile != null) {
                //Write it
                writer.nextBufferToWrite(dataToWriteToFile);
                //Then give it back
                if (RECYCLE_BUFFER) {
                    recycleChannel.offer(dataToWriteToFile);
                }

            }

        } catch (InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(System.err);
        }

    }

    /**
     * If we detect that the in-coming transfer outputDataQueue channel is empty
     * then it could be an excellent time to sync to disk.
     */
    private void queueEmptyMaybeFlush() {
        if (PERIODIC_FORCE_FLUSH) {
            long currentTime = time.get();
            /* Try not to flush more than once every x times per mili-seconds time period. */
            if ((currentTime - lastFlushTime) > FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS) {

                /* If the writer had things to flush, and we flushed then
                increment the number of flushes.
                 */
                if (writer.syncToDisk()) { //could take 100 ms to 1 second
                    this.numberOfFlushesTotal.incrementAndGet();
                }
                /* We update the flush time no matter what. */
                lastFlushTime = time.get();
            }
        }
    }

    /**
     * If we don't have any data, and we have flushed,
     * then we can wait on the outputDataQueue. There is no sense spin-locking.
     * The poll(time, timeunit) call will block until there is something to do
     * or until the timeout.
     *
     * @return the next byte buffer.
     * @throws InterruptedException
     */
    private ByteBuffer waitForNextDataToWrite() throws InterruptedException {
        ByteBuffer dataToWriteToFile;

        dataToWriteToFile =
                inputChannel.poll(FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS,
                        TimeUnit.MILLISECONDS);

        return dataToWriteToFile;
    }

    /**
     * Start up the health monitor.
     */
    private void startMonitor() {

        final ScheduledExecutorService monitor = Executors.newScheduledThreadPool(2,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setPriority(Thread.NORM_PRIORITY + 1);
                        return thread;
                    }
                }
        );


        monitorFuture = monitor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                monitor();
            }
        }, MONITOR_INTERVAL_SECONDS, MONITOR_INTERVAL_SECONDS, TimeUnit.SECONDS);


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("shutting down....");
                monitor();
            }
        }));


    }

    private void monitor() {

        if (recoverMode.get()) {
            check("outputDir", this.writer.outputDir());
        }

        //monitor runtime state... TBD
        //Health check of system.
        putl("Monitor:",
                sputs("    total bytes transferred:          ", String.format("%,d", this.writer.totalBytesTransferred())),
                sputs("    avg buffer size :       ", String.format("%,d", this.writer.averageBufferSize())),
                sputs("    numberOfFlushesTotal:             ", this.numberOfFlushesTotal.get()),
                sputs("    input inputChannel size:          ", this.inputChannel.size()),
                sputs("    recycle inputChannel size:        ", this.recycleChannel.size()),
                sputs("    bytes transferred for log:       ", String.format("%,d", this.writer.bytesTransferred())),
                sputs("    current log                      ", this.writer.fileName())
        );


        monitorCount++;

        if (monitorCount % 5 == 0) {
            String outputDir = this.writer.outputDir();

            check("output directory", outputDir);

        }


    }

    private void check(String description, String fileName) {

        try {
            Path path = IO.path(fileName);

            if (!Files.isWritable(path) || !Files.exists(path)) {
                writer.setError();
                Exception ex = new IOException();
                ex.fillInStackTrace();
                ex.printStackTrace(System.err);
                puts("Unable to write to ", fileName, "which is the", description);
                this.writer.diagnose();
                System.out.flush();
                System.err.flush();


                recoverMode.set(true);

            } else {
                recoverMode.set(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void stop() {

        stop.set(true);
        writerFuture.cancel(true);
        monitorFuture.cancel(true);
        tickTock.cancel(true);

    }


    /**
     * Starts up the batch writer.
     */
    public void start(final TimeAware receiver) {

        //This starts itself up again every 1/2 second if something really bad
        //happens like disk full. As soon as the problem gets corrected
        //then things start working again...happy day.    Only
        // one is running per instance of CollectionManagerImpl.
        writerFuture =
                scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        processWrites();
                    }
                }, 0, 500, TimeUnit.MILLISECONDS);

        startMonitor();


        tickTock =
                this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {

                        long time = System.nanoTime() / 1_000_000;
                        if (receiver != null) {
                            receiver.tick(time);
                        }
                        tick(time);

                    }
                }, 0, 20, TimeUnit.MILLISECONDS);


    }


    /**
     * nano time cost 100 nano seconds to call.
     * System.currentTimeMilis is not accurate (day light saving time shift)
     * We want the speed of System.currentTimeMilis and the accuracy of
     * System.nanoTime w/o the overhead so we call nano time
     * every 20 miliseconds. and store the results in an atomic.
     */
    @Override
    public final void tick(long time) {
        this.time.set(time);
        this.writer.tick(time);
    }


    public void init(DataStoreConfig config) {
        this.config = config;
        writer.init(config);
    }
}

