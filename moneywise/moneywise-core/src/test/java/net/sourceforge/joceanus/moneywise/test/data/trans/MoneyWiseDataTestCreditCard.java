/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.test.data.trans;

import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Test Expense.
 */
public class MoneyWiseDataTestCreditCard
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    public MoneyWiseDataTestCreditCard(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "CreditCard";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.idPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent);
        createLoans(MoneyWiseDataTestAccounts.idLN_Barclaycard);
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple expense */
        theTransBuilder.date("01-Jan-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idLN_Barclaycard).amount("76.56")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refunded expense */
        theTransBuilder.date("02-Jan-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idLN_Barclaycard).amount("5.23")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A late payment fine */
        theTransBuilder.date("03-Jan-1986").category(MoneyWiseDataTestCategories.idTC_ChgFines)
                .account(MoneyWiseDataTestAccounts.idLN_Barclaycard).amount("6.89")
                .to().partner(MoneyWiseDataTestAccounts.idPY_Barclays)
                .build();

        /* A payment */
        theTransBuilder.date("04-Jan-1986").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("50.00")
                .to().partner(MoneyWiseDataTestAccounts.idLN_Barclaycard)
                .build();

        /* An interest charge */
        theTransBuilder.date("05-Jan-1986").category(MoneyWiseDataTestCategories.idTC_MortgageInterest)
                .account(MoneyWiseDataTestAccounts.idLN_Barclaycard).amount("25.67")
                .from().partner(MoneyWiseDataTestAccounts.idLN_Barclaycard)
                .build();

        /* A cashback */
        theTransBuilder.date("06-Jan-1986").category(MoneyWiseDataTestCategories.idTC_CashBack)
                .account(MoneyWiseDataTestAccounts.idLN_Barclaycard).amount("10.00")
                .to().partner(MoneyWiseDataTestAccounts.idLN_Barclaycard)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "9950.00");
        checkAccountValue(MoneyWiseDataTestAccounts.idLN_Barclaycard, "-143.89");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Barclays, "10.00", "32.56");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_ASDA, "0", "71.33");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ShopFood, "0", "71.33");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ChgFines, "0", "6.89");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MortgageInterest, "0", "25.67");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_CashBack, "10", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "10.00");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-103.89");
    }
}
