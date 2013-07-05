/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory.EventCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventGroup;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.CapitalEvent.CapitalAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.DilutionEvent.DilutionEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.Statement.StatementLine;
import net.sourceforge.jOceanus.jMoneyWise.views.Statement.StatementLines;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * Class to analyse events.
 * @author Tony Washer
 */
public class EventAnalysis
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(EventAnalysis.class.getSimpleName());

    /**
     * Analysis field Id.
     */
    public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

    /**
     * Years field Id.
     */
    private static final JDataField FIELD_YEARS = FIELD_DEFS.declareLocalField("Years");

    /**
     * Account field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    /**
     * Date field Id.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

    /**
     * Dilutions field Id.
     */
    private static final JDataField FIELD_DILUTIONS = FIELD_DEFS.declareLocalField("Dilutions");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return (theAnalysis == null)
                    ? JDataFieldValue.SkipField
                    : theAnalysis;
        }
        if (FIELD_YEARS.equals(pField)) {
            return (theYears == null)
                    ? JDataFieldValue.SkipField
                    : theYears;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return (theAccount == null)
                    ? JDataFieldValue.SkipField
                    : theAccount;
        }
        if (FIELD_DATE.equals(pField)) {
            return (theDate == null)
                    ? JDataFieldValue.SkipField
                    : theDate;
        }
        if (FIELD_DILUTIONS.equals(pField)) {
            return (theDilutions == null)
                    ? JDataFieldValue.SkipField
                    : theDilutions;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Amount Tax threshold for "small" transactions (�3000).
     */
    private static final JMoney LIMIT_VALUE = JMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    private static final JRate LIMIT_RATE = JRate.getWholePercentage(5);

    /**
     * The dataSet being analysed.
     */
    private final FinanceData theData;

    /**
     * The event categories.
     */
    private final EventCategoryList theCategories;

    /**
     * The analysis.
     */
    private Analysis theAnalysis = null;

    /**
     * The account bucket list.
     */
    private AccountBucketList theAccountBuckets = null;

    /**
     * The event category buckets.
     */
    private EventCategoryBucketList theCategoryBuckets = null;

    /**
     * The taxMan account.
     */
    private AccountBucket theTaxMan = null;

    /**
     * The metaAnalysis.
     */
    private MetaAnalysis theMetaAnalysis = null;

    /**
     * The set of TaxYear analyses.
     */
    private AnalysisYearList theYears = null;

    /**
     * The account being analysed.
     */
    private AccountBucket theAccount = null;

    /**
     * The date for the analysis.
     */
    private JDateDay theDate = null;

    /**
     * The dilutions.
     */
    private DilutionEventList theDilutions = null;

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the metaAnalysis.
     * @return the analysis
     */
    public MetaAnalysis getMetaAnalysis() {
        return theMetaAnalysis;
    }

    /**
     * Obtain the list of taxYear analyses.
     * @return the list
     */
    public AnalysisYearList getAnalysisYears() {
        return theYears;
    }

    /**
     * Obtain the analysis for a particular tax year.
     * @param pYear the tax year
     * @return the analysis
     */
    public AnalysisYear getAnalysisYear(final TaxYear pYear) {
        return (theYears == null)
                ? null
                : theYears.findItemForYear(pYear);
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor for a dated analysis.
     * @param pData the data to analyse events for
     * @param pDate the Date for the analysis
     * @throws JDataException on error
     */
    public EventAnalysis(final FinanceData pData,
                         final JDateDay pDate) throws JDataException {
        /* Store the parameters */
        theData = pData;
        theDate = pDate;

        /* Access event categories and taxMan */
        theCategories = theData.getEventCategories();
        Account myTaxMan = theData.getAccounts().getSingularClass(AccountCategoryClass.TaxMan);

        /* Create the analysis */
        theAnalysis = new Analysis(theData, theDate);

        /* Access details from the analysis */
        theAccountBuckets = theAnalysis.getAccounts();
        theCategoryBuckets = theAnalysis.getEventCategories();
        theTaxMan = theAccountBuckets.getBucket(myTaxMan);

        /* Create associated MetaAnalyser */
        theMetaAnalysis = new MetaAnalysis(theAnalysis);

        /* Access the events and the iterator */
        EventList myEvents = pData.getEvents();
        Iterator<Event> myIterator = myEvents.iterator();

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* Check the range and exit loop if necessary */
            int myResult = theDate.compareTo(myCurr.getDate());
            if (myResult < 0) {
                break;
            }

            /* Process the event in the asset report */
            processEvent(myCurr);
        }

        /* Analyse accounts */
        theMetaAnalysis.analyseAccounts();
    }

    /**
     * Constructor for a statement analysis.
     * @param pData the data to analyse events for
     * @param pStatement the statement to prepare
     * @throws JDataException on error
     */
    public EventAnalysis(final FinanceData pData,
                         final Statement pStatement) throws JDataException {
        /* Access key points of the statement */
        JDateDayRange myRange = pStatement.getDateRange();
        Account myAccount = pStatement.getAccount();
        StatementLines myList = pStatement.getLines();

        /* Store the parameters */
        theData = pData;
        theDate = myRange.getStart();

        /* Access event categories and taxMan */
        theCategories = theData.getEventCategories();
        Account myTaxMan = theData.getAccounts().getSingularClass(AccountCategoryClass.TaxMan);

        /* Create the analysis */
        theAnalysis = new Analysis(theData, theDate);

        /* Access details from the analysis */
        theAccountBuckets = theAnalysis.getAccounts();
        theCategoryBuckets = theAnalysis.getEventCategories();
        theAccount = theAccountBuckets.getBucket(myAccount);
        theTaxMan = theAccountBuckets.getBucket(myTaxMan);

        /* Access the events and the iterator */
        EventList myEvents = pData.getEvents();
        OrderedListIterator<Event> myIterator = myEvents.listIterator();

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Check the range and exit loop if required */
            int myResult = myRange.compareTo(myCurr.getDate());
            if (myResult != 1) {
                break;
            }

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* Ignore items that do not relate to this account */
            if (!myCurr.relatesTo(myAccount)) {
                continue;
            }

            /* Process the event in the asset report */
            processEvent(myCurr);
        }

        /* move the iterator back one */
        if (myIterator.hasPrevious()) {
            myIterator.previous();
        }

        /* create a save point */
        theAccount.createSavePoint();

        /* Set starting balance and units for account */
        pStatement.setStartBalances(theAccount);

        /* Add the opening balance to the statement */
        StatementLine myLine = new StatementLine(pStatement);
        myList.append(myLine);

        /* Continue looping through the Events extracting relevant elements */
        boolean addChildren = false;
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* Check the range and exit loop if required */
            int myResult = myRange.compareTo(myCurr.getDate());
            if (myResult == -1) {
                break;
            }

            /* If this is a split line */
            if (myCurr.isSplit()) {
                /* If this is the parent */
                if (!myCurr.isChild()) {
                    /* Access the event group */
                    EventGroup<Event> myGroup = myEvents.getGroup(myCurr);

                    /* Set flag for subsequent children */
                    addChildren = (myGroup.relatesTo(myAccount));
                }

                /* Ignore items that do not relate to this account */
                if (!addChildren) {
                    continue;
                }

                /* else this is a standard line */
            } else {
                /* Clear addChildren flag */
                addChildren = false;

                /* Ignore items that do not relate to this account */
                if (!myCurr.relatesTo(myAccount)) {
                    continue;
                }
            }

            /* Add a statement line to the statement */
            myLine = new StatementLine(myList, myCurr);
            myList.append(myLine);

            /* If this is a child line */
            if (myLine.isChild()) {
                /* Register child against parent (in this edit list) */
                myList.registerChild(myLine);
            }
        }

        /* Reset the statement balances */
        resetStatementBalance(pStatement);
    }

    /**
     * recalculate statement balance.
     * @param pStatement the statement
     * @throws JDataException on error
     */
    protected final void resetStatementBalance(final Statement pStatement) throws JDataException {
        /* If we don't have balances just return */
        if (!theAccount.getCategoryType().hasBalances()) {
            return;
        }

        /* Access the correct iterator */
        Iterator<StatementLine> myIterator = pStatement.getIterator();

        /* Restore the SavePoint */
        theAccount.restoreSavePoint(theDate);

        /* Loop through the lines adjusting the balance */
        while (myIterator.hasNext()) {
            StatementLine myCurr = myIterator.next();
            /* Skip deleted lines */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* Ignore if it is not a valid event */
            if ((myCurr.getPartner() == null)
                || (myCurr.getCategory() == null)
                || (myCurr.getAmount() == null)) {
                continue;
            }

            /* Process the event */
            processEvent(myCurr);

            /* Update the balances */
            myCurr.setBalances();
        }

        /* Set the ending balances */
        pStatement.setEndBalances();
    }

    /**
     * Constructor for a full year set of accounts.
     * @param pView the Data view
     * @param pData the Data to analyse
     * @throws JDataException on error
     */
    public EventAnalysis(final View pView,
                         final FinanceData pData) throws JDataException {
        /* Store the parameters */
        theData = pData;

        /* Access the Categories and TaxMan account */
        theCategories = theData.getEventCategories();
        Account myTaxMan = theData.getAccounts().getSingularClass(AccountCategoryClass.TaxMan);

        /* Access the top level debug entry for this analysis */
        JDataEntry mySection = pView.getDataEntry(DataControl.DATA_ANALYSIS);

        /* Create a list of AnalysisYears */
        theYears = new AnalysisYearList(this, pView.getPreferenceMgr());

        /* Create the Dilution Event List */
        theDilutions = new DilutionEventList(theData);

        /* Access the lists */
        TaxYearList myTaxYears = theData.getTaxYears();
        EventList myEvents = theData.getEvents();

        /* Access the Event iterator */
        Iterator<Event> myIterator = myEvents.listIterator();
        TaxYear myTax = null;
        JDateDay myDate = null;
        IncomeBreakdown myBreakdown = null;
        int myResult = -1;

        /* Reset the groups */
        myEvents.resetGroups();

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();
            JDateDay myCurrDay = myCurr.getDate();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* If we have a current tax year */
            if (myDate != null) {
                /* Check that this event is still in the tax year */
                myResult = myDate.compareTo(myCurrDay);
            }

            /* If we have exhausted the tax year or else this is the first tax year */
            if (myResult < 0) {
                /* Access the relevant tax year */
                myTax = myTaxYears.findTaxYearForDate(myCurrDay);
                myDate = myTax.getTaxYear();

                /* If we have an existing meta analysis year */
                if (theMetaAnalysis != null) {
                    /* analyse accounts */
                    theMetaAnalysis.analyseAccounts();
                }

                /* Create the new Analysis */
                AnalysisYear myYear = theYears.getNewAnalysis(myTax, theAnalysis);
                theAnalysis = myYear.getAnalysis();
                theMetaAnalysis = myYear.getMetaAnalysis();
                myBreakdown = myYear.getBreakdown();

                /* Access details from the analysis */
                theAccountBuckets = theAnalysis.getAccounts();
                theCategoryBuckets = theAnalysis.getEventCategories();
                theTaxMan = theAccountBuckets.getBucket(myTaxMan);
            }

            /* Touch credit account */
            Account myAccount = myCurr.getCredit();
            myAccount.touchItem(myCurr);

            /* Touch debit accounts */
            myAccount = myCurr.getDebit();
            myAccount.touchItem(myCurr);

            /* Touch Category */
            EventCategory myCategory = myCurr.getCategory();
            myCategory.touchItem(myCurr);

            /* If the event has a parent */
            Event myParent = myCurr.getParent();
            if (myParent != null) {
                /* Touch the parent */
                myParent.touchItem(myCurr);

                /* Register child against parent */
                myEvents.registerChild(myCurr);
            }

            /* If the event has a dilution factor */
            if (myCurr.getDilution() != null) {
                /* Add to the dilution event list */
                theDilutions.addDilution(myCurr);
            }

            /* Process the event in the breakdown */
            myBreakdown.processEvent(myCurr);

            /* Process the event in the report set */
            processEvent(myCurr);
            myTax.touchItem(myCurr);
        }

        /* analyse accounts */
        if (theMetaAnalysis != null) {
            theMetaAnalysis.analyseAccounts();
        }

        /* Update analysis object */
        mySection.setObject(this);
    }

    /**
     * An analysis of a taxYear.
     */
    public static final class AnalysisYear
            implements OrderedIdItem<Integer>, JDataContents, Comparable<AnalysisYear> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisYear.class.getSimpleName());

        /**
         * Analysis field id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

        /**
         * TaxYear field id.
         */
        public static final JDataField FIELD_YEAR = FIELD_DEFS.declareEqualityField("TaxYear");

        /**
         * Breakdown field id.
         */
        public static final JDataField FIELD_BREAKDOWN = FIELD_DEFS.declareLocalField("IncomeBreakdown");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_YEAR.equals(pField)) {
                return theYear;
            }
            if (FIELD_BREAKDOWN.equals(pField)) {
                return theBreakdown;
            }
            return JDataFieldValue.UnknownField;
        }

        @Override
        public String formatObject() {
            return theYear.toString()
                   + " Analysis";
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The metaAnalysis.
         */
        private final MetaAnalysis theMetaAnalysis;

        /**
         * The incomeBreakdown.
         */
        private final IncomeBreakdown theBreakdown;

        /**
         * The preference manager.
         */
        private final PreferenceManager thePreferenceMgr;

        /**
         * The taxYear.
         */
        private final TaxYear theYear;

        /**
         * The dataSet.
         */
        private final FinanceData theData;

        /**
         * has tax been calculated?
         */
        private boolean taxCalculated = false;

        /**
         * Obtain the Date.
         * @return the date.
         */
        public JDateDay getDate() {
            return theYear.getTaxYear();
        }

        /**
         * Obtain the TaxYear.
         * @return the taxYear.
         */
        public TaxYear getTaxYear() {
            return theYear;
        }

        /**
         * Obtain the Analysis.
         * @return the Analysis.
         */
        public Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the MetaAnalysis.
         * @return the metaAnalysis.
         */
        public MetaAnalysis getMetaAnalysis() {
            return theMetaAnalysis;
        }

        /**
         * Obtain the Income Breakdown.
         * @return the breakdown.
         */
        public IncomeBreakdown getBreakdown() {
            return theBreakdown;
        }

        @Override
        public Integer getOrderedId() {
            return theYear.getId();
        }

        /**
         * Constructor for the Analysis Year.
         * @param pData the DataSet
         * @param pManager the preference manager
         * @param pYear the Tax Year
         * @param pPrevious the previous analysis
         */
        private AnalysisYear(final FinanceData pData,
                             final PreferenceManager pManager,
                             final TaxYear pYear,
                             final Analysis pPrevious) {
            /* Store data */
            theData = pData;
            thePreferenceMgr = pManager;

            /* Store tax year */
            theYear = pYear;

            /* Create new analysis */
            theAnalysis = new Analysis(theData, pYear, pPrevious);

            /* Create associated MetaAnalyser */
            theMetaAnalysis = new MetaAnalysis(theAnalysis);

            /* Create the breakdown */
            theBreakdown = new IncomeBreakdown(theData);
        }

        @Override
        public int compareTo(final AnalysisYear pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Compare the bucket order */
            return getDate().compareTo(pThat.getDate());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (getClass() != pThat.getClass()) {
                return false;
            }

            /* Access as AnalysisYear */
            AnalysisYear myThat = (AnalysisYear) pThat;

            /* Check equality */
            return Difference.isEqual(getDate(), myThat.getDate());
        }

        @Override
        public int hashCode() {
            return getDate().hashCode();
        }

        /**
         * calculate tax.
         */
        protected void calculateTax() {
            /* If tax has not yet been calculated */
            if (!taxCalculated) {
                /* Calculate the tax and record the fact */
                theMetaAnalysis.calculateTax(thePreferenceMgr);
                taxCalculated = true;
            }
        }
    }

    /**
     * The AnalysisYear list class.
     */
    public static class AnalysisYearList
            extends OrderedIdList<Integer, AnalysisYear>
            implements JDataContents {
        /**
         * List name.
         */
        public static final String LIST_NAME = AnalysisYearList.class.getSimpleName();

        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME);

        /**
         * Events Field Id.
         */
        public static final JDataField FIELD_EVENTS = FIELD_DEFS.declareLocalField("Events");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_EVENTS.equals(pField)) {
                return theEvents;
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * The events.
         */
        private final EventAnalysis theEvents;

        /**
         * The preference manager.
         */
        private final PreferenceManager thePreferenceMgr;

        /**
         * Construct a top-level List.
         * @param pEvents the events
         * @param pManager the preference manager
         */
        public AnalysisYearList(final EventAnalysis pEvents,
                                final PreferenceManager pManager) {
            /* Call super constructor */
            super(AnalysisYear.class);
            theEvents = pEvents;
            thePreferenceMgr = pManager;
        }

        /**
         * Add new analysis based on the previous analysis.
         * @param pYear the tax year
         * @param pAnalysis the previous analysis
         * @return the bucket
         */
        protected AnalysisYear getNewAnalysis(final TaxYear pYear,
                                              final Analysis pAnalysis) {
            /* Locate the bucket in the list */
            AnalysisYear myYear = new AnalysisYear(theEvents.theData, thePreferenceMgr, pYear, pAnalysis);
            append(myYear);
            return myYear;
        }

        /**
         * Search for tax year.
         * @param pYear the tax year to search for
         * @return the analysis
         */
        public AnalysisYear findItemForYear(final TaxYear pYear) {
            /* Access the list iterator */
            Iterator<AnalysisYear> myIterator = listIterator();

            /* Loop through the tax parameters */
            while (myIterator.hasNext()) {
                AnalysisYear myCurr = myIterator.next();
                /* Break on match */
                if (myCurr.theYear.compareTo(pYear) == 0) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }
    }

    /**
     * Process an event.
     * @param pEvent the event to process
     * @throws JDataException on error
     */
    private void processEvent(final Event pEvent) throws JDataException {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();

        /* If the event relates to a priced item split out the workings */
        if ((myDebit.hasUnits())
            || (myCredit.hasUnits())) {
            /* Process as a Capital event */
            processCapitalEvent(pEvent);

            /* Else handle the event normally */
        } else {
            EventCategory myCat = pEvent.getCategory();

            /* If the event is interest */
            if (pEvent.isInterest()) {
                /* If the account is tax free */
                if (myDebit.isTaxFree()) {
                    /* The true transaction type is TaxFreeInterest */
                    myCat = theCategories.getSingularClass(EventCategoryClass.TaxFreeInterest);
                }

                /* True debit account is the parent */
                myDebit = myDebit.getParent();
            }

            /* Adjust the debit account bucket */
            AccountBucket myBucket = theAccountBuckets.getBucket(myDebit);

            /* If the debit account is auto-Expense */
            EventCategory myAuto = myDebit.getAutoExpense();
            if (myAuto != null) {
                /* Subtract the expense */
                myBucket.subtractExpense(myAmount);

                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(myAmount);

                /* else handle normally */
            } else {
                myBucket.adjustForDebit(pEvent);
            }

            /* Adjust the credit account bucket */
            myBucket = theAccountBuckets.getBucket(myCredit);

            /* If the credit account is auto-Expense */
            myAuto = myCredit.getAutoExpense();
            if (myAuto != null) {
                /* Add the expense */
                myBucket.addExpense(myAmount);

                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(myAmount);

                /* else handle normally */
            } else {
                myBucket.adjustForCredit(pEvent);
            }

            /* Adjust the tax payments */
            theTaxMan.adjustForTaxPayments(pEvent);

            /* If the event category is not a transfer */
            if (!myCat.isTransfer()) {
                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
                myCatBucket.adjustValues(pEvent);
            }
        }
    }

    /**
     * Process a capital event.
     * @param pEvent the event to process
     * @throws JDataException on error
     */
    private void processCapitalEvent(final Event pEvent) throws JDataException {
        EventCategory myCat = pEvent.getCategory();

        /* Switch on the category */
        switch (myCat.getCategoryTypeClass()) {
        /* Process a stock split */
            case StockSplit:
            case StockAdjust:
                processStockSplit(pEvent);
                break;
            /* Process a stock right taken */
            case StockRightsTaken:
                processTransferIn(pEvent);
                break;
            /* Process a stock right taken */
            case StockRightsWaived:
                processStockRightWaived(pEvent);
                break;
            /* Process a stock DeMerger */
            case StockDeMerger:
                processStockDeMerger(pEvent);
                break;
            /* Process a Cash TakeOver TODO */
            // case CashTakeOver:
            // processCashTakeover(pEvent);
            // break;
            /* Process a Cash TakeOver */
            case StockTakeOver:
                processStockTakeover(pEvent);
                break;
            /* Process a dividend */
            case Dividend:
                processDividend(pEvent);
                break;
            /* Process standard transfer in/out */
            case Transfer:
            case Expense:
            case Inherited:
            case OtherIncome:
                if (pEvent.getDebit().isCategoryClass(AccountCategoryClass.LifeBond)) {
                    processTaxableGain(pEvent);
                } else if (pEvent.getCredit().hasUnits()) {
                    processTransferIn(pEvent);
                } else {
                    processTransferOut(pEvent);
                }
                break;
            /* Throw an Exception */
            default:
                throw new JDataException(ExceptionClass.LOGIC, "Unexpected category type: "
                                                               + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process an event that is a stock split.
     * <p>
     * This capital event relates only to the Credit Account since the debit account is the same.
     * @param pEvent the event
     */
    private void processStockSplit(final Event pEvent) {
        /* Stock split has identical credit/debit so just obtain credit account */
        Account myAccount = pEvent.getCredit();
        JUnits myDelta = pEvent.getCreditUnits();
        if (myDelta == null) {
            myDelta = new JUnits(pEvent.getDebitUnits());
            myDelta.negate();
        }

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event and adjust the units */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
        myEvent.adjustUnits(myUnits, myDelta);

        /* StockSplit/Adjust is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is a transfer into capital (also StockRightTaken).
     * <p>
     * This capital event relates only to the Credit Account.
     * @param pEvent the event
     */
    private void processTransferIn(final Event pEvent) {
        /* Transfer is to the credit account and may or may not have a change to the units */
        Account myAccount = pEvent.getCredit();
        Account myDebit = pEvent.getDebit();
        JUnits myDeltaUnits = pEvent.getCreditUnits();
        JMoney myAmount = pEvent.getAmount();
        EventCategory myCat = pEvent.getCategory();

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event and adjust cost */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);
        myEvent.adjustCost(myCost, myAmount);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        myEvent.adjustInvested(myInvested, myAmount);

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change and adjust units */
            JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
            myEvent.adjustUnits(myUnits, myDeltaUnits);
        }

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Adjust the debit account bucket for the debit */
        AccountBucket myBucket = theAccountBuckets.getBucket(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant event category bucket */
            EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
            myCatBucket.adjustValues(pEvent);
        }
    }

    /**
     * Process a dividend event. This capital event relates to the only to Debit account,
     * @param pEvent the event
     */
    private void processDividend(final Event pEvent) {
        /* The main account that we are interested in is the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        EventCategory myCat = pEvent.getCategory();
        JMoney myAmount = pEvent.getAmount();
        JMoney myTaxCredit = pEvent.getTaxCredit();
        JUnits myDeltaUnits = pEvent.getCreditUnits();

        /* If the account is tax free */
        if (myAccount.isTaxFree()) {
            /* The true category type is TaxFreeDividend */
            myCat = theCategories.getSingularClass(EventCategoryClass.TaxFreeDividend);

            /* else if the account is a unit trust */
        } else if (myAccount.isCategoryClass(AccountCategoryClass.UnitTrust)) {
            /* The true category type is UnitTrustDividend */
            myCat = theCategories.getSingularClass(EventCategoryClass.UnitTrustDividend);
        }

        /* True debit account is the parent */
        Account myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* If this is a re-investment */
        if (myAccount.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);
            myEvent.adjustCost(myCost, myAmount);

            /* Record the current/delta investment */
            JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
            myEvent.adjustInvested(myInvested, myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record current and delta units */
                JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
                myEvent.adjustUnits(myUnits, myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                JMoney myDividend = myAsset.getMoneyAttribute(AccountAttribute.Dividend);
                myEvent.adjustDividend(myDividend, myTaxCredit);
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            JMoney myAdjust = new JMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            JMoney myDividend = myAsset.getMoneyAttribute(AccountAttribute.Dividend);
            myEvent.adjustDividend(myDividend, myAdjust);

            /* Adjust the credit account bucket */
            myBucket = theAccountBuckets.getBucket(myCredit);
            myBucket.adjustForCredit(pEvent);
        }

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Adjust the relevant transaction bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
    }

    /**
     * Process an event that is a transfer from capital. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTransferOut(final Event pEvent) {
        /* Transfer out is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();
        JUnits myDeltaUnits = pEvent.getDebitUnits();
        EventCategory myCat = pEvent.getCategory();

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myEvent.adjustInvested(myInvested, myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myEvent.adjustUnits(myUnits, myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.adjustCost(myCost, myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            JMoney myGains = myAsset.getMoneyAttribute(AccountAttribute.Gains);
            myEvent.adjustGains(myGains, myDeltaGains);
        }

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant transaction bucket */
            EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
            myCatBucket.adjustValues(pEvent);
        }
    }

    /**
     * Process an event that is a taxable gain. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTaxableGain(final Event pEvent) {
        /* Transfer in is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();
        JUnits myDeltaUnits = pEvent.getDebitUnits();
        EventCategory myCat = pEvent.getCategory();

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myEvent.adjustInvested(myInvested, myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myEvent.adjustUnits(myUnits, myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.adjustCost(myCost, myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            JMoney myGains = myAsset.getMoneyAttribute(AccountAttribute.Gains);
            myEvent.adjustGains(myGains, myDeltaGains);
        }

        /* True debit account is the parent */
        // Account myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        // AccountBucket myDebitBucket = theAccountBuckets.getBucket(myDebit);
        // myDebitBucket.adjustForTaxGainTaxCredit(pEvent);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant category bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
        // myCatBucket.getAmount().subtractAmount(myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Add the chargeable event */
        theAnalysis.getCharges().addEvent(pEvent, myDeltaGains);
    }

    /**
     * Process an event that is stock right waived. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processStockRightWaived(final Event pEvent) {
        /* Stock Right Waived is from the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPriceList myPrices = theData.getPrices();
        JMoney myAmount = pEvent.getAmount();
        JMoney myReduction;

        /* Access the Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myEvent.adjustInvested(myInvested, myDelta);

        /* Get the appropriate price for the account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myAccount, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();
        myEvent.setAttribute(CapitalAttribute.InitialPrice, myPrice);

        /* Determine value of this stock at the current time */
        JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
        JMoney myValue = myUnits.valueAtPrice(myPrice);
        myEvent.setAttribute(CapitalAttribute.InitialValue, myValue);

        /* Access the current cost */
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Determine the total value of rights plus share value */
            JMoney myTotalValue = new JMoney(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the reduction as a proportion of the total value */
            myReduction = myCost.valueAtWeight(myAmount, myTotalValue);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            myReduction = new JMoney(myAmount);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Calculate the delta cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.adjustCost(myCost, myDeltaCost);

        /* Calculate the delta gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* Record the current/delta gains */
        JMoney myGains = myAsset.getMoneyAttribute(AccountAttribute.Gains);
        myEvent.adjustGains(myGains, myDeltaGains);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* StockRightWaived is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is Stock DeMerger. This capital event relates to both the Credit and Debit accounts
     * @param pEvent the event
     */
    private void processStockDeMerger(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JDilution myDilution = pEvent.getDilution();
        JUnits myDeltaUnits = pEvent.getCreditUnits();

        /* Access the Debit Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Calculate the diluted value of the Debit account */
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);
        JMoney myNewCost = myCost.getDilutedMoney(myDilution);

        /* Calculate the delta to the cost */
        JMoney myDeltaCost = new JMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the current/delta cost */
        myEvent.adjustCost(myCost, myDeltaCost);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        myEvent.adjustInvested(myInvested, myDeltaCost);

        /* Access the Credit Asset Account Bucket */
        myAsset = theAccountBuckets.getBucket(myCredit);

        /* Allocate a Capital event */
        myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.adjustCost(myCost, myDeltaCost);

        /* Record the current/delta investment */
        myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        myEvent.adjustInvested(myInvested, myDeltaCost);

        /* Record the current/delta units */
        JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
        myEvent.adjustUnits(myUnits, myDeltaUnits);

        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is the Cash portion of a StockTakeOver. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processCashTakeover(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPriceList myPrices = theData.getPrices();
        JMoney myAmount = pEvent.getAmount();
        EventCategory myCat = pEvent.getCategory();

        /* Access the Debit Asset Account Bucket */
        AccountBucket myAsset = theAccountBuckets.getBucket(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(AccountAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myEvent.adjustInvested(myInvested, myDelta);

        /* Get the appropriate price for the account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myDebit, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();
        myEvent.setAttribute(CapitalAttribute.InitialPrice, myPrice);

        /* Determine value of this stock at the current time */
        JUnits myUnits = myAsset.getUnitsAttribute(AccountAttribute.Units);
        JMoney myValue = myUnits.valueAtPrice(myPrice);
        myEvent.setAttribute(CapitalAttribute.InitialValue, myValue);

        /* Access the current cost */
        JMoney myCost = myAsset.getMoneyAttribute(AccountAttribute.Cost);

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* We have to defer the allocation of cost until we know of the Stock TakeOver part */
            myEvent.setAttribute(CapitalAttribute.TakeOverCash, myAmount);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            JMoney myReduction = new JMoney(myAmount);

            /* If the reduction is greater than the total cost */
            if (myReduction.compareTo(myCost) > 0) {
                /* Reduction is the total cost */
                myReduction = new JMoney(myCost);
            }

            /* Calculate the residual cost */
            JMoney myResidualCost = new JMoney(myReduction);
            myResidualCost.negate();
            myResidualCost.addAmount(myCost);

            /* Record the residual cost */
            myEvent.setAttribute(CapitalAttribute.TakeOverCost, myResidualCost);

            /* Calculate the delta cost */
            JMoney myDeltaCost = new JMoney(myCost);
            myDeltaCost.negate();

            /* Record the current/delta cost */
            myEvent.adjustCost(myCost, myDeltaCost);

            /* Calculate the gains */
            JMoney myDeltaGains = new JMoney(myAmount);
            myDeltaGains.addAmount(myDeltaCost);

            /* Record the current/delta cost */
            JMoney myGains = myAsset.getMoneyAttribute(AccountAttribute.Gains);
            myEvent.adjustGains(myGains, myDeltaGains);
        }

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant category bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
    }

    /**
     * Process an event that is StockTakeover. This capital event relates to both the Credit and Debit accounts In particular it makes reference to the
     * CashTakeOver aspect of the debit account
     * @param pEvent the event
     */
    private void processStockTakeover(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPriceList myPrices = theData.getPrices();
        EventCategory myCat = pEvent.getCategory();

        /* Access the Asset Account Buckets */
        AccountBucket myDebAsset = theAccountBuckets.getBucket(myDebit);
        AccountBucket myCredAsset = theAccountBuckets.getBucket(myCredit);

        /* Access the cash takeover record for the debit account if it exists */
        CapitalEvent myDebEvent = myDebAsset.getCapitalEvents().getCashTakeOver();
        JMoney myResidualCash = null;
        JMoney myDeltaCost;
        JMoney myCost;
        JUnits myDeltaUnits = pEvent.getCreditUnits();

        /* If we have had a cash takeover event */
        if (myDebEvent != null) {
            /* Access the residual cost/cash */
            myResidualCash = myDebEvent.getMoneyAttribute(CapitalAttribute.TakeOverCash);
        }

        /* Allocate new Capital events */
        myDebEvent = myDebAsset.getCapitalEvents().addEvent(pEvent);
        CapitalEvent myCredEvent = myCredAsset.getCapitalEvents().addEvent(pEvent);

        /* If we have a Cash TakeOver component */
        if (myResidualCash != null) {
            /* Get the appropriate price for the credit account */
            AccountPrice myActPrice = myPrices.getLatestPrice(myCredit, pEvent.getDate());
            JPrice myPrice = myActPrice.getPrice();
            myDebEvent.setAttribute(CapitalAttribute.TakeOverPrice, myPrice);

            /* Determine value of the stock part of the takeover */
            JMoney myValue = myDeltaUnits.valueAtPrice(myPrice);
            myDebEvent.setAttribute(CapitalAttribute.TakeoverValue, myValue);

            /* Calculate the total cost of the takeover */
            JMoney myTotalCost = new JMoney(myResidualCash);
            myTotalCost.addAmount(myValue);

            /* Split the total cost of the takeover between stock and cash */
            JMoney myStockCost = myTotalCost.valueAtWeight(myValue, myTotalCost);
            JMoney myCashCost = new JMoney(myTotalCost);
            myCashCost.subtractAmount(myStockCost);

            /* Record the values */
            myDebEvent.setAttribute(CapitalAttribute.TakeOverCash, myCashCost);
            myDebEvent.setAttribute(CapitalAttribute.TakeOverStock, myStockCost);
            myDebEvent.setAttribute(CapitalAttribute.TakeOverTotal, myTotalCost);

            /* The Delta Gains is the Amount minus the CashCost */
            JMoney myDeltaGains = new JMoney(myResidualCash);
            myDeltaGains.subtractAmount(myCashCost);

            /* Record the gains */
            JMoney myGains = myDebAsset.getMoneyAttribute(AccountAttribute.Gains);
            myDebEvent.adjustGains(myGains, myDeltaGains);

            /* The cost of the new stock is the stock cost */
            myCost = myCredAsset.getMoneyAttribute(AccountAttribute.Cost);
            myCredEvent.adjustCost(myCost, myStockCost);

            /* else there is no cash part to this takeover */
        } else {
            /* The cost of the new stock is the residual debit cost */
            myDeltaCost = myDebAsset.getMoneyAttribute(AccountAttribute.Cost);
            myCost = myCredAsset.getMoneyAttribute(AccountAttribute.Cost);
            myCredEvent.adjustCost(myCost, myDeltaCost);
        }

        /* Calculate the delta cost */
        myDeltaCost = myDebAsset.getMoneyAttribute(AccountAttribute.Cost);
        myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebEvent.adjustCost(myCost, myDeltaCost);

        /* Calculate the delta units */
        JUnits myUnits = myDebAsset.getUnitsAttribute(AccountAttribute.Units);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebEvent.adjustUnits(myUnits, myDeltaUnits);

        /* Record the current/delta units */
        myUnits = myCredAsset.getUnitsAttribute(AccountAttribute.Units);
        myDeltaUnits = pEvent.getCreditUnits();
        myCredEvent.adjustUnits(myUnits, myDeltaUnits);

        /* Adjust the relevant transaction bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
    }
}
