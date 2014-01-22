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

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for SecurityPrice.
 * @author Tony Washer
 */
public class SheetSecurityPrice
        extends SheetDataItem<SecurityPrice> {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = SecurityPrice.LIST_NAME;

    /**
     * Security column.
     */
    private static final int COL_SECURITY = COL_CONTROLID + 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_SECURITY + 1;

    /**
     * Price column.
     */
    private static final int COL_PRICE = COL_DATE + 1;

    /**
     * Prices data list.
     */
    private final SecurityPriceList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetSecurityPrice(final MoneyWiseReader pReader) {
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
    protected SheetSecurityPrice(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Access the Prices list */
        theList = pWriter.getData().getPrices();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer mySecId = loadInteger(COL_SECURITY);

        /* Access the rates and end-date */
        JDateDay myDate = loadDate(COL_DATE);
        byte[] myPriceBytes = loadBytes(COL_PRICE);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myDate, mySecId, myPriceBytes);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the Security */
        String mySecurity = loadString(COL_SECURITY);

        /* Access the name and description bytes */
        JDateDay myDate = loadDate(COL_DATE);
        String myPrice = loadString(COL_PRICE);

        /* Load the item */
        theList.addOpenItem(pId, myDate, mySecurity, myPrice);
    }

    @Override
    protected void insertSecureItem(final SecurityPrice pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_SECURITY, pItem.getSecurityId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
    }

    @Override
    protected void insertOpenItem(final SecurityPrice pItem) throws JOceanusException {
        /* Set the fields */
        writeString(COL_SECURITY, pItem.getSecurityName());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_PRICE, pItem.getPrice());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_SECURITY, SecurityPrice.FIELD_SECURITY.getName());
        writeHeader(COL_DATE, SecurityPrice.FIELD_DATE.getName());
        writeHeader(COL_PRICE, SecurityPrice.FIELD_PRICE.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_SECURITY);
        setDateColumn(COL_DATE);
        setPriceColumn(COL_PRICE);

        /* Apply validation */
        applyDataValidation(COL_SECURITY, SheetAccount.AREA_ACCOUNTNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PRICE;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final JDateDay pLastEvent) throws JOceanusException {
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
            SecurityPriceList myList = pData.getPrices();

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
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load Prices", e);
        }

        /* Return to caller */
        return true;
    }
}
