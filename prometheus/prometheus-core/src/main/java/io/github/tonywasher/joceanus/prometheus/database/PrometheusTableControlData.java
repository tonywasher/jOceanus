/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusControlData;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;

/**
 * Database table class for ControlData.
 */
public class PrometheusTableControlData
        extends PrometheusTableDataItem<PrometheusControlData> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = PrometheusControlData.LIST_NAME;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     */
    protected PrometheusTableControlData(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addIntegerColumn(PrometheusDataResource.CONTROLDATA_VERSION);
        myTableDef.addReferenceColumn(PrometheusCryptographyDataType.CONTROLKEY, PrometheusTableControlKeys.TABLE_NAME);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        setList(pData.getControlData());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(PrometheusControlData.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.CONTROLDATA_VERSION, myTableDef.getIntegerValue(PrometheusDataResource.CONTROLDATA_VERSION));
        myValues.addValue(PrometheusCryptographyDataType.CONTROLKEY, myTableDef.getIntegerValue(PrometheusCryptographyDataType.CONTROLKEY));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final PrometheusControlData pItem,
                                 final MetisDataFieldId pField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusDataResource.CONTROLDATA_VERSION.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getDataVersion());
        } else if (PrometheusCryptographyDataType.CONTROLKEY.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getControlKeyId());
        } else {
            super.setFieldValue(pItem, pField);
        }
    }
}
