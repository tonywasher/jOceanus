/*******************************************************************************
 * JDecimal: Decimals represented by long values
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
package net.sourceforge.JDecimal;

import java.text.DecimalFormatSymbols;

/**
 * Provides classes to represent decimal numbers with fixed numbers of decimal digits {@link #theDecimals} as
 * Long integers. The decimal value is multiplied by 10 to the power of the number of decimals for the number
 * ({@link #theFactor}). The integral part of the number can be expressed as (Value / Factor) and the
 * fractional part as (Value % Factor). Arithmetic is then performed as whole number arithmetic on these
 * values, with due care taken on multiplication and division to express the result to the correct number of
 * decimals without losing any part of the answer to overflow.
 * <ul>
 * <li>TODO handle decimals with zero decimal digits
 * <li>TODO enable money/rate etc to be extended with differing numbers of decimals
 * <li>TODO pick up money decimals from local
 * <li>TODO provide Excel format patterns
 * </ul>
 */
public abstract class Decimal {
    /**
     * The Decimal formats.
     */
    public static final DecimalFormatSymbols DECIMAL_FORMATS = new DecimalFormatSymbols();

    /**
     * The Buffer length for building decimal strings.
     */
    private static final int INITIAL_BUFLEN = 20;

    /**
     * The Maximum # of Decimals.
     */
    public static final int MAX_DECIMALS = 10;

    /**
     * The decimal grouping.
     */
    public static final int GROUP_DIGITS = 3;

    /**
     * The Decimal radix.
     */
    public static final int RADIX_TEN = 10;

    /**
     * Powers of Ten.
     */
    private static final long[] POWERS_OF_TEN = { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L,
            100000000L, 1000000000L, 10000000000L };

    /**
     * The number of decimals for this object.
     */
    private int theDecimals = 0;

    /**
     * The number of hidden decimals for this object.
     */
    private int theHiddenDecimals = 0;

    /**
     * The value of the object multiplied by 10 to the power of {@link #theDecimals}. This allows
     * multiplication etc to be performed on integral values rather than the inaccurate method of using
     * doubles.
     */
    private long theValue = 0;

    /**
     * The Decimal factor, used for isolating integral and fractional parts.
     */
    private long theFactor = 1;

    /**
     * The Number class of the object.
     */
    private NumberClass theClass = NumberClass.STANDARD;

    /**
     * Access the value of the object.
     * @return the value
     */
    public long getValue() {
        return theValue;
    }

    /**
     * Set the value of the object.
     * @param uValue the value of the object
     */
    protected void setValue(final long uValue) {
        theValue = uValue;
    }

    /**
     * Construct a standard number type.
     * @param uDecimals the number of decimals for the number.
     */
    protected Decimal(final int uDecimals) {
        this(uDecimals, NumberClass.STANDARD);
    }

    /**
     * Construct a specific number type.
     * @param uDecimals the number of decimals for the number.
     * @param pClass the class of the number
     */
    protected Decimal(final int uDecimals,
                      final NumberClass pClass) {
        /* Store factors */
        theDecimals = uDecimals;
        theClass = pClass;

        /* Throw exception on invalid decimals */
        if ((theDecimals < 1) || (theDecimals > MAX_DECIMALS)) {
            throw new IllegalArgumentException("Decimals must be in the range 1 to 10");
        }

        /* Set two hidden decimals for Rate class */
        if (pClass == NumberClass.RATE) {
            theHiddenDecimals = 2;
        }

        /* Calculate decimal factor */
        theFactor = getFactor(getDecimals());
    }

    /**
     * Obtain integral part of number.
     * @return the integer part of the number
     */
    private long getIntegral() {
        return theValue / theFactor;
    }

    /**
     * Obtain fractional part of number.
     * @return the decimal part of the number
     */
    private long getFractional() {
        return theValue % theFactor;
    }

    /**
     * Obtain number of decimals.
     * @return the decimal part of the number
     */
    private int getDecimals() {
        return theDecimals + theHiddenDecimals;
    }

    /**
     * Obtain factor.
     * @param pDecimals the number of decimals
     * @return the decimal part of the number
     */
    private static long getFactor(final int pDecimals) {
        return POWERS_OF_TEN[pDecimals];
    }

    /**
     * Determine whether we have a non-zero value.
     * @return <code>true</code> if the value is non-zero, <code>false</code> otherwise.
     */
    public boolean isNonZero() {
        return (theValue != 0);
    }

    /**
     * Determine whether we have a positive (or zero) value.
     * @return <code>true</code> if the value is non-negative, <code>false</code> otherwise.
     */
    public boolean isPositive() {
        return (theValue >= 0);
    }

    /**
     * Negate the value.
     */
    public void negate() {
        theValue = -theValue;
    }

    /**
     * Multiply two decimals together to produce a third.
     * <p>
     * This function splits each part of the multiplication into integral and fractional parts (a,b) and
     * (c,d). It then treats each factor as the sum of the two parts (a+b) etc and calculates the product as
     * (a.c + a.d + b.c + b.d). To avoid losing significant digits by at either end of the calculation each
     * partial product is split into integral and fractional parts. The integers are summed together and the
     * fractional parts are summed together at combined decimal places of the two factors. Once all partial
     * products have been calculated, the integral and fractional totals are adjusted to the correct number of
     * decimal places and combined. This allows the multiplication to be built without risk of unnecessary
     * arithmetic overflow.
     * @param pFirst the first factor
     * @param pSecond the second factor
     */
    protected void calculateProduct(final Decimal pFirst,
                                    final Decimal pSecond) {
        /* Access information about first factor */
        long myIntFirst = pFirst.getIntegral();
        long myFracFirst = pFirst.getFractional();
        int myDecFirst = pFirst.getDecimals();

        /* Access information about second factor */
        long myIntSecond = pSecond.getIntegral();
        long myFracSecond = pSecond.getFractional();
        int myDecSecond = pSecond.getDecimals();

        /* Calculate (a.c) the integral part of the answer and initialise the fractional part (at maxDecimals) */
        int maxDecimals = myDecFirst + myDecSecond;
        long myIntegral = myIntFirst * myIntSecond;
        long myFractional = 0;

        /* Calculate (a.d) (myDecSecond decimals) and split off fractions */
        long myIntermediate = myIntFirst * myFracSecond;
        long myFractions = myIntermediate % getFactor(myDecSecond);
        myIntermediate -= myFractions;
        myIntegral += adjustDecimals(myIntermediate, -myDecSecond);
        myFractional += adjustDecimals(myFractions, maxDecimals - myDecSecond);

        /* Calculate (b.c) (myDecFirst decimals) and split off fractions */
        myIntermediate = myIntSecond * myFracFirst;
        myFractions = myIntermediate % getFactor(myDecFirst);
        myIntermediate -= myFractions;
        myIntegral += adjustDecimals(myIntermediate, -myDecFirst);
        myFractional += adjustDecimals(myFractions, maxDecimals - myDecFirst);

        /* Calculate (b.d) (maxDecimals decimals) */
        myIntermediate = myFracFirst * myFracSecond;
        myFractional += myIntermediate;

        /* Adjust and combine the two calculations */
        myIntegral = adjustDecimals(myIntegral, theDecimals);
        myFractional = adjustDecimals(myFractional, theDecimals - maxDecimals);
        theValue = myIntegral + myFractional;
    }

    /**
     * Adjust a value to a different number of decimals.
     * <p>
     * If the adjustment is to reduce the number of decimals, the most significant digit of the discarded
     * digits is examined to determine whether to round up. If the number of decimals is to be increased,
     * zeros are simply added to the end.
     * @param pValue the value to adjust
     * @param iAdjust the adjustment (positive if # of decimals are to increase, negative if they are to
     *            decrease)
     * @return the adjusted value
     */
    protected static long adjustDecimals(final long pValue,
                                         final int iAdjust) {
        /* Take a copy of the value */
        long myValue = pValue;

        /* If we need to reduce decimals */
        if (iAdjust < 0) {
            /* If we have more than one decimal to remove */
            if (iAdjust + 1 < 0) {
                /* Calculate division factor (minus one) */
                long myFactor = getFactor(-(iAdjust + 1));

                /* Reduce to 10 times required value */
                myValue /= myFactor;
            }

            /* Access last digit */
            long myDigit = pValue % RADIX_TEN;

            /* Reduce final decimal and round up if required */
            myValue /= RADIX_TEN;
            if (myDigit >= (RADIX_TEN / 2)) {
                myValue++;
            }

            /* else if we need to expand fractional product */
        } else if (iAdjust > 0) {
            myValue *= getFactor(iAdjust);
        }

        /* Return the adjusted value */
        return myValue;
    }

    /**
     * Divide a decimal by another decimals to produce a third.
     * <p>
     * In order to avoid loss of digits at the least significant bit end, the dividend is shifted left to
     * insert the required number of digits. An additional decimal is added before division and removed
     * afterwards to handle rounding correctly. In addition if the dividend has a different number of decimals
     * to the divisor the dividend is adjusted accordingly.
     * @param pDividend the number to divide
     * @param pDivisor the number to divide
     */
    protected void calculateQuotient(final Decimal pDividend,
                                     final Decimal pDivisor) {
        /* Access the two values */
        long myDividend = pDividend.getValue();
        long myDivisor = pDivisor.getValue();

        /* Determine how many decimals to factor in to the dividend to get the correct result */
        int myDecimals = getDecimals() + 1;
        myDecimals += pDivisor.getDecimals() - pDividend.getDecimals();

        /* Add in the decimals of the result plus 1 */
        myDividend = adjustDecimals(myDividend, myDecimals);

        /* Calculate the quotient */
        long myQuotient = myDividend / myDivisor;

        /* Remove the additional decimal to round correctly */
        myQuotient = adjustDecimals(myQuotient, -1);

        /* Set the value */
        theValue = myQuotient;
    }

    /**
     * Set to zero value.
     */
    public void setZero() {
        theValue = 0;
    }

    /**
     * Convert the value into a Double.
     * @return the value as a double
     */
    public double convertToDouble() {
        /* Format the string */
        String myString = format(false);

        /* return the double value */
        return Double.parseDouble(myString);
    }

    /**
     * Add a number to the value.
     * @param pValue The number to add to this one.
     */
    protected void addValue(final Decimal pValue) {
        theValue += pValue.theValue;
    }

    /**
     * Subtract a number from the value.
     * @param pValue The number to subtract from this one.
     */
    protected void subtractValue(final Decimal pValue) {
        theValue -= pValue.theValue;
    }

    /**
     * Parse a string to set the value.
     * @param pString The string to parse.
     */
    protected void parseStringValue(final String pString) {
        int myLen = pString.length();
        StringBuilder myWork;
        StringBuilder myDecimals = null;
        int myPos;
        char myLastDigit = '0';
        boolean isNegative;

        /* Create a working copy */
        myWork = new StringBuilder(pString);

        /* Trim leading and trailing blanks */
        while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0)))) {
            myWork.deleteCharAt(0);
        }
        while ((myLen = myWork.length()) > 0) {
            if (!Character.isWhitespace(myWork.charAt(myLen - 1))) {
                break;
            }
            myWork.deleteCharAt(myLen - 1);
        }

        /* If the value is negative, strip the leading minus sign */
        isNegative = (myWork.charAt(0) == DECIMAL_FORMATS.getMinusSign());
        if (isNegative) {
            myWork = myWork.deleteCharAt(0);
        }

        /* If this is a rate, remove any percent sign from the end of the string */
        myLen = myWork.length();
        if ((theClass == NumberClass.RATE) && (myWork.charAt(myLen - 1) == DECIMAL_FORMATS.getPercent())) {
            myWork.deleteCharAt(myLen - 1);
        }

        /* If this is money, remove any currency symbol from the beginning of the string */
        if (theClass == NumberClass.MONEY) {
            String myCurrency = DECIMAL_FORMATS.getCurrencySymbol();
            while ((myPos = myWork.indexOf(myCurrency)) != -1) {
                myWork.delete(myPos, myPos + myCurrency.length());
            }
        }

        /* Remove any grouping characters from the value */
        String myGroup = Character.toString(DECIMAL_FORMATS.getGroupingSeparator());
        while ((myPos = myWork.indexOf(myGroup)) != -1) {
            myWork.deleteCharAt(myPos);
        }

        /* Trim leading and trailing blanks again */
        while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0)))) {
            myWork.deleteCharAt(0);
        }
        while ((myLen = myWork.length()) > 0) {
            if (!Character.isWhitespace(myWork.charAt(myLen - 1))) {
                break;
            }
            myWork.deleteCharAt(myLen - 1);
        }

        /* Locate the decimal point if present */
        String myDec = Character.toString(DECIMAL_FORMATS.getDecimalSeparator());
        myPos = myWork.indexOf(myDec);

        /* If we have a decimal point */
        if (myPos != -1) {
            /* Split into the two parts being careful of a trailing decimal point */
            if ((myPos + 1) < myLen) {
                myDecimals = new StringBuilder(myWork.substring(myPos + 1));
            }
            myWork.setLength(myPos);
        }

        /* Handle leading decimal point on value */
        if (myWork.length() == 0) {
            myWork.append("0");
        }

        /* Parse the integer part */
        try {
            Long.parseLong(myWork.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non Decimal Numeric Value: " + pString, e);
        }

        /* If we have a decimal part */
        if (myDecimals != null) {
            /* Extend the decimal token to correct number of decimals */
            while ((myLen = myDecimals.length()) < theDecimals) {
                myDecimals.append('0');
            }

            /* If we have too many decimals */
            if (myLen > theDecimals) {
                /* Extract most significant trailing digit and truncate the value */
                myLastDigit = myDecimals.charAt(theDecimals);
                myDecimals.setLength(theDecimals);
            }

            /* Parse the decimals */
            try {
                Long.parseLong(myDecimals.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Non Decimal Numeric Value: " + pString, e);
            }

            /* Round value according to most significant discarded decimal digit */
            if (myLastDigit >= '5') {
                theValue++;
            }

            /* else we have no decimals */
        } else {
            /* Raise to appropriate factor given by number of decimals */
            theValue *= getFactor(theDecimals);
        }

        /* If the value is negative, negate the number */
        if (isNegative) {
            negate();
        }
    }

    /**
     * Format a numeric decimal value.
     * @param bPretty <code>true</code> if the value is to be formatted with thousands separator (and other
     *            appropriate enhancements such as %), <code>false</code> otherwise
     * @return the formatted value.
     */
    protected StringBuilder formatNumber(final boolean bPretty) {
        StringBuilder myString = new StringBuilder(INITIAL_BUFLEN);
        StringBuilder myBuild;
        String myDecimals;
        String myWhole;
        String myPart;
        int myLen;
        long myValue = theValue;
        boolean isNegative;

        /* handle negative values */
        isNegative = (theValue < 0);
        if (isNegative) {
            myValue = -myValue;
        }

        /* Format the string */
        myString.append(Long.toString(myValue));

        /* Add leading zeroes */
        while ((myLen = myString.length()) < (theDecimals + 1)) {
            myString.insert(0, '0');
        }

        /* Split into whole and decimal parts */
        myWhole = myString.substring(0, myLen - theDecimals);
        myDecimals = myString.substring(myLen - theDecimals);

        /* If this is a pretty format */
        if (bPretty) {
            /* Access grouping character */
            char myGroup = DECIMAL_FORMATS.getGroupingSeparator();

            /* Initialise build */
            myBuild = new StringBuilder(INITIAL_BUFLEN);

            /* Loop while we need to add grouping */
            while ((myLen = myWhole.length()) > GROUP_DIGITS) {
                /* Split out the next part */
                myPart = myWhole.substring(myLen - GROUP_DIGITS);
                myWhole = myWhole.substring(0, myLen - GROUP_DIGITS);

                /* Add existing build */
                if (myBuild.length() > 0) {
                    myBuild.insert(0, myGroup);
                }
                myBuild.insert(0, myPart);
            }

            /* If we have added some commas */
            if (myBuild.length() > 0) {
                /* Access the full string */
                myBuild.insert(0, myGroup);
                myBuild.insert(0, myWhole);
                myWhole = myBuild.toString();
            }
        }

        /* Rebuild the number */
        myString.setLength(0);
        myString.append(myWhole);
        myString.append(DECIMAL_FORMATS.getDecimalSeparator());
        myString.append(myDecimals);
        if (isNegative) {
            myString.insert(0, DECIMAL_FORMATS.getMinusSign());
        }

        /* Return the string */
        return myString;
    }

    /**
     * Compare this Number to another to establish sort order.
     * @param that The Number to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    public int compareTo(final Decimal that) {
        /* Handle trivial case */
        if (this == that) {
            return 0;
        }

        /* Make sure that the object is the same class */
        if (that.getClass() != this.getClass()) {
            return -1;
        }

        /* Compare values */
        if (theValue < that.theValue) {
            return -1;
        }
        if (theValue > that.theValue) {
            return 1;
        }
        return 0;
    }

    /**
     * Format the value.
     * @param bPretty add formatting characters
     * @return the formatted value
     */
    public abstract String format(final boolean bPretty);

    /**
     * Determine whether two Number objects differ.
     * @param pCurr The current Number
     * @param pNew The new Number
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(final Decimal pCurr,
                                      final Decimal pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return (pNew != null);
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return true;
        }

        /* Handle Standard cases */
        return (pCurr.compareTo(pNew) != 0);
    }

    /**
     * Class of a number for formatting purposes.
     */
    protected enum NumberClass {
        /**
         * Standard formatting for number.
         */
        STANDARD,

        /**
         * Rate formatting (% at end) plus two hidden decimal points.
         */
        RATE,

        /**
         * Money formatting (£ at front).
         */
        MONEY;
    }
}
