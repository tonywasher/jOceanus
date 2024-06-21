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

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;

/**
 * Debit XferOut Analysis.
 */
public class MoneyWiseXAnalysisXferOut {
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
    MoneyWiseXAnalysisXferOut(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theTrans = pAnalyser.getAnalyser();
    }

    /**
     * Process a transaction that is a transferOut.
     * @param pTrans  the transaction
     */
    void processTransferOut(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Access credit account and category */
        final MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTransaction.getCreditAccount();
        final MoneyWiseSecurityHolding myDebit = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();

        /* Adjust the credit account bucket */
        theTrans.processCreditAsset(myCredit);

        /* Adjust the debit transfer details */
        processDebitXferOut(myDebit);
    }

    /**
     * Process a transaction that is a chargeableGain.
     * @param pTrans  the transaction
     */
    void processChargeableGain(final MoneyWiseXAnalysisTransaction pTrans) {
//        /* Chargeable Gain is from the debit account and may or may not have units */
//        final MoneyWiseSecurity myDebit = pHolding.getSecurity();
//        TethysMoney myAmount = theHelper.getDebitAmount();
//        TethysUnits myDeltaUnits = theHelper.getDebitUnits();
//
//        /* Access the Asset Security Bucket */
//        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
//        final MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
//
//        /* If this is a foreign currency asset */
//        if (Boolean.TRUE.equals(myAsset.isForeignCurrency())) {
//            /* Adjust foreign invested amount */
//            final TethysMoney myDelta = new TethysMoney(myAmount);
//            myDelta.negate();
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getDebitExchangeRate());
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myDelta);
//
//            /* Switch to local amount */
//            myAmount = theHelper.getLocalAmount();
//        }
//
//        /* Record the delta investment */
//        final TethysMoney myDelta = new TethysMoney(myAmount);
//        myDelta.negate();
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDelta);
//
//        /* Assume the cost reduction is the full value */
//        TethysMoney myReduction = new TethysMoney(myAmount);
//        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//
//        /* If we are reducing units in the account */
//        if (myDeltaUnits != null) {
//            /* The reduction is the relevant fraction of the cost */
//            final TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);
//
//            /* Access units as negative value */
//            myDeltaUnits = new TethysUnits(myDeltaUnits);
//            myDeltaUnits.negate();
//
//            /* Record delta to units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//        }
//
//        /* If the reduction is greater than the total cost */
//        if (myReduction.compareTo(myCost) > 0) {
//            /* Reduction is the total cost */
//            myReduction = new TethysMoney(myCost);
//        }
//
//        /* Determine the delta to the cost */
//        final TethysMoney myDeltaCost = new TethysMoney(myReduction);
//        myDeltaCost.negate();
//
//        /* If we have a delta to the cost */
//        if (myDeltaCost.isNonZero()) {
//            /* Adjust the cost */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        }
//
//        /* Determine the delta to the gains */
//        final TethysMoney myDeltaGains = new TethysMoney(myAmount);
//        myDeltaGains.addAmount(myDeltaCost);
//
//        /* If we have a delta to the gains */
//        if (myDeltaGains.isNonZero()) {
//            /* Adjust the gains */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myDeltaGains);
//        }
//
//        /* Register the event */
//        myAsset.registerTransaction(theHelper);
//
//        /* True debit account is the parent */
//        final MoneyWiseAssetBase myParent = myDebit.getParent();
//
//        /* Adjust the debit account bucket */
//        final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myParent);
//        myPayee.adjustForTaxCredit(theHelper);
//
//        /* Adjust the credit account bucket */
//        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
//        myBucket.adjustForCredit(theHelper);
//
//        /* Adjust the chargeableGains category bucket */
//        theCategoryBuckets.adjustChargeableGain(theHelper, myReduction);
//
//        /* Adjust the TaxMan account for the tax credit */
//        theTaxMan.adjustForTaxPayments(theHelper);
//
//        /* Add the chargeable event */
//        theTaxBasisBuckets.recordChargeableGain(theHelper.getTransaction(), myDeltaGains);
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pTrans  the transaction
     */
    void processDebitXferOut(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Process the debit */
        final MoneyWiseSecurityHolding myDebit = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        processDebitXferOut(myDebit);
    }

    /**
     * Process the debit side of a transfer out transaction.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pHolding the debit holding
     */
    private void processDebitXferOut(final MoneyWiseSecurityHolding pHolding) {
//        /* Transfer out is from the debit account and may or may not have units */
//        final MoneyWiseSecurity myDebit = pHolding.getSecurity();
//        TethysMoney myAmount = theHelper.getDebitAmount();
//        boolean isLargeCash = false;
//
//        /* Access the Asset Security Bucket */
//        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
//        MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
//        final TethysRatio myXchangeRate = theHelper.getDebitExchangeRate();
//        final boolean isForeign = myAsset.isForeignCurrency();
//
//        /* If this is a foreign currency asset */
//        if (isForeign) {
//            /* Adjust foreign invested amount */
//            final TethysMoney myDelta = new TethysMoney(myAmount);
//            myDelta.negate();
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myDelta);
//
//            /* Switch to local amount */
//            myAmount = theHelper.getLocalAmount();
//        }
//
//        /* Record the delta investment */
//        final TethysMoney myDelta = new TethysMoney(myAmount);
//        myDelta.negate();
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDelta);
//
//        /* Get the appropriate price for the account */
//        final TethysPrice myPrice = thePriceMap.getPriceForDate(myDebit, theHelper.getDate());
//
//        /* Assume that the allowed cost is the full value */
//        TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myAllowedCost = new TethysMoney(myAmount);
//        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//        TethysRatio myCostDilution = null;
//        TethysMoney myConsideration = null;
//
//        /* Determine the delta units */
//        TethysUnits myDeltaUnits = theHelper.getCategoryClass().isSecurityClosure()
//                ? myUnits
//                : theHelper.getDebitUnits();
//        final boolean isCapitalDistribution = myDeltaUnits == null;
//
//        /* If this is not a capital distribution */
//        if (!isCapitalDistribution) {
//            /* The allowed cost is the relevant fraction of the cost */
//            myAllowedCost = myCost.valueAtWeight(myDeltaUnits, myUnits);
//
//            /* Access units as negative value */
//            myDeltaUnits = new TethysUnits(myDeltaUnits);
//            myDeltaUnits.negate();
//
//            /* Record delta to units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//            final TethysUnits myNewUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//
//            /* Determine the cost dilution */
//            myCostDilution = new TethysRatio(myNewUnits, myUnits);
//            myUnits = myNewUnits;
//        }
//
//        /* Determine value of this stock after the transaction */
//        TethysMoney myValue = myUnits.valueAtPrice(myPrice);
//
//        /* If we are foreign */
//        if (isForeign) {
//            /* Determine local value */
//            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myXchangeRate);
//        }
//
//        /* If we are performing a capital distribution */
//        if (isCapitalDistribution) {
//            /* Determine condition as to whether this is a large cash transaction */
//            final TethysMoney myPortion = myValue.valueAtRate(LIMIT_RATE);
//            isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
//                    && (myAmount.compareTo(myPortion) > 0);
//
//            /* If this is large cash */
//            if (isLargeCash) {
//                /* Determine the total value of rights plus share value */
//                myConsideration = new TethysMoney(myAmount);
//                myConsideration.addAmount(myValue);
//
//                /* Determine the allowedCost as a proportion of the total value */
//                myAllowedCost = myCost.valueAtWeight(myAmount, myConsideration);
//
//                /* Determine the cost dilution */
//                myCostDilution = new TethysRatio(myValue, myConsideration);
//
//                /* else this is viewed as small and is taken out of the cost */
//            } else {
//                /* Set the allowed cost to be the least of the cost or the returned cash */
//                myAllowedCost = myAmount.compareTo(myCost) > 0
//                        ? new TethysMoney(myCost)
//                        : new TethysMoney(myAmount);
//            }
//        }
//
//        /* Determine the delta to the cost */
//        final TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
//        myDeltaCost.negate();
//
//        /* If we have a delta to the cost */
//        if (myDeltaCost.isNonZero()) {
//            /* Adjust the cost */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        }
//
//        /* Determine the capital gain */
//        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
//        myCapitalGain.addAmount(myDeltaCost);
//
//        /* If we have a delta to the gains */
//        if (myCapitalGain.isNonZero()) {
//            /* Adjust the gains */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myCapitalGain);
//
//            /* Adjust the capitalGains category bucket */
//            theCategoryBuckets.adjustStandardGain(theHelper, pHolding, myCapitalGain);
//        }
//
//        /* Register the transaction */
//        myValues = myAsset.registerTransaction(theHelper);
//
//        /* record details */
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myValue);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, myAmount);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
//        if (myCostDilution != null) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
//        }
//        if (myConsideration != null) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration);
//        }
//        if (myCapitalGain.isNonZero()) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
//        }
//        if (isForeign) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myXchangeRate);
//        }
//        if (isCapitalDistribution) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, isLargeCash
//                    ? MoneyWiseCashType.LARGECASH
//                    : MoneyWiseCashType.SMALLCASH);
//        }
    }
}
