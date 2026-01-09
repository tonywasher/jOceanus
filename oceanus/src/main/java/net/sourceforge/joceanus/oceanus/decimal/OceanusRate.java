/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012-2026 Tony Washer
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

/**
 * Represents a Rate object.
 */
public class OceanusRate
        extends OceanusDecimal {
    /**
     * Standard number of decimals for Rate.
     */
    protected static final int NUM_DECIMALS = 4;

    /**
     * One hundred percent.
     */
    public static final OceanusRate RATE_ONEHUNDREDPERCENT = getWholePercentage(100);

    /**
     * Construct a new Rate.
     */
    protected OceanusRate() {
    }

    /**
     * Construct a new Rate by copying another rate.
     * @param pRate the Rate to copy
     */
    public OceanusRate(final OceanusRate pRate) {
        super(pRate.unscaledValue(), pRate.scale());
    }

    /**
     * Construct a new Ratio by the ratio between two decimals.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     */
    public OceanusRate(final OceanusDecimal pFirst,
                       final OceanusDecimal pSecond) {
        recordScale(NUM_DECIMALS);
        calculateQuotient(pFirst, pSecond);
    }

    /**
     * Construct a new Rate from a ratio.
     * @param pRatio the Ratio
     */
    public OceanusRate(final OceanusRatio pRatio) {
        super(pRatio.unscaledValue(), pRatio.scale());
        adjustToScale(NUM_DECIMALS);
        super.subtractValue(RATE_ONEHUNDREDPERCENT);
    }

    /**
     * Constructor for rate from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public OceanusRate(final String pSource) {
        /* Parse the string and correct the scale */
        OceanusDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Create the rate from a byte array.
     * @param pBuffer the buffer
     */
    public OceanusRate(final byte[] pBuffer) {
        super(pBuffer);
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value of a whole percentage (e.g. 2 = 2%)
     * @return the new Rate
     */
    public static OceanusRate getWholePercentage(final long pValue) {
        final OceanusRate myRate = new OceanusRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS
                                               - OceanusDecimalParser.ADJUST_PERCENT), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value of a whole percentage (e.g. 25 = 2.5%)
     * @return the new Rate
     */
    public static OceanusRate getWholePermille(final long pValue) {
        final OceanusRate myRate = new OceanusRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS
                                               - OceanusDecimalParser.ADJUST_PERMILLE), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value of a whole percentage (e.g. 25 = 0.25%)
     * @return the new Rate
     */
    public static OceanusRate getTenthPermille(final long pValue) {
        final OceanusRate myRate = new OceanusRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS
                - OceanusDecimalParser.ADJUST_PERMILLE - 1), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Obtain remaining rate of this rate (i.e. 100% - this rate).
     * @return the remaining rate
     */
    public OceanusRate getRemainingRate() {
        /* Create a copy of this rate and reverse it */
        final OceanusRate myRate = new OceanusRate(this);
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
    public OceanusRatio getInverseRatio() {
        return new OceanusRatio(RATE_ONEHUNDREDPERCENT, this);
    }

    @Override
    public void addValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }
}
