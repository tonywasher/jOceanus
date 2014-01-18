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

import java.text.DecimalFormatSymbols;
import java.util.Currency;

/**
 * Represents a Money object.
 */
public class JMoney
        extends JDecimal {
    /**
     * Invalid Currency error text.
     */
    private static final String ERROR_DIFFER = "Cannot add together two different currencies";

    /**
     * Currency for money.
     */
    private final Currency theCurrency;

    /**
     * Access the currency.
     * @return the currency
     */
    public Currency getCurrency() {
        return theCurrency;
    }

    /**
     * Factory method for generating whole monetary units for a currency (e.g. £1)
     * @param pUnits the number of whole monetary units
     * @param pCurrency the currency
     * @return the allocated money
     */
    public static JMoney getWholeUnits(final long pUnits,
                                       final Currency pCurrency) {
        /* Allocate the money */
        JMoney myResult = new JMoney(pCurrency);
        int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Factory method for generating whole monetary units (e.g. £1)
     * @param pUnits the number of whole monetary units
     * @return the allocated money
     */
    public static JMoney getWholeUnits(final long pUnits) {
        /* Allocate the money */
        JMoney myResult = new JMoney();
        int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Constructor for money of value zero in the default currency.
     */
    public JMoney() {
        this(DecimalFormatSymbols.getInstance().getCurrency());
    }

    /**
     * Constructor for money of value zero.
     * @param pCurrency the currency
     */
    public JMoney(final Currency pCurrency) {
        theCurrency = pCurrency;
        recordScale(theCurrency.getDefaultFractionDigits());
    }

    /**
     * Construct a new Money by copying another money.
     * @param pMoney the Money to copy
     */
    public JMoney(final JMoney pMoney) {
        super(pMoney.unscaledValue(), pMoney.scale());
        theCurrency = pMoney.getCurrency();
    }

    /**
     * Constructor for money from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public JMoney(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        JDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(theCurrency.getDefaultFractionDigits());
    }

    /**
     * Construct a new Money by combining units and price.
     * @param pUnits the number of units
     * @param pPrice the price of each unit
     */
    protected JMoney(final JUnits pUnits,
                     final JPrice pPrice) {
        this(pPrice.getCurrency());
        calculateProduct(pUnits, pPrice);
    }

    /**
     * Construct a new Money by combining money and rate.
     * @param pMoney the Money to apply rate to
     * @param pRate the Rate to apply
     */
    private JMoney(final JMoney pMoney,
                   final JRate pRate) {
        this(pMoney.getCurrency());
        calculateProduct(pMoney, pRate);
    }

    /**
     * Construct a new Money by combining money and ratio.
     * @param pMoney the Money to apply ratio to
     * @param pRatio the Ratio to apply
     */
    private JMoney(final JMoney pMoney,
                   final JRatio pRatio) {
        this(pMoney.getCurrency());
        calculateProduct(pMoney, pRatio);
    }

    /**
     * Construct a new Money by combining money and dilution.
     * @param pMoney the Money to dilute
     * @param pDilution the Dilution factor
     */
    private JMoney(final JMoney pMoney,
                   final JDilution pDilution) {
        this(pMoney.getCurrency());
        recordScale(theCurrency.getDefaultFractionDigits());
        calculateProduct(pMoney, pDilution);
    }

    /**
     * Add a monetary amount to the value.
     * @param pValue The money to add to this one.
     */
    public void addAmount(final JMoney pValue) {
        /* Currency must be identical */
        if (!theCurrency.equals(pValue.getCurrency())) {
            throw new IllegalArgumentException(ERROR_DIFFER);
        }

        /* Add the value */
        super.addValue(pValue);
    }

    /**
     * Subtract a monetary amount from the value.
     * @param pValue The money to subtract from this one.
     */
    public void subtractAmount(final JMoney pValue) {
        /* Currency must be identical */
        if (!theCurrency.equals(pValue.getCurrency())) {
            throw new IllegalArgumentException(ERROR_DIFFER);
        }

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
     * Obtain converted money.
     * @param pCurrency the currency to convert to
     * @param pRate the conversion rate
     * @return the converted money in the new currency
     */
    public JMoney convertCurrency(final Currency pCurrency,
                                  final JRatio pRate) {
        /* If this is the same currency then no conversion */
        if (theCurrency.equals(pCurrency)) {
            return new JMoney(this);
        }

        /* Create the new Money */
        JMoney myResult = new JMoney(pCurrency);
        myResult.calculateProduct(this, pRate);
        return myResult;
    }

    /**
     * obtain a Diluted money.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public JMoney getDilutedMoney(final JDilution pDilution) {
        /* Calculate diluted value */
        return new JMoney(this, pDilution);
    }

    /**
     * calculate the value of this money at a given rate.
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public JMoney valueAtRate(final JRate pRate) {
        /* Calculate the money at this rate */
        return new JMoney(this, pRate);
    }

    /**
     * calculate the gross value of this money at a given rate used to convert from net to gross values form interest and dividends.
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public JMoney grossValueAtRate(final JRate pRate) {
        /* Calculate the Gross corresponding to this net value at the rate */
        JRatio myRatio = pRate.getRemainingRate().getInverseRatio();
        return new JMoney(this, myRatio);
    }

    /**
     * calculate the TaxCredit value of this money at a given rate used to convert from net to gross. values form interest and dividends
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public JMoney taxCreditAtRate(final JRate pRate) {
        /* Calculate the Tax Credit corresponding to this net value at the rate */
        JRatio myRatio = new JRatio(pRate, pRate.getRemainingRate());
        return new JMoney(this, myRatio);
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     * @param pWeight the weight of this item
     * @param pTotal the total weight of all the items
     * @return the calculated value
     */
    public JMoney valueAtWeight(final JMoney pWeight,
                                final JMoney pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new JMoney(theCurrency);
        }

        /* Calculate the defined ratio of this value */
        JRatio myRatio = new JRatio(pWeight, pTotal);
        return new JMoney(this, myRatio);
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     * @param pWeight the weight of this item
     * @param pTotal the total weight of all the items
     * @return the calculated value
     */
    public JMoney valueAtWeight(final JUnits pWeight,
                                final JUnits pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new JMoney(theCurrency);
        }

        /* Calculate the defined ratio of this value */
        JRatio myRatio = new JRatio(pWeight, pTotal);
        return new JMoney(this, myRatio);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Cast as money */
        JMoney myThat = (JMoney) pThat;

        /* Check currency */
        if (!theCurrency.equals(myThat.getCurrency())) {
            return false;
        }

        /* Check value and scale */
        return super.equals(pThat);
    }

    @Override
    public int hashCode() {
        return theCurrency.hashCode()
               ^ super.hashCode();
    }
}
