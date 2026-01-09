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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XDoc Report XchangeRate Builder.
 */
public class MoneyWiseDataXDocRate {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The xchangeRate.
     */
    private final Map<MoneyWiseCurrency, OceanusRatio> theMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     */
    MoneyWiseDataXDocRate(final MoneyWiseDataXDocReport pReport,
                          final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theMap = new LinkedHashMap<>();
    }

    /**
     * Create ExchangeRate Table.
     */
    void createExchangeRates() {
        /* Ignore if we have no exchangeRates */
        if (!hasExchangeRates()) {
            return;
        }

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_DATA, "ExchangeRates");
        theReport.newTable();

        /* Add the headers */
        createExchangeRateHeaders();

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process rates */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.XCHANGERATE.equals(myEvent.getEventType())) {
                /* Update rate map */
                final Iterator<MoneyWiseExchangeRate> myRateIterator = myEvent.xchgRateIterator();
                while (myRateIterator.hasNext()) {
                    final MoneyWiseExchangeRate myRate = myRateIterator.next();
                    theMap.put(myRate.getToCurrency(), myRate.getExchangeRate());
                }

                /* New row */
                theReport.newRow();
                theReport.newCell();
                theReport.setCellValue(myEvent.getDate());

                /* Loop through the map */
                for (OceanusRatio myRate : theMap.values()) {
                    theReport.newCell();
                    if (myRate != null) {
                        theReport.setCellValue(myRate);
                    }
                }

                /* Add row to table */
                theReport.addRowToTable();
            }
        }
    }

    /**
     * Create ExchangeRate Headers.
     */
    private void createExchangeRateHeaders() {
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
            if (MoneyWiseXAnalysisEventType.XCHANGERATE.equals(myEvent.getEventType())) {
                /* Update rate map */
                final Iterator<MoneyWiseExchangeRate> myRateIterator = myEvent.xchgRateIterator();
                while (myRateIterator.hasNext()) {
                    final MoneyWiseExchangeRate myRate = myRateIterator.next();
                    final MoneyWiseCurrency myCurrency = myRate.getToCurrency();
                    if (!theMap.containsKey(myCurrency)) {
                        /* Add header */
                        theReport.newHeader();
                        theReport.setCellValue(myCurrency.getName());

                        /* Note the currency */
                        theMap.put(myCurrency, null);
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
    private boolean hasExchangeRates() {
        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Return true if we have exchange rates */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.XCHANGERATE.equals(myEvent.getEventType())) {
                return true;
            }
        }

        /* No exchange Rates */
        return false;
    }
}
