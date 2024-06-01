/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Transactions builder.
 */
public class MoneyWiseTestTransactions {
    /**
     * TrasnactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseTestTransactions(final MoneyWiseDataSet pDataSet) {
        /* Create the builders */
        theTransBuilder = new MoneyWiseTransactionBuilder(pDataSet);
        theData = pDataSet;
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    public void createTransfers() throws OceanusException {
        /* A simple transfer from one account to another */
        theTransBuilder.date("01-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("2000").build();

        /* A simple transfer from standard currency to non-standard currency */
        theTransBuilder.date("02-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("2000").partnerAmount("2100").build();

        /* A simple transfer from non-standard currency to standard currency */
        theTransBuilder.date("03-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("1000").partnerAmount("950").build();

        /* A simple transfer from non-standard currency to non-standard currency */
        theTransBuilder.date("04-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idDP_StarlingDollar)
                .amount("500").partnerAmount("550").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple expenses.
     * @throws OceanusException on error
     */
    public void createExpenses() throws OceanusException {
        /* A simple expense */
        theTransBuilder.date("01-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idPY_ASDA)
                .amount("21.95").build();

        /* A simple refunded expense */
        theTransBuilder.date("02-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idPY_ASDA, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("9.99").build();

        /* A simple expense from non-standard currency */
        theTransBuilder.date("03-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idPY_ASDA)
                .amount("31.2").build();

        /* A simple refunded expense from non-standard currency */
        theTransBuilder.date("04-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idPY_ASDA, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("500").partnerAmount("550").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    public void createAutoExpenses() throws OceanusException {
        /* A simple expense to auto-expense */
        theTransBuilder.date("01-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idCS_Cash)
                .amount("21.95").build();

        /* A simple refunded auto-expense */
        theTransBuilder.date("02-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_Cash, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("9.99").build();

        /* A simple auto-expense from non-standard currency */
        theTransBuilder.date("03-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idCS_EurosCash)
                .amount("17.1").build();

        /* A simple refunded auto-expense from non-standard currency */
        theTransBuilder.date("04-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idCS_EurosCash, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("500").partnerAmount("550").build();

        /* A simple transferred expense from auto-expense */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_Cash, MoneyWiseTestAccounts.idPY_CoOp)
                .amount("2.95").build();

        /* A simple transferred expense to auto-expense */
        theTransBuilder.date("65-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idPY_CoOp, MoneyWiseTestAccounts.idCS_Cash)
                .amount("0.95").build();

        /* A simple transferred expense from non-standard currency auto-expense */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_EurosCash, MoneyWiseTestAccounts.idPY_CoOp)
                .amount("2.95").build();

        /* A simple transferred expense to non-standard currency auto-expense */
        theTransBuilder.date("65-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idPY_CoOp, MoneyWiseTestAccounts.idCS_EurosCash)
                .amount("0.95").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple incomes.
     * @throws OceanusException on error
     */
    public void createIncomes() throws OceanusException {
        /* A simple salary income */
        theTransBuilder.date("01-Jun-1988").category(MoneyWiseTestCategories.idTC_Salary)
                .pair(MoneyWiseTestAccounts.idPY_IBM, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("1000").taxCredit("100")
                .employeesNI("60").employersNI("20").benefit("50").build();

        /* A simple interest income */
        theTransBuilder.date("02-Jun-1988").category(MoneyWiseTestCategories.idTC_Interest)
                .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
                .amount("199.99").taxCredit("20").withheld("10").build();

        /* A simple cashback income */
        theTransBuilder.date("03-Jun-1988").category(MoneyWiseTestCategories.idTC_CashBack)
                .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
                .amount("25").build();

        /* A simple dividend income */
        theTransBuilder.date("04-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
                .pair(MoneyWiseTestAccounts.idPY_IBM, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("125").taxCredit("20").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple share buy/sell.
     * @throws OceanusException on error
     */
    public void createShareBuySell() throws OceanusException {
        /* A simple inherited holding */
        theTransBuilder.date("01-Jun-1989").category(MoneyWiseTestCategories.idTC_Inheritance)
                .pair(MoneyWiseTestAccounts.idPY_Parents, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("1000").accountUnits("100").build();

        /* A simple share purchase */
        theTransBuilder.date("02-Jun-1989").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("750").partnerUnits("25").build();

        /* A simple dividend income */
        theTransBuilder.date("03-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
                .amount("25").taxCredit("5").build();

        /* A re-invested dividend income */
        theTransBuilder.date("04-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("125").taxCredit("20").partnerUnits("3").build();

        /* A full transfer out */
        theTransBuilder.date("05-Jun-1988").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("1500").accountUnits("-128").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }
}
