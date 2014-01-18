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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.ControlData;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for ControlData.
 */
public class TableControlData
        extends DatabaseTable<ControlData> {
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
    protected TableControlData(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addIntegerColumn(ControlData.FIELD_VERSION);
        myTableDef.addReferenceColumn(ControlData.FIELD_CONTROLKEY, TableControlKeys.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        theList = pData.getControlData();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myVers = myTableDef.getIntegerValue(ControlData.FIELD_VERSION);
        Integer myControl = myTableDef.getIntegerValue(ControlData.FIELD_CONTROLKEY);

        /* Add into the list */
        theList.addSecureItem(pId, myVers, myControl);
    }

    @Override
    protected void setFieldValue(final ControlData pItem,
                                 final JDataField pField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (ControlData.FIELD_VERSION.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getDataVersion());
        } else if (ControlData.FIELD_CONTROLKEY.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getControlKeyId());
        } else {
            super.setFieldValue(pItem, pField);
        }
    }
}
