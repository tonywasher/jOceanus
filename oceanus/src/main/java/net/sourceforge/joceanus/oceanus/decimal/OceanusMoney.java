/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.oceanus.decimal;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Currency;

/**
 * Represents a Money object.
 */
public class OceanusMoney
        extends OceanusDecimal {
    /**
     * Money Byte length.
     */
    public static final int BYTE_LEN = Long.BYTES + 4;

    /**
     * Invalid Currency error text.
     */
    protected static final String ERROR_DIFFER = "Cannot add together two different currencies";

    /**
     * Currency code length.
     */
    private static final int CURRCODE_LEN = 2;

    /**
     * Currency for money.
     */
    private final Currency theCurrency;

    /**
     * Constructor for money of value zero in the default currency.
     */
    public OceanusMoney() {
        this(DecimalFormatSymbols.getInstance().getCurrency());
    }

    /**
     * Constructor for money of value zero.
     *
     * @param pCurrency the currency
     */
    public OceanusMoney(final Currency pCurrency) {
        theCurrency = pCurrency;
        recordScale(theCurrency.getDefaultFractionDigits());
    }

    /**
     * Construct a new Money by copying another money.
     *
     * @param pMoney the Money to copy
     */
    public OceanusMoney(final OceanusMoney pMoney) {
        super(pMoney.unscaledValue(), pMoney.scale());
        theCurrency = pMoney.getCurrency();
    }

    /**
     * Constructor for money from a decimal string.
     *
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public OceanusMoney(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        OceanusDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(theCurrency.getDefaultFractionDigits());
    }

    /**
     * Constructor for money from a decimal string.
     *
     * @param pSource The source decimal string
     * @param pCurrency the currency
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public OceanusMoney(final String pSource,
                        final Currency pCurrency) {
        /* Use currency constructor */
        this(pCurrency);

        /* Parse the string and correct the scale */
        OceanusDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(theCurrency.getDefaultFractionDigits());
    }

    /**
     * Construct a new Money by combining units and price.
     *
     * @param pUnits the number of units
     * @param pPrice the price of each unit
     */
    protected OceanusMoney(final OceanusUnits pUnits,
                           final OceanusPrice pPrice) {
        this(pPrice.getCurrency());
        calculateProduct(pUnits, pPrice);
    }

    /**
     * Construct a new Money by combining money and rate.
     *
     * @param pMoney the Money to apply rate to
     * @param pRate  the Rate to apply
     */
    private OceanusMoney(final OceanusMoney pMoney,
                         final OceanusRate pRate) {
        this(pMoney.getCurrency());
        calculateProduct(pMoney, pRate);
    }

    /**
     * Construct a new Money by combining money and ratio.
     *
     * @param pMoney the Money to apply ratio to
     * @param pRatio the Ratio to apply
     */
    private OceanusMoney(final OceanusMoney pMoney,
                         final OceanusRatio pRatio) {
        this(pMoney.getCurrency());
        calculateProduct(pMoney, pRatio);
    }

    /**
     * Create the decimal from a byte array.
     * @param pBuffer the buffer
     */
    public OceanusMoney(final byte[] pBuffer) {
        super(pBuffer);
        if (pBuffer.length < Long.BYTES + 1 + CURRCODE_LEN) {
            throw new IllegalArgumentException();
        }
        final byte[] myCurr = Arrays.copyOfRange(pBuffer, Long.BYTES + 1, pBuffer.length);
        final String myCurrCode = new String(myCurr);
        theCurrency = Currency.getInstance(myCurrCode);
    }

    /**
     * Access the currency.
     *
     * @return the currency
     */
    public Currency getCurrency() {
        return theCurrency;
    }

    /**
     * Factory method for generating whole monetary units for a currency (e.g. £)
     *
     * @param pUnits    the number of whole monetary units
     * @param pCurrency the currency
     * @return the allocated money
     */
    public static OceanusMoney getWholeUnits(final long pUnits,
                                             final Currency pCurrency) {
        /* Allocate the money */
        final OceanusMoney myResult = new OceanusMoney(pCurrency);
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Factory method for generating whole monetary units (e.g. £)
     *
     * @param pUnits the number of whole monetary units
     * @return the allocated money
     */
    public static OceanusMoney getWholeUnits(final long pUnits) {
        /* Allocate the money */
        final OceanusMoney myResult = new OceanusMoney();
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Add a monetary amount to the value.
     *
     * @param pValue The money to add to this one.
     */
    public void addAmount(final OceanusMoney pValue) {
        /* Currency must be identical */
        if (!theCurrency.equals(pValue.getCurrency())) {
            throw new IllegalArgumentException(ERROR_DIFFER);
        }

        /* Add the value */
        super.addValue(pValue);
    }

    /**
     * Subtract a monetary amount from the value.
     *
     * @param pValue The money to subtract from this one.
     */
    public void subtractAmount(final OceanusMoney pValue) {
        /* Currency must be identical */
        if (!theCurrency.equals(pValue.getCurrency())) {
            throw new IllegalArgumentException(ERROR_DIFFER);
        }

        /* Subtract the value */
        super.subtractValue(pValue);
    }

    @Override
    public void addValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Obtain value in different currency.
     *
     * @param pCurrency the currency to convert to
     * @return the converted money in the new currency
     */
    public OceanusMoney changeCurrency(final Currency pCurrency) {
        /* Convert currency with an exchange rate of one */
        return convertCurrency(pCurrency, OceanusRatio.ONE);
    }

    /**
     * Obtain converted money.
     *
     * @param pCurrency the currency to convert to
     * @param pRate     the conversion rate
     * @return the converted money in the new currency
     */
    public OceanusMoney convertCurrency(final Currency pCurrency,
                                        final OceanusRatio pRate) {
        /* If this is the same currency then no conversion */
        if (theCurrency.equals(pCurrency)) {
            return new OceanusMoney(this);
        }

        /* Create the new Money */
        final OceanusMoney myResult = new OceanusMoney(pCurrency);
        myResult.calculateProduct(this, pRate);
        return myResult;
    }

    /**
     * obtain a Diluted money.
     *
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public OceanusMoney getDilutedMoney(final OceanusRatio pDilution) {
        /* Calculate diluted value */
        return new OceanusMoney(this, pDilution);
    }

    /**
     * calculate the value of this money at a given rate.
     *
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public OceanusMoney valueAtRate(final OceanusRate pRate) {
        /* Calculate the money at this rate */
        return new OceanusMoney(this, pRate);
    }

    /**
     * calculate the value of this money at a given ratio.
     *
     * @param pRatio the ratio to multiply by
     * @return the calculated value
     */
    public OceanusMoney valueAtRatio(final OceanusRatio pRatio) {
        /* Calculate the money at this rate */
        return new OceanusMoney(this, pRatio);
    }

    /**
     * calculate the gross value of this money at a given rate used to convert from net to gross
     * values form interest and dividends.
     *
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public OceanusMoney grossValueAtRate(final OceanusRate pRate) {
        /* Calculate the Gross corresponding to this net value at the rate */
        final OceanusRatio myRatio = pRate.getRemainingRate().getInverseRatio();
        return new OceanusMoney(this, myRatio);
    }

    /**
     * calculate the TaxCredit value of this money at a given rate used to convert from net to
     * gross. values form interest and dividends
     *
     * @param pRate the rate to calculate at
     * @return the calculated value
     */
    public OceanusMoney taxCreditAtRate(final OceanusRate pRate) {
        /* Calculate the Tax Credit corresponding to this net value at the rate */
        final OceanusRatio myRatio = new OceanusRatio(pRate, pRate.getRemainingRate());
        return new OceanusMoney(this, myRatio);
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     *
     * @param pWeight the weight of this item
     * @param pTotal  the total weight of all the items
     * @return the calculated value
     */
    public OceanusMoney valueAtWeight(final OceanusMoney pWeight,
                                      final OceanusMoney pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new OceanusMoney(theCurrency);
        }

        /* Calculate the defined ratio of this value */
        final OceanusRatio myRatio = new OceanusRatio(pWeight, pTotal);
        return new OceanusMoney(this, myRatio);
    }

    /**
     * calculate the value of this money at a given proportion (i.e. weight/total).
     *
     * @param pWeight the weight of this item
     * @param pTotal  the total weight of all the items
     * @return the calculated value
     */
    public OceanusMoney valueAtWeight(final OceanusUnits pWeight,
                                      final OceanusUnits pTotal) {
        /* Handle zero total */
        if (!pTotal.isNonZero()) {
            return new OceanusMoney(theCurrency);
        }

        /* Calculate the defined ratio of this value */
        final OceanusRatio myRatio = new OceanusRatio(pWeight, pTotal);
        return new OceanusMoney(this, myRatio);
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
        final OceanusMoney myThat = (OceanusMoney) pThat;

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

    @Override
    public byte[] toBytes() {
        final byte[] myBase = super.toBytes();
        final byte[] myCurr = theCurrency.getCurrencyCode().getBytes(StandardCharsets.UTF_8);
        final byte[] myResult = Arrays.copyOf(myBase, myBase.length + myCurr.length);
        System.arraycopy(myCurr, 0, myResult, myBase.length, myCurr.length);
        return myResult;
    }
}
