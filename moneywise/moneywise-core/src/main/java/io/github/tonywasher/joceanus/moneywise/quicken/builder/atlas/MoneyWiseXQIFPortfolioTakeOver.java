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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPortfolioEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Portfolio TakeOver Builder class for QIF File.
 */
public class MoneyWiseXQIFPortfolioTakeOver {
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
     * Constructor.
     *
     * @param pHelper the portfolio helper
     */
    MoneyWiseXQIFPortfolioTakeOver(final MoneyWiseXQIFPortfolioHelper pHelper) {
        thePortHelper = pHelper;
        theHelper = thePortHelper.getHelper();
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();
    }

    /**
     * Process exchange between securities.
     *
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans  the transaction
     */
    void processSecurityExchange(final MoneyWiseSecurityHolding pSource,
                                 final MoneyWiseSecurityHolding pTarget,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurity mySource = pSource.getSecurity();
        final MoneyWiseSecurity myTarget = pTarget.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myQSource = theRegister.registerSecurity(mySource);
        final MoneyWiseXQIFSecurity myQTarget = theRegister.registerSecurity(myTarget);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits mySourceUnits = pTrans.getAccountDeltaUnits();
        mySourceUnits = new OceanusUnits(mySourceUnits);
        mySourceUnits.negate();
        final OceanusUnits myTargetUnits = pTrans.getPartnerDeltaUnits();
        final OceanusPrice mySourcePrice = thePortHelper.getPriceForDate(mySource, myDate);
        final OceanusPrice myTargetPrice = thePortHelper.getPriceForDate(myTarget, myDate);

        /* Create a sellShares/returnCapital event */
        MoneyWiseXQIFPortfolioEvent myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SELL);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQSource);
        myPortEvent.recordQuantity(mySourceUnits);
        myPortEvent.recordPrice(mySourcePrice);

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);

        /* Create a buyShares event */
        myPortEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.BUY);
        myPortEvent.recordAmount(myAmount);
        myPortEvent.recordSecurity(myQTarget);
        myPortEvent.recordQuantity(myTargetUnits);
        myPortEvent.recordPrice(myTargetPrice);

        /* Add to event list */
        myQPortfolio.addEvent(myPortEvent);
    }

    /**
     * Process stock deMerger.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    void processStockDeMerger(final MoneyWiseSecurityHolding pHolding,
                              final MoneyWiseSecurityHolding pCredit,
                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Determine whether we can return capital */
        final boolean canReturnCapital = theFileType.canReturnCapital();
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myDebitSecurity = theRegister.registerSecurity(mySecurity);
        final MoneyWiseXQIFSecurity myCreditSecurity = theRegister.registerSecurity(myCredit);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        if (myUnits != null) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }
        final OceanusPrice myDebitPrice = thePortHelper.getPriceForDate(mySecurity, myDate);
        final OceanusPrice myCreditPrice = thePortHelper.getPriceForDate(myCredit, myDate);

        /* Obtain the delta cost (i.e. value transferred) */
        OceanusMoney myValue = thePortHelper.getDeltaCostForHolding(pHolding, pTrans);
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
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, doReturnCapital
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
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSIN);
            myEvent.recordSecurity(myDebitSecurity);
            myEvent.recordQuantity(myUnits);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Create a buy shares event for the new shares */
        myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.BUY);
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
    void processStockTakeOver(final MoneyWiseSecurityHolding pSource,
                              final MoneyWiseSecurityHolding pTarget,
                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();
        final MoneyWiseSecurity mySource = pSource.getSecurity();
        final MoneyWiseSecurity myTarget = pTarget.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myDebitSecurity = theRegister.registerSecurity(mySource);
        final MoneyWiseXQIFSecurity myCreditSecurity = theRegister.registerSecurity(myTarget);

        /* Access details */
        final OceanusDate myDate = pTrans.getDate();
        final OceanusUnits myUnits = pTrans.getPartnerDeltaUnits();
        final OceanusPrice myDebitPrice = thePortHelper.getPriceForDate(mySource, myDate);
        final OceanusPrice myCreditPrice = thePortHelper.getPriceForDate(myTarget, myDate);
        final MoneyWiseDeposit myThirdParty = (MoneyWiseDeposit) pTrans.getReturnedCashAccount();
        final OceanusMoney myAmount = pTrans.getReturnedCash();

        /* Obtain the number of units that we are selling */
        final OceanusUnits myBaseUnits = thePortHelper.getBaseUnitsForHolding(pSource, pTrans);

        /* Obtain the delta cost (i.e. value transferred) */
        final OceanusMoney myStockCost = thePortHelper.getDeltaCostForHolding(pSource, pTrans);

        /* Determine the total sale value */
        final OceanusMoney mySaleValue = new OceanusMoney(myStockCost);
        mySaleValue.addAmount(myAmount);

        /* Create a sellShares event for the share reduction */
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SELL);
        myEvent.recordAmount(mySaleValue);
        myEvent.recordSecurity(myDebitSecurity);
        myEvent.recordPrice(myDebitPrice);
        myEvent.recordQuantity(myBaseUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* Create a buy shares event for the new shares */
        myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.BUY);
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
            final MoneyWiseXQIFAccountEvents myQTarget = theRegister.registerAccount(myThirdParty);

            /* If we can transfer direct */
            if (canXferDirect) {
                /* Create a transfer out event for the cash payment */
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
                myEvent.recordAmount(myAmount);
                myEvent.recordXfer(myQTarget.getAccount(), myAmount);

                /* Add to event list */
                myQPortfolio.addEvent(myEvent);
            } else {
                /* Build the target transfer */
                final MoneyWiseXQIFEvent myXferEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
                myXferEvent.recordAccount(myQPortfolio.getAccount());
                myXferEvent.recordAmount(myAmount);

                /* Build payee description */
                myEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));

                /* Add event to event list */
                myQTarget.addEvent(myEvent);
            }
        }
    }
}
