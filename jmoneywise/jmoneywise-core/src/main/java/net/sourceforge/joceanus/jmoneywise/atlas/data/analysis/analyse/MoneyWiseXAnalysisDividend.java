/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse;

/**
 * Dividend Analysis.
 */
public class MoneyWiseXAnalysisDividend {
    /**
     * The transAnalyser.
     */
    private final MoneyWiseXAnalysisTransAnalyser theTrans;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the event analyser.
     */
    MoneyWiseXAnalysisDividend(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theTrans = pAnalyser.getTransAnalyser();
    }

    /**
     * Process a transaction that is a dividend.
     * @param pTrans  the transaction
     */
    void processDividend(final MoneyWiseXAnalysisTransaction pTrans) {
        /* The main security that we are interested in is the debit account */
        //final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        //final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        //TethysMoney myAmount = theHelper.getDebitAmount();
        //final TethysMoney myTaxCredit = theHelper.getTaxCredit();
        //final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();
        //final MoneyWiseTaxCredit myYear = theHelper.getTransaction().getTaxYear();

        /* Obtain detailed category */
        //MoneyWiseTransCategory myCat = myPortfolio.getDetailedCategory(theHelper.getCategory(), myYear);
        //myCat = mySecurity.getDetailedCategory(myCat, myYear);

        /* True debit account is the parent */
        //final MoneyWiseAssetBase myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        //final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        //myPayee.adjustForDebit(theHelper);

        /* Access the Asset Account Bucket */
        //final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        //final boolean isForeign = myAsset.isForeignCurrency();
        //final boolean isReInvest = pCredit instanceof MoneyWiseSecurityHolding;

        /* If this is a foreign dividend */
        //if (isForeign) {
        /* If this is a reInvestment */
        //    if (isReInvest) {
        //        /* Adjust counters */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);
        //        myAsset.getValues().setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getCreditExchangeRate());
        //    }

        /* Switch to local amount */
        //    myAmount = theHelper.getLocalAmount();
        //}

        /* If this is a re-investment */
        //if (isReInvest) {
        /* This amount is added to the cost, so record as the delta cost */
        //    myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);

        /* Record the investment */
        //    myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);

        /* If we have new units */
        //    if (myDeltaUnits != null) {
        /* Record delta units */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
        //    }

        /* If we have a tax credit */
        //    if (myTaxCredit != null) {
        //        /* The Tax Credit is viewed as a received dividend from the account */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myTaxCredit);
        //    }

        /* else we are paying out to another account */
        //} else {
        /* Adjust the dividend total for this asset */
        //    final TethysMoney myAdjust = new TethysMoney(myAmount);

        /* Any tax credit is viewed as a realised dividend from the account */
        //    if (myTaxCredit != null) {
        //        myAdjust.addAmount(myTaxCredit);
        //    }

        /* The Dividend is viewed as a dividend from the account */
        //   myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myAdjust);

        /* Adjust the credit account bucket */
        //    final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket((MoneyWiseAssetBase) pCredit);
        //    myBucket.adjustForCredit(theHelper);
        //}

        /* Register the transaction */
        //myAsset.registerTransaction(theHelper);

        /* Adjust the tax payments */
        //theTaxMan.adjustForTaxPayments(theHelper);

        /* Adjust the relevant category buckets */
        //theCategoryBuckets.adjustCategories(theHelper, myCat);
    }
}
