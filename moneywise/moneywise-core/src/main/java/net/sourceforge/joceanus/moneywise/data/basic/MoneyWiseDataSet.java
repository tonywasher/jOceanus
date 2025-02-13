/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanInfo.MoneyWiseLoanInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayeeInfo.MoneyWisePayeeInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolioInfo.MoneyWisePortfolioInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType.MoneyWiseCashCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType.MoneyWiseDepositCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType.MoneyWisePayeeTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType.MoneyWiseTransCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * MoneyWise dataSet.
 */
public class MoneyWiseDataSet
        extends PrometheusDataSet {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseDataSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDataSet.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseStaticDataType.class, MoneyWiseDataSet::getFieldListValue);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseBasicDataType.class, MoneyWiseDataSet::getFieldListValue);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.CURRENCY_REPORTING, MoneyWiseDataSet::getReportingCurrency);
    }

    /**
     * TaxFactory.
     */
    private final MoneyWiseTaxFactory theTaxFactory;

    /**
     * Reporting Currency.
     */
    private MoneyWiseCurrency theReportingCurrency;

    /**
     * Check Closed Accounts.
     */
    private boolean ignoreCheckClosedAccounts;

    /**
     * Standard constructor.
     * @param pUtilitySet the utility set
     * @param pTaxFactory the tax factory
     */
    public MoneyWiseDataSet(final PrometheusToolkit pUtilitySet,
                            final MoneyWiseTaxFactory pTaxFactory) {
        /* Call Super-constructor */
        super(pUtilitySet);

        /* Record the tax factory */
        theTaxFactory = pTaxFactory;

        /* Loop through the list types */
        for (MoneyWiseStaticDataType myType : MoneyWiseStaticDataType.values()) {
            /* Create the empty list */
            addList(myType, newList(myType));
        }

        /* Loop through the list types */
        for (MoneyWiseBasicDataType myType : MoneyWiseBasicDataType.values()) {
            /* Create the empty list */
            addList(myType, newList(myType));
        }
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    private MoneyWiseDataSet(final MoneyWiseDataSet pSource) {
        super(pSource);
        theTaxFactory = pSource.getTaxFactory();
    }

    @Override
    public MetisFieldSet<MoneyWiseDataSet> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return MoneyWiseDataSet.class.getSimpleName();
    }

    /**
     * Obtain DepositCategoryTypes.
     * @return the Deposit category types
     */
    public MoneyWiseDepositCategoryTypeList getDepositCategoryTypes() {
        return getDataList(MoneyWiseStaticDataType.DEPOSITTYPE, MoneyWiseDepositCategoryTypeList.class);
    }

    /**
     * Obtain CashCategoryTypes.
     * @return the Cash category types
     */
    public MoneyWiseCashCategoryTypeList getCashCategoryTypes() {
        return getDataList(MoneyWiseStaticDataType.CASHTYPE, MoneyWiseCashCategoryTypeList.class);
    }

    /**
     * Obtain LoanCategoryTypes.
     * @return the Loan category types
     */
    public MoneyWiseLoanCategoryTypeList getLoanCategoryTypes() {
        return getDataList(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryTypeList.class);
    }

    /**
     * Obtain PayeeTypes.
     * @return the Payee types
     */
    public MoneyWisePayeeTypeList getPayeeTypes() {
        return getDataList(MoneyWiseStaticDataType.PAYEETYPE, MoneyWisePayeeTypeList.class);
    }

    /**
     * Obtain PortfolioTypes.
     * @return the Portfolio types
     */
    public MoneyWisePortfolioTypeList getPortfolioTypes() {
        return getDataList(MoneyWiseStaticDataType.PORTFOLIOTYPE, MoneyWisePortfolioTypeList.class);
    }

    /**
     * Obtain SecurityTypes.
     * @return the Security types
     */
    public MoneyWiseSecurityTypeList getSecurityTypes() {
        return getDataList(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseSecurityTypeList.class);
    }

    /**
     * Obtain TransactionCategoryTypes.
     * @return the Transaction Category types
     */
    public MoneyWiseTransCategoryTypeList getTransCategoryTypes() {
        return getDataList(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryTypeList.class);
    }

    /**
     * Obtain TaxBases.
     * @return the Tax bases
     */
    public MoneyWiseTaxBasisList getTaxBases() {
        return getDataList(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseTaxBasisList.class);
    }

    /**
     * Obtain Asset Currencies.
     * @return the Asset Currencies
     */
    public MoneyWiseCurrencyList getAccountCurrencies() {
        return getDataList(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrencyList.class);
    }

    /**
     * Obtain AccountInfoTypes.
     * @return the Account Info types
     */
    public MoneyWiseAccountInfoTypeList getActInfoTypes() {
        return getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);
    }

    /**
     * Obtain TransactionInfoTypes.
     * @return the Transaction Info types
     */
    public MoneyWiseTransInfoTypeList getTransInfoTypes() {
        return getDataList(MoneyWiseStaticDataType.TRANSINFOTYPE, MoneyWiseTransInfoTypeList.class);
    }

    /**
     * Obtain Transaction Tags.
     * @return the Transaction Tags
     */
    public MoneyWiseTransTagList getTransactionTags() {
        return getDataList(MoneyWiseBasicDataType.TRANSTAG, MoneyWiseTransTagList.class);
    }

    /**
     * Obtain Regions.
     * @return the regions
     */
    public MoneyWiseRegionList getRegions() {
        return getDataList(MoneyWiseBasicDataType.REGION, MoneyWiseRegionList.class);
    }

    /**
     * Obtain DepositCategories.
     * @return the Deposit categories
     */
    public MoneyWiseDepositCategoryList getDepositCategories() {
        return getDataList(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseDepositCategoryList.class);
    }

    /**
     * Obtain CashCategories.
     * @return the Cash categories
     */
    public MoneyWiseCashCategoryList getCashCategories() {
        return getDataList(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseCashCategoryList.class);
    }

    /**
     * Obtain LoanCategories.
     * @return the Loan categories
     */
    public MoneyWiseLoanCategoryList getLoanCategories() {
        return getDataList(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseLoanCategoryList.class);
    }

    /**
     * Obtain TransactionCategories.
     * @return the Transaction categories
     */
    public MoneyWiseTransCategoryList getTransCategories() {
        return getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);
    }

    /**
     * Obtain ExchangeRates.
     * @return the ExchangeRates
     */
    public MoneyWiseExchangeRateList getExchangeRates() {
        return getDataList(MoneyWiseBasicDataType.EXCHANGERATE, MoneyWiseExchangeRateList.class);
    }

    /**
     * Obtain Payees.
     * @return the Payees
     */
    public MoneyWisePayeeList getPayees() {
        return getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
    }

    /**
     * Obtain PayeeInfo.
     * @return the Payee Info
     */
    public MoneyWisePayeeInfoList getPayeeInfo() {
        return getDataList(MoneyWiseBasicDataType.PAYEEINFO, MoneyWisePayeeInfoList.class);
    }

    /**
     * Obtain Securities.
     * @return the Securities
     */
    public MoneyWiseSecurityList getSecurities() {
        return getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class);
    }

    /**
     * Obtain SecurityPrices.
     * @return the Security prices
     */
    public MoneyWiseSecurityPriceList getSecurityPrices() {
        return getDataList(MoneyWiseBasicDataType.SECURITYPRICE, MoneyWiseSecurityPriceList.class);
    }

    /**
     * Obtain SecurityInfo.
     * @return the Security Info
     */
    public MoneyWiseSecurityInfoList getSecurityInfo() {
        return getDataList(MoneyWiseBasicDataType.SECURITYINFO, MoneyWiseSecurityInfoList.class);
    }

    /**
     * Obtain Deposits.
     * @return the Deposits
     */
    public MoneyWiseDepositList getDeposits() {
        return getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class);
    }

    /**
     * Obtain DepositRates.
     * @return the Deposit rates
     */
    public MoneyWiseDepositRateList getDepositRates() {
        return getDataList(MoneyWiseBasicDataType.DEPOSITRATE, MoneyWiseDepositRateList.class);
    }

    /**
     * Obtain DepositInfo.
     * @return the Deposit Info
     */
    public MoneyWiseDepositInfoList getDepositInfo() {
        return getDataList(MoneyWiseBasicDataType.DEPOSITINFO, MoneyWiseDepositInfoList.class);
    }

    /**
     * Obtain Cash.
     * @return the Cash
     */
    public MoneyWiseCashList getCash() {
        return getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class);
    }

    /**
     * Obtain CashInfo.
     * @return the Cash Info
     */
    public MoneyWiseCashInfoList getCashInfo() {
        return getDataList(MoneyWiseBasicDataType.CASHINFO, MoneyWiseCashInfoList.class);
    }

    /**
     * Obtain Loans.
     * @return the Loans
     */
    public MoneyWiseLoanList getLoans() {
        return getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class);
    }

    /**
     * Obtain LoanInfo.
     * @return the Loan Info
     */
    public MoneyWiseLoanInfoList getLoanInfo() {
        return getDataList(MoneyWiseBasicDataType.LOANINFO, MoneyWiseLoanInfoList.class);
    }

    /**
     * Obtain Portfolios.
     * @return the Portfolios
     */
    public MoneyWisePortfolioList getPortfolios() {
        return getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
    }

    /**
     * Obtain PortfolioInfo.
     * @return the Portfolio Info
     */
    public MoneyWisePortfolioInfoList getPortfolioInfo() {
        return getDataList(MoneyWiseBasicDataType.PORTFOLIOINFO, MoneyWisePortfolioInfoList.class);
    }

    /**
     * Obtain Transactions.
     * @return the Transactions
     */
    public MoneyWiseTransactionList getTransactions() {
        return getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
    }

    /**
     * Obtain TransactionInfo.
     * @return the Transaction Info
     */
    public MoneyWiseTransInfoList getTransactionInfo() {
        return getDataList(MoneyWiseBasicDataType.TRANSACTIONINFO, MoneyWiseTransInfoList.class);
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
    public OceanusDateRange getDateRange() {
        return theTaxFactory.getDateRange();
    }

    /**
     * Obtain default currency.
     * @return the default currency
     */
    public MoneyWiseCurrency getReportingCurrency() {
        /* If we have note yet determined the reporting currency */
        if (theReportingCurrency == null) {
            /* Determine the default currency */
            theReportingCurrency = getAccountCurrencies().findReporting();
        }
        return theReportingCurrency;
    }

    /**
     * Obtain security prices map.
     * @return the prices map
     */
    public MoneyWiseSecurityPriceDataMap getSecurityPriceDataMap() {
        return getSecurityPrices().getDataMap();
    }

    /**
     * Obtain deposit rates map.
     * @return the rates map
     */
    public MoneyWiseDepositRateDataMap getDepositRateDataMap() {
        return getDepositRates().getDataMap();
    }

    /**
     * Obtain exchange rates map.
     * @return the rates map
     */
    public MoneyWiseExchangeRateDataMap getExchangeRateDataMap() {
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
    private PrometheusDataList<?> newList(final MoneyWiseStaticDataType pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case DEPOSITTYPE:
                return new MoneyWiseDepositCategoryTypeList(this);
            case CASHTYPE:
                return new MoneyWiseCashCategoryTypeList(this);
            case LOANTYPE:
                return new MoneyWiseLoanCategoryTypeList(this);
            case PAYEETYPE:
                return new MoneyWisePayeeTypeList(this);
            case PORTFOLIOTYPE:
                return new MoneyWisePortfolioTypeList(this);
            case SECURITYTYPE:
                return new MoneyWiseSecurityTypeList(this);
            case TRANSTYPE:
                return new MoneyWiseTransCategoryTypeList(this);
            case TAXBASIS:
                return new MoneyWiseTaxBasisList(this);
            case CURRENCY:
                return new MoneyWiseCurrencyList(this);
            case ACCOUNTINFOTYPE:
                return new MoneyWiseAccountInfoTypeList(this);
            case TRANSINFOTYPE:
                return new MoneyWiseTransInfoTypeList(this);
            default:
                throw new IllegalArgumentException(pListType.toString());
        }
    }

    /**
     * Create new list of required type.
     * @param pListType the list type
     * @return the new list
     */
    private PrometheusDataList<?> newList(final MoneyWiseBasicDataType pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case TRANSTAG:
                return new MoneyWiseTransTagList(this);
            case REGION:
                return new MoneyWiseRegionList(this);
            case DEPOSITCATEGORY:
                return new MoneyWiseDepositCategoryList(this);
            case CASHCATEGORY:
                return new MoneyWiseCashCategoryList(this);
            case LOANCATEGORY:
                return new MoneyWiseLoanCategoryList(this);
            case TRANSCATEGORY:
                return new MoneyWiseTransCategoryList(this);
            case EXCHANGERATE:
                return new MoneyWiseExchangeRateList(this);
            case PAYEE:
                return new MoneyWisePayeeList(this);
            case PAYEEINFO:
                return new MoneyWisePayeeInfoList(this);
            case SECURITY:
                return new MoneyWiseSecurityList(this);
            case SECURITYPRICE:
                return new MoneyWiseSecurityPriceList(this);
            case SECURITYINFO:
                return new MoneyWiseSecurityInfoList(this);
            case DEPOSIT:
                return new MoneyWiseDepositList(this);
            case DEPOSITRATE:
                return new MoneyWiseDepositRateList(this);
            case DEPOSITINFO:
                return new MoneyWiseDepositInfoList(this);
            case CASH:
                return new MoneyWiseCashList(this);
            case CASHINFO:
                return new MoneyWiseCashInfoList(this);
            case LOAN:
                return new MoneyWiseLoanList(this);
            case LOANINFO:
                return new MoneyWiseLoanInfoList(this);
            case PORTFOLIO:
                return new MoneyWisePortfolioList(this);
            case PORTFOLIOINFO:
                return new MoneyWisePortfolioInfoList(this);
            case TRANSACTION:
                return new MoneyWiseTransactionList(this);
            case TRANSACTIONINFO:
                return new MoneyWiseTransInfoList(this);
            default:
                throw new IllegalArgumentException(pListType.toString());
        }
    }

    @Override
    public MoneyWiseDataSet deriveUpdateSet() throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseDataSet myExtract = new MoneyWiseDataSet(this);

        /* Obtain underlying updates */
        myExtract.deriveUpdateSet(this);

        /* Return the extract */
        return myExtract;
    }

    @Override
    public MoneyWiseDataSet deriveCloneSet() throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseDataSet myExtract = new MoneyWiseDataSet(this);

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
    public MoneyWiseDataSet getDifferenceSet(final TethysUIThreadStatusReport pReport,
                                             final PrometheusDataSet pOld) throws OceanusException {
        /* Build an empty DataSet */
        final MoneyWiseDataSet myDiffers = new MoneyWiseDataSet(this);

        /* Obtain underlying differences */
        myDiffers.deriveDifferences(pReport, this, pOld);

        /* Return the differences */
        return myDiffers;
    }

    /**
     * Update Maps.
     */
    public void updateMaps() {
        /* Loop through the list types */
        final Iterator<Entry<PrometheusListKey, PrometheusDataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKey, PrometheusDataList<?>> myEntry = myIterator.next();

            /* Prepare list for analysis (ignoring cryptography tables) */
            if (!(myEntry.getKey() instanceof PrometheusCryptographyDataType)) {
                final PrometheusDataList<?> myList = myEntry.getValue();
                myList.updateMaps();
            }
        }
    }
}
