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

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamodels.data.DataKey;
import net.sourceforge.joceanus.jdatamodels.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for DataKey.
 */
public class TableDataKeys
        extends DatabaseTable<DataKey> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = DataKey.LIST_NAME;

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
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addReferenceColumn(DataKey.FIELD_CONTROLKEY, TableControlKeys.TABLE_NAME);
        myTableDef.addIntegerColumn(DataKey.FIELD_KEYTYPE);
        myTableDef.addBinaryColumn(DataKey.FIELD_KEYDEF, DataKey.KEYLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        theList = pData.getDataKeys();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myControl = myTableDef.getIntegerValue(DataKey.FIELD_CONTROLKEY);
        Integer myKeyType = myTableDef.getIntegerValue(DataKey.FIELD_KEYTYPE);
        byte[] myKey = myTableDef.getBinaryValue(DataKey.FIELD_KEYDEF);

        /* Add into the list */
        theList.addSecureItem(pId, myControl, myKeyType, myKey);
    }

    @Override
    protected void setFieldValue(final DataKey pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (DataKey.FIELD_CONTROLKEY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKeyId());
        } else if (DataKey.FIELD_KEYTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getKeyTypeId());
        } else if (DataKey.FIELD_KEYDEF.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSecuredKeyDef());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
