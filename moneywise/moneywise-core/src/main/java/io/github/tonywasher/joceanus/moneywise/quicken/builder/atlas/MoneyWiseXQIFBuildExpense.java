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
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;

import java.util.List;

/**
 * Build expense events.
 */
public class MoneyWiseXQIFBuildExpense {
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
     * Constructor.
     *
     * @param pData   the data
     * @param pHelper the helper
     */
    MoneyWiseXQIFBuildExpense(final MoneyWiseDataSet pData,
                              final MoneyWiseXQIFHelper pHelper) {
        theHelper = pHelper;
        theRegister = theHelper.getRegister();

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
     * Process standard expense.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processStandardExpense(final MoneyWisePayee pPayee,
                                          final MoneyWiseTransAsset pDebit,
                                          final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pDebit);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

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
     * Process detailed expense.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the expense
     */
    protected void processDetailedExpense(final MoneyWisePayee pPayee,
                                          final MoneyWiseTransAsset pDebit,
                                          final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the Payee details */
        final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(pPayee);
        final MoneyWiseXQIFPayee myTaxPayee = theRegister.registerPayee(theTaxMan);

        /* Access the Category details */
        final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pDebit);

        /* Obtain classes */
        final List<MoneyWiseXQIFClass> myList = theHelper.getTransactionClasses(pTrans);

        /* Obtain basic amount */
        OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new OceanusMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myTaxCredit);

            /* Access the Category details */
            final MoneyWiseXQIFEventCategory myTaxCategory = theRegister.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        final OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myNatIns);

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
            myAmount.subtractAmount(myWithheld);
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
}
