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

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for DataInfo Items. Each data type that represents DataInfo should extend this class.
 * @param <T> the data type
 */
public abstract class TableDataInfo<T extends DataInfo<T, ?, ?, ?>>
        extends TableEncrypted<T> {
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

    /**
     * Load the data info.
     * @param pId the id
     * @param pControlId the control id
     * @param pInfoTypeId the infoType id
     * @param pOwnerId the owner id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    protected abstract void loadTheItem(final Integer pId,
                                        final Integer pControlId,
                                        final Integer pInfoTypeId,
                                        final Integer pOwnerId,
                                        final byte[] pValue) throws JOceanusException;

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myInfoType = myTableDef.getIntegerValue(DataInfo.FIELD_INFOTYPE);
        Integer myOwner = myTableDef.getIntegerValue(DataInfo.FIELD_OWNER);
        byte[] myValue = myTableDef.getBinaryValue(DataInfo.FIELD_VALUE);

        /* Add into the list */
        loadTheItem(pId, pControlId, myInfoType, myOwner, myValue);
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
}
