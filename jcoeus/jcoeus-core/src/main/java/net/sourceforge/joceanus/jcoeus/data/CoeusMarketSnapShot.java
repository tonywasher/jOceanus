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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Loan Market SnapShot.
 */
public class CoeusMarketSnapShot
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusMarketSnapShot.class);

    /**
     * Market Field Id.
     */
    private static final MetisDataField FIELD_MARKET = FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET);

    /**
     * Date Field Id.
     */
    private static final MetisDataField FIELD_DATE = FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE);

    /**
     * LoanMap Field Id.
     */
    private static final MetisDataField FIELD_LOANS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANMAP);

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY);

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
     * LoanMap.
     */
    private final Map<String, CoeusLoan> theLoanMap;

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
    protected CoeusMarketSnapShot(final CoeusMarket pMarket,
                                  final TethysDate pDate) {
        /* Store parameters */
        theMarket = pMarket;
        theDate = pDate;

        /* Create loan map/list */
        theLoanMap = new HashMap<>();
        theLoanList = new ArrayList<>();

        /* Create the history */
        theHistory = determineHistory();

        /* Sort the loans */
        sortLoans();
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
     * Sort the loans.
     */
    private void sortLoans() {
        theLoanList.sort((l, r) -> l.compareTo(r));
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
     * Is there history for the loan?
     * @param pLoan the loan
     * @return true/false
     */
    public boolean availableLoan(final CoeusLoan pLoan) {
        return theLoanMap.containsKey(pLoan.getLoanId());
    }

    /**
     * Determine the history.
     * @return the history
     */
    private CoeusHistory determineHistory() {
        /* Create the history */
        final CoeusHistory myHistory = theMarket.newHistory();

        /* Loop through the transactions */
        final Iterator<CoeusTransaction> myIterator = theMarket.transactionIterator();
        while (myIterator.hasNext()) {
            final CoeusTransaction myTransaction = myIterator.next();
            final TethysDate myDate = myTransaction.getDate();

            /* If we have gone past the date, break the loop */
            if (myDate.compareTo(theDate) > 0) {
                break;
            }

            /* Adjust the history */
            myHistory.addTransactionToHistory(myTransaction);

            /* Note badDebt */
            if (CoeusTransactionType.BADDEBT.equals(myTransaction.getTransType())) {
                hasBadDebt = true;
            }

            /* If the item has a loan */
            CoeusLoan myLoan = myTransaction.getLoan();
            if (myLoan != null) {
                /* Obtain the snapShot loan */
                myLoan = getSnapShotLoan(myLoan);

                /* Add to the loans history */
                myLoan.addTransactionToHistory(myTransaction);
            }
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Obtain the loan for the snapShot.
     * @param pLoan the market loan
     * @return the loan
     */
    private CoeusLoan getSnapShotLoan(final CoeusLoan pLoan) {
        /* Look up existing snapShot */
        final String myId = pLoan.getLoanId();
        return theLoanMap.computeIfAbsent(myId, i -> newSnapShotLoan(pLoan));
    }

    /**
     * Create a new loan for the snapShot.
     * @param pLoan the market loan
     * @return the loan
     */
    private CoeusLoan newSnapShotLoan(final CoeusLoan pLoan) {
        /* Create and record it */
        final CoeusLoan myLoan = theMarket.newLoan(pLoan.getLoanId());
        theLoanList.add(myLoan);

        /* Ensure that the badDebt date is copied */
        myLoan.setBadDebtDate(pLoan.getBadDebtDate());

        /* return the loan SnapShot */
        return myLoan;
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theMarket).append('@').append(theDate);
        return myBuilder.toString();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_LOANS.equals(pField)) {
            return theLoanMap;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
