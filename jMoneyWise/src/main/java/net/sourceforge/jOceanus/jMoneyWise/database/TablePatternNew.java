/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.database;

import java.util.Date;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.ColumnDefinition;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDefinition;
import net.sourceforge.jOceanus.jDataModels.database.TableEncrypted;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData;
import net.sourceforge.jOceanus.jMoneyWise.data.EventBase;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.PatternNew;
import net.sourceforge.jOceanus.jMoneyWise.data.PatternNew.PatternNewList;

/**
 * TabelEncrypted extension for Pattern.
 * @author Tony Washer
 */
public class TablePatternNew
        extends TableEncrypted<PatternNew> {
    /**
     * The name of the Patterns table.
     */
    protected static final String TABLE_NAME = PatternNew.LIST_NAME;

    /**
     * The pattern list.
     */
    private PatternNewList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePatternNew(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(EventBase.FIELD_DATE);
        myTableDef.addEncryptedColumn(EventBase.FIELD_DESC, EventBase.DESCLEN);
        myTableDef.addReferenceColumn(EventBase.FIELD_DEBIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_CREDIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);
        myTableDef.addEncryptedColumn(EventBase.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(PatternNew.FIELD_FREQ, TableFrequency.TABLE_NAME);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getNewPatterns();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Date myDate = myTableDef.getDateValue(EventBase.FIELD_DATE);
        byte[] myDesc = myTableDef.getBinaryValue(EventBase.FIELD_DESC);
        Integer myDebitId = myTableDef.getIntegerValue(EventBase.FIELD_DEBIT);
        Integer myCreditId = myTableDef.getIntegerValue(EventBase.FIELD_CREDIT);
        Integer myTranType = myTableDef.getIntegerValue(EventBase.FIELD_TRNTYP);
        byte[] myAmount = myTableDef.getBinaryValue(EventBase.FIELD_AMOUNT);
        Integer myFreq = myTableDef.getIntegerValue(PatternNew.FIELD_FREQ);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myDate, myDesc, myDebitId, myCreditId, myTranType, myAmount, myFreq);
    }

    @Override
    protected void setFieldValue(final PatternNew pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventBase.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(EventBase.FIELD_DATE, pItem.getDate());
        } else if (EventBase.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(EventBase.FIELD_DESC, pItem.getDescBytes());
        } else if (EventBase.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(EventBase.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (EventBase.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(EventBase.FIELD_DEBIT, pItem.getDebitId());
        } else if (EventBase.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(EventBase.FIELD_CREDIT, pItem.getCreditId());
        } else if (EventBase.FIELD_TRNTYP.equals(iField)) {
            myTableDef.setIntegerValue(EventBase.FIELD_TRNTYP, pItem.getTransTypeId());
        } else if (PatternNew.FIELD_FREQ.equals(iField)) {
            myTableDef.setIntegerValue(PatternNew.FIELD_FREQ, pItem.getFrequencyId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
