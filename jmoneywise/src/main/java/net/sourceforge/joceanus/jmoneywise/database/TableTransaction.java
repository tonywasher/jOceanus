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

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Transaction.
 * @author Tony Washer
 */
public class TableTransaction
        extends TableEncrypted<Transaction, MoneyWiseDataType> {
    /**
     * The name of the Transactions table.
     */
    protected static final String TABLE_NAME = Transaction.LIST_NAME;

    /**
     * The transaction list.
     */
    private TransactionList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTransaction(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(Transaction.FIELD_DATE);
        myTableDef.addIntegerColumn(Transaction.FIELD_PAIR);
        myTableDef.addIntegerColumn(Transaction.FIELD_DEBIT);
        myTableDef.addIntegerColumn(Transaction.FIELD_CREDIT);
        myTableDef.addEncryptedColumn(Transaction.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(Transaction.FIELD_CATEGORY, TableTransCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(Transaction.FIELD_RECONCILED);
        myTableDef.addBooleanColumn(Transaction.FIELD_SPLIT);
        myTableDef.addNullReferenceColumn(Transaction.FIELD_PARENT, TABLE_NAME);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getTransactions();
        setList(theList);
    }

    /* Load the event */
    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Transaction.OBJECT_NAME);
        myValues.addValue(Transaction.FIELD_DATE, myTableDef.getDateValue(Transaction.FIELD_DATE));
        myValues.addValue(Transaction.FIELD_PAIR, myTableDef.getIntegerValue(Transaction.FIELD_PAIR));
        myValues.addValue(Transaction.FIELD_CATEGORY, myTableDef.getIntegerValue(Transaction.FIELD_CATEGORY));
        myValues.addValue(Transaction.FIELD_DEBIT, myTableDef.getIntegerValue(Transaction.FIELD_DEBIT));
        myValues.addValue(Transaction.FIELD_CREDIT, myTableDef.getIntegerValue(Transaction.FIELD_CREDIT));
        myValues.addValue(Transaction.FIELD_AMOUNT, myTableDef.getBinaryValue(Transaction.FIELD_AMOUNT));
        myValues.addValue(Transaction.FIELD_RECONCILED, myTableDef.getBooleanValue(Transaction.FIELD_RECONCILED));
        myValues.addValue(Transaction.FIELD_SPLIT, myTableDef.getBooleanValue(Transaction.FIELD_SPLIT));
        myValues.addValue(Transaction.FIELD_PARENT, myTableDef.getIntegerValue(Transaction.FIELD_PARENT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Transaction pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Transaction.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (Transaction.FIELD_PAIR.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetPairId());
        } else if (Transaction.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (Transaction.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDebitId());
        } else if (Transaction.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCreditId());
        } else if (Transaction.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Transaction.FIELD_RECONCILED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isReconciled());
        } else if (Transaction.FIELD_SPLIT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isSplit());
        } else if (Transaction.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
