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
 * Share deMerge Analysis.
 */
public class MoneyWiseXAnalysisDeMerger {
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
    MoneyWiseXAnalysisDeMerger(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theTrans = pAnalyser.getAnalyser();
    }

    /**
     * Process a transaction that is a stockDeMerger.
     * @param pTrans  the transaction
     */
    void processStockDeMerger(final MoneyWiseXAnalysisTransaction pTrans) {
//        /* Access the Debit Asset Security Bucket */
//        MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pDebit);
//        MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
//        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
//
//        /* Obtain current cost */
//        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//        final TethysRatio myDilution = theHelper.getDilution();
//        final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();
//
//        /* If we reduced the units */
//        if (myDeltaUnits != null) {
//            /* Record the delta units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//        }
//
//        /* Calculate the cost dilution */
//        final TethysMoney myNewCost = myCost.getDilutedMoney(myDilution);
//        final TethysRatio myCostDilution = new TethysRatio(myNewCost, myCost);
//
//        /* Calculate the delta to the cost */
//        TethysMoney myDeltaCost = new TethysMoney(myNewCost);
//        myDeltaCost.subtractAmount(myCost);
//
//        /* Record the delta cost/investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
//        final boolean isForeignDebit = myAsset.isForeignCurrency();
//        if (isForeignDebit) {
//            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myDebitRate);
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Register the event */
//        myValues = myAsset.registerTransaction(theHelper);
//
//        /* Access the Credit Asset Account Bucket */
//        myAsset = thePortfolioBuckets.getBucket(pCredit);
//
//        /* The deltaCost is transferred to the credit account */
//        myDeltaCost = new TethysMoney(myDeltaCost);
//        myDeltaCost.negate();
//
//        /* Record details */
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
//        if (isForeignDebit) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
//        }
//
//        /* Record the delta cost/investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
//        final boolean isForeignCredit = myAsset.isForeignCurrency();
//        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
//        if (isForeignCredit) {
//            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myCreditRate);
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Get the appropriate prices/rates for the stock */
//        final TethysDate myDate = theHelper.getDate();
//        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myAsset.getSecurity(), myDate);
//        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();
//
//        /* Determine value of the stock being deMerged */
//        final TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
//        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
//        if (isForeignCredit) {
//            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Record the current/delta units */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
//
//        /* Register the transaction */
//        myValues = myAsset.registerTransaction(theHelper);
//
//        /* Record values */
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        if (isForeignCredit) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
//        }
//
//        /* StockDeMerger is a transfer, so no need to update the categories */
    }
}
