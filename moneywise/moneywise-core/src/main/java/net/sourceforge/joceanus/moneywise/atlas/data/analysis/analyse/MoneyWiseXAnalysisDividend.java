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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Dividend Analysis.
 */
public class MoneyWiseXAnalysisDividend {
    /**
     * The portfolioBuckets.
     */
    private final MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The analysis state.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The transAnalyser.
     */
    private final MoneyWiseXAnalysisTransAnalyser theTransAnalyser;

    /**
     * The securityAnalyser.
     */
    private final MoneyWiseXAnalysisSecurity theSecurity;

    /**
     * Constructor.
     * @param pAnalyser the event analyser
     * @param pSecurity the securityAnalyser
     */
    MoneyWiseXAnalysisDividend(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                               final MoneyWiseXAnalysisSecurity pSecurity) {
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
        theTransAnalyser = theSecurity.getTransAnalyser();
    }

    /**
     * Process a transaction that is a dividend.
     * @param pTrans  the transaction
     */
    void processDividend(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Adjust the parent for the transaction */
        pTrans.adjustParent();

        /* The main security that we are interested in is the debit account */
        final MoneyWiseTransaction myTransaction = pTrans.getTransaction();
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) myTransaction.getAccount();
        final MoneyWiseSecurity mySecurity = myHolding.getSecurity();
        final MoneyWiseTransAsset myCredit = pTrans.getCreditAccount();
        final OceanusMoney myTaxCredit = myTransaction.getTaxCredit();
        final OceanusUnits myDeltaUnits = pTrans.getCreditUnitsDelta();

        /* True debit account is the parent */
        final MoneyWisePayee myDebit = mySecurity.getParent();

        /* Access the Asset Account Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();
        final boolean isForeign = myAsset.isForeignCurrency();
        final boolean isReInvest = myCredit instanceof MoneyWiseSecurityHolding;

        /* Determine the debit amount */
        final OceanusMoney myAmount = isForeign
                ? theTransAnalyser.adjustForeignAssetDebit(myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE))
                : pTrans.getDebitAmount();

        /* Adjust the debit payee bucket */
        theTransAnalyser.processDebitPayee(myDebit);

        /* If this is a re-investment */
        if (isReInvest) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustResidualCost(myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record delta units */
                myAsset.adjustUnits(myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                myAsset.adjustDividend(myTaxCredit);
            }

            /* Adjust the valuation */
            theSecurity.adjustAssetValuation(myAsset);

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            final OceanusMoney myAdjust = new OceanusMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            //if (myTaxCredit != null) {
            //    myAdjust.addAmount(myTaxCredit);
            //}

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustDividend(myAdjust);

            /* Adjust the credit account bucket */
            theTransAnalyser.processCreditAsset((MoneyWiseAssetBase) myCredit);
        }

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }
}
