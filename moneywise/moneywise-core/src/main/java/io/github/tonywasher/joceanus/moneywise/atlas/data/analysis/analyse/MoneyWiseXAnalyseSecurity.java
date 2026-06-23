/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalyse.MoneyWiseXAnalyseEventAnalyserCtl;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalyse.MoneyWiseXAnalyseMarketCtl;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalyse.MoneyWiseXAnalyseSecurityCtl;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalyse.MoneyWiseXAnalyseTransAnalyserCtl;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseLogicException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Security analysis.
 */
public class MoneyWiseXAnalyseSecurity
        implements MoneyWiseXAnalyseSecurityCtl {
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
    private final MoneyWiseXAnalyseState theState;

    /**
     * The market analysis.
     */
    private final MoneyWiseXAnalyseMarketCtl theMarket;

    /**
     * The transAnalyser.
     */
    private final MoneyWiseXAnalyseTransAnalyserCtl theTransAnalyser;

    /**
     * The xferIn analysis.
     */
    private final MoneyWiseXAnalyseXferIn theXferIn;

    /**
     * The xferOut analysis.
     */
    private final MoneyWiseXAnalyseXferOut theXferOut;

    /**
     * The dividend analysis.
     */
    private final MoneyWiseXAnalyseDividend theDividend;

    /**
     * The deMerger analysis.
     */
    private final MoneyWiseXAnalyseDeMerger theDeMerger;

    /**
     * The takeover analysis.
     */
    private final MoneyWiseXAnalyseTakeover theTakeover;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalyseTransaction theTransaction;

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
     * The chargeableGains category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theChargeableCat;

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
     * The chargeableGains TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theChargeableTax;

    /**
     * Constructor.
     *
     * @param pAnalyser the event analyser
     * @param pTrans    the transAnalyser
     * @throws OceanusException on error
     */
    MoneyWiseXAnalyseSecurity(final MoneyWiseXAnalyseEventAnalyserCtl pAnalyser,
                              final MoneyWiseXAnalyseTransAnalyserCtl pTrans) throws OceanusException {
        /* Record important classes */
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theTransAnalyser = pTrans;

        /* Create analysers */
        theXferIn = new MoneyWiseXAnalyseXferIn(pAnalyser, this);
        theXferOut = new MoneyWiseXAnalyseXferOut(pAnalyser, this);
        theDividend = new MoneyWiseXAnalyseDividend(pAnalyser, this);
        theDeMerger = new MoneyWiseXAnalyseDeMerger(pAnalyser, this);
        theTakeover = new MoneyWiseXAnalyseTakeover(pAnalyser, this);

        /* Determine important categoryBuckets */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = myAnalysis.getTransCategories();
        theCapitalCat = myCategories.getBucket(MoneyWiseTransCategoryClass.CAPITALGAIN);
        theTaxFreeCat = myCategories.getBucket(MoneyWiseTransCategoryClass.TAXFREEGAIN);
        theResidentialCat = myCategories.getBucket(MoneyWiseTransCategoryClass.RESIDENTIALGAIN);
        theChargeableCat = myCategories.getBucket(MoneyWiseTransCategoryClass.CHARGEABLEGAIN);

        /* Determine important taxBuckets */
        final MoneyWiseXAnalysisTaxBasisBucketList myTaxBases = myAnalysis.getTaxBasis();
        theCapitalTax = myTaxBases.getBucket(MoneyWiseTaxClass.CAPITALGAINS);
        theTaxFreeTax = myTaxBases.getBucket(MoneyWiseTaxClass.TAXFREE);
        theResidentialTax = myTaxBases.getBucket(MoneyWiseTaxClass.RESIDENTIALGAINS);
        theChargeableTax = myTaxBases.getBucket(MoneyWiseTaxClass.CHARGEABLEGAINS);
    }

    @Override
    public MoneyWiseXAnalyseTransAnalyserCtl getTransAnalyser() {
        return theTransAnalyser;
    }

    /**
     * Obtain the xferIn analyser.
     *
     * @return the xferInAnalyser
     */
    MoneyWiseXAnalyseXferIn getXferInAnalyser() {
        return theXferIn;
    }

    /**
     * Process a debit security transaction.
     *
     * @param pTrans the transaction
     * @throws OceanusException on error
     */
    void processDebitSecurity(final MoneyWiseXAnalyseTransaction pTrans) throws OceanusException {
        /* Store transaction */
        theTransaction = pTrans;

        /* If credit account is also SecurityHolding */
        if (MoneyWiseXAnalyseTransaction.isSecurityHolding(theTransaction.getCreditAccount())) {
            /* Split out working */
            processDebitCreditSecurity();
            return;
        }

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTransaction.getCategoryClass();
        switch (myCatClass) {
            /* Process a dividend */
            case DIVIDEND:
                theDividend.processDividend(theTransaction);
                break;
            /* Process standard transfer in/out */
            case TRANSFER, SECURITYCLOSURE, EXPENSE, INHERITED, OTHERINCOME, STOCKRIGHTSISSUE:
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
     * @param pTrans the transaction
     * @throws OceanusException on error
     */
    void processCreditSecurity(final MoneyWiseXAnalyseTransaction pTrans) throws OceanusException {
        /* Store transaction */
        theTransaction = pTrans;

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTransaction.getCategoryClass();
        switch (myCatClass) {
            /* Process standard transfer in/out */
            case STOCKRIGHTSISSUE, TRANSFER, EXPENSE, INHERITED, OTHERINCOME, PENSIONCONTRIB:
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
            case STOCKSPLIT, UNITSADJUST:
                processUnitsAdjust();
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                theDeMerger.processStockDeMerger(theTransaction);
                break;
            /* Process a Stock TakeOver */
            case SECURITYREPLACE, STOCKTAKEOVER:
                theTakeover.processStockTakeover(theTransaction);
                break;
            /* Process a dividend */
            case DIVIDEND:
                theDividend.processDividend(theTransaction);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
                processStockXchange();
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                        + myCatClass);
        }
    }

    @Override
    public void adjustAssetValuation(final MoneyWiseXAnalysisSecurityBucket pAsset) {
        /* Value the asset and calculate unrealised gains */
        pAsset.valueAsset();
        pAsset.adjustValuation();
        pAsset.calculateUnrealisedGains();

        /* determine the MarketGrowth */
        final OceanusMoney myDeltaValue = pAsset.getDeltaUnrealisedGains();
        theMarket.adjustTotalsForMarketGrowth(theTransaction.getEvent(), myDeltaValue);
    }

    @Override
    public void adjustStandardGain(final MoneyWiseSecurityHolding pSource,
                                   final OceanusMoney pGains) {
        /* Access security and portfolio */
        final MoneyWiseSecurity mySecurity = pSource.getSecurity();
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurityClass myClass = mySecurity.getCategoryClass();

        /* Determine the type of gains */
        MoneyWiseXAnalysisTransCategoryBucket myCategory = theCapitalCat;
        MoneyWiseXAnalysisTaxBasisBucket myTaxBasis = theCapitalTax;
        if (myPortfolio.isTaxFree()) {
            myCategory = theTaxFreeCat;
            myTaxBasis = theTaxFreeTax;
        } else if (myClass.isResidentialGains()) {
            myCategory = theResidentialCat;
            myTaxBasis = theResidentialTax;
        } else if (myClass.isChargeableGains()) {
            myCategory = theChargeableCat;
            myTaxBasis = theChargeableTax;
        }

        /* Add to Capital Gains income/expense */
        if (pGains.isPositive()) {
            myCategory.addIncome(pGains);
        } else {
            myCategory.subtractExpense(pGains);
        }
        myTaxBasis.adjustGrossAndNett(pGains);
        theMarket.adjustForGains(pGains);

        /* Register the buckets */
        theState.registerBucketInterest(myCategory);
        theState.registerBucketInterest(myTaxBasis);
    }

    /**
     * Process a transaction that is a unitsAdjust.
     */
    void processUnitsAdjust() {
        /* Access the units */
        final OceanusUnits myDelta = theTransaction.getDebitUnitsDelta();

        /* Adjust the Security Units */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) theTransaction.getDebitAccount();
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        myAsset.adjustUnits(myDelta);

        /* Record the price of the asset if we have a price */
        final OceanusPrice myPrice = theTransaction.getTransaction().getPrice();
        if (myPrice != null) {
            /* Record the new price and update the bucket */
            theState.setNewPriceViaTransaction(myHolding.getSecurity(), myPrice);

            /* update the price and determine the value delta */
            myAsset.recordSecurityPrice();
        }

        /* Adjust the valuation */
        adjustAssetValuation(myAsset);

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
