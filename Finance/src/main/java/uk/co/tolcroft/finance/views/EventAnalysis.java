/*******************************************************************************
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
package uk.co.tolcroft.finance.views;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.views.Analysis.ActDetail;
import uk.co.tolcroft.finance.views.Analysis.AnalysisState;
import uk.co.tolcroft.finance.views.Analysis.AssetAccount;
import uk.co.tolcroft.finance.views.Analysis.BucketList;
import uk.co.tolcroft.finance.views.Analysis.ExternalAccount;
import uk.co.tolcroft.finance.views.Analysis.TransDetail;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class EventAnalysis implements JDataContents {
    /**
     * Report fields
     */
    protected static final JDataFields theFields = new JDataFields(EventAnalysis.class.getSimpleName());

    /* Field IDs */
    public static final JDataField FIELD_ANALYSIS = theFields.declareLocalField("Analysis");
    public static final JDataField FIELD_YEARS = theFields.declareLocalField("Years");
    public static final JDataField FIELD_ACCOUNT = theFields.declareLocalField("Account");
    public static final JDataField FIELD_DATE = theFields.declareLocalField("Date");
    public static final JDataField FIELD_DILUTIONS = theFields.declareLocalField("Dilutions");

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_ANALYSIS)
            return (theAnalysis == null) ? JDataObject.FIELD_SKIP : theAnalysis;
        if (pField == FIELD_YEARS)
            return (theYears == null) ? JDataObject.FIELD_SKIP : theYears;
        if (pField == FIELD_ACCOUNT)
            return (theAccount == null) ? JDataObject.FIELD_SKIP : theAccount;
        if (pField == FIELD_DATE)
            return (theDate == null) ? JDataObject.FIELD_SKIP : theDate;
        if (pField == FIELD_DILUTIONS)
            return (theDilutions == null) ? JDataObject.FIELD_SKIP : theDilutions;
        return null;
    }

    @Override
    public String formatObject() {
        return theFields.getName();
    }

    /**
     * The Amount Tax threshold for "small" transactions (�3000)
     */
    private final static Money valueLimit = new Money(Money.convertToValue(3000));

    /**
     * The Rate Tax threshold for "small" transactions (5%)
     */
    private final static Rate rateLimit = new Rate(Rate.convertToValue(5));

    /* Members */
    private FinanceData theData = null;
    private Analysis theAnalysis = null;
    private MetaAnalysis theMetaAnalysis = null;
    private AnalysisYearList theYears = null;
    private ActDetail theAccount = null;
    private DateDay theDate = null;
    private DilutionEventList theDilutions = null;
    private ExternalAccount theTaxMan = null;
    private TransDetail theTaxPaid = null;

    /* Access methods */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    public MetaAnalysis getMetaAnalysis() {
        return theMetaAnalysis;
    }

    public AnalysisYearList getAnalysisYears() {
        return theYears;
    }

    public AnalysisYear getAnalysisYear(TaxYear pYear) {
        return (theYears == null) ? null : theYears.searchFor(pYear);
    }

    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor for a dated analysis
     * @param pData the data to analyse events for
     * @param pDate the Date for the analysis
     * @throws JDataException
     */
    public EventAnalysis(FinanceData pData,
                         DateDay pDate) throws JDataException {
        DataListIterator<Event> myIterator;
        EventList myEvents;
        Event myCurr;
        int myResult;

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
        theTaxMan = (ExternalAccount) myBuckets.getAccountDetail(myTaxMan);
        theTaxPaid = myBuckets.getTransDetail(TransClass.TAXCREDIT);

        /* Access the events and the iterator */
        myEvents = pData.getEvents();
        myIterator = myEvents.listIterator();

        /* Loop through the Events extracting relevant elements */
        while ((myCurr = myIterator.next()) != null) {
            /* Check the range */
            myResult = theDate.compareTo(myCurr.getDate());

            /* Handle out of range */
            if (myResult == -1)
                break;

            /* Process the event in the asset report */
            processEvent(myCurr);
        }

        /* Value priced assets */
        theMetaAnalysis.valueAssets();

        /* produce totals */
        theMetaAnalysis.produceTotals();
    }

    /**
     * Constructor for a statement analysis
     * @param pData the data to analyse events for
     * @param pStatement the statement to prepare
     * @throws JDataException
     */
    public EventAnalysis(FinanceData pData,
                         Statement pStatement) throws JDataException {
        DataListIterator<Event> myIterator;
        EventList myEvents;
        Event myCurr;
        DateDayRange myRange;
        Account myAccount;
        Statement.StatementLine myLine;
        Statement.StatementLines myList;
        int myResult;

        /* Access key points of the statement */
        myRange = pStatement.getDateRange();
        myAccount = pStatement.getAccount();
        myList = pStatement.getLines();

        /* Store the parameters */
        theData = pData;
        theDate = myRange.getStart();

        /* Create the analysis */
        theAnalysis = new Analysis(theData, myAccount, theDate);

        /* Access the TaxMan account and Tax Credit transaction */
        Account myTaxMan = theData.getAccounts().getTaxMan();
        BucketList myBuckets = theAnalysis.getList();
        theTaxMan = (ExternalAccount) myBuckets.getAccountDetail(myTaxMan);
        theTaxPaid = myBuckets.getTransDetail(TransClass.TAXCREDIT);
        theAccount = myBuckets.getAccountDetail(myAccount);

        /* Access the events and the iterator */
        myEvents = pData.getEvents();
        myIterator = myEvents.listIterator();

        /* Loop through the Events extracting relevant elements */
        while ((myCurr = myIterator.next()) != null) {
            /* Check the range */
            myResult = myRange.compareTo(myCurr.getDate());

            /* If we are at or past the range break the loop */
            if (myResult != 1)
                break;

            /* Ignore items that do not relate to this account */
            if (!myCurr.relatesTo(myAccount))
                continue;

            /* Process the event in the asset report */
            processEvent(myCurr);
        }

        /* move the iterator back one */
        myIterator.previous();

        /* create a save point */
        theAccount.createSavePoint();

        /* Set starting balance and units for account */
        pStatement.setStartBalances(theAccount);

        /* Continue looping through the Events extracting relevant elements */
        while ((myCurr = myIterator.next()) != null) {
            /* Check the range */
            myResult = myRange.compareTo(myCurr.getDate());

            /* Handle past limit */
            if (myResult == -1)
                break;

            /* Ignore items that do not relate to this account */
            if (!myCurr.relatesTo(myAccount))
                continue;

            /* Add a statement line to the statement */
            myLine = new Statement.StatementLine(myList, myCurr);
            myList.add(myLine);
        }

        /* Reset the statement balances */
        resetStatementBalance(pStatement);
    }

    /**
     * recalculate statement balance
     * @param pStatement the statement
     * @throws JDataException
     */
    protected void resetStatementBalance(Statement pStatement) throws JDataException {
        Statement.StatementLine myLine;
        Statement.StatementLines myLines;
        DataListIterator<Event> myIterator;

        /* Access the iterator */
        myLines = pStatement.getLines();
        myIterator = myLines.listIterator();

        /* If we don't have balances just return */
        if (theAccount instanceof ExternalAccount)
            return;

        /* Restore the SavePoint */
        theAccount.restoreSavePoint();

        /* Loop through the lines adjusting the balance */
        while ((myLine = (Statement.StatementLine) myIterator.next()) != null) {
            /* Skip deleted lines */
            if (myLine.isDeleted())
                continue;

            /* Ignore if it is not a valid event */
            if (myLine.getPartner() == null)
                continue;
            if (myLine.getTransType() == null)
                continue;
            if (myLine.getAmount() == null)
                continue;

            /* Process the event */
            processEvent(myLine);

            /* Update the balances */
            myLine.setBalances();
        }

        /* Set the ending balances */
        pStatement.setEndBalances();
    }

    /**
     * Constructor for a full year set of accounts
     * @param pView the Data view
     * @param pData the Data to analyse
     * @throws JDataException
     */
    public EventAnalysis(DataControl<?> pView,
                         FinanceData pData) throws JDataException {
        Event myCurr;
        DataListIterator<Event> myIterator;
        int myResult = -1;
        TaxYear myTax = null;
        DateDay myDate = null;
        TaxYear.TaxYearList myList;
        AnalysisYear myYear;
        Account myAccount;
        TransactionType myTransType;
        Account myTaxMan;
        JDataEntry mySection;
        IncomeBreakdown myBreakdown = null;

        /* Store the parameters */
        theData = pData;

        /* Access the TaxMan account */
        myTaxMan = theData.getAccounts().getTaxMan();

        /* Access the top level debug entry for this analysis */
        mySection = pView.getDataEntry(DataControl.DATA_ANALYSIS);

        /* Create a list of AnalysisYears */
        theYears = new AnalysisYearList(this);

        /* Create the Dilution Event List */
        theDilutions = new DilutionEventList(theData);

        /* Access the tax years list */
        myList = theData.getTaxYears();

        /* Access the Event iterator */
        myIterator = theData.getEvents().listIterator();

        /* Loop through the Events extracting relevant elements */
        while ((myCurr = myIterator.next()) != null) {
            /* If we have a current tax year */
            if (myTax != null) {
                /* Check that this event is still in the tax year */
                myResult = myDate.compareTo(myCurr.getDate());
            }

            /* If we have exhausted the tax year or else this is the first tax year */
            if (myResult == -1) {
                /* Access the relevant tax year */
                myTax = myList.searchFor(myCurr.getDate());
                myDate = myTax.getTaxYear();

                /* If we have an existing meta analysis year */
                if (theMetaAnalysis != null) {
                    /* Value priced assets */
                    theMetaAnalysis.valueAssets();
                }

                /* Create the new Analysis */
                myYear = theYears.getNewAnalysis(myTax, theAnalysis);
                theAnalysis = myYear.getAnalysis();
                theMetaAnalysis = myYear.getMetaAnalysis();
                myBreakdown = myYear.getBreakdown();

                /* Access the TaxMan account bucket and Tax Credit transaction */
                BucketList myBuckets = theAnalysis.getList();
                theTaxMan = (ExternalAccount) myBuckets.getAccountDetail(myTaxMan);
                theTaxPaid = myBuckets.getTransDetail(TransClass.TAXCREDIT);
            }

            /* Touch credit account */
            myAccount = myCurr.getCredit();
            myAccount.touchItem(myCurr);

            /* Touch debit accounts */
            myAccount = myCurr.getDebit();
            myAccount.touchItem(myCurr);

            /* Touch Transaction Type */
            myTransType = myCurr.getTransType();
            myTransType.touchItem(myCurr);

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
        if (theMetaAnalysis != null)
            theMetaAnalysis.valueAssets();

        /* Update analysis object */
        mySection.setObject(this);
    }

    /* Public class for analysis year */
    public static class AnalysisYear extends DataItem<AnalysisYear> {
        /**
         * Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisYear.class.getSimpleName(),
                DataItem.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");
        public static final JDataField FIELD_YEAR = FIELD_DEFS.declareEqualityField("TaxYear");
        public static final JDataField FIELD_BREAKDOWN = FIELD_DEFS.declareLocalField("IncomeBreakdown");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ANALYSIS)
                return theAnalysis;
            if (pField == FIELD_YEAR)
                return theYear;
            if (pField == FIELD_BREAKDOWN)
                return theBreakdown;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Analysis theAnalysis = null;
        private MetaAnalysis theMetaAnalysis = null;
        private IncomeBreakdown theBreakdown = null;
        private TaxYear theYear = null;
        private JDataEntry theListDebug = null;
        private JDataEntry theChargeDebug = null;
        private FinanceData theData = null;

        /* Access methods */
        public DateDay getDate() {
            return theYear.getTaxYear();
        }

        public TaxYear getTaxYear() {
            return theYear;
        }

        public Analysis getAnalysis() {
            return theAnalysis;
        }

        public MetaAnalysis getMetaAnalysis() {
            return theMetaAnalysis;
        }

        public IncomeBreakdown getBreakdown() {
            return theBreakdown;
        }

        /**
         * Constructor for the Analysis Year
         * @param pList the list
         * @param pYear the Tax Year
         * @param pPrevious the previous analysis
         */
        private AnalysisYear(AnalysisYearList pList,
                             TaxYear pYear,
                             Analysis pPrevious) {
            /* Call super-constructor */
            super(pList, 0);
            theData = pList.theEvents.theData;

            /* Store tax year */
            theYear = pYear;

            /* Create new analysis */
            theAnalysis = new Analysis(theData, pYear, pPrevious);

            /* Create associated MetaAnalyser */
            theMetaAnalysis = new MetaAnalysis(theAnalysis);

            /* Create the breakdown */
            theBreakdown = new IncomeBreakdown(theData);
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is the same class */
            if (pThat.getClass() != this.getClass())
                return -1;

            /* Access the object as am Analysis Year */
            AnalysisYear myThat = (AnalysisYear) pThat;

            /* Compare the bucket order */
            result = getDate().compareTo(myThat.getDate());
            return result;
        }

        /**
         * Produce totals for an analysis year
         */
        public void produceTotals() {
            /* If we are in valued state */
            if (theAnalysis.getState() == AnalysisState.VALUED) {
                /* call the meta analyser to produce totals */
                theMetaAnalysis.produceTotals();

                /* Declare a change in the debug entry */
                theListDebug.setChanged();
            }
        }

        /**
         * Calculate tax for an analysis year
         */
        public void calculateTax() {
            /* If we are not in taxed state */
            if (theAnalysis.getState() != AnalysisState.TAXED) {
                /* call the meta analyser to calculate tax */
                theMetaAnalysis.calculateTax();

                /* Declare a change in the debug entry */
                theListDebug.setChanged();

                /* Show the charges */
                theChargeDebug.showEntry();
            }
        }
    }

    /* the list class */
    public static class AnalysisYearList extends DataList<AnalysisYearList, AnalysisYear> {
        /**
         * List name
         */
        public static String listName = "AnalysisYears";

        @Override
        public String listName() {
            return listName;
        }

        /**
         * Report fields
         */
        protected static final JDataFields theFields = new JDataFields(
                AnalysisYearList.class.getSimpleName(), DataList.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_EVENTS = theFields.declareLocalField("Events");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return theFields;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_EVENTS)
                return theEvents;
            return super.getFieldValue(pField);
        }

        /* Members */
        private EventAnalysis theEvents = null;

        /**
         * Construct a top-level List
         * @param pEvents the events
         */
        public AnalysisYearList(EventAnalysis pEvents) {
            /* Call super constructor */
            super(AnalysisYearList.class, AnalysisYear.class, ListStyle.VIEW, false);
            theEvents = pEvents;
        }

        /* Obtain extract lists. */
        @Override
        public AnalysisYearList getUpdateList() {
            return null;
        }

        @Override
        public AnalysisYearList getEditList() {
            return null;
        }

        @Override
        public AnalysisYearList getShallowCopy() {
            return null;
        }

        @Override
        public AnalysisYearList getDeepCopy(DataSet<?> pData) {
            return null;
        }

        @Override
        public AnalysisYearList getDifferences(AnalysisYearList pOld) {
            return null;
        }

        /**
         * Add a new item to the list
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public AnalysisYear addNewItem(DataItem<?> pItem) {
            return null;
        }

        /**
         * Add a new item to the edit list
         * @return the newly added item
         */
        @Override
        public AnalysisYear addNewItem() {
            return null;
        }

        /**
         * Add new analysis based on the previous analysis
         * @param pYear the tax year
         * @param pAnalysis the previous analysis
         * @return the bucket
         */
        protected AnalysisYear getNewAnalysis(TaxYear pYear,
                                              Analysis pAnalysis) {
            /* Locate the bucket in the list */
            AnalysisYear myYear = new AnalysisYear(this, pYear, pAnalysis);
            add(myYear);
            return myYear;
        }

        /**
         * Search for tax year
         * @param pYear the tax year to search for
         * @return the analysis
         */
        public AnalysisYear searchFor(TaxYear pYear) {
            DataListIterator<AnalysisYear> myIterator;
            AnalysisYear myCurr;

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the tax parameters */
            while ((myCurr = myIterator.next()) != null) {
                /* Break on match */
                if (myCurr.theYear.compareTo(pYear) == 0)
                    break;
            }

            /* Return to caller */
            return myCurr;
        }
    }

    /**
     * Process an event
     * @param pEvent the event to process
     * @throws JDataException
     */
    private void processEvent(Event pEvent) throws JDataException {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();

        /* If the event relates to a priced item split out the workings */
        if ((myDebit.isPriced()) || (myCredit.isPriced())) {
            /* Process as a Capital event */
            processCapitalEvent(pEvent);
        }

        /* Else handle the event normally */
        else {
            TransactionType myTrans = pEvent.getTransType();
            TransactionType.TransTypeList myTranList = theData.getTransTypes();
            BucketList myBuckets = theAnalysis.getList();

            /* If the event is interest */
            if (myTrans.isInterest()) {
                /* If the account is tax free */
                if (myDebit.isTaxFree()) {
                    /* The true transaction type is TaxFreeInterest */
                    myTrans = myTranList.searchFor(TransClass.TAXFREEINTEREST);
                }

                /* True debit account is the parent */
                myDebit = myDebit.getParent();
            }

            /* Adjust the debit account bucket */
            ActDetail myBucket = myBuckets.getAccountDetail(myDebit);
            myBucket.adjustForDebit(pEvent);

            /* Adjust the credit account bucket */
            myBucket = myBuckets.getAccountDetail(myCredit);
            myBucket.adjustForCredit(pEvent);

            /* If the event causes a tax credit */
            if (pEvent.getTaxCredit() != null) {
                /* Adjust the TaxMan account for the tax credit */
                theTaxMan.adjustForTaxCredit(pEvent);
                theTaxPaid.adjustForTaxCredit(pEvent);
            }

            /* Adjust the relevant transaction bucket */
            TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
            myTranBucket.adjustAmount(pEvent);
        }
    }

    /**
     * Process a capital event
     * @param pEvent the event to process
     * @throws JDataException
     */
    void processCapitalEvent(Event pEvent) throws JDataException {
        TransactionType myTrans = pEvent.getTransType();

        /* Switch on the transaction */
        switch (myTrans.getTranClass()) {
        /* Process a stock split */
            case STOCKSPLIT:
            case ADMINCHARGE:
                processStockSplit(pEvent);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTTAKEN:
                processTransferIn(pEvent);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTWAIVED:
                processStockRightWaived(pEvent);
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                processStockDeMerger(pEvent);
                break;
            /* Process a Cash TakeOver */
            case CASHTAKEOVER:
                processCashTakeover(pEvent);
                break;
            /* Process a Cash TakeOver */
            case STOCKTAKEOVER:
                processStockTakeover(pEvent);
                break;
            /* Process a Taxable Gain */
            case TAXABLEGAIN:
                processTaxableGain(pEvent);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pEvent);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case ENDOWMENT:
            case EXPENSE:
            case INHERITED:
            case TAXFREEINCOME:
                if (pEvent.getCredit().isPriced())
                    processTransferIn(pEvent);
                else
                    processTransferOut(pEvent);
                break;
            /* Throw an Exception */
            default:
                throw new JDataException(ExceptionClass.LOGIC, "Unexpected transaction type: "
                        + myTrans.getTranClass());
        }
    }

    /**
     * Process an event that is a stock split. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processStockSplit(Event pEvent) {
        /* Stock split has identical credit/debit and always has Units */
        Account myAccount = pEvent.getCredit();
        Units myUnits = pEvent.getUnits();

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event and record Current and delta units */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
        myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

        /* Add/Subtract the units movement for the account */
        myAsset.getUnits().addUnits(myUnits);
        myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
    }

    /**
     * Process an event that is a transfer into capital (also StockRightTaken and Dividend Re-investment).
     * This capital event relates only to the Credit Account
     * @param pEvent the event
     */
    private void processTransferIn(Event pEvent) {
        /* Transfer in is to the credit account and may or may not have units */
        Account myAccount = pEvent.getCredit();
        Account myDebit = pEvent.getDebit();
        Units myUnits = pEvent.getUnits();
        Money myAmount = pEvent.getAmount();
        TransactionType myTrans = pEvent.getTransType();

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
        myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
        myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myAmount);

        /* Adjust the cost of this account */
        myAsset.getCost().addAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

        /* If we have new units */
        if (myUnits != null) {
            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().addUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
        }

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

        /* Adjust the total money invested into this account */
        myAsset.getInvested().addAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* If the event causes a tax credit */
        if (pEvent.getTaxCredit() != null) {
            /* Adjust the TaxMan account for the tax credit */
            theTaxMan.adjustForTaxCredit(pEvent);
            theTaxPaid.adjustForTaxCredit(pEvent);
        }

        /* Adjust the debit account bucket */
        ActDetail myBucket = myBuckets.getAccountDetail(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }

    /**
     * Process a dividend event. This capital event relates to the only to Debit account,
     * @param pEvent the event
     */
    private void processDividend(Event pEvent) {
        /* The main account that we are interested in is the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        TransactionType myTrans = pEvent.getTransType();
        TransactionType.TransTypeList myTranList = theData.getTransTypes();
        Money myAmount = pEvent.getAmount();
        Money myTaxCredit = pEvent.getTaxCredit();
        Units myUnits = pEvent.getUnits();
        Account myDebit;

        /* If the account is tax free */
        if (myAccount.isTaxFree()) {
            /* The true transaction type is TaxFreeDividend */
            myTrans = myTranList.searchFor(TransClass.TAXFREEDIVIDEND);
        }

        /* else if the account is a unit trust */
        else if (myAccount.isUnitTrust()) {
            /* The true transaction type is UnitTrustDividend */
            myTrans = myTranList.searchFor(TransClass.UNITTRUSTDIVIDEND);
        }

        /* True debit account is the parent */
        myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        BucketList myBuckets = theAnalysis.getList();
        ActDetail myBucket = myBuckets.getAccountDetail(myDebit);
        myBucket.adjustForDebit(pEvent);

        /* Access the Asset Account Bucket */
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* If this is a re-investment */
        if (myAccount.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
            myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myAmount);

            /* Adjust the cost of this account */
            myAsset.getCost().addAmount(myAmount);
            myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

            /* Record the current/delta investment */
            myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
            myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

            /* Adjust the total money invested into this account */
            myAsset.getInvested().addAmount(myAmount);
            myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

            /* If we have new units */
            if (myUnits != null) {
                /* Record current and delta units */
                myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
                myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

                /* Add the units movement for the account */
                myAsset.getUnits().addUnits(myUnits);
                myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a gain from the account */
                myEvent.addAttribute(CapitalEvent.capitalInitialDiv, myAsset.getDividend());
                myEvent.addAttribute(CapitalEvent.capitalDeltaDiv, myTaxCredit);

                /* The Tax Credit is viewed as a gain from the account */
                myAsset.getDividend().addAmount(myTaxCredit);
                myEvent.addAttribute(CapitalEvent.capitalFinalDiv, myAsset.getDividend());
            }
        }

        /* else we are paying out to another account */
        else {
            /* Adjust the gains total for this asset */
            Money myDividends = new Money(myAmount);

            /* Any tax credit is viewed as a realised gain from the account */
            if (myTaxCredit != null)
                myDividends.addAmount(myTaxCredit);

            /* The Dividend is viewed as a dividend from the account */
            myEvent.addAttribute(CapitalEvent.capitalInitialDiv, myAsset.getDividend());
            myEvent.addAttribute(CapitalEvent.capitalDeltaDiv, myDividends);

            /* The Dividend is viewed as a gain from the account */
            myAsset.getDividend().addAmount(myDividends);
            myEvent.addAttribute(CapitalEvent.capitalFinalDiv, myAsset.getDividend());

            /* Adjust the credit account bucket */
            myBucket = myBuckets.getAccountDetail(myCredit);
            myBucket.adjustForCredit(pEvent);
        }

        /* If the event causes a tax credit */
        if (pEvent.getTaxCredit() != null) {
            /* Adjust the TaxMan account for the tax credit */
            theTaxMan.adjustForTaxCredit(pEvent);
            theTaxPaid.adjustForTaxCredit(pEvent);
        }

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }

    /**
     * Process an event that is a transfer from capital. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTransferOut(Event pEvent) {
        /* Transfer out is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Money myAmount = pEvent.getAmount();
        Units myUnits = pEvent.getUnits();
        TransactionType myTrans = pEvent.getTransType();
        Money myReduction;
        Money myDeltaCost;
        Money myDeltaGains;
        Money myCost;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event and record Current and delta costs */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Assume the the cost reduction is the full value */
        myReduction = new Money(myAmount);
        myCost = myAsset.getCost();

        /* If we are reducing units in the account */
        if ((myUnits != null) && (myUnits.isNonZero())) {
            /* The reduction is the relevant fraction of the cost */
            myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.getValue() > myCost.getValue()) {
            /* Reduction is the total cost */
            myReduction = new Money(myCost);
        }

        /* Determine the delta to the cost */
        myDeltaCost = new Money(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
            myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

            /* Adjust the cost appropriately */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
        }

        /* Determine the delta to the gains */
        myDeltaGains = new Money(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);

            /* Adjust the cost appropriately */
            myAsset.getGains().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
        }

        /* If we have reduced units */
        if (myUnits != null) {
            /* Access units as negative value */
            Units myDeltaUnits = new Units(myUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().subtractUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
        }

        /* Adjust the credit account bucket */
        ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }

    /**
     * Process an event that is a taxable gain. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTaxableGain(Event pEvent) {
        /* Transfer in is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Money myAmount = pEvent.getAmount();
        Units myUnits = pEvent.getUnits();
        TransactionType myTrans = pEvent.getTransType();
        Money myReduction;
        Money myDeltaCost;
        Money myDeltaGains;
        Money myCost;
        Account myDebit;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Assume the the cost reduction is the full value */
        myReduction = new Money(myAmount);
        myCost = myAsset.getCost();

        /* If we are reducing units in the account */
        if ((myUnits != null) && (myUnits.isNonZero())) {
            /* The reduction is the relevant fraction of the cost */
            myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.getValue() > myCost.getValue()) {
            /* Reduction is the total cost */
            myReduction = new Money(myCost);
        }

        /* Determine the delta to the cost */
        myDeltaCost = new Money(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
            myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

            /* Adjust the cost appropriately */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
        }

        /* Determine the delta to the gains */
        myDeltaGains = new Money(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);

            /* Adjust the cost appropriately */
            myAsset.getGains().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
        }

        /* If we have reduced units */
        if (myUnits != null) {
            /* Access units as negative value */
            Units myDeltaUnits = new Units(myUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
            myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

            /* Add the units movement for the account */
            myAsset.getUnits().subtractUnits(myUnits);
            myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
        }

        /* True debit account is the parent */
        myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        ExternalAccount myDebitBucket = (ExternalAccount) myBuckets.getAccountDetail(myDebit);
        myDebitBucket.adjustForTaxGainTaxCredit(pEvent);

        /* Adjust the credit account bucket */
        ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
        myTranBucket.getAmount().subtractAmount(myReduction);

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
    private void processStockRightWaived(Event pEvent) {
        /* Stock Right Waived is from the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPrice.AccountPriceList myPrices = theData.getPrices();
        Money myAmount = pEvent.getAmount();
        TransactionType myTrans = pEvent.getTransType();
        AccountPrice myActPrice;
        Price myPrice;
        Money myValue;
        Money myCost;
        Money myReduction;
        Money myPortion;
        Money myDeltaCost;
        Money myDeltaGains;

        /* Access the Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myAccount);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Get the appropriate price for the account */
        myActPrice = myPrices.getLatestPrice(myAccount, pEvent.getDate());
        myPrice = myActPrice.getPrice();
        myEvent.addAttribute(CapitalEvent.capitalInitialPrice, myPrice);

        /* Determine value of this stock at the current time */
        myValue = myAsset.getUnits().valueAtPrice(myPrice);
        myEvent.addAttribute(CapitalEvent.capitalInitialValue, myValue);

        /* Access the current cost */
        myCost = myAsset.getCost();

        /* Calculate the portion of the value that creates a large transaction */
        myPortion = myValue.valueAtRate(rateLimit);

        /* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
        if ((myAmount.getValue() > valueLimit.getValue()) && (myAmount.getValue() > myPortion.getValue())) {
            /* Determine the total value of rights plus share value */
            Money myTotalValue = new Money(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the reduction as a proportion of the total value */
            myReduction = myAsset.getCost().valueAtWeight(myAmount, myTotalValue);
        }

        /* else this is viewed as small and is taken out of the cost */
        else {
            /* Set the reduction to be the entire amount */
            myReduction = new Money(myAmount);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.getValue() > myCost.getValue()) {
            /* Reduction is the total cost */
            myReduction = new Money(myCost);
        }

        /* Calculate the delta cost */
        myDeltaCost = new Money(myReduction);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
        myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

        /* Adjust the cost */
        myCost.addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);

        /* Calculate the delta gains */
        myDeltaGains = new Money(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* Record the current/delta gains */
        myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
        myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);

        /* Adjust the gains */
        myAsset.getGains().addAmount(myDeltaGains);
        myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());

        /* Adjust the credit account bucket */
        ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }

    /**
     * Process an event that is Stock DeMerger. This capital event relates to both the Credit and Debit
     * accounts
     * @param pEvent the event
     */
    private void processStockDeMerger(Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Dilution myDilution = pEvent.getDilution();
        Units myUnits = pEvent.getUnits();
        Money myCost;
        Money myDeltaCost;
        Money myNewCost;

        /* Access the Debit Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Calculate the diluted value of the Debit account */
        myCost = myAsset.getCost();
        myNewCost = myCost.getDilutedAmount(myDilution);

        /* Calculate the delta to the cost */
        myDeltaCost = new Money(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
        myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

        /* Record the new total cost */
        myCost.addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myDeltaCost);

        /* Adjust the investment for the debit account */
        myAsset.getInvested().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Access the Credit Asset Account Bucket */
        myAsset = (AssetAccount) myBuckets.getAccountDetail(myCredit);

        /* Allocate a Capital event */
        myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new Money(myDeltaCost);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
        myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

        /* Adjust the cost */
        myAsset.getCost().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myDeltaCost);

        /* Adjust the investment */
        myAsset.getInvested().addAmount(myDeltaCost);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Record the current/delta units */
        myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
        myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

        /* Adjust the units for the credit account */
        myAsset.getUnits().addUnits(myUnits);
        myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
    }

    /**
     * Process an event that is the Cash portion of a StockTakeOver. This capital event relates only to the
     * Debit Account
     * @param pEvent the event
     */
    private void processCashTakeover(Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPrice.AccountPriceList myPrices = theData.getPrices();
        Money myAmount = pEvent.getAmount();
        TransactionType myTrans = pEvent.getTransType();
        AccountPrice myActPrice;
        Price myPrice;
        Money myValue;
        Money myPortion;
        Money myReduction;
        Money myCost;
        Money myResidualCost;
        Money myDeltaCost;
        Money myDeltaGains;

        /* Access the Debit Asset Account Bucket */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myAsset = (AssetAccount) myBuckets.getAccountDetail(myDebit);

        /* Allocate a Capital event */
        CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

        /* Record the current/delta investment */
        myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

        /* Adjust the total amount invested into this account */
        myAsset.getInvested().subtractAmount(myAmount);
        myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());

        /* Get the appropriate price for the account */
        myActPrice = myPrices.getLatestPrice(myDebit, pEvent.getDate());
        myPrice = myActPrice.getPrice();
        myEvent.addAttribute(CapitalEvent.capitalInitialPrice, myPrice);

        /* Determine value of this stock at the current time */
        myValue = myAsset.getUnits().valueAtPrice(myPrice);
        myEvent.addAttribute(CapitalEvent.capitalInitialValue, myValue);

        /* Access the current cost */
        myCost = myAsset.getCost();

        /* Calculate the portion of the value that creates a large transaction */
        myPortion = myValue.valueAtRate(rateLimit);

        /* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.getValue() > valueLimit.getValue()) && (myAmount.getValue() > myPortion.getValue())) {
            /* We have to defer the allocation of cost until we know of the Stock TakeOver part */
            myEvent.addAttribute(CapitalEvent.capitalTakeoverCash, myAmount);
        }

        /* else this is viewed as small and is taken out of the cost */
        else {
            /* Set the reduction to be the entire amount */
            myReduction = new Money(myAmount);

            /* If the reduction is greater than the total cost */
            if (myReduction.getValue() > myCost.getValue()) {
                /* Reduction is the total cost */
                myReduction = new Money(myCost);
            }

            /* Calculate the residual cost */
            myResidualCost = new Money(myReduction);
            myResidualCost.negate();
            myResidualCost.addAmount(myCost);

            /* Record the residual cost */
            myEvent.addAttribute(CapitalEvent.capitalTakeoverCost, myResidualCost);

            /* Calculate the delta cost */
            myDeltaCost = new Money(myCost);
            myDeltaCost.negate();

            /* Record the current/delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
            myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

            /* Adjust the cost */
            myCost.addAmount(myDeltaCost);
            myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);

            /* Calculate the gains */
            myDeltaGains = new Money(myAmount);
            myDeltaGains.addAmount(myDeltaCost);

            /* Record the current/delta cost */
            myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
            myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);

            /* Adjust the gained */
            myAsset.getGained().addAmount(myDeltaGains);
            myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
        }

        /* Adjust the credit account bucket */
        ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }

    /**
     * Process an event that is StockTakeover. This capital event relates to both the Credit and Debit
     * accounts In particular it makes reference to the CashTakeOver aspect of the debit account
     * @param pEvent the event
     */
    private void processStockTakeover(Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPrice.AccountPriceList myPrices = theData.getPrices();
        Units myUnits = pEvent.getUnits();
        TransactionType myTrans = pEvent.getTransType();
        AccountPrice myActPrice;
        Price myPrice;
        Money myValue;
        Money myStockCost;
        Money myCashCost;
        Money myTotalCost;
        Money myDeltaCost;
        Money myDeltaGains;
        Units myDeltaUnits;
        Money myResidualCash = null;
        CapitalEvent myCredEvent;
        CapitalEvent myDebEvent;

        /* Access the Asset Account Buckets */
        BucketList myBuckets = theAnalysis.getList();
        AssetAccount myDebAsset = (AssetAccount) myBuckets.getAccountDetail(myDebit);
        AssetAccount myCredAsset = (AssetAccount) myBuckets.getAccountDetail(myCredit);

        /* Access the cash takeover record for the debit account if it exists */
        myDebEvent = myDebAsset.getCapitalEvents().getCashTakeOver();

        /* If we have had a cash takeover event */
        if (myDebEvent != null) {
            /* Access the residual cost/cash */
            myResidualCash = (Money) myDebEvent.findAttribute(CapitalEvent.capitalTakeoverCash);
        }

        /* Allocate new Capital events */
        myDebEvent = myDebAsset.getCapitalEvents().addEvent(pEvent);
        myCredEvent = myCredAsset.getCapitalEvents().addEvent(pEvent);

        /* If we have a Cash TakeOver component */
        if (myResidualCash != null) {
            /* Get the appropriate price for the credit account */
            myActPrice = myPrices.getLatestPrice(myCredit, pEvent.getDate());
            myPrice = myActPrice.getPrice();
            myDebEvent.addAttribute(CapitalEvent.capitalTakeoverPrice, myPrice);

            /* Determine value of the stock part of the takeover */
            myValue = myUnits.valueAtPrice(myPrice);
            myDebEvent.addAttribute(CapitalEvent.capitalTakeoverValue, myValue);

            /* Calculate the total cost of the takeover */
            myTotalCost = new Money(myResidualCash);
            myTotalCost.addAmount(myValue);

            /* Split the total cost of the takeover between stock and cash */
            myStockCost = myTotalCost.valueAtWeight(myValue, myTotalCost);
            myCashCost = new Money(myTotalCost);
            myCashCost.subtractAmount(myStockCost);

            /* Record the values */
            myDebEvent.addAttribute(CapitalEvent.capitalTakeoverCash, myCashCost);
            myDebEvent.addAttribute(CapitalEvent.capitalTakeoverStock, myStockCost);
            myDebEvent.addAttribute(CapitalEvent.capitalTakeoverTotal, myTotalCost);

            /* The Delta Gains is the Amount minus the CashCost */
            myDeltaGains = new Money(myResidualCash);
            myDeltaGains.subtractAmount(myCashCost);

            /* Record the gains */
            myDebEvent.addAttribute(CapitalEvent.capitalInitialGains, myDebAsset.getGains());
            myDebEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);
            myDebAsset.getGained().addAmount(myDeltaGains);
            myDebEvent.addAttribute(CapitalEvent.capitalFinalGains, myDebAsset.getGains());

            /* The cost of the new stock is the stock cost */
            myCredEvent.addAttribute(CapitalEvent.capitalInitialCost, myCredAsset.getCost());
            myCredEvent.addAttribute(CapitalEvent.capitalDeltaCost, myStockCost);
            myCredAsset.getCost().addAmount(myStockCost);
            myCredEvent.addAttribute(CapitalEvent.capitalFinalCost, myCredAsset.getCost());
        }

        /* else there is no cash part to this takeover */
        else {
            /* The cost of the new stock is the residual debit cost */
            myDeltaCost = myDebAsset.getCost();
            myCredEvent.addAttribute(CapitalEvent.capitalInitialCost, myCredAsset.getCost());
            myCredEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);
            myCredAsset.getCost().addAmount(myDeltaCost);
            myCredEvent.addAttribute(CapitalEvent.capitalFinalCost, myCredAsset.getCost());
        }

        /* Calculate the delta cost */
        myDeltaCost = new Money(myDebAsset.getCost());
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myDebEvent.addAttribute(CapitalEvent.capitalInitialCost, myDebAsset.getCost());
        myDebEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

        /* Adjust the cost */
        myDebAsset.getCost().addAmount(myDeltaCost);
        myDebEvent.addAttribute(CapitalEvent.capitalFinalCost, myDebAsset.getCost());

        /* Calculate the delta units */
        myDeltaUnits = new Units(myDebAsset.getUnits());
        myDeltaUnits.negate();

        /* Record the current/delta units */
        myDebEvent.addAttribute(CapitalEvent.capitalInitialUnits, myDebAsset.getUnits());
        myDebEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

        /* Adjust the Units */
        myDebAsset.getUnits().addUnits(myDeltaUnits);
        myDebEvent.addAttribute(CapitalEvent.capitalFinalUnits, myDebAsset.getUnits());

        /* Record the current/delta units */
        myCredEvent.addAttribute(CapitalEvent.capitalInitialUnits, myCredAsset.getUnits());
        myCredEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);
        myCredAsset.getUnits().addUnits(myUnits);
        myCredEvent.addAttribute(CapitalEvent.capitalFinalUnits, myCredAsset.getUnits());

        /* Adjust the relevant transaction bucket */
        TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
        myTranBucket.adjustAmount(pEvent);
    }
}
