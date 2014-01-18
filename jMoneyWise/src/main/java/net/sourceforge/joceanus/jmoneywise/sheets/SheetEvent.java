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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfoSet;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventBase;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet.ArchiveYear;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet.YearRange;
import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Event.
 * @author Tony Washer
 */
public class SheetEvent
        extends SheetDataItem<Event> {
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
     * Event info list.
     */
    private final EventInfoList theInfoList;

    /**
     * DataInfoSet Helper.
     */
    private final SheetEventInfoSet theInfoSheet;

    /**
     * Last loaded parent.
     */
    private Event theLastParent = null;

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
    protected SheetEvent(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
        theList = myData.getEvents();
        theInfoList = myData.getEventInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup()
                ? null
                : new SheetEventInfoSet(EventInfoClass.class, this, COL_RECONCILED);
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
        theInfoList = myData.getEventInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup()
                ? null
                : new SheetEventInfoSet(EventInfoClass.class, this, COL_RECONCILED);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myDebitId = loadInteger(COL_DEBIT);
        Integer myCreditId = loadInteger(COL_CREDIT);
        Integer myCatId = loadInteger(COL_CATEGORY);
        Integer myParentId = loadInteger(COL_PARENT);

        /* Load flags */
        Boolean myReconciled = loadBoolean(COL_RECONCILED);
        Boolean mySplit = loadBoolean(COL_SPLIT);

        /* Access the date and years */
        JDateDay myDate = loadDate(COL_DATE);

        /* Access the binary values */
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myDate, myDebitId, myCreditId, myAmount, myCatId, myReconciled, mySplit, myParentId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the Account */
        String myDebit = loadString(COL_DEBIT);
        String myCredit = loadString(COL_CREDIT);
        String myCategory = loadString(COL_CATEGORY);

        /* Access the date and name and description bytes */
        JDateDay myDate = loadDate(COL_DATE);

        /* Load flags */
        Boolean myReconciled = loadBoolean(COL_RECONCILED);

        /* Access the binary values */
        String myAmount = loadString(COL_AMOUNT);

        /* If we don't have a date */
        Event myEvent;
        if ((myDate == null)
            && (theLastParent != null)) {
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
            myEvent = theList.addOpenItem(pId, myDate, myDebit, myCredit, myAmount, myCategory, myReconciled, Boolean.TRUE, theLastParent);
            theLastParent.setSplit(Boolean.TRUE);
        } else {
            /* Load the item */
            myEvent = theList.addOpenItem(pId, myDate, myDebit, myCredit, myAmount, myCategory, myReconciled, Boolean.FALSE, null);
            theLastParent = myEvent;
        }

        /* Store last credit and debit */
        theLastCredit = myCredit;
        theLastDebit = myDebit;

        /* Load infoSet items */
        theInfoSheet.loadDataInfoSet(theInfoList, myEvent);
    }

    @Override
    protected void insertSecureItem(final Event pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
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
    protected void insertOpenItem(final Event pItem) throws JOceanusException {
        /* Determine whether we are a child event */
        boolean isChild = pItem.getParent() != null;

        /* Access debit/credit names */
        String myDebit = pItem.getDebitName();
        String myCredit = pItem.getCreditName();

        /* Write standard values */
        writeDecimal(COL_AMOUNT, pItem.getAmount());
        writeBoolean(COL_RECONCILED, pItem.isReconciled());
        writeString(COL_CATEGORY, pItem.getCategoryName());

        /* If we are a child */
        if (isChild) {
            /* Only fill in debit credit if they are different */
            if (!Difference.isEqual(myDebit, theLastDebit)) {
                writeString(COL_DEBIT, myDebit);
            }
            if (!Difference.isEqual(myCredit, theLastCredit)) {
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

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_DATE, EventBase.FIELD_DATE.getName());
        writeHeader(COL_AMOUNT, EventBase.FIELD_AMOUNT.getName());
        writeHeader(COL_DEBIT, EventBase.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, EventBase.FIELD_CREDIT.getName());
        writeHeader(COL_CATEGORY, EventBase.FIELD_CATEGORY.getName());
        writeHeader(COL_RECONCILED, EventBase.FIELD_RECONCILED.getName());

        /* prepare the info sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_DEBIT);
        setStringColumn(COL_CREDIT);
        setStringColumn(COL_CATEGORY);

        /* Set Number columns */
        setDateColumn(COL_DATE);
        setMoneyColumn(COL_AMOUNT);
        setBooleanColumn(COL_RECONCILED);

        /* Apply validation */
        applyDataValidation(COL_DEBIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_CREDIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_CATEGORY, SheetEventCategoryType.AREA_CATTYPENAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();

        applyDataFilter(COL_DEBIT);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return (isBackup())
                ? COL_PARENT
                : COL_RECONCILED
                  + theInfoSheet.getXtraColumnCount();
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
     * @param pRange the range of tax years
     * @param pLastEvent the last date to load
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final YearRange pRange,
                                         final JDateDay pLastEvent) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Access the list of events */
            EventList myList = pData.getEvents();
            EventInfoList myInfoList = pData.getEventInfo();

            /* Obtain the range iterator */
            ListIterator<ArchiveYear> myIterator = pRange.getReverseIterator();

            /* Loop through the individual year ranges */
            while (myIterator.hasPrevious()) {
                /* Access year */
                ArchiveYear myYear = myIterator.previous();

                /* Find the range of cells */
                DataView myView = pWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                if (!pTask.setNewStage("Events from "
                                       + myYear.getDate().getYear())) {
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
                    if ((myDate != null)
                        && (pLastEvent.compareTo(myDate) < 0)) {
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

                    /* If we have a null date */
                    Event myEvent;
                    if ((myDate == null)
                        && (myLastParent != null)) {
                        /* Pick up last date */
                        myDate = myLastParent.getDate();

                        /* Pick up debit and credit from last values */
                        if (myDebit == null) {
                            myDebit = myLastDebit;
                        }
                        if (myCredit == null) {
                            myCredit = myLastCredit;
                        }

                        /* Add the event */
                        myEvent = myList.addOpenItem(0, myDate, myDebit, myCredit, myAmount, myCategory, myReconciled, Boolean.TRUE, myLastParent);
                        myLastParent.setSplit(Boolean.TRUE);
                    } else {
                        /* Add the event */
                        myEvent = myList.addOpenItem(0, myDate, myDebit, myCredit, myAmount, myCategory, myReconciled, Boolean.FALSE, null);
                        myLastParent = myEvent;
                    }

                    /* Store last credit/debit */
                    myLastDebit = myDebit;
                    myLastCredit = myCredit;

                    /* Add information relating to the account */
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.COMMENTS, myDesc);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.TAXCREDIT, myTaxCredit);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.NATINSURANCE, myNatInsurance);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.DEEMEDBENEFIT, myBenefit);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.DEBITUNITS, myDebitUnits);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.CREDITUNITS, myCreditUnits);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.DILUTION, myDilution);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.REFERENCE, myReference);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.QUALIFYYEARS, myYears);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.CHARITYDONATION, myDonation);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.THIRDPARTY, myThirdParty);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0)
                        && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* If the year is too late */
                if (pLastEvent.compareTo(myYear.getDate()) < 0) {
                    /* Break the loop */
                    break;
                }
            }

            /* Sort the list */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the list */
            myList.validateOnLoad();

            /* Handle Exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to load Events", e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * EventInfoSet sheet.
     */
    private static class SheetEventInfoSet
            extends SheetDataInfoSet<EventInfo, Event, EventInfoType, EventInfoClass> {

        /**
         * Constructor.
         * @param pClass the info type class
         * @param pOwner the Owner
         * @param pBaseCol the base column
         */
        public SheetEventInfoSet(final Class<EventInfoClass> pClass,
                                 final SheetDataItem<Event> pOwner,
                                 final int pBaseCol) {
            super(pClass, pOwner, pBaseCol);
        }

        @Override
        public void formatSheet() throws JOceanusException {
            /* Apply basic formatting */
            super.formatSheet();

            /* Set the Validations */
            applyDataValidation(EventInfoClass.THIRDPARTY, SheetAccount.AREA_ACCOUNTNAMES);
        }
    }
}
