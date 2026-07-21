/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisPortfolioBucket;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPortfolioEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

import java.util.Iterator;
import java.util.List;

/**
 * Portfolio Transfer Builder class for QIF File.
 */
public class MoneyWiseXQIFPortfolioXfer {
    /**
     * The Helper.
     */
    private final MoneyWiseXQIFHelper theHelper;

    /**
     * The Portfolio Helper.
     */
    private final MoneyWiseXQIFPortfolioHelper thePortHelper;

    /**
     * The QIF Register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The Analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pHelper   the portfolio helper
     */
    MoneyWiseXQIFPortfolioXfer(final MoneyWiseXAnalysis pAnalysis,
                               final MoneyWiseXQIFPortfolioHelper pHelper) {
        thePortHelper = pHelper;
        theHelper = thePortHelper.getHelper();
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();
        theAnalysis = pAnalysis;
    }

    /**
     * Process standard transfer out from a security.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    void processTransferOut(final MoneyWiseSecurityHolding pHolding,
                            final MoneyWiseTransAsset pCredit,
                            final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pCredit);
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);

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
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

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
        final OceanusPrice myPrice = thePortHelper.getPriceForDate(mySecurity, pTrans.getDate());

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
        MoneyWiseXQIFPortfolioEvent myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, doReturnCapital
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
            myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSIN);
            myPortEvent.recordSecurity(myQSecurity);
            myPortEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myPortEvent);
        }

        /* If we are not hiding the balancing transfer */
        if (!hideBalancingSplitXfer) {
            /* Build the source transfer */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
            myEvent.recordAccount(myQPortfolio.getAccount(), myList);
            myEvent.recordAmount(myAmount);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));

            /* Add event to event list */
            myTarget.addEvent(myEvent);
        }
    }

    /**
     * Process transfer to a security.
     * <p>
     *
     * @param pHolding the security holding
     * @param pDebit   the debit account
     * @param pTrans   the transaction
     */
    protected void processTransferTosSecurity(final MoneyWiseSecurityHolding pHolding,
                                              final MoneyWiseTransAsset pDebit,
                                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPort = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myPortfolio = theRegister.registerAccount(myPort);

        /* Access Transaction details */
        final MoneyWiseXQIFAccountEvents mySource = theRegister.registerAccount(pDebit);
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);

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
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits == null) {
            myUnits = pTrans.getPartnerDeltaUnits();
        }
        final OceanusPrice myPrice = thePortHelper.getPriceForDate(mySecurity, pTrans.getDate());

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
        MoneyWiseXQIFPortfolioEvent myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, canXferLinked
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
            myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSOUT);
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
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
            myEvent.recordAccount(myPortfolio.getAccount(), myList);
            myEvent.recordAmount(myOutAmount);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferToPayee(myPort));

            /* Add event to event list */
            mySource.addEvent(myEvent);
        }
    }

    /**
     * Process PortfolioXfer between portfolios.
     *
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    void processPortfolioXferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                               final MoneyWisePortfolio pTarget,
                                               final MoneyWiseXAnalysisEvent pTrans) {
        /* If there is cash to transfer */
        final OceanusMoney myAmount = thePortHelper.getPortfolioCashValue(pSource, pTrans);
        if (myAmount != null) {
            /* Access details */
            final MoneyWiseXQIFAccountEvents mySource = theRegister.registerAccount(pSource);
            final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pTarget);

            /* Obtain classes */
            final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

            /* Create an XOut event */
            MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theHelper.buildXferToPayee(pTarget));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            mySource.addEvent(myEvent);

            /* Create an XIn event */
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XIN);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theHelper.buildXferFromPayee(pSource));
            myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

            /* Add to event list */
            myTarget.addEvent(myEvent);
        }

        /* Access the relevant bucket */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseXAnalysisPortfolioBucket myBucket = myPortfolios.getBucket(pSource);

        /* Loop through the securities */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = myBucket.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket mySecurity = myIterator.next();

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
    void processPortfolioXferForHolding(final MoneyWiseSecurityHolding pSource,
                                        final MoneyWisePortfolio pTarget,
                                        final MoneyWiseXAnalysisEvent pTrans) {
        /* Determine if this holding was transferred */
        final OceanusUnits myUnits = thePortHelper.getBaseUnitsForHolding(pSource, pTrans);
        if (myUnits.isNonZero()) {
            /* Access details */
            final MoneyWisePortfolio mySourcePortfolio = pSource.getPortfolio();
            final MoneyWiseSecurity mySecurity = pSource.getSecurity();
            final MoneyWiseXQIFAccountEvents mySource = theRegister.registerAccount(mySourcePortfolio);
            final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pTarget);
            final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);
            OceanusMoney myCost = thePortHelper.getDeltaCostForHolding(pSource, pTrans);

            /* If there is an associated cost */
            if (myCost != null) {
                /* Convert cost to positive */
                myCost = new OceanusMoney(myCost);
                myCost.negate();

                /* Obtain price for the date */
                final OceanusPrice myPrice = thePortHelper.getPriceForDate(mySecurity, pTrans.getDate());

                /* Obtain classes */
                final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

                /* Create a sell shares event */
                MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SELL);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XOut event */
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theHelper.buildXferToPayee(pTarget));
                myEvent.recordXfer(myTarget.getAccount(), myList, myCost);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an XIn event */
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XIN);
                myEvent.recordAmount(myCost);
                myEvent.recordPayee(theHelper.buildXferFromPayee(pSource));
                myEvent.recordXfer(mySource.getAccount(), myList, myCost);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* Create a buy shares event */
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.BUY);
                myEvent.recordAmount(myCost);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);
                myEvent.recordPrice(myPrice);

                /* Add to event list */
                myTarget.addEvent(myEvent);

                /* else just simple transfer of shares */
            } else {
                /* Create an SharesOut event */
                MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSOUT);
                myEvent.recordSecurity(myQSecurity);
                myEvent.recordQuantity(myUnits);

                /* Add to event list */
                mySource.addEvent(myEvent);

                /* Create an SharesIn event */
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSIN);
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
    void processCashTransferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                              final MoneyWisePortfolio pTarget,
                                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        final MoneyWiseXQIFAccountEvents mySource = theRegister.registerAccount(pSource);
        final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pTarget);
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Create an XOut event */
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theHelper.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        mySource.addEvent(myEvent);

        /* Create an XIn event */
        myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XIN);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theHelper.buildXferFromPayee(pSource));
        myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

        /* Add to event list */
        myTarget.addEvent(myEvent);
    }

    /**
     * Process Cash Transfer to portfolio.
     *
     * @param pPortfolio the target portfolio
     * @param pSource    the source account
     * @param pTrans     the transaction
     */
    void processCashTransferToPortfolio(final MoneyWisePortfolio pPortfolio,
                                        final MoneyWiseTransAsset pSource,
                                        final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        final MoneyWiseXQIFAccountEvents myPortfolio = theRegister.registerAccount(pPortfolio);
        final MoneyWiseXQIFAccountEvents mySource = theRegister.registerAccount(pSource);
        OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Create an XIn event */
        final MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XIN);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theHelper.buildXferToPayee(pSource));
        myEvent.recordXfer(mySource.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the sending transfer event */
        final MoneyWiseXQIFEvent myXferEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myAmount = new OceanusMoney(myAmount);
        myAmount.negate();
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theHelper.buildXferFromPayee(pPortfolio));
        myXferEvent.recordAccount(myPortfolio.getAccount(), myList);

        /* Add to event list */
        mySource.addEvent(myXferEvent);
    }

    /**
     * Process Cash Transfer from portfolio.
     *
     * @param pPortfolio the source portfolio
     * @param pTarget    the target account
     * @param pTrans     the transaction
     */
    void processCashTransferFromPortfolio(final MoneyWisePortfolio pPortfolio,
                                          final MoneyWiseTransAsset pTarget,
                                          final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        final MoneyWiseXQIFAccountEvents myPortfolio = theRegister.registerAccount(pPortfolio);
        final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pTarget);
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Create an XOut event */
        final MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(theHelper.buildXferToPayee(pTarget));
        myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);

        /* Create the receiving transfer event */
        final MoneyWiseXQIFEvent myXferEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myXferEvent.recordAmount(myAmount);
        myXferEvent.recordPayee(theHelper.buildXferFromPayee(pPortfolio));
        myXferEvent.recordAccount(myPortfolio.getAccount(), myList);

        /* Add to event list */
        myTarget.addEvent(myXferEvent);
    }
}
