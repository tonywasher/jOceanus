/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data.trans;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;

/**
 * Test Salary.
 */
public class MoneyWiseDataTestPayeeIncome
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    public MoneyWiseDataTestPayeeIncome(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "PayeeIncome";
    }

    @Override
    public String getTitle() {
        return "Payee Income Transactions";
    }

    @Override
    public String getDesc() {
        return "Income from Payees can be made as follows";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_IBM,
                MoneyWiseDataTestAccounts.IDPY_PARENTS);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT);
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        switch (pInfoClass) {
            case TAXCREDIT:
            case DEEMEDBENEFIT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple Inheritance */
        theTransBuilder.date("01-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("500")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();

        /* A refund of inheritance */
        theTransBuilder.date("02-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("60")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();

        /* A simple salary payment */
        theTransBuilder.date("03-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_SALARY)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_IBM).taxCredit("20").benefit("10")
                .build();

        /* A refund of salary */
        theTransBuilder.date("04-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_SALARY)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("100")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_IBM).taxCredit("0.9").benefit("0.5")
                .build();

        /* A simple untaxed payment */
        theTransBuilder.date("05-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_SOCIAL_SECURITY)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("100")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_GOVERNMENT)
                .build();

        /* A refund of untaxed payment */
        theTransBuilder.date("06-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_SOCIAL_SECURITY)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("10")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_GOVERNMENT)
                .build();

        /* A simple gift */
        theTransBuilder.date("07-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_INC_GIFTS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("50")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();

        /* A refund of gift */
        theTransBuilder.date("08-Aug-1986").category(MoneyWiseDataTestCategories.IDTC_INC_GIFTS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("5")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "11475");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_IBM, "928.6", "9.5");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_PARENTS, "485", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_GOVERNMENT, "90", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_HMRC, "0", "19.1");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_INHERITANCE, "440", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_SALARY, "919.1", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_SOCIAL_SECURITY, "90", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_INC_GIFTS, "45", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_BENEFIT, "9.5", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAX_INCOME, "0", "19.1");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_EXP_VIRTUAL, "0", "9.5");
        checkTaxBasisValue(MoneyWiseTaxClass.SALARY, "1018.6");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXPAID, "-19.1");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "485");
        checkTaxBasisValue(MoneyWiseTaxClass.VIRTUAL, "-9.5");
    }
}
