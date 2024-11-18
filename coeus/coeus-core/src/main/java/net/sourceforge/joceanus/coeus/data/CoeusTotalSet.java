/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;

/**
 * TotalSet.
 */
public enum CoeusTotalSet {
    /**
     * Invested.
     */
    INVESTED,

    /**
     * Earnings.
     */
    EARNINGS,

    /**
     * Losses.
     */
    LOSSES,

    /**
     * TaxableEarnings.
     */
    TAXABLEEARNINGS,

    /**
     * Interest.
     */
    INTEREST,

    /**
     * nettInterest.
     */
    NETTINTEREST,

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST,

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL,

    /**
     * Fees.
     */
    FEES,

    /**
     * Shield.
     */
    SHIELD,

    /**
     * CashBack.
     */
    CASHBACK,

    /**
     * XferPayment.
     */
    XFERPAYMENT,

    /**
     * BadDebt.
     */
    BADDEBT,

    /**
     * Recovered.
     */
    RECOVERED,

    /**
     * LoanROR.
     */
    LOANROR,

    /**
     * AssetROR.
     */
    ASSETROR,

    /**
     * Holding.
     */
    HOLDING,

    /**
     * LoanBook.
     */
    LOANBOOK;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CoeusResource.getKeyForTotalSet(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain balance field for TotalSet.
     * @return the balance field
     */
    public MetisDataFieldId getBalanceField() {
        return CoeusTotals.getBalanceField(this);
    }
}
