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
package uk.co.tolcroft.finance.database;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JGordianKnot.EncryptedData;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

/**
 * TableEncrypted extension for AccountPrice.
 * @author Tony Washer
 */
public class TableAccountPrice extends TableEncrypted<AccountPrice> {
    /**
     * The name of the Prices table.
     */
    protected static final String TABLE_NAME = AccountPrice.LIST_NAME;

    /**
     * The table definition.
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The price list.
     */
    private AccountPriceList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountPrice(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    /**
     * Define the table columns (called from within super-constructor).
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(final TableDefinition pTableDef) {
        /* Define sort column variable */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define Sort Column variables */
        ColumnDefinition myDateCol;
        ColumnDefinition myActCol;

        /* Declare the columns */
        myActCol = theTableDef.addReferenceColumn(AccountPrice.FIELD_ACCOUNT, TableAccount.TABLE_NAME);
        myDateCol = theTableDef.addDateColumn(AccountPrice.FIELD_DATE);
        theTableDef.addEncryptedColumn(AccountPrice.FIELD_PRICE, EncryptedData.PRICELEN);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getPrices();
        setList(theList);
    }

    @Override
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        int myAccountId = theTableDef.getIntegerValue(AccountPrice.FIELD_ACCOUNT);
        Date myDate = theTableDef.getDateValue(AccountPrice.FIELD_DATE);
        byte[] myPrice = theTableDef.getBinaryValue(AccountPrice.FIELD_PRICE);

        /* Add into the list */
        theList.addItem(pId, pControlId, myDate, myAccountId, myPrice);
    }

    @Override
    protected void setFieldValue(final AccountPrice pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == AccountPrice.FIELD_ACCOUNT) {
            theTableDef.setIntegerValue(AccountPrice.FIELD_ACCOUNT, pItem.getAccount().getId());
        } else if (iField == AccountPrice.FIELD_DATE) {
            theTableDef.setDateValue(AccountPrice.FIELD_DATE, pItem.getDate());
        } else if (iField == AccountPrice.FIELD_PRICE) {
            theTableDef.setBinaryValue(AccountPrice.FIELD_PRICE, pItem.getPriceBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
