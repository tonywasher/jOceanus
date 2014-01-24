/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.data;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class to record reference to a DataItem via another data item.
 * @param <E> the DataItem types
 */
public class DataTouch<E extends Enum<E>> {
    /**
     * The map of Item type to access count.
     */
    private final Map<E, TouchCounter<E>> theMap;

    /**
     * Constructor.
     * @param pClass the eNum class
     */
    public DataTouch(Class<E> pClass) {
        /* Create the map */
        theMap = new EnumMap<E, TouchCounter<E>>(pClass);
    }

    /**
     * Reset all touches.
     */
    public void resetTouches() {
        /* Clear the map */
        theMap.clear();
    }

    /**
     * Touch an item.
     * @param pItemType the item type
     */
    public void touchItem(final E pItemType) {
        /* Access the record for the item type */
        TouchCounter<E> myCounter = getCounter(pItemType);

        /* If this is a new dataType */
        if (myCounter == null) {
            /* Store a new counter */
            theMap.put(pItemType, new TouchCounter<E>(pItemType));

            /* else just record the touch */
        } else {
            myCounter.touch();
        }
    }

    /**
     * Is the item active.
     * @return true/false
     */
    public boolean isActive() {
        return !theMap.isEmpty();
    }

    /**
     * Obtain item count.
     * @param pItemType the item type
     * @return the counter (or null)
     */
    public TouchCounter<E> getCounter(final E pItemType) {
        return theMap.get(pItemType);
    }

    /**
     * Obtain iterator.
     * @return the iterator
     */
    public Iterator<TouchCounter<E>> iterator() {
        return theMap.values().iterator();
    }

    /**
     * Simple counter.
     * @param <E> the DataItem types
     */
    public static final class TouchCounter<E> {
        /**
         * The item type.
         */
        private final E theItemType;

        /**
         * The number of touches.
         */
        private int theTouches;

        /**
         * Obtain the item type.
         * @return the item type
         */
        public E getItemType() {
            return theItemType;
        }

        /**
         * Obtain the touch count.
         * @return the touches
         */
        public int getTouches() {
            return theTouches;
        }

        /**
         * Constructor.
         * @param pItemType the item type
         */
        private TouchCounter(final E pItemType) {
            theItemType = pItemType;
            theTouches = 0;
        }

        /**
         * Increment counter.
         */
        private void touch() {
            theTouches++;
        }
    }
}
