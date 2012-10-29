/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.database;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * Database table class for Static Data Items. Each data type that represents Static Data should extend this
 * class.
 * @param <T> the data type
 */
public abstract class TableStaticData<T extends StaticData<T, ?>> extends TableEncrypted<T> {
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

    /**
     * Load the static data.
     * @param pId the id
     * @param pControlId the control id
     * @param isEnabled is the item enabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    protected abstract void loadTheItem(final Integer pId,
                                        final Integer pControlId,
                                        final Boolean isEnabled,
                                        final Integer iOrder,
                                        final byte[] pName,
                                        final byte[] pDesc) throws JDataException;

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Boolean myEnabled = myTableDef.getBooleanValue(StaticData.FIELD_ENABLED);
        Integer myOrder = myTableDef.getIntegerValue(StaticData.FIELD_ORDER);
        byte[] myType = myTableDef.getBinaryValue(StaticData.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(StaticData.FIELD_DESC);

        /* Add into the list */
        loadTheItem(pId, pControlId, myEnabled, myOrder, myType, myDesc);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JDataException {
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
}
