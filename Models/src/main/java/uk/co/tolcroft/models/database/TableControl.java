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
import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.data.ControlData.ControlDataList;
import uk.co.tolcroft.models.data.DataSet;

/**
 * Database table class for ControlData.
 */
public class TableControl extends DatabaseTable<ControlData> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = ControlData.LIST_NAME;

    /**
     * The control data list.
     */
    private ControlDataList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableControl(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addIntegerColumn(ControlData.FIELD_VERSION);
        myTableDef.addReferenceColumn(ControlData.FIELD_CONTROLKEY, TableControlKeys.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        theList = pData.getControlData();
        setList(theList);
    }

    @Override
    protected void loadItem(final int pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        int myVers = myTableDef.getIntegerValue(ControlData.FIELD_VERSION);
        int myControl = myTableDef.getIntegerValue(ControlData.FIELD_CONTROLKEY);

        /* Add into the list */
        theList.addItem(pId, myVers, myControl);
    }

    @Override
    protected void setFieldValue(final ControlData pItem,
                                 final JDataField pField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (pField == ControlData.FIELD_VERSION) {
            myTableDef.setIntegerValue(pField, pItem.getDataVersion());
        } else if (pField == ControlData.FIELD_CONTROLKEY) {
            myTableDef.setIntegerValue(pField, pItem.getControlKey().getId());
        } else {
            super.setFieldValue(pItem, pField);
        }
    }
}
