/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Basic UK Tax Allowance.
 */
public abstract class MoneyWiseBasicAllowance {
    /**
     * Allowance.
     */
    private final TethysMoney theAllowance;

    /**
     * Rental Allowance.
     */
    private final TethysMoney theRentalAllowance;

    /**
     * Capital Allowance.
     */
    private final TethysMoney theCapitalAllowance;

    /**
     * Marginal Reduction Type.
     */
    private final MoneyWiseMarginalReduction theMarginalReduction;

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pReduction the marginal reduction
     */
    protected MoneyWiseBasicAllowance(final TethysMoney pAllowance,
                                      final TethysMoney pRentalAllowance,
                                      final TethysMoney pCapitalAllowance,
                                      final MoneyWiseMarginalReduction pReduction) {
        theAllowance = pAllowance;
        theRentalAllowance = pRentalAllowance;
        theCapitalAllowance = pCapitalAllowance;
        theMarginalReduction = pReduction;
    }

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     */
    protected MoneyWiseBasicAllowance(final TethysMoney pAllowance,
                                      final TethysMoney pRentalAllowance,
                                      final TethysMoney pCapitalAllowance) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, MoneyWiseMarginalReduction.TWOINTHREE);
    }

    /**
     * Obtain the allowance.
     * @return the Allowance
     */
    protected TethysMoney getAllowance() {
        return theAllowance;
    }

    /**
     * Obtain the rental allowance.
     * @return the Allowance
     */
    protected TethysMoney getRentalAllowance() {
        return theRentalAllowance;
    }

    /**
     * Obtain the capital allowance.
     * @return the Allowance
     */
    protected TethysMoney getCapitalAllowance() {
        return theCapitalAllowance;
    }

    /**
     * Obtain the marginal reduction.
     * @return the Reduction
     */
    protected MoneyWiseMarginalReduction getMarginalReduction() {
        return theMarginalReduction;
    }

    /**
     * Calculate the allowance.
     * @param pConfig the tax configuration
     * @return the calculated allowance
     */
    protected TethysMoney calculateBasicAllowance(final MoneyWiseTaxConfig pConfig) {
        /* Return the basic allowance */
        return getAllowance();
    }

    /**
     * Calculate the savings allowance.
     * @param pConfig the tax configuration
     * @return the savings allowance
     */
    protected TethysMoney calculateSavingsAllowance(final MoneyWiseTaxConfig pConfig) {
        /* Return a zero allowance */
        TethysMoney myAllowance = new TethysMoney(theAllowance);
        myAllowance.setZero();
        return myAllowance;
    }

    /**
     * Calculate the dividend allowance.
     * @return the dividend allowance
     */
    protected TethysMoney calculateDividendAllowance() {
        /* Return a zero allowance */
        TethysMoney myAllowance = new TethysMoney(theAllowance);
        myAllowance.setZero();
        return myAllowance;
    }
}
