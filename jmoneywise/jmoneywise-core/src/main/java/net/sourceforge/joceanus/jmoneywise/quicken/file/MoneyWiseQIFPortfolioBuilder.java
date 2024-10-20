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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQIFType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

import java.util.Iterator;
import java.util.List;

/**
 * Portfolio Builder class for QIF File.
 */
public class MoneyWiseQIFPortfolioBuilder {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseQIFPortfolioBuilder.class);

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
     * @param pBuilder the builder
     * @param pData the data
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
     * @param pSecurity the security
     * @param pDate the date
     * @return the price
     */
    private TethysPrice getPriceForDate(final MoneyWiseSecurity pSecurity,
                                        final TethysDate pDate) {
        /* Add the price */
        final MoneyWiseSecurityPriceDataMap myPriceMap = theData.getSecurityPriceDataMap();
        return myPriceMap.getPriceForDate(pSecurity, pDate);
    }

    /**
     * Obtain resulting units for a security holding event.
     * @param pHolding the security holding
     * @param pTrans the transaction
     * @return the units
     */
    private TethysUnits getUnitsForHoldingEvent(final MoneyWiseSecurityHolding pHolding,
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
     * @param pHolding the security holding
     * @param pTrans the transaction
     * @return the units
     */
    protected TethysUnits getBaseUnitsForHolding(final MoneyWiseSecurityHolding pHolding,
                                                 final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the base values */
        final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        if (myValues != null) {
            TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            myUnits = new TethysUnits(myUnits);

            /* Determine the delta in units */
            final TethysUnits myDelta = myBucket.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
            if (myDelta != null) {
                myUnits.subtractUnits(myDelta);
            }
            return myUnits;
        } else {
            return TethysUnits.getWholeUnits(0);
        }
    }

    /**
     * Obtain delta cost for a security holding.
     * @param pHolding the security holding
     * @param pTrans the transaction
     * @return the delta cost
     */
    private TethysMoney getDeltaCostForHolding(final MoneyWiseSecurityHolding pHolding,
                                               final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Obtain the cost delta for the transaction */
        return myBucket.getMoneyDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
    }

    /**
     * Obtain portfolio cash value.
     * @param pPortfolio the portfolio
     * @param pTrans the transaction
     * @return the cash value (or null if none)
     */
    private TethysMoney getPortfolioCashValue(final MoneyWisePortfolio pPortfolio,
                                              final MoneyWiseTransaction pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseAnalysisPortfolioCashBucket myBucket = myPortfolios.getCashBucket(pPortfolio);

        /* Obtain the value delta for the transaction */
        TethysMoney myValue = myBucket.getMoneyDeltaForTransaction(pTrans, MoneyWiseAnalysisAccountAttr.VALUATION);
        if (myValue != null) {
            myValue = new TethysMoney(myValue);
            myValue.negate();
        }
        return myValue;
    }

    /**
     * Process income to a security.
     * @param pPayee the payee
     * @param pHolding the security holding
     * @param pTrans the transaction
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
        final TethysMoney myAmount = pTrans.getAmount();
        final TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create output amount */
            final TethysMoney myOutAmount = new TethysMoney(myAmount);
            myOutAmount.negate();

            /* Create an event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myEvent.recordAmount(new TethysMoney());
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
     * @param pPayee the payee
     * @param pHolding the security holding
     * @param pTrans the transaction
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
        final TethysMoney myAmount = pTrans.getAmount();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        myUnits = new TethysUnits(myUnits);
        myUnits.negate();
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Create a sell shares event */
        MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.SELL);
        myEvent.recordAmount(myAmount);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);
        myEvent.recordPrice(myPrice);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create output amount */
        final TethysMoney myOutAmount = new TethysMoney(myAmount);
        myOutAmount.negate();

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create an event */
            final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
            myHoldEvent.recordAmount(new TethysMoney());
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
     * @param pHolding the security holding
     * @param pDebit the debit account
     * @param pTrans the transaction
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
        final TethysMoney myAmount = pTrans.getAmount();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits == null) {
            myUnits = pTrans.getPartnerDeltaUnits();
        }
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = TethysUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new TethysUnits();
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
            final TethysMoney myOutAmount = new TethysMoney(myAmount);
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
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans the transaction
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
     * @param pHolding the security holding
     * @param pCredit the credit account
     * @param pTrans the transaction
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
     * @param pHolding the security holding
     * @param pTrans the transaction
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
        final TethysUnits myTotalUnits = getUnitsForHoldingEvent(pHolding, pTrans);

        /* Access the delta units */
        final TethysUnits myDeltaUnits = pTrans.getAccountDeltaUnits();

        /* Obtain number of units before event */
        final TethysUnits myBaseUnits = new TethysUnits(myTotalUnits);
        myBaseUnits.subtractUnits(myDeltaUnits);

        /* Obtain split ratio */
        final TethysRatio mySplit = new TethysRatio(myTotalUnits, myBaseUnits);
        mySplit.multiply(TethysDecimal.RADIX_TEN);

        /* Create a stock split event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.STKSPLIT);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(mySplit);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);
    }

    /**
     * Process stock adjustment.
     * @param pHolding the security holding
     * @param pTrans the transaction
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
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        final boolean isCredit = myUnits.isPositive();
        if (!isCredit) {
            myUnits = new TethysUnits(myUnits);
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
     * @param pHolding the security holding
     * @param pCredit the credit account
     * @param pTrans the transaction
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
        TethysMoney myAmount = pTrans.getAmount();
        final TethysMoney myTaxCredit = pTrans.getTaxCredit();
        final TethysMoney myFullAmount = new TethysMoney(myAmount);
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
            final TethysMoney myOutAmount = new TethysMoney(myTaxCredit);
            myOutAmount.negate();

            /* If we are using a holding account */
            if (useHoldingAccount) {
                /* Access Holding Account */
                final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                /* Create an event */
                final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                myHoldEvent.recordAmount(new TethysMoney());
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
     * @param pHolding the security holding
     * @param pTrans the transaction
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
        TethysMoney myAmount = pTrans.getAmount();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        final TethysMoney myTaxCredit = pTrans.getTaxCredit();
        myAmount = new TethysMoney(myAmount);
        if (myTaxCredit != null) {
            myAmount.addAmount(myTaxCredit);
        }

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = TethysUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new TethysUnits();
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
                final TethysMoney myOutAmount = new TethysMoney(myTaxCredit);
                myOutAmount.negate();

                /* If we are using a holding account */
                if (useHoldingAccount) {
                    /* Access Holding Account */
                    final MoneyWiseQIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                    /* Create an event */
                    final MoneyWiseQIFEvent myHoldEvent = new MoneyWiseQIFEvent(theFile, pTrans);
                    myHoldEvent.recordAmount(new TethysMoney());
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
     * @param pHolding the security holding
     * @param pCredit the credit account
     * @param pTrans the transaction
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
        final TethysDate myDate = pTrans.getDate();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits != null) {
            myUnits = new TethysUnits(myUnits);
            myUnits.negate();
        }
        final TethysPrice myDebitPrice = getPriceForDate(mySecurity, myDate);
        final TethysPrice myCreditPrice = getPriceForDate(myCredit, myDate);

        /* Obtain the delta cost (i.e. value transferred) */
        TethysMoney myValue = getDeltaCostForHolding(pHolding, pTrans);
        myValue = new TethysMoney(myValue);
        myValue.negate();

        /* Determine whether we use return capital */
        final boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = TethysUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new TethysUnits();
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
     * @param pSource the source security
     * @param pTarget the target security
     * @param pTrans the transaction
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
        final TethysDate myDate = pTrans.getDate();
        final TethysUnits myUnits = pTrans.getPartnerDeltaUnits();
        final TethysPrice myDebitPrice = getPriceForDate(mySource, myDate);
        final TethysPrice myCreditPrice = getPriceForDate(myTarget, myDate);
        final MoneyWiseDeposit myThirdParty = (MoneyWiseDeposit) pTrans.getReturnedCashAccount();
        final TethysMoney myAmount = pTrans.getReturnedCash();

        /* Obtain the number of units that we are selling */
        final TethysUnits myBaseUnits = getBaseUnitsForHolding(pSource, pTrans);

        /* Obtain the delta cost (i.e. value transferred) */
        final TethysMoney myStockCost = getDeltaCostForHolding(pSource, pTrans);

        /* Determine the total sale value */
        final TethysMoney mySaleValue = new TethysMoney(myStockCost);
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
     * @param pHolding the security holding
     * @param pCredit the credit account
     * @param pTrans the transaction
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
        final TethysMoney myAmount = pTrans.getAmount();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits == null) {
            myUnits = pTrans.getPartnerDeltaUnits();
        }
        if (myUnits != null) {
            myUnits = new TethysUnits(myUnits);
            myUnits.negate();
        }
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Determine whether we use return capital */
        final boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = TethysUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new TethysUnits();
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
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans the transaction
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
        final TethysDate myDate = pTrans.getDate();
        final TethysMoney myAmount = pTrans.getAmount();
        TethysUnits mySourceUnits = pTrans.getAccountDeltaUnits();
        mySourceUnits = new TethysUnits(mySourceUnits);
        mySourceUnits.negate();
        final TethysUnits myTargetUnits = pTrans.getPartnerDeltaUnits();
        final TethysPrice mySourcePrice = getPriceForDate(mySource, myDate);
        final TethysPrice myTargetPrice = getPriceForDate(myTarget, myDate);

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
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans the transaction
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
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans the transaction
     */
    protected void processPortfolioXferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                         final MoneyWisePortfolio pTarget,
                                                         final MoneyWiseTransaction pTrans) {
        /* If there is cash to transfer */
        final TethysMoney myAmount = getPortfolioCashValue(pSource, pTrans);
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
     * @param pSource the source holding
     * @param pTarget the target portfolio
     * @param pTrans the transaction
     */
    protected void processPortfolioXferForHolding(final MoneyWiseSecurityHolding pSource,
                                                  final MoneyWisePortfolio pTarget,
                                                  final MoneyWiseTransaction pTrans) {
        /* Determine if this holding was transferred */
        final TethysUnits myUnits = getBaseUnitsForHolding(pSource, pTrans);
        if (myUnits.isNonZero()) {
            /* Access details */
            final MoneyWisePortfolio mySourcePortfolio = pSource.getPortfolio();
            final MoneyWiseSecurity mySecurity = pSource.getSecurity();
            final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(mySourcePortfolio);
            final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
            final MoneyWiseQIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
            TethysMoney myCost = getDeltaCostForHolding(pSource, pTrans);

            /* If there is an associated cost */
            if (myCost != null) {
                /* Convert cost to positive */
                myCost = new TethysMoney(myCost);
                myCost.negate();

                /* Obtain price for the date */
                final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

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
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans the transaction
     */
    protected void processCashTransferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                        final MoneyWisePortfolio pTarget,
                                                        final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pSource);
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final TethysMoney myAmount = pTrans.getAmount();

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
     * @param pPortfolio the portfolio
     * @param pDebit the source account
     * @param pTrans the transaction
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
     * @param pPortfolio the target portfolio
     * @param pSource the source account
     * @param pTrans the transaction
     */
    protected void processCashTransferToPortfolio(final MoneyWisePortfolio pPortfolio,
                                                  final MoneyWiseTransAsset pSource,
                                                  final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFAccountEvents mySource = theFile.registerAccount(pSource);
        TethysMoney myAmount = pTrans.getAmount();

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
        myAmount = new TethysMoney(myAmount);
        myAmount.negate();
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theBuilder.buildXferFromPayee(pPortfolio));
        myXferEvent.recordAccount(myPortfolio.getAccount(), myList);

        /* Add to event list */
        mySource.addEvent(myXferEvent);
    }

    /**
     * Process transfer from a portfolio.
     * @param pPortfolio the portfolio
     * @param pCredit the target account
     * @param pTrans the transaction
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
     * @param pPortfolio the source portfolio
     * @param pTarget the target account
     * @param pTrans the transaction
     */
    protected void processCashTransferFromPortfolio(final MoneyWisePortfolio pPortfolio,
                                                    final MoneyWiseTransAsset pTarget,
                                                    final MoneyWiseTransaction pTrans) {
        /* Access details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final TethysMoney myAmount = pTrans.getAmount();

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
     * @param pCredit the target payee
     * @param pPortfolio the portfolio
     * @param pTrans the transaction
     */
    protected void processExpenseFromPortfolio(final MoneyWisePayee pCredit,
                                               final MoneyWisePortfolio pPortfolio,
                                               final MoneyWiseTransaction pTrans) {
        /* Access Details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pCredit);
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final TethysMoney myAmount = new TethysMoney(pTrans.getAmount());
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
     * @param pDebit the source payee
     * @param pPortfolio the portfolio
     * @param pTrans the transaction
     */
    protected void processIncomeToPortfolio(final MoneyWisePayee pDebit,
                                            final MoneyWisePortfolio pPortfolio,
                                            final MoneyWiseTransaction pTrans) {
        /* Access Details */
        final MoneyWiseQIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pDebit);
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final TethysMoney myAmount = pTrans.getAmount();

        /* Create an income event */
        final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, pTrans, MoneyWiseQActionType.CASH);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }
}
