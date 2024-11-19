/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.database;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.prometheus.data.PrometheusControlKey;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Database table class for ControlKey.
 */
public class PrometheusTableControlKeys
        extends PrometheusTableDataItem<PrometheusControlKey> {
    /**
     * The name of the ControlKeys table.
     */
    protected static final String TABLE_NAME = PrometheusControlKey.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected PrometheusTableControlKeys(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addDateColumn(PrometheusDataResource.CONTROLKEY_CREATION);
        myTableDef.addBinaryColumn(PrometheusDataResource.CONTROLKEY_LOCKBYTES, PrometheusControlKey.LOCKLEN);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        setList(pData.getControlKeys());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(PrometheusControlKey.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.CONTROLKEY_CREATION, myTableDef.getDateValue(PrometheusDataResource.CONTROLKEY_CREATION));
        myValues.addValue(PrometheusDataResource.CONTROLKEY_LOCKBYTES, myTableDef.getBinaryValue(PrometheusDataResource.CONTROLKEY_LOCKBYTES));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final PrometheusControlKey pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusDataResource.CONTROLKEY_CREATION.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getCreationDate());
        } else if (PrometheusDataResource.CONTROLKEY_LOCKBYTES.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getLockBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
