/*
 * Oceanus: Java Utilities
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Provides classes to represent decimal numbers with fixed numbers of decimal digits
 * {@link #theScale} as Long integers. The decimal value is multiplied by 10 to the power of the
 * number of decimals for the number ({@link #theFactor}). The integral part of the number can be
 * expressed as (Value / Factor) and the fractional part as (Value % Factor). Arithmetic is then
 * performed as whole number arithmetic on these values, with due care taken on multiplication and
 * division to express the result to the correct number of decimals without losing any part of the
 * answer to overflow.
 */
package io.github.tonywasher.joceanus.oceanus.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decimal class performing integer arithmetic on large decimals.
 */
public class OceanusNewDecimal {
    /**
     * The Decimal radix.
     */
    private static final int RADIX_TEN = 10;

    /**
     * The Maximum # of Decimals.
     */
    public static final int MAX_DECIMALS = 9;

    /**
     * The Integer boost.
     */
    private static final long INTEGER_BOOST = 0x100000000L;

    /**
     * The Integer mask.
     */
    private static final long INTEGER_MASK = 0xFFFFFFFFL;

    /**
     * Powers of Ten.
     */
    private static final int[] POWERS_OF_TEN = getPowersOfTen(MAX_DECIMALS);

    /**
     * The number of decimal digits.
     */
    private final int theScale;

    /**
     * The Decimal factor, used for rounding fractional parts.
     */
    private final int theFactor;

    /**
     * sign.
     */
    private int theSign;

    /**
     * Positive Integral part.
     */
    private long theIntegral;

    /**
     * Positive Fractional part.
     */
    private int theFractional;

    /**
     * Constructor.
     *
     * @param pScale the number of decimal digits
     */
    public OceanusNewDecimal(final int pScale) {
        this(0, 0, 0, pScale);
    }

    /**
     * Constructor.
     *
     * @param pSource the source BigDecimal
     */
    public OceanusNewDecimal(final BigDecimal pSource) {
        /* Store sign and scale */
        theSign = pSource.signum();
        theScale = pSource.scale();
        checkValidScale(theScale);
        theFactor = getFactor(theScale);

        /* Extract the integral and fractional parts */
        theIntegral = pSource.longValue();
        theIntegral *= theSign;
        long myFractional = pSource.movePointRight(theScale).longValue() * theSign;
        myFractional %= theFactor;
        theFractional = (int) myFractional;
    }

    /**
     * Constructor.
     *
     * @param pIntegral   the integral part of the decimal.
     * @param pFractional the fractional part of the decimal
     * @param pSign       the sign of the decimal
     * @param pScale      the number of decimal digits
     */
    public OceanusNewDecimal(final long pIntegral,
                             final int pFractional,
                             final int pSign,
                             final int pScale) {
        /* Check that scale if valid */
        checkValidScale(pScale);

        /* Store details */
        theIntegral = pIntegral;
        theFractional = pFractional;
        theSign = pSign;
        theScale = pScale;
        theFactor = getFactor(theScale);
    }

    /**
     * Check that the scale is valid.
     *
     * @param pScale the scale
     */
    private static void checkValidScale(final int pScale) {
        if (pScale < 0 || pScale > MAX_DECIMALS) {
            throw new IllegalArgumentException("Invalid scale - " + pScale);
        }
    }

    /**
     * Obtain the integral part of the decimal.
     *
     * @return the integral part of the decimal
     */
    public long integralValue() {
        return theIntegral * theSign;
    }

    /**
     * Obtain the sign of the decimal.
     *
     * @return -1, 0, or 1 as the value of this Decimal is negative, zero, or positive
     */
    public int signum() {
        return theSign;
    }

    /**
     * Obtain the fractional part of the decimal.
     *
     * @return the fractional part of the decimal
     */
    public int fractionalValue() {
        return theFractional * theSign;
    }

    /**
     * Obtain the scale of the decimal.
     *
     * @return the scale of the decimal
     */
    public int scale() {
        return theScale;
    }

    /**
     * Add a decimal to value.
     *
     * @param pDecimal the decimal to add to this value
     */
    public void add(final OceanusNewDecimal pDecimal) {
        /* Access the integral/fractional part of the second decimal at the same scale */
        long myIntegral = pDecimal.theIntegral;
        long myFractional = adjustDecimals(pDecimal.theFractional, theScale - pDecimal.theScale);
        if (myFractional >= theFactor) {
            myFractional %= theFactor;
            myIntegral++;
        }
        add(myIntegral, (int) myFractional);
    }

    /**
     * Subtract a decimal from value.
     *
     * @param pDecimal the decimal to subtract from this value
     */
    public void subtract(final OceanusNewDecimal pDecimal) {
        /* Access the integral/fractional part of the second decimal at the same scale */
        long myIntegral = pDecimal.theIntegral;
        long myFractional = adjustDecimals(pDecimal.theFractional, theScale - pDecimal.theScale);
        if (myFractional >= theFactor) {
            myFractional %= theFactor;
            myIntegral++;
        }
        add(-myIntegral, (int) -myFractional);
    }

    /**
     * Add a decimal to this value.
     *
     * @param pIntegral   the integral part of the decimal.
     * @param pFractional the fractional part of the decimal
     */
    private void add(final long pIntegral,
                     final int pFractional) {
        /* Add the fractional and non-fractional parts of the sum */
        theFractional = pFractional + fractionalValue();
        theIntegral = pIntegral + integralValue();

        /* If we have a positive integral # */
        if (theIntegral > 0) {
            /* Handle fractional too small */
            if (theFractional < 0) {
                theFractional += theFactor;
                theIntegral--;

                /* Handle fractional too large */
            } else if (theFractional >= theFactor) {
                theFractional -= theFactor;
                theIntegral++;
            }

            /* Set sign */
            theSign = 1;

            /* If we have a negative integral # */
        } else if (theIntegral < 0) {
            /* Handle fractional too large */
            if (theFractional > 0) {
                theFractional -= theFactor;
                theIntegral++;

                /* Handle fractional too small */
            } else if (theFractional <= -theFactor) {
                theFractional += theFactor;
                theIntegral--;
            }

            /* Set sign */
            theSign = -1;
            theFractional = -theFractional;
            theIntegral = -theIntegral;

            /* else we have a zero integral */
        } else {
            /* Handle fractional too large */
            if (theFractional >= theFactor) {
                theFractional -= theFactor;
                theIntegral = 1;
                theSign = 1;

                /* Handle Fractional too small */
            } else if (theFractional <= -theFactor) {
                theFractional += theFactor;
                theSign = -1;
                theIntegral = 1;
                theFractional = -theFractional;

                /* Handle positive fractional */
            } else if (theFractional > 0) {
                theSign = 1;

                /* Handle negative fractional */
            } else if (theFractional < 0) {
                theSign = -1;
                theFractional = -theFractional;

                /* Handle zero fractional */
            } else {
                theSign = 0;
            }
        }
    }

    /**
     * Multiply by another decimal
     * <p>
     * This function splits the values into three separate integers and then performs long arithmetic to
     * prevent loss of precision. The value is represented as (x,y,z,s) where the decimal may be written as
     * x*2<sup>32</sup> + y + z*10<sup>-s</sup> and x,y,z,s are all integers.
     * <p>
     * The product of (x<sub>1</sub>, y<sub>1</sub>, z<sub>1</sub>, s) by
     * (x<sub>2</sub>, y<sub>2</sub>, z<sub>2</sub>, t)
     * is therefore x<sub>1</sub>*x<sub>2</sub>*2<sup>64</sup> (discardable)
     * + (x<sub>1</sub>*y<sub>2</sub> + x<sub>2</sub>*y<sub>1</sub>)*2<sup>32</sup>
     * + x<sub>2</sub>*y<sub>2</sub>
     * + x<sub>1</sub>*z<sub>2</sub>*2<sup>32</sup>*10<sup>-t</sup>
     * + x<sub>2</sub>*z<sub>1</sub>*2<sup>32</sup>*10<sup>-s</sup>
     * + y<sub>1</sub>*z<sub>2</sub>*10<sup>-t</sup>
     * + y<sub>2</sub>*z<sub>1</sub>*10<sup>-s</sup>
     * + z<sub>1</sub>*z<sub>2</sub>*10<sup>-s-t</sup>
     *
     * @param pMultiplicand the decimal to multiply by
     */
    public void multiply(final OceanusNewDecimal pMultiplicand) {
        /* Access the parts of this value */
        final long myX1 = theIntegral >>> Integer.SIZE;
        final long myY1 = theIntegral & INTEGER_MASK;
        final long myZ1 = theFractional;
        final int myS = theScale;
        final int mySFactor = theFactor;

        /* Access the parts of the multiplicand */
        final long myX2 = pMultiplicand.theIntegral >>> Integer.SIZE;
        final long myY2 = pMultiplicand.theIntegral & INTEGER_MASK;
        final long myZ2 = pMultiplicand.theFractional;
        final int myT = pMultiplicand.theScale;
        final int myTFactor = pMultiplicand.theFactor;
        final long mySTFactor = mySFactor * (long) myTFactor;

        /* Calculate integral products */
        long myIntegral = ((myX1 * myY2) + (myY1 * myX2)) << Integer.SIZE;
        myIntegral += myY2 * myX2;

        /* Calculate product of X2 and Z1 and multiply by 2^32 */
        long myProduct = myX2 * myZ1;
        long myIntPart = myProduct / mySFactor;
        long myFracPart = myProduct % mySFactor;
        myIntegral += myIntPart << Integer.SIZE;
        myFracPart *= INTEGER_BOOST;
        myIntegral += myFracPart / mySFactor;
        long myFractional = adjustDecimals(myFracPart % mySFactor, myT);

        /* Calculate products of Y2 and Z1 */
        myProduct = myY2 * myZ1;
        myIntegral += myProduct / mySFactor;
        myFractional += adjustDecimals(myProduct % mySFactor, myT);

        /* Calculate product of X1 and Z2 and multiply by 2^32 */
        myProduct = myX1 * myZ2;
        myIntPart = myProduct / myTFactor;
        myFracPart = myProduct % myTFactor;
        myIntegral += myIntPart << Integer.SIZE;
        myFracPart *= INTEGER_BOOST;
        myIntegral += myFracPart / myTFactor;
        myFractional += adjustDecimals(myFracPart % myTFactor, myS);

        /* Calculate products of Y1 and Z2 */
        myProduct = myY1 * myZ2;
        myIntegral += myProduct / myTFactor;
        myFractional += adjustDecimals(myProduct % myTFactor, myS);

        /* Calculate products of Z1 and Z2 */
        myFractional += myZ1 * myZ2;

        /* Handle wrap of fractional */
        myIntegral += myFracPart / mySTFactor;
        myFractional %= mySTFactor;

        /* Adjust decimals */
        myFractional = adjustDecimals(myFractional, -myT);
        if (myFractional >= mySFactor) {
            myFractional %= mySFactor;
            myIntegral++;
        }

        /* Store the result */
        theIntegral = myIntegral;
        theFractional = (int) myFractional;
        theSign *= pMultiplicand.theSign;
    }

    /**
     * Divide by another decimal.
     * <p>
     * This function uses BigDecimal to perform the calculation
     *
     * @param pDivisor the decimal to divide by
     */
    public void divide(final OceanusNewDecimal pDivisor) {
        /* Calculate the result */
        final BigDecimal myNumerator = toBigDecimal();
        final BigDecimal myDenominator = pDivisor.toBigDecimal();
        final BigDecimal myResult = myNumerator.divide(myDenominator, theScale, RoundingMode.HALF_UP);

        /* Extract the integral and fractional parts */
        theSign = myResult.signum();
        theIntegral = myResult.longValue();
        theIntegral *= theSign;
        long myFractional = myResult.movePointRight(theScale).longValue() * theSign;
        myFractional %= theFactor;
        theFractional = (int) myFractional;
    }

    /**
     * Convert to BigDecimal.
     *
     * @return the BigDecimal equivalent
     */
    public BigDecimal toBigDecimal() {
        final BigDecimal myIntegral = new BigDecimal(theIntegral);
        final BigDecimal myFractional = new BigDecimal(theFractional).movePointLeft(theScale);
        final BigDecimal myResult = myIntegral.add(myFractional);
        return theSign == -1 ? myResult.negate() : myResult;
    }

    @Override
    public String toString() {
        /* Format the string */
        final StringBuilder myString = new StringBuilder(100);
        if (theSign == -1) {
            myString.append('-');
        }
        myString.append(theIntegral);
        if (theScale > 0) {
            final int myLen = myString.length();
            myString.append(theFractional + theFactor);
            myString.setCharAt(myLen, '.');
        }

        /* Return the string */
        return myString.toString();
    }

    /**
     * Build powers of ten.
     *
     * @param pMax maximum power of ten
     * @return array of powers of ten
     */
    private static int[] getPowersOfTen(final int pMax) {
        /* Allocate the array */
        final int[] myArray = new int[pMax + 1];

        /* Initialise array */
        int myValue = 1;
        myArray[0] = myValue;

        /* Loop through array */
        for (int i = 1; i < pMax + 1; i++) {
            /* Adjust value and record it */
            myValue *= RADIX_TEN;
            myArray[i] = myValue;
        }

        /* Return the array */
        return myArray;
    }

    /**
     * Obtain factor.
     *
     * @param pDecimals the number of decimals
     * @return the decimal part of the number
     */
    private static int getFactor(final int pDecimals) {
        return POWERS_OF_TEN[pDecimals];
    }

    /**
     * Adjust a value to a different number of decimals.
     * <p>
     * If the adjustment is to reduce the number of decimals, the most significant digit of the
     * discarded digits is examined to determine whether to round up. If the number of decimals is
     * to be increased, zeros are simply added to the end.
     *
     * @param pValue  the value to adjust
     * @param iAdjust the adjustment (positive if # of decimals are to increase, negative if they
     *                are to decrease)
     * @return the adjusted value
     */
    private static long adjustDecimals(final long pValue,
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

            /* Handle negative values */
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
}
