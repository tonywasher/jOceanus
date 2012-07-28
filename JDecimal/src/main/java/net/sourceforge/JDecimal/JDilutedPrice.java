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

import java.util.Currency;

/**
 * Represents a Diluted Price object.
 */
public class JDilutedPrice extends JDecimal {
    /**
     * Currency for price.
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
     * Constructor.
     * @param pCurrency the currency
     */
    protected JDilutedPrice(final Currency pCurrency) {
        theCurrency = pCurrency;
        recordScale(theCurrency.getDefaultFractionDigits() + JPrice.XTRA_DECIMALS);
    }

    /**
     * Construct a new DilutedPrice by copying another price.
     * @param pPrice the Price to copy
     */
    public JDilutedPrice(final JDilutedPrice pPrice) {
        super(pPrice.unscaledValue(), pPrice.scale());
        theCurrency = pPrice.getCurrency();
    }

    /**
     * Construct a new Diluted Price by combining price and dilution.
     * @param pPrice the Price to dilute
     * @param pDilution the Dilution factor
     */
    protected JDilutedPrice(final JPrice pPrice,
                            final JDilution pDilution) {
        this(pPrice.getCurrency());
        calculateProduct(pPrice, pDilution);
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
     * obtain a base price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public JPrice getPrice(final JDilution pDilution) {
        /* Calculate original base price */
        return new JPrice(this, pDilution);
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

        /* Cast as diluted price */
        JDilutedPrice myThat = (JDilutedPrice) pThat;

        /* Check currency */
        if (!theCurrency.equals(myThat.getCurrency())) {
            return false;
        }

        /* Check value and scale */
        return super.equals(pThat);
    }

    @Override
    public int hashCode() {
        return theCurrency.hashCode() ^ super.hashCode();
    }
}
