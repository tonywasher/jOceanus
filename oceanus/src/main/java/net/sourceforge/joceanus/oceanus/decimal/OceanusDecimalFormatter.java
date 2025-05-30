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

import net.sourceforge.joceanus.oceanus.base.OceanusLocale;

import java.util.Currency;
import java.util.Locale;

/**
 * Presentation methods for decimals in a particular locale.
 * @author Tony Washer
 */
public class OceanusDecimalFormatter {
    /**
     * The Buffer length for building decimal strings.
     */
    protected static final int INITIAL_BUFLEN = 20;

    /**
     * The Blank character.
     */
    public static final char CHAR_BLANK = ' ';

    /**
     * The Zero character.
     */
    public static final char CHAR_ZERO = '0';

    /**
     * The Minus character.
     */
    public static final char CHAR_MINUS = '-';

    /**
     * The Group character.
     */
    public static final char CHAR_GROUP = ',';

    /**
     * The Decimal character.
     */
    public static final String STR_DEC = ".";

    /**
     * The Currency separator.
     */
    protected static final String STR_CURRSEP = ":";

    /**
     * The locale.
     */
    static final OceanusDecimalLocale LOCALE_DEFAULT = new OceanusDecimalLocale();

    /**
     * The locale.
     */
    private OceanusDecimalLocale theLocale;

    /**
     * Do we use accounting format for monetary values?
     */
    private boolean useAccounting;

    /**
     * Width for accounting format.
     */
    private int theAccountingWidth;

    /**
     * Constructor.
     */
    public OceanusDecimalFormatter() {
        /* Use default locale */
        this(OceanusLocale.getDefaultLocale());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public OceanusDecimalFormatter(final Locale pLocale) {
        /* Set the locale */
        setLocale(pLocale);
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public final void setLocale(final Locale pLocale) {
        /* Store the locale */
        theLocale = new OceanusDecimalLocale(pLocale);
    }

    /**
     * Set accounting width.
     * @param pWidth the accounting width to use
     */
    public void setAccountingWidth(final int pWidth) {
        /* Set accounting mode on and set the width */
        useAccounting = true;
        theAccountingWidth = pWidth;
    }

    /**
     * Clear accounting mode.
     */
    public void clearAccounting() {
        /* Clear the accounting mode flag */
        useAccounting = false;
    }

    /**
     * Format a decimal value without reference to locale.
     * @param pValue the value to format
     * @return the formatted value
     */
    protected static String toString(final OceanusDecimal pValue) {
        /* Access the value and scale */
        long myValue = pValue.unscaledValue();
        final int myScale = pValue.scale();

        /* handle negative values */
        final boolean isNegative = myValue < 0;
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        final StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        myString.append(myValue);

        /* Add leading zeroes */
        int myLen = myString.length();
        while (myLen < (myScale + 1)) {
            myString.insert(0, CHAR_ZERO);
            myLen++;
        }

        /* Insert the decimal into correct position if needed */
        if (myScale > 0) {
            myString.insert(myLen
                            - myScale, STR_DEC);
        }

        /* Add minus sign if required */
        if (isNegative) {
            myString.insert(0, CHAR_MINUS);
        }

        /* Return the string */
        return myString.toString();
    }

    /**
     * Format a money value with currency code, into a locale independent format.
     * @param pValue the value to format
     * @return the formatted value
     */
    public String toCurrencyString(final OceanusMoney pValue) {
        /* Format the basic value */
        final StringBuilder myWork = new StringBuilder(toString(pValue));

        /* Add the currency symbol */
        final Currency myCurrency = pValue.getCurrency();
        myWork.insert(0, STR_CURRSEP);
        myWork.insert(0, myCurrency.getCurrencyCode());

        /* Return the string */
        return myWork.toString();
    }

    /**
     * Format a numeric decimal value.
     * @param pValue the value to format
     * @param pScale the scale of the decimal
     * @param pDecSeparator the decimal separator
     * @return the formatted value.
     */
    private StringBuilder formatDecimal(final long pValue,
                                        final int pScale,
                                        final String pDecSeparator) {
        /* Access the value */
        long myValue = pValue;

        /* Reject negative scales */
        if (pScale < 0) {
            throw new IllegalArgumentException("Decimals cannot be negative");
        }

        /* handle negative values */
        final boolean isNegative = myValue < 0;
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        final StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        myString.append(myValue);

        /* Add leading zeroes */
        int myLen = myString.length();
        while (myLen < (pScale + 1)) {
            myString.insert(0, CHAR_ZERO);
            myLen++;
        }

        /* If we have decimals */
        if (pScale > 0) {
            /* Insert decimal point and remove decimals from length */
            myString.insert(myLen
                            - pScale, pDecSeparator);
            myLen -= pScale;
        }

        /* Loop while we need to add grouping */
        final int myGroupingSize = theLocale.getGroupingSize();
        final String myGrouping = theLocale.getGrouping();
        while (myLen > myGroupingSize) {
            /* Insert grouping character and remove grouping size from length */
            myString.insert(myLen
                            - myGroupingSize, myGrouping);
            myLen -= myGroupingSize;
        }

        /* Add minus sign if required */
        if (isNegative) {
            myString.insert(0, theLocale.getMinusSign());
        }

        /* Return the string */
        return myString;
    }

    /**
     * Format a long value.
     * @param pValue the value to format
     * @return the formatted value.
     */
    private StringBuilder formatLong(final long pValue) {
        /* Access the value */
        long myValue = pValue;

        /* handle negative values */
        final boolean isNegative = myValue < 0;
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        final StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        myString.append(myValue);

        /* Loop while we need to add grouping */
        int myLen = myString.length();
        final int myGroupingSize = theLocale.getGroupingSize();
        final String myGrouping = theLocale.getGrouping();
        while (myLen > myGroupingSize) {
            /* Insert grouping character and remove grouping size from length */
            myString.insert(myLen
                            - myGroupingSize, myGrouping);
            myLen -= myGroupingSize;
        }

        /* Add minus sign if required */
        if (isNegative) {
            myString.insert(0, theLocale.getMinusSign());
        }

        /* Return the string */
        return myString;
    }

    /**
     * Format Money value.
     * @param pMoney the value to format
     * @return the formatted value
     */
    public String formatMoney(final OceanusMoney pMoney) {
        /* If we are using accounting and have zero */
        if (useAccounting
            && pMoney.isZero()) {
            /* Format the zero */
            return formatZeroAccounting(pMoney.getCurrency());
        }

        /* Format the basic value */
        final StringBuilder myWork = formatDecimal(pMoney.unscaledValue(), pMoney.scale(), theLocale.getMoneyDecimal());

        /* If we have a leading minus sign */
        final char myMinus = theLocale.getMinusSign();
        final boolean isNegative = myWork.charAt(0) == myMinus;
        if (isNegative) {
            /* Remove the minus sign */
            myWork.deleteCharAt(0);
        }

        /* If we are using accounting mode */
        if (useAccounting) {
            /* Format for accounting */
            formatForAccounting(myWork);
        }

        /* Add the currency symbol */
        final Currency myCurrency = pMoney.getCurrency();
        myWork.insert(0, theLocale.getSymbol(myCurrency));

        /* Re-Add the minus sign */
        if (isNegative) {
            myWork.insert(0, myMinus);
        }

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Price value.
     * @param pPrice the value to format
     * @return the formatted value
     */
    public String formatPrice(final OceanusPrice pPrice) {
        /* return the formatted value */
        return formatMoney(pPrice);
    }

    /**
     * Format Rate value.
     * @param pRate the value to format
     * @return the formatted value
     */
    public String formatRate(final OceanusRate pRate) {
        /* Format the basic value */
        final StringBuilder myWork = formatDecimal(pRate.unscaledValue(), pRate.scale()
                                                                          - OceanusDecimalParser.ADJUST_PERCENT, theLocale.getDecimal());

        /* Append the perCent sign */
        myWork.append(theLocale.getPerCent());

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Rate value.
     * @param pRate the value to format
     * @return the formatted value
     */
    public String formatRatePerMille(final OceanusRate pRate) {
        /* Format the basic value */
        final StringBuilder myWork = formatDecimal(pRate.unscaledValue(), pRate.scale()
                                                                          - OceanusDecimalParser.ADJUST_PERMILLE, theLocale.getDecimal());

        /* Append the perMille sign */
        myWork.append(theLocale.getPerMille());

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Units value.
     * @param pUnits the value to format
     * @return the formatted value
     */
    public String formatUnits(final OceanusUnits pUnits) {
        /* Format the basic value */
        return formatBasicDecimal(pUnits);
    }

    /**
     * Format Ratio value.
     * @param pRatio the value to format
     * @return the formatted value
     */
    public String formatRatio(final OceanusRatio pRatio) {
        /* Format the basic value */
        return formatBasicDecimal(pRatio);
    }

    /**
     * Format Decimal value.
     * @param pDecimal the value to format
     * @return the formatted value
     */
    public String formatDecimal(final OceanusDecimal pDecimal) {
        /* Split out special cases */
        if (pDecimal instanceof OceanusMoney myMoney) {
            return formatMoney(myMoney);
        } else if (pDecimal instanceof OceanusRate myRate) {
            return formatRate(myRate);
        }

        /* return the formatted value */
        return formatBasicDecimal(pDecimal);
    }

    /**
     * Format Decimal value.
     * @param pDecimal the value to format
     * @return the formatted value
     */
    private String formatBasicDecimal(final OceanusDecimal pDecimal) {
        /* Format the basic value */
        final StringBuilder myWork = formatDecimal(pDecimal.unscaledValue(), pDecimal.scale(), theLocale.getDecimal());

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format for accounting.
     * @param pWork the working buffer
     */
    private void formatForAccounting(final StringBuilder pWork) {
        /* If we are short of the width */
        int myLen = pWork.length();
        while (myLen < theAccountingWidth) {
            /* Prefix with blank */
            pWork.insert(0, CHAR_BLANK);
            myLen++;
        }
    }

    /**
     * Format a Zero for accounting.
     * @param pCurrency the currency
     * @return the formatted string
     */
    private String formatZeroAccounting(final Currency pCurrency) {
        /* Determine the scale */
        final int myScale = pCurrency.getDefaultFractionDigits();

        /* Create a buffer build */
        final StringBuilder myWork = new StringBuilder(Character.toString(CHAR_MINUS));

        /* If we have decimals */
        /* Add a blank in place of the decimal digit */
        myWork.append(String.valueOf(CHAR_BLANK).repeat(Math.max(0, myScale)));

        /* If we are short of the width */
        int myLen = myWork.length();
        while (myLen < theAccountingWidth) {
            /* Prefix with blank */
            myWork.insert(0, CHAR_BLANK);
            myLen++;
        }

        /* Add the currency symbol */
        myWork.insert(0, theLocale.getSymbol(pCurrency));

        /* Return the string */
        return myWork.toString();
    }

    /**
     * Format Long value.
     * @param pValue the value to format
     * @return the formatted value
     */
    public String formatLong(final Long pValue) {
        /* Format the basic value */
        final StringBuilder myWork = formatLong(pValue.longValue());

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Integer value.
     * @param pValue the value to format
     * @return the formatted value
     */
    public String formatInteger(final Integer pValue) {
        /* Format the basic value */
        final StringBuilder myWork = formatLong(pValue.longValue());

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Short value.
     * @param pValue the value to format
     * @return the formatted value
     */
    public String formatShort(final Short pValue) {
        /* Format the basic value */
        final StringBuilder myWork = formatLong(pValue.longValue());

        /* return the formatted value */
        return myWork.toString();
    }
}
