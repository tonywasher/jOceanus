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
 * Represents a Diluted Price object.
 */
public class DilutedPrice extends Decimal {
    /**
     * DilutedPrices have six decimal points.
     */
    public static final int NUMDEC = 6;

    /**
     * DilutedPrices are formatted in pretty mode with a width of 12 characters.
     */
    public static final int WIDTH = 12;

    /**
     * Access the value of the Price.
     * @return the value
     */
    public long getDilutedPrice() {
        return getValue();
    }

    /**
     * Construct a new DilutedPrice from a value.
     * @param uPrice the value
     */
    public DilutedPrice(final long uPrice) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(uPrice);
    }

    /**
     * Construct a new DilutedPrice by copying another price.
     * @param pPrice the Price to copy
     */
    public DilutedPrice(final DilutedPrice pPrice) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(pPrice.getDilutedPrice());
    }

    /**
     * Construct a new Diluted Price by combining price and dilution.
     * @param pFirst the Price to dilute
     * @param pSecond the Dilution factor
     */
    public DilutedPrice(final Price pFirst,
                        final Dilution pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new DilutedPrice by parsing a string value.
     * @param pPrice the Price to parse
     */
    public DilutedPrice(final String pPrice) {
        super(NUMDEC, NumberClass.MONEY);
        parseStringValue(pPrice);
    }

    /**
     * obtain a base price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public Price getPrice(final Dilution pDilution) {
        /* Calculate original base price */
        return new Price(this, pDilution);
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
                myFormat.append("      -     ");

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

                /* Add the pound sign */
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
    public static DilutedPrice parseString(final String pPrice) {
        try {
            return new DilutedPrice(pPrice);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
