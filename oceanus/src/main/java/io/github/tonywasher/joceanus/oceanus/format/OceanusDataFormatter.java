/*
 * Oceanus: Java Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.oceanus.format;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusLocale;
import io.github.tonywasher.joceanus.oceanus.convert.OceanusDataConverter;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalParser;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Data Formatter.
 */
public class OceanusDataFormatter {
    /**
     * Formatter extension.
     */
    public interface OceanusDataFormatterExtension {
        /**
         * Format an object value.
         *
         * @param pValue the object to format
         * @return the formatted value (or null if not recognised)
         */
        String formatObject(Object pValue);
    }

    /**
     * Invalid class error.
     */
    private static final String ERROR_CLASS = "Invalid Class: ";

    /**
     * Date Formatter.
     */
    private final OceanusDateFormatter theDateFormatter;

    /**
     * Decimal Formatter.
     */
    private final OceanusDecimalFormatter theDecimalFormatter;

    /**
     * Date Formatter.
     */
    private final OceanusDecimalParser theDecimalParser;

    /**
     * Extensions.
     */
    private final List<OceanusDataFormatterExtension> theExtensions;

    /**
     * Constructor.
     */
    public OceanusDataFormatter() {
        this(OceanusLocale.getDefaultLocale());
    }

    /**
     * Constructor.
     *
     * @param pLocale the locale
     */
    public OceanusDataFormatter(final Locale pLocale) {
        theDateFormatter = new OceanusDateFormatter(pLocale);
        theDecimalFormatter = new OceanusDecimalFormatter(pLocale);
        theDecimalParser = new OceanusDecimalParser(pLocale);
        theExtensions = new ArrayList<>();
    }

    /**
     * Obtain the date formatter.
     *
     * @return the formatter
     */
    public OceanusDateFormatter getDateFormatter() {
        return theDateFormatter;
    }

    /**
     * Obtain the decimal formatter.
     *
     * @return the formatter
     */
    public OceanusDecimalFormatter getDecimalFormatter() {
        return theDecimalFormatter;
    }

    /**
     * Obtain the decimal parser.
     *
     * @return the parser
     */
    public OceanusDecimalParser getDecimalParser() {
        return theDecimalParser;
    }

    /**
     * Extend the formatter.
     *
     * @param pExtension the extension
     */
    public void extendFormatter(final OceanusDataFormatterExtension pExtension) {
        theExtensions.add(pExtension);
    }

    /**
     * Set accounting width.
     *
     * @param pWidth the accounting width to use
     */
    public void setAccountingWidth(final int pWidth) {
        /* Set accounting width on decimal formatter */
        theDecimalFormatter.setAccountingWidth(pWidth);
    }

    /**
     * Clear accounting mode.
     */
    public void clearAccounting() {
        /* Clear the accounting mode flag */
        theDecimalFormatter.clearAccounting();
    }

    /**
     * Set the date format.
     *
     * @param pFormat the format string
     */
    public final void setFormat(final String pFormat) {
        /* Tell the formatters about the format */
        theDateFormatter.setFormat(pFormat);
    }

    /**
     * Set the locale.
     *
     * @param pLocale the locale
     */
    public final void setLocale(final Locale pLocale) {
        /* Tell the formatters about the locale */
        theDateFormatter.setLocale(pLocale);
        theDecimalFormatter.setLocale(pLocale);
        theDecimalParser.setLocale(pLocale);
    }

    /**
     * Obtain the locale.
     *
     * @return the locale
     */
    public Locale getLocale() {
        /* Obtain locale from date formatter */
        return theDateFormatter.getLocale();
    }

    /**
     * Format an object value.
     *
     * @param pValue the object to format
     * @return the formatted value
     */
    public String formatObject(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Loop through extensions */
        for (OceanusDataFormatterExtension myExtension : theExtensions) {
            final String myResult = myExtension.formatObject(pValue);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Access the class */
        final Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (pValue instanceof String s) {
            return s;
        }
        if (pValue instanceof Boolean) {
            return Boolean.TRUE.equals(pValue)
                    ? "true"
                    : "false";
        }
        if (pValue instanceof Short
                || pValue instanceof Integer
                || pValue instanceof Long) {
            return pValue.toString();
        }
        if (pValue instanceof Float
                || pValue instanceof Double) {
            return pValue.toString();
        }
        if (pValue instanceof BigInteger
                || pValue instanceof BigDecimal) {
            return pValue.toString();
        }

        /* Handle Enumerated classes */
        if (pValue instanceof Enum) {
            return pValue.toString();
        }

        /* Handle Class */
        if (pValue instanceof Class<?> myClazz) {
            return myClazz.getCanonicalName();
        }

        /* Handle Native array classes */
        if (pValue instanceof byte[] ba) {
            return OceanusDataConverter.bytesToHexString(ba);
        }
        if (pValue instanceof char[] ca) {
            return new String(ca);
        }

        /* Handle date classes */
        if (pValue instanceof Calendar myCal) {
            return theDateFormatter.formatCalendarDay(myCal);
        }
        if (pValue instanceof Date myDate) {
            return theDateFormatter.formatJavaDate(myDate);
        }
        if (pValue instanceof LocalDate myDate) {
            return theDateFormatter.formatLocalDate(myDate);
        }
        if (pValue instanceof OceanusDate myDate) {
            return theDateFormatter.formatDate(myDate);
        }
        if (pValue instanceof OceanusDateRange myRange) {
            return theDateFormatter.formatDateRange(myRange);
        }

        /* Handle decimal classes */
        if (pValue instanceof OceanusDecimal myDecimal) {
            return theDecimalFormatter.formatDecimal(myDecimal);
        }

        /* Handle TethysProfile */
        if (pValue instanceof OceanusProfile myProfile) {
            /* Format the profile */
            return myProfile.getName()
                    + ": "
                    + (myProfile.isRunning()
                    ? myProfile.getStatus()
                    : myProfile.getElapsed());
        }

        /* Handle OceanusExceptions */
        if (pValue instanceof OceanusException) {
            return myClass.getSimpleName();
        }

        /* Standard format option */
        return formatBasicValue(pValue);
    }

    /**
     * Parse object value.
     *
     * @param <T>     the value type
     * @param pSource the source value
     * @param pClazz  the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad Date/Decimal format
     * @throws NumberFormatException    on bad Integer format
     */
    public <T> T parseValue(final String pSource,
                            final Class<T> pClazz) {
        if (Boolean.class.equals(pClazz)) {
            return pClazz.cast(Boolean.parseBoolean(pSource));
        }
        if (Short.class.equals(pClazz)) {
            return pClazz.cast(Short.parseShort(pSource));
        }
        if (Integer.class.equals(pClazz)) {
            return pClazz.cast(Integer.parseInt(pSource));
        }
        if (Long.class.equals(pClazz)) {
            return pClazz.cast(Long.parseLong(pSource));
        }
        if (Float.class.equals(pClazz)) {
            return pClazz.cast(Float.parseFloat(pSource));
        }
        if (Double.class.equals(pClazz)) {
            return pClazz.cast(Double.parseDouble(pSource));
        }
        if (BigInteger.class.equals(pClazz)) {
            return pClazz.cast(new BigInteger(pSource));
        }
        if (BigDecimal.class.equals(pClazz)) {
            return pClazz.cast(new BigDecimal(pSource));
        }
        if (Date.class.equals(pClazz)) {
            /* Parse the date */
            return pClazz.cast(theDateFormatter.parseJavaDate(pSource));
        }
        if (OceanusDate.class.equals(pClazz)) {
            /* Parse the date */
            return pClazz.cast(theDateFormatter.parseDate(pSource));
        }
        if (Calendar.class.equals(pClazz)) {
            /* Parse the date */
            return pClazz.cast(theDateFormatter.parseCalendarDay(pSource));
        }
        if (LocalDate.class.equals(pClazz)) {
            /* Parse the date */
            return pClazz.cast(theDateFormatter.parseLocalDate(pSource));
        }
        if (OceanusPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.parsePriceValue(pSource));
        }
        if (OceanusMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.parseMoneyValue(pSource));
        }
        if (OceanusRate.class.equals(pClazz)) {
            /* Parse the rate */
            return pClazz.cast(theDecimalParser.parseRateValue(pSource));
        }
        if (OceanusUnits.class.equals(pClazz)) {
            /* Parse the units */
            return pClazz.cast(theDecimalParser.parseUnitsValue(pSource));
        }
        if (OceanusRatio.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.parseRatioValue(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Parse object value.
     *
     * @param <T>     the value type
     * @param pSource the source value
     * @param pClazz  the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final Class<T> pClazz) {
        if (OceanusPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.createPriceFromDouble(pSource));
        }
        if (OceanusMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.createMoneyFromDouble(pSource));
        }
        if (OceanusRate.class.equals(pClazz)) {
            /* Parse the rate */
            return pClazz.cast(theDecimalParser.createRateFromDouble(pSource));
        }
        if (OceanusUnits.class.equals(pClazz)) {
            /* Parse the units */
            return pClazz.cast(theDecimalParser.createUnitsFromDouble(pSource));
        }
        if (OceanusRatio.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.createRatioFromDouble(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Parse object value.
     *
     * @param <T>       the value type
     * @param pSource   the source value
     * @param pCurrCode the currency code
     * @param pClazz    the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final String pCurrCode,
                            final Class<T> pClazz) {
        if (OceanusPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.createPriceFromDouble(pSource, pCurrCode));
        }
        if (OceanusMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.createMoneyFromDouble(pSource, pCurrCode));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Format basic object.
     *
     * @param pValue the object
     * @return the formatted value
     */
    private static String formatBasicValue(final Object pValue) {
        /* Access the class */
        final Class<?> myClass = pValue.getClass();

        /* Create basic result */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myClass.getCanonicalName());

        /* Handle list/map instances */
        if (pValue instanceof List<?> myList) {
            formatSize(myBuilder, myList.size());
        } else if (pValue instanceof Map<?, ?> myMap) {
            formatSize(myBuilder, myMap.size());
        }

        /* Return the value */
        return myBuilder.toString();
    }

    /**
     * Format size.
     *
     * @param pBuilder the string builder
     * @param pSize    the size
     */
    private static void formatSize(final StringBuilder pBuilder,
                                   final Object pSize) {
        /* Append the size */
        pBuilder.append('(');
        pBuilder.append(pSize);
        pBuilder.append(')');
    }
}
