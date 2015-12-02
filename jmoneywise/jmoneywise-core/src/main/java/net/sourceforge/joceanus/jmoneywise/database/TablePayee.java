/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Payee.
 */
public class TablePayee
        extends TableEncrypted<Payee, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Payee.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePayee(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(Payee.FIELD_PAYEETYPE, TablePayeeType.TABLE_NAME);
        myTableDef.addEncryptedColumn(Payee.FIELD_NAME, Payee.NAMELEN);
        myTableDef.addNullEncryptedColumn(Payee.FIELD_DESC, Payee.DESCLEN);
        myTableDef.addBooleanColumn(Payee.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getPayees());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Payee.OBJECT_NAME);
        myValues.addValue(Payee.FIELD_NAME, myTableDef.getBinaryValue(Payee.FIELD_NAME));
        myValues.addValue(Payee.FIELD_DESC, myTableDef.getBinaryValue(Payee.FIELD_DESC));
        myValues.addValue(Payee.FIELD_PAYEETYPE, myTableDef.getIntegerValue(Payee.FIELD_PAYEETYPE));
        myValues.addValue(Payee.FIELD_CLOSED, myTableDef.getBooleanValue(Payee.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Payee pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Payee.FIELD_PAYEETYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getPayeeTypeId());
        } else if (Payee.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Payee.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Payee.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
