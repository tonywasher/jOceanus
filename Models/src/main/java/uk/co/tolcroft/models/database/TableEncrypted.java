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
import uk.co.tolcroft.models.data.EncryptedItem;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend this class.
 * @param <T> the data type
 */
public abstract class TableEncrypted<T extends EncryptedItem & Comparable<T>> extends DatabaseTable<T> {
    /**
     * The table definition.
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected TableEncrypted(final Database<?> pDatabase,
                             final String pTabName) {
        super(pDatabase, pTabName);
    }

    /**
     * Define the table columns (called from within super-constructor).
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(final TableDefinition pTableDef) {
        super.defineTable(pTableDef);
        theTableDef = pTableDef;
        theTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, TableControlKeys.TABLE_NAME);
    }

    /**
     * Load an individual item from the result set.
     * @param pId the Id of the item
     * @param pControlId the ControlKey id of the item
     * @throws JDataException on error
     */
    protected abstract void loadItem(final int pId,
                                     final int pControlId) throws JDataException;

    @Override
    protected void loadItem(final int pId) throws JDataException {
        int myControlId;

        /* Get the various fields */
        myControlId = theTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);

        /* Add into the list */
        loadItem(pId, myControlId);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == EncryptedItem.FIELD_CONTROL) {
            theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
