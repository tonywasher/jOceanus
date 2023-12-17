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
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for Static Data Items. Each data type that represents Static Data should
 * extend this class.
 * @param <T> the data type
 */
public abstract class PrometheusXTableStaticData<T extends StaticDataItem>
        extends PrometheusXTableEncrypted<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected PrometheusXTableStaticData(final PrometheusXDataStore pDatabase,
                                         final String pTabName) {
        super(pDatabase, pTabName);

        /* Define the columns */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(StaticDataItem.FIELD_ENABLED);
        final PrometheusXColumnDefinition mySortCol = myTableDef.addIntegerColumn(StaticDataItem.FIELD_ORDER);
        myTableDef.addEncryptedColumn(StaticDataItem.FIELD_NAME, StaticDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(StaticDataItem.FIELD_DESC, StaticDataItem.DESCLEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (StaticDataItem.FIELD_ENABLED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.getEnabled());
        } else if (StaticDataItem.FIELD_ORDER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOrder());
        } else if (StaticDataItem.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (StaticDataItem.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected DataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues myValues = super.getRowValues(pName);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Add the info and return the new values */
        myValues.addValue(StaticDataItem.FIELD_NAME, myTableDef.getBinaryValue(StaticDataItem.FIELD_NAME));
        myValues.addValue(StaticDataItem.FIELD_DESC, myTableDef.getBinaryValue(StaticDataItem.FIELD_DESC));
        myValues.addValue(StaticDataItem.FIELD_ORDER, myTableDef.getIntegerValue(StaticDataItem.FIELD_ORDER));
        myValues.addValue(StaticDataItem.FIELD_ENABLED, myTableDef.getBooleanValue(StaticDataItem.FIELD_ENABLED));
        return myValues;
    }
}
