/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.test.lethe.data.statics;

import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * CurrencyCode checker.
 */
final class CurrencyTest {
    /**
     * List of irrelevant currency codes.
     */
    private static final String[] IRRELEVANT =
    { "XBB", "ZWR", "XBA", "YUM", "AZM", "XFO", "COU", "SDD", "MTL", "XBC", "XDR", "ROL", "USS", "AFA", "XXX",
            "MZM", "PTE", "SIT", "ATS", "CYP", "ZWN", "SKK", "ADP", "FIM", "XTS", "FRF", "USN", "ZMK", "CHE",
            "GWP", "VEB", "DEM", "XBD", "UYI", "BYB", "XPT", "EEK", "ZWD", "GHC", "BEF", "XFU", "XAU", "XSU",
            "BGL", "MGF", "IEP", "TPE", "XUA", "CLF", "LUF", "XPD", "SRG", "RUR", "ESP", "TMM", "GRD", "CHW",
            "TRL", "XAG", "BOV", "ITL", "CSD", "MXV", "NLG", "AYM", "BYR" };

    /**
     * Create the analysis test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> checkCurrencies() throws OceanusException {
        return Stream.of(
                DynamicTest.dynamicTest("checkCurrency", CurrencyTest::checkCurrency)
        );
    }

    /**
     * Main test.
     */
    private static void checkCurrency() {
        /* Create list of irrelevant codes */
        final List<String> myIrrelevantCurrencies = new ArrayList<>(Arrays.asList(IRRELEVANT));

        /* Create collections */
        final List<AssetCurrencyClass> mySupportedCurrencies = new ArrayList<>();
        final List<AssetCurrencyClass> myInvalidCurrencies = new ArrayList<>();
        final List<Currency> myMissingCurrencies = new ArrayList<>();

        /* Check Available Currencies */
        for (Currency myCurr : Currency.getAvailableCurrencies()) {
            /* Determine whether the currency is supported */
            final String myCode = myCurr.getCurrencyCode();
            try {
                final AssetCurrencyClass myClass = AssetCurrencyClass.valueOf(myCode);
                mySupportedCurrencies.add(myClass);
            } catch (IllegalArgumentException e) {
                if (!myIrrelevantCurrencies.contains(myCode)) {
                    myMissingCurrencies.add(myCurr);
                }
            }
        }

        /* Loop through all the declared currencies */
        for (final AssetCurrencyClass myClass : AssetCurrencyClass.values()) {
            /* Check for invalid currencies */
            if (!mySupportedCurrencies.contains(myClass)) {
                myInvalidCurrencies.add(myClass);
            }
        }

        /* Report failures */
        Assertions.assertTrue(myInvalidCurrencies.isEmpty(), "Invalid currencies:\n" + myInvalidCurrencies);
        Assertions.assertTrue(myMissingCurrencies.isEmpty(), "Missing currencies:\n" + myMissingCurrencies);
    }
}
