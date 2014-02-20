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

import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate.AccountRateList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.EventTag.EventTagList;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.data.Pattern.PatternList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

/**
 * MoneyWise dataSet.
 */
public class MoneyWiseData
        extends DataSet<MoneyWiseData, MoneyWiseDataType> {
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
    private static final Map<JDataField, MoneyWiseDataType> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, MoneyWiseDataType.class);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle List fields */
        MoneyWiseDataType myType = FIELDSET_MAP.get(pField);
        if (myType != null) {
            /* Access the list */
            return getFieldListValue(myType);
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
        return getDataList(MoneyWiseDataType.ACCOUNTTYPE, AccountCategoryTypeList.class);
    }

    /**
     * Obtain PayeeTypes.
     * @return the Payee types
     */
    public PayeeTypeList getPayeeTypes() {
        return getDataList(MoneyWiseDataType.PAYEETYPE, PayeeTypeList.class);
    }

    /**
     * Obtain SecurityTypes.
     * @return the Security types
     */
    public SecurityTypeList getSecurityTypes() {
        return getDataList(MoneyWiseDataType.SECURITYTYPE, SecurityTypeList.class);
    }

    /**
     * Obtain EventCategoryTypes.
     * @return the Event Category types
     */
    public EventCategoryTypeList getEventCategoryTypes() {
        return getDataList(MoneyWiseDataType.EVENTTYPE, EventCategoryTypeList.class);
    }

    /**
     * Obtain TaxBases.
     * @return the Tax bases
     */
    public TaxBasisList getTaxBases() {
        return getDataList(MoneyWiseDataType.TAXBASIS, TaxBasisList.class);
    }

    /**
     * Obtain TaxCategories.
     * @return the Tax categories
     */
    public TaxCategoryList getTaxCategories() {
        return getDataList(MoneyWiseDataType.TAXTYPE, TaxCategoryList.class);
    }

    /**
     * Obtain Account Currencies.
     * @return the Account Currencies
     */
    public AccountCurrencyList getAccountCurrencies() {
        return getDataList(MoneyWiseDataType.CURRENCY, AccountCurrencyList.class);
    }

    /**
     * Obtain TaxRegimes.
     * @return the TaxRegimes
     */
    public TaxRegimeList getTaxRegimes() {
        return getDataList(MoneyWiseDataType.TAXREGIME, TaxRegimeList.class);
    }

    /**
     * Obtain Frequencies.
     * @return the Frequencies
     */
    public FrequencyList getFrequencys() {
        return getDataList(MoneyWiseDataType.FREQUENCY, FrequencyList.class);
    }

    /**
     * Obtain TaxInfoTypes.
     * @return the TaxYear Info types
     */
    public TaxYearInfoTypeList getTaxInfoTypes() {
        return getDataList(MoneyWiseDataType.TAXINFOTYPE, TaxYearInfoTypeList.class);
    }

    /**
     * Obtain AccountInfoTypes.
     * @return the Account Info types
     */
    public AccountInfoTypeList getActInfoTypes() {
        return getDataList(MoneyWiseDataType.ACCOUNTINFOTYPE, AccountInfoTypeList.class);
    }

    /**
     * Obtain EventInfoTypes.
     * @return the Event Info types
     */
    public EventInfoTypeList getEventInfoTypes() {
        return getDataList(MoneyWiseDataType.EVENTINFOTYPE, EventInfoTypeList.class);
    }

    /**
     * Obtain EventClasses.
     * @return the EventClasses
     */
    public EventTagList getEventClasses() {
        return getDataList(MoneyWiseDataType.EVENTTAG, EventTagList.class);
    }

    /**
     * Obtain AccountCategories.
     * @return the Account categories
     */
    public AccountCategoryList getAccountCategories() {
        return getDataList(MoneyWiseDataType.ACCOUNTCATEGORY, AccountCategoryList.class);
    }

    /**
     * Obtain EventCategories.
     * @return the Event categories
     */
    public EventCategoryList getEventCategories() {
        return getDataList(MoneyWiseDataType.EVENTCATEGORY, EventCategoryList.class);
    }

    /**
     * Obtain TaxYears.
     * @return the TaxYears
     */
    public TaxYearList getTaxYears() {
        return getDataList(MoneyWiseDataType.TAXYEAR, TaxYearList.class);
    }

    /**
     * Obtain TaxInfo.
     * @return the Tax Info
     */
    public TaxInfoList getTaxInfo() {
        return getDataList(MoneyWiseDataType.TAXYEARINFO, TaxInfoList.class);
    }

    /**
     * Obtain ExchangeRates.
     * @return the ExchangeRates
     */
    public ExchangeRateList getExchangeRates() {
        return getDataList(MoneyWiseDataType.EXCHANGERATE, ExchangeRateList.class);
    }

    /**
     * Obtain Payees.
     * @return the Payees
     */
    public PayeeList getPayees() {
        return getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
    }

    /**
     * Obtain Securities.
     * @return the Securities
     */
    public SecurityList getSecurities() {
        return getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
    }

    /**
     * Obtain Deposits.
     * @return the Deposits
     */
    public DepositList getDeposits() {
        return getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
    }

    /**
     * Obtain Accounts.
     * @return the Accounts
     */
    public AccountList getAccounts() {
        return getDataList(MoneyWiseDataType.ACCOUNT, AccountList.class);
    }

    /**
     * Obtain Portfolios.
     * @return the Portfolios
     */
    public PortfolioList getPortfolios() {
        return getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
    }

    /**
     * Obtain AccountInfo.
     * @return the Account Info
     */
    public AccountInfoList getAccountInfo() {
        return getDataList(MoneyWiseDataType.ACCOUNTINFO, AccountInfoList.class);
    }

    /**
     * Obtain AccountRates.
     * @return the Account rates
     */
    public AccountRateList getRates() {
        return getDataList(MoneyWiseDataType.ACCOUNTRATE, AccountRateList.class);
    }

    /**
     * Obtain AccountPrices.
     * @return the Account prices
     */
    public SecurityPriceList getPrices() {
        return getDataList(MoneyWiseDataType.SECURITYPRICE, SecurityPriceList.class);
    }

    /**
     * Obtain Patterns.
     * @return the Patterns
     */
    public PatternList getPatterns() {
        return getDataList(MoneyWiseDataType.PATTERN, PatternList.class);
    }

    /**
     * Obtain Events.
     * @return the Events
     */
    public EventList getEvents() {
        return getDataList(MoneyWiseDataType.EVENT, EventList.class);
    }

    /**
     * Obtain EventInfo.
     * @return the Event Info
     */
    public EventInfoList getEventInfo() {
        return getDataList(MoneyWiseDataType.EVENTINFO, EventInfoList.class);
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
        super(MoneyWiseDataType.class, pSecurity, pPreferenceMgr, pFieldMgr.getDataFormatter());

        /* Loop through the list types */
        for (MoneyWiseDataType myType : MoneyWiseDataType.values()) {
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
    private DataList<?, MoneyWiseDataType> newList(final MoneyWiseDataType pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case ACCOUNTTYPE:
                return new AccountCategoryTypeList(this);
            case PAYEETYPE:
                return new PayeeTypeList(this);
            case SECURITYTYPE:
                return new SecurityTypeList(this);
            case EVENTTYPE:
                return new EventCategoryTypeList(this);
            case TAXBASIS:
                return new TaxBasisList(this);
            case TAXTYPE:
                return new TaxCategoryList(this);
            case CURRENCY:
                return new AccountCurrencyList(this);
            case TAXREGIME:
                return new TaxRegimeList(this);
            case FREQUENCY:
                return new FrequencyList(this);
            case TAXINFOTYPE:
                return new TaxYearInfoTypeList(this);
            case ACCOUNTINFOTYPE:
                return new AccountInfoTypeList(this);
            case EVENTINFOTYPE:
                return new EventInfoTypeList(this);
            case EVENTTAG:
                return new EventTagList(this);
            case ACCOUNTCATEGORY:
                return new AccountCategoryList(this);
            case EVENTCATEGORY:
                return new EventCategoryList(this);
            case TAXYEAR:
                return new TaxYearList(this);
            case TAXYEARINFO:
                return new TaxInfoList(this);
            case EXCHANGERATE:
                return new ExchangeRateList(this);
            case PAYEE:
                return new PayeeList(this);
            case SECURITY:
                return new SecurityList(this);
            case DEPOSIT:
                return new DepositList(this);
            case ACCOUNT:
                return new AccountList(this);
            case PORTFOLIO:
                return new PortfolioList(this);
            case ACCOUNTRATE:
                return new AccountRateList(this);
            case SECURITYPRICE:
                return new SecurityPriceList(this);
            case PATTERN:
                return new PatternList(this);
            case ACCOUNTINFO:
                return new AccountInfoList(this);
            case EVENT:
                return new EventList(this);
            case EVENTINFO:
                return new EventInfoList(this);
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
        /* Release the lock */
        setLocked(false);

        /* Loop through the list types */
        Iterator<Entry<MoneyWiseDataType, DataList<?, MoneyWiseDataType>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<MoneyWiseDataType, DataList<?, MoneyWiseDataType>> myEntry = myIterator.next();

            /* Access list and switch on type */
            DataList<?, MoneyWiseDataType> myList = myEntry.getValue();
            switch (myEntry.getKey()) {
            /* Ignore lists that are never referenced */
                case TAXBASIS:
                case TAXTYPE:
                    break;

                /* Reset the flags on low-lying data */
                case ACCOUNTTYPE:
                case PAYEETYPE:
                case SECURITYTYPE:
                case EVENTTYPE:
                case CURRENCY:
                case TAXREGIME:
                case FREQUENCY:
                case TAXINFOTYPE:
                case ACCOUNTINFOTYPE:
                case EVENTINFOTYPE:
                case EVENTTAG:
                    myList.clearActive();
                    break;

                /* Reset flags and touch underlying on intermediate data */
                case ACCOUNTCATEGORY:
                case EVENTCATEGORY:
                case TAXYEAR:
                case PAYEE:
                case SECURITY:
                case DEPOSIT:
                case ACCOUNT:
                case PORTFOLIO:
                    myList.clearActive();
                    myList.touchUnderlyingItems();
                    break;

                /* Touch underlying data for high level data */
                case EXCHANGERATE:
                case PATTERN:
                    myList.touchUnderlyingItems();
                    break;

                /* Ignore lists that will be handled during analysis */
                case EVENT:
                case ACCOUNTRATE:
                case SECURITYPRICE:
                    break;

                /* Ignore info lists that will be handled by their owner */
                case EVENTINFO:
                case ACCOUNTINFO:
                case TAXYEARINFO:
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

        /* Reinstate the lock */
        setLocked(true);
    }
}
