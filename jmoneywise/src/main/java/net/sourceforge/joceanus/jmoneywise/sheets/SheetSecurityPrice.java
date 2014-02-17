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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for SecurityPrice.
 * @author Tony Washer
 */
public class SheetSecurityPrice
        extends SheetEncrypted<SecurityPrice, MoneyWiseDataType> {
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
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(SecurityPrice.OBJECT_NAME);
        myValues.addValue(SecurityPrice.FIELD_SECURITY, loadInteger(COL_SECURITY));
        myValues.addValue(SecurityPrice.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(SecurityPrice.FIELD_PRICE, loadBytes(COL_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final SecurityPrice pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_SECURITY, pItem.getSecurityId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
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
        /* Access the list of prices */
        SecurityPriceList myList = pData.getPrices();

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
            int myTotal = (myRows - 1) * (myCols - 1);

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
                    String mySecurity = myCell.getStringValue();

                    /* Handle price which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Access the formatted cell */
                        String myPrice = myCell.getStringValue();

                        /* Build data values */
                        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(SecurityPrice.OBJECT_NAME);
                        myValues.addValue(SecurityPrice.FIELD_SECURITY, mySecurity);
                        myValues.addValue(SecurityPrice.FIELD_DATE, myDate);
                        myValues.addValue(SecurityPrice.FIELD_PRICE, myPrice);

                        /* Add the value into the list */
                        myList.addValuesItem(myValues);
                    }

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the prices */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
