/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Basic UK Tax Allowance.
 */
public abstract class MoneyWiseUKBasicAllowance
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKBasicAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKBasicAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_BASIC, MoneyWiseUKBasicAllowance::getAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_RENTAL, MoneyWiseUKBasicAllowance::getRentalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_CAPITAL, MoneyWiseUKBasicAllowance::getCapitalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.MARGINAL_REDUCTION, MoneyWiseUKBasicAllowance::getMarginalReduction);
    }

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
    protected MoneyWiseUKBasicAllowance(final TethysMoney pAllowance,
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
    protected MoneyWiseUKBasicAllowance(final TethysMoney pAllowance,
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
    protected TethysMoney calculateBasicAllowance(final MoneyWiseUKTaxConfig pConfig) {
        return getAllowance();
    }

    /**
     * Calculate the savings allowance.
     * @param pConfig the tax configuration
     * @return the savings allowance
     */
    protected TethysMoney calculateSavingsAllowance(final MoneyWiseUKTaxConfig pConfig) {
        return getZeroAmount();
    }

    /**
     * Calculate the dividend allowance.
     * @return the dividend allowance
     */
    protected TethysMoney calculateDividendAllowance() {
        return getZeroAmount();
    }

    /**
     * Calculate the loSavings band.
     * @param pConfig the tax configuration
     * @param pLoSavings the low savings band
     * @return the loSavings band
     */
    protected MoneyWiseTaxBand calculateLoSavingsBand(final MoneyWiseUKTaxConfig pConfig,
                                                      final MoneyWiseTaxBand pLoSavings) {
        /* Obtain the loSavings band */
        if (pLoSavings == null) {
            return new MoneyWiseTaxBand(getZeroAmount(), TethysRate.getWholePercentage(0));
        }
        final MoneyWiseTaxBand myBand = new MoneyWiseTaxBand(pLoSavings);
        final TethysMoney myAmount = myBand.getAmount();

        /* Obtain the preSavings income remaining after the allowance */
        final TethysMoney myPreSavings = new TethysMoney(pConfig.getGrossPreSavings());
        myPreSavings.subtractAmount(pConfig.getAllowance());

        /* If we have allowance left over, return the full loSavings band */
        if (!myPreSavings.isPositive()) {
            return myBand;
        }

        /* Subtract remaining income from the loSavings band */
        myAmount.subtractAmount(myPreSavings);

        /* If we are negative, reset to zero */
        if (!myAmount.isPositive()) {
            myAmount.setZero();
        }

        /* return the amount */
        return myBand;
    }

    /**
     * Obtain zero amount.
     * @return the zero amount
     */
    protected TethysMoney getZeroAmount() {
        /* Return a zero allowance */
        final TethysMoney myAllowance = new TethysMoney(theAllowance);
        myAllowance.setZero();
        return myAllowance;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFieldSet<? extends MoneyWiseUKBasicAllowance> getBaseFieldSet() {
        return FIELD_DEFS;
    }
}
