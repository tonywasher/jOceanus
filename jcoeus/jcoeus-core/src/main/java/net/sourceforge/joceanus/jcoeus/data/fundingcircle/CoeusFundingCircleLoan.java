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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle Loan.
 */
public class CoeusFundingCircleLoan
        extends CoeusLoan {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusFundingCircleLoan.class, CoeusLoan.getBaseFieldSet());

    /**
     * LoanBookItem Field Id.
     */
    private static final MetisDataField FIELD_BOOKITEM = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEM.getValue());

    /**
     * The bookItem.
     */
    private final CoeusFundingCircleLoanBookItem theBookItem;

    /**
     * The loanId.
     */
    private final Integer theLoanIdNo;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loan book item
     */
    protected CoeusFundingCircleLoan(final CoeusFundingCircleMarket pMarket,
                                     final CoeusFundingCircleLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItem = pBookItem;
        theLoanIdNo = Integer.parseInt(getLoanId());
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loan id
     */
    protected CoeusFundingCircleLoan(final CoeusFundingCircleMarket pMarket,
                                     final String pId) {
        super(pMarket, pId);
        theBookItem = null;
        theLoanIdNo = Integer.parseInt(pId);
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
    }

    /**
     * Obtain the book item.
     * @return the book item
     */
    public CoeusFundingCircleLoanBookItem getLoanBookItem() {
        return theBookItem;
    }

    @Override
    public TethysMoney getInitialLoan() {
        return (TethysMoney) super.getInitialLoan();
    }

    @Override
    public CoeusFundingCircleTotals getTotals() {
        return (CoeusFundingCircleTotals) super.getTotals();
    }

    @Override
    protected CoeusFundingCircleHistory newHistory() {
        return new CoeusFundingCircleHistory(this);
    }

    @Override
    protected CoeusFundingCircleHistory newHistory(final TethysDate pDate) {
        return new CoeusFundingCircleHistory(this, pDate);
    }

    @Override
    public int compareTo(final CoeusLoan pThat) {
        return theLoanIdNo.compareTo(((CoeusFundingCircleLoan) pThat).theLoanIdNo);
    }

    @Override
    protected void checkLoan() throws CoeusDataException {
        /* Access details */
        final TethysMoney myBookBalance = getBalance();
        final CoeusFundingCircleTotals myTotals = getTotals();
        TethysMoney myLoanBalance = myTotals.getLoanBook();

        /* If this is a badDebt */
        if (theBookItem.getStatus().isBadDebt()) {
            /* Loan Balance is badDebt */
            myLoanBalance = new TethysMoney(myTotals.getBadDebt());
            myLoanBalance.negate();
        }

        /* Check that this matches the book balance */
        if (!myBookBalance.equals(myLoanBalance)) {
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
