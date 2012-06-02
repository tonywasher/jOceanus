/*******************************************************************************
 * JDataModel: Data models
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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import uk.co.tolcroft.models.data.DataKey;
import uk.co.tolcroft.models.data.DataKey.DataKeyList;
import uk.co.tolcroft.models.data.DataSet;

/**
 * Database table class for DataKey.
 */
public class TableDataKeys extends DatabaseTable<DataKey> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = DataKey.LIST_NAME;

    /**
     * The table definition.
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The DataKey data list.
     */
    private DataKeyList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDataKeys(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    /**
     * Define the table columns (called from within super-constructor).
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(final TableDefinition pTableDef) {
        /* Define Standard table */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define the columns */
        theTableDef.addReferenceColumn(DataKey.FIELD_CONTROLKEY, TableControlKeys.TABLE_NAME);
        theTableDef.addIntegerColumn(DataKey.FIELD_KEYTYPE);
        theTableDef.addBinaryColumn(DataKey.FIELD_KEY, DataKey.KEYLEN);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        theList = pData.getDataKeys();
        setList(theList);
    }

    @Override
    protected void loadItem(final int pId) throws JDataException {
        int myControl;
        int myKeyType;
        byte[] myKey;

        /* Get the various fields */
        myControl = theTableDef.getIntegerValue(DataKey.FIELD_CONTROLKEY);
        myKeyType = theTableDef.getIntegerValue(DataKey.FIELD_KEYTYPE);
        myKey = theTableDef.getBinaryValue(DataKey.FIELD_KEY);

        /* Add into the list */
        theList.addItem(pId, myControl, myKeyType, myKey);
    }

    @Override
    protected void setFieldValue(final DataKey pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == DataKey.FIELD_CONTROLKEY) {
            theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());
        } else if (iField == DataKey.FIELD_KEYTYPE) {
            theTableDef.setIntegerValue(iField, pItem.getKeyType().getId());
        } else if (iField == DataKey.FIELD_KEY) {
            theTableDef.setBinaryValue(iField, pItem.getSecuredKeyDef());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
