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
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import org.junit.jupiter.api.Assertions;

/**
 * Test Transfers.
 */
public class MoneyWiseDataTestTransfers
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    public MoneyWiseDataTestTransfers(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "Transfers";
    }

    @Override
    public String getTitle() {
        return "Simple Transfer Transactions";
    }

    @Override
    public String getDesc() {
        return "Simple transfers can be made between any valued account.";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent,
                       MoneyWiseDataTestAccounts.idDP_NatWideFlexDirect,
                       MoneyWiseDataTestAccounts.idDP_StarlingEuro,
                       MoneyWiseDataTestAccounts.idDP_StarlingDollar);
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
        return MoneyWiseTransInfoClass.PARTNERAMOUNT.equals(pInfoClass);
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple transfer from one account to another */
        theTransBuilder.date("01-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("2000")
                .to().partner(MoneyWiseDataTestAccounts.idDP_NatWideFlexDirect)
                .build();

        /* A simple transfer from standard currency to non-standard currency */
        theTransBuilder.date("02-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("2000")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro).partnerAmount("2100")
                .build();

        /* A simple transfer from non-standard currency to standard currency */
        theTransBuilder.date("03-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("1000")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).partnerAmount("950")
                .build();

        /* A simple transfer from non-standard currency to non-standard currency */
        theTransBuilder.date("04-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("500")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingDollar).partnerAmount("550")
                .build();
    }

    @Override
    public void checkErrors() {
        /* Check for failure on a transfer with same account as debit/credit */
        Assertions.assertThrows(MoneyWiseDataException.class,
                () -> theTransBuilder.date("05-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("500")
                        .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).partnerAmount("550")
                        .build(),
                "Failed to reject identical Debit/Credit for transfer");

        /* Check for failure on a transfer from standard currency to non-standard currency with no partnerAmount */
        Assertions.assertThrows(MoneyWiseDataException.class,
                () -> theTransBuilder.date("06-Jun-1985").category(MoneyWiseDataTestCategories.idTC_Transfer)
                        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("2000")
                        .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro)
                        .build(),
                "Failed to reject missing partnerAmount when transferring between accounts with differing currencies");
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "6950");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_NatWideFlexDirect, "12000");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingEuro, "5320");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingDollar, "4717.5");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Market, "607.5", "120");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MktCurrAdjust, "607.5", "120");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "487.5");
    }
}
