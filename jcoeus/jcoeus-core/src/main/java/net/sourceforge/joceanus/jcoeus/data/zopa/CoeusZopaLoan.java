/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
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
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaLoan.class.getSimpleName(), CoeusLoan.getBaseFields());

    /**
     * LoanBookItem Field Id.
     */
    private static final MetisField FIELD_BOOKITEM = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEM.getValue());

    /**
     * LoanBookItemList Field Id.
     */
    private static final MetisField FIELD_BOOKITEMLIST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEMS.getValue());

    /**
     * Missing Field Id.
     */
    private static final MetisField FIELD_MISSING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MISSING.getValue());

    /**
     * The list of bookItems.
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
            /* switch the original to the list and replace with a totalling item */
            theBookItems.add(theBookItem);
            theBookItem = new CoeusZopaLoanBookItem(theBookItem);
        }

        /* add to list and totalling item */
        theBookItem.addBookItem(pBookItem);
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
        myBookBalance.addValue(theBookItem.getMissing());

        /* Access the total capital */
        CoeusZopaTotals myTotals = getTotals();
        TethysDecimal myLoanBalance = myTotals.getTotalLoanBook();

        /* If this is a badDebt */
        if (CoeusLoanStatus.BADDEBT.equals(theBookItem.getStatus())) {
            /* Loan Balance is badDebt */
            myLoanBalance = myTotals.getTotalBadDebt();
        }

        /* Check that this matches the book balance */
        if (!myBookBalance.equals(myLoanBalance)) {
            /* Calculate the missing payments */
            myLoanBalance = new TethysDecimal(myLoanBalance);
            myLoanBalance.subtractValue(myBookBalance);
            getMarket().recordMissingPayments(myLoanBalance);
            theMissing.addValue(myLoanBalance);
        }
    }

    @Override
    public TethysDecimal getBalance() {
        return theBookItem.getBalance();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_BOOKITEM.equals(pField)) {
            return theBookItem;
        }
        if (FIELD_BOOKITEMLIST.equals(pField)) {
            return theBookItems.isEmpty()
                                          ? MetisFieldValue.SKIP
                                          : theBookItems;
        }
        if (FIELD_MISSING.equals(pField)) {
            return theMissing.isNonZero()
                                          ? MetisFieldValue.SKIP
                                          : theMissing;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
