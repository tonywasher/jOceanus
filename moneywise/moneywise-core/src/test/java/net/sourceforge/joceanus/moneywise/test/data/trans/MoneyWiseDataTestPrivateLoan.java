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
public class MoneyWiseDataTestPrivateLoan
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     * @param pBuilder the builder
     */
    public MoneyWiseDataTestPrivateLoan(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "PrivateLoan";
    }

    @Override
    public String getTitle() {
        return "PrivateLoan Transactions";
    }

    @Override
    public String getDesc() {
        return "PrivateLoan transactions can be made as follows";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_DAMAGE);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT);
        createLoans(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN);
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* Create the loan */
        theTransBuilder.date("01-Mar-1986").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1000.00")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN)
                .build();

        /* Interest Charge on loan */
        theTransBuilder.date("02-Mar-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN).amount("10.00")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN)
                .build();

        /* Refund of Interest Charge on loan */
        theTransBuilder.date("02-Mar-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN).amount("1.00")
                .from().partner(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN)
                .build();

        /* WriteOff some of loan */
        theTransBuilder.date("03-Mar-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_WRITE_DOWN)
                .account(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN).amount("489")
                .from().partner(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN)
                .build();

        /* Refund of WriteOff some of loan */
        theTransBuilder.date("04-Mar-1986").category(MoneyWiseDataTestCategories.IDTC_LOAN_WRITE_DOWN)
                .account(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN).amount("23")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "9000.00");
        checkAccountValue(MoneyWiseDataTestAccounts.IDLN_DAMAGE_LOAN, "543.00");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_DAMAGE, "9.00", "466.00");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_LOAN_INTEREST, "9.00", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_LOAN_WRITE_DOWN, "0", "466.00");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-466.00");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "9.00");
    }
}
