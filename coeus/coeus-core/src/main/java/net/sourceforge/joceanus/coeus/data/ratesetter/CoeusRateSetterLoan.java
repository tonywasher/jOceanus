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
package net.sourceforge.joceanus.coeus.data.ratesetter;

import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * RateSetter Loan.
 */
public class CoeusRateSetterLoan
        extends CoeusLoan {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterLoan.class);

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BOOKITEM, CoeusRateSetterLoan::getLoanBookItem);
    }

    /**
     * The market.
     */
    private final CoeusRateSetterLoanBookItem theBookItem;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loan book item
     */
    CoeusRateSetterLoan(final CoeusRateSetterMarket pMarket,
                        final CoeusRateSetterLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItem = pBookItem;
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loan id
     */
    CoeusRateSetterLoan(final CoeusRateSetterMarket pMarket,
                        final String pId) {
        super(pMarket, pId);
        theBookItem = null;
    }

    /**
     * Constructor.
     * @param pLoan the loan
     * @param pRange the dateRange
     */
    CoeusRateSetterLoan(final CoeusRateSetterLoan pLoan,
                        final OceanusDateRange pRange) {
        super(pLoan, pRange);
        theBookItem = pLoan.getLoanBookItem();
    }

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    @Override
    public CoeusRateSetterTotals getTotals() {
        return (CoeusRateSetterTotals) super.getTotals();
    }

    @Override
    public OceanusMoney getInitialLoan() {
        return (OceanusMoney) super.getInitialLoan();
    }

    /**
     * Obtain the book item.
     * @return the book item
     */
    CoeusRateSetterLoanBookItem getLoanBookItem() {
        return theBookItem;
    }

    @Override
    protected CoeusRateSetterHistory newHistory() {
        return new CoeusRateSetterHistory(this);
    }

    @Override
    protected void checkLoan() throws CoeusDataException {
        final OceanusMoney myBookBalance = theBookItem.getBalance();
        if (!myBookBalance.equals(getTotals().getLoanBook())) {
            throw new CoeusDataException(this, "Bad Balance");
        }
    }

    @Override
    public OceanusMoney getBalance() {
        return theBookItem.getBalance();
    }

    @Override
    public MetisFieldSet<CoeusRateSetterLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
