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

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * NetWorth report builder.
 */
public class NetWorth
        implements MoneyWiseReport {
    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

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
        /* Store values */
        theManager = pManager;

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
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTotal = theBuilder.startTableBody(myTable);
        myTable = theBuilder.startEmbeddedTable(myTotal, ReportBuilder.TEXT_TOTAL, myColumns, true);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Loop through the SubTotal Buckets */
        boolean isOdd = true;
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (myBucket.getCategoryType() != CategoryType.SubTotal) {
                continue;
            }

            /* Format the Category Total */
            Element myCategory = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the category report */
            makeCategoryReport(myTBody, myColumns, myBucket);
        }

        /* Build the total row */
        Element myRow = theBuilder.startTotalRow(myTotal, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Valuation));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final Element pBody,
                                    final Integer pNumColumns,
                                    final AccountCategoryBucket pCategory) {
        /* Access the category */
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        AccountCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, myCategory.getName(), pNumColumns, true);
        Element myBody = theBuilder.startTableBody(myTable);

        /* Loop through the Category Buckets */
        boolean isOdd = true;
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

            /* Create the SubCategory row */
            Element myRow = (isOdd)
                    ? theBuilder.startSubCategoryRow(myBody, myCurr.getSubCategory(), myBucket.getName())
                    : theBuilder.startAlternateSubCatRow(myBody, myCurr.getSubCategory(), myBucket.getName());
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the sub category report */
            makeSubCategoryReport(myBody, pNumColumns, myBucket);
        }
    }

    /**
     * Build a subCategory report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pSubCategory the subCategory bucket
     */
    private void makeSubCategoryReport(final Element pBody,
                                       final Integer pNumColumns,
                                       final AccountCategoryBucket pSubCategory) {
        /* Access the category and class */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategory myCategory = pSubCategory.getAccountCategory();
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Create a new table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, myCategory.getName(), pNumColumns, false);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow;

        /* Build the headers */
        boolean isOdd = true;
        if (myClass.hasUnits()) {
            myRow = theBuilder.startDetailTitleRow(myHdr, "Asset");
            theBuilder.makeTitleCell(myRow, "Units");
            theBuilder.makeTitleCell(myRow, "Price");
            theBuilder.makeTitleCell(myRow, "Valuation");
            isOdd = false;
        } else if (!myClass.isLoan()) {
            myRow = theBuilder.startDetailTitleRow(myHdr, "Account");
            theBuilder.makeTitleCell(myRow, "Rate");
            theBuilder.makeTitleCell(myRow, "Maturity");
            theBuilder.makeTitleCell(myRow, "Valuation");
            isOdd = false;
        }

        /* Start the body */
        Element myBody = theBuilder.startTableBody(myTable);

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

            /* Create the detail row */
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myBucket.getName());
            if (myClass.hasUnits()) {
                theBuilder.makeValueCell(myRow, myBucket.getUnitsAttribute(AccountAttribute.Units));
                theBuilder.makeValueCell(myRow, myBucket.getPriceAttribute(AccountAttribute.Price));
            } else if (!myClass.isLoan()) {
                theBuilder.makeValueCell(myRow, myBucket.getRateAttribute(AccountAttribute.Rate));
                theBuilder.makeValueCell(myRow, myBucket.getDateAttribute(AccountAttribute.Maturity));
            }
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Valuation));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }
    }
}
