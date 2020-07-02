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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
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
    private static final MetisFieldSet<CoeusZopaLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaLoan.class);

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BOOKITEM, CoeusZopaLoan::getBookItem);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BOOKITEMS, CoeusZopaLoan::getBookItems);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGCAPITAL, CoeusZopaLoan::getMissingCapital);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGINTEREST, CoeusZopaLoan::getMissingInterest);
    }

    /**
     * The list of bookItems.
     */
    private final List<CoeusZopaLoanBookItem> theBookItems;

    /**
     * The MissingCapital.
     */
    private final TethysDecimal theMissingCapital;

    /**
     * The MissingInterest.
     */
    private final TethysDecimal theMissingInterest;

    /**
     * The UpFront interest.
     */
    private final TethysDecimal theUpFrontInterest;

    /**
     * The bookItem.
     */
    private CoeusZopaLoanBookItem theBookItem;

    /**
     * is the loan a zombie loan?
     */
    private boolean isZombie;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loanBookItem
     */
    CoeusZopaLoan(final CoeusZopaMarket pMarket,
                  final CoeusZopaLoanBookItem pBookItem) {
        this(pMarket, pBookItem.getLoanId(), pBookItem);
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loanId
     */
    CoeusZopaLoan(final CoeusZopaMarket pMarket,
                  final String pId) {
        this(pMarket, pId, null);
    }

    /**
     * Constructor.
     * @param pMarket the market
     * @param pId the loanId
     * @param pBookItem the loanBookItem
     */
    private CoeusZopaLoan(final CoeusZopaMarket pMarket,
                          final String pId,
                          final CoeusZopaLoanBookItem pBookItem) {
        super(pMarket, pId);
        theBookItems = new ArrayList<>();
        theBookItem = pBookItem;
        theMissingCapital = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theMissingInterest = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theUpFrontInterest = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);

        /* If this is a badDebt, record it */
        if (pBookItem != null
            && pBookItem.isBadDebt()) {
            setBadDebtDate(pBookItem.getBadDebtDate());
        }
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
    void addBookItem(final CoeusZopaLoanBookItem pBookItem) {
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
    private CoeusZopaLoanBookItem getBookItem() {
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
     * Obtain the book item list.
     * @return the list
     */
    private List<CoeusZopaLoanBookItem> getBookItems() {
        return theBookItems;
    }

    /**
     * Has multiple bookItems?
     * @return true/false
     */
    public boolean hasMultipleBookItems() {
        return !theBookItems.isEmpty();
    }

    /**
     * Obtain the missing capital.
     * @return the missing capital
     */
    private TethysDecimal getMissingCapital() {
        return theMissingCapital;
    }

    /**
     * Obtain the missing interest.
     * @return the missing interest
     */
    private TethysDecimal getMissingInterest() {
        return theMissingInterest;
    }

    /**
     * Add upFrontInterest.
     * @param pInterest the upFrontInterest
     */
    void addUpFrontInterest(final TethysDecimal pInterest) {
        theUpFrontInterest.addValue(pInterest);
    }

    @Override
    protected CoeusZopaHistory newHistory() {
        return new CoeusZopaHistory(this);
    }

    @Override
    protected void checkLoan() throws CoeusDataException {
        /* Obtain the book balance and adjust for missing payments */
        final TethysDecimal myBookBalance = new TethysDecimal(theBookItem.getBalance());

        /* Access the total capital */
        final CoeusZopaTotals myTotals = getTotals();
        TethysDecimal myLoanBalance = myTotals.getLoanBook();

        /* If this is a badDebt */
        final CoeusLoanStatus myStatus = theBookItem.getStatus();
        if (CoeusLoanStatus.BADDEBT.equals(myStatus)) {
            /* Loan Balance is badDebt */
            myLoanBalance = myTotals.getBadDebt();
            myLoanBalance.negate();
        }

        /* Check that this matches the book balance */
        if (!myBookBalance.equals(myLoanBalance)) {
            /* Calculate the missing payments */
            myLoanBalance = new TethysDecimal(myLoanBalance);
            myLoanBalance.subtractValue(myBookBalance);
            getMarket().recordMissingCapital(myLoanBalance);
            theMissingCapital.addValue(myLoanBalance);
        }

        /* Check bookItem interest */
        final TethysDecimal myInterest = new TethysDecimal(myTotals.getInterest());
        myInterest.subtractValue(theUpFrontInterest);
        if (!myInterest.equals(theBookItem.getInterestRepaid())) {
            myInterest.subtractValue(theBookItem.getInterestRepaid());
            getMarket().recordMissingInterest(myInterest);
            theMissingInterest.addValue(myInterest);
        }

        /* Check for zombieLoan */
        isZombie = CoeusLoanStatus.REPAID.equals(myStatus)
                   && myLoanBalance.isNonZero();
        if (isZombie) {
            getMarket().recordZombieLoan(myLoanBalance);
        }

        /* If the we have questions over the item */
        boolean isInteresting = theBookItem.getMissing().isNonZero()
                                || theMissingCapital.isNonZero();
        isInteresting |= theMissingInterest.isNonZero();
        isInteresting &= !theMissingCapital.equals(theBookItem.getMissing());
        if (isInteresting || isZombie) {
            /* Record the interesting loan */
            getMarket().recordInterestingLoan(this);
        }
    }

    @Override
    public TethysDecimal getBalance() {
        return theBookItem.getBalance();
    }

    @Override
    public MetisFieldSet<CoeusZopaLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder(super.toString());
        if (theMissingCapital.isNonZero()) {
            myBuilder.insert(0, "M:");
        }
        if (theMissingInterest.isNonZero()) {
            myBuilder.insert(0, "I:");
        }
        if (isZombie) {
            myBuilder.insert(0, "Z:");
        }
        return myBuilder.toString();
    }
}
