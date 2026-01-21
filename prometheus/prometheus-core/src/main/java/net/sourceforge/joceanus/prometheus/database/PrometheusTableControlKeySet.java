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
package net.sourceforge.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.prometheus.data.PrometheusControlKeySet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataKeySet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;

/**
 * Database table class for ControlKey.
 */
public class PrometheusTableControlKeySet
        extends PrometheusTableDataItem<PrometheusControlKeySet> {
    /**
     * The name of the DataKeySet table.
     */
    protected static final String TABLE_NAME = PrometheusControlKeySet.LIST_NAME;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     */
    protected PrometheusTableControlKeySet(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addReferenceColumn(PrometheusCryptographyDataType.CONTROLKEY, PrometheusTableControlKeys.TABLE_NAME);
        myTableDef.addBinaryColumn(PrometheusDataResource.KEYSET_KEYSETDEF, PrometheusDataKeySet.WRAPLEN);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        setList(pData.getControlKeySets());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(PrometheusDataKeySet.OBJECT_NAME);
        myValues.addValue(PrometheusCryptographyDataType.CONTROLKEY, myTableDef.getIntegerValue(PrometheusCryptographyDataType.CONTROLKEY));
        myValues.addValue(PrometheusDataResource.KEYSET_KEYSETDEF, myTableDef.getBinaryValue(PrometheusDataResource.KEYSET_KEYSETDEF));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final PrometheusControlKeySet pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusCryptographyDataType.CONTROLKEY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKeyId());
        } else if (PrometheusDataResource.KEYSET_KEYSETDEF.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSecuredKeySetDef());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
