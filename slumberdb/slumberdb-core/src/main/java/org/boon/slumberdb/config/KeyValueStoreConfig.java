package org.boon.slumberdb.config;

public class KeyValueStoreConfig extends Config {

    private Class<?> componentClass;

    public KeyValueStoreConfig(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    public KeyValueStoreConfig() {
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }


    @Override
    public String toString() {
        return "KeyValueStoreConfig{" +
                "componentClass=" + componentClass +
                "} " + super.toString();
    }
}
