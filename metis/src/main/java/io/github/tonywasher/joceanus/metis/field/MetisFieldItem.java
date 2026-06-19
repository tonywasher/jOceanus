/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataIndexedItem;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

import java.util.Iterator;

/**
 * FieldItem Interface.
 */
public interface MetisFieldItem
        extends MetisDataObjectFormat {
    /**
     * Obtain the fieldSet.
     *
     * @return the fieldSet
     */
    MetisFieldSetDef getDataFieldSet();

    /**
     * Field interface.
     */
    interface MetisFieldDef {
        /**
         * Obtain the id of the field.
         *
         * @return the name of the field.
         */
        MetisDataFieldId getFieldId();

        /**
         * Get the dataType of the field.
         *
         * @return the dataType
         */
        MetisDataType getDataType();

        /**
         * Get the maximum length.
         *
         * @return the maxLength
         */
        Integer getMaxLength();

        /**
         * Is the field calculated?
         *
         * @return true/false
         */
        boolean isCalculated();

        /**
         * Obtain the value of a field.
         *
         * @param pObject the object
         * @return the value
         */
        Object getFieldValue(Object pObject);

        /**
         * Obtain the value of a field cast to a particular class.
         *
         * @param <X>     the value type
         * @param pObject the object
         * @param pClazz  the class of the value
         * @return the value
         */
        <X> X getFieldValue(Object pObject,
                            Class<X> pClazz);
    }

    /**
     * Versioned Field interface.
     */
    interface MetisFieldVersionedDef
            extends MetisFieldDef {
        /**
         * Obtain the index of the field.
         *
         * @return the index of the field.
         */
        Integer getIndex();

        /**
         * Is this an equality field?
         *
         * @return true/false
         */
        boolean isEquality();

        /**
         * Set the value of a field.
         *
         * @param pObject the object
         * @param pValue  the new value
         * @throws OceanusException on error
         */
        void setFieldValue(Object pObject,
                           Object pValue) throws OceanusException;

        /**
         * Set the value of a field (without checks).
         *
         * @param pObject the object
         * @param pValue  the new value
         */
        void setFieldUncheckedValue(Object pObject,
                                    Object pValue);
    }

    /**
     * FieldSet interface.
     */
    interface MetisFieldSetDef {
        /**
         * Obtain the name of the fieldSet.
         *
         * @return the name of the fieldSet.
         */
        String getName();

        /**
         * Obtain the number of versioned fields.
         *
         * @return the number of fields.
         */
        Integer getNumVersioned();

        /**
         * Obtain the iterator over the fields.
         *
         * @return the iterator
         */
        Iterator<MetisFieldDef> fieldIterator();

        /**
         * Does the item have versioned values?
         *
         * @return true/false
         */
        boolean hasVersions();

        /**
         * Does the item have link values?
         *
         * @return true/false
         */
        default boolean hasLinks() {
            return false;
        }

        /**
         * Does the item have pairedLink values?
         *
         * @return true/false
         */
        default boolean hasPairedLinks() {
            return false;
        }

        /**
         * Lock the fieldSet.
         */
        void setLocked();

        /**
         * Obtain the itemType.
         *
         * @return the itemType
         */
        MetisFieldItemType getItemType();

        /**
         * Obtain field from fieldId.
         *
         * @param pId the fieldId.
         * @return the corresponding field
         * @throws IllegalArgumentException if name is not present
         */
        MetisFieldDef getField(MetisDataFieldId pId);
    }

    /**
     * Field Item Type.
     */
    interface MetisFieldItemType {
        /**
         * Obtain the item name.
         *
         * @return the item name
         */
        String getItemName();
    }

    /**
     * Table Item.
     */
    interface MetisFieldTableItem
            extends MetisFieldItem, MetisDataIndexedItem {
    }

    /**
     * FieldSet.
     *
     * @param <T> the data type
     */
    interface MetisFieldSetCtl<T extends MetisFieldItem> {
        /**
         * Obtain the field class.
         *
         * @return the field class
         */
        Class<T> getFieldClass();

        /**
         * Obtain next value index.
         *
         * @return the next index
         */
        Integer getNextIndex();
    }
}
