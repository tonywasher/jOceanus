/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.database;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamodels.data.ControlKey;
import net.sourceforge.joceanus.jdatamodels.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;

/**
 * Database table class for ControlKey.
 */
public class TableControlKeys
        extends DatabaseTable<ControlKey> {
    /**
     * The name of the ControlKeys table.
     */
    protected static final String TABLE_NAME = ControlKey.LIST_NAME;

    /**
     * The control key list.
     */
    private ControlKeyList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableControlKeys(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addBinaryColumn(ControlKey.FIELD_PASSHASH, ControlKey.HASHLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        theList = pData.getControlKeys();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myHash = myTableDef.getBinaryValue(ControlKey.FIELD_PASSHASH);

        /* Add into the list */
        theList.addSecureItem(pId, myHash);
    }

    @Override
    protected void setFieldValue(final ControlKey pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (ControlKey.FIELD_PASSHASH.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getHashBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
