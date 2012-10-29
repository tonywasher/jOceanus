/*******************************************************************************
 * jDecimal: Decimals represented by long values
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
package net.sourceforge.jOceanus.jDecimal;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

/**
 * Parsing methods for decimals in a particular locale.
 * @author Tony Washer
 */
public class JDecimalParser {
    /**
     * Error message.
     */
    private static final String ERROR_PARSE = "Non Decimal Numeric Value: ";

    /**
     * PerCent adjustment.
     */
    protected static final int ADJUST_PERCENT = 2;

    /**
     * PerMille adjustment.
     */
    protected static final int ADJUST_PERMILLE = 3;

    /**
     * The locale.
     */
    private Locale theLocale;

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
     * The default currency.
     */
    private Currency theCurrency;

    /**
     * Do we use strict # of decimals?
     */
    private boolean useStrictDecimals = true;

    /**
     * Constructor.
     */
    public JDecimalParser() {
        /* Use default locale */
        this(Locale.getDefault());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public JDecimalParser(final Locale pLocale) {
        /* Store locale */
        setLocale(pLocale);
    }

    /**
     * Should we parse to strict decimals.
     * @param bStrictDecimals true/false
     */
    public void setStrictDecimals(final boolean bStrictDecimals) {
        /* Set accounting mode on and set the width */
        useStrictDecimals = bStrictDecimals;
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public final void setLocale(final Locale pLocale) {
        /* Store the locale */
        theLocale = pLocale;

        /* Access decimal formats */
        DecimalFormatSymbols mySymbols = DecimalFormatSymbols.getInstance(theLocale);

        /* Access various interesting formats */
        theMinusSign = mySymbols.getMinusSign();
        thePerCent = mySymbols.getPercent();
        thePerMille = mySymbols.getPerMill();
        theGrouping = Character.toString(mySymbols.getGroupingSeparator());
        theDecimal = Character.toString(mySymbols.getDecimalSeparator());
        theMoneyDecimal = Character.toString(mySymbols.getMonetaryDecimalSeparator());

        /* Access the default currency */
        theCurrency = mySymbols.getCurrency();
    }

    /**
     * Parse a string into a decimal.
     * @param pValue The value to parse.
     * @param pDecSeparator the decimal separator
     * @param pResult the decimal to hold the result in
     */
    private void parseDecimalValue(final String pValue,
                                   final String pDecSeparator,
                                   final JDecimal pResult) {
        /* Create a working copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        int myPos;
        long myValue;

        /* If the value is negative, strip the leading minus sign */
        boolean isNegative = (myWork.charAt(0) == theMinusSign);
        if (isNegative) {
            myWork.deleteCharAt(0);
        }

        /* Remove any grouping characters from the value */
        while ((myPos = myWork.indexOf(theGrouping)) != -1) {
            myWork.deleteCharAt(myPos);
        }

        /* Trim leading and trailing blanks again */
        trimBuffer(myWork);

        /* Locate the decimal point if present */
        myPos = myWork.indexOf(pDecSeparator);

        /* Assume no decimals */
        StringBuilder myDecimals = null;
        int myScale = 0;

        /* If we have a decimal point */
        if (myPos != -1) {
            /* Split into the two parts being careful of a trailing decimal point */
            if ((myPos + 1) < myWork.length()) {
                myDecimals = new StringBuilder(myWork.substring(myPos + 1));
            }
            myWork.setLength(myPos);
        }

        /* Handle leading decimal point on value */
        if (myWork.length() == 0) {
            myWork.append(JDecimalFormatter.CHAR_ZERO);
        }

        /* Parse the integral part */
        try {
            myValue = Long.parseLong(myWork.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ERROR_PARSE + pValue, e);
        }

        /* If we have a decimal part */
        if (myDecimals != null) {
            /* If we have too many decimals */
            char myLastDigit = JDecimalFormatter.CHAR_ZERO;
            myScale = myDecimals.length();
            if (myScale > JDecimal.MAX_DECIMALS) {
                /* Extract most significant trailing digit and truncate the value */
                myLastDigit = myDecimals.charAt(JDecimal.MAX_DECIMALS);
                myDecimals.setLength(JDecimal.MAX_DECIMALS);
                myScale = myDecimals.length();
            }

            /* Adjust the value to make room for the decimals */
            myValue *= JDecimal.getFactor(myScale);

            /* Parse the decimals */
            try {
                myValue += Long.parseLong(myDecimals.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_PARSE + pValue, e);
            }

            /* Round value according to most significant discarded decimal digit */
            if (myLastDigit >= Character.forDigit(JDecimal.RADIX_TEN >> 1, JDecimal.RADIX_TEN)) {
                myValue++;
            }
        }

        /* If the value is negative, negate the number */
        if (isNegative) {
            myValue = -myValue;
        }

        /* Store the result into the decimal */
        pResult.setValue(myValue, myScale);
    }

    /**
     * Parse a string into a decimal without reference to a locale.
     * @param pValue The value to parse.
     * @param pResult the decimal to hold the result in
     */
    protected static void parseDecimalValue(final String pValue,
                                            final JDecimal pResult) {
        /* Create a working copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        int myPos;
        long myValue;

        /* If the value is negative, strip the leading minus sign */
        boolean isNegative = (myWork.charAt(0) == JDecimalFormatter.CHAR_MINUS);
        if (isNegative) {
            myWork.deleteCharAt(0);
        }

        /* Locate the decimal point if present */
        myPos = myWork.indexOf(JDecimalFormatter.STR_DEC);

        /* Assume no decimals */
        StringBuilder myDecimals = null;
        int myScale = 0;

        /* If we have a decimal point */
        if (myPos != -1) {
            /* Split into the two parts being careful of a trailing decimal point */
            if ((myPos + 1) < myWork.length()) {
                myDecimals = new StringBuilder(myWork.substring(myPos + 1));
            }
            myWork.setLength(myPos);
        }

        /* Handle leading decimal point on value */
        if (myWork.length() == 0) {
            myWork.append(JDecimalFormatter.CHAR_ZERO);
        }

        /* Parse the integral part */
        try {
            myValue = Long.parseLong(myWork.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ERROR_PARSE + pValue, e);
        }

        /* If we have a decimal part */
        if (myDecimals != null) {
            /* If we have too many decimals */
            char myLastDigit = JDecimalFormatter.CHAR_ZERO;
            myScale = myDecimals.length();
            if (myScale > JDecimal.MAX_DECIMALS) {
                /* Extract most significant trailing digit and truncate the value */
                myLastDigit = myDecimals.charAt(JDecimal.MAX_DECIMALS);
                myDecimals.setLength(JDecimal.MAX_DECIMALS);
                myScale = myDecimals.length();
            }

            /* Adjust the value to make room for the decimals */
            myValue *= JDecimal.getFactor(myScale);

            /* Parse the decimals */
            try {
                myValue += Long.parseLong(myDecimals.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_PARSE + pValue, e);
            }

            /* Round value according to most significant discarded decimal digit */
            if (myLastDigit >= Character.forDigit(JDecimal.RADIX_TEN >> 1, JDecimal.RADIX_TEN)) {
                myValue++;
            }
        }

        /* If the value is negative, negate the number */
        if (isNegative) {
            myValue = -myValue;
        }

        /* Store the result into the decimal */
        pResult.setValue(myValue, myScale);
    }

    /**
     * Adjust to desired decimals.
     * @param pValue the value to adjust
     * @param pDecimals the desired decimals
     */
    private void adjustDecimals(final JDecimal pValue,
                                final int pDecimals) {
        /* If we are using strict decimals */
        if (useStrictDecimals) {
            /* Correct the scale */
            pValue.adjustToScale(pDecimals);

            /* else we should honour what we can */
        } else {
            /* Calculate the standard correction */
            int myAdjust = pDecimals - pValue.scale();

            /* If we have too few decimals */
            if (myAdjust > 0) {
                /* Adjust the value appropriately */
                pValue.movePointRight(myAdjust);

                /* else if we have too many */
            } else if (myAdjust < 0) {
                /* remove redundant decimal places */
                pValue.reduceScale(pDecimals);
            }
        }
    }

    /**
     * Parse a currency string.
     * @param pWork the buffer to parse
     * @return the parsed currency
     */
    private static Currency parseCurrency(final StringBuilder pWork) {
        /* Find the currency separator */
        int iPos = pWork.indexOf(JDecimalFormatter.STR_CURRSEP);
        if (iPos > -1) {
            String myCurr = pWork.substring(0, iPos - 1);
            pWork.delete(0, iPos);
            return Currency.getInstance(myCurr);
        }

        /* Return the default */
        return null;
    }

    /**
     * Obtain a new zero money value.
     * @return the new money
     */
    public JMoney zeroMoney() {
        return new JMoney(theCurrency);
    }

    /**
     * Parse Money value.
     * @param pValue the string value to parse.
     * @return the parsed money
     */
    public JMoney parseMoneyValue(final String pValue) {
        /* Use default currency */
        return parseMoneyValue(pValue, theCurrency);
    }

    /**
     * Parse Money value.
     * @param pValue the string value to parse.
     * @param pCurrency the currency
     * @return the parsed money
     */
    public JMoney parseMoneyValue(final String pValue,
                                  final Currency pCurrency) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        String myDec = JDecimalFormatter.STR_DEC;
        int myPos;

        /* Look for explicit currency */
        Currency myCurrency = parseCurrency(myWork);
        if (myCurrency == null) {
            myCurrency = pCurrency;
            myDec = theMoneyDecimal;
        }

        /* While there are instances of the Currency symbol */
        String mySymbol = myCurrency.getSymbol(theLocale);
        while ((myPos = myWork.indexOf(mySymbol)) != -1) {
            /* Remove it */
            myWork.delete(myPos, myPos + mySymbol.length());
        }

        /* If we have a leading minus sign */
        if (myWork.charAt(0) == theMinusSign) {
            /* Ensure there is no whitespace between minus sign and number */
            myWork = myWork.deleteCharAt(0);
            trimBuffer(myWork);
            myWork.insert(0, theMinusSign);
        }

        /* Create the new Money object */
        JMoney myMoney = new JMoney(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), myDec, myMoney);

        /* Correct the scale */
        adjustDecimals(myMoney, myCurrency.getDefaultFractionDigits());

        /* return the parsed money object */
        return myMoney;
    }

    /**
     * Obtain a new zero price value.
     * @return the new money
     */
    public JPrice zeroPrice() {
        return new JPrice(theCurrency);
    }

    /**
     * Parse Price value.
     * @param pValue the string value to parse.
     * @return the parsed price
     */
    public JPrice parsePriceValue(final String pValue) {
        /* Use default currency */
        return parsePriceValue(pValue, theCurrency);
    }

    /**
     * Parse Price value.
     * @param pValue the string value to parse.
     * @param pCurrency the currency
     * @return the parsed price
     */
    public JPrice parsePriceValue(final String pValue,
                                  final Currency pCurrency) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        String myDec = JDecimalFormatter.STR_DEC;
        int myPos;

        /* Look for explicit currency */
        Currency myCurrency = parseCurrency(myWork);
        if (myCurrency == null) {
            myCurrency = pCurrency;
            myDec = theMoneyDecimal;
        }

        /* While there are instances of the Currency symbol */
        String mySymbol = myCurrency.getSymbol(theLocale);
        while ((myPos = myWork.indexOf(mySymbol)) != -1) {
            /* Remove it */
            myWork.delete(myPos, myPos + mySymbol.length());
        }

        /* If we have a leading minus sign */
        if (myWork.charAt(0) == theMinusSign) {
            /* Ensure there is no whitespace between minus sign and number */
            myWork = myWork.deleteCharAt(0);
            trimBuffer(myWork);
            myWork.insert(0, theMinusSign);
        }

        /* Create the new Price object */
        JPrice myPrice = new JPrice(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), myDec, myPrice);

        /* Correct the scale */
        adjustDecimals(myPrice, myCurrency.getDefaultFractionDigits() + JPrice.XTRA_DECIMALS);

        /* return the parsed price object */
        return myPrice;
    }

    /**
     * Parse DilutedPrice value.
     * @param pValue the string value to parse.
     * @return the parsed DilutedPrice
     */
    public JDilutedPrice parseDilutedPriceValue(final String pValue) {
        /* Use default currency */
        return parseDilutedPriceValue(pValue, theCurrency);
    }

    /**
     * Parse Diluted Price value.
     * @param pValue the string value to parse.
     * @param pCurrency the currency
     * @return the parsed diluted price
     */
    public JDilutedPrice parseDilutedPriceValue(final String pValue,
                                                final Currency pCurrency) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        String myDec = JDecimalFormatter.STR_DEC;
        int myPos;

        /* Look for explicit currency */
        Currency myCurrency = parseCurrency(myWork);
        if (myCurrency == null) {
            myCurrency = pCurrency;
            myDec = theMoneyDecimal;
        }

        /* While there are instances of the Currency symbol */
        String mySymbol = myCurrency.getSymbol(theLocale);
        while ((myPos = myWork.indexOf(mySymbol)) != -1) {
            /* Remove it */
            myWork.delete(myPos, myPos + mySymbol.length());
        }

        /* If we have a leading minus sign */
        if (myWork.charAt(0) == theMinusSign) {
            /* Ensure there is no whitespace between minus sign and number */
            myWork = myWork.deleteCharAt(0);
            trimBuffer(myWork);
            myWork.insert(0, theMinusSign);
        }

        /* Create the new DilutedPrice object */
        JDilutedPrice myDilutedPrice = new JDilutedPrice(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), myDec, myDilutedPrice);

        /* Correct the scale */
        adjustDecimals(myDilutedPrice, myCurrency.getDefaultFractionDigits() + JDilutedPrice.XTRA_DECIMALS);

        /* return the parsed diluted price object */
        return myDilutedPrice;
    }

    /**
     * Parse Rate value.
     * @param pValue the string value to parse.
     * @return the parsed rate
     */
    public JRate parseRateValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        int myXtraDecimals = 0;

        /* If there is a trailing perCent, remove any percent sign from the end of the string */
        int myLast = myWork.length() - 1;
        if (myWork.charAt(myLast) == thePerCent) {
            myWork.deleteCharAt(myLast);
            myXtraDecimals = ADJUST_PERCENT;

            /* If there is a trailing perMille, remove any percent sign from the end of the string */
        } else if (myWork.charAt(myLast) == thePerMille) {
            myWork.deleteCharAt(myLast);
            myXtraDecimals = ADJUST_PERMILLE;
        }

        /* Create the new Rate object */
        JRate myRate = new JRate();

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), theDecimal, myRate);

        /* If we have extra Decimals to add */
        if (myXtraDecimals > 0) {
            /* Adjust the value appropriately */
            myRate.recordScale(myXtraDecimals + myRate.scale());
        }

        /* Correct the scale */
        adjustDecimals(myRate, JRate.NUM_DECIMALS);

        /* return the parsed rate object */
        return myRate;
    }

    /**
     * Parse Units value.
     * @param pValue the string value to parse.
     * @return the parsed units
     */
    public JUnits parseUnitsValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Units object */
        JUnits myUnits = new JUnits();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theDecimal, myUnits);

        /* Correct the scale */
        adjustDecimals(myUnits, JUnits.NUM_DECIMALS);

        /* return the parsed units object */
        return myUnits;
    }

    /**
     * Parse Dilution value.
     * @param pValue the string value to parse.
     * @return the parsed dilution
     */
    public JDilution parseDilutionValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Dilution object */
        JDilution myDilution = new JDilution();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theDecimal, myDilution);

        /* Correct the scale */
        adjustDecimals(myDilution, JDilution.NUM_DECIMALS);

        /* return the parsed dilution object */
        return myDilution;
    }

    /**
     * Parse Ratio value.
     * @param pValue the string value to parse.
     * @return the parsed ratio
     */
    public JRatio parseRatioValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Ratio object */
        JRatio myRatio = new JRatio();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theDecimal, myRatio);

        /* Correct the scale */
        adjustDecimals(myRatio, JRatio.NUM_DECIMALS);

        /* return the parsed ratio object */
        return myRatio;
    }

    /**
     * Parse Decimal value.
     * @param pValue the string value to parse.
     * @return the parsed decimal
     */
    public JDecimal parseDecimalValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Decimal object */
        JDecimal myDecimal = new JDecimal();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theDecimal, myDecimal);

        /* remove redundant decimal places */
        myDecimal.reduceScale(0);

        /* return the parsed ratio object */
        return myDecimal;
    }

    /**
     * Trim parsing buffer.
     * @param pBuffer the buffer to trim
     */
    private static void trimBuffer(final StringBuilder pBuffer) {
        /* Remove leading blanks */
        while ((pBuffer.length() > 0) && (Character.isWhitespace(pBuffer.charAt(0)))) {
            pBuffer.deleteCharAt(0);
        }

        /* Remove trailing blanks */
        int myLen = pBuffer.length();
        while (myLen-- > 0) {
            if (!Character.isWhitespace(pBuffer.charAt(myLen))) {
                break;
            }
            pBuffer.deleteCharAt(myLen);
        }
    }
}
