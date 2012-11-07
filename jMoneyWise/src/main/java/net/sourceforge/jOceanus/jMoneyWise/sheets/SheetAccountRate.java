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
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate.AccountRateList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetAccountRate
        extends SheetDataItem<AccountRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = AccountRate.LIST_NAME;

    /**
     * NamedArea for Archive Rates.
     */
    private static final String AREA_RATES1 = "Rates";

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = COL_CONTROLID + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_ACCOUNT + 1;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = COL_RATE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_BONUS + 1;

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

        /* Access the Rates list */
        theList = pReader.getData().getRates();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountRate(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        theList = pWriter.getData().getRates();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActId = loadInteger(COL_ACCOUNT);

        /* Access the rates and end-date */
        byte[] myRateBytes = loadBytes(COL_RATE);
        byte[] myBonusBytes = loadBytes(COL_BONUS);
        Date myEndDate = loadDate(COL_ENDDATE);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myActId, myRateBytes, myEndDate, myBonusBytes);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the account */
        int myID = loadInteger(COL_ID);
        String myAccount = loadString(COL_ACCOUNT);

        /* Access the name and description bytes */
        String myRate = loadString(COL_RATE);
        String myBonus = loadString(COL_BONUS);
        Date myEndDate = loadDate(COL_ENDDATE);

        /* Load the item */
        theList.addOpenItem(myID, myAccount, myRate, myEndDate, myBonus);
    }

    @Override
    protected void insertSecureItem(final AccountRate pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected void insertOpenItem(final AccountRate pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeString(COL_ACCOUNT, pItem.getAccountName());
        writeNumber(COL_RATE, pItem.getRate());
        writeNumber(COL_BONUS, pItem.getBonus());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_ACCOUNT, AccountRate.FIELD_ACCOUNT.getName());
        writeHeader(COL_RATE, AccountRate.FIELD_RATE.getName());
        writeHeader(COL_BONUS, AccountRate.FIELD_BONUS.getName());
        writeHeader(COL_ENDDATE, AccountRate.FIELD_ENDDATE.getName());

        /* Set the Account column width */
        setColumnWidth(COL_ACCOUNT, AccountBase.NAMELEN);

        /* Set Rate and Date columns */
        setRateColumn(COL_RATE);
        setRateColumn(COL_BONUS);
        setDateColumn(COL_ENDDATE);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_ENDDATE);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Set validation */
            applyDataValidation(COL_ACCOUNT, SheetAccount.AREA_ACCOUNTNAMES);
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
            AreaReference myRange = pHelper.resolveAreaReference(AREA_RATES1);

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
                int myTotal = myBottom.getRow()
                              - myTop.getRow()
                              + 1;

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
                    Cell myCell = myRow.getCell(myCol
                                                + iAdjust++);
                    String myAccount = myCell.getStringCellValue();

                    /* Handle Rate */
                    myCell = myRow.getCell(myCol
                                           + iAdjust++);
                    String myRate = pHelper.formatNumericCell(myCell);

                    /* Handle bonus which may be missing */
                    myCell = myRow.getCell(myCol
                                           + iAdjust++);
                    String myBonus = null;
                    if (myCell != null) {
                        myBonus = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle expiration which may be missing */
                    myCell = myRow.getCell(myCol
                                           + iAdjust++);
                    Date myExpiry = null;
                    if (myCell != null) {
                        myExpiry = myCell.getDateCellValue();
                    }

                    /* Add the value into the finance tables */
                    myList.addOpenItem(0, myAccount, myRate, myExpiry, myBonus);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0)
                        && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Rates", e);
        }

        /* Return to caller */
        return true;
    }
}
