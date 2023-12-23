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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Additional Band UK Tax Allowance.
 */
public class MoneyWiseXUKAdditionalAllowance
        extends MoneyWiseXUKAgeAllowance {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKAdditionalAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKAdditionalAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.LIMIT_ADDALLOWANCE, MoneyWiseXUKAdditionalAllowance::getAdditionalAllowanceLimit);
    }

    /**
     * IncomeBoundary.
     */
    private final TethysMoney theAddAllowLimit;

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pLoAgeAllowance the low age allowance
     * @param pHiAgeAllowance the high age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     * @param pAddAllowLimit the additional allowance limit
     */
    protected MoneyWiseXUKAdditionalAllowance(final TethysMoney pAllowance,
                                              final TethysMoney pRentalAllowance,
                                              final TethysMoney pCapitalAllowance,
                                              final TethysMoney pLoAgeAllowance,
                                              final TethysMoney pHiAgeAllowance,
                                              final TethysMoney pAgeAllowanceLimit,
                                              final TethysMoney pAddAllowLimit) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance, pHiAgeAllowance, pAgeAllowanceLimit);
        theAddAllowLimit = pAddAllowLimit;
    }

    /**
     * Obtain the additional Allowance limit.
     * @return the Limit
     */
    protected TethysMoney getAdditionalAllowanceLimit() {
        return theAddAllowLimit;
    }

    @Override
    protected TethysMoney calculateBasicAllowance(final MoneyWiseXUKTaxConfig pConfig) {
        /* Determine AgeRelated allowance */
        TethysMoney myAllowance = super.calculateBasicAllowance(pConfig);

        /* If we have additional tax possible and we are above the allowance limit */
        final TethysMoney myGross = pConfig.getGrossTaxable();
        if (myGross.compareTo(theAddAllowLimit) > 0) {
            /* Calculate and apply the reduction */
            final TethysMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAddAllowLimit);
            myAllowance = new TethysMoney(myAllowance);
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
    public MetisFieldSet<MoneyWiseXUKAdditionalAllowance> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
