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
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend this class.
 * @param <T> the data type
 * @param <E> the data list enum class
 */
public abstract class TableEncrypted<T extends EncryptedItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends DatabaseTable<T, E> {
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
     * @throws JOceanusException on error
     */
    protected abstract void loadItem(final Integer pId,
                                     final Integer pControlId) throws JOceanusException;

    @Override
    protected void loadItem(final Integer pId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myControlId = myTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);

        /* Add into the list */
        loadItem(pId, myControlId);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EncryptedItem.FIELD_CONTROL.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getControlKeyId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
