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
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.views.AccountBucket.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.views.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.views.AccountCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.joceanus.jmoneywise.views.Analysis;
import net.sourceforge.joceanus.jmoneywise.views.EventFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BalanceSheet report builder.
 */
public class BalanceSheet
        extends BasicReport<AccountCategoryBucket, AccountBucket> {
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
    protected BalanceSheet(final ReportManager pManager) {
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
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

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
            if (myBucket.getCategoryType() != CategoryType.SubTotal) {
                continue;
            }

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

            /* Add the category report */
            makeCategoryReport(myTable, myBucket);
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myTable, myTotals.getBaseMoneyAttribute(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.ValueDelta));

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
            theBuilder.makeTotalCell(myTable, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

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

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pTable.getParent());

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
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected void processFilter(final AccountBucket pSource) {
        /* Create the new filter */
        EventFilter myFilter = new EventFilter(theAnalysis.getData());
        myFilter.setFilter(pSource);
    }
}
