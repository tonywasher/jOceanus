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
public class MoneyWiseDataTestMortgage
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    public MoneyWiseDataTestMortgage(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "Mortgage";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.idPY_Barclays);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent);
        createLoans(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage);
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* Create the mortgage */
        theTransBuilder.date("01-Feb-1986").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage).amount("50000.00")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* Payment on mortgage */
        theTransBuilder.date("02-Feb-1986").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("1000.00")
                .to().partner(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage)
                .build();

        /* Interest Payment on Mortgage */
        theTransBuilder.date("03-Feb-1986").category(MoneyWiseDataTestCategories.idTC_MortgageInterest)
                .account(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage).amount("1876.49")
                .from().partner(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage).taxCredit("523.87")
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "59000.00");
        checkAccountValue(MoneyWiseDataTestAccounts.idLN_BarclaysMortgage, "-50876.49");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Barclays, "0", "2400.36");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_HMRC, "523.87", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MortgageInterest, "0", "2400.36");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_TaxRelief, "523.87", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-2400.36");
        checkTaxBasisValue(MoneyWiseTaxClass.VIRTUAL, "523.87");
    }
}
