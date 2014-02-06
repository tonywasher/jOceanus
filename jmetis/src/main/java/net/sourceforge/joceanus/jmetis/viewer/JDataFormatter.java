/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataDifference;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class JDataFormatter {
    /**
     * Invalid class error.
     */
    private static final String ERROR_CLASS = "Invalid Class: ";

    /**
     * The Decimal formatter.
     */
    private final JDecimalFormatter theDecimalFormatter;

    /**
     * The Decimal parser.
     */
    private final JDecimalParser theDecimalParser;

    /**
     * The Date formatter.
     */
    private final JDateDayFormatter theDateFormatter;

    /**
     * Obtain the DecimalParser.
     * @return the decimal parser
     */
    public JDecimalParser getDecimalParser() {
        return theDecimalParser;
    }

    /**
     * Obtain the DecimalFormatter.
     * @return the decimal formatter
     */
    public JDecimalFormatter getDecimalFormatter() {
        return theDecimalFormatter;
    }

    /**
     * Obtain the Date formatter.
     * @return the date formatter
     */
    public JDateDayFormatter getDateFormatter() {
        return theDateFormatter;
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
     * Constructor.
     */
    public JDataFormatter() {
        this(Locale.getDefault());
    }

    /**
     * Constructor for a locale.
     * @param pLocale the locale
     */
    public JDataFormatter(final Locale pLocale) {
        theDecimalFormatter = new JDecimalFormatter(pLocale);
        theDecimalParser = new JDecimalParser(pLocale);
        theDateFormatter = new JDateDayFormatter(pLocale);
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

        /* Handle ones that we can directly format */
        if (JDataFormat.class.isInstance(pValue)) {
            return ((JDataFormat) pValue).formatObject();
        }

        /* Access the class */
        Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (myClass == String.class) {
            return (String) pValue;
        }
        if (myClass == Boolean.class) {
            return ((Boolean) pValue)
                                     ? "true"
                                     : "false";
        }
        if (myClass == Short.class) {
            return ((Short) pValue).toString();
        }
        if (myClass == Integer.class) {
            return ((Integer) pValue).toString();
        }
        if (myClass == Long.class) {
            return ((Long) pValue).toString();
        }
        if (myClass == Float.class) {
            return ((Float) pValue).toString();
        }
        if (myClass == Double.class) {
            return ((Double) pValue).toString();
        }
        if (myClass == BigInteger.class) {
            return ((BigInteger) pValue).toString();
        }
        if (myClass == BigDecimal.class) {
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
        if (myClass == byte[].class) {
            return DataConverter.bytesToHexString((byte[]) pValue);
        }
        if (myClass == char[].class) {
            return new String((char[]) pValue);
        }

        /* Handle date classes */
        if (myClass == Date.class) {
            return theDateFormatter.formatDate((Date) pValue);
        }
        if (myClass == Calendar.class) {
            return theDateFormatter.formatCalendarDay((Calendar) pValue);
        }
        if (myClass == JDateDay.class) {
            return theDateFormatter.formatDateDay((JDateDay) pValue);
        }
        if (myClass == JDateDayRange.class) {
            return theDateFormatter.formatDateDayRange((JDateDayRange) pValue);
        }

        /* Handle decimal classes */
        if (JDecimal.class.isInstance(pValue)) {
            return theDecimalFormatter.formatDecimal((JDecimal) pValue);
        }

        /* Handle difference class */
        if (JDataDifference.class.isInstance(pValue)) {
            return formatObject(((JDataDifference) pValue).getObject());
        }

        /* Handle JOceanusExceptions */
        if (JOceanusException.class.isInstance(pValue)) {
            return myClass.getSimpleName();
        }

        /* Standard format option */
        return myClass.getCanonicalName();
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     * @throws IllegalArgumentException on bad Date/JDecimal format
     * @throws NumberFormatException on bad Integer format
     */
    public <T> T parseValue(final String pSource,
                            final Class<T> pClass) {
        if (pClass == Boolean.class) {
            return pClass.cast(Boolean.parseBoolean(pSource));
        }
        if (pClass == Short.class) {
            return pClass.cast(Short.parseShort(pSource));
        }
        if (pClass == Integer.class) {
            return pClass.cast(Integer.parseInt(pSource));
        }
        if (pClass == Long.class) {
            return pClass.cast(Long.parseLong(pSource));
        }
        if (pClass == Float.class) {
            return pClass.cast(Float.parseFloat(pSource));
        }
        if (pClass == Double.class) {
            return pClass.cast(Double.parseDouble(pSource));
        }
        if (pClass == BigInteger.class) {
            return pClass.cast(new BigInteger(pSource));
        }
        if (pClass == BigDecimal.class) {
            return pClass.cast(new BigDecimal(pSource));
        }
        if (pClass == Date.class) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseDate(pSource));
        }
        if (pClass == JDateDay.class) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseDateDay(pSource));
        }
        if (pClass == Calendar.class) {
            /* Parse the date */
            return pClass.cast(theDateFormatter.parseCalendarDay(pSource));
        }
        if (pClass == JPrice.class) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.parsePriceValue(pSource));
        }
        if (pClass == JMoney.class) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.parseMoneyValue(pSource));
        }
        if (pClass == JRate.class) {
            /* Parse the rate */
            return pClass.cast(theDecimalParser.parseRateValue(pSource));
        }
        if (pClass == JUnits.class) {
            /* Parse the units */
            return pClass.cast(theDecimalParser.parseUnitsValue(pSource));
        }
        if (pClass == JDilution.class) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.parseDilutionValue(pSource));
        }
        if (pClass == JRatio.class) {
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
     * @throws IllegalArgumentException on bad JDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final Class<T> pClass) {
        if (pClass == JPrice.class) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.createPriceFromDouble(pSource));
        }
        if (pClass == JMoney.class) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.createMoneyFromDouble(pSource));
        }
        if (pClass == JRate.class) {
            /* Parse the rate */
            return pClass.cast(theDecimalParser.createRateFromDouble(pSource));
        }
        if (pClass == JUnits.class) {
            /* Parse the units */
            return pClass.cast(theDecimalParser.createUnitsFromDouble(pSource));
        }
        if (pClass == JDilution.class) {
            /* Parse the dilution */
            return pClass.cast(theDecimalParser.createDilutionFromDouble(pSource));
        }
        if (pClass == JRatio.class) {
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
     * @throws IllegalArgumentException on bad JDecimal format
     */
    public <T> T parseValue(final Double pSource,
                            final String pCurrCode,
                            final Class<T> pClass) {
        if (pClass == JPrice.class) {
            /* Parse the price */
            return pClass.cast(theDecimalParser.createPriceFromDouble(pSource, pCurrCode));
        }
        if (pClass == JMoney.class) {
            /* Parse the money */
            return pClass.cast(theDecimalParser.createMoneyFromDouble(pSource, pCurrCode));
        }
        throw new IllegalArgumentException(ERROR_CLASS + pClass.getSimpleName());
    }
}
