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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket.MoneyWiseAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDilutionEvent.MoneyWiseAnalysisDilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket.MoneyWiseAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransTagBucket.MoneyWiseAnalysisTransTagBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxYear;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class MoneyWiseAnalysis
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_RANGE, MoneyWiseAnalysis::getDateRange);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.TAX_YEARS, MoneyWiseAnalysis::getTaxYearCache);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.CURRENCY, MoneyWiseAnalysis::getCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSIT.getListId(), MoneyWiseAnalysis::getDeposits);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASH.getListId(), MoneyWiseAnalysis::getCash);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOAN.getListId(), MoneyWiseAnalysis::getLoans);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PAYEE.getListId(), MoneyWiseAnalysis::getPayees);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO.getListId(), MoneyWiseAnalysis::getPortfolios);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY.getListId(), MoneyWiseAnalysis::getDepositCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY.getListId(), MoneyWiseAnalysis::getCashCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY.getListId(), MoneyWiseAnalysis::getLoanCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSCATEGORY.getListId(), MoneyWiseAnalysis::getTransCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSTAG.getListId(), MoneyWiseAnalysis::getTransactionTags);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS.getListId(), MoneyWiseAnalysis::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.TAX_CALCULATION, MoneyWiseAnalysis::getTaxAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_DILUTIONS, MoneyWiseAnalysis::getDilutions);
    }

    /**
     * The DataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * The taxYear cache.
     */
    private final MoneyWiseTaxYearCache theTaxYearCache;

    /**
     * The Currency.
     */
    private final MoneyWiseCurrency theCurrency;

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePreferences;

    /**
     * The DataRange.
     */
    private final TethysDateRange theDateRange;

    /**
     * The deposit buckets.
     */
    private final MoneyWiseAnalysisDepositBucketList theDeposits;

    /**
     * The cash buckets.
     */
    private final MoneyWiseAnalysisCashBucketList theCash;

    /**
     * The loan buckets.
     */
    private final MoneyWiseAnalysisLoanBucketList theLoans;

    /**
     * The payee buckets.
     */
    private final MoneyWiseAnalysisPayeeBucketList thePayees;

    /**
     * The portfolio buckets.
     */
    private final MoneyWiseAnalysisPortfolioBucketList thePortfolios;

    /**
     * The deposit category buckets.
     */
    private final MoneyWiseAnalysisDepositCategoryBucketList theDepositCategories;

    /**
     * The cash category buckets.
     */
    private final MoneyWiseAnalysisCashCategoryBucketList theCashCategories;

    /**
     * The loan category buckets.
     */
    private final MoneyWiseAnalysisLoanCategoryBucketList theLoanCategories;

    /**
     * The transaction category buckets.
     */
    private final MoneyWiseAnalysisTransCategoryBucketList theTransCategories;

    /**
     * The TransactionTag buckets.
     */
    private final MoneyWiseAnalysisTransTagBucketList theTransTags;

    /**
     * The tax basis buckets.
     */
    private final MoneyWiseAnalysisTaxBasisBucketList theTaxBasis;

    /**
     * The new tax calculations.
     */
    private final MoneyWiseTaxAnalysis theTaxAnalysis;

    /**
     * The dilutions.
     */
    private final MoneyWiseAnalysisDilutionEventMap theDilutions;

    /**
     * The security transactions.
     */
    private final List<MoneyWiseTransaction> theSecurities;

    /**
     * Constructor for a full analysis.
     * @param pData the data to analyse events for
     * @param pPreferenceMgr the preference manager
     */
    protected MoneyWiseAnalysis(final MoneyWiseDataSet pData,
                                final MetisPreferenceManager pPreferenceMgr) {
        /* Store the data */
        theData = pData;
        theCurrency = pData.getDefaultCurrency();
        thePreferences = pPreferenceMgr;
        theDateRange = theData.getDateRange();

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) theData.getTaxFactory();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseAnalysisDepositBucketList(this);
        theCash = new MoneyWiseAnalysisCashBucketList(this);
        theLoans = new MoneyWiseAnalysisLoanBucketList(this);
        thePortfolios = new MoneyWiseAnalysisPortfolioBucketList(this);
        thePayees = new MoneyWiseAnalysisPayeeBucketList(this);
        theTaxBasis = new MoneyWiseAnalysisTaxBasisBucketList(this);
        theTransCategories = new MoneyWiseAnalysisTransCategoryBucketList(this);
        theTransTags = new MoneyWiseAnalysisTransTagBucketList(this);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseAnalysisLoanCategoryBucketList(this);
        theTaxAnalysis = null;

        /* Create the Dilution Event List */
        theDilutions = new MoneyWiseAnalysisDilutionEventMap();
        theSecurities = new ArrayList<>();
    }

    /**
     * Constructor for a view analysis.
     * @param pSource the base analysis
     */
    protected MoneyWiseAnalysis(final MoneyWiseAnalysis pSource) {
        /* Store the data */
        theData = pSource.getData();
        theCurrency = pSource.getCurrency();
        thePreferences = pSource.getPreferenceMgr();
        theDateRange = pSource.getDateRange();

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) theData.getTaxFactory();

        /* Access the underlying maps/lists */
        final TethysDate myStart = theDateRange.getStart();
        theDilutions = myStart == null
                ? new MoneyWiseAnalysisDilutionEventMap()
                : new MoneyWiseAnalysisDilutionEventMap(pSource.getDilutions(), myStart);
        theSecurities = pSource.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseAnalysisDepositBucketList(this, pSource.getDeposits());
        theCash = new MoneyWiseAnalysisCashBucketList(this, pSource.getCash());
        theLoans = new MoneyWiseAnalysisLoanBucketList(this, pSource.getLoans());
        thePortfolios = new MoneyWiseAnalysisPortfolioBucketList(this, pSource.getPortfolios());
        thePayees = new MoneyWiseAnalysisPayeeBucketList(this);
        theTaxBasis = new MoneyWiseAnalysisTaxBasisBucketList(this);
        theTransCategories = new MoneyWiseAnalysisTransCategoryBucketList(this);
        theTransTags = new MoneyWiseAnalysisTransTagBucketList(this);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseAnalysisLoanCategoryBucketList(this);
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a dated analysis.
     * @param pManager the analysis manager
     * @param pDate the date for the analysis
     */
    protected MoneyWiseAnalysis(final MoneyWiseAnalysisManager pManager,
                                final TethysDate pDate) {
        /* Store the data */
        final MoneyWiseAnalysis myBase = pManager.getAnalysis();
        theData = myBase.getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = new TethysDateRange(theData.getDateRange().getStart(), pDate);

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) theData.getTaxFactory();

        /* Access the underlying maps/lists */
        theDilutions = myBase.getDilutions();
        theSecurities = myBase.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseAnalysisDepositBucketList(this, myBase.getDeposits(), pDate);
        theCash = new MoneyWiseAnalysisCashBucketList(this, myBase.getCash(), pDate);
        theLoans = new MoneyWiseAnalysisLoanBucketList(this, myBase.getLoans(), pDate);
        thePortfolios = new MoneyWiseAnalysisPortfolioBucketList(this, myBase.getPortfolios(), pDate);
        thePayees = new MoneyWiseAnalysisPayeeBucketList(this, myBase.getPayees(), pDate);
        theTaxBasis = new MoneyWiseAnalysisTaxBasisBucketList(this, myBase.getTaxBasis(), pDate);
        theTransCategories = new MoneyWiseAnalysisTransCategoryBucketList(this, myBase.getTransCategories(), pDate);
        theTransTags = new MoneyWiseAnalysisTransTagBucketList(this, myBase.getTransactionTags(), pDate);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseAnalysisLoanCategoryBucketList(this);
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a ranged analysis.
     * @param pManager the analysis manager
     * @param pRange the range for the analysis
     */
    protected MoneyWiseAnalysis(final MoneyWiseAnalysisManager pManager,
                                final TethysDateRange pRange) {
        /* Store the data */
        final MoneyWiseAnalysis myBase = pManager.getAnalysis();
        theData = myBase.getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = pRange;

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) theData.getTaxFactory();

        /* Access the underlying maps/lists */
        theDilutions = myBase.getDilutions();
        theSecurities = myBase.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseAnalysisDepositBucketList(this, myBase.getDeposits(), pRange);
        theCash = new MoneyWiseAnalysisCashBucketList(this, myBase.getCash(), pRange);
        theLoans = new MoneyWiseAnalysisLoanBucketList(this, myBase.getLoans(), pRange);
        thePortfolios = new MoneyWiseAnalysisPortfolioBucketList(this, myBase.getPortfolios(), pRange);
        thePayees = new MoneyWiseAnalysisPayeeBucketList(this, myBase.getPayees(), pRange);
        theTaxBasis = new MoneyWiseAnalysisTaxBasisBucketList(this, myBase.getTaxBasis(), pRange);
        theTransCategories = new MoneyWiseAnalysisTransCategoryBucketList(this, myBase.getTransCategories(), pRange);
        theTransTags = new MoneyWiseAnalysisTransTagBucketList(this, myBase.getTransactionTags(), pRange);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseAnalysisLoanCategoryBucketList(this);

        /* Handle new tax calculations */
        final MoneyWiseTaxYear myYear = theTaxYearCache.findTaxYearForRange(theDateRange);
        theTaxAnalysis = myYear != null
                ? myYear.analyseTaxYear(thePreferences, theTaxBasis)
                : null;
    }

    /**
     * Is the analysis manager idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theCurrency == null;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysis> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public MoneyWiseDataSet getData() {
        return theData;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    private MoneyWiseTaxYearCache getTaxYearCache() {
        return theTaxYearCache;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public MoneyWiseCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the preference manager.
     * @return the data
     */
    public MetisPreferenceManager getPreferenceMgr() {
        return thePreferences;
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public TethysDateRange getDateRange() {
        return theDateRange;
    }

    /**
     * Is this a ranged analysis?
     * @return true/false
     */
    public boolean isRangedAnalysis() {
        return theDateRange.getStart() == null;
    }

    /**
     * Obtain the deposit buckets list.
     * @return the list
     */
    public MoneyWiseAnalysisDepositBucketList getDeposits() {
        return theDeposits;
    }

    /**
     * Obtain the cash buckets list.
     * @return the list
     */
    public MoneyWiseAnalysisCashBucketList getCash() {
        return theCash;
    }

    /**
     * Obtain the loan buckets list.
     * @return the list
     */
    public MoneyWiseAnalysisLoanBucketList getLoans() {
        return theLoans;
    }

    /**
     * Obtain the portfolio buckets list.
     * @return the list
     */
    public MoneyWiseAnalysisPortfolioBucketList getPortfolios() {
        return thePortfolios;
    }

    /**
     * Obtain the payee buckets list.
     * @return the list
     */
    public MoneyWiseAnalysisPayeeBucketList getPayees() {
        return thePayees;
    }

    /**
     * Obtain the deposit categories list.
     * @return the list
     */
    public MoneyWiseAnalysisDepositCategoryBucketList getDepositCategories() {
        return theDepositCategories;
    }

    /**
     * Obtain the cash categories list.
     * @return the list
     */
    public MoneyWiseAnalysisCashCategoryBucketList getCashCategories() {
        return theCashCategories;
    }

    /**
     * Obtain the loan categories list.
     * @return the list
     */
    public MoneyWiseAnalysisLoanCategoryBucketList getLoanCategories() {
        return theLoanCategories;
    }

    /**
     * Obtain the transaction categories list.
     * @return the list
     */
    public MoneyWiseAnalysisTransCategoryBucketList getTransCategories() {
        return theTransCategories;
    }

    /**
     * Obtain the transactionTag list.
     * @return the list
     */
    public MoneyWiseAnalysisTransTagBucketList getTransactionTags() {
        return theTransTags;
    }

    /**
     * Obtain the tax basis list.
     * @return the list
     */
    public MoneyWiseAnalysisTaxBasisBucketList getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Obtain the tax analysis.
     * @return the analysis
     */
    public MoneyWiseTaxAnalysis getTaxAnalysis() {
        return theTaxAnalysis;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public MoneyWiseAnalysisDilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Obtain the securities.
     * @return the securities
     */
    public List<MoneyWiseTransaction> getSecurities() {
        return theSecurities;
    }

    /**
     * Add opening balances for accounts.
     * @param pHelper the transaction helper
     */
    protected void addOpeningBalances(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Iterate through the deposits */
        final Iterator<MoneyWiseDeposit> myDepIterator = theData.getDeposits().iterator();
        while (myDepIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myDepIterator.next();

            /* If the deposit has an opening balance */
            final TethysMoney myBalance = myDeposit.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual deposit bucket */
                final MoneyWiseAnalysisDepositBucket myBucket = theDeposits.getBucket(myDeposit);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }

        /* Iterate through the cash */
        final Iterator<MoneyWiseCash> myCashIterator = theData.getCash().iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseCash myCash = myCashIterator.next();

            /* If the cash has an opening balance */
            final TethysMoney myBalance = myCash.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual cash bucket */
                final MoneyWiseAnalysisCashBucket myBucket = theCash.getBucket(myCash);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }

        /* Iterate through the loans */
        final Iterator<MoneyWiseLoan> myLoanIterator = theData.getLoans().iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseLoan myLoan = myLoanIterator.next();

            /* If the loan has an opening balance */
            final TethysMoney myBalance = myLoan.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual loan bucket */
                final MoneyWiseAnalysisLoanBucket myBucket = theLoans.getBucket(myLoan);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }
    }
}
