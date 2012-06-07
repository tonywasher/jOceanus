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
package uk.co.tolcroft.finance.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JGordianKnot.SecureManager;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.AccountRate.AccountRateList;
import uk.co.tolcroft.finance.data.AccountType.AccountTypeList;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.EventData.EventDataList;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.finance.data.EventValue.EventValueList;
import uk.co.tolcroft.finance.data.Frequency.FrequencyList;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.finance.data.TaxRegime.TaxRegimeList;
import uk.co.tolcroft.finance.data.TaxType.TaxTypeList;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.views.EventAnalysis;
import uk.co.tolcroft.finance.views.MetaAnalysis;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class FinanceData extends DataSet<FinanceData> {
    /**
     * Report fields.
     */
    protected static final JDataFields theFields = new JDataFields(FinanceData.class.getSimpleName(),
            DataSet.FIELD_DEFS);

    /* Field IDs */
    public static final JDataField FIELD_ACTTYPES = theFields.declareLocalField("AccountTypes");
    public static final JDataField FIELD_TRANTYPES = theFields.declareLocalField("TransactionTypes");
    public static final JDataField FIELD_TAXTYPES = theFields.declareLocalField("TaxTypes");
    public static final JDataField FIELD_FREQS = theFields.declareLocalField("Frequencies");
    public static final JDataField FIELD_TAXREGS = theFields.declareLocalField("TaxRegimes");
    public static final JDataField FIELD_INFOTYPES = theFields.declareLocalField("EventInfoTypes");
    public static final JDataField FIELD_TAXYEARS = theFields.declareLocalField("TaxYears");
    public static final JDataField FIELD_ACCOUNTS = theFields.declareLocalField("Accounts");
    public static final JDataField FIELD_RATES = theFields.declareLocalField("AccountRates");
    public static final JDataField FIELD_PRICES = theFields.declareLocalField("AccountPrices");
    public static final JDataField FIELD_PATTERNS = theFields.declareLocalField("AccountPatterns");
    public static final JDataField FIELD_EVENTS = theFields.declareLocalField("Events");
    public static final JDataField FIELD_EVENTVALUES = theFields.declareLocalField("EventValues");
    public static final JDataField FIELD_EVENTDATA = theFields.declareLocalField("EventData");

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_ACTTYPES)
            return theActTypes;
        if (pField == FIELD_TRANTYPES)
            return theTransTypes;
        if (pField == FIELD_TAXTYPES)
            return theTaxTypes;
        if (pField == FIELD_TAXREGS)
            return theTaxRegimes;
        if (pField == FIELD_FREQS)
            return theFrequencys;
        if (pField == FIELD_INFOTYPES)
            return theInfoTypes;
        if (pField == FIELD_TAXYEARS)
            return theTaxYears;
        if (pField == FIELD_ACCOUNTS)
            return theAccounts;
        if (pField == FIELD_RATES)
            return theRates;
        if (pField == FIELD_PRICES)
            return thePrices;
        if (pField == FIELD_PATTERNS)
            return thePatterns;
        if (pField == FIELD_EVENTS)
            return theEvents;
        if (pField == FIELD_EVENTVALUES)
            return theEventValues;
        if (pField == FIELD_EVENTDATA)
            return theEventData;
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FinanceData.class.getSimpleName();
    }

    /* Members */
    private AccountTypeList theActTypes = null;
    private TransTypeList theTransTypes = null;
    private TaxTypeList theTaxTypes = null;
    private TaxRegimeList theTaxRegimes = null;
    private FrequencyList theFrequencys = null;
    private EventInfoTypeList theInfoTypes = null;
    private TaxYearList theTaxYears = null;
    private AccountList theAccounts = null;
    private AccountRateList theRates = null;
    private AccountPriceList thePrices = null;
    private PatternList thePatterns = null;
    private EventList theEvents = null;
    private EventValueList theEventValues = null;
    private EventDataList theEventData = null;
    private DateDayRange theDateRange = null;
    private EventAnalysis theAnalysis = null;
    private LoadState theLoadState = LoadState.INITIAL;

    /* Access Methods */
    public AccountTypeList getAccountTypes() {
        return theActTypes;
    }

    public TransTypeList getTransTypes() {
        return theTransTypes;
    }

    public TaxTypeList getTaxTypes() {
        return theTaxTypes;
    }

    public TaxRegimeList getTaxRegimes() {
        return theTaxRegimes;
    }

    public FrequencyList getFrequencys() {
        return theFrequencys;
    }

    public EventInfoTypeList getInfoTypes() {
        return theInfoTypes;
    }

    public TaxYearList getTaxYears() {
        return theTaxYears;
    }

    public AccountList getAccounts() {
        return theAccounts;
    }

    public AccountRateList getRates() {
        return theRates;
    }

    public AccountPriceList getPrices() {
        return thePrices;
    }

    public PatternList getPatterns() {
        return thePatterns;
    }

    public EventList getEvents() {
        return theEvents;
    }

    public EventValueList getEventValues() {
        return theEventValues;
    }

    public EventDataList getEventData() {
        return theEventData;
    }

    public DateDayRange getDateRange() {
        return theDateRange;
    }

    public EventAnalysis getAnalysis() {
        return theAnalysis;
    }

    public LoadState getLoadState() {
        return theLoadState;
    }

    /**
     * Standard constructor
     * @param pSecurity the secure manager
     */
    public FinanceData(SecureManager pSecurity) {
        /* Call Super-constructor */
        super(pSecurity);

        /* Create the empty lists */
        theActTypes = new AccountTypeList(this);
        theTransTypes = new TransTypeList(this);
        theTaxTypes = new TaxTypeList(this);
        theTaxRegimes = new TaxRegimeList(this);
        theFrequencys = new FrequencyList(this);
        theInfoTypes = new EventInfoTypeList(this);
        theTaxYears = new TaxYearList(this);
        theAccounts = new AccountList(this);
        theRates = new AccountRateList(this);
        thePrices = new AccountPriceList(this);
        thePatterns = new PatternList(this);
        theEvents = new EventList(this);
        theEventData = new EventDataList(this);
        theEventValues = new EventValueList(this);

        /* Declare the lists */
        declareLists();
    }

    /**
     * Constructor for a cloned DataSet
     * @param pSource the source DataSet
     */
    private FinanceData(FinanceData pSource) {
        super(pSource);
    }

    /**
     * Construct an update extract for a FinanceData Set.
     * @return the extract
     */
    public FinanceData getUpdateSet() {
        /* Build an empty DataSet */
        FinanceData myExtract = new FinanceData(this);

        /* Obtain underlying updates */
        myExtract.getUpdateSet(this);

        /* Build the static extract */
        myExtract.theActTypes = theActTypes.getUpdateList();
        myExtract.theTransTypes = theTransTypes.getUpdateList();
        myExtract.theTaxTypes = theTaxTypes.getUpdateList();
        myExtract.theTaxRegimes = theTaxRegimes.getUpdateList();
        myExtract.theFrequencys = theFrequencys.getUpdateList();
        myExtract.theInfoTypes = theInfoTypes.getUpdateList();

        /* Build the data extract */
        myExtract.theTaxYears = theTaxYears.getUpdateList();
        myExtract.theAccounts = theAccounts.getUpdateList();
        myExtract.theRates = theRates.getUpdateList();
        myExtract.thePrices = thePrices.getUpdateList();
        myExtract.thePatterns = thePatterns.getUpdateList();
        myExtract.theEvents = theEvents.getUpdateList();
        myExtract.theEventData = theEventData.getUpdateList();
        myExtract.theEventValues = theEventValues.getUpdateList();

        /* Declare the lists */
        myExtract.declareLists();

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a Deep Copy for a DataSet.
     * @return the deep copy
     */
    @Override
    public FinanceData getDeepCopy() {
        /* Build an empty DataSet */
        FinanceData myExtract = new FinanceData(this);

        /* Obtain underlying updates */
        myExtract.getDeepCopy(this);

        /* Build the static extract */
        myExtract.theActTypes = theActTypes.getDeepCopy(myExtract);
        myExtract.theTransTypes = theTransTypes.getDeepCopy(myExtract);
        myExtract.theTaxTypes = theTaxTypes.getDeepCopy(myExtract);
        myExtract.theTaxRegimes = theTaxRegimes.getDeepCopy(myExtract);
        myExtract.theFrequencys = theFrequencys.getDeepCopy(myExtract);
        myExtract.theInfoTypes = theInfoTypes.getDeepCopy(myExtract);

        /* Build the data extract */
        myExtract.theTaxYears = theTaxYears.getDeepCopy(myExtract);
        myExtract.theAccounts = theAccounts.getDeepCopy(myExtract);
        myExtract.theRates = theRates.getDeepCopy(myExtract);
        myExtract.thePrices = thePrices.getDeepCopy(myExtract);
        myExtract.thePatterns = thePatterns.getDeepCopy(myExtract);
        myExtract.theEvents = theEvents.getDeepCopy(myExtract);
        myExtract.theEventData = theEventData.getDeepCopy(myExtract);
        myExtract.theEventValues = theEventValues.getDeepCopy(myExtract);

        /* Declare the lists */
        myExtract.declareLists();

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items
     * that differ between the two DataSets. Items that are in this list, but not in the old list will be
     * viewed as inserted. Items that are in the old list but not in this list will be viewed as deleted.
     * Items that are in both lists but differ will be viewed as changed
     * @param pOld The DataSet to compare to
     * @return the difference extract
     * @throws JDataException
     */
    @Override
    public FinanceData getDifferenceSet(FinanceData pOld) throws JDataException {
        /* Make sure that the DataSet if the same type */
        if (!(pOld instanceof FinanceData))
            throw new JDataException(ExceptionClass.LOGIC, "Invalid DataSet type");

        /* Cast correctly */
        FinanceData myOld = (FinanceData) pOld;

        /* Build an empty DataSet */
        FinanceData myDiffers = new FinanceData(this);

        /* Obtain underlying differences */
        myDiffers.getDifferenceSet(this, myOld);

        /* Build the static differences */
        myDiffers.theActTypes = theActTypes.getDifferences(myOld.getAccountTypes());
        myDiffers.theTransTypes = theTransTypes.getDifferences(myOld.getTransTypes());
        myDiffers.theTaxTypes = theTaxTypes.getDifferences(myOld.getTaxTypes());
        myDiffers.theTaxRegimes = theTaxRegimes.getDifferences(myOld.getTaxRegimes());
        myDiffers.theFrequencys = theFrequencys.getDifferences(myOld.getFrequencys());
        myDiffers.theInfoTypes = theInfoTypes.getDifferences(myOld.getInfoTypes());

        /* Build the data differences */
        myDiffers.theTaxYears = theTaxYears.getDifferences(myOld.getTaxYears());
        myDiffers.theAccounts = theAccounts.getDifferences(myOld.getAccounts());
        myDiffers.theRates = theRates.getDifferences(myOld.getRates());
        myDiffers.thePrices = thePrices.getDifferences(myOld.getPrices());
        myDiffers.thePatterns = thePatterns.getDifferences(myOld.getPatterns());
        myDiffers.theEvents = theEvents.getDifferences(myOld.getEvents());
        myDiffers.theEventData = theEventData.getDifferences(myOld.getEventData());
        myDiffers.theEventValues = theEventValues.getDifferences(myOld.getEventValues());

        /* Declare the lists */
        myDiffers.declareLists();

        /* Return the differences */
        return myDiffers;
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pOld The old data to reBase against
     * @throws JDataException
     */
    @Override
    public void reBase(FinanceData pOld) throws JDataException {
        /* Make sure that the DataSet if the same type */
        if (!(pOld instanceof FinanceData))
            throw new JDataException(ExceptionClass.LOGIC, "Invalid DataSet type");

        /* Cast correctly */
        FinanceData myOld = (FinanceData) pOld;

        /* Call super-class */
        super.reBase(myOld);

        /* ReBase the static items */
        theActTypes.reBase(myOld.getAccountTypes());
        theTransTypes.reBase(myOld.getTransTypes());
        theTaxTypes.reBase(myOld.getTaxTypes());
        theTaxRegimes.reBase(myOld.getTaxRegimes());
        theFrequencys.reBase(myOld.getFrequencys());
        theInfoTypes.reBase(myOld.getInfoTypes());

        /* ReBase the data items */
        theTaxYears.reBase(myOld.getTaxYears());
        theAccounts.reBase(myOld.getAccounts());
        theRates.reBase(myOld.getRates());
        thePrices.reBase(myOld.getPrices());
        thePatterns.reBase(myOld.getPatterns());
        theEvents.reBase(myOld.getEvents());
        theEventData.reBase(myOld.getEventData());
        theEventValues.reBase(myOld.getEventValues());
    }

    /**
     * Declare lists
     */
    private void declareLists() {
        /* Declare the lists */
        addList(theActTypes);
        addList(theTransTypes);
        addList(theTaxTypes);
        addList(theTaxRegimes);
        addList(theFrequencys);
        addList(theInfoTypes);
        addList(theTaxYears);
        addList(theAccounts);
        addList(theRates);
        addList(thePrices);
        addList(thePatterns);
        addList(theEvents);
        addList(theEventData);
        addList(theEventValues);
    }

    /**
     * Calculate the allowed Date Range
     */
    public void calculateDateRange() {
        theDateRange = theTaxYears.getRange();
        theEvents.setRange(theDateRange);
    }

    /**
     * Analyse the data
     * @param pControl the data view
     * @throws JDataException
     */
    @Override
    public void analyseData(DataControl<?> pControl) throws JDataException {
        MetaAnalysis myMetaAnalysis;

        /* Update INITIAL Load status */
        if (theLoadState == LoadState.INITIAL)
            theLoadState = LoadState.FINAL;

        /* Reset the flags on static data (ignoring TaxTypes) */
        theActTypes.clearActive();
        theTransTypes.clearActive();
        theTaxRegimes.clearActive();
        theFrequencys.clearActive();

        /* Reset the flags on the accounts and tax years */
        theAccounts.clearActive();
        theTaxYears.clearActive();

        /* Note active items referenced by tax years */
        theTaxYears.markActiveItems();

        /* Create the analysis */
        theAnalysis = new EventAnalysis(pControl, this);

        /* Note active items referenced by rates */
        theRates.markActiveItems();

        /* Note active items referenced by prices */
        thePrices.markActiveItems();

        /* Mark active items referenced by patterns */
        thePatterns.markActiveItems();

        /* Access the most recent metaAnalysis */
        myMetaAnalysis = theAnalysis.getMetaAnalysis();

        /* Note active accounts by asset */
        if (myMetaAnalysis != null)
            myMetaAnalysis.markActiveAccounts();

        /* Note active accounts */
        theAccounts.markActiveItems();

        /* Note that we are now fully loaded */
        theLoadState = LoadState.LOADED;
    }

    /**
     * Enumeration of load states of data
     */
    public static enum LoadState {
        /**
         * Initial loading, with parental account links and close-ability not yet done
         */
        INITIAL,

        /**
         * Final loading with parental links and close-ability done
         */
        FINAL,

        /**
         * Fully loaded
         */
        LOADED;
    }
}
