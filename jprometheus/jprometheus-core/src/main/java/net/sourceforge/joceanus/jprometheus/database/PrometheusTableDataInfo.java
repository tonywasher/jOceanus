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
package net.sourceforge.joceanus.jprometheus.database;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for DataInfo Items. Each data type that represents DataInfo should extend
 * this class.
 * @param <T> the data type
 */
public abstract class PrometheusTableDataInfo<T extends PrometheusDataInfoItem>
        extends PrometheusTableEncrypted<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     * @param pInfoTable the InfoTypes table name
     * @param pOwnerTable the Owner table name
     */
    protected PrometheusTableDataInfo(final PrometheusDataStore pDatabase,
                                      final String pTabName,
                                      final String pInfoTable,
                                      final String pOwnerTable) {
        super(pDatabase, pTabName);

        /* Define the columns */
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addReferenceColumn(PrometheusDataResource.DATAINFO_TYPE, pInfoTable);
        myTableDef.addReferenceColumn(PrometheusDataResource.DATAINFO_OWNER, pOwnerTable);
        myTableDef.addEncryptedColumn(PrometheusDataResource.DATAINFO_VALUE, PrometheusDataInfoItem.DATALEN);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (PrometheusDataResource.DATAINFO_TYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getInfoTypeId());
        } else if (PrometheusDataResource.DATAINFO_OWNER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOwnerId());
        } else if (PrometheusDataResource.DATAINFO_VALUE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getValueBytes());
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
        myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myTableDef.getIntegerValue(PrometheusDataResource.DATAINFO_TYPE));
        myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, myTableDef.getIntegerValue(PrometheusDataResource.DATAINFO_OWNER));
        myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, myTableDef.getBinaryValue(PrometheusDataResource.DATAINFO_VALUE));
        return myValues;
    }
}
