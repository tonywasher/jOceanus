/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BalanceSheet report builder.
 */
public class BalanceSheetAlt
        extends BasicReportAlt {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(BalanceSheet.class.getName());

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = NLS_BUNDLE.getString("ReportTitle");

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
    protected BalanceSheetAlt(final ReportManagerAlt pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        JDateDayRange myDateRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotals();

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

        /* Loop through the SubTotal Buckets */
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (!myBucket.getCategoryType().isSubTotal()) {
                continue;
            }

            /* Access values */
            AccountValues myValues = myBucket.getValues();
            AccountValues myBaseValues = myBucket.getBaseValues();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Delta));

            /* If this is the portfolio category */
            if (myBucket.getAccountCategory().isCategoryClass(AccountCategoryClass.Portfolio)) {
                /* Add the portfolio report */
                makePortfolioReport(myTable, myBucket);
            } else {
                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }
        }

        /* Access values */
        AccountValues myValues = myTotals.getValues();
        AccountValues myBaseValues = myTotals.getBaseValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Delta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final HTMLTable pParent,
                                    final AccountCategoryBucket pCategory) {
        /* Access the category */
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        AccountCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            AccountCategory myCurr = myBucket.getAccountCategory();
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
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.Delta));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a portfolio report.
     * @param pParent the table parent
     * @param pCategory the portfolio category bucket
     */
    private void makePortfolioReport(final HTMLTable pParent,
                                     final AccountCategoryBucket pCategory) {
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
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.Delta));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        Object mySource = pTable.getSource();
        if (mySource instanceof AccountCategoryBucket) {
            AccountCategoryBucket mySourceBucket = (AccountCategoryBucket) mySource;
            return createDelayedCategory(pTable.getParent(), mySourceBucket);
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
    private HTMLTable createDelayedCategory(final HTMLTable pParent,
                                            final AccountCategoryBucket pSource) {
        /* Access the category and class */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategory myCategory = pSource.getAccountCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Account Buckets */
        Iterator<AccountBucket> myIterator = myAccounts.iterator();
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory(), myCategory)) {
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
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(AccountAttribute.Delta));

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
        /* Access the securities and portfolio */
        SecurityBucketList mySecurities = theAnalysis.getSecurities();
        Account myPortfolio = pSource.getPortfolio();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Skip record if incorrect portfolio */
            if (!Difference.isEqual(myBucket.getPortfolio(), myPortfolio)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();
            SecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Delta));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected void processFilter(final Object pSource) {
        /* Create the new filter */
        // EventFilter myFilter = new EventFilter(theAnalysis.getData());
        // myFilter.setFilter(pSource);
    }
}
