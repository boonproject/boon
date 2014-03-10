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

package org.boon.di;

import org.boon.Lists;
import org.boon.di.impl.ContextImpl;
import org.boon.di.modules.InstanceModule;
import org.boon.di.modules.SupplierModule;

import java.util.List;
import java.util.Map;

public class DependencyInjection {


    public static Context context( final Module... modules ) {
        return new ContextImpl( modules );
    }

    public static Module classes( Class... classes ) {
        List<ProviderInfo> wrap = Lists.wrap(ProviderInfo.class, classes);
        return new SupplierModule(wrap);
    }



    public static Module objects( Object... objects ) {


        List<ProviderInfo> wrap = (List<ProviderInfo>) Lists.mapBy(objects, ProviderInfo.class, "objectProviderOf");

        return new SupplierModule( wrap );
    }


    public static Module prototypes( Object... objects ) {

        List<ProviderInfo> wrap = (List<ProviderInfo>) Lists.mapBy(objects, ProviderInfo.class, "prototypeProviderOf");

        return new SupplierModule( wrap );
    }

    public static Module module( Object module ) {

        return new InstanceModule( module );
    }

    public static Module suppliers( ProviderInfo... suppliers ) {

        return new SupplierModule( suppliers );
    }

    public static Context fromMap( Map<?, ?> map ) {
        return new ContextImpl(new SupplierModule( map ));
    }
}
