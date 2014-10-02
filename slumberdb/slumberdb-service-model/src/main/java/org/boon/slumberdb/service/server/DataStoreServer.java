package org.boon.slumberdb.service.server;

import org.boon.core.reflection.ClassMeta;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.stores.MasterDataStore;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.boon.Logger;
import org.boon.core.Sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

public abstract class DataStoreServer {

    private static final int LOAD_DELAY_SECONDS = 2; //Wait for cache warm up and native thread warm-up
    private final boolean debug = GlobalConfig.DEBUG;
    private final DataOutputQueueTransferQueue queue = new DataOutputQueueTransferQueue(10);
    private final AtomicBoolean stop = new AtomicBoolean();
    protected List<ScheduledExecutorService> scheduledExecutorServices = new ArrayList<>();
    protected List<Future<?>> futures = new ArrayList<>();
    private Logger logger = configurableLogger(this.getClass());
    private MasterDataStore masterDataStore = new MasterDataStore();

    private RequestHandler requestHandler = new RequestHandler();

    private List<ResponseHandler> responseHandlers = new ArrayList<>();


    public void init(DataStoreServerConfig config) {


        logger.info("DataStoreServer::Server config will be loaded from", Sys.sysProp("DataStoreServerConfig"));
        logger.info("DataStoreServer::Server Configuration", config);


        logger.info("DataStoreServer::Data store will be loaded from", Sys.sysProp("DataStoreConfig"));


        masterDataStore.init(queue, config.startupMode());
        masterDataStore.start();


        final int dataStoreDrainerCount = config.dataStoreDrainerCount();

        for (int index = 0; index < dataStoreDrainerCount; index++) {

            ResponseHandler responseHandler =
                    new ResponseHandler();

            responseHandlers.add(responseHandler);
            responseHandler.init(this, config, index, masterDataStore, queue);
        }

        requestHandler.init(this, config, masterDataStore, queue);


        for (int index = 0; index < LOAD_DELAY_SECONDS; index++) {
            Sys.sleep(1_000);
            if (debug)
                logger.info("DataStoreServer::Starting HTTP server in seconds ", index + 1, " of ", LOAD_DELAY_SECONDS);
        }


    }


    protected abstract void sendToAllClients(String reply);


    protected abstract void sendMessageToClientId(String clientId, String reply);


    public void startWorker(final String name, final Runnable inputRunnable) {


        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (stop.get()) {
                    if (debug)
                        logger.info("DataStoreServer::Service is stopped, so we are not starting up the", name, "system");
                    return;
                }
                try {
                    inputRunnable.run();
                } catch (Exception ex) {
                    if (ex instanceof InterruptedException) {
                        if (stop.get()) {
                            return;
                        }
                        Thread.interrupted();
                    }
                    logger.warn(ex, "Error running ", name);
                }
            }

        };

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setName("DataStore " + name);
                        return thread;
                    }
                }
        );

        scheduledExecutorServices.add(scheduledExecutorService);


        Future<?> future = scheduledExecutorService.scheduleAtFixedRate(
                runnable, 0, 100, TimeUnit.MILLISECONDS);
        futures.add(future);


    }


    @ServiceMethod
    public abstract boolean clientExistsStill(String clientId);


    public void stop() {

        requestHandler.stop();

        Sys.sleep(1_000);

        stop.set(true);

        try {
            masterDataStore.stop();
        } catch (Exception ex) {
            if (debug) logger.error(ex, "DataStoreServer::error shutting down data store");
        }


        if (futures != null) {
            for (Future future : futures) {

                try {
                    future.cancel(true);
                } catch (Exception ex) {

                    if (debug) logger.error(ex, "error shutting down futures");
                }
            }
        }

        if (scheduledExecutorServices != null) {
            for (ScheduledExecutorService service : scheduledExecutorServices) {

                try {
                    service.shutdown();
                } catch (Exception ex) {

                    if (debug) logger.error(ex, "DataStoreServer::error shutting down resumable threads");
                }
            }
        }

        requestHandler.stop();

    }


    protected abstract void registerOutputHandler(String clientId, Object commChannel);


    public void handleCallFromClient(final String textData, final Object commChannel) {

        requestHandler.handleCallFromClient(textData, commChannel);
    }


    public void handleCallWithMap(String ipAddress, Map<String, String> message, String uri, Object commChannel) {
        requestHandler.handleCallWithMap(ipAddress, message, uri, commChannel);
    }


    public Map<String, ClassMeta<?>> getServicesDefinition() {
        return requestHandler.getServicesDefinition();
    }


}