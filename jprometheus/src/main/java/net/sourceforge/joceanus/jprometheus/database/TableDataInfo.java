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
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for DataInfo Items. Each data type that represents DataInfo should extend this class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class TableDataInfo<T extends DataInfo<T, ?, ?, ?, E>, E extends Enum<E>>
                                                                                          extends TableEncrypted<T, E> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     * @param pInfoTable the InfoTypes table name
     * @param pOwnerTable the Owner table name
     */
    protected TableDataInfo(final Database<?> pDatabase,
                            final String pTabName,
                            final String pInfoTable,
                            final String pOwnerTable) {
        super(pDatabase, pTabName);

        /* Define the columns */
        TableDefinition myTableDef = getTableDef();
        myTableDef.addReferenceColumn(DataInfo.FIELD_INFOTYPE, pInfoTable);
        myTableDef.addReferenceColumn(DataInfo.FIELD_OWNER, pOwnerTable);
        myTableDef.addEncryptedColumn(DataInfo.FIELD_VALUE, DataInfo.DATALEN);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (DataInfo.FIELD_INFOTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getInfoTypeId());
        } else if (DataInfo.FIELD_OWNER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getOwnerId());
        } else if (DataInfo.FIELD_VALUE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getValueBytes());
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
        myValues.addValue(DataInfo.FIELD_INFOTYPE, myTableDef.getIntegerValue(DataInfo.FIELD_INFOTYPE));
        myValues.addValue(DataInfo.FIELD_OWNER, myTableDef.getIntegerValue(DataInfo.FIELD_OWNER));
        myValues.addValue(DataInfo.FIELD_VALUE, myTableDef.getBinaryValue(DataInfo.FIELD_VALUE));
        return myValues;
    }
}
