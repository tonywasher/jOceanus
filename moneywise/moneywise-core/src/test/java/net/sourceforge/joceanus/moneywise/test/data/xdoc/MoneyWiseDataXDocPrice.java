/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XDoc Report SecurityPrice Builder.
 */
public class MoneyWiseDataXDocPrice {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The securityPrices.
     */
    private final Map<MoneyWiseSecurity, OceanusPrice> theMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     */
    MoneyWiseDataXDocPrice(final MoneyWiseDataXDocReport pReport,
                           final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theMap = new LinkedHashMap<>();
    }

    /**
     * Create Security Price Table.
     */
    void createSecurityPrices() {
        /* Ignore if we have no security prices */
        if (!hasSecurityPrices()) {
            return;
        }

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_DATA, "SecurityPrices");
        theReport.newTable();

        /* Add the headers */
        createSecurityPriceHeaders();

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process prices */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.SECURITYPRICE.equals(myEvent.getEventType())) {
                /* Update price map */
                final Iterator<MoneyWiseSecurityPrice> myPriceIterator = myEvent.priceIterator();
                while (myPriceIterator.hasNext()) {
                    final MoneyWiseSecurityPrice myPrice = myPriceIterator.next();
                    theMap.put(myPrice.getSecurity(), myPrice.getPrice());
                }

                /* New row */
                theReport.newRow();
                theReport.newCell();
                theReport.setCellValue(myEvent.getDate());

                /* Loop through the map */
                for (OceanusPrice myPrice : theMap.values()) {
                    theReport.newCell();
                    if (myPrice != null) {
                        theReport.setCellValue(myPrice);
                    }
                }

                /* Add row to table */
                theReport.addRowToTable();
            }
        }
    }

    /**
     * Create Security Price Headers.
     */
    private void createSecurityPriceHeaders() {
        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Loop through the events */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.SECURITYPRICE.equals(myEvent.getEventType())) {
                /* Update price map */
                final Iterator<MoneyWiseSecurityPrice> myPriceIterator = myEvent.priceIterator();
                while (myPriceIterator.hasNext()) {
                    final MoneyWiseSecurityPrice myPrice = myPriceIterator.next();
                    final MoneyWiseSecurity mySecurity = myPrice.getSecurity();
                    if (!theMap.containsKey(mySecurity)) {
                        /* Add header */
                        theReport.newHeader();
                        theReport.setCellValue(mySecurity.getName());

                        /* Note the security */
                        theMap.put(mySecurity, null);
                    }
                }
            }
        }

        /* Add the headers */
        theReport.addRowToTable();
    }

    /**
     * Do we have Security Prices?
     * @return true/false
     */
    private boolean hasSecurityPrices() {
        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Return true if we have security prices */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.SECURITYPRICE.equals(myEvent.getEventType())) {
                return true;
            }
        }

        /* No security prices */
        return false;
    }
}
