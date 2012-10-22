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
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.Pattern;
import net.sourceforge.JFinanceApp.data.Pattern.PatternList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for Pattern.
 * @author Tony Washer
 */
public class SheetPattern extends SheetDataItem<Pattern> {
    /**
     * NamedArea for Patterns.
     */
    private static final String AREA_PATTERNS = Pattern.LIST_NAME;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = COL_CONTROLID + 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_ACCOUNT + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_DATE + 1;

    /**
     * isCredit column.
     */
    private static final int COL_CREDIT = COL_DESC + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_CREDIT + 1;

    /**
     * Partner column.
     */
    private static final int COL_PARTNER = COL_AMOUNT + 1;

    /**
     * TransType column.
     */
    private static final int COL_TRAN = COL_PARTNER + 1;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = COL_TRAN + 1;

    /**
     * Patterns data list.
     */
    private final PatternList theList;

    /**
     * Accounts data list.
     */
    private Account.AccountList theAccounts = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPattern(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PATTERNS);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theAccounts = myData.getAccounts();
        theList = myData.getPatterns();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPattern(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PATTERNS);

        /* Access the Patterns list */
        theList = pWriter.getData().getPatterns();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActId = loadInteger(COL_ACCOUNT);
        Integer myPartId = loadInteger(COL_PARTNER);
        Integer myTranId = loadInteger(COL_TRAN);
        Integer myFreqId = loadInteger(COL_FREQ);

        /* Access the date and credit flag */
        Date myDate = loadDate(COL_DATE);
        boolean isCredit = loadBoolean(COL_CREDIT);

        /* Access the binary values */
        byte[] myDesc = loadBytes(COL_DESC);
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myDate, myDesc, myAmount, myActId, myPartId, myTranId,
                              myFreqId, isCredit);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the Account */
        Integer myID = loadInteger(COL_ID);
        String myAccount = loadString(COL_ACCOUNT);
        String myPartner = loadString(COL_PARTNER);
        String myTransType = loadString(COL_TRAN);
        String myFrequency = loadString(COL_FREQ);

        /* Access the date and credit flag */
        Date myDate = loadDate(COL_DATE);
        Boolean isCredit = loadBoolean(COL_CREDIT);

        /* Access the string values */
        String myDesc = loadString(COL_DESC);
        String myAmount = loadString(COL_AMOUNT);

        /* Load the item */
        theList.addOpenItem(myID, myDate, myDesc, myAmount, myAccount, myPartner, myTransType, myFrequency,
                            isCredit);
    }

    @Override
    protected void insertSecureItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKey().getId());
        writeInteger(COL_ACCOUNT, pItem.getAccount().getId());
        writeInteger(COL_PARTNER, pItem.getPartner().getId());
        writeInteger(COL_TRAN, pItem.getTransType().getId());
        writeInteger(COL_FREQ, pItem.getFrequency().getId());
        writeDate(COL_DATE, pItem.getDate());
        writeBoolean(COL_CREDIT, pItem.isCredit());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected void insertOpenItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeString(COL_ACCOUNT, pItem.getAccount().getName());
        writeString(COL_PARTNER, pItem.getPartner().getName());
        writeString(COL_TRAN, pItem.getTransType().getName());
        writeString(COL_FREQ, pItem.getFrequency().getName());
        writeDate(COL_DATE, pItem.getDate());
        writeBoolean(COL_CREDIT, pItem.isCredit());
        writeString(COL_DESC, pItem.getDesc());
        writeNumber(COL_AMOUNT, pItem.getAmount());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_ACCOUNT, Pattern.FIELD_ACCOUNT.getName());
        writeHeader(COL_DATE, Event.FIELD_DATE.getName());
        writeHeader(COL_DESC, Event.FIELD_DESC.getName());
        writeHeader(COL_CREDIT, Pattern.FIELD_ISCREDIT.getName());
        writeHeader(COL_AMOUNT, Event.FIELD_AMOUNT.getName());
        writeHeader(COL_PARTNER, Pattern.FIELD_PARTNER.getName());
        writeHeader(COL_TRAN, Event.FIELD_TRNTYP.getName());
        writeHeader(COL_FREQ, Pattern.FIELD_FREQ.getName());

        /* Set the Account column width */
        setColumnWidth(COL_ACCOUNT, Account.NAMELEN);
        setColumnWidth(COL_DESC, Event.DESCLEN);
        setColumnWidth(COL_PARTNER, Account.NAMELEN);
        setColumnWidth(COL_TRAN, StaticData.NAMELEN);
        setColumnWidth(COL_FREQ, StaticData.NAMELEN);

        /* Set Number columns */
        setDateColumn(COL_DATE);
        setBooleanColumn(COL_CREDIT);
        setMoneyColumn(COL_AMOUNT);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_FREQ);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Apply Validation */
            applyDataValidation(COL_ACCOUNT, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_PARTNER, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_TRAN, SheetTransactionType.AREA_TRANSTYPENAMES);
            applyDataValidation(COL_FREQ, SheetFrequency.AREA_FREQUENCYNAMES);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theAccounts.validateLoadedAccounts();
    }

    /**
     * Load the Patterns from an archive.
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
            AreaReference myRange = pHelper.resolveAreaReference(AREA_PATTERNS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_PATTERNS)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of patterns */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of patterns */
                PatternList myList = pData.getPatterns();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);
                    int iAdjust = 0;

                    /* Access strings */
                    String myAccount = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    Date myDate = myRow.getCell(myCol + iAdjust++).getDateCellValue();
                    String myDesc = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    String myAmount = pHelper.formatNumericCell(myRow.getCell(myCol + iAdjust++));
                    String myPartner = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    String myTransType = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    boolean isCredit = myRow.getCell(myCol + iAdjust++).getBooleanCellValue();
                    String myFrequency = myRow.getCell(myCol + iAdjust++).getStringCellValue();

                    /* Add the value into the finance tables */
                    myList.addOpenItem(0, myDate, myDesc, myAmount, myAccount, myPartner, myTransType,
                                       myFrequency, isCredit);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
