/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Analysis Bucket History.
 * @param <E> the enum class
 */
public class MoneyWiseAnalysisHistory<E extends Enum<E> & MoneyWiseAnalysisAttribute> {
    /**
     * The range.
     */
    private final TethysDateRange theRange;

    /**
     * The snapShot list.
     */
    private final List<MoneyWiseAnalysisSnapShot<E>> theSnapShots;

    /**
     * The snapShot Map.
     */
    private final Map<Integer, MoneyWiseAnalysisSnapShot<E>> theMap;

    /**
     * The initial values.
     */
    private final MoneyWiseAnalysisValues<E> theInitial;

    /**
     * The total values.
     */
    private MoneyWiseAnalysisValues<E> theTotal;

    /**
     * Constructor.
     * @param pInitial the initial values.
     */
    MoneyWiseAnalysisHistory(final MoneyWiseAnalysisValues<E> pInitial) {
        theSnapShots = new ArrayList<>();
        theMap = new HashMap<>();
        theInitial = pInitial;
        theTotal = pInitial;
        theRange = null;
    }

    /**
     * Constructor.
     * @param pHistory the history
     * @param pDate the end date.
     */
    MoneyWiseAnalysisHistory(final MoneyWiseAnalysisHistory<E> pHistory,
                             final TethysDate pDate) {
        this(pHistory, new TethysDateRange(null, pDate));
    }

    /**
     * Constructor.
     * @param pHistory the history
     * @param pRange the date range.
     */
    MoneyWiseAnalysisHistory(final MoneyWiseAnalysisHistory<E> pHistory,
                             final TethysDateRange pRange) {
        /* Record the range */
        theRange = pRange;

        /* Determine indices */
        final int[] myIndices = findIndicesForRange(pHistory, pRange);

        /* Create the cut down history */
        theSnapShots = pHistory.theSnapShots.subList(myIndices[0], myIndices[1]);
        theMap = pHistory.theMap;

        /* Determine initial values and totals */
        final boolean empty = theSnapShots.isEmpty();
        theInitial = empty
                ? pHistory.theInitial
                :  theSnapShots.get(0).getPrevious();
        theTotal = empty
                ? theInitial
                : theSnapShots.get(theSnapShots.size() - 1).getValues();
    }

    /**
     * Is the history idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theSnapShots.isEmpty();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseAnalysisValues<E> getValues() {
        return theTotal;
    }

    /**
     * Create a new snapShot.
     * @param pEvent the event.
     * @return the new snapShot.
     */
    MoneyWiseAnalysisSnapShot<E> newSnapShot(final MoneyWiseAnalysisEvent pEvent) {
        /* Create the new snapshot */
        final MoneyWiseAnalysisSnapShot<E> mySnapShot = new MoneyWiseAnalysisSnapShot<E>(pEvent, theTotal);
        theSnapShots.add(mySnapShot);
        theTotal = mySnapShot.getValues();
        return mySnapShot;
    }

    /**
     * Register snapShot.
     * @param pSnapShot the snapShot
     */
    public void registerSnapShot(final MoneyWiseAnalysisSnapShot<E> pSnapShot) {
        /* Flatten the values */
        pSnapShot.flattenValues();

        /* Add to map and list */
        theMap.put(pSnapShot.getId(), pSnapShot);
        theSnapShots.add(pSnapShot);
    }

    /**
     * Obtain the snapShot for the event (if it exists and is in range).
     * @param pEvent the event
     * @return the snapShot (if valid)
     */
    public MoneyWiseAnalysisSnapShot<E> getSnapShotForEvent(final MoneyWiseAnalysisEvent pEvent) {
        /* Look in map and return if not found or we have no range */
        final MoneyWiseAnalysisSnapShot<E> mySnapShot = theMap.get(pEvent.getId());
        if (mySnapShot == null || theRange == null) {
            return mySnapShot;
        }

        /* Check that the event is in range */
        final TethysDate myDate = mySnapShot.getDate();
        return theRange.compareToDate(myDate) == 0 ? mySnapShot : null;
    }

    /**
     * Obtain indices for ranged view.
     * @param pHistory the base history
     * @param pRange the date range
     * @return the indices
     */
    private int[] findIndicesForRange(final MoneyWiseAnalysisHistory<E> pHistory,
                                      final TethysDateRange pRange) {
        /* Determine the dates */
        final TethysDate myStartDate = pRange.getStart();
        final TethysDate myEndDate = pRange.getEnd();

        /* Create indices */
        int myIndex = 0;
        int myStart = -1;

        /* Loop through the snapShots */
        for (MoneyWiseAnalysisSnapShot<E> mySnapShot : pHistory.theSnapShots) {
            /* Break loop if we have hit the end */
            final TethysDate myDate = mySnapShot.getDate();
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
