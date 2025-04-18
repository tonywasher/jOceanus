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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Test Interest.
 */
public class MoneyWiseDataTestDepositIncome
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     * @param pBuilder the builder
     */
    public MoneyWiseDataTestDepositIncome(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "DepositIncome";
    }

    @Override
    public String getTitle() {
        return "Deposit Income Transactions";
    }

    @Override
    public String getDesc() {
        return "Income from Deposits transactions can be made as follows";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT,
                MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA,
                MoneyWiseDataTestAccounts.IDDP_STARLING_EURO);
    }

    @Override
    public void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "06-Apr-1980", "0.9");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jan-2025", "0.85");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "01-Jan-2025", "0.95");
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        switch (pInfoClass) {
            case TAXCREDIT:
            case WITHHELD:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple interest with tax and withheld */
        theTransBuilder.date("01-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("13")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("2.81").withheld("0.77")
                .build();

        /* A refund of interest with tax and withheld */
        theTransBuilder.date("02-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("3.22")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("0.76").withheld("0.03")
                .build();

        /* A simple loyalty bonus with tax */
        theTransBuilder.date("03-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("9.80")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("2.30")
                .build();

        /* A refund of loyaltyBonus with tax */
        theTransBuilder.date("04-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("0.76")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("0.34")
                .build();

        /* A simple cashback */
        theTransBuilder.date("05-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_CASH_BACK)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A refund of cashback */
        theTransBuilder.date("06-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_CASH_BACK)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("0.10")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A taxFree interest */
        theTransBuilder.date("07-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA).amount("56")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA)
                .build();

        /* A refund of taxFree interest */
        theTransBuilder.date("08-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA).amount("8.70")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA)
                .build();

        /* A taxFree loyaltyBonus */
        theTransBuilder.date("09-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA).amount("14.56")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA)
                .build();

        /* A refund of taxFree loyaltyBonus */
        theTransBuilder.date("10-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA).amount("7.98")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA)
                .build();

        /* A simple foreign interest with tax and withheld */
        theTransBuilder.date("11-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("47")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).taxCredit("11.76").withheld("0.93")
                .build();

        /* A refund of foreign interest with tax and withheld */
        theTransBuilder.date("12-Sept-1986").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("5.21")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).taxCredit("1.65").withheld("0.12")
                .build();

        /* A gross interest */
        theTransBuilder.date("01-Sept-2020").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("7.34")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A refund of gross interest */
        theTransBuilder.date("02-Sept-2020").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("2.98")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A gross loyalty bonus */
        theTransBuilder.date("03-Sept-2020").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("8.45")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A refund of gross loyalty bonus */
        theTransBuilder.date("04-Sept-2020").category(MoneyWiseDataTestCategories.IDTC_LOYALTY_BONUS)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1.45")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A foreign interest */
        theTransBuilder.date("01-Oct-2020").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("8.62")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO)
                .build();

        /* A refund of foreign gross interest */
        theTransBuilder.date("02-Oct-2020").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("1.31")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO)
                .build();
    }

    @Override
    public void checkErrors() {
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "10031.08");
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_NAT_WIDE_ISA, "10053.88");
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO, "4796.65");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_BARCLAYS, "35.83", "0.74");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_NATIONWIDE, "53.88", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_STARLING, "54.01", "0.73");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_HMRC, "0", "13.10");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_MARKET, "252.46", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAXED_INTEREST, "60.00", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_GROSS_INTEREST, "10.94", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAX_FREE_INT, "47.3", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_CASH_BACK, "0.90", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAX_INCOME, "0", "13.10");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_EXP_VIRTUAL, "0", "1.47");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAXED_LOYALTY_BONUS, "11.00", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_GROSS_LOYALTY_BONUS, "7.00", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_TAX_FREE_LOYALTY_BONUS, "6.58", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_MKT_CURR_ADJUST, "252.46", "0");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXEDINTEREST, "71.00");
        checkTaxBasisValue(MoneyWiseTaxClass.UNTAXEDINTEREST, "17.94");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXPAID, "-13.10");
        checkTaxBasisValue(MoneyWiseTaxClass.VIRTUAL, "-1.47");
        checkTaxBasisValue(MoneyWiseTaxClass.TAXFREE, "54.78");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "252.46");
    }
}
