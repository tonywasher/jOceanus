/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.oceanus.decimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Locale constants.
 */
public class OceanusDecimalLocale {
    /**
     * The dollar.
     */
    private static final String DOLLAR = "$";

    /**
     * The pound.
     */
    private static final String POUND = "£";

    /**
     * The locale.
     */
    private final Locale theLocale;

    /**
     * The currencies map.
     */
    private final Map<String, Currency> theCurrencyMap;

    /**
     * The currency symbols map.
     */
    private final Map<String, String> theSymbolMap;

    /**
     * The grouping size.
     */
    private int theGroupingSize;

    /**
     * The grouping separator.
     */
    private final String theGrouping;

    /**
     * The minus sign.
     */
    private final char theMinusSign;

    /**
     * The perCent symbol.
     */
    private final char thePerCent;

    /**
     * The perMille symbol.
     */
    private final char thePerMille;

    /**
     * The decimal separator.
     */
    private final String theDecimal;

    /**
     * The money decimal separator.
     */
    private final String theMoneyDecimal;

    /**
     * The default currency.
     */
    private final Currency theCurrency;

    /**
     * Constructor.
     */
    protected OceanusDecimalLocale() {
        /* Use default locale */
        this(Locale.getDefault());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    protected OceanusDecimalLocale(final Locale pLocale) {
        /* Store the locale */
        theLocale = pLocale;

        /* Create currency maps */
        theCurrencyMap = new HashMap<>();
        theSymbolMap = new HashMap<>();

        /* Access decimal formats */
        final DecimalFormatSymbols mySymbols = DecimalFormatSymbols.getInstance(theLocale);
        final DecimalFormat myFormat = (DecimalFormat) NumberFormat.getInstance(pLocale);
        theGroupingSize = myFormat.getGroupingSize();

        /* Access various interesting formats */
        theMinusSign = mySymbols.getMinusSign();
        thePerCent = mySymbols.getPercent();
        thePerMille = mySymbols.getPerMill();
        theGrouping = Character.toString(mySymbols.getGroupingSeparator());
        theDecimal = Character.toString(mySymbols.getDecimalSeparator());
        theMoneyDecimal = Character.toString(mySymbols.getMonetaryDecimalSeparator());

        /* Access the default currency */
        theCurrency = mySymbols.getCurrency();
        final String myCurrSymbol = theCurrency.getSymbol(theLocale);
        declareSymbol(myCurrSymbol, theCurrency);

        /* Declare simplified USD/GBP if possible */
        if (!DOLLAR.equals(myCurrSymbol)) {
            declareSymbol(DOLLAR, Currency.getInstance(Locale.US));
        }
        if (!POUND.equals(myCurrSymbol)) {
            declareSymbol(POUND, Currency.getInstance(Locale.UK));
        }
    }

    /**
     * Obtain the grouping size.
     * @return the size
     */
    protected int getGroupingSize() {
        return theGroupingSize;
    }

    /**
     * Obtain the grouping string.
     * @return the string
     */
    protected String getGrouping() {
        return theGrouping;
    }

    /**
     * Obtain the minus sign.
     * @return the sign
     */
    protected char getMinusSign() {
        return theMinusSign;
    }

    /**
     * Obtain the perCent sign.
     * @return the sign
     */
    protected char getPerCent() {
        return thePerCent;
    }

    /**
     * Obtain the perMille sign.
     * @return the sign
     */
    protected char getPerMille() {
        return thePerMille;
    }

    /**
     * Obtain the decimal string.
     * @return the string
     */
    protected String getDecimal() {
        return theDecimal;
    }

    /**
     * Obtain the grouping string.
     * @return the string
     */
    protected String getMoneyDecimal() {
        return theMoneyDecimal;
    }

    /**
     * Obtain the default currency.
     * @return the currency
     */
    protected Currency getDefaultCurrency() {
        return theCurrency;
    }

    /**
     * Parse currency symbol.
     * @param pSymbol the symbol
     * @return the currency
     * @throws IllegalArgumentException on invalid currency
     */
    protected Currency parseCurrencySymbol(final String pSymbol) {
        /* Look for the currency in the map */
        Currency myCurrency = theCurrencyMap.get(pSymbol);

        /* If this is a new currency */
        if (myCurrency == null) {
            /* Loop through all the currencies */
            for (Currency myCurr : Currency.getAvailableCurrencies()) {
                /* If the symbol matches */
                if (pSymbol.equals(myCurr.getSymbol(theLocale))) {
                    /* Record currency and break the loop */
                    myCurrency = myCurr;
                    declareSymbol(pSymbol, myCurrency);
                    break;
                }
            }

            /* If we did not find a currency */
            if (myCurrency == null) {
                /* Reject the currency */
                throw new IllegalArgumentException("Invalid currency: "
                                                   + pSymbol);
            }
        }

        /* Return the currency */
        return myCurrency;
    }

    /**
     * Declare symbol.
     * @param pSymbol the symbol
     * @param pCurrency the currency
     */
    private void declareSymbol(final String pSymbol,
                               final Currency pCurrency) {
        /* Store in currency map */
        theCurrencyMap.put(pSymbol, pCurrency);

        /* Store symbol if not already declared */
        final String myCode = pCurrency.getCurrencyCode();
        theSymbolMap.computeIfAbsent(myCode, c -> pSymbol);
    }

    /**
     * Get currency symbol.
     * @param pCurrency the currency
     * @return the symbol
     */
    protected String getSymbol(final Currency pCurrency) {
        /* Look for the currency in the map */
        final String mySymbol = theSymbolMap.get(pCurrency.getCurrencyCode());
        return mySymbol == null
                                ? pCurrency.getSymbol(theLocale)
                                : mySymbol;
    }
}
