/*******************************************************************************
 * jDecimal: Decimals represented by long values
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDecimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Presentation methods for decimals in a particular locale.
 * @author Tony Washer
 */
public class JDecimalFormatter {
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
    private Locale theLocale;

    /**
     * The grouping size.
     */
    private int theGroupingSize;

    /**
     * The grouping separator.
     */
    private String theGrouping;

    /**
     * The minus sign.
     */
    private char theMinusSign;

    /**
     * The perCent symbol.
     */
    private char thePerCent;

    /**
     * The perMille symbol.
     */
    private char thePerMille;

    /**
     * The decimal separator.
     */
    private String theDecimal;

    /**
     * The money decimal separator.
     */
    private String theMoneyDecimal;

    /**
     * Do we use accounting format for monetary values?
     */
    private boolean useAccounting = false;

    /**
     * Width for accounting format.
     */
    private int theAccountingWidth = 0;

    /**
     * Constructor.
     */
    public JDecimalFormatter() {
        /* Use default locale */
        this(Locale.getDefault());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public JDecimalFormatter(final Locale pLocale) {
        /* Set the locale */
        setLocale(pLocale);
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public final void setLocale(final Locale pLocale) {
        /* Store the locale */
        theLocale = pLocale;

        /* Access decimal formats */
        DecimalFormatSymbols mySymbols = new DecimalFormatSymbols(pLocale);
        DecimalFormat myFormat = (DecimalFormat) NumberFormat.getInstance(pLocale);
        theGroupingSize = myFormat.getGroupingSize();

        /* Access various interesting formats */
        theMinusSign = mySymbols.getMinusSign();
        theGrouping = Character.toString(mySymbols.getGroupingSeparator());
        thePerCent = mySymbols.getPercent();
        thePerMille = mySymbols.getPerMill();
        theDecimal = Character.toString(mySymbols.getDecimalSeparator());
        theMoneyDecimal = Character.toString(mySymbols.getMonetaryDecimalSeparator());
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
    protected static String toString(final JDecimal pValue) {
        /* Access the value and scale */
        long myValue = pValue.unscaledValue();
        int myScale = pValue.scale();

        /* handle negative values */
        boolean isNegative = (myValue < 0);
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        myString.append(Long.toString(myValue));

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
    public String toCurrencyString(final JMoney pValue) {
        /* Format the basic value */
        StringBuilder myWork = new StringBuilder(toString(pValue));

        /* Add the currency symbol */
        Currency myCurrency = pValue.getCurrency();
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
        boolean isNegative = (myValue < 0);
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        myString.append(Long.toString(myValue));

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
        while (myLen > theGroupingSize) {
            /* Insert grouping character and remove grouping size from length */
            myString.insert(myLen
                            - theGroupingSize, theGrouping);
            myLen -= theGroupingSize;
        }

        /* Add minus sign if required */
        if (isNegative) {
            myString.insert(0, theMinusSign);
        }

        /* Return the string */
        return myString;
    }

    /**
     * Format Money value.
     * @param pMoney the value to format
     * @return the formatted value
     */
    public String formatMoney(final JMoney pMoney) {
        /* If we are using accounting and have zero */
        if ((useAccounting)
            && (pMoney.isZero())) {
            /* Format the zero */
            return formatZeroAccounting(pMoney.getCurrency());
        }

        /* Format the basic value */
        StringBuilder myWork = formatDecimal(pMoney.unscaledValue(), pMoney.scale(), theMoneyDecimal);

        /* If we have a leading minus sign */
        boolean isNegative = (myWork.charAt(0) == theMinusSign);
        if (isNegative) {
            /* Remove the minus sign */
            myWork = myWork.deleteCharAt(0);
        }

        /* If we are using accounting mode */
        if (useAccounting) {
            /* Format for accounting */
            formatForAccounting(myWork);
        }

        /* Add the currency symbol */
        Currency myCurrency = pMoney.getCurrency();
        myWork.insert(0, myCurrency.getSymbol(theLocale));

        /* Re-Add the minus sign */
        if (isNegative) {
            myWork.insert(0, theMinusSign);
        }

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Price value.
     * @param pPrice the value to format
     * @return the formatted value
     */
    public String formatPrice(final JPrice pPrice) {
        /* return the formatted value */
        return formatMoney(pPrice);
    }

    /**
     * Format DilutedPrice value.
     * @param pDilutedPrice the value to format
     * @return the formatted value
     */
    public String formatDilutedPrice(final JDilutedPrice pDilutedPrice) {
        /* return the formatted value */
        return formatMoney(pDilutedPrice);
    }

    /**
     * Format Rate value.
     * @param pRate the value to format
     * @return the formatted value
     */
    public String formatRate(final JRate pRate) {
        /* Format the basic value */
        StringBuilder myWork = formatDecimal(pRate.unscaledValue(), pRate.scale()
                                                                    - JDecimalParser.ADJUST_PERCENT, theDecimal);

        /* Append the perCent sign */
        myWork.append(thePerCent);

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Rate value.
     * @param pRate the value to format
     * @return the formatted value
     */
    public String formatRatePerMille(final JRate pRate) {
        /* Format the basic value */
        StringBuilder myWork = formatDecimal(pRate.unscaledValue(), pRate.scale()
                                                                    - JDecimalParser.ADJUST_PERMILLE, theDecimal);

        /* Append the perMille sign */
        myWork.append(thePerMille);

        /* return the formatted value */
        return myWork.toString();
    }

    /**
     * Format Units value.
     * @param pUnits the value to format
     * @return the formatted value
     */
    public String formatUnits(final JUnits pUnits) {
        /* Format the basic value */
        return formatBasicDecimal(pUnits);
    }

    /**
     * Format Dilution value.
     * @param pDilution the value to format
     * @return the formatted value
     */
    public String formatDilution(final JDilution pDilution) {
        /* Format the basic value */
        return formatBasicDecimal(pDilution);
    }

    /**
     * Format Ratio value.
     * @param pRatio the value to format
     * @return the formatted value
     */
    public String formatRatio(final JRatio pRatio) {
        /* Format the basic value */
        return formatBasicDecimal(pRatio);
    }

    /**
     * Format Decimal value.
     * @param pDecimal the value to format
     * @return the formatted value
     */
    public String formatDecimal(final JDecimal pDecimal) {
        /* Split out special cases */
        if (pDecimal instanceof JMoney) {
            return formatMoney((JMoney) pDecimal);
        } else if (pDecimal instanceof JRate) {
            return formatRate((JRate) pDecimal);
        }

        /* return the formatted value */
        return formatBasicDecimal(pDecimal);
    }

    /**
     * Format Decimal value.
     * @param pDecimal the value to format
     * @return the formatted value
     */
    private String formatBasicDecimal(final JDecimal pDecimal) {
        /* Format the basic value */
        StringBuilder myWork = formatDecimal(pDecimal.unscaledValue(), pDecimal.scale(), theDecimal);

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
        int myScale = pCurrency.getDefaultFractionDigits();

        /* Create a buffer build */
        StringBuilder myWork = new StringBuilder(Character.toString(CHAR_MINUS));

        /* If we have decimals */
        for (int i = 0; i < myScale; i++) {
            /* Add a blank in place of the decimal digit */
            myWork.append(CHAR_BLANK);
        }

        /* If we are short of the width */
        int myLen = myWork.length();
        while (myLen < theAccountingWidth) {
            /* Prefix with blank */
            myWork.insert(0, CHAR_BLANK);
            myLen++;
        }

        /* Add the currency symbol */
        myWork.insert(0, pCurrency.getSymbol(theLocale));

        /* Return the string */
        return myWork.toString();
    }
}