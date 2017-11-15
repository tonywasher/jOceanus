/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-core/src/main/java/net/sourceforge/joceanus/jmoneywise/lethe/data/statics/AssetCurrencyClass.java $
 * $Revision: 829 $
 * $Author: Tony $
 * $Date: 2017-08-13 19:17:14 +0100 (Sun, 13 Aug 2017) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * CurrencyCode checker.
 */
public final class CurrencyCheck {
    /**
     * List of irrelevant currency codes.
     */
    private static final String[] IRRELEVANT =
    { "XBB", "ZWR", "XBA", "YUM", "AZM", "XFO", "COU", "SDD", "MTL", "XBC", "XDR", "ROL", "USS", "AFA", "XXX",
            "MZM", "PTE", "SIT", "ATS", "CYP", "ZWN", "SKK", "ADP", "FIM", "XTS", "FRF", "USN", "ZMK", "CHE",
            "GWP", "VEB", "DEM", "XBD", "UYI", "BYB", "XPT", "EEK", "ZWD", "GHC", "BEF", "XFU", "XAU", "XSU",
            "BGL", "MGF", "IEP", "TPE", "XUA", "CLF", "LUF", "XPD", "SRG", "RUR", "ESP", "TMM", "GRD", "CHW",
            "TRL", "XAG", "BOV", "ITL", "CSD", "MXV", "NLG", "AYM", "ZWL" };

    /**
     * Constructor.
     */
    private CurrencyCheck() {
    }

    /**
     * Main entry point.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* run the test */
        checkCurrency();
    }

    /**
     * Main test.
     */
    private static void checkCurrency() {
        /* Create list of irrelevant codes */
        final List<String> myIrrelevantCurrencies = new ArrayList<>();
        for (String myCode : IRRELEVANT) {
            myIrrelevantCurrencies.add(myCode);
        }

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
        boolean bOK = true;
        if (!myInvalidCurrencies.isEmpty()) {
            System.out.println("The following currencies are invalid\n" + myInvalidCurrencies);
            bOK = false;
        }
        if (!myMissingCurrencies.isEmpty()) {
            System.out.println("The following currencies are missing\n" + myMissingCurrencies);
            bOK = false;
        }

        /* Report success */
        if (bOK) {
            System.out.println("Currencies are OK\n");
        }
    }
}