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
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

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
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RANGE, CoeusMarketAnnual::getDateRange);
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
     * Date.
     */
    private final TethysDateRange theDateRange;

    /**
     * The Map of MonthlyHistories.
     */
    private final Map<Month, CoeusHistory> theMonthlyHistories;

    /**
     * The Map of MonthlyHistories.
     */
    private final Map<Month, CoeusHistory> theXMonthlyHistories;

    /**
     * The AnnualHistory.
     */
    private final CoeusHistory theHistory;

    /**
     * The XHistory.
     */
    private final CoeusHistory theXHistory;

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

        /* Determine the initial date */
        final TethysDate myInitial = new TethysDate(pDate);
        myInitial.adjustYear(-1);
        myInitial.adjustDay(1);
        theDateRange = new TethysDateRange(myInitial, pDate);

        /* Create monthly history map */
        theMonthlyHistories = new LinkedHashMap<>();
        theXMonthlyHistories = new LinkedHashMap<>();

        /* Create the history */
        theHistory = determineHistory();
        theXHistory = pMarket.viewHistory(theDateRange);

        /* Create the monthly views */
        final TethysDate myDate = new TethysDate(myInitial);
        while (myDate.compareTo(pDate) < 0) {
            final TethysDate myStart = theCalendar.getStartOfMonth(myDate);
            final TethysDate myEnd = theCalendar.getEndOfMonth(myDate);
            final Month myMonth = myEnd.getMonthValue();
            final TethysDateRange myRange = new TethysDateRange(myStart, myEnd);
            final CoeusHistory myHistory = pMarket.viewHistory(theXHistory, myRange);

            /* Store a non-zero month */
            if (!myHistory.isEmpty()) {
                theXMonthlyHistories.put(myMonth, myHistory);
            }

            /* Next month */
            myDate.adjustMonth(1);
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
    public TethysDateRange getDateRange() {
        return theDateRange;
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

        /* Access the dates */
        final TethysDate myStart = theDateRange.getStart();
        final TethysDate myEnd = theDateRange.getEnd();

        /* Loop through the transactions */
        final Iterator<CoeusTransaction> myIterator = theMarket.transactionIterator();
        while (myIterator.hasNext()) {
            final CoeusTransaction myTransaction = myIterator.next();
            final TethysDate myDate = myTransaction.getDate();

            /* If we have gone past the date, break the loop */
            if (myDate.compareTo(myEnd) > 0) {
                break;
            }

            /* If this is a relevant transaction */
            if (myStart.compareTo(myTransaction.getDate()) <= 0) {
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
     * Set flags.
     */
    private void setFlags() {
        /* Loop through the totals */
        final Iterator<CoeusTotals> myIterator = theXHistory.historyIterator();
        while (myIterator.hasNext()) {
            final CoeusTotals myTotals = myIterator.next();

            /* Switch on transaction type */
            switch (myTotals.getTransType()) {
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
        return theMonthlyHistories.computeIfAbsent(myMonth, m -> theMarket.newHistory());
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
        return String.valueOf(theMarket) + '@' + theDateRange.getEnd();
    }

    @Override
    public MetisFieldSet<CoeusMarketAnnual> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
