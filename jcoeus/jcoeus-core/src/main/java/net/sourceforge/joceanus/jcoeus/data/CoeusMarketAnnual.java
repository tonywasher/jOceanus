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

import java.time.Month;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Annual Market Totals.
 */
public class CoeusMarketAnnual
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusMarketAnnual.class.getSimpleName());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * Monthly Histories Field Id.
     */
    private static final MetisField FIELD_MONTHS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MONTHLYTOTALS.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

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
    protected CoeusMarketAnnual(final CoeusMarket pMarket,
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

            /* If this is a relevant transaction */
            if (theInitialDate.compareTo(myTransaction.getDate()) <= 0) {
                /* Obtain the monthly history and adjust */
                CoeusHistory myMonth = getMonthlyHistory(myDate);
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
        TethysDate myDate = theCalendar.getEndOfMonth(pDate);
        Month myMonth = myDate.getMonthValue();

        /* Look up an existing history */
        CoeusHistory myHistory = theMonthlyHistories.get(myMonth);
        if (myHistory == null) {
            /* Create new history and record it */
            myHistory = theMarket.newHistory(myDate);
            theMonthlyHistories.put(myMonth, myHistory);
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Obtain monthly history.
     * @param pDate the date
     * @return the history
     */
    public CoeusHistory getMonthlyHistory(final Month pDate) {
        return theMonthlyHistories.get(pDate);
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
        if (FIELD_MONTHS.equals(pField)) {
            return theMonthlyHistories;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
