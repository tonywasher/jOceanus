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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XDoc Report Portfolio Builder.
 */
public class MoneyWiseDataXDocPortfolio {
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
     * The securities builder.
     */
    private final MoneyWiseDataXDocSecurity theSecurities;

    /**
     * The value map.
     */
    private final Map<MoneyWisePortfolio, OceanusMoney> theValueMap;

    /**
     * The foreign map.
     */
    private final Map<MoneyWisePortfolio, OceanusMoney> theForeignMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     * @param pParents the parents list
     * @param pSecurity the security builder
     */
    MoneyWiseDataXDocPortfolio(final MoneyWiseDataXDocReport pReport,
                               final MoneyWiseDataTestCase pTest,
                               final List<MoneyWisePayee> pParents,
                               final MoneyWiseDataXDocSecurity pSecurity) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theParents = pParents;
        theSecurities = pSecurity;
        theValueMap = new HashMap<>();
        theForeignMap = new HashMap<>();
    }

    /**
     * create portfolio definitions table.
     */
    void createPortfolioDefinitions() {
        /* Obtain the portfolios and return if we have none */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        if (myPortfolios.isEmpty()) {
            return;
        }

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Portfolio Accounts");
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
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_OPENING);
        theReport.addRowToTable();

        /* Create the detail */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortIterator.next();
            final MoneyWisePortfolio myPortfolio = myBucket.getPortfolio();

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myPortfolio.getName());

            /* Set parent */
            final MoneyWisePayee myParent = myPortfolio.getParent();
            theReport.newCell();
            theReport.setCellValue(myParent.getName());
            if (!theParents.contains(myParent)) {
                theParents.add(myParent);
            }

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myPortfolio.getCategory().getName());

            /* Set currency */
            theReport.newCell();
            theReport.setCellValue(myPortfolio.getAssetCurrency().getName());

            /* Set opening balance */
            theReport.newCell();
            final OceanusMoney myStarting = myPortfolio.getOpeningBalance();
            if (myStarting != null) {
                theReport.setCellValue(myStarting);
            }

            /* Add row to table */
            theReport.addRowToTable();
        }
    }

    /**
     * create main portfolio headers.
     * @param pForeign are there foreign assets?
     * @return the number of header cells
     */
    int createMainPortfolioHeaders(final boolean pForeign) {
        /* Create the initial headers */
        int myNumCells = 0;
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortIterator.next();

            /* Don't process portfolio cash if it is idle */
            final MoneyWiseXAnalysisPortfolioCashBucket myCash = myBucket.getPortfolioCash();
            if (!myCash.isIdle() || myCash.isActive()) {
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
                theReport.setCellValue(myBucket.getName());
                myNumCells++;
            }

            /* Add security headers */
            myNumCells += theSecurities.createMainHoldingHeaders(myBucket, pForeign);
        }

        /* Return the number of cells */
        return myNumCells;
    }

    /**
     * create foreign portfolio headers.
     */
    void createForeignPortfolioHeaders() {
        /* Create the initial headers */
        final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortIterator.next();

            /* Don't process portfolio cash if it is idle */
            final MoneyWiseXAnalysisPortfolioCashBucket myCash = myBucket.getPortfolioCash();
            if (!myCash.isIdle() || myCash.isActive()) {
                /* Create foreign headers for portfolio if necessary */
                if (myBucket.isForeignCurrency()) {
                    theReport.newHeader();
                    theReport.setCellValue(myBucket.getPortfolio().getAssetCurrency().getName());
                    theReport.newHeader();
                    theReport.setCellValue(myCurrency.getName());
                }
            }

            /* Create foreign headers for securities */
            theSecurities.createForeignHoldingHeaders(myBucket);
        }
    }

    /**
     * update portfolio asset row for event.
     * @param pEvent the event
     * @return isNonEmpty true/false
     */
    boolean updatePortfolioAssetRow(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the portfolios */
        boolean nonEmpty = false;
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortIterator.next();
            final MoneyWisePortfolio myPortfolio = myBucket.getPortfolio();
            final MoneyWiseXAnalysisPortfolioCashBucket myCash = myBucket.getPortfolioCash();

            /* Don't process portfolio cash if it is idle */
            if (!myCash.isIdle() || myCash.isActive()) {
                /* Obtain values for the event */
                final MoneyWiseXAnalysisAccountValues myValues = myCash.getValuesForEvent(pEvent);

                /* If this is a foreign account */
                if (myPortfolio.isForeign()) {
                    /* Report foreign valuation */
                    theReport.newCell();
                    OceanusMoney myForeign = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
                    myForeign = myForeign == null ? theForeignMap.get(myPortfolio) : myForeign;
                    if (myForeign != null) {
                        theReport.setCellValue(myForeign);
                        theForeignMap.put(myPortfolio, myForeign);
                    }
                }

                /* Report standard valuation */
                theReport.newCell();
                OceanusMoney myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
                myValue = myValue == null ? theValueMap.get(myPortfolio) : myValue;
                if (myValue != null) {
                    theReport.setCellValue(myValue);
                    theValueMap.put(myPortfolio, myValue);
                }
            }

            /* Update holdings for this portfolio */
            nonEmpty |= theSecurities.updateHoldingAssetRow(myBucket, pEvent);
        }

        /* Return nonEmpty indication */
        return nonEmpty || !theValueMap.isEmpty() || !theForeignMap.isEmpty();
    }

    /**
     * Are there any foreign assets?
     * @return true/false
     */
    boolean haveForeignAssets() {
        /* Check for foreign portfolios */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortIterator.next();
            if (myBucket.hasForeignCurrency()) {
                return true;
            }

            /* If we have foreign holdings */
            if (theSecurities.haveForeignAssets(myBucket)) {
                return true;
            }
        }

        /* No foreign assets */
        return false;
    }
}
