/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.database;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend
 * this class.
 * @param <T> the data type
 */
public abstract class PrometheusTableEncrypted<T extends PrometheusEncryptedDataItem>
        extends PrometheusTableDataItem<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected PrometheusTableEncrypted(final PrometheusDataStore pDatabase,
                                       final String pTabName) {
        super(pDatabase, pTabName);
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addReferenceColumn(PrometheusCryptographyDataType.DATAKEYSET, PrometheusTableDataKeySet.TABLE_NAME);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusCryptographyDataType.DATAKEYSET.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDataKeySetId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected PrometheusDataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final PrometheusDataValues myValues = super.getRowValues(pName);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Add the control id and return the new values */
        myValues.addValue(PrometheusCryptographyDataType.DATAKEYSET, myTableDef.getIntegerValue(PrometheusCryptographyDataType.DATAKEYSET));
        return myValues;
    }
}
