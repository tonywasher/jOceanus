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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XDoc Report Loan Builder.
 */
public class MoneyWiseDataXDocLoan {
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
    private final Map<MoneyWiseLoan, OceanusMoney> theValueMap;

    /**
     * The foreign map.
     */
    private final Map<MoneyWiseLoan, OceanusMoney> theForeignMap;

    /**
     * Constructor.
     * @param pReport the report
     * @param pTest the test case
     * @param pParents the parents list
     */
    MoneyWiseDataXDocLoan(final MoneyWiseDataXDocReport pReport,
                          final MoneyWiseDataTestCase pTest,
                          final List<MoneyWisePayee> pParents) {
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theParents = pParents;
        theValueMap = new HashMap<>();
        theForeignMap = new HashMap<>();
    }

    /**
     * create loan definitions table.
     */
    void createLoanDefinitions() {
        /* Obtain the loans and return if we have none */
        final MoneyWiseXAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        if (myLoans.isEmpty()) {
            return;
        }

        /* Create detail and table */
        theReport.newDetail(MoneyWiseDataXDocBuilder.GRP_ACCOUNTS, "Loan Accounts");
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
        final Iterator<MoneyWiseXAnalysisLoanBucket> myLoanIterator = myLoans.iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myLoanIterator.next();
            final MoneyWiseLoan myLoan = myBucket.getAccount();

            /* Set name */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myLoan.getName());

            /* Set parent */
            final MoneyWisePayee myParent = myLoan.getParent();
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myParent.getName());
            if (!theParents.contains(myParent)) {
                theParents.add(myParent);
            }

            /* Set type */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myLoan.getCategory().getCategoryType().getName());

            /* Set currency */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myLoan.getAssetCurrency().getName());

            /* Set opening balance */
            theReport.newRow();
            theReport.newCell();
            final OceanusMoney myStarting = myLoan.getOpeningBalance();
            if (myStarting != null) {
                theReport.setCellValue(myStarting);
            }

            /* Add row to table */
            theReport.addRowToTable();
        }
    }

    /**
     * create main loan headers.
     * @param pForeign are there foreign assets?
     * @return the number of header cells
     */
    int createMainLoanHeaders(final boolean pForeign) {
        /* Create the initial headers */
        int myNumCells = 0;
        final MoneyWiseXAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final Iterator<MoneyWiseXAnalysisLoanBucket> myLoanIterator = myLoans.iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myLoanIterator.next();

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
     * create foreign loan headers.
     */
    void createForeignLoanHeaders() {
        /* Create the initial headers */
        final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
        final MoneyWiseXAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final Iterator<MoneyWiseXAnalysisLoanBucket> myLoanIterator = myLoans.iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myLoanIterator.next();
            if (myBucket.isForeignCurrency()) {
                theReport.newHeader();
                theReport.setCellValue(myBucket.getAccount().getAssetCurrency().getName());
                theReport.newHeader();
                theReport.setCellValue(myCurrency.getName());
            }
        }
    }

    /**
     * update loan asset row for event.
     * @param pEvent the event
     * @return isNonEmpty true/false
     */
    boolean updateLoanAssetRow(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the loans */
        final MoneyWiseXAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final Iterator<MoneyWiseXAnalysisLoanBucket> myLoanIterator = myLoans.iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myLoanIterator.next();
            final MoneyWiseLoan myLoan = myBucket.getAccount();

            /* Obtain values for the event */
            final MoneyWiseXAnalysisAccountValues myValues = myBucket.getValuesForEvent(pEvent);

            /* If this is a foreign account */
            if (myLoan.isForeign()) {
                /* Report foreign valuation */
                theReport.newCell();
                OceanusMoney myForeign = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
                myForeign = myForeign == null ? theForeignMap.get(myLoan) : myForeign;
                if (myForeign != null) {
                    theReport.setCellValue(myForeign);
                    theForeignMap.put(myLoan, myForeign);
                }
            }

            /* Report standard valuation */
            theReport.newCell();
            OceanusMoney myValue = myValues == null ? null : myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
            myValue = myValue == null ? theValueMap.get(myLoan) : myValue;
            if (myValue != null) {
                theReport.setCellValue(myValue);
                theValueMap.put(myLoan, myValue);
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
        /* Check for foreign loans */
        final MoneyWiseXAnalysisLoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        final Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myLoanIterator = myLoans.iterator();
        while (myLoanIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanCategoryBucket myBucket = myLoanIterator.next();
            if (myBucket.hasForeignCurrency()) {
                return true;
            }
        }

        /* No foreign assets */
        return false;
    }
}
