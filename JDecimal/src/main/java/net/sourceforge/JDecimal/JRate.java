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

/**
 * Represents a Rate object.
 */
public class JRate extends JDecimal {
    /**
     * Standard number of decimals for Rate.
     */
    protected static final int NUM_DECIMALS = 4;

    /**
     * One hundred percent.
     */
    public static final JRate RATE_ONEHUNDREDPERCENT = getWholePercentage(100);

    /**
     * Construct a new Rate.
     */
    protected JRate() {
    }

    /**
     * Construct a new Rate by copying another rate.
     * @param pRate the Rate to copy
     */
    public JRate(final JRate pRate) {
        super(pRate.unscaledValue(), pRate.scale());
    }

    /**
     * Constructor for rate from a decimal string.
     * @param pSource The source decimal string
     */
    public JRate(final String pSource) {
        /* Parse the string and correct the scale */
        JDecimalParser.parseDecimalValue(pSource.trim(), this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Rate by setting the value explicitly.
     * @param pValue the unscaled value
     * @return the new Rate
     */
    public static JRate getWholePercentage(final long pValue) {
        JRate myRate = new JRate();
        myRate.setValue(adjustDecimals(pValue, NUM_DECIMALS - JDecimalParser.ADJUST_PERCENT), NUM_DECIMALS);
        return myRate;
    }

    /**
     * Obtain remaining rate of this rate (i.e. 100% - this rate).
     * @return the remaining rate
     */
    public JRate getRemainingRate() {
        /* Create a negated copy of this rate */
        JRate myRate = new JRate(this);
        myRate.negate();

        /* Add one hundred percent and return the result */
        myRate.addValue(RATE_ONEHUNDREDPERCENT);
        return myRate;
    }

    /**
     * Obtain inverse ratio of this rate (i.e. 100%/this rate).
     * @return the inverse ratio
     */
    public JRatio getInverseRatio() {
        return new JRatio(RATE_ONEHUNDREDPERCENT, this);
    }

    @Override
    public void addValue(final JDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final JDecimal pValue) {
        throw new UnsupportedOperationException();
    }
}