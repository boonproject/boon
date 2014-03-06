package org.boon.di.modules;

import org.boon.core.Supplier;
import org.boon.di.Context;
import org.boon.di.Module;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Richard on 2/5/14.
 */
public abstract class BaseModule implements Module{

    private AtomicReference<Context> parent = new AtomicReference<>(  );

    @Override
    public void parent(Context context) {
        this.parent.set( context );
    }

    @Override
    public <T> T get( Class<T> type ) {
        return null;
    }

    @Override
    public Object get( String name ) {
        return null;
    }

    @Override
    public <T> T get( Class<T> type, String name ) {
        return null;
    }

    @Override
    public boolean has( Class type ) {
        return false;
    }

    @Override
    public boolean has( String name ) {
        return false;
    }

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type, String name ) {
        return null;
    }

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type ) {
        return null;
    }

}
