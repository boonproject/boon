package org.boon.qbit.vertx;

import org.boon.qbit.vertx.impl.FactoryImpl;

/**
 * Created by Richard on 10/2/14.
 */
public class QBitVertx {


    public static Factory factory() {

        return new FactoryImpl();
    }

}
