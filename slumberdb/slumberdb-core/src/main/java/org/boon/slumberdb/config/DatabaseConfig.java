package org.boon.slumberdb.config;

public class DatabaseConfig extends KeyValueStoreConfig {

    private String url;
    private String password;
    private String userName;
    private String tableName;


    public DatabaseConfig() {
    }

    public DatabaseConfig(Class<?> componentClass, String url, String password, String userName, String tableName) {
        super(componentClass);
        this.url = url;
        this.password = password;
        this.userName = userName;
        this.tableName = tableName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "url='" + url + '\'' +
                ", password='" + password.length() + '\'' +
                ", userName='" + userName + '\'' +
                ", tableName='" + tableName + '\'' +
                "} " + super.toString();
    }
}
