/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Provides classes to represent decimal numbers with fixed numbers of decimal digits
 * {@link #theScale} as Long integers. The decimal value is multiplied by 10 to the power of the
 * number of decimals for the number ({@link #theFactor}). The integral part of the number can be
 * expressed as (Value / Factor) and the fractional part as (Value % Factor). Arithmetic is then
 * performed as whole number arithmetic on these values, with due care taken on multiplication and
 * division to express the result to the correct number of decimals without losing any part of the
 * answer to overflow.
 */
public class TethysDecimal
        implements Comparable<TethysDecimal> {
    /**
     * The Decimal radix.
     */
    public static final int RADIX_TEN = 10;

    /**
     * The Maximum # of Decimals.
     */
    public static final int MAX_DECIMALS = 10;

    /**
     * Powers of Ten.
     */
    private static final long[] POWERS_OF_TEN = getPowersOfTen(MAX_DECIMALS);

    /**
     * The Shift factor to move top part of long to an integer.
     */
    private static final int INT_SHIFT = 32;

    /**
     * Out of range error text.
     */
    private static final String ERROR_RANGE = "Value out of range";

    /**
     * The unscaled value.
     */
    private long theValue;

    /**
     * The scale.
     */
    private int theScale;

    /**
     * The Decimal factor, used for isolating integral and fractional parts.
     */
    private long theFactor;

    /**
     * Standard constructor.
     */
    protected TethysDecimal() {
        theValue = 0;
        theScale = 0;
        theFactor = 1;
    }

    /**
     * Constructor.
     * @param pSource the decimal as a string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public TethysDecimal(final String pSource) {
        /* Parse the string */
        TethysDecimalParser.parseDecimalValue(pSource, this);

        /* Remove redundant decimals */
        reduceScale(0);
    }

    /**
     * Constructor.
     * @param pSource the decimal as a double
     */
    public TethysDecimal(final double pSource) {
        /* Convert to string and parse */
        this(Double.toString(pSource));
    }

    /**
     * Constructor.
     * @param pSource the source decimal
     */
    public TethysDecimal(final TethysDecimal pSource) {
        /* Copy value and scale */
        setValue(pSource.unscaledValue(), pSource.scale());
    }

    /**
     * Constructor.
     * @param pUnscaledValue the unscaled value
     * @param pScale the scale
     */
    public TethysDecimal(final long pUnscaledValue,
                         final int pScale) {
        /* Store value and scale */
        setValue(pUnscaledValue, pScale);
    }

    /**
     * Create the decimal from a byte array.
     * @param pBuffer the buffer
     */
    public TethysDecimal(final byte[] pBuffer) {
        if (pBuffer == null || pBuffer.length < Long.BYTES + 1) {
            throw new IllegalArgumentException();
        }
        final byte[] myValue = Arrays.copyOf(pBuffer, Long.BYTES);
        final long myUnscaled = TethysDataConverter.byteArrayToLong(myValue);
        final int myScale = pBuffer[Long.BYTES];
        setValue(myUnscaled, myScale);
    }

    /**
     * Obtain the unscaled value of the decimal.
     * @return the unscaled value
     */
    public long unscaledValue() {
        return theValue;
    }

    /**
     * Obtain the scale of the decimal.
     * @return the scale
     */
    public int scale() {
        return theScale;
    }

    /**
     * Set the value and scale.
     * @param pUnscaledValue the unscaled value
     * @param pScale the scale
     */
    protected final void setValue(final long pUnscaledValue,
                                  final int pScale) {
        /* Validate the scale */
        recordScale(pScale);

        /* Store value and scale */
        theValue = pUnscaledValue;
    }

    /**
     * Record the scale. The unscaled value is unchanged.
     * @param pScale the scale
     */
    protected final void recordScale(final int pScale) {
        /* Validate the scale */
        validateScale(pScale);

        /* Store scale */
        theScale = pScale;

        /* Calculate decimal factor */
        theFactor = getFactor(theScale);
    }

    /**
     * Adjust to scale.
     * @param pScale required scale
     */
    protected void adjustToScale(final int pScale) {
        /* If the scale is not correct */
        if (theScale != pScale) {
            /* Adjust the value appropriately */
            movePointLeft(pScale
                          - theScale);
        }
    }

    /**
     * Obtain factor.
     * @param pDecimals the number of decimals
     * @return the decimal part of the number
     */
    protected static long getFactor(final int pDecimals) {
        return POWERS_OF_TEN[pDecimals];
    }

    /**
     * Validate the scale.
     * @param pScale the scale
     */
    private static void validateScale(final int pScale) {
        /* Throw exception on invalid decimals */
        if ((pScale < 0)
            || (pScale > MAX_DECIMALS)) {
            throw new IllegalArgumentException("Decimals must be in the range 0 to "
                                               + MAX_DECIMALS);
        }
    }

    /**
     * Obtain integral part of number.
     * @return the integer part of the number
     */
    private long getIntegral() {
        return theValue
               / theFactor;
    }

    /**
     * Obtain fractional part of number.
     * @return the decimal part of the number
     */
    private long getFractional() {
        return theValue
               % theFactor;
    }

    /**
     * Determine whether we have a non-zero value.
     * @return <code>true</code> if the value is non-zero, <code>false</code> otherwise.
     */
    public boolean isNonZero() {
        return theValue != 0;
    }

    /**
     * Determine whether we have a zero value.
     * @return <code>true</code> if the value is zero, <code>false</code> otherwise.
     */
    public boolean isZero() {
        return theValue == 0;
    }

    /**
     * Determine whether we have a positive (or zero) value.
     * @return <code>true</code> if the value is non-negative, <code>false</code> otherwise.
     */
    public boolean isPositive() {
        return theValue >= 0;
    }

    /**
     * Negate the value.
     */
    public void negate() {
        theValue = -theValue;
    }

    /**
     * Set to zero value.
     */
    public void setZero() {
        theValue = 0;
    }

    /**
     * Returns the sign function.
     * @return -1, 0, or 1 as the value of this Decimal is negative, zero, or positive.
     */
    public int signum() {
        if (theValue == 0) {
            return 0;
        }
        return (theValue < 0)
                              ? -1
                              : 1;
    }

    /**
     * Reduce scale. Remove redundant zero digits in scale.
     * @param pDesiredScale the desired scale.
     */
    protected final void reduceScale(final int pDesiredScale) {
        /* While we have a large scale */
        while (theScale > pDesiredScale) {
            /* If we have relevant digits, break loop */
            if ((theValue % RADIX_TEN) != 0) {
                break;
            }

            /* Adjust the value appropriately */
            movePointRight(1);
        }
    }

    /**
     * Adjust a value to a different number of decimals.
     * <p>
     * If the adjustment is to reduce the number of decimals, the most significant digit of the
     * discarded digits is examined to determine whether to round up. If the number of decimals is
     * to be increased, zeros are simply added to the end.
     * @param pValue the value to adjust
     * @param iAdjust the adjustment (positive if # of decimals are to increase, negative if they
     * are to decrease)
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
                final long myFactor = getFactor(-(iAdjust + 1));

                /* Reduce to 10 times required value */
                myValue /= myFactor;
            }

            /* Access last digit */
            long myDigit = myValue
                             % RADIX_TEN;

            /* Handle negatiove values */
            int myAdjust = 1;
            if (myDigit < 0) {
                myAdjust = -1;
                myDigit = -myDigit;
            }

            /* Reduce final decimal and round up if required */
            myValue /= RADIX_TEN;
            if (myDigit >= (RADIX_TEN >> 1)) {
                myValue += myAdjust;
            }

            /* else if we need to expand fractional product */
        } else if (iAdjust > 0) {
            myValue *= getFactor(iAdjust);
        }

        /* Return the adjusted value */
        return myValue;
    }

    /**
     * Multiply two decimals together to produce a third.
     * <p>
     * This function splits each part of the multiplication into integral and fractional parts (a,b)
     * and (c,d). It then treats each factor as the sum of the two parts (a+b) etc. and calculates
     * the product as (a.c + a.d + b.c + b.d). To avoid losing significant digits at either end of
     * the calculation each partial product is split into integral and fractional parts. The
     * integers are summed together and the fractional parts are summed together at combined decimal
     * places of the two factors. Once all partial products have been calculated, the integral and
     * fractional totals are adjusted to the correct number of decimal places and combined. This
     * allows the multiplication to be built without risk of unnecessary arithmetic overflow.
     * @param pFirst the first factor
     * @param pSecond the second factor
     */
    protected void calculateProduct(final TethysDecimal pFirst,
                                    final TethysDecimal pSecond) {
        /* Access information about first factor */
        final long myIntFirst = pFirst.getIntegral();
        final long myFracFirst = pFirst.getFractional();
        final int myScaleFirst = pFirst.scale();

        /* Access information about second factor */
        final long myIntSecond = pSecond.getIntegral();
        final long myFracSecond = pSecond.getFractional();
        final int myScaleSecond = pSecond.scale();

        /*
         * Calculate (a.c) the integral part of the answer and initialise the fractional part (at
         * maxScale)
         */
        int maxScale = myScaleFirst
                       + myScaleSecond;
        long myIntegral = myIntFirst
                          * myIntSecond;
        long myFractional = 0;

        /* Calculate (a.d) (@myScaleSecond scale) and split off fractions */
        long myIntermediate = myIntFirst
                              * myFracSecond;
        long myFractions = myIntermediate
                           % getFactor(myScaleSecond);
        myIntermediate -= myFractions;
        myIntegral += adjustDecimals(myIntermediate, -myScaleSecond);
        myFractional += adjustDecimals(myFractions, maxScale
                                                    - myScaleSecond);

        /* Calculate (b.c) (@myScaleFirst scale) and split off fractions */
        myIntermediate = myIntSecond
                         * myFracFirst;
        myFractions = myIntermediate
                      % getFactor(myScaleFirst);
        myIntermediate -= myFractions;
        myIntegral += adjustDecimals(myIntermediate, -myScaleFirst);
        myFractional += adjustDecimals(myFractions, maxScale
                                                    - myScaleFirst);

        /* Calculate (b.d) (@maxScale scale) */
        myIntermediate = myFracFirst
                         * myFracSecond;
        myFractional += myIntermediate;

        /* If the maxScale is too large, reduce it */
        if (maxScale > MAX_DECIMALS) {
            /* Adjust the decimals */
            myFractional = adjustDecimals(myFractional, MAX_DECIMALS
                                                        - maxScale);

            /* Reduce maxScale */
            maxScale = MAX_DECIMALS;
        }

        /* Adjust and combine the two calculations */
        myIntegral = adjustDecimals(myIntegral, theScale);
        myFractional = adjustDecimals(myFractional, theScale
                                                    - maxScale);
        theValue = myIntegral
                   + myFractional;
    }

    /**
     * Divide a decimal by another decimal to produce a third.
     * <p>
     * The calculation can be written as
     * <code>x.10<sup>a</sup>/y.10<sup>b</sup> = (x/y).10<sup>a-b</sup> = z.10<sup>c</sup></code>.
     * <p>
     * where x is the unscaled dividend, y the unscaled divisor and z the unscaled result, and a,b,c
     * the relevant scales.
     * <p>
     * In order to avoid losing significant digits at either end of the calculation we calculate
     * (x/y) in integer arithmetic.
     * <p>
     * <code>x/y = m, x%y = n =&gt; x=my + n</code> where m and n are integers, and
     * <p>
     * <code>(x/y).10<sup>a-b</sup> = (my +n).10<sup>a-b</sup>/y = (m + (n/y)).10<sup>a-b</sup></code>
     * <p>
     * To obtain the result in the correct scale we find
     * <p>
     * <code>z.10<sup>c</sup> = m.10<sup>c-(a-b)</sup> + IntegralPart(n.10<sup>c-(a-b)</sup>/y)</code>
     * <p>
     * taking care to round the IntegralPart calculation correctly.
     * <p>
     * In the case where it is not possible to avoid overflow, the slower safeQuotient method is used.
     * @param pDividend the number to divide
     * @param pDivisor the number to divide
     */
    protected void calculateQuotient(final TethysDecimal pDividend,
                                     final TethysDecimal pDivisor) {
        /* Access the two values */
        final long myDividend = pDividend.unscaledValue();
        final long myDivisor = pDivisor.unscaledValue();

        /* Check for possible overflow */
        final int numDivisorBits = 1 + Long.SIZE - Long.numberOfLeadingZeros(pDivisor.isPositive() ? myDivisor : -myDivisor);
        final int numScaleBits = 1 + Long.SIZE - Long.numberOfLeadingZeros(POWERS_OF_TEN[theScale + 1]);
        if (numDivisorBits + numScaleBits >= Long.SIZE) {
            calculateSafeQuotient(pDividend, pDivisor);
            return;
        }

        /* Calculate fractions (m,n) */
        long myInteger = myDividend
                         / myDivisor;
        long myRemainder = myDividend
                           % myDivisor;

        /* Calculate the required shift (c-(a-b)) */
        int myShift = scale();
        myShift += pDivisor.scale()
                   - pDividend.scale();

        /* If the shift is positive */
        if (myShift > 0) {
            /* Adjust integer and remainder taking care of rounding for remainder */
            myInteger = adjustDecimals(myInteger, myShift);
            myRemainder = adjustDecimals(myRemainder, myShift + 1);
            myRemainder /= myDivisor;
            myRemainder = adjustDecimals(myRemainder, -1);

            /* Combine values */
            theValue = myInteger
                       + myRemainder;
        } else if (myShift == 0) {
            /* Only need to adjust remainder for rounding */
            myRemainder = adjustDecimals(myRemainder, 1);
            myRemainder /= myDivisor;
            myRemainder = adjustDecimals(myRemainder, -1);

            /* Combine values */
            theValue = myInteger
                       + myRemainder;
        } else {
            /* Integer value also rounds so add in prior to rounding */
            myInteger = adjustDecimals(myInteger, myShift + 1);
            myRemainder = adjustDecimals(myRemainder, myShift + 1);
            myRemainder /= myDivisor;
            myInteger += myRemainder;
            myInteger = adjustDecimals(myInteger, -1);

            /* Combine values */
            theValue = adjustDecimals(myInteger, -1);
        }
    }

    /**
     * Divide a decimal by another decimal to produce a third using slow BigDecimal arithmetic.
     * <p>
     * This is necessary when the quotient is large since there is a danger of overflow in the standard method
     * @param pDividend the number to divide
     * @param pDivisor the number to divide
     */
    protected void calculateSafeQuotient(final TethysDecimal pDividend,
                                         final TethysDecimal pDivisor) {
        final BigDecimal myDividend = pDividend.toBigDecimal();
        final BigDecimal myDivisor = pDivisor.toBigDecimal();
        BigDecimal myResult = myDividend.divide(myDivisor, theScale, RoundingMode.HALF_UP);
        myResult = myResult.movePointRight(theScale);
        theValue = myResult.longValue();
    }

    /**
     * Add a Decimal to the value. The value of this Decimal is updated and the scale is
     * maintained.
     * @param pValue The Decimal to add to this one.
     */
    public void addValue(final TethysDecimal pValue) {
        /* Access the parameter at the correct scale */
        long myDelta = pValue.unscaledValue();
        final int myScale = pValue.scale();
        if (theScale != myScale) {
            myDelta = adjustDecimals(myDelta, theScale
                                              - myScale);
        }

        /* Adjust the value accordingly */
        theValue += myDelta;
    }

    /**
     * Subtract a Decimal from the value. The value of this Decimal is updated and the scale is
     * maintained.
     * @param pValue The decimal to subtract from this one.
     */
    public void subtractValue(final TethysDecimal pValue) {
        /* Access the parameter at the correct scale */
        long myDelta = pValue.unscaledValue();
        final int myScale = pValue.scale();
        if (theScale != myScale) {
            myDelta = adjustDecimals(myDelta, theScale
                                              - myScale);
        }

        /* Adjust the value accordingly */
        theValue -= myDelta;
    }

    /**
     * Move decimal point to the left.
     * @param pPlaces number of places to move the decimal point
     */
    public final void movePointLeft(final int pPlaces) {
        /* Calculate the new scale */
        final int myNewScale = theScale
                               + pPlaces;

        /* record the scale */
        recordScale(myNewScale);

        /* Adjust the value and record the new scale */
        theValue = adjustDecimals(theValue, pPlaces);
    }

    /**
     * Move decimal point to the right.
     * @param pPlaces number of places to move the decimal point
     */
    public final void movePointRight(final int pPlaces) {
        /* Call movePointLeft */
        movePointLeft(-pPlaces);
    }

    @Override
    public String toString() {
        /* Format the value */
        return TethysDecimalFormatter.toString(this);
    }

    /**
     * Returns the maximum of this Decimal and pValue.
     * @param pValue the value to compare.
     * @return the Decimal whose value is the greater of this Decimal and pValue. If they are
     * equal, as defined by the compareTo method, this is returned
     */
    public TethysDecimal max(final TethysDecimal pValue) {
        /* return the BigDecimal value */
        return (compareTo(pValue) < 0)
                                       ? pValue
                                       : this;
    }

    /**
     * Returns the minimum of this Decimal and pValue.
     * @param pValue the value to compare.
     * @return the Decimal whose value is the lesser of this Decimal and pValue. If they are
     * equal, as defined by the compareTo method, this is returned
     */
    public TethysDecimal min(final TethysDecimal pValue) {
        /* return the BigDecimal value */
        return (compareTo(pValue) > 0)
                                       ? pValue
                                       : this;
    }

    /**
     * Returns a new Decimal which is the sum of this Decimal and pValue, and whose scale is the
     * maximum of the two.
     * @param pValue the value to add.
     * @return the resulting Decimal
     * @see BigDecimal#add
     */
    public TethysDecimal add(final TethysDecimal pValue) {
        /* Create the new decimal */
        final TethysDecimal myResult;

        /* If the operand has the higher scale */
        if (theScale < pValue.scale()) {
            /* Initialise from operand and add this value */
            myResult = new TethysDecimal(pValue);
            myResult.addValue(this);
        } else {
            /* Initialise from operand and add this value */
            myResult = new TethysDecimal(this);
            myResult.addValue(pValue);
        }

        /* return the result */
        return myResult;
    }

    /**
     * Returns a new Decimal which is the difference of this Decimal and pValue, and whose scale
     * is the maximum of the two.
     * @param pValue the value to subtract.
     * @return the resulting Decimal
     * @see BigDecimal#subtract
     */
    public TethysDecimal subtract(final TethysDecimal pValue) {
        /* Create the new decimal */
        final TethysDecimal myResult;

        /* If the operand has the higher scale */
        if (theScale < pValue.scale()) {
            /* Initialise from operand and subtract this value */
            myResult = new TethysDecimal(pValue);
            myResult.subtractValue(this);
        } else {
            /* Initialise from operand and subtract this value */
            myResult = new TethysDecimal(this);
            myResult.subtractValue(pValue);
        }

        /* return the result */
        return myResult;
    }

    /**
     * Returns a new Decimal which is the product of this Decimal and pValue, and whose scale is
     * the sum of the two.
     * @param pValue the value to multiply by.
     * @return the resulting Decimal
     * @see BigDecimal#multiply
     */
    public TethysDecimal multiply(final TethysDecimal pValue) {
        /* Create the new decimal at the correct scale */
        final TethysDecimal myResult = new TethysDecimal();
        myResult.setValue(0, theScale
                             + pValue.scale());

        /* Calculate the product */
        myResult.calculateProduct(this, pValue);

        /* return the result */
        return myResult;
    }

    /**
     * Multiplies the value by the amount given. The scale remains the same.
     * @param pValue the value to multiply by.
     */
    public void multiply(final long pValue) {
        /* Multiply the value */
        theValue *= pValue;
    }

    /**
     * Returns a new Decimal whose value is (this / pValue), and whose scale is the same as this
     * Decimal.
     * @param pValue the value to divide by.
     * @return the resulting Decimal
     * @see BigDecimal#divide
     */
    public TethysDecimal divide(final TethysDecimal pValue) {
        /* Create the new decimal at the correct scale */
        final TethysDecimal myResult = new TethysDecimal();
        myResult.setValue(0, theScale);

        /* Calculate the quotient */
        myResult.calculateQuotient(this, pValue);

        /* return the result */
        return myResult;
    }

    /**
     * Divides the value by the amount given. The scale remains the same.
     * @param pValue the value to divide by.
     */
    public void divide(final long pValue) {
        /* Multiply the value */
        theValue /= pValue;
    }

    /**
     * Returns a new Decimal whose value is the integral part of (this / pValue).
     * @param pValue the value to divide by.
     * @return the resulting Decimal
     * @see BigDecimal#divide
     */
    public TethysDecimal divideToIntegralValue(final TethysDecimal pValue) {
        /* Create the new decimal at the correct scale */
        final TethysDecimal myResult = new TethysDecimal();
        myResult.setValue(0, theScale);

        /* Calculate the quotient */
        myResult.calculateQuotient(this, pValue);

        /* Extract the integral part of the result */
        myResult.setValue(getIntegral(), 0);

        /* return the result */
        return myResult;
    }

    /**
     * Returns a new Decimal whose value is (this / pValue), and whose scale is the same as this
     * Decimal.
     * @param pValue the value to divide by.
     * @return the resulting Decimal
     * @see BigDecimal#remainder
     */
    public TethysDecimal remainder(final TethysDecimal pValue) {
        /* Create the new decimal at the correct scale */
        final TethysDecimal myQuotient = new TethysDecimal();
        myQuotient.setValue(0, theScale);

        /* Calculate the quotient */
        myQuotient.calculateQuotient(this, pValue);

        /* Extract the integral part of the result */
        myQuotient.setValue(getIntegral(), 0);

        /* Re-multiply by the divisor and adjust to correct scale */
        final TethysDecimal myWhole = myQuotient.multiply(pValue);
        myWhole.setValue(adjustDecimals(myWhole.unscaledValue(), theScale
                                                                 - pValue.scale()), theScale);

        /* Calculate the result */
        final TethysDecimal myResult = new TethysDecimal(this);
        myResult.subtractValue(myWhole);

        /* return the result */
        return myResult;
    }

    /**
     * Convert the value into a BigDecimal.
     * @return the value as a BigDecimal
     */
    public BigDecimal toBigDecimal() {
        /* return the BigDecimal value */
        return new BigDecimal(toString());
    }

    /**
     * Convert the value into a Double.
     * @return the value as a double
     * @see BigDecimal#doubleValue
     */
    public double doubleValue() {
        /* Format the string */
        final String myString = toString();

        /* return the double value */
        return Double.parseDouble(myString);
    }

    /**
     * Convert the value into a Float.
     * @return the value as a float
     * @see BigDecimal#floatValue
     */
    public float floatValue() {
        /* Format the string */
        final String myString = toString();

        /* return the float value */
        return Float.parseFloat(myString);
    }

    /**
     * Convert the value into a BigInteger.
     * @return the value as a BigInteger
     * @see BigDecimal#toBigInteger
     */
    public BigInteger toBigInteger() {
        /* return the BigInteger value */
        return new BigInteger(Long.toString(getIntegral()));
    }

    /**
     * Convert the value into a long.
     * @return the value as a long
     * @see BigDecimal#longValue
     */
    public long longValue() {
        /* return the long value */
        return getIntegral();
    }

    /**
     * Convert the value into an integer.
     * @return the value as an integer
     * @see BigDecimal#intValue
     */
    public int intValue() {
        /* return the integer value */
        return (int) getIntegral();
    }

    /**
     * Convert the value into a short.
     * @return the value as a short
     * @see BigDecimal#shortValue
     */
    public short shortValue() {
        /* return the short value */
        return (short) getIntegral();
    }

    /**
     * Convert the value into a byte.
     * @return the value as a byte
     * @see BigDecimal#byteValue
     */
    public byte byteValue() {
        /* return the byte value */
        return (byte) getIntegral();
    }

    /**
     * Check for fractional part on conversion.
     */
    public void checkFractionalZero() {
        /* If we have a fractional part */
        if (getFractional() != 0) {
            throw new ArithmeticException("Decimal has fractional part");
        }
    }

    /**
     * Convert the value into a BigInteger, checking for loss of information.
     * @return the value as a BigInteger
     * @see BigDecimal#toBigIntegerExact
     */
    public BigInteger toBigIntegerExact() {
        /* Check fractional is zero */
        checkFractionalZero();

        /* return the BigInteger value */
        return toBigInteger();
    }

    /**
     * Convert the value into a long, checking for loss of information.
     * @return the value as a long
     * @see BigDecimal#longValueExact
     */
    public long longValueExact() {
        /* Check fractional is zero */
        checkFractionalZero();

        /* return the long value */
        return longValue();
    }

    /**
     * Convert the value into an integer, checking for loss of information.
     * @return the value as an integer
     * @see BigDecimal#intValueExact
     */
    public int intValueExact() {
        /* Check fractional is zero */
        checkFractionalZero();

        /* If we have a fractional part */
        final long myValue = getIntegral();
        if ((myValue > Integer.MAX_VALUE)
            || (myValue < Integer.MIN_VALUE)) {
            throw new ArithmeticException(ERROR_RANGE);
        }

        /* return the integer value */
        return (int) myValue;
    }

    /**
     * Convert the value into a short, checking for loss of information.
     * @return the value as a short
     * @see BigDecimal#shortValueExact
     */
    public short shortValueExact() {
        /* Check fractional is zero */
        checkFractionalZero();

        /* If we have a fractional part */
        final long myValue = getIntegral();
        if ((myValue > Short.MAX_VALUE)
            || (myValue < Short.MIN_VALUE)) {
            throw new ArithmeticException(ERROR_RANGE);
        }

        /* return the short value */
        return (short) myValue;
    }

    /**
     * Convert the value into a byte, checking for loss of information.
     * @return the value as a byte
     * @see BigDecimal#byteValueExact
     */
    public byte byteValueExact() {
        /* Check fractional is zero */
        checkFractionalZero();

        /* If we have a fractional part */
        final long myValue = getIntegral();
        if ((myValue > Byte.MAX_VALUE)
            || (myValue < Byte.MIN_VALUE)) {
            throw new ArithmeticException(ERROR_RANGE);
        }

        /* return the byte value */
        return (byte) myValue;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Cast as decimal */
        final TethysDecimal myThat = (TethysDecimal) pThat;

        /* Check value and scale */
        return (theValue == myThat.theValue)
               && (theScale == myThat.theScale);
    }

    @Override
    public int hashCode() {
        return (int) (theValue ^ (theValue >>> INT_SHIFT))
               + theScale;
    }

    @Override
    public int compareTo(final TethysDecimal pThat) {
        /* Handle trivial case */
        if (this.equals(pThat)) {
            return 0;
        }

        /* If there is no difference in scale */
        final int myScaleDiff = scale()
                                - pThat.scale();
        if (myScaleDiff == 0) {
            /* Just compare unscaled value */
            if (theValue == pThat.theValue) {
                return 0;
            }
            return (theValue < pThat.theValue)
                                               ? -1
                                               : 1;
        }

        /* Compare integral values */
        long myDiff = getIntegral()
                      - pThat.getIntegral();
        if (myDiff != 0) {
            return (myDiff < 0)
                                ? -1
                                : 1;
        }

        /* Access fractional parts */
        long myFirst = getFractional();
        long mySecond = pThat.getFractional();

        /* Adjust to same maximum scale */
        if (myScaleDiff < 0) {
            myFirst = adjustDecimals(myFirst, -myScaleDiff);
        } else {
            mySecond = adjustDecimals(mySecond, myScaleDiff);
        }

        /* Compare fractional values */
        myDiff = myFirst
                 - mySecond;
        if (myDiff != 0) {
            return (myDiff < 0)
                                ? -1
                                : 1;
        }

        /* Equal to all intents and purposes */
        return 0;
    }

    /**
     * Build powers of ten.
     * @param pMax maximum power of ten
     * @return array of powers of ten
     */
    private static long[] getPowersOfTen(final int pMax) {
        /* Allocate the array */
        final long[] myArray = new long[pMax + 2];

        /* Initialise array */
        long myValue = 1;
        myArray[0] = myValue;

        /* Loop through array */
        for (int i = 1; i <= pMax + 1; i++) {
            /* Adjust value and record it */
            myValue *= RADIX_TEN;
            myArray[i] = myValue;
        }

        /* Return the array */
        return myArray;
    }

    /**
     * Convert the Decimal to a byte array.
     * @return the byte array
     */
    public byte[] toBytes() {
        final byte[] myValue = TethysDataConverter.longToByteArray(unscaledValue());
        final byte[] myResult = Arrays.copyOf(myValue, myValue.length + 1);
        myResult[myValue.length] = (byte) scale();
        return myResult;
    }
}
