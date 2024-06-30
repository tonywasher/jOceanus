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
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
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
     * The market analysis.
     */
    private final MoneyWiseXAnalysisMarket theMarket;

    /**
     * The reporting currency.
     */
    private final MoneyWiseCurrency theCurrency;

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
    MoneyWiseXAnalysisDividend(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                               final MoneyWiseXAnalysisTransAnalyser pTrans) {
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theCurrency = myAnalysis.getCurrency();
        theTrans = pTrans;
    }

    /**
     * Process a transaction that is a dividend.
     * @param pTrans  the transaction
     */
    void processDividend(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Adjust the parent for the transaction */
        theTransaction = pTrans;
        theTransaction.adjustParent();

        /* The main security that we are interested in is the base account */
        final MoneyWiseTransaction myTransaction = theTransaction.getTransaction();
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) myTransaction.getAccount();
        final MoneyWiseSecurity mySecurity = myHolding.getSecurity();
        final MoneyWiseTransAsset myCredit = theTransaction.getCreditAccount();
        TethysMoney myAmount = theTransaction.getDebitAmount();
        final TethysMoney myTaxCredit = myTransaction.getTaxCredit();
        final TethysUnits myDeltaUnits = theTransaction.getCreditUnitsDelta();

        /* True debit account is the parent */
        final MoneyWiseAssetBase myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        theTrans.processDebitAsset(myDebit);

        /* Access the Asset Account Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();
        final boolean isForeign = myAsset.isForeignCurrency();
        final boolean isReInvest = myCredit instanceof MoneyWiseSecurityHolding;

        /* If this is a foreign dividend */
        if (isForeign) {
            /* Calculate the value in the local currency */
            final TethysRatio myRate = myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            myAmount = myAmount.convertCurrency(theCurrency.getCurrency(), myRate);
            theTransaction.setDebitAmount(myAmount);

            /* Adjust for currencyFluctuation */
            final TethysMoney myCreditAmount = theTransaction.getCreditAmount();
            final TethysMoney myFluctuation = new TethysMoney(myAmount);
            myFluctuation.addAmount(myCreditAmount);
            if (myFluctuation.isNonZero()) {
                theMarket.adjustTotalsForCurrencyFluctuation(theTransaction.getEvent(), myFluctuation);
            }
        }

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
            theTrans.processCreditAsset((MoneyWiseAssetBase) myCredit);
        }

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }
}
