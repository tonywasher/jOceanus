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
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;

/**
 * Data object formatting and interfaces.
 * @author Tony Washer
 */
public final class MetisDataObject {
    /**
     * Format object interface.
     */
    @FunctionalInterface
    public interface MetisDataFormat {
        /**
         * Obtain Object summary.
         * @return the display summary of the object
         */
        String formatObject();
    }

    /**
     * Detail object interface.
     */
    public interface MetisDataContents
            extends MetisDataFormat {
        /**
         * Obtain the Report Fields.
         * @return the report fields
         */
        MetisFields getDataFields();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(MetisField pField);
    }

    /**
     * ValueSet object interface.
     */
    public interface MetisDataValues
            extends MetisDataContents {
        /**
         * Obtain Object ValueSet.
         * @return the ValueSet of the object
         */
        MetisValueSet getValueSet();

        /**
         * Obtain Object ValueSet History.
         * @return the ValueSet of the object
         */
        MetisValueSetHistory getValueSetHistory();

        /**
         * Should we skip a ValueSet object?.
         * @param pField the field
         * @return true/false
         */
        boolean skipField(MetisField pField);

        /**
         * Declare the valueSet as active.
         * @param pValues the active values
         */
        void declareValues(MetisValueSet pValues);
    }

    /**
     * Deletable Item interface.
     */
    public interface MetisDataDeletable {
        /**
         * Is this active?
         * @return true/false
         */
        boolean isActive();

        /**
         * Is this item deleted?
         * @return true/false
         */
        boolean isDeleted();

        /**
         * Set the deleted flags for the item.
         * @param pFlag true/false
         */
        void setDeleted(boolean pFlag);
    }

    /**
     * Updateable Item interface.
     */
    public interface MetisDataUpdatable {
        /**
         * Push current values into history buffer ready for changes to be made.
         */
        void pushHistory();

        /**
         * Remove the last changes for the history buffer and restore values from it.
         */
        void popHistory();

        /**
         * Check to see whether any changes were made. If no changes were made remove last saved
         * history since it is not needed.
         * @return <code>true</code> if changes were made, <code>false</code> otherwise
         */
        boolean checkForHistory();

        /**
         * Determine whether a particular field has Errors.
         * @param pField the particular field
         * @return <code>true/false</code>
         */
        default boolean hasErrors(final MetisField pField) {
            return false;
        }
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

    /**
     * Difference interface.
     */
    @FunctionalInterface
    public interface MetisDataDiffers {
        /**
         * Test for difference with another object.
         * @param pThat the other object
         * @return the difference
         */
        MetisDifference differs(Object pThat);
    }

    /**
     * Difference class.
     */
    public static class MetisDataDifference {
        /**
         * The object itself.
         */
        private final Object theObject;

        /**
         * The difference.
         */
        private final MetisDifference theDifference;

        /**
         * Constructor.
         * @param pObject the object
         * @param pDifference the difference
         */
        public MetisDataDifference(final Object pObject,
                                   final MetisDifference pDifference) {
            theObject = pObject;
            theDifference = pDifference;
        }

        /**
         * Obtain the object.
         * @return the object
         */
        public Object getObject() {
            return theObject;
        }

        /**
         * Obtain the difference.
         * @return the difference
         */
        public MetisDifference getDifference() {
            return theDifference;
        }
    }
}
