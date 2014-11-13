/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.List;

import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Portfolio Builder class for QIF File.
 */
public class QIFPortfolioBuilder {
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
     * @param pView the view
     */
    protected QIFPortfolioBuilder(final QIFBuilder pBuilder,
                                  final View pView) {
        /* Store parameters */
        theBuilder = pBuilder;
        theFile = theBuilder.getFile();
        theFileType = theFile.getFileType();
        theData = pView.getData();

        /* Obtain base analysis */
        AnalysisManager myManager = pView.getAnalysisManager();
        theAnalysis = myManager.getAnalysis();
    }

    /**
     * Obtain latest price for a security.
     * @param pSecurity the security
     * @param pDate the date
     * @return the price
     */
    private JPrice getPriceForDate(final Security pSecurity,
                                   final JDateDay pDate) {
        /* Add the price */
        SecurityPriceList myPrices = theData.getSecurityPrices();
        SecurityPrice myPrice = myPrices.getLatestPrice(pSecurity, pDate);
        return myPrice.getPrice();
    }

    /**
     * Obtain resulting units for a security holding event.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     * @param pTrans the transaction
     * @return the units
     */
    private JUnits getUnitsForHoldingEvent(final Portfolio pPortfolio,
                                           final Security pSecurity,
                                           final Transaction pTrans) {
        /* Access the relevant bucket */
        PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        SecurityBucket myBucket = myPortfolios.getBucket(pPortfolio, pSecurity);

        /* Access the base values */
        SecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        return myValues.getUnitsValue(SecurityAttribute.UNITS);
    }

    /**
     * Obtain resulting units for a security holding event.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     * @param pTrans the transaction
     * @return the units
     */
    protected JUnits getBaseUnitsForHolding(final Portfolio pPortfolio,
                                            final Security pSecurity,
                                            final Transaction pTrans) {
        /* Access the relevant bucket */
        PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        SecurityBucket myBucket = myPortfolios.getBucket(pPortfolio, pSecurity);

        /* Access the base values */
        SecurityValues myValues = myBucket.getValuesForTransaction(pTrans);
        JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
        myUnits = new JUnits(myUnits);

        /* Determine the delta in units */
        JUnits myDelta = myBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        myUnits.subtractUnits(myDelta);
        return myUnits;
    }

    /**
     * Obtain base units for a security holding.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     * @param pTrans the transaction
     * @return the delta cost
     */
    private JMoney getDeltaCostForHolding(final Portfolio pPortfolio,
                                          final Security pSecurity,
                                          final Transaction pTrans) {
        /* Access the relevant bucket */
        PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        SecurityBucket myBucket = myPortfolios.getBucket(pPortfolio, pSecurity);

        /* Obtain the cost delta for the transaction */
        return myBucket.getMoneyDeltaForTransaction(pTrans, SecurityAttribute.COST);
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
        Portfolio myPort = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        QIFPayee myQPayee = theFile.registerPayee(pPayee);
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        QIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JUnits myUnits = pTrans.getCreditUnits();
        JPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create output amount */
            JMoney myOutAmount = new JMoney(myAmount);
            myOutAmount.negate();

            /* Create an event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);
            myEvent.recordAmount(new JMoney());
            myEvent.recordPayee(myQPayee);

            /* record the splits */
            myEvent.recordSplitRecord(myQCategory, myList, myAmount, myQPayee.getName());
            myEvent.recordSplitRecord(myPortfolio.getAccount(), myOutAmount, myPort.getName());

            /* Add to event list */
            myHolding.addEvent(myEvent);

            /* else we can do this properly */
        } else {
            /* Create a miscellaneous cash event */
            QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.CASH);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myQPayee);
            myEvent.recordCategory(myQCategory, myList);

            /* Add to event list */
            myPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.BUY);
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
        Portfolio myPort = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Determine style */
        boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        QIFPayee myQPayee = theFile.registerPayee(pPayee);
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        QIFEventCategory myQCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JUnits myUnits = pTrans.getDebitUnits();
        JPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Create a sell shares event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.SELL);
        myEvent.recordAmount(myAmount);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);
        myEvent.recordPrice(myPrice);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create output amount */
        JMoney myOutAmount = new JMoney(myAmount);
        myOutAmount.negate();

        /* If we are using a holding account */
        if (useHoldingAccount) {
            /* Access Holding Account */
            QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPort);

            /* Create an event */
            QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
            myHoldEvent.recordAmount(new JMoney());
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
     * Note that the source cannot be a Security, since that case is handled by {@link #processTransferFromSecurity}
     * @param pHolding the security holding
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processTransferToSecurity(final SecurityHolding pHolding,
                                             final TransactionAsset pDebit,
                                             final Transaction pTrans) {
        /* Access Portfolio Account */
        Portfolio myPort = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myPortfolio = theFile.registerAccount(myPort);

        /* Access Transaction details */
        QIFAccountEvents mySource = theFile.registerAccount(pDebit);
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Determine various flags */
        boolean canTradeZeroShares = theFileType.canTradeZeroShares();
        boolean canXferLinked = theFileType.canXferPortfolio();
        boolean hideBalancingSplitXfer = theFileType.hideBalancingSplitTransfer();

        /* Obtain classes */
        List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JUnits myUnits = pTrans.getCreditUnits();
        JPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = JUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new JUnits();
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
        if (!hideBalancingSplitXfer || !canXferLinked) {
            /* Build output amount */
            JMoney myOutAmount = new JMoney(myAmount);
            myOutAmount.negate();

            /* Build the source transfer */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);
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
            default:
                processSecurityExchange(pSource, pTarget, pTrans);
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
            case TRANSFER:
            case STOCKRIGHTSWAIVED:
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Obtain number of units after this event */
        JUnits myTotalUnits = getUnitsForHoldingEvent(myPortfolio, mySecurity, pTrans);

        /* Access the delta units */
        JUnits myDeltaUnits = pTrans.getCreditUnits();
        if (myDeltaUnits == null) {
            myDeltaUnits = new JUnits(pTrans.getDebitUnits());
            myDeltaUnits.negate();
        }

        /* Obtain number of units before event */
        JUnits myBaseUnits = new JUnits(myTotalUnits);
        myBaseUnits.subtractUnits(myDeltaUnits);

        /* Obtain split ratio */
        JRatio mySplit = new JRatio(myTotalUnits, myBaseUnits);
        mySplit.multiply(JDecimal.RADIX_TEN);

        /* Create a stock split event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, QActionType.STKSPLIT);
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Access the delta units */
        boolean isCredit = true;
        JUnits myUnits = pTrans.getCreditUnits();
        if (myUnits == null) {
            myUnits = pTrans.getDebitUnits();
            isCredit = false;
        }

        /* Create a share movement event */
        QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, pTrans, isCredit
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Obtain flags */
        boolean canXferLinked = theFileType.canXferPortfolio();

        /* Access Transaction details */
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        QIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        JMoney myAmount = pTrans.getAmount();
        JMoney myTaxCredit = pTrans.getTaxCredit();
        JMoney myFullAmount = new JMoney(myAmount);
        if (myTaxCredit != null) {
            myFullAmount.addAmount(myTaxCredit);
        }

        /* Obtain classes */
        List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Determine whether we should XferLinked */
        boolean doXferLinked = canXferLinked && myTaxCredit == null;

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
            myEvent.recordSecurity(myQSecurity);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Create the receiving transfer event */
        QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theBuilder.buildXferFromPayee(myPortfolio));
        myXferEvent.recordAccount(myQPortfolio.getAccount(), myList);

        /* Add to event list */
        myTarget.addEvent(myXferEvent);

        /* If we have a Tax Credit */
        if (myTaxCredit != null) {
            /* Determine flags */
            boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

            /* Access category */
            QIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            QIFPayee myTaxPayee = theBuilder.getTaxMan();

            /* Create output amount */
            JMoney myOutAmount = new JMoney(myTaxCredit);
            myOutAmount.negate();

            /* If we are using a holding account */
            if (useHoldingAccount) {
                /* Access Holding Account */
                QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                /* Create an event */
                QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
                myHoldEvent.recordAmount(new JMoney());
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine various flags */
        boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);
        JMoney myAmount = pTrans.getAmount();
        JUnits myUnits = pTrans.getCreditUnits();
        JMoney myTaxCredit = pTrans.getTaxCredit();
        myAmount = new JMoney(myAmount);
        if (myTaxCredit != null) {
            myAmount.addAmount(myTaxCredit);
        }

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = JUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new JUnits();
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
            boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();
            boolean useMiscIncX = theFileType.useMiscIncX4TaxCredit();

            /* Access category */
            QIFEventCategory myTaxCategory = theBuilder.getTaxCategory();
            QIFPayee myTaxPayee = theBuilder.getTaxMan();

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
                JMoney myOutAmount = new JMoney(myTaxCredit);
                myOutAmount.negate();

                /* If we are using a holding account */
                if (useHoldingAccount) {
                    /* Access Holding Account */
                    QIFAccountEvents myHolding = theFile.registerHoldingAccount(myPortfolio);

                    /* Create an event */
                    QIFEvent myHoldEvent = new QIFEvent(theFile, pTrans);
                    myHoldEvent.recordAmount(new JMoney());
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        Security myCredit = pCredit.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Determine whether we can return capital */
        boolean canReturnCapital = theFileType.canReturnCapital();
        boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        QIFSecurity myDebitSecurity = theFile.registerSecurity(mySecurity);
        QIFSecurity myCreditSecurity = theFile.registerSecurity(myCredit);

        /* Access details */
        JDateDay myDate = pTrans.getDate();
        JUnits myUnits = pTrans.getDebitUnits();
        JPrice myDebitPrice = getPriceForDate(mySecurity, myDate);
        JPrice myCreditPrice = getPriceForDate(myCredit, myDate);

        /* Obtain the delta cost (i.e. value transferred) */
        JMoney myValue = getDeltaCostForHolding(myPortfolio, mySecurity, pTrans);
        myValue = new JMoney(myValue);
        myValue.negate();

        /* Determine whether we use return capital */
        boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = JUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new JUnits();
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
        myEvent.recordQuantity(pTrans.getCreditUnits());
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
        Portfolio myPortfolio = pSource.getPortfolio();
        Security mySource = pSource.getSecurity();
        Security myTarget = pTarget.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        QIFSecurity myDebitSecurity = theFile.registerSecurity(mySource);
        QIFSecurity myCreditSecurity = theFile.registerSecurity(myTarget);

        /* Access details */
        JDateDay myDate = pTrans.getDate();
        JUnits myUnits = pTrans.getCreditUnits();
        JPrice myDebitPrice = getPriceForDate(mySource, myDate);
        JPrice myCreditPrice = getPriceForDate(myTarget, myDate);
        Deposit myThirdParty = pTrans.getThirdParty();
        JMoney myAmount = pTrans.getAmount();

        /* Obtain the number of units that we are selling */
        JUnits myBaseUnits = getBaseUnitsForHolding(myPortfolio, mySource, pTrans);

        /* Obtain the delta cost (i.e. value transferred) */
        JMoney myStockCost = getDeltaCostForHolding(myPortfolio, mySource, pTrans);

        /* Determine the total sale value */
        JMoney mySaleValue = new JMoney(myStockCost);
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
            boolean canXferDirect = theFileType.canXferPortfolio();

            /* Access Target account */
            QIFAccountEvents myQTarget = theFile.registerAccount(myThirdParty);

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
                QIFEvent myXferEvent = new QIFEvent(theFile, pTrans);
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
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        QIFAccountEvents myTarget = theFile.registerAccount(pCredit);
        QIFSecurity myQSecurity = theFile.registerSecurity(mySecurity);

        /* Determine various flags */
        boolean canReturnCapital = theFileType.canReturnCapital();
        boolean canTradeZeroShares = theFileType.canTradeZeroShares();
        boolean canXferLinked = theFileType.canXferPortfolio();
        boolean hideBalancingSplitXfer = theFileType.hideBalancingSplitTransfer();

        /* Obtain classes */
        List<QIFClass> myList = theBuilder.getTransactionClasses(pTrans);

        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JUnits myUnits = pTrans.getDebitUnits();
        JPrice myPrice = getPriceForDate(mySecurity, pTrans.getDate());

        /* Determine whether we use return capital */
        boolean doReturnCapital = canReturnCapital && myUnits == null;

        /* Handle zero units */
        boolean autoCorrectZeroUnits = false;
        if (!canReturnCapital && myUnits == null) {
            if (!canTradeZeroShares) {
                myUnits = JUnits.getWholeUnits(1);
                autoCorrectZeroUnits = true;
            } else {
                myUnits = new JUnits();
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
        if (!hideBalancingSplitXfer || !canXferLinked) {
            /* Build the source transfer */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);
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
        Portfolio myPortfolio = pSource.getPortfolio();
        Security mySource = pSource.getSecurity();
        Security myTarget = pTarget.getSecurity();
        QIFAccountEvents myQPortfolio = theFile.registerAccount(myPortfolio);

        /* Access Transaction details */
        QIFSecurity myQSource = theFile.registerSecurity(mySource);
        QIFSecurity myQTarget = theFile.registerSecurity(myTarget);

        /* Access details */
        JDateDay myDate = pTrans.getDate();
        JMoney myAmount = pTrans.getAmount();
        JUnits mySourceUnits = pTrans.getDebitUnits();
        JUnits myTargetUnits = pTrans.getCreditUnits();
        JPrice mySourcePrice = getPriceForDate(mySource, myDate);
        JPrice myTargetPrice = getPriceForDate(myTarget, myDate);

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
}
