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
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventBase;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern.PatternList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for Pattern.
 * @author Tony Washer
 */
public class SheetPattern
        extends SheetDataItem<Pattern> {
    /**
     * NamedArea for Patterns.
     */
    private static final String AREA_PATTERNS = Pattern.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_CONTROLID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_DATE + 1;

    /**
     * Debit column.
     */
    private static final int COL_DEBIT = COL_DESC + 1;

    /**
     * Credit column.
     */
    private static final int COL_CREDIT = COL_DEBIT + 1;

    /**
     * TransType column.
     */
    private static final int COL_TRAN = COL_CREDIT + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_TRAN + 1;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = COL_AMOUNT + 1;

    /**
     * Patterns data list.
     */
    private final PatternList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPattern(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PATTERNS);

        /* Access the Lists */
        theList = pReader.getData().getPatterns();
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
        Integer myDebitId = loadInteger(COL_DEBIT);
        Integer myCreditId = loadInteger(COL_CREDIT);
        Integer myTranId = loadInteger(COL_TRAN);
        Integer myFreqId = loadInteger(COL_FREQ);

        /* Access the date */
        Date myDate = loadDate(COL_DATE);

        /* Access the binary values */
        byte[] myDesc = loadBytes(COL_DESC);
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myDate, myDesc, myDebitId, myCreditId, myTranId, myAmount, myFreqId);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the Account */
        Integer myID = loadInteger(COL_ID);
        String myCredit = loadString(COL_DEBIT);
        String myDebit = loadString(COL_CREDIT);
        String myTransType = loadString(COL_TRAN);
        String myFrequency = loadString(COL_FREQ);

        /* Access the date */
        Date myDate = loadDate(COL_DATE);

        /* Access the string values */
        String myDesc = loadString(COL_DESC);
        String myAmount = loadString(COL_AMOUNT);

        /* Load the item */
        theList.addOpenItem(myID, myDate, myDesc, myDebit, myCredit, myTransType, myAmount, myFrequency);
    }

    @Override
    protected void insertSecureItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_TRAN, pItem.getTransTypeId());
        writeInteger(COL_FREQ, pItem.getFrequencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected void insertOpenItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeString(COL_DEBIT, pItem.getDebitName());
        writeString(COL_CREDIT, pItem.getCreditName());
        writeString(COL_TRAN, pItem.getTransTypeName());
        writeString(COL_FREQ, pItem.getFrequencyName());
        writeDate(COL_DATE, pItem.getDate());
        writeString(COL_DESC, pItem.getDesc());
        writeNumber(COL_AMOUNT, pItem.getAmount());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_DATE, EventBase.FIELD_DATE.getName());
        writeHeader(COL_DESC, EventBase.FIELD_DESC.getName());
        writeHeader(COL_DEBIT, EventBase.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, EventBase.FIELD_CREDIT.getName());
        writeHeader(COL_AMOUNT, EventBase.FIELD_AMOUNT.getName());
        writeHeader(COL_TRAN, EventBase.FIELD_TRNTYP.getName());
        writeHeader(COL_FREQ, Pattern.FIELD_FREQ.getName());

        /* Set the Account column width */
        setColumnWidth(COL_DESC, Event.DESCLEN);
        setColumnWidth(COL_DEBIT, Account.NAMELEN);
        setColumnWidth(COL_CREDIT, Account.NAMELEN);
        setColumnWidth(COL_TRAN, StaticData.NAMELEN);
        setColumnWidth(COL_FREQ, StaticData.NAMELEN);

        /* Set Number columns */
        setDateColumn(COL_DATE);
        setMoneyColumn(COL_AMOUNT);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_FREQ);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Apply Validation */
            applyDataValidation(COL_DEBIT, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_CREDIT, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_TRAN, SheetTransactionType.AREA_TRANSTYPENAMES);
            applyDataValidation(COL_FREQ, SheetFrequency.AREA_FREQUENCYNAMES);
        }
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
            AreaReference myRange = pHelper.resolveAreaReference(SheetPattern.AREA_PATTERNS);

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
                int myTotal = myBottom.getRow()
                              - myTop.getRow()
                              + 1;

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
                    String myAccount = myRow.getCell(myCol
                                                     + iAdjust++).getStringCellValue();
                    Date myDate = myRow.getCell(myCol
                                                + iAdjust++).getDateCellValue();
                    String myDesc = myRow.getCell(myCol
                                                  + iAdjust++).getStringCellValue();
                    String myAmount = pHelper.formatNumericCell(myRow.getCell(myCol
                                                                              + iAdjust++));
                    String myPartner = myRow.getCell(myCol
                                                     + iAdjust++).getStringCellValue();
                    String myTransType = myRow.getCell(myCol
                                                       + iAdjust++).getStringCellValue();
                    boolean isCredit = myRow.getCell(myCol
                                                     + iAdjust++).getBooleanCellValue();
                    String myFrequency = myRow.getCell(myCol
                                                       + iAdjust++).getStringCellValue();

                    /* Add the value into the finance tables */
                    myList.addOpenItem(0, myDate, myDesc, isCredit ? myPartner : myAccount, isCredit ? myAccount : myPartner, myTransType, myAmount,
                            myFrequency);

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
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
