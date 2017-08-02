/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    public interface MetisDataObjectFormat {
        /**
         * Obtain Object summary.
         * @param pFormatter the data formatter
         * @return the display summary of the object
         */
        default String formatObject(MetisDataFormatter pFormatter) {
            return toString();
        }
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
     * Data Field Item.
     */
    public interface MetisDataFieldItem
            extends MetisDataObjectFormat {
        /**
         * Obtain the Data FieldSet.
         * @return the report fields
         */
        MetisDataFieldSet getDataFieldSet();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(MetisDataField pField);
    }

    /**
     * Table Item.
     */
    public interface MetisDataTableItem
            extends MetisDataFieldItem, MetisIndexedItem {
    }

    /**
     * ValueSet object interface.
     */
    public interface MetisDataVersionedItem
            extends MetisDataTableItem {
        /**
         * Obtain Object Version Control.
         * @return the versionControl of the object
         */
        MetisDataVersionControl getVersionControl();
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
         * Obtain the list iterator.
         * @return the iterator
         */
        default Iterator<T> iterator() {
            return getUnderlyingList().iterator();
        }

        /**
         * Obtain the list iterator.
         * @param pIndex the list position
         * @return the iterator
         */
        default ListIterator<T> listIterator(final int pIndex) {
            return getUnderlyingList().listIterator(pIndex);
        }

        /**
         * Is the list empty?.
         * @return true/false
         */
        default boolean isEmpty() {
            return getUnderlyingList().isEmpty();
        }

        /**
         * Obtain the size of the list.
         * @return the size
         */
        default int size() {
            return getUnderlyingList().size();
        }

        /**
         * Clear the list.
         */
        default void clear() {
            getUnderlyingList().clear();
        }

        /**
         * Add the item.
         * @param pItem the item
         * @return true/false - was item added?
         */
        default boolean add(final T pItem) {
            return getUnderlyingList().add(pItem);
        }

        /**
         * Add the item at index.
         * @param pIndex the index
         * @param pItem the item
         */
        default void add(final int pIndex,
                         final T pItem) {
            getUnderlyingList().add(pIndex, pItem);
        }

        /**
         * Remove the item.
         * @param pItem the item
         * @return true/false - was item in list?
         */
        default boolean remove(final T pItem) {
            return getUnderlyingList().remove(pItem);
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

        /**
         * Put the value into the map.
         * @param pKey the key
         * @param pValue the value
         * @return the original value
         */
        default V put(final K pKey,
                      final V pValue) {
            return getUnderlyingMap().put(pKey, pValue);
        }

        /**
         * Obtain the value for the key.
         * @param pKey the key
         * @return the associated value
         */
        default V get(final K pKey) {
            return getUnderlyingMap().get(pKey);
        }
    }
}
