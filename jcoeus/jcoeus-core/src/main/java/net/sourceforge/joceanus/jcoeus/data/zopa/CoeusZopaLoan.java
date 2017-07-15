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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa Loan.
 */
public class CoeusZopaLoan
        extends CoeusLoan {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusZopaLoan.class, CoeusLoan.getBaseFieldSet());

    /**
     * LoanBookItem Field Id.
     */
    private static final MetisDataField FIELD_BOOKITEM = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEM.getValue());

    /**
     * LoanBookItemList Field Id.
     */
    private static final MetisDataField FIELD_BOOKITEMLIST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEMS.getValue());

    /**
     * Missing Field Id.
     */
    private static final MetisDataField FIELD_MISSING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MISSING.getValue());

    /**
     * The bookItem.
     */
    private CoeusZopaLoanBookItem theBookItem;

    /**
     * The list of bookItems.
     */
    private final List<CoeusZopaLoanBookItem> theBookItems;

    /**
     * The Missing.
     */
    private final TethysDecimal theMissing;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loanBookItem
     */
    protected CoeusZopaLoan(final CoeusZopaMarket pMarket,
                            final CoeusZopaLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItems = new ArrayList<>();
        theBookItem = pBookItem;
        theMissing = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loanId
     */
    protected CoeusZopaLoan(final CoeusZopaMarket pMarket,
                            final String pId) {
        super(pMarket, pId);
        theBookItems = new ArrayList<>();
        theBookItem = null;
        theMissing = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
    }

    @Override
    public CoeusZopaTotals getTotals() {
        return (CoeusZopaTotals) super.getTotals();
    }

    /**
     * Add a book item.
     * @param pBookItem the book item
     */
    protected void addBookItem(final CoeusZopaLoanBookItem pBookItem) {
        /* If this is the first secondary item */
        if (theBookItems.isEmpty()) {
            /* Add the original to the list */
            theBookItems.add(theBookItem);
        }

        /* Create merged item */
        theBookItem = new CoeusZopaLoanBookItem(theBookItem, pBookItem);

        /* add to list */
        theBookItems.add(pBookItem);
    }

    /**
     * Obtain the book item.
     * @return the item
     */
    public CoeusZopaLoanBookItem getBookItem() {
        return theBookItem;
    }

    /**
     * Obtain the book item iterator.
     * @return the iterator
     */
    public Iterator<CoeusZopaLoanBookItem> bookItemIterator() {
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
    protected CoeusZopaHistory newHistory() {
        return new CoeusZopaHistory(this);
    }

    @Override
    protected CoeusZopaHistory newHistory(final TethysDate pDate) {
        return new CoeusZopaHistory(this, pDate);
    }

    @Override
    protected void checkLoan() throws CoeusDataException {
        /* Obtain the book balance and adjust for missing payments */
        TethysDecimal myBookBalance = new TethysDecimal(theBookItem.getBalance());

        /* Access the total capital */
        CoeusZopaTotals myTotals = getTotals();
        TethysDecimal myLoanBalance = myTotals.getLoanBook();

        /* If this is a badDebt */
        CoeusLoanStatus myStatus = theBookItem.getStatus();
        if (CoeusLoanStatus.BADDEBT.equals(myStatus)) {
            /* Loan Balance is badDebt */
            myLoanBalance = myTotals.getBadDebt();
        }

        /* Check that this matches the book balance */
        if (!myBookBalance.equals(myLoanBalance)) {
            /* Calculate the missing payments */
            myLoanBalance = new TethysDecimal(myLoanBalance);
            myLoanBalance.subtractValue(myBookBalance);
            getMarket().recordMissingPayments(myLoanBalance);
            theMissing.addValue(myLoanBalance);
        }

        /* Check for zombieLoan */
        boolean isZombie = CoeusLoanStatus.REPAID.equals(myStatus) && theBookItem.getBalance().isNonZero();
        if (isZombie) {
            getMarket().recordZombieLoan(theBookItem.getBalance());
        }

        /* If the bookBalance is negative */
        if (hasMultipleBookItems() || theMissing.isNonZero() || isZombie) {
            /* Record the interesting loan */
            getMarket().recordInterestingLoan(this);
        }
    }

    @Override
    public TethysDecimal getBalance() {
        return theBookItem.getBalance();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        String myID = super.toString();
        if (theMissing.isNonZero()) {
            myID = "M:" + myID;
        }
        return myID;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_BOOKITEM.equals(pField)) {
            return theBookItem;
        }
        if (FIELD_BOOKITEMLIST.equals(pField)) {
            return theBookItems.isEmpty()
                                          ? MetisDataFieldValue.SKIP
                                          : theBookItems;
        }
        if (FIELD_MISSING.equals(pField)) {
            return theMissing.isZero()
                                       ? MetisDataFieldValue.SKIP
                                       : theMissing;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
