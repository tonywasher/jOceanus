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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldSetDef;

/**
 * Metis Data FieldSet.
 * @param <T> the data type
 */
public class MetisDataEosVersionedFieldSet<T extends MetisDataEosVersionedItem>
        extends MetisDataEosFieldSet<T> {
    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pStatic is this a static fieldSet?
     * @param pParent the parent fields
     */
    protected MetisDataEosVersionedFieldSet(final Class<T> pClazz,
                                            final MetisDataEosFieldSetDef pParent,
                                            final boolean pStatic) {
        /* Pass call on */
        super(pClazz, pParent, pStatic);
    }

    /**
     * Declare a static fieldSet.
     * @param <T> the itemType
     * @param pClazz the class of the fieldSet
     * @return the fieldSet.
     */
    public static <T extends MetisDataEosVersionedItem> MetisDataEosVersionedFieldSet<T> newVersionedFieldSet(final Class<T> pClazz) {
        /* Synchronise on class */
        synchronized (MetisDataEosFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisDataEosFieldSetDef myParent = lookUpFieldSet(pClazz);

            /* Create the new fieldSet and store into map */
            final MetisDataEosVersionedFieldSet<T> myFieldSet = new MetisDataEosVersionedFieldSet<>(pClazz, myParent, true);
            registerFieldSet(pClazz, myFieldSet);

            /* Return the new fieldSet */
            return myFieldSet;
        }
    }

    /**
     * Declare versioned field not used for equality test.
     * @param pId the fieldId
     * @return the field
     */
    public MetisDataEosVersionedField<T> declareDerivedVersionedField(final MetisFieldId pId) {
        return declareVersionedField(pId, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisDataFieldEquality.DERIVED, MetisDataFieldStorage.VERSIONED);
    }

    /**
     * Declare versioned field used for equality test.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisDataEosVersionedField<T> declareEqualityVersionedField(final MetisFieldId pId,
                                                                       final MetisDataType pDataType) {
        return declareEqualityVersionedField(pId, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned field used for equality test.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisDataEosVersionedField<T> declareEqualityVersionedField(final MetisFieldId pId,
                                                                       final MetisDataType pDataType,
                                                                       final Integer pMaxLength) {
        return declareVersionedField(pId, pDataType, pMaxLength, MetisDataFieldEquality.EQUALITY, MetisDataFieldStorage.VERSIONED);
    }

    /**
     * Declare versioned field.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @param pStorage the field storage type
     * @return the field
     */
    private MetisDataEosVersionedField<T> declareVersionedField(final MetisFieldId pId,
                                                                final MetisDataType pDataType,
                                                                final Integer pMaxLength,
                                                                final MetisDataFieldEquality pEquality,
                                                                final MetisDataFieldStorage pStorage) {
        /* Create the field */
        final MetisDataEosVersionedField<T> myField = new MetisDataEosVersionedField<>(this, pId, pDataType, pMaxLength, pEquality, pStorage);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}
