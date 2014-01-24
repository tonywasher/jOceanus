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
import net.sourceforge.joceanus.jprometheus.data.ControlKey;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for ControlKey.
 */
public class TableControlKeys
        extends DatabaseTable<ControlKey, CryptographyList> {
    /**
     * The name of the ControlKeys table.
     */
    protected static final String TABLE_NAME = ControlKey.LIST_NAME;

    /**
     * The control key list.
     */
    private ControlKeyList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableControlKeys(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addBinaryColumn(ControlKey.FIELD_PASSHASH, ControlKey.HASHLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        theList = pData.getControlKeys();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myHash = myTableDef.getBinaryValue(ControlKey.FIELD_PASSHASH);

        /* Add into the list */
        theList.addSecureItem(pId, myHash);
    }

    @Override
    protected void setFieldValue(final ControlKey pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (ControlKey.FIELD_PASSHASH.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getHashBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
