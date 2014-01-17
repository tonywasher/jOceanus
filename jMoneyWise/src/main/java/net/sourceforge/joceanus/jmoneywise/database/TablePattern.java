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

import net.sourceforge.joceanus.jdatamanager.EncryptedData;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamodels.data.DataErrorList;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition;
import net.sourceforge.joceanus.jdatamodels.database.Database;
import net.sourceforge.joceanus.jdatamodels.database.TableDefinition;
import net.sourceforge.joceanus.jdatamodels.database.TableEncrypted;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.data.EventBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Pattern;
import net.sourceforge.joceanus.jmoneywise.data.Pattern.PatternList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TabelEncrypted extension for Pattern.
 * @author Tony Washer
 */
public class TablePattern
        extends TableEncrypted<Pattern> {
    /**
     * The name of the Patterns table.
     */
    protected static final String TABLE_NAME = Pattern.LIST_NAME;

    /**
     * The pattern list.
     */
    private PatternList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePattern(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(EventBase.FIELD_DATE);
        myTableDef.addReferenceColumn(EventBase.FIELD_DEBIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_CREDIT, TableAccount.TABLE_NAME);
        myTableDef.addEncryptedColumn(EventBase.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(EventBase.FIELD_CATEGORY, TableEventCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Pattern.FIELD_FREQ, TableFrequency.TABLE_NAME);
        myTableDef.addBooleanColumn(EventBase.FIELD_SPLIT);
        myTableDef.addNullReferenceColumn(EventBase.FIELD_PARENT, TABLE_NAME);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getPatterns();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        JDateDay myDate = myTableDef.getDateValue(EventBase.FIELD_DATE);
        Integer myDebitId = myTableDef.getIntegerValue(EventBase.FIELD_DEBIT);
        Integer myCreditId = myTableDef.getIntegerValue(EventBase.FIELD_CREDIT);
        Integer myCategoryId = myTableDef.getIntegerValue(EventBase.FIELD_CATEGORY);
        byte[] myAmount = myTableDef.getBinaryValue(EventBase.FIELD_AMOUNT);
        Integer myFreq = myTableDef.getIntegerValue(Pattern.FIELD_FREQ);
        Boolean mySplit = myTableDef.getBooleanValue(EventBase.FIELD_SPLIT);
        Integer myParentId = myTableDef.getIntegerValue(EventBase.FIELD_PARENT);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myDate, myDebitId, myCreditId, myAmount, myCategoryId, myFreq, mySplit, myParentId);
    }

    @Override
    protected void setFieldValue(final Pattern pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventBase.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (EventBase.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (EventBase.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDebitId());
        } else if (EventBase.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCreditId());
        } else if (EventBase.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Pattern.FIELD_FREQ.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFrequencyId());
        } else if (EventBase.FIELD_SPLIT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isSplit());
        } else if (EventBase.FIELD_PARENT.equals(iField)) {
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

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the events */
        DataErrorList<DataItem> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JOceanusException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
