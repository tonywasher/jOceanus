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
package net.sourceforge.joceanus.oceanus.base;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

/**
 * Oceanus Locale Defaults.
 */
public final class OceanusLocale {
    /**
     * The default Locale.
     */
    private static final Locale DEFAULT_LOCALE = determineDefaultLocale();

    /**
     * Private constructor.
     */
    private OceanusLocale() {
    }

    /**
     * Determine default locale.
     * @return the default locale
     */
    private static Locale determineDefaultLocale() {
        /* Obtain the default locale and it's currency */
        final Locale myLocale = Locale.getDefault();
        final Currency myCurrency = DecimalFormatSymbols.getInstance(myLocale).getCurrency();

        /* If the default is a pseudo-currency then default to Locale.UK */
        return myCurrency.getDefaultFractionDigits() < 0
                ? Locale.UK
                : myLocale;
    }

    /**
     * Obtain default locale.
     * @return the default locale
     */
    public static Locale getDefaultLocale() {
        return DEFAULT_LOCALE;
    }
}
