/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateDataMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate.ExchangeRateDataMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Schedule.ScheduleList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusListKeyX;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * MoneyWise dataSet.
 */
public class MoneyWiseData
        extends DataSet {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseData> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseData.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseDataType.class, MoneyWiseData::getFieldListValue);
        FIELD_DEFS.declareLocalField(StaticDataResource.CURRENCY_DEFAULT, MoneyWiseData::getDefaultCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_HOLDINGSMAP, MoneyWiseData::getSecurityHoldingsMap);
    }

    /**
     * TaxFactory.
     */
    private final MoneyWiseTaxFactory theTaxFactory;

    /**
     * Default Currency.
     */
    private AssetCurrency theDefaultCurrency;

    /**
     * SecurityHoldings Map.
     */
    private SecurityHoldingMap theSecurityHoldings;

    /**
     * Check Closed Accounts.
     */
    private boolean ignoreCheckClosedAccounts;

    /**
     * Standard constructor.
     * @param pUtilitySet the utility set
     * @param pTaxFactory the tax factory
     */
    public MoneyWiseData(final PrometheusToolkit pUtilitySet,
                         final MoneyWiseTaxFactory pTaxFactory) {
        /* Call Super-constructor */
        super(pUtilitySet);

        /* Record the tax factory */
        theTaxFactory = pTaxFactory;

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
        theTaxFactory = pSource.getTaxFactory();
    }

    @Override
    public MetisFieldSet<MoneyWiseData> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return MoneyWiseData.class.getSimpleName();
    }

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
     * Obtain PortfolioTypes.
     * @return the Portfolio types
     */
    public PortfolioTypeList getPortfolioTypes() {
        return getDataList(MoneyWiseDataType.PORTFOLIOTYPE, PortfolioTypeList.class);
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
     * Obtain Asset Currencies.
     * @return the Asset Currencies
     */
    public AssetCurrencyList getAccountCurrencies() {
        return getDataList(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);
    }

    /**
     * Obtain Frequencies.
     * @return the Frequencies
     */
    public FrequencyList getFrequencys() {
        return getDataList(MoneyWiseDataType.FREQUENCY, FrequencyList.class);
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
     * Obtain Regions.
     * @return the regions
     */
    public RegionList getRegions() {
        return getDataList(MoneyWiseDataType.REGION, RegionList.class);
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
     * Obtain Tax Factory.
     * @return the taxFactory
     */
    public MoneyWiseTaxFactory getTaxFactory() {
        return theTaxFactory;
    }

    /**
     * Obtain Date range.
     * @return the Date Range
     */
    public TethysDateRange getDateRange() {
        return theTaxFactory.getDateRange();
    }

    /**
     * Obtain default currency.
     * @return the default currency
     */
    public AssetCurrency getDefaultCurrency() {
        /* If we have note yet determined the default currency */
        if (theDefaultCurrency == null) {
            /* Determine the default currency */
            theDefaultCurrency = getAccountCurrencies().findDefault();
        }
        return theDefaultCurrency;
    }

    /**
     * Obtain security holdings map.
     * @return the holdings map
     */
    public SecurityHoldingMap getSecurityHoldingsMap() {
        /* If we have note yet created the map */
        if (theSecurityHoldings == null) {
            /* Create the holdings map */
            theSecurityHoldings = new SecurityHoldingMap(this);
        }
        return theSecurityHoldings;
    }

    /**
     * Obtain security prices map.
     * @return the prices map
     */
    public SecurityPriceDataMap getSecurityPriceDataMap() {
        return getSecurityPrices().getDataMap();
    }

    /**
     * Obtain deposit rates map.
     * @return the rates map
     */
    public DepositRateDataMap getDepositRateDataMap() {
        return getDepositRates().getDataMap();
    }

    /**
     * Obtain exchange rates map.
     * @return the rates map
     */
    public ExchangeRateDataMap getExchangeRateDataMap() {
        return getExchangeRates().getDataMap();
    }

    /**
     * Should we check closed accounts?
     * @return true/false
     */
    public boolean checkClosedAccounts() {
        return !ignoreCheckClosedAccounts;
    }

    /**
     * Note that we hit the last event limit.
     */
    public void hitEventLimit() {
        ignoreCheckClosedAccounts = true;
    }

    /**
     * Create new list of required type.
     * @param pListType the list type
     * @return the new list
     */
    private DataList<?> newList(final MoneyWiseDataType pListType) {
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
            case PORTFOLIOTYPE:
                return new PortfolioTypeList(this);
            case SECURITYTYPE:
                return new SecurityTypeList(this);
            case TRANSTYPE:
                return new TransactionCategoryTypeList(this);
            case TAXBASIS:
                return new TaxBasisList(this);
            case CURRENCY:
                return new AssetCurrencyList(this);
            case FREQUENCY:
                return new FrequencyList(this);
            case ACCOUNTINFOTYPE:
                return new AccountInfoTypeList(this);
            case TRANSINFOTYPE:
                return new TransactionInfoTypeList(this);
            case TRANSTAG:
                return new TransactionTagList(this);
            case REGION:
                return new RegionList(this);
            case DEPOSITCATEGORY:
                return new DepositCategoryList(this);
            case CASHCATEGORY:
                return new CashCategoryList(this);
            case LOANCATEGORY:
                return new LoanCategoryList(this);
            case TRANSCATEGORY:
                return new TransactionCategoryList(this);
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
    public MoneyWiseData deriveUpdateSet() throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseData myExtract = new MoneyWiseData(this);

        /* Obtain underlying updates */
        myExtract.deriveUpdateSet(this);

        /* Return the extract */
        return myExtract;
    }

    @Override
    public MoneyWiseData deriveCloneSet() throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseData myExtract = new MoneyWiseData(this);

        /* Create empty clone lists */
        myExtract.buildEmptyCloneSet(this);

        /* Obtain underlying updates */
        myExtract.deriveCloneSet(this);

        /* Return the extract */
        return myExtract;
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain
     * items that differ between the two DataSets. Items that are in this list, but not in the old
     * list will be viewed as inserted. Items that are in the old list but not in this list will be
     * viewed as deleted. Items that are in both lists but differ will be viewed as changed
     * @param pReport the report
     * @param pOld The DataSet to compare to
     * @return the difference extract
     * @throws OceanusException on error
     */
    @Override
    public MoneyWiseData getDifferenceSet(final TethysUIThreadStatusReport pReport,
                                          final DataSet pOld) throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseData myDiffers = new MoneyWiseData(this);

        /* Obtain underlying differences */
        myDiffers.deriveDifferences(pReport, this, pOld);

        /* Return the differences */
        return myDiffers;
    }

    /**
     * Initialise the analysis.
     */
    public void initialiseAnalysis() {
        /* Loop through the list types */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Prepare list for analysis */
            final DataList<?> myList = myEntry.getValue();
            myList.prepareForAnalysis();
        }
    }

    /**
     * Adjust security map.
     */
    public void adjustSecurityMap() {
        /* Reset security map names */
        getSecurityHoldingsMap().resetNames();
    }
}
