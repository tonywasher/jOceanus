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
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;

/**
 * Additional Band UK Tax Allowance.
 */
public class MoneyWiseUKAdditionalAllowance
        extends MoneyWiseUKAgeAllowance {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKAdditionalAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKAdditionalAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.LIMIT_ADDALLOWANCE, MoneyWiseUKAdditionalAllowance::getAdditionalAllowanceLimit);
    }

    /**
     * IncomeBoundary.
     */
    private final OceanusMoney theAddAllowLimit;

    /**
     * Constructor.
     *
     * @param pAllowance         the allowance
     * @param pRentalAllowance   the rental allowance
     * @param pCapitalAllowance  the capital allowance
     * @param pLoAgeAllowance    the low age allowance
     * @param pHiAgeAllowance    the high age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     * @param pAddAllowLimit     the additional allowance limit
     */
    protected MoneyWiseUKAdditionalAllowance(final OceanusMoney pAllowance,
                                             final OceanusMoney pRentalAllowance,
                                             final OceanusMoney pCapitalAllowance,
                                             final OceanusMoney pLoAgeAllowance,
                                             final OceanusMoney pHiAgeAllowance,
                                             final OceanusMoney pAgeAllowanceLimit,
                                             final OceanusMoney pAddAllowLimit) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance, pHiAgeAllowance, pAgeAllowanceLimit);
        theAddAllowLimit = pAddAllowLimit;
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
        /* Determine AgeRelated allowance */
        OceanusMoney myAllowance = super.calculateBasicAllowance(pConfig);

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
    public MetisFieldSet<MoneyWiseUKAdditionalAllowance> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
