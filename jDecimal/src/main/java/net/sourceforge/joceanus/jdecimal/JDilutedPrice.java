/*******************************************************************************
 * jDecimal: Decimals represented by long values
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
package net.sourceforge.joceanus.jdecimal;

import java.util.Currency;

/**
 * Represents a Diluted Price object.
 */
public class JDilutedPrice
        extends JMoney {
    /**
     * Additional number of decimals for Price.
     */
    protected static final int XTRA_DECIMALS = JPrice.XTRA_DECIMALS;

    /**
     * Constructor.
     * @param pCurrency the currency
     */
    protected JDilutedPrice(final Currency pCurrency) {
        super(pCurrency);
        recordScale(pCurrency.getDefaultFractionDigits()
                    + XTRA_DECIMALS);
    }

    /**
     * Construct a new DilutedPrice by copying another price.
     * @param pPrice the Price to copy
     */
    public JDilutedPrice(final JDilutedPrice pPrice) {
        super(pPrice.getCurrency());
        setValue(pPrice.unscaledValue(), pPrice.scale());
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

    /**
     * obtain a base price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public JPrice getPrice(final JDilution pDilution) {
        /* Calculate original base price */
        return new JPrice(this, pDilution);
    }
}
