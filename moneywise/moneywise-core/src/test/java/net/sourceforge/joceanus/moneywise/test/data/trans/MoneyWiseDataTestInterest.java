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
 * Test Interest.
 */
public class MoneyWiseDataTestInterest
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    public MoneyWiseDataTestInterest(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "Interest";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent,
                MoneyWiseDataTestAccounts.idDP_NatWideISA);
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple interest with tax and withheld */
        theTransBuilder.date("01-Sept-1986").category(MoneyWiseDataTestCategories.idTC_Interest)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("13")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).taxCredit("2.81").withheld("0.77")
                .build();

        /* A refund of interest with tax and withheld */
        //theTransBuilder.date("02-Sept-1986").category(MoneyWiseDataTestCategories.idTC_Interest)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("3.22")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).taxCredit("0.76").withheld("0.03")
        //        .build();

        /* A simple loyalty bonus with tax */
        //theTransBuilder.date("03-Sept-1986").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("9.80")
        //        .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).taxCredit("2.30")
        //        .build();

        /* A refund of loyaltyBonus with tax */
        //theTransBuilder.date("04-Sept-1986").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("0.76")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).taxCredit("0.34")
        //        .build();

        /* A simple cashback */
        theTransBuilder.date("05-Sept-1986").category(MoneyWiseDataTestCategories.idTC_CashBack)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("1")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A refund of cashback */
        //theTransBuilder.date("06-Sept-1986").category(MoneyWiseDataTestCategories.idTC_CashBack)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("0.10")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();

        /* A taxFree interest */
        theTransBuilder.date("07-Sept-1986").category(MoneyWiseDataTestCategories.idTC_Interest)
                .account(MoneyWiseDataTestAccounts.idDP_NatWideISA).amount("56")
                .to().partner(MoneyWiseDataTestAccounts.idDP_NatWideISA)
                .build();

        /* A refund of taxFree interest */
        //theTransBuilder.date("08-Sept-1986").category(MoneyWiseDataTestCategories.idTC_Interest)
        //        .account(MoneyWiseDataTestAccounts.idDP_NatWideISA).amount("8.70")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_NatWideISA)
        //        .build();

        /* A taxFree loyaltyBonus */
        //theTransBuilder.date("09-Sept-1986").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_NatWideISA).amount("14.56")
        //        .to().partner(MoneyWiseDataTestAccounts.idDP_NatWideISA)
        //        .build();

        /* A refund of taxFree loyaltyBonus */
        //theTransBuilder.date("10-Sept-1986").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_NatWideISA).amount("7.98")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_NatWideISA)
        //        .build();

        /* A gross interest */
        theTransBuilder.date("01-Sept-2020").category(MoneyWiseDataTestCategories.idTC_Interest)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("7.34")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A refund of gross interest */
        //theTransBuilder.date("02-Sept-2020").category(MoneyWiseDataTestCategories.idTC_Interest)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("2.98")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();

        /* A gross loyalty bonus */
        //theTransBuilder.date("03-Sept-2020").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("8.45")
        //        .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();

        /* A refund of gross loyalty bonus */
        //theTransBuilder.date("04-Sept-2020").category(MoneyWiseDataTestCategories.idTC_LoyaltyBonus)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("1.45")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();
    }

    @Override
    public void checkErrors() {
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "10021.34");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_NatWideISA, "10056");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Barclays, "24.92", "0.77");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Nationwide, "56", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_HMRC, "0", "2.81");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_TaxedInterest, "16.58", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_GrossInterest, "7.34", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_TaxFreeInt, "56", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_CashBack, "1", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_TaxIncome, "0", "2.81");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ExpVirtual, "0", "0.77");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXEDINTEREST, "16.58");
        checkTaxBasisValue(MoneyWiseTaxClass.UNTAXEDINTEREST, "7.34");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXPAID, "-2.81");
        checkTaxBasisValue(MoneyWiseTaxClass.VIRTUAL, "-0.77");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "57");
    }
}
