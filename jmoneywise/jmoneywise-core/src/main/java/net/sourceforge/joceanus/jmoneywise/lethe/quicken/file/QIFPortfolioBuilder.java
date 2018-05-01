/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Portfolio Builder class for QIF File.
 */
public class QIFPortfolioBuilder {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(QIFPortfolioBuilder.class);

    /**
     * The QIF File.
     */
    private final QIFFile theFile;

    /**
     * The QIF File Type.
     */
    private final QIFType theFileType;

    /**
     * The Builder.
     */
    private final QIFBuilder theBuilder;

    /**
     * The Data.
     */
    private final MoneyWiseData theData;

    /**
     * The Analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Constructor.
     * @param pBuilder the builder
     * @param pData the data
     * @param pAnalysis the analysis
     */
    protected QIFPortfolioBuilder(final QIFBuilder pBuilder,
                                  final MoneyWiseData pData,
                                  final Analysis pAnalysis) {
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
    private TethysPrice getPriceForDate(final Security pSecurity,
                                        final TethysDate pDate) {
        /* Add the price */
        final SecurityPriceDataMap<SecurityPrice> myPriceMap = theData.getSecurityPriceDataMap();
        return myPriceMap.getPriceForDate(pSecurity, pDate);
    }

    /**
     * Obtain resulting units for a security holding event.
     * @param pHolding the security holding
     * @param pTrans the transaction
     * @return the units
     */
    private TethysUnits getUnitsForHoldingEvent(final SecurityHolding pHolding,
                                                final Transaction pTrans) {
        /* Access the relevant bucket */
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final SecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the resulting values */
        final SecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        return myValues.getUnitsValue(SecurityAttribute.UNITS);
    }

    /**
     * Obtain base units for a security holding event.
     * @param pHolding the security holding
     * @param pTrans the transaction
     * @return the units
     */
    protected TethysUnits getBaseUnitsForHolding(final SecurityHolding pHolding,
                                                 final Transaction pTrans) {
        /* Access the relevant bucket */
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final SecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the base values */
        final SecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        if (myValues != null) {
            TethysUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myUnits = new TethysUnits(myUnits);

            /* Determine the delta in units */
            final TethysUnits myDelta = myBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
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
    private TethysMoney getDeltaCostForHolding(final SecurityHolding pHolding,
                                               final Transaction pTrans) {
        /* Access the relevant bucket */
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final SecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Obtain the cost delta for the transaction */
        return myBucket.getMoneyDeltaForTransaction(pTrans, SecurityAttribute.RESIDUALCOST);
    }

    /**
     * Obtain portfolio cash value.
     * @param pPortfolio the portfolio
     * @param pTrans the transaction
     * @return the cash value (or null if none)
     */
    private TethysMoney getPortfolioCashValue(final Portfolio pPortfolio,
                                              final Transaction pTrans) {
        /* Access the relevant bucket */
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final PortfolioCashBucket myBucket = myPortfolios.getCashBucket(pPortfolio);

        /* Obtain the value delta for the transaction */
        TethysMoney myValue = myBucket.getMoneyDeltaForTransaction(pTrans, AccountAttribute.VALUATION);
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
    protected void processIncomeToSecurity(final Payee pPayee,
                                           final SecurityHolding pHolding,
                                           final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPort = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        final QIFPayee myQPayee = theFile.registerPayee(pPayee);
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final QIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final TethysMoney myAmount = pTrans.getAmount();
        final TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            final QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create output amount */
            final TethysMoney myOutAmount = new TethysMoney(myAmount);
            myOutAmount.negate();

            /* Create an event */
            final QIFEvent myEvent = new QIFEvent(theFile, pTrans);
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
            final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myQPayee);
            myEvent.recordCategory(myQCategory, myList);

            /* Add to event list */
            myPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
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
    protected void processExpenseFromSecurity(final Payee pPayee,
                                              final SecurityHolding pHolding,
                                              final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPort = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        final QIFPayee myQPayee = theFile.registerPayee(pPayee);
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final QIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        final TethysMoney myAmount = pTrans.getAmount();
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        myUnits = new TethysUnits(myUnits);
        myUnits.negate();
        final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Create a sell shares event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SELL);
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
            final QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create an event */
            final QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
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
            myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
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
    protected void processTransferToSecurity(final SecurityHolding pHolding,
                                             final TransactionAsset pDebit,
                                             final Transaction pTrans) {
        /* Handle Loyalty bonus separately */
        if (TransactionCategoryClass.LOYALTYBONUS.equals(pTrans.getCategoryClass())) {
            processIncomeToSecurity((Payee) pDebit.getParent(), pHolding, pTrans);
            return;
        }

        /* Access Portfolio Account */
        final Portfolio myPort = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Access Transaction details */
        final QIFAccountEvents mySource = theFile.registerAccount(pDebit);
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

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
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

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
        QIFPortfolioEvent myPortEvent = new QIFPortfolioEvent(theFile, pTrans, canXferLinked
                                                                                             ? QActionType.BUYX
                                                                                             : QActionType.BUY);
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
            myPortEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSOUT);
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
            final QIFEvent myEvent = new QIFEvent(theFile, pTrans);
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
    protected void processTransferBetweenSecurities(final SecurityHolding pSource,
                                                    final SecurityHolding pTarget,
                                                    final Transaction pTrans) {
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
                LOGGER.error("Unsupported TransferBetweenSecurities Category: {}", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process transfer from a security.
     * @param pHolding the security holding
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processTransferFromSecurity(final SecurityHolding pHolding,
                                               final TransactionAsset pCredit,
                                               final Transaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case DIVIDEND:
                processStockDividend(pHolding, pCredit, pTrans);
                break;
            case PORTFOLIOXFER:
                processPortfolioXferForHolding(pHolding, (Portfolio) pCredit, pTrans);
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
    private void processStockSplit(final SecurityHolding pHolding,
                                   final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

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
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.STKSPLIT);
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
    private void processSecurityAdjust(final SecurityHolding pHolding,
                                       final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Access the delta units */
        TethysUnits myUnits = pTrans.getAccountDeltaUnits();
        final boolean isCredit = myUnits.isPositive();
        if (!isCredit) {
            myUnits = new TethysUnits(myUnits);
            myUnits.negate();
        }

        /* Create a share movement event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, isCredit
                                                                                          ? QActionType.SHRSIN
                                                                                          : QActionType.SHRSOUT);
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
    private void processStockDividend(final SecurityHolding pHolding,
                                      final TransactionAsset pCredit,
                                      final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Obtain flags */
        boolean canXferLinked = theFileType.canXferPortfolio();
        final boolean isPortfolio = pCredit.equals(myPortfolio);

        /* Access Transaction details */
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        final QIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        TethysMoney myAmount = pTrans.getAmount();
        final TethysMoney myTaxCredit = pTrans.getTaxCredit();
        final TethysMoney myFullAmount = new TethysMoney(myAmount);
        if (myTaxCredit != null) {
            myFullAmount.addAmount(myTaxCredit);
        }

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Determine whether we should XferLinked */
        boolean doXferLinked = canXferLinked && myTaxCredit == null;

        /* Check for dividend held in portfolio */
        if (isPortfolio) {
            /* Make sure we don't try to link account */
            doXferLinked = false;
            canXferLinked = false;
        }

        /* Create a dividend event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, doXferLinked
                                                                                        ? QActionType.DIVX
                                                                                        : QActionType.DIV);
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
            myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Don't do if receiving dividend in portfolio */
        if (!isPortfolio) {
            /* If the receiving account is a portfolio */
            if (pCredit instanceof Portfolio) {
                /* Create the receiving transfer event */
                final QIFPortfolioEvent myXferEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XIN);
                myXferEvent.recordAmount(myAmount);
                myXferEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
                myXferEvent.recordXfer(myQPortfolio.getAccount(), myList, myAmount);

                /* Add to event list */
                myTarget.addEvent(myXferEvent);

                /* else standard account */
            } else {
                /* Create the receiving transfer event */
                final QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
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
            final QIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            final QIFPayee myTaxPayee = theBuilder.getTaxMan();

            /* Create output amount */
            final TethysMoney myOutAmount = new TethysMoney(myTaxCredit);
            myOutAmount.negate();

            /* If we are using a holding account */
            if (useHoldingAccount) {
                /* Access Holding Account */
                final QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                /* Create an event */
                final QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
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
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
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
    private void processReinvestDividend(final SecurityHolding pHolding,
                                         final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine various flags */
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
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
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.REINVDIV);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordAmount(myAmount);
        myEvent.recordQuantity(myUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsOut event to balance */
            myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSOUT);
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
            final QIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            final QIFPayee myTaxPayee = theBuilder.getTaxMan();

            /* Create a tax credit event */
            myEvent = new QIFPortfolioEvent(theFile, pTrans, useMiscIncX
                                                                         ? QActionType.MISCINCX
                                                                         : QActionType.MISCINC);
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
                    final QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                    /* Create an event */
                    final QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
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
                    myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
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
    private void processStockDeMerger(final SecurityHolding pHolding,
                                      final SecurityHolding pCredit,
                                      final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final Security myCredit = pCredit.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine whether we can return capital */
        final boolean canReturnCapital = theFileType.canReturnCapital();
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final QIFSecurity myDebitSecurity = theFile.registerSecurity(mySecurity);
        final QIFSecurity myCreditSecurity = theFile.registerSecurity(myCredit);

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
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, doReturnCapital
                                                                                           ? QActionType.RTRNCAP
                                                                                           : QActionType.SELL);
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
            myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSIN);
            myEvent.recordSecurity(myDebitSecurity);
            myEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event for the new shares */
        myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
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
    private void processStockTakeOver(final SecurityHolding pSource,
                                      final SecurityHolding pTarget,
                                      final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pSource.getPortfolio();
        final Security mySource = pSource.getSecurity();
        final Security myTarget = pTarget.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final QIFSecurity myDebitSecurity = theFile.registerSecurity(mySource);
        final QIFSecurity myCreditSecurity = theFile.registerSecurity(myTarget);

        /* Access details */
        final TethysDate myDate = pTrans.getDate();
        final TethysUnits myUnits = pTrans.getPartnerDeltaUnits();
        final TethysPrice myDebitPrice = getPriceForDate(mySource, myDate);
        final TethysPrice myCreditPrice = getPriceForDate(myTarget, myDate);
        final Deposit myThirdParty = (Deposit) pTrans.getReturnedCashAccount();
        final TethysMoney myAmount = pTrans.getReturnedCash();

        /* Obtain the number of units that we are selling */
        final TethysUnits myBaseUnits = getBaseUnitsForHolding(pSource, pTrans);

        /* Obtain the delta cost (i.e. value transferred) */
        final TethysMoney myStockCost = getDeltaCostForHolding(pSource, pTrans);

        /* Determine the total sale value */
        final TethysMoney mySaleValue = new TethysMoney(myStockCost);
        mySaleValue.addAmount(myAmount);

        /* Create a sellShares event for the share reduction */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SELL);
        myEvent.recordAmount(mySaleValue);
        myEvent.recordSecurity(myDebitSecurity);
        myEvent.recordPrice(myDebitPrice);
        myEvent.recordQuantity(myBaseUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* Create a buy shares event for the new shares */
        myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
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
            final QIFAccountEvents myQTarget = theFile.registerAccount(myThirdParty);

            /* If we can transfer direct */
            if (canXferDirect) {
                /* Create a transfer out event for the cash payment */
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
                myEvent.recordAmount(myAmount);
                myEvent.recordXfer(myQTarget.getAccount(), myAmount);

                /* Add to event list */
                myQPortfolio.addEvent(myEvent);
            } else {
                /* Build the target transfer */
                final QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
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
    private void processTransferOut(final SecurityHolding pHolding,
                                    final TransactionAsset pCredit,
                                    final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pHolding.getPortfolio();
        final Security mySecurity = pHolding.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final QIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

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
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

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
        QIFPortfolioEvent myPortEvent = new QIFPortfolioEvent(theFile, pTrans, doReturnCapital
                                                                                               ? canXferLinked
                                                                                                               ? QActionType.RTRNCAPX
                                                                                                               : QActionType.RTRNCAP
                                                                                               : canXferLinked
                                                                                                               ? QActionType.SELLX
                                                                                                               : QActionType.SELL);
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
            myPortEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSIN);
            myPortEvent.recordSecurity(myQSecurity);
            myPortEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myPortEvent);
        }

        /* If we are not hiding the balancing transfer */
        if (!hideBalancingSplitXfer) {
            /* Build the source transfer */
            final QIFEvent myEvent = new QIFEvent(theFile, pTrans);
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
    private void processSecurityExchange(final SecurityHolding pSource,
                                         final SecurityHolding pTarget,
                                         final Transaction pTrans) {
        /* Access Portfolio Account */
        final Portfolio myPortfolio = pSource.getPortfolio();
        final Security mySource = pSource.getSecurity();
        final Security myTarget = pTarget.getSecurity();
        final QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        final QIFSecurity myQSource = theFile.registerSecurity(mySource);
        final QIFSecurity myQTarget = theFile.registerSecurity(myTarget);

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
        QIFPortfolioEvent myPortEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SELL);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQSource);
        myPortEvent.recordQuantity(mySourceUnits);
        myPortEvent.recordPrice(mySourcePrice);

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);

        /* Create a buyShares event */
        myPortEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
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
    protected void processTransferBetweenPortfolios(final Portfolio pSource,
                                                    final Portfolio pTarget,
                                                    final Transaction pTrans) {
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
                LOGGER.error("Unsupported TransferBetweenPortfolios Category: {}", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process PortfolioXfer between portfolios.
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans the transaction
     */
    protected void processPortfolioXferBetweenPortfolios(final Portfolio pSource,
                                                         final Portfolio pTarget,
                                                         final Transaction pTrans) {
        /* If there is cash to transfer */
        final TethysMoney myAmount = getPortfolioCashValue(pSource, pTrans);
        if (myAmount != null) {
            /* Access details */
            final QIFAccountEvents mySource = theFile.registerAccount(pSource);
            final QIFAccountEvents myTarget = theFile.registerAccount(pTarget);

            /* Obtain classes */
            final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

            /* Create an XOut event */
            QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            mySource.addEvent(myEvent);

            /* Create an XIn event */
            myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XIN);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferFromPayee(pSource));
            myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

            /* Add to event list */
            myTarget.addEvent(myEvent);
        }

        /* Access the relevant bucket */
        final PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final PortfolioBucket myBucket = myPortfolios.getBucket(pSource);

        /* Loop through the securities */
        final Iterator<SecurityBucket> myIterator = myBucket.securityIterator();
        while (myIterator.hasNext()) {
            final SecurityBucket mySecurity = myIterator.next();

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
    protected void processPortfolioXferForHolding(final SecurityHolding pSource,
                                                  final Portfolio pTarget,
                                                  final Transaction pTrans) {
        /* Determine if this holding was transferred */
        final TethysUnits myUnits = getBaseUnitsForHolding(pSource, pTrans);
        if (myUnits.isNonZero()) {
            /* Access details */
            final Portfolio mySourcePortfolio = pSource.getPortfolio();
            final Security mySecurity = pSource.getSecurity();
            final QIFAccountEvents mySource = theFile.registerAccount(mySourcePortfolio);
            final QIFAccountEvents myTarget = theFile.registerAccount(pTarget);
            final QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
            TethysMoney myCost = getDeltaCostForHolding(pSource, pTrans);

            /* If there is an associated cost */
            if (myCost != null) {
                /* Convert cost to positive */
                myCost = new TethysMoney(myCost);
                myCost.negate();

                /* Obtain price for the date */
                final TethysPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

                /* Obtain classes */
                final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

                /* Create a sell shares event */
                QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SELL);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XOut event */
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
                myEvent.recordXfer(myTarget.getAccount(), myList, myCost);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XIn event */
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XIN);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theBuilder.buildXferFromPayee(pSource));
                myEvent.recordXfer(mySource.getAccount(), myList, myCost);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* Create a buy shares event */
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* else just simple transfer of shares */
            } else {
                /* Create an SharesOut event */
                QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSOUT);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an SharesIn event */
                myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SHRSIN);
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
    protected void processCashTransferBetweenPortfolios(final Portfolio pSource,
                                                        final Portfolio pTarget,
                                                        final Transaction pTrans) {
        /* Access details */
        final QIFAccountEvents mySource = theFile.registerAccount(pSource);
        final QIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final TethysMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XOut event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        mySource.addEvent(myEvent);

        /* Create an XIn event */
        myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XIN);
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
    protected void processTransferToPortfolio(final Portfolio pPortfolio,
                                              final TransactionAsset pDebit,
                                              final Transaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case TRANSFER:
                processCashTransferToPortfolio(pPortfolio, pDebit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferToPortfolio Category: {}", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process Cash Transfer to portfolio.
     * @param pPortfolio the target portfolio
     * @param pSource the source account
     * @param pTrans the transaction
     */
    protected void processCashTransferToPortfolio(final Portfolio pPortfolio,
                                                  final TransactionAsset pSource,
                                                  final Transaction pTrans) {
        /* Access details */
        final QIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final QIFAccountEvents mySource = theFile.registerAccount(pSource);
        TethysMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XIn event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XIN);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pSource));
        myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the sending transfer event */
        final QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
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
    protected void processTransferFromPortfolio(final Portfolio pPortfolio,
                                                final TransactionAsset pCredit,
                                                final Transaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getCategoryClass()) {
            case TRANSFER:
                processCashTransferFromPortfolio(pPortfolio, pCredit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferFromPortfolio Category: {}", pTrans.getCategoryClass());
                break;
        }
    }

    /**
     * Process Cash Transfer from portfolio.
     * @param pPortfolio the source portfolio
     * @param pTarget the target account
     * @param pTrans the transaction
     */
    protected void processCashTransferFromPortfolio(final Portfolio pPortfolio,
                                                    final TransactionAsset pTarget,
                                                    final Transaction pTrans) {
        /* Access details */
        final QIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final QIFAccountEvents myTarget = theFile.registerAccount(pTarget);
        final TethysMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Create an XOut event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theBuilder.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the receiving transfer event */
        final QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
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
    protected void processExpenseFromPortfolio(final Payee pCredit,
                                               final Portfolio pPortfolio,
                                               final Transaction pTrans) {
        /* Access Details */
        final QIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final QIFPayee myPayee = theFile.registerPayee(pCredit);
        final QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final TethysMoney myAmount = new TethysMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create an expense event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
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
    protected void processIncomeToPortfolio(final Payee pDebit,
                                            final Portfolio pPortfolio,
                                            final Transaction pTrans) {
        /* Access Details */
        final QIFAccountEvents myPortfolio = theFile.registerAccount(pPortfolio);
        final QIFPayee myPayee = theFile.registerPayee(pDebit);
        final QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final TethysMoney myAmount = pTrans.getAmount();

        /* Create an income event */
        final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }
}
