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
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AssetAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.PayeeAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis.AnalysisState;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis.BucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.DilutionEvent.DilutionEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryDetail;
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
    public static final JDataField FIELD_YEARS = FIELD_DEFS.declareLocalField("Years");

    /**
     * Account field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    /**
     * Date field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

    /**
     * Dilutions field Id.
     */
    public static final JDataField FIELD_DILUTIONS = FIELD_DEFS.declareLocalField("Dilutions");

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
     * The Amount Tax threshold for "small" transactions (£3000).
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
     * The analysis.
     */
    private Analysis theAnalysis = null;

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
     * The taxMan account.
     */
    private PayeeAccountDetail theTaxMan = null;

    /**
     * The taxPaid bucket.
     */
    private EventCategoryDetail theTaxPaid = null;

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
     * Obtain the analysis for a tax year.
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

        /* Create the analysis */
        theAnalysis = new Analysis(theData, theDate);

        /* Create associated MetaAnalyser */
        theMetaAnalysis = new MetaAnalysis(theAnalysis);

        /* Access the TaxMan account and Tax Credit transaction */
        Account myTaxMan = theData.getAccounts().getTaxMan();
        BucketList myBuckets = theAnalysis.getList();
        theTaxMan = (PayeeAccountDetail) myBuckets.getAccountBucket(myTaxMan);
        theTaxPaid = myBuckets.getCategoryDetail(EventCategoryClass.TaxCredit);

        /* Access the events and the iterator */
        EventList myEvents = pData.getEvents();
        Iterator<Event> myIterator = myEvents.iterator();

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Check the range and exit loop if necessary */
            int myResult = theDate.compareTo(myCurr.getDate());
            if (myResult < 0) {
                break;
            }

            /* Process the event in the asset report */
            processEvent(myCurr);
        }

        /* Value priced assets */
        theMetaAnalysis.valueAssets();

        /* produce totals */
        theMetaAnalysis.produceTotals();
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

        /* Create the analysis */
        theAnalysis = new Analysis(theData, myAccount, theDate);

        /* Access the TaxMan account and Tax Credit transaction */
        Account myTaxMan = theData.getAccounts().getTaxMan();
        BucketList myBuckets = theAnalysis.getList();
        theTaxMan = (PayeeAccountDetail) myBuckets.getAccountBucket(myTaxMan);
        theTaxPaid = myBuckets.getCategoryDetail(EventCategoryClass.TaxCredit);
        theAccount = myBuckets.getAccountBucket(myAccount);

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
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Check the range and exit loop if required */
            int myResult = myRange.compareTo(myCurr.getDate());
            if (myResult == -1) {
                break;
            }

            /* Ignore items that do not relate to this account */
            if (!myCurr.relatesTo(myAccount)) {
                continue;
            }

            /* Add a statement line to the statement */
            myLine = new StatementLine(myList, myCurr);
            myList.append(myLine);
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
        if (theAccount instanceof PayeeAccountDetail) {
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
                || (myCurr.getCategoryType() == null)
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

        /* Access the TaxMan account */
        Account myTaxMan = theData.getAccounts().getTaxMan();

        /* Access the top level debug entry for this analysis */
        JDataEntry mySection = pView.getDataEntry(DataControl.DATA_ANALYSIS);

        /* Create a list of AnalysisYears */
        theYears = new AnalysisYearList(this, pView.getPreferenceMgr());

        /* Create the Dilution Event List */
        theDilutions = new DilutionEventList(theData);

        /* Access the tax years list */
        TaxYearList myList = theData.getTaxYears();

        /* Access the Event iterator */
        Iterator<Event> myIterator = theData.getEvents().listIterator();
        TaxYear myTax = null;
        JDateDay myDate = null;
        IncomeBreakdown myBreakdown = null;
        int myResult = -1;

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();
            JDateDay myCurrDay = myCurr.getDate();

            /* If we have a current tax year */
            if (myDate != null) {
                /* Check that this event is still in the tax year */
                myResult = myDate.compareTo(myCurrDay);
            }

            /* If we have exhausted the tax year or else this is the first tax year */
            if (myResult < 0) {
                /* Access the relevant tax year */
                myTax = myList.findTaxYearForDate(myCurrDay);
                myDate = myTax.getTaxYear();

                /* If we have an existing meta analysis year */
                if (theMetaAnalysis != null) {
                    /* Value priced assets */
                    theMetaAnalysis.valueAssets();
                }

                /* Create the new Analysis */
                AnalysisYear myYear = theYears.getNewAnalysis(myTax, theAnalysis);
                theAnalysis = myYear.getAnalysis();
                theMetaAnalysis = myYear.getMetaAnalysis();
                myBreakdown = myYear.getBreakdown();

                /* Access the TaxMan account bucket and Tax Credit transaction */
                BucketList myBuckets = theAnalysis.getList();
                theTaxMan = (PayeeAccountDetail) myBuckets.getAccountBucket(myTaxMan);
                theTaxPaid = myBuckets.getCategoryDetail(EventCategoryClass.TaxCredit);
            }

            /* Touch credit account */
            Account myAccount = myCurr.getCredit();
            myAccount.touchItem(myCurr);

            /* Touch debit accounts */
            myAccount = myCurr.getDebit();
            myAccount.touchItem(myCurr);

            /* Touch Category Type */
            EventCategoryType myCategory = myCurr.getCategoryType();
            myCategory.touchItem(myCurr);

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

        /* Value priced assets of the most recent set */
        if (theMetaAnalysis != null) {
            theMetaAnalysis.valueAssets();
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
         * Produce totals for an analysis year.
         */
        public void produceTotals() {
            /* If we are in valued state */
            if (theAnalysis.getState() == AnalysisState.VALUED) {
                /* call the meta analyser to produce totals */
                theMetaAnalysis.produceTotals();
            }
        }

        /**
         * Calculate tax for an analysis year.
         */
        public void calculateTax() {
            /* If we are not in taxed state */
            if (theAnalysis.getState() != AnalysisState.TAXED) {
                /* call the meta analyser to calculate tax */
                theMetaAnalysis.calculateTax(thePreferenceMgr);
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

        /* If the event relates to a priced item split out the workings */
        if ((myDebit.isPriced())
            || (myCredit.isPriced())) {
            /* Process as a Capital event */
            processCapitalEvent(pEvent);

            /* Else handle the event normally */
        } else {
            EventCategoryType myCat = pEvent.getCategoryType();
            EventCategoryTypeList myTranList = theData.getEventCategoryTypes();
            BucketList myBuckets = theAnalysis.getList();

            /* If the event is interest */
            if (myCat.isInterest()) {
                /* If the account is tax free */
                if (myDebit.isTaxFree()) {
                    /* The true transaction type is TaxFreeInterest */
                    myCat = myTranList.findItemByClass(EventCategoryClass.TaxFreeInterest);
                }

                /* True debit account is the parent */
                myDebit = myDebit.getParent();
            }

            /* Adjust the debit account bucket */
            AccountBucket myBucket = myBuckets.getAccountBucket(myDebit);
            myBucket.adjustForDebit(pEvent);

            /* Adjust the credit account bucket */
            myBucket = myBuckets.getAccountBucket(myCredit);
            myBucket.adjustForCredit(pEvent);

            /* If the event causes a tax credit */
            if (pEvent.getTaxCredit() != null) {
                /* Adjust the TaxMan account for the tax credit */
                theTaxMan.adjustForTaxCredit(pEvent);
                theTaxPaid.adjustForTaxCredit(pEvent);
            }

            /* Adjust the relevant category bucket */
            EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
            myCatBucket.adjustAmount(pEvent);
        }
    }

    /**
     * Process a capital event.
     * @param pEvent the event to process
     * @throws JDataException on error
     */
    private void processCapitalEvent(final Event pEvent) throws JDataException {
        EventCategoryType myCat = pEvent.getCategoryType();

        /* Switch on the category */
        switch (myCat.getCategoryClass()) {
        /* Process a stock split */
            case StockSplit:
            case AdminCharge:
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
            /* Process a Cash TakeOver */
            case CashTakeOver:
                processCashTakeover(pEvent);
                break;
            /* Process a Cash TakeOver */
            case StockTakeOver:
                processStockTakeover(pEvent);
                break;
            /* Process a Taxable Gain */
            case TaxableGain:
                processTaxableGain(pEvent);
                break;
            /* Process a dividend */
            case Dividend:
                processDividend(pEvent);
                break;
            /* Process standard transfer in/out */
            case Transfer:
            case Endowment:
            case Expense:
            case Inherited:
            case TaxFreeIncome:
                if (pEvent.getCredit().isPriced()) {
                    processTransferIn(pEvent);
                } else {
                    processTransferOut(pEvent);
                }
                break;
            /* Throw an Exception */
            default:
                throw new JDataException(ExceptionClass.LOGIC, "Unexpected category type: "
                                                               + myCat.getCategoryClass());
        }
    }

    /**
     * Process an event that is a stock split. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processStockSplit(final Event pEvent) {
        /* Stock split has identical credit/debit and always has Units */
        Account myAccount = pEvent.getCredit();
        JUnits myUnits = pEvent.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits(pEvent.getDebitUnits());
            myUnits.negate();
        }

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event and record Current and delta units */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myUnits);

        /* Add/Subtract the units movement for the account */
        myAsset.getUnits().addUnits(myUnits);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
    }

    /**
     * Process an event that is a transfer into capital (also StockRightTaken and Dividend Re-investment). This capital event relates only to the Credit Account
     * @param pEvent the event
     */
    private void processTransferIn(final Event pEvent) {
        /* Transfer in is to the credit account and may or may not have units */
        Account myAccount = pEvent.getCredit();
        Account myDebit = pEvent.getDebit();
        JUnits myUnits = pEvent.getCreditUnits();
        JMoney myAmount = pEvent.getAmount();
        EventCategoryType myCat = pEvent.getCategoryType();

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myAsset.getCost());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myAmount);

        /* Adjust the cost of this account */
        myAsset.getCost().addAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myAsset.getCost());

        /* If we have new units */
        if (myUnits != null) {
            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().addUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
        }

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

        /* Adjust the total money invested into this account */
        myAsset.getInvested().addAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* If the event causes a tax credit */
        if (pEvent.getTaxCredit() != null) {
            /* Adjust the TaxMan account for the tax credit */
            theTaxMan.adjustForTaxCredit(pEvent);
            theTaxPaid.adjustForTaxCredit(pEvent);
        }

        /* Adjust the debit account bucket */
        AccountBucket myBucket = myBuckets.getAccountBucket(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* Adjust the relevant transaction bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
    }

    /**
     * Process a dividend event. This capital event relates to the only to Debit account,
     * @param pEvent the event
     */
    private void processDividend(final Event pEvent) {
        /* The main account that we are interested in is the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        EventCategoryType myCat = pEvent.getCategoryType();
        EventCategoryTypeList myCatList = theData.getEventCategoryTypes();
        JMoney myAmount = pEvent.getAmount();
        JMoney myTaxCredit = pEvent.getTaxCredit();
        JUnits myUnits = pEvent.getCreditUnits();
        Account myDebit;

        /* If the account is tax free */
        if (myAccount.isTaxFree()) {
            /* The true category type is TaxFreeDividend */
            myCat = myCatList.findItemByClass(EventCategoryClass.TaxFreeDividend);

            /* else if the account is a unit trust */
        } else if (myAccount.isUnitTrust()) {
            /* The true category type is UnitTrustDividend */
            myCat = myCatList.findItemByClass(EventCategoryClass.UnitTrustDividend);
        }

        /* True debit account is the parent */
        myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        BucketList myBuckets = theAnalysis.getList();
        AccountBucket myBucket = myBuckets.getAccountBucket(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* Access the Asset Account Bucket */
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* If this is a re-investment */
        if (myAccount.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myAsset.getCost());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myAmount);

            /* Adjust the cost of this account */
            myAsset.getCost().addAmount(myAmount);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myAsset.getCost());

            /* Record the current/delta investment */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

            /* Adjust the total money invested into this account */
            myAsset.getInvested().addAmount(myAmount);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

            /* If we have new units */
            if (myUnits != null) {
                /* Record current and delta units */
                myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
                myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myUnits);

                /* Add the units movement for the account */
                myAsset.getUnits().addUnits(myUnits);
                myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a gain from the account */
                myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALDIVIDEND, myAsset.getDividend());
                myEvent.addAttribute(CapitalEvent.CAPITAL_DELTADIVIDEND, myTaxCredit);

                /* The Tax Credit is viewed as a gain from the account */
                myAsset.getDividend().addAmount(myTaxCredit);
                myEvent.addAttribute(CapitalEvent.CAPITAL_FINALDIVIDEND, myAsset.getDividend());
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the gains total for this asset */
            JMoney myDividends = new JMoney(myAmount);

            /* Any tax credit is viewed as a realised gain from the account */
            if (myTaxCredit != null) {
                myDividends.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALDIVIDEND, myAsset.getDividend());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTADIVIDEND, myDividends);

            /* The Dividend is viewed as a gain from the account */
            myAsset.getDividend().addAmount(myDividends);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALDIVIDEND, myAsset.getDividend());

            /* Adjust the credit account bucket */
            myBucket = myBuckets.getAccountBucket(myCredit);
            myBucket.adjustForCredit(pEvent);
        }

        /* If the event causes a tax credit */
        if (pEvent.getTaxCredit() != null) {
            /* Adjust the TaxMan account for the tax credit */
            theTaxMan.adjustForTaxCredit(pEvent);
            theTaxPaid.adjustForTaxCredit(pEvent);
        }

        /* Adjust the relevant transaction bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
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
        JUnits myUnits = pEvent.getDebitUnits();
        EventCategoryType myCat = pEvent.getCategoryType();
        JMoney myReduction;
        JMoney myDeltaCost;
        JMoney myDeltaGains;
        JMoney myCost;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Assume the the cost reduction is the full value */
        myReduction = new JMoney(myAmount);
        myCost = myAsset.getCost();

        /* If we are reducing units in the account */
        if ((myUnits != null)
            && (myUnits.isNonZero())) {
            /* The reduction is the relevant fraction of the cost */
            myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

            /* Adjust the cost appropriately */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCost);
        }

        /* Determine the delta to the gains */
        myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINS, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINS, myDeltaGains);

            /* Adjust the cost appropriately */
            myAsset.getGains().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, myAsset.getGains());
        }

        /* If we have reduced units */
        if (myUnits != null) {
            /* Access units as negative value */
            JUnits myDeltaUnits = new JUnits(myUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myDeltaUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().subtractUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
        }

        /* Adjust the credit account bucket */
        AccountBucket myBucket = myBuckets.getAccountBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant transaction bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
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
        JUnits myUnits = pEvent.getDebitUnits();
        EventCategoryType myCat = pEvent.getCategoryType();
        JMoney myReduction;
        JMoney myDeltaCost;
        JMoney myDeltaGains;
        JMoney myCost;
        Account myDebit;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Assume the the cost reduction is the full value */
        myReduction = new JMoney(myAmount);
        myCost = myAsset.getCost();

        /* If we are reducing units in the account */
        if ((myUnits != null)
            && (myUnits.isNonZero())) {
            /* The reduction is the relevant fraction of the cost */
            myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

            /* Adjust the cost appropriately */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCost);
        }

        /* Determine the delta to the gains */
        myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINS, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINS, myDeltaGains);

            /* Adjust the cost appropriately */
            myAsset.getGains().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, myAsset.getGains());
        }

        /* If we have reduced units */
        if (myUnits != null) {
            /* Access units as negative value */
            JUnits myDeltaUnits = new JUnits(myUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myDeltaUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().subtractUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
        }

        /* True debit account is the parent */
        myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        PayeeAccountDetail myDebitBucket = (PayeeAccountDetail) myBuckets.getAccountBucket(myDebit);
        myDebitBucket.adjustForTaxGainTaxCredit(pEvent);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = myBuckets.getAccountBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant category bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
        myCatBucket.getAmount().subtractAmount(myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxCredit(pEvent);
        theTaxPaid.adjustForTaxCredit(pEvent);

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
        EventCategoryType myCat = pEvent.getCategoryType();
        AccountPrice myActPrice;
        JPrice myPrice;
        JMoney myValue;
        JMoney myCost;
        JMoney myReduction;
        JMoney myPortion;
        JMoney myDeltaCost;
        JMoney myDeltaGains;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Get the appropriate price for the account */
        myActPrice = myPrices.getLatestPrice(myAccount, pEvent.getDate());
        myPrice = myActPrice.getPrice();
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALPRICE, myPrice);

        /* Determine value of this stock at the current time */
        myValue = myAsset.getUnits().valueAtPrice(myPrice);
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALVALUE, myValue);

        /* Access the current cost */
        myCost = myAsset.getCost();

        /* Calculate the portion of the value that creates a large transaction */
        myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Determine the total value of rights plus share value */
            JMoney myTotalValue = new JMoney(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the reduction as a proportion of the total value */
            myReduction = myAsset.getCost().valueAtWeight(myAmount, myTotalValue);

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
        myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

        /* Adjust the cost */
        myCost.addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCost);

        /* Calculate the delta gains */
        myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* Record the current/delta gains */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINS, myAsset.getGains());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINS, myDeltaGains);

        /* Adjust the gains */
        myAsset.getGains().addAmount(myDeltaGains);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, myAsset.getGains());

        /* Adjust the credit account bucket */
        AccountBucket myBucket = myBuckets.getAccountBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant category bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
    }

    /**
     * Process an event that is Stock DeMerger. This capital event relates to both the Credit and Debit accounts
     * @param pEvent the event
     */
    private void processStockDeMerger(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JDilution myDilution = pEvent.getDilution();
        JUnits myUnits = pEvent.getCreditUnits();
        JMoney myCost;
        JMoney myDeltaCost;
        JMoney myNewCost;

        /* Access the Debit Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Calculate the diluted value of the Debit account */
        myCost = myAsset.getCost();
        myNewCost = myCost.getDilutedMoney(myDilution);

        /* Calculate the delta to the cost */
        myDeltaCost = new JMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

        /* Record the new total cost */
        myCost.addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCost);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myDeltaCost);

        /* Adjust the investment for the debit account */
        myAsset.getInvested().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Access the Credit Asset Account Bucket */
        myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myCredit);

        /* Allocate a Capital event */
        myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myAsset.getCost());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

        /* Adjust the cost */
        myAsset.getCost().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myAsset.getCost());

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myDeltaCost);

        /* Adjust the investment */
        myAsset.getInvested().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Record the current/delta units */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myAsset.getUnits());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myUnits);

        /* Adjust the units for the credit account */
        myAsset.getUnits().addUnits(myUnits);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myAsset.getUnits());
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
        EventCategoryType myCat = pEvent.getCategoryType();
        AccountPrice myActPrice;
        JPrice myPrice;
        JMoney myValue;
        JMoney myPortion;
        JMoney myReduction;
        JMoney myCost;
        JMoney myResidualCost;
        JMoney myDeltaCost;
        JMoney myDeltaGains;

        /* Access the Debit Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALINVEST, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAINVEST, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, myAsset.getInvested());

        /* Get the appropriate price for the account */
        myActPrice = myPrices.getLatestPrice(myDebit, pEvent.getDate());
        myPrice = myActPrice.getPrice();
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALPRICE, myPrice);

        /* Determine value of this stock at the current time */
        myValue = myAsset.getUnits().valueAtPrice(myPrice);
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALVALUE, myValue);

        /* Access the current cost */
        myCost = myAsset.getCost();

        /* Calculate the portion of the value that creates a large transaction */
        myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* We have to defer the allocation of cost until we know of the Stock TakeOver part */
            myEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERCASH, myAmount);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            myReduction = new JMoney(myAmount);

            /* If the reduction is greater than the total cost */
            if (myReduction.compareTo(myCost) > 0) {
                /* Reduction is the total cost */
                myReduction = new JMoney(myCost);
            }

            /* Calculate the residual cost */
            myResidualCost = new JMoney(myReduction);
            myResidualCost.negate();
            myResidualCost.addAmount(myCost);

            /* Record the residual cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERCOST, myResidualCost);

            /* Calculate the delta cost */
            myDeltaCost = new JMoney(myCost);
            myDeltaCost.negate();

            /* Record the current/delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

            /* Adjust the cost */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCost);

            /* Calculate the gains */
            myDeltaGains = new JMoney(myAmount);
            myDeltaGains.addAmount(myDeltaCost);

            /* Record the current/delta cost */
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINS, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINS, myDeltaGains);

            /* Adjust the gained */
            myAsset.getGained().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, myAsset.getGains());
        }

        /* Adjust the credit account bucket */
        AccountBucket myBucket = myBuckets.getAccountBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant category bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
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
        JUnits myUnits = pEvent.getCreditUnits();
        EventCategoryType myCat = pEvent.getCategoryType();
        AccountPrice myActPrice;
        JPrice myPrice;
        JMoney myValue;
        JMoney myStockCost;
        JMoney myCashCost;
        JMoney myTotalCost;
        JMoney myDeltaCost;
        JMoney myDeltaGains;
        JUnits myDeltaUnits;
        JMoney myResidualCash = null;
        CapitalEvent myCredEvent;
        CapitalEvent myDebEvent;

        /* Access the Asset Account Buckets */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccountDetail myDebAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myDebit);
        AssetAccountDetail myCredAsset = (AssetAccountDetail) myBuckets.getAccountBucket(myCredit);

        /* Access the cash takeover record for the debit account if it exists */
        myDebEvent = myDebAsset.getCapitalEvents().getCashTakeOver();

        /* If we have had a cash takeover event */
        if (myDebEvent != null) {
            /* Access the residual cost/cash */
            myResidualCash = myDebEvent.findAttribute(CapitalEvent.CAPITAL_TAKEOVERCASH, JMoney.class);
        }

        /* Allocate new Capital events */
        myDebEvent = myDebAsset.getCapitalEvents().addEvent(pEvent);
        myCredEvent = myCredAsset.getCapitalEvents().addEvent(pEvent);

        /* If we have a Cash TakeOver component */
        if (myResidualCash != null) {
            /* Get the appropriate price for the credit account */
            myActPrice = myPrices.getLatestPrice(myCredit, pEvent.getDate());
            myPrice = myActPrice.getPrice();
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERPRICE, myPrice);

            /* Determine value of the stock part of the takeover */
            myValue = myUnits.valueAtPrice(myPrice);
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERVALUE, myValue);

            /* Calculate the total cost of the takeover */
            myTotalCost = new JMoney(myResidualCash);
            myTotalCost.addAmount(myValue);

            /* Split the total cost of the takeover between stock and cash */
            myStockCost = myTotalCost.valueAtWeight(myValue, myTotalCost);
            myCashCost = new JMoney(myTotalCost);
            myCashCost.subtractAmount(myStockCost);

            /* Record the values */
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERCASH, myCashCost);
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERSTOCK, myStockCost);
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_TAKEOVERTOTAL, myTotalCost);

            /* The Delta Gains is the Amount minus the CashCost */
            myDeltaGains = new JMoney(myResidualCash);
            myDeltaGains.subtractAmount(myCashCost);

            /* Record the gains */
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINS, myDebAsset.getGains());
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINS, myDeltaGains);
            myDebAsset.getGained().addAmount(myDeltaGains);
            myDebEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, myDebAsset.getGains());

            /* The cost of the new stock is the stock cost */
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCredAsset.getCost());
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myStockCost);
            myCredAsset.getCost().addAmount(myStockCost);
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCredAsset.getCost());

            /* else there is no cash part to this takeover */
        } else {
            /* The cost of the new stock is the residual debit cost */
            myDeltaCost = myDebAsset.getCost();
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myCredAsset.getCost());
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);
            myCredAsset.getCost().addAmount(myDeltaCost);
            myCredEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myCredAsset.getCost());
        }

        /* Calculate the delta cost */
        myDeltaCost = new JMoney(myDebAsset.getCost());
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_INITIALCOST, myDebAsset.getCost());
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_DELTACOST, myDeltaCost);

        /* Adjust the cost */
        myDebAsset.getCost().addAmount(myDeltaCost);
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_FINALCOST, myDebAsset.getCost());

        /* Calculate the delta units */
        myDeltaUnits = new JUnits(myDebAsset.getUnits());
        myDeltaUnits.negate();

        /* Record the current/delta units */
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myDebAsset.getUnits());
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myDeltaUnits);

        /* Adjust the Units */
        myDebAsset.getUnits().addUnits(myDeltaUnits);
        myDebEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myDebAsset.getUnits());

        /* Record the current/delta units */
        myCredEvent.addAttribute(CapitalEvent.CAPITAL_INITIALUNITS, myCredAsset.getUnits());
        myCredEvent.addAttribute(CapitalEvent.CAPITAL_DELTAUNITS, myUnits);
        myCredAsset.getUnits().addUnits(myUnits);
        myCredEvent.addAttribute(CapitalEvent.CAPITAL_FINALUNITS, myCredAsset.getUnits());

        /* Adjust the relevant transaction bucket */
        EventCategoryDetail myCatBucket = myBuckets.getCategoryDetail(myCat);
        myCatBucket.adjustAmount(pEvent);
    }
}
