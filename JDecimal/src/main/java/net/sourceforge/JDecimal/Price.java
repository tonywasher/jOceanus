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
 * Represents a Price object.
 */
public class Price extends Decimal {
    /**
     * Prices have four decimal points.
     */
    public static final int NUMDEC = 4;

    /**
     * Prices are formatted in pretty mode with a width of 10 characters.
     */
    public static final int WIDTH = 10;

    /**
     * Access the value of the Price.
     * @return the value
     */
    public long getPrice() {
        return getValue();
    }

    /**
     * Construct a new Price from a value.
     * @param uPrice the value
     */
    public Price(final long uPrice) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(uPrice);
    }

    /**
     * Construct a new Price by copying another price.
     * @param pPrice the price to copy
     */
    public Price(final Price pPrice) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(pPrice.getPrice());
    }

    /**
     * Construct a new Price by combining diluted price and dilution.
     * @param pFirst the DilutedPrice to unDilute
     * @param pSecond the Dilution factor
     */
    public Price(final DilutedPrice pFirst,
                 final Dilution pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateQuotient(pFirst, pSecond);
    }

    /**
     * Construct a new Price by parsing a string value.
     * @param pPrice the Price to parse
     */
    public Price(final String pPrice) {
        super(NUMDEC, NumberClass.MONEY);
        parseStringValue(pPrice);
    }

    /**
     * obtain a Diluted price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public DilutedPrice getDilutedPrice(final Dilution pDilution) {
        /* Calculate diluted price */
        DilutedPrice myTotal = new DilutedPrice(this, pDilution);

        /* Return value */
        return myTotal;
    }

    @Override
    public String format(final boolean bPretty) {
        StringBuilder myFormat;
        boolean isNegative;

        /* Format the value in a standard fashion */
        myFormat = formatNumber(bPretty);

        /* If we are in pretty mode */
        if (bPretty) {
            /* If the value is zero */
            if (!isNonZero()) {
                /* Provide special display */
                myFormat.setLength(0);
                myFormat.append(DECIMAL_FORMATS.getCurrencySymbol());
                myFormat.append("      -   ");

                /* Else non-zero value */
            } else {
                /* Access the minus sign */
                char myMinus = DECIMAL_FORMATS.getMinusSign();

                /* If the value is negative, strip the leading minus sign */
                isNegative = (myFormat.charAt(0) == myMinus);
                if (isNegative) {
                    myFormat.deleteCharAt(0);
                }

                /* Extend the value to the desired width */
                while ((myFormat.length()) < WIDTH) {
                    myFormat.insert(0, ' ');
                }

                /* Add the currency symbol */
                myFormat.insert(0, DECIMAL_FORMATS.getCurrencySymbol());

                /* Add back any minus sign */
                if (isNegative) {
                    myFormat.insert(0, myMinus);
                }
            }
        }
        return myFormat.toString();
    }

    /**
     * Create a new Price by parsing a string value.
     * @param pPrice the Price to parse
     * @return the new Price or <code>null</code> if parsing failed
     */
    public static Price parseString(final String pPrice) {
        try {
            return new Price(pPrice);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Format a Price.
     * @param pPrice the price to format
     * @return the formatted Price
     */
    public static String format(final Price pPrice) {
        return (pPrice != null) ? pPrice.format(false) : "null";
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
