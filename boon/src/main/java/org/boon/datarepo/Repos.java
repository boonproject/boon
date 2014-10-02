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
package org.boon.datarepo;

import org.boon.datarepo.spi.RepoComposer;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.core.Function;
import org.boon.core.Supplier;

public class Repos {

    public static void setRepoBuilder( Supplier<RepoBuilder> factory ) {
        SPIFactory.setRepoBuilderFactory( factory );
    }

    public static void setDefaultSearchIndexFactory( Function<Class, SearchIndex> factory ) {
        SPIFactory.setSearchIndexFactory( factory );
    }

    public static void setLookupIndexFactory( Function<Class, LookupIndex> factory ) {
        SPIFactory.setLookupIndexFactory( factory );
    }

    public static void setUniqueLookupIndexFactory( Function<Class, LookupIndex> factory ) {
        SPIFactory.setUniqueLookupIndexFactory( factory );
    }

    public static void setUniqueSearchIndexFactory( Function<Class, SearchIndex> factory ) {
        SPIFactory.setUniqueSearchIndexFactory( factory );
    }

    public static void setRepoFactory( Supplier<RepoComposer> factory ) {
        SPIFactory.setRepoFactory( factory );
    }

    public static void setFilterFactory( Supplier<Filter> factory ) {
        SPIFactory.setFilterFactory( factory );
    }

    public static RepoBuilder builder() {
        SPIFactory.init();
        return SPIFactory.getRepoBuilderFactory().get();
    }

}
