package org.boon.slumberdb.stores.log;

import java.nio.file.Path;

/**
 * Created by Scott on 10/21/14
 */
public class LogFile implements Comparable<LogFile> {
    public Path path;
    public long lastModified;
    public long number;
    public long time;
    public String serverName;

    public LogFile(Path path) {
        this.path = path;
        lastModified = path.toFile().lastModified();
        String fileName = path.getFileName().toString();
        String[] split = fileName.split("\\Q_\\E");
        number = Long.parseLong(split[split.length - 3]);
        time = Long.parseLong(split[split.length - 2]);
        serverName = split[split.length - 1];
        int at = serverName.lastIndexOf(".");
        serverName = serverName.substring(0, at);
    }

    @Override
    public int compareTo(LogFile o) {
        return Long.compare(lastModified, o.lastModified);
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "path='" + path + '\'' +
                ", modified=" + lastModified +
                ", number=" + number +
                ", time=" + time +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
