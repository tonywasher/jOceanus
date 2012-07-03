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
 * Represents a Dilution object. Many shares are quoted with a diluted price which must be rectified by a
 * dilution factor to obtain the correct price. This object represents such a dilution factor
 */
public class Dilution extends Decimal {
    /**
     * Dilutions have six decimal points.
     */
    public static final int NUMDEC = 6;

    /**
     * Define the maximum dilution value.
     */
    public static final int MAX_VALUE = 1000000;

    /**
     * Define the minimum dilution value.
     */
    public static final int MIN_VALUE = 0;

    /**
     * Access the value of the Dilution.
     * @return the value
     */
    public long getDilution() {
        return getValue();
    }

    /**
     * Construct a new Dilution from a value.
     * @param uDilution the value
     */
    public Dilution(final long uDilution) {
        super(NUMDEC);
        setValue(uDilution);
    }

    /**
     * Construct a new Dilution by copying another dilution.
     * @param pDilution the Dilution to copy
     */
    public Dilution(final Dilution pDilution) {
        super(NUMDEC);
        setValue(pDilution.getDilution());
    }

    /**
     * Construct a new Dilution by combining two dilution factors.
     * @param pFirst the first Dilution factor
     * @param pSecond the second Dilution factor
     */
    public Dilution(final Dilution pFirst,
                    final Dilution pSecond) {
        super(NUMDEC);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new Dilution by parsing a string value.
     * @param pDilution the Dilution to parse
     */
    public Dilution(final String pDilution) {
        super(NUMDEC);
        parseStringValue(pDilution);
        if (outOfRange()) {
            throw new IllegalArgumentException("Dilution value invalid :" + pDilution);
        }
    }

    /**
     * obtain a further dilution.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public Dilution getFurtherDilution(final Dilution pDilution) {
        /* Calculate the new dilution */
        return new Dilution(this, pDilution);
    }

    /**
     * Is the dilution factor outside the valid range.
     * @return true/false
     */
    public final boolean outOfRange() {
        return ((getValue() > MAX_VALUE) || (getValue() < MIN_VALUE));
    }

    @Override
    public String format(final boolean bPretty) {
        StringBuilder myFormat = formatNumber(bPretty);
        return myFormat.toString();
    }

    /**
     * Create a new Dilution by parsing a string value.
     * @param pDilution the Dilution to parse
     * @return the new Dilution or <code>null</code> if parsing failed
     */
    public static Dilution parseString(final String pDilution) {
        try {
            return new Dilution(pDilution);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
