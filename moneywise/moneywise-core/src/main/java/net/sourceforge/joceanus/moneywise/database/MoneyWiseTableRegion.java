/*
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableEncrypted;

/**
 * TableEncrypted extension Region.
 */
public class MoneyWiseTableRegion
        extends PrometheusTableEncrypted<MoneyWiseRegion> {
    /**
     * The name of the region table.
     */
    protected static final String TABLE_NAME = MoneyWiseRegion.LIST_NAME;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     */
    protected MoneyWiseTableRegion(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_NAME, PrometheusDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_DESC, PrometheusDataItem.DESCLEN);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getRegions());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseRegion.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseRegion pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
