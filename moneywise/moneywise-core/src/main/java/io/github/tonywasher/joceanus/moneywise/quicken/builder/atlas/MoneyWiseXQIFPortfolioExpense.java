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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
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
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

import java.util.List;

/**
 * Portfolio Expense Builder class for QIF File.
 */
public class MoneyWiseXQIFPortfolioExpense {
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
    MoneyWiseXQIFPortfolioExpense(final MoneyWiseXQIFPortfolioHelper pHelper) {
        thePortHelper = pHelper;
        theHelper = thePortHelper.getHelper();
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();
    }

    /**
     * Process expense from a security.
     *
     * @param pPayee   the payee
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    public void processExpenseFromSecurity(final MoneyWisePayee pPayee,
                                           final MoneyWiseSecurityHolding pHolding,
                                           final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPort = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myPortfolio = theRegister.registerAccount(myPort);

        /* Determine style */
        final boolean useHoldingAccount = theFileType.useInvestmentHolding4Category();

        /* Access Transaction details */
        final MoneyWiseXQIFPayee myQPayee = theRegister.registerPayee(pPayee);
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);
        final MoneyWiseXQIFEventCategory myQCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        myUnits = new OceanusUnits(myUnits);
        myUnits.negate();
        final OceanusPrice myPrice = thePortHelper.getPriceForDate(mySecurity, pTrans.getDate());

        /* Create a sell shares event */
        MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.SELL);
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
            final MoneyWiseXQIFAccountEvents myHolding = theRegister.registerHoldingAccount(myPort);

            /* Create an event */
            final MoneyWiseXQIFEvent myHoldEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
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
            myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.CASH);
            myEvent.recordAmount(myOutAmount);
            myEvent.recordPayee(myQPayee);
            myEvent.recordCategory(myQCategory, myList);

            /* Add to event list */
            myPortfolio.addEvent(myEvent);
        }
    }

    /**
     * Process expense from a portfolio.
     *
     * @param pCredit    the target payee
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     */
    void processExpenseFromPortfolio(final MoneyWisePayee pCredit,
                                     final MoneyWisePortfolio pPortfolio,
                                     final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Details */
        final MoneyWiseXQIFAccountEvents myPortfolio = theRegister.registerAccount(pPortfolio);
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pCredit);
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());
        final OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create an expense event */
        final MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.CASH);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add to event list */
        myPortfolio.addEvent(myEvent);
    }
}
