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
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Debit XferOut Analysis.
 */
public class MoneyWiseXAnalysisXferOut {
    /**
     * The Amount Tax threshold for "small" transactions (£3000).
     */
    private static final TethysMoney LIMIT_VALUE = TethysMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    private static final TethysRate LIMIT_RATE = TethysRate.getWholePercentage(5);

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
     * The capitalGains category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theCapitalCat;

    /**
     * The taxFreeGains category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theTaxFreeCat;

    /**
     * The residentialGains category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theResidentialCat;

    /**
     * The capitalGains TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theCapitalTax;

    /**
     * The taxFreeGains TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theTaxFreeTax;

    /**
     * The residentialGains TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theResidentialTax;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the event analyser
     * @param pTrans the transAnalyser
     */
    MoneyWiseXAnalysisXferOut(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                              final MoneyWiseXAnalysisTransAnalyser pTrans) {
        /* Store parameters */
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theTrans = pTrans;
        theCurrency = myAnalysis.getCurrency();

        /* Determine important categoryBuckets */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = myAnalysis.getTransCategories();
        theCapitalCat = myCategories.getBucket(MoneyWiseTransCategoryClass.CAPITALGAIN);
        theTaxFreeCat = myCategories.getBucket(MoneyWiseTransCategoryClass.TAXFREEGAIN);
        theResidentialCat = myCategories.getBucket(MoneyWiseTransCategoryClass.RESIDENTIALGAIN);

        /* Determine important taxBuckets */
        final MoneyWiseXAnalysisTaxBasisBucketList myTaxBases = myAnalysis.getTaxBasis();
        theCapitalTax = myTaxBases.getBucket(MoneyWiseTaxClass.CAPITALGAINS);
        theTaxFreeTax = myTaxBases.getBucket(MoneyWiseTaxClass.TAXFREE);
        theResidentialTax = myTaxBases.getBucket(MoneyWiseTaxClass.RESIDENTIALGAINS);
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
        /* Chargeable Gain is from the debit account and may or may not have units */
        //final MoneyWiseSecurity myDebit = pHolding.getSecurity();
        //TethysMoney myAmount = theTransaction.getDebitAmount();
        //TethysUnits myDeltaUnits = theTransaction.getDebitUnitsDelta();

        /* Access the Asset Security Bucket */
        //final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(pHolding);
        //final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* If this is a foreign currency asset */
        //if (myAsset.isForeignCurrency()) {
            /* Adjust foreign invested amount */
        //    final TethysMoney myDelta = new TethysMoney(myAmount);
        //    myDelta.negate();
        //    myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getDebitExchangeRate());
        //    myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
        //   myAmount = theHelper.getLocalAmount();
        //}

        /* Record the delta investment */
        //final TethysMoney myDelta = new TethysMoney(myAmount);
        //myDelta.negate();
        //myAsset.adjustInvested(myDelta);

        /* Assume the cost reduction is the full value */
        //TethysMoney myReduction = new TethysMoney(myAmount);
        //final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* If we are reducing units in the account */
        //if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
        //    final TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
       //     myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
        //    myDeltaUnits = new TethysUnits(myDeltaUnits);
        //    myDeltaUnits.negate();

            /* Record delta to units */
        //    myAsset.adjustUnits(myDeltaUnits);
        //}

        /* If the reduction is greater than the total cost */
        //if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
        //    myReduction = new TethysMoney(myCost);
        //}

        /* Determine the delta to the cost */
        //final TethysMoney myDeltaCost = new TethysMoney(myReduction);
        //myDeltaCost.negate();

        /* If we have a delta to the cost */
        //if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
        //    myAsset.adjustResidualCost(myDeltaCost);
        //}

        /* Determine the delta to the gains */
        //final TethysMoney myDeltaGains = new TethysMoney(myAmount);
        //myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        //if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
        //    myAsset.adjustRealisedGains(myDeltaGains);
        //}

        /* Register the event */
        //myAsset.registerTransaction(theHelper);

        /* True debit account is the parent */
        //final MoneyWiseAssetBase myParent = myDebit.getParent();

        /* Adjust the debit account bucket */
        //final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myParent);
        //myPayee.adjustForTaxCredit(theHelper);

        /* Adjust the credit account bucket */
        //final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
        //myBucket.adjustForCredit(theHelper);

        /* Adjust the chargeableGains category bucket */
        //theCategoryBuckets.adjustChargeableGain(theHelper, myReduction);

        /* Adjust the TaxMan account for the tax credit */
        //theTaxMan.adjustForTaxPayments(theHelper);

        /* Add the chargeable event */
        //theTaxBasisBuckets.recordChargeableGain(theHelper.getTransaction(), myDeltaGains);
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
        /* Transfer out is from the debit account and may or may not have units */
        TethysMoney myAmount = theTransaction.getDebitAmount();
        boolean isLargeCash = false;

        /* Access the Asset Security Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(pHolding);
        MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* If this is a foreign asset */
        if (myAsset.isForeignCurrency()) {
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

        /* Adjust the investment total */
        myAsset.adjustInvested(myAmount);

        /* Assume that the allowed cost is the full value */
        TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        TethysMoney myAllowedCost = new TethysMoney(myAmount);
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        TethysRatio myCostDilution = null;
        TethysMoney myConsideration = null;

        /* Determine the delta units */
        TethysUnits myDeltaUnits = theTransaction.getCategoryClass().isSecurityClosure()
                ? myUnits
                : theTransaction.getDebitUnitsDelta();
        final boolean isCapitalDistribution = myDeltaUnits == null;

        /* If this is not a capital distribution */
        if (!isCapitalDistribution) {
            /* The allowed cost is the relevant fraction of the cost */
            myAllowedCost = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustUnits(myDeltaUnits);
            final TethysUnits myNewUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myNewUnits, myUnits);
        }

        /* Determine the valuation */
        myAsset.valueAsset();
        myAsset.adjustValuation();
        myAsset.calculateUnrealisedGains();
        final TethysMoney myValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);

        /* If we are performing a capital distribution */
        if (isCapitalDistribution) {
            /* Determine condition as to whether this is a large cash transaction */
            final TethysMoney myPortion = myValue.valueAtRate(LIMIT_RATE);
            isLargeCash = myAmount.compareTo(LIMIT_VALUE) > 0
                    && myAmount.compareTo(myPortion) > 0;

            /* If this is large cash */
            if (isLargeCash) {
                /* Determine the total value of rights plus share value */
                myConsideration = new TethysMoney(myAmount);
                myConsideration.addAmount(myValue);

                /* Determine the allowedCost as a proportion of the total value */
                myAllowedCost = myCost.valueAtWeight(myAmount, myConsideration);

                /* Determine the cost dilution */
                myCostDilution = new TethysRatio(myValue, myConsideration);

                /* else this is viewed as small and is taken out of the cost */
            } else {
                /* Set the allowed cost to be the least of the cost or the returned cash */
                myAllowedCost = myAmount.compareTo(myCost) > 0
                        ? new TethysMoney(myCost)
                        : new TethysMoney(myAmount);
            }
        }

        /* Determine the delta to the cost */
        final TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustResidualCost(myDeltaCost);
        }

        /* Determine the capital gain */
        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myCapitalGain.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustRealisedGains(myCapitalGain);

            /* Adjust the capitalGains category bucket */
            adjustStandardGain(pHolding, myCapitalGain);
        }

        /* record details */
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH, myAmount);
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
        if (myCostDilution != null) {
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        }
        if (myConsideration != null) {
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, myConsideration);
        }
        if (myCapitalGain.isNonZero()) {
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
        }
        if (isCapitalDistribution) {
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, isLargeCash
                    ? MoneyWiseCashType.LARGECASH
                    : MoneyWiseCashType.SMALLCASH);
        }

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }

    /**
     * Adjust for Standard Gains.
     * @param pSource the source security holding
     * @param pGains the gains
     */
    public void adjustStandardGain(final MoneyWiseSecurityHolding pSource,
                                   final TethysMoney pGains) {
        /* Access security and portfolio */
        final MoneyWiseSecurity mySecurity = pSource.getSecurity();
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurityClass myClass = mySecurity.getCategoryClass();

        /* Determine the type of gains */
        MoneyWiseXAnalysisTransCategoryBucket myCategory = theCapitalCat;
        MoneyWiseXAnalysisTaxBasisBucket myTaxBasis = theCapitalTax;
        if (myPortfolio.isTaxFree() || !myClass.isCapitalGains()) {
            myCategory = theTaxFreeCat;
            myTaxBasis = theTaxFreeTax;
        } else if (myClass.isResidentialGains()) {
            myCategory = theResidentialCat;
            myTaxBasis = theResidentialTax;
        }

        /* Add to Capital Gains income/expense */
        if (pGains.isPositive()) {
            myCategory.addIncome(pGains);
        } else {
            myCategory.subtractExpense(pGains);
        }
        myTaxBasis.adjustGrossAndNett(pGains);

        /* Register the buckets */
        theState.registerBucketInterest(myCategory);
        theState.registerBucketInterest(myTaxBasis);
    }
}
