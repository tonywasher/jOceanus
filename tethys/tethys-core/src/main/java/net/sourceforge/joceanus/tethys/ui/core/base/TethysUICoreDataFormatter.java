/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateFormatter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalParser;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Tethys Data Formatter.
 */
public class TethysUICoreDataFormatter
        implements TethysUIDataFormatter {
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
     * Constructor.
     */
    public TethysUICoreDataFormatter() {
        this(Locale.getDefault());
    }

    /**
     * Extensions.
     */
    private final List<TethysUIDataFormatterExtension> theExtensions;

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public TethysUICoreDataFormatter(final Locale pLocale) {
        theDateFormatter = new OceanusDateFormatter(pLocale);
        theDecimalFormatter = new OceanusDecimalFormatter(pLocale);
        theDecimalParser = new OceanusDecimalParser(pLocale);
        theExtensions = new ArrayList<>();
    }

    @Override
    public OceanusDateFormatter getDateFormatter() {
        return theDateFormatter;
    }

    @Override
    public OceanusDecimalFormatter getDecimalFormatter() {
        return theDecimalFormatter;
    }

    @Override
    public OceanusDecimalParser getDecimalParser() {
        return theDecimalParser;
    }

    @Override
    public void extendFormatter(final TethysUIDataFormatterExtension pExtension) {
        theExtensions.add(pExtension);
    }

    @Override
    public void setAccountingWidth(final int pWidth) {
        /* Set accounting width on decimal formatter */
        theDecimalFormatter.setAccountingWidth(pWidth);
    }

    @Override
    public void clearAccounting() {
        /* Clear the accounting mode flag */
        theDecimalFormatter.clearAccounting();
    }

    @Override
    public final void setFormat(final String pFormat) {
        /* Tell the formatters about the format */
        theDateFormatter.setFormat(pFormat);
    }

    @Override
    public final void setLocale(final Locale pLocale) {
        /* Tell the formatters about the locale */
        theDateFormatter.setLocale(pLocale);
        theDecimalFormatter.setLocale(pLocale);
        theDecimalParser.setLocale(pLocale);
    }

    @Override
    public Locale getLocale() {
        /* Obtain locale from date formatter */
        return theDateFormatter.getLocale();
    }

    @Override
    public String formatObject(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Loop through extensions */
        for (TethysUIDataFormatterExtension myExtension : theExtensions) {
            final String myResult = myExtension.formatObject(pValue);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Access the class */
        final Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (pValue instanceof String) {
            return (String) pValue;
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
        if (pValue instanceof Class) {
            return ((Class<?>) pValue).getCanonicalName();
        }

        /* Handle Native array classes */
        if (pValue instanceof byte[]) {
            return OceanusDataConverter.bytesToHexString((byte[]) pValue);
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
        if (pValue instanceof OceanusDate) {
            return theDateFormatter.formatDate((OceanusDate) pValue);
        }
        if (pValue instanceof OceanusDateRange) {
            return theDateFormatter.formatDateRange((OceanusDateRange) pValue);
        }

        /* Handle decimal classes */
        if (pValue instanceof OceanusDecimal) {
            return theDecimalFormatter.formatDecimal((OceanusDecimal) pValue);
        }


        /* Handle TethysProfile */
        if (pValue instanceof OceanusProfile) {
            /* Format the profile */
            final OceanusProfile myProfile = (OceanusProfile) pValue;
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

    @Override
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

    @Override
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

    @Override
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
