/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataKeySet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for ControlKey.
 */
public class PrometheusTableDataKeySet
        extends PrometheusTableDataItem<DataKeySet, CryptographyDataType> {
    /**
     * The name of the DataKeySet table.
     */
    protected static final String TABLE_NAME = DataKeySet.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected PrometheusTableDataKeySet(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addReferenceColumn(DataKeySet.FIELD_CONTROLKEY, PrometheusTableControlKeys.TABLE_NAME);
        myTableDef.addDateColumn(DataKeySet.FIELD_CREATEDATE);
        myTableDef.addBooleanColumn(DataKeySet.FIELD_HASHPRIME);
        myTableDef.addBinaryColumn(DataKeySet.FIELD_KEYSETDEF, DataKeySet.WRAPLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        setList(pData.getDataKeySets());
    }

    @Override
    protected DataValues<CryptographyDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<CryptographyDataType> myValues = getRowValues(DataKeySet.OBJECT_NAME);
        myValues.addValue(DataKeySet.FIELD_CONTROLKEY, myTableDef.getIntegerValue(DataKeySet.FIELD_CONTROLKEY));
        myValues.addValue(DataKeySet.FIELD_CREATEDATE, myTableDef.getDateValue(DataKeySet.FIELD_CREATEDATE));
        myValues.addValue(DataKeySet.FIELD_HASHPRIME, myTableDef.getBooleanValue(DataKeySet.FIELD_HASHPRIME));
        myValues.addValue(DataKeySet.FIELD_KEYSETDEF, myTableDef.getBinaryValue(DataKeySet.FIELD_KEYSETDEF));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DataKeySet pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (DataKeySet.FIELD_CONTROLKEY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKeyId());
        } else if (DataKeySet.FIELD_CREATEDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getCreationDate());
        } else if (DataKeySet.FIELD_HASHPRIME.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isHashPrime());
        } else if (DataKeySet.FIELD_KEYSETDEF.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSecuredKeySetDef());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
