package org.boon.slumberdb.utility;

import org.boon.IO;
import org.boon.core.Dates;
import org.boon.core.Sys;
import org.boon.slumberdb.service.client.DataStoreClient;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.config.LogFilesReplicatorConfig;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.log.LogEntry;
import org.boon.slumberdb.stores.log.LogFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.boon.Boon.*;

/**
 * Created by Scott on 10/21/14
 */
public class LogFilesReplicator implements IO.EachLine {

    public static void main(String[] args) {
        // LogFilesReplicatorConfig has information on how to run the replication
        // DataStoreClientConfig has information pertainting to where we push the data
        // DataStoreConfig has one piece of information, the local server's output directory

        LogFilesReplicatorConfig config = LogFilesReplicatorConfig.load();
        if (config.verbose) {
            println("Log Files Replicator @ " + new Date());
            println("DataStoreClientConfig=" + Sys.sysProp("DataStoreClientConfig"));
            println("DataStoreConfig=" + Sys.sysProp("DataStoreConfig"));
            println("LogFilesReplicatorConfig=" + Sys.sysProp("LogFilesReplicatorConfig"));
            print("LogFilesReplicatorConfig ");
            println(toPrettyJson(config));
            println();
        }

        IO.createDirectories(config.archiveDirectory);

        long id = 0;
        while (true) {
            if (++id < 1) { id = 1; }
            if (config.verbose) {
                println();
                println("Processing Round: " + id);
            }
            List<LogFile> logFiles = getLogFilesToProcess(config.minimumAgeMillis, config.verbose);

            while (logFiles.size() > 0) {

                LogFile logFile = logFiles.remove(0);
                DataStoreClient client = ClientHelper.getVertxWebSocketClient(config.clientName + "." + id);

                LogFilesReplicator replicator = new LogFilesReplicator(config, client);

                IO.eachLine(logFile.path.toString(), replicator);
                replicator.afterIoEachFile();
                archive(config, logFile);
            }

            if (config.verbose) {
                println("Round Completed: " + id);
            }

            if (config.forever) {
                Sys.sleep(config.millisPauseAfterRound);
            }
            else {
                break;
            }
        }
    }

    private static List<LogFile> getLogFilesToProcess(long minimumAgeMillis, boolean verbose) {
        String logFilesDirectory = getLogFilesDirectory();
        List<LogFile> logFiles = new ArrayList<>();

        List<Path> paths = IO.listPath(IO.path(logFilesDirectory));
        if (paths != null && paths.size() > 0) {
            long maxLastModified = Dates.now() - minimumAgeMillis;
            for (Path path : paths) {
                LogFile logFile = new LogFile(path);
                if (logFile.lastModified < maxLastModified) {
                    logFiles.add(logFile);
                    if (verbose) {
                        println("Adding for processing: " + logFile.path);
                    }
                }
                else if (verbose) {
                    println("Skipping: " + logFile.path);
                }
            }

            // Sort the list oldest to newest
            Collections.sort(logFiles);
        }

        return logFiles;
    }

    private static void archive(LogFilesReplicatorConfig config, LogFile logFile) {
        Path target = Paths.get(config.archiveDirectory, logFile.path.getFileName().toString());
        IO.move(logFile.path, target);
    }

    private static String getLogFilesDirectory() {
        DataStoreConfig dsc = DataStoreConfig.load();
        return dsc.outputDirectory();
    }

    private LogFilesReplicatorConfig config;
    private DataStoreClient client;
    private boolean batch;
    private Map<String, Object> batchMap;

    public LogFilesReplicator(LogFilesReplicatorConfig config, DataStoreClient client) {
        this.config = config;
        this.client = client;
        batch = config.batchSize > 1;
        if (batch) {
            batchMap = new LinkedHashMap<>(config.batchSize);
        }
    }

    @Override
    public boolean line(String line, int index) {
        LogEntry le = LogEntry.load(line);
        if (batch) {
            log("Adding to batch: ", le.key, "=", le.value);
            batchMap.put(le.key, le.value);
            if (batchMap.size() >= config.batchSize) {
                clientSetBatch();
                batchMap.clear();
            }
        }
        else {
            clientSet(le);
        }
        return true;
    }

    public void afterIoEachFile() {
        if (batch && batchMap.size() > 0) { // any leftovers smaller than the full batchsize
            clientSetBatch();
        }
    }

    private void clientSet(LogEntry le) {
        log("Setting: ", le.key, "=", le.value);
        client.set(DataStoreSource.REPLICATION, le.key, le.value);
        sleepAfterSet();
    }

    private void clientSetBatch() {
        log("Setting Batch");
        client.setBatch(DataStoreSource.REPLICATION, batchMap);
        sleepAfterSet();
System.exit(0);
    }

    private void sleepAfterSet() {
        Sys.sleep(config.millisPauseAfterSet);
    }

    private void log(String... messages) {
        if (config.verbose) {
            puts(messages);
        }
    }
}
