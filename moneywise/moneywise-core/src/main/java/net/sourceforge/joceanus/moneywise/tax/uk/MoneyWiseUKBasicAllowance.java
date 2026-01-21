/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.tax.uk;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;

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
    private final OceanusMoney theAllowance;

    /**
     * Rental Allowance.
     */
    private final OceanusMoney theRentalAllowance;

    /**
     * Capital Allowance.
     */
    private final OceanusMoney theCapitalAllowance;

    /**
     * Marginal Reduction Type.
     */
    private final MoneyWiseMarginalReduction theMarginalReduction;

    /**
     * Constructor.
     *
     * @param pAllowance        the allowance
     * @param pRentalAllowance  the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pReduction        the marginal reduction
     */
    protected MoneyWiseUKBasicAllowance(final OceanusMoney pAllowance,
                                        final OceanusMoney pRentalAllowance,
                                        final OceanusMoney pCapitalAllowance,
                                        final MoneyWiseMarginalReduction pReduction) {
        theAllowance = pAllowance;
        theRentalAllowance = pRentalAllowance;
        theCapitalAllowance = pCapitalAllowance;
        theMarginalReduction = pReduction;
    }

    /**
     * Constructor.
     *
     * @param pAllowance        the allowance
     * @param pRentalAllowance  the rental allowance
     * @param pCapitalAllowance the capital allowance
     */
    protected MoneyWiseUKBasicAllowance(final OceanusMoney pAllowance,
                                        final OceanusMoney pRentalAllowance,
                                        final OceanusMoney pCapitalAllowance) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, MoneyWiseMarginalReduction.TWOINTHREE);
    }

    /**
     * Obtain the allowance.
     *
     * @return the Allowance
     */
    protected OceanusMoney getAllowance() {
        return theAllowance;
    }

    /**
     * Obtain the rental allowance.
     *
     * @return the Allowance
     */
    protected OceanusMoney getRentalAllowance() {
        return theRentalAllowance;
    }

    /**
     * Obtain the capital allowance.
     *
     * @return the Allowance
     */
    protected OceanusMoney getCapitalAllowance() {
        return theCapitalAllowance;
    }

    /**
     * Obtain the marginal reduction.
     *
     * @return the Reduction
     */
    protected MoneyWiseMarginalReduction getMarginalReduction() {
        return theMarginalReduction;
    }

    /**
     * Calculate the allowance.
     *
     * @param pConfig the tax configuration
     * @return the calculated allowance
     */
    protected OceanusMoney calculateBasicAllowance(final MoneyWiseUKTaxConfig pConfig) {
        return getAllowance();
    }

    /**
     * Calculate the savings allowance.
     *
     * @param pConfig the tax configuration
     * @return the savings allowance
     */
    protected OceanusMoney calculateSavingsAllowance(final MoneyWiseUKTaxConfig pConfig) {
        return getZeroAmount();
    }

    /**
     * Calculate the dividend allowance.
     *
     * @return the dividend allowance
     */
    protected OceanusMoney calculateDividendAllowance() {
        return getZeroAmount();
    }

    /**
     * Calculate the loSavings band.
     *
     * @param pConfig    the tax configuration
     * @param pLoSavings the low savings band
     * @return the loSavings band
     */
    protected MoneyWiseTaxBand calculateLoSavingsBand(final MoneyWiseUKTaxConfig pConfig,
                                                      final MoneyWiseTaxBand pLoSavings) {
        /* Obtain the loSavings band */
        if (pLoSavings == null) {
            return new MoneyWiseTaxBand(getZeroAmount(), OceanusRate.getWholePercentage(0));
        }
        final MoneyWiseTaxBand myBand = new MoneyWiseTaxBand(pLoSavings);
        final OceanusMoney myAmount = myBand.getAmount();

        /* Obtain the preSavings income remaining after the allowance */
        final OceanusMoney myPreSavings = new OceanusMoney(pConfig.getGrossPreSavings());
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
     *
     * @return the zero amount
     */
    protected OceanusMoney getZeroAmount() {
        /* Return a zero allowance */
        final OceanusMoney myAllowance = new OceanusMoney(theAllowance);
        myAllowance.setZero();
        return myAllowance;
    }

    /**
     * Obtain the data fields.
     *
     * @return the data fields
     */
    protected static MetisFieldSet<? extends MoneyWiseUKBasicAllowance> getBaseFieldSet() {
        return FIELD_DEFS;
    }
}
