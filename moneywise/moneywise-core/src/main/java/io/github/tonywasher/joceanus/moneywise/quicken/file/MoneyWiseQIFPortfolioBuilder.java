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
package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;

import java.util.Iterator;
import java.util.List;

/**
 * Portfolio Builder class for QIF File.
 */
public class MoneyWiseQIFPortfolioBuilder {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseQIFPortfolioBuilder.class);

    /**
     * The QIF File.
     */
    private final MoneyWiseQIFFile theFile;

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The Builder.
     */
    private final MoneyWiseQIFBuilder theBuilder;

    /**
     * The Data.
     */
    private final MoneyWiseDataSet theData;

    /**
     * The Analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pBuilder  the builder
     * @param pData     the data
     * @param pAnalysis the analysis
     */
    protected MoneyWiseQIFPortfolioBuilder(final MoneyWiseQIFBuilder pBuilder,
                                           final MoneyWiseDataSet pData,
                                           final MoneyWiseAnalysis pAnalysis) {
        /* Store parameters */
        theBuilder = pBuilder;
        theFile = theBuilder.getFile();
        theFileType = theFile.getFileType();
        theData = pData;
        theAnalysis = pAnalysis;
    }

    /**
     * Obtain latest price for a security.
     *
     * @param pSecurity the security
     * @param pDate     the date
     * @return the price
     */
    private OceanusPrice getPriceForDate(final MoneyWiseSecurity pSecurity,
                                         final OceanusDate pDate) {
        /* Add the price */
        final MoneyWiseSecurityPriceDataMap myPriceMap = theData.getSecurityPriceDataMap();
        return myPriceMap.getPriceForDate(pSecurity, pDate);
    }

    /**
     * Obtain resulting units for a security holding event.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the units
     */
    private OceanusUnits getUnitsForHoldingEvent(final MoneyWiseSecurityHolding pHolding,
                                                 final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the resulting values */
        final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        return myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
    }

    /**
     * Obtain base units for a security holding event.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the units
     */
    protected OceanusUnits getBaseUnitsForHolding(final MoneyWiseSecurityHolding pHolding,
                                                  final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the base values */
        final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        if (myValues != null) {
            OceanusUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            myUnits = new OceanusUnits(myUnits);

            /* Determine the delta in units */
            final OceanusUnits myDelta = myBucket.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
            if (myDelta != null) {
                myUnits.subtractUnits(myDelta);
            }
            return myUnits;
        } else {
            return OceanusUnits.getWholeUnits(0);
        }
    }

    /**
     * Obtain delta cost for a security holding.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the delta cost
     */
    private OceanusMoney getDeltaCostForHolding(final MoneyWiseSecurityHolding pHolding,
                                                final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Obtain the cost delta for the transaction */
        return myBucket.getMoneyDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
    }

    /**
     * Obtain portfolio cash value.
     *
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     * @return the cash value (or null if none)
     */
    private OceanusMoney getPortfolioCashValue(final MoneyWisePortfolio pPortfolio,
                                               final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisPortfolioCashBucket myBucket = myPortfolios.getCashBucket(pPortfolio);

        /* Obtain the value delta for the transaction */
        OceanusMoney myValue = myBucket.getMoneyDeltaForTransaction(pTrans, MoneyWiseAnalysisAccountAttr.VALUATION);
        if (myValue != null) {
            myValue = new OceanusMoney(myValue);
            myValue.negate();
        }
        return myValue;
    }

    /**
     * Process income to a security.
     *
     * @param pPayee   the payee
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    protected void processIncomeToSecurity(final MoneyWisePayee pPayee,
                                           final MoneyWiseSecurityHolding pHolding,
                                           final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPort = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        final MoneyWiseQIFPayee myQPayee = theFile.registerPayee(pPayee);
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final MoneyWiseQIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        final OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        final OceanusPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create output amount */
            final OceanusMoney myOutAmount = new OceanusMoney(myAmount);
            myOutAmount.negate();

            /* Create an event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myEvent.recordAmount(new OceanusMoney());
            myEvent.recordPayee(myQPayee);

            /* record the splits */
            myEvent.recordSplitRecord(myQCategory, myList, myAmount, myQPayee.getName());
            myEvent.recordSplitRecord(myPortfolio.getAccount(), myOutAmount, myPort.getName());

            /* Add to event list */
            myHolding.addEvent(myEvent);

            /* else we can do this properly */
        } else {
            /* Create a miscellaneous cash event */
            final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myQPayee);
            myEvent.recordCategory(myQCategory, myList);

            /* Add to event list */
            myPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.BUY);
        myEvent.recordAmount(myAmount);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);
        myEvent.recordPrice(myPrice);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }

    /**
     * Process expense from a security.
     *
     * @param pPayee   the payee
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    protected void processExpenseFromSecurity(final MoneyWisePayee pPayee,
                                              final MoneyWiseSecurityHolding pHolding,
                                              final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPort = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        final MoneyWiseQIFPayee myQPayee = theFile.registerPayee(pPayee);
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final MoneyWiseQIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        myUnits = new OceanusUnits(myUnits);
        myUnits.negate();
        final OceanusPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Create a sell shares event */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SELL);
        myEvent.recordAmount(myAmount);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);
        myEvent.recordPrice(myPrice);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create output amount */
        final OceanusMoney myOutAmount = new OceanusMoney(myAmount);
        myOutAmount.negate();

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create an event */
            final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myHoldEvent.recordAmount(new OceanusMoney());
            myHoldEvent.recordPayee(myQPayee);

            /* record the splits */
            myHoldEvent.recordSplitRecord(myPortfolio.getAccount(), myAmount, myPort.getName());
            myHoldEvent.recordSplitRecord(myQCategory, myList, myOutAmount, myQPayee.getName());

            /* Add to event list */
            myHolding.addEvent(myEvent);

            /* else we can do this properly */
        } else {
            /* Create a miscellaneous cash event */
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
            myEvent.recordAmount(myOutAmount);
            myEvent.recordPayee(myQPayee);
            myEvent.recordCategory(myQCategory, myList);

            /* Add to event list */
            myPortfolio.addEvent(myEvent);
        }
    }

    /**
     * Process transfer to a security.
     * <p>
     * Note that the source cannot be a Security, since that case is handled by
     * {@link #processTransferFromSecurity}
     *
     * @param pHolding the security holding
     * @param pDebit   the debit account
     * @param pTrans   the transaction
     */
    protected void processTransferToSecurity(final MoneyWiseSecurityHolding pHolding,
                                             final MoneyWiseTransAsset pDebit,
                                             final MoneyWiseTransaction pTrans) {
        /* Handle Loyalty bonus separately */
        if (MoneyWiseTransCategoryClass.LOYALTYBONUS.equals(pTrans.getCategoryClass())) {
            processIncomeToSecurity((MoneyWisePayee) pDebit.getParent(), pHolding, pTrans);
            return;
        }

        /* Access Portfolio Account */
        final MoneyWisePortfolio myPort = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Access Transaction details */
        final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pDebit);
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Determine various flags */
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();
        boolean canXferLinked = theFileType.canXferPortfolio();
        boolean hideBalancingSplitXfer = theFileType.hideBalancingSplitTransfer();
        hideBalancingSplitXfer &= canXferLinked;

        /* Check for transfer from portfolio */
        if (pDebit.equals(myPort)) {
            /* Make sure we don't try to link account */
            canXferLinked = false;
            hideBalancingSplitXfer = true;
        }

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits == null) {
            myUnits = pTrans.getPartnerDeltaUnits();
        }
        final OceanusPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = OceanusUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new OceanusUnits();
            }
        }

        /* Create a buy shares event for the new shares */
        MoneyWiseQIFPortfolioEvent myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, canXferLinked
                ? MoneyWiseQActionType.BUYX
                : MoneyWiseQActionType.BUY);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQSecurity);
        myPortEvent.recordQuantity(myUnits);
        myPortEvent.recordPrice(myPrice);
        if (canXferLinked) {
            myPortEvent.recordXfer(mySource.getAccount(), myList, myAmount);
        }

        /* Add to event list */
        myPortfolio.addEvent(myPortEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsOut event to balance */
            myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSOUT);
            myPortEvent.recordSecurity(myQSecurity);
            myPortEvent.recordQuantity(myUnits);

            /* Add to event list */
            myPortfolio.addEvent(myPortEvent);
        }

        /* If we are not hiding the balancing transfer */
        if (!hideBalancingSplitXfer) {
            /* Build output amount */
            final OceanusMoney myOutAmount = new OceanusMoney(myAmount);
            myOutAmount.negate();

            /* Build the source transfer */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myEvent.recordAccount(myPortfolio.getAccount(), myList);
            myEvent.recordAmount(myOutAmount);

            /* Build payee description */
            myEvent.recordPayee(theBuilder.buildXferToPayee(myPort));

            /* Add event to event list */
            mySource.addEvent(myEvent);
        }
    }

    /**
     * Process transfer between securities.
     *
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans  the transaction
     */
    protected void processTransferBetweenSecurities(final MoneyWiseSecurityHolding pSource,
                                                    final MoneyWiseSecurityHolding pTarget,
                                                    final MoneyWiseTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case STOCKSPLIT:
                if (theFileType.useStockSplit()) {
                    processStockSplit(pSource, pTrans);
                } else {
                    processSecurityAdjust(pSource, pTrans);
                }
                break;
            case UNITSADJUST:
                processSecurityAdjust(pSource, pTrans);
                break;
            case DIVIDEND:
                processReinvestDividend(pSource, pTrans);
                break;
            case STOCKDEMERGER:
                processStockDeMerger(pSource, pTarget, pTrans);
                break;
            case STOCKTAKEOVER:
            case SECURITYREPLACE:
                processStockTakeOver(pSource, pTarget, pTrans);
                break;
            case TRANSFER:
                processSecurityExchange(pSource, pTarget, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferBetweenSecurities Category: <%s>", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process transfer from a security.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    protected void processTransferFromSecurity(final MoneyWiseSecurityHolding pHolding,
                                               final MoneyWiseTransAsset pCredit,
                                               final MoneyWiseTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case DIVIDEND:
                processStockDividend(pHolding, pCredit, pTrans);
                break;
            case PORTFOLIOXFER:
                processPortfolioXferForHolding(pHolding, (MoneyWisePortfolio) pCredit, pTrans);
                break;
            case TRANSFER:
            case STOCKRIGHTSISSUE:
            default:
                processTransferOut(pHolding, pCredit, pTrans);
                break;
        }
    }

    /**
     * Process Stock Split.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    private void processStockSplit(final MoneyWiseSecurityHolding pHolding,
                                   final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Obtain number of units after this event */
        final OceanusUnits myTotalUnits = getUnitsForHoldingEvent(pHolding, pTrans);

        /* Access the delta units */
        final OceanusUnits myDeltaUnits = pTrans.getAccountDeltaUnits();

        /* Obtain number of units before event */
        final OceanusUnits myBaseUnits = new OceanusUnits(myTotalUnits);
        myBaseUnits.subtractUnits(myDeltaUnits);

        /* Obtain split ratio */
        final OceanusRatio mySplit = new OceanusRatio(myTotalUnits, myBaseUnits);
        mySplit.multiply(OceanusDecimal.RADIX_TEN);

        /* Create a stock split event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.STKSPLIT);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(mySplit);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);
    }

    /**
     * Process stock adjustment.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    private void processSecurityAdjust(final MoneyWiseSecurityHolding pHolding,
                                       final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Access the delta units */
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        final boolean isCredit = myUnits.isPositive();
        if (!isCredit) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }

        /* Create a share movement event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, isCredit
                ? MoneyWiseQActionType.SHRSIN
                : MoneyWiseQActionType.SHRSOUT);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);
    }

    /**
     * Process stock dividend.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    private void processStockDividend(final MoneyWiseSecurityHolding pHolding,
                                      final MoneyWiseTransAsset pCredit,
                                      final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Obtain flags */
        boolean canXferLinked = theFileType.canXferPortfolio();
        final boolean isPortfolio = pCredit.equals(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        OceanusMoney myAmount = pTrans.getAmount();
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        final OceanusMoney myFullAmount = new OceanusMoney(myAmount);
        if (myTaxCredit != null) {
            myFullAmount.addAmount(myTaxCredit);
        }

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Determine whether we should XferLinked */
        boolean doXferLinked = canXferLinked && myTaxCredit == null;

        /* Check for dividend held in portfolio */
        if (isPortfolio) {
            /* Make sure we don't try to link account */
            doXferLinked = false;
            canXferLinked = false;
        }

        /* Create a dividend event */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, doXferLinked
                ? MoneyWiseQActionType.DIVX
                : MoneyWiseQActionType.DIV);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordAmount(myFullAmount);
        if (doXferLinked) {
            myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);
        }

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we can use XOut records */
        if (!doXferLinked && canXferLinked) {
            /* Create a transfer out event */
            myAmount = pTrans.getAmount();
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Don't do if receiving dividend in portfolio */
        if (!isPortfolio) {
            /* If the receiving account is a portfolio */
            if (pCredit instanceof MoneyWisePortfolio) {
                /* Create the receiving transfer event */
                final MoneyWiseQIFPortfolioEvent myXferEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XIN);
                myXferEvent.recordAmount(myAmount);
                myXferEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
                myXferEvent.recordXfer(myQPortfolio.getAccount(), myList, myAmount);

                /* Add to event list */
                myTarget.addEvent(myXferEvent);

                /* else standard account */
            } else {
                /* Create the receiving transfer event */
                final MoneyWiseQIFEvent myXferEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                myXferEvent.recordAmount(myAmount);
                myXferEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
                myXferEvent.recordAccount(myQPortfolio.getAccount(), myList);

                /* Add to event list */
                myTarget.addEvent(myXferEvent);
            }
        }

        /* If we have a Tax Credit */
        if (myTaxCredit != null) {
            /* Determine flags */
            final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

            /* Access category */
            final MoneyWiseQIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            final MoneyWiseQIFPayee myTaxPayee = theBuilder.getTaxMan();

            /* Create output amount */
            final OceanusMoney myOutAmount = new OceanusMoney(myTaxCredit);
            myOutAmount.negate();

            /* If we are using a holding account */
            if (useHoldingAccount) {
                /* Access Holding Account */
                final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                /* Create an event */
                final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                myHoldEvent.recordAmount(new OceanusMoney());
                myHoldEvent.recordPayee(myTaxPayee);

                /* record the splits */
                myHoldEvent.recordSplitRecord(myQPortfolio.getAccount(), myTaxCredit, myPortfolio.getName());
                myHoldEvent.recordSplitRecord(myTaxCategory, myOutAmount, myTaxPayee.getName());

                /* Add to event list */
                myHolding.addEvent(myHoldEvent);

                /* else we can do this properly */
            } else {
                /* Create a tax credit event */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
                myEvent.recordAmount(myOutAmount);
                myEvent.recordPayee(myTaxPayee);
                myEvent.recordCategory(myTaxCategory);

                /* Add to event list */
                myQPortfolio.addEvent(myEvent);
            }
        }
    }

    /**
     * Process reinvested dividend.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    private void processReinvestDividend(final MoneyWiseSecurityHolding pHolding,
                                         final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine various flags */
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        myAmount = new OceanusMoney(myAmount);
        if (myTaxCredit != null) {
            myAmount.addAmount(myTaxCredit);
        }

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = OceanusUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new OceanusUnits();
            }
        }

        /* Create a re-invest dividend event */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.REINVDIV);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordAmount(myAmount);
        myEvent.recordQuantity(myUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsOut event to balance */
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSOUT);
            myEvent.recordSecurity(myQSecurity);
            myEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* If we have a Tax Credit */
        if (myTaxCredit != null) {
            /* Determine flags */
            final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();
            final boolean useMiscIncX = theFileType.useMiscIncX4TaxCredit();

            /* Access category */
            final MoneyWiseQIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            final MoneyWiseQIFPayee myTaxPayee = theBuilder.getTaxMan();

            /* Create a tax credit event */
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, useMiscIncX
                    ? MoneyWiseQActionType.MISCINCX
                    : MoneyWiseQActionType.MISCINC);
            myEvent.recordSecurity(myQSecurity);
            myEvent.recordAmount(myTaxCredit);
            if (useMiscIncX) {
                myEvent.recordPayee(myTaxPayee);
                myEvent.recordCategory(myTaxCategory);
            }

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);

            /* If we need further elements */
            if (!useMiscIncX) {
                /* Create output amount */
                final OceanusMoney myOutAmount = new OceanusMoney(myTaxCredit);
                myOutAmount.negate();

                /* If we are using a holding account */
                if (useHoldingAccount) {
                    /* Access Holding Account */
                    final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                    /* Create an event */
                    final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                    myHoldEvent.recordAmount(new OceanusMoney());
                    myHoldEvent.recordPayee(myTaxPayee);

                    /* record the splits */
                    myHoldEvent.recordSplitRecord(myQPortfolio.getAccount(), myTaxCredit, myPortfolio.getName());
                    myHoldEvent.recordSplitRecord(myTaxCategory, myOutAmount, myTaxPayee.getName());

                    /* Add to event list */
                    myHolding.addEvent(myHoldEvent);

                    /* else we can do this properly */
                } else {
                    /* Create a tax credit event */
                    myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
                    myEvent.recordAmount(myOutAmount);
                    myEvent.recordPayee(myTaxPayee);
                    myEvent.recordCategory(myTaxCategory);

                    /* Add to event list */
                    myQPortfolio.addEvent(myEvent);
                }
            }
        }
    }

    /**
     * Process stock deMerger.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    private void processStockDeMerger(final MoneyWiseSecurityHolding pHolding,
                                      final MoneyWiseSecurityHolding pCredit,
                                      final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine whether we can return capital */
        final boolean canReturnCapital = theFileType.canReturnCapital();
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myDebitSecurity = theFile.registerSecurity(mySecurity);
        final MoneyWiseQIFSecurity myCreditSecurity = theFile.registerSecurity(myCredit);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits != null) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }
        final OceanusPrice myDebitPrice = getPriceForDate(mySecurity, myDate);
        final OceanusPrice myCreditPrice = getPriceForDate(myCredit, myDate);

        /* Obtain the delta cost (i.e. value transferred) */
        OceanusMoney myValue = getDeltaCostForHolding(pHolding, pTrans);
        myValue = new OceanusMoney(myValue);
        myValue.negate();

        /* Determine whether we use return capital */
        final boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = OceanusUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new OceanusUnits();
            }
        }

        /* Create a sellShares/returnCapital event for the share reduction */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, doReturnCapital
                ? MoneyWiseQActionType.RTRNCAP
                : MoneyWiseQActionType.SELL);
        myEvent.recordAmount(myValue);
        myEvent.recordSecurity(myDebitSecurity);
        myEvent.recordPrice(myDebitPrice);
        if (!doReturnCapital) {
            myEvent.recordQuantity(myUnits);
        }

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsIn event to balance */
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSIN);
            myEvent.recordSecurity(myDebitSecurity);
            myEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event for the new shares */
        myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.BUY);
        myEvent.recordAmount(myValue);
        myEvent.recordSecurity(myCreditSecurity);
        myEvent.recordQuantity(pTrans.getPartnerDeltaUnits());
        myEvent.recordPrice(myCreditPrice);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);
    }

    /**
     * Process security Exchange/TakeOver.
     *
     * @param pSource the source security
     * @param pTarget the target security
     * @param pTrans  the transaction
     */
    private void processStockTakeOver(final MoneyWiseSecurityHolding pSource,
                                      final MoneyWiseSecurityHolding pTarget,
                                      final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurity mySource = pSource.getSecurity();
        final MoneyWiseSecurity myTarget = pTarget.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myDebitSecurity = theFile.registerSecurity(mySource);
        final MoneyWiseQIFSecurity myCreditSecurity = theFile.registerSecurity(myTarget);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        final OceanusUnits myUnits = pTrans.getPartnerDeltaUnits();
        final OceanusPrice myDebitPrice = getPriceForDate(mySource, myDate);
        final OceanusPrice myCreditPrice = getPriceForDate(myTarget, myDate);
        final MoneyWiseDeposit myThirdParty = (MoneyWiseDeposit) pTrans.getReturnedCashAccount();
        final OceanusMoney myAmount = pTrans.getReturnedCash();

        /* Obtain the number of units that we are selling */
        final OceanusUnits myBaseUnits = getBaseUnitsForHolding(pSource, pTrans);

        /* Obtain the delta cost (i.e. value transferred) */
        final OceanusMoney myStockCost = getDeltaCostForHolding(pSource, pTrans);

        /* Determine the total sale value */
        final OceanusMoney mySaleValue = new OceanusMoney(myStockCost);
        mySaleValue.addAmount(myAmount);

        /* Create a sellShares event for the share reduction */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SELL);
        myEvent.recordAmount(mySaleValue);
        myEvent.recordSecurity(myDebitSecurity);
        myEvent.recordPrice(myDebitPrice);
        myEvent.recordQuantity(myBaseUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* Create a buy shares event for the new shares */
        myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.BUY);
        myEvent.recordAmount(myStockCost);
        myEvent.recordSecurity(myCreditSecurity);
        myEvent.recordQuantity(myUnits);
        myEvent.recordPrice(myCreditPrice);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we have a ThirdParty Account */
        if (myThirdParty != null) {
            /* determine flags */
            final boolean canXferDirect = theFileType.canXferPortfolio();

            /* Access Target account */
            final MoneyWiseQIFAccountEvents myQTarget = theFile.registerAccount(myThirdParty);

            /* If we can transfer direct */
            if (canXferDirect) {
                /* Create a transfer out event for the cash payment */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
                myEvent.recordAmount(myAmount);
                myEvent.recordXfer(myQTarget.getAccount(), myAmount);

                /* Add to event list */
                myQPortfolio.addEvent(myEvent);
            } else {
                /* Build the target transfer */
                final MoneyWiseQIFEvent myXferEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                myXferEvent.recordAccount(myQPortfolio.getAccount());
                myXferEvent.recordAmount(myAmount);

                /* Build payee description */
                myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));

                /* Add event to event list */
                myQTarget.addEvent(myEvent);
            }
        }
    }

    /**
     * Process standard transfer out from a security.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    private void processTransferOut(final MoneyWiseSecurityHolding pHolding,
                                    final MoneyWiseTransAsset pCredit,
                                    final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Determine various flags */
        final boolean canReturnCapital = theFileType.canReturnCapital();
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();
        boolean canXferLinked = theFileType.canXferPortfolio();
        boolean hideBalancingSplitXfer = theFileType.hideBalancingSplitTransfer();
        hideBalancingSplitXfer &= canXferLinked;

        /* Check for transfer to portfolio */
        if (pCredit.equals(myPortfolio)) {
            /* Make sure we don't try to link account */
            canXferLinked = false;
            hideBalancingSplitXfer = true;
        }

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits == null) {
            myUnits = pTrans.getPartnerDeltaUnits();
        }
        if (myUnits != null) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }
        final OceanusPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Determine whether we use return capital */
        final boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = OceanusUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new OceanusUnits();
            }
        }

        /* Create a sellShares/returnCapital event */
        MoneyWiseQIFPortfolioEvent myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, doReturnCapital
                ? canXferLinked
                ? MoneyWiseQActionType.RTRNCAPX
                : MoneyWiseQActionType.RTRNCAP
                : canXferLinked
                ? MoneyWiseQActionType.SELLX
                : MoneyWiseQActionType.SELL);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQSecurity);
        if (!doReturnCapital) {
            myPortEvent.recordQuantity(myUnits);
        }
        myPortEvent.recordPrice(myPrice);
        if (canXferLinked) {
            myPortEvent.recordXfer(myTarget.getAccount(), myList, myAmount);
        }

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsIn event to balance */
            myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSIN);
            myPortEvent.recordSecurity(myQSecurity);
            myPortEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myPortEvent);
        }

        /* If we are not hiding the balancing transfer */
        if (!hideBalancingSplitXfer) {
            /* Build the source transfer */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myEvent.recordAccount(myQPortfolio.getAccount(), myList);
            myEvent.recordAmount(myAmount);

            /* Build payee description */
            myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));

            /* Add event to event list */
            myTarget.addEvent(myEvent);
        }
    }

    /**
     * Process exchange between securities.
     *
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans  the transaction
     */
    private void processSecurityExchange(final MoneyWiseSecurityHolding pSource,
                                         final MoneyWiseSecurityHolding pTarget,
                                         final MoneyWiseTransaction pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurity mySource = pSource.getSecurity();
        final MoneyWiseSecurity myTarget = pTarget.getSecurity();
        final MoneyWiseQIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseQIFSecurity myQSource = theFile.registerSecurity(mySource);
        final MoneyWiseQIFSecurity myQTarget = theFile.registerSecurity(myTarget);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits mySourceUnits = pTrans.getAccountDeltaUnits();
        mySourceUnits = new OceanusUnits(mySourceUnits);
        mySourceUnits.negate();
        final OceanusUnits myTargetUnits = pTrans.getPartnerDeltaUnits();
        final OceanusPrice mySourcePrice = getPriceForDate(mySource, myDate);
        final OceanusPrice myTargetPrice = getPriceForDate(myTarget, myDate);

        /* Create a sellShares/returnCapital event */
        MoneyWiseQIFPortfolioEvent myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SELL);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQSource);
        myPortEvent.recordQuantity(mySourceUnits);
        myPortEvent.recordPrice(mySourcePrice);

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);

        /* Create a buyShares event */
        myPortEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.BUY);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQTarget);
        myPortEvent.recordQuantity(myTargetUnits);
        myPortEvent.recordPrice(myTargetPrice);

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);
    }

    /**
     * Process transfer between portfolios.
     *
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    protected void processTransferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                    final MoneyWisePortfolio pTarget,
                                                    final MoneyWiseTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case INTEREST:
            case LOYALTYBONUS:
                processIncomeToPortfolio(pSource.getParent(), pTarget, pTrans);
                break;
            case PORTFOLIOXFER:
                processPortfolioXferBetweenPortfolios(pSource, pTarget, pTrans);
                break;
            case TRANSFER:
                processCashTransferBetweenPortfolios(pSource, pTarget, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferBetweenPortfolios Category: <%s>", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process PortfolioXfer between portfolios.
     *
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    protected void processPortfolioXferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                         final MoneyWisePortfolio pTarget,
                                                         final MoneyWiseTransaction pTrans) {
        /* If there is cash to transfer */
        final OceanusMoney myAmount = getPortfolioCashValue(pSource, pTrans);
        if (myAmount != null) {
            /* Access details */
            final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pSource);
            final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);

            /* Obtain classes */
            final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

            /* Create an XOut event */
            MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            mySource.addEvent(myEvent);

            /* Create an XIn event */
            myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XIN);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferFromPayee(pSource));
            myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

            /* Add to event list */
            myTarget.addEvent(myEvent);
        }

        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisPortfolioBucket myBucket = myPortfolios.getBucket(pSource);

        /* Loop through the securities */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = myBucket.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket mySecurity = myIterator.next();

            /* Process transfer for this bucket */
            processPortfolioXferForHolding(mySecurity.getSecurityHolding(), pTarget, pTrans);
        }
    }

    /**
     * Process PortfolioXfer for Holding.
     *
     * @param pSource the source holding
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    protected void processPortfolioXferForHolding(final MoneyWiseSecurityHolding pSource,
                                                  final MoneyWisePortfolio pTarget,
                                                  final MoneyWiseTransaction pTrans) {
        /* Determine if this holding was transferred */
        final OceanusUnits myUnits = getBaseUnitsForHolding(pSource, pTrans);
        if (myUnits.isNonZero()) {
            /* Access details */
            final MoneyWisePortfolio mySourcePortfolio = pSource.getPortfolio();
            final MoneyWiseSecurity mySecurity = pSource.getSecurity();
            final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(mySourcePortfolio);
            final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
            final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
            OceanusMoney myCost = getDeltaCostForHolding(pSource, pTrans);

            /* If there is an associated cost */
            if (myCost != null) {
                /* Convert cost to positive */
                myCost = new OceanusMoney(myCost);
                myCost.negate();

                /* Obtain price for the date */
                final OceanusPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

                /* Obtain classes */
                final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

                /* Create a sell shares event */
                MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SELL);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XOut event */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
                myEvent.recordXfer(myTarget.getAccount(), myList, myCost);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XIn event */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XIN);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theBuilder.buildXferFromPayee(pSource));
                myEvent.recordXfer(mySource.getAccount(), myList, myCost);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* Create a buy shares event */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.BUY);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* else just simple transfer of shares */
            } else {
                /* Create an SharesOut event */
                MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSOUT);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an SharesIn event */
                myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SHRSIN);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);

                /* Add to event list */
                myTarget.addEvent(myEvent);
            }
        }
    }

    /**
     * Process Cash Transfer between portfolios.
     *
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    protected void processCashTransferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                        final MoneyWisePortfolio pTarget,
                                                        final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pSource);
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XOut event */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        mySource.addEvent(myEvent);

        /* Create an XIn event */
        myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XIN);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferFromPayee(pSource));
        myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

        /* Add to event list */
        myTarget.addEvent(myEvent);
    }

    /**
     * Process transfer to a portfolio.
     *
     * @param pPortfolio the portfolio
     * @param pDebit     the source account
     * @param pTrans     the transaction
     */
    protected void processTransferToPortfolio(final MoneyWisePortfolio pPortfolio,
                                              final MoneyWiseTransAsset pDebit,
                                              final MoneyWiseTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case TRANSFER:
                processCashTransferToPortfolio(pPortfolio, pDebit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferToPortfolio Category: <%s>", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process Cash Transfer to portfolio.
     *
     * @param pPortfolio the target portfolio
     * @param pSource    the source account
     * @param pTrans     the transaction
     */
    protected void processCashTransferToPortfolio(final MoneyWisePortfolio pPortfolio,
                                                  final MoneyWiseTransAsset pSource,
                                                  final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pSource);
        OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XIn event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XIN);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pSource));
        myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the sending transfer event */
        final MoneyWiseQIFEvent myXferEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myAmount = new OceanusMoney(myAmount);
        myAmount.negate();
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theBuilder.buildXferFromPayee(pPortfolio));
        myXferEvent.recordAccount(myPortfolio.getAccount(), myList);

        /* Add to event list */
        mySource.addEvent(myXferEvent);
    }

    /**
     * Process transfer from a portfolio.
     *
     * @param pPortfolio the portfolio
     * @param pCredit    the target account
     * @param pTrans     the transaction
     */
    protected void processTransferFromPortfolio(final MoneyWisePortfolio pPortfolio,
                                                final MoneyWiseTransAsset pCredit,
                                                final MoneyWiseTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case TRANSFER:
                processCashTransferFromPortfolio(pPortfolio, pCredit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferFromPortfolio Category: <%s>", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process Cash Transfer from portfolio.
     *
     * @param pPortfolio the source portfolio
     * @param pTarget    the target account
     * @param pTrans     the transaction
     */
    protected void processCashTransferFromPortfolio(final MoneyWisePortfolio pPortfolio,
                                                    final MoneyWiseTransAsset pTarget,
                                                    final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XOut event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the receiving transfer event */
        final MoneyWiseQIFEvent myXferEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theBuilder.buildXferFromPayee(pPortfolio));
        myXferEvent.recordAccount(myPortfolio.getAccount(), myList);

        /* Add to event list */
        myTarget.addEvent(myXferEvent);
    }

    /**
     * Process expense from a portfolio.
     *
     * @param pCredit    the target payee
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     */
    protected void processExpenseFromPortfolio(final MoneyWisePayee pCredit,
                                               final MoneyWisePortfolio pPortfolio,
                                               final MoneyWiseTransaction pTrans) {
        /* Access Details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pCredit);
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create an expense event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }

    /**
     * Process income to a portfolio.
     *
     * @param pDebit     the source payee
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     */
    protected void processIncomeToPortfolio(final MoneyWisePayee pDebit,
                                            final MoneyWisePortfolio pPortfolio,
                                            final MoneyWiseTransaction pTrans) {
        /* Access Details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pDebit);
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Create an income event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }
}
