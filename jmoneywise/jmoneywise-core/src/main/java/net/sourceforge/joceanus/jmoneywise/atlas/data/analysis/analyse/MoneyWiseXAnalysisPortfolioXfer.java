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
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Portfolio Xfer support.
 */
public class MoneyWiseXAnalysisPortfolioXfer {
    /**
     * The portfolioBuckets.
     */
    private final MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The security holding map.
     */
    private final MoneyWiseSecurityHoldingMap theHoldingMap;

    /**
     * The current transaction.
     */
    private MoneyWiseXAnalysisTransaction theTrans;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisPortfolioXfer(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        /* Access details */
        thePortfolios = pAnalyser.getAnalysis().getPortfolios();
        theHoldingMap = pAnalyser.getSecurityHoldingMap();
    }

    /**
     * Process a transaction that is a portfolio transfer.
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processPortfolioXfer(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException  {
        /* Store the transaction */
        theTrans = pTrans;

        /* Target must be portfolio */
        final MoneyWiseTransAsset myCredit = theTrans.getCreditAccount();
        if (!MoneyWiseXAnalysisTransAnalyser.isPortfolio(myCredit)) {
            throw new MoneyWiseLogicException("Credit account is no portfolio");
        }

        /* If this is a transfer from a portfolio */
        final MoneyWiseTransAsset myDebit = theTrans.getDebitAccount();
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
//        /* Access the portfolio buckets */
//        final MoneyWisePortfolio mySrcPortfolio = (MoneyWisePortfolio) theTrans.getDebitAccount();
//        final MoneyWisePortfolio myTgtPortfolio = (MoneyWisePortfolio) theTrans.getCreditAccount();
//        final MoneyWiseXAnalysisPortfolioBucket mySource = thePortfolios.getBucket(mySrcPortfolio);
//        final MoneyWiseXAnalysisPortfolioBucket myTarget = thePortfolios.getBucket(myTgtPortfolio);
//
//        /* Access source cash bucket */
//        final MoneyWiseXAnalysisPortfolioCashBucket mySourceCash = mySource.getPortfolioCash();
//        if (mySourceCash.isActive()) {
//            /* Transfer any cash element */
//            final MoneyWiseXAnalysisPortfolioCashBucket myTargetCash = myTarget.getPortfolioCash();
//            myTargetCash.adjustForXfer(mySourceCash, theHelper);
//        }
//
//        /* Loop through the source portfolio */
//        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = mySource.securityIterator();
//        while (myIterator.hasNext()) {
//            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();
//
//            /* If the bucket is active */
//            if (myBucket.isActive()) {
//                /* Adjust the Target Bucket */
//                final MoneyWiseSecurityHolding myTargetHolding = theHoldingMap.declareHolding(pTarget, myBucket.getSecurity());
//                final MoneyWiseXAnalysisSecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);
//                theHelper.setSecurity(myBucket.getSecurity());
//
//                /* Process the Transfer */
//                processPortfolioXfer(myBucket, myTargetBucket);
//            }
//        }
//
//        /* PortfolioXfer is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a holding to portfolio transfer.
     */
    private void processHolding2Portfolio() {
        /* Access the portfolio buckets */
        final MoneyWiseSecurityHolding mySrcHolding = (MoneyWiseSecurityHolding) theTrans.getDebitAccount();
        final MoneyWisePortfolio myTgtPortfolio = (MoneyWisePortfolio) theTrans.getCreditAccount();
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

        /* PortfolioXfer is a transfer, so no need to update the categories */
    }

    /**
     * Process a portfolio transfer.
     * @param pSource the source holding
     * @param pTarget the target holding
     */
    private void processPortfolioXfer(final MoneyWiseXAnalysisSecurityBucket pSource,
                                      final MoneyWiseXAnalysisSecurityBucket pTarget) {
//        /* Access source details */
//        MoneyWiseXAnalysisSecurityValues mySourceValues = pSource.getValues();
//        TethysUnits myUnits = mySourceValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
//        TethysMoney myCost = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
//        TethysMoney myGains = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
//        TethysMoney myInvested = mySourceValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED);
//        final boolean isForeign = pSource.isForeignCurrency();
//
//        /* Determine value of the stock being transferred */
//        final TethysPrice myPrice = thePriceMap.getPriceForDate(pSource.getSecurity(), theHelper.getDate());
//        TethysMoney myStockValue = myUnits.valueAtPrice(myPrice);
//        TethysMoney myForeignValue = null;
//        TethysRatio myRate = null;
//
//        /* If we are foreign */
//        if (isForeign) {
//            /* Determine foreign and local value */
//            myRate = theHelper.getDebitExchangeRate();
//            myForeignValue = myStockValue;
//            myStockValue = myStockValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);
//        }
//
//        /* Allocate current profit between the two stocks */
//        TethysMoney myProfit = new TethysMoney(myStockValue);
//        myProfit.subtractAmount(myCost);
//        pSource.adjustCounter(MoneyWiseXAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//        myProfit = new TethysMoney(myProfit);
//        myProfit.negate();
//        pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.GROWTHADJUST, myProfit);
//
//        /* Transfer Units/Cost/Invested to target */
//        pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits);
//        pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost);
//        pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.INVESTED, myInvested);
//        pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myGains);
//        final MoneyWiseXAnalysisSecurityValues myTargetValues = pTarget.registerTransaction(theHelper);
//        myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.PRICE, myPrice);
//        myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myStockValue);
//        myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCost);
//        if (isForeign) {
//            myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.FOREIGNVALUE, myForeignValue);
//            myTargetValues.setValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, myRate);
//            pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.FOREIGNINVESTED, myForeignInvested);
//        }
//
//        /* Adjust the Source Units/Cost/Invested to zero */
//        myUnits = new TethysUnits(myUnits);
//        myUnits.negate();
//        pSource.adjustCounter(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits);
//        myCost = new TethysMoney(myCost);
//        myCost.negate();
//        pSource.adjustCounter(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost);
//        myCost.negate();
//        myInvested = new TethysMoney(myInvested);
//        myInvested.negate();
//        pSource.adjustCounter(MoneyWiseXAnalysisSecurityAttr.INVESTED, myInvested);
//        myGains = new TethysMoney(myGains);
//        myGains.negate();
//        pSource.adjustCounter(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myGains);
//        mySourceValues = pSource.registerTransaction(theHelper);
//        mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.PRICE, myPrice);
//        mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myStockValue);
//        mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCost);
//        if (isForeign) {
//            mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.FOREIGNVALUE, myForeignValue);
//            mySourceValues.setValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, myRate);
//            myForeignInvested = new TethysMoney(myForeignInvested);
//            myForeignInvested.negate();
//            pTarget.adjustCounter(MoneyWiseXAnalysisSecurityAttr.FOREIGNINVESTED, myForeignInvested);
//        }
    }
}
