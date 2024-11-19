/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisCashFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisDepositFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisLoanFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisSecurityFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * BalanceSheet report builder.
 */
public class MoneyWiseXReportBalanceSheet
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.BALANCESHEET_TITLE.getValue();

    /**
     * The Portfolio cash account name.
     */
    protected static final String TEXT_CASH = MoneyWiseBasicResource.CASH_NAME.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    MoneyWiseXReportBalanceSheet(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
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
        final OceanusDateRange myDateRange = theAnalysis.getDateRange();

        /* Create the totals */
        final OceanusMoney myTotal = new OceanusMoney();
        final OceanusMoney myBase = new OceanusMoney();
        final OceanusMoney myDelta = new OceanusMoney();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDateRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getEnd()));
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getStart()));
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_PROFIT);

        /* If we have deposits */
        if (!myDeposits.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisDepositCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeDepositCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisDepositCategoryBucket myTotals = myDeposits.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisCashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisCashCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeCashCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisCashCategoryBucket myTotals = myCash.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            final MoneyWiseXAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
            final MoneyWiseXAnalysisSecurityValues myValues = myTotals.getValues();
            final MoneyWiseXAnalysisSecurityValues myBaseValues = myTotals.getBaseValues();

            /* Access interesting values */
            final OceanusMoney myValuation = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
            final OceanusMoney myBaseValuation = myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
            final OceanusMoney myDeltaValuation = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA);

            /* Format the Portfolios Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, MoneyWiseBasicDataType.PORTFOLIO.getListName());
            theBuilder.makeTotalCell(myTable, myValuation);
            theBuilder.makeTotalCell(myTable, myBaseValuation);
            theBuilder.makeTotalCell(myTable, myDeltaValuation);

            /* Make the portfolio report */
            makePortfolioReport(myTable);

            /* Add to running totals */
            myTotal.addAmount(myValuation);
            myBase.addAmount(myBaseValuation);
            myDelta.addAmount(myDeltaValuation);
        }

        /* If we have loans */
        if (!myLoans.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisLoanCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeLoanCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseXAnalysisLoanCategoryBucket myTotals = myLoans.getTotals();
            final MoneyWiseXAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotal);
        theBuilder.makeTotalCell(myTable, myBase);
        theBuilder.makeTotalCell(myTable, myDelta);

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeDepositCategoryReport(final MetisHTMLTable pParent,
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

            /* Skip record if incorrect category */
            final MoneyWiseDepositCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeCashCategoryReport(final MetisHTMLTable pParent,
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

            /* Skip record if incorrect category */
            final MoneyWiseCashCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeLoanCategoryReport(final MetisHTMLTable pParent,
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

            /* Skip record if incorrect category */
            final MoneyWiseLoanCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a portfolio report.
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

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA));

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
        if (mySource instanceof MoneyWiseXAnalysisDepositCategoryBucket) {
            final MoneyWiseXAnalysisDepositCategoryBucket mySourceBucket = (MoneyWiseXAnalysisDepositCategoryBucket) mySource;
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisCashCategoryBucket) {
            final MoneyWiseXAnalysisCashCategoryBucket mySourceBucket = (MoneyWiseXAnalysisCashCategoryBucket) mySource;
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisLoanCategoryBucket) {
            final MoneyWiseXAnalysisLoanCategoryBucket mySourceBucket = (MoneyWiseXAnalysisLoanCategoryBucket) mySource;
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseXAnalysisPortfolioBucket) {
            final MoneyWiseXAnalysisPortfolioBucket mySourceBucket = (MoneyWiseXAnalysisPortfolioBucket) mySource;
            return createDelayedPortfolio(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed deposit category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedDeposit(final MetisHTMLTable pParent,
                                                final MoneyWiseXAnalysisDepositCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseXAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        final MoneyWiseDepositCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Deposit Buckets */
        final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed cash category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedCash(final MetisHTMLTable pParent,
                                             final MoneyWiseXAnalysisCashCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        final MoneyWiseCashCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Cash Buckets */
        final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed loan category table.
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

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Loan Buckets */
        final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed portfolio table.
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

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* If the portfolio cash is not idle */
        if (!myCash.isIdle()) {
            /* Access values */
            final MoneyWiseXAnalysisAccountValues myValues = myCash.getValues();
            final MoneyWiseXAnalysisAccountValues myBaseValues = myCash.getBaseValues();

            /* Access bucket name */
            final String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, TEXT_CASH);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myCash.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseXAnalysisFilter<?, ?> processFilter(final Object pSource) {
        /* If this is a DepositBucket */
        if (pSource instanceof MoneyWiseXAnalysisDepositBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisDepositFilter((MoneyWiseXAnalysisDepositBucket) pSource);
        }
        /* If this is a CashBucket */
        if (pSource instanceof MoneyWiseXAnalysisCashBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisCashFilter((MoneyWiseXAnalysisCashBucket) pSource);
        }
        /* If this is a LoanBucket */
        if (pSource instanceof MoneyWiseXAnalysisLoanBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisLoanFilter((MoneyWiseXAnalysisLoanBucket) pSource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseXAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisSecurityFilter((MoneyWiseXAnalysisSecurityBucket) pSource);
        }
        /* If this is a PortfolioBucket */
        if (pSource instanceof MoneyWiseXAnalysisPortfolioBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisPortfolioCashFilter((MoneyWiseXAnalysisPortfolioBucket) pSource);
        }
        return null;
    }
}
