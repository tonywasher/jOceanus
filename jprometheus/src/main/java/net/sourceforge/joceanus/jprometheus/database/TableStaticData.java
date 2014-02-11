/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for Static Data Items. Each data type that represents Static Data should extend this class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class TableStaticData<T extends StaticData<T, ?, E>, E extends Enum<E>>
        extends TableEncrypted<T, E> {
    /**
     * The list of items for this table.
     */
    private DataList<T, E> theList = null;

    @Override
    protected void setList(final DataList<T, E> pList) {
        super.setList(pList);
        theList = pList;
    }

    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected TableStaticData(final Database<?> pDatabase,
                              final String pTabName) {
        super(pDatabase, pTabName);

        /* Define the columns */
        TableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(StaticData.FIELD_ENABLED);
        ColumnDefinition mySortCol = myTableDef.addIntegerColumn(StaticData.FIELD_ORDER);
        myTableDef.addEncryptedColumn(StaticData.FIELD_NAME, StaticData.NAMELEN);
        myTableDef.addNullEncryptedColumn(StaticData.FIELD_DESC, StaticData.DESCLEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
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
    protected DataValues<E> getRowValues(final String pName) throws JOceanusException {
        /* Obtain the values */
        DataValues<E> myValues = super.getRowValues(pName);
        TableDefinition myTableDef = getTableDef();

        /* Add the info and return the new values */
        myValues.addValue(StaticData.FIELD_NAME, myTableDef.getBinaryValue(StaticData.FIELD_NAME));
        myValues.addValue(StaticData.FIELD_DESC, myTableDef.getBinaryValue(StaticData.FIELD_DESC));
        myValues.addValue(StaticData.FIELD_ORDER, myTableDef.getIntegerValue(StaticData.FIELD_ORDER));
        myValues.addValue(StaticData.FIELD_ENABLED, myTableDef.getBooleanValue(StaticData.FIELD_ENABLED));
        return myValues;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Sort the data */
        theList.reSort();

        /* Validate the data */
        theList.validateOnLoad();
    }
}
