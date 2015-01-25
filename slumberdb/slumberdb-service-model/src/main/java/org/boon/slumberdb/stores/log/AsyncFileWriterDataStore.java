package org.boon.slumberdb.stores.log;

import org.boon.Logger;
import org.boon.core.Dates;
import org.boon.core.Sys;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.stores.DataStore;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.toJson;


/**
 * Created by Richard on 6/27/14.
 */
public class AsyncFileWriterDataStore implements DataStore, TimeAware {


    /**
     * How big our buffer size is, this is the max size of each write.
     */
    public final static int BUFFER_OUT_SIZE_MAX
            = Sys.sysProp("NFL.USER_DATA_TRACKER.BUFFER_OUT_SIZE_MAX", 10_000_000);
    public final static int BUFFER_MIN
            = Sys.sysProp("NFL.USER_DATA_TRACKER.BUFFER_OUT_SIZE_MIN", 2_000_000);
    /**
     * outputDataQueue manager for batch writer.
     */
    private final CollectorManager collector;
    /**
     * The current UTC time within 20 mili-seconds accuracy.
     */
    final private AtomicLong approxTime = new AtomicLong(Dates.utcNow());
    protected LinkedTransferQueue<DataStoreRequest> queue = new LinkedTransferQueue<>();
    protected List<ScheduledExecutorService> scheduledExecutorServices = new ArrayList<>();
    protected List<Future<?>> futures = new ArrayList<>();
    long lastForceFlush = 0;
    private Logger logger = configurableLogger(this.getClass());
    private AtomicLong time = new AtomicLong(Dates.utcNow());
    private DataStoreConfig config;
    /**
     * Index / sequence of the line we just wrote to the JSON log.
     */
    private long index = 0;

    public AsyncFileWriterDataStore() {
        this.collector = new CollectorManager();
    }

    public void start() {
        collector.start(this);


        startWorker("AsyncFileWriterDataStore ", new Runnable() {
            @Override
            public void run() {

                try {
                    processSets();

                } catch (Exception ex) {
                    logger.error(ex, "AsyncFileWriterDataStore failed");
                }
            }
        });


    }

    private void processSets() {


        /** Current output buffer. */
        ByteBuffer buffer = collector.allocateBuffer(BUFFER_OUT_SIZE_MAX + BUFFER_MIN);

        DataStoreRequest op = null;


        DataStoreRequest data = null;

        BatchSetRequest dataMap = null;

        while (true) {

            try {
                op = queue.poll(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }


            if (op instanceof RemoveRequest) {
                data = op;
                dataMap = null;
            } else if (op instanceof SetRequest) {
                data = op;
                dataMap = null;
            } else if (op instanceof BatchSetRequest) {
                dataMap = (BatchSetRequest) op;
                data = null;
            }

            while (op != null) {


                if (data != null) {
                    putDataInBuffer(buffer, data.messageId(), data.clientId(), data.key(), data.payload());
                } else if (dataMap != null) {


                    long messageId = dataMap.messageId();
                    String clientId = dataMap.clientId();
                    final Map<String, String> payload = dataMap.payloadAsMap();
                    for (Map.Entry<String, String> entry : payload.entrySet()) {

                        putDataInBuffer(buffer, messageId, clientId, entry.getKey(), entry.getValue());
                    }

                }


                /* If the buffer is bigger than max or if the writer is waiting then send
                buffer on output channel.  */
                if (buffer.position() >= BUFFER_OUT_SIZE_MAX || collector.isWriterWaiting()) {

                    if (buffer.position() > BUFFER_MIN) {
                        collector.offer(buffer);
                        buffer = collector.allocateBuffer(BUFFER_OUT_SIZE_MAX + BUFFER_MIN);
                    }
                }

                op = queue.poll();


                if (op == null) {

                    if (buffer.position() > BUFFER_MIN && collector.isWriterWaiting()) {
                        collector.offer(buffer);
                        buffer = collector.allocateBuffer(BUFFER_OUT_SIZE_MAX + BUFFER_MIN);
                    } else {
                        long now = time.get();

                        long duration = now - lastForceFlush;

                        if (duration > 30_000) {
                            collector.offer(buffer);
                            buffer = collector.allocateBuffer(BUFFER_OUT_SIZE_MAX + BUFFER_MIN);
                        }
                    }
                } else {


                    if (op instanceof RemoveRequest) {
                        data = op;
                        dataMap = null;
                    } else if (op instanceof SetRequest) {
                        data = op;
                        dataMap = null;
                    } else if (op instanceof BatchSetRequest) {
                        dataMap = (BatchSetRequest) op;
                        data = null;
                    }

                }


            }


        }


    }

    private void putDataInBuffer(ByteBuffer buffer, long messageId, String clientId, String key, String value) {
        LogEntry logEntry = new LogEntry(time.get(), index++, messageId, clientId, key, value);
        buffer.put(toJson(logEntry).getBytes());
        buffer.put((byte)'\n');
    }

    @Override
    public void stop() {


        flush();
        Sys.sleep(2_000);
        collector.stop();

        for (Future future : futures) {

            try {
                future.cancel(true);
            } catch (Exception ex) {

            }
        }

        for (ScheduledExecutorService service : scheduledExecutorServices) {

            try {
                service.shutdown();
            } catch (Exception ex) {

            }
        }
    }


    public void flush() {

    }

    @Override
    public void clearStats() {

    }

    @Override
    public void sendStats(long now) {

    }


    @Override
    public void set(SetRequest setRequest) {

        queue.put(setRequest);
    }

    @Override
    public void remove(RemoveRequest removeRequest) {
        queue.put(removeRequest);
    }

    @Override
    public void addAll(BatchSetRequest batchSetRequest) {
        queue.put(batchSetRequest);

    }


    @Override
    public void get(GetRequest getRequest) {

    }

    @Override
    public void search(SearchRequest searchRequest) {

    }

    @Override
    public void batchRead(ReadBatchRequest request) {

    }


    /**
     * Calculate utc time. This gets called every 20 mili-seconds or so.
     */
    @Override
    public void tick(long time) {
        this.time.set(time);
         /*Foreign thread    every 20 or so mili-seconds so we don't spend too
         much time figuring out utc time. */
        approxTime.set(Dates.utcNow());
    }

    private void startWorker(final String name, final Runnable runnable) {


        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setName(this.getClass().getSimpleName() + " " + name);
                        return thread;
                    }
                }
        );

        scheduledExecutorServices.add(scheduledExecutorService);


        Future<?> future;

        future = scheduledExecutorService.scheduleAtFixedRate(
                runnable, 0, 100, TimeUnit.MILLISECONDS);
        futures.add(future);


    }

    public void init(DataStoreConfig config) {
        this.config = config;
        collector.init(config);
    }

}

