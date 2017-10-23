/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.eos.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldSetDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosVersionedFieldSet;

/**
 * Prometheus Data fieldSet.
 * @param <T> the data type
 */
public class PrometheusEncryptedFieldSet<T extends PrometheusEncryptedItem>
        extends MetisDataEosVersionedFieldSet<T> {
    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pStatic is this a static fieldSet?
     * @param pParent the parent fields
     */
    PrometheusEncryptedFieldSet(final Class<T> pClazz,
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
    public static <T extends PrometheusEncryptedItem> PrometheusEncryptedFieldSet<T> newEncryptedFieldSet(final Class<T> pClazz) {
        /* Synchronise on class */
        synchronized (MetisDataEosFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisDataEosFieldSetDef myParent = lookUpFieldSet(pClazz);

            /* Create the new fieldSet and store into map */
            final PrometheusEncryptedFieldSet<T> myFieldSet = new PrometheusEncryptedFieldSet<>(pClazz, myParent, true);
            registerFieldSet(pClazz, myFieldSet);

            /* Return the new fieldSet */
            return myFieldSet;
        }
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEqualityEncryptedField(final String pName,
                                                                     final MetisDataType pDataType) {
        return declareEqualityEncryptedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEqualityEncryptedField(final String pName,
                                                                     final MetisDataType pDataType,
                                                                     final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisDataFieldEquality.EQUALITY);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @return the field
     */
    private PrometheusEncryptedField<T> declareDataField(final String pName,
                                                         final MetisDataType pDataType,
                                                         final Integer pMaxLength,
                                                         final MetisDataFieldEquality pEquality) {
        /* Create the field */
        final PrometheusEncryptedField<T> myField = new PrometheusEncryptedField<>(this, pName, pDataType, pMaxLength, pEquality, MetisDataFieldStorage.VERSIONED);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}
