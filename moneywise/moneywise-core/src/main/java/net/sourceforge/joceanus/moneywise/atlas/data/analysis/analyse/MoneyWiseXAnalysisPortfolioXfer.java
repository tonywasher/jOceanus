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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import java.util.Iterator;

import net.sourceforge.joceanus.moneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Portfolio Xfer support.
 */
public class MoneyWiseXAnalysisPortfolioXfer {
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
     * The security holding map.
     */
    private final MoneyWiseSecurityHoldingMap theHoldingMap;

    /**
     * The current transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     * @param pSecurity the securityAnalyser
     */
    MoneyWiseXAnalysisPortfolioXfer(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                                    final MoneyWiseXAnalysisSecurity pSecurity) {
        /* Access details */
        thePortfolios = pAnalyser.getAnalysis().getPortfolios();
        theState = pAnalyser.getState();
        theSecurity = pSecurity;
        theHoldingMap = pAnalyser.getSecurityHoldingMap();
    }

    /**
     * Process a transaction that is a portfolio transfer.
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processPortfolioXfer(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException  {
        /* Store the transaction */
        theTransaction = pTrans;

        /* Target must be portfolio */
        final MoneyWiseTransAsset myCredit = theTransaction.getCreditAccount();
        if (!MoneyWiseXAnalysisTransAnalyser.isPortfolio(myCredit)) {
            throw new MoneyWiseLogicException("Credit account is no portfolio");
        }

        /* If this is a transfer from a portfolio */
        final MoneyWiseTransAsset myDebit = theTransaction.getDebitAccount();
        if (MoneyWiseXAnalysisTransAnalyser.isPortfolio(myDebit)) {
            processPortfolio2Portfolio();
        } else if (MoneyWiseXAnalysisTransAnalyser.isSecurityHolding(myDebit)) {
            processHolding2Portfolio();
        } else {
            throw new MoneyWiseLogicException("Debit account is neither portfolio or securityHolding");
        }
    }

    /**
     * Process a transaction that is a portfolio to portfolio transfer.
     */
    private void processPortfolio2Portfolio() {
        /* Access the portfolio buckets */
        final MoneyWisePortfolio mySrcPortfolio = (MoneyWisePortfolio) theTransaction.getDebitAccount();
        final MoneyWisePortfolio myTgtPortfolio = (MoneyWisePortfolio) theTransaction.getCreditAccount();
        final MoneyWiseXAnalysisPortfolioBucket mySource = thePortfolios.getBucket(mySrcPortfolio);
        final MoneyWiseXAnalysisPortfolioBucket myTarget = thePortfolios.getBucket(myTgtPortfolio);

        /* Access source cash bucket */
        final MoneyWiseXAnalysisPortfolioCashBucket mySourceCash = mySource.getPortfolioCash();
        if (mySourceCash.isActive()) {
            /* Adjust target bucket */
            final TethysMoney myCashValue = mySourceCash.getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
            mySourceCash.addToBalance(myCashValue);
            mySourceCash.adjustValuation();
            theState.registerBucketInterest(mySourceCash);

            /* Adjust target bucket */
            final MoneyWiseXAnalysisPortfolioCashBucket myTargetCash = myTarget.getPortfolioCash();
            myTargetCash.addToBalance(myCashValue);
            myTargetCash.adjustValuation();
            theState.registerBucketInterest(myTargetCash);
        }

        /* Loop through the source portfolio */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = mySource.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* If the bucket is active */
            if (myBucket.isActive()) {
                /* Adjust the Target Bucket */
                final MoneyWiseSecurityHolding myTargetHolding = theHoldingMap.declareHolding(myTgtPortfolio, myBucket.getSecurity());
                final MoneyWiseXAnalysisSecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);

                /* Process the Transfer */
                processPortfolioXfer(myBucket, myTargetBucket);
            }
        }
    }

    /**
     * Process a transaction that is a holding to portfolio transfer.
     */
    private void processHolding2Portfolio() {
        /* Access the portfolio buckets */
        final MoneyWiseSecurityHolding mySrcHolding = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        final MoneyWisePortfolio myTgtPortfolio = (MoneyWisePortfolio) theTransaction.getCreditAccount();
        final MoneyWiseXAnalysisPortfolioBucket mySource = thePortfolios.getBucket(mySrcHolding.getPortfolio());
        final MoneyWiseXAnalysisPortfolioBucket myTarget = thePortfolios.getBucket(myTgtPortfolio);

        /* Access source security bucket */
        final MoneyWiseXAnalysisSecurityBucket myBucket = mySource.getSecurityBucket(mySrcHolding);

        /* If the bucket is active */
        if (myBucket.isActive()) {
            /* Adjust the Target Bucket */
            final MoneyWiseSecurityHolding myTargetHolding = theHoldingMap.declareHolding(myTgtPortfolio, myBucket.getSecurity());
            final MoneyWiseXAnalysisSecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);

            /* Process the Transfer */
            processPortfolioXfer(myBucket, myTargetBucket);
        }
    }

    /**
     * Process a portfolio transfer.
     * @param pSource the source holding
     * @param pTarget the target holding
     */
    private void processPortfolioXfer(final MoneyWiseXAnalysisSecurityBucket pSource,
                                      final MoneyWiseXAnalysisSecurityBucket pTarget) {
        /* Access source details */
        MoneyWiseXAnalysisSecurityValues mySourceValues = pSource.getValues();
        TethysUnits myUnits = mySourceValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        TethysMoney myCost = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        TethysMoney myGains = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);

        /* Determine value of the stock being transferred */
        final TethysMoney myStockValue = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);

        /* Transfer Units/Cost/Gains to target */
        pTarget.adjustUnits(myUnits);
        pTarget.adjustResidualCost(myCost);
        pTarget.adjustRealisedGains(myGains);
        final MoneyWiseXAnalysisSecurityValues myTargetValues = pTarget.getValues();
        myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCost);
        myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStockValue);

        /* Adjust the Source Units/Cost/Invested to zero */
        mySourceValues.setZeroUnits(MoneyWiseXAnalysisSecurityAttr.UNITS);
        mySourceValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        mySourceValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCost);
        mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStockValue);

        /* Value the assets */
        theSecurity.adjustAssetValuation(pSource);
        theSecurity.adjustAssetValuation(pTarget);

        /* Register the transaction */
        theState.registerBucketInterest(pSource);
        theState.registerBucketInterest(pTarget);
    }
}
