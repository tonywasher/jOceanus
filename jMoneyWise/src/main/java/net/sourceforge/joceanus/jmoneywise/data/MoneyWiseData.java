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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate.AccountRateList;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.EventClass.EventClassList;
import net.sourceforge.joceanus.jmoneywise.data.EventClassLink.EventClassLinkList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.data.Pattern.PatternList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * MoneyWise dataSet.
 */
public class MoneyWiseData
        extends DataSet<MoneyWiseData, MoneyWiseList> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MoneyWiseData.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, MoneyWiseList> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, MoneyWiseList.class);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle List fields */
        MoneyWiseList myType = FIELDSET_MAP.get(pField);
        if (myType != null) {
            /* Access the list */
            DataList<?> myList = getDataList(myType, DataList.class);
            return (myList.isEmpty())
                    ? JDataFieldValue.SKIP
                    : myList;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return MoneyWiseData.class.getSimpleName();
    }

    /**
     * DataSet range.
     */
    private JDateDayRange theDateRange = null;

    /**
     * Default Currency.
     */
    private AccountCurrency theDefaultCurrency = null;

    /**
     * Obtain AccountCategoryTypes.
     * @return the Account category types
     */
    public AccountCategoryTypeList getAccountCategoryTypes() {
        return getDataList(MoneyWiseList.ACCOUNTTYPES, AccountCategoryTypeList.class);
    }

    /**
     * Obtain EventCategoryTypes.
     * @return the Event Category types
     */
    public EventCategoryTypeList getEventCategoryTypes() {
        return getDataList(MoneyWiseList.EVENTTYPES, EventCategoryTypeList.class);
    }

    /**
     * Obtain TaxBases.
     * @return the Tax bases
     */
    public TaxBasisList getTaxBases() {
        return getDataList(MoneyWiseList.TAXBASES, TaxBasisList.class);
    }

    /**
     * Obtain TaxCategories.
     * @return the Tax categories
     */
    public TaxCategoryList getTaxCategories() {
        return getDataList(MoneyWiseList.TAXTYPES, TaxCategoryList.class);
    }

    /**
     * Obtain Account Currencies.
     * @return the Account Currencies
     */
    public AccountCurrencyList getAccountCurrencies() {
        return getDataList(MoneyWiseList.CURRENCIES, AccountCurrencyList.class);
    }

    /**
     * Obtain TaxRegimes.
     * @return the TaxRegimes
     */
    public TaxRegimeList getTaxRegimes() {
        return getDataList(MoneyWiseList.TAXREGIMES, TaxRegimeList.class);
    }

    /**
     * Obtain Frequencies.
     * @return the Frequencies
     */
    public FrequencyList getFrequencys() {
        return getDataList(MoneyWiseList.FREQUENCIES, FrequencyList.class);
    }

    /**
     * Obtain TaxInfoTypes.
     * @return the TaxYear Info types
     */
    public TaxYearInfoTypeList getTaxInfoTypes() {
        return getDataList(MoneyWiseList.TAXINFOTYPES, TaxYearInfoTypeList.class);
    }

    /**
     * Obtain AccountInfoTypes.
     * @return the Account Info types
     */
    public AccountInfoTypeList getActInfoTypes() {
        return getDataList(MoneyWiseList.ACCOUNTINFOTYPES, AccountInfoTypeList.class);
    }

    /**
     * Obtain EventInfoTypes.
     * @return the Event Info types
     */
    public EventInfoTypeList getEventInfoTypes() {
        return getDataList(MoneyWiseList.EVENTINFOTYPES, EventInfoTypeList.class);
    }

    /**
     * Obtain EventClasses.
     * @return the EventClasses
     */
    public EventClassList getEventClasses() {
        return getDataList(MoneyWiseList.EVENTCLASSES, EventClassList.class);
    }

    /**
     * Obtain AccountCategories.
     * @return the Account categories
     */
    public AccountCategoryList getAccountCategories() {
        return getDataList(MoneyWiseList.ACCOUNTCATEGORIES, AccountCategoryList.class);
    }

    /**
     * Obtain EventCategories.
     * @return the Event categories
     */
    public EventCategoryList getEventCategories() {
        return getDataList(MoneyWiseList.EVENTCATEGORIES, EventCategoryList.class);
    }

    /**
     * Obtain TaxYears.
     * @return the TaxYears
     */
    public TaxYearList getTaxYears() {
        return getDataList(MoneyWiseList.TAXYEARS, TaxYearList.class);
    }

    /**
     * Obtain TaxInfo.
     * @return the Tax Info
     */
    public TaxInfoList getTaxInfo() {
        return getDataList(MoneyWiseList.TAXYEARINFO, TaxInfoList.class);
    }

    /**
     * Obtain ExchangeRates.
     * @return the ExchangeRates
     */
    public ExchangeRateList getExchangeRates() {
        return getDataList(MoneyWiseList.EXCHANGERATES, ExchangeRateList.class);
    }

    /**
     * Obtain Accounts.
     * @return the Accounts
     */
    public AccountList getAccounts() {
        return getDataList(MoneyWiseList.ACCOUNTS, AccountList.class);
    }

    /**
     * Obtain AccountInfo.
     * @return the Account Info
     */
    public AccountInfoList getAccountInfo() {
        return getDataList(MoneyWiseList.ACCOUNTINFO, AccountInfoList.class);
    }

    /**
     * Obtain AccountRates.
     * @return the Account rates
     */
    public AccountRateList getRates() {
        return getDataList(MoneyWiseList.ACCOUNTRATES, AccountRateList.class);
    }

    /**
     * Obtain AccountPrices.
     * @return the Account prices
     */
    public AccountPriceList getPrices() {
        return getDataList(MoneyWiseList.ACCOUNTPRICES, AccountPriceList.class);
    }

    /**
     * Obtain Patterns.
     * @return the Patterns
     */
    public PatternList getPatterns() {
        return getDataList(MoneyWiseList.PATTERNS, PatternList.class);
    }

    /**
     * Obtain Events.
     * @return the Events
     */
    public EventList getEvents() {
        return getDataList(MoneyWiseList.EVENTS, EventList.class);
    }

    /**
     * Obtain EventInfo.
     * @return the Event Info
     */
    public EventInfoList getEventInfo() {
        return getDataList(MoneyWiseList.EVENTINFO, EventInfoList.class);
    }

    /**
     * Obtain EventClass Links.
     * @return the EventClass Links
     */
    public EventClassLinkList getEventClassLinks() {
        return getDataList(MoneyWiseList.EVENTCLASSLINKS, EventClassLinkList.class);
    }

    /**
     * Obtain Date range.
     * @return the Date Range
     */
    public JDateDayRange getDateRange() {
        return theDateRange;
    }

    /**
     * Obtain default currency.
     * @return the default currency
     */
    public AccountCurrency getDefaultCurrency() {
        return theDefaultCurrency;
    }

    /**
     * Standard constructor.
     * @param pSecurity the secure manager
     * @param pPreferenceMgr the preference manager
     * @param pFieldMgr the field manager
     */
    public MoneyWiseData(final SecureManager pSecurity,
                         final PreferenceManager pPreferenceMgr,
                         final JFieldManager pFieldMgr) {
        /* Call Super-constructor */
        super(MoneyWiseList.class, pSecurity, pPreferenceMgr, pFieldMgr.getDataFormatter());

        /* Loop through the list types */
        for (MoneyWiseList myType : MoneyWiseList.values()) {
            /* Create the empty list */
            addList(myType, newList(myType));
        }
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    private MoneyWiseData(final MoneyWiseData pSource) {
        super(pSource);
    }

    /**
     * Create new list of required type.
     * @param pListType the list type
     * @return the new list
     */
    private DataList<?> newList(final MoneyWiseList pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case ACCOUNTTYPES:
                return new AccountCategoryTypeList(this);
            case EVENTTYPES:
                return new EventCategoryTypeList(this);
            case TAXBASES:
                return new TaxBasisList(this);
            case TAXTYPES:
                return new TaxCategoryList(this);
            case CURRENCIES:
                return new AccountCurrencyList(this);
            case TAXREGIMES:
                return new TaxRegimeList(this);
            case FREQUENCIES:
                return new FrequencyList(this);
            case TAXINFOTYPES:
                return new TaxYearInfoTypeList(this);
            case ACCOUNTINFOTYPES:
                return new AccountInfoTypeList(this);
            case EVENTINFOTYPES:
                return new EventInfoTypeList(this);
            case EVENTCLASSES:
                return new EventClassList(this);
            case ACCOUNTCATEGORIES:
                return new AccountCategoryList(this);
            case EVENTCATEGORIES:
                return new EventCategoryList(this);
            case TAXYEARS:
                return new TaxYearList(this);
            case TAXYEARINFO:
                return new TaxInfoList(this);
            case EXCHANGERATES:
                return new ExchangeRateList(this);
            case ACCOUNTS:
                return new AccountList(this);
            case ACCOUNTRATES:
                return new AccountRateList(this);
            case ACCOUNTPRICES:
                return new AccountPriceList(this);
            case PATTERNS:
                return new PatternList(this);
            case ACCOUNTINFO:
                return new AccountInfoList(this);
            case EVENTS:
                return new EventList(this);
            case EVENTINFO:
                return new EventInfoList(this);
            case EVENTCLASSLINKS:
                return new EventClassLinkList(this);
            default:
                throw new IllegalArgumentException(pListType.toString());
        }
    }

    @Override
    public MoneyWiseData deriveUpdateSet() throws JOceanusException {
        /* Build an empty DataSet */
        MoneyWiseData myExtract = new MoneyWiseData(this);

        /* Obtain underlying updates */
        myExtract.deriveUpdateSet(this);

        /* Return the extract */
        return myExtract;
    }

    @Override
    public MoneyWiseData deriveCloneSet() throws JOceanusException {
        /* Build an empty DataSet */
        MoneyWiseData myExtract = new MoneyWiseData(this);

        /* Obtain underlying updates */
        myExtract.deriveCloneSet(this);

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in this list, but not in the old list will be viewed as inserted. Items that are in the old list but not in this list will be viewed as deleted. Items
     * that are in both lists but differ will be viewed as changed
     * @param pOld The DataSet to compare to
     * @return the difference extract
     * @throws JOceanusException on error
     */
    @Override
    public MoneyWiseData getDifferenceSet(final MoneyWiseData pOld) throws JOceanusException {
        /* Build an empty DataSet */
        MoneyWiseData myDiffers = new MoneyWiseData(this);

        /* Obtain underlying differences */
        myDiffers.deriveDifferences(this, pOld);

        /* Return the differences */
        return myDiffers;
    }

    /**
     * Calculate the allowed Date Range.
     */
    public void calculateDateRange() {
        theDefaultCurrency = getAccountCurrencies().findDefault();
        theDateRange = getTaxYears().getRange();
        getEvents().setRange(theDateRange);
    }

    /**
     * Initialise the analysis.
     * @throws JOceanusException on error
     */
    public void initialiseAnalysis() throws JOceanusException {
        /* Loop through the list types */
        Iterator<Entry<MoneyWiseList, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<MoneyWiseList, DataList<?>> myEntry = myIterator.next();

            /* Access list and switch on type */
            DataList<?> myList = myEntry.getValue();
            switch (myEntry.getKey()) {
            /* Reset the flags on low-lying data */
                case ACCOUNTTYPES:
                case EVENTTYPES:
                case CURRENCIES:
                case TAXREGIMES:
                case FREQUENCIES:
                case TAXINFOTYPES:
                case ACCOUNTINFOTYPES:
                case EVENTINFOTYPES:
                case EVENTCLASSES:
                    myList.clearActive();
                    break;

                /* Reset flags and touch underlying on intermediate data */
                case ACCOUNTCATEGORIES:
                case EVENTCATEGORIES:
                case TAXYEARS:
                case ACCOUNTS:
                    myList.clearActive();
                    myList.touchUnderlyingItems();
                    break;

                /* Touch underlying data for high level data */
                case EXCHANGERATES:
                case PATTERNS:
                    myList.touchUnderlyingItems();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Complete the data analysis.
     * @throws JOceanusException on error
     */
    public void completeAnalysis() throws JOceanusException {
        /* Note active accounts */
        getAccounts().validateOnLoad();
    }
}
