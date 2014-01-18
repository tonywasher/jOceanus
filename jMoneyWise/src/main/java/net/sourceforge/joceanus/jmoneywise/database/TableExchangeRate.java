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

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamodels.data.DataErrorList;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition;
import net.sourceforge.joceanus.jdatamodels.database.Database;
import net.sourceforge.joceanus.jdatamodels.database.DatabaseTable;
import net.sourceforge.joceanus.jdatamodels.database.TableDefinition;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for ExchangeRate.
 * @author Tony Washer
 */
public class TableExchangeRate
        extends DatabaseTable<ExchangeRate> {
    /**
     * The name of the ExchangeRate table.
     */
    protected static final String TABLE_NAME = ExchangeRate.LIST_NAME;

    /**
     * The rate list.
     */
    private ExchangeRateList theList = null;

    /**
     * The formatter.
     */
    private JDataFormatter theFormatter = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableExchangeRate(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(ExchangeRate.FIELD_DATE);
        ColumnDefinition myFromCol = myTableDef.addReferenceColumn(ExchangeRate.FIELD_FROM, TableAccountCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(ExchangeRate.FIELD_TO, TableAccountCurrency.TABLE_NAME);
        myTableDef.addRatioColumn(ExchangeRate.FIELD_RATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.DESCENDING);
        myFromCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getExchangeRates();
        setList(theList);
        theFormatter = myData.getDataFormatter();
    }

    @Override
    protected void loadItem(final Integer pId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        JDateDay myDate = myTableDef.getDateValue(ExchangeRate.FIELD_DATE);
        Integer myFromId = myTableDef.getIntegerValue(ExchangeRate.FIELD_FROM);
        Integer myToId = myTableDef.getIntegerValue(ExchangeRate.FIELD_TO);
        JRatio myRate = myTableDef.getRatioValue(ExchangeRate.FIELD_RATE, theFormatter);

        /* Add into the list */
        theList.addSecureItem(pId, myDate, myFromId, myToId, myRate);
    }

    @Override
    protected void setFieldValue(final ExchangeRate pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (ExchangeRate.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (ExchangeRate.FIELD_FROM.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFromCurrencyId());
        } else if (ExchangeRate.FIELD_TO.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getToCurrencyId());
        } else if (ExchangeRate.FIELD_RATE.equals(iField)) {
            myTableDef.setRatioValue(iField, pItem.getExchangeRate());
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

        /* Validate the account categories */
        DataErrorList<DataItem> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
