/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.views;

import java.util.Iterator;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountType;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.StaticClass.TaxClass;
import net.sourceforge.JFinanceApp.data.StaticClass.TransClass;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.data.TransactionType;
import net.sourceforge.JFinanceApp.views.Analysis.AnalysisBucket;
import net.sourceforge.JFinanceApp.views.Analysis.AssetAccount;
import net.sourceforge.JFinanceApp.views.Analysis.AssetSummary;
import net.sourceforge.JFinanceApp.views.Analysis.AssetTotal;
import net.sourceforge.JFinanceApp.views.Analysis.BucketList;
import net.sourceforge.JFinanceApp.views.Analysis.BucketType;
import net.sourceforge.JFinanceApp.views.Analysis.DebtAccount;
import net.sourceforge.JFinanceApp.views.Analysis.ExternalAccount;
import net.sourceforge.JFinanceApp.views.Analysis.ExternalTotal;
import net.sourceforge.JFinanceApp.views.Analysis.MarketTotal;
import net.sourceforge.JFinanceApp.views.Analysis.MoneyAccount;
import net.sourceforge.JFinanceApp.views.Analysis.TaxDetail;
import net.sourceforge.JFinanceApp.views.Analysis.TransDetail;
import net.sourceforge.JFinanceApp.views.Analysis.TransSummary;
import net.sourceforge.JFinanceApp.views.Analysis.TransTotal;
import net.sourceforge.JFinanceApp.views.Analysis.ValueAccount;
import net.sourceforge.JFinanceApp.views.CapitalEvent.CapitalEventList;
import net.sourceforge.JFinanceApp.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.JFinanceApp.views.EventAnalysis.AnalysisYear;
import net.sourceforge.JFinanceApp.views.IncomeBreakdown.AccountRecord;
import net.sourceforge.JFinanceApp.views.IncomeBreakdown.IncomeTotals;
import net.sourceforge.JFinanceApp.views.IncomeBreakdown.RecordList;

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
    private final DateDay theDate;

    /**
     * The reporting TaxYear.
     */
    private final TaxYear theYear;

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

        /* Produce totals */
        pAnalysis.getMetaAnalysis().produceTotals();
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

        /* Produce totals for the analysis year */
        pAnalysisYear.produceTotals();
    }

    /**
     * Build a web output of the Year report.
     * @return Web output
     */
    public String getYearReport() {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Asset Report for ");
        myOutput.append(theDate.getYear());
        myOutput.append("</h1></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
        myOutput.append("<thead><th>");
        myOutput.append(theDate.getYear());
        myOutput.append("</th><th>");
        myOutput.append(theDate.getYear() - 1);
        myOutput.append("</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Summary Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Only process summary items */
            if (myBucket.getBucketType() != BucketType.ASSETSUMMARY) {
                continue;
            }

            /* Access the summary bucket */
            AssetSummary mySummary = (AssetSummary) myBucket;

            /* Format the Summary */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append("<a href=\"#Detail");
            myOutput.append(mySummary.getName());
            myOutput.append("\">");
            myOutput.append(mySummary.getName());
            myOutput.append("</a></th>");
            myOutput.append(Report.makeMoneyItem(mySummary.getValue()));
            myOutput.append(Report.makeMoneyItem(mySummary.getPrevValue()));
            myOutput.append("</tr>");

            /* Format the detail */
            myDetail.append(makeStandardReport(mySummary));
        }

        /* Access the totals */
        AssetTotal myTotal = myList.getAssetTotal();

        /* Format the totals */
        myOutput.append("<tr><th>Totals</th>");
        myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getPrevValue()));
        myOutput.append("</tr>");

        /* Format the profit */
        myOutput.append("<tr><th>Profit</th>");
        myOutput.append(Report.makeMoneyProfit(myTotal.getProfit()));
        myOutput.append("</tr></tbody></table>");

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        myOutput.append("</body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the instant report.
     * @return Web output
     */
    public String getInstantReport() {
        /* Access the bucket list */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Instant Asset Report for ");
        myOutput.append(JDataObject.formatField(theDate));
        myOutput.append("</h1></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Class</th><th>Value</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Summary Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Only process summary items */
            if (myBucket.getBucketType() != BucketType.ASSETSUMMARY) {
                continue;
            }

            /* Access the summary bucket */
            AssetSummary mySummary = (AssetSummary) myBucket;

            /* Format the Summary */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append("<a href=\"#Detail");
            myOutput.append(mySummary.getName());
            myOutput.append("\">");
            myOutput.append(mySummary.getName());
            myOutput.append("</a></th>");
            myOutput.append(Report.makeMoneyItem(mySummary.getValue()));
            myOutput.append("</tr>");

            /* Access the type */
            AccountType myType = mySummary.getAccountType();

            /* Format the detail */
            if (myType.isMoney()) {
                myDetail.append(makeRatedReport(mySummary));
            } else if (myType.isPriced()) {
                myDetail.append(makePricedReport(mySummary));
            } else {
                myDetail.append(makeDebtReport(mySummary));
            }
        }

        /* Access the totals */
        AssetTotal myTotal = myList.getAssetTotal();

        /* Format the totals */
        myOutput.append("<tr><th>Totals</th>");
        myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
        myOutput.append("</tr></tbody></table>");

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        myOutput.append("</body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the market report.
     * @return Web output
     */
    public String getMarketReport() {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Market Report for ");
        myOutput.append(JDataObject.formatField(theDate));
        myOutput.append("</h1></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Cost</th>");
        myOutput.append("<th>Value</th><th>Gains</th>");
        myOutput.append("<th>Profit</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Only process detail items */
            if (myBucket.getBucketType() != BucketType.ASSETDETAIL) {
                continue;
            }

            /* Access the summary bucket */
            AssetAccount myAsset = (AssetAccount) myBucket;

            /* Access the type */
            AccountType myType = myAsset.getAccountType();

            /* Ignore non-priced items */
            if (!myType.isPriced()) {
                continue;
            }

            /* Format the Asset */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append("<a href=\"#Detail");
            myOutput.append(myAsset.getName());
            myOutput.append("\">");
            myOutput.append(myAsset.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myAsset.getCost()));
            myOutput.append(Report.makeMoneyItem(myAsset.getValue()));
            myOutput.append(Report.makeMoneyItem(myAsset.getGained()));
            myOutput.append(Report.makeMoneyItem(myAsset.getProfit()));
            myOutput.append("</tr>");

            /* If this is not an Endowment */
            if (!myType.isEndowment()) {
                /* Format the detail */
                myDetail.append(makeCapitalEventReport(myAsset));
            }
        }

        /* Access the totals */
        MarketTotal myTotal = myList.getMarketTotal();

        /* Format the totals */
        myOutput.append("<tr><th>Totals</th>");
        myOutput.append(Report.makeMoneyTotal(myTotal.getCost()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getGained()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getProfit()));
        myOutput.append("</tr></tbody></table>");

        /* Add the detail */
        myOutput.append(myDetail);

        /* Terminate the html */
        myOutput.append("</body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the income/expense report.
     * @return Web output
     */
    public String getIncomeReport() {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Income/Expense Report for ");
        myOutput.append(theDate.getYear());
        myOutput.append("</h1></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th rowspan=\"2\">Name</th>");
        myOutput.append("<th colspan=\"2\">");
        myOutput.append(theDate.getYear());
        myOutput.append("</th>");
        myOutput.append("<th colspan=\"2\">");
        myOutput.append(theDate.getYear() - 1);
        myOutput.append("</th></thead>");
        myOutput.append("<thead><th>Income</th><th>Expense</th>");
        myOutput.append("<th>Income</th><th>Expense</th></thead>");
        myOutput.append("<tbody>");

        /* Create the bucket iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip bucket if this is not an external account */
            if (myBucket.getBucketType() != BucketType.EXTERNALDETAIL) {
                continue;
            }

            /* Access the account */
            ExternalAccount myExternal = (ExternalAccount) myBucket;

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myExternal.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myExternal.getIncome()));
            myOutput.append(Report.makeMoneyItem(myExternal.getExpense()));
            myOutput.append(Report.makeMoneyItem(myExternal.getPrevIncome()));
            myOutput.append(Report.makeMoneyItem(myExternal.getPrevExpense()));
            myOutput.append("</tr>");
        }

        /* Access the totals */
        ExternalTotal myTotal = myList.getExternalTotal();

        /* Format the totals */
        myOutput.append("<tr><th>Totals</th>");
        myOutput.append(Report.makeMoneyTotal(myTotal.getIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getExpense()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getPrevIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotal.getPrevExpense()));
        myOutput.append("</tr>");

        /* Format the profit */
        myOutput.append("<tr><th>Profit</th>");
        myOutput.append(Report.makeMoneyProfit(myTotal.getProfit()));
        myOutput.append(Report.makeMoneyProfit(myTotal.getPrevProfit()));
        myOutput.append("</tr></tbody></table></body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a standard yearly report element.
     * @param pSummary the class of the element
     * @return Web output
     */
    public StringBuilder makeStandardReport(final AssetSummary pSummary) {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the type */
        AccountType myType = pSummary.getAccountType();

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pSummary.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pSummary.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th rowspan=\"2\">Name</th><th colspan=\"2\">Value</th></thead>");
        myOutput.append("<thead><th>");
        myOutput.append(theDate.getYear());
        myOutput.append("</th><th>");
        myOutput.append(theDate.getYear() - 1);
        myOutput.append("</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip record if not a value account */
            if (!(myBucket instanceof ValueAccount)) {
                continue;
            }

            /* Access the bucket */
            ValueAccount myValue = (ValueAccount) myBucket;

            /* Skip record if incorrect type */
            if (!Difference.isEqual(myValue.getAccountType(), myType)) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myValue.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myValue.getValue()));
            myOutput.append(Report.makeMoneyItem(myValue.getPrevValue()));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
        myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
        myOutput.append(Report.makeMoneyTotal(pSummary.getPrevValue()));
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a rated instant report element.
     * @param pSummary the class of the element
     * @return Web output
     */
    private StringBuilder makeRatedReport(final AssetSummary pSummary) {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the type */
        AccountType myType = pSummary.getAccountType();

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pSummary.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pSummary.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Value</th>");
        myOutput.append("<th>Rate</th><th>Maturity</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip record if this is not a money detail */
            if (myBucket.getBucketType() != BucketType.MONEYDETAIL) {
                continue;
            }

            /* Access the bucket */
            MoneyAccount myMoney = (MoneyAccount) myBucket;

            /* Skip record if incorrect type */
            if (!Difference.isEqual(myMoney.getAccountType(), myType)) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myMoney.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myMoney.getValue()));
            myOutput.append(Report.makeRateItem(myMoney.getRate()));
            myOutput.append(Report.makeDateItem(myMoney.getMaturity()));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
        myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
        myOutput.append("<td/><td/>");
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a debt instant report element.
     * @param pSummary the class of element
     * @return Web output
     */
    public StringBuilder makeDebtReport(final AssetSummary pSummary) {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the type */
        AccountType myType = pSummary.getAccountType();

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pSummary.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pSummary.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip record if this is not debt detail */
            if (myBucket.getBucketType() != BucketType.DEBTDETAIL) {
                continue;
            }

            /* Access the bucket */
            DebtAccount myDebt = (DebtAccount) myBucket;

            /* Skip record if incorrect type */
            if (!Difference.isEqual(myDebt.getAccountType(), myType)) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myDebt.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myDebt.getValue()));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
        myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a priced instant report element.
     * @param pSummary the class of element
     * @return Web output
     */
    public StringBuilder makePricedReport(final AssetSummary pSummary) {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Access the type */
        AccountType myType = pSummary.getAccountType();

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pSummary.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pSummary.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Units</th>");
        myOutput.append("<th>Price</th><th>Value</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip record if this is not asset detail */
            if (myBucket.getBucketType() != BucketType.ASSETDETAIL) {
                continue;
            }

            /* Access the bucket */
            AssetAccount myAsset = (AssetAccount) myBucket;

            /* Skip record if incorrect type */
            if (!Difference.isEqual(myAsset.getAccountType(), myType)) {
                continue;
            }

            /* Skip irrelevant records */
            if (!myAsset.isRelevant()) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myAsset.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeUnitsItem(myAsset.getUnits()));
            myOutput.append(Report.makePriceItem(myAsset.getPrice()));
            myOutput.append(Report.makeMoneyItem(myAsset.getValue()));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
        myOutput.append("<td/><td/>");
        myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a capital event report element.
     * @param pAsset the asset to report on
     * @return Web output
     */
    public StringBuilder makeCapitalEventReport(final AssetAccount pAsset) {
        /* Access the event lists */
        CapitalEventList myList = pAsset.getCapitalEvents();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pAsset.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pAsset.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">");
        myOutput.append("<thead><th>Date</th><th>DeltaUnits</th>");
        myOutput.append("<th>DeltaCost</th><th>DeltaGains</th><th>Dividend</th></thead>");
        myOutput.append("<tbody>");

        /* Access the iterator */
        Iterator<CapitalEvent> myIterator = myList.iterator();

        /* Loop through the Events */
        while (myIterator.hasNext()) {
            CapitalEvent myEvent = myIterator.next();

            /* Skip record if this is not based on an event (at present) */
            if (myEvent.getEvent() == null) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th>");
            myOutput.append(JDataObject.formatField(myEvent.getDate()));
            myOutput.append("</th>");
            myOutput.append(Report.makeUnitsItem((Units) myEvent
                    .findAttribute(CapitalEvent.CAPITAL_DELTAUNITS)));
            myOutput.append(Report.makeMoneyItem((Money) myEvent
                    .findAttribute(CapitalEvent.CAPITAL_DELTACOST)));
            myOutput.append(Report.makeMoneyItem((Money) myEvent
                    .findAttribute(CapitalEvent.CAPITAL_DELTAGAINS)));
            myOutput.append(Report.makeMoneyItem((Money) myEvent
                    .findAttribute(CapitalEvent.CAPITAL_DELTADIVIDEND)));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Totals</a></th>");
        myOutput.append(Report.makeUnitsItem(pAsset.getUnits()));
        myOutput.append(Report.makeMoneyItem(pAsset.getCost()));
        myOutput.append(Report.makeMoneyItem(pAsset.getGained()));
        myOutput.append("<td/></tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a web output of the transaction report.
     * @return Web output
     */
    public String getTransReport() {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Transaction Report for ");
        myOutput.append(theDate.getYear());
        myOutput.append("</h1></a>");
        myOutput.append("<a name=\"TransactionTotals\"><h2 align=\"center\">Transaction Totals</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
        myOutput.append("<thead><th>");
        myOutput.append(theDate.getYear());
        myOutput.append("</th><th>");
        myOutput.append(theDate.getYear() - 1);
        myOutput.append("</th></thead>");
        myOutput.append("<tbody>");

        /* Access the bucket iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Transaction Summary Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Switch on bucket type */
            switch (myBucket.getBucketType()) {
            /* Summary */
                case TRANSSUMMARY:
                    TransSummary mySummary = (TransSummary) myBucket;

                    /* Format the detail */
                    myOutput.append("<tr><th align=\"center\">" + mySummary.getName() + "</th>");
                    myOutput.append(Report.makeMoneyItem(mySummary.getAmount()));
                    myOutput.append(Report.makeMoneyItem(mySummary.getPrevAmount()));
                    myOutput.append("</tr>");
                    break;
                /* Total */
                case TRANSTOTAL:
                    TransTotal myTotal = (TransTotal) myBucket;

                    /* Format the detail */
                    myOutput.append("<tr><th align=\"center\">" + myTotal.getName() + "</th>");
                    myOutput.append(Report.makeMoneyItem(myTotal.getAmount()));
                    myOutput.append(Report.makeMoneyItem(myTotal.getPrevAmount()));
                    myOutput.append("</tr>");
                    break;
                default:
                    break;
            }
        }

        /* Format the next table */
        myOutput.append("</tbody></table>");
        myOutput.append("<a name=\"Trans\"><h2 align=\"center\">Transaction Breakdown</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th rowspan=\"2\">Class</th>");
        myOutput.append("<th colspan=\"2\">");
        myOutput.append(theDate.getYear());
        myOutput.append("</th><th  colspan=\"2\">");
        myOutput.append(theDate.getYear() - 1);
        myOutput.append("</th></thead>");
        myOutput.append("<thead><th>Value</th><th>TaxCredit</th><th>Value</th><th>TaxCredit</th></thead>");
        myOutput.append("<tbody>");

        /* Access a new bucket iterator */
        myIterator = myList.iterator();

        /* Loop through the Transaction Summary Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip entries that are not TransDetail */
            if (myBucket.getBucketType() != BucketType.TRANSDETAIL) {
                continue;
            }

            /* Access the detail */
            TransDetail myDetail = (TransDetail) myBucket;

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myDetail.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myDetail.getAmount()));
            myOutput.append(Report.makeMoneyItem(myDetail.getTaxCredit()));
            myOutput.append(Report.makeMoneyItem(myDetail.getPrevAmount()));
            myOutput.append(Report.makeMoneyItem(myDetail.getPrevTax()));
            myOutput.append("</tr>");
        }

        /* Close the table */
        myOutput.append("</tbody></table></body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a web output of the taxation report.
     * @return Web output
     */
    public String getTaxReport() {
        /* Ensure that tax has been calculated */
        theAnalysisYear.calculateTax();

        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        StringBuilder myDetail = new StringBuilder(BUFFER_LEN);
        TaxDetail myTax;

        /* Initialise the detail */
        myDetail.append("<h1 align=\"center\">Taxation Breakdown</h1>");

        /* Format the header */
        myOutput.append("<html><body><a name=\"Top\">");
        myOutput.append("<h1 align=\"center\">Taxation Report for ");
        myOutput.append(theDate.getYear());
        myOutput.append("</h1></a>");
        myOutput.append("<a name=\"TaxSummary\"><h2 align=\"center\">Taxation Summary</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Class</th><th>Total Income</th><th>Taxation Due</th></thead>");
        myOutput.append("<tbody>");

        /* Access the tax bucket iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Tax Summary Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip the non-summary elements */
            if (myBucket.getBucketType() != BucketType.TAXSUMMARY) {
                continue;
            }

            /* Access the tax detail */
            myTax = (TaxDetail) myBucket;

            /* Format the line */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append("<a href=\"#Detail");
            myOutput.append(myTax.getName());
            myOutput.append("\">");
            myOutput.append(myTax.getName());
            myOutput.append("</a></th>");
            myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
            myOutput.append(Report.makeMoneyItem(myTax.getTaxation()));
            myOutput.append("</tr>");

            /* Format the detail */
            myDetail.append(makeTaxReport(myTax));
        }

        /* Access the Total taxation bucket */
        myTax = myList.getTaxDetail(TaxClass.TOTALTAXATION);
        myOutput.append("<tr><th align=\"center\">");
        myOutput.append(myTax.getName());
        myOutput.append("</th>");
        myOutput.append(Report.makeMoneyTotal(myTax.getAmount()));
        myOutput.append(Report.makeMoneyTotal(myTax.getTaxation()));
        myOutput.append("</tr>");

        /* Access the Tax Paid bucket */
        TransSummary myTrans = myList.getTransSummary(TaxClass.TAXPAID);
        myOutput.append("<tr><th align=\"center\">");
        myOutput.append(myTrans.getName());
        myOutput.append("</th>");
        myOutput.append(Report.makeMoneyTotal(new Money(0)));
        myOutput.append(Report.makeMoneyTotal(myTrans.getAmount()));
        myOutput.append("</tr>");

        /* Access the Tax Profit bucket */
        myTax = myList.getTaxDetail(TaxClass.TAXPROFITLOSS);
        myOutput.append("<tr><th align=\"center\">");
        myOutput.append(myTax.getName());
        myOutput.append("</th>");
        myOutput.append(Report.makeMoneyTotal(myTax.getAmount()));
        myOutput.append(Report.makeMoneyTotal(myTax.getTaxation()));
        myOutput.append("</tr>");

        /* Finish the table */
        myOutput.append("</tbody></table>");

        /* Format the tax parameters */
        myOutput.append("<a name=\"TaxParms\"><h1 align=\"center\">Taxation Parameters</h1></a>");

        /* Format the allowances */
        myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">Allowances</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
        myOutput.append("<tbody>");
        myOutput.append("<tr><th>PersonalAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getAllowance()));
        myOutput.append("</tr>");
        myOutput.append("<tr><th>Age 65-74 PersonalAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getLoAgeAllow()));
        myOutput.append("</tr>");
        myOutput.append("<tr><th>Age 75+ PersonalAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getHiAgeAllow()));
        myOutput.append("</tr>");
        myOutput.append("<tr><th>RentalAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getRentalAllowance()));
        myOutput.append("</tr>");
        myOutput.append("<tr><th>CapitalAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getCapitalAllow()));
        myOutput.append("</tr>");
        myOutput.append("<tr><th>Income Limit for AgeAllowance</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getAgeAllowLimit()));
        myOutput.append("</tr>");
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append("<tr><th>Income Limit for PersonalAllowance</th>");
            myOutput.append(Report.makeMoneyItem(theYear.getAddAllowLimit()));
            myOutput.append("</tr>");
        }
        myOutput.append("</tbody></table>");

        /* Format the Rates */
        myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxRates</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>IncomeType</th><th>LoRate</th>");
        myOutput.append("<th>BasicRate</th><th>HiRate</th>");
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append("<th>AdditionalRate</th>");
        }
        myOutput.append("</thead><tbody>");
        myOutput.append("<tr><th>Salary/Rental</th>");
        myOutput.append(Report.makeRateItem(theYear.hasLoSalaryBand() ? theYear.getLoTaxRate() : null));
        myOutput.append(Report.makeRateItem(theYear.getBasicTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
        }
        myOutput.append("</tr>");
        myOutput.append("<tr><th>Interest</th>");
        myOutput.append(Report.makeRateItem(theYear.getLoTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getIntTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
        }
        myOutput.append("</tr>");
        myOutput.append("<tr><th>Dividends</th>");
        myOutput.append(Report.makeRateItem(null));
        myOutput.append(Report.makeRateItem(theYear.getDivTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getHiDivTaxRate()));
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append(Report.makeRateItem(theYear.getAddDivTaxRate()));
        }
        myOutput.append("</tr>");
        myOutput.append("<tr><th>TaxableGains</th>");
        myOutput.append(Report.makeRateItem(null));
        myOutput.append(Report.makeRateItem(theYear.getBasicTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
        }
        myOutput.append("</tr>");
        myOutput.append("<tr><th>CapitalGains</th>");
        myOutput.append(Report.makeRateItem(null));
        myOutput.append(Report.makeRateItem(theYear.getCapTaxRate()));
        myOutput.append(Report.makeRateItem(theYear.getHiCapTaxRate()));
        if (theYear.hasAdditionalTaxBand()) {
            myOutput.append(Report.makeRateItem(null));
        }
        myOutput.append("</tr>");
        myOutput.append("</tbody></table>");

        /* Format the tax bands */
        myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxBands</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
        myOutput.append("<tbody>");
        myOutput.append("<tr><th>Age for Tax Year</th>");
        myOutput.append("<td align=\"right\" color=\"blue\">");
        myOutput.append(theAnalysis.getAge());
        myOutput.append("</td></tr>");

        /* Access the original allowance */
        myTax = myList.getTaxDetail(TaxClass.ORIGALLOW);
        myOutput.append("<tr><th>Personal Allowance</th>");
        myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
        myOutput.append("</tr>");

        /* if we have adjusted the allowance */
        if (theAnalysis.hasReducedAllow()) {
            /* Access the gross income */
            myTax = myList.getTaxDetail(TaxClass.GROSSINCOME);
            myOutput.append("<tr><th>Gross Taxable Income</th>");
            myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
            myOutput.append("</tr>");

            /* Access the gross income */
            myTax = myList.getTaxDetail(TaxClass.ADJALLOW);
            myOutput.append("<tr><th>Adjusted Allowance</th>");
            myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
            myOutput.append("</tr>");
        }

        /* Access the Low Tax Band */
        if (theYear.getLoBand() != null) {
            myOutput.append("<tr><th>Low Tax Band</th>");
            myOutput.append(Report.makeMoneyItem(theYear.getLoBand()));
            myOutput.append("</tr>");
        }

        /* Access the Basic Tax Band */
        myOutput.append("<tr><th>Basic Tax Band</th>");
        myOutput.append(Report.makeMoneyItem(theYear.getBasicBand()));
        myOutput.append("</tr>");

        /* If we have a high tax band */
        if (theYear.hasAdditionalTaxBand()) {
            /* Access the gross income */
            myTax = myList.getTaxDetail(TaxClass.HITAXBAND);
            myOutput.append("<tr><th>High Tax Band</th>");
            myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
            myOutput.append("</tr>");
        }
        myOutput.append("</tbody></table>");

        /* Add the detail */
        myOutput.append(myDetail);

        /* If we need a tax slice report */
        if (theAnalysis.hasGainsSlices()) {
            myOutput.append(makeTaxSliceReport());
        }

        /* Close the document */
        myOutput.append("</body></html>");

        /* Return the output */
        return myOutput.toString();
    }

    /**
     * Build a standard tax report element.
     * @param pSummary the summary
     * @return Web output
     */
    public StringBuilder makeTaxReport(final TaxDetail pSummary) {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        myOutput.append("<a name=\"Detail");
        myOutput.append(pSummary.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pSummary.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Class</th><th>Income</th>");
        myOutput.append("<th>Rate</th><th>Taxation Due</th></thead>");
        myOutput.append("<tbody>");

        /* Access the tax bucket iterator */
        Iterator<AnalysisBucket> myIterator = myList.iterator();

        /* Loop through the Transaction Detail Buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myBucket = myIterator.next();

            /* Skip non-detail buckets */
            if (myBucket.getBucketType() != BucketType.TAXDETAIL) {
                continue;
            }

            /* Access the bucket */
            TaxDetail myTax = (TaxDetail) myBucket;

            /* Skip record if incorrect parent */
            if (myTax.getParent() != pSummary) {
                continue;
            }

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(myTax.getName());
            myOutput.append("</th>");
            myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
            myOutput.append(Report.makeRateItem(myTax.getRate()));
            myOutput.append(Report.makeMoneyItem(myTax.getTaxation()));
            myOutput.append("</tr>");
        }

        myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
        myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
        myOutput.append(Report.makeRateItem(null));
        myOutput.append(Report.makeMoneyTotal(pSummary.getTaxation()));
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }

    /**
     * Build a tax slice report.
     * @return Web output
     */
    public StringBuilder makeTaxSliceReport() {
        /* Access the bucket lists */
        BucketList myList = theAnalysis.getList();
        ChargeableEventList myCharges = theAnalysis.getCharges();
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Format the detail */
        myOutput.append("<a name=\"DetailChargeableEvents>");
        myOutput.append("<h2 align=\"center\">Chargeable Events</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Date</th><th>Description</th>");
        myOutput.append("<th>Amount</th><th>TaxCredit</th><th>Years</th>");
        myOutput.append("<th>Slice</th><th>Taxation</th></thead>");
        myOutput.append("<tbody>");

        /* Create the list iterator */
        Iterator<ChargeableEvent> myIterator = theAnalysis.getCharges().iterator();

        /* Loop through the Charges */
        while (myIterator.hasNext()) {
            ChargeableEvent myCharge = myIterator.next();

            /* Format the detail */
            myOutput.append("<tr><td>");
            myOutput.append(JDataObject.formatField(myCharge.getDate()));
            myOutput.append("</td><td>");
            myOutput.append(myCharge.getDesc());
            myOutput.append("</td>");
            myOutput.append(Report.makeMoneyItem(myCharge.getAmount()));
            myOutput.append(Report.makeMoneyItem(myCharge.getTaxCredit()));
            myOutput.append("<td>");
            myOutput.append(myCharge.getYears());
            myOutput.append("</td>");
            myOutput.append(Report.makeMoneyItem(myCharge.getSlice()));
            myOutput.append(Report.makeMoneyItem(myCharge.getTaxation()));
            myOutput.append("</tr>");
        }

        /* Format the totals */
        myOutput.append("<tr><th>Totals</th><td/><td/>");
        myOutput.append(Report.makeMoneyTotal(myCharges.getGainsTotal()));
        myOutput.append("<td/>");
        myOutput.append(Report.makeMoneyTotal(myCharges.getSliceTotal()));
        myOutput.append(Report.makeMoneyTotal(myCharges.getTaxTotal()));
        myOutput.append("</tr></tbody></table>");

        /* Access the Summary Tax Due Slice */
        TaxDetail myTax = myList.getTaxDetail(TaxClass.TAXDUESLICE);

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
        IncomeBreakdown myBreakdown = theAnalysisYear.getBreakdown();

        /* Build the report */
        StringBuilder myBuilder = makeAccountListReport(myBreakdown.getSalary(), null);
        myBuilder.append(makeAccountListReport(myBreakdown.getRental(), null));
        myBuilder.append(makeAccountListReport(myBreakdown.getTaxableInterest(), null));
        myBuilder.append(makeAccountListReport(myBreakdown.getTaxableDividend(), null));
        myBuilder.append(makeAccountListReport(myBreakdown.getUnitTrustDividend(), null));
        myBuilder.append(makeAccountListReport(myBreakdown.getTaxFreeInterest(), null));
        myBuilder.append(makeAccountListReport(myBreakdown.getTaxFreeDividend(), null));

        /* Return the report */
        return myBuilder.toString();
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
        myOutput.append("<a name=\"Income");
        myOutput.append(pList.getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(pList.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Account</th>");
        myOutput.append("<th>Gross</th><th>Net<th>TaxCredit</h></thead>");
        myOutput.append("<tbody>");

        /* Access the account iterator */
        Iterator<AccountRecord> myIterator = pList.iterator();

        /* Loop through the Accounts associated with this List */
        while (myIterator.hasNext()) {
            AccountRecord myAccount = myIterator.next();

            /* Access the name of the sublist */
            String myListName = myAccount.getChildren().getName();

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\"><a href=\"#Income");
            myOutput.append(myListName);
            myOutput.append("\">");
            myOutput.append(myAccount.getAccount().getName());
            myOutput.append("</a></th>");

            /* Format the totals */
            IncomeTotals myTotals = myAccount.getTotals();
            myOutput.append(Report.makeMoneyItem(myTotals.getGrossIncome()));
            myOutput.append(Report.makeMoneyItem(myTotals.getNetIncome()));
            myOutput.append(Report.makeMoneyItem(myTotals.getTaxCredit()));
            myOutput.append("</tr>");

            /* If we have events */
            if (myAccount.getEvents().size() > 0) {
                /* Add the child report */
                myDetail.append(makeAccountEventReport(myAccount, pList.getName()));

                /* If we have children */
            } else if (myAccount.getChildren().size() > 0) {
                /* Add the child report */
                myDetail.append(makeAccountListReport(myAccount.getChildren(), pList.getName()));
            }
        }

        /* Build the list totals */
        IncomeTotals myTotals = pList.getTotals();
        myOutput.append("<tr><th>");
        if (pReturn != null) {
            myOutput.append("<a href=\"#Income");
            myOutput.append(pReturn);
            myOutput.append("\">Total</a>");
        } else {
            myOutput.append("Total");
        }
        myOutput.append("</th>");
        myOutput.append(Report.makeMoneyTotal(myTotals.getGrossIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotals.getNetIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotals.getTaxCredit()));
        myOutput.append("</tr></tbody></table>");

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
        myOutput.append("<a name=\"Income");
        myOutput.append(pAccount.getChildren().getName());
        myOutput.append("\">");
        myOutput.append("<h2 align=\"center\">");
        myOutput.append(myAccount.getName());
        myOutput.append("</h2></a>");
        myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myOutput.append("<thead><th>Date</th><th>Description</th>");
        myOutput.append("<th>Gross</th><th>Net<th>TaxCredit</h></thead>");
        myOutput.append("<tbody>");

        /* Access the event iterator */
        Iterator<Event> myIterator = pAccount.getEvents().iterator();

        /* Loop through the Events associated with this Account */
        while (myIterator.hasNext()) {
            Event myEvent = myIterator.next();

            /* Format the detail */
            myOutput.append("<tr><th align=\"center\">");
            myOutput.append(JDataObject.formatField(myEvent.getDate()));
            myOutput.append("</th><td>");
            myOutput.append(myEvent.getDesc());
            myOutput.append("</td>");

            /* Calculate Gross */
            TransactionType myTrans = myEvent.getTransType();
            Money myGross = new Money(myEvent.getAmount());
            Money myNet = myEvent.getAmount();

            /* If we are NatInsurance/Benefit */
            if ((myTrans.getTranClass() == TransClass.NATINSURANCE)
                    || (myTrans.getTranClass() == TransClass.BENEFIT)) {
                /* Just add to gross */
                myNet = new Money(0);
            } else if (myEvent.getTaxCredit() != null) {
                myGross.addAmount(myEvent.getTaxCredit());
            }

            /* Report the values */
            myOutput.append(Report.makeMoneyItem(myGross));
            myOutput.append(Report.makeMoneyItem(myNet));
            myOutput.append(Report.makeMoneyItem(myEvent.getTaxCredit()));
            myOutput.append("</tr>");
        }

        /* Format the totals */
        IncomeTotals myTotals = pAccount.getTotals();
        myOutput.append("<tr><th><a href=\"#");
        myOutput.append(pReturn);
        myOutput.append("\">Total</a></th><td/>");
        myOutput.append(Report.makeMoneyTotal(myTotals.getGrossIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotals.getNetIncome()));
        myOutput.append(Report.makeMoneyTotal(myTotals.getTaxCredit()));
        myOutput.append("</tr></tbody></table>");

        /* Return the output */
        return myOutput;
    }
}