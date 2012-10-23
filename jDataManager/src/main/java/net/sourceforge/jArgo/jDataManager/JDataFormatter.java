/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jArgo.jDataManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.jArgo.jDataManager.JDataObject.JDataDifference;
import net.sourceforge.jArgo.jDataManager.JDataObject.JDataFormat;
import net.sourceforge.jArgo.jDateDay.JDateDay;
import net.sourceforge.jArgo.jDateDay.JDateDayFormatter;
import net.sourceforge.jArgo.jDateDay.JDateDayRange;
import net.sourceforge.jArgo.jDecimal.JDecimal;
import net.sourceforge.jArgo.jDecimal.JDecimalFormatter;
import net.sourceforge.jArgo.jDecimal.JDecimalParser;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class JDataFormatter {
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
            return (((Boolean) pValue)) ? "true" : "false";
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

        /* Standard format option */
        return pValue.getClass().getCanonicalName();
    }
}
