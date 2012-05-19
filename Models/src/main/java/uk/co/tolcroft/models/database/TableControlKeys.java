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
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;
import uk.co.tolcroft.models.data.DataSet;

public class TableControlKeys extends DatabaseTable<ControlKey> {
    /**
     * The name of the ControlKeys table
     */
    protected final static String TableName = ControlKey.listName;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The control key list
     */
    private ControlKeyList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableControlKeys(Database<?> pDatabase) {
        super(pDatabase, TableName);
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

        /* Define the columns */
        theTableDef.addBinaryColumn(ControlKey.FIELD_PASSHASH, ControlKey.HASHLEN);
    }

    @Override
    protected void declareData(DataSet<?> pData) {
        theList = pData.getControlKeys();
        setList(theList);
    }

    @Override
    protected void loadItem(int pId) throws ModelException {
        byte[] myHash;

        /* Get the various fields */
        myHash = theTableDef.getBinaryValue(ControlKey.FIELD_PASSHASH);

        /* Add into the list */
        theList.addItem(pId, myHash);
    }

    @Override
    protected void setFieldValue(ControlKey pItem,
                                 ReportField iField) throws ModelException {
        /* Switch on field id */
        if (iField == ControlKey.FIELD_PASSHASH) {
            theTableDef.setBinaryValue(iField, pItem.getHashBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
