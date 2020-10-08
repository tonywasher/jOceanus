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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Transaction Totals History.
 */
public abstract class CoeusHistory
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusHistory.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET, CoeusHistory::getMarket);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOAN, CoeusHistory::getLoan);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RANGE, CoeusHistory::getDateRange);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY, CoeusHistory::getHistory);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_INITIAL, CoeusHistory::getInitial);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_TOTALS, CoeusHistory::getTotals);
    }

    /**
     * The market.
     */
    private final CoeusMarket theMarket;

    /**
     * The loan.
     */
    private final CoeusLoan theLoan;

    /**
     * The range for the totals.
     */
    private final TethysDateRange theRange;

    /**
     * The individual totals.
     */
    private final List<CoeusTotals> theHistory;

    /**
     * The initial totals.
     */
    private final CoeusTotals theInitial;

    /**
     * The summary totals.
     */
    private CoeusTotals theTotals;

    /**
     * Constructor.
     * @param pTotals the totals
     */
    protected CoeusHistory(final CoeusTotals pTotals) {
        /* Record parameters */
        theInitial = pTotals;
        theTotals = pTotals;

        /* Record details */
        theMarket = pTotals.getMarket();
        theLoan = pTotals.getLoan();
        theRange = null;

        /* Create the list */
        theHistory = new ArrayList<>();
    }

    /**
     * Constructor for partial view.
     * @param pHistory the base history
     * @param pRange the date range
     */
    protected CoeusHistory(final CoeusHistory pHistory,
                           final TethysDateRange pRange) {
        /* Record details */
        theMarket = pHistory.getMarket();
        theLoan = pHistory.getLoan();
        theRange = pRange;

        /* Determine indices */
        final int[] myIndices = findIndicesForRange(pHistory, pRange);

        /* Create the cut down history */
        theHistory = pHistory.getHistory().subList(myIndices[0], myIndices[1]);

        /* Determine bounds */
        final boolean empty = isEmpty();
        theInitial = empty
                      ? pHistory.getInitial()
                      :  theHistory.get(0).getPrevious();
        theTotals = empty
                      ? theInitial
                      : theHistory.get(theHistory.size() - 1);

        /* If we need to calculate a delta */
        if (theRange.getStart() != null) {
            theTotals = newTotals(theTotals, null);
            theTotals.calculateDelta(theInitial);
        }
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public CoeusLoan getLoan() {
        return theLoan;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDateRange getDateRange() {
        return theRange;
    }

    /**
     * Is the history empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theHistory.isEmpty();
    }

    /**
     * Obtain the initial totals.
     * @return the totals
     */
    public CoeusTotals getInitial() {
        return theInitial;
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public CoeusTotals getTotals() {
        return theTotals;
    }

    /**
     * Obtain the history.
     * @return the history
     */
    List<CoeusTotals> getHistory() {
        return theHistory;
    }

    /**
     * Obtain the history iterator.
     * @return the iterator
     */
    public Iterator<CoeusTotals> historyIterator() {
        return theHistory.iterator();
    }

    /**
     * Add transaction to history.
     * @param pTrans the transaction to add
     */
    void addTransactionToHistory(final CoeusTransaction pTrans) {
        /* Only allowed for base history */
        if (theRange == null) {
            /* Create the new entry and add to the list */
            final CoeusTotals myTotals = newTotals(theTotals, pTrans);
            myTotals.addTransactionToTotals(pTrans);
            theHistory.add(myTotals);
            theTotals = myTotals;
        }
    }

    /**
     * Obtain indices for ranged view.
     * @param pHistory the base history
     * @param pRange the date range
     * @return the indices
     */
    private static int[] findIndicesForRange(final CoeusHistory pHistory,
                                             final TethysDateRange pRange) {
        /* Determine the dates */
        final TethysDate myStartDate = pRange.getStart();
        final TethysDate myEndDate = pRange.getEnd();

        /* Create indices */
        int myIndex = 0;
        int myStart = -1;

        /* Loop through the totals */
        final Iterator<CoeusTotals> myIterator = pHistory.historyIterator();
        while (myIterator.hasNext()) {
            final CoeusTotals myTotals = myIterator.next();
            final TethysDate myDate = myTotals.getDate();

            /* Break loop if we have hit the end */
            if (myEndDate.compareTo(myDate) < 0) {
                break;
            }

            /* If we have not yet hit the start, check for start being hit */
            if (myStart == -1
                && (myStartDate == null
                    || myStartDate.compareTo(myDate) <= 0)) {
                /* Record the start */
                myStart = myIndex;
            }

            /* increment the index */
            myIndex++;
        }

        /* Handle empty list */
        return myStart == -1
                ? new int[] { 0, 0 }
                : new int[] { myStart, myIndex };
    }

    /**
     * Create a new Totals element.
     * @param pTotals the existing totals
     * @param pTrans the transaction
     * @return the new totals
     */
    protected abstract CoeusTotals newTotals(CoeusTotals pTotals,
                                             CoeusTransaction pTrans);

    /**
     * Clear the history.
     */
    public void clear() {
        if (theRange == null) {
            theTotals = theInitial;
            theHistory.clear();
        }
    }

    @Override
    public String toString() {
        return theTotals.toString();
    }
}
