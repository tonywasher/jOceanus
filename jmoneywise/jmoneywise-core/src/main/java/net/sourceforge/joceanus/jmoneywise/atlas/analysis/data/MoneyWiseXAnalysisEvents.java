/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisEvent.MoneyWiseAnalysisEventType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Analysis Event List Builder.
 */
public class MoneyWiseXAnalysisEvents
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisEvents> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisEvents.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_CURRENCY, MoneyWiseXAnalysisEvents::getReportingCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_EVENTS, MoneyWiseXAnalysisEvents::getEvents);
    }

    /**
     * The reporting currency.
     */
    private final AssetCurrency theReporting;

    /**
     * The list of events.
     */
    private final List<MoneyWiseXAnalysisEvent> theEvents;

    /**
     * Constructor.
     * @param pDataSet the dataSet to derive events from
     */
    MoneyWiseXAnalysisEvents(final MoneyWiseData pDataSet) {
        /* Create list and record the reporting currency */
        theEvents = new ArrayList<>();
        theReporting = pDataSet.getDefaultCurrency();

        /* Build the events */
        buildXchangeRates(pDataSet.getExchangeRates());
        buildPrices(pDataSet.getSecurityPrices());
        buildTransactions(pDataSet.getTransactions());

        /* Sort the events */
        Collections.sort(theEvents);
    }

    /**
     * Constructor.
     * @param pBase the base events
     * @param pDate the end date.
     */
    MoneyWiseXAnalysisEvents(final MoneyWiseXAnalysisEvents pBase,
                             final TethysDate pDate) {
        this(pBase, new TethysDateRange(null, pDate));
    }

    /**
     * Constructor.
     * @param pBase the base events
     * @param pRange the date range.
     */
    MoneyWiseXAnalysisEvents(final MoneyWiseXAnalysisEvents pBase,
                             final TethysDateRange pRange) {
        /* Determine indices */
        final int[] myIndices = findIndicesForRange(pBase.theEvents, pRange);

        /* Create the cut down list */
        theEvents = pBase.theEvents.subList(myIndices[0], myIndices[1]);
        theReporting = pBase.getReportingCurrency();
    }

    /**
     * Obtain the reporting currency.
     * @return the currency
     */
    public AssetCurrency getReportingCurrency() {
        return theReporting;
    }

    /**
     * Obtain an iterator for the events.
     * @return the iterator
     */
    public List<MoneyWiseXAnalysisEvent> getEvents() {
        return theEvents;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * build transaction events.
     * @param pTransactions the transactions
     */
    private void buildTransactions(final TransactionList pTransactions) {
        /* Loop through the transactions */
        final Iterator<Transaction> myIterator = pTransactions.iterator();
        while (myIterator.hasNext()) {
            final Transaction myTrans = myIterator.next();
            theEvents.add(new MoneyWiseXAnalysisEvent(myTrans));
        }
    }

    /**
     * build price events.
     * @param pPrices the prices
     */
    private void buildPrices(final SecurityPriceList pPrices) {
        /* Create event map */
        final Map<TethysDate, MoneyWiseXAnalysisEvent> myEvents = new HashMap<>();

        /* Loop through the prices */
        final Iterator<SecurityPrice> myIterator = pPrices.iterator();
        while (myIterator.hasNext()) {
            final SecurityPrice myPrice = myIterator.next();

            /* Get/Create event and add price to event */
            final MoneyWiseXAnalysisEvent myEvent = myEvents.computeIfAbsent(myPrice.getDate(),
                    d -> new MoneyWiseXAnalysisEvent(d, MoneyWiseAnalysisEventType.PRICE));
            myEvent.addSecurityPrice(myPrice);
        }

        /* Add the events to the list */
        theEvents.addAll(myEvents.values());
    }

    /**
     * build xchangeRate events.
     * @param pRates the xchangeRates
     */
    private void buildXchangeRates(final ExchangeRateList pRates) {
        /* Create event map */
        final Map<TethysDate, MoneyWiseXAnalysisEvent> myEvents = new HashMap<>();

        /* Loop through the xchangeRates */
        final Iterator<ExchangeRate> myIterator = pRates.iterator();
        while (myIterator.hasNext()) {
            final ExchangeRate myRate = myIterator.next();

            /* Get/Create event and add xchgRate to event */
            final MoneyWiseXAnalysisEvent myEvent = myEvents.computeIfAbsent(myRate.getDate(),
                    d -> new MoneyWiseXAnalysisEvent(d, MoneyWiseAnalysisEventType.XCHGRATE));
            myEvent.addXchangeRate(myRate);
        }

        /* Add the events to the list */
        theEvents.addAll(myEvents.values());
    }

    /**
     * Obtain indices for ranged view.
     * @param pEvents the base events
     * @param pRange the date range
     * @return the indices
     */
    private int[] findIndicesForRange(final List<MoneyWiseXAnalysisEvent> pEvents,
                                      final TethysDateRange pRange) {
        /* Determine the dates */
        final TethysDate myStartDate = pRange.getStart();
        final TethysDate myEndDate = pRange.getEnd();

        /* Create indices */
        int myIndex = 0;
        int myStart = -1;

        /* Loop through the snapShots */
        for (MoneyWiseXAnalysisEvent myEvent : pEvents) {
            /* Break loop if we have hit the end */
            final TethysDate myDate = myEvent.getDate();
            if (myEndDate.compareTo(myDate) < 0) {
                break;
            }

            /* If we have not yet hit the start, check for start being hit */
            if (myStart == -1
                    && (myStartDate == null
                    || myStartDate.compareTo(myDate) <= 0)) {
                /* Record the start */
                myStart = myIndex;
            }

            /* increment the index */
            myIndex++;
        }

        /* Handle empty list */
        return myStart == -1
                ? new int[] { 0, 0 }
                : new int[] { myStart, myIndex };
    }
}
