package org.boon.slumberdb.stores.log;

import org.boon.core.Sys;

import java.nio.file.Path;

/**
 * Created by Scott on 10/21/14
 */
public class LogFilesConfig {
    public String logFilesNameFormatPattern = "%s/user_data_collection_%s_%s_%s.json";
    public String logFilesServerName = "server1";
    public Integer logFileSizeBytes = 10_000_000;
    public Integer logFileTimeoutMinutes = 10;
    public Integer logFileFlushEveryBytes = 20_000_000;

    public static LogFilesConfig load() {
        String fileLocation = Sys.sysProp("LogFilesConfig", "/opt/org/slumberdb/logfiles.json");
        return Sys.loadFromFileLocation(LogFilesConfig.class, fileLocation);
    }

    public static String getLogFileName(String formatPattern, Path outputDirectory, long number, long time, String serverName) {
        try {
            return String.format(formatPattern, outputDirectory, number, time, serverName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLogFileName(String formatPattern, String outputDirectory, long number, long time, String serverName) {
        try {
            return String.format(formatPattern, outputDirectory, number, time, serverName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
