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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Security.
 */
public class TableSecurity
        extends TableEncrypted<Security, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Security.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSecurity(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(Security.FIELD_SECTYPE, TableSecurityType.TABLE_NAME);
        myTableDef.addReferenceColumn(Security.FIELD_CURRENCY, TableAccountCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Security.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(Security.FIELD_NAME, Security.NAMELEN);
        myTableDef.addNullEncryptedColumn(Security.FIELD_DESC, Security.DESCLEN);
        myTableDef.addEncryptedColumn(Security.FIELD_SYMBOL, Security.SYMBOLLEN);
        myTableDef.addBooleanColumn(Security.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSecurities());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_NAME, myTableDef.getBinaryValue(Security.FIELD_NAME));
        myValues.addValue(Security.FIELD_DESC, myTableDef.getBinaryValue(Security.FIELD_DESC));
        myValues.addValue(Security.FIELD_SECTYPE, myTableDef.getIntegerValue(Security.FIELD_SECTYPE));
        myValues.addValue(Security.FIELD_PARENT, myTableDef.getIntegerValue(Security.FIELD_PARENT));
        myValues.addValue(Security.FIELD_CURRENCY, myTableDef.getIntegerValue(Security.FIELD_CURRENCY));
        myValues.addValue(Security.FIELD_SYMBOL, myTableDef.getBinaryValue(Security.FIELD_SYMBOL));
        myValues.addValue(Security.FIELD_CLOSED, myTableDef.getBooleanValue(Security.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Security pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Security.FIELD_SECTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityTypeId());
        } else if (Security.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Security.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityCurrencyId());
        } else if (Security.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Security.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Security.FIELD_SYMBOL.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSymbolBytes());
        } else if (Security.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
