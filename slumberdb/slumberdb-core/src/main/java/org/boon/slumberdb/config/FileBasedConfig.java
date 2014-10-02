package org.boon.slumberdb.config;

public class FileBasedConfig extends KeyValueStoreConfig {
    private String fileName;
    private String directory;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
