/*
 * Metis: Java Data Framework
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.metis.field;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataDeletableItem;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldSetCtl;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;

/**
 * Metis Version Interfaces.
 */
public interface MetisFieldVersion {
    /**
     * Updatable Item interface.
     */
    interface MetisFieldUpdatableItem {
        /**
         * Push current values into history buffer ready for changes to be made.
         */
        void pushHistory();

        /**
         * Obtain the next version for the history.
         *
         * @return the next version
         */
        int getNextVersion();

        /**
         * Remove the last changes for the history buffer and restore values from it.
         */
        void popHistory();

        /**
         * Check to see whether any changes were made. If no changes were made remove last saved
         * history since it is not needed.
         *
         * @return <code>true</code> if changes were made, <code>false</code> otherwise
         */
        boolean checkForHistory();

        /**
         * Determine whether a particular field has Errors.
         *
         * @param pField the particular field
         * @return <code>true/false</code>
         */
        default boolean hasErrors(final MetisDataFieldId pField) {
            return false;
        }

        /**
         * Obtain error details for a field.
         *
         * @param pField the field
         * @return the error details
         */
        String getFieldErrors(MetisDataFieldId pField);

        /**
         * Obtain error details for a set of fields.
         *
         * @param pFields the fields
         * @return the error details
         */
        String getFieldErrors(MetisDataFieldId[] pFields);

        /**
         * Is the item editable?
         *
         * @return true/false
         */
        default boolean isEditable() {
            return false;
        }

        /**
         * Obtain Object ValueSet.
         *
         * @return the ValueSet of the object
         */
        MetisFieldVersionValuesCtl getValues();

        /**
         * Obtain original Object ValueSet.
         *
         * @return the ValueSet of the object
         */
        MetisFieldVersionValuesCtl getOriginalValues();

        /**
         * Obtain Object ValueSet History.
         *
         * @return the ValueSet of the object
         */
        MetisFieldVersionHistoryCtl getValuesHistory();

        /**
         * Should we skip a ValueSet object?.
         *
         * @param pField the field
         * @return true/false
         */
        default boolean skipField(final MetisDataFieldId pField) {
            return false;
        }
    }

    /**
     * Versioned Field.
     */
    interface MetisFieldVersionedCtl {
    }

    /**
     * Versioned Set.
     *
     * @param <T> the dataType
     */
    interface MetisFieldVersionedSetCtl<T extends MetisFieldVersionedItemCtl>
            extends MetisFieldSetCtl<T> {
    }

    /**
     * Version History.
     */
    interface MetisFieldVersionHistoryCtl
            extends MetisFieldItem {
    }

    /**
     * Version Values.
     */
    interface MetisFieldVersionValuesCtl {
    }

    /**
     * Versioned Item.
     */
    interface MetisFieldVersionedItemCtl
            extends MetisFieldTableItem, MetisDataDeletableItem, MetisFieldUpdatableItem {
    }
}
