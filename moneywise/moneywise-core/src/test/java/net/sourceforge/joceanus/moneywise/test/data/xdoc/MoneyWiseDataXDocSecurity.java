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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XDoc Report Security Builder.
 */
public class MoneyWiseDataXDocSecurity {
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
    private final List<MoneyWisePayee> theParents;

    /**
     * The testCase.
     */
    private final MoneyWiseDataTestCase theTest;

    /**
     * The value map.
     */
    private final Map<MoneyWiseSecurityHolding, OceanusMoney> theValueMap;

    /**
     * The foreign map.
     */
    private final Map<MoneyWiseSecurityHolding, OceanusMoney> theForeignMap;

    /**
     * Note if we have no securities.
     */
    private boolean noSecurities;

    /**
     * Note if this is first holding.
     */
    private boolean firstHolding;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     * @param pParents the parents list
     */
    MoneyWiseDataXDocSecurity(final MoneyWiseDataXDocReport pReport,
                              final MoneyWiseDataTestCase pTest,
                              final List<MoneyWisePayee> pParents) {
        theReport = pReport;
        theTest = pTest;
        theAnalysis = theTest.getAnalysis();
        theParents = pParents;
        theValueMap = new HashMap<>();
        theForeignMap = new HashMap<>();
    }

    /**
     * create security definitions table.
     */
    void createSecurityDefinitions() {
        /* Check that we actually have securities */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        noSecurities = true;
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = myPortIterator.next();
            if (!myPortfolio.getSecurities().isEmpty()) {
                noSecurities = false;
            }
        }
        if (noSecurities) {
            return;
        }

        /* Create Security list */
        final List<MoneyWiseSecurity> mySecurities = new ArrayList<>();

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Security Accounts");
        theReport.newTable();

        /* Add the headers */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_NAME);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_PARENT);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_CATEGORY);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_CURRENCY);
        theReport.addRowToTable();

        /* Loop through the portfolios */
        myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = myPortIterator.next();

            /* Loop through the holdings */
            Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = myPortfolio.getSecurities().iterator();
            while (mySecIterator.hasNext()) {
                final MoneyWiseXAnalysisSecurityBucket myBucket = mySecIterator.next();
                final MoneyWiseSecurity mySecurity = myBucket.getSecurity();

                /* Ignore if we have already processed this security */
                if (mySecurities.contains(mySecurity)) {
                    continue;
                }
                mySecurities.add(mySecurity);

                /* Set name */
                theReport.newRow();
                theReport.newCell();
                theReport.setCellValue(mySecurity.getName());

                /* Set parent */
                final MoneyWisePayee myParent = mySecurity.getParent();
                theReport.newCell();
                theReport.setCellValue(myParent.getName());
                if (!theParents.contains(myParent)) {
                    theParents.add(myParent);
                }

                /* Set type */
                theReport.newCell();
                theReport.setCellValue(mySecurity.getCategory().getName());

                /* Set currency */
                theReport.newCell();
                theReport.setCellValue(mySecurity.getAssetCurrency().getName());

                /* Add row to table */
                theReport.addRowToTable();
            }
        }
    }

    /**
     * create main holding headers.
     * @param pPortfolio the portfolio bucket
     * @param pForeign are there foreign assets?
     * @return the number of header cells
     */
    int createMainHoldingHeaders(final MoneyWiseXAnalysisPortfolioBucket pPortfolio,
                                 final boolean pForeign) {
        /* Create the initial headers */
        int myNumCells = 0;
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pPortfolio.getSecurities();
        Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = mySecurities.iterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = mySecIterator.next();

            /* If this is a foreign account */
            if (pForeign) {
                /* Create the correct spanning header */
                if (myBucket.isForeignCurrency()) {
                    theReport.newColSpanHeader(2);
                    myNumCells++;
                } else {
                    theReport.newRowSpanHeader(2);
                }

                /* else standard account */
            } else {
                theReport.newHeader();
            }

            /* Store the name */
            theReport.setSplitCellValue(myBucket.getSecurityHolding().getName());
            myNumCells++;
        }

        /* Return the number of cells */
        return myNumCells;
    }

    /**
     * create foreign holding headers.
     * @param pPortfolio the portfolio bucket
     */
    void createForeignHoldingHeaders(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
        /* Create the initial headers */
        final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pPortfolio.getSecurities();
        Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = mySecurities.iterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = mySecIterator.next();
            if (myBucket.isForeignCurrency()) {
                theReport.newHeader();
                theReport.setCellValue(myBucket.getPortfolio().getAssetCurrency().getName());
                theReport.newHeader();
                theReport.setCellValue(myCurrency.getName());
            }
        }
    }

    /**
     * update holding asset row for event.
     * @param pPortfolio the portfolio bucket
     * @param pEvent the event
     * @return isNonEmpty true/false
     */
    boolean updateHoldingAssetRow(final MoneyWiseXAnalysisPortfolioBucket pPortfolio,
                                  final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the securities */
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pPortfolio.getSecurities();
        Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = mySecurities.iterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = mySecIterator.next();
            final MoneyWiseSecurityHolding myHolding = myBucket.getSecurityHolding();

            /* Obtain values for the event */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValuesForEvent(pEvent);

            /* If this is a foreign account */
            if (myHolding.isForeign()) {
                /* Report foreign valuation */
                theReport.newCell();
                OceanusMoney myForeign = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE);
                myForeign = myForeign == null ? theForeignMap.get(myHolding) : myForeign;
                if (myForeign != null) {
                    theReport.setCellValue(myForeign);
                    theForeignMap.put(myHolding, myForeign);
                }
            }

            /* Report standard valuation */
            theReport.newCell();
            OceanusMoney myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
            myValue = myValue == null ? theValueMap.get(myHolding) : myValue;
            if (myValue != null) {
                theReport.setCellValue(myValue);
                theValueMap.put(myHolding, myValue);
            }
        }

        /* Return nonEmpty indication */
        return !theValueMap.isEmpty() || !theForeignMap.isEmpty();
    }

    /**
     * Are there any foreign assets?
     * @param pPortfolio the portfolio bucket
     * @return true/false
     */
    boolean haveForeignAssets(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
        /* Check for foreign holdings */
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pPortfolio.getSecurities();
        Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = mySecurities.iterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = mySecIterator.next();
            if (myBucket.getSecurity().isForeign()) {
                return true;
            }
        }

        /* No foreign assets */
        return false;
    }

    /**
     * Format Holding history.
     */
    void createHoldingHistory() {
        /* Ignore if we have no securities */
        if (noSecurities) {
            return;
        }

        /* Create the detail */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ANALYSIS, "Holding History");
        firstHolding = true;

        /* Loop through the portfolios */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = theAnalysis.getPortfolios().iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = myPortIterator.next();

            /* Loop through the holdings */
            final Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = myPortfolio.securityIterator();
            while (mySecIterator.hasNext()) {
                createHoldingHistory(mySecIterator.next());
            }
        }
    }

    /**
     * Format Holding history.
     * @param pHolding the security holding bucket
     */
    private void createHoldingHistory(final MoneyWiseXAnalysisSecurityBucket pHolding) {
        /* Create the table */
        if (firstHolding) {
            theReport.newOpenSubDetail(MoneyWiseDataXDocBuilder.GRP_HOLDINGS, pHolding.getDecoratedName());
            firstHolding = false;
        } else {
            theReport.newSubDetail(MoneyWiseDataXDocBuilder.GRP_HOLDINGS, pHolding.getDecoratedName());
        }
        theReport.newTable();
        theReport.newRow();

        /* Add the date header */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Add any required attributes to header */
        for (MoneyWiseXAnalysisSecurityAttr myAttr : MoneyWiseXAnalysisSecurityAttr.values()) {
            /* If we are using this attribute */
            if (theTest.useSecurityAttr(myAttr)) {
                /* Add the header */
                theReport.newHeader();
                theReport.setCellValue(myAttr.toString());
            }
        }
        theReport.addRowToTable();

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process events/prices/XchgRates/openingBalance */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            switch (myEvent.getEventType()) {
                case SECURITYPRICE:
                case XCHANGERATE:
                case TRANSACTION:
                    break;
                default:
                    continue;
            }

            /* Create the new row and add the date */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myEvent.getDate());
            final MoneyWiseXAnalysisSecurityValues myValues = pHolding.getValuesForEvent(myEvent);

            /* Ignore items where we have no values */
            if (myValues == null) {
                continue;
            }

            /* Add any required attribute values */
            for (MoneyWiseXAnalysisSecurityAttr myAttr : MoneyWiseXAnalysisSecurityAttr.values()) {
                /* If we are using this attribute */
                if (theTest.useSecurityAttr(myAttr)) {
                    /* Add the header */
                    theReport.newCell();
                    final OceanusDecimal myValue = myValues.getDecimalValue(myAttr);
                    if (myValue != null) {
                        theReport.setCellValue(myValue);
                    }
                }
            }

            /* Add the row to the table */
            theReport.addRowToTable();
        }
    }
}
