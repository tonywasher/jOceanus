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

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    public static final int ADJUST_PERCENT = 2;

    /**
     * PerMille adjustment.
     */
    public static final int ADJUST_PERMILLE = 3;

    /**
     * The locale.
     */
    private Locale theLocale;

    /**
     * The currencies map.
     */
    private final Map<String, Currency> theMap;

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
        /* Create currency map */
        theMap = new HashMap<String, Currency>();

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

        /* Clear the currency map */
        theMap.clear();

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
        theMap.put(theCurrency.getSymbol(theLocale), theCurrency);
    }

    /**
     * Parse a string into a decimal.
     * @param pValue The value to parse.
     * @param pDecSeparator the decimal separator
     * @param pResult the decimal to hold the result in
     * @throws IllegalArgumentException on invalid decimal
     */
    private void parseDecimalValue(final String pValue,
                                   final String pDecSeparator,
                                   final JDecimal pResult) throws IllegalArgumentException {
        /* Create a working copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        int myPos;
        long myValue;

        /* If the value is negative, strip the leading minus sign */
        boolean isNegative = ((myWork.length() > 0) && (myWork.charAt(0) == theMinusSign));
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
            throw new IllegalArgumentException(ERROR_PARSE
                                               + pValue, e);
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
                throw new IllegalArgumentException(ERROR_PARSE
                                                   + pValue, e);
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
     * @throws IllegalArgumentException on invalid decimal
     * @throws NullPointerException on null value
     */
    protected static void parseDecimalValue(final String pValue,
                                            final JDecimal pResult) throws IllegalArgumentException, NullPointerException {
        /* Handle null value */
        if (pValue == null) {
            throw new NullPointerException();
        }

        /* Create a working copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());
        int myPos;
        long myValue;

        /* If the value is negative, strip the leading minus sign */
        boolean isNegative = ((myWork.length() > 0) && (myWork.charAt(0) == JDecimalFormatter.CHAR_MINUS));
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
            throw new IllegalArgumentException(ERROR_PARSE
                                               + pValue, e);
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
                throw new IllegalArgumentException(ERROR_PARSE
                                                   + pValue, e);
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
            int myAdjust = pDecimals
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
     * @return the parsed currency
     * @throws IllegalArgumentException on invalid currency
     */
    private Currency parseCurrency(final StringBuilder pWork) throws IllegalArgumentException {
        /* Look for a currency separator */
        int iPos = pWork.indexOf(JDecimalFormatter.STR_CURRSEP);
        if (iPos > -1) {
            /* Extract currency detail and determine currency */
            String myCurr = pWork.substring(0, iPos);
            pWork.delete(0, iPos + 1);
            return Currency.getInstance(myCurr);
        }

        /* Set default currency */
        Currency myCurrency = theCurrency;

        /* If we have a leading minus sign */
        int iNumChars = pWork.length();
        boolean isNegative = false;
        if ((iNumChars > 0)
            && (pWork.charAt(0) == theMinusSign)) {
            /* Delete it and note the presence */
            pWork.deleteCharAt(0);
            iNumChars--;
            isNegative = true;
        }

        /* Look for currency symbol as leading non-digits and non-whitespace */
        int iNumSymbols = 0;
        while (iNumSymbols < iNumChars) {
            char c = pWork.charAt(iNumSymbols);
            if (Character.isDigit(c)
                || (c == JDecimalFormatter.CHAR_MINUS)
                || Character.isWhitespace(c)) {
                break;
            }
            iNumSymbols++;
        }

        /* If we have a symbol */
        if (iNumSymbols > 0) {
            /* Extract Symbol from buffer */
            String mySymbol = pWork.substring(0, iNumSymbols);
            pWork.delete(0, iNumSymbols);

            /* Look for the currency in the map */
            myCurrency = theMap.get(mySymbol);

            /* If this is a new currency */
            if (myCurrency == null) {
                /* Loop through all the currencies */
                for (Currency myCurr : Currency.getAvailableCurrencies()) {
                    /* If the symbol matches */
                    if (mySymbol.equals(myCurr.getSymbol(theLocale))) {
                        /* Record currency and break the loop */
                        myCurrency = myCurr;
                        theMap.put(mySymbol, myCurrency);
                        break;
                    }
                }

                /* If we did not find a currency */
                if (myCurrency == null) {
                    /* Reject the currency */
                    throw new IllegalArgumentException("Invalid currency: "
                                                       + mySymbol);
                }
            }
        }

        /* If we were negative */
        if (isNegative) {
            /* Reinsert the minus sign */
            pWork.insert(0, theMinusSign);
        }

        /* Return the currency */
        return myCurrency;
    }

    /**
     * Obtain a new zero money value for the default currency.
     * @return the new money
     */
    public JMoney zeroMoney() {
        return new JMoney(theCurrency);
    }

    /**
     * Obtain a new zero money value for the currency.
     * @param pCurrency the currency
     * @return the new money
     */
    public JMoney zeroMoney(final Currency pCurrency) {
        return new JMoney(pCurrency);
    }

    /**
     * Parse Money value.
     * @param pValue the string value to parse.
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public JMoney parseMoneyValue(final String pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());

        /* Determine currency */
        Currency myCurrency = parseCurrency(myWork);

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
        parseDecimalValue(myWork.toString(), theMoneyDecimal, myMoney);

        /* Correct the scale */
        adjustDecimals(myMoney, myCurrency.getDefaultFractionDigits());

        /* return the parsed money object */
        return myMoney;
    }

    /**
     * Obtain a new zero price value for the default currency.
     * @return the new price
     */
    public JPrice zeroPrice() {
        return new JPrice(theCurrency);
    }

    /**
     * Obtain a new zero price value for the currency.
     * @param pCurrency the currency
     * @return the new price
     */
    public JPrice zeroPrice(final Currency pCurrency) {
        return new JPrice(pCurrency);
    }

    /**
     * Parse Price value.
     * @param pValue the string value to parse.
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public JPrice parsePriceValue(final String pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());

        /* Look for explicit currency */
        Currency myCurrency = parseCurrency(myWork);

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
        parseDecimalValue(myWork.toString(), theMoneyDecimal, myPrice);

        /* Correct the scale */
        adjustDecimals(myPrice, myCurrency.getDefaultFractionDigits()
                                + JPrice.XTRA_DECIMALS);

        /* return the parsed price object */
        return myPrice;
    }

    /**
     * Parse DilutedPrice value.
     * @param pValue the string value to parse.
     * @return the parsed DilutedPrice
     * @throws IllegalArgumentException on invalid diluted price value
     */
    public JDilutedPrice parseDilutedPriceValue(final String pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create a working trimmed copy */
        StringBuilder myWork = new StringBuilder(pValue.trim());

        /* Determine currency */
        Currency myCurrency = parseCurrency(myWork);

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
        parseDecimalValue(myWork.toString(), theMoneyDecimal, myDilutedPrice);

        /* Correct the scale */
        adjustDecimals(myDilutedPrice, myCurrency.getDefaultFractionDigits()
                                       + JDilutedPrice.XTRA_DECIMALS);

        /* return the parsed diluted price object */
        return myDilutedPrice;
    }

    /**
     * Parse Rate value.
     * @param pValue the string value to parse.
     * @return the parsed rate
     * @throws IllegalArgumentException on invalid rate value
     */
    public JRate parseRateValue(final String pValue) throws IllegalArgumentException {
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
            myRate.recordScale(myXtraDecimals
                               + myRate.scale());
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
     * @throws IllegalArgumentException on invalid units value
     */
    public JUnits parseUnitsValue(final String pValue) throws IllegalArgumentException {
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
     * @throws IllegalArgumentException on invalid dilution value
     */
    public JDilution parseDilutionValue(final String pValue) throws IllegalArgumentException {
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
     * @throws IllegalArgumentException on invalid ratio value
     */
    public JRatio parseRatioValue(final String pValue) throws IllegalArgumentException {
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
     * @throws IllegalArgumentException on invalid decimal value
     */
    public JDecimal parseDecimalValue(final String pValue) throws IllegalArgumentException {
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

        /* return the parsed decimal object */
        return myDecimal;
    }

    /**
     * Trim parsing buffer.
     * @param pBuffer the buffer to trim
     */
    private static void trimBuffer(final StringBuilder pBuffer) {
        /* Remove leading blanks */
        while ((pBuffer.length() > 0)
               && (Character.isWhitespace(pBuffer.charAt(0)))) {
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
    public JMoney createMoneyFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Use default currency */
        return createMoneyFromDouble(pValue, theCurrency.getCurrencyCode());
    }

    /**
     * create Money from double.
     * @param pValue the double value.
     * @param pCurrCode the currency code
     * @return the parsed money
     * @throws IllegalArgumentException on invalid money value
     */
    public JMoney createMoneyFromDouble(final Double pValue,
                                        final String pCurrCode) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Determine currency */
        Currency myCurrency = Currency.getInstance(pCurrCode);

        /* Create the new Money object */
        JMoney myMoney = new JMoney(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theMoneyDecimal, myMoney);

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
    public JPrice createPriceFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Use default currency */
        return createPriceFromDouble(pValue, theCurrency.getCurrencyCode());
    }

    /**
     * create Price from double.
     * @param pValue the double value.
     * @param pCurrCode the currency code
     * @return the parsed price
     * @throws IllegalArgumentException on invalid price value
     */
    public JPrice createPriceFromDouble(final Double pValue,
                                        final String pCurrCode) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Determine currency */
        Currency myCurrency = Currency.getInstance(pCurrCode);

        /* Create the new Price object */
        JPrice myPrice = new JPrice(myCurrency);

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theMoneyDecimal, myPrice);

        /* Correct the scale */
        adjustDecimals(myPrice, myCurrency.getDefaultFractionDigits()
                                + JPrice.XTRA_DECIMALS);

        /* return the parsed price object */
        return myPrice;
    }

    /**
     * create Rate from double.
     * @param pValue the double value.
     * @return the parsed rate
     * @throws IllegalArgumentException on invalid rate value
     */
    public JRate createRateFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Rate object */
        JRate myRate = new JRate();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theDecimal, myRate);

        /* Correct the scale */
        adjustDecimals(myRate, JRate.NUM_DECIMALS);

        /* return the parsed rate object */
        return myRate;
    }

    /**
     * create Units from double.
     * @param pValue the double value.
     * @return the parsed units
     * @throws IllegalArgumentException on invalid units value
     */
    public JUnits createUnitsFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Units object */
        JUnits myUnits = new JUnits();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theDecimal, myUnits);

        /* Correct the scale */
        adjustDecimals(myUnits, JUnits.NUM_DECIMALS);

        /* return the parsed units object */
        return myUnits;
    }

    /**
     * create Dilution from double.
     * @param pValue the double value.
     * @return the parsed rate
     * @throws IllegalArgumentException on invalid price value
     */
    public JDilution createDilutionFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Dilution object */
        JDilution myDilution = new JDilution();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theDecimal, myDilution);

        /* Correct the scale */
        adjustDecimals(myDilution, JDilution.NUM_DECIMALS);

        /* return the parsed dilution object */
        return myDilution;
    }

    /**
     * create Rate from double.
     * @param pValue the double value.
     * @return the parsed ratio
     * @throws IllegalArgumentException on invalid ratio value
     */
    public JRatio createRatioFromDouble(final Double pValue) throws IllegalArgumentException {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Create the new Ratio object */
        JRatio myRatio = new JRatio();

        /* Parse the remaining string */
        parseDecimalValue(pValue.toString(), theDecimal, myRatio);

        /* Correct the scale */
        adjustDecimals(myRatio, JRatio.NUM_DECIMALS);

        /* return the parsed ratio object */
        return myRatio;
    }
}
