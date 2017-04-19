/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.List;
import java.util.Map;

/**
 * Standard item interfaces.
 */
public final class MetisDataItem {
    /**
     * Private constructor.
     */
    private MetisDataItem() {
    }

    /**
     * Format object interface.
     */
    @FunctionalInterface
    public interface MetisDataObjectFormat {
        /**
         * Obtain Object summary.
         * @param pFormatter the data formatter
         * @return the display summary of the object
         */
        String formatObject(MetisDataFormatter pFormatter);
    }

    /**
     * Interface for items that are looked up via an integer index id.
     */
    public interface MetisIndexedItem {
        /**
         * Get the Id to index the list.
         * @return the Id
         */
        Integer getIndexedId();
    }

    /**
     * Interface for items that wish to control disabled items.
     */
    @FunctionalInterface
    public interface MetisDisableItem {
        /**
         * Determine whether the item is disabled.
         * @return true/false
         */
        boolean isDisabled();
    }

    /**
     * List interface.
     * @param <T> the list element type
     */
    @FunctionalInterface
    public interface MetisDataList<T> {
        /**
         * Obtain underlying list.
         * @return the list
         */
        List<T> getUnderlyingList();

        /**
         * Is the map empty?.
         * @return true/false
         */
        default boolean isEmpty() {
            return getUnderlyingList().isEmpty();
        }

        /**
         * Obtain the size of the map.
         * @return the size
         */
        default int size() {
            return getUnderlyingList().size();
        }
    }

    /**
     * Map interface.
     * @param <K> the map key type
     * @param <V> the map value type
     */
    @FunctionalInterface
    public interface MetisDataMap<K, V> {
        /**
         * Obtain underlying map.
         * @return the map
         */
        Map<K, V> getUnderlyingMap();

        /**
         * Is the map empty?.
         * @return true/false
         */
        default boolean isEmpty() {
            return getUnderlyingMap().isEmpty();
        }

        /**
         * Obtain the size of the map.
         * @return the size
         */
        default int size() {
            return getUnderlyingMap().size();
        }
    }
}
