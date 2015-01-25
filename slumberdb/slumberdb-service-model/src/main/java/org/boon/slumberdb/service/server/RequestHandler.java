package org.boon.slumberdb.service.server;

import org.boon.*;
import org.boon.collections.LazyMap;
import org.boon.concurrent.SimpleExecutors;
import org.boon.concurrent.Timer;
import org.boon.core.Sys;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.primitive.Arry;
import org.boon.primitive.CharBuf;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.factory.RequestFactory;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.service.results.StatsResults;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.MasterDataStore;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;
import static org.boon.Lists.safeList;
import static org.boon.slumberdb.service.protocol.ProtocolConstants.DELIMITER_STR;


/**
 * Created by Richard on 9/10/14.
 */
public class RequestHandler {

    final List<CallStatusTracker> callStatusTrackers = safeList(CallStatusTracker.class);
    private final boolean debug = GlobalConfig.DEBUG;
    protected LinkedTransferQueue<BroadCastSendData> broadCastQueue = new LinkedTransferQueue<>();
    ThreadLocal<JsonParserAndMapper> parserThreadLocal = new ThreadLocal<>();
    ThreadLocal<CharBuf> prettyPrintBuf = new ThreadLocal<>();
    ThreadLocal<JsonSerializer> jsonSerializer = new ThreadLocal<>();
    ThreadLocal<CallStatusTracker> callStatusTrackerThreadLocal = new ThreadLocal<>();
    int textBodyCall;
    int mapBodyCall;
    private boolean verbose = GlobalConfig.VERBOSE;
    private MasterDataStore masterDataStore = null;
    private ExecutorService executorService;
    private ExecutorService utilityThreads;
    private RequestFactory<String, DataStoreRequest> requestFromTextFactory;
    private RequestFactory<Map<String, String>, DataStoreRequest> requestFromMapFactory;
    private DataOutputQueue queue = null;
    /*

_____         .__         ___________       __                  .___ __________________________
/     \ _____  |__| ____   \_   _____/ _____/  |________ ___.__. |   |\      \__    ___/\_____  \
/  \ /  \\__  \ |  |/    \   |    __)_ /    \   __\_  __ <   |  | |   |/   |   \|    |    /   |   \
/    Y    \/ __ \|  |   |  \  |        \   |  \  |  |  | \/\___  | |   /    |    \    |   /    |    \
\____|__  (____  /__|___|  / /_______  /___|  /__|  |__|   / ____| |___\____|__  /____|   \_______  /
    \/     \/        \/          \/     \/             \/                  \/                 \/
________      _____________________      ____________________________ _____________________
\______ \    /  _  \__    ___/  _  \    /   _____/\__    ___/\_____  \\______   \_   _____/
|    |  \  /  /_\  \|    | /  /_\  \   \_____  \   |    |    /   |   \|       _/|    __)_
|    `   \/    |    \    |/    |    \  /        \  |    |   /    |    \    |   \|        \
/_______  /\____|__  /____|\____|__  / /_______  /  |____|   \_______  /____|_  /_______  /
    \/         \/              \/          \/                    \/       \/        \/
 */
    private Logger logger = configurableLogger(this.getClass());
    private DataStoreServerConfig config;
    private DataStoreServer storeServer = null;
    private Map<String, ClassMeta<?>> servicesDefinition;


    private Map<String, Object> services;
    private long messageIdGenerator = System.currentTimeMillis();
    private Set<String> dontEncodeMethods = Sets.safeSet("jmxStats");

    /**
     * Main entry into system.
     * <p/>
     * <p/>
     * <p/>
     * If you are debugging something not coming into the system.
     * <p/>
     * Start here!
     */

    @ServiceMethod
    public void mainRequestHandler(DataStoreRequest dataStoreRequest) {


        if (verbose || debug) {
            logger.info("RequestHandler::mainRequestHandler", dataStoreRequest);
        }
        switch (dataStoreRequest.action()) {
            case GET:
                masterDataStore.get((GetRequest) dataStoreRequest);
                break;
            case GET_LOCAL_DB:
                handleGetLocalDbVerb(dataStoreRequest);
                break;
            case GET_MEM:
                handleGetMemVerb(dataStoreRequest);
                break;
            case SET:
                masterDataStore.set((SetRequest) dataStoreRequest);
                break;
            case SET_BROADCAST:
                handleSetAndBroadCastVerb(dataStoreRequest);
                break;
            case SET_IF_NOT_EXIST:
                handleSetIfNotExistsVerb(dataStoreRequest);
                break;
            case SET_BATCH:
                handleSetBatch(dataStoreRequest);
                break;
            case SET_BATCH_IF_NOT_EXISTS:
                handleSetBatchIfNotExists(dataStoreRequest);
                break;
            case SET_SOURCE:
                handleSetSource(dataStoreRequest);
                break;
            case GET_SOURCE:
                handleGetSource(dataStoreRequest);
                break;

            case BATCH_READ:
                handleBatchRead(dataStoreRequest);
                break;

            case CLEAR_STATS:
                handleClearStats(dataStoreRequest);
                break;


            case GET_STATS:
                handleGetStats(dataStoreRequest);
                break;


            case REMOVE:
                handleRemove(dataStoreRequest);
                break;


            case REMOVE_SOURCE:
                handleRemoveSource(dataStoreRequest);
                break;

            case METHOD_CALL:
                handleMethodCall((MethodCall) dataStoreRequest);
                break;

            case SEARCH:
                handleSearchVerb(dataStoreRequest);
                break;

            default:
                puts(dataStoreRequest);


        }
        trackCall(dataStoreRequest.action());
    }

    @ServiceMethod
    public String jmxStats() {
        return MBeans.toJson();
    }

    @ServiceMethod
    public Object jmxStatsPretty() {
        final Object fromJson = Boon.fromJson(MBeans.toJson());
        return fromJson;
    }

    @ServiceMethod
    public List<Map<String, Object>> methodList(String object) {

        List<Map<String, Object>> methodList = new ArrayList<>();
        Iterable<MethodAccess> methods = servicesDefinition.get(object).methods();

        for (MethodAccess access : methods) {
            boolean publicFlag = true;
            String methodName = access.name();
            boolean weirdMethod = methodName.indexOf('$') != -1;
            boolean serviceMethod = access.hasAnnotation("serviceMethod");

            if (publicFlag && !weirdMethod && serviceMethod) {

                methodList.add(
                        Maps.map(
                                "name", access.name(),
                                "parameterTypes", BeanUtils.idx(access.parameterTypes(), "name"),
                                "returnType", access.returnType().getName()
                        )
                );
            }
        }

        return methodList;
    }

    private void handleMethodCall(MethodCall methodCall) {

        logger.info(methodCall);

        final ClassMeta<?> classMeta = servicesDefinition.get(methodCall.object());
        if (classMeta == null) {
            this.storeServer.sendMessageToClientId(methodCall.clientId(), "[\"UNKNOWN OBJECT\"]");
            return;
        }


        MethodAccess methodAccess = classMeta.method(methodCall.method());

        if (methodAccess == null) {
            this.storeServer.sendMessageToClientId(methodCall.clientId(), "[\"UNKNOWN METHOD\"]");
            return;
        }

        Object service = services.get(methodCall.object());

        Object returnValue = null;

        try {

            /* If there are no parameters, just invoke it. */
            if (methodAccess.parameterTypes().length == 0) {
                returnValue = methodAccess.invoke(service);

            /* If the length and argument sizes match then invoke using invokeFromList. */
            } else if (methodAccess.parameterTypes().length
                    == methodCall.arguments().size()) {

                returnValue = Invoker.invokeFromList(service, methodAccess.name(), methodCall.arguments());


            /* If method call only has one argument, and we know they don't match, then
                convert this argument into list or map and try to invoke it. */
            } else if (methodCall.arguments().size() == 1) {

                final Object objectArg = jsonParser().parse(methodCall.arguments().get(0));
                if (objectArg instanceof List) {

                    List argList = (List) objectArg;

                    final Object[] objectArray = Arry.objectArray(argList);
                    if (methodAccess.respondsTo(objectArray)) {
                        returnValue = methodAccess.invoke(service, objectArray);
                    } else {
                        returnValue = Invoker.invokeFromList(service, methodAccess.name(), (List) objectArg);
                    }

                } else if (objectArg instanceof Map) {
                    returnValue = Invoker.invokeFromObject(service, methodAccess.name(), objectArg);
                } else {
                    returnValue = Invoker.invokeFromList(service, methodAccess.name(), Lists.list(objectArg));

                }
            }

        } catch (Exception ex) {
            logger.error(ex, "Error tyring to invoke method", methodCall);
            returnValue = ex;
        }


        String returnString;

        if (dontEncodeMethods.contains(methodCall.method())) {
            returnString = Str.str(returnValue.toString());
        } else if (methodCall.isPrettyPrint()) {

            final CharBuf charBuf = prettyPrintBuf();
            charBuf.recycle();

            returnString = prettyPrintBuf().prettyPrintObject(returnValue, false, 0).toString();
        } else {
            returnString = jsonSerializer().serialize(returnValue).toString();
        }

        storeServer.sendMessageToClientId(methodCall.clientId(), returnString);

    }

    private JsonParserAndMapper jsonParser() {

        JsonParserAndMapper jsonParser = parserThreadLocal.get();
        if (jsonParser == null) {
            jsonParser = new JsonParserFactory().create();

            parserThreadLocal.set(jsonParser);
        }
        return jsonParser;
    }

    private CharBuf prettyPrintBuf() {

        CharBuf buf = prettyPrintBuf.get();
        if (buf == null) {

            buf = CharBuf.createCharBuf();
            prettyPrintBuf.set(buf);
        }
        return buf;
    }

    private JsonSerializer jsonSerializer() {

        JsonSerializer jsonSerializer = this.jsonSerializer.get();
        if (jsonSerializer == null) {

            jsonSerializer = new JsonSerializerFactory().create();
            this.jsonSerializer.set(jsonSerializer);
        }
        return jsonSerializer;
    }

    private void trackCall(Action action) {
        callStatusTracker().addCall(action, queue);
    }

    CallStatusTracker callStatusTracker() {

        CallStatusTracker callStatusTracker = callStatusTrackerThreadLocal.get();
        if (callStatusTracker == null) {
            callStatusTracker = new CallStatusTracker();
            callStatusTrackers.add(callStatusTracker);
            callStatusTrackerThreadLocal.set(callStatusTracker);
        }
        return callStatusTracker;


    }

    private void handleGetLocalDbVerb(DataStoreRequest dataStoreRequest) {
        masterDataStore.getSource((GetRequest) dataStoreRequest);

    }

    private void handleGetMemVerb(DataStoreRequest dataStoreRequest) {
        String clientId = dataStoreRequest.clientId();
        String key = dataStoreRequest.key();
        String value = masterDataStore.get(key);
        value = value == null ? "null" : value;

        SingleResult dataItem = new SingleResult(dataStoreRequest.messageId(), dataStoreRequest.clientId(), DataStoreSource.MEMORY, key, value);

        if (debug) logger.info("RequestHandler::handleGetMemVerb::", dataItem);

        storeServer.sendMessageToClientId(clientId, dataItem.toTextMessage());
    }


    private void handleSetIfNotExistsVerb(DataStoreRequest request) {

        if (debug) logger.info("RequestHandler::handleSetNotExistsVerb\n", request);


        if (!masterDataStore.exists(request.key())) {
            masterDataStore.set((SetRequest) request);
        }
    }

    private void handleSetAndBroadCastVerb(DataStoreRequest request) {

        if (debug) logger.info("RequestHandler::handleSetAndBroadCastVerb::", request);

        masterDataStore.set((SetRequest) request);

        SingleResult dataItem = new SingleResult(request.messageId(), request.clientId(), DataStoreSource.CLIENT, request.key(), request.payload());


        broadCastQueue.offer(new BroadCastSendData(request.key(), dataItem.toBroadcastTextMessage()));

    }


    @ServiceMethod
    public void turnOnVerbose() {
        this.verbose = true;
    }

    private void handleGetStats(final DataStoreRequest dataStoreRequest) {


        final long now = Timer.timer().time();


        utilityThreads.submit(new Runnable() {
            @Override
            public void run() {
                masterDataStore.sendStats(now);

            }
        });


        try {

            StatCount count;
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread CPU Time " + Thread.currentThread().getName(), Sys.threadCPUTime());
            this.queue.put(count);

        } catch (Throwable ex) {
            logger.warn("Could not get thread cpu time");
        }

        utilityThreads.submit(new Runnable() {
            @Override
            public void run() {

                collectStats(now);

            }
        });


        utilityThreads.submit(new Runnable() {
            @Override
            public void run() {

                Sys.sleep(1000);

                queue.put(new StatsResults((StatsRequest) dataStoreRequest));

            }
        });

    }


    private void handleRemoveSource(DataStoreRequest dataStoreRequest) {

        masterDataStore.removeFromSource((RemoveRequest) dataStoreRequest);
    }


    @ServiceMethod
    public void collectStats(long now) {


        collectConfig(now);


        StatCount count;


        try {

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME USER  RequestHandler "
                    + Thread.currentThread().getName(), Sys.threadUserTime());
            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME CPU  RequestHandler "
                    + Thread.currentThread().getName(), Sys.threadCPUTime());
            this.queue.put(count);


            long num = Sys.availableProcessors();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "CPU availableProcessors", num);
            this.queue.put(count);


            num = Sys.freeMemory();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory freeMemory", num);
            this.queue.put(count);

            num = Sys.maxMemory();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory maxMemory", num);
            this.queue.put(count);

            num = Sys.totalMemory();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory totalMemory", num);
            this.queue.put(count);


            num = Sys.pendingFinalizationCount();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory pendingFinalizationCount", num);
            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Load ", Sys.loadAverage());
            this.queue.put(count);

            MemoryUsage memoryUsage = Sys.heapMemoryUsage();
            num = memoryUsage.getCommitted();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory heapUsage committed", num);
            this.queue.put(count);

            num = memoryUsage.getInit();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory heapUsage init", num);
            this.queue.put(count);

            num = memoryUsage.getMax();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory heapUsage max", num);
            this.queue.put(count);

            num = memoryUsage.getUsed();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory heapUsage used", num);
            this.queue.put(count);

            memoryUsage = Sys.nonHeapMemoryUsage();
            num = memoryUsage.getCommitted();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory non-heap committed", num);
            this.queue.put(count);

            num = memoryUsage.getInit();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory non-heap init", num);
            this.queue.put(count);

            num = memoryUsage.getMax();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory non-heap max", num);
            this.queue.put(count);

            num = memoryUsage.getUsed();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory non-heap used", num);
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread count", Sys.threadCount());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread started", Sys.threadsStarted());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread peakCount", Sys.threadPeakCount());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread daemon count", Sys.threadDaemonCount());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "FILE max file descriptor count", Sys.maxFileDescriptorCount());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "FILE open file descriptor count", Sys.openFileDescriptorCount());
            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory committedVirtualMemorySize", Sys.committedVirtualMemorySize());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory totalSwapSpaceSize", Sys.totalSwapSpaceSize());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory freeSwapSpaceSize", Sys.freeSwapSpaceSize());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "CPU processCpuTime", Sys.processCpuTime());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "CPU systemCpuLoad", Sys.systemCpuLoad());
            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory freePhysicalMemorySize", Sys.freePhysicalMemorySize());
            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Memory totalPhysicalMemorySize", Sys.totalPhysicalMemorySize());
            this.queue.put(count);


            final List<GarbageCollectorMXBean> gc = Sys.gc();

            for (GarbageCollectorMXBean bean : gc) {
                if (bean.isValid()) {
                    String area = Str.joinObjects(' ', "GC", bean.getName(), "collection count");
                    count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, area, bean.getCollectionCount());
                    this.queue.put(count);
                    area = Str.joinObjects(' ', "GC", bean.getName(), "collection time");

                    count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, area, bean.getCollectionTime());
                    this.queue.put(count);

                }
            }

            num = Sys.uptime();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Time uptime", num);
            this.queue.put(count);


            num = Sys.startTime();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Time start time", num);
            this.queue.put(count);


            num = Timer.timer().time();
            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Time now", num);
            this.queue.put(count);


        } catch (Throwable t) {

            if (debug) logger.info(t, "Unable to get JMX data ");
        }


        try {
            String sDir = masterDataStore.config().outputDirectory();
            File dir = new File(sDir);
            if (dir.exists()) {
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "localDB FreeSpace " + dir, dir.getFreeSpace());
                this.queue.put(count);
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "localDB TotalSpace " + dir, dir.getTotalSpace());
                this.queue.put(count);
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "localDB UsableSpace " + dir, dir.getUsableSpace());
                this.queue.put(count);
            }

            sDir = config.logDirectory();
            dir = new File(sDir);
            if (dir.exists()) {
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "log FreeSpace " + dir, dir.getFreeSpace());
                this.queue.put(count);
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "log TotalSpace " + dir, dir.getTotalSpace());
                this.queue.put(count);
                count = new StatCount(DataStoreSource.FILE_SYSTEM, Action.GET_STATS, "log UsableSpace " + dir, dir.getUsableSpace());
                this.queue.put(count);
            }

        } catch (Throwable ex) {
            if (debug) logger.info(ex, "RequestHandler::Unable to send file stats");
        }

    }

    private void collectConfig(long now) {

        StatCount statCount;

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG websocketWorkerCount",
                config.websocketWorkerCount());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dataStoreDrainerCount",
                config.dataStoreDrainerCount());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG broadcastInterval",
                config.broadcastInterval());
        queue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG broadcastInterval",
                config.broadcastInterval());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG maxFrameSize",
                config.maxFrameSize());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG port",
                config.port());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG batchResultSize",
                config.batchResultSize());
        queue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG statusInterval",
                config.statusInterval());
        queue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG debug",
                config.debug() ? 1 : 0);
        queue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG logDirectory " + config.logDirectory(),
                config.logDirectory().length());
        queue.put(statCount);

    }


    private void handleClearStats(DataStoreRequest dataStoreRequest) {

        for (CallStatusTracker tracker : callStatusTrackers) {
            tracker.clearStats();
        }

        masterDataStore.clearStats();
    }

    private void handleRemove(DataStoreRequest dataStoreRequest) {

        RemoveRequest removeRequest = (RemoveRequest) dataStoreRequest;

        masterDataStore.remove(removeRequest);

    }

    public void handleCallFromClient(final String textData, final Object commChannel) {


        textBodyCall++;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doHandleCallFromClient(textData, commChannel);
            }
        });

        if (textBodyCall % 20 == 0) {
            StatCount count = new StatCount(DataStoreSource.SERVER, Action.NONE, "Call Counter TEXT" + Thread.currentThread().getName(), textBodyCall);
            queue.put(count);
        }
    }

    public void handleCallFromClient(final Map<String, String> message, final String uri, final Object commChannel) {

        mapBodyCall++;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doHandleCallFromClient(message, uri, commChannel);
            }
        });


        if (mapBodyCall % 20 == 0) {
            StatCount count = new StatCount(DataStoreSource.SERVER, Action.NONE, "Call Counter MAP" + Thread.currentThread().getName(), mapBodyCall);
            queue.put(count);
        }
    }

    private DataStoreRequest createRequest(String message) {
        try {
            return requestFromTextFactory.createRequest(message);
        } catch (Exception ex) {
            logger.error("Unable to parse message\n", message, "\n");

            logger.error(ex, "Unable to parse message");
            logger.error("Unable to parse message\n", message, "\n", "RECIEVED: \n\n\t", message.replaceAll(DELIMITER_STR, "\n\t"), "\n");
            return null;
        }
    }

    private DataStoreRequest createRequest(Map<String, String> message) {
        try {
            return requestFromMapFactory.createRequest(message);
        } catch (Exception ex) {
            logger.error("Unable to parse message\n", message, "\n");

            logger.error(ex, "Unable to parse message");
            logger.error("Unable to parse message\n", message, "\n", "RECIEVED: \n\n\t", message, "\n");
            return null;
        }
    }

    public void init(DataStoreServer dataStoreServer,
                     DataStoreServerConfig config,
                     MasterDataStore masterDataStore,
                     DataOutputQueue queue) {


        this.storeServer = dataStoreServer;
        this.config = config;
        this.masterDataStore = masterDataStore;
        this.queue = queue;


        services = Maps.safeMap(
                "server", storeServer,
                "this", storeServer,
                "admin", this,
                "dataStore", masterDataStore,
                "masterDataStore", masterDataStore,
                "storeServer", storeServer,
                "queue", queue
        );

        servicesDefinition = Maps.safeMap(
                "server", ClassMeta.classMeta(storeServer.getClass()),
                "this", ClassMeta.classMeta(storeServer.getClass()),
                "admin", ClassMeta.classMeta(this.getClass()),
                "dataStore", ClassMeta.classMeta(masterDataStore.getClass()),
                "masterDataStore", ClassMeta.classMeta(masterDataStore.getClass()),
                "storeServer", ClassMeta.classMeta(storeServer.getClass()),
                "queue", ClassMeta.classMeta(queue.getClass())
        );


        executorService = SimpleExecutors.threadPool(config.websocketWorkerCount(), "DS WebSocketWorker");
        utilityThreads = SimpleExecutors.threadPool("DS RequestHandler Utility");

        requestFromTextFactory = ProtocolConstants.textProtocolFactory();
        requestFromMapFactory = ProtocolConstants.mapProtocolFactory();


        startBroadcastDrainer();

    }

    private void doHandleCallFromClient(final String message, final Object commChannel) {

        try {
            if (debug) logger.info("RequestHandler:doHandleCallFromClient\n", ProtocolConstants.prettyPrintMessageWithLinesTabs(message));

            final DataStoreRequest dataStoreRequest = createRequest(message);

            if (debug) logger.info("RequestHandler:doHandleCallFromClient", "REQUEST=", dataStoreRequest);

            storeServer.registerOutputHandler(dataStoreRequest.clientId(), commChannel);

            mainRequestHandler(dataStoreRequest);
        }
        catch (Exception ex) {
            logger.error(ex, "RequestHandler::Unable to handle request");
            logger.error("RequestHandler::Unable to handle request TEXT DATA\n",
            ProtocolConstants.prettyPrintMessageWithLinesTabs(message));
        }

    }

    private void doHandleCallFromClient(final Map<String, String> message, String uri, final Object commChannel) {
        try {
            if (debug) logger.info("RequestHandler::doHandleCallFromClient", message);

            final DataStoreRequest dataStoreRequest = createRequest(message);

            if (debug) logger.info("RequestHandler::doHandleCallFromClient", dataStoreRequest);

            storeServer.registerOutputHandler(dataStoreRequest.clientId(), commChannel);
            mainRequestHandler(dataStoreRequest);
        }
        catch (Exception ex) {
            logger.error(ex, "RequestHandler::Unable to handle request");
            logger.error("RequestHandler::Unable to handle request MAP DATA", message);
        }

    }

    private void startBroadcastDrainer() {


        storeServer.startWorker("Broadcast handler",
                new Runnable() {
                    @Override
                    public void run() {
                        runBroadcastProcess();
                    }
                }
        );


    }

    private void runBroadcastProcess() {

        while (true) {
            Sys.sleep(config.broadcastInterval());

            Map<String, BroadCastSendData> map = new LinkedHashMap();

            BroadCastSendData broadCastSendData = broadCastQueue.poll();

            while (broadCastSendData != null) {

                map.put(broadCastSendData.key, broadCastSendData);
                broadCastSendData = broadCastQueue.poll();

            }


            for (String key : map.keySet()) {
                storeServer.sendToAllClients(map.get(key).reply);
            }
        }
    }

    public void handleCallWithMap(String ipAddress, Map<String, String> message, String uri,  Object commChannel ) {


        if (debug) {
            logger.info("RequestHandler::handleCallWithMap()", ipAddress, message, uri, commChannel);

        }

        if (!message.containsKey(ProtocolConstants.MESSAGE_ID_MAP_KEY)) {
            message.put(ProtocolConstants.MESSAGE_ID_MAP_KEY, "" + (messageIdGenerator++));
        }


        if (!message.containsKey(ProtocolConstants.CLIENT_ID_MAP_KEY)) {
            message.put(ProtocolConstants.CLIENT_ID_MAP_KEY, "generatedClientId" + ipAddress);
        }

        handleCallFromClient(message, uri, commChannel);

    }

    private void handleSetSource(DataStoreRequest dataStoreRequest) {
        SetRequest request = (SetRequest) dataStoreRequest;
        masterDataStore.setSource(request);
    }

    private void handleGetSource(DataStoreRequest dataStoreRequest) {
        GetRequest request = (GetRequest) dataStoreRequest;
        masterDataStore.getSource(request);
    }

    private void handleSetBatchIfNotExists(DataStoreRequest dataStoreRequest) {

        BatchSetRequest request = (BatchSetRequest) dataStoreRequest;

        final List<String> keys = request.keys();

        LazyMap map = new LazyMap(keys, request.values(), true);

        for (String key : keys) {

            if (masterDataStore.exists(key)) {
                map.remove(key);
            }
        }
        masterDataStore.addAll(new BatchSetRequest(request, map.keySet(), (List) map.values()));//only send short map
    }

    private void handleSetBatch(DataStoreRequest dataStoreRequest) {

        BatchSetRequest request = (BatchSetRequest) dataStoreRequest;

        masterDataStore.addAll(request);

    }

    private void handleBatchRead(DataStoreRequest request) {


        try {


            ReadBatchRequest batchRequest = (ReadBatchRequest) request;


            int chunkSize = config.batchResultSize() * 2;

            List<String> keysToFetch = new ArrayList<>();
            for (String key : batchRequest.keys()) {
                keysToFetch.add(key);
                if (keysToFetch.size() > chunkSize) {


                    masterDataStore.batchRead(
                            new ReadBatchRequest(batchRequest, new ArrayList<>(keysToFetch)));

                    keysToFetch.clear();
                }

            }


            if (keysToFetch.size() > 0) {
                masterDataStore.batchRead(
                        new ReadBatchRequest(batchRequest, new ArrayList<>(keysToFetch)));

            }


        } catch (Exception ex) {

            logger.error(ex, "Unable to handle bulk send", request.clientId(), request.payload());
        }
    }

    private void handleSearchVerb(DataStoreRequest dataStoreRequest) {
        masterDataStore.search((SearchRequest) dataStoreRequest);
    }

    public void stop() {


        try {
            if (utilityThreads != null) {
                utilityThreads.shutdownNow();
            }
        } catch (Exception ex) {

            if (debug) logger.error(ex, "error shutting down utilityThreads executor service");
        }

        try {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (Exception ex) {

            if (debug) logger.error(ex, "error shutting down executor service");
        }

    }

    private static class CallStatus {

        Action action;
        int count;
    }

    private static class CallStatusTracker {
        long numberOfCallsSinceLastSendAttempt = 0;
        long totalNumCalls = 0;
        long lastStatSend = 0;
        Map<Action, CallStatus> callStatusMap = new HashMap<>();
        private Logger logger = configurableLogger(this.getClass());

        void addCall(Action action, DataOutputQueue queue) {
            CallStatus callStatus = callStatusMap.get(action);

            if (callStatus == null) {
                callStatus = new CallStatus();
                callStatus.action = action;
                callStatus.count = 1;
                callStatusMap.put(action, callStatus);
            } else {
                callStatus.count++;
            }

            numberOfCallsSinceLastSendAttempt++;
            if (numberOfCallsSinceLastSendAttempt > 100) {
                totalNumCalls += numberOfCallsSinceLastSendAttempt;
                numberOfCallsSinceLastSendAttempt = 0;

                long now = Timer.timer().time();
                long duration = now - lastStatSend;

                /* Only send every 20 seconds. */
                if (duration > 20_000) {
                    for (CallStatus stat : callStatusMap.values()) {
                        StatCount count = new StatCount(now, DataStoreSource.SERVER,
                                stat.action,
                                "CALL COUNT " + Thread.currentThread().getName(),
                                stat.count);
                        queue.put(count);
                    }

                    StatCount count = new StatCount(now, DataStoreSource.SERVER,
                            Action.NONE,
                            "CALL COUNT TOTAL COUNT FOR " + Thread.currentThread().getName(),
                            totalNumCalls);
                    queue.put(count);
                    lastStatSend = now;

                    try {

                        count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread CPU Time " + Thread.currentThread().getName(), Sys.threadCPUTime());
                        queue.put(count);

                    } catch (Throwable ex) {

                        logger.info(ex, "Could not get CPU time");
                    }

                }
            }
        }

        public void clearStats() {

            numberOfCallsSinceLastSendAttempt = 0;
            totalNumCalls = 0;
            lastStatSend = 0;
            callStatusMap.clear();
        }
    }

    public static class BroadCastSendData {
        private String key;
        private String reply;


        public BroadCastSendData(String key, String reply) {
            this.key = key;
            this.reply = reply;
        }

    }

    public Map<String, ClassMeta<?>> getServicesDefinition() {
        return servicesDefinition;
    }


}
