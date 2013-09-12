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
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategorySection;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAnalysisList;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class ReportBuilder {
    /**
     * The Total text.
     */
    private static final String TEXT_TOTAL = "Total";

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = "Profit";

    /**
     * The Income text.
     */
    private static final String TEXT_INCOME = "Income";

    /**
     * The Expense text.
     */
    private static final String TEXT_EXPENSE = "Expense";

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Report formatter.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

    /**
     * Data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pManager the report manager
     * @throws JDataException on error
     */
    public ReportBuilder(final ReportManager pManager) throws JDataException {
        /* Record the details */
        theBuilder = new HTMLBuilder();
        theFormatter = theBuilder.getDataFormatter();
        theManager = pManager;
    }

    /**
     * Build a web document of the net worth report.
     * @param pAnalysis the analysis
     * @return Web document
     */
    public Document getNetWorthReport(final Analysis pAnalysis) {
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
        myTable = theBuilder.startEmbeddedTable(myTotal, TEXT_TOTAL, myColumns, true);
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
            makeNetWorthCategoryReport(myTBody, myColumns, myBucket);
        }

        /* Build the total row */
        Element myRow = theBuilder.startTotalRow(myTotal, TEXT_TOTAL);
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
    private void makeNetWorthCategoryReport(final Element pBody,
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
            makeNetWorthSubCategoryReport(myBody, pNumColumns, myBucket);
        }
    }

    /**
     * Build a subCategory report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pSubCategory the subCategory bucket
     */
    private void makeNetWorthSubCategoryReport(final Element pBody,
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

    /**
     * Build a web document of the TaxYear asset report.
     * @param pAnalysis the analysis
     * @return Web document
     */
    public Document getAssetReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myBuffer = new StringBuilder();
        JDateDayRange myDateRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Asset Report for ");
        myBuffer.append(theFormatter.formatObject(myDateRange));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, theFormatter.formatObject(myDateRange.getEnd()));
        myColumns++;
        theBuilder.makeTitleCell(myRow, theFormatter.formatObject(myDateRange.getStart()));
        myColumns++;
        theBuilder.makeTitleCell(myRow, TEXT_PROFIT);
        myColumns++;
        Element myTotal = theBuilder.startTableBody(myTable);
        myTable = theBuilder.startEmbeddedTable(myTotal, TEXT_TOTAL, myColumns, true);
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
            theBuilder.makeTotalCell(myCategory, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the category report */
            makeAssetCategoryReport(myTBody, myColumns, myBucket);
        }

        /* Format the total */
        myRow = theBuilder.startTotalRow(myTotal, TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myRow, myTotals.getBaseMoneyAttribute(AccountAttribute.Valuation));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.ValueDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pCategory the category bucket
     */
    private void makeAssetCategoryReport(final Element pBody,
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
            theBuilder.makeTotalCell(myRow, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the sub category report */
            makeAssetSubCategoryReport(myBody, pNumColumns, myBucket);
        }
    }

    /**
     * Build a subCategory report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pSubCategory the subCategory bucket
     */
    private void makeAssetSubCategoryReport(final Element pBody,
                                            final Integer pNumColumns,
                                            final AccountCategoryBucket pSubCategory) {
        /* Access the category and class */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategory myCategory = pSubCategory.getAccountCategory();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, myCategory.getName(), pNumColumns, false);
        Element myBody = theBuilder.startTableBody(myTable);

        /* Loop through the Account Buckets */
        boolean isOdd = true;
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
            Element myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myRow, myBucket.getBaseMoneyAttribute(AccountAttribute.Valuation));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.ValueDelta));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }
    }

    /**
     * Build a web document of the TaxYear category report.
     * @param pAnalysis the analysis
     * @return Web document
     */
    public Document getCategoryReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Obtain the totals bucket */
        EventCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Event Category Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, TEXT_INCOME);
        myColumns++;
        theBuilder.makeTitleCell(myRow, TEXT_EXPENSE);
        myColumns++;
        theBuilder.makeTitleCell(myRow, TEXT_PROFIT);
        myColumns++;
        Element myTotal = theBuilder.startTableBody(myTable);
        myTable = theBuilder.startEmbeddedTable(myTotal, TEXT_TOTAL, myColumns, true);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Loop through the SubTotal Buckets */
        boolean isOdd = true;
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (myBucket.getEventCategoryType().getCategoryClass() != EventCategoryClass.Category) {
                continue;
            }

            /* Format the Category Total */
            Element myCategory = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the category report */
            makeEventCategoryReport(myTBody, myColumns, myBucket);
        }

        /* Format the total */
        myRow = theBuilder.startTotalRow(myTotal, TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.Income));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.Expense));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pCategory the category bucket
     */
    private void makeEventCategoryReport(final Element pBody,
                                         final Integer pNumColumns,
                                         final EventCategoryBucket pCategory) {
        /* Access the category */
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        EventCategory myCategory = pCategory.getEventCategory();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, myCategory.getName(), pNumColumns, false);
        Element myBody = theBuilder.startTableBody(myTable);

        /* Loop through the Category Buckets */
        boolean isOdd = true;
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            EventCategory myCurr = myBucket.getEventCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Create the SubCategory row */
            Element myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myCurr.getSubCategory(), myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myCurr.getSubCategory(), myBucket.getName());
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }
    }

    /**
     * Build a web output of the tax category report.
     * @param pAnalysis the analysis
     * @return Web output
     */
    public Document getTaxCategoryReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        TaxCategoryBucketList myTax = theAnalysis.getTaxCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Tax Category Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Access the bucket iterator */
        boolean isOdd = true;
        Iterator<TaxCategoryBucket> myTaxIterator = myTax.iterator();

        /* Loop through the Category Summary Buckets */
        while (myTaxIterator.hasNext()) {
            TaxCategoryBucket myBucket = myTaxIterator.next();

            /* Skip the non-summary elements */
            switch (myBucket.getCategorySection()) {
                case CATSUMM:
                case CATTOTAL:
                    /* Access the amount */
                    JMoney myAmount = myBucket.getMoneyAttribute(TaxAttribute.Amount);

                    /* If we have a non-zero value */
                    if (myAmount.isNonZero()) {
                        /* Format the detail */
                        Element myRow = (isOdd)
                                ? theBuilder.startDetailRow(myTBody, myBucket.getName())
                                : theBuilder.startAlternateRow(myTBody, myBucket.getName());
                        theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Amount));

                        /* Record the selection */
                        theManager.setSelectionForId(myBucket.getName(), myBucket);

                        /* Flip row type */
                        isOdd = !isOdd;
                    }
                    break;
                default:
                    break;
            }
        }

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a web output of the income/expense report.
     * @param pAnalysis the analysis
     * @return Web output
     */
    public Document getIncomeReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        AccountBucketList myList = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        JDateDayRange myRange = theAnalysis.getDateRange();
        StringBuilder myBuffer = new StringBuilder();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Income/Expense Report for ");
        myBuffer.append(theFormatter.formatObject(myRange));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, TEXT_INCOME);
        theBuilder.makeTitleCell(myRow, TEXT_EXPENSE);
        theBuilder.makeTitleCell(myRow, TEXT_PROFIT);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Create the bucket iterator */
        boolean isOdd = true;
        Iterator<AccountBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip bucket if this is not a payee account */
            if (myBucket.getCategoryType() != CategoryType.Payee) {
                continue;
            }

            /* Format the detail */
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Income));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Expense));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.IncomeDelta));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the total */
        myRow = theBuilder.startTotalRow(myTBody, TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Income));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Expense));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a web output of the market report.
     * @param pAnalysis the analysis
     * @return the Web output
     */
    public Document getMarketReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Access the totals */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Market Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange().getEnd()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, "Cost");
        theBuilder.makeTitleCell(myRow, "Valuation");
        theBuilder.makeTitleCell(myRow, "Gains");
        theBuilder.makeTitleCell(myRow, "Profit");
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Access the iterator */
        boolean isOdd = true;
        Iterator<AccountBucket> myIterator = myAccounts.iterator();

        /* Loop through the Account Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Only process priced asset types */
            if (myBucket.getCategoryType() != CategoryType.Priced) {
                continue;
            }

            /* Format the Asset */
            myRow = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Cost));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.MarketValue));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Gained));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Profit));

            /* Flip row type */
            isOdd = !isOdd;

            /* Format the detail */
            makeCapitalEventReport(myTBody, myColumns, myBucket);
        }

        /* Create the total row */
        myRow = theBuilder.startTotalRow(myTBody, TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Cost));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.MarketValue));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Gained));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Profit));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a capital event report element.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pAsset the asset to report on
     */
    public void makeCapitalEventReport(final Element pBody,
                                       final Integer pNumColumns,
                                       final AccountBucket pAsset) {
        /* Access the investment analyses */
        InvestmentAnalysisList myList = pAsset.getInvestmentAnalyses();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, pAsset.getName(), pNumColumns, false);

        /* Format the header */
        Element myBody = theBuilder.startTableBody(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myBody, "Date");
        theBuilder.makeTitleCell(myRow, "Category");
        theBuilder.makeTitleCell(myRow, "DeltaUnits");
        theBuilder.makeTitleCell(myRow, "DeltaCost");
        theBuilder.makeTitleCell(myRow, "DeltaGains");
        theBuilder.makeTitleCell(myRow, "Dividend");

        /* Access the iterator */
        Iterator<InvestmentAnalysis> myIterator = myList.iterator();
        boolean isOdd = false;

        /* Loop through the Analyses */
        while (myIterator.hasNext()) {
            InvestmentAnalysis myAnalysis = myIterator.next();

            /* Skip record if this is not based on an event (at present) */
            if (myAnalysis.getEvent() == null) {
                continue;
            }

            /* Format the detail */
            String myDate = theFormatter.formatObject(myAnalysis.getDate());
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myDate)
                    : theBuilder.startAlternateRow(myBody, myDate);
            theBuilder.makeValueCell(myRow, myAnalysis.getCategory());
            theBuilder.makeValueCell(myRow, myAnalysis.getUnitsAttribute(InvestmentAttribute.DeltaUnits));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaCost));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaGains));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaDividend));

            /* Flip row type */
            isOdd = !isOdd;
        }
    }

    /**
     * Build a web output of the taxation report.
     * @param pAnalysis the analysis
     * @param pYear the tax year
     * @return Web output
     */
    public Document getTaxReport(final Analysis pAnalysis,
                                 final TaxYear pYear) {
        /* Store the analysis */
        theAnalysis = pAnalysis;
        StringBuffer myBuffer = new StringBuffer();

        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        TaxCategoryBucket myTax;

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Taxation Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;

        /* Format the header */
        theBuilder.makeSubTitle(myBody, "Taxation Summary");
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr, "Class");
        theBuilder.makeTitleCell(myRow, "Total Income");
        theBuilder.makeTitleCell(myRow, "Taxation Due");
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Tax Summary Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip the non-summary elements */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXSUMM) {
                continue;
            }

            /* Format the line */
            myRow = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Amount));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Taxation));

            /* Format the detail */
            makeTaxReport(myTBody, myColumns, myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Access the Total taxation bucket */
        myTax = myList.getBucket(TaxCategoryClass.TotalTaxationDue);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Taxation));

        /* Access the Tax Paid bucket */
        myTax = myList.getBucket(TaxCategoryClass.TaxPaid);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

        /* Access the Tax Profit bucket */
        myTax = myList.getBucket(TaxCategoryClass.TaxProfitLoss);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Taxation));

        /* If we need a tax slice report */
        if (myList.hasGainsSlices()) {
            makeTaxSliceReport(myBody, myColumns);
        }

        /* Format the tax parameters */
        makeTaxParameters(myBody, pYear);

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a web output of the taxation parameters.
     * @param pBody the report body
     * @param pYear the taxYear
     */
    public void makeTaxParameters(final Element pBody,
                                  final TaxYear pYear) {
        boolean hasAdditionalBand = pYear.hasAdditionalTaxBand();

        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();

        /* Format the tax parameters */
        theBuilder.makeTitle(pBody, "Taxation Parameters");

        /* Format the allowances */
        theBuilder.makeSubTitle(pBody, "Allowances");
        Element myTable = theBuilder.startTable(pBody);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myHdr, "Name");
        theBuilder.makeTitleCell(myRow, "Value");
        Element myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myBody, "PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getAllowance());
        myRow = theBuilder.startDetailRow(myBody, "Age 65-74 PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getLoAgeAllow());
        myRow = theBuilder.startAlternateRow(myBody, "Age 75+ PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getHiAgeAllow());
        myRow = theBuilder.startDetailRow(myBody, "RentalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getRentalAllowance());
        myRow = theBuilder.startAlternateRow(myBody, "CapitalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getCapitalAllow());
        myRow = theBuilder.startDetailRow(myBody, "Income Limit for AgeAllowance");
        theBuilder.makeValueCell(myRow, pYear.getAgeAllowLimit());
        if (hasAdditionalBand) {
            myRow = theBuilder.startAlternateRow(myBody, "Income Limit for PersonalAllowance");
            theBuilder.makeValueCell(myRow, pYear.getAddAllowLimit());
        }

        /* Format the Rates */
        theBuilder.makeSubTitle(pBody, "TaxRates");
        myTable = theBuilder.startTable(pBody);
        myHdr = theBuilder.startTableHeader(myTable);
        myRow = theBuilder.startDetailTitleRow(myHdr, "IncomeType");
        theBuilder.makeTitleCell(myRow, "LoRate");
        theBuilder.makeTitleCell(myRow, "BasicRate");
        theBuilder.makeTitleCell(myRow, "HiRate");
        if (hasAdditionalBand) {
            theBuilder.makeTitleCell(myRow, "AdditionalRate");
        }
        myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myBody, "Salary/Rental");
        theBuilder.makeValueCell(myRow, pYear.hasLoSalaryBand()
                ? pYear.getLoTaxRate()
                : null);
        theBuilder.makeValueCell(myRow, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startDetailRow(myBody, "Interest");
        theBuilder.makeValueCell(myRow, pYear.getLoTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getIntTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startAlternateRow(myBody, "Dividends");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getDivTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiDivTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddDivTaxRate());
        }
        myRow = theBuilder.startDetailRow(myBody, "TaxableGains");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startAlternateRow(myBody, "CapitalGains");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getCapTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiCapTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow);
        }

        /* Format the tax bands */
        theBuilder.makeSubTitle(pBody, "TaxBands");
        myTable = theBuilder.startTable(pBody);
        myHdr = theBuilder.startTableHeader(myTable);
        myRow = theBuilder.startDetailTitleRow(myHdr, "Name");
        theBuilder.makeTitleCell(myRow, "Value");
        myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myRow, "Age for Tax Year");
        theBuilder.makeValueCell(myRow, myList.getAge());

        /* Access the original allowance */
        TaxCategoryBucket myTax = myList.getBucket(TaxCategoryClass.OriginalAllowance);
        myRow = theBuilder.startDetailRow(myBody, "Personal Allowance");
        theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

        /* if we have adjusted the allowance */
        if (myList.hasReducedAllow()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.GrossIncome);
            myRow = theBuilder.startAlternateRow(myBody, "Gross Taxable Income");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.AdjustedAllowance);
            myRow = theBuilder.startDetailRow(myBody, "Adjusted Allowance");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        }

        /* Access the Low Tax Band */
        boolean isOdd = true;
        if (pYear.getLoBand().isNonZero()) {
            myRow = theBuilder.startAlternateRow(myBody, "Low Tax Band");
            theBuilder.makeValueCell(myRow, pYear.getLoBand());
            isOdd = false;
        }

        /* Access the Basic Tax Band */
        myRow = (!isOdd)
                ? theBuilder.startDetailRow(myBody, "Basic Tax Band")
                : theBuilder.startAlternateRow(myBody, "Basic Tax Band");
        theBuilder.makeValueCell(myRow, pYear.getBasicBand());

        /* If we have a high tax band */
        if (pYear.hasAdditionalTaxBand()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.HiTaxBand);
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, "High Tax Band")
                    : theBuilder.startAlternateRow(myBody, "High Tax Band");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        }
    }

    /**
     * Build a standard tax report element.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pSummary the tax summary
     */
    public void makeTaxReport(final Element pBody,
                              final Integer pNumColumns,
                              final TaxCategoryBucket pSummary) {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();

        /* Format the detail */
        Element myTable = theBuilder.startEmbeddedTable(pBody, pSummary.getName(), pNumColumns, true);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myHdr, "Class");
        theBuilder.makeTitleCell(myRow, "Income");
        theBuilder.makeTitleCell(myRow, "Rate");
        theBuilder.makeTitleCell(myRow, "Taxation Due");
        Element myBody = theBuilder.startTableBody(myTable);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Transaction Detail Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip non-detail buckets */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXDETAIL) {
                continue;
            }

            /* Skip record if incorrect parent */
            if (!pSummary.equals(myBucket.getParent())) {
                continue;
            }

            /* Format the detail */
            myRow = (!isOdd)
                    ? theBuilder.startDetailRow(myBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Amount));
            theBuilder.makeValueCell(myRow, myBucket.getRateAttribute(TaxAttribute.Rate));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Taxation));

            /* Flip row type */
            isOdd = !isOdd;
        }
    }

    /**
     * Build a tax slice report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     */
    public void makeTaxSliceReport(final Element pBody,
                                   final Integer pNumColumns) {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        ChargeableEventList myCharges = theAnalysis.getCharges();

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;

        /* Format the detail */
        Element myTable = theBuilder.startEmbeddedTable(pBody, "ChargeableEvents", pNumColumns, false);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myHdr, "Date");
        theBuilder.makeTitleCell(myRow, "Description");
        theBuilder.makeTitleCell(myRow, "Amount");
        theBuilder.makeTitleCell(myRow, "Tax Credit");
        theBuilder.makeTitleCell(myRow, "Years");
        theBuilder.makeTitleCell(myRow, "Slice");
        theBuilder.makeTitleCell(myRow, "Taxation");
        Element myBody = theBuilder.startTableBody(myTable);

        /* Create the list iterator */
        Iterator<ChargeableEvent> myIterator = theAnalysis.getCharges().iterator();
        boolean isOdd = true;

        /* Loop through the Charges */
        while (myIterator.hasNext()) {
            ChargeableEvent myCharge = myIterator.next();

            /* Format the detail */
            String myDate = theFormatter.formatObject(myCharge.getDate());
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myDate)
                    : theBuilder.startAlternateRow(myBody, myDate);
            theBuilder.makeValueCell(myRow, myCharge.getComments());
            theBuilder.makeValueCell(myRow, myCharge.getAmount());
            theBuilder.makeValueCell(myRow, myCharge.getTaxCredit());
            theBuilder.makeValueCell(myRow, myCharge.getYears());
            theBuilder.makeValueCell(myRow, myCharge.getSlice());
            theBuilder.makeValueCell(myRow, myCharge.getTaxation());

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the totals */
        myRow = theBuilder.startTotalRow(myBody, TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myCharges.getGainsTotal());
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myCharges.getSliceTotal());
        theBuilder.makeTotalCell(myRow, myCharges.getTaxTotal());

        /* Access the Summary Tax Due Slice */
        TaxCategoryBucket myTax = myList.getBucket(TaxCategoryClass.TaxDueSlice);

        /* Add the Slice taxation details */
        makeTaxReport(myBody, myColumns, myTax);
    }
}
