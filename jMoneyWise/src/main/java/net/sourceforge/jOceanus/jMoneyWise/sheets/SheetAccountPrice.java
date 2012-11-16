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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountBase;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.DilutionEvent.DilutionEventList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

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
     * Alternate NamedArea for Prices.
     */
    private static final String AREA_SPOTPRICES = "SpotPricesData";

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
    protected SheetAccountPrice(final FinanceReader pReader) {
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
    protected SheetAccountPrice(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Access the Prices list */
        theList = pWriter.getData().getPrices();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActId = loadInteger(COL_ACCOUNT);

        /* Access the rates and end-date */
        Date myDate = loadDate(COL_DATE);
        byte[] myPriceBytes = loadBytes(COL_PRICE);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myDate, myActId, myPriceBytes);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the Account */
        Integer myID = loadInteger(COL_ID);
        String myAccount = loadString(COL_ACCOUNT);

        /* Access the name and description bytes */
        Date myDate = loadDate(COL_DATE);
        String myPrice = loadString(COL_PRICE);

        /* Load the item */
        theList.addOpenItem(myID, myDate, myAccount, myPrice);
    }

    @Override
    protected void insertSecureItem(final AccountPrice pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
    }

    @Override
    protected void insertOpenItem(final AccountPrice pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeString(COL_ACCOUNT, pItem.getAccountName());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_PRICE, pItem.getPrice());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_ACCOUNT, AccountPrice.FIELD_ACCOUNT.getName());
        writeHeader(COL_DATE, AccountPrice.FIELD_DATE.getName());
        writeHeader(COL_PRICE, AccountPrice.FIELD_PRICE.getName());

        /* Set the Account column width */
        setColumnWidth(COL_ACCOUNT, AccountBase.NAMELEN);

        /* Set Price and Date columns */
        setDateColumn(COL_DATE);
        setPriceColumn(COL_PRICE);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_PRICE);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Apply validation */
            applyDataValidation(COL_ACCOUNT, SheetAccount.AREA_ACCOUNTNAMES);
        }
    }

    /**
     * Load the Prices from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @param pDilution the dilution events to modify the prices with
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData,
                                         final DilutionEventList pDilution) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_SPOTPRICES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_PRICES)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myDateCol = myTop.getCol();
                Row myActRow = mySheet.getRow(myTop.getRow());

                /* Count the number of tax classes */
                int myTotal = (myBottom.getRow()
                               - myTop.getRow() + 1);
                myTotal *= (myBottom.getCol()
                            - myTop.getCol() - 1);

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = myTop.getRow() + 1; i <= myBottom.getRow(); i++) {

                    /* Access the row */
                    Row myRow = mySheet.getRow(i);

                    /* Access date */
                    Cell myCell = myRow.getCell(myDateCol);
                    Date myDate = myCell.getDateCellValue();

                    /* Loop through the columns of the table */
                    for (int j = myTop.getCol() + 2; j <= myBottom.getCol(); j++) {

                        /* Access account */
                        myCell = myActRow.getCell(j);
                        if (myCell == null) {
                            continue;
                        }
                        String myAccount = myCell.getStringCellValue();

                        /* Handle price which may be missing */
                        myCell = myRow.getCell(j);
                        String myPrice = null;
                        if (myCell != null) {
                            /* Access the formatted cell */
                            myPrice = pHelper.formatNumericCell(myCell);

                            /* If the price is non-zero */
                            if (!myPrice.equals("0.0")) {
                                /* Add the item to the data set */
                                pDilution.addPrice(myAccount, myDate, myPrice);
                            }
                        }

                        /* Report the progress */
                        myCount++;
                        if (((myCount % mySteps) == 0)
                            && (!pTask.setStepsDone(myCount))) {
                            return false;
                        }
                    }
                }

                /* Sort the list */
                pData.getPrices().reSort();
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Prices", e);
        }

        /* Return to caller */
        return true;
    }
}
