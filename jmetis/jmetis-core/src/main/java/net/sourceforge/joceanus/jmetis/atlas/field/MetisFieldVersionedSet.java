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
package net.sourceforge.joceanus.jmetis.atlas.field;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldSetDef;

/**
 * Metis Data FieldSet.
 * @param <T> the data type
 */
public class MetisFieldVersionedSet<T extends MetisFieldVersionedItem>
        extends MetisFieldSet<T> {
    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pStatic is this a static fieldSet?
     * @param pParent the parent fields
     */
    protected MetisFieldVersionedSet(final Class<T> pClazz,
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
    public static <T extends MetisFieldVersionedItem> MetisFieldVersionedSet<T> newVersionedFieldSet(final Class<T> pClazz) {
        /* Synchronise on class */
        synchronized (MetisFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisFieldSetDef myParent = lookUpParentFieldSet(pClazz);

            /* Create the new fieldSet and store into map */
            final MetisFieldVersionedSet<T> myFieldSet = new MetisFieldVersionedSet<>(pClazz, myParent, true);
            registerFieldSet(pClazz, myFieldSet);

            /* Return the new fieldSet */
            return myFieldSet;
        }
    }

    /**
     * Declare versioned string field.
     * @param pId the fieldId
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisFieldVersioned<T> declareStringField(final MetisFieldId pId,
                                                     final int pMaxLength) {
        return declareEqualityVersionedField(pId, MetisDataType.STRING, pMaxLength);
    }

    /**
     * Declare versioned charArray field.
     * @param pId the fieldId
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisFieldVersioned<T> declareCharArrayField(final MetisFieldId pId,
                                                        final int pMaxLength) {
        return declareEqualityVersionedField(pId, MetisDataType.CHARARRAY, pMaxLength);
    }

    /**
     * Declare versioned short field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareShortField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.SHORT, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned integer field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareIntegerField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.INTEGER, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned long field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareLongField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.LONG, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned boolean field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareBooleanField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.BOOLEAN, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned date field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareDateField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.DATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned money field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareMoneyField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.MONEY, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned price field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declarePriceField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.PRICE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned units field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareUnitsField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.UNITS, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned rate field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareRateField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.RATE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned ratio field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareRatioField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.RATIO, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned dilution field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareDilutionField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.DILUTION, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned dilutedPrice field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareDilutedPriceField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.DILUTEDPRICE, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned link field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareLinkField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.LINK, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned linkSet field.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareLinkSetField(final MetisFieldId pId) {
        return declareEqualityVersionedField(pId, MetisDataType.LINKSET, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned field not used for equality test.
     * @param pId the fieldId
     * @return the field
     */
    public MetisFieldVersioned<T> declareDerivedVersionedField(final MetisFieldId pId) {
        return declareVersionedField(pId, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisDataFieldEquality.DERIVED, MetisDataFieldStorage.VERSIONED);
    }

    /**
     * Declare versioned field used for equality test.
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisFieldVersioned<T> declareEqualityVersionedField(final MetisFieldId pId,
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
    public MetisFieldVersioned<T> declareEqualityVersionedField(final MetisFieldId pId,
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
    private MetisFieldVersioned<T> declareVersionedField(final MetisFieldId pId,
                                                         final MetisDataType pDataType,
                                                         final Integer pMaxLength,
                                                         final MetisDataFieldEquality pEquality,
                                                         final MetisDataFieldStorage pStorage) {
        /* Create the field */
        final MetisFieldVersioned<T> myField = new MetisFieldVersioned<>(this, pId, pDataType, pMaxLength, pEquality, pStorage);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}
