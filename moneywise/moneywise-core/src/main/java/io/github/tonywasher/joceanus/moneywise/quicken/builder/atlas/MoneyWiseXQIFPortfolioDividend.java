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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPortfolioEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

import java.util.List;

/**
 * Portfolio Dividend Builder class for QIF File.
 */
public class MoneyWiseXQIFPortfolioDividend {
    /**
     * The Helper.
     */
    private final MoneyWiseXQIFHelper theHelper;

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
    MoneyWiseXQIFPortfolioDividend(final MoneyWiseXQIFPortfolioHelper pHelper) {
        theHelper = pHelper.getHelper();
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();
    }

    /**
     * Process stock dividend.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    void processStockDividend(final MoneyWiseSecurityHolding pHolding,
                              final MoneyWiseTransAsset pCredit,
                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Obtain flags */
        boolean canXferLinked = theFileType.canXferPortfolio();
        final boolean isPortfolio = pCredit.equals(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);
        final MoneyWiseXQIFAccountEvents myTarget = theRegister.registerAccount(pCredit);
        OceanusMoney myAmount = pTrans.getAmount();
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        final OceanusMoney myFullAmount = new OceanusMoney(myAmount);
        if (myTaxCredit != null) {
            myFullAmount.addAmount(myTaxCredit);
        }

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Determine whether we should XferLinked */
        boolean doXferLinked = canXferLinked && myTaxCredit == null;

        /* Check for dividend held in portfolio */
        if (isPortfolio) {
            /* Make sure we don't try to link account */
            doXferLinked = false;
            canXferLinked = false;
        }

        /* Create a dividend event */
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, doXferLinked
                ? MoneyWiseQActionType.DIVX
                : MoneyWiseQActionType.DIV);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordAmount(myFullAmount);
        if (doXferLinked) {
            myEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);
        }

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we can use XOut records */
        if (!doXferLinked && canXferLinked) {
            /* Create a transfer out event */
            myAmount = pTrans.getAmount();
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XOUT);
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));
            myEvent.recordXfer(myTarget.getAccount(), myList, myAmount);

            /* Add to event list */
            myQPortfolio.addEvent(myEvent);
        }

        /* Don't do if receiving dividend in portfolio */
        if (!isPortfolio) {
            /* If the receiving account is a portfolio */
            if (pCredit instanceof MoneyWisePortfolio) {
                /* Create the receiving transfer event */
                final MoneyWiseXQIFPortfolioEvent myXferEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.XIN);
                myXferEvent.recordAmount(myAmount);
                myXferEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));
                myXferEvent.recordXfer(myQPortfolio.getAccount(), myList, myAmount);

                /* Add to event list */
                myTarget.addEvent(myXferEvent);

                /* else standard account */
            } else {
                /* Create the receiving transfer event */
                final MoneyWiseXQIFEvent myXferEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
                myXferEvent.recordAmount(myAmount);
                myXferEvent.recordPayee(theHelper.buildXferFromPayee(myPortfolio));
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
            final MoneyWiseXQIFEventCategory myTaxCategory = theHelper.getTaxCategory();
            final MoneyWiseXQIFPayee myTaxPayee = theHelper.getTaxMan();

            /* Create output amount */
            final OceanusMoney myOutAmount = new OceanusMoney(myTaxCredit);
            myOutAmount.negate();

            /* If we are using a holding account */
            if (useHoldingAccount) {
                /* Access Holding Account */
                final MoneyWiseXQIFAccountEvents myHolding = theRegister.registerHoldingAccount(myPortfolio);

                /* Create an event */
                final MoneyWiseXQIFEvent myHoldEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
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
                myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.CASH);
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
    void processReinvestDividend(final MoneyWiseSecurityHolding pHolding,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Determine various flags */
        final boolean canTradeZeroShares = theFileType.canTradeZeroShares();

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);
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
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.REINVDIV);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordAmount(myAmount);
        myEvent.recordQuantity(myUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* Create a ShrsOut event to balance */
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SHRSOUT);
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
            final MoneyWiseXQIFEventCategory myTaxCategory = theHelper.getTaxCategory();
            final MoneyWiseXQIFPayee myTaxPayee = theHelper.getTaxMan();

            /* Create a tax credit event */
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, useMiscIncX
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
                    final MoneyWiseXQIFAccountEvents myHolding = theRegister.registerHoldingAccount(myPortfolio);

                    /* Create an event */
                    final MoneyWiseXQIFEvent myHoldEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
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
                    myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.CASH);
                    myEvent.recordAmount(myOutAmount);
                    myEvent.recordPayee(myTaxPayee);
                    myEvent.recordCategory(myTaxCategory);

                    /* Add to event list */
                    myQPortfolio.addEvent(myEvent);
                }
            }
        }
    }
}
