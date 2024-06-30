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
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Security analysis.
 */
public class MoneyWiseXAnalysisSecurity {
    /**
     * Local Report fields.
     */
    private static final String ERROR_CATEGORY = "Unexpected Category Type: ";

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
     * The xferIn analysis.
     */
    private final MoneyWiseXAnalysisXferIn theXferIn;

    /**
     * The xferOut analysis.
     */
    private final MoneyWiseXAnalysisXferOut theXferOut;

    /**
     * The dividend analysis.
     */
    private final MoneyWiseXAnalysisDividend theDividend;

    /**
     * The deMerger analysis.
     */
    private final MoneyWiseXAnalysisDeMerger theDeMerger;

    /**
     * The takeover analysis.
     */
    private final MoneyWiseXAnalysisTakeover theTakeover;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     *
     * @param pAnalyser the event analyser
     * @param pTrans the transAnalyser
     * @throws OceanusException on error
     */
    MoneyWiseXAnalysisSecurity(final MoneyWiseXAnalysisEventAnalyser pAnalyser,
                               final MoneyWiseXAnalysisTransAnalyser pTrans) throws OceanusException {
        thePortfolios = pAnalyser.getAnalysis().getPortfolios();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theXferIn = new MoneyWiseXAnalysisXferIn(pAnalyser, pTrans);
        theXferOut = new MoneyWiseXAnalysisXferOut(pAnalyser, pTrans);
        theDividend = new MoneyWiseXAnalysisDividend(pAnalyser, pTrans);
        theDeMerger = new MoneyWiseXAnalysisDeMerger(pAnalyser);
        theTakeover = new MoneyWiseXAnalysisTakeover(pAnalyser);
    }

    /**
     * Obtain the xferIn analyser
     * @return the xferInAnalyser
     */
    MoneyWiseXAnalysisXferIn getXferInAnalyser() {
        return theXferIn;
    }

    /**
     * Process a debit security transaction.
     *
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processDebitSecurity(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Store transaction */
        theTransaction = pTrans;

        /* If credit account is also SecurityHolding */
        if (MoneyWiseXAnalysisTransAnalyser.isSecurityHolding(theTransaction.getCreditAccount())) {
            /* Split out working */
            processDebitCreditSecurity();
            return;
        }

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTransaction.getCategoryClass();
        final MoneyWiseSecurityHolding myDebitHolding = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        switch (myCatClass) {
            /* Process a dividend */
            case DIVIDEND:
                theDividend.processDividend(theTransaction);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case SECURITYCLOSURE:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
            case STOCKRIGHTSISSUE:
                theXferOut.processTransferOut(theTransaction);
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                        + myCatClass);
        }
    }

    /**
     * Process a credit security transaction.
     *
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processCreditSecurity(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Store transaction */
        theTransaction = pTrans;

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTransaction.getCategoryClass();
        switch (myCatClass) {
            /* Process standard transfer in/out */
            case STOCKRIGHTSISSUE:
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
            case PENSIONCONTRIB:
                theXferIn.processTransferIn(theTransaction);
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                        + myCatClass);
        }
    }

    /**
     * Process a debit+credit security transaction.
     *
     * @throws OceanusException on error
     */
    private void processDebitCreditSecurity() throws OceanusException {
        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTransaction.getCategoryClass();
        switch (myCatClass) {
            /* Process a stock split */
            case STOCKSPLIT:
            case UNITSADJUST:
                processUnitsAdjust();
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                theDeMerger.processStockDeMerger(theTransaction);
                break;
            /* Process a Stock TakeOver */
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                theTakeover.processStockTakeover(theTransaction);
                break;
            /* Process a dividend */
            case DIVIDEND:
                theDividend.processDividend(theTransaction);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                processStockXchange();
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                        + myCatClass);
        }
    }

    /**
     * Process a transaction that is a unitsAdjust.
     */
    void processUnitsAdjust() {
        /* Access the units */
        final TethysUnits myDelta = theTransaction.getDebitUnitsDelta();

        /* Adjust the Security Units */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        myAsset.adjustUnits(myDelta);

        /* Record the price of the asset if we have a price */
        final TethysPrice myPrice = theTransaction.getTransaction().getPrice();
        if (myPrice != null) {
            /* Record the new price and update the bucket */
            theState.setNewPriceViaTransaction(myHolding.getSecurity(), myPrice);

            /* update the price and determine the value delta */
            myAsset.recordSecurityPrice();
        }

        /* Value the asset and determine the unrealisedGains */
        myAsset.valueAsset();
        myAsset.adjustValuation();
        myAsset.calculateUnrealisedGains();

        /* determine the MarketGrowth */
        final TethysMoney myDeltaValue = myAsset.getDeltaValuation();
        theMarket.adjustTotalsForMarketGrowth(theTransaction.getEvent(), myDeltaValue);

        /* Register the transaction */
        theState.registerBucketInterest(myAsset);
    }

    /**
     * Process a transaction that is a stockXchange.
     */
    void processStockXchange() {
        /* Adjust the debit transfer details */
        theXferOut.processDebitXferOut(theTransaction);

        /* Adjust the credit transfer details */
        theXferIn.processCreditXferIn(theTransaction);
    }
}
