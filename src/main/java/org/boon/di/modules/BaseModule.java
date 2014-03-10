/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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
