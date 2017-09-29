/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
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
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusRateSetterLoan.class, CoeusLoan.getBaseFieldSet());

    /**
     * LoanBookItem Field Id.
     */
    private static final MetisDataField FIELD_BOOKITEM = FIELD_DEFS.declareLocalField(CoeusResource.DATA_BOOKITEM.getValue());

    /**
     * The market.
     */
    private final CoeusRateSetterLoanBookItem theBookItem;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loan book item
     */
    protected CoeusRateSetterLoan(final CoeusRateSetterMarket pMarket,
                                  final CoeusRateSetterLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItem = pBookItem;
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loan id
     */
    protected CoeusRateSetterLoan(final CoeusRateSetterMarket pMarket,
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
    public CoeusRateSetterLoanBookItem getLoanBookItem() {
        return theBookItem;
    }

    @Override
    protected CoeusRateSetterHistory newHistory() {
        return new CoeusRateSetterHistory(this);
    }

    @Override
    protected CoeusRateSetterHistory newHistory(final TethysDate pDate) {
        return new CoeusRateSetterHistory(this, pDate);
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
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_BOOKITEM.equals(pField)) {
            return theBookItem;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
