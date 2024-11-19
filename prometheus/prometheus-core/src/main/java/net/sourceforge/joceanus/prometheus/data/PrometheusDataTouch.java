/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataTouch.PrometheusTouchCounter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class to record reference to a DataItem via another data item.
 */
public class PrometheusDataTouch
        implements MetisDataObjectFormat, MetisDataMap<PrometheusListKey, PrometheusTouchCounter> {
    /**
     * Map of touches.
     */
    private final Map<PrometheusListKey, PrometheusTouchCounter> theTouchMap;

    /**
     * Constructor.
     */
    public PrometheusDataTouch() {
        /* Create the map */
        theTouchMap = new HashMap<>();
    }

    @Override
    public Map<PrometheusListKey, PrometheusTouchCounter> getUnderlyingMap() {
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
    protected void copyMap(final PrometheusDataTouch pSource) {
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
    public void resetTouches(final PrometheusListKey pItemType) {
        theTouchMap.remove(pItemType);
    }

    /**
     * Touch an item.
     * @param pItemType the item type
     */
    public void touchItem(final PrometheusListKey pItemType) {
        /* Access the record for the item type */
        final PrometheusTouchCounter myCounter = getCounter(pItemType);

        /* If this is a new dataType */
        if (myCounter == null) {
            /* Store a new counter */
            theTouchMap.put(pItemType, new PrometheusTouchCounter(pItemType));

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
    public boolean touchedBy(final PrometheusListKey pItemType) {
        /* Access the record for the item type */
        final PrometheusTouchCounter myCounter = getCounter(pItemType);

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
    public PrometheusTouchCounter getCounter(final PrometheusListKey pItemType) {
        return theTouchMap.get(pItemType);
    }

    /**
     * Obtain iterator.
     * @return the iterator
     */
    public Iterator<PrometheusTouchCounter> iterator() {
        return theTouchMap.values().iterator();
    }

    /**
     * Simple counter.
     */
    public static final class PrometheusTouchCounter
            implements MetisDataObjectFormat {
        /**
         * The item type.
         */
        private final PrometheusListKey theItemType;

        /**
         * The number of touches.
         */
        private int theTouches;

        /**
         * Constructor.
         * @param pItemType the item type
         */
        private PrometheusTouchCounter(final PrometheusListKey pItemType) {
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
        public PrometheusListKey getItemType() {
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
