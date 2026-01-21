/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseCashType;

import java.util.Currency;
import java.util.Objects;

/**
 * Stock Takeover analysis.
 */
public class MoneyWiseXAnalysisTakeover {
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
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     *
     * @param pAnalyser the event analyser
     * @param pSecurity the securityAnalyser
     */
    MoneyWiseXAnalysisTakeover(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                               final MoneyWiseXAnalysisSecurity pSecurity) {
        thePortfolios = pAnalyser.getAnalysis().getPortfolios();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
        theTransAnalyser = theSecurity.getTransAnalyser();
    }

    /**
     * Process a transaction that is a stockTakeover.
     *
     * @param pTrans the transaction
     */
    void processStockTakeover(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Store the transaction */
        theTransaction = pTrans;

        /* Access returned cash */
        final MoneyWiseTransaction myTrans = pTrans.getTransaction();
        final OceanusMoney myAmount = myTrans.getReturnedCash();

        /* If we have a returned cash part of the transaction */
        if (myAmount != null
                && myAmount.isNonZero()) {
            /* Process a Stock And Cash TakeOver */
            processStockAndCashTakeOver(myAmount);
        } else {
            /* Process a StockOnly TakeOver */
            processStockOnlyTakeOver();
        }
    }

    /**
     * Process a transaction that is a StockOnlyTakeover.
     */
    void processStockOnlyTakeOver() {
        /* Access details */
        final MoneyWiseSecurityHolding myCreditHolding = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();
        final MoneyWiseSecurityHolding myDebitHolding = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();

        /* Access the Asset Security Buckets */
        final MoneyWiseXAnalysisSecurityBucket myDebitAsset = thePortfolios.getBucket(myDebitHolding);
        final MoneyWiseXAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
        final MoneyWiseXAnalysisSecurityBucket myCreditAsset = thePortfolios.getBucket(myCreditHolding);
        final MoneyWiseXAnalysisSecurityValues myCreditValues = myCreditAsset.getValues();

        /* Determine value of the stock in both parts of the takeOver */
        final OceanusMoney myDebitXferValue = myDebitValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        final OceanusPrice myCreditPrice = myDebitValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);
        final OceanusUnits myCreditUnits = theTransaction.getCreditUnitsDelta();
        OceanusMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
        if (myCreditAsset.isForeignCurrency()) {
            final Currency myCurrency = theTransAnalyser.getCurrency().getCurrency();
            final OceanusRatio myRate = myCreditValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myRate);
        }

        /* Record the transferValues */
        myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myDebitXferValue);
        myCreditValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);

        /* Adjust units of debit and credit */
        myCreditAsset.adjustUnits(myCreditUnits);
        myDebitValues.setZeroUnits(MoneyWiseXAnalysisSecurityAttr.UNITS);

        /* Adjust the residual cost of debit and credit */
        final OceanusMoney myXferCost = myDebitValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myDebitValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myCreditAsset.adjustResidualCost(myXferCost);

        /* Adjust the debit Funded to zero */
        myDebitValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.FUNDED);

        /* Value the assets */
        theSecurity.adjustAssetValuation(myDebitAsset);
        theSecurity.adjustAssetValuation(myCreditAsset);

        /* Register the transaction */
        theState.registerBucketInterest(myDebitAsset);
        theState.registerBucketInterest(myCreditAsset);
    }

    /**
     * Process a transaction that is StockAndCashTakeover.
     *
     * @param pCashValue the cash part of the takeOver
     */
    private void processStockAndCashTakeOver(final OceanusMoney pCashValue) {
        /* Access details */
        final MoneyWiseSecurityHolding myDebit = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        final MoneyWiseSecurityHolding myCredit = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();
        final MoneyWiseAssetBase myReturnedCashAccount = (MoneyWiseAssetBase) Objects.requireNonNull(theTransaction.getTransaction().getReturnedCashAccount());

        /* Access the Asset Security Buckets */
        final MoneyWiseXAnalysisSecurityBucket myDebitAsset = thePortfolios.getBucket(myDebit);
        final MoneyWiseXAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
        final MoneyWiseXAnalysisSecurityBucket myCreditAsset = thePortfolios.getBucket(myCredit);
        final MoneyWiseXAnalysisSecurityValues myCreditValues = myDebitAsset.getValues();

        /* Get the appropriate prices for the assets */
        final OceanusMoney myStartingDebitValue = myDebitValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        final OceanusMoney myStartingCreditValue = myCreditValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStartingDebitValue);

        /* Adjust units of the stocks */
        myDebitAsset.adjustUnits(theTransaction.getDebitUnitsDelta());
        myCreditAsset.adjustUnits(theTransaction.getCreditUnitsDelta());

        /* Adjust the debit Funded to zero */
        myDebitValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.FUNDED);

        /* Determine value of the stock part of the takeOver */
        myCreditAsset.valueAsset();
        myCreditAsset.adjustValuation();
        OceanusMoney myStockValue = myCreditValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myStockValue = new OceanusMoney(myStockValue);
        myStockValue.addAmount(myStartingCreditValue);
        myCreditValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStockValue);

        /* Calculate the total consideration */
        final OceanusMoney myConsideration = new OceanusMoney(pCashValue);
        myConsideration.addAmount(myStockValue);
        myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, myConsideration);

        /* Determine whether this is a large cash transaction */
        final OceanusMoney myPortion = myConsideration.valueAtRate(MoneyWiseXAnalysisXferOut.LIMIT_RATE);
        final boolean isLargeCash = pCashValue.compareTo(MoneyWiseXAnalysisXferOut.LIMIT_VALUE) > 0
                && pCashValue.compareTo(myPortion) > 0;
        myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, isLargeCash
                ? MoneyWiseCashType.LARGECASH
                : MoneyWiseCashType.SMALLCASH);

        /* Access the current debit cost */
        final OceanusMoney myCost = myDebitValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusMoney myAllowedCost;
        final OceanusMoney myCostXfer;

        /* If this is a large cash takeOver */
        if (isLargeCash) {
            /* Determine the transferable cost */
            myCostXfer = myCost.valueAtWeight(myStockValue, myConsideration);

            /* Determine the cost dilution */
            final OceanusRatio myCostDilution = new OceanusRatio(pCashValue, myConsideration);
            myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution);

            /* Determine the allowed cost */
            myAllowedCost = new OceanusMoney(myCost);
            myAllowedCost.subtractAmount(myCostXfer);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the allowed cost to be the least of the cost or the returned cash */
            myAllowedCost = pCashValue.compareTo(myCost) > 0
                    ? new OceanusMoney(myCost)
                    : new OceanusMoney(pCashValue);

            /* Transferred cost is cost minus the allowed cost */
            myCostXfer = new OceanusMoney(myCost);
            myCostXfer.subtractAmount(myAllowedCost);
        }
        myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStockValue);
        myCreditValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStockValue);

        /* Value the assets */
        theSecurity.adjustAssetValuation(myDebitAsset);
        myCreditAsset.calculateUnrealisedGains();

        /* Register the transaction */
        theState.registerBucketInterest(myDebitAsset);
        theState.registerBucketInterest(myCreditAsset);

        /* Determine the capital gain */
        final OceanusMoney myCapitalGain = new OceanusMoney(pCashValue);
        myCapitalGain.subtractAmount(myAllowedCost);
        if (myCapitalGain.isNonZero()) {
            /* Record the delta gains */
            myDebitAsset.adjustRealisedGains(myCapitalGain);
            myDebitValues.setValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
            theSecurity.adjustStandardGain(myDebit, myCapitalGain);
        }

        /* Adjust residualCost of debit/credit */
        myCreditAsset.adjustResidualCost(myCostXfer);
        myDebitValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* Adjust the ThirdParty account bucket */
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = theTransAnalyser.getAccountBucket(myReturnedCashAccount);
        myBucket.addToBalance(pCashValue);
        myBucket.adjustValuation();
        theState.registerBucketInterest(myBucket);
    }
}
