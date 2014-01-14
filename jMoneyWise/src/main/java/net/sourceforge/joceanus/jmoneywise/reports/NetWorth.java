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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.AccountFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * NetWorth report builder.
 */
public class NetWorth
        extends BasicReport {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(NetWorth.class.getName());

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = NLS_BUNDLE.getString("ReportTitle");

    /**
     * The Asset text.
     */
    private static final String TEXT_ASSET = NLS_BUNDLE.getString("ReportAsset");

    /**
     * The Units text.
     */
    private static final String TEXT_UNITS = NLS_BUNDLE.getString("ReportUnits");

    /**
     * The Price text.
     */
    private static final String TEXT_PRICE = NLS_BUNDLE.getString("ReportPrice");

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = NLS_BUNDLE.getString("ReportValue");

    /**
     * The Account text.
     */
    private static final String TEXT_ACCOUNT = NLS_BUNDLE.getString("ReportAccount");

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = NLS_BUNDLE.getString("ReportRate");

    /**
     * The Maturity text.
     */
    private static final String TEXT_MATURITY = NLS_BUNDLE.getString("ReportMaturity");

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
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotals();
        JDateDay myDate = theAnalysis.getDateRange().getEnd();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);

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

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

            /* If this is the portfolio category */
            if (myBucket.getAccountCategory().isCategoryClass(AccountCategoryClass.PORTFOLIO)) {
                /* Add the portfolio report */
                makePortfolioReport(myTable, myBucket);
            } else {
                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }
        }

        /* Access values */
        AccountValues myValues = myTotals.getValues();

        /* Build the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(AccountAttribute.VALUATION));

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

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));

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
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Create a new table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        if (!myClass.isLoan()) {
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
            theBuilder.makeTitleCell(myTable, TEXT_RATE);
            theBuilder.makeTitleCell(myTable, TEXT_MATURITY);
            theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        }

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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            if (!myClass.isLoan()) {
                theBuilder.makeValueCell(myTable, myValues.getRateValue(AccountAttribute.RATE));
                theBuilder.makeValueCell(myTable, myValues.getDateValue(AccountAttribute.MATURITY));
            }
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
        /* Access the securities and portfolio */
        SecurityBucketList mySecurities = theAnalysis.getSecurities();
        Account myPortfolio = pSource.getPortfolio();

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

            /* Skip record if incorrect portfolio */
            if (!Difference.isEqual(myBucket.getPortfolio(), myPortfolio)) {
                continue;
            }

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
        /* If this is an AccountBucket */
        if (pSource instanceof AccountBucket) {
            /* Create the new filter */
            return new AccountFilter((AccountBucket) pSource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof SecurityBucket) {
            /* Create the new filter */
            return new SecurityFilter((SecurityBucket) pSource);
        }
        return null;
    }
}
