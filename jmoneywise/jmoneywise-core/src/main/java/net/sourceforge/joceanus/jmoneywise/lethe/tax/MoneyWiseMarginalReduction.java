/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import java.util.Currency;

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Marginal allowance reduction.
 */
public enum MoneyWiseMarginalReduction {
    /**
     * Half.
     */
    ONEINTWO(1, 2),

    /**
     * TwoThirds.
     */
    TWOINTHREE(2, 3);

    /**
     * The String name.
     */
    private String theName;

    /**
     * The multiplier.
     */
    private final int theMultiplier;

    /**
     * The quotient.
     */
    private final int theQuotient;

    /**
     * Constructor.
     * @param pMultiplier the multiplier
     * @param pQuotient the quotient
     */
    MoneyWiseMarginalReduction(final int pMultiplier,
                               final int pQuotient) {
        theMultiplier = pMultiplier;
        theQuotient = pQuotient;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseTaxResource.getKeyForMarginalReduction(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Calculate the allowance reduction.
     * @param pGrossTaxable the gross taxable income
     * @param pLimit the allowance limit
     * @return the reduction
     */
    public TethysMoney calculateReduction(final TethysMoney pGrossTaxable,
                                          final TethysMoney pLimit) {
        /* Determine the amount by which we are over the limit */
        final TethysMoney myExcess = new TethysMoney(pGrossTaxable);
        myExcess.subtractAmount(pLimit);

        /* Determine the quotient and multiplier in the required currency */
        final Currency myCurrency = myExcess.getCurrency();
        final TethysMoney myQuotient = TethysMoney.getWholeUnits(theQuotient, myCurrency);
        final TethysMoney myMultiplier = TethysMoney.getWholeUnits(theMultiplier, myCurrency);

        /* Calculate the reduction */
        myExcess.divide(myQuotient.unscaledValue());
        myExcess.multiply(myMultiplier.unscaledValue());

        /* return the reduction */
        return myExcess;
    }
}
