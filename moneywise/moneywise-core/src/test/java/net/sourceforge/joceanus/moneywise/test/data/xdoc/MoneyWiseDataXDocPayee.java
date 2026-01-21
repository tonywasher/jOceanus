/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.moneywise.test.data.xdoc;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XDoc Report Payee Builder.
 */
public class MoneyWiseDataXDocPayee {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The parent payees.
     */
    private List<MoneyWisePayee> theParents;

    /**
     * Constructor.
     *
     * @param pReport the report
     * @param pTest   the test case
     */
    MoneyWiseDataXDocPayee(final MoneyWiseDataXDocReport pReport,
                           final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
    }

    /**
     * Declare parent payees.
     *
     * @param pPayees the parent payees
     */
    void declareParentPayees(final List<MoneyWisePayee> pPayees) {
        theParents = pPayees;
    }

    /**
     * create payee definitions table.
     */
    void createPayeeDefinitions() {
        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Payee Accounts");
        theReport.newTable();

        /* Add the headers */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_NAME);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_CATEGORY);
        theReport.addRowToTable();

        /* Create the detail */
        final MoneyWiseXAnalysisPayeeBucketList myBuckets = theAnalysis.getPayees();
        final Iterator<MoneyWiseXAnalysisPayeeBucket> myPayeeIterator = myBuckets.iterator();
        while (myPayeeIterator.hasNext()) {
            final MoneyWiseXAnalysisPayeeBucket myBucket = myPayeeIterator.next();
            final MoneyWisePayee myPayee = myBucket.getPayee();

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myPayee.getName());

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myPayee.getCategoryName());

            /* Add row to table */
            theReport.addRowToTable();

            /* Remove payee from parents list if present */
            theParents.remove(myPayee);
        }

        /* Add any missing parents */
        for (MoneyWisePayee myPayee : theParents) {
            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myPayee.getName());

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myPayee.getCategoryName());
            theReport.addRowToTable();
        }
    }

    /**
     * create payee analysis table.
     */
    void createPayeeAnalysis() {
        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ANALYSIS, "PayeeAnalysis");
        theReport.newTable();

        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Create the headers */
        final MoneyWiseXAnalysisPayeeBucketList myBuckets = theAnalysis.getPayees();
        Iterator<MoneyWiseXAnalysisPayeeBucket> myPayeeIterator = myBuckets.iterator();
        while (myPayeeIterator.hasNext()) {
            final MoneyWiseXAnalysisPayeeBucket myBucket = myPayeeIterator.next();

            theReport.newHeader();
            theReport.setCellValue(myBucket.getName());
        }
        theReport.addRowToTable();

        /* Loop through the events */
        final Map<MoneyWisePayee, OceanusDecimal> myMap = new HashMap<>();
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process events/prices/XchgRates */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            switch (myEvent.getEventType()) {
                case SECURITYPRICE:
                case XCHANGERATE:
                case TRANSACTION:
                    break;
                default:
                    continue;
            }

            /* Create the new row */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myEvent.getDate());

            /* Create the detail */
            myPayeeIterator = myBuckets.iterator();
            while (myPayeeIterator.hasNext()) {
                final MoneyWiseXAnalysisPayeeBucket myBucket = myPayeeIterator.next();
                final MoneyWisePayee myPayee = myBucket.getPayee();

                theReport.newCell();
                final MoneyWiseXAnalysisPayeeValues myValues = myBucket.getValuesForEvent(myEvent);
                OceanusDecimal myValue = calculateProfit(myValues);
                myValue = myValue == null ? myMap.get(myPayee) : myValue;
                if (myValue != null) {
                    theReport.setCellValue(myValue);
                    myMap.put(myPayee, myValue);
                }
            }
            if (!myMap.isEmpty()) {
                theReport.addRowToTable();
            }
        }

        /* Create the totals row */
        theReport.newRow();
        theReport.newCell();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_PROFIT);
        theReport.newBoldSpanCell(myMap.size());
        theReport.setCellValue(myBuckets.getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisPayeeAttr.PROFIT));
        theReport.addRowToTable();
    }

    /**
     * Calculate profit.
     *
     * @param pValues the values
     * @return the profit
     */
    private OceanusMoney calculateProfit(final MoneyWiseXAnalysisPayeeValues pValues) {
        OceanusMoney myProfit = null;
        if (pValues != null) {
            final OceanusMoney myIncome = pValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
            final OceanusMoney myExpense = pValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE);
            if (myExpense == null) {
                myProfit = myIncome;
            } else {
                myProfit = new OceanusMoney(myExpense);
                myProfit.negate();
                if (myIncome != null) {
                    myProfit.addAmount(myIncome);
                }
            }
        }
        return myProfit;
    }
}
