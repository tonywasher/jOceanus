/*******************************************************************************
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
package uk.co.tolcroft.models.database;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public abstract class TableStaticData<T extends StaticData<T, ?>> extends TableEncrypted<T> {
    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * Constructor
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected TableStaticData(Database<?> pDatabase,
                              String pTabName) {
        super(pDatabase, pTabName);
    }

    /**
     * Define the table columns (called from within super-constructor)
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(TableDefinition pTableDef) {
        /* Define Standard table */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define sort column variable */
        ColumnDefinition mySortCol;

        /* Define the columns */
        theTableDef.addBooleanColumn(StaticData.FIELD_ENABLED);
        mySortCol = theTableDef.addIntegerColumn(StaticData.FIELD_ORDER);
        theTableDef.addEncryptedColumn(StaticData.FIELD_NAME, StaticData.NAMELEN);
        theTableDef.addNullEncryptedColumn(StaticData.FIELD_DESC, StaticData.DESCLEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Load the Static Data */
    protected abstract void loadTheItem(int pId,
                                        int pControlId,
                                        boolean isEnabled,
                                        int iOrder,
                                        byte[] pName,
                                        byte[] pDesc) throws ModelException;

    @Override
    protected void loadItem(int pId,
                            int pControlId) throws ModelException {
        int myOrder;
        boolean myEnabled;
        byte[] myType;
        byte[] myDesc;

        /* Get the various fields */
        myEnabled = theTableDef.getBooleanValue(StaticData.FIELD_ENABLED);
        myOrder = theTableDef.getIntegerValue(StaticData.FIELD_ORDER);
        myType = theTableDef.getBinaryValue(StaticData.FIELD_NAME);
        myDesc = theTableDef.getBinaryValue(StaticData.FIELD_DESC);

        /* Add into the list */
        loadTheItem(pId, pControlId, myEnabled, myOrder, myType, myDesc);

        /* Return to caller */
        return;
    }

    @Override
    protected void setFieldValue(T pItem,
                                 ReportField iField) throws ModelException {
        /* Switch on field id */
        if (iField == StaticData.FIELD_ENABLED) {
            theTableDef.setBooleanValue(iField, pItem.getEnabled());
        } else if (iField == StaticData.FIELD_ORDER) {
            theTableDef.setIntegerValue(iField, pItem.getOrder());
        } else if (iField == StaticData.FIELD_NAME) {
            theTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (iField == StaticData.FIELD_DESC) {
            theTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
