/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.decimal;

/**
 * Represents a Units object.
 */
public class JUnits
        extends JDecimal {
    /**
     * Singular unit.
     */
    protected static final int NUM_DECIMALS = 4;

    /**
     * Construct a new Units.
     */
    public JUnits() {
        recordScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Units by copying another units.
     * @param pUnits the Units to copy
     */
    public JUnits(final JUnits pUnits) {
        super(pUnits.unscaledValue(), pUnits.scale());
    }

    /**
     * Constructor for units from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public JUnits(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        JDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Units by setting the value explicitly.
     * @param pValue the unscaled value
     * @return the new Rate
     */
    public static JUnits getWholeUnits(final long pValue) {
        JUnits myUnits = new JUnits();
        myUnits.setValue(adjustDecimals(pValue, NUM_DECIMALS), NUM_DECIMALS);
        return myUnits;
    }

    /**
     * Add units to the value.
     * @param pValue The units to add to this one.
     */
    public void addUnits(final JUnits pValue) {
        /* Add the value */
        super.addValue(pValue);
    }

    /**
     * Subtract units from the value.
     * @param pValue The units to subtract from this one.
     */
    public void subtractUnits(final JUnits pValue) {
        /* Subtract the value */
        super.subtractValue(pValue);
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
     * calculate the value of these units at a given price.
     * @param pPrice the per unit price
     * @return the calculated value
     */
    public JMoney valueAtPrice(final JPrice pPrice) {
        /* Calculate value of units */
        return new JMoney(this, pPrice);
    }
}
