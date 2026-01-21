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
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;

import java.util.Iterator;

/**
 * XDoc Report Transaction Builder.
 */
public class MoneyWiseDataXDocTrans {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The testCase.
     */
    private final MoneyWiseDataTestCase theTest;

    /**
     * Constructor.
     *
     * @param pReport the report
     * @param pTest   the test case
     */
    MoneyWiseDataXDocTrans(final MoneyWiseDataXDocReport pReport,
                           final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theTest = pTest;
        theAnalysis = pTest.getAnalysis();
    }

    /**
     * Create Transactions.
     */
    void createTransactions() {
        /* Create detail and table */
        theReport.newOpenDetail(MoneyWiseDataXDocBuilder.GRP_DATA, "Transactions");
        theReport.newTable();

        /* Add the headers */
        createTransactionHeaders();

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process transactions */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            if (MoneyWiseXAnalysisEventType.TRANSACTION.equals(myType)) {
                /* New row */
                theReport.newRow();
                theReport.newCell();
                theReport.setCellValue(myEvent.getDate());

                /* Set account */
                theReport.newCell();
                final MoneyWiseTransAsset myAccount = myEvent.getAccount();
                if (myAccount instanceof MoneyWiseSecurityHolding) {
                    theReport.setSplitCellValue(myAccount.getName());
                } else {
                    theReport.setCellValue(myAccount.getName());
                }

                /* Set transaction category */
                theReport.newCell();
                theReport.setCellValue(myEvent.getCategory().getName());

                /* Set amount */
                theReport.newCell();
                theReport.setCellValue(myEvent.getAmount());

                /* Set direction */
                theReport.newCell();
                theReport.setCellValue(myEvent.getDirection().toString());

                /* Set partner */
                theReport.newCell();
                final MoneyWiseTransAsset myPartner = myEvent.getPartner();
                if (myPartner instanceof MoneyWiseSecurityHolding) {
                    theReport.setSplitCellValue(myPartner.getName());
                } else if (myPartner != null) {
                    theReport.setCellValue(myPartner.getName());
                }

                /* Loop through the infoClasses */
                for (MoneyWiseTransInfoClass myInfoClass : MoneyWiseTransInfoClass.values()) {
                    /* If we are using this class */
                    if (theTest.useInfoClass(myInfoClass)) {
                        /* Report data */
                        theReport.newCell();
                        final Object myValue = myEvent.getEventInfo(myInfoClass);
                        if (myValue instanceof OceanusDecimal) {
                            theReport.setCellValue((OceanusDecimal) myValue);
                        } else if (myValue instanceof MetisDataNamedItem) {
                            theReport.setCellValue(((MetisDataNamedItem) myValue).getName());
                        } else if (myValue instanceof String) {
                            theReport.setCellValue((String) myValue);
                        }
                    }
                }

                /* Add row to table */
                theReport.addRowToTable();
            }
        }
    }

    /**
     * Create Transaction Headers.
     */
    private void createTransactionHeaders() {
        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Set account */
        theReport.newHeader();
        theReport.setCellValue("Account");

        /* Set transaction category */
        theReport.newHeader();
        theReport.setCellValue("Category");

        /* Set amount */
        theReport.newHeader();
        theReport.setCellValue("Amount");

        /* Set direction */
        theReport.newHeader();
        theReport.setCellValue("Direction");

        /* Set partner */
        theReport.newHeader();
        theReport.setCellValue("Partner");

        /* Loop through the infoClasses */
        for (MoneyWiseTransInfoClass myInfoClass : MoneyWiseTransInfoClass.values()) {
            /* If we are using this class */
            if (theTest.useInfoClass(myInfoClass)) {
                /* Add the header */
                theReport.newHeader();
                theReport.setCellValue(myInfoClass.toString());
            }
        }

        /* Add the headers */
        theReport.addRowToTable();
    }
}
