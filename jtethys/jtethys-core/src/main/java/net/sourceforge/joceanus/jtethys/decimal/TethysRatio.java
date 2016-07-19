/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.decimal;

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
        recordScale(NUM_DECIMALS);
        calculateQuotient(pFirst, pSecond);
    }

    /**
     * Obtain inverse rate of this rate (i.e. 100%/this rate).
     * @return the remaining rate
     */
    public TethysRatio getInverseRatio() {
        return new TethysRatio(TethysRate.RATE_ONEHUNDREDPERCENT, this);
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
