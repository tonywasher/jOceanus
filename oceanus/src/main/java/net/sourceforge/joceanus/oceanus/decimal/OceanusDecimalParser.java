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
 * Parsing methods for decimals in a particular locale.
 * @author Tony Washer
 */
public class OceanusDecimalParser {
    /**
     * Parse Error message.
     */
    private static final String ERROR_PARSE = "Non Decimal Numeric Value: ";

    /**
     * Bounds Error message.
     */
    private static final String ERROR_BOUNDS = "Value out of range: ";

    /**
     * PerCent adjustment.
     */
    public static final int ADJUST_PERCENT = 2;

    /**
     * PerMille adjustment.
     */
    public static final int ADJUST_PERMILLE = 3;

    /**
     * The locale.
     */
    private OceanusDecimalLocale theLocale;

    /**
     * Do we use strict # of decimals?
     */
    private boolean useStrictDecimals = true;

    /**
     * Constructor.
     */
    public OceanusDecimalParser() {
        /* Use default locale */
        this(OceanusLocale.getDefaultLocale());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public OceanusDecimalParser(final Locale pLocale) {
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
        theLocale = new OceanusDecimalLocale(pLocale);
    }

    /**
     * Obtain the default currency.
     * @return the default currency
     */
    public final Currency getDefaultCurrency() {
        return theLocale.getDefaultCurrency();
    }

    /**
     * Parse a string into a decimal.
     * @param pValue The value to parse.
     * @param pResult the decimal to hold the result in
     * @throws IllegalArgumentException on invalid decimal
     */
    protected static void parseDecimalValue(final String pValue,
                                            final OceanusDecimal pResult) {
        parseDecimalValue(pValue, OceanusDecimalFormatter.LOCALE_DEFAULT, false, pResult);
    }

    /**
     * Parse a string into a decimal.
     * @param pValue The value to parse.
     * @param pLocale the Decimal locale
     * @param useMoneyDecimal use money decimal rather than standard decimal true/false
     * @param pResult the decimal to hold the result in
     * @throws IllegalArgumentException on invalid decimal
     */
    protected static void parseDecimalValue(final String pValue,
                                            final OceanusDecimalLocale pLocale,
                                            final boolean useMoneyDecimal,
                                            final OceanusDecimal pResult) {
        /* Handle null value */
        if (pValue == null) {
            throw new IllegalArgumentException();
        }

        /* Create a working copy */
        final StringBuilder myWork = new StringBuilder(pValue.trim());

        /* If the value is negative, strip the leading minus sign */
        final boolean isNegative = !myWork.isEmpty()
                                   && myWork.charAt(0) == pLocale.getMinusSign();
        if (isNegative) {
            myWork.deleteCharAt(0);
        }

        /* Remove any grouping characters from the value */
        final String myGrouping = pLocale.getGrouping();
        int myPos;
        for (;;) {
            myPos = myWork.indexOf(myGrouping);
            if (myPos == -1) {
                break;
            }
            myWork.deleteCharAt(myPos);
        }

        /* Trim leading and trailing blanks again */
        trimBuffer(myWork);

        /* Locate the exponent if present */
        int myExponent = 0;
        myPos = myWork.indexOf("e");
        if (myPos != -1) {
            /* Obtain the exponent and remove from decimals */
            final String myExp = myWork.substring(myPos + 1);
            myWork.setLength(myPos);

            /* Parse the integral part */
            try {
                myExponent = Integer.parseInt(myExp);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_PARSE
                                                   + pValue, e);
            }
        }

        /* Locate the decimal point if present */
        myPos = myWork.indexOf(useMoneyDecimal
                                               ? pLocale.getMoneyDecimal()
                                               : pLocale.getDecimal());

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

        /* If we have a positive exponent */
        if (myExponent > 0) {
            /* Determine the number of decimals */
            int myNumDec = myDecimals == null
                                              ? 0
                                              : myDecimals.length();

            /* Shift decimals across */
            while (myExponent > 0 && myNumDec > 0) {
                /* Copy decimal across */
                final char myChar = myDecimals.charAt(0);
                myDecimals.deleteCharAt(0);
                myWork.append(myChar);

                /* Adjust counters */
                myExponent--;
                myNumDec--;
            }

            /* Finish off with zeroes */
            while (myExponent > 0) {
                myWork.append(OceanusDecimalFormatter.CHAR_ZERO);
                myExponent--;
            }

            /* If we now have no decimals remove decimal indication */
            if (myNumDec == 0) {
                myDecimals = null;
            }
            /* If we have a negative exponent */
        } else if (myExponent < 0) {
            /* Determine the number of integer digits */
            int myNumDigits = myWork.length();
            final StringBuilder myCopy = new StringBuilder();

            /* Shift decimals across */
            while (myExponent < 0 && myNumDigits > 0) {
                /* Copy digit across */
                final char myChar = myWork.charAt(myNumDigits - 1);
                myWork.deleteCharAt(myNumDigits - 1);
                myCopy.insert(0, myChar);

                /* Adjust counters */
                myExponent++;
                myNumDigits--;
            }

            /* Finish off with zeroes */
            while (myExponent < 0) {
                myCopy.insert(0, OceanusDecimalFormatter.CHAR_ZERO);
                myExponent++;
            }

            /* If we have decimals already */
            if (myDecimals != null) {
                myDecimals.insert(0, myCopy);
            } else {
                myDecimals = myCopy;
            }
        }

        /* Handle leading decimal point on value */
        if (myWork.isEmpty()) {
            myWork.append(OceanusDecimalFormatter.CHAR_ZERO);
        }

        /* Parse the integral part */
        long myValue;
        try {
            myValue = Long.parseLong(myWork.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ERROR_PARSE
                                               + pValue, e);
        }

        /* If we have a decimal part */
        if (myDecimals != null) {
            /* If we have too many decimals */
            char myLastDigit = OceanusDecimalFormatter.CHAR_ZERO;
            myScale = myDecimals.length();
            if (myScale > OceanusDecimal.MAX_DECIMALS) {
                /* Extract most significant trailing digit and truncate the value */
                myLastDigit = myDecimals.charAt(OceanusDecimal.MAX_DECIMALS);
                myDecimals.setLength(OceanusDecimal.MAX_DECIMALS);
                myScale = myDecimals.length();
            }

            /* Adjust the value to make room for the decimals */
            myValue *= OceanusDecimal.getFactor(myScale);

            /* Parse the decimals */
            try {
                myValue += Long.parseLong(myDecimals.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_PARSE
                                                   + pValue, e);
            }

            /* Round value according to most significant discarded decimal digit */
            if (myLastDigit >= Character.forDigit(OceanusDecimal.RADIX_TEN >> 1, OceanusDecimal.RADIX_TEN)) {
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
    private void adjustDecimals(final OceanusDecimal pValue,
                                final int pDecimals) {
        /* If we are using strict decimals */
        if (useStrictDecimals) {
            /* Correct the scale */
            pValue.adjustToScale(pDecimals);

            /* else we should honour what we can */
        } else {
            /* Calculate the standard correction */
            final int myAdjust = pDecimals
                                 - pValue.scale();

            /* If we have too few decimals */
            if (myAdjust > 0) {
                /* Adjust the value appropriately */
                pValue.movePointLeft(myAdjust);

                /* else if we have too many */
            } else if (myAdjust < 0) {
                /* remove redundant decimal places */
                pValue.reduceScale(pDecimals);
            }
        }
    }

    /**
     * Parse a string to extract currency information.
     * @param pWork the buffer to parse
     * @param pDeemedCurrency the assumed currency if no currency identifier
     * @return the parsed currency
     * @throws IllegalArgumentException on invalid currency
     */
    private Currency parseCurrency(final StringBuilder pWork,
                                   final Currency pDeemedCurrency) {
        /* Look for a currency separator */
        final int iPos = pWork.indexOf(OceanusDecimalFormatter.STR_CURRSEP);
        if (iPos > -1) {
            /* Extract currency detail and determine currency */
            final String myCurr = pWork.substring(0, iPos);
            pWork.delete(0, iPos + 1);
            return Currency.getInstance(myCurr);
        }

        /* Set default currency */
        Currency myCurrency = pDeemedCurrency;
        final char myMinus = theLocale.getMinusSign();

        /* If we have a leading minus sign */
        int iNumChars = pWork.length();
        boolean isNegative = false;
        if ((iNumChars > 0)
            && (pWork.charAt(0) == myMinus)) {
            /* Delete it and note the presence */
            pWork.deleteCharAt(0);
            iNumChars--;
            isNegative = true;
        }

        /* Look for currency symbol as leading non-digits and non-whitespace */
        int iNumSymbols = 0;
        while (iNumSymbols < iNumChars) {
            final char c = pWork.charAt(iNumSymbols);
            if (Character.isDigit(c)
                || (c == OceanusDecimalFormatter.CHAR_MINUS)
                || Character.isWhitespace(c)) {
                break;
            }
            iNumSymbols++;
        }

        /* If we have a symbol */
        if (iNumSymbols > 0) {
            /* Extract Symbol from buffer */
            final String mySymbol = pWork.substring(0, iNumSymbols);
            pWork.delete(0, iNumSymbols);

            /* Parse the currency symbol */
            myCurrency = theLocale.parseCurrencySymbol(mySymbol);
        }

        /* If we were negative */
        if (isNegative) {
            /* Reinsert the minus sign */
            pWork.insert(0, myMinus);
        }

        /* Return the currency */
        return myCurrency;
    }

    /**
     * Parse a long value.
     * @param pValue The value to parse.
     * @param pLocale the Decimal locale
     * @return the long value
     * @throws IllegalArgumentException on invalid decimal
     */
    protected static long parseLongValue(final String pValue,
                                         final OceanusDecimalLocale pLocale) {
        /* Handle null value */
        if (pValue == null) {
            throw new IllegalArgumentException();
        }

        /* Create a working copy */
        final StringBuilder myWork = new StringBuilder(pValue.trim());

        /* If the value is negative, strip the leading minus sign */
        final boolean isNegative = !myWork.isEmpty()
                                   && myWork.charAt(0) == pLocale.getMinusSign();
        if (isNegative) {
            myWork.deleteCharAt(0);
        }

        /* Remove any grouping characters from the value */
        final String myGrouping = pLocale.getGrouping();
        int myPos;
        for (;;) {
            myPos = myWork.indexOf(myGrouping);
            if (myPos == -1) {
                break;
            }
            myWork.deleteCharAt(myPos);
        }

        /* Trim leading and trailing blanks again */
        trimBuffer(myWork);

        /* Parse the long value */
        long myValue;
        try {
            myValue = Long.parseLong(myWork.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ERROR_PARSE
                                               + pValue, e);
        }

        /* If the value is negative, negate the number */
        if (isNegative) {
            myValue = -myValue;
        }

        /* return the result */
        return myValue;
    }

    /**
     * Obtain a new zero money value for the default currency.
     * @return the new money
     */
    public OceanusMoney zeroMoney() {
        return new OceanusMoney(theLocale.getDefaultCurrency());
    }

    /**
     * Obtain a new zero money value for the currency.
     * @param pCurrency the currency
     * @return the new money
     */
    public OceanusMoney zeroMoney(final Currency pCurrency) {
        return new OceanusMoney(pCurrency);
    }

    /**
     * Parse Money value.
     * @param pValue the string value to parse.
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public OceanusMoney parseMoneyValue(final String pValue) {
        return parseMoneyValue(pValue, null);
    }

    /**
     * Parse Money value.
     * @param pValue the string value to parse.
     * @param pDeemedCurrency the assumed currency if no currency identifier
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public OceanusMoney parseMoneyValue(final String pValue,
                                        final Currency pDeemedCurrency) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        final StringBuilder myWork = new StringBuilder(pValue.trim());

        /* Determine currency */
        final Currency myCurrency = parseCurrency(myWork, pDeemedCurrency == null
                                                                                  ? getDefaultCurrency()
                                                                                  : pDeemedCurrency);
        final char myMinus = theLocale.getMinusSign();

        /* If we have a leading minus sign */
        if (!myWork.isEmpty()
            && myWork.charAt(0) == myMinus) {
            /* Ensure there is no whitespace between minus sign and number */
            myWork.deleteCharAt(0);
            trimBuffer(myWork);
            myWork.insert(0, myMinus);
        }

        /* Create the new Money object */
        final OceanusMoney myMoney = new OceanusMoney(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), theLocale, true, myMoney);

        /* Correct the scale */
        adjustDecimals(myMoney, myCurrency.getDefaultFractionDigits());

        /* return the parsed money object */
        return myMoney;
    }

    /**
     * Obtain a new zero price value for the default currency.
     * @return the new price
     */
    public OceanusPrice zeroPrice() {
        return new OceanusPrice(theLocale.getDefaultCurrency());
    }

    /**
     * Obtain a new zero price value for the currency.
     * @param pCurrency the currency
     * @return the new price
     */
    public OceanusPrice zeroPrice(final Currency pCurrency) {
        return new OceanusPrice(pCurrency);
    }

    /**
     * Parse Price value.
     * @param pValue the string value to parse.
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public OceanusPrice parsePriceValue(final String pValue) {
        return parsePriceValue(pValue, null);
    }

    /**
     * Parse Price value.
     * @param pValue the string value to parse.
     * @param pDeemedCurrency the assumed currency if no currency identifier
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public OceanusPrice parsePriceValue(final String pValue,
                                        final Currency pDeemedCurrency) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        final StringBuilder myWork = new StringBuilder(pValue.trim());

        /* Look for explicit currency */
        final Currency myCurrency = parseCurrency(myWork, pDeemedCurrency == null
                                                                                  ? getDefaultCurrency()
                                                                                  : pDeemedCurrency);
        final char myMinus = theLocale.getMinusSign();

        /* If we have a leading minus sign */
        if (myWork.charAt(0) == myMinus) {
            /* Ensure there is no whitespace between minus sign and number */
            myWork.deleteCharAt(0);
            trimBuffer(myWork);
            myWork.insert(0, myMinus);
        }

        /* Create the new Price object */
        final OceanusPrice myPrice = new OceanusPrice(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), theLocale, true, myPrice);

        /* Correct the scale */
        adjustDecimals(myPrice, myCurrency.getDefaultFractionDigits()
                                + OceanusPrice.XTRA_DECIMALS);

        /* return the parsed price object */
        return myPrice;
    }

    /**
     * Parse Rate value.
     * @param pValue the string value to parse.
     * @return the parsed rate
     * @throws IllegalArgumentException on invalid rate value
     */
    public OceanusRate parseRateValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        final StringBuilder myWork = new StringBuilder(pValue.trim());
        int myXtraDecimals = 0;

        /* If there is a trailing perCent, remove any percent sign from the end of the string */
        final int myLast = myWork.length() - 1;
        if (myWork.charAt(myLast) == theLocale.getPerCent()) {
            myWork.deleteCharAt(myLast);
            myXtraDecimals = ADJUST_PERCENT;

            /*
             * If there is a trailing perMille, remove any percent sign from the end of the string
             */
        } else if (myWork.charAt(myLast) == theLocale.getPerMille()) {
            myWork.deleteCharAt(myLast);
            myXtraDecimals = ADJUST_PERMILLE;
        }

        /* Create the new Rate object */
        final OceanusRate myRate = new OceanusRate();

        /* Parse the remaining string */
        parseDecimalValue(myWork.toString(), theLocale, false, myRate);

        /* If we have extra Decimals to add */
        if (myXtraDecimals > 0) {
            /* Adjust the value appropriately */
            myRate.recordScale(myXtraDecimals
                               + myRate.scale());
        }

        /* Correct the scale */
        adjustDecimals(myRate, OceanusRate.NUM_DECIMALS);

        /* return the parsed rate object */
        return myRate;
    }

    /**
     * Parse Units value.
     * @param pValue the string value to parse.
     * @return the parsed units
     * @throws IllegalArgumentException on invalid units value
     */
    public OceanusUnits parseUnitsValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Units object */
        final OceanusUnits myUnits = new OceanusUnits();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theLocale, false, myUnits);

        /* Correct the scale */
        adjustDecimals(myUnits, OceanusUnits.NUM_DECIMALS);

        /* return the parsed units object */
        return myUnits;
    }

    /**
     * Parse Ratio value.
     * @param pValue the string value to parse.
     * @return the parsed ratio
     * @throws IllegalArgumentException on invalid ratio value
     */
    public OceanusRatio parseRatioValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Ratio object */
        final OceanusRatio myRatio = new OceanusRatio();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theLocale, false, myRatio);

        /* Correct the scale */
        adjustDecimals(myRatio, OceanusRatio.NUM_DECIMALS);

        /* return the parsed ratio object */
        return myRatio;
    }

    /**
     * Parse Decimal value.
     * @param pValue the string value to parse.
     * @param pScale the scale of the resulting decimal
     * @return the parsed decimal
     * @throws IllegalArgumentException on invalid decimal value
     */
    public OceanusDecimal parseDecimalValue(final String pValue,
                                            final int pScale) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Decimal object */
        final OceanusDecimal myDecimal = new OceanusDecimal();

        /* Parse the remaining string */
        parseDecimalValue(pValue.trim(), theLocale, false, myDecimal);
        adjustDecimals(myDecimal, pScale);

        /* return the parsed decimal object */
        return myDecimal;
    }

    /**
     * Parse Long value.
     * @param pValue the string value to parse.
     * @return the parsed value
     * @throws IllegalArgumentException on invalid value
     */
    public Long parseLongValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Parse the value */
        return parseLongValue(pValue, theLocale);
    }

    /**
     * Parse Integer value.
     * @param pValue the string value to parse.
     * @return the parsed value
     * @throws IllegalArgumentException on invalid value
     */
    public Integer parseIntegerValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Parse the value */
        final long myValue = parseLongValue(pValue, theLocale);

        /* Check bounds */
        if (myValue > Integer.MAX_VALUE || myValue < Integer.MIN_VALUE) {
            throw new IllegalArgumentException(ERROR_BOUNDS
                                               + pValue);
        }

        /* Return value */
        return (int) myValue;
    }

    /**
     * Parse Short value.
     * @param pValue the string value to parse.
     * @return the parsed value
     * @throws IllegalArgumentException on invalid value
     */
    public Short parseShortValue(final String pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Parse the value */
        final long myValue = parseLongValue(pValue, theLocale);

        /* Check bounds */
        if ((myValue > Short.MAX_VALUE) || (myValue < Short.MIN_VALUE)) {
            throw new IllegalArgumentException(ERROR_BOUNDS
                                               + pValue);
        }

        /* Return value */
        return (short) myValue;
    }

    /**
     * Trim parsing buffer.
     * @param pBuffer the buffer to trim
     */
    private static void trimBuffer(final StringBuilder pBuffer) {
        /* Remove leading blanks */
        while (!pBuffer.isEmpty()
               && Character.isWhitespace(pBuffer.charAt(0))) {
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

    /**
     * create Money from double.
     * @param pValue the double value.
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public OceanusMoney createMoneyFromDouble(final Double pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Use default currency */
        final Currency myCurrency = theLocale.getDefaultCurrency();
        return createMoneyFromDouble(pValue, myCurrency.getCurrencyCode());
    }

    /**
     * create Money from double.
     * @param pValue the double value.
     * @param pCurrCode the currency code
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public OceanusMoney createMoneyFromDouble(final Double pValue,
                                              final String pCurrCode) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Determine currency */
        final Currency myCurrency = Currency.getInstance(pCurrCode);

        /* Create the new Money object */
        final OceanusMoney myMoney = new OceanusMoney(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theLocale, true, myMoney);

        /* Correct the scale */
        adjustDecimals(myMoney, myCurrency.getDefaultFractionDigits());

        /* return the parsed money object */
        return myMoney;
    }

    /**
     * create Price from double.
     * @param pValue the double value.
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public OceanusPrice createPriceFromDouble(final Double pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Use default currency */
        final Currency myCurrency = theLocale.getDefaultCurrency();
        return createPriceFromDouble(pValue, myCurrency.getCurrencyCode());
    }

    /**
     * create Price from double.
     * @param pValue the double value.
     * @param pCurrCode the currency code
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public OceanusPrice createPriceFromDouble(final Double pValue,
                                              final String pCurrCode) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Determine currency */
        final Currency myCurrency = Currency.getInstance(pCurrCode);

        /* Create the new Price object */
        final OceanusPrice myPrice = new OceanusPrice(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theLocale, false, myPrice);

        /* Correct the scale */
        adjustDecimals(myPrice, myCurrency.getDefaultFractionDigits()
                                + OceanusPrice.XTRA_DECIMALS);

        /* return the parsed price object */
        return myPrice;
    }

    /**
     * create Rate from double.
     * @param pValue the double value.
     * @return the parsed rate
     * @throws IllegalArgumentException on invalid rate value
     */
    public OceanusRate createRateFromDouble(final Double pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Rate object */
        final OceanusRate myRate = new OceanusRate();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theLocale, false, myRate);

        /* Correct the scale */
        adjustDecimals(myRate, OceanusRate.NUM_DECIMALS);

        /* return the parsed rate object */
        return myRate;
    }

    /**
     * create Units from double.
     * @param pValue the double value.
     * @return the parsed units
     * @throws IllegalArgumentException on invalid units value
     */
    public OceanusUnits createUnitsFromDouble(final Double pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Units object */
        final OceanusUnits myUnits = new OceanusUnits();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theLocale, false, myUnits);

        /* Correct the scale */
        adjustDecimals(myUnits, OceanusUnits.NUM_DECIMALS);

        /* return the parsed units object */
        return myUnits;
    }

    /**
     * create Ratio from double.
     * @param pValue the double value.
     * @return the parsed ratio
     * @throws IllegalArgumentException on invalid ratio value
     */
    public OceanusRatio createRatioFromDouble(final Double pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Ratio object */
        final OceanusRatio myRatio = new OceanusRatio();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theLocale, false, myRatio);

        /* Correct the scale */
        adjustDecimals(myRatio, OceanusRatio.NUM_DECIMALS);

        /* return the parsed ratio object */
        return myRatio;
    }
}
