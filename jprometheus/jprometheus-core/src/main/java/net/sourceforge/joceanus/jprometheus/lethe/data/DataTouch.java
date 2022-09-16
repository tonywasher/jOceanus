/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataTouch.TouchCounter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class to record reference to a DataItem via another data item.
 * @param <E> the Data item type class
 */
public class DataTouch<E extends Enum<E>>
        implements MetisDataObjectFormat, MetisDataMap<E, TouchCounter<E>> {
    /**
     * Map of touches.
     */
    private final Map<E, TouchCounter<E>> theTouchMap;

    /**
     * Constructor.
     * @param pClass the eNum class
     */
    public DataTouch(final Class<E> pClass) {
        /* Create the map */
        theTouchMap = new EnumMap<>(pClass);
    }

    @Override
    public Map<E, TouchCounter<E>> getUnderlyingMap() {
        return theTouchMap;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    /**
     * Constructor.
     * @param pSource the source map
     */
    protected void copyMap(final DataTouch<E> pSource) {
        /* Create the map */
        theTouchMap.putAll(pSource.getUnderlyingMap());
    }

    /**
     * Reset all touches.
     */
    public void resetTouches() {
        /* Clear the map */
        theTouchMap.clear();
    }

    /**
     * Reset touches for a dataType.
     * @param pItemType the ItemType
     */
    public void resetTouches(final E pItemType) {
        theTouchMap.remove(pItemType);
    }

    /**
     * Touch an item.
     * @param pItemType the item type
     */
    public void touchItem(final E pItemType) {
        /* Access the record for the item type */
        final TouchCounter<E> myCounter = getCounter(pItemType);

        /* If this is a new dataType */
        if (myCounter == null) {
            /* Store a new counter */
            theTouchMap.put(pItemType, new TouchCounter<E>(pItemType));

            /* else just record the touch */
        } else {
            myCounter.touch();
        }
    }

    /**
     * Is the item touched by the ItemType?
     * @param pItemType the item type
     * @return true/false
     */
    public boolean touchedBy(final E pItemType) {
        /* Access the record for the item type */
        final TouchCounter<E> myCounter = getCounter(pItemType);

        /* If this is a new dataType */
        return myCounter != null;
    }

    /**
     * Is the item active.
     * @return true/false
     */
    public boolean isActive() {
        return !isEmpty();
    }

    /**
     * Obtain item count.
     * @param pItemType the item type
     * @return the counter (or null)
     */
    public TouchCounter<E> getCounter(final E pItemType) {
        return theTouchMap.get(pItemType);
    }

    /**
     * Obtain iterator.
     * @return the iterator
     */
    public Iterator<TouchCounter<E>> iterator() {
        return theTouchMap.values().iterator();
    }

    /**
     * Simple counter.
     * @param <E> the DataItem types
     */
    public static final class TouchCounter<E>
            implements MetisDataObjectFormat {
        /**
         * The item type.
         */
        private final E theItemType;

        /**
         * The number of touches.
         */
        private int theTouches;

        /**
         * Constructor.
         * @param pItemType the item type
         */
        private TouchCounter(final E pItemType) {
            theItemType = pItemType;
            theTouches = 1;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return Integer.toString(theTouches);
        }

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
         * Increment counter.
         */
        private void touch() {
            theTouches++;
        }
    }
}
