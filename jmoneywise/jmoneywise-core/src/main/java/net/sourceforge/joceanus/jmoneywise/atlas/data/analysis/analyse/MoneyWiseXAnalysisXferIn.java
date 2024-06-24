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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
    private final MoneyWiseXAnalysisTransAnalyser theTrans;

    /**
     * The market analysis.
     */
    private final MoneyWiseXAnalysisMarket theMarket;

    /**
     * The reporting currency.
     */
    private final MoneyWiseCurrency theCurrency;

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
     * @param pAnalyser the event analyser.
     * @throws OceanusException on error
     */
    MoneyWiseXAnalysisXferIn(final MoneyWiseXAnalysisEventAnalyser pAnalyser) throws OceanusException {
        /* Initialise values */
        theAnalysis = pAnalyser.getAnalysis();
        thePortfolios = theAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theTrans = pAnalyser.getTransAnalyser();
        theMarket = pAnalyser.getMarket();
        theCurrency = theAnalysis.getCurrency();

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
        final boolean isPayee = theTrans.isPayee(myDebit);

        /* Process debit asset */
        if (!isPayee) {
            theTrans.processDebitAsset(myDebit);
        }

        /* Adjust the credit transfer details */
        processCreditXferIn(myCredit);

        /* Process debit payee */
        if (isPayee) {
            theTrans.processDebitPayee((MoneyWisePayee) myDebit);
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
        TethysMoney myAmount = theTransaction.getCreditAmount();
        TethysUnits myDeltaUnits = theTransaction.getCreditUnitsDelta();

        /* Access the Asset Security Bucket */
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(pHolding);
        final MoneyWiseXAnalysisSecurityValues myValues = myAsset.getValues();

        /* If this is a foreign asset */
        if (myAsset.isForeignCurrency()) {
            /* Calculate the value in the local currency */
            final TethysRatio myRate = myValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            myAmount = myAmount.convertCurrency(theCurrency.getCurrency(), myRate);
            theTransaction.setCreditAmount(myAmount);

            /* Adjust for currencyFluctuation */
            final TethysMoney myDebitAmount = theTransaction.getDebitAmount();
            final TethysMoney myFluctuation = new TethysMoney(myDebitAmount);
            myFluctuation.addAmount(myAmount);
            if (myFluctuation.isNonZero()) {
                theMarket.adjustTotalsForCurrencyFluctuation(theTransaction.getEvent(), myFluctuation);
            }
        }

        /* Adjust the cost and investment */
        myAsset.adjustResidualCost(myAmount);
        myAsset.adjustInvested(myAmount);

        /* If there is no change to the # of units */
        if (myDeltaUnits == null || myDeltaUnits.isZero()) {
            /* Adjust the funded value and ensure the startDate */
            myAsset.adjustFunded(myAmount);
            myAsset.ensureStartDate(theTransaction.getTransaction().getDate());

            /* else record change in units */
        } else {
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Determine the valuation */
        myAsset.valueAsset();
        myAsset.adjustValuation();
        myAsset.calculateUnrealisedGains();

        /* determine the MarketGrowth */
        final TethysMoney myDeltaValue = myAsset.getDeltaUnrealisedGains();
        theMarket.adjustTotalsForMarketGrowth(theTransaction.getEvent(), myDeltaValue);

        /* Register the interest in the bucket */
        theState.registerBucketInterest(myAsset);
    }

    /**
     * Process statePension contribution.
     * @param pAmount the statePension contribution
     */
    void processStatePensionContribution(final TethysMoney pAmount) {
        /* Adjust the cost and investment */
        theStatePension.adjustResidualCost(pAmount);
        theStatePension.adjustInvested(pAmount);
        theStatePension.adjustFunded(pAmount);

        /* Adjust the valuation */
        theStatePension.adjustValuation();

        /* Register the interest in the bucket */
        theState.registerBucketInterest(theStatePension);
    }
}
