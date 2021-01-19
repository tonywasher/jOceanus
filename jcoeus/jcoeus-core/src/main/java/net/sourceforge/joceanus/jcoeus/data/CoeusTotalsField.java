/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;

/**
 * Transaction FieldIds.
 */
public enum CoeusTotalsField
        implements MetisDataFieldId {
    /**
     * ID.
     */
    ID(CoeusResource.DATA_ID),

    /**
     * Market.
     */
    MARKET(CoeusResource.DATA_MARKET),

    /**
     * Loan.
     */
    LOAN(CoeusResource.DATA_LOAN),

    /**
     * Date.
     */
    DATE(CoeusResource.DATA_DATE),

    /**
     * Description.
     */
    DESC(CoeusResource.DATA_DESC),

    /**
     * TransType.
     */
    TRANSTYPE(CoeusResource.DATA_TRANSTYPE),

    /**
     * Transaction.
     */
    TRANSACTION(CoeusResource.DATA_TRANSACTION),

    /**
     * SourceValue.
     */
    SOURCEVALUE(CoeusResource.DATA_SOURCEVALUE),

    /**
     * AssetValue.
     */
    ASSETVALUE(CoeusResource.DATA_ASSETVALUE),

    /**
     * Invested.
     */
    INVESTED(CoeusResource.DATA_INVESTED),

    /**
     * Holding.
     */
    HOLDING(CoeusResource.DATA_HOLDING),

    /**
     * LoanBook.
     */
    LOANBOOK(CoeusResource.DATA_LOANBOOK),

    /**
     * Earnings.
     */
    EARNINGS(CoeusResource.DATA_EARNINGS),

    /**
     * TaxableEarnings.
     */
    TAXABLEEARNINGS(CoeusResource.DATA_TAXABLEEARNINGS),

    /**
     * Interest.
     */
    INTEREST(CoeusResource.DATA_INTEREST),

    /**
     * NettInterest.
     */
    NETTINTEREST(CoeusResource.DATA_NETTINTEREST),

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST(CoeusResource.DATA_BADDEBTINTEREST),

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL(CoeusResource.DATA_BADDEBTCAPITAL),

    /**
     * Fees.
     */
    FEES(CoeusResource.DATA_FEES),

    /**
     * CashBack.
     */
    CASHBACK(CoeusResource.DATA_CASHBACK),

    /**
     * XferPayment.
     */
    XFERPAYMENT(CoeusResource.DATA_XFERPAYMENT),

    /**
     * Losses.
     */
    LOSSES(CoeusResource.DATA_LOSSES),

    /**
     * BadDebt.
     */
    BADDEBT(CoeusResource.DATA_BADDEBT),

    /**
     * Recovered.
     */
    RECOVERED(CoeusResource.DATA_RECOVERED),

    /**
     * LoanRateOfReturn.
     */
    LOANROR(CoeusResource.DATA_LOANROR),

    /**
     * AssetRateOfReturn.
     */
    ASSETROR(CoeusResource.DATA_ASSETROR),

    /**
     * Delta.
     */
    DELTA(CoeusResource.DATA_DELTA),

    /**
     * Balance.
     */
    BALANCE(CoeusResource.DATA_BALANCE);

    /**
     * The FieldId.
     */
    private final MetisDataFieldId theField;

    /**
     * Constructor.
     * @param pField the field
     */
    CoeusTotalsField(final MetisDataFieldId pField) {
        theField = pField;
    }

    @Override
    public String getId() {
        return theField.getId();
    }

    @Override
    public String toString() {
        return getId();
    }
}
