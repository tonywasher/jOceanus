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
package net.sourceforge.JFinanceApp.sheets;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountRate;
import net.sourceforge.JFinanceApp.data.AccountRate.AccountRateList;
import net.sourceforge.JFinanceApp.data.FinanceData;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetAccountRate extends SheetDataItem<AccountRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = AccountRate.LIST_NAME;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 6;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = 2;

    /**
     * Rate column.
     */
    private static final int COL_RATE = 3;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = 4;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = 5;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private final boolean isBackup;

    /**
     * Rates data list.
     */
    private final AccountRateList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountRate(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Rates list */
        theList = pReader.getData().getRates();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountRate(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Rates list */
        theList = pWriter.getData().getRates();
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
            byte[] myRateBytes = loadBytes(COL_RATE);
            byte[] myBonusBytes = loadBytes(COL_BONUS);
            Date myEndDate = loadDate(COL_ENDDATE);

            /* Load the item */
            theList.addItem(myID, myControlId, myActId, myRateBytes, myEndDate, myBonusBytes);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the account */
            int myID = loadInteger(COL_ID);
            String myAccount = loadString(COL_ACCOUNT - 1);

            /* Access the name and description bytes */
            String myRate = loadString(COL_RATE - 1);
            String myBonus = loadString(COL_BONUS - 1);
            Date myEndDate = loadDate(COL_ENDDATE - 1);

            /* Load the item */
            theList.addItem(myID, myAccount, myRate, myEndDate, myBonus);
        }
    }

    @Override
    protected void insertItem(final AccountRate pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeInteger(COL_ACCOUNT, pItem.getAccount().getId());
            writeBytes(COL_RATE, pItem.getRateBytes());
            writeBytes(COL_BONUS, pItem.getBonusBytes());
            writeDate(COL_ENDDATE, pItem.getEndDate());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeString(COL_ACCOUNT - 1, pItem.getAccount().getName());
            writeNumber(COL_RATE - 1, pItem.getRate());
            writeNumber(COL_BONUS - 1, pItem.getBonus());
            writeDate(COL_ENDDATE - 1, pItem.getEndDate());
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
        writeHeader(COL_ACCOUNT - 1, AccountRate.FIELD_ACCOUNT.getName());
        writeHeader(COL_RATE - 1, AccountRate.FIELD_RATE.getName());
        writeHeader(COL_BONUS - 1, AccountRate.FIELD_BONUS.getName());
        writeHeader(COL_ENDDATE - 1, AccountRate.FIELD_ENDDATE.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the six columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the five columns as the range */
            nameRange(NUM_COLS - 1);

            /* Hide the ID column */
            setHiddenColumn(COL_ID);
            setIntegerColumn(COL_ID);

            /* Set the Account column width */
            setColumnWidth(COL_ACCOUNT - 1, Account.NAMELEN);
            applyDataValidation(COL_ACCOUNT - 1, SheetAccount.AREA_ACCOUNTNAMES);

            /* Set Rate and Date columns */
            setRateColumn(COL_RATE - 1);
            setRateColumn(COL_RATE - 1);
            setDateColumn(COL_ENDDATE - 1);
        }
    }

    /**
     * Load the Rates from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_RATES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_RATES)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of rates */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of rates */
                AccountRateList myList = pData.getRates();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);
                    int iAdjust = 0;

                    /* Access account */
                    Cell myCell = myRow.getCell(myCol + iAdjust++);
                    String myAccount = myCell.getStringCellValue();

                    /* Handle Rate */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    String myRate = pHelper.formatRateCell(myCell);

                    /* Handle bonus which may be missing */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    String myBonus = null;
                    if (myCell != null) {
                        myBonus = pHelper.formatRateCell(myCell);
                    }

                    /* Handle expiration which may be missing */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    Date myExpiry = null;
                    if (myCell != null) {
                        myExpiry = myCell.getDateCellValue();
                    }

                    /* Add the value into the finance tables */
                    myList.addItem(0, myAccount, myRate, myExpiry, myBonus);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Rates", e);
        }

        /* Return to caller */
        return true;
    }
}
