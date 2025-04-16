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

import java.util.Currency;

/**
 * Represents a Price object.
 */
public class OceanusPrice
        extends OceanusMoney {
    /**
     * Additional number of decimals for Price.
     */
    static final int XTRA_DECIMALS = 2;

    /**
     * Constructor for price of value zero in the default currency.
     */
    public OceanusPrice() {
        this(DEFAULT_CURRENCY);
    }

    /**
     * Constructor for money of value zero in specified currency.
     * @param pCurrency the currency
     */
    public OceanusPrice(final Currency pCurrency) {
        super(pCurrency);
        recordScale(pCurrency.getDefaultFractionDigits()
                    + XTRA_DECIMALS);
    }

    /**
     * Construct a new Price by copying another price.
     * @param pPrice the Price to copy
     */
    public OceanusPrice(final OceanusPrice pPrice) {
        super(pPrice.getCurrency());
        setValue(pPrice.unscaledValue(), pPrice.scale());
    }

    /**
     * Constructor for price from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public OceanusPrice(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        OceanusDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(getCurrency().getDefaultFractionDigits()
                      + XTRA_DECIMALS);
    }

    /**
     * Constructor for price from a decimal string.
     * @param pSource The source decimal string
     * @param pCurrency the currency
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public OceanusPrice(final String pSource,
                        final Currency pCurrency) {
        /* Use default constructor */
        this(pCurrency);

        /* Parse the string and correct the scale */
        OceanusDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(getCurrency().getDefaultFractionDigits()
                + XTRA_DECIMALS);
    }

    /**
     * Create the price from a byte array.
     * @param pBuffer the buffer
     */
    public OceanusPrice(final byte[] pBuffer) {
        super(pBuffer);
    }

    /**
     * Factory method for generating whole monetary units for a currency (e.g. £)
     * @param pUnits the number of whole monetary units
     * @param pCurrency the currency
     * @return the allocated price
     */
    public static OceanusPrice getWholeUnits(final long pUnits,
                                             final Currency pCurrency) {
        /* Allocate the money */
        final OceanusPrice myResult = new OceanusPrice(pCurrency);
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Factory method for generating whole monetary units (e.g. £)
     * @param pUnits the number of whole monetary units
     * @return the allocated price
     */
    public static OceanusPrice getWholeUnits(final long pUnits) {
        /* Allocate the price */
        final OceanusPrice myResult = new OceanusPrice();
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    @Override
    public OceanusPrice changeCurrency(final Currency pCurrency) {
        /* Convert currency with an exchange rate of one */
        return convertCurrency(pCurrency, OceanusRatio.ONE);
    }

    @Override
    public OceanusPrice convertCurrency(final Currency pCurrency,
                                        final OceanusRatio pRate) {
        /* If this is the same currency then no conversion */
        if (getCurrency().equals(pCurrency)) {
            return new OceanusPrice(this);
        }

        /* Create the new Price */
        final OceanusPrice myResult = new OceanusPrice(pCurrency);
        myResult.calculateProduct(this, pRate);
        return myResult;
    }

    /**
     * Subtract a monetary price from the value.
     *
     * @param pValue The money to subtract from this one.
     */
    public void subtractPrice(final OceanusPrice pValue) {
        /* Currency must be identical */
        if (!getCurrency().equals(pValue.getCurrency())) {
            throw new IllegalArgumentException(ERROR_DIFFER);
        }

        /* Subtract the value */
        super.subtractValue(pValue);
    }
}
