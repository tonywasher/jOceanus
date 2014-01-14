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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jdatamodels.sheets.SheetDataItem;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetDataItem extension for AccountPrice.
 * @author Tony Washer
 */
public class SheetAccountPrice
        extends SheetDataItem<AccountPrice> {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = AccountPrice.LIST_NAME;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = COL_CONTROLID + 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_ACCOUNT + 1;

    /**
     * Price column.
     */
    private static final int COL_PRICE = COL_DATE + 1;

    /**
     * Prices data list.
     */
    private final AccountPriceList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountPrice(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PRICES);

        /* Access the Prices list */
        theList = pReader.getData().getPrices();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountPrice(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Access the Prices list */
        theList = pWriter.getData().getPrices();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActId = loadInteger(COL_ACCOUNT);

        /* Access the rates and end-date */
        JDateDay myDate = loadDate(COL_DATE);
        byte[] myPriceBytes = loadBytes(COL_PRICE);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myDate, myActId, myPriceBytes);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Account */
        String myAccount = loadString(COL_ACCOUNT);

        /* Access the name and description bytes */
        JDateDay myDate = loadDate(COL_DATE);
        String myPrice = loadString(COL_PRICE);

        /* Load the item */
        theList.addOpenItem(pId, myDate, myAccount, myPrice);
    }

    @Override
    protected void insertSecureItem(final AccountPrice pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
    }

    @Override
    protected void insertOpenItem(final AccountPrice pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_ACCOUNT, pItem.getAccountName());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_PRICE, pItem.getPrice());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_ACCOUNT, AccountPrice.FIELD_ACCOUNT.getName());
        writeHeader(COL_DATE, AccountPrice.FIELD_DATE.getName());
        writeHeader(COL_PRICE, AccountPrice.FIELD_PRICE.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setStringColumn(COL_ACCOUNT);
        setDateColumn(COL_DATE);
        setPriceColumn(COL_PRICE);

        /* Apply validation */
        applyDataValidation(COL_ACCOUNT, SheetAccount.AREA_ACCOUNTNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PRICE;
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the prices */
        theList.validateOnLoad();
    }

    /**
     * Load the Prices from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLastEvent the last date to load
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final JDateDay pLastEvent) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_PRICES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_PRICES)) {
                return false;
            }

            /* Count the number of Prices */
            int myRows = myView.getRowCount();
            int myCols = myView.getColumnCount();
            int myTotal = (myRows - 1)
                          * (myCols - 1);

            /* Access the list of prices */
            AccountPriceList myList = pData.getPrices();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the table */
            DataRow myActRow = myView.getRowByIndex(0);
            for (int i = 1; i < myRows; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);

                /* Access date */
                DataCell myCell = myView.getRowCellByIndex(myRow, 0);
                JDateDay myDate = myCell.getDateValue();

                /* If the price is too late */
                if (pLastEvent.compareTo(myDate) < 0) {
                    /* Break the loop */
                    break;
                }

                /* Loop through the columns of the table */
                for (int j = 1; j < myCols; j++) {
                    /* Access account */
                    myCell = myView.getRowCellByIndex(myActRow, j);
                    if (myCell == null) {
                        continue;
                    }
                    String myAccount = myCell.getStringValue();

                    /* Handle price which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Access the formatted cell */
                        String myPrice = myCell.getStringValue();

                        /* Add the item to the data set */
                        myList.addOpenItem(0, myDate, myAccount, myPrice);
                    }

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0)
                        && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the prices */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Prices", e);
        }

        /* Return to caller */
        return true;
    }
}
