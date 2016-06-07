/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
        Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (String.class.equals(myClass)) {
            return (String) pValue;
        }
        if (Boolean.class.equals(myClass)) {
            return ((Boolean) pValue)
                                      ? "true"
                                      : "false";
        }
        if (Short.class.equals(myClass)) {
            return ((Short) pValue).toString();
        }
        if (Integer.class.equals(myClass)) {
            return ((Integer) pValue).toString();
        }
        if (Long.class.equals(myClass)) {
            return ((Long) pValue).toString();
        }
        if (Float.class.equals(myClass)) {
            return ((Float) pValue).toString();
        }
        if (Double.class.equals(myClass)) {
            return ((Double) pValue).toString();
        }
        if (BigInteger.class.equals(myClass)) {
            return ((BigInteger) pValue).toString();
        }
        if (BigDecimal.class.equals(myClass)) {
            return ((BigDecimal) pValue).toString();
        }

        /* Handle Enumerated classes */
        if (Enum.class.isInstance(pValue)) {
            return ((Enum<?>) pValue).toString();
        }

        /* Handle Class */
        if (Class.class.isInstance(pValue)) {
            return ((Class<?>) pValue).getCanonicalName();
        }

        /* Handle Native array classes */
        if (byte[].class.equals(myClass)) {
            return TethysDataConverter.bytesToHexString((byte[]) pValue);
        }
        if (char[].class.equals(myClass)) {
            return new String((char[]) pValue);
        }

        /* Handle date classes */
        if (Date.class.equals(myClass)) {
            return theDateFormatter.formatDate((Date) pValue);
        }
        if (Calendar.class.equals(myClass)) {
            return theDateFormatter.formatCalendarDay((Calendar) pValue);
        }
        if (LocalDate.class.equals(myClass)) {
            return theDateFormatter.formatLocalDate((LocalDate) pValue);
        }
        if (TethysDate.class.equals(myClass)) {
            return theDateFormatter.formatDateDay((TethysDate) pValue);
        }
        if (TethysDateRange.class.equals(myClass)) {
            return theDateFormatter.formatDateDayRange((TethysDateRange) pValue);
        }

        /* Handle decimal classes */
        if (TethysDecimal.class.isInstance(pValue)) {
            return theDecimalFormatter.formatDecimal((TethysDecimal) pValue);
        }

        /* Handle OceanusExceptions */
        if (OceanusException.class.isInstance(pValue)) {
            return myClass.getSimpleName();
        }

        /* Standard format option */
        return formatBasicValue(pValue);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad Date/Decimal format
     * @throws NumberFormatException on bad Integer format
     */
    public <T> T parseValue(final String pSource,
                            final Class<T> pClass) {
        if (Boolean.class.equals(pClass)) {
            return pClass.cast(Boolean.parseBoolean(pSource));
        }
        if (Short.class.equals(pClass)) {
            return pClass.cast(Short.parseShort(pSource));
        }
        if (Integer.class.equals(pClass)) {
            return pClass.cast(Integer.parseInt(pSource));
        }
        if (Long.class.equals(pClass)) {
            return pClass.cast(Long.parseLong(pSource));
        }
        if (Float.class.equals(pClass)) {
            return pClass.cast(Float.parseFloat(pSource));
        }
        if (Double.class.equals(pClass)) {
            return pClass.cast(Double.parseDouble(pSource));
        }
        if (BigInteger.class.equals(pClass)) {
            return pClass.cast(new BigInteger(pSource));
        }
        if (BigDecimal.class.equals(pClass)) {
            return pClass.cast(new BigDecimal(pSource));
        }
        if (Date.class.equals(pClass)) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseDate(pSource));
        }
        if (TethysDate.class.equals(pClass)) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseDateDay(pSource));
        }
        if (Calendar.class.equals(pClass)) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseCalendarDay(pSource));
        }
        if (LocalDate.class.equals(pClass)) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseLocalDate(pSource));
        }
        if (TethysPrice.class.equals(pClass)) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.parsePriceValue(pSource));
        }
        if (TethysMoney.class.equals(pClass)) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.parseMoneyValue(pSource));
        }
        if (TethysRate.class.equals(pClass)) {
            /* Parse the rate */
            return pClass.cast(theDecimalParser.parseRateValue(pSource));
        }
        if (TethysUnits.class.equals(pClass)) {
            /* Parse the units */
            return pClass.cast(theDecimalParser.parseUnitsValue(pSource));
        }
        if (TethysDilution.class.equals(pClass)) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.parseDilutionValue(pSource));
        }
        if (TethysRatio.class.equals(pClass)) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.parseRatioValue(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClass.getSimpleName());
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final Class<T> pClass) {
        if (TethysPrice.class.equals(pClass)) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.createPriceFromDouble(pSource));
        }
        if (TethysMoney.class.equals(pClass)) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.createMoneyFromDouble(pSource));
        }
        if (TethysRate.class.equals(pClass)) {
            /* Parse the rate */
            return pClass.cast(theDecimalParser.createRateFromDouble(pSource));
        }
        if (TethysUnits.class.equals(pClass)) {
            /* Parse the units */
            return pClass.cast(theDecimalParser.createUnitsFromDouble(pSource));
        }
        if (TethysDilution.class.equals(pClass)) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.createDilutionFromDouble(pSource));
        }
        if (TethysRatio.class.equals(pClass)) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.createRatioFromDouble(pSource));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClass.getSimpleName());
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pCurrCode the currency code
     * @param pClass the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad TethysDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final String pCurrCode,
                            final Class<T> pClass) {
        if (TethysPrice.class.equals(pClass)) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.createPriceFromDouble(pSource, pCurrCode));
        }
        if (TethysMoney.class.equals(pClass)) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.createMoneyFromDouble(pSource, pCurrCode));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClass.getSimpleName());
    }

    /**
     * Format basic object.
     * @param pValue the object
     * @return the formatted value
     */
    private static String formatBasicValue(final Object pValue) {
        /* Access the class */
        Class<?> myClass = pValue.getClass();

        /* Create basic result */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myClass.getCanonicalName());

        /* Handle list/map instances */
        if (List.class.isInstance(pValue)) {
            formatSize(myBuilder, ((List<?>) pValue).size());
        } else if (Map.class.isInstance(pValue)) {
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
