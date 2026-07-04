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

import java.util.List;

/**
 * Build income events.
 */
public class MoneyWiseXQIFBuildIncome {
    /**
     * The Helper.
     */
    private final MoneyWiseXQIFHelper theHelper;

    /**
     * The QIF Register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * The TaxMan payee.
     */
    private final MoneyWisePayee theTaxMan;

    /**
     * The TaxCredit category.
     */
    private final MoneyWiseTransCategory theTaxCategory;

    /**
     * The NatInsurance category.
     */
    private final MoneyWiseTransCategory theNatInsCategory;

    /**
     * The DeemedBenefit category.
     */
    private final MoneyWiseTransCategory theBenefitCategory;

    /**
     * The Withheld category.
     */
    private final MoneyWiseTransCategory theWithheldCategory;

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * Constructor.
     *
     * @param pData   the data
     * @param pHelper the helper
     */
    MoneyWiseXQIFBuildIncome(final MoneyWiseDataSet pData,
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
        theNatInsCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.EMPLOYEENATINS);
        theBenefitCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.DEEMEDBENEFIT);
        theWithheldCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.WITHHELD);
    }

    /**
     * Process standard income.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processStandardIncome(final MoneyWisePayee pPayee,
                                         final MoneyWiseTransAsset pCredit,
                                         final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

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

    /**
     * Process detailed income.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processDetailedIncome(final MoneyWisePayee pPayee,
                                         final MoneyWiseTransAsset pCredit,
                                         final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);
        final MoneyWiseXQIFPayee myTaxPayee = theRegister.registerPayee(theTaxMan);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Obtain basic amount */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new OceanusMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Add to amount */
            myAmount.addAmount(myTaxCredit);
            myTaxCredit = new OceanusMoney(myTaxCredit);
            myTaxCredit.negate();

            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myTaxCategory = theRegister.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null) {
            /* Add to amount */
            myAmount.addAmount(myNatIns);
            myNatIns = new OceanusMoney(myNatIns);
            myNatIns.negate();

            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myInsCategory = theRegister.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        OceanusMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myBenCategory = theRegister.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myBenCategory, myBenefit, myPayee.getName());

            /* Add to amount */
            myBenefit = new OceanusMoney(myBenefit);
            myBenefit.negate();

            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myWithCategory = theRegister.registerCategory(theWithheldCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myWithCategory, myBenefit, myPayee.getName());
        }

        /* Handle Withheld */
        OceanusMoney myWithheld = pTrans.getWithheld();
        if (myWithheld != null) {
            /* Add to amount */
            myAmount.addAmount(myWithheld);
            myWithheld = new OceanusMoney(myWithheld);
            myWithheld.negate();

            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myWithCategory = theRegister.registerCategory(theWithheldCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myWithCategory, myWithheld, myPayee.getName());
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process interest.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processInterest(final MoneyWiseTransAsset pDebit,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Determine the direction */
        final boolean isFrom = pTrans.getDirection().isFrom();
        if (isFrom) {
            myAmount = new OceanusMoney(myAmount);
            myAmount.negate();
        }

        /* Determine mode */
        final boolean isRecursive = pDebit.equals(pCredit);
        final boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();
        final boolean hasXtraDetail = theHelper.hasXtraDetail(pTrans);

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myIntAccount = theRegister.registerAccount(pDebit);

        /* Access the payee */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee((MoneyWisePayee) pDebit.getParent());

        /* Access the category */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* If this is a simple interest */
        if (isRecursive && !hasXtraDetail) {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Record basic details */
            myEvent.recordAmount(isRecursive
                    ? myAmount
                    : new OceanusMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new OceanusMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

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
                myAmount.addAmount(myTaxCredit);
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
                myAmount.addAmount(myWithheld);
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
                final OceanusMoney myOutAmount = new OceanusMoney(pTrans.getAmount());
                myOutAmount.negate();

                /* Access the Account details */
                final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

                /* Add Split event */
                myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);
            }

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(pTrans.getAmount());
            myEvent.recordAccount(myIntAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferFromPayee(pDebit));

            /* Add event to event list */
            myAccount.addEvent(myEvent);
        }
    }

    /**
     * Process cashBack.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processCashBack(final MoneyWiseTransAsset pDebit,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseXAnalysisEvent pTrans) {
        /* Access details */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Determine the direction */
        final boolean isFrom = pTrans.getDirection().isFrom();
        if (isFrom) {
            myAmount = new OceanusMoney(myAmount);
            myAmount.negate();
        }

        /* Determine mode */
        final boolean isRecursive = pDebit.equals(pCredit);
        final boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myBaseAccount = theRegister.registerAccount(pDebit);

        /* Access the payee */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee((MoneyWisePayee) pDebit.getParent());

        /* Access the category */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* If this is a simple cashBack */
        if (isRecursive) {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Record basic details */
            myEvent.recordAmount(new OceanusMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new OceanusMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

            /* Add to amount */
            final OceanusMoney myOutAmount = new OceanusMoney(pTrans.getAmount());
            myOutAmount.negate();

            /* Access the Account details */
            final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

            /* Add Split event */
            myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pCredit);

            /* Create a new event */
            final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(pTrans.getAmount());
            myEvent.recordAccount(myBaseAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(theHelper.buildXferFromPayee(pDebit));

            /* Add event to event list */
            myAccount.addEvent(myEvent);
        }
    }
}
