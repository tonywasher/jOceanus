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
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend this class.
 * @param <T> the data type
 * @param <E> the data type enum class
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
        myTableDef.addReferenceColumn(EncryptedItem.FIELD_KEYSET, TableDataKeySet.TABLE_NAME);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EncryptedItem.FIELD_KEYSET.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDataKeySetId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected DataValues<E> getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        DataValues<E> myValues = super.getRowValues(pName);
        TableDefinition myTableDef = getTableDef();

        /* Add the control id and return the new values */
        myValues.addValue(EncryptedItem.FIELD_KEYSET, myTableDef.getIntegerValue(EncryptedItem.FIELD_KEYSET));
        return myValues;
    }
}
