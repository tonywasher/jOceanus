/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.jOceanus.jDataModels.database;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend this class.
 * @param <T> the data type
 */
public abstract class TableEncrypted<T extends EncryptedItem & Comparable<? super T>> extends
        DatabaseTable<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected TableEncrypted(final Database<?> pDatabase,
                             final String pTabName) {
        super(pDatabase, pTabName);
        TableDefinition myTableDef = getTableDef();
        myTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, TableControlKeys.TABLE_NAME);
    }

    /**
     * Load an individual item from the result set.
     * @param pId the Id of the item
     * @param pControlId the ControlKey id of the item
     * @throws JDataException on error
     */
    protected abstract void loadItem(final Integer pId,
                                     final Integer pControlId) throws JDataException;

    @Override
    protected void loadItem(final Integer pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myControlId = myTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);

        /* Add into the list */
        loadItem(pId, myControlId);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EncryptedItem.FIELD_CONTROL.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKey().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
