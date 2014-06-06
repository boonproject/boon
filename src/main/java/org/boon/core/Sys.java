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

package org.boon.core;


import org.boon.Lists;
import org.boon.Str;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.Reflection;
import org.boon.core.timer.TimeKeeper;
import org.boon.core.timer.TimeKeeperBasic;
import org.boon.logging.Logging;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;


public class Sys {

    private final static boolean isWindows = System.getProperty ( "os.name" ).contains ( "Windows" );
    private final static boolean inContainer;
    private final static boolean is1_7OorLater;
    private final static int buildNumber;
    private final static BigDecimal version;
    private final static boolean is1_7;
    private final static boolean is1_8;


    static {
        BigDecimal v = new BigDecimal ( "-1" );
        int b = -1;
        String sversion = System.getProperty ( "java.version" );
        if ( sversion.indexOf ( "_" ) != -1 ) {
            final String[] split = sversion.split ( "_" );
            try {

                String ver = split [0];
                if (ver.startsWith ( "1.8" )) {
                    v = new BigDecimal ("1.8" );
                }
                if (ver.startsWith ( "1.7" )) {
                    v = new BigDecimal ("1.7" );
                }

                if (ver.startsWith ( "1.6" )) {
                    v = new BigDecimal ("1.6" );
                }


                if (ver.startsWith ( "1.5" )) {
                    v = new BigDecimal ("1.5" );
                }


                if (ver.startsWith ( "1.9" )) {
                    v = new BigDecimal ("1.9" );
                }

                b = Integer.parseInt ( split[ 1 ] );
            } catch ( Exception ex ) {
                ex.printStackTrace ();
                System.err.println ( "Unable to determine build number or version" );
            }
        } else if ("1.8.0".equals(sversion)) {
            b = -1;
            v = new BigDecimal("1.8");
        } else {
            v = new BigDecimal ( sversion );
            b = -1;
        }

        buildNumber = b;
        version = v;

        is1_7OorLater = version.compareTo ( new BigDecimal ( "1.7" )) >=0;
        is1_7 = version.compareTo ( new BigDecimal ( "1.7" ))==0;
        is1_8 = version.compareTo ( new BigDecimal ( "1.8" ))==0;
    }


    public static void println ( String message ) {
        System.out.println ( message );
    }

    public static void print ( String message ) {
        System.out.print ( message );
    }


    public static boolean isWindows () {
        return isWindows;
    }

    public static boolean is1_7OrLater () {
        return is1_7OorLater;
    }

    public static boolean is1_7() {
        return is1_7;
    }
    public static boolean is1_8() {
        return is1_8;
    }

    public static int buildNumber () {
        return buildNumber;
    }

    public static char windowsPathSeparator () {
        return '\\';
    }


    final static AtomicReference<TimeKeeper> timer = new AtomicReference<TimeKeeper> ( new TimeKeeperBasic () );

    public static TimeKeeper timer () {
        return timer.get ();
    }

    public static long time () {
        return timer.get ().time ();
    }


    static {
        boolean _inContainer;
        boolean forceInContainer = Boolean.parseBoolean ( System.getProperty ( "org.boon.forceInContainer", "false" ) );
        boolean forceNoContainer = Boolean.parseBoolean ( System.getProperty ( "org.boon.forceNoContainer", "false" ) );

        if ( forceNoContainer ) {
            _inContainer = false;
        } else if ( forceInContainer ) {
            _inContainer = true;
        } else {
            _inContainer = detectContainer ();
        }

        inContainer = _inContainer;
    }

    private static boolean detectContainer () {

        boolean _inContainer;

        try {
            Class.forName ( "javax.servlet.http.HttpServlet" );

            _inContainer = true;
        } catch ( ClassNotFoundException e ) {
            _inContainer = false;
        }
        if ( !_inContainer ) {
            try {
                Class.forName ( "javax.ejb.EJBContext" );

                _inContainer = true;
            } catch ( ClassNotFoundException e ) {
                _inContainer = false;
            }

        }

        return _inContainer;

    }


    public static boolean inContainer () {
        return inContainer;
    }


    /* Everything that has a cache you need to hold on to, should use this so they can
     * all be stuffed into application context of web-app or ear if you use Java EE. */
    public static Object contextToHold () {

        return Lists.list ( Reflection.contextToHold (),
                Annotations.contextToHold (),
                Logging.contextToHold() );
    }

    public static String sysProp(String key) {
            return sysProp(key, null);
    }

    /**
     * Checks for the key under system property.
     * Then checks it as an environment variable.
     * (Looks up in env using straight key and performing underBarCase on it.)
     * Then converts defaultValue into a string.
     * @param key
     * @param defaultValue
     * @return
     */
    public static String sysProp(String key, Object defaultValue) {
        String property = System.getProperty(key, null);
        if (property == null) {
            property = System.getenv(key);
        }

        if (property == null) {
            String newKey = Str.underBarCase(key);
            property = System.getenv(newKey);
        }

        if (property == null) {
            return Conversions.toString(defaultValue);
        }

        return property;
    }


    public static String putSysProp(String key, Object value) {
        return System.setProperty(key, Conversions.toString(value));
    }

    public static boolean hasSysProp(String propertyName) {
        return System.getProperties().containsKey(propertyName);
    }

    public static void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
}
