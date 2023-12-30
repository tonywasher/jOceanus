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
package net.sourceforge.joceanus.jmoneywise.atlas.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisAccountBucket.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket.MoneyWiseAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket.MoneyWiseAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisCashFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisDepositFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisLoanFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * BalanceSheet report builder.
 */
public class MoneyWiseReportBalanceSheet
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.BALANCESHEET_TITLE.getValue();

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
    private MoneyWiseAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportBalanceSheet(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        final MoneyWiseAnalysisDepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        final MoneyWiseAnalysisCashCategoryBucketList myCash = theAnalysis.getCashCategories();
        final MoneyWiseAnalysisLoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final TethysDateRange myDateRange = theAnalysis.getDateRange();

        /* Create the totals */
        final TethysMoney myTotal = new TethysMoney();
        final TethysMoney myBase = new TethysMoney();
        final TethysMoney myDelta = new TethysMoney();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDateRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getEnd()));
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getStart()));
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_PROFIT);

        /* If we have deposits */
        if (!myDeposits.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseAnalysisDepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisDepositCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeDepositCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisDepositCategoryBucket myTotals = myDeposits.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisCashCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeCashCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisCashCategoryBucket myTotals = myCash.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            final MoneyWiseAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
            final MoneyWiseAnalysisSecurityValues myValues = myTotals.getValues();
            final MoneyWiseAnalysisSecurityValues myBaseValues = myTotals.getBaseValues();

            /* Access interesting values */
            final TethysMoney myValuation = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
            final TethysMoney myBaseValuation = myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
            final TethysMoney myDeltaValuation = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);

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
            final Iterator<MoneyWiseAnalysisLoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisLoanCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
                final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

                /* Add the category report */
                makeLoanCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisLoanCategoryBucket myTotals = myLoans.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
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
                                           final MoneyWiseAnalysisDepositCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisDepositCategoryBucketList myCategories = theAnalysis.getDepositCategories();
        final MoneyWiseDepositCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisDepositCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisDepositCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final MoneyWiseDepositCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
                                        final MoneyWiseAnalysisCashCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisCashCategoryBucketList myCategories = theAnalysis.getCashCategories();
        final MoneyWiseCashCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final MoneyWiseCashCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
                                        final MoneyWiseAnalysisLoanCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisLoanCategoryBucketList myCategories = theAnalysis.getLoanCategories();
        final MoneyWiseLoanCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisLoanCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisLoanCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final MoneyWiseLoanCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA));

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
        if (mySource instanceof MoneyWiseAnalysisDepositCategoryBucket) {
            final MoneyWiseAnalysisDepositCategoryBucket mySourceBucket = (MoneyWiseAnalysisDepositCategoryBucket) mySource;
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisCashCategoryBucket) {
            final MoneyWiseAnalysisCashCategoryBucket mySourceBucket = (MoneyWiseAnalysisCashCategoryBucket) mySource;
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisLoanCategoryBucket) {
            final MoneyWiseAnalysisLoanCategoryBucket mySourceBucket = (MoneyWiseAnalysisLoanCategoryBucket) mySource;
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisPortfolioBucket) {
            final MoneyWiseAnalysisPortfolioBucket mySourceBucket = (MoneyWiseAnalysisPortfolioBucket) mySource;
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
                                                final MoneyWiseAnalysisDepositCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        final MoneyWiseDepositCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Deposit Buckets */
        final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisDepositBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
                                             final MoneyWiseAnalysisCashCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisCashBucketList myCash = theAnalysis.getCash();
        final MoneyWiseCashCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Cash Buckets */
        final Iterator<MoneyWiseAnalysisCashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
                                             final MoneyWiseAnalysisLoanCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final MoneyWiseLoanCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Loan Buckets */
        final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisLoanBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

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
                                                  final MoneyWiseAnalysisPortfolioBucket pSource) {
        /* Access the securities */
        final MoneyWiseAnalysisPortfolioCashBucket myCash = pSource.getPortfolioCash();
        final MoneyWiseAnalysisSecurityBucketList mySecurities = pSource.getSecurities();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* If the portfolio cash is not idle */
        if (!myCash.isIdle()) {
            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myCash.getValues();
            final MoneyWiseAnalysisAccountValues myBaseValues = myCash.getBaseValues();

            /* Access bucket name */
            final String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, TEXT_CASH);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myCash.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseAnalysisFilter<?, ?> processFilter(final Object pSource) {
        /* If this is a DepositBucket */
        if (pSource instanceof MoneyWiseAnalysisDepositBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisDepositFilter((MoneyWiseAnalysisDepositBucket) pSource);
        }
        /* If this is a CashBucket */
        if (pSource instanceof MoneyWiseAnalysisCashBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisCashFilter((MoneyWiseAnalysisCashBucket) pSource);
        }
        /* If this is a LoanBucket */
        if (pSource instanceof MoneyWiseAnalysisLoanBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisLoanFilter((MoneyWiseAnalysisLoanBucket) pSource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisSecurityFilter((MoneyWiseAnalysisSecurityBucket) pSource);
        }
        /* If this is a PortfolioBucket */
        if (pSource instanceof MoneyWiseAnalysisPortfolioBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisPortfolioCashFilter((MoneyWiseAnalysisPortfolioBucket) pSource);
        }
        return null;
    }
}
