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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Basic UK Tax Allowance.
 */
public abstract class MoneyWiseUKBasicAllowance
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKBasicAllowance.class.getSimpleName());

    /**
     * Allowance Field Id.
     */
    private static final MetisField FIELD_ALLOWANCE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_BASIC.getValue());

    /**
     * RentalAllowance Field Id.
     */
    private static final MetisField FIELD_RENTAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_RENTAL.getValue());

    /**
     * CapitalAllowance Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_CAPITAL.getValue());

    /**
     * MarginalReduction Field Id.
     */
    private static final MetisField FIELD_MARGINAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.MARGINAL_REDUCTION.getValue());

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
        MoneyWiseTaxBand myBand = new MoneyWiseTaxBand(pLoSavings);
        TethysMoney myAmount = myBand.getAmount();

        /* Obtain the preSavings income remaining after the allowance */
        TethysMoney myPreSavings = new TethysMoney(pConfig.getGrossPreSavings());
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
        TethysMoney myAllowance = new TethysMoney(theAllowance);
        myAllowance.setZero();
        return myAllowance;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_ALLOWANCE.equals(pField)) {
            return theAllowance;
        }
        if (FIELD_RENTAL.equals(pField)) {
            return theRentalAllowance;
        }
        if (FIELD_CAPITAL.equals(pField)) {
            return theCapitalAllowance;
        }
        if (FIELD_MARGINAL.equals(pField)) {
            return theMarginalReduction;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
