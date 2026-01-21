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
package io.github.tonywasher.joceanus.moneywise.tax.uk;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseMarginalReduction;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxResource;

import java.util.Iterator;

/**
 * Savings UK Tax Allowance.
 */
public class MoneyWiseUKSavingsAllowance
        extends MoneyWiseUKBasicAllowance {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKSavingsAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKSavingsAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_SAVINGS, MoneyWiseUKSavingsAllowance::getSavingsAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_DIVIDEND, MoneyWiseUKSavingsAllowance::getDividendAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.LIMIT_ADDALLOWANCE, MoneyWiseUKSavingsAllowance::getAdditionalAllowanceLimit);
    }

    /**
     * SavingsAllowance.
     */
    private final OceanusMoney theSavingsAllowance;

    /**
     * DividendAllowance.
     */
    private final OceanusMoney theDividendAllowance;

    /**
     * AdditionalAllowanceLimit.
     */
    private final OceanusMoney theAddAllowLimit;

    /**
     * Constructor.
     *
     * @param pAllowance         the allowance
     * @param pRentalAllowance   the rental allowance
     * @param pCapitalAllowance  the capital allowance
     * @param pSavingsAllowance  the savings allowance
     * @param pDividendAllowance the dividend allowance
     * @param pAddAllowLimit     the additional allowance limit
     */
    protected MoneyWiseUKSavingsAllowance(final OceanusMoney pAllowance,
                                          final OceanusMoney pRentalAllowance,
                                          final OceanusMoney pCapitalAllowance,
                                          final OceanusMoney pSavingsAllowance,
                                          final OceanusMoney pDividendAllowance,
                                          final OceanusMoney pAddAllowLimit) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, MoneyWiseMarginalReduction.ONEINTWO);
        theSavingsAllowance = pSavingsAllowance;
        theDividendAllowance = pDividendAllowance;
        theAddAllowLimit = pAddAllowLimit;
    }

    /**
     * Obtain the savings allowance.
     *
     * @return the Allowance
     */
    protected OceanusMoney getSavingsAllowance() {
        return theSavingsAllowance;
    }

    /**
     * Obtain the dividend allowance.
     *
     * @return the Allowance
     */
    protected OceanusMoney getDividendAllowance() {
        return theDividendAllowance;
    }

    /**
     * Obtain the additional Allowance limit.
     *
     * @return the Limit
     */
    protected OceanusMoney getAdditionalAllowanceLimit() {
        return theAddAllowLimit;
    }

    @Override
    protected OceanusMoney calculateBasicAllowance(final MoneyWiseUKTaxConfig pConfig) {
        /* Determine Basic allowance */
        OceanusMoney myAllowance = getAllowance();

        /* If we have additional tax possible and we are above the allowance limit */
        final OceanusMoney myGross = pConfig.getGrossTaxable();
        if (myGross.compareTo(theAddAllowLimit) > 0) {
            /* Calculate and apply the reduction */
            final OceanusMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAddAllowLimit);
            myAllowance = new OceanusMoney(myAllowance);
            myAllowance.subtractAmount(myReduction);

            /* If we have reduced below zero */
            if (!myAllowance.isPositive()) {
                /* Set the allowance to zero */
                myAllowance.setZero();
            }
        }

        /* Return the allowance */
        return myAllowance;
    }

    @Override
    protected OceanusMoney calculateSavingsAllowance(final MoneyWiseUKTaxConfig pConfig) {
        /* Obtain the gross taxable and allowance */
        final OceanusMoney myGross = pConfig.getGrossTaxable();
        final OceanusMoney myBoundary = new OceanusMoney(pConfig.getAllowance());

        /* Obtain the tax bands */
        final MoneyWiseUKTaxBands myBands = pConfig.getTaxYear().getTaxBands();
        final Iterator<MoneyWiseTaxBand> myIterator = myBands.getStandardSet().iterator();

        /* If we have a basic band */
        if (myIterator.hasNext()) {
            /* If we are only a basic taxPayer return the full allowance */
            myBoundary.addAmount(myIterator.next().getAmount());
            if (myBoundary.compareTo(myGross) >= 0) {
                return theSavingsAllowance;
            }
        }

        /* If we have a high band */
        final OceanusMoney myAllowance = new OceanusMoney(theSavingsAllowance);
        if (myIterator.hasNext()) {
            /* If we are only a high taxPayer return the half allowance */
            myBoundary.addAmount(myIterator.next().getAmount());
            if (myBoundary.compareTo(myGross) >= 0) {
                myAllowance.divide(2);
                return myAllowance;
            }
        }

        /* No Allowance */
        myAllowance.setZero();
        return theSavingsAllowance;
    }

    @Override
    protected OceanusMoney calculateDividendAllowance() {
        return theDividendAllowance;
    }

    @Override
    public MetisFieldSet<MoneyWiseUKSavingsAllowance> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
