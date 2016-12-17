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
     * Loan Market.
     */
    private final CoeusMarket theMarket;

    /**
     * Date.
     */
    private final TethysDate theDate;

    /**
     * The Map of MonthlyHistories.
     */
    private final Map<TethysDate, CoeusHistory> theMonthlyHistories;

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
     * @param pDate the annual date
     */
    protected CoeusMarketAnnual(final CoeusMarket pMarket,
                                final TethysDate pDate) {
        /* Store parameters */
        theMarket = pMarket;
        theDate = pDate;

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
            if (relevantTransaction(myTransaction)) {
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
    public CoeusHistory getMonthlyHistory(final TethysDate pDate) {
        /* Determine the date of the month */
        TethysDate myDate = theMarket.getEndOfMonth(pDate);

        /* Look up an existing history */
        CoeusHistory myHistory = theMonthlyHistories.get(myDate);
        if (myHistory == null) {
            /* Create new history and record it */
            myHistory = theMarket.newHistory(myDate);
            theMonthlyHistories.put(myDate, myHistory);
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Determine whether we are interested in this transaction.
     * @param pTrans the transaction
     * @return true/false
     */
    private boolean relevantTransaction(final CoeusTransaction pTrans) {
        /* Switch on transaction type */
        switch (pTrans.getTransType()) {
            case INTEREST:
            case FEES:
            case CASHBACK:
            case RATEPROMISE:
            case RECOVERY:
            case BADDEBT:
                return true;
            case TRANSFER:
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
            case BUYLOAN:
            default:
                return false;
        }
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

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
