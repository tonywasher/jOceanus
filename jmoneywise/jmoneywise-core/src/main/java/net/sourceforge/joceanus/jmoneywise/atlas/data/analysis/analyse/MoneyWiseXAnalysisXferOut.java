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

import java.time.temporal.ChronoUnit;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
    static final TethysMoney LIMIT_VALUE = TethysMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    static final TethysRate LIMIT_RATE = TethysRate.getWholePercentage(5);

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

        /* Determine the initial value of the asset */
        final TethysMoney myInitialValue = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);

        /* Determine the debit amount */
        final TethysMoney myAmount = myAsset.isForeignCurrency()
                ? theTransAnalyser.adjustForeignAssetDebit(myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE))
                : theTransaction.getDebitAmount();

        /* Assume that the allowed cost is the full value */
        TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        TethysMoney myAllowedCost;
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

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

        /* else we are performing a capital distribution */
        } else {
            /* Determine whether this is a large cash transaction */
            final TethysMoney myPortion = myInitialValue.valueAtRate(LIMIT_RATE);
            final boolean isLargeCash = myAmount.compareTo(LIMIT_VALUE) > 0
                    && myAmount.compareTo(myPortion) > 0;

            /* If this is large cash */
            if (isLargeCash) {
                /* Determine the allowedCost as a proportion of the initial value */
                myAllowedCost = myCost.valueAtWeight(myAmount, myInitialValue);

                /* else this is viewed as small and is taken out of the cost */
            } else {
                /* Set the allowed cost to be the least of the cost or the returned cash */
                myAllowedCost = myAmount.compareTo(myCost) > 0
                        ? new TethysMoney(myCost)
                        : new TethysMoney(myAmount);
            }

            /* Record details */
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, isLargeCash
                    ? MoneyWiseCashType.LARGECASH
                    : MoneyWiseCashType.SMALLCASH);
        }

        /* Determine the delta to the cost */
        final TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustResidualCost(myDeltaCost);
        }

        /* Determine the gain */
        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myCapitalGain.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustRealisedGains(myCapitalGain);
            myValues.setValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);

            /* If this is a chargeable Gain */
            if (myAsset.getSecurity().getCategoryClass().isChargeableGains()) {
                /* Determine the # of years that the bond has been open */
                final TethysDate myCurrent = theTransaction.getTransaction().getDate();
                final TethysDate myStart = myValues.getDateValue(MoneyWiseXAnalysisSecurityAttr.STARTDATE);
                final int myYears = (int) ChronoUnit.YEARS.between(myStart.getDate(), myCurrent.getDate()) + 1;
                final TethysRatio myRatio = new TethysRatio(String.valueOf(myYears)).getInverseRatio();
                final TethysMoney mySlice = myCapitalGain.valueAtRatio(myRatio);

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
