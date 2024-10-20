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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Share deMerge Analysis.
 */
public class MoneyWiseXAnalysisDeMerger {
    /**
     * The portfolioBuckets.
     */
    private final MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The analysis state.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The securityAnalyser.
     */
    private final MoneyWiseXAnalysisSecurity theSecurity;

    /**
     * Constructor.
     * @param pAnalyser the event analyser
     * @param pSecurity the securityAnalyser
     */
    MoneyWiseXAnalysisDeMerger(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                               final MoneyWiseXAnalysisSecurity pSecurity) {
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
    }

    /**
     * Process a transaction that is a stockDeMerger.
     * @param pTrans  the transaction
     */
    void processStockDeMerger(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Access the Debit Asset Security Bucket */
        final MoneyWiseSecurityHolding mySource = (MoneyWiseSecurityHolding) pTrans.getDebitAccount();
        MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(mySource);
        MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* Obtain current cost */
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysRatio myDilution = pTrans.getTransaction().getDilution();
        final TethysUnits myDeltaUnits = pTrans.getDebitUnitsDelta();
        TethysMoney myDeltaValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myDeltaValue = new TethysMoney(myDeltaValue);

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the delta units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Calculate the cost dilution */
        final TethysMoney myNewCost = myCost.getDilutedMoney(myDilution);
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myDilution);

        /* Calculate the delta to the cost */
        TethysMoney myDeltaCost = new TethysMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the delta cost */
        myAsset.adjustResidualCost(myDeltaCost);

        /* Adjust the valuation */
        theSecurity.adjustAssetValuation(myAsset);
        TethysMoney myNewValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myDeltaValue.subtractAmount(myNewValue);
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myDeltaValue);

        /* Register the event */
        theState.registerBucketInterest(myAsset);

        /* Access the Credit Asset Account Bucket */
        final MoneyWiseSecurityHolding myTarget = (MoneyWiseSecurityHolding) pTrans.getCreditAccount();
        myAsset = thePortfolios.getBucket(myTarget);
        myValues = myAsset.getValues();
        myDeltaValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myDeltaValue = new TethysMoney(myDeltaValue);
        myDeltaValue.negate();

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new TethysMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record details */
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);

        /* Record the delta cost */
        myAsset.adjustResidualCost(myDeltaCost);

        /* Determine value of the stock being deMerged */
        final TethysUnits myCreditUnits = pTrans.getCreditUnitsDelta();
        myAsset.adjustUnits(myCreditUnits);

        /* Adjust the valuation */
        theSecurity.adjustAssetValuation(myAsset);
        myNewValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myDeltaValue.addAmount(myNewValue);
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myDeltaValue);

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);

        /* Record values */
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
    }
}
