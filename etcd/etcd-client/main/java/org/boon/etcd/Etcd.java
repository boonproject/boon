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



/**
 * Created by rhightower on 10/8/14.
 */
package org.boon.etcd;

import org.boon.core.Handler;

interface Etcd {


    /**
     * Create a directory using async handler
     * @param responseHandler handler
     * @param name name of dir
     */
    void createDir(Handler<Response> responseHandler, String name);

    /**
     * Create a directory (blocking)
     * @param name name of dir
     * @return response
     */
    Response createDir(String name);


    /**
     * Create a temp directory, i.e., one with a time to live TTL
     * @param name name of dir
     * @param ttl ttl
     * @return
     */
    Response createTempDir(String name, long ttl);

    /**
     * Create a temp dir async.
     * @param responseHandler async handler
     * @param name name of dir
     * @param ttl time to live
     */
    void createTempDir(Handler<Response> responseHandler, String name, long ttl);

    /**
     * Update a directories time to live.
     * @param name dir name (path)
     * @param ttl ttl
     * @return
     */
    Response updateDirTTL(String name, long ttl);

    /**
     * Update a directories time to live.
     * @param responseHandler
     * @param name
     * @param ttl
     */
    void updateDirTTL(Handler<Response> responseHandler, String name, long ttl);

    /**
     * Delete a dir
     * @param name
     * @return
     */
    Response deleteDir(String name);

    /**
     * Delete a dir async.
     * @param responseHandler
     * @param name
     */
    void deleteDir(Handler<Response> responseHandler, String name);


    /**
     * Delete a dir and all of its children recursively.
     * @param name
     * @return
     */
    Response deleteDirRecursively(String name);
    void deleteDirRecursively(Handler<Response> responseHandler, String name);


    /**
     * List keys and value
     * @param key
     * @return
     */
    Response list(String key);


    /**
     * List keys and values asycn
     * @param responseHandler
     * @param key
     */
    void list(Handler<Response> responseHandler, String key);

    /**
     * List dir recursively.
     * @param key
     * @return
     */
    Response listRecursive(String key);
    void listRecursive(Handler<Response> responseHandler, String key);

    /**
     * List dir sorted for order so we can pull things out FIFO for job queuing.
     * @param key
     * @return
     */
    Response listSorted(String key);
    void listSorted(Handler<Response> responseHandler, String key);


    /**
     * Add key / value to dir
     * @param key
     * @param value
     * @return
     */
    Response addToDir(String dirName, String key, String value);
    void addToDir(Handler<Response> responseHandler, String dirName, String key, String value);

    /**
     * Set a key
     * @param key
     * @param value
     * @return
     */
    Response set(String key, String value);
    void set(Handler<Response> responseHandler, String key, String value);

    /**
     * Add a config under this key
     * @param key
     * @param fileName
     * @return
     */
    Response setConfigFile(String key, String fileName);
    void  setConfigFile(Handler<Response> responseHandler, String key, String fileName);

    /**
     * Update the key with a new value if it already exists
     * @param key
     * @param value
     * @return
     */
    Response setIfExists(String key, String value);
    void  setIfExists(Handler<Response> responseHandler, String key, String value);


    /**
     * Create the new key value only if it does not already exist.
     * @param key
     * @param value
     * @return
     */
    Response setIfNotExists(String key, String value);
    void  setIfNotExists(Handler<Response> responseHandler, String key, String value);

    /**
     * Create a temporary value with ttl set
     * @param key
     * @param value
     * @param ttl
     * @return
     */
    Response setTemp(String key, String value, int ttl);
    void  setTemp(Handler<Response> responseHandler, String key, String value, int ttl);

    /**
     * Remove TTL from key/value
     * @param key
     * @param value
     * @return
     */
    Response removeTTL(String key, String value);
    void removeTTL(Handler<Response> responseHandler, String key, String value);


    /**
     * Compare and swap if the previous value is the same
     * @param key
     * @param preValue
     * @param value
     * @return
     */
    Response compareAndSwapByValue(String key, String preValue, String value);
    void compareAndSwapByValue(Handler<Response> responseHandler, String key, String preValue, String value);

    /**
     * Compare and swap if the modified index has not changed.
     * @param key
     * @param prevIndex
     * @param value
     * @return
     */
    Response compareAndSwapByModifiedIndex(String key, long prevIndex, String value);
    void compareAndSwapByModifiedIndex(Handler<Response> responseHandler, String key, long prevIndex, String value);


    /**
     * Get the value
     * @param key
     * @return
     */
    Response get(String key);
    void get(Handler<Response> responseHandler, String key);


    /**
     * Get the value and ensure it is consistent. (Slow but consistent)
     * @param key
     * @return
     */
    Response getConsistent(String key);
    void getConsistent(Handler<Response> responseHandler, String key);

    /**
     * Wait for this key to change
     * @param key
     * @return
     */
    Response wait(String key);
    void wait(Handler<Response> responseHandler, String key);


    /**
     * Wait for this key to change and you can ask for the past key value based on index just in case you missed it.
     * @param key
     * @param index
     * @return
     */
    Response wait(String key, long index);
    void wait(Handler<Response> responseHandler, String key, long index);


    /**
     * Wait for this key to change and any key under this key dir recursively.
     * @param key
     * @return
     */
    Response waitRecursive(String key);
    void waitRecursive(Handler<Response> responseHandler, String key);


    /**
     * Wait for this key to change and any key under this key dir recursively, and
     * ask for the past key value based on index just in case you missed it.
     * @param key
     * @param index
     * @return
     */
    Response waitRecursive(String key, long index);
    void waitRecursive(Handler<Response> responseHandler, String key, long index);

    /**
     * Delete the key.
     * @param key
     * @return
     */
    Response delete(String key);
    void delete(Handler<Response> responseHandler, String key);

    /** Delete the key only if it is at this index
     *
     * @param key
     * @param index
     * @return
     */
    Response deleteIfAtIndex(String key, long index);
    void deleteIfAtIndex(Handler<Response> responseHandler, String key, long index);

    /**
     * Delete the value but only if it is at the previous value
     * @param key
     * @param prevValue
     * @return
     */
    Response deleteIfValue(String key, String prevValue);
    void deleteIfValue(Handler<Response> responseHandler, String key, String prevValue);

}

