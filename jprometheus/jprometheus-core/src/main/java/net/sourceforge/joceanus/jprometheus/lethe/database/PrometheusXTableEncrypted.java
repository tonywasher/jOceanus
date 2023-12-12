/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for Encrypted Items. Each data type that uses encrypted data should extend
 * this class.
 * @param <T> the data type
 */
public abstract class PrometheusXTableEncrypted<T extends EncryptedItem>
        extends PrometheusXTableDataItem<T> {
    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTabName the table name
     */
    protected PrometheusXTableEncrypted(final PrometheusXDataStore pDatabase,
                                        final String pTabName) {
        super(pDatabase, pTabName);
        final PrometheusXTableDefinition myTableDef = getTableDef();
        myTableDef.addReferenceColumn(EncryptedItem.FIELD_KEYSET, PrometheusXTableDataKeySet.TABLE_NAME);
    }

    @Override
    protected void setFieldValue(final T pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (EncryptedItem.FIELD_KEYSET.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDataKeySetId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected DataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues myValues = super.getRowValues(pName);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Add the control id and return the new values */
        myValues.addValue(EncryptedItem.FIELD_KEYSET, myTableDef.getIntegerValue(EncryptedItem.FIELD_KEYSET));
        return myValues;
    }
}
