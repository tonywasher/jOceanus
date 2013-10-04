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
package net.sourceforge.jOceanus.jMoneyWise.reports;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.EventFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * NetWorth report builder.
 */
public class NetWorth
        extends BasicReport<AccountCategoryBucket, AccountBucket> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(NetWorth.class.getName());

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
        StringBuilder myBuffer = new StringBuilder();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Net Worth Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange().getEnd()));
        theBuilder.makeTitle(myBody, myBuffer.toString());

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);

        /* Loop through the SubTotal Buckets */
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (myBucket.getCategoryType() != CategoryType.SubTotal) {
                continue;
            }

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Add the category report */
            makeCategoryReport(myTable, myBucket);
        }

        /* Build the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Valuation));

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

            /* Skip irrelevant records */
            if (myBucket.getIntegerAttribute(AccountAttribute.Children) == 0) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the category and class */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategoryBucket mySource = pTable.getSource();
        AccountCategory myCategory = mySource.getAccountCategory();
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Create a new table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pTable.getParent());

        /* Build the headers */
        if (myClass.hasUnits()) {
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, TEXT_ASSET);
            theBuilder.makeTitleCell(myTable, TEXT_UNITS);
            theBuilder.makeTitleCell(myTable, TEXT_PRICE);
            theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        } else if (!myClass.isLoan()) {
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

            /* Skip irrelevant records */
            if (myCategory.getCategoryTypeClass().hasUnits()
                && !myBucket.isRelevant()) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            if (myClass.hasUnits()) {
                theBuilder.makeValueCell(myTable, myBucket.getUnitsAttribute(AccountAttribute.Units));
                theBuilder.makeValueCell(myTable, myBucket.getPriceAttribute(AccountAttribute.Price));
            } else if (!myClass.isLoan()) {
                theBuilder.makeValueCell(myTable, myBucket.getRateAttribute(AccountAttribute.Rate));
                theBuilder.makeValueCell(myTable, myBucket.getDateAttribute(AccountAttribute.Maturity));
            }
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected void processFilter(final AccountBucket pSource) {
        /* Create the new filter */
        EventFilter myFilter = new EventFilter();
        myFilter.setFilter(pSource);
    }
}
