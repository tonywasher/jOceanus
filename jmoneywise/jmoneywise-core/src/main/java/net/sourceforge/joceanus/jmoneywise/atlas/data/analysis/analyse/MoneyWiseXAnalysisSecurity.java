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
     * The tax analysis.
     */
    private final MoneyWiseXAnalysisTax theTax;

    /**
     * The takeover analysis.
     */
    private final MoneyWiseXAnalysisTakeover theTakeover;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTrans;

    /**
     * Constructor.
     *
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisSecurity(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        thePortfolios = pAnalyser.getAnalysis().getPortfolios();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theTax = pAnalyser.getTax();
        theTakeover = new MoneyWiseXAnalysisTakeover();
    }

    /**
     * Process a debit security transaction.
     *
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processDebitSecurity(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Store transaction */
        theTrans = pTrans;

        /* If credit account is also SecurityHolding */
        if (MoneyWiseXAnalysisTransAnalyser.isSecurityHolding(theTrans.getCreditAccount())) {
            /* Split out working */
            processDebitCreditSecurity();
            return;
        }

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTrans.getCategoryClass();
        final MoneyWiseSecurityHolding myDebitHolding = (MoneyWiseSecurityHolding) pTrans.getDebitAccount();
        switch (myCatClass) {
            /* Process a stock right waived */
            case STOCKRIGHTSISSUE:
                processTransferOut();
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend();
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case SECURITYCLOSURE:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                if (myDebitHolding.getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)) {
                    processChargeableGain();
                } else {
                    processTransferOut();
                }
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
        theTrans = pTrans;

        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTrans.getCategoryClass();
        switch (myCatClass) {
            /* Process standard transfer in/out */
            case STOCKRIGHTSISSUE:
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
            case PENSIONCONTRIB:
                processTransferIn();
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
    void processDebitCreditSecurity() throws OceanusException {
        /* Switch on the category */
        final MoneyWiseTransCategoryClass myCatClass = theTrans.getCategoryClass();
        switch (myCatClass) {
            /* Process a stock split */
            case STOCKSPLIT:
            case UNITSADJUST:
                processUnitsAdjust();
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                processStockDeMerger();
                break;
            /* Process a Stock TakeOver */
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                theTakeover.processStockTakeover(theTrans);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend();
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
     * Process a transaction that is a dividend.
     */
    void processDividend() {
        /* The main security that we are interested in is the debit account */
        //final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        //final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        //TethysMoney myAmount = theHelper.getDebitAmount();
        //final TethysMoney myTaxCredit = theHelper.getTaxCredit();
        //final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();
        //final MoneyWiseTaxCredit myYear = theHelper.getTransaction().getTaxYear();

        /* Obtain detailed category */
        //MoneyWiseTransCategory myCat = myPortfolio.getDetailedCategory(theHelper.getCategory(), myYear);
        //myCat = mySecurity.getDetailedCategory(myCat, myYear);

        /* True debit account is the parent */
        //final MoneyWiseAssetBase myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        //final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        //myPayee.adjustForDebit(theHelper);

        /* Access the Asset Account Bucket */
        //final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        //final boolean isForeign = myAsset.isForeignCurrency();
        //final boolean isReInvest = pCredit instanceof MoneyWiseSecurityHolding;

        /* If this is a foreign dividend */
        //if (isForeign) {
            /* If this is a reInvestment */
        //    if (isReInvest) {
        //        /* Adjust counters */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);
        //        myAsset.getValues().setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getCreditExchangeRate());
        //    }

            /* Switch to local amount */
        //    myAmount = theHelper.getLocalAmount();
        //}

        /* If this is a re-investment */
        //if (isReInvest) {
            /* This amount is added to the cost, so record as the delta cost */
        //    myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);

            /* Record the investment */
        //    myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);

            /* If we have new units */
        //    if (myDeltaUnits != null) {
                /* Record delta units */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
        //    }

            /* If we have a tax credit */
        //    if (myTaxCredit != null) {
        //        /* The Tax Credit is viewed as a received dividend from the account */
        //        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myTaxCredit);
        //    }

            /* else we are paying out to another account */
        //} else {
            /* Adjust the dividend total for this asset */
        //    final TethysMoney myAdjust = new TethysMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
        //    if (myTaxCredit != null) {
        //        myAdjust.addAmount(myTaxCredit);
        //    }

            /* The Dividend is viewed as a dividend from the account */
         //   myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myAdjust);

            /* Adjust the credit account bucket */
        //    final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket((MoneyWiseAssetBase) pCredit);
        //    myBucket.adjustForCredit(theHelper);
        //}

        /* Register the transaction */
        //myAsset.registerTransaction(theHelper);

        /* Adjust the tax payments */
        //theTaxMan.adjustForTaxPayments(theHelper);

        /* Adjust the relevant category buckets */
        //theCategoryBuckets.adjustCategories(theHelper, myCat);
    }

    /**
     * Process a transaction that is a transferIn.
     */
    void processTransferIn() {
//        /* Access debit account and category */
//        final MoneyWiseTransCategory myCat = theHelper.getCategory();
//
//        /* Adjust the credit transfer details */
//        processCreditXferIn(pCredit);
//
//        /* Adjust the tax payments */
//        theTaxMan.adjustForTaxPayments(theHelper);
//
//        /* Determine the type of the debit account */
//        switch (pDebit.getAssetType()) {
//            case PAYEE:
//                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(pDebit);
//                myPayee.adjustForDebit(theHelper);
//                break;
//            default:
//                final MoneyWiseAnalysisAccountBucket<?> myAccount = getAccountBucket(pDebit);
//                myAccount.adjustForDebit(theHelper);
//                break;
//        }
//
//        /* If the event category is not a transfer */
//        if (!myCat.isTransfer()) {
//            /* Adjust the relevant category buckets */
//            theCategoryBuckets.adjustCategories(theHelper, myCat);
//        }
    }

    /**
     * Process a transaction that is a transferOut.
     */
    void processTransferOut() {
//        /* Access credit account and category */
//        final MoneyWiseTransCategory myCat = theHelper.getCategory();
//
//        /* Adjust the debit transfer details */
//        processDebitXferOut(pHolding);
//
//        /* Adjust the credit account bucket */
//        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
//        myBucket.adjustForCredit(theHelper);
//
//        /* If the event category is not a transfer */
//        if (!myCat.isTransfer()) {
//            /* Adjust the relevant category buckets */
//            theCategoryBuckets.adjustCategories(theHelper, myCat);
//        }
    }

    /**
     * Process a transaction that is a chargeableGain.
     */
    void processChargeableGain() {
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
     * Process a transaction that is a unitsAdjust.
     */
    void processUnitsAdjust() {
        /* Access the units */
        final TethysUnits myDelta = theTrans.getDebitUnitsDelta();

        /* Adjust the Security Units */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) theTrans.getDebitAccount();
        final MoneyWiseXAnalysisSecurityBucket myAsset = thePortfolios.getBucket(myHolding);
        myAsset.adjustUnits(myDelta);

        /* Record the price of the asset if we have a price */
        final TethysPrice myPrice = theTrans.getTransaction().getPrice();
        if (myPrice != null) {
            /* Record the new price and update the bucket */
            theState.setNewPriceViaTransaction(myHolding.getSecurity(), myPrice);

            /* update the price and determine the value delta */
            myAsset.recordSecurityPrice();
        }

        /* Value the asset and determine the unrealisedGains and MrketGrowth */
        myAsset.valueAsset();
        myAsset.calculateUnrealisedGains();
        final TethysMoney myDeltaValue = myAsset.getDeltaValuation();
        theMarket.adjustTotalsForMarketGrowth(theTrans.getEvent(), myDeltaValue);

        /* Register the transaction */
        theState.registerBucketForEvent(myAsset);
        theState.registerBucketsForEvent(theTrans.getEvent());

        /* StockSplit/Adjust is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a stockDeMerger.
     */
    void processStockDeMerger() {
//        /* Access the Debit Asset Security Bucket */
//        MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pDebit);
//        MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
//        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
//
//        /* Obtain current cost */
//        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
//        final TethysRatio myDilution = theHelper.getDilution();
//        final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();
//
//        /* If we reduced the units */
//        if (myDeltaUnits != null) {
//            /* Record the delta units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//        }
//
//        /* Calculate the cost dilution */
//        final TethysMoney myNewCost = myCost.getDilutedMoney(myDilution);
//        final TethysRatio myCostDilution = new TethysRatio(myNewCost, myCost);
//
//        /* Calculate the delta to the cost */
//        TethysMoney myDeltaCost = new TethysMoney(myNewCost);
//        myDeltaCost.subtractAmount(myCost);
//
//        /* Record the delta cost/investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
//        final boolean isForeignDebit = myAsset.isForeignCurrency();
//        if (isForeignDebit) {
//            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myDebitRate);
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Register the event */
//        myValues = myAsset.registerTransaction(theHelper);
//
//        /* Access the Credit Asset Account Bucket */
//        myAsset = thePortfolioBuckets.getBucket(pCredit);
//
//        /* The deltaCost is transferred to the credit account */
//        myDeltaCost = new TethysMoney(myDeltaCost);
//        myDeltaCost.negate();
//
//        /* Record details */
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
//        if (isForeignDebit) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
//        }
//
//        /* Record the delta cost/investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
//        final boolean isForeignCredit = myAsset.isForeignCurrency();
//        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
//        if (isForeignCredit) {
//            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myCreditRate);
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
//        }
//
//        /* Get the appropriate prices/rates for the stock */
//        final TethysDate myDate = theHelper.getDate();
//        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myAsset.getSecurity(), myDate);
//        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();
//
//        /* Determine value of the stock being deMerged */
//        final TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
//        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
//        if (isForeignCredit) {
//            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
//        }
//
//        /* Record the current/delta units */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
//
//        /* Register the transaction */
//        myValues = myAsset.registerTransaction(theHelper);
//
//        /* Record values */
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
//        if (isForeignCredit) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
//        }
//
//        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a stockXchange.
     */
    void processStockXchange() {
//        /* Adjust the debit transfer details */
//        processDebitXferOut();
//
//        /* Adjust the credit transfer details */
//        processCreditXferIn();
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pHolding the credit holding
     */
    private void processCreditXferIn(final MoneyWiseSecurityHolding pHolding) {
//        /* Transfer is to the credit account and may or may not have a change to the units */
//        TethysMoney myAmount = theHelper.getCreditAmount();
//        final TethysRatio myExchangeRate = theHelper.getCreditExchangeRate();
//        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
//
//        /* Access the Asset Security Bucket */
//        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
//        final boolean isForeign = myAsset.isForeignCurrency();
//
//        /* If this is a foreign currency asset */
//        if (isForeign) {
//            /* Adjust foreign invested amount */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);
//
//            /* Switch to local amount */
//            myAmount = theHelper.getLocalAmount();
//        }
//
//        /* Adjust the cost and investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);
//
//        /* Determine the delta units */
//        final MoneyWiseSecurityClass mySecClass = mySecurity.getCategoryClass();
//        TethysUnits myDeltaUnits = theHelper.getCreditUnits();
//        TethysUnits myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        if (mySecClass.isAutoUnits() && myUnits.isZero()) {
//            myDeltaUnits = TethysUnits.getWholeUnits(mySecClass.getAutoUnits());
//        }
//
//        /* If we have new units */
//        if (myDeltaUnits != null) {
//            /* Record change in units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//        }
//
//        /* Adjust for National Insurance */
//        myAsset.adjustForNIPayments(theHelper);
//
//        /* Get the appropriate price for the account */
//        final TethysPrice myPrice = thePriceMap.getPriceForDate(mySecurity, theHelper.getDate());
//
//        /* Determine value of this stock after the transaction */
//        myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myValue = myUnits.valueAtPrice(myPrice);
//
//        /* If we are foreign */
//        if (isForeign) {
//            /* Determine local value */
//            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myExchangeRate);
//        }
//
//        /* Register the transaction */
//        final MoneyWiseAnalysisSecurityValues myValues = myAsset.registerTransaction(theHelper);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myValue);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHINVESTED, myAmount);
//        if (isForeign) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myExchangeRate);
//        }
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
