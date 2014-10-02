package org.boon.slumberdb.config;


import org.boon.Boon;

public class ConfigUtils {


    public static <CONF extends Config> CONF readConfig(String path, Class<CONF> cls) {
        CONF config = Boon.resourceObject(path, cls);
        return config;
    }
}
