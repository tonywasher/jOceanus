/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for Static Data Items. Each data type that represents Static Data should
 * extend this class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class PrometheusTableStaticData<T extends StaticData<T, ?, E>, E extends Enum<E>>
        extends PrometheusTableEncrypted<T, E> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected PrometheusTableStaticData(final PrometheusDataStore<?> pDatabase,
                                        final String pTabName) {
        super(pDatabase, pTabName);

        /* Define the columns */
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(StaticData.FIELD_ENABLED);
        final PrometheusColumnDefinition mySortCol = myTableDef.addIntegerColumn(StaticData.FIELD_ORDER);
        myTableDef.addEncryptedColumn(StaticData.FIELD_NAME, StaticData.NAMELEN);
        myTableDef.addNullEncryptedColumn(StaticData.FIELD_DESC, StaticData.DESCLEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (StaticData.FIELD_ENABLED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.getEnabled());
        } else if (StaticData.FIELD_ORDER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOrder());
        } else if (StaticData.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (StaticData.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected DataValues<E> getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues<E> myValues = super.getRowValues(pName);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Add the info and return the new values */
        myValues.addValue(StaticData.FIELD_NAME, myTableDef.getBinaryValue(StaticData.FIELD_NAME));
        myValues.addValue(StaticData.FIELD_DESC, myTableDef.getBinaryValue(StaticData.FIELD_DESC));
        myValues.addValue(StaticData.FIELD_ORDER, myTableDef.getIntegerValue(StaticData.FIELD_ORDER));
        myValues.addValue(StaticData.FIELD_ENABLED, myTableDef.getBooleanValue(StaticData.FIELD_ENABLED));
        return myValues;
    }
}
