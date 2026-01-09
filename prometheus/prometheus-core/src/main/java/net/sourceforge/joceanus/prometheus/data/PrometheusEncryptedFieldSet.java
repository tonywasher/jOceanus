/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;

/**
 * Prometheus Data fieldSet.
 * @param <T> the data type
 */
public class PrometheusEncryptedFieldSet<T extends PrometheusEncryptedDataItem>
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
    public static <T extends PrometheusEncryptedDataItem> PrometheusEncryptedFieldSet<T> newEncryptedFieldSet(final Class<T> pClazz) {
        /* Synchronise on class */
        synchronized (MetisFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisFieldSetDef myParent = lookUpParentFieldSet(pClazz);

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
    public PrometheusEncryptedField<T> declareEncryptedStringField(final MetisDataFieldId pId,
                                                                   final int pMaxLength) {
        return declareEncryptedField(pId, MetisDataType.STRING, pMaxLength);
    }

    /**
     * Declare encrypted versioned charArray field.
     * @param pId the fieldId
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedCharArrayField(final MetisDataFieldId pId,
                                                                      final int pMaxLength) {
        return declareEncryptedField(pId, MetisDataType.CHARARRAY, pMaxLength);
    }

    /**
     * Declare encrypted versioned short field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedShortField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.SHORT, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned integer field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedIntegerField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.INTEGER, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned long field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedLongField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.LONG, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned boolean field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedBooleanField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.BOOLEAN, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned date field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedDateField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.DATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned money field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedMoneyField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.MONEY, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned price field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedPriceField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.PRICE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned units field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedUnitsField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.UNITS, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned rate field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedRateField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.RATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned ratio field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedRatioField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.RATIO, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned context field.
     * @param pId the fieldId
     * @return the field
     */
    public PrometheusEncryptedField<T> declareEncryptedContextField(final MetisDataFieldId pId) {
        return declareEncryptedField(pId, MetisDataType.CONTEXT, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted versioned field.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    private PrometheusEncryptedField<T> declareEncryptedField(final MetisDataFieldId pId,
                                                              final MetisDataType pDataType,
                                                              final Integer pMaxLength) {
        return declareDataField(pId, pDataType, pMaxLength);
    }

    /**
     * Declare field.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    private PrometheusEncryptedField<T> declareDataField(final MetisDataFieldId pId,
                                                         final MetisDataType pDataType,
                                                         final Integer pMaxLength) {
        /* Create the field */
        final PrometheusEncryptedField<T> myField = new PrometheusEncryptedField<>(this, pId, pDataType, pMaxLength);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}
