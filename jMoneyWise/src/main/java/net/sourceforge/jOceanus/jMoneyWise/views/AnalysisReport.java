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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.AnalysisBucket.BucketType;
import net.sourceforge.jOceanus.jMoneyWise.views.CapitalEvent.CapitalAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.CapitalEvent.CapitalEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventAnalysis.AnalysisYear;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.IncomeBreakdown.AccountRecord;
import net.sourceforge.jOceanus.jMoneyWise.views.IncomeBreakdown.IncomeTotals;
import net.sourceforge.jOceanus.jMoneyWise.views.IncomeBreakdown.RecordList;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

/**
 * Reporting class to build HTML from analysis.
 * @author Tony Washer
 */
public class AnalysisReport {
    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 10000;

    /**
     * The Total text.
     */
    private static final String TOTAL_TEXT = "Total";

    /**
     * The Profit text.
     */
    private static final String PROFIT_TEXT = "Profit";

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The Tax year analysis.
     */
    private final AnalysisYear theAnalysisYear;

    /**
     * The reporting date.
     */
    private final JDateDay theDate;

    /**
     * The reporting TaxYear.
     */
    private final TaxYear theYear;

    /**
     * Report formatter.
     */
    private final Report theReport;

    /**
     * Data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    public AnalysisReport(final EventAnalysis pAnalysis) {
        /* Record the details */
        theAnalysis = pAnalysis.getAnalysis();
        theDate = theAnalysis.getDate();
        theYear = null;
        theAnalysisYear = null;
        theReport = new Report();
        theFormatter = theReport.getDataFormatter();
    }

    /**
     * Constructor.
     * @param pAnalysisYear the years analysis.
     */
    public AnalysisReport(final AnalysisYear pAnalysisYear) {
        /* Record the details */
        theAnalysisYear = pAnalysisYear;
        theAnalysis = pAnalysisYear.getAnalysis();
        theDate = theAnalysis.getDate();
        theYear = pAnalysisYear.getTaxYear();
        theReport = new Report();
        theFormatter = theReport.getDataFormatter();
    }

    /**
     * Build a web output of the Year report.
     * @return Web output
     */
    public String getYearReport() {
        /* Access the bucket list */
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Asset Report for "
                                            + myOutput.append(theDate.getYear()));
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Class", 2);
        theReport.makeTableColumnSpan(myOutput, "Value", 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear()));
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear() - 1));
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        boolean isOdd = true;

        /* Loop through the Category Total Buckets */
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (myBucket.getCategoryType() != CategoryType.SubTotal) {
                continue;
            }

            /* Format the SubTotal */
            theReport.startLinkRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;

            /* Format the detail */
            myDetail.append(makeCategoryReport(myBucket, false));
        }

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Format the totals */
        theReport.startTotalRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);

        /* Format the profit */
        theReport.startTotalRow(myOutput, PROFIT_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.ValueDelta, JMoney.class));
        theReport.makeTotalCell(myOutput);
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the instant report.
     * @return Web output
     */
    public String getInstantReport() {
        /* Access the bucket list */
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Instant Asset Report for "
                                            + theFormatter.formatObject(theDate));
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Class");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountCategoryBucket> myIterator = myCategories.iterator();
        boolean isOdd = true;

        /* Loop through the SubTotal Buckets */
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            if (myBucket.getCategoryType() != CategoryType.SubTotal) {
                continue;
            }

            /* Format the SubTotal */
            theReport.startLinkRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;

            /* Format the detail */
            myDetail.append(makeCategoryReport(myBucket, true));

            /* Access the category class */
            // AccountCategoryClass myCategory = mySummary.getAccountCategory().getCategoryTypeClass();

            /* Format the detail */
            // if (myCategory.isLoan()) {
            // myDetail.append(makeLoanReport(mySummary));
            // } else if (myCategory.hasUnits()) {
            // myDetail.append(makePricedReport(mySummary));
            // } else {
            // myDetail.append(makeRatedReport(mySummary));
            // }

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Format the totals */
        theReport.startTotalRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the market report.
     * @return Web output
     */
    public String getMarketReport() {
        /* Access the bucket lists */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Market Report for "
                                            + theFormatter.formatObject(theDate));
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Cost");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.makeTableColumn(myOutput, "Gains");
        theReport.makeTableColumn(myOutput, "Profit");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountBucket> myIterator = myAccounts.iterator();
        boolean isOdd = true;

        /* Loop through the Account Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Only process priced asset types */
            if (myBucket.getCategoryType() != CategoryType.Priced) {
                continue;
            }

            /* Format the Asset */
            theReport.startLinkRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Cost, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Gained, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Profit, JMoney.class));
            theReport.endRow(myOutput);

            /* If this is Capital */
            if (myBucket.getAccountCategory().getCategoryTypeClass().isCapital()) {
                /* Format the detail */
                myDetail.append(makeCapitalEventReport(myBucket));
            }

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Access the totals */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Format the totals */
        theReport.startTotalRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Cost, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Gained, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Profit, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the income/expense report.
     * @return Web output
     */
    public String getIncomeReport() {
        /* Access the bucket lists */
        AccountBucketList myList = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Income/Expense Report for "
                                            + theDate.getYear());
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Name", 2);
        theReport.makeTableColumnSpan(myOutput, Integer.toString(theDate.getYear()), 2);
        theReport.makeTableColumnSpan(myOutput, Integer.toString(theDate.getYear() - 1), 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, "Income");
        theReport.makeTableColumn(myOutput, "Expense");
        theReport.makeTableColumn(myOutput, "Income");
        theReport.makeTableColumn(myOutput, "Expense");
        theReport.startTableBody(myOutput);

        /* Create the bucket iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip bucket if this is not a payee account */
            if (myBucket.getCategoryType() != CategoryType.Payee) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Income, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Expense, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getBaseAttribute(AccountAttribute.Income, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getBaseAttribute(AccountAttribute.Expense, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Access the totals */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Format the totals */
        theReport.startTotalRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Income, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.Expense, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getBaseAttribute(AccountAttribute.Income, JMoney.class));
        theReport.makeTotalCell(myOutput, myTotals.getBaseAttribute(AccountAttribute.Expense, JMoney.class));
        theReport.endRow(myOutput);

        /* Format the profit */
        theReport.startTotalRow(myOutput, PROFIT_TEXT);
        theReport.makeTotalCell(myOutput, myTotals.getAttribute(AccountAttribute.IncomeDelta, JMoney.class));
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, myTotals.getBaseAttribute(AccountAttribute.IncomeDelta, JMoney.class));
        theReport.makeTotalCell(myOutput);
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a standard yearly category element.
     * @param pSummary the class of the element
     * @param doDetailed produce detailed report
     * @return Web output
     */
    public StringBuilder makeCategoryReport(final AccountCategoryBucket pSummary,
                                            final boolean doDetailed) {
        /* Access the bucket lists */
        AccountCategoryBucketList myList = theAnalysis.getAccountCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Access the category */
        AccountCategory myCategory = pSummary.getAccountCategory();

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Name", 2);
        theReport.makeTableColumnSpan(myOutput, "Value", 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear()));
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear() - 1));
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Category Buckets */
        while (myIterator.hasNext()) {
            AccountCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory().getParentCategory(), myCategory)) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;

            /* If we are performing a detailed report */
            if (doDetailed) {
                /* Access the category class */
                AccountCategoryClass myClass = pSummary.getAccountCategory().getCategoryTypeClass();

                /* Format the detail */
                if (myClass.isLoan()) {
                    myDetail.append(makeLoanReport(myBucket));
                } else if (myClass.hasUnits()) {
                    myDetail.append(makePricedReport(myBucket));
                } else {
                    myDetail.append(makeRatedReport(myBucket));
                }
            } else {
                /* Format the standard detail */
                myDetail.append(makeStandardReport(myBucket));
            }
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pSummary.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.makeTotalCell(myOutput, pSummary.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Add the detail */
        myOutput.append(myDetail);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a standard yearly report element.
     * @param pSummary the class of the element
     * @return Web output
     */
    public StringBuilder makeStandardReport(final AccountCategoryBucket pSummary) {
        /* Access the bucket lists */
        AccountBucketList myList = theAnalysis.getAccounts();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the category */
        AccountCategory myCategory = pSummary.getAccountCategory();

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Name", 2);
        theReport.makeTableColumnSpan(myOutput, "Value", 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear()));
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear() - 1));
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory(), myCategory)) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pSummary.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.makeTotalCell(myOutput, pSummary.getBaseAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a rated instant report element.
     * @param pSummary the class of the element
     * @return Web output
     */
    private StringBuilder makeRatedReport(final AccountCategoryBucket pSummary) {
        /* Access the bucket lists */
        AccountBucketList myList = theAnalysis.getAccounts();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the category */
        AccountCategory myCategory = pSummary.getAccountCategory();

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.makeTableColumn(myOutput, "Rate");
        theReport.makeTableColumn(myOutput, "Maturity");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory(), myCategory)) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Rate, JMoney.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Maturity, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pSummary.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput);
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a loan instant report element.
     * @param pSummary the class of element
     * @return Web output
     */
    public StringBuilder makeLoanReport(final AccountCategoryBucket pSummary) {
        /* Access the bucket lists */
        AccountBucketList myList = theAnalysis.getAccounts();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the category */
        AccountCategory myCategory = pSummary.getAccountCategory();

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory(), myCategory)) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pSummary.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a priced instant report element.
     * @param pSummary the class of element
     * @return Web output
     */
    public StringBuilder makePricedReport(final AccountCategoryBucket pSummary) {
        /* Access the bucket lists */
        AccountBucketList myList = theAnalysis.getAccounts();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the category */
        AccountCategory myCategory = pSummary.getAccountCategory();

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Units");
        theReport.makeTableColumn(myOutput, "Price");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            if (!Difference.isEqual(myBucket.getAccountCategory(), myCategory)) {
                continue;
            }

            /* Skip irrelevant records */
            if (!myBucket.isRelevant()) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Units, JUnits.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Price, JPrice.class));
            theReport.makeValueCell(myOutput, myBucket.getAttribute(AccountAttribute.Valuation, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, pSummary.getAttribute(AccountAttribute.Valuation, JMoney.class));
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a capital event report element.
     * @param pAsset the asset to report on
     * @return Web output
     */
    public StringBuilder makeCapitalEventReport(final AccountBucket pAsset) {
        /* Access the event lists */
        CapitalEventList myList = pAsset.getCapitalEvents();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pAsset.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Date");
        theReport.makeTableColumn(myOutput, "DeltaUnits");
        theReport.makeTableColumn(myOutput, "DeltaCost");
        theReport.makeTableColumn(myOutput, "DeltaGains");
        theReport.makeTableColumn(myOutput, "Dividend");
        theReport.startTableBody(myOutput);

        /* Access the iterator */
        Iterator<CapitalEvent> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Events */
        while (myIterator.hasNext()) {
            CapitalEvent myEvent = myIterator.next();

            /* Skip record if this is not based on an event (at present) */
            if (myEvent.getEvent() == null) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, theFormatter.formatObject(myEvent.getDate()));
            theReport.makeValueCell(myOutput, myEvent.getAttribute(CapitalAttribute.DeltaUnits, JUnits.class));
            theReport.makeValueCell(myOutput, myEvent.getAttribute(CapitalAttribute.DeltaCost, JMoney.class));
            theReport.makeValueCell(myOutput, myEvent.getAttribute(CapitalAttribute.DeltaGains, JMoney.class));
            theReport.makeValueCell(myOutput, myEvent.getAttribute(CapitalAttribute.DeltaDividend, JMoney.class));
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build Totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pAsset.getAttribute(AccountAttribute.Units, JUnits.class));
        theReport.makeTotalCell(myOutput, pAsset.getAttribute(AccountAttribute.Cost, JMoney.class));
        theReport.makeTotalCell(myOutput, pAsset.getAttribute(AccountAttribute.Gained, JMoney.class));
        theReport.makeTotalCell(myOutput);
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a web output of the transaction report.
     * @return Web output
     */
    public String getTransReport() {
        /* Access the bucket lists */
        EventCategoryBucketList myList = theAnalysis.getEventCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Transaction Report for "
                                            + theDate.getYear());
        theReport.makeSubHeading(myOutput, "Transaction Totals");
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Class", 2);
        theReport.makeTableColumnSpan(myOutput, "Value", 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear()));
        theReport.makeTableColumn(myOutput, Integer.toString(theDate.getYear() - 1));
        theReport.startTableBody(myOutput);

        /* Access the bucket iterator */
        Iterator<EventCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Transaction Summary Buckets */
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Switch on bucket type */
            // switch (myBucket.getBucketType()) {
            /* Summary */
            // case CATSUMMARY:
            // CategorySummary mySummary = (CategorySummary) myBucket;

            /* Format the detail */
            // theReport.startDataRow(myOutput, isOdd, mySummary.getName());
            // theReport.makeValueCell(myOutput, mySummary.getAmount());
            // theReport.makeValueCell(myOutput, mySummary.getPrevAmount());
            // theReport.endRow(myOutput);
            // break;
            /* Total */
            // case CATTOTAL:
            // CategoryTotal myTotal = (CategoryTotal) myBucket;

            /* Format the detail */
            // theReport.startDataRow(myOutput, isOdd, myTotal.getName());
            // theReport.makeValueCell(myOutput, myTotal.getAmount());
            // theReport.makeValueCell(myOutput, myTotal.getPrevAmount());
            // theReport.endRow(myOutput);
            // break;
            // default:
            // continue;
            // }

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the next table */
        theReport.endTable(myOutput);
        theReport.makeSubHeading(myOutput, "Transaction Breakdown");
        theReport.startTable(myOutput);
        theReport.makeTableRowSpan(myOutput, "Class", 2);
        theReport.makeTableColumnSpan(myOutput, Integer.toString(theDate.getYear()), 2);
        theReport.makeTableColumnSpan(myOutput, Integer.toString(theDate.getYear() - 1), 2);
        theReport.makeTableNewRow(myOutput);
        theReport.makeTableColumn(myOutput, "Value");
        theReport.makeTableColumn(myOutput, "TaxCredit");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.makeTableColumn(myOutput, "TaxCredit");
        theReport.startTableBody(myOutput);

        /* Access a new bucket iterator */
        myIterator = myList.iterator();
        isOdd = true;

        /* Loop through the Transaction Summary Buckets */
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Skip entries that are not TransDetail */
            // if (myBucket.getBucketType() != BucketType.CATDETAIL) {
            // continue;
            // }

            /* Access the detail */
            // EventCategoryDetail myDetail = (EventCategoryDetail) myBucket;

            /* Format the detail */
            // theReport.startDataRow(myOutput, isOdd, myDetail.getName());
            // theReport.makeValueCell(myOutput, myDetail.getAmount());
            // theReport.makeValueCell(myOutput, myDetail.getTaxCredit());
            // theReport.makeValueCell(myOutput, myDetail.getPrevAmount());
            // theReport.makeValueCell(myOutput, myDetail.getPrevTax());
            // theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Close the table */
        theReport.endTable(myOutput);
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the taxation report.
     * @return Web output
     */
    public String getTaxReport() {
        /* Ensure that tax has been calculated */
        // theAnalysisYear.calculateTax();

        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);
        TaxCategoryBucket myTax;

        /* Initialise the detail */
        theReport.makeHeading(myOutput, "Taxation Breakdown");

        /* Format the header */
        theReport.startReport(myOutput);
        theReport.makeLinkHeading(myOutput, "Taxation Report for "
                                            + theDate.getYear());
        theReport.makeSubHeading(myOutput, "Taxation Summary");
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Class");
        theReport.makeTableColumn(myOutput, "Total Income");
        theReport.makeTableColumn(myOutput, "Taxation Due");
        theReport.startTableBody(myOutput);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Tax Summary Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip the non-summary elements */
            if (myBucket.getBucketType() != BucketType.TAXSUMMARY) {
                continue;
            }

            /* Format the line */
            theReport.startLinkRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAmount());
            theReport.makeValueCell(myOutput, myBucket.getTaxation());
            theReport.endRow(myOutput);

            /* Format the detail */
            myDetail.append(makeTaxReport(myBucket));

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Access the Total taxation bucket */
        myTax = myList.getBucket(TaxCategoryClass.TotalTaxationDue);
        theReport.startTotalRow(myOutput, myTax.getName());
        theReport.makeTotalCell(myOutput, myTax.getAmount());
        theReport.makeTotalCell(myOutput, myTax.getTaxation());
        theReport.endRow(myOutput);

        /* Access the Tax Paid bucket */
        // CategorySummary myTrans = myList.getCategorySummary(TaxCategoryClass.TaxPaid);
        // theReport.startTotalRow(myOutput, myTrans.getName());
        // theReport.makeTotalCell(myOutput);
        // theReport.makeTotalCell(myOutput, myTrans.getAmount());
        // theReport.endRow(myOutput);

        /* Access the Tax Profit bucket */
        myTax = myList.getBucket(TaxCategoryClass.TaxProfitLoss);
        theReport.startTotalRow(myOutput, myTax.getName());
        theReport.makeTotalCell(myOutput, myTax.getAmount());
        theReport.makeTotalCell(myOutput, myTax.getTaxation());
        theReport.endRow(myOutput);

        /* Finish the table */
        theReport.endTable(myOutput);

        /* Format the tax parameters */
        theReport.makeHeading(myOutput, "Taxation Parameters");

        /* Format the allowances */
        theReport.makeSubHeading(myOutput, "Allowances");
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.startTableBody(myOutput);
        theReport.startDataRow(myOutput, true, "PersonalAllowance");
        theReport.makeValueCell(myOutput, theYear.getAllowance());
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, false, "Age 65-74 PersonalAllowance");
        theReport.makeValueCell(myOutput, theYear.getLoAgeAllow());
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, true, "Age 75+ PersonalAllowance");
        theReport.makeValueCell(myOutput, theYear.getHiAgeAllow());
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, false, "RentalAllowance");
        theReport.makeValueCell(myOutput, theYear.getRentalAllowance());
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, true, "CapitalAllowance");
        theReport.makeValueCell(myOutput, theYear.getCapitalAllow());
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, false, "Income Limit for AgeAllowance");
        theReport.makeValueCell(myOutput, theYear.getAgeAllowLimit());
        theReport.endRow(myOutput);
        if (theYear.hasAdditionalTaxBand()) {
            theReport.startDataRow(myOutput, true, "Income Limit for PersonalAllowance");
            theReport.makeValueCell(myOutput, theYear.getAddAllowLimit());
            theReport.endRow(myOutput);
        }
        theReport.endTable(myOutput);

        /* Format the Rates */
        theReport.makeSubHeading(myOutput, "TaxRates");
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "IncomeType");
        theReport.makeTableColumn(myOutput, "LoRate");
        theReport.makeTableColumn(myOutput, "BasicRate");
        theReport.makeTableColumn(myOutput, "HiRate");
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeTableColumn(myOutput, "AdditionalRate");
        }
        theReport.startTableBody(myOutput);
        theReport.startDataRow(myOutput, true, "Salary/Rental");
        theReport.makeValueCell(myOutput, theYear.hasLoSalaryBand()
                ? theYear.getLoTaxRate()
                : null);
        theReport.makeValueCell(myOutput, theYear.getBasicTaxRate());
        theReport.makeValueCell(myOutput, theYear.getHiTaxRate());
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeValueCell(myOutput, theYear.getAddTaxRate());
        }
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, false, "Interest");
        theReport.makeValueCell(myOutput, theYear.getLoTaxRate());
        theReport.makeValueCell(myOutput, theYear.getIntTaxRate());
        theReport.makeValueCell(myOutput, theYear.getHiTaxRate());
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeValueCell(myOutput, theYear.getAddTaxRate());
        }
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, true, "Dividends");
        theReport.makeValueCell(myOutput);
        theReport.makeValueCell(myOutput, theYear.getDivTaxRate());
        theReport.makeValueCell(myOutput, theYear.getHiDivTaxRate());
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeValueCell(myOutput, theYear.getAddDivTaxRate());
        }
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, false, "TaxableGains");
        theReport.makeValueCell(myOutput);
        theReport.makeValueCell(myOutput, theYear.getBasicTaxRate());
        theReport.makeValueCell(myOutput, theYear.getHiTaxRate());
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeValueCell(myOutput, theYear.getAddTaxRate());
        }
        theReport.endRow(myOutput);
        theReport.startDataRow(myOutput, true, "CapitalGains");
        theReport.makeValueCell(myOutput);
        theReport.makeValueCell(myOutput, theYear.getCapTaxRate());
        theReport.makeValueCell(myOutput, theYear.getHiCapTaxRate());
        if (theYear.hasAdditionalTaxBand()) {
            theReport.makeValueCell(myOutput);
        }
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Format the tax bands */
        theReport.makeSubHeading(myOutput, "TaxBands");
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Name");
        theReport.makeTableColumn(myOutput, "Value");
        theReport.startTableBody(myOutput);
        theReport.startDataRow(myOutput, true, "Age for Tax Year");
        theReport.makeValueCell(myOutput, theAnalysis.getAge());
        theReport.endRow(myOutput);

        /* Access the original allowance */
        myTax = myList.getBucket(TaxCategoryClass.OriginalAllowance);
        theReport.startDataRow(myOutput, false, "Personal Allowance");
        theReport.makeValueCell(myOutput, myTax.getAmount());
        theReport.endRow(myOutput);

        /* if we have adjusted the allowance */
        if (theAnalysis.hasReducedAllow()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.GrossIncome);
            theReport.startDataRow(myOutput, true, "Gross Taxable Income");
            theReport.makeValueCell(myOutput, myTax.getAmount());
            theReport.endRow(myOutput);

            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.AdjustedAllowance);
            theReport.startDataRow(myOutput, false, "Adjusted Allowance");
            theReport.makeValueCell(myOutput, myTax.getAmount());
            theReport.endRow(myOutput);
        }

        /* Access the Low Tax Band */
        isOdd = true;
        if (theYear.getLoBand() != null) {
            theReport.startDataRow(myOutput, isOdd, "Low Tax Band");
            theReport.makeValueCell(myOutput, theYear.getLoBand());
            theReport.endRow(myOutput);
            isOdd = false;
        }

        /* Access the Basic Tax Band */
        theReport.startDataRow(myOutput, isOdd, "Basic Tax Band");
        theReport.makeValueCell(myOutput, theYear.getBasicBand());
        theReport.endRow(myOutput);

        /* If we have a high tax band */
        if (theYear.hasAdditionalTaxBand()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.HiTaxBand);
            theReport.startDataRow(myOutput, !isOdd, "High Tax Band");
            theReport.makeValueCell(myOutput, myTax.getAmount());
            theReport.endRow(myOutput);
        }
        theReport.endTable(myOutput);

        /* Add the detail */
        myOutput.append(myDetail);

        /* If we need a tax slice report */
        if (theAnalysis.hasGainsSlices()) {
            myOutput.append(makeTaxSliceReport());
        }

        /* Close the document */
        theReport.endReport(myOutput);

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a standard tax report element.
     * @param pSummary the summary
     * @return Web output
     */
    public StringBuilder makeTaxReport(final TaxCategoryBucket pSummary) {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        theReport.makeLinkSubHeading(myOutput, pSummary.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Class");
        theReport.makeTableColumn(myOutput, "Income");
        theReport.makeTableColumn(myOutput, "Rate");
        theReport.makeTableColumn(myOutput, "Taxation Due");
        theReport.startTableBody(myOutput);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Transaction Detail Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip non-detail buckets */
            if (myBucket.getBucketType() != BucketType.TAXDETAIL) {
                continue;
            }

            /* Skip record if incorrect parent */
            if (!pSummary.equals(myBucket.getParent())) {
                continue;
            }

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, myBucket.getName());
            theReport.makeValueCell(myOutput, myBucket.getAmount());
            theReport.makeValueCell(myOutput, myBucket.getRate());
            theReport.makeValueCell(myOutput, myBucket.getTaxation());
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Build totals */
        theReport.startTotalLinkRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput, pSummary.getAmount());
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, pSummary.getTaxation());
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a tax slice report.
     * @return Web output
     */
    public StringBuilder makeTaxSliceReport() {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        ChargeableEventList myCharges = theAnalysis.getCharges();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        theReport.makeSubHeading(myOutput, "Chargeable Events");
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Date");
        theReport.makeTableColumn(myOutput, "Description");
        theReport.makeTableColumn(myOutput, "Amount");
        theReport.makeTableColumn(myOutput, "TaxCredit");
        theReport.makeTableColumn(myOutput, "Years");
        theReport.makeTableColumn(myOutput, "Slice");
        theReport.makeTableColumn(myOutput, "Taxation");
        theReport.startTableBody(myOutput);

        /* Create the list iterator */
        Iterator<ChargeableEvent> myIterator = theAnalysis.getCharges().iterator();
        boolean isOdd = true;

        /* Loop through the Charges */
        while (myIterator.hasNext()) {
            ChargeableEvent myCharge = myIterator.next();

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, theFormatter.formatObject(myCharge.getDate()));
            theReport.makeValueCell(myOutput, myCharge.getComments());
            theReport.makeValueCell(myOutput, myCharge.getAmount());
            theReport.makeValueCell(myOutput, myCharge.getTaxCredit());
            theReport.makeValueCell(myOutput, myCharge.getYears());
            theReport.makeValueCell(myOutput, myCharge.getSlice());
            theReport.makeValueCell(myOutput, myCharge.getTaxation());
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the totals */
        theReport.startTotalRow(myOutput, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, myCharges.getGainsTotal());
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, myCharges.getSliceTotal());
        theReport.makeTotalCell(myOutput, myCharges.getTaxTotal());
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Access the Summary Tax Due Slice */
        TaxCategoryBucket myTax = myList.getBucket(TaxCategoryClass.TaxDueSlice);

        /* Add the Slice taxation details */
        myOutput.append(makeTaxReport(myTax));

        /* Return the output */
        return myOutput;
    }

    /**
     * Build income breakdown report.
     * @return Web output
     */
    public String getBreakdownReport() {
        /* Access the Income Breakdown */
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        IncomeBreakdown myBreakdown = theAnalysisYear.getBreakdown();

        /* Create the heading */
        theReport.makeLinkHeading(myOutput, "Income Breakdown Report for "
                                            + theDate.getYear());

        /* Build the report */
        myOutput.append(makeAccountListReport(myBreakdown.getSalary(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getRental(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getTaxableInterest(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getTaxableDividend(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getUnitTrustDividend(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getTaxFreeInterest(), null));
        myOutput.append(makeAccountListReport(myBreakdown.getTaxFreeDividend(), null));

        /* Return the report */
        return myOutput.toString();
    }

    /**
     * Build a standard income child report element.
     * @param pList the record list
     * @param pReturn return link
     * @return Web output
     */
    public StringBuilder makeAccountListReport(final RecordList pList,
                                               final String pReturn) {
        /* If there is zero income return empty string */
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);
        if (!pList.getTotals().getGrossIncome().isNonZero()) {
            return myOutput;
        }

        /* Format the detail */
        String myLinkName = pList.getName();
        Account myOwner = pList.getOwner();
        theReport.makeLinkSubHeading(myOutput, myLinkName, (myOwner == null)
                ? myLinkName
                : myOwner.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Account");
        theReport.makeTableColumn(myOutput, "Gross");
        theReport.makeTableColumn(myOutput, "Net");
        theReport.makeTableColumn(myOutput, "TaxCredit");
        theReport.startTableBody(myOutput);

        /* Access the account iterator */
        Iterator<AccountRecord> myIterator = pList.iterator();
        boolean isOdd = true;

        /* Loop through the Accounts associated with this List */
        while (myIterator.hasNext()) {
            AccountRecord myAccount = myIterator.next();

            /* Access the name of the sublist */
            String myListName = myAccount.getChildren().getName();
            String myName = myAccount.getAccount().getName();

            /* Format the detail */
            theReport.startLinkRow(myOutput, isOdd, myListName, myName);

            /* Format the totals */
            IncomeTotals myTotals = myAccount.getTotals();
            theReport.makeValueCell(myOutput, myTotals.getGrossIncome());
            theReport.makeValueCell(myOutput, myTotals.getNetIncome());
            theReport.makeValueCell(myOutput, myTotals.getTaxCredit());
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;

            /* If we have events */
            if (myAccount.getEvents().size() > 0) {
                /* Add the child report */
                myDetail.append(makeAccountEventReport(myAccount, myLinkName));

                /* If we have children */
            } else if (myAccount.getChildren().size() > 0) {
                /* Add the child report */
                myDetail.append(makeAccountListReport(myAccount.getChildren(), myLinkName));
            }
        }

        /* Build the list totals */
        IncomeTotals myTotals = pList.getTotals();
        if (pReturn != null) {
            theReport.startTotalDataLinkRow(myOutput, pReturn, TOTAL_TEXT);
        } else {
            theReport.startTotalRow(myOutput, TOTAL_TEXT);
        }
        theReport.makeTotalCell(myOutput, myTotals.getGrossIncome());
        theReport.makeTotalCell(myOutput, myTotals.getNetIncome());
        theReport.makeTotalCell(myOutput, myTotals.getTaxCredit());
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Append the detail */
        myOutput.append(myDetail);

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a standard income event report element.
     * @param pAccount the account
     * @param pReturn return link
     * @return Web output
     */
    public StringBuilder makeAccountEventReport(final AccountRecord pAccount,
                                                final String pReturn) {
        /* Format the detail */
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        Account myAccount = pAccount.getAccount();
        String myName = pAccount.getChildren().getName();
        theReport.makeLinkSubHeading(myOutput, myName, myAccount.getName());
        theReport.startTable(myOutput);
        theReport.makeTableColumn(myOutput, "Date");
        theReport.makeTableColumn(myOutput, "Description");
        theReport.makeTableColumn(myOutput, "Gross");
        theReport.makeTableColumn(myOutput, "Net");
        theReport.makeTableColumn(myOutput, "TaxCredit");
        theReport.startTableBody(myOutput);

        /* Access the event iterator */
        Iterator<Event> myIterator = pAccount.getEvents().iterator();
        boolean isOdd = true;

        /* Loop through the Events associated with this Account */
        while (myIterator.hasNext()) {
            Event myEvent = myIterator.next();

            /* Format the detail */
            theReport.startDataRow(myOutput, isOdd, theFormatter.formatObject(myEvent.getDate()));
            theReport.makeValueCell(myOutput, myEvent.getComments());

            /* Calculate Gross */
            EventCategory myCategory = myEvent.getCategory();
            JMoney myGross = new JMoney(myEvent.getAmount());
            JMoney myNet = myEvent.getAmount();

            /* If we are NatInsurance/Benefit */
            if ((myCategory.getCategoryTypeClass() == EventCategoryClass.NatInsurance)
                || (myCategory.getCategoryTypeClass() == EventCategoryClass.Benefit)) {
                /* Just add to gross */
                myNet = new JMoney();
            } else if (myEvent.getTaxCredit() != null) {
                myGross.addAmount(myEvent.getTaxCredit());
            }

            /* Report the values */
            theReport.makeValueCell(myOutput, myGross);
            theReport.makeValueCell(myOutput, myNet);
            theReport.makeValueCell(myOutput, myEvent.getTaxCredit());
            theReport.endRow(myOutput);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the totals */
        IncomeTotals myTotals = pAccount.getTotals();
        theReport.startTotalDataLinkRow(myOutput, pReturn, TOTAL_TEXT);
        theReport.makeTotalCell(myOutput);
        theReport.makeTotalCell(myOutput, myTotals.getGrossIncome());
        theReport.makeTotalCell(myOutput, myTotals.getNetIncome());
        theReport.makeTotalCell(myOutput, myTotals.getTaxCredit());
        theReport.endRow(myOutput);
        theReport.endTable(myOutput);

        /* Return the output */
        return myOutput;
    }
}
