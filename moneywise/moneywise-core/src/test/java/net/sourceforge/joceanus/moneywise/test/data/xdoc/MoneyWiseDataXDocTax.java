/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.test.data.xdoc;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisValues;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XDoc Report TaxAnalysis Builder.
 */
public class MoneyWiseDataXDocTax {
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
     * @param pReport the report
     * @param pTest the test case
     */
    MoneyWiseDataXDocTax(final MoneyWiseDataXDocReport pReport,
                         final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
    }

    /**
     * create table.
     */
    void createTaxAnalysis() {
        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ANALYSIS, "TaxAnalysis");
        theReport.newTable();

        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Create the headers */
        final MoneyWiseXAnalysisTaxBasisBucketList myBases = theAnalysis.getTaxBasis();
        Iterator<MoneyWiseXAnalysisTaxBasisBucket> myTaxIterator = myBases.iterator();
        while (myTaxIterator.hasNext()) {
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = myTaxIterator.next();
            theReport.newHeader();
            theReport.setCellValue(myBucket.getName());
        }
        theReport.addRowToTable();

        /* Loop through the events */
        final Map<MoneyWiseTaxBasis, OceanusDecimal> myMap = new HashMap<>();
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
            myTaxIterator = myBases.iterator();
            while (myTaxIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = myTaxIterator.next();
                final MoneyWiseTaxBasis myBasis = myBucket.getTaxBasis();

                theReport.newCell();
                final MoneyWiseXAnalysisTaxBasisValues myValues = myBucket.getValuesForEvent(myEvent);
                OceanusDecimal myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
                myValue = myValue == null ? myMap.get(myBasis) : myValue;
                if (myValue != null) {
                     theReport.setCellValue(myValue);
                     myMap.put(myBasis, myValue);
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
        theReport.newBoldSpanCell(myBases.size());
        theReport.setCellValue(myBases.getTotals().getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS));
        theReport.addRowToTable();
    }
}
