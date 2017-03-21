/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;

/**
 * Prometheus Data fieldSet.
 */
public class PrometheusDataFieldSet
        extends MetisDataFieldSet {
    /**
     * Constructor.
     * @param pName the name of the item
     */
    public PrometheusDataFieldSet(final String pName) {
        this(pName, null);
    }

    /**
     * Constructor.
     * @param pParent the parent fields
     */
    public PrometheusDataFieldSet(final PrometheusDataFieldSet pParent) {
        this(pParent.getName(), pParent);
    }

    /**
     * Constructor.
     * @param pName the name of the item
     * @param pParent the parent fields
     */
    public PrometheusDataFieldSet(final String pName,
                                  final PrometheusDataFieldSet pParent) {
        /* Initialise underlying class */
        super(pName, pParent);
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public PrometheusDataField declareEqualityEncryptedField(final String pName,
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
    public PrometheusDataField declareEqualityEncryptedField(final String pName,
                                                             final MetisDataType pDataType,
                                                             final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.EQUALITY);
    }

    /**
     * Declare encrypted valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public PrometheusDataField declareComparisonEncryptedField(final String pName,
                                                               final MetisDataType pDataType) {
        return declareComparisonEncryptedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public PrometheusDataField declareComparisonEncryptedField(final String pName,
                                                               final MetisDataType pDataType,
                                                               final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.COMPARISON);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @return the field
     */
    private synchronized PrometheusDataField declareDataField(final String pName,
                                                              final MetisDataType pDataType,
                                                              final Integer pMaxLength,
                                                              final MetisFieldEquality pEquality) {
        /* Check the name */
        checkUniqueName(pName);

        /* Create the field */
        PrometheusDataField myField = new PrometheusDataField(this, pName, pDataType, pMaxLength, pEquality, MetisFieldStorage.VERSIONED);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }
}
