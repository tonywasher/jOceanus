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

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pTrans.getDebitAccount();
        final MoneyWiseSecurity mySecurity = myHolding.getSecurity();
        final MoneyWiseTransAsset myCredit = pTrans.getCreditAccount();
        final TethysMoney myTaxCredit = myTransaction.getTaxCredit();
        final TethysUnits myDeltaUnits = pTrans.getCreditUnitsDelta();

        /* True debit account is the parent */
        final MoneyWiseAssetBase myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        theTransAnalyser.processDebitAsset(myDebit);

        /* Access the Asset Account Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();
        final boolean isForeign = myAsset.isForeignCurrency();
        final boolean isReInvest = myCredit instanceof MoneyWiseSecurityHolding;

        /* Determine the debit amount */
        final TethysMoney myAmount = isForeign
                ? theTransAnalyser.adjustForeignAssetDebit(myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE))
                : pTrans.getDebitAmount();

        /* If this is a re-investment */
        if (isReInvest) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustResidualCost(myAmount);

            /* Record the investment */
            myAsset.adjustInvested(myAmount);

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
            final TethysMoney myAdjust = new TethysMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustDividend(myAdjust);

            /* Adjust the credit account bucket */
            theTransAnalyser.processCreditAsset((MoneyWiseAssetBase) myCredit);
        }

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }
}
