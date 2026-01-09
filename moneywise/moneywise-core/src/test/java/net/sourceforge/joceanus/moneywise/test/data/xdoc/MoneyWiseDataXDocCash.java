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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XDoc Report Cash Builder.
 */
public class MoneyWiseDataXDocCash {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The value map.
     */
    private final Map<MoneyWiseCash, OceanusMoney> theValueMap;

    /**
     * The foreign map.
     */
    private final Map<MoneyWiseCash, OceanusMoney> theForeignMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     */
    MoneyWiseDataXDocCash(final MoneyWiseDataXDocReport pReport,
                          final MoneyWiseDataTestCase pTest) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theValueMap = new HashMap<>();
        theForeignMap = new HashMap<>();
    }

    /**
     * create cash definitions table.
     */
    void createCashDefinitions() {
        /* Obtain the loans and return if we have none */
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        if (myCash.isEmpty()) {
            return;
        }

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Cash Accounts");
        theReport.newTable();

        /* Add the headers */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_NAME);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_CATEGORY);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_CURRENCY);
        theReport.newHeader();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_OPENING);
        theReport.newHeader();
        theReport.setCellValue("AutoPayee");
        theReport.newHeader();
        theReport.setCellValue("AutoExpense");
        theReport.addRowToTable();

        /* Create the detail */
        final Iterator<MoneyWiseXAnalysisCashBucket> myCashIterator = myCash.iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myCashIterator.next();
            final MoneyWiseCash myAccount = myBucket.getAccount();

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myAccount.getName());

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myAccount.getCategory().getCategoryType().getName());

            /* Set currency */
            theReport.newCell();
            theReport.setCellValue(myAccount.getAssetCurrency().getName());

            /* Set opening balance */
            theReport.newCell();
            final OceanusMoney myStarting = myAccount.getOpeningBalance();
            if (myStarting != null) {
                theReport.setCellValue(myStarting);
            }

            /* Set autoPayee */
            theReport.newCell();
            final MoneyWisePayee myAutoPayee = myAccount.getAutoPayee();
            if (myAutoPayee != null) {
                theReport.setCellValue(myAutoPayee.getName());
            }

            /* Set autoExpense */
            theReport.newCell();
            final MoneyWiseTransCategory myAutoExpense = myAccount.getAutoExpense();
            if (myAutoExpense != null) {
                theReport.setCellValue(myAutoExpense.getName());
            }

            /* Add row to table */
            theReport.addRowToTable();
        }
    }

    /**
     * create main cash headers.
     * @param pForeign are there foreign assets?
     * @return the number of header cells
     */
    int createMainCashHeaders(final boolean pForeign) {
        /* Create the initial headers */
        int myNumCells = 0;
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        final Iterator<MoneyWiseXAnalysisCashBucket> myCashIterator = myCash.iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myCashIterator.next();

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

        /* Return the number of cells */
        return myNumCells;
    }


    /**
     * update cash asset row for event.
     * @param pEvent the event
     * @return isNonEmpty true/false
     */
    boolean updateCashAssetRow(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the cash */
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        final Iterator<MoneyWiseXAnalysisCashBucket> myCashIterator = myCash.iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myCashIterator.next();
            final MoneyWiseCash myAccount = myBucket.getAccount();

            /* Obtain values for the event */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValuesForEvent(pEvent);

            /* If this is a foreign account */
            if (myAccount.isForeign()) {
                /* Report foreign valuation */
                theReport.newCell();
                OceanusMoney myForeign = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
                myForeign = myForeign == null ? theForeignMap.get(myAccount) : myForeign;
                if (myForeign != null) {
                    theReport.setCellValue(myForeign);
                    theForeignMap.put(myAccount, myForeign);
                }
            }

            /* Report standard valuation */
            theReport.newCell();
            OceanusMoney myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
            myValue = myValue == null ? theValueMap.get(myAccount) : myValue;
            if (myValue != null) {
                theReport.setCellValue(myValue);
                theValueMap.put(myAccount, myValue);
            }
        }

        /* Return nonEmpty indication */
        return !theValueMap.isEmpty() || !theForeignMap.isEmpty();
    }

    /**
     * create foreign cash headers.
     */
    void createForeignCashHeaders() {
        /* Create the initial headers */
        final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
        final MoneyWiseXAnalysisCashBucketList myCash = theAnalysis.getCash();
        final Iterator<MoneyWiseXAnalysisCashBucket> myCashIterator = myCash.iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myCashIterator.next();
            if (myBucket.isForeignCurrency()) {
                theReport.newHeader();
                theReport.setCellValue(myBucket.getAccount().getAssetCurrency().getName());
                theReport.newHeader();
                theReport.setCellValue(myCurrency.getName());
            }
        }
    }

    /**
     * Are there any foreign assets?
     * @return true/false
     */
    boolean haveForeignAssets() {
        /* Check for foreign cash */
        final MoneyWiseXAnalysisCashCategoryBucketList myCash = theAnalysis.getCashCategories();
        final Iterator<MoneyWiseXAnalysisCashCategoryBucket> myCashIterator = myCash.iterator();
        while (myCashIterator.hasNext()) {
            final MoneyWiseXAnalysisCashCategoryBucket myBucket = myCashIterator.next();
            if (myBucket.hasForeignCurrency()) {
                return true;
            }
        }

        /* No foreign assets */
        return false;
    }
}
