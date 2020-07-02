/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

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

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    @Override
    public CoeusRateSetterTotals getTotals() {
        return (CoeusRateSetterTotals) super.getTotals();
    }

    @Override
    public TethysMoney getInitialLoan() {
        return (TethysMoney) super.getInitialLoan();
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
        final TethysMoney myBookBalance = theBookItem.getBalance();
        if (!myBookBalance.equals(getTotals().getLoanBook())) {
            throw new CoeusDataException(this, "Bad Balance");
        }
    }

    @Override
    public TethysMoney getBalance() {
        return theBookItem.getBalance();
    }

    @Override
    public MetisFieldSet<CoeusRateSetterLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
