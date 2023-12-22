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
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Rental Tax Scheme.
 */
public class MoneyWiseXUKRoomRentalScheme
        extends MoneyWiseXUKIncomeScheme {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKRoomRentalScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKRoomRentalScheme.class);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseXUKTaxConfig pConfig,
                                           final TethysMoney pAmount) {
        /* Adjust against the rental allowance for room rental */
        TethysMoney myRemaining = adjustForAllowance(pConfig.getRentalAllowance(), pAmount);

        /* If we have any income left */
        if (myRemaining.isNonZero()) {
            /* Adjust the basic allowance */
            myRemaining = super.adjustAllowances(pConfig, myRemaining);
        }

        /* Return unallocated income */
        return myRemaining;
    }

    @Override
    protected TethysMoney getAmountInAllowance(final MoneyWiseXUKTaxConfig pConfig,
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the room rental allowance */
        TethysMoney myAmount = getAmountInBand(pConfig.getCapitalAllowance(), pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            final TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by basic allowance */
            final TethysMoney myXtra = super.getAmountInAllowance(pConfig, myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    @Override
    public MetisFieldSet<MoneyWiseXUKRoomRentalScheme> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
