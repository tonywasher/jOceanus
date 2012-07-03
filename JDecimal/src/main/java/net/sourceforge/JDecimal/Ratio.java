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
 * Represents a Ratio object.
 */
public class Ratio extends Decimal {
    /**
     * Ratio have six decimal points.
     */
    public static final int NUMDEC = 6;

    /**
     * Access the value of the Ratio.
     * @return the value
     */
    public long getRatio() {
        return getValue();
    }

    /**
     * Construct a new Ratio from a value.
     * @param uRatio the value
     */
    public Ratio(final long uRatio) {
        super(NUMDEC, NumberClass.RATE);
        setValue(uRatio);
    }

    /**
     * Construct a new Ratio by copying another ratio.
     * @param pRatio the Ratio to copy
     */
    public Ratio(final Ratio pRatio) {
        super(NUMDEC, NumberClass.RATE);
        setValue(pRatio.getRatio());
    }

    /**
     * Construct a new Ratio by the ratio between two decimals.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     */
    public Ratio(final Decimal pFirst,
                 final Decimal pSecond) {
        super(NUMDEC, NumberClass.RATE);
        calculateQuotient(pFirst, pSecond);
    }

    /**
     * Construct a new Ratio by parsing a string value.
     * @param pRatio the Ratio to parse
     */
    public Ratio(final String pRatio) {
        super(NUMDEC, NumberClass.RATE);
        parseStringValue(pRatio);
    }

    /**
     * Obtain inverse rate of this rate (i.e. 100%/this rate).
     * @return the remaining rate
     */
    public Ratio getInverseRatio() {
        return new Ratio(Rate.RATE_ONEHUNDREDPERCENT, this);
    }

    @Override
    public String format(final boolean bPretty) {
        StringBuilder myFormat;
        myFormat = formatNumber(bPretty);
        if (bPretty) {
            myFormat.append(DECIMAL_FORMATS.getPercent());
        }
        return myFormat.toString();
    }

    /**
     * Create a new Ratio by parsing a string value.
     * @param pRatio the Ratio to parse
     * @return the new Ratio or <code>null</code> if parsing failed
     */
    public static Ratio parseString(final String pRatio) {
        try {
            return new Ratio(pRatio);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert a whole number value to include decimals.
     * @param pValue the whole number value
     * @return the converted value with added zeros
     */
    public static long convertToValue(final long pValue) {
        /* Build in the decimals to the value */
        return adjustDecimals(pValue, NUMDEC);
    }
}
