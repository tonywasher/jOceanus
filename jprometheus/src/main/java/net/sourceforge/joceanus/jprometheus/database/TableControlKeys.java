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

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.ControlKey;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database table class for ControlKey.
 */
public class TableControlKeys
        extends DatabaseTable<ControlKey, CryptographyDataType> {
    /**
     * The name of the ControlKeys table.
     */
    protected static final String TABLE_NAME = ControlKey.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableControlKeys(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addBooleanColumn(ControlKey.FIELD_HASHPRIME);
        myTableDef.addBinaryColumn(ControlKey.FIELD_PRIMEPASSHASH, ControlKey.HASHLEN);
        myTableDef.addNullBinaryColumn(ControlKey.FIELD_ALTPASSHASH, ControlKey.HASHLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        setList(pData.getControlKeys());
    }

    @Override
    protected DataValues<CryptographyDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<CryptographyDataType> myValues = getRowValues(ControlKey.OBJECT_NAME);
        myValues.addValue(ControlKey.FIELD_HASHPRIME, myTableDef.getBooleanValue(ControlKey.FIELD_HASHPRIME));
        myValues.addValue(ControlKey.FIELD_PRIMEPASSHASH, myTableDef.getBinaryValue(ControlKey.FIELD_PRIMEPASSHASH));
        myValues.addValue(ControlKey.FIELD_ALTPASSHASH, myTableDef.getBinaryValue(ControlKey.FIELD_ALTPASSHASH));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final ControlKey pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (ControlKey.FIELD_HASHPRIME.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isHashPrime());
        } else if (ControlKey.FIELD_PRIMEPASSHASH.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPrimeHashBytes());
        } else if (ControlKey.FIELD_ALTPASSHASH.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAltHashBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
