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

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Transaction Totals History.
 * @param <L> the loan type
 * @param <T> the transaction type
 * @param <S> the totals type
 * @param <H> the history type
 */
public abstract class CoeusHistory<L extends CoeusLoan<L, T, S, H>, T extends CoeusTransaction<L, T, S, H>, S extends CoeusTotals<L, T, S, H>, H extends CoeusHistory<L, T, S, H>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusHistory.class.getSimpleName());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Loan Field Id.
     */
    private static final MetisField FIELD_LOAN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOAN.getValue());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

    /**
     * Totals Field Id.
     */
    private static final MetisField FIELD_TOTALS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALS.getValue());

    /**
     * The market.
     */
    private final CoeusLoanMarket<L, T, S, H> theMarket;

    /**
     * The loan.
     */
    private final L theLoan;

    /**
     * The date for the totals.
     */
    private final TethysDate theDate;

    /**
     * The individual totals.
     */
    private final List<S> theHistory;

    /**
     * The summary totals.
     */
    private final S theTotals;

    /**
     * Constructor.
     * @param pTotals the totals
     */
    protected CoeusHistory(final S pTotals) {
        /* Record parameters */
        theTotals = pTotals;

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
    public CoeusLoanMarket<L, T, S, H> getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public L getLoan() {
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
    public S getTotals() {
        return theTotals;
    }

    /**
     * Obtain the history iterator.
     * @return the iterator
     */
    public Iterator<S> historyIterator() {
        return theHistory.iterator();
    }

    /**
     * Add transaction to history.
     * @param pTrans the transaction to add
     */
    public void addTransactionToHistory(final T pTrans) {
        /* Add to the totals */
        theTotals.addTransactionToTotals(pTrans);

        /* Create the new entry and add to the list */
        S myTotals = newTotals(theTotals, pTrans);
        theHistory.add(myTotals);
    }

    /**
     * Create a new Totals element.
     * @param pTotals the existing totals
     * @param pTrans the transaction
     * @return the new totals
     */
    protected abstract S newTotals(final S pTotals,
                                   final T pTrans);

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
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public String formatObject() {
        return theTotals.formatObject();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOAN.equals(pField)) {
            L myLoan = getLoan();
            return myLoan == null
                                  ? MetisFieldValue.SKIP
                                  : myLoan;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate == null
                                   ? MetisFieldValue.SKIP
                                   : theDate;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_TOTALS.equals(pField)) {
            return theTotals;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
