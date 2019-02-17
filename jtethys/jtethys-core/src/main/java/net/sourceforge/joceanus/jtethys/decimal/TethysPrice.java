/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.decimal;

import java.text.DecimalFormatSymbols;
import java.util.Currency;

/**
 * Represents a Price object.
 */
public class TethysPrice
        extends TethysMoney {
    /**
     * Additional number of decimals for Price.
     */
    protected static final int XTRA_DECIMALS = 2;

    /**
     * Constructor for price of value zero in the default currency.
     */
    public TethysPrice() {
        this(DecimalFormatSymbols.getInstance().getCurrency());
    }

    /**
     * Constructor for money of value zero in specified currency.
     * @param pCurrency the currency
     */
    public TethysPrice(final Currency pCurrency) {
        super(pCurrency);
        recordScale(pCurrency.getDefaultFractionDigits()
                    + XTRA_DECIMALS);
    }

    /**
     * Construct a new Price by copying another price.
     * @param pPrice the Price to copy
     */
    public TethysPrice(final TethysPrice pPrice) {
        super(pPrice.getCurrency());
        setValue(pPrice.unscaledValue(), pPrice.scale());
    }

    /**
     * Constructor for price from a decimal string.
     * @param pSource The source decimal string
     * @throws IllegalArgumentException on invalidly formatted argument
     */
    public TethysPrice(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        TethysDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(getCurrency().getDefaultFractionDigits()
                      + XTRA_DECIMALS);
    }

    /**
     * Construct a new Price by combining diluted price and dilution.
     * @param pDilutedPrice the DilutedPrice to unDilute
     * @param pDilution the Dilution factor
     */
    protected TethysPrice(final TethysDilutedPrice pDilutedPrice,
                          final TethysDilution pDilution) {
        this(pDilutedPrice.getCurrency());
        calculateQuotient(pDilutedPrice, pDilution);
    }

    /**
     * Factory method for generating whole monetary units for a currency (e.g. £1)
     * @param pUnits the number of whole monetary units
     * @param pCurrency the currency
     * @return the allocated price
     */
    public static TethysPrice getWholeUnits(final long pUnits,
                                            final Currency pCurrency) {
        /* Allocate the money */
        final TethysPrice myResult = new TethysPrice(pCurrency);
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * Factory method for generating whole monetary units (e.g. £1)
     * @param pUnits the number of whole monetary units
     * @return the allocated price
     */
    public static TethysPrice getWholeUnits(final long pUnits) {
        /* Allocate the price */
        final TethysPrice myResult = new TethysPrice();
        final int myScale = myResult.scale();
        myResult.setValue(adjustDecimals(pUnits, myScale), myScale);
        return myResult;
    }

    /**
     * obtain a Diluted price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public TethysDilutedPrice getDilutedPrice(final TethysDilution pDilution) {
        /* Calculate diluted price */
        return new TethysDilutedPrice(this, pDilution);
    }
}
