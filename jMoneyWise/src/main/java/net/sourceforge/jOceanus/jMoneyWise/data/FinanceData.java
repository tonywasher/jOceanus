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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataList.ListStyle;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jGordianKnot.SecureManager;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate.AccountRateList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory.EventCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern.PatternList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency.FrequencyList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;

/**
 * FinanceData dataSet.
 * @author Tony Washer
 */
public class FinanceData
        extends DataSet<FinanceData> {
    /**
     * Money accounting format width.
     */
    private static final int ACCOUNTING_WIDTH = 10;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(FinanceData.class.getSimpleName(), DataSet.FIELD_DEFS);

    /**
     * AccountTypes Field Id.
     */
    public static final JDataField FIELD_ACTCATTYPES = FIELD_DEFS.declareLocalField("AccountCategoryTypes");

    /**
     * TransactionTypes Field Id.
     */
    public static final JDataField FIELD_EVTCATTYPES = FIELD_DEFS.declareLocalField("EventCategoryTypes");

    /**
     * TaxTypes Field Id.
     */
    public static final JDataField FIELD_TAXCATEGORIES = FIELD_DEFS.declareLocalField("TaxCategories");

    /**
     * Account Currencies Field Id.
     */
    public static final JDataField FIELD_CURRENCIES = FIELD_DEFS.declareLocalField("Currencies");

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
     * AccountCategories Field Id.
     */
    public static final JDataField FIELD_ACTCATEGORIES = FIELD_DEFS.declareLocalField("AccountCategories");

    /**
     * EventCategories Field Id.
     */
    public static final JDataField FIELD_EVTCATEGORIES = FIELD_DEFS.declareLocalField("EventCategories");

    /**
     * TaxYears Field Id.
     */
    public static final JDataField FIELD_TAXYEARS = FIELD_DEFS.declareLocalField("TaxYears");

    /**
     * TaxInfo Field Id.
     */
    public static final JDataField FIELD_TAXINFO = FIELD_DEFS.declareLocalField("TaxInfo");

    /**
     * Accounts Field Id.
     */
    public static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField("Accounts");

    /**
     * AccountInfo Field Id.
     */
    public static final JDataField FIELD_ACCOUNTINFO = FIELD_DEFS.declareLocalField("AccountInfo");

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
     * EventInfo Field Id.
     */
    public static final JDataField FIELD_EVENTINFO = FIELD_DEFS.declareLocalField("EventInfo");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACTCATTYPES.equals(pField)) {
            return (theActCatTypes.size() > 0)
                    ? theActCatTypes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTCATTYPES.equals(pField)) {
            return (theEvtCatTypes.size() > 0)
                    ? theEvtCatTypes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXCATEGORIES.equals(pField)) {
            return (theTaxCategories.size() > 0)
                    ? theTaxCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_CURRENCIES.equals(pField)) {
            return (theCurrencies.size() > 0)
                    ? theCurrencies
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXREGS.equals(pField)) {
            return (theTaxRegimes.size() > 0)
                    ? theTaxRegimes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_FREQS.equals(pField)) {
            return (theFrequencys.size() > 0)
                    ? theFrequencys
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXINFOTYPES.equals(pField)) {
            return (theTaxInfoTypes.size() > 0)
                    ? theTaxInfoTypes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACTINFOTYPES.equals(pField)) {
            return (theActInfoTypes.size() > 0)
                    ? theActInfoTypes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTINFOTYPES.equals(pField)) {
            return (theEventInfoTypes.size() > 0)
                    ? theEventInfoTypes
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACTCATEGORIES.equals(pField)) {
            return (theActCategories.size() > 0)
                    ? theActCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTCATEGORIES.equals(pField)) {
            return (theEvtCategories.size() > 0)
                    ? theEvtCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXYEARS.equals(pField)) {
            return (theTaxYears.size() > 0)
                    ? theTaxYears
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXINFO.equals(pField)) {
            return (theTaxInfo.size() > 0)
                    ? theTaxInfo
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACCOUNTS.equals(pField)) {
            return (theAccounts.size() > 0)
                    ? theAccounts
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACCOUNTINFO.equals(pField)) {
            return (theAccountInfo.size() > 0)
                    ? theAccountInfo
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_RATES.equals(pField)) {
            return (theRates.size() > 0)
                    ? theRates
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PRICES.equals(pField)) {
            return (thePrices.size() > 0)
                    ? thePrices
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PATTERNS.equals(pField)) {
            return (thePatterns.size() > 0)
                    ? thePatterns
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVENTS.equals(pField)) {
            return (theEvents.size() > 0)
                    ? theEvents
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVENTINFO.equals(pField)) {
            return (theEventInfo.size() > 0)
                    ? theEventInfo
                    : JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FinanceData.class.getSimpleName();
    }

    /**
     * AccountCategoryTypes.
     */
    private AccountCategoryTypeList theActCatTypes = null;

    /**
     * EventCategoryTypes.
     */
    private EventCategoryTypeList theEvtCatTypes = null;

    /**
     * TaxCategories.
     */
    private TaxCategoryList theTaxCategories = null;

    /**
     * AccountCurrencies.
     */
    private AccountCurrencyList theCurrencies = null;

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
     * AccountCategories.
     */
    private AccountCategoryList theActCategories = null;

    /**
     * EventCategories.
     */
    private EventCategoryList theEvtCategories = null;

    /**
     * TaxYearInfo.
     */
    private TaxInfoList theTaxInfo = null;

    /**
     * AccountInfo.
     */
    private AccountInfoList theAccountInfo = null;

    /**
     * EventInfo.
     */
    private EventInfoList theEventInfo = null;

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
     * DataSet range.
     */
    private JDateDayRange theDateRange = null;

    /**
     * General formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Obtain AccountCategoryTypes.
     * @return the Account category types
     */
    public AccountCategoryTypeList getAccountCategoryTypes() {
        return theActCatTypes;
    }

    /**
     * Obtain EventCategoryTypes.
     * @return the Event Category types
     */
    public EventCategoryTypeList getEventCategoryTypes() {
        return theEvtCatTypes;
    }

    /**
     * Obtain TaxCategories.
     * @return the Tax categories
     */
    public TaxCategoryList getTaxCategories() {
        return theTaxCategories;
    }

    /**
     * Obtain Account Currencies.
     * @return the Account Currencies
     */
    public AccountCurrencyList getAccountCurrencies() {
        return theCurrencies;
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
     * Obtain AccountCategories.
     * @return the Account categories
     */
    public AccountCategoryList getAccountCategories() {
        return theActCategories;
    }

    /**
     * Obtain EventCategories.
     * @return the Event categories
     */
    public EventCategoryList getEventCategories() {
        return theEvtCategories;
    }

    /**
     * Obtain TaxYears.
     * @return the TaxYears
     */
    public TaxYearList getTaxYears() {
        return theTaxYears;
    }

    /**
     * Obtain TaxInfo.
     * @return the Tax Info
     */
    public TaxInfoList getTaxInfo() {
        return theTaxInfo;
    }

    /**
     * Obtain Accounts.
     * @return the Accounts
     */
    public AccountList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain AccountInfo.
     * @return the Account Info
     */
    public AccountInfoList getAccountInfo() {
        return theAccountInfo;
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
     * Obtain EventInfo.
     * @return the Event Info
     */
    public EventInfoList getEventInfo() {
        return theEventInfo;
    }

    /**
     * Obtain Date range.
     * @return the Date Range
     */
    public JDateDayRange getDateRange() {
        return theDateRange;
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
     * @param pFieldMgr the field manager
     */
    public FinanceData(final SecureManager pSecurity,
                       final PreferenceManager pPreferenceMgr,
                       final JFieldManager pFieldMgr) {
        /* Call Super-constructor */
        super(pSecurity, pPreferenceMgr);

        /* Create the empty lists */
        theActCatTypes = new AccountCategoryTypeList(this);
        theEvtCatTypes = new EventCategoryTypeList(this);
        theTaxCategories = new TaxCategoryList(this);
        theCurrencies = new AccountCurrencyList(this);
        theTaxRegimes = new TaxRegimeList(this);
        theFrequencys = new FrequencyList(this);
        theTaxInfoTypes = new TaxYearInfoTypeList(this);
        theActInfoTypes = new AccountInfoTypeList(this);
        theEventInfoTypes = new EventInfoTypeList(this);
        theActCategories = new AccountCategoryList(this);
        theEvtCategories = new EventCategoryList(this);
        theTaxYears = new TaxYearList(this);
        theTaxInfo = new TaxInfoList(this);
        theAccounts = new AccountList(this);
        theRates = new AccountRateList(this);
        thePrices = new AccountPriceList(this);
        thePatterns = new PatternList(this);
        theAccountInfo = new AccountInfoList(this);
        theEvents = new EventList(this);
        theEventInfo = new EventInfoList(this);

        /* Declare the lists */
        declareLists();

        /* Create data formatter */
        theFormatter = pFieldMgr.getDataFormatter();
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
        myExtract.theActCatTypes = theActCatTypes.deriveList(ListStyle.UPDATE);
        myExtract.theEvtCatTypes = theEvtCatTypes.deriveList(ListStyle.UPDATE);
        myExtract.theTaxCategories = theTaxCategories.deriveList(ListStyle.UPDATE);
        myExtract.theCurrencies = theCurrencies.deriveList(ListStyle.UPDATE);
        myExtract.theTaxRegimes = theTaxRegimes.deriveList(ListStyle.UPDATE);
        myExtract.theFrequencys = theFrequencys.deriveList(ListStyle.UPDATE);
        myExtract.theTaxInfoTypes = theTaxInfoTypes.deriveList(ListStyle.UPDATE);
        myExtract.theActInfoTypes = theActInfoTypes.deriveList(ListStyle.UPDATE);
        myExtract.theEventInfoTypes = theEventInfoTypes.deriveList(ListStyle.UPDATE);

        /* Build the data extract */
        myExtract.theActCategories = theActCategories.deriveList(ListStyle.UPDATE);
        myExtract.theEvtCategories = theEvtCategories.deriveList(ListStyle.UPDATE);
        myExtract.theTaxYears = theTaxYears.deriveList(ListStyle.UPDATE);
        myExtract.theTaxInfo = theTaxInfo.deriveList(ListStyle.UPDATE);
        myExtract.theAccounts = theAccounts.deriveList(ListStyle.UPDATE);
        myExtract.theRates = theRates.deriveList(ListStyle.UPDATE);
        myExtract.thePrices = thePrices.deriveList(ListStyle.UPDATE);
        myExtract.thePatterns = thePatterns.deriveList(ListStyle.UPDATE);
        myExtract.theAccountInfo = theAccountInfo.deriveList(ListStyle.UPDATE);
        myExtract.theEvents = theEvents.deriveList(ListStyle.UPDATE);
        myExtract.theEventInfo = theEventInfo.deriveList(ListStyle.UPDATE);

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
        myExtract.theActCatTypes = theActCatTypes.cloneList(this);
        myExtract.theEvtCatTypes = theEvtCatTypes.cloneList(this);
        myExtract.theTaxCategories = theTaxCategories.cloneList(this);
        myExtract.theCurrencies = theCurrencies.cloneList(this);
        myExtract.theTaxRegimes = theTaxRegimes.cloneList(this);
        myExtract.theFrequencys = theFrequencys.cloneList(this);
        myExtract.theTaxInfoTypes = theTaxInfoTypes.cloneList(this);
        myExtract.theActInfoTypes = theActInfoTypes.cloneList(this);
        myExtract.theEventInfoTypes = theEventInfoTypes.cloneList(this);

        /* Build the data extract */
        myExtract.theActCategories = theActCategories.cloneList(this);
        myExtract.theEvtCategories = theEvtCategories.cloneList(this);
        myExtract.theTaxYears = theTaxYears.cloneList(this);
        myExtract.theTaxInfo = theTaxInfo.cloneList(this);
        myExtract.theAccounts = theAccounts.cloneList(this);
        myExtract.theRates = theRates.cloneList(this);
        myExtract.thePrices = thePrices.cloneList(this);
        myExtract.thePatterns = thePatterns.cloneList(this);
        myExtract.theAccountInfo = theAccountInfo.cloneList(this);
        myExtract.theEvents = theEvents.cloneList(this);
        myExtract.theEventInfo = theEventInfo.cloneList(this);

        /* Declare the lists */
        myExtract.declareLists();

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in this list, but not in the old list will be viewed as inserted. Items that are in the old list but not in this list will be viewed as deleted. Items
     * that are in both lists but differ will be viewed as changed
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
        myDiffers.theActCatTypes = theActCatTypes.deriveDifferences(pOld.getAccountCategoryTypes());
        myDiffers.theEvtCatTypes = theEvtCatTypes.deriveDifferences(pOld.getEventCategoryTypes());
        myDiffers.theTaxCategories = theTaxCategories.deriveDifferences(pOld.getTaxCategories());
        myDiffers.theCurrencies = theCurrencies.deriveDifferences(pOld.getAccountCurrencies());
        myDiffers.theTaxRegimes = theTaxRegimes.deriveDifferences(pOld.getTaxRegimes());
        myDiffers.theFrequencys = theFrequencys.deriveDifferences(pOld.getFrequencys());
        myDiffers.theTaxInfoTypes = theTaxInfoTypes.deriveDifferences(pOld.getTaxInfoTypes());
        myDiffers.theActInfoTypes = theActInfoTypes.deriveDifferences(pOld.getActInfoTypes());
        myDiffers.theEventInfoTypes = theEventInfoTypes.deriveDifferences(pOld.getEventInfoTypes());

        /* Build the data differences */
        myDiffers.theActCategories = theActCategories.deriveDifferences(pOld.getAccountCategories());
        myDiffers.theEvtCategories = theEvtCategories.deriveDifferences(pOld.getEventCategories());
        myDiffers.theTaxYears = theTaxYears.deriveDifferences(pOld.getTaxYears());
        myDiffers.theTaxInfo = theTaxInfo.deriveDifferences(pOld.getTaxInfo());
        myDiffers.theAccounts = theAccounts.deriveDifferences(pOld.getAccounts());
        myDiffers.theRates = theRates.deriveDifferences(pOld.getRates());
        myDiffers.thePrices = thePrices.deriveDifferences(pOld.getPrices());
        myDiffers.thePatterns = thePatterns.deriveDifferences(pOld.getPatterns());
        myDiffers.theAccountInfo = theAccountInfo.deriveDifferences(pOld.getAccountInfo());
        myDiffers.theEvents = theEvents.deriveDifferences(pOld.getEvents());
        myDiffers.theEventInfo = theEventInfo.deriveDifferences(pOld.getEventInfo());

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
        theActCatTypes.reBase(pOld.getAccountCategoryTypes());
        theEvtCatTypes.reBase(pOld.getEventCategoryTypes());
        theTaxCategories.reBase(pOld.getTaxCategories());
        theCurrencies.reBase(pOld.getAccountCurrencies());
        theTaxRegimes.reBase(pOld.getTaxRegimes());
        theFrequencys.reBase(pOld.getFrequencys());
        theTaxInfoTypes.reBase(pOld.getTaxInfoTypes());
        theActInfoTypes.reBase(pOld.getActInfoTypes());
        theEventInfoTypes.reBase(pOld.getEventInfoTypes());

        /* ReBase the data items */
        theActCategories.reBase(pOld.getAccountCategories());
        theEvtCategories.reBase(pOld.getEventCategories());
        theTaxYears.reBase(pOld.getTaxYears());
        theTaxInfo.reBase(pOld.getTaxInfo());
        theAccounts.reBase(pOld.getAccounts());
        theRates.reBase(pOld.getRates());
        thePrices.reBase(pOld.getPrices());
        thePatterns.reBase(pOld.getPatterns());
        theAccountInfo.reBase(pOld.getAccountInfo());
        theEvents.reBase(pOld.getEvents());
        theEventInfo.reBase(pOld.getEventInfo());
    }

    /**
     * Declare lists.
     */
    private void declareLists() {
        /* Declare the lists */
        addList(theActCatTypes);
        addList(theEvtCatTypes);
        addList(theTaxCategories);
        addList(theCurrencies);
        addList(theTaxRegimes);
        addList(theFrequencys);
        addList(theTaxInfoTypes);
        addList(theActInfoTypes);
        addList(theEventInfoTypes);
        addList(theActCategories);
        addList(theEvtCategories);
        addList(theTaxYears);
        addList(theTaxInfo);
        addList(theAccounts);
        addList(theRates);
        addList(thePrices);
        addList(thePatterns);
        addList(theAccountInfo);
        addList(theEvents);
        addList(theEventInfo);
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
        /* Reset the flags on static data (ignoring TaxTypes) */
        theActCatTypes.clearActive();
        theEvtCatTypes.clearActive();
        theCurrencies.clearActive();
        theTaxRegimes.clearActive();
        theFrequencys.clearActive();
        theTaxInfoTypes.clearActive();
        theActInfoTypes.clearActive();
        theEventInfoTypes.clearActive();

        /* Reset the flags on the categories */
        theActCategories.clearActive();
        theEvtCategories.clearActive();

        /* Reset the flags on the accounts and tax years */
        theAccounts.clearActive();
        theTaxYears.clearActive();

        /* Note active items referenced by tax years */
        theTaxYears.markActiveItems();
    }

    /**
     * Complete the data analysis.
     * @throws JDataException on error
     */
    public void completeAnalysis() throws JDataException {
        /* Note active accounts */
        theAccounts.markActiveItems();
    }
}
