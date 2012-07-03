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
 * Represents a Units object.
 */
public class Units extends Decimal {
    /**
     * Units have four decimal points.
     */
    public static final int NUMDEC = 4;

    /**
     * Access the value of the Units.
     * @return the value
     */
    public long getUnits() {
        return getValue();
    }

    /**
     * Add units to the value.
     * @param pUnits The units to add to this one.
     */
    public void addUnits(final Units pUnits) {
        addValue(pUnits);
    }

    /**
     * Subtract units from the value.
     * @param pUnits The units to subtract from this one.
     */
    public void subtractUnits(final Units pUnits) {
        subtractValue(pUnits);
    }

    /**
     * Construct a new Units from a value.
     * @param uUnits the value
     */
    public Units(final long uUnits) {
        super(NUMDEC);
        setValue(uUnits);
    }

    /**
     * Construct a new Units by copying another units.
     * @param pUnits the units to copy
     */
    public Units(final Units pUnits) {
        super(NUMDEC);
        setValue(pUnits.getUnits());
    }

    /**
     * Construct a new Units by parsing a string value.
     * @param pUnits the Units to parse
     */
    public Units(final String pUnits) {
        super(NUMDEC);
        parseStringValue(pUnits);
    }

    @Override
    public String format(final boolean bPretty) {
        StringBuilder myFormat = formatNumber(bPretty);
        return myFormat.toString();
    }

    /**
     * calculate the value of these units at a given price.
     * @param pPrice the per unit price
     * @return the calculated value
     */
    public Money valueAtPrice(final Price pPrice) {
        /* Calculate value of units */
        return new Money(this, pPrice);
    }

    /**
     * Create a new Units by parsing a string value.
     * @param pUnits the Units to parse
     * @return the new Units or <code>null</code> if parsing failed
     */
    public static Units parseString(final String pUnits) {
        try {
            return new Units(pUnits);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
