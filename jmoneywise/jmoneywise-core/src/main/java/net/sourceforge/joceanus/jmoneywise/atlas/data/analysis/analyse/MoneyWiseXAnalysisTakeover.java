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

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;

/**
 * Stock Takeover analysis.
 */
public class MoneyWiseXAnalysisTakeover {
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
    MoneyWiseXAnalysisTakeover(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theTrans = pAnalyser.getAnalyser();
    }

    /**
     * Process a transaction that is a stockTakeover.
     * @param pTrans  the transaction
     */
    void processStockTakeover(final MoneyWiseXAnalysisTransaction pTrans) {
//        final TethysMoney myAmount = theHelper.getReturnedCash();
//        final MoneyWiseTransAsset myReturnedCashAct = theHelper.getReturnedCashAccount();
//
//        /* If we have a returned cash part of the transaction */
//        if (myReturnedCashAct != null
//                && myAmount.isNonZero()) {
//            /* Process a Stock And Cash TakeOver */
//            processStockAndCashTakeOver(pDebit, pCredit);
//        } else {
//            /* Process a StockOnly TakeOver */
//            processStockOnlyTakeOver(pDebit, pCredit);
//        }
    }

    /**
     * Process a transaction that is a StockOnlyTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockOnlyTakeOver(final MoneyWiseSecurityHolding pDebit,
                                          final MoneyWiseSecurityHolding pCredit) {
//        /* Access details */
//        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
//        final MoneyWiseSecurity myDebit = pDebit.getSecurity();
//
//        /* Access the Asset Security Buckets */
//        final MoneyWiseAnalysisSecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
//        MoneyWiseAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
//        final MoneyWiseAnalysisSecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);
//        final TethysDate myDate = theHelper.getDate();
//
//        /* Get the appropriate prices/rates for the stock */
//        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
//        final TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
//        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
//        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
//        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();
//
//        /* Determine value of the stock in both parts of the takeOver */
//        TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
//        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
//        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);
//        TethysMoney myInvested = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
//
//        /* Handle foreign debit */
//        final boolean isForeignDebit = myDebitAsset.isForeignCurrency();
//        if (isForeignDebit) {
//            myDebitValue = myDebitValue.convertCurrency(myCurrency, myDebitRate);
//        }
//
//        /* Handle foreign credit */
//        final boolean isForeignCredit = myCreditAsset.isForeignCurrency();
//        if (isForeignCredit) {
//            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Determine the residual cost of the old stock */
//        final TethysMoney myDebitCost = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//
//        /* Allocate current profit between the two stocks */
//        TethysMoney myProfit = new TethysMoney(myDebitValue);
//        myProfit.subtractAmount(myDebitCost);
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//        myProfit = new TethysMoney(myProfit);
//        myProfit.negate();
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//
//        /* Adjust cost/units/invested of the credit account */
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDebitCost);
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
//        if (isForeignCredit) {
//            final TethysMoney myForeign = myInvested.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
//            myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeign);
//        }
//
//        /* Determine final value of the credit stock after the takeOver */
//        myCreditUnits = myCreditAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);
//        if (isForeignCredit) {
//            myCreditValue = myCreditValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Register the transaction */
//        final MoneyWiseAnalysisSecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDebitCost);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myCreditValue);
//        if (isForeignCredit) {
//            myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
//        }
//
//        /* Drive debit cost down to zero */
//        final TethysMoney myDeltaCost = new TethysMoney(myDebitCost);
//        myDeltaCost.negate();
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        myDeltaCost.negate();
//
//        /* Drive debit units down to zero */
//        myDebitUnits = new TethysUnits(myDebitUnits);
//        myDebitUnits.negate();
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDebitUnits);
//
//        /* Adjust debit Invested amount */
//        myInvested = new TethysMoney(myInvested);
//        myInvested.negate();
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
//        if (isForeignDebit) {
//            myInvested = new TethysMoney(myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED));
//            myInvested.negate();
//            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Register the transaction */
//        myDebitValues = myDebitAsset.registerTransaction(theHelper);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myDebitPrice);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myDebitValue);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
//        if (isForeignDebit) {
//            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
//        }
    }

    /**
     * Process a transaction that is StockAndCashTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts. In particular it makes
     * reference to the CashTakeOver aspect of the debit account
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockAndCashTakeOver(final MoneyWiseSecurityHolding pDebit,
                                             final MoneyWiseSecurityHolding pCredit) {
//        /* Access details */
//        final MoneyWiseSecurity myDebit = pDebit.getSecurity();
//        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
//        final TethysDate myDate = theHelper.getDate();
//        final MoneyWiseTransAsset myReturnedCashAccount = theHelper.getReturnedCashAccount();
//        final TethysMoney myAmount = theHelper.getLocalReturnedCash();
//
//        /* Access the Asset Security Buckets */
//        final MoneyWiseAnalysisSecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
//        MoneyWiseAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
//        final MoneyWiseAnalysisSecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);
//
//        /* Get the appropriate prices for the assets */
//        final TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
//        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
//        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
//        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
//        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();
//
//        /* Determine value of the base stock */
//        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);
//
//        /* Determine value of the stock part of the takeOver */
//        TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
//        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
//
//        /* Handle foreign debit */
//        final boolean isForeignDebit = myDebitAsset.isForeignCurrency();
//        if (isForeignDebit) {
//            myDebitValue = myDebitValue.convertCurrency(myCurrency, myDebitRate);
//        }
//
//        /* Handle foreign credit */
//        final boolean isForeignCredit = myCreditAsset.isForeignCurrency();
//        if (isForeignCredit) {
//            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Calculate the total consideration */
//        final TethysMoney myConsideration = new TethysMoney(myAmount);
//        myConsideration.addAmount(myCreditXferValue);
//
//        /* Access the current debit cost */
//        final TethysMoney myCost = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//        TethysRatio myCostDilution = null;
//        final TethysMoney myCostXfer;
//        final TethysMoney myAllowedCost;
//
//        /* Determine condition as to whether this is a large cash transaction */
//        final TethysMoney myPortion = myDebitValue.valueAtRate(LIMIT_RATE);
//        final boolean isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
//                && (myAmount.compareTo(myPortion) > 0);
//
//        /* If this is a large cash takeOver */
//        if (isLargeCash) {
//            /* Determine the transferable cost */
//            myCostXfer = myCost.valueAtWeight(myCreditXferValue, myConsideration);
//
//            /* Determine the cost dilution */
//            myCostDilution = new TethysRatio(myAmount, myConsideration);
//
//            /* Determine the allowed cost */
//            myAllowedCost = new TethysMoney(myCost);
//            myAllowedCost.subtractAmount(myCostXfer);
//
//            /* else this is viewed as small and is taken out of the cost */
//        } else {
//            /* Set the allowed cost to be the least of the cost or the returned cash */
//            myAllowedCost = myAmount.compareTo(myCost) > 0
//                    ? new TethysMoney(myCost)
//                    : new TethysMoney(myAmount);
//
//            /* Transferred cost is cost minus the allowed cost */
//            myCostXfer = new TethysMoney(myCost);
//            myCostXfer.subtractAmount(myAllowedCost);
//        }
//
//        /* Determine the capital gain */
//        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
//        myCapitalGain.subtractAmount(myAllowedCost);
//        if (myCapitalGain.isNonZero()) {
//            /* Record the delta gains */
//            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myCapitalGain);
//
//            /* Adjust the capitalGains category bucket */
//            theCategoryBuckets.adjustStandardGain(theHelper, pDebit, myCapitalGain);
//        }
//
//        /* Allocate current profit between the two stocks */
//        TethysMoney myProfit = new TethysMoney(myCreditXferValue);
//        myProfit.subtractAmount(myCostXfer);
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//        myProfit = new TethysMoney(myProfit);
//        myProfit.negate();
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//
//        /* Adjust cost/units/invested of the credit account */
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCostXfer);
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
//        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myCostXfer);
//        if (isForeignCredit) {
//            final TethysMoney myForeign = myCostXfer.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
//            myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeign);
//        }
//
//        /* Determine final value of the credit stock after the takeOver */
//        myCreditUnits = myCreditAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);
//        if (isForeignCredit) {
//            myCreditValue = myCreditValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Register the transaction */
//        final MoneyWiseAnalysisSecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
//        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myCreditValue);
//        if (isForeignCredit) {
//            myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
//        }
//
//        /* Drive debit cost down to zero */
//        final TethysMoney myDeltaCost = new TethysMoney(myCost);
//        myDeltaCost.negate();
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//
//        /* Drive debit units down to zero */
//        myDebitUnits = new TethysUnits(myDebitUnits);
//        myDebitUnits.negate();
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDebitUnits);
//
//        /* Adjust debit Invested amount */
//        TethysMoney myInvested = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
//        myInvested = new TethysMoney(myInvested);
//        myInvested.setZero();
//        myInvested.subtractAmount(myAmount);
//        myInvested.subtractAmount(myCostXfer);
//        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
//        if (isForeignDebit) {
//            myInvested = myInvested.convertCurrency(myDebitAsset.getCurrency().getCurrency(), myDebitRate);
//            myInvested.negate();
//            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Register the transaction */
//        myDebitValues = myDebitAsset.registerTransaction(theHelper);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myDebitPrice);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myDebitValue);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, myAmount);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
//        if (myCostDilution != null) {
//            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
//        }
//        if (myCapitalGain.isNonZero()) {
//            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
//        }
//        if (isForeignDebit) {
//            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
//        }
//        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, isLargeCash
//                ? MoneyWiseCashType.LARGECASH
//                : MoneyWiseCashType.SMALLCASH);
//
//        /* Adjust the ThirdParty account bucket */
//        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket((MoneyWiseAssetBase) myReturnedCashAccount);
//        myBucket.adjustForReturnedCashCredit(theHelper);
    }
}
