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

import java.time.Month;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Annual Market Totals.
 */
public class CoeusMarketAnnual
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusMarketAnnual> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusMarketAnnual.class);

    /*
     * Field IDs.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET, CoeusMarketAnnual::getMarket);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE, CoeusMarketAnnual::getDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MONTHLYTOTALS, CoeusMarketAnnual::monthlyHistories);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY, CoeusMarketAnnual::getHistory);
    }

    /**
     * Loan Market.
     */
    private final CoeusMarket theMarket;

    /**
     * Calendar.
     */
    private final CoeusCalendar theCalendar;

    /**
     * Initial Date.
     */
    private final TethysDate theInitialDate;

    /**
     * Date.
     */
    private final TethysDate theDate;

    /**
     * The Map of MonthlyHistories.
     */
    private final Map<Month, CoeusHistory> theMonthlyHistories;

    /**
     * The AnnualHistory.
     */
    private final CoeusHistory theHistory;

    /**
     * Do we have badDebt?
     */
    private boolean hasBadDebt;

    /**
     * Do we have Fees?
     */
    private boolean hasFees;

    /**
     * Do we have cashBack?
     */
    private boolean hasCashBack;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pCalendar the calendar
     * @param pDate the annual date
     */
    CoeusMarketAnnual(final CoeusMarket pMarket,
                      final CoeusCalendar pCalendar,
                      final TethysDate pDate) {
        /* Store parameters */
        theMarket = pMarket;
        theCalendar = pCalendar;
        theDate = pDate;

        /* Determine the initial date */
        theInitialDate = new TethysDate(theDate);
        theInitialDate.adjustYear(-1);
        theInitialDate.adjustDay(1);

        /* Create monthly history map */
        theMonthlyHistories = new LinkedHashMap<>();

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
     * Obtain the monthly iterator.
     * @return the iterator
     */
    public Iterator<CoeusHistory> monthlyIterator() {
        return theMonthlyHistories.values().iterator();
    }

    /**
     * Obtain the monthly histories.
     * @return the histories
     */
    private Map<Month, CoeusHistory> monthlyHistories() {
        return theMonthlyHistories;
    }

    /**
     * Is there history for the month?
     * @param pMonth the month
     * @return true/false
     */
    public boolean availableMonth(final Month pMonth) {
        return theMonthlyHistories.containsKey(pMonth);
    }

    /**
     * Do we have badDebt?
     * @return true/false
     */
    public boolean hasBadDebt() {
        return hasBadDebt;
    }

    /**
     * Do we have Fees?
     * @return true/false
     */
    public boolean hasFees() {
        return hasFees;
    }

    /**
     * Do we have cashBack?
     * @return true/false
     */
    public boolean hasCashBack() {
        return hasCashBack;
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

            /* If this is a relevant transaction */
            if (theInitialDate.compareTo(myTransaction.getDate()) <= 0) {
                /* Obtain the monthly history and adjust */
                final CoeusHistory myMonth = getMonthlyHistory(myDate);
                myMonth.addTransactionToHistory(myTransaction);

                /* Adjust the history */
                myHistory.addTransactionToHistory(myTransaction);

                /* Switch on transaction type */
                switch (myTransaction.getTransType()) {
                    case BADDEBT:
                    case RECOVERY:
                        hasBadDebt = true;
                        break;
                    case FEES:
                        hasFees = true;
                        break;
                    case CASHBACK:
                        hasCashBack = true;
                        break;
                    default:
                        break;
                }
            }
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Obtain monthly history.
     * @param pDate the date
     * @return the history
     */
    private CoeusHistory getMonthlyHistory(final TethysDate pDate) {
        /* Determine the date of the month */
        final TethysDate myDate = theCalendar.getEndOfMonth(pDate);
        final Month myMonth = myDate.getMonthValue();

        /* Look up and return the history */
        return theMonthlyHistories.computeIfAbsent(myMonth, m -> theMarket.newHistory(myDate));
    }

    /**
     * Obtain monthly history.
     * @param pDate the date
     * @return the history
     */
    public final CoeusHistory getMonthlyHistory(final Month pDate) {
        return theMonthlyHistories.get(pDate);
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theMarket).append('@').append(theDate);
        return myBuilder.toString();
    }

    @Override
    public MetisFieldSet<CoeusMarketAnnual> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
