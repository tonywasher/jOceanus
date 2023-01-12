/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.base;

import java.util.Locale;

import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;

/**
 * Tethys Data Formatter.
 */
public interface TethysUIDataFormatter {
    /**
     * Formatter extension.
     */
    interface TethysUIDataFormatterExtension {
        /**
         * Format an object value.
         * @param pValue the object to format
         * @return the formatted value (or null if not recognised)
         */
        String formatObject(Object pValue);
    }

    /**
     * Obtain the date formatter.
     * @return the formatter
     */
    TethysDateFormatter getDateFormatter();

    /**
     * Obtain the decimal formatter.
     * @return the formatter
     */
    TethysDecimalFormatter getDecimalFormatter();

    /**
     * Obtain the decimal parser.
     * @return the parser
     */
    TethysDecimalParser getDecimalParser();

    /**
     * Extend the formatter.
     * @param pExtension the extension
     */
    void extendFormatter(TethysUIDataFormatterExtension pExtension);

    /**
     * Set accounting width.
     * @param pWidth the accounting width to use
     */
    void setAccountingWidth(int pWidth);

    /**
     * Clear accounting mode.
     */
    void clearAccounting();

    /**
     * Set the date format.
     * @param pFormat the format string
     */
    void setFormat(String pFormat);

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    void setLocale(Locale pLocale);

    /**
     * Obtain the locale.
     * @return the locale
     */
    Locale getLocale();

    /**
     * Format an object value.
     * @param pValue the object to format
     * @return the formatted value
     */
    String formatObject(Object pValue);

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad Date/Decimal format
     * @throws NumberFormatException on bad Integer format
     */
    <T> T parseValue(String pSource,
                     Class<T> pClazz);

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    <T> T parseValue(Double pSource,
                     Class<T> pClazz);

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pCurrCode the currency code
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    <T> T parseValue(Double pSource,
                     String pCurrCode,
                     Class<T> pClazz);
}
