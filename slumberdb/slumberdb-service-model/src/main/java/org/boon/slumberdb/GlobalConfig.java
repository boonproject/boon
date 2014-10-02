package org.boon.slumberdb;

import org.boon.core.Sys;

/**
 * Created by Richard on 6/27/14.
 */
public class GlobalConfig {


    public static final boolean DEBUG = Sys.sysProp("PE_DATA_STORE.DEBUG", false);

    public static final boolean VERBOSE = Sys.sysProp("PE_DATA_STORE.VERBOSE", false);


}
