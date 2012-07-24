/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.database;

import java.util.Date;

import javax.swing.SortOrder;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.database.ColumnDefinition;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.database.TableDefinition;
import net.sourceforge.JDataModels.database.TableEncrypted;
import net.sourceforge.JFinanceApp.data.AccountRate;
import net.sourceforge.JFinanceApp.data.AccountRate.AccountRateList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JGordianKnot.EncryptedData;

/**
 * TableEncrypted extension for AccountRate.
 * @author Tony Washer
 */
public class TableAccountRate extends TableEncrypted<AccountRate> {
    /**
     * The name of the Rates table.
     */
    protected static final String TABLE_NAME = AccountRate.LIST_NAME;

    /**
     * The rate list.
     */
    private AccountRateList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountRate(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myActCol = myTableDef.addReferenceColumn(AccountRate.FIELD_ACCOUNT,
                                                                  TableAccount.TABLE_NAME);
        myTableDef.addEncryptedColumn(AccountRate.FIELD_RATE, EncryptedData.RATELEN);
        myTableDef.addNullEncryptedColumn(AccountRate.FIELD_BONUS, EncryptedData.RATELEN);
        ColumnDefinition myDateCol = myTableDef.addNullDateColumn(AccountRate.FIELD_ENDDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getRates();
        setList(theList);
    }

    @Override
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        int myAccountId = myTableDef.getIntegerValue(AccountRate.FIELD_ACCOUNT);
        byte[] myRate = myTableDef.getBinaryValue(AccountRate.FIELD_RATE);
        byte[] myBonus = myTableDef.getBinaryValue(AccountRate.FIELD_BONUS);
        Date myEndDate = myTableDef.getDateValue(AccountRate.FIELD_ENDDATE);

        /* Add into the list */
        theList.addItem(pId, pControlId, myAccountId, myRate, myEndDate, myBonus);
    }

    @Override
    protected void setFieldValue(final AccountRate pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountRate.FIELD_ACCOUNT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAccount().getId());
        } else if (AccountRate.FIELD_RATE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getRateBytes());
        } else if (AccountRate.FIELD_BONUS.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getBonusBytes());
        } else if (AccountRate.FIELD_ENDDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getEndDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
