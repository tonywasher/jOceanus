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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;

import java.util.List;

/**
 * Build Cash Events.
 */
public class MoneyWiseXQIFBuildCash {
    /**
     * The Helper.
     */
    private final MoneyWiseXQIFHelper theHelper;

    /**
     * The QIF Register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * Constructor.
     *
     * @param pHelper the helper
     */
    MoneyWiseXQIFBuildCash(final MoneyWiseXQIFHelper pHelper) {
        theHelper = pHelper;
        theRegister = theHelper.getRegister();
    }

    /**
     * Process cash recovery.
     *
     * @param pPayee the payee
     * @param pCash  the cash account
     * @param pTrans the transaction
     */
    void processCashRecovery(final MoneyWisePayee pPayee,
                             final MoneyWiseCash pCash,
                             final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());
        final MoneyWiseXQIFEventCategory myAutoCategory = theRegister.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCash);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Access the amount */
        final OceanusMoney myInAmount = pTrans.getAmount();
        final OceanusMoney myOutAmount = new OceanusMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordAmount(new OceanusMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myCategory, myList, myInAmount, myPayee.getName());
        myEvent.recordSplitRecord(myAutoCategory, myList, myOutAmount, pCash.getAutoPayee().getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash payment.
     *
     * @param pPayee the payee
     * @param pCash  the cash account
     * @param pTrans the transaction
     */
    void processCashPayment(final MoneyWisePayee pPayee,
                            final MoneyWiseCash pCash,
                            final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());
        final MoneyWiseXQIFEventCategory myAutoCategory = theRegister.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCash);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Access the amount */
        final OceanusMoney myInAmount = pTrans.getAmount();
        final OceanusMoney myOutAmount = new OceanusMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordAmount(new OceanusMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myAutoCategory, myList, myInAmount, pCash.getAutoPayee().getName());
        myEvent.recordSplitRecord(myCategory, myList, myOutAmount, myPayee.getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash expense.
     *
     * @param pCash  the cash account
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    void processCashExpense(final MoneyWiseCash pCash,
                            final MoneyWiseTransAsset pDebit,
                            final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pCash.getAutoExpense());

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pDebit);

        /* Access the amount */
        final OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash receipt.
     *
     * @param pCash   the cash account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    void processCashReceipt(final MoneyWiseCash pCash,
                            final MoneyWiseTransAsset pCredit,
                            final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }
}
