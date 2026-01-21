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
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;

/**
 * Rental Tax Scheme.
 */
public class MoneyWiseUKRoomRentalScheme
        extends MoneyWiseUKIncomeScheme {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKRoomRentalScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKRoomRentalScheme.class);

    /**
     * Constructor.
     */
    public MoneyWiseUKRoomRentalScheme() {
    }

    @Override
    protected OceanusMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                            final OceanusMoney pAmount) {
        /* Adjust against the rental allowance for room rental */
        OceanusMoney myRemaining = adjustForAllowance(pConfig.getRentalAllowance(), pAmount);

        /* If we have any income left */
        if (myRemaining.isNonZero()) {
            /* Adjust the basic allowance */
            myRemaining = super.adjustAllowances(pConfig, myRemaining);
        }

        /* Return unallocated income */
        return myRemaining;
    }

    @Override
    protected OceanusMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
                                                final OceanusMoney pAmount) {
        /* Obtain the amount covered by the room rental allowance */
        OceanusMoney myAmount = getAmountInBand(pConfig.getCapitalAllowance(), pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            final OceanusMoney myRemaining = new OceanusMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by basic allowance */
            final OceanusMoney myXtra = super.getAmountInAllowance(pConfig, myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new OceanusMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    @Override
    public MetisFieldSet<MoneyWiseUKRoomRentalScheme> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
