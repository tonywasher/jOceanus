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

import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Schedule.ScheduleList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionInfo.StockOptionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest.StockOptionVestList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

/**
 * MoneyWise dataSet.
 */
public class MoneyWiseData
        extends DataSet<MoneyWiseData, MoneyWiseDataType> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseDataResource.MONEYWISEDATA_NAME.getValue(), DataSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, MoneyWiseDataType> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, MoneyWiseDataType.class);

    /**
     * DateRange Type Field Id.
     */
    public static final JDataField FIELD_DATERANGE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_RANGE.getValue());

    /**
     * DefaultCurrency Field Id.
     */
    public static final JDataField FIELD_DEFCURR = FIELD_DEFS.declareLocalField(StaticDataResource.CURRENCY_DEFAULT.getValue());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_DATERANGE.equals(pField)) {
            return theDateRange;
        }
        if (FIELD_DEFCURR.equals(pField)) {
            return theDefaultCurrency;
        }

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
     * Obtain DepositCategoryTypes.
     * @return the Deposit category types
     */
    public DepositCategoryTypeList getDepositCategoryTypes() {
        return getDataList(MoneyWiseDataType.DEPOSITTYPE, DepositCategoryTypeList.class);
    }

    /**
     * Obtain CashCategoryTypes.
     * @return the Cash category types
     */
    public CashCategoryTypeList getCashCategoryTypes() {
        return getDataList(MoneyWiseDataType.CASHTYPE, CashCategoryTypeList.class);
    }

    /**
     * Obtain LoanCategoryTypes.
     * @return the Loan category types
     */
    public LoanCategoryTypeList getLoanCategoryTypes() {
        return getDataList(MoneyWiseDataType.LOANTYPE, LoanCategoryTypeList.class);
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
     * Obtain TransactionCategoryTypes.
     * @return the Transaction Category types
     */
    public TransactionCategoryTypeList getTransCategoryTypes() {
        return getDataList(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);
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
     * Obtain TransactionInfoTypes.
     * @return the Transaction Info types
     */
    public TransactionInfoTypeList getTransInfoTypes() {
        return getDataList(MoneyWiseDataType.TRANSINFOTYPE, TransactionInfoTypeList.class);
    }

    /**
     * Obtain Transaction Tags.
     * @return the Transaction Tags
     */
    public TransactionTagList getTransactionTags() {
        return getDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
    }

    /**
     * Obtain DepositCategories.
     * @return the Deposit categories
     */
    public DepositCategoryList getDepositCategories() {
        return getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);
    }

    /**
     * Obtain CashCategories.
     * @return the Cash categories
     */
    public CashCategoryList getCashCategories() {
        return getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);
    }

    /**
     * Obtain LoanCategories.
     * @return the Loan categories
     */
    public LoanCategoryList getLoanCategories() {
        return getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);
    }

    /**
     * Obtain TransactionCategories.
     * @return the Transaction categories
     */
    public TransactionCategoryList getTransCategories() {
        return getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);
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
     * Obtain PayeeInfo.
     * @return the Payee Info
     */
    public PayeeInfoList getPayeeInfo() {
        return getDataList(MoneyWiseDataType.PAYEEINFO, PayeeInfoList.class);
    }

    /**
     * Obtain Securities.
     * @return the Securities
     */
    public SecurityList getSecurities() {
        return getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
    }

    /**
     * Obtain SecurityPrices.
     * @return the Security prices
     */
    public SecurityPriceList getSecurityPrices() {
        return getDataList(MoneyWiseDataType.SECURITYPRICE, SecurityPriceList.class);
    }

    /**
     * Obtain SecurityInfo.
     * @return the Security Info
     */
    public SecurityInfoList getSecurityInfo() {
        return getDataList(MoneyWiseDataType.SECURITYINFO, SecurityInfoList.class);
    }

    /**
     * Obtain Deposits.
     * @return the Deposits
     */
    public DepositList getDeposits() {
        return getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
    }

    /**
     * Obtain DepositRates.
     * @return the Deposit rates
     */
    public DepositRateList getDepositRates() {
        return getDataList(MoneyWiseDataType.DEPOSITRATE, DepositRateList.class);
    }

    /**
     * Obtain DepositInfo.
     * @return the Deposit Info
     */
    public DepositInfoList getDepositInfo() {
        return getDataList(MoneyWiseDataType.DEPOSITINFO, DepositInfoList.class);
    }

    /**
     * Obtain Cash.
     * @return the Cash
     */
    public CashList getCash() {
        return getDataList(MoneyWiseDataType.CASH, CashList.class);
    }

    /**
     * Obtain CashInfo.
     * @return the Cash Info
     */
    public CashInfoList getCashInfo() {
        return getDataList(MoneyWiseDataType.CASHINFO, CashInfoList.class);
    }

    /**
     * Obtain Loans.
     * @return the Loans
     */
    public LoanList getLoans() {
        return getDataList(MoneyWiseDataType.LOAN, LoanList.class);
    }

    /**
     * Obtain LoanInfo.
     * @return the Loan Info
     */
    public LoanInfoList getLoanInfo() {
        return getDataList(MoneyWiseDataType.LOANINFO, LoanInfoList.class);
    }

    /**
     * Obtain Portfolios.
     * @return the Portfolios
     */
    public PortfolioList getPortfolios() {
        return getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
    }

    /**
     * Obtain PortfolioInfo.
     * @return the Portfolio Info
     */
    public PortfolioInfoList getPortfolioInfo() {
        return getDataList(MoneyWiseDataType.PORTFOLIOINFO, PortfolioInfoList.class);
    }

    /**
     * Obtain StockOptions.
     * @return the Options
     */
    public StockOptionList getStockOptions() {
        return getDataList(MoneyWiseDataType.STOCKOPTION, StockOptionList.class);
    }

    /**
     * Obtain StockOptionVests.
     * @return the OptionVests
     */
    public StockOptionVestList getStockOptionVests() {
        return getDataList(MoneyWiseDataType.STOCKOPTIONVEST, StockOptionVestList.class);
    }

    /**
     * Obtain StockOptionInfo.
     * @return the StockOption Info
     */
    public StockOptionInfoList getStockOptionInfo() {
        return getDataList(MoneyWiseDataType.STOCKOPTIONINFO, StockOptionInfoList.class);
    }

    /**
     * Obtain Transactions.
     * @return the Transactions
     */
    public TransactionList getTransactions() {
        return getDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
    }

    /**
     * Obtain TransactionInfo.
     * @return the Transaction Info
     */
    public TransactionInfoList getTransactionInfo() {
        return getDataList(MoneyWiseDataType.TRANSACTIONINFO, TransactionInfoList.class);
    }

    /**
     * Obtain Schedules.
     * @return the Schedules
     */
    public ScheduleList getSchedules() {
        return getDataList(MoneyWiseDataType.SCHEDULE, ScheduleList.class);
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
            case DEPOSITTYPE:
                return new DepositCategoryTypeList(this);
            case CASHTYPE:
                return new CashCategoryTypeList(this);
            case LOANTYPE:
                return new LoanCategoryTypeList(this);
            case PAYEETYPE:
                return new PayeeTypeList(this);
            case SECURITYTYPE:
                return new SecurityTypeList(this);
            case TRANSTYPE:
                return new TransactionCategoryTypeList(this);
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
            case TRANSINFOTYPE:
                return new TransactionInfoTypeList(this);
            case TRANSTAG:
                return new TransactionTagList(this);
            case DEPOSITCATEGORY:
                return new DepositCategoryList(this);
            case CASHCATEGORY:
                return new CashCategoryList(this);
            case LOANCATEGORY:
                return new LoanCategoryList(this);
            case TRANSCATEGORY:
                return new TransactionCategoryList(this);
            case TAXYEAR:
                return new TaxYearList(this);
            case TAXYEARINFO:
                return new TaxInfoList(this);
            case EXCHANGERATE:
                return new ExchangeRateList(this);
            case PAYEE:
                return new PayeeList(this);
            case PAYEEINFO:
                return new PayeeInfoList(this);
            case SECURITY:
                return new SecurityList(this);
            case SECURITYPRICE:
                return new SecurityPriceList(this);
            case SECURITYINFO:
                return new SecurityInfoList(this);
            case DEPOSIT:
                return new DepositList(this);
            case DEPOSITRATE:
                return new DepositRateList(this);
            case DEPOSITINFO:
                return new DepositInfoList(this);
            case CASH:
                return new CashList(this);
            case CASHINFO:
                return new CashInfoList(this);
            case LOAN:
                return new LoanList(this);
            case LOANINFO:
                return new LoanInfoList(this);
            case PORTFOLIO:
                return new PortfolioList(this);
            case PORTFOLIOINFO:
                return new PortfolioInfoList(this);
            case STOCKOPTION:
                return new StockOptionList(this);
            case STOCKOPTIONVEST:
                return new StockOptionVestList(this);
            case STOCKOPTIONINFO:
                return new StockOptionInfoList(this);
            case TRANSACTION:
                return new TransactionList(this);
            case TRANSACTIONINFO:
                return new TransactionInfoList(this);
            case SCHEDULE:
                return new ScheduleList(this);
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

        /* Create empty clone lists */
        myExtract.buildEmptyCloneSet(this);

        /* Obtain underlying updates */
        myExtract.deriveCloneSet(this);

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in this list, but not in the old list will be viewed as inserted. Items that are in the old list but not in this list will be viewed as deleted. Items
     * that are in both lists but differ will be viewed as changed
     * @param pTask the task control
     * @param pOld The DataSet to compare to
     * @return the difference extract
     * @throws JOceanusException on error
     */
    @Override
    public MoneyWiseData getDifferenceSet(final TaskControl<MoneyWiseData> pTask,
                                          final MoneyWiseData pOld) throws JOceanusException {
        /* Build an empty DataSet */
        MoneyWiseData myDiffers = new MoneyWiseData(this);

        /* Obtain underlying differences */
        myDiffers.deriveDifferences(pTask, this, pOld);

        /* Return the differences */
        return myDiffers;
    }

    /**
     * Calculate the allowed Date Range.
     */
    public void calculateDateRange() {
        theDefaultCurrency = getAccountCurrencies().findDefault();
        theDateRange = getTaxYears().getRange();
        getTransactions().setRange(theDateRange);
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

            /* Prepare list for analysis */
            DataList<?, MoneyWiseDataType> myList = myEntry.getValue();
            myList.prepareForAnalysis();
        }
    }

    /**
     * Complete the data analysis.
     * @throws JOceanusException on error
     */
    public void completeAnalysis() throws JOceanusException {
        /* Reinstate the lock */
        setLocked(true);
    }
}
