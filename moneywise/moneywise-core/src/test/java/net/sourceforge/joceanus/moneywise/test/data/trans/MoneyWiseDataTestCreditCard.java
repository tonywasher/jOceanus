/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
     * @param pBuilder the builder
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
    public String getTitle() {
        return "CreditCard Transactions";
    }

    @Override
    public String getDesc() {
        return "CreditCard transactions can be made as follows";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT);
        createLoans(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD);
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple expense */
        theTransBuilder.date("01-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("76.56")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A simple refunded expense */
        theTransBuilder.date("02-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("5.23")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A late payment fine */
        theTransBuilder.date("03-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_CHG_FINES)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("6.89")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_BARCLAYS)
                .build();

        /* A refunded late payment fine */
        theTransBuilder.date("04-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_CHG_FINES)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("0.13")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_BARCLAYS)
                .build();

        /* A payment */
        theTransBuilder.date("05-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("50.00")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD)
                .build();

        /* An interest charge */
        theTransBuilder.date("06-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST_CHG)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("25.67")
                .from().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD)
                .build();

        /* A refunded interest charge */
        theTransBuilder.date("07-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST_CHG)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("1.56")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD)
                .build();

        /* A cashback */
        theTransBuilder.date("08-Jan-1986").category(MoneyWiseDataTestCategories.IDTC_CASH_BACK)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD).amount("10.00")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "9950.00");
        checkAccountValue(MoneyWiseDataTestAccounts.IDLN_BARCLAYCARD, "-142.20");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_BARCLAYS, "10.00", "30.87");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_ASDA, "0", "71.33");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD, "0", "71.33");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_CHG_FINES, "0", "6.76");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST_CHG, "0", "24.11");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_CASH_BACK, "10", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "10.00");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-102.20");
    }
}
