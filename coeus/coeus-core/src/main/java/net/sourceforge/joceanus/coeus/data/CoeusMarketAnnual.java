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

import java.time.Month;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;

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
     * Date.
     */
    private final OceanusDateRange theDateRange;

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
     * Do we have shield?
     */
    private boolean hasShield;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pCalendar the calendar
     * @param pDate the annual date
     */
    CoeusMarketAnnual(final CoeusMarket pMarket,
                      final CoeusCalendar pCalendar,
                      final OceanusDate pDate) {
        /* Store parameters */
        theMarket = pMarket;

        /* Determine the initial date */
        final OceanusDate myInitial = new OceanusDate(pDate);
        myInitial.adjustYear(-1);
        myInitial.adjustDay(1);
        theDateRange = new OceanusDateRange(myInitial, pDate);

        /* Create monthly history map */
        theMonthlyHistories = new LinkedHashMap<>();

        /* Create the history */
        theHistory = pMarket.viewHistory(theDateRange);
        setFlags();

        /* Create the monthly views */
        final OceanusDate myDate = new OceanusDate(myInitial);
        while (myDate.compareTo(pDate) < 0) {
            final OceanusDate myStart = pCalendar.getStartOfMonth(myDate);
            final OceanusDate myEnd = pCalendar.getEndOfMonth(myDate);
            final Month myMonth = myEnd.getMonthValue();
            final OceanusDateRange myRange = new OceanusDateRange(myStart, myEnd);
            final CoeusHistory myHistory = pMarket.viewHistory(theHistory, myRange);

            /* Store a non-zero month */
            if (!myHistory.isEmpty()) {
                theMonthlyHistories.put(myMonth, myHistory);
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
    public OceanusDateRange getDateRange() {
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
     * Do we have shield payments?
     * @return true/false
     */
    public boolean hasShield() {
        return hasShield;
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
     * Set flags.
     */
    private void setFlags() {
        /* Loop through the totals */
        final Iterator<CoeusTotals> myIterator = theHistory.historyIterator();
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
                case SHIELD:
                    hasShield = true;
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
