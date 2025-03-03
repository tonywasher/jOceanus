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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XDoc Report Deposit Builder.
 */
public class MoneyWiseDataXDocDeposit {
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
     * The value map.
     */
    private final Map<MoneyWiseDeposit, OceanusMoney> theValueMap;

    /**
     * The foreign map.
     */
    private final Map<MoneyWiseDeposit, OceanusMoney> theForeignMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     * @param pParents the parents list
     */
    MoneyWiseDataXDocDeposit(final MoneyWiseDataXDocReport pReport,
                             final MoneyWiseDataTestCase pTest,
                             final List<MoneyWisePayee> pParents) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theParents = pParents;
        theValueMap = new HashMap<>();
        theForeignMap = new HashMap<>();
    }

    /**
     * create deposit definitions table.
     */
    void createDepositDefinitions() {
        /* Obtain the deposits and return if we have none */
        final MoneyWiseXAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        if (myDeposits.isEmpty()) {
            return;
        }

        /* Create detail and table */
        theReport.newOpenDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Deposit Accounts");
        theReport.newTable();

        /* Add the headers */
        theReport.newRow();
        theReport.newHeader();
        theReport.setCellValue("Name");
        theReport.newHeader();
        theReport.setCellValue("Parent");
        theReport.newHeader();
        theReport.setCellValue("Category");
        theReport.newHeader();
        theReport.setCellValue("Currency");
        theReport.newHeader();
        theReport.setCellValue("Starting Balance");
        theReport.addRowToTable();

        /* Create the detail */
        Iterator<MoneyWiseXAnalysisDepositBucket> myDepositIterator = myDeposits.iterator();
        while (myDepositIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myDepositIterator.next();
            final MoneyWiseDeposit myDeposit = myBucket.getAccount();

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myDeposit.getName());

            /* Set parent */
            final MoneyWisePayee myParent = myDeposit.getParent();
            theReport.newCell();
            theReport.setCellValue(myParent.getName());
            if (!theParents.contains(myParent)) {
                theParents.add(myParent);
            }

            /* Set type */
            theReport.newCell();
            theReport.setCellValue(myDeposit.getCategory().getCategoryType().getName());

            /* Set currency */
            theReport.newCell();
            theReport.setCellValue(myDeposit.getAssetCurrency().getName());

            /* Set opening balance */
            theReport.newCell();
            final OceanusMoney myStarting = myDeposit.getOpeningBalance();
            if (myStarting != null) {
                theReport.setCellValue(myStarting);
            }
            theReport.addRowToTable();
        }
    }

    /**
     * create main deposit headers.
     * @param pForeign are there foreign assets?
     * @return the number of header cells
     */
    int createMainDepositHeaders(final boolean pForeign) {
        /* Create the initial headers */
        int myNumHeaders = 0;
        final MoneyWiseXAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        Iterator<MoneyWiseXAnalysisDepositBucket> myDepositIterator = myDeposits.iterator();
        while (myDepositIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myDepositIterator.next();

            if (pForeign) {
                if (myBucket.isForeignCurrency()) {
                    theReport.newColSpanHeader(2);
                    myNumHeaders++;
                } else {
                    theReport.newRowSpanHeader(2);
                }
            } else {
                theReport.newHeader();
            }
            theReport.setCellValue(myBucket.getName());
            myNumHeaders++;
        }
        return myNumHeaders;
    }

    /**
     * create foreign deposit headers.
     */
    void createForeignDepositHeaders() {
        /* Create the initial headers */
        final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
        final MoneyWiseXAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        Iterator<MoneyWiseXAnalysisDepositBucket> myDepositIterator = myDeposits.iterator();
        while (myDepositIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myDepositIterator.next();
            if (myBucket.isForeignCurrency()) {
                theReport.newHeader();
                theReport.setCellValue(myBucket.getAccount().getAssetCurrency().getName());
                theReport.newHeader();
                theReport.setCellValue(myCurrency.getName());
            }
        }
    }

    /**
     * update deposit asset row for event.
     * @param pEvent the event
     * @return isNonEmpty true/false
     */
    boolean updateDepositAssetRow(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the deposits */
        final MoneyWiseXAnalysisDepositBucketList myDeposits = theAnalysis.getDeposits();
        Iterator<MoneyWiseXAnalysisDepositBucket> myDepositIterator = myDeposits.iterator();
        while (myDepositIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myDepositIterator.next();
            final MoneyWiseDeposit myDeposit = myBucket.getAccount();

            /* Obtain values for the event */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValuesForEvent(pEvent);

            /* If this is a foreign account */
            if (myDeposit.isForeign()) {
                /* Report foreign valuation */
                theReport.newCell();
                OceanusMoney myForeign = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
                myForeign = myForeign == null ? theForeignMap.get(myDeposit) : myForeign;
                if (myForeign != null) {
                    theReport.setCellValue(myForeign);
                    theForeignMap.put(myDeposit, myForeign);
                }
            }

            /* Report standard valuation */
            theReport.newCell();
            OceanusMoney myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
            myValue = myValue == null ? theValueMap.get(myDeposit) : myValue;
            if (myValue != null) {
                theReport.setCellValue(myValue);
                theValueMap.put(myDeposit, myValue);
            }
        }

        /* Return nonEmpty indication */
        return !theValueMap.isEmpty() || !theForeignMap.isEmpty();
    }

    /**
     * Are there any foreign assets?
     * @return true/false
     */
    boolean haveForeignAssets() {
        /* Check for foreign deposits */
        final MoneyWiseXAnalysisDepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myDepositIterator = myDeposits.iterator();
        while (myDepositIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositCategoryBucket myBucket = myDepositIterator.next();
            if (myBucket.hasForeignCurrency()) {
                return true;
            }
        }

        /* No foreign assets */
        return false;
    }
}
