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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
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
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * NetWorth report builder.
 */
public class NetWorth
        extends BasicReport {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = ReportResource.NETWORTH_TITLE.getValue();

    /**
     * The Asset text.
     */
    private static final String TEXT_ASSET = ReportResource.NETWORTH_ASSET.getValue();

    /**
     * The Units text.
     */
    private static final String TEXT_UNITS = AnalysisResource.SECURITYATTR_UNITS.getValue();

    /**
     * The Price text.
     */
    private static final String TEXT_PRICE = MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = AnalysisResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Account text.
     */
    private static final String TEXT_ACCOUNT = AnalysisResource.BUCKET_ACCOUNT.getValue();

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE.getValue();

    /**
     * The Maturity text.
     */
    private static final String TEXT_MATURITY = AnalysisResource.ACCOUNTATTR_MATURITY.getValue();

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
    protected NetWorth(final ReportManager pManager) {
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
        JDateDay myDate = theAnalysis.getDateRange().getEnd();

        /* Create the totals */
        JMoney myTotal = new JMoney();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);

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

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            DepositCategoryBucket myTotals = myDeposits.getTotals();
            AccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
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

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            CashCategoryBucket myTotals = myCash.getTotals();
            AccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            PortfolioBucket myTotals = myPortfolios.getTotals();
            SecurityValues myValues = myTotals.getValues();
            JMoney myValuation = myValues.getMoneyValue(SecurityAttribute.VALUATION);

            /* Format the Portfolios Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, MoneyWiseDataType.PORTFOLIO.getListName());
            theBuilder.makeTotalCell(myTable, myValuation);

            /* Make the portfolio report */
            makePortfolioReport(myTable);

            /* Add to running totals */
            myTotal.addAmount(myValuation);
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

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            LoanCategoryBucket myTotals = myLoans.getTotals();
            AccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(AccountAttribute.VALUATION));
        }

        /* Build the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotal);

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

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

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

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

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

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

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

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));

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
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private HTMLTable createDelayedDeposit(final HTMLTable pParent,
                                           final DepositCategoryBucket pSource) {
        /* Access the category */
        DepositBucketList myAccounts = theAnalysis.getDeposits();
        DepositCategory myCategory = pSource.getAccountCategory();

        /* Create a new table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        theBuilder.makeTitleCell(myTable, TEXT_RATE);
        theBuilder.makeTitleCell(myTable, TEXT_MATURITY);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Deposit Buckets */
        Iterator<DepositBucket> myIterator = myAccounts.iterator();
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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getRateValue(AccountAttribute.RATE));
            theBuilder.makeValueCell(myTable, myValues.getDateValue(AccountAttribute.MATURITY));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private HTMLTable createDelayedCash(final HTMLTable pParent,
                                        final CashCategoryBucket pSource) {
        /* Access the category and class */
        CashBucketList myCash = theAnalysis.getCash();
        CashCategory myCategory = pSource.getAccountCategory();

        /* Create a new table */
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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private HTMLTable createDelayedLoan(final HTMLTable pParent,
                                        final LoanCategoryBucket pSource) {
        /* Access the category */
        LoanBucketList myLoans = theAnalysis.getLoans();
        LoanCategory myCategory = pSource.getAccountCategory();

        /* Create a new table */
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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

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
        SecurityBucketList mySecurities = pSource.getSecurities();

        /* Create a new table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ASSET);
        theBuilder.makeTitleCell(myTable, TEXT_UNITS);
        theBuilder.makeTitleCell(myTable, TEXT_PRICE);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Skip inactive securities */
            if (!myBucket.isActive()) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getUnitsValue(SecurityAttribute.UNITS));
            theBuilder.makeValueCell(myTable, myValues.getPriceValue(SecurityAttribute.PRICE));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected AnalysisFilter<?> processFilter(final Object pSource) {
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
        return null;
    }
}
