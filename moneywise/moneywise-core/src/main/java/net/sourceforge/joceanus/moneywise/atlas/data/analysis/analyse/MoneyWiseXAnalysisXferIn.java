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

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Credit XferIn Analysis.
 */
public class MoneyWiseXAnalysisXferIn {
    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

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
     * The statePension bucket.
     */
    private final MoneyWiseXAnalysisSecurityBucket theStatePension;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the event analyser
     * @param pSecurity the securityAnalyser
     * @throws OceanusException on error
     */
    MoneyWiseXAnalysisXferIn(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                             final MoneyWiseXAnalysisSecurity pSecurity) throws OceanusException {
        /* Initialise values */
        theAnalysis = pAnalyser.getAnalysis();
        thePortfolios = theAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
        theTransAnalyser = theSecurity.getTransAnalyser();

        /* Access the StatePension */
        theStatePension = getStatePension(theAnalysis.getEditSet());
    }

    /**
     * Obtain statePension bucket.
     * @param pEditSet the editSet
     * @return the statePension bucket
     * @throws OceanusException on error
     */
    private MoneyWiseXAnalysisSecurityBucket getStatePension(final PrometheusEditSet pEditSet) throws OceanusException  {
        /* Access the singular portfolio and security */
        final MoneyWisePortfolioList myPortfolioList = pEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
        final MoneyWisePortfolio myPensionPort = myPortfolioList.getSingularClass(MoneyWisePortfolioClass.PENSION);
        final MoneyWiseSecurity myStatePension = pEditSet.getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class).getSingularClass(MoneyWiseSecurityClass.STATEPENSION);

        /* If they exist, access the bucket */
        if (myPensionPort != null
                && myStatePension != null) {
            final MoneyWiseSecurityHolding myHolding = myPortfolioList.getSecurityHoldingsMap().declareHolding(myPensionPort, myStatePension);
            return theAnalysis.getPortfolios().getBucket(myHolding);
        }

        /* Default to no bucket */
        throw new MoneyWiseLogicException("StatePension not found");
    }

    /**
     * Process a transaction that is a transferIn.
     * @param pTrans  the transaction
     */
    void processTransferIn(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Access debit account and category */
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTransaction.getDebitAccount();
        final MoneyWiseSecurityHolding myCredit = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();
        final boolean isPayee = theTransAnalyser.isPayee(myDebit);

        /* Process debit asset */
        if (!isPayee) {
            theTransAnalyser.processDebitAsset(myDebit);
        }

        /* Adjust the credit transfer details */
        processCreditXferIn(myCredit);

        /* Process debit payee */
        if (isPayee) {
            theTransAnalyser.processDebitPayee((MoneyWisePayee) myDebit);
        }
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pTrans  the transaction
     */
    void processCreditXferIn(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Process the credit */
        final MoneyWiseSecurityHolding myCredit = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();
        processCreditXferIn(myCredit);
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pHolding the credit holding
     */
    private void processCreditXferIn(final MoneyWiseSecurityHolding pHolding) {
        /* Obtain credit amount and credit delta units */
        final OceanusUnits myDeltaUnits = theTransaction.getCreditUnitsDelta();

        /* Access the Asset Security Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(pHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* If this is a foreign asset */
        final OceanusMoney myAmount = myAsset.isForeignCurrency()
                ? theTransAnalyser.adjustForeignAssetCredit(myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE))
                : theTransaction.getCreditAmount();

        /* Adjust the residual cost */
        myAsset.adjustResidualCost(myAmount);

        /* If there is no change to the # of units */
        if (myDeltaUnits == null || myDeltaUnits.isZero()) {
            /* Adjust the funded value and ensure the startDate */
            myAsset.adjustFunded(myAmount);
            myAsset.ensureStartDate(theTransaction.getTransaction().getDate());

            /* else record change in units */
        } else {
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Adjust the valuation */
        theSecurity.adjustAssetValuation(myAsset);

        /* Register the interest in the bucket */
        theState.registerBucketInterest(myAsset);
    }

    /**
     * Process statePension contribution.
     * @param pAmount the statePension contribution
     */
    void processStatePensionContribution(final OceanusMoney pAmount) {
        /* Adjust the cost and investment */
        theStatePension.adjustResidualCost(pAmount);
        theStatePension.adjustFunded(pAmount);

        /* Adjust the valuation */
        theStatePension.adjustValuation();

        /* Register the interest in the bucket */
        theState.registerBucketInterest(theStatePension);
    }
}
