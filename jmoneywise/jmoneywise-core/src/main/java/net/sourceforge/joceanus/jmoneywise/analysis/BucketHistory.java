/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * History for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class BucketHistory<T extends BucketValues<T, E>, E extends Enum<E> & BucketAttribute>
        extends LinkedHashMap<Integer, BucketSnapShot<T, E>>
        implements MetisDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6976602929502517385L;

    /**
     * values.
     */
    private final transient T theValues;

    /**
     * Base values.
     */
    private final transient T theBaseValues;

    /**
     * Last values.
     */
    private transient T theLastValues = null;

    /**
     * Constructor.
     * @param pValues the initial values
     */
    protected BucketHistory(final T pValues) {
        /* Store the values */
        theValues = pValues;

        /* Create base as a snapshot */
        theBaseValues = theValues.getSnapShot();
    }

    /**
     * Constructor.
     * @param pHistory the base history
     */
    protected BucketHistory(final BucketHistory<T, E> pHistory) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().getSnapShot();
        theValues = theBaseValues.getSnapShot();
        theLastValues = theBaseValues;
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pDate the date for history cut-off
     */
    protected BucketHistory(final BucketHistory<T, E> pHistory,
                            final TethysDate pDate) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().getSnapShot();
        theLastValues = theBaseValues;

        /* Record latest transaction */
        BucketSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T, E>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myTrans.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            BucketSnapShot<T, E> myNewTrans = new BucketSnapShot<T, E>(myTrans, theBaseValues, theLastValues);
            theLastValues = myNewTrans.getSnapShot();
            put(myEntry.getKey(), myNewTrans);

            /* Store latest value */
            myLatest = myTrans;
        }

        /* If we have no entries */
        if (myLatest == null) {
            /* Values are identical to base values */
            theValues = theBaseValues.getSnapShot();
        } else {
            /* Take a snapShot of the latest values */
            theValues = myLatest.getNewSnapShot();
        }
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pRange the date range for history cut-off
     */
    protected BucketHistory(final BucketHistory<T, E> pHistory,
                            final TethysDateRange pRange) {
        /* Record first and last events */
        BucketSnapShot<T, E> myFirst = null;
        BucketSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T, E>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we are past the initial Date */
            int iRange = pRange.compareTo(myTrans.getDate());
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
                                         ? pHistory.getBaseValues().getSnapShot()
                                         : myFirst.getNewSnapShot();
        theLastValues = theBaseValues;

        /* If we broke the loop because we found an event */
        if (myLatest != null) {
            /* Add to the map */
            BucketSnapShot<T, E> myNewTrans = new BucketSnapShot<T, E>(myLatest, theBaseValues, theLastValues);
            put(myLatest.getId(), myNewTrans);
            theLastValues = myNewTrans.getSnapShot();
        }

        /* Continue the loop */
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myTrans = myEntry.getValue();

            /* If we are past the range, break the loop */
            if (pRange.compareTo(myTrans.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            BucketSnapShot<T, E> myNewTrans = new BucketSnapShot<T, E>(myTrans, theBaseValues, theLastValues);
            put(myEntry.getKey(), myNewTrans);
            theLastValues = myNewTrans.getNewSnapShot();

            /* Store latest value */
            myLatest = myTrans;
        }

        /* Store the values */
        theValues = (myLatest != null)
                                      ? myLatest.getNewSnapShot()
                                      : theBaseValues.getSnapShot();
    }

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Are there any entries in the map?
     * @return true/false
     */
    protected boolean isIdle() {
        return isEmpty();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    protected T getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    protected T getBaseValues() {
        return theBaseValues;
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction to register.
     * @param pValues the values
     * @return the snapShot values
     */
    protected T registerTransaction(final Transaction pTrans,
                                    final T pValues) {
        /* Allocate the transaction and add to map */
        BucketSnapShot<T, E> myTrans = new BucketSnapShot<T, E>(pTrans, pValues, theLastValues);
        put(pTrans.getId(), myTrans);
        theLastValues = myTrans.getSnapShot();

        /* Return the values */
        return theLastValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public T getValuesForTransaction(final Transaction pTrans) {
        /* Locate the transaction in the map */
        BucketSnapShot<T, E> myTrans = get(pTrans.getId());
        return (myTrans == null)
                                ? null
                                : myTrans.getSnapShot();
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaValue(final Transaction pTrans,
                                  final E pAttr) {
        /* Locate the transaction in the map */
        BucketSnapShot<T, E> myTrans = get(pTrans.getId());
        return (myTrans == null)
                                ? null
                                : myTrans.getDeltaValue(pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysMoney getDeltaMoneyValue(final Transaction pTrans,
                                     final E pAttr) {
        /* Locate the transaction in the map */
        BucketSnapShot<T, E> myTrans = get(pTrans.getId());
        return (myTrans == null)
                                ? null
                                : myTrans.getDeltaMoneyValue(pAttr);
    }

    /**
     * Obtain units delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysUnits getDeltaUnitsValue(final Transaction pTrans,
                                     final E pAttr) {
        /* Locate the transaction in the map */
        BucketSnapShot<T, E> myTrans = get(pTrans.getId());
        return (myTrans == null)
                                ? null
                                : myTrans.getDeltaUnitsValue(pAttr);
    }
}
