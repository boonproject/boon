package org.boon.slumberdb.stores.replicate;

import org.boon.Logger;
import org.boon.core.Dates;
import org.boon.core.Sys;
import org.boon.slumberdb.service.client.DataStoreClient;
import org.boon.slumberdb.service.config.ReplicationDataStoreConfig;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.stores.DataStore;
import org.boon.slumberdb.stores.log.TimeAware;
import org.boon.slumberdb.utility.ClientHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Scott in January 2015
 */
public class ReplicationDataStore implements DataStore, TimeAware {

    private AtomicLong time = new AtomicLong(Dates.utcNow());
    protected LinkedTransferQueue<DataStoreRequest> queue = new LinkedTransferQueue<>();
    protected List<ScheduledExecutorService> scheduledExecutorServices = new ArrayList<>();
    protected List<Future<?>> futures = new ArrayList<>();
    private Logger logger = configurableLogger(this.getClass());
    private ReplicationDataStoreConfig config;
    private DataStoreClient dataStoreClient;

    public ReplicationDataStore() {
    }

    public void start() {
        startWorker("ReplicationDataStore ", new Runnable() {
            @Override
            public void run() {
                try {
                    processSets();
                }
                catch (Exception ex) {
                    logger.error(ex, "ReplicationDataStore failed");
                }
            }
        });
    }

    private void processSets() {

        Map<String, Object> map = new HashMap<>();
        DataStoreRequest op = null;
        long lastSend = 0;

        while (true) {
            try {
                while (op == null) {
                    op = queue.poll(10, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
                continue;
            }

            map.clear();
            lastSend = time.get();

            while (op != null) {
                if (op instanceof SetRequest) {
                    SetRequest setRequest = (SetRequest)op;
                    map.put(setRequest.key(), setRequest.payload());
                }
                else if (op instanceof BatchSetRequest) {
                    BatchSetRequest bsr = (BatchSetRequest) op;
                    bsr.appendEntriesTo(map);
                }

                boolean sendNow = map.size() >= config.maxBatchSize();
                if (!sendNow) {
                    long elapsed = time.get() - lastSend;
                    sendNow = elapsed >= config.maxWaitMillis();
                }
                if (sendNow) {
                    dataStoreClient.setBatch(map);
                    map.clear();
                    lastSend = time.get();
                }

                op = queue.poll();
            }

            if (map.size() > 0) {
                dataStoreClient.setBatch(map);
            }
        }
    }

    @Override
    public void stop() {
        flush();
        Sys.sleep(2_000);

        for (Future future : futures) {
            try {
                future.cancel(true);
            } catch (Exception ex) { /* don't care */ }
        }

        for (ScheduledExecutorService service : scheduledExecutorServices) {
            try {
                service.shutdown();
            } catch (Exception ex) { /* don't care */ }
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
        this.time.set(time); /* Foreign thread every 20 or so mili-seconds so we don't spend too much time figuring out utc time. */
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

        future = scheduledExecutorService.scheduleAtFixedRate( runnable, 0, 100, TimeUnit.MILLISECONDS);
        futures.add(future);
    }

    public void init(ReplicationDataStoreConfig config) {
        this.config = config;
        dataStoreClient = ClientHelper.getVertxWebSocketClient(config.dataStoreClient().clientId(), config.dataStoreClient());
    }

}

