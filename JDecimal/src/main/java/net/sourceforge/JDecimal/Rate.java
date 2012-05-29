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
public class Rate extends Decimal {
    /**
     * Rates have two decimal points.
     */
    public static final int NUMDEC = 2;

    /**
     * One hundred percent.
     */
    public static final Rate RATE_ONEHUNDREDPERCENT = new Rate(10000);

    /**
     * Access the value of the Rate.
     * @return the value
     */
    public long getRate() {
        return getValue();
    }

    /**
     * Construct a new Rate from a value.
     * @param uRate the value
     */
    public Rate(final long uRate) {
        super(NUMDEC, NumberClass.RATE);
        setValue(uRate);
    }

    /**
     * Construct a new Rate by copying another rate.
     * @param pRate the Rate to copy
     */
    public Rate(final Rate pRate) {
        super(NUMDEC, NumberClass.RATE);
        setValue(pRate.getRate());
    }

    /**
     * Construct a new Rate by parsing a string value.
     * @param pRate the Rate to parse
     */
    public Rate(final String pRate) {
        super(NUMDEC, NumberClass.RATE);
        parseStringValue(pRate);
    }

    /**
     * Obtain remaining rate of this rate (i.e. 100% - this rate).
     * @return the remaining rate
     */
    public Rate getRemainingRate() {
        long myValue = RATE_ONEHUNDREDPERCENT.getRate() - getRate();
        return new Rate(myValue);
    }

    /**
     * Obtain inverse ratio of this rate (i.e. 100%/this rate).
     * @return the inverse ratio
     */
    public Ratio getInverseRatio() {
        return new Ratio(RATE_ONEHUNDREDPERCENT, this);
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
     * Format a Rate.
     * @param pRate the rate to format
     * @return the formatted Rate
     */
    public static String format(final Rate pRate) {
        return (pRate != null) ? pRate.format(false) : "null";
    }

    /**
     * Create a new Rate by parsing a string value.
     * @param pRate the Rate to parse
     * @return the new Rate or <code>null</code> if parsing failed
     */
    public static Rate parseString(final String pRate) {
        try {
            return new Rate(pRate);
        } catch (Exception e) {
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

    @Override
    public double convertToDouble() {
        /* Convert the value into units (to increase decimals by two) */
        Units myUnits = new Units(getValue());

        /* Format the string */
        String myString = myUnits.format(false);

        /* return the double value */
        return Double.parseDouble(myString);
    }
}
