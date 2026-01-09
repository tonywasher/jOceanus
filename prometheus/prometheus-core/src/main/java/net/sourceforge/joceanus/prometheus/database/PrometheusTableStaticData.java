/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Database table class for Static Data Items. Each data type that represents Static Data should
 * extend this class.
 * @param <T> the data type
 */
public abstract class PrometheusTableStaticData<T extends PrometheusStaticDataItem>
        extends PrometheusTableEncrypted<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected PrometheusTableStaticData(final PrometheusDataStore pDatabase,
                                        final String pTabName) {
        super(pDatabase, pTabName);

        /* Define the columns */
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(PrometheusDataResource.STATICDATA_ENABLED);
        final PrometheusColumnDefinition mySortCol = myTableDef.addIntegerColumn(PrometheusDataResource.STATICDATA_SORT);
        myTableDef.addEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_NAME, PrometheusDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_DESC, PrometheusDataItem.DESCLEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusDataResource.STATICDATA_ENABLED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.getEnabled());
        } else if (PrometheusDataResource.STATICDATA_SORT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOrder());
        } else if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected PrometheusDataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final PrometheusDataValues myValues = super.getRowValues(pName);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Add the info and return the new values */
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_DESC));
        myValues.addValue(PrometheusDataResource.STATICDATA_SORT, myTableDef.getIntegerValue(PrometheusDataResource.STATICDATA_SORT));
        myValues.addValue(PrometheusDataResource.STATICDATA_ENABLED, myTableDef.getBooleanValue(PrometheusDataResource.STATICDATA_ENABLED));
        return myValues;
    }
}
