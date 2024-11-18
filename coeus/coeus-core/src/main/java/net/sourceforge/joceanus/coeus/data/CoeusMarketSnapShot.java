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
package net.sourceforge.joceanus.coeus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Loan Market SnapShot.
 */
public class CoeusMarketSnapShot
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusMarketSnapShot> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusMarketSnapShot.class);

    /*
     * Field Ids.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET, CoeusMarketSnapShot::getMarket);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE, CoeusMarketSnapShot::getDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANS, CoeusMarketSnapShot::getLoans);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY, CoeusMarketSnapShot::getHistory);
    }

    /**
     * Loan Market.
     */
    private final CoeusMarket theMarket;

    /**
     * Date.
     */
    private final TethysDate theDate;

    /**
     * LoanList.
     */
    private final List<CoeusLoan> theLoanList;

    /**
     * History.
     */
    private final CoeusHistory theHistory;

    /**
     * Do we have badDebt?
     */
    private boolean hasBadDebt;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pDate the snapshot date
     */
    CoeusMarketSnapShot(final CoeusMarket pMarket,
                        final TethysDate pDate) {
        /* Store parameters */
        theMarket = pMarket;
        theDate = pDate;
        final TethysDateRange myRange = new TethysDateRange(null, pDate);

        /* Create loan list */
        theLoanList = new ArrayList<>();

        /* Create the history */
        theHistory = pMarket.viewHistory(myRange);
        setFlags();

        /* Loop through the market loans */
        final Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();
            final CoeusLoan myView = theMarket.viewLoan(myLoan, myRange);
            if (!myLoan.isEmpty()) {
                theLoanList.add(myView);
            }
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
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain loanList.
     * @return the loans
     */
    private List<CoeusLoan> getLoans() {
        return theLoanList;
    }

    /**
     * Obtain history.
     * @return the history
     */
    public CoeusHistory getHistory() {
        return theHistory;
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public CoeusTotals getTotals() {
        return theHistory.getTotals();
    }

    /**
     * Obtain an iterator for the sorted loans.
     * @return the iterator
     */
    public Iterator<CoeusLoan> loanIterator() {
        return theLoanList.iterator();
    }

    /**
     * Do we have badDebt?
     * @return true/false
     */
    public boolean hasBadDebt() {
        return hasBadDebt;
    }

    /**
     * Set flags.
     */
    private void setFlags() {
        /* Loop through the totals */
        final Iterator<CoeusTotals> myIterator = theHistory.historyIterator();
        while (myIterator.hasNext()) {
            final CoeusTotals myTotals = myIterator.next();

            /* Detect badDebt */
            if (CoeusTransactionType.BADDEBT.equals(myTotals.getTransType())) {
                hasBadDebt = true;
                return;
            }
        }
    }

    /**
     * Obtain the dateRange of this snapshot.
     * @return the dateRange.
     */
    public TethysDateRange getDateRange() {
        final CoeusTotals myFirst = theHistory.getHistory().iterator().next();
        return new TethysDateRange(myFirst.getDate(), theDate);
    }

    @Override
    public String toString() {
        return String.valueOf(theMarket) + '@' + theDate;
    }

    @Override
    public MetisFieldSet<CoeusMarketSnapShot> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
