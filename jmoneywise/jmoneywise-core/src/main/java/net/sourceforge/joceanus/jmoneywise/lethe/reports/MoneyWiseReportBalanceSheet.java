/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * BalanceSheet report builder.
 */
public class MoneyWiseReportBalanceSheet
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.BALANCESHEET_TITLE.getValue();

    /**
     * The Portfolio cash account name.
     */
    protected static final String TEXT_CASH = MoneyWiseDataTypeResource.CASH_NAME.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportBalanceSheet(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        final DepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        final CashCategoryBucketList myCash = theAnalysis.getCashCategories();
        final LoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
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
            final Iterator<DepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                final DepositCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final AccountValues myValues = myBucket.getValues();
                final AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeDepositCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final DepositCategoryBucket myTotals = myDeposits.getTotals();
            final AccountValues myValues = myTotals.getValues();
            final AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<CashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                final CashCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final AccountValues myValues = myBucket.getValues();
                final AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeCashCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final CashCategoryBucket myTotals = myCash.getTotals();
            final AccountValues myValues = myTotals.getValues();
            final AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            final PortfolioBucket myTotals = myPortfolios.getTotals();
            final SecurityValues myValues = myTotals.getValues();
            final SecurityValues myBaseValues = myTotals.getBaseValues();

            /* Access interesting values */
            final TethysMoney myValuation = myValues.getMoneyValue(SecurityAttribute.VALUATION);
            final TethysMoney myBaseValuation = myBaseValues.getMoneyValue(SecurityAttribute.VALUATION);
            final TethysMoney myDeltaValuation = myValues.getMoneyValue(SecurityAttribute.VALUEDELTA);

            /* Format the Portfolios Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, MoneyWiseDataType.PORTFOLIO.getListName());
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
            final Iterator<LoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                final LoanCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(LoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final AccountValues myValues = myBucket.getValues();
                final AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeLoanCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final LoanCategoryBucket myTotals = myLoans.getTotals();
            final AccountValues myValues = myTotals.getValues();
            final AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
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
                                           final DepositCategoryBucket pCategory) {
        /* Access the category */
        final DepositCategoryBucketList myCategories = theAnalysis.getDepositCategories();
        final DepositCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<DepositCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final DepositCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final DepositCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
                                        final CashCategoryBucket pCategory) {
        /* Access the category */
        final CashCategoryBucketList myCategories = theAnalysis.getCashCategories();
        final CashCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<CashCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final CashCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final CashCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
                                        final LoanCategoryBucket pCategory) {
        /* Access the category */
        final LoanCategoryBucketList myCategories = theAnalysis.getLoanCategories();
        final LoanCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<LoanCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final LoanCategory myCurr = myBucket.getAccountCategory();
            if (!MetisDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Portfolio Buckets */
        final Iterator<PortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final PortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final SecurityValues myValues = myBucket.getValues();
            final SecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUEDELTA));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, MoneyWiseDataType.PORTFOLIO.getListName());
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof DepositCategoryBucket) {
            final DepositCategoryBucket mySourceBucket = (DepositCategoryBucket) mySource;
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof CashCategoryBucket) {
            final CashCategoryBucket mySourceBucket = (CashCategoryBucket) mySource;
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof LoanCategoryBucket) {
            final LoanCategoryBucket mySourceBucket = (LoanCategoryBucket) mySource;
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof PortfolioBucket) {
            final PortfolioBucket mySourceBucket = (PortfolioBucket) mySource;
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
                                                final DepositCategoryBucket pSource) {
        /* Access the category */
        final DepositBucketList myDeposits = theAnalysis.getDeposits();
        final DepositCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Deposit Buckets */
        final Iterator<DepositBucket> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final DepositBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
                                             final CashCategoryBucket pSource) {
        /* Access the category */
        final CashBucketList myCash = theAnalysis.getCash();
        final CashCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Cash Buckets */
        final Iterator<CashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            final CashBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
                                             final LoanCategoryBucket pSource) {
        /* Access the category */
        final LoanBucketList myLoans = theAnalysis.getLoans();
        final LoanCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Loan Buckets */
        final Iterator<LoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            final LoanBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!MetisDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final AccountValues myValues = myBucket.getValues();
            final AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

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
                                                  final PortfolioBucket pSource) {
        /* Access the securities */
        final PortfolioCashBucket myCash = pSource.getPortfolioCash();
        final SecurityBucketList mySecurities = pSource.getSecurities();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* If the portfolio cash is not idle */
        if (!myCash.isIdle()) {
            /* Access values */
            final AccountValues myValues = myCash.getValues();
            final AccountValues myBaseValues = myCash.getBaseValues();

            /* Access bucket name */
            final String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, TEXT_CASH);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myCash.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        final Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final SecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final SecurityValues myValues = myBucket.getValues();
            final SecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
                    theBuilder.makeStretchedValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
                theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public AnalysisFilter<?, ?> processFilter(final Object pSource) {
        /* If this is a DepositBucket */
        if (pSource instanceof DepositBucket) {
            /* Create the new filter */
            return new DepositFilter((DepositBucket) pSource);
        }
        /* If this is a CashBucket */
        if (pSource instanceof CashBucket) {
            /* Create the new filter */
            return new CashFilter((CashBucket) pSource);
        }
        /* If this is a LoanBucket */
        if (pSource instanceof LoanBucket) {
            /* Create the new filter */
            return new LoanFilter((LoanBucket) pSource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof SecurityBucket) {
            /* Create the new filter */
            return new SecurityFilter((SecurityBucket) pSource);
        }
        /* If this is a PortfolioBucket */
        if (pSource instanceof PortfolioBucket) {
            /* Create the new filter */
            return new PortfolioCashFilter((PortfolioBucket) pSource);
        }
        return null;
    }
}
