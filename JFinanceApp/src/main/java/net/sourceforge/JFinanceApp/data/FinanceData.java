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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataModels.data.DataList.ListStyle;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDateDay.JDateDayRange;
import net.sourceforge.JFinanceApp.data.Account.AccountList;
import net.sourceforge.JFinanceApp.data.AccountPrice.AccountPriceList;
import net.sourceforge.JFinanceApp.data.AccountRate.AccountRateList;
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.EventData.EventDataList;
import net.sourceforge.JFinanceApp.data.EventValue.EventValueList;
import net.sourceforge.JFinanceApp.data.Pattern.PatternList;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.JFinanceApp.data.statics.AccountType.AccountTypeList;
import net.sourceforge.JFinanceApp.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.JFinanceApp.data.statics.Frequency.FrequencyList;
import net.sourceforge.JFinanceApp.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.JFinanceApp.data.statics.TaxType.TaxTypeList;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.JFinanceApp.data.statics.TransactionType.TransTypeList;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JPreferenceSet.PreferenceManager;

/**
 * FinanceData dataSet.
 * @author Tony Washer
 */
public class FinanceData extends DataSet<FinanceData> {
    /**
     * Money accounting format width.
     */
    private static final int ACCOUNTING_WIDTH = 10;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(FinanceData.class.getSimpleName(),
            DataSet.FIELD_DEFS);

    /**
     * AccountTypes Field Id.
     */
    public static final JDataField FIELD_ACTTYPES = FIELD_DEFS.declareLocalField("AccountTypes");

    /**
     * TransactionTypes Field Id.
     */
    public static final JDataField FIELD_TRANTYPES = FIELD_DEFS.declareLocalField("TransactionTypes");

    /**
     * TaxTypes Field Id.
     */
    public static final JDataField FIELD_TAXTYPES = FIELD_DEFS.declareLocalField("TaxTypes");

    /**
     * Frequencies Field Id.
     */
    public static final JDataField FIELD_FREQS = FIELD_DEFS.declareLocalField("Frequencies");

    /**
     * TaxRegimes Field Id.
     */
    public static final JDataField FIELD_TAXREGS = FIELD_DEFS.declareLocalField("TaxRegimes");

    /**
     * TaxYearInfoTypes Field Id.
     */
    public static final JDataField FIELD_TAXINFOTYPES = FIELD_DEFS.declareLocalField("TaxYearInfoTypes");

    /**
     * AccountInfoTypes Field Id.
     */
    public static final JDataField FIELD_ACTINFOTYPES = FIELD_DEFS.declareLocalField("AccountInfoTypes");

    /**
     * EventInfoTypes Field Id.
     */
    public static final JDataField FIELD_EVTINFOTYPES = FIELD_DEFS.declareLocalField("EventInfoTypes");

    /**
     * TaxYears Field Id.
     */
    public static final JDataField FIELD_TAXYEARS = FIELD_DEFS.declareLocalField("TaxYears");

    /**
     * Accounts Field Id.
     */
    public static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField("Accounts");

    /**
     * Rates Field Id.
     */
    public static final JDataField FIELD_RATES = FIELD_DEFS.declareLocalField("AccountRates");

    /**
     * Prices Field Id.
     */
    public static final JDataField FIELD_PRICES = FIELD_DEFS.declareLocalField("AccountPrices");

    /**
     * Patterns Field Id.
     */
    public static final JDataField FIELD_PATTERNS = FIELD_DEFS.declareLocalField("AccountPatterns");

    /**
     * Events Field Id.
     */
    public static final JDataField FIELD_EVENTS = FIELD_DEFS.declareLocalField("Events");

    /**
     * EventValues Field Id.
     */
    public static final JDataField FIELD_EVENTVALUES = FIELD_DEFS.declareLocalField("EventValues");

    /**
     * EventData Field Id.
     */
    public static final JDataField FIELD_EVENTDATA = FIELD_DEFS.declareLocalField("EventData");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACTTYPES.equals(pField)) {
            return (theActTypes.size() > 0) ? theActTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_TRANTYPES.equals(pField)) {
            return (theTransTypes.size() > 0) ? theTransTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXTYPES.equals(pField)) {
            return (theTaxTypes.size() > 0) ? theTaxTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXREGS.equals(pField)) {
            return (theTaxRegimes.size() > 0) ? theTaxRegimes : JDataFieldValue.SkipField;
        }
        if (FIELD_FREQS.equals(pField)) {
            return (theFrequencys.size() > 0) ? theFrequencys : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXINFOTYPES.equals(pField)) {
            return (theTaxInfoTypes.size() > 0) ? theTaxInfoTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_ACTINFOTYPES.equals(pField)) {
            return (theActInfoTypes.size() > 0) ? theActInfoTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTINFOTYPES.equals(pField)) {
            return (theEventInfoTypes.size() > 0) ? theEventInfoTypes : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXYEARS.equals(pField)) {
            return (theTaxYears.size() > 0) ? theTaxYears : JDataFieldValue.SkipField;
        }
        if (FIELD_ACCOUNTS.equals(pField)) {
            return (theAccounts.size() > 0) ? theAccounts : JDataFieldValue.SkipField;
        }
        if (FIELD_RATES.equals(pField)) {
            return (theRates.size() > 0) ? theRates : JDataFieldValue.SkipField;
        }
        if (FIELD_PRICES.equals(pField)) {
            return (thePrices.size() > 0) ? thePrices : JDataFieldValue.SkipField;
        }
        if (FIELD_PATTERNS.equals(pField)) {
            return (thePatterns.size() > 0) ? thePatterns : JDataFieldValue.SkipField;
        }
        if (FIELD_EVENTS.equals(pField)) {
            return (theEvents.size() > 0) ? theEvents : JDataFieldValue.SkipField;
        }
        if (FIELD_EVENTVALUES.equals(pField)) {
            return (theEventValues.size() > 0) ? theEventValues : JDataFieldValue.SkipField;
        }
        if (FIELD_EVENTDATA.equals(pField)) {
            return (theEventData.size() > 0) ? theEventData : JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FinanceData.class.getSimpleName();
    }

    /**
     * AccountTypes.
     */
    private AccountTypeList theActTypes = null;

    /**
     * TransactionTypes.
     */
    private TransTypeList theTransTypes = null;

    /**
     * TaxTypes.
     */
    private TaxTypeList theTaxTypes = null;

    /**
     * TaxRegimes.
     */
    private TaxRegimeList theTaxRegimes = null;

    /**
     * Frequencies.
     */
    private FrequencyList theFrequencys = null;

    /**
     * TaxYearInfoTypes.
     */
    private TaxYearInfoTypeList theTaxInfoTypes = null;

    /**
     * AccountInfoTypes.
     */
    private AccountInfoTypeList theActInfoTypes = null;

    /**
     * EventInfoTypes.
     */
    private EventInfoTypeList theEventInfoTypes = null;

    /**
     * TaxYears.
     */
    private TaxYearList theTaxYears = null;

    /**
     * Accounts.
     */
    private AccountList theAccounts = null;

    /**
     * Rates.
     */
    private AccountRateList theRates = null;

    /**
     * Prices.
     */
    private AccountPriceList thePrices = null;

    /**
     * Patterns.
     */
    private PatternList thePatterns = null;

    /**
     * Events.
     */
    private EventList theEvents = null;

    /**
     * EventValues.
     */
    private EventValueList theEventValues = null;

    /**
     * EventData.
     */
    private EventDataList theEventData = null;

    /**
     * DataSet range.
     */
    private JDateDayRange theDateRange = null;

    /**
     * LoadState.
     */
    private LoadState theLoadState = LoadState.INITIAL;

    /**
     * General formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Obtain AccountTypes.
     * @return the Account types
     */
    public AccountTypeList getAccountTypes() {
        return theActTypes;
    }

    /**
     * Obtain TransactionTypes.
     * @return the Transaction types
     */
    public TransTypeList getTransTypes() {
        return theTransTypes;
    }

    /**
     * Obtain TaxTypes.
     * @return the Tax types
     */
    public TaxTypeList getTaxTypes() {
        return theTaxTypes;
    }

    /**
     * Obtain TaxRegimes.
     * @return the TaxRegimes
     */
    public TaxRegimeList getTaxRegimes() {
        return theTaxRegimes;
    }

    /**
     * Obtain Frequencies.
     * @return the Frequencies
     */
    public FrequencyList getFrequencys() {
        return theFrequencys;
    }

    /**
     * Obtain TaxInfoTypes.
     * @return the TaxYear Info types
     */
    public TaxYearInfoTypeList getTaxInfoTypes() {
        return theTaxInfoTypes;
    }

    /**
     * Obtain AccountInfoTypes.
     * @return the Account Info types
     */
    public AccountInfoTypeList getActInfoTypes() {
        return theActInfoTypes;
    }

    /**
     * Obtain EventInfoTypes.
     * @return the Event Info types
     */
    public EventInfoTypeList getEventInfoTypes() {
        return theEventInfoTypes;
    }

    /**
     * Obtain TaxYears.
     * @return the TaxYears
     */
    public TaxYearList getTaxYears() {
        return theTaxYears;
    }

    /**
     * Obtain Accounts.
     * @return the Accounts
     */
    public AccountList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain AccountRates.
     * @return the Account rates
     */
    public AccountRateList getRates() {
        return theRates;
    }

    /**
     * Obtain AccountPrices.
     * @return the Account prices
     */
    public AccountPriceList getPrices() {
        return thePrices;
    }

    /**
     * Obtain Patterns.
     * @return the Patterns
     */
    public PatternList getPatterns() {
        return thePatterns;
    }

    /**
     * Obtain Events.
     * @return the Events
     */
    public EventList getEvents() {
        return theEvents;
    }

    /**
     * Obtain EventValues.
     * @return the Event Values
     */
    public EventValueList getEventValues() {
        return theEventValues;
    }

    /**
     * Obtain EventData.
     * @return the Event Data
     */
    public EventDataList getEventData() {
        return theEventData;
    }

    /**
     * Obtain Date range.
     * @return the Date Range
     */
    public JDateDayRange getDateRange() {
        return theDateRange;
    }

    /**
     * Obtain Load State.
     * @return the Load State
     */
    public LoadState getLoadState() {
        return theLoadState;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Standard constructor.
     * @param pSecurity the secure manager
     * @param pPreferenceMgr the preference manager
     */
    public FinanceData(final SecureManager pSecurity,
                       final PreferenceManager pPreferenceMgr) {
        /* Call Super-constructor */
        super(pSecurity, pPreferenceMgr);

        /* Create the empty lists */
        theActTypes = new AccountTypeList(this);
        theTransTypes = new TransTypeList(this);
        theTaxTypes = new TaxTypeList(this);
        theTaxRegimes = new TaxRegimeList(this);
        theFrequencys = new FrequencyList(this);
        theTaxInfoTypes = new TaxYearInfoTypeList(this);
        theActInfoTypes = new AccountInfoTypeList(this);
        theEventInfoTypes = new EventInfoTypeList(this);
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

        /* Create data formatter */
        theFormatter = new JDataFormatter();
        theFormatter.setAccountingWidth(ACCOUNTING_WIDTH);
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    private FinanceData(final FinanceData pSource) {
        super(pSource);

        /* Copy formatter */
        theFormatter = pSource.getDataFormatter();
    }

    @Override
    public FinanceData deriveUpdateSet() {
        /* Build an empty DataSet */
        FinanceData myExtract = new FinanceData(this);

        /* Obtain underlying updates */
        myExtract.deriveUpdateSet(this);

        /* Build the static extract */
        myExtract.theActTypes = theActTypes.deriveList(ListStyle.UPDATE);
        myExtract.theTransTypes = theTransTypes.deriveList(ListStyle.UPDATE);
        myExtract.theTaxTypes = theTaxTypes.deriveList(ListStyle.UPDATE);
        myExtract.theTaxRegimes = theTaxRegimes.deriveList(ListStyle.UPDATE);
        myExtract.theFrequencys = theFrequencys.deriveList(ListStyle.UPDATE);
        myExtract.theTaxInfoTypes = theTaxInfoTypes.deriveList(ListStyle.UPDATE);
        myExtract.theActInfoTypes = theActInfoTypes.deriveList(ListStyle.UPDATE);
        myExtract.theEventInfoTypes = theEventInfoTypes.deriveList(ListStyle.UPDATE);

        /* Build the data extract */
        myExtract.theTaxYears = theTaxYears.deriveList(ListStyle.UPDATE);
        myExtract.theAccounts = theAccounts.deriveList(ListStyle.UPDATE);
        myExtract.theRates = theRates.deriveList(ListStyle.UPDATE);
        myExtract.thePrices = thePrices.deriveList(ListStyle.UPDATE);
        myExtract.thePatterns = thePatterns.deriveList(ListStyle.UPDATE);
        myExtract.theEvents = theEvents.deriveList(ListStyle.UPDATE);
        myExtract.theEventData = theEventData.deriveList(ListStyle.UPDATE);
        myExtract.theEventValues = theEventValues.deriveList(ListStyle.UPDATE);

        /* Declare the lists */
        myExtract.declareLists();

        /* Return the extract */
        return myExtract;
    }

    @Override
    public FinanceData deriveCloneSet() {
        /* Build an empty DataSet */
        FinanceData myExtract = new FinanceData(this);

        /* Obtain underlying updates */
        myExtract.deriveCloneSet(this);

        /* Build the static extract */
        myExtract.theActTypes = theActTypes.cloneList(this);
        myExtract.theTransTypes = theTransTypes.cloneList(this);
        myExtract.theTaxTypes = theTaxTypes.cloneList(this);
        myExtract.theTaxRegimes = theTaxRegimes.cloneList(this);
        myExtract.theFrequencys = theFrequencys.cloneList(this);
        myExtract.theTaxInfoTypes = theTaxInfoTypes.cloneList(this);
        myExtract.theActInfoTypes = theActInfoTypes.cloneList(this);
        myExtract.theEventInfoTypes = theEventInfoTypes.cloneList(this);

        /* Build the data extract */
        myExtract.theTaxYears = theTaxYears.cloneList(this);
        myExtract.theAccounts = theAccounts.cloneList(this);
        myExtract.theRates = theRates.cloneList(this);
        myExtract.thePrices = thePrices.cloneList(this);
        myExtract.thePatterns = thePatterns.cloneList(this);
        myExtract.theEvents = theEvents.cloneList(this);
        myExtract.theEventData = theEventData.cloneList(this);
        myExtract.theEventValues = theEventValues.cloneList(this);

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
     * @throws JDataException on error
     */
    @Override
    public FinanceData getDifferenceSet(final FinanceData pOld) throws JDataException {
        /* Build an empty DataSet */
        FinanceData myDiffers = new FinanceData(this);

        /* Obtain underlying differences */
        myDiffers.deriveDifferences(this, pOld);

        /* Build the static differences */
        myDiffers.theActTypes = theActTypes.deriveDifferences(pOld.getAccountTypes());
        myDiffers.theTransTypes = theTransTypes.deriveDifferences(pOld.getTransTypes());
        myDiffers.theTaxTypes = theTaxTypes.deriveDifferences(pOld.getTaxTypes());
        myDiffers.theTaxRegimes = theTaxRegimes.deriveDifferences(pOld.getTaxRegimes());
        myDiffers.theFrequencys = theFrequencys.deriveDifferences(pOld.getFrequencys());
        myDiffers.theTaxInfoTypes = theTaxInfoTypes.deriveDifferences(pOld.getTaxInfoTypes());
        myDiffers.theActInfoTypes = theActInfoTypes.deriveDifferences(pOld.getActInfoTypes());
        myDiffers.theEventInfoTypes = theEventInfoTypes.deriveDifferences(pOld.getEventInfoTypes());

        /* Build the data differences */
        myDiffers.theTaxYears = theTaxYears.deriveDifferences(pOld.getTaxYears());
        myDiffers.theAccounts = theAccounts.deriveDifferences(pOld.getAccounts());
        myDiffers.theRates = theRates.deriveDifferences(pOld.getRates());
        myDiffers.thePrices = thePrices.deriveDifferences(pOld.getPrices());
        myDiffers.thePatterns = thePatterns.deriveDifferences(pOld.getPatterns());
        myDiffers.theEvents = theEvents.deriveDifferences(pOld.getEvents());
        myDiffers.theEventData = theEventData.deriveDifferences(pOld.getEventData());
        myDiffers.theEventValues = theEventValues.deriveDifferences(pOld.getEventValues());

        /* Declare the lists */
        myDiffers.declareLists();

        /* Return the differences */
        return myDiffers;
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pOld The old data to reBase against
     * @throws JDataException on error
     */
    @Override
    public void reBase(final FinanceData pOld) throws JDataException {
        /* Call super-class */
        super.reBase(pOld);

        /* ReBase the static items */
        theActTypes.reBase(pOld.getAccountTypes());
        theTransTypes.reBase(pOld.getTransTypes());
        theTaxTypes.reBase(pOld.getTaxTypes());
        theTaxRegimes.reBase(pOld.getTaxRegimes());
        theFrequencys.reBase(pOld.getFrequencys());
        theTaxInfoTypes.reBase(pOld.getTaxInfoTypes());
        theActInfoTypes.reBase(pOld.getActInfoTypes());
        theEventInfoTypes.reBase(pOld.getEventInfoTypes());

        /* ReBase the data items */
        theTaxYears.reBase(pOld.getTaxYears());
        theAccounts.reBase(pOld.getAccounts());
        theRates.reBase(pOld.getRates());
        thePrices.reBase(pOld.getPrices());
        thePatterns.reBase(pOld.getPatterns());
        theEvents.reBase(pOld.getEvents());
        theEventData.reBase(pOld.getEventData());
        theEventValues.reBase(pOld.getEventValues());
    }

    /**
     * Declare lists.
     */
    private void declareLists() {
        /* Declare the lists */
        addList(theActTypes);
        addList(theTransTypes);
        addList(theTaxTypes);
        addList(theTaxRegimes);
        addList(theFrequencys);
        addList(theTaxInfoTypes);
        addList(theActInfoTypes);
        addList(theEventInfoTypes);
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
     * Calculate the allowed Date Range.
     */
    public void calculateDateRange() {
        theDateRange = theTaxYears.getRange();
        theEvents.setRange(theDateRange);
    }

    /**
     * Initialise the analysis.
     * @throws JDataException on error
     */
    public void initialiseAnalysis() throws JDataException {
        /* Update INITIAL Load status */
        if (theLoadState == LoadState.INITIAL) {
            theLoadState = LoadState.FINAL;
        }

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
    }

    /**
     * Analyse the data.
     * @throws JDataException on error
     */
    public void houseKeepAnalysis() throws JDataException {
        /* Note active items referenced by rates */
        theRates.markActiveItems();

        /* Note active items referenced by prices */
        thePrices.markActiveItems();

        /* Mark active items referenced by patterns */
        thePatterns.markActiveItems();
    }

    /**
     * Complete the data analysis.
     * @throws JDataException on error
     */
    public void completeAnalysis() throws JDataException {
        /* Note active accounts */
        theAccounts.markActiveItems();

        /* Note that we are now fully loaded */
        theLoadState = LoadState.LOADED;
    }

    /**
     * Enumeration of load states of data.
     */
    public static enum LoadState {
        /**
         * Initial loading, with parental account links and close-ability not yet done.
         */
        INITIAL,

        /**
         * Final loading with parental links and close-ability done.
         */
        FINAL,

        /**
         * Fully loaded.
         */
        LOADED;
    }
}
