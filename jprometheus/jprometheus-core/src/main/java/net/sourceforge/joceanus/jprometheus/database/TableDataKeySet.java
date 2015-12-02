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

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jprometheus.data.DataKeySet;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for ControlKey.
 */
public class TableDataKeySet
        extends DatabaseTable<DataKeySet, CryptographyDataType> {
    /**
     * The name of the DataKeySet table.
     */
    protected static final String TABLE_NAME = DataKeySet.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDataKeySet(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addReferenceColumn(DataKeySet.FIELD_CONTROLKEY, TableControlKeys.TABLE_NAME);
        myTableDef.addDateColumn(DataKeySet.FIELD_CREATEDATE);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        setList(pData.getDataKeySets());
    }

    @Override
    protected DataValues<CryptographyDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<CryptographyDataType> myValues = getRowValues(DataKeySet.OBJECT_NAME);
        myValues.addValue(DataKeySet.FIELD_CONTROLKEY, myTableDef.getIntegerValue(DataKeySet.FIELD_CONTROLKEY));
        myValues.addValue(DataKeySet.FIELD_CREATEDATE, myTableDef.getDateValue(DataKeySet.FIELD_CREATEDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DataKeySet pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (DataKeySet.FIELD_CONTROLKEY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKeyId());
        } else if (DataKeySet.FIELD_CREATEDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getCreationDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
