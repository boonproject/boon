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

package org.boon.criteria;

import org.boon.Lists;
import org.boon.datarepo.ObjectEditor;

import java.io.Serializable;
import java.util.List;


public abstract class Update implements Serializable {

    private String name;

    public String getName() {
        return name;
    }


    public abstract void doSet( ObjectEditor repo, Object item );

    public static Update set( final String name, final int value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update delete(  ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.delete( item );
            }
        };
    }

    public static Update incInt( final String name ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                int v = repo.getInt( item, name );
                v++;
                repo.modify( item, name, v );
            }
        };
    }

    public static Update incPercent( final String name, final int percent ) {
        return new Update() {

            //Avoid the lookupWithDefault, pass the fields.
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                int value = repo.getInt( item, name );
                double dvalue = value;
                double dprecent = percent / 100.0;
                dvalue = dvalue + ( dvalue * dprecent );
                value = ( int ) dvalue;
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final long value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final Object value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final byte value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final float value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final char value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static Update set( final String name, final String value ) {
        return new Update() {
            @Override
            public void doSet( ObjectEditor repo, Object item ) {
                repo.modify( item, name, value );
            }
        };
    }

    public static List<Update> update( Update... values ) {
        return Lists.list( values );
    }


}
