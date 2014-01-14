/*******************************************************************************
 * jDecimal: Decimals represented by long values
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jdecimal;

/**
 * Represents a Dilution object. Many shares are quoted with a diluted price which must be rectified by a dilution factor to obtain the correct price. This
 * object represents such a dilution factor
 */
public class JDilution
        extends JDecimal {
    /**
     * Standard number of decimals for Dilution.
     */
    protected static final int NUM_DECIMALS = 6;

    /**
     * Define the maximum dilution value.
     */
    public static final JDilution MAX_DILUTION = new JDilution(1);

    /**
     * Define the minimum dilution value.
     */
    public static final JDilution MIN_DILUTION = new JDilution(0);

    /**
     * Construct a new Dilution.
     */
    protected JDilution() {
    }

    /**
     * Construct a new Dilution from an integral number.
     * @param pValue the dilution value to correct number of places
     */
    protected JDilution(final long pValue) {
        setValue(pValue, 0);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Dilution by copying another dilution.
     * @param pDilution the Dilution to copy
     */
    public JDilution(final JDilution pDilution) {
        super(pDilution.unscaledValue(), pDilution.scale());
    }

    /**
     * Constructor for dilution from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public JDilution(final String pSource) {
        /* Parse the string and correct the scale */
        JDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Dilution by combining two dilution factors.
     * @param pFirst the first Dilution factor
     * @param pSecond the second Dilution factor
     */
    protected JDilution(final JDilution pFirst,
                        final JDilution pSecond) {
        recordScale(NUM_DECIMALS);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Is the dilution factor outside the valid range.
     * @return true/false
     */
    public final boolean outOfRange() {
        return (compareTo(MIN_DILUTION) < 0)
               || (compareTo(MAX_DILUTION) > 0);
    }

    @Override
    public void addValue(final JDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final JDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * obtain a further dilution.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public JDilution getFurtherDilution(final JDilution pDilution) {
        /* Calculate the new dilution */
        return new JDilution(this, pDilution);
    }

    /**
     * obtain inverse ratio.
     * @return the inverse ratio
     */
    public JRatio getInverseRatio() {
        /* Calculate the new dilution */
        JRatio myRatio = new JRatio(this);
        return myRatio.getInverseRatio();
    }
}
