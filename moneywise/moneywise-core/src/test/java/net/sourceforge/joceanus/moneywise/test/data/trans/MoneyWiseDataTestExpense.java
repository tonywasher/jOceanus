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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Test Expense.
 */
public class MoneyWiseDataTestExpense
    extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    MoneyWiseDataTestExpense(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    String getName() {
        return "Expenses";
    }

    @Override
    void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.idPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent,
                MoneyWiseDataTestAccounts.idDP_StarlingEuro);
    }

    @Override
    void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "06-Apr-1980", "0.9");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jun-2010", "0.85");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "01-Jun-2010", "0.95");
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple expense */
        theTransBuilder.date("01-Jun-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("21.95")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refunded expense */
        theTransBuilder.date("02-Jun-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("9.99")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple expense in foreign currency */
        theTransBuilder.date("03-Jun-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("31.20")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refunded expense in foreign currency */
        theTransBuilder.date("04-Jun-1986").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("5.12")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();
    }

    @Override
    void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "9988.04");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingEuro, "4725.22");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Market, "248.69", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_ASDA, "14.6", "50.03");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ShopFood, "14.6", "50.03");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MktCurrAdjust, "248.69", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "248.49");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-35.43");
    }
}
