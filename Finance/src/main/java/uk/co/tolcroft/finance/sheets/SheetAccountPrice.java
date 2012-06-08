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
package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

/**
 * SheetStaticData extension for AccountPrice.
 * @author Tony Washer
 */
public class SheetAccountPrice extends SheetDataItem<AccountPrice> {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = AccountPrice.LIST_NAME;

    /**
     * Alternate NamedArea for Prices.
     */
    private static final String AREA_SPOTPRICES = "SpotPricesData";

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 5;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = 2;

    /**
     * Date column.
     */
    private static final int COL_DATE = 3;

    /**
     * Price column.
     */
    private static final int COL_PRICE = 4;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private final boolean isBackup;

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

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Prices list */
        theList = pReader.getData().getPrices();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountPrice(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Prices list */
        theList = pWriter.getData().getPrices();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {
        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            int myActId = loadInteger(COL_ACCOUNT);

            /* Access the rates and end-date */
            Date myDate = loadDate(COL_DATE);
            byte[] myPriceBytes = loadBytes(COL_PRICE);

            /* Load the item */
            theList.addItem(myID, myControlId, myDate, myActId, myPriceBytes);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the Account */
            int myID = loadInteger(COL_ID);
            String myAccount = loadString(COL_ACCOUNT - 1);

            /* Access the name and description bytes */
            Date myDate = loadDate(COL_DATE - 1);
            String myPrice = loadString(COL_PRICE - 1);

            /* Load the item */
            theList.addItem(myID, myDate, myAccount, myPrice);
        }
    }

    @Override
    protected void insertItem(final AccountPrice pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeInteger(COL_ACCOUNT, pItem.getAccount().getId());
            writeDate(COL_DATE, pItem.getDate());
            writeBytes(COL_PRICE, pItem.getPriceBytes());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeString(COL_ACCOUNT, pItem.getAccount().getName());
            writeDate(COL_DATE, pItem.getDate());
            writeNumber(COL_PRICE, pItem.getPrice());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if we are creating a backup */
        if (isBackup) {
            return;
        }

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());
        writeHeader(COL_ACCOUNT - 1, AccountPrice.FIELD_ACCOUNT.getName());
        writeHeader(COL_DATE - 1, AccountPrice.FIELD_DATE.getName());
        writeHeader(COL_PRICE - 1, AccountPrice.FIELD_PRICE.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the five columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the four columns as the range */
            nameRange(NUM_COLS - 1);

            /* Hide the ID Column */
            setHiddenColumn(COL_ID);
            setIntegerColumn(COL_ID);

            /* Set the Account column width */
            setColumnWidth(COL_ACCOUNT - 1, Account.NAMELEN);
            applyDataValidation(COL_ACCOUNT - 1, SheetAccount.AREA_ACCOUNTNAMES);

            /* Set Price and Date columns */
            setDateColumn(COL_DATE - 1);
            setPriceColumn(COL_PRICE - 1);
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
                int myTotal = (myBottom.getRow() - myTop.getRow() + 1);
                myTotal *= (myBottom.getCol() - myTop.getCol() - 1);

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
                        if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                            return false;
                        }
                    }
                }
            }

            /* Handle exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Prices", e);
        }

        /* Return to caller */
        return true;
    }
}
