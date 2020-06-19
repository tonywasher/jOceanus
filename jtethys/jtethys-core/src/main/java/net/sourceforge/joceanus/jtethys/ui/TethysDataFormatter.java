/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Tethys Data Formatter.
 */
public class TethysDataFormatter {
    /**
     * Invalid class error.
     */
    private static final String ERROR_CLASS = "Invalid Class: ";

    /**
     * Date Formatter.
     */
    private final TethysDateFormatter theDateFormatter;

    /**
     * Decimal Formatter.
     */
    private final TethysDecimalFormatter theDecimalFormatter;

    /**
     * Date Formatter.
     */
    private final TethysDecimalParser theDecimalParser;

    /**
     * Constructor.
     */
    public TethysDataFormatter() {
        this(Locale.getDefault());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public TethysDataFormatter(final Locale pLocale) {
        theDateFormatter = new TethysDateFormatter(pLocale);
        theDecimalFormatter = new TethysDecimalFormatter(pLocale);
        theDecimalParser = new TethysDecimalParser(pLocale);
    }

    /**
     * Obtain the date formatter.
     * @return the formatter
     */
    public TethysDateFormatter getDateFormatter() {
        return theDateFormatter;
    }

    /**
     * Obtain the decimal formatter.
     * @return the formatter
     */
    public TethysDecimalFormatter getDecimalFormatter() {
        return theDecimalFormatter;
    }

    /**
     * Obtain the decimal parser.
     * @return the parser
     */
    public TethysDecimalParser getDecimalParser() {
        return theDecimalParser;
    }

    /**
     * Set accounting width.
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
     * @param pFormat the format string
     */
    public final void setFormat(final String pFormat) {
        /* Tell the formatters about the format */
        theDateFormatter.setFormat(pFormat);
    }

    /**
     * Set the locale.
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
     * @return the locale
     */
    public Locale getLocale() {
        /* Obtain locale from date formatter */
        return theDateFormatter.getLocale();
    }

    /**
     * Format an object value.
     * @param pValue the object to format
     * @return the formatted value
     */
    public String formatObject(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Access the class */
        final Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (pValue instanceof String) {
            return (String) pValue;
        }
        if (pValue instanceof Boolean) {
            return ((Boolean) pValue)
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
        if (pValue instanceof Class) {
            return ((Class<?>) pValue).getCanonicalName();
        }

        /* Handle Native array classes */
        if (pValue instanceof byte[]) {
            return TethysDataConverter.bytesToHexString((byte[]) pValue);
        }
        if (pValue instanceof char[]) {
            return new String((char[]) pValue);
        }

        /* Handle date classes */
        if (pValue instanceof Calendar) {
            return theDateFormatter.formatCalendarDay((Calendar) pValue);
        }
        if (pValue instanceof Date) {
            return theDateFormatter.formatJavaDate((Date) pValue);
        }
        if (pValue instanceof LocalDate) {
            return theDateFormatter.formatLocalDate((LocalDate) pValue);
        }
        if (pValue instanceof TethysDate) {
            return theDateFormatter.formatDate((TethysDate) pValue);
        }
        if (pValue instanceof TethysDateRange) {
            return theDateFormatter.formatDateRange((TethysDateRange) pValue);
        }

        /* Handle decimal classes */
        if (pValue instanceof TethysDecimal) {
            return theDecimalFormatter.formatDecimal((TethysDecimal) pValue);
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
     * @param <T> the value type
     * @param pSource the source value
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad Date/Decimal format
     * @throws NumberFormatException on bad Integer format
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
        if (TethysDate.class.equals(pClazz)) {
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
        if (TethysPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.parsePriceValue(pSource));
        }
        if (TethysMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.parseMoneyValue(pSource));
        }
        if (TethysRate.class.equals(pClazz)) {
            /* Parse the rate */
            return pClazz.cast(theDecimalParser.parseRateValue(pSource));
        }
        if (TethysUnits.class.equals(pClazz)) {
            /* Parse the units */
            return pClazz.cast(theDecimalParser.parseUnitsValue(pSource));
        }
        if (TethysDilution.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.parseDilutionValue(pSource));
        }
        if (TethysRatio.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.parseRatioValue(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final Class<T> pClazz) {
        if (TethysPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.createPriceFromDouble(pSource));
        }
        if (TethysMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.createMoneyFromDouble(pSource));
        }
        if (TethysRate.class.equals(pClazz)) {
            /* Parse the rate */
            return pClazz.cast(theDecimalParser.createRateFromDouble(pSource));
        }
        if (TethysUnits.class.equals(pClazz)) {
            /* Parse the units */
            return pClazz.cast(theDecimalParser.createUnitsFromDouble(pSource));
        }
        if (TethysDilution.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.createDilutionFromDouble(pSource));
        }
        if (TethysRatio.class.equals(pClazz)) {
            /* Parse the dilution */
            return pClazz.cast(theDecimalParser.createRatioFromDouble(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pCurrCode the currency code
     * @param pClazz the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final String pCurrCode,
                            final Class<T> pClazz) {
        if (TethysPrice.class.equals(pClazz)) {
            /* Parse the price */
            return pClazz.cast(theDecimalParser.createPriceFromDouble(pSource, pCurrCode));
        }
        if (TethysMoney.class.equals(pClazz)) {
            /* Parse the money */
            return pClazz.cast(theDecimalParser.createMoneyFromDouble(pSource, pCurrCode));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClazz.getSimpleName());
    }

    /**
     * Format basic object.
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
        if (pValue instanceof List) {
            formatSize(myBuilder, ((List<?>) pValue).size());
        } else if (pValue instanceof Map) {
            formatSize(myBuilder, ((Map<?, ?>) pValue).size());
        }

        /* Return the value */
        return myBuilder.toString();
    }

    /**
     * Format size.
     * @param pBuilder the string builder
     * @param pSize the size
     */
    private static void formatSize(final StringBuilder pBuilder,
                                   final Object pSize) {
        /* Append the size */
        pBuilder.append('(');
        pBuilder.append(pSize);
        pBuilder.append(')');
    }
}
