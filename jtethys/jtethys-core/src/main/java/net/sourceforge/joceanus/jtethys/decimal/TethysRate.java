/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

/**
 * Represents a Rate object.
 */
public class TethysRate
        extends TethysDecimal {
    /**
     * Standard number of decimals for Rate.
     */
    protected static final int NUM_DECIMALS = 4;

    /**
     * One hundred percent.
     */
    public static final TethysRate RATE_ONEHUNDREDPERCENT = getWholePercentage(100);

    /**
     * Construct a new Rate.
     */
    protected TethysRate() {
    }

    /**
     * Construct a new Rate by copying another rate.
     * @param pRate the Rate to copy
     */
    public TethysRate(final TethysRate pRate) {
        super(pRate.unscaledValue(), pRate.scale());
    }

    /**
     * Construct a new Ratio by the ratio between two decimals.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     */
    public TethysRate(final TethysDecimal pFirst,
                      final TethysDecimal pSecond) {
        recordScale(NUM_DECIMALS);
        calculateQuotient(pFirst, pSecond);
    }

    /**
     * Construct a new Rate from a ratio.
     * @param pRatio the Ratio
     */
    public TethysRate(final TethysRatio pRatio) {
        super(pRatio.unscaledValue(), pRatio.scale());
        adjustToScale(NUM_DECIMALS);
        super.subtractValue(RATE_ONEHUNDREDPERCENT);
    }

    /**
     * Constructor for rate from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public TethysRate(final String pSource) {
        /* Parse the string and correct the scale */
        TethysDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value of a whole percentage (e.g. 2 = 2%)
     * @return the new Rate
     */
    public static TethysRate getWholePercentage(final long pValue) {
        final TethysRate myRate = new TethysRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS
                                               - TethysDecimalParser.ADJUST_PERCENT), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value of a whole percentage (e.g. 25 = 2.5%)
     * @return the new Rate
     */
    public static TethysRate getWholePermille(final long pValue) {
        final TethysRate myRate = new TethysRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS
                                               - TethysDecimalParser.ADJUST_PERMILLE), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Obtain remaining rate of this rate (i.e. 100% - this rate).
     * @return the remaining rate
     */
    public TethysRate getRemainingRate() {
        /* Create a copy of this rate and reverse it */
        final TethysRate myRate = new TethysRate(this);
        myRate.reverseRate();
        return myRate;
    }

    /**
     * Obtain remaining rate of this rate (i.e. 100% - this rate).
     */
    private void reverseRate() {
        /* Negate the value and add 100% */
        negate();
        super.addValue(RATE_ONEHUNDREDPERCENT);
    }

    /**
     * Obtain inverse ratio of this rate (i.e. 100%/this rate).
     * @return the inverse ratio
     */
    public TethysRatio getInverseRatio() {
        return new TethysRatio(RATE_ONEHUNDREDPERCENT, this);
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
