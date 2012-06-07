/*******************************************************************************
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
package uk.co.tolcroft.finance.database;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JGordianKnot.EncryptedData;
import uk.co.tolcroft.finance.data.AccountRate;
import uk.co.tolcroft.finance.data.AccountRate.AccountRateList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

public class TableAccountRate extends TableEncrypted<AccountRate> {
    /**
     * The name of the Rates table
     */
    protected final static String TableName = AccountRate.LIST_NAME;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The rate list
     */
    private AccountRateList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableAccountRate(Database<?> pDatabase) {
        super(pDatabase, TableName);
    }

    /**
     * Define the table columns (called from within super-constructor)
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(TableDefinition pTableDef) {
        /* Define sort column variable */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define Sort Column variables */
        ColumnDefinition myDateCol;
        ColumnDefinition myActCol;

        /* Declare the columns */
        myActCol = theTableDef.addReferenceColumn(AccountRate.FIELD_ACCOUNT, TableAccount.TableName);
        theTableDef.addEncryptedColumn(AccountRate.FIELD_RATE, EncryptedData.RATELEN);
        theTableDef.addNullEncryptedColumn(AccountRate.FIELD_BONUS, EncryptedData.RATELEN);
        myDateCol = theTableDef.addNullDateColumn(AccountRate.FIELD_ENDDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getRates();
        setList(theList);
    }

    /* Load the rate */
    @Override
    protected void loadItem(int pId,
                            int pControlId) throws JDataException {
        int myAccountId;
        byte[] myRate;
        byte[] myBonus;
        Date myEndDate;

        /* Get the various fields */
        myAccountId = theTableDef.getIntegerValue(AccountRate.FIELD_ACCOUNT);
        myRate = theTableDef.getBinaryValue(AccountRate.FIELD_RATE);
        myBonus = theTableDef.getBinaryValue(AccountRate.FIELD_BONUS);
        myEndDate = theTableDef.getDateValue(AccountRate.FIELD_ENDDATE);

        /* Add into the list */
        theList.addItem(pId, pControlId, myAccountId, myRate, myEndDate, myBonus);

        /* Return to caller */
        return;
    }

    /* Set a field value */
    @Override
    protected void setFieldValue(AccountRate pItem,
                                 JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == AccountRate.FIELD_ACCOUNT) {
            theTableDef.setIntegerValue(iField, pItem.getAccount().getId());
        } else if (iField == AccountRate.FIELD_RATE) {
            theTableDef.setBinaryValue(iField, pItem.getRateBytes());
        } else if (iField == AccountRate.FIELD_BONUS) {
            theTableDef.setBinaryValue(iField, pItem.getBonusBytes());
        } else if (iField == AccountRate.FIELD_ENDDATE) {
            theTableDef.setDateValue(iField, pItem.getEndDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
