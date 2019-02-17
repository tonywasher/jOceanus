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
import net.sourceforge.joceanus.jprometheus.lethe.data.DataKey;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for DataKey.
 */
public class PrometheusTableDataKeys
        extends PrometheusTableDataItem<DataKey, CryptographyDataType> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = DataKey.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected PrometheusTableDataKeys(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addReferenceColumn(DataKey.FIELD_KEYSET, PrometheusTableDataKeySet.TABLE_NAME);
        myTableDef.addBooleanColumn(DataKey.FIELD_HASHPRIME);
        myTableDef.addIntegerColumn(DataKey.FIELD_KEYTYPE);
        myTableDef.addBinaryColumn(DataKey.FIELD_KEYDEF, DataKey.KEYLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        setList(pData.getDataKeys());
    }

    @Override
    protected DataValues<CryptographyDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<CryptographyDataType> myValues = getRowValues(DataKey.OBJECT_NAME);
        myValues.addValue(DataKey.FIELD_KEYSET, myTableDef.getIntegerValue(DataKey.FIELD_KEYSET));
        myValues.addValue(DataKey.FIELD_HASHPRIME, myTableDef.getBooleanValue(DataKey.FIELD_HASHPRIME));
        myValues.addValue(DataKey.FIELD_KEYTYPE, myTableDef.getIntegerValue(DataKey.FIELD_KEYTYPE));
        myValues.addValue(DataKey.FIELD_KEYDEF, myTableDef.getBinaryValue(DataKey.FIELD_KEYDEF));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DataKey pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (DataKey.FIELD_KEYSET.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDataKeySetId());
        } else if (DataKey.FIELD_HASHPRIME.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isHashPrime());
        } else if (DataKey.FIELD_KEYTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getKeyTypeId());
        } else if (DataKey.FIELD_KEYDEF.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSecuredKeyDef());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
