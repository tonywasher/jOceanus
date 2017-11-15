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
package net.sourceforge.joceanus.jprometheus.atlas.field;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;

/**
 * Prometheus Data fieldSet.
 * @param <T> the data type
 */
public class PrometheusEncryptedFieldSet<T extends PrometheusEncryptedItem>
        extends MetisFieldVersionedSet<T> {
    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pStatic is this a static fieldSet?
     * @param pParent the parent fields
     */
    PrometheusEncryptedFieldSet(final Class<T> pClazz,
                                final MetisFieldSetDef pParent,
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
        synchronized (MetisFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisFieldSetDef myParent = lookUpFieldSet(pClazz);

            /* Create the new fieldSet and store into map */
            final PrometheusEncryptedFieldSet<T> myFieldSet = new PrometheusEncryptedFieldSet<>(pClazz, myParent, true);
            registerFieldSet(pClazz, myFieldSet);

            /* Return the new fieldSet */
            return myFieldSet;
        }
    }

    /**
     * Declare encrypted versioned string field.
     * @param pId the fieldId
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedStringField(final MetisFieldId pId,
                                                                   final int pMaxLength) {
        return declareEqualityEncryptedField(pId, MetisDataType.STRING, pMaxLength);
    }

    /**
     * Declare encrypted versioned charArray field.
     * @param pId the fieldId
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedCharArrayField(final MetisFieldId pId,
                                                                      final int pMaxLength) {
        return declareEqualityEncryptedField(pId, MetisDataType.CHARARRAY, pMaxLength);
    }

    /**
     * Declare encrypted versioned short field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedShortField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.SHORT, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned integer field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedIntegerField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.INTEGER, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned long field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedLongField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.LONG, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned boolean field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedBooleanField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.BOOLEAN, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned date field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedDateField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.DATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned money field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedMoneyField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.MONEY, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned price field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedPriceField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.PRICE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned units field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedUnitsField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.UNITS, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned rate field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedRateField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.RATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned ratio field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedRatioField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.RATIO, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned dilution field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedDilutionField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.DILUTION, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned dilutedPrice field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedDilutedPriceField(final MetisFieldId pId) {
        return declareEqualityEncryptedField(pId, MetisDataType.DILUTEDPRICE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned field used for equality test.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    private PrometheusEncryptedField<T> declareEqualityEncryptedField(final MetisFieldId pId,
                                                                      final MetisDataType pDataType,
                                                                      final Integer pMaxLength) {
        return declareDataField(pId, pDataType, pMaxLength, MetisDataFieldEquality.EQUALITY);
    }

    /**
     * Declare field.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @return the field
     */
    private PrometheusEncryptedField<T> declareDataField(final MetisFieldId pId,
                                                         final MetisDataType pDataType,
                                                         final Integer pMaxLength,
                                                         final MetisDataFieldEquality pEquality) {
        /* Create the field */
        final PrometheusEncryptedField<T> myField = new PrometheusEncryptedField<>(this, pId, pDataType, pMaxLength, pEquality, MetisDataFieldStorage.VERSIONED);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}