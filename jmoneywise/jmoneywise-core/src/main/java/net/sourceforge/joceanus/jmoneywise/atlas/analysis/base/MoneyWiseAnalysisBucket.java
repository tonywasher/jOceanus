/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Analysis Bucket.
 * @param <T> the owner
 * @param <E> the attribute
 */
public abstract class MoneyWiseAnalysisBucket<T, E extends Enum<E> & MoneyWiseAnalysisAttribute>
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseAnalysisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.BUCKET_OWNER, MoneyWiseAnalysisBucket::getOwner);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.BUCKET_HISTORY, MoneyWiseAnalysisBucket::getHistory);
    }

    /**
     * The owner.
     */
    private final T theOwner;

    /**
     * The history.
     */
    private final MoneyWiseAnalysisHistory<E> theHistory;

    /**
     * Constructor.
     *
     * @param pOwner   the owner
     * @param pInitial the initial values
     */
    protected MoneyWiseAnalysisBucket(final T pOwner,
                                      final MoneyWiseAnalysisValues<E> pInitial) {
        theOwner = pOwner;
        theHistory = new MoneyWiseAnalysisHistory<>(pInitial);
    }

    /**
     * Constructor.
     *
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    protected MoneyWiseAnalysisBucket(final MoneyWiseAnalysisBucket<T, E> pBase,
                                      final TethysDate pDate) {
        theOwner = pBase.getOwner();
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.theHistory, pDate);
    }

    /**
     * Constructor.
     *
     * @param pBase  the base bucket
     * @param pRange the date range.
     */
    protected MoneyWiseAnalysisBucket(final MoneyWiseAnalysisBucket<T, E> pBase,
                                      final TethysDateRange pRange) {
        theOwner = pBase.getOwner();
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.theHistory, pRange);
    }

    /**
     * Obtain the owner.
     *
     * @return the owner
     */
    public T getOwner() {
        return theOwner;
    }

    /**
     * Obtain the history.
     * @return the history
     */
    public MoneyWiseAnalysisHistory<E> getHistory() {
        return theHistory;
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseAnalysisValues<E> getValues() {
        return theHistory.getValues();
    }

    /**
     * Obtain the initial values.
     * @return the initial values
     */
    public MoneyWiseAnalysisValues<E> getInitial() {
        return theHistory.getInitial();
    }

    /**
     * Is the bucket idle?
     *
     * @return true/false
     */
    public boolean isIdle() {
        return theHistory.isIdle();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Is the bucket active?
     *
     * @return true/false
     */
    public abstract boolean isActive();

    /**
     * create a new filtered map from the source map.
     * @param <B> the bucket type
     * @param pSource the source map
     * @param pGenerator the generator
     * @return the new bucket
     */
    public static <B extends MoneyWiseAnalysisBucket<?, ?>> Map<Integer, B> newMap(final Map<Integer, B> pSource,
                                                                                   final Function<B, B> pGenerator) {
        final Map<Integer, B> myMap = new HashMap<>();
        for (Map.Entry<Integer, B> myEntry : pSource.entrySet()) {
            final B myBucket = myEntry.getValue();
            final B myNewBucket = pGenerator.apply(myBucket);
            if (myNewBucket.isActive() || !myNewBucket.isIdle()) {
                myMap.put(myEntry.getKey(), myNewBucket);
            }
        }
        return myMap;
    }

    /**
     * create a new bucket for the end date.
     * @param pDate the end date.
     * @return the new bucket
     */
    public abstract MoneyWiseAnalysisBucket<T, E> newBucket(TethysDate pDate);

    /**
     * create a new bucket for the date range.
     * @param pRange the date range.
     * @return the new bucket
     */
    public abstract MoneyWiseAnalysisBucket<T, E> newBucket(TethysDateRange pRange);
}
