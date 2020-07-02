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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
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
    private static final MetisFieldSet<CoeusFundingCircleLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleLoan.class);

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BOOKITEM, CoeusFundingCircleLoan::getLoanBookItem);
    }

    /**
     * The list of bookItems.
     */
    private final List<CoeusFundingCircleLoanBookItem> theBookItems;

    /**
     * The bookItem.
     */
    private CoeusFundingCircleLoanBookItem theBookItem;

    /**
     * The loanId.
     */
    private final Integer theLoanIdNo;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loan book item
     */
    CoeusFundingCircleLoan(final CoeusFundingCircleMarket pMarket,
                           final CoeusFundingCircleLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItems = new ArrayList<>();
        theBookItem = pBookItem;
        theLoanIdNo = getLoanId() == null ? 0 : Integer.parseInt(getLoanId());
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loan id
     */
    CoeusFundingCircleLoan(final CoeusFundingCircleMarket pMarket,
                           final String pId) {
        super(pMarket, pId);
        theBookItems = null;
        theBookItem = null;
        theLoanIdNo = Integer.parseInt(pId);
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
    }

    /**
     * Add a book item.
     * @param pBookItem the book item
     */
    void addBookItem(final CoeusFundingCircleLoanBookItem pBookItem) {
        /* If this is the first secondary item */
        if (theBookItems.isEmpty()) {
            /* Add the original to the list */
            theBookItems.add(theBookItem);
        }

        /* Create merged item */
        theBookItem = new CoeusFundingCircleLoanBookItem(theBookItem, pBookItem);

        /* add to list */
        theBookItems.add(pBookItem);
    }

    /**
     * Obtain the book item.
     * @return the book item
     */
    CoeusFundingCircleLoanBookItem getLoanBookItem() {
        return theBookItem;
    }

    /**
     * Obtain the book item iterator.
     * @return the iterator
     */
    public Iterator<CoeusFundingCircleLoanBookItem> bookItemIterator() {
        return theBookItems.iterator();
    }

    /**
     * Has multiple bookItems?
     * @return true/false
     */
    public boolean hasMultipleBookItems() {
        return !theBookItems.isEmpty();
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
    public MetisFieldSet<CoeusFundingCircleLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
