/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2020 Tony Washer
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
import net.sourceforge.joceanus.jprometheus.lethe.data.ControlKey;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for ControlKey.
 */
public class PrometheusTableControlKeys
        extends PrometheusTableDataItem<ControlKey, CryptographyDataType> {
    /**
     * The name of the ControlKeys table.
     */
    protected static final String TABLE_NAME = ControlKey.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected PrometheusTableControlKeys(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addBooleanColumn(ControlKey.FIELD_HASHPRIME);
        myTableDef.addBinaryColumn(ControlKey.FIELD_PRIMEBYTES, ControlKey.HASHLEN);
        myTableDef.addNullBinaryColumn(ControlKey.FIELD_ALTBYTES, ControlKey.HASHLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        setList(pData.getControlKeys());
    }

    @Override
    protected DataValues<CryptographyDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<CryptographyDataType> myValues = getRowValues(ControlKey.OBJECT_NAME);
        myValues.addValue(ControlKey.FIELD_HASHPRIME, myTableDef.getBooleanValue(ControlKey.FIELD_HASHPRIME));
        myValues.addValue(ControlKey.FIELD_PRIMEBYTES, myTableDef.getBinaryValue(ControlKey.FIELD_PRIMEBYTES));
        myValues.addValue(ControlKey.FIELD_ALTBYTES, myTableDef.getBinaryValue(ControlKey.FIELD_ALTBYTES));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final ControlKey pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (ControlKey.FIELD_HASHPRIME.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isHashPrime());
        } else if (ControlKey.FIELD_PRIMEBYTES.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPrimeHashBytes());
        } else if (ControlKey.FIELD_ALTBYTES.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAltHashBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
