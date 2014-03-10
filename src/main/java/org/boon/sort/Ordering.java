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

package org.boon.sort;

import org.boon.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by Richard on 3/8/14.
 */
public class Ordering {


    /**
     * Does a binary search
     *
     * NOTE THIS WILL NOT SORT THE LIST ASCENDING.
     *
     * @param list items you want to search.
     * @param item the item you are searching for.
     * @param <T> type of item
     * @return item found or null
     */
    public static <T> T search(List<T> list, T item) {

        if (list.size()>1) {

            Object o = list;
            int index =  Collections.binarySearch((List<? extends Comparable<? super T>>) o, item);
            return list.get(index);
        } else {
            return null;
        }
    }


    /**
     * Does a binary search
     *
     *
     * NOTE THIS WILL NOT SORT THE LIST ASCENDING.
     *
     * @param list list you are searching
     * @param item the item you are searching for
     * @return the index of the item
     */
    public static int searchForIndex(List<?> list, Object item) {

        if (list.size()>1) {

            Object o = list;
            return  Collections.binarySearch((List<? extends Comparable<? super Object>>) o, item);
        } else {
            return -1;
        }
    }


    /**
     * Gets the max item from the list.
     * Sorts the list descending first.
     *
     * @param list the list
     * @param <T> type of items
     * @return the max item
     */
    public static <T> T max( List<T> list ) {

        if (list.size()>1) {
          Sorting.sortDesc(list);

          return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets the max item from the array.
     * Sorts the list descending first.
     *
     * @param array the list
     * @param <T> type of items
     * @return the max item
     */
    public static <T> T max( T[] array ) {

        if (array.length > 1) {
            Sorting.sortDesc(array);

            return array[0];
        } else {
            return null;
        }
    }



    /**
     * First of... out of hte sorts. This is the first item
     *
     * @param list
     * @param sorts
     * @param <T>
     * @return
     */
    public static <T> T firstOf( List<T> list, Sort... sorts ) {

        if (list.size()>1) {
            Sorting.sort(list, sorts);

            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * From the sorts, this is the first few items.
     *
     * @param list
     * @param count
     * @param sorts
     * @param <T>
     * @return
     */
    public static <T> List<T> firstOf( List<T> list, int count, Sort... sorts ) {

        if (list.size()>1) {
            Sorting.sort(list, sorts);

            return Lists.sliceOf(list, 0, count);
        } else {
            return null;
        }
    }


    /**
     * Grabs the last items after the sort.
     * @param list
     * @param sorts
     * @param <T>
     * @return
     */
    public static <T> T lastOf( List<T> list, Sort... sorts ) {

        if (list.size()>1) {
            Sorting.sort(list, sorts);

            return list.get(list.size()-1);
        } else {
            return null;
        }
    }


    /**
     * Grabs the last few items from the list.
     * @param list
     * @param count
     * @param sorts
     * @param <T>
     * @return
     */
    public static <T> List<T> lastOf( List<T> list, int count, Sort... sorts ) {

        if (list.size()>1) {
            Sorting.sort(list, sorts);

            return Lists.endSliceOf(list, count *-1);
        } else {
            return null;
        }
    }


    /**
     * Returns the max value of the object with the property given.
     *
     * @param list
     * @param sortBy
     * @param <T>
     * @return
     */
    public static <T> T max( List<T> list, String sortBy ) {

        if (list.size()>1) {
            Sorting.sortDesc(list, sortBy);

            return list.get(0);
        } else {
            return null;
        }
    }



    /**
     * Returns the max value of the object with the property given.
     *
     * @param array
     * @param sortBy
     * @param <T>
     * @return
     */
    public static <T> T max( T[] array, String sortBy ) {

        if ( array.length > 1 ) {
            Sorting.sortDesc(array, sortBy);

            return array[0];
        } else {
            return null;
        }
    }

    /**
     * Returns the least few.
     * @param list
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<T> least( List<T> list, int count ) {

        if (list.size()>1) {
            Sorting.sort(list);

            return Lists.sliceOf(list, 0, count);
        } else {
            return null;
        }
    }


    /**
     * Returns the least few.
     * @param list
     * @param sortBy
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<T> least( List<T> list, String sortBy, int count ) {

        if (list.size()>1) {
            Sorting.sort(list, sortBy);

            return Lists.sliceOf(list, 0, count);
        } else {
            return null;
        }
    }


    /**
     * Returns the greatest.
     * @param list
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<T> greatest( List<T> list, int count ) {

        if (list.size()>1) {
            Sorting.sortDesc(list);

            return Lists.sliceOf(list, 0, count);
        } else {
            return null;
        }
    }


    /**
     * Returns the greatest few.
     * @param list
     * @param sortBy
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<T> greatest( List<T> list, String sortBy, int count ) {

        if (list.size()>1) {
            Sorting.sortDesc(list, sortBy);

            return Lists.sliceOf(list, 0, count);
        } else {
            return null;
        }
    }

    /**
     * Returns the min value using a natural sort.
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T min( List<T> list ) {

        if (list.size()>1) {
            Sorting.sort(list);

            return list.get(0);
        } else {
            return null;
        }
    }


    /**
     * Returns the max value of the object with the property given.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T min( T[] array ) {

        if ( array.length > 1 ) {
            Sorting.sort(array);

            return array[0];
        } else {
            return null;
        }
    }


    /**
     * Returns the min value after sorting by the sortBy parameter.
     * @param list
     * @param sortBy
     * @param <T>
     * @return
     */
    public static <T> T min (List<T> list, String sortBy) {
        if (list.size()>1) {
            Sorting.sort(list, sortBy);

            return list.get(0);
        } else {
            return null;
        }

    }


    /**
     * Returns the min value of the object with the property given.
     *
     * @param array
     * @param sortBy
     * @param <T>
     * @return
     */
    public static <T> T min( T[] array, String sortBy ) {

        if ( array.length > 1 ) {
            Sorting.sort(array, sortBy);

            return array[0];
        } else {
            return null;
        }
    }

}
