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
package io.github.tonywasher.joceanus.moneywise.test.data.xdoc;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransValues;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XDoc Report Transaction Category Builder.
 */
public class MoneyWiseDataXDocTransCat {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pReport the report
     * @param pTest   the test case
     */
    MoneyWiseDataXDocTransCat(final MoneyWiseDataXDocReport pReport,
                              final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
    }

    /**
     * create transaction definitions table.
     */
    void createTransDefinitions() {
        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "TransactionCategories");
        theReport.newTable();

        /* Add the headers */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue("Name");
        theReport.newHeader();
        theReport.setCellValue("Type");
        theReport.addRowToTable();

        /* Create the detail */
        final MoneyWiseXAnalysisTransCategoryBucketList myBuckets = theAnalysis.getTransCategories();
        final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myTransIterator = myBuckets.iterator();
        while (myTransIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myTransIterator.next();

            /* Ignore parents */
            final MoneyWiseTransCategory myParent = myBucket.getTransactionCategory().getParentCategory();
            if (myParent.isCategoryClass(MoneyWiseTransCategoryClass.TOTALS)) {
                continue;
            }

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myBucket.getName());

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myBucket.getTransactionCategoryType().toString());
            theReport.addRowToTable();
        }
    }

    /**
     * create transaction category analysis table.
     */
    void createTransAnalysis() {
        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ANALYSIS, "TransactionCategoryAnalysis");
        theReport.newTable();

        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Create the headers */
        final MoneyWiseXAnalysisTransCategoryBucketList myBuckets = theAnalysis.getTransCategories();
        Iterator<MoneyWiseXAnalysisTransCategoryBucket> myTransIterator = myBuckets.iterator();
        while (myTransIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myTransIterator.next();

            /* Ignore parents */
            final MoneyWiseTransCategory myParent = myBucket.getTransactionCategory().getParentCategory();
            if (myParent.isCategoryClass(MoneyWiseTransCategoryClass.TOTALS)) {
                continue;
            }

            theReport.newHeader();
            theReport.setSplitCellValue(myBucket.getName());
        }
        theReport.addRowToTable();

        /* Loop through the events */
        final Map<MoneyWiseTransCategory, OceanusDecimal> myMap = new HashMap<>();
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
            myTransIterator = myBuckets.iterator();
            while (myTransIterator.hasNext()) {
                final MoneyWiseXAnalysisTransCategoryBucket myBucket = myTransIterator.next();
                final MoneyWiseTransCategory myCategory = myBucket.getTransactionCategory();

                /* Ignore parents */
                final MoneyWiseTransCategory myParent = myCategory.getParentCategory();
                if (myParent.isCategoryClass(MoneyWiseTransCategoryClass.TOTALS)) {
                    continue;
                }

                theReport.newCell();
                final MoneyWiseXAnalysisTransValues myValues = myBucket.getValuesForEvent(myEvent);
                OceanusDecimal myValue = calculateProfit(myValues);
                myValue = myValue == null ? myMap.get(myCategory) : myValue;
                if (myValue != null) {
                    theReport.setCellValue(myValue);
                    myMap.put(myCategory, myValue);
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
        theReport.setCellValue(myBuckets.getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisTransAttr.PROFIT));
        theReport.addRowToTable();
    }

    /**
     * Calculate profit.
     *
     * @param pValues the values
     * @return the profit
     */
    private OceanusMoney calculateProfit(final MoneyWiseXAnalysisTransValues pValues) {
        OceanusMoney myProfit = null;
        if (pValues != null) {
            final OceanusMoney myIncome = pValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
            final OceanusMoney myExpense = pValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE);
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
