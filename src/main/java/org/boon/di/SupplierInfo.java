package org.boon.di;

import org.boon.core.Supplier;

/**
 * Created by Richard on 2/3/14.
 */
public class SupplierInfo <T> {

    private Class<T> type;
    private Supplier <T> supplier;

    public SupplierInfo( Class<T> type, Supplier<T> supplier ) {
        this.type = type;
        this.supplier = supplier;
    }

    public static <T> SupplierInfo<T>  supplierOf(Class<T> type, Supplier<T> supplier) {
        return new SupplierInfo<>( type, supplier );
    }

    public Class<T> type() {
        return type;
    }

    public Supplier<T> supplier() {
        return supplier;
    }
}
