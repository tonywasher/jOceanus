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
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.EventBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Pattern;
import net.sourceforge.joceanus.jmoneywise.data.Pattern.PatternList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Pattern.
 * @author Tony Washer
 */
public class SheetPattern
        extends SheetDataItem<Pattern, MoneyWiseDataType> {
    /**
     * NamedArea for Patterns.
     */
    private static final String AREA_PATTERNS = Pattern.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_CONTROLID + 1;

    /**
     * Debit column.
     */
    private static final int COL_DEBIT = COL_DATE + 1;

    /**
     * Credit column.
     */
    private static final int COL_CREDIT = COL_DEBIT + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_CREDIT + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_AMOUNT + 1;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = COL_CATEGORY + 1;

    /**
     * Split column.
     */
    private static final int COL_SPLIT = COL_CATEGORY + 1;

    /**
     * Reconciled column.
     */
    private static final int COL_PARENT = COL_SPLIT + 1;

    /**
     * Patterns data list.
     */
    private final PatternList theList;

    /**
     * Last loaded parent.
     */
    private Pattern theLastParent = null;

    /**
     * Last debit.
     */
    private String theLastDebit = null;

    /**
     * Last credit.
     */
    private String theLastCredit = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPattern(final MoneyWiseReader pReader) {
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
    protected SheetPattern(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PATTERNS);

        /* Access the Patterns list */
        theList = pWriter.getData().getPatterns();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myDebitId = loadInteger(COL_DEBIT);
        Integer myCreditId = loadInteger(COL_CREDIT);
        Integer myCatId = loadInteger(COL_CATEGORY);
        Integer myFreqId = loadInteger(COL_FREQ);
        Integer myParentId = loadInteger(COL_PARENT);

        /* Access the flags */
        Boolean mySplit = loadBoolean(COL_SPLIT);

        /* Access the date */
        JDateDay myDate = loadDate(COL_DATE);

        /* Access the binary values */
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myDate, myDebitId, myCreditId, myAmount, myCatId, myFreqId, mySplit, myParentId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the Account */
        String myDebit = loadString(COL_DEBIT);
        String myCredit = loadString(COL_CREDIT);
        String myCategory = loadString(COL_CATEGORY);
        String myFrequency = loadString(COL_FREQ);

        /* Access the date */
        JDateDay myDate = loadDate(COL_DATE);

        /* Access the string values */
        String myAmount = loadString(COL_AMOUNT);

        /* If we don't have a date */
        if ((myDate == null) && (theLastParent != null)) {
            /* Pick up last date */
            myDate = theLastParent.getDate();

            /* Pick up debit and credit from last values */
            if (myDebit == null) {
                myDebit = theLastDebit;
            }
            if (myCredit == null) {
                myCredit = theLastCredit;
            }

            /* Load the item */
            theList.addOpenItem(pId, myDate, myDebit, myCredit, myAmount, myCategory, myFrequency, Boolean.FALSE, theLastParent);
            theLastParent.setSplit(Boolean.TRUE);
        } else {
            /* Load the item */
            theLastParent = theList.addOpenItem(pId, myDate, myDebit, myCredit, myAmount, myCategory, myFrequency, Boolean.FALSE, null);
        }

        /* Store last credit and debit */
        theLastCredit = myCredit;
        theLastDebit = myDebit;
    }

    @Override
    protected void insertSecureItem(final Pattern pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_FREQ, pItem.getFrequencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
        writeBoolean(COL_SPLIT, pItem.isSplit());
        writeInteger(COL_PARENT, pItem.getParentId());
    }

    @Override
    protected void insertOpenItem(final Pattern pItem) throws JOceanusException {
        /* Determine whether we are a child event */
        boolean isChild = pItem.getParent() == null;

        /* Access debit/credit names */
        String myDebit = pItem.getDebitName();
        String myCredit = pItem.getCreditName();

        /* Write standard values */
        writeString(COL_CATEGORY, pItem.getCategoryName());
        writeString(COL_FREQ, pItem.getFrequencyName());
        writeDecimal(COL_AMOUNT, pItem.getAmount());

        /* If we are a child */
        if (isChild) {
            /* Only fill in debit credit if they are different */
            if (Difference.isEqual(myDebit, theLastDebit)) {
                writeString(COL_DEBIT, myDebit);
            }
            if (Difference.isEqual(myCredit, theLastCredit)) {
                writeString(COL_CREDIT, myCredit);
            }
        } else {
            writeDate(COL_DATE, pItem.getDate());
            writeString(COL_DEBIT, myDebit);
            writeString(COL_CREDIT, myCredit);
        }

        /* Store last credit and debit */
        theLastCredit = myCredit;
        theLastDebit = myDebit;
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_DATE, EventBase.FIELD_DATE.getName());
        writeHeader(COL_DEBIT, EventBase.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, EventBase.FIELD_CREDIT.getName());
        writeHeader(COL_AMOUNT, EventBase.FIELD_AMOUNT.getName());
        writeHeader(COL_CATEGORY, EventBase.FIELD_CATEGORY.getName());
        writeHeader(COL_FREQ, Pattern.FIELD_FREQ.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
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
        return (isBackup())
                           ? COL_PARENT
                           : COL_FREQ;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the patterns */
        theList.validateOnLoad();
    }

    /**
     * Load the Patterns from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
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

            /* Create memory copies */
            Pattern myLastParent = null;
            String myLastDebit = null;
            String myLastCredit = null;

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access date */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                JDateDay myDate = (myCell != null)
                                                  ? myCell.getDateValue()
                                                  : null;

                /* Access the values */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myDebit = (myCell != null)
                                                 ? myCell.getStringValue()
                                                 : null;
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myCredit = (myCell != null)
                                                  ? myCell.getStringValue()
                                                  : null;
                String myAmount = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myCategory = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myFrequency = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();

                /* If we have a null date */
                if ((myDate == null) && (myLastParent != null)) {
                    /* Pick up last date */
                    myDate = myLastParent.getDate();

                    /* Pick up debit and credit from last values */
                    if (myDebit == null) {
                        myDebit = myLastDebit;
                    }
                    if (myCredit == null) {
                        myCredit = myLastCredit;
                    }

                    /* Add the pattern */
                    myList.addOpenItem(0, myDate, myDebit, myCredit, myAmount, myCategory, myFrequency, Boolean.FALSE, myLastParent);
                    myLastParent.setSplit(Boolean.TRUE);
                } else {
                    /* Add the pattern */
                    myLastParent = myList.addOpenItem(0, myDate, myDebit, myCredit, myAmount, myCategory, myFrequency, Boolean.FALSE, null);
                }

                /* Store last credit/debit */
                myLastDebit = myDebit;
                myLastCredit = myCredit;

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the patterns */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
