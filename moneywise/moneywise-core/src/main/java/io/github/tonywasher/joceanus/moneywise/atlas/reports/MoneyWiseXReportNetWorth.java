/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.reports;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.report.MetisReportBase;
import io.github.tonywasher.joceanus.metis.report.MetisReportHTMLBuilder;
import io.github.tonywasher.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import io.github.tonywasher.joceanus.metis.report.MetisReportManager;
import io.github.tonywasher.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisValuesResource;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisCashFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisDepositFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisLoanFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPortfolioCashFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisSecurityFilter;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * NetWorth report builder.
 */
public class MoneyWiseXReportNetWorth
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.NETWORTH_TITLE.getValue();

    /**
     * The Asset text.
     */
    private static final String TEXT_ASSET = MoneyWiseXReportResource.NETWORTH_ASSET.getValue();

    /**
     * The Units text.
     */
    private static final String TEXT_UNITS = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_UNITS.getValue();

    /**
     * The Price text.
     */
    private static final String TEXT_PRICE = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE.getValue();

    /**
     * The ForeignValue text.
     */
    private static final String TEXT_FOREIGNVALUE = MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_BALANCE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Account text.
     */
    private static final String TEXT_ACCOUNT = MoneyWiseAnalysisDataResource.BUCKET_ACCOUNT.getValue();

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE.getValue();

    /**
     * The Maturity text.
     */
    private static final String TEXT_MATURITY = MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_MATURITY.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pManager the Report Manager
     */
    MoneyWiseXReportNetWorth(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        final MoneyWiseXAnalysisDepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        final MoneyWiseXAnalysisCashCategoryBucketList myCash = theAnalysis.getCashCategories();
        final MoneyWiseXAnalysisLoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final OceanusDate myDate = theAnalysis.getDateRange().getEnd();

        /* Create the totals */
        final OceanusMoney myTotal = new OceanusMoney();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);

        /* If we have deposits */
        if (!myDeposits.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisDepositCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisDepositCategoryBucket myTotals = myDeposits.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisCashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisCashCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisCashCategoryBucket myTotals = myCash.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            final MoneyWiseXAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
            final MoneyWiseXAnalysisSecurityValues myValues = myTotals.getValues();
            final OceanusMoney myValuation = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);

            /* Format the Portfolios Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, MoneyWiseBasicDataType.PORTFOLIO.getListName());
            theBuilder.makeTotalCell(myTable, myValuation);

            /* Make the portfolio report */
            makePortfolioReport(myTable);

            /* Add to running totals */
            myTotal.addAmount(myValuation);
        }

        /* If we have loans */
        if (!myLoans.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisLoanCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisLoanCategoryBucket myTotals = myLoans.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        }

        /* Build the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotal);

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     *
     * @param pParent   the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseXAnalysisDepositCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseXAnalysisDepositCategoryBucketList myCategories = theAnalysis.getDepositCategories();
        final MoneyWiseDepositCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseDepositCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     *
     * @param pParent   the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseXAnalysisCashCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseXAnalysisCashCategoryBucketList myCategories = theAnalysis.getCashCategories();
        final MoneyWiseCashCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseXAnalysisCashCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseCashCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     *
     * @param pParent   the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseXAnalysisLoanCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseXAnalysisLoanCategoryBucketList myCategories = theAnalysis.getLoanCategories();
        final MoneyWiseLoanCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseLoanCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a portfolio report.
     *
     * @param pParent the table parent
     */
    private void makePortfolioReport(final MetisHTMLTable pParent) {
        /* Access the portfolios */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Skip inactive portfolios */
            if (!myBucket.isActive()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, MoneyWiseBasicDataType.PORTFOLIO.getListName());
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseXAnalysisDepositCategoryBucket mySourceBucket) {
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisCashCategoryBucket mySourceBucket) {
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisLoanCategoryBucket mySourceBucket) {
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisPortfolioBucket mySourceBucket) {
            return createDelayedPortfolio(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed category table.
     *
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedDeposit(final MetisHTMLTable pParent,
                                                final MoneyWiseXAnalysisDepositCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseXAnalysisDepositBucketList myAccounts = theAnalysis.getDeposits();
        final MoneyWiseDepositCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        theBuilder.makeTitleCell(myTable, TEXT_RATE);
        theBuilder.makeTitleCell(myTable, TEXT_MATURITY);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Deposit Buckets */
        final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = myAccounts.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getRateValue(MoneyWiseXAnalysisAccountAttr.DEPOSITRATE));
            theBuilder.makeValueCell(myTable, myValues.getDateValue(MoneyWiseXAnalysisAccountAttr.MATURITY));

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     *
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedCash(final MetisHTMLTable pParent,
                                             final MoneyWiseXAnalysisCashCategoryBucket pSource) {
        /* Access the category and class */
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        final MoneyWiseCashCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Cash Buckets */
        final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     *
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedLoan(final MetisHTMLTable pParent,
                                             final MoneyWiseXAnalysisLoanCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseXAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final MoneyWiseLoanCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Loan Buckets */
        final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed portfolio table.
     *
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedPortfolio(final MetisHTMLTable pParent,
                                                  final MoneyWiseXAnalysisPortfolioBucket pSource) {
        /* Access the securities */
        final MoneyWiseXAnalysisPortfolioCashBucket myCash = pSource.getPortfolioCash();
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pSource.getSecurities();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ASSET);
        theBuilder.makeTitleCell(myTable, TEXT_UNITS);
        theBuilder.makeTitleCell(myTable, TEXT_PRICE);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* If the portfolio cash is active */
        if (myCash.isActive()) {
            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myCash.getValues();

            /* Access bucket name */
            final String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, MoneyWiseXReportBalanceSheet.TEXT_CASH);
            theBuilder.makeValueCell(myTable);
            theBuilder.makeValueCell(myTable);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myCash.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* Skip inactive securities */
            if (!myBucket.isActive()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS));
            theBuilder.makeValueCell(myTable, myValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE));

            /* Handle foreign securities */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseXAnalysisFilter<?, ?> processFilter(final Object pSource) {
        /* If this is a DepositBucket */
        if (pSource instanceof MoneyWiseXAnalysisDepositBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisDepositFilter(mySource);
        }
        /* If this is a CashBucket */
        if (pSource instanceof MoneyWiseXAnalysisCashBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisCashFilter(mySource);
        }
        /* If this is a LoanBucket */
        if (pSource instanceof MoneyWiseXAnalysisLoanBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisLoanFilter(mySource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseXAnalysisSecurityBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisSecurityFilter(mySource);
        }
        /* If this is a PortfolioBucket */
        if (pSource instanceof MoneyWiseXAnalysisPortfolioBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisPortfolioCashFilter(mySource);
        }
        return null;
    }
}
