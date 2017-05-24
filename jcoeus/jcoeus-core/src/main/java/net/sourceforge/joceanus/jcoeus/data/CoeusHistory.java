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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Transaction Totals History.
 */
public abstract class CoeusHistory
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusHistory.class);

    /**
     * Market Field Id.
     */
    private static final MetisDataField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Loan Field Id.
     */
    private static final MetisDataField FIELD_LOAN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOAN.getValue());

    /**
     * Date Field Id.
     */
    private static final MetisDataField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

    /**
     * Totals Field Id.
     */
    private static final MetisDataField FIELD_TOTALS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALS.getValue());

    /**
     * The market.
     */
    private final CoeusMarket theMarket;

    /**
     * The loan.
     */
    private final CoeusLoan theLoan;

    /**
     * The date for the totals.
     */
    private final TethysDate theDate;

    /**
     * The individual totals.
     */
    private final List<CoeusTotals> theHistory;

    /**
     * The summary totals.
     */
    private final CoeusTotals theTotals;

    /**
     * The last totals.
     */
    private CoeusTotals theLastTotals;

    /**
     * Constructor.
     * @param pTotals the totals
     */
    protected CoeusHistory(final CoeusTotals pTotals) {
        /* Record parameters */
        theTotals = pTotals;
        theLastTotals = theTotals;

        /* Record details */
        theMarket = pTotals.getMarket();
        theLoan = pTotals.getLoan();
        theDate = pTotals.getDate();

        /* Create the list */
        theHistory = new ArrayList<>();
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
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public CoeusTotals getTotals() {
        return theTotals;
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
    public void addTransactionToHistory(final CoeusTransaction pTrans) {
        /* Create the new entry and add to the list */
        CoeusTotals myTotals = newTotals(theLastTotals, pTrans);
        myTotals.addTransactionToTotals(pTrans);
        theHistory.add(myTotals);
        theLastTotals = myTotals;

        /* Add to the totals */
        theTotals.addTransactionToTotals(pTrans);
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
        theTotals.resetTotals();
        theHistory.clear();
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return theTotals.toString();
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOAN.equals(pField)) {
            CoeusLoan myLoan = getLoan();
            return myLoan == null
                                  ? MetisDataFieldValue.SKIP
                                  : myLoan;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate == null
                                   ? MetisDataFieldValue.SKIP
                                   : theDate;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_TOTALS.equals(pField)) {
            return theTotals;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
