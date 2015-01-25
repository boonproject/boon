package org.boon.slumberdb.service.config;

import org.boon.Boon;
import org.boon.core.Sys;

/**
 * Created by Scott on 10/21/14
 */
public class LogFilesReplicatorConfig {
    public String clientName;
    public int batchSize = 1;
    public boolean forever = false;
    public boolean verbose = false;
    public long millisPauseAfterSet = 100l;
    public long millisPauseAfterRound = 5000l;
    public long minimumAgeMillis = 180000;
    public String archiveDirectory;

    public static LogFilesReplicatorConfig load() {
        String fileLocation = Sys.sysProp("LogFilesReplicatorConfig", "/opt/org/slumberdb/logfilesreplicator.json");
        return Sys.loadFromFileLocation(LogFilesReplicatorConfig.class, fileLocation);
    }

    public static void main(String[] args) {
        Sys.putSysProp("LogFilesReplicatorConfig", "C:\\dev\\boonproject\\_data\\logfilesreplicator.json");
        LogFilesReplicatorConfig x = new LogFilesReplicatorConfig();
        System.out.println(Boon.toJson(x));
    }
}
