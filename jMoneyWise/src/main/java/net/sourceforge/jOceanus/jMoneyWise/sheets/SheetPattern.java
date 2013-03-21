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
import net.sourceforge.jOceanus.jMoneyWise.data.EventBase;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern.PatternList;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

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
     * CategoryType column.
     */
    private static final int COL_CATEGORY = COL_CREDIT + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_CATEGORY + 1;

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
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myDebitId = loadInteger(COL_DEBIT);
        Integer myCreditId = loadInteger(COL_CREDIT);
        Integer myCatId = loadInteger(COL_CATEGORY);
        Integer myFreqId = loadInteger(COL_FREQ);

        /* Access the date */
        Date myDate = loadDate(COL_DATE);

        /* Access the binary values */
        byte[] myDesc = loadBytes(COL_DESC);
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myDate, myDesc, myDebitId, myCreditId, myCatId, myAmount, myFreqId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Account */
        String myDebit = loadString(COL_DEBIT);
        String myCredit = loadString(COL_CREDIT);
        String myCategory = loadString(COL_CATEGORY);
        String myFrequency = loadString(COL_FREQ);

        /* Access the date */
        Date myDate = loadDate(COL_DATE);

        /* Access the string values */
        String myDesc = loadString(COL_DESC);
        String myAmount = loadString(COL_AMOUNT);

        /* Load the item */
        theList.addOpenItem(pId, myDate, myDesc, myDebit, myCredit, myCategory, myAmount, myFrequency);
    }

    @Override
    protected void insertSecureItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_CATEGORY, pItem.getCategoryTypeId());
        writeInteger(COL_FREQ, pItem.getFrequencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected void insertOpenItem(final Pattern pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_DEBIT, pItem.getDebitName());
        writeString(COL_CREDIT, pItem.getCreditName());
        writeString(COL_CATEGORY, pItem.getCategoryTypeName());
        writeString(COL_FREQ, pItem.getFrequencyName());
        writeDate(COL_DATE, pItem.getDate());
        writeString(COL_DESC, pItem.getDesc());
        writeDecimal(COL_AMOUNT, pItem.getAmount());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_DATE, EventBase.FIELD_DATE.getName());
        writeHeader(COL_DESC, EventBase.FIELD_DESC.getName());
        writeHeader(COL_DEBIT, EventBase.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, EventBase.FIELD_CREDIT.getName());
        writeHeader(COL_AMOUNT, EventBase.FIELD_AMOUNT.getName());
        writeHeader(COL_CATEGORY, EventBase.FIELD_CATTYP.getName());
        writeHeader(COL_FREQ, Pattern.FIELD_FREQ.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setStringColumn(COL_DESC);
        setStringColumn(COL_DEBIT);
        setStringColumn(COL_CREDIT);
        setStringColumn(COL_CATEGORY);
        setStringColumn(COL_FREQ);
        setDateColumn(COL_DATE);
        setMoneyColumn(COL_AMOUNT);

        /* Apply Validation */
        applyDataValidation(COL_DEBIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_CREDIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_CATEGORY, SheetEventCategoryType.AREA_CATTYPENAMES);
        applyDataValidation(COL_FREQ, SheetFrequency.AREA_FREQUENCYNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_FREQ;
    }

    /**
     * Load the Patterns from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_PATTERNS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_PATTERNS)) {
                return false;
            }

            /* Count the number of Patterns */
            int myTotal = myView.getRowCount();

            /* Access the list of patterns */
            PatternList myList = pData.getPatterns();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access strings */
                String myAccount = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                Date myDate = myView.getRowCellByIndex(myRow, iAdjust++).getDateValue();
                String myDesc = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myAmount = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myPartner = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myTransType = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                boolean isCredit = myView.getRowCellByIndex(myRow, iAdjust++).getBooleanValue();
                String myFrequency = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myDate, myDesc, isCredit
                        ? myPartner
                        : myAccount, isCredit
                        ? myAccount
                        : myPartner, myTransType, myAmount, myFrequency);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the list */
            myList.reSort();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
