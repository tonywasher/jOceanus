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

import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.sheets.ArchiveLoader.ArchiveYear;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Event.
 * @author Tony Washer
 */
public class SheetEvent
        extends SheetEncrypted<Event, MoneyWiseDataType> {
    /**
     * NamedArea for Events.
     */
    private static final String AREA_EVENTS = Event.LIST_NAME;

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
     * Reconciled column.
     */
    private static final int COL_RECONCILED = COL_CATEGORY + 1;

    /**
     * Split column.
     */
    private static final int COL_SPLIT = COL_RECONCILED + 1;

    /**
     * Reconciled column.
     */
    private static final int COL_PARENT = COL_SPLIT + 1;

    /**
     * Events data list.
     */
    private final EventList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEvent(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
        theList = myData.getEvents();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEvent(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTS);

        /* Access the Events list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getEvents();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Event.OBJECT_NAME);
        myValues.addValue(Event.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(Event.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Event.FIELD_DEBIT, loadInteger(COL_DEBIT));
        myValues.addValue(Event.FIELD_CREDIT, loadInteger(COL_CREDIT));
        myValues.addValue(Event.FIELD_AMOUNT, loadBytes(COL_AMOUNT));
        myValues.addValue(Event.FIELD_RECONCILED, loadBoolean(COL_RECONCILED));
        myValues.addValue(Event.FIELD_SPLIT, loadBoolean(COL_SPLIT));
        myValues.addValue(Event.FIELD_PARENT, loadInteger(COL_PARENT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Event pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeBoolean(COL_RECONCILED, pItem.isReconciled());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
        writeBoolean(COL_SPLIT, pItem.isSplit());
        writeInteger(COL_PARENT, pItem.getParentId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PARENT;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();
    }

    /**
     * Load the Events from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @param pLastEvent the last date to load
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final ArchiveLoader pLoader,
                                         final JDateDay pLastEvent) throws JOceanusException {
        /* Access the list of events */
        EventList myList = pData.getEvents();
        EventInfoList myInfoList = pData.getEventInfo();

        /* Protect against exceptions */
        try {
            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Obtain the range iterator */
            ListIterator<ArchiveYear> myIterator = pLoader.getReverseIterator();

            /* Loop through the individual year ranges */
            while (myIterator.hasPrevious()) {
                /* Access year */
                ArchiveYear myYear = myIterator.previous();

                /* Find the range of cells */
                DataView myView = pWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                if (!pTask.setNewStage("Events from " + myYear.getDate().getYear())) {
                    return false;
                }

                /* Count the number of Events */
                int myTotal = myView.getRowCount();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Create memory copies */
                Event myLastParent = null;
                String myLastDebit = null;
                String myLastCredit = null;

                /* Loop through the rows of the table */
                for (int i = 0; i < myTotal; i++) {
                    /* Access the row */
                    DataRow myRow = myView.getRowByIndex(i);
                    int iAdjust = 0;

                    /* Access date */
                    DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    JDateDay myDate = (myCell != null)
                                                      ? myCell.getDateValue()
                                                      : null;

                    /* If the event is too late */
                    if ((myDate != null) && (pLastEvent.compareTo(myDate) < 0)) {
                        /* Break the loop */
                        break;
                    }

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

                    /* Handle Reconciled which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    Boolean myReconciled = Boolean.FALSE;
                    if (myCell != null) {
                        myReconciled = Boolean.TRUE;
                    }

                    /* Handle Description which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myDesc = null;
                    if (myCell != null) {
                        myDesc = myCell.getStringValue();
                    }

                    /* Handle Tax Credit which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myTaxCredit = null;
                    if (myCell != null) {
                        myTaxCredit = myCell.getStringValue();
                    }

                    /* Handle NatInsurance which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myNatInsurance = null;
                    if (myCell != null) {
                        myNatInsurance = myCell.getStringValue();
                    }

                    /* Handle Benefit which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myBenefit = null;
                    if (myCell != null) {
                        myBenefit = myCell.getStringValue();
                    }

                    /* Handle DebitUnits which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myDebitUnits = null;
                    if (myCell != null) {
                        myDebitUnits = myCell.getStringValue();
                    }

                    /* Handle CreditUnits which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myCreditUnits = null;
                    if (myCell != null) {
                        myCreditUnits = myCell.getStringValue();
                    }

                    /* Handle Dilution which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myDilution = null;
                    if (myCell != null) {
                        myDilution = myCell.getStringValue();
                        if (!myDilution.startsWith("0.")) {
                            myDilution = null;
                        }
                    }

                    /* Handle Reference which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myReference = null;
                    if (myCell != null) {
                        myReference = myCell.getStringValue();
                    }

                    /* Handle Years which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    Integer myYears = null;
                    if (myCell != null) {
                        myYears = myCell.getIntegerValue();
                    }

                    /* Handle Donation which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myDonation = null;
                    if (myCell != null) {
                        myDonation = myCell.getStringValue();
                    }

                    /* Handle ThirdParty which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myThirdParty = null;
                    if (myCell != null) {
                        myThirdParty = myCell.getStringValue();
                    }

                    /* Handle TagList which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                    String myTagList = null;
                    if (myCell != null) {
                        myTagList = myCell.getStringValue();
                    }

                    /* Set defaults */
                    Boolean isSplit = Boolean.FALSE;
                    Event myParent = null;

                    /* If we don't have a date */
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

                        /* Mark parent as split */
                        isSplit = Boolean.TRUE;
                        myLastParent.setSplit(Boolean.TRUE);
                        myParent = myLastParent;
                    }

                    /* Build data values */
                    DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Event.OBJECT_NAME);
                    myValues.addValue(Event.FIELD_DATE, myDate);
                    myValues.addValue(Event.FIELD_CATEGORY, myCategory);
                    myValues.addValue(Event.FIELD_DEBIT, myDebit);
                    myValues.addValue(Event.FIELD_CREDIT, myCredit);
                    myValues.addValue(Event.FIELD_AMOUNT, myAmount);
                    myValues.addValue(Event.FIELD_RECONCILED, myReconciled);
                    myValues.addValue(Event.FIELD_SPLIT, isSplit);
                    myValues.addValue(Event.FIELD_PARENT, myParent);

                    /* Add the event */
                    Event myEvent = myList.addValuesItem(myValues);

                    /* If we were not a child */
                    if (myParent == null) {
                        /* Note the last parent */
                        myLastParent = myEvent;
                    }

                    /* Store last credit/debit */
                    myLastDebit = myDebit;
                    myLastCredit = myCredit;

                    /* Add information relating to the account */
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.COMMENTS, myDesc);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.TAXCREDIT, myTaxCredit);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.NATINSURANCE, myNatInsurance);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.DEEMEDBENEFIT, myBenefit);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.DEBITUNITS, myDebitUnits);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.CREDITUNITS, myCreditUnits);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.DILUTION, myDilution);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.REFERENCE, myReference);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.QUALIFYYEARS, myYears);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.CHARITYDONATION, myDonation);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.THIRDPARTY, myThirdParty);
                    myInfoList.addInfoItem(null, myEvent, EventInfoClass.EVENTTAG, myTagList);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* If the year is too late */
                if (pLastEvent.compareTo(myYear.getDate()) < 0) {
                    /* Break the loop */
                    break;
                }
            }

            /* Resolve ValueLinks */
            myInfoList.resolveValueLinks();

            /* Sort the list */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the list */
            myList.validateOnLoad();

            /* Handle Exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
