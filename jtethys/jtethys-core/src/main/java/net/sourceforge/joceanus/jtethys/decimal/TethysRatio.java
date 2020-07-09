/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
import java.math.RoundingMode;

/**
 * Represents a Ratio object.
 */
public class TethysRatio
        extends TethysDecimal {
    /**
     * Standard number of decimals for Ratio.
     */
    protected static final int NUM_DECIMALS = 6;

    /**
     * Ratio of one.
     */
    public static final TethysRatio ONE = new TethysRatio("1");

    /**
     * Number of days in a standard year.
     */
    private static final int DAYS_IN_YEAR = 365;

    /**
     * Constructor.
     */
    protected TethysRatio() {
    }

    /**
     * Construct a new Ratio by copying another ratio.
     * @param pRatio the Ratio to copy
     */
    public TethysRatio(final TethysRatio pRatio) {
        super(pRatio.unscaledValue(), pRatio.scale());
    }

    /**
     * Construct a ratio from a dilution.
     * @param pDilution the dilution
     */
    protected TethysRatio(final TethysDilution pDilution) {
        super(pDilution.unscaledValue(), pDilution.scale());
    }

    /**
     * Constructor for ratio from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public TethysRatio(final String pSource) {
        /* Parse the string and correct the scale */
        TethysDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Ratio by the ratio between two decimals.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     */
    public TethysRatio(final TethysDecimal pFirst,
                       final TethysDecimal pSecond) {
        final BigDecimal myFirst = pFirst.toBigDecimal();
        final BigDecimal mySecond = pSecond.toBigDecimal();
        BigDecimal myResult = myFirst.divide(mySecond, NUM_DECIMALS, RoundingMode.HALF_UP);
        myResult = myResult.movePointRight(NUM_DECIMALS);
        setValue(myResult.longValue(), NUM_DECIMALS);
    }

    /**
     * Obtain inverse ratio of this ratio (i.e. 100%/this rate).
     * @return the inverse ratio
     */
    public TethysRatio getInverseRatio() {
        return new TethysRatio(TethysRate.RATE_ONEHUNDREDPERCENT, this);
    }

    /**
     * Multiply by ratio.
     * @param pRatio the multiplying ratio
     * @return the new ratio
     */
    public TethysRatio multiplyBy(final TethysRatio pRatio) {
        final TethysRatio myRatio = new TethysRatio();
        myRatio.recordScale(NUM_DECIMALS);
        myRatio.calculateProduct(this, pRatio);
        return myRatio;
    }

    /**
     * Obtain annualised ratio.
     * @param pDays the number of days in the period
     * @return the annualised ratio
     */
    public TethysRatio annualise(final int pDays) {
        /* Calculate the annualised value and convert to ratio */
        final double myValue = Math.pow(doubleValue(), ((double) DAYS_IN_YEAR) / pDays);
        return new TethysRatio(Double.toString(myValue));
    }

    @Override
    public void addValue(final TethysDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final TethysDecimal pValue) {
        throw new UnsupportedOperationException();
    }
}
