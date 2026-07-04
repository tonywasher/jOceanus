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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;

import java.util.List;

/**
 * Build Xfer events.
 */
public class MoneyWiseXQIFBuildXfer {
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
     * @param pHelper the helper
     */
    MoneyWiseXQIFBuildXfer(final MoneyWiseXQIFHelper pHelper) {
        theHelper = pHelper;
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();
    }

    /**
     * Process standard transfer.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    void processStandardTransfer(final MoneyWiseTransAsset pDebit,
                                 final MoneyWiseTransAsset pCredit,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        /* Determine credit and debit amounts allowing for differing currencies */
        final boolean isFrom = pTrans.getDirection().isFrom();
        boolean isCurrencyXfer = false;
        OceanusMoney myDebitAmount = pTrans.getAmount();
        OceanusMoney myCreditAmount = pTrans.getPartnerAmount();
        if (myCreditAmount != null) {
            /* If we have a transfer between currencies */
            isCurrencyXfer = true;

            /* Ensure correct credit/debit amounts */
            if (isFrom) {
                myDebitAmount = myCreditAmount;
                myCreditAmount = pTrans.getAmount();
            }

            /* else credit amount is same as debit amount */
        } else {
            myCreditAmount = myDebitAmount;
        }

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myDebitAccount = theRegister.registerAccount(pDebit);
        final MoneyWiseXQIFAccountEvents myCreditAccount = theRegister.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Deteremine whether we hide the debit transfer */
        final boolean hideDebitXfer = isCurrencyXfer && theFileType.hideCurrencyDebitTransfer();

        /* Determine the transactionID */
        final String myTranID = hideDebitXfer
                ? "AMT=" + myDebitAmount
                : "TRN" + pTrans.getIndexedId();

        /* Negate the debit amount */
        myDebitAmount = new OceanusMoney(myDebitAmount);
        myDebitAmount.negate();

        /* Create a new credit event */
        MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordAmount(myCreditAmount);
        myEvent.recordAccount(myDebitAccount.getAccount(), myList);

        /* Build payee description */
        myEvent.recordPayee(theHelper.buildXferFromPayee(pDebit));
        myEvent.recordComment(myTranID);

        /* Add event to event list */
        myCreditAccount.addEvent(myEvent);

        /* If we are not changing currencies or can handle matching transfers */
        if (!isCurrencyXfer || !theFileType.hideCurrencyDebitTransfer()) {
            /* Create a new event */
            myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
            myEvent.recordAmount(myDebitAmount);
            myEvent.recordAccount(myCreditAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferToPayee(pCredit));
            myEvent.recordComment(myTranID);

            /* Add event to event list */
            myDebitAccount.addEvent(myEvent);
        }
    }
}
