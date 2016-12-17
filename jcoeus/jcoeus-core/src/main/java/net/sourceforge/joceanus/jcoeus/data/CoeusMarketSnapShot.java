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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Loan Market SnapShot.
 */
public class CoeusMarketSnapShot
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusMarketSnapShot.class.getSimpleName());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * LoanMap Field Id.
     */
    private static final MetisField FIELD_LOANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANMAP.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

    /**
     * Loan Market.
     */
    private final CoeusMarket theMarket;

    /**
     * Date.
     */
    private final TethysDate theDate;

    /**
     * Loans.
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

        /* Create loan map */
        theLoanMap = new LinkedHashMap<>();

        /* Create the history */
        theHistory = determineHistory();
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
     * Obtain the loan iterator.
     * @return the iterator
     */
    public Iterator<CoeusLoan> loanIterator() {
        return theLoanMap.values().iterator();
    }

    /**
     * Do we have badDebt?
     * @return true/false
     */
    public boolean hasBadDebt() {
        return hasBadDebt;
    }

    /**
     * Determine the history.
     * @return the history
     */
    private CoeusHistory determineHistory() {
        /* Create the history */
        CoeusHistory myHistory = theMarket.newHistory();

        /* Loop through the transactions */
        Iterator<CoeusTransaction> myIterator = theMarket.transactionIterator();
        while (myIterator.hasNext()) {
            CoeusTransaction myTransaction = myIterator.next();
            TethysDate myDate = myTransaction.getDate();

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
                myLoan.addTransactionToHistory(myDate, myTransaction);
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
        String myId = pLoan.getLoanId();
        CoeusLoan myLoan = theLoanMap.get(myId);

        /* If we do not have a snapShot */
        if (myLoan == null) {
            /* Create and record it */
            myLoan = theMarket.newLoan(myId);
            theLoanMap.put(myId, myLoan);

            /* Ensure that the badDebt date is copied */
            myLoan.setBadDebtDate(pLoan.getBadDebtDate());
        }

        /* return the loan SnapShot */
        return myLoan;
    }

    @Override
    public String formatObject() {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theMarket.formatObject());
        myBuilder.append('@');
        myBuilder.append(theDate);
        return myBuilder.toString();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
        return MetisFieldValue.UNKNOWN;
    }
}
