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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;

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
     * The TaxMan payee.
     */
    private final MoneyWisePayee theTaxMan;

    /**
     * The TaxCredit category.
     */
    private final MoneyWiseTransCategory theTaxCategory;

    /**
     * The Withheld category.
     */
    private final MoneyWiseTransCategory theWithheldCategory;

    /**
     * Constructor.
     *
     * @param pData   the data
     * @param pHelper the helper
     */
    MoneyWiseXQIFBuildXfer(final MoneyWiseDataSet pData,
                           final MoneyWiseXQIFHelper pHelper) {
        theHelper = pHelper;
        theRegister = theHelper.getRegister();
        theFileType = theRegister.getFileType();

        /* Store Tax account */
        final MoneyWisePayeeList myPayees = pData.getPayees();
        theTaxMan = myPayees.getSingularClass(MoneyWisePayeeClass.TAXMAN);

        /* Store categories */
        final MoneyWiseTransCategoryList myCategories = pData.getTransCategories();
        theTaxCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.TAXCREDIT);
        theWithheldCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.WITHHELD);
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

        /* Determine whether we hide the debit transfer */
        final boolean hideDebitXfer = isCurrencyXfer && theFileType.hideCurrencyDebitTransfer();

        /* Determine the transactionID */
        final String myTranID = hideDebitXfer
                ? "AMT=" + myDebitAmount + "/" + myCreditAmount
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

    /**
     * Process payee transfer.
     *
     * @param pTrans the transaction
     */
    protected void processPayeeTransfer(final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        OceanusMoney myAmount = pTrans.getAmount();
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final PrometheusDataItem myParent = myAccount.getParent();
        final MoneyWiseTransAsset myPartner = pTrans.getPartner();
        final boolean isFrom = pTrans.getDirection().isFrom();
        final boolean isRecursive = myAccount.equals(myPartner);

        /* Determine credit and debit amounts allowing for differing currencies */
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

        /* Determine whether we hide the debit transfer */
        final boolean hideDebitXfer = isCurrencyXfer && theFileType.hideCurrencyDebitTransfer();

        /* Determine the transactionID */
        final String myTranID = hideDebitXfer
                ? "AMT=" + myDebitAmount + "/" + myCreditAmount
                : "TRN" + pTrans.getIndexedId();

        /* Negate the debit amount */
        myDebitAmount = new OceanusMoney(myDebitAmount);
        myDebitAmount.negate();

        /* Determine mode */
        final boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();
        final boolean hasXtraDetail = theHelper.hasXtraDetail(pTrans);

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myMainAccount = theRegister.registerAccount(myAccount);

        /* Access the payee */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee((MoneyWisePayee) myParent);

        /* Access the category */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* If this is a simple interest */
        if (isRecursive && !hasXtraDetail) {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
            final OceanusMoney myMainAmount = isFrom ? myDebitAmount : myCreditAmount;

            /* Build simple event and add it */
            myEvent.recordAmount(myMainAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);
            myEvent.recordComment(myTranID);

            /* Add event to event list */
            myMainAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
            OceanusMoney myMainAmount = isFrom ? myCreditAmount : myDebitAmount;
            myMainAmount = new OceanusMoney(myMainAmount);
            myMainAmount.negate();

            /* Record basic details */
            myEvent.recordAmount(isRecursive
                    ? myMainAmount
                    : new OceanusMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            final OceanusMoney myTotalAmount = new OceanusMoney(myMainAmount);
            myEvent.recordSplitRecord(myCategory, myList, myTotalAmount, myPayee.getName());

            /* Handle Tax Credit */
            OceanusMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                /* Access tax payee */
                final MoneyWiseXQIFPayee myTaxPayee = theRegister.registerPayee(theTaxMan);
                if (isFrom) {
                    myTaxCredit = new OceanusMoney(myTaxCredit);
                    myTaxCredit.negate();
                }

                /* Add to amount */
                myTotalAmount.addAmount(myTaxCredit);
                myTaxCredit = new OceanusMoney(myTaxCredit);
                myTaxCredit.negate();

                /* Access the Category details */
                final MoneyWiseXQIFEventCategory myTaxCategory = theRegister.registerCategory(theTaxCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
            }

            /* Handle Withheld */
            OceanusMoney myWithheld = pTrans.getWithheld();
            if (myWithheld != null) {
                /* Add to amount */
                if (isFrom) {
                    myWithheld = new OceanusMoney(myWithheld);
                    myWithheld.negate();
                }
                myTotalAmount.addAmount(myWithheld);
                myWithheld = new OceanusMoney(myWithheld);
                myWithheld.negate();

                /* Access the Category details */
                final MoneyWiseXQIFEventCategory myWithCategory = theRegister.registerCategory(theWithheldCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myWithCategory, myWithheld, myPayee.getName());
            }

            /* Handle Non-Recursion */
            if (!isRecursive) {
                /* Add to amount */
                final OceanusMoney myOutAmount = isFrom ? myCreditAmount : myDebitAmount;

                /* Access the Account details */
                final MoneyWiseXQIFAccountEvents myPartnerAccount = theRegister.registerAccount(myPartner);

                /* Add Split event */
                myEvent.recordSplitRecord(myPartnerAccount.getAccount(), myOutAmount, myTranID);
            }

            /* Add event to event list */
            myMainAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            final MoneyWiseXQIFAccountEvents myPartnerAccount = theRegister.registerAccount(myPartner);

            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Build simple event and add it */
            myAmount = new OceanusMoney(myAmount);
            myAmount.negate();
            myEvent.recordAmount(myAmount);
            myEvent.recordAccount(myMainAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferFromPayee(myAccount));

            /* Add event to event list */
            myPartnerAccount.addEvent(myEvent);
        }
    }
}
