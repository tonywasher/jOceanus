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
package net.sourceforge.joceanus.jmetis.eos.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FieldItem Interface.
 */
public interface MetisDataEosFieldItem
        extends MetisDataObjectFormat {
    /**
     * Obtain the fieldSet.
     * @return the fieldSet
     */
    MetisDataEosFieldSetDef getDataFieldSet();

    /**
     * Field interface.
     */
    interface MetisDataEosFieldDef {
        /**
         * Obtain the id of the field.
         * @return the name of the field.
         */
        MetisFieldId getFieldId();

        /**
         * Obtain the index of the field.
         * @return the index of the field.
         */
        Integer getIndex();

        /**
         * Get the dataType of the field.
         * @return the dataType
         */
        MetisDataType getDataType();

        /**
         * Get the maximum length.
         * @return the maxLength
         */
        Integer getMaxLength();

        /**
         * Obtain the equality type.
         * @return equalityType
         */
        MetisDataFieldEquality getEquality();

        /**
         * Obtain the storage type.
         * @return storageType
         */
        MetisDataFieldStorage getStorage();

        /**
         * Obtain the value of a field.
         * @param pObject the object
         * @return the value
         */
        Object getFieldValue(Object pObject);

        /**
         * Obtain the value of a field cast to a particular class.
         * @param <X> the value type
         * @param pObject the object
         * @param pClazz the class of the value
         * @return the value
         */
        <X> X getFieldValue(Object pObject,
                            Class<X> pClazz);
    }

    /**
     * Versioned Field interface.
     */
    interface MetisDataEosVersionedFieldDef
            extends MetisDataEosFieldDef {
        /**
         * Set the value of a field.
         * @param pObject the object
         * @param pValue the new value
         * @throws OceanusException on error
         */
        void setFieldValue(Object pObject,
                           Object pValue) throws OceanusException;

        /**
         * Set the value of a field (without checks).
         * @param pObject the object
         * @param pValue the new value
         */
        void setFieldUncheckedValue(Object pObject,
                                    Object pValue);
    }

    /**
     * FieldSet interface.
     */
    interface MetisDataEosFieldSetDef {
        /**
         * Obtain the name of the fieldSet.
         * @return the name of the fieldSet.
         */
        String getName();

        /**
         * Obtain the number of versioned fields.
         * @return the number of fields.
         */
        Integer getNumVersioned();

        /**
         * Obtain the iterator over the fields.
         * @return the iterator
         */
        Iterator<MetisDataEosFieldDef> fieldIterator();

        /**
         * Does the item have versioned values?
         * @return true/false
         */
        boolean hasVersions();

        /**
         * Lock the fieldSet.
         */
        void setLocked();

        /**
         * Obtain field from fieldId.
         * @param pId the fieldId.
         * @return the corresponding field
         * @throws IllegalArgumentException if name is not present
         */
        MetisDataEosFieldDef getField(MetisFieldId pId);
    }

    /**
     * Table Item.
     */
    public interface MetisDataEosTableItem
            extends MetisDataEosFieldItem, MetisIndexedItem {
    }
}
