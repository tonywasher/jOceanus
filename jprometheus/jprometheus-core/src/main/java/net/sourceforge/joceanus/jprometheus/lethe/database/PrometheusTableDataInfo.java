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
package net.sourceforge.joceanus.jprometheus.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for DataInfo Items. Each data type that represents DataInfo should extend
 * this class.
 * @param <T> the data type
 */
public abstract class PrometheusTableDataInfo<T extends DataInfoItem<T>>
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
        myTableDef.addReferenceColumn(DataInfoItem.FIELD_INFOTYPE, pInfoTable);
        myTableDef.addReferenceColumn(DataInfoItem.FIELD_OWNER, pOwnerTable);
        myTableDef.addEncryptedColumn(DataInfoItem.FIELD_VALUE, DataInfoItem.DATALEN);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (DataInfoItem.FIELD_INFOTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getInfoTypeId());
        } else if (DataInfoItem.FIELD_OWNER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOwnerId());
        } else if (DataInfoItem.FIELD_VALUE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getValueBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected DataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues myValues = super.getRowValues(pName);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Add the info and return the new values */
        myValues.addValue(DataInfoItem.FIELD_INFOTYPE, myTableDef.getIntegerValue(DataInfoItem.FIELD_INFOTYPE));
        myValues.addValue(DataInfoItem.FIELD_OWNER, myTableDef.getIntegerValue(DataInfoItem.FIELD_OWNER));
        myValues.addValue(DataInfoItem.FIELD_VALUE, myTableDef.getBinaryValue(DataInfoItem.FIELD_VALUE));
        return myValues;
    }
}
