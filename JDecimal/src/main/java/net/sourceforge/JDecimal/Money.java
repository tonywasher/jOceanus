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
 * Represents a Money object.
 */
public class Money extends Decimal {
    /**
     * Money has two decimal points.
     */
    public static final int NUMDEC = 2;

    /**
     * Money is formatted in pretty mode with a width of 10 characters.
     */
    public static final int WIDTH = 10;

    /**
     * Access the value of the Money.
     * @return the value
     */
    public long getAmount() {
        return getValue();
    }

    /**
     * Add money to the value.
     * @param pAmount The amount to add to this one.
     */
    public void addAmount(final Money pAmount) {
        addValue(pAmount);
    }

    /**
     * Subtract money from the value.
     * @param pAmount The amount to subtract from this one.
     */
    public void subtractAmount(final Money pAmount) {
        subtractValue(pAmount);
    }

    /**
     * Construct a new Money from a value.
     * @param uAmount the value
     */
    public Money(final long uAmount) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(uAmount);
    }

    /**
     * Construct a new Money by copying another money.
     * @param pMoney the Money to copy
     */
    public Money(final Money pMoney) {
        super(NUMDEC, NumberClass.MONEY);
        setValue(pMoney.getAmount());
    }

    /**
     * Construct a new Money by combining units and price.
     * @param pFirst the number of units
     * @param pSecond the price of each unit
     */
    public Money(final Units pFirst,
                 final Price pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new Money by combining money and rate.
     * @param pFirst the Money to apply rate to
     * @param pSecond the Rate to apply
     */
    public Money(final Money pFirst,
                 final Rate pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new Money by combining money and ratio.
     * @param pFirst the Money to apply ratio to
     * @param pSecond the Ratio to apply
     */
    public Money(final Money pFirst,
                 final Ratio pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new Money by combining money and dilution.
     * @param pFirst the Money to dilute
     * @param pSecond the Dilution factor
     */
    public Money(final Money pFirst,
                 final Dilution pSecond) {
        super(NUMDEC, NumberClass.MONEY);
        calculateProduct(pFirst, pSecond);
    }

    /**
     * Construct a new Money by parsing a string value.
     * @param pMoney the Money to parse
     */
    public Money(final String pMoney) {
        super(NUMDEC, NumberClass.MONEY);
        parseStringValue(pMoney);
    }

    /**
     * obtain a Diluted value.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public Money getDilutedAmount(final Dilution pDilution) {
        /* Calculate diluted value */
        Money myTotal = new Money(this, pDilution);

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
                    myFormat = myFormat.deleteCharAt(0);
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
     * Format money.
     * @param pMoney the money to format
     * @return the formatted Money
     */
    public static String format(final Money pMoney) {
        return (pMoney != null) ? pMoney.format(false) : "null";
    }

    /**
     * Create a new Money by parsing a string value.
     * @param pMoney the Money to parse
     * @return the new Money or <code>null</code> if parsing failed
     */
    public static Money parseString(final String pMoney) {
        try {
            return new Money(pMoney);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * calculate the value of this money at a given rate.
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public Money valueAtRate(final Rate pRate) {
        /* Calculate the money at this rate */
        Money myTotal = new Money(this, pRate);

        /* Return value */
        return myTotal;
    }

    /**
     * calculate the gross value of this money at a given rate used to convert from net to gross values form
     * interest and dividends.
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public Money grossValueAtRate(final Rate pRate) {
        /* Calculate the Gross corresponding to this net value at the rate */
        Ratio myRatio = pRate.getRemainingRate().getInverseRatio();
        Money myTotal = new Money(this, myRatio);

        /* Return value */
        return myTotal;
    }

    /**
     * calculate the TaxCredit value of this money at a given rate used to convert from net to gross. values
     * form interest and dividends
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public Money taxCreditAtRate(final Rate pRate) {
        /* Calculate the Tax Credit corresponding to this net value at the rate */
        Ratio myRatio = new Ratio(pRate, pRate.getRemainingRate());
        Money myTotal = new Money(this, myRatio);

        /* Return value */
        return myTotal;
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     * @param pWeight the weight of this item
     * @param pTotal the total weight of all the items
     * @return the calculated value
     */
    public Money valueAtWeight(final Money pWeight,
                               final Money pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new Money(0);
        }

        /* Calculate the defined ratio of this value */
        Ratio myRatio = new Ratio(pWeight, pTotal);
        Money myTotal = new Money(this, myRatio);

        /* Return value */
        return myTotal;
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     * @param pWeight the weight of this item
     * @param pTotal the total weight of all the items
     * @return the calculated value
     */
    public Money valueAtWeight(final Units pWeight,
                               final Units pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new Money(0);
        }

        /* Calculate the defined ratio of this value */
        Ratio myRatio = new Ratio(pWeight, pTotal);
        Money myTotal = new Money(this, myRatio);

        /* Return value */
        return myTotal;
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
