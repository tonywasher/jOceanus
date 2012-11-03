/*******************************************************************************
 * jDecimal: Decimals represented by long values
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
package net.sourceforge.jOceanus.jDecimal;

import java.text.DecimalFormatSymbols;
import java.util.Currency;

/**
 * Represents a Price object.
 */
public class JPrice extends JMoney {
    /**
     * Additional number of decimals for Price.
     */
    protected static final int XTRA_DECIMALS = 2;

    /**
     * Constructor for price of value zero in the default currency.
     */
    public JPrice() {
        this(DecimalFormatSymbols.getInstance().getCurrency());
    }

    /**
     * Constructor for money of value zero.
     * @param pCurrency the currency
     */
    public JPrice(final Currency pCurrency) {
        super(pCurrency);
        recordScale(pCurrency.getDefaultFractionDigits() + XTRA_DECIMALS);
    }

    /**
     * Construct a new Price by copying another price.
     * @param pPrice the Price to copy
     */
    public JPrice(final JPrice pPrice) {
        super(pPrice.getCurrency());
        setValue(pPrice.unscaledValue(), pPrice.scale());
    }

    /**
     * Constructor for price from a decimal string.
     * @param pSource The source decimal string
     */
    public JPrice(final String pSource) {
        /* Use default constructor */
        this();

        /* Parse the string and correct the scale */
        JDecimalParser.parseDecimalValue(pSource, this);
        adjustToScale(getCurrency().getDefaultFractionDigits() + XTRA_DECIMALS);
    }

    /**
     * Construct a new Price by combining diluted price and dilution.
     * @param pDilutedPrice the DilutedPrice to unDilute
     * @param pDilution the Dilution factor
     */
    protected JPrice(final JDilutedPrice pDilutedPrice,
                     final JDilution pDilution) {
        this(pDilutedPrice.getCurrency());
        calculateQuotient(pDilutedPrice, pDilution);
    }

    /**
     * obtain a Diluted price.
     * @param pDilution the dilution factor
     * @return the calculated value
     */
    public JDilutedPrice getDilutedPrice(final JDilution pDilution) {
        /* Calculate diluted price */
        return new JDilutedPrice(this, pDilution);
    }
}
