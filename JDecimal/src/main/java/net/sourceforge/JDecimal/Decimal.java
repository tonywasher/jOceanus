/**
 * Decimal Numbers represented as longs
 * Copyright (C) 2011 Tony Washer
 * Tony.Washer@yahoo.co.uk
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
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
     * The Decimal formats
     */
    public static final DecimalFormatSymbols theFormats = new DecimalFormatSymbols();

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
     * doubles
     */
    private long theValue = 0;

    /**
     * The Decimal factor, used for isolating integral and fractional parts
     */
    private long theFactor = 1;

    /**
     * The Number class of the object
     */
    private NumberClass theClass = NumberClass.STANDARD;

    /**
     * Access the value of the object
     * @return the value
     */
    public long getValue() {
        return theValue;
    }

    /**
     * Set the value of the object
     * @param uValue the value of the object
     */
    private void setValue(long uValue) {
        theValue = uValue;
    }

    /**
     * Construct a standard number type
     * @param uDecimals the number of decimals for the number.
     */
    private Decimal(int uDecimals) {
        this(uDecimals, NumberClass.STANDARD);
    }

    /**
     * Construct a specific number type
     * @param uDecimals the number of decimals for the number.
     * @param pClass the class of the number
     */
    private Decimal(int uDecimals,
                    NumberClass pClass) {
        /* Store factors */
        theDecimals = uDecimals;
        theClass = pClass;

        /* Throw exception on invalid decimals */
        if ((theDecimals < 1) || (theDecimals > 10))
            throw new IllegalArgumentException("Decimals must be in the range 1 to 10");

        /* Set two hidden decimals for Rate class */
        if (pClass == NumberClass.RATE)
            theHiddenDecimals = 2;

        /* Calculate decimal factor */
        theFactor = getFactor(getDecimals());
    }

    /**
     * Obtain integral part of number
     * @return the integer part of the number
     */
    private long getIntegral() {
        return theValue / theFactor;
    }

    /**
     * Obtain fractional part of number
     * @return the decimal part of the number
     */
    private long getFractional() {
        return theValue % theFactor;
    }

    /**
     * Obtain number of decimals
     * @return the decimal part of the number
     */
    private int getDecimals() {
        return theDecimals + theHiddenDecimals;
    }

    /**
     * Obtain factor
     * @param pDecimals the number of decimals
     * @return the decimal part of the number
     */
    private static long getFactor(int pDecimals) {
        long myFactor = 1;
        for (int i = 0; i < pDecimals; i++)
            myFactor *= 10;
        return myFactor;
    }

    /**
     * Determine whether we have a non-zero value
     * @return <code>true</code> if the value is non-zero, <code>false</code> otherwise.
     */
    public boolean isNonZero() {
        return (theValue != 0);
    }

    /**
     * Determine whether we have a positive (or zero) value
     * @return <code>true</code> if the value is non-negative, <code>false</code> otherwise.
     */
    public boolean isPositive() {
        return (theValue >= 0);
    }

    /**
     * Negate the value
     */
    public void negate() {
        theValue = -theValue;
    }

    /**
     * Multiply two decimals together to produce a third
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
    protected void calculateProduct(Decimal pFirst,
                                    Decimal pSecond) {
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
     * Adjust a value to a different number of decimals
     * <p>
     * If the adjustment is to reduce the number of decimals, the most significant digit of the discarded
     * digits is examined to determine whether to round up. If the number of decimals is to be increased,
     * zeros are simply added to the end
     * @param pValue the value to adjust
     * @param iAdjust the adjustment (positive if # of decimals are to increase, negative if they are to
     *            decrease)
     * @return the adjusted value
     */
    private static long adjustDecimals(long pValue,
                                       int iAdjust) {
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
            long myDigit = pValue % 10;

            /* Reduce final decimal and round up if required */
            myValue /= 10;
            if (myDigit >= 5)
                myValue++;
        }

        /* else if we need to expand fractional product */
        else if (iAdjust > 0)
            myValue *= getFactor(iAdjust);

        /* Return the adjusted value */
        return myValue;
    }

    /**
     * Divide a decimal by another decimals to produce a third
     * <p>
     * In order to avoid loss of digits at the least significant bit end, the dividend is shifted left to
     * insert the required number of digits. An additional decimal is added before division and removed
     * afterwards to handle rounding correctly. In addition if the dividend has a different number of decimals
     * to the divisor the dividend is adjusted accordingly
     * @param pDividend the number to divide
     * @param pDivisor the number to divide
     */
    protected void calculateQuotient(Decimal pDividend,
                                     Decimal pDivisor) {
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
     * Set to zero value
     */
    public void setZero() {
        theValue = 0;
    }

    /**
     * Convert the value into a Double
     * @return the value as a double
     */
    public double convertToDouble() {
        /* Format the string */
        String myString = format(false);

        /* return the double value */
        return Double.parseDouble(myString);
    }

    /**
     * Add a number to the value
     * @param pValue The number to add to this one.
     */
    private void addValue(Decimal pValue) {
        theValue += pValue.theValue;
    }

    /**
     * Subtract a number from the value
     * @param pValue The number to subtract from this one.
     */
    private void subtractValue(Decimal pValue) {
        theValue -= pValue.theValue;
    }

    /**
     * Parse a string to set the value
     * @param pString The string to parse.
     */
    private void ParseString(String pString) {
        int myLen = pString.length();
        StringBuilder myWork;
        StringBuilder myDecimals = null;
        int myPos;
        char myDigit = '0';
        char myChar;
        boolean isNegative;

        /* Create a working copy */
        myWork = new StringBuilder(pString);

        /* Trim leading and trailing blanks */
        while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0))))
            myWork.deleteCharAt(0);
        while (((myLen = myWork.length()) > 0) && (Character.isWhitespace(myWork.charAt(myLen - 1))))
            myWork.deleteCharAt(myLen - 1);

        /* If the value is negative, strip the leading minus sign */
        isNegative = (myWork.charAt(0) == theFormats.getMinusSign());
        if (isNegative)
            myWork = myWork.deleteCharAt(0);

        /* If this is a rate, remove any percent sign from the end of the string */
        myLen = myWork.length();
        if ((theClass == NumberClass.RATE) && (myWork.charAt(myLen - 1) == theFormats.getPercent()))
            myWork.deleteCharAt(myLen - 1);

        /* If this is money, remove any currency symbol from the beginning of the string */
        if (theClass == NumberClass.MONEY) {
            String myCurrency = theFormats.getCurrencySymbol();
            while ((myPos = myWork.indexOf(myCurrency)) != -1)
                myWork.delete(myPos, myPos + myCurrency.length());
        }

        /* Remove any grouping characters from the value */
        String myGroup = Character.toString(theFormats.getGroupingSeparator());
        while ((myPos = myWork.indexOf(myGroup)) != -1)
            myWork.deleteCharAt(myPos);

        /* Trim leading and trailing blanks */
        while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0))))
            myWork.deleteCharAt(0);
        while (((myLen = myWork.length()) > 0) && (Character.isWhitespace(myWork.charAt(myLen - 1))))
            myWork.deleteCharAt(myLen - 1);

        /* Locate the decimal point if present */
        String myDec = Character.toString(theFormats.getDecimalSeparator());
        myPos = myWork.indexOf(myDec);

        /* If we have a decimal point */
        if (myPos != -1) {
            /* Split into the two parts being careful of a trailing decimal point */
            if ((myPos + 1) < myLen)
                myDecimals = new StringBuilder(myWork.substring(myPos + 1));
            myWork.setLength(myPos);
        }

        /* Handle leading decimal point on value */
        if (myWork.length() == 0)
            myWork.append("0");

        /* Loop through the characters of the integer part of the value */
        myLen = myWork.length();
        for (int i = 0; i < myLen; i++) {
            /* Access the next character */
            myChar = myWork.charAt(i);

            /* Check that the char is a valid digit */
            if (!Character.isDigit(myChar))
                throw new IllegalArgumentException("Non Decimal Numeric Value: " + pString);

            /* Add into the value */
            theValue *= 10;
            theValue += (myChar - '0');
        }

        /* If we have a decimal part */
        if (myDecimals != null) {
            /* Extend the decimal token to correct number of decimals */
            while ((myLen = myDecimals.length()) < theDecimals)
                myDecimals.append('0');

            /* If we have too many decimals */
            if (myLen > theDecimals) {
                /* Extract most significant trailing digit and truncate the value */
                myDigit = myDecimals.charAt(theDecimals);
                myDecimals.setLength(theDecimals);
            }

            /* Loop through the characters of the decimal part of the value */
            myLen = myDecimals.length();
            for (int i = 0; i < myLen; i++) {
                /* Access the next character */
                myChar = myDecimals.charAt(i);

                /* Check that the char is a valid hex digit */
                if (!Character.isDigit(myChar))
                    throw new IllegalArgumentException("Non Decimal Numeric Value: " + pString);

                /* Add into the value */
                theValue *= 10;
                theValue += (myChar - '0');
            }

            /* Round value according to most significant discarded decimal digit */
            if (myDigit >= '5')
                theValue++;
        }

        /* else we have no decimals */
        else {
            /* Raise to appropriate factor given by number of decimals */
            for (int i = 0; i < theDecimals; i++)
                theValue *= 10;
        }

        /* If the value is negative, negate the number */
        if (isNegative)
            negate();
    }

    /**
     * Format a numeric decimal value
     * @param bPretty <code>true</code> if the value is to be formatted with thousands separator (and other
     *            appropriate enhancements such as %), <code>false</code> otherwise
     * @return the formatted value.
     */
    private StringBuilder formatNumber(boolean bPretty) {
        StringBuilder myString = new StringBuilder(20);
        StringBuilder myBuild;
        String myDecimals;
        String myWhole;
        String myPart;
        int myLen;
        long myValue = theValue;
        boolean isNegative;

        /* handle negative values */
        isNegative = (theValue < 0);
        if (isNegative)
            myValue = -myValue;

        /* Special case for zero */
        if (myValue == 0)
            myString.append('0');

        /* else need to loop through the digits */
        else {
            /* While we have digits to format */
            while (myValue > 0) {
                /* Format the digit and move to next one */
                myString.insert(0, (char) ('0' + (myValue % 10)));
                myValue /= 10;
            }
        }

        /* Add leading zeroes */
        while ((myLen = myString.length()) < (theDecimals + 1))
            myString.insert(0, '0');

        /* Split into whole and decimal parts */
        myWhole = myString.substring(0, myLen - theDecimals);
        myDecimals = myString.substring(myLen - theDecimals);

        /* If this is a pretty format */
        if (bPretty) {
            /* Access grouping character */
            char myGroup = theFormats.getGroupingSeparator();

            /* Initialise build */
            myBuild = new StringBuilder(20);

            /* Loop while we need to add grouping */
            while ((myLen = myWhole.length()) > 3) {
                /* Split out the next part */
                myPart = myWhole.substring(myLen - 3);
                myWhole = myWhole.substring(0, myLen - 3);

                /* Add existing build */
                if (myBuild.length() > 0)
                    myBuild.insert(0, myGroup);
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
        myString.append(theFormats.getDecimalSeparator());
        myString.append(myDecimals);
        if (isNegative)
            myString.insert(0, theFormats.getMinusSign());

        /* Return the string */
        return myString;
    }

    /**
     * Compare this Number to another to establish sort order.
     * @param that The Number to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    public int compareTo(Decimal that) {
        /* Handle trivial case */
        if (this == that)
            return 0;

        /* Make sure that the object is the same class */
        if (that.getClass() != this.getClass())
            return -1;

        /* Compare values */
        if (theValue < that.theValue)
            return -1;
        if (theValue > that.theValue)
            return 1;
        return 0;
    }

    /* Number interfaces */
    public abstract String format(boolean bPretty);

    /**
     * Determine whether two Number objects differ.
     * @param pCurr The current Number
     * @param pNew The new Number
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(Decimal pCurr,
                                      Decimal pNew) {
        /* Handle case where current value is null */
        if (pCurr == null)
            return (pNew != null);

        /* Handle case where new value is null */
        if (pNew == null)
            return true;

        /* Handle Standard cases */
        return (pCurr.compareTo(pNew) != 0);
    }

    /**
     * Class of a number for formatting purposes.
     */
    private enum NumberClass {
        /**
         * Standard formatting for number
         */
        STANDARD,

        /**
         * Rate formatting (% at end) plus two hidden decimal points
         */
        RATE,

        /**
         * Money formatting (£ at front)
         */
        MONEY;
    }

    /**
     * Represents a Rate object.
     */
    public static class Rate extends Decimal {
        /**
         * Rates have two decimal points
         */
        public final static int NUMDEC = 2;

        /**
         * One hundred percent
         */
        public final static Rate OneHundredPerCent = new Rate(10000);

        /**
         * Access the value of the Rate
         * @return the value
         */
        public long getRate() {
            return getValue();
        }

        /**
         * Construct a new Rate from a value
         * @param uRate the value
         */
        public Rate(long uRate) {
            super(NUMDEC, NumberClass.RATE);
            super.setValue(uRate);
        }

        /**
         * Construct a new Rate by copying another rate
         * @param pRate the Rate to copy
         */
        public Rate(Rate pRate) {
            super(NUMDEC, NumberClass.RATE);
            super.setValue(pRate.getRate());
        }

        /**
         * Construct a new Rate by parsing a string value
         * @param pRate the Rate to parse
         */
        public Rate(String pRate) {
            super(NUMDEC, NumberClass.RATE);
            super.ParseString(pRate);
        }

        /**
         * Obtain remaining rate of this rate (i.e. 100% - this rate)
         * @return the remaining rate
         */
        public Rate getRemainingRate() {
            long myValue = OneHundredPerCent.getRate() - getRate();
            return new Rate(myValue);
        }

        /**
         * Obtain inverse ratio of this rate (i.e. 100%/this rate)
         * @return the inverse ratio
         */
        public Ratio getInverseRatio() {
            return new Ratio(OneHundredPerCent, this);
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat;
            myFormat = super.formatNumber(bPretty);
            if (bPretty)
                myFormat.append(theFormats.getPercent());
            return myFormat.toString();
        }

        /**
         * Format a Rate
         * @param pRate the rate to format
         * @return the formatted Rate
         */
        public static String format(Rate pRate) {
            return (pRate != null) ? pRate.format(false) : "null";
        }

        /**
         * Create a new Rate by parsing a string value
         * @param pRate the Rate to parse
         * @return the new Rate or <code>null</code> if parsing failed
         */
        public static Rate Parse(String pRate) {
            try {
                return new Rate(pRate);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Convert a whole number value to include decimals
         * @param pValue the whole number value
         * @return the converted value with added zeros
         */
        public static long convertToValue(long pValue) {
            /* Build in the decimals to the value */
            return adjustDecimals(pValue, NUMDEC);
        }

        @Override
        public double convertToDouble() {
            /* Convert the value into units (to increase decimals by two) */
            Units myUnits = new Units(getValue());

            /* Format the string */
            String myString = myUnits.format(false);

            /* return the double value */
            return Double.parseDouble(myString);
        }
    }

    /**
     * Represents a Ratio object.
     */
    public static class Ratio extends Decimal {
        /**
         * Ratio have six decimal points (plus two hidden)
         */
        public final static int NUMDEC = 6;

        /**
         * Access the value of the Ratio
         * @return the value
         */
        public long getRatio() {
            return getValue();
        }

        /**
         * Construct a new Ratio from a value
         * @param uRatio the value
         */
        public Ratio(long uRatio) {
            super(NUMDEC, NumberClass.RATE);
            super.setValue(uRatio);
        }

        /**
         * Construct a new Ratio by copying another rate
         * @param pRatio the Ratio to copy
         */
        public Ratio(Ratio pRatio) {
            super(NUMDEC, NumberClass.RATE);
            super.setValue(pRatio.getRatio());
        }

        /**
         * Construct a new Ratio by the ratio between two decimals
         * @param pFirst the first decimal
         * @param pSecond the second decimal
         */
        public Ratio(Decimal pFirst,
                     Decimal pSecond) {
            super(NUMDEC, NumberClass.RATE);
            calculateQuotient(pFirst, pSecond);
        }

        /**
         * Construct a new Ratio by parsing a string value
         * @param pRatio the Ratio to parse
         */
        public Ratio(String pRatio) {
            super(NUMDEC, NumberClass.RATE);
            super.ParseString(pRatio);
        }

        /**
         * Obtain inverse rate of this rate (i.e. 100%/this rate)
         * @return the remaining rate
         */
        public Ratio getInverseRatio() {
            return new Ratio(Rate.OneHundredPerCent, this);
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat;
            myFormat = super.formatNumber(bPretty);
            if (bPretty)
                myFormat.append(theFormats.getPercent());
            return myFormat.toString();
        }

        /**
         * Format a Ratio
         * @param pRatio the rate to format
         * @return the formatted Ratio
         */
        public static String format(Ratio pRatio) {
            return (pRatio != null) ? pRatio.format(false) : "null";
        }

        /**
         * Create a new Ratio by parsing a string value
         * @param pRatio the Ratio to parse
         * @return the new Ratio or <code>null</code> if parsing failed
         */
        public static Ratio Parse(String pRatio) {
            try {
                return new Ratio(pRatio);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Convert a whole number value to include decimals
         * @param pValue the whole number value
         * @return the converted value with added zeros
         */
        public static long convertToValue(long pValue) {
            /* Build in the decimals to the value */
            return adjustDecimals(pValue, NUMDEC);
        }
    }

    /**
     * Represents a Price object.
     */
    public static class Price extends Decimal {
        /**
         * Prices have four decimal points
         */
        public final static int NUMDEC = 4;

        /**
         * Prices are formatted in pretty mode with a width of 10 characters
         */
        public final static int WIDTH = 10;

        /**
         * Access the value of the Price
         * @return the value
         */
        public long getPrice() {
            return getValue();
        }

        /**
         * Construct a new Price from a value
         * @param uPrice the value
         */
        public Price(long uPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(uPrice);
        }

        /**
         * Construct a new Price by copying another price
         * @param pPrice the price to copy
         */
        public Price(Price pPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(pPrice.getPrice());
        }

        /**
         * Construct a new Price by combining diluted price and dilution
         * @param pFirst the DilutedPrice to unDilute
         * @param pSecond the Dilution factor
         */
        public Price(DilutedPrice pFirst,
                     Dilution pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateQuotient(pFirst, pSecond);
        }

        /**
         * Construct a new Price by parsing a string value
         * @param pPrice the Price to parse
         */
        public Price(String pPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.ParseString(pPrice);
        }

        /**
         * obtain a Diluted price
         * @param pDilution the dilution factor
         * @return the calculated value
         */
        public DilutedPrice getDilutedPrice(Dilution pDilution) {
            /* Calculate diluted price */
            DilutedPrice myTotal = new DilutedPrice(this, pDilution);

            /* Return value */
            return myTotal;
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat;
            boolean isNegative;

            /* Format the value in a standard fashion */
            myFormat = super.formatNumber(bPretty);

            /* If we are in pretty mode */
            if (bPretty) {
                /* If the value is zero */
                if (!isNonZero()) {
                    /* Provide special display */
                    myFormat.setLength(0);
                    myFormat.append(theFormats.getCurrencySymbol());
                    myFormat.append("      -   ");
                }

                /* Else non-zero value */
                else {
                    /* Access the minus sign */
                    char myMinus = theFormats.getMinusSign();

                    /* If the value is negative, strip the leading minus sign */
                    isNegative = (myFormat.charAt(0) == myMinus);
                    if (isNegative)
                        myFormat.deleteCharAt(0);

                    /* Extend the value to the desired width */
                    while ((myFormat.length()) < WIDTH)
                        myFormat.insert(0, ' ');

                    /* Add the currency symbol */
                    myFormat.insert(0, theFormats.getCurrencySymbol());

                    /* Add back any minus sign */
                    if (isNegative)
                        myFormat.insert(0, myMinus);
                }
            }
            return myFormat.toString();
        }

        /**
         * Create a new Price by parsing a string value
         * @param pPrice the Price to parse
         * @return the new Price or <code>null</code> if parsing failed
         */
        public static Price Parse(String pPrice) {
            try {
                return new Price(pPrice);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Format a Price
         * @param pPrice the price to format
         * @return the formatted Price
         */
        public static String format(Price pPrice) {
            return (pPrice != null) ? pPrice.format(false) : "null";
        }

        /**
         * Convert a whole number value to include decimals
         * @param pValue the whole number value
         * @return the converted value with added zeros
         */
        public static long convertToValue(long pValue) {
            /* Build in the decimals to the value */
            return adjustDecimals(pValue, NUMDEC);
        }
    }

    /**
     * Represents a Diluted Price object.
     */
    public static class DilutedPrice extends Decimal {
        /**
         * DilutedPrices have six decimal points
         */
        public final static int NUMDEC = 6;

        /**
         * DilutedPrices are formatted in pretty mode with a width of 12 characters
         */
        public final static int WIDTH = 12;

        /**
         * Access the value of the Price
         * @return the value
         */
        public long getDilutedPrice() {
            return getValue();
        }

        /**
         * Construct a new DilutedPrice from a value
         * @param uPrice the value
         */
        public DilutedPrice(long uPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(uPrice);
        }

        /**
         * Construct a new DilutedPrice by copying another price
         * @param pPrice the Price to copy
         */
        public DilutedPrice(DilutedPrice pPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(pPrice.getDilutedPrice());
        }

        /**
         * Construct a new Diluted Price by combining price and dilution
         * @param pFirst the Price to dilute
         * @param pSecond the Dilution factor
         */
        public DilutedPrice(Price pFirst,
                            Dilution pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new DilutedPrice by parsing a string value
         * @param pPrice the Price to parse
         */
        public DilutedPrice(String pPrice) {
            super(NUMDEC, NumberClass.MONEY);
            super.ParseString(pPrice);
        }

        /**
         * obtain a base price
         * @param pDilution the dilution factor
         * @return the calculated value
         */
        public Price getPrice(Dilution pDilution) {
            /* Calculate original base price */
            Price myTotal = new Price(this, pDilution);

            /* Return value */
            return myTotal;
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat;
            boolean isNegative;

            /* Format the value in a standard fashion */
            myFormat = super.formatNumber(bPretty);

            /* If we are in pretty mode */
            if (bPretty) {
                /* If the value is zero */
                if (!isNonZero()) {
                    /* Provide special display */
                    myFormat.setLength(0);
                    myFormat.append(theFormats.getCurrencySymbol());
                    myFormat.append("      -     ");
                }

                /* Else non-zero value */
                else {
                    /* Access the minus sign */
                    char myMinus = theFormats.getMinusSign();

                    /* If the value is negative, strip the leading minus sign */
                    isNegative = (myFormat.charAt(0) == myMinus);
                    if (isNegative)
                        myFormat.deleteCharAt(0);

                    /* Extend the value to the desired width */
                    while ((myFormat.length()) < WIDTH)
                        myFormat.insert(0, ' ');

                    /* Add the pound sign */
                    myFormat.insert(0, theFormats.getCurrencySymbol());

                    /* Add back any minus sign */
                    if (isNegative)
                        myFormat.insert(0, myMinus);
                }
            }
            return myFormat.toString();
        }

        /**
         * Create a new Price by parsing a string value
         * @param pPrice the Price to parse
         * @return the new Price or <code>null</code> if parsing failed
         */
        public static DilutedPrice Parse(String pPrice) {
            try {
                return new DilutedPrice(pPrice);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Format a DilutedPrice
         * @param pPrice the price to format
         * @return the formatted Price
         */
        public static String format(DilutedPrice pPrice) {
            return (pPrice != null) ? pPrice.format(false) : "null";
        }
    }

    /**
     * Represents a Units object.
     */
    public static class Units extends Decimal {
        /**
         * Units have four decimal points
         */
        public final static int NUMDEC = 4;

        /**
         * Access the value of the Units
         * @return the value
         */
        public long getUnits() {
            return getValue();
        }

        /**
         * Add units to the value
         * @param pUnits The units to add to this one.
         */
        public void addUnits(Units pUnits) {
            super.addValue(pUnits);
        }

        /**
         * Subtract units from the value
         * @param pUnits The units to subtract from this one.
         */
        public void subtractUnits(Units pUnits) {
            super.subtractValue(pUnits);
        }

        /**
         * Construct a new Units from a value
         * @param uUnits the value
         */
        public Units(long uUnits) {
            super(NUMDEC);
            super.setValue(uUnits);
        }

        /**
         * Construct a new Units by copying another units
         * @param pUnits the units to copy
         */
        public Units(Units pUnits) {
            super(NUMDEC);
            super.setValue(pUnits.getUnits());
        }

        /**
         * Construct a new Units by parsing a string value
         * @param pUnits the Units to parse
         */
        public Units(String pUnits) {
            super(NUMDEC);
            super.ParseString(pUnits);
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat = super.formatNumber(bPretty);
            return myFormat.toString();
        }

        /**
         * calculate the value of these units at a given price
         * @param pPrice the per unit price
         * @return the calculated value
         */
        public Money valueAtPrice(Price pPrice) {
            /* Calculate value of units */
            Money myTotal = new Money(this, pPrice);

            /* Return value */
            return myTotal;
        }

        /**
         * Create a new Units by parsing a string value
         * @param pUnits the Units to parse
         * @return the new Units or <code>null</code> if parsing failed
         */
        public static Units Parse(String pUnits) {
            try {
                return new Units(pUnits);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Format a Units
         * @param pUnits the units to format
         * @return the formatted Units
         */
        public static String format(Units pUnits) {
            return (pUnits != null) ? pUnits.format(false) : "null";
        }
    }

    /**
     * Represents a Dilution object.
     */
    public static class Dilution extends Decimal {
        /**
         * Dilutions have six decimal points
         */
        public final static int NUMDEC = 6;

        /**
         * Define the maximum dilution value
         */
        public final static int MAX_VALUE = 1000000;

        /**
         * Define the minimum dilution value
         */
        public final static int MIN_VALUE = 0;

        /**
         * Access the value of the Dilution
         * @return the value
         */
        public long getDilution() {
            return getValue();
        }

        /**
         * Construct a new Dilution from a value
         * @param uDilution the value
         */
        public Dilution(long uDilution) {
            super(NUMDEC);
            super.setValue(uDilution);
        }

        /**
         * Construct a new Dilution by copying another dilution
         * @param pDilution the Dilution to copy
         */
        public Dilution(Dilution pDilution) {
            super(NUMDEC);
            super.setValue(pDilution.getDilution());
        }

        /**
         * Construct a new Dilution by combining two dilution factors
         * @param pFirst the first Dilution factor
         * @param pSecond the second Dilution factor
         */
        public Dilution(Dilution pFirst,
                        Dilution pSecond) {
            super(NUMDEC);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new Dilution by parsing a string value
         * @param pDilution the Dilution to parse
         */
        public Dilution(String pDilution) {
            super(NUMDEC);
            super.ParseString(pDilution);
            if (outOfRange())
                throw new IllegalArgumentException("Dilution value invalid :" + pDilution);
        }

        /**
         * obtain a further dilution
         * @param pDilution the dilution factor
         * @return the calculated value
         */
        public Dilution getFurtherDilution(Dilution pDilution) {
            /* Calculate the new dilution */
            Dilution myTotal = new Dilution(this, pDilution);

            /* Return value */
            return myTotal;
        }

        /**
         * Is the dilution factor outside the valid range
         * @return true/false
         */
        public boolean outOfRange() {
            return ((getValue() > MAX_VALUE) || (getValue() < MIN_VALUE));
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat = super.formatNumber(bPretty);
            return myFormat.toString();
        }

        /**
         * Create a new Dilution by parsing a string value
         * @param pDilution the Dilution to parse
         * @return the new Dilution or <code>null</code> if parsing failed
         */
        public static Dilution Parse(String pDilution) {
            try {
                return new Dilution(pDilution);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Format a Dilution
         * @param pDilution the dilution to format
         * @return the formatted Dilution
         */
        public static String format(Dilution pDilution) {
            return (pDilution != null) ? pDilution.format(false) : "null";
        }
    }

    /**
     * Represents a Money object.
     */
    public static class Money extends Decimal {
        /**
         * Money has two decimal points
         */
        public final static int NUMDEC = 2;

        /**
         * Money is formatted in pretty mode with a width of 10 characters
         */
        public final static int WIDTH = 10;

        /**
         * Access the value of the Money
         * @return the value
         */
        public long getAmount() {
            return getValue();
        }

        /**
         * Add money to the value
         * @param pAmount The amount to add to this one.
         */
        public void addAmount(Money pAmount) {
            super.addValue(pAmount);
        }

        /**
         * Subtract money from the value
         * @param pAmount The amount to subtract from this one.
         */
        public void subtractAmount(Money pAmount) {
            super.subtractValue(pAmount);
        }

        /**
         * Construct a new Money from a value
         * @param uAmount the value
         */
        public Money(long uAmount) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(uAmount);
        }

        /**
         * Construct a new Money by copying another money
         * @param pMoney the Money to copy
         */
        public Money(Money pMoney) {
            super(NUMDEC, NumberClass.MONEY);
            super.setValue(pMoney.getAmount());
        }

        /**
         * Construct a new Money by combining units and price
         * @param pFirst the number of units
         * @param pSecond the price of each unit
         */
        public Money(Units pFirst,
                     Price pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new Money by combining money and rate
         * @param pFirst the Money to apply rate to
         * @param pSecond the Rate to apply
         */
        public Money(Money pFirst,
                     Rate pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new Money by combining money and ratio
         * @param pFirst the Money to apply ratio to
         * @param pSecond the Ratio to apply
         */
        public Money(Money pFirst,
                     Ratio pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new Money by combining money and dilution
         * @param pFirst the Money to dilute
         * @param pSecond the Dilution factor
         */
        public Money(Money pFirst,
                     Dilution pSecond) {
            super(NUMDEC, NumberClass.MONEY);
            calculateProduct(pFirst, pSecond);
        }

        /**
         * Construct a new Money by parsing a string value
         * @param pMoney the Money to parse
         */
        public Money(String pMoney) {
            super(NUMDEC, NumberClass.MONEY);
            super.ParseString(pMoney);
        }

        /**
         * obtain a Diluted value
         * @param pDilution the dilution factor
         * @return the calculated value
         */
        public Money getDilutedAmount(Dilution pDilution) {
            /* Calculate diluted value */
            Money myTotal = new Money(this, pDilution);

            /* Return value */
            return myTotal;
        }

        @Override
        public String format(boolean bPretty) {
            StringBuilder myFormat;
            boolean isNegative;

            /* Format the value in a standard fashion */
            myFormat = super.formatNumber(bPretty);

            /* If we are in pretty mode */
            if (bPretty) {
                /* If the value is zero */
                if (!isNonZero()) {
                    /* Provide special display */
                    myFormat.setLength(0);
                    myFormat.append(theFormats.getCurrencySymbol());
                    myFormat.append("      -   ");
                }

                /* Else non-zero value */
                else {
                    /* Access the minus sign */
                    char myMinus = theFormats.getMinusSign();

                    /* If the value is negative, strip the leading minus sign */
                    isNegative = (myFormat.charAt(0) == myMinus);
                    if (isNegative)
                        myFormat = myFormat.deleteCharAt(0);

                    /* Extend the value to the desired width */
                    while ((myFormat.length()) < WIDTH)
                        myFormat.insert(0, ' ');

                    /* Add the pound sign */
                    myFormat.insert(0, theFormats.getCurrencySymbol());

                    /* Add back any minus sign */
                    if (isNegative)
                        myFormat.insert(0, myMinus);
                }
            }
            return myFormat.toString();
        }

        /**
         * Format money
         * @param pMoney the money to format
         * @return the formatted Money
         */
        public static String format(Money pMoney) {
            return (pMoney != null) ? pMoney.format(false) : "null";
        }

        /**
         * Create a new Money by parsing a string value
         * @param pMoney the Money to parse
         * @return the new Money or <code>null</code> if parsing failed
         */
        public static Money Parse(String pMoney) {
            try {
                return new Money(pMoney);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * calculate the value of this money at a given rate
         * @param pRate the rate to calculate at
         * @return the calculated value
         */
        public Money valueAtRate(Rate pRate) {
            /* Calculate the money at this rate */
            Money myTotal = new Money(this, pRate);

            /* Return value */
            return myTotal;
        }

        /**
         * calculate the gross value of this money at a given rate used to convert from net to gross values
         * form interest and dividends
         * @param pRate the rate to calculate at
         * @return the calculated value
         */
        public Money grossValueAtRate(Rate pRate) {
            /* Calculate the Gross corresponding to this net value at the rate */
            Ratio myRatio = pRate.getRemainingRate().getInverseRatio();
            Money myTotal = new Money(this, myRatio);

            /* Return value */
            return myTotal;
        }

        /**
         * calculate the TaxCredit value of this money at a given rate used to convert from net to gross
         * values form interest and dividends
         * @param pRate the rate to calculate at
         * @return the calculated value
         */
        public Money taxCreditAtRate(Rate pRate) {
            /* Calculate the Tax Credit corresponding to this net value at the rate */
            Ratio myRatio = new Ratio(pRate, pRate.getRemainingRate());
            Money myTotal = new Money(this, myRatio);

            /* Return value */
            return myTotal;
        }

        /**
         * calculate the value of this money at a given proportion (i.e. weight/total)
         * @param pWeight the weight of this item
         * @param pTotal the total weight of all the items
         * @return the calculated value
         */
        public Money valueAtWeight(Money pWeight,
                                   Money pTotal) {
            /* Handle zero total */
            if (!pTotal.isNonZero())
                return new Money(0);

            /* Calculate the defined ratio of this value */
            Ratio myRatio = new Ratio(pWeight, pTotal);
            Money myTotal = new Money(this, myRatio);

            /* Return value */
            return myTotal;
        }

        /**
         * calculate the value of this money at a given proportion (i.e. weight/total)
         * @param pWeight the weight of this item
         * @param pTotal the total weight of all the items
         * @return the calculated value
         */
        public Money valueAtWeight(Units pWeight,
                                   Units pTotal) {
            /* Handle zero total */
            if (!pTotal.isNonZero())
                return new Money(0);

            /* Calculate the defined ratio of this value */
            Ratio myRatio = new Ratio(pWeight, pTotal);
            Money myTotal = new Money(this, myRatio);

            /* Return value */
            return myTotal;
        }

        /**
         * Convert a whole number value to include decimals
         * @param pValue the whole number value
         * @return the converted value with added zeros
         */
        public static long convertToValue(long pValue) {
            /* Build in the decimals to the value */
            return adjustDecimals(pValue, NUMDEC);
        }
    }
}
