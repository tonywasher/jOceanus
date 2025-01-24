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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket.MoneyWiseXAnalysisTransTagBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxYear;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxYearCache;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class MoneyWiseXAnalysis
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_RANGE, MoneyWiseXAnalysis::getDateRange);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.TAX_YEARS, MoneyWiseXAnalysis::getTaxYearCache);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.CURRENCY, MoneyWiseXAnalysis::getCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSIT.getListId(), MoneyWiseXAnalysis::getDeposits);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASH.getListId(), MoneyWiseXAnalysis::getCash);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOAN.getListId(), MoneyWiseXAnalysis::getLoans);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PAYEE.getListId(), MoneyWiseXAnalysis::getPayees);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO.getListId(), MoneyWiseXAnalysis::getPortfolios);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY.getListId(), MoneyWiseXAnalysis::getDepositCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY.getListId(), MoneyWiseXAnalysis::getCashCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY.getListId(), MoneyWiseXAnalysis::getLoanCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSCATEGORY.getListId(), MoneyWiseXAnalysis::getTransCategories);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSTAG.getListId(), MoneyWiseXAnalysis::getTransactionTags);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS.getListId(), MoneyWiseXAnalysis::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.TAX_CALCULATION, MoneyWiseXAnalysis::getTaxAnalysis);
    }

    /**
     * The EditSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The Cursor.
     */
    private final MoneyWiseXAnalysisCursor theCursor;

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
    private final OceanusDateRange theDateRange;

    /**
     * The deposit buckets.
     */
    private final MoneyWiseXAnalysisDepositBucketList theDeposits;

    /**
     * The cash buckets.
     */
    private final MoneyWiseXAnalysisCashBucketList theCash;

    /**
     * The loan buckets.
     */
    private final MoneyWiseXAnalysisLoanBucketList theLoans;

    /**
     * The payee buckets.
     */
    private final MoneyWiseXAnalysisPayeeBucketList thePayees;

    /**
     * The portfolio buckets.
     */
    private final MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The deposit category buckets.
     */
    private final MoneyWiseXAnalysisDepositCategoryBucketList theDepositCategories;

    /**
     * The cash category buckets.
     */
    private final MoneyWiseXAnalysisCashCategoryBucketList theCashCategories;

    /**
     * The loan category buckets.
     */
    private final MoneyWiseXAnalysisLoanCategoryBucketList theLoanCategories;

    /**
     * The transaction category buckets.
     */
    private final MoneyWiseXAnalysisTransCategoryBucketList theTransCategories;

    /**
     * The TransactionTag buckets.
     */
    private final MoneyWiseXAnalysisTransTagBucketList theTransTags;

    /**
     * The tax basis buckets.
     */
    private final MoneyWiseXAnalysisTaxBasisBucketList theTaxBasis;

    /**
     * The new tax calculations.
     */
    private final MoneyWiseTaxAnalysis theTaxAnalysis;

    /**
     * Constructor for a full analysis.
     * @param pEditSet the editSet to analyse events for
     * @param pCursor the cursor
     * @param pPreferenceMgr the preference manager
     */
    public MoneyWiseXAnalysis(final PrometheusEditSet pEditSet,
                              final MoneyWiseXAnalysisCursor pCursor,
                              final MetisPreferenceManager pPreferenceMgr) {
        /* Store the data */
        theEditSet = pEditSet;
        theCursor = pCursor;
        final MoneyWiseDataSet myDataSet = getData();
        theCurrency = myDataSet.getReportingCurrency();
        thePreferences = pPreferenceMgr;
        theDateRange = myDataSet.getDateRange();

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) myDataSet.getTaxFactory();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseXAnalysisDepositBucketList(this);
        theCash = new MoneyWiseXAnalysisCashBucketList(this);
        theLoans = new MoneyWiseXAnalysisLoanBucketList(this);
        thePortfolios = new MoneyWiseXAnalysisPortfolioBucketList(this);
        thePayees = new MoneyWiseXAnalysisPayeeBucketList(this);
        theTaxBasis = new MoneyWiseXAnalysisTaxBasisBucketList(this);
        theTransCategories = new MoneyWiseXAnalysisTransCategoryBucketList(this);
        theTransTags = new MoneyWiseXAnalysisTransTagBucketList(this);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseXAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseXAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseXAnalysisLoanCategoryBucketList(this);
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a dated analysis.
     * @param pManager the analysis manager
     * @param pDate the date for the analysis
     */
    public MoneyWiseXAnalysis(final MoneyWiseXAnalysisManager pManager,
                              final OceanusDate pDate) {
        /* Store the data */
        final MoneyWiseXAnalysis myBase = pManager.getAnalysis();
        theEditSet = myBase.getEditSet();
        final MoneyWiseDataSet myDataSet = getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = new OceanusDateRange(myDataSet.getDateRange().getStart(), pDate);
        theCursor = null;

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) myDataSet.getTaxFactory();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseXAnalysisDepositBucketList(this, myBase.getDeposits(), pDate);
        theCash = new MoneyWiseXAnalysisCashBucketList(this, myBase.getCash(), pDate);
        theLoans = new MoneyWiseXAnalysisLoanBucketList(this, myBase.getLoans(), pDate);
        thePortfolios = new MoneyWiseXAnalysisPortfolioBucketList(this, myBase.getPortfolios(), pDate);
        thePayees = new MoneyWiseXAnalysisPayeeBucketList(this, myBase.getPayees(), pDate);
        theTaxBasis = new MoneyWiseXAnalysisTaxBasisBucketList(this, myBase.getTaxBasis(), pDate);
        theTransCategories = new MoneyWiseXAnalysisTransCategoryBucketList(this, myBase.getTransCategories(), pDate);
        theTransTags = new MoneyWiseXAnalysisTransTagBucketList(this, myBase.getTransactionTags(), pDate);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseXAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseXAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseXAnalysisLoanCategoryBucketList(this);
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a ranged analysis.
     * @param pManager the analysis manager
     * @param pRange the range for the analysis
     */
    public MoneyWiseXAnalysis(final MoneyWiseXAnalysisManager pManager,
                              final OceanusDateRange pRange) {
        /* Store the data */
        final MoneyWiseXAnalysis myBase = pManager.getAnalysis();
        theEditSet = myBase.getEditSet();
        final MoneyWiseDataSet myDataSet = getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = pRange;
        theCursor = null;

        /* Access the TaxYearCache */
        theTaxYearCache = (MoneyWiseUKTaxYearCache) myDataSet.getTaxFactory();

        /* Create a new set of buckets */
        theDeposits = new MoneyWiseXAnalysisDepositBucketList(this, myBase.getDeposits(), pRange);
        theCash = new MoneyWiseXAnalysisCashBucketList(this, myBase.getCash(), pRange);
        theLoans = new MoneyWiseXAnalysisLoanBucketList(this, myBase.getLoans(), pRange);
        thePortfolios = new MoneyWiseXAnalysisPortfolioBucketList(this, myBase.getPortfolios(), pRange);
        thePayees = new MoneyWiseXAnalysisPayeeBucketList(this, myBase.getPayees(), pRange);
        theTaxBasis = new MoneyWiseXAnalysisTaxBasisBucketList(this, myBase.getTaxBasis(), pRange);
        theTransCategories = new MoneyWiseXAnalysisTransCategoryBucketList(this, myBase.getTransCategories(), pRange);
        theTransTags = new MoneyWiseXAnalysisTransTagBucketList(this, myBase.getTransactionTags(), pRange);

        /* Create totalling buckets */
        theDepositCategories = new MoneyWiseXAnalysisDepositCategoryBucketList(this);
        theCashCategories = new MoneyWiseXAnalysisCashCategoryBucketList(this);
        theLoanCategories = new MoneyWiseXAnalysisLoanCategoryBucketList(this);

        /* Handle new tax calculations */
        final MoneyWiseTaxYear myYear = (MoneyWiseTaxYear) theTaxYearCache.findTaxYearForRange(theDateRange);
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
    public MetisFieldSet<MoneyWiseXAnalysis> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the editSet.
     * @return the editSet
     */
    public PrometheusEditSet getEditSet() {
        return theEditSet;
    }

    /**
     * Obtain the cursor.
     * @return the cursor
     */
    public MoneyWiseXAnalysisCursor getCursor() {
        return theCursor;
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public MoneyWiseDataSet getData() {
        return (MoneyWiseDataSet) theEditSet.getDataSet();
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
    public OceanusDateRange getDateRange() {
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
    public MoneyWiseXAnalysisDepositBucketList getDeposits() {
        return theDeposits;
    }

    /**
     * Obtain the cash buckets list.
     * @return the list
     */
    public MoneyWiseXAnalysisCashBucketList getCash() {
        return theCash;
    }

    /**
     * Obtain the loan buckets list.
     * @return the list
     */
    public MoneyWiseXAnalysisLoanBucketList getLoans() {
        return theLoans;
    }

    /**
     * Obtain the portfolio buckets list.
     * @return the list
     */
    public MoneyWiseXAnalysisPortfolioBucketList getPortfolios() {
        return thePortfolios;
    }

    /**
     * Obtain the payee buckets list.
     * @return the list
     */
    public MoneyWiseXAnalysisPayeeBucketList getPayees() {
        return thePayees;
    }

    /**
     * Obtain the deposit categories list.
     * @return the list
     */
    public MoneyWiseXAnalysisDepositCategoryBucketList getDepositCategories() {
        return theDepositCategories;
    }

    /**
     * Obtain the cash categories list.
     * @return the list
     */
    public MoneyWiseXAnalysisCashCategoryBucketList getCashCategories() {
        return theCashCategories;
    }

    /**
     * Obtain the loan categories list.
     * @return the list
     */
    public MoneyWiseXAnalysisLoanCategoryBucketList getLoanCategories() {
        return theLoanCategories;
    }

    /**
     * Obtain the transaction categories list.
     * @return the list
     */
    public MoneyWiseXAnalysisTransCategoryBucketList getTransCategories() {
        return theTransCategories;
    }

    /**
     * Obtain the transactionTag list.
     * @return the list
     */
    public MoneyWiseXAnalysisTransTagBucketList getTransactionTags() {
        return theTransTags;
    }

    /**
     * Obtain the tax basis list.
     * @return the list
     */
    public MoneyWiseXAnalysisTaxBasisBucketList getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Obtain the tax analysis.
     * @return the analysis
     */
    public MoneyWiseTaxAnalysis getTaxAnalysis() {
        return theTaxAnalysis;
    }
}
