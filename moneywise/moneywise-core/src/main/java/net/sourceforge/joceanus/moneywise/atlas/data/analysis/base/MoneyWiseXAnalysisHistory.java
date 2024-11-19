/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.date.TethysDateRange;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;
import net.sourceforge.joceanus.tethys.decimal.TethysUnits;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * History for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class MoneyWiseXAnalysisHistory<T extends MoneyWiseXAnalysisValues<T, E>, E extends Enum<E> & MoneyWiseXAnalysisAttribute>
        implements MetisDataObjectFormat, MetisDataMap<Integer, MoneyWiseXAnalysisSnapShot<T, E>> {
    /**
     * The history map.
     */
    private final Map<Integer, MoneyWiseXAnalysisSnapShot<T, E>> theHistoryMap;

    /**
     * values.
     */
    private final T theValues;

    /**
     * Base values.
     */
    private final T theBaseValues;

    /**
     * Last values.
     */
    private T theLastValues;

    /**
     * Constructor.
     * @param pValues the initial values
     */
    public MoneyWiseXAnalysisHistory(final T pValues) {
        /* Store the values */
        theValues = pValues;

        /* Create the history map */
        theHistoryMap = new LinkedHashMap<>();

        /* Create base as a snapshot */
        theBaseValues = theValues.newSnapShot();
        theLastValues = theBaseValues;
    }

    /**
     * Constructor.
     * @param pHistory the base history
     */
    public MoneyWiseXAnalysisHistory(final MoneyWiseXAnalysisHistory<T, E> pHistory) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().newSnapShot();
        theValues = theBaseValues.newSnapShot();
        theValues.resetNonPreserved();
        theLastValues = theBaseValues;

        /* Create the history map */
        theHistoryMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pDate the date for history cut-off
     */
    public MoneyWiseXAnalysisHistory(final MoneyWiseXAnalysisHistory<T, E> pHistory,
                                     final TethysDate pDate) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().newSnapShot();
        theLastValues = theBaseValues;

        /* Create the history map */
        theHistoryMap = new LinkedHashMap<>();

        /* Record latest transaction */
        MoneyWiseXAnalysisSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        final Iterator<Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>>> myIterator = pHistory.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>> myEntry = myIterator.next();
            final MoneyWiseXAnalysisSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myTrans.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            final MoneyWiseXAnalysisSnapShot<T, E> myNewTrans = new MoneyWiseXAnalysisSnapShot<>(myTrans, theBaseValues, theLastValues);
            theLastValues = myNewTrans.getSnapShot();
            theHistoryMap.put(myEntry.getKey(), myNewTrans);

            /* Store latest value */
            myLatest = myTrans;
        }

        /* If we have no entries */
        if (myLatest == null) {
            /* Values are identical to base values */
            theValues = theBaseValues.newSnapShot();
        } else {
            /* Take a snapShot of the latest values */
            theValues = myLatest.newSnapShot();
        }
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pRange the date range for history cut-off
     */
    public MoneyWiseXAnalysisHistory(final MoneyWiseXAnalysisHistory<T, E> pHistory,
                                     final TethysDateRange pRange) {
        /* Create the history map */
        theHistoryMap = new LinkedHashMap<>();

        /* Record first and last events */
        MoneyWiseXAnalysisSnapShot<T, E> myFirst = null;
        MoneyWiseXAnalysisSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        final Iterator<Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>>> myIterator = pHistory.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>> myEntry = myIterator.next();
            final MoneyWiseXAnalysisSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we are past the initial Date */
            final int iRange = pRange.compareToDate(myTrans.getDate());
            if (iRange <= 0) {
                /* If we are within the range */
                if (iRange == 0) {
                    /* Note that this counts as latest */
                    myLatest = myTrans;
                }

                /* Break the loop */
                break;
            }

            /* Store first value */
            myFirst = myTrans;
        }

        /* Determine the base values */
        theBaseValues = (myFirst == null)
                ? pHistory.getBaseValues().newSnapShot()
                : myFirst.newSnapShot();
        theLastValues = theBaseValues;

        /* If we broke the loop because we found an event */
        if (myLatest != null) {
            /* Add to the map */
            final MoneyWiseXAnalysisSnapShot<T, E> myNewTrans = new MoneyWiseXAnalysisSnapShot<>(myLatest, theBaseValues, theLastValues);
            theHistoryMap.put(myLatest.getId(), myNewTrans);
            theLastValues = myNewTrans.getSnapShot();
        }

        /* Continue the loop */
        while (myIterator.hasNext()) {
            final Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>> myEntry = myIterator.next();
            final MoneyWiseXAnalysisSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we are past the range, break the loop */
            if (pRange.compareToDate(myTrans.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            final MoneyWiseXAnalysisSnapShot<T, E> myNewTrans = new MoneyWiseXAnalysisSnapShot<>(myTrans, theBaseValues, theLastValues);
            theHistoryMap.put(myEntry.getKey(), myNewTrans);
            theLastValues = myNewTrans.getSnapShot();

            /* Store latest value */
            myLatest = myTrans;
        }

        /* Store the values */
        theValues = myLatest != null
                ? myLatest.newSnapShot()
                : theBaseValues.newSnapShot();
    }

    @Override
    public Map<Integer, MoneyWiseXAnalysisSnapShot<T, E>> getUnderlyingMap() {
        return theHistoryMap;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    /**
     * Obtain the entry set iterator.
     * @return the iterator
     */
    private Iterator<Entry<Integer, MoneyWiseXAnalysisSnapShot<T, E>>> entryIterator() {
        return theHistoryMap.entrySet().iterator();
    }

    /**
     * Are there any entries in the map?
     * @return true/false
     */
    public boolean isIdle() {
        return theHistoryMap.isEmpty();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public T getValues() {
        return theValues;
    }

    /**
     * Obtain the last values.
     * @return the last values
     */
    public T getLastValues() {
        return theLastValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public T getBaseValues() {
        return theBaseValues;
    }

    /**
     * Register the event.
     * @param pEvent the event to register.
     * @param pValues the values
     */
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent,
                              final T pValues) {
        /* Allocate the transaction and add to map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = new MoneyWiseXAnalysisSnapShot<>(pEvent, pValues, theLastValues);
        theHistoryMap.put(pEvent.getIndexedId(), myEvent);
        theLastValues = myEvent.getSnapShot();

        /* Reset non-preserved values */
        theValues.resetNonPreserved();
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public T getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Locate the transaction in the map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = theHistoryMap.get(pEvent.getIndexedId());
        return myEvent == null
                ? null
                : myEvent.getSnapShot();
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public T getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Locate the transaction in the map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = theHistoryMap.get(pEvent.getIndexedId());
        return myEvent == null
                ? null
                : myEvent.getPrevious();
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaValue(final MoneyWiseXAnalysisEvent pEvent,
                                       final E pAttr) {
        /* Locate the transaction in the map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = theHistoryMap.get(pEvent.getIndexedId());
        return myEvent == null
                ? null
                : myEvent.getDeltaValue(pAttr);
    }

    /**
     * Obtain money delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysMoney getDeltaMoneyValue(final MoneyWiseXAnalysisEvent pEvent,
                                          final E pAttr) {
        /* Locate the transaction in the map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = theHistoryMap.get(pEvent.getIndexedId());
        return myEvent == null
                ? null
                : myEvent.getDeltaMoneyValue(pAttr);
    }

    /**
     * Obtain units delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysUnits getDeltaUnitsValue(final MoneyWiseXAnalysisEvent pEvent,
                                          final E pAttr) {
        /* Locate the transaction in the map */
        final MoneyWiseXAnalysisSnapShot<T, E> myEvent = theHistoryMap.get(pEvent.getIndexedId());
        return myEvent == null
                ? null
                : myEvent.getDeltaUnitsValue(pAttr);
    }
}
