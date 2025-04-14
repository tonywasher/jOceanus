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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
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
     * @param pBuilder the builder
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
    public String getTitle() {
        return "Mortgage Transactions";
    }

    @Override
    public String getDesc() {
        return "Mortgage transactions can be made as follows";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_BARCLAYS);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT);
        createLoans(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE);
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        return MoneyWiseTransInfoClass.TAXCREDIT.equals(pInfoClass);
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* Create the mortgage */
        theTransBuilder.date("01-Feb-1986").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE).amount("50000.00")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* Payment on mortgage */
        theTransBuilder.date("02-Feb-1986").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1000.00")
                .to().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE)
                .build();

        /* Interest Payment on Mortgage */
        theTransBuilder.date("03-Feb-1986").category(MoneyWiseDataTestCategories.IDTC_MORTGAGE_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE).amount("1876.49")
                .from().partner(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE).taxCredit("523.87")
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "59000.00");
        checkAccountValue(MoneyWiseDataTestAccounts.IDLN_BARCLAYS_MORTGAGE, "-50876.49");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_BARCLAYS, "0", "2400.36");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_HMRC, "523.87", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_MORTGAGE_INTEREST, "0", "2400.36");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAX_RELIEF, "523.87", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-2400.36");
        checkTaxBasisValue(MoneyWiseTaxClass.VIRTUAL, "523.87");
    }
}
