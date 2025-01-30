/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

import java.time.temporal.ChronoUnit;

/**
 * Debit XferOut Analysis.
 */
public class MoneyWiseXAnalysisXferOut {
    /**
     * The Amount Tax threshold for "small" transactions (£3000).
     */
    static final OceanusMoney LIMIT_VALUE = OceanusMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    static final OceanusRate LIMIT_RATE = OceanusRate.getWholePercentage(5);

    /**
     * The portfolioBuckets.
     */
    private final MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The taxBasisBuckets.
     */
    private final MoneyWiseXAnalysisTaxBasisBucketList theTaxBases;

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
     * @param pAnalyser the event analyser
     * @param pSecurity the securityAnalyser
     */
    MoneyWiseXAnalysisXferOut(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                              final MoneyWiseXAnalysisSecurity pSecurity) {
        /* Store parameters */
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theTaxBases = myAnalysis.getTaxBasis();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
        theTransAnalyser = theSecurity.getTransAnalyser();
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
        theTransAnalyser.processCreditAsset(myCredit);

        /* Adjust the debit transfer details */
        processDebitXferOut(myDebit);
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
        /* Access the Asset Security Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(pHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* Determine the stock value of the asset */
        final OceanusMoney myStockValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);

        /* Determine the debit amount */
        final OceanusMoney myAmount = myAsset.isForeignCurrency()
                ? theTransAnalyser.adjustForeignAssetDebit(myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE))
                : theTransaction.getDebitAmount();

        /* Assume that the allowed cost is the full value */
        final OceanusUnits myUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final OceanusMoney myAllowedCost;
        final OceanusMoney myCost = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* Determine the delta units */
        OceanusUnits myDeltaUnits = theTransaction.getCategoryClass().isSecurityClosure()
                ? myUnits
                : theTransaction.getDebitUnitsDelta();
        final boolean isCapitalDistribution = myDeltaUnits == null;

        /* If this is not a capital distribution */
        if (!isCapitalDistribution) {
            /* The allowed cost is the relevant fraction of the cost */
            myAllowedCost = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new OceanusUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units and costDilution */
            myAsset.adjustUnits(myDeltaUnits);
            final OceanusUnits myNewUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            final OceanusRatio myCostDilution = new OceanusRatio(myNewUnits, myUnits);
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution);

        /* else we are performing a capital distribution */
        } else {
            /* Determine whether this is a large cash transaction */
            final OceanusMoney myPortion = myStockValue.valueAtRate(LIMIT_RATE);
            final boolean isLargeCash = myAmount.compareTo(LIMIT_VALUE) > 0
                    && myAmount.compareTo(myPortion) > 0;

            /* If this is large cash */
            if (isLargeCash) {
                /* Determine the consideration and costDilution */
                final OceanusMoney myConsideration = new OceanusMoney(myAmount);
                myConsideration.addAmount(myStockValue);
                myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, myConsideration);
                final OceanusRatio myCostDilution = new OceanusRatio(myStockValue, myConsideration);
                myValues.setValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution);

                /* Determine the allowedCost as a proportion of the consideration */
                myAllowedCost = myCost.valueAtWeight(myAmount, myConsideration);

                /* else this is viewed as small and is taken out of the cost */
            } else {
                /* Set the allowed cost to be the least of the cost or the returned cash */
                myAllowedCost = myAmount.compareTo(myCost) > 0
                        ? new OceanusMoney(myCost)
                        : new OceanusMoney(myAmount);
            }

            /* Record details */
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, isLargeCash
                    ? MoneyWiseCashType.LARGECASH
                    : MoneyWiseCashType.SMALLCASH);
        }

        /* Determine the delta to the cost */
        final OceanusMoney myDeltaCost = new OceanusMoney(myAllowedCost);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustResidualCost(myDeltaCost);
        }

        /* Determine the gain */
        final OceanusMoney myCapitalGain = new OceanusMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myCapitalGain.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustRealisedGains(myCapitalGain);
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);

            /* If this is a chargeable Gain */
            if (myAsset.getSecurity().getCategoryClass().isChargeableGains()) {
                /* Determine the # of years that the bond has been open */
                final OceanusDate myCurrent = theTransaction.getTransaction().getDate();
                final OceanusDate myStart = myValues.getDateValue(MoneyWiseXAnalysisSecurityAttr.STARTDATE);
                final int myYears = (int) ChronoUnit.YEARS.between(myStart.getDate(), myCurrent.getDate()) + 1;
                final OceanusRatio myRatio = new OceanusRatio(String.valueOf(myYears)).getInverseRatio();
                final OceanusMoney mySlice = myCapitalGain.valueAtRatio(myRatio);

                /* Record details */
                myValues.setValue(MoneyWiseXAnalysisSecurityAttr.SLICEYEARS, myYears);
                myValues.setValue(MoneyWiseXAnalysisSecurityAttr.SLICEGAIN, mySlice);

                /* Adjust slices */
                theTaxBases.recordChargeableGain(theTransaction.getTransaction(), myCapitalGain, mySlice, myYears);
            }

            /* Adjust the capitalGains category bucket */
            theSecurity.adjustStandardGain(pHolding, myCapitalGain);
        }

        /* Adjust the valuation */
        theSecurity.adjustAssetValuation(myAsset);

        /* record details */
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH, myAmount);
        myValues.setValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }
}
