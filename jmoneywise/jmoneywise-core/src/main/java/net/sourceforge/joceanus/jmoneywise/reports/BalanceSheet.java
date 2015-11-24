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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.dateday.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BalanceSheet report builder.
 */
public class BalanceSheet
        extends BasicReport {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = ReportResource.BALANCESHEET_TITLE.getValue();

    /**
     * The Portfolio cash account name.
     */
    protected static final String TEXT_CASH = MoneyWiseDataTypeResource.CASH_NAME.getValue();

    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected BalanceSheet(final ReportManager pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        DepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        CashCategoryBucketList myCash = theAnalysis.getCashCategories();
        LoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        TethysDateRange myDateRange = theAnalysis.getDateRange();

        /* Create the totals */
        TethysMoney myTotal = new TethysMoney();
        TethysMoney myBase = new TethysMoney();
        TethysMoney myDelta = new TethysMoney();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDateRange));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getEnd()));
        theBuilder.makeTitleCell(myTable, theFormatter.formatObject(myDateRange.getStart()));
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_PROFIT);

        /* If we have deposits */
        if (!myDeposits.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            Iterator<DepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                DepositCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                AccountValues myValues = myBucket.getValues();
                AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            DepositCategoryBucket myTotals = myDeposits.getTotals();
            AccountValues myValues = myTotals.getValues();
            AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            Iterator<CashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                CashCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                AccountValues myValues = myBucket.getValues();
                AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            CashCategoryBucket myTotals = myCash.getTotals();
            AccountValues myValues = myTotals.getValues();
            AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            PortfolioBucket myTotals = myPortfolios.getTotals();
            SecurityValues myValues = myTotals.getValues();
            SecurityValues myBaseValues = myTotals.getBaseValues();

            /* Access interesting values */
            TethysMoney myValuation = myValues.getMoneyValue(SecurityAttribute.VALUATION);
            TethysMoney myBaseValuation = myBaseValues.getMoneyValue(SecurityAttribute.VALUATION);
            TethysMoney myDeltaValuation = myValues.getMoneyValue(SecurityAttribute.VALUEDELTA);

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
            Iterator<LoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                LoanCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                if (!myBucket.getAccountCategory().isCategoryClass(LoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                AccountValues myValues = myBucket.getValues();
                AccountValues myBaseValues = myBucket.getBaseValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            LoanCategoryBucket myTotals = myLoans.getTotals();
            AccountValues myValues = myTotals.getValues();
            AccountValues myBaseValues = myTotals.getBaseValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
            myBase.addAmount(myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            myDelta.addAmount(myValues.getMoneyValue(AccountAttribute.VALUEDELTA));
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_TOTAL);
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
    private void makeCategoryReport(final HTMLTable pParent,
                                    final DepositCategoryBucket pCategory) {
        /* Access the category */
        DepositCategoryBucketList myCategories = theAnalysis.getDepositCategories();
        DepositCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        Iterator<DepositCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            DepositCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            DepositCategory myCurr = myBucket.getAccountCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

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
    private void makeCategoryReport(final HTMLTable pParent,
                                    final CashCategoryBucket pCategory) {
        /* Access the category */
        CashCategoryBucketList myCategories = theAnalysis.getCashCategories();
        CashCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        Iterator<CashCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            CashCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            CashCategory myCurr = myBucket.getAccountCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

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
    private void makeCategoryReport(final HTMLTable pParent,
                                    final LoanCategoryBucket pCategory) {
        /* Access the category */
        LoanCategoryBucketList myCategories = theAnalysis.getLoanCategories();
        LoanCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        Iterator<LoanCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            LoanCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            LoanCategory myCurr = myBucket.getAccountCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

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
    private void makePortfolioReport(final HTMLTable pParent) {
        /* Access the portfolios */
        PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Portfolio Buckets */
        Iterator<PortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();
            SecurityValues myBaseValues = myBucket.getBaseValues();

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
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        Object mySource = pTable.getSource();
        if (mySource instanceof DepositCategoryBucket) {
            DepositCategoryBucket mySourceBucket = (DepositCategoryBucket) mySource;
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof CashCategoryBucket) {
            CashCategoryBucket mySourceBucket = (CashCategoryBucket) mySource;
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof LoanCategoryBucket) {
            LoanCategoryBucket mySourceBucket = (LoanCategoryBucket) mySource;
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof PortfolioBucket) {
            PortfolioBucket mySourceBucket = (PortfolioBucket) mySource;
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
    private HTMLTable createDelayedDeposit(final HTMLTable pParent,
                                           final DepositCategoryBucket pSource) {
        /* Access the category */
        DepositBucketList myDeposits = theAnalysis.getDeposits();
        DepositCategory myCategory = pSource.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Deposit Buckets */
        Iterator<DepositBucket> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            DepositBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
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
    private HTMLTable createDelayedCash(final HTMLTable pParent,
                                        final CashCategoryBucket pSource) {
        /* Access the category */
        CashBucketList myCash = theAnalysis.getCash();
        CashCategory myCategory = pSource.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Cash Buckets */
        Iterator<CashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            CashBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
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
    private HTMLTable createDelayedLoan(final HTMLTable pParent,
                                        final LoanCategoryBucket pSource) {
        /* Access the category */
        LoanBucketList myLoans = theAnalysis.getLoans();
        LoanCategory myCategory = pSource.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Loan Buckets */
        Iterator<LoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            LoanBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
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
    private HTMLTable createDelayedPortfolio(final HTMLTable pParent,
                                             final PortfolioBucket pSource) {
        /* Access the securities */
        PortfolioCashBucket myCash = pSource.getPortfolioCash();
        SecurityBucketList mySecurities = pSource.getSecurities();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* If the portfolio cash is not idle */
        if (!myCash.isIdle()) {
            /* Access values */
            AccountValues myValues = myCash.getValues();
            AccountValues myBaseValues = myCash.getBaseValues();

            /* Access bucket name */
            String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, TEXT_CASH);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();
            SecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUEDELTA));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected AnalysisFilter<?, ?> processFilter(final Object pSource) {
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
