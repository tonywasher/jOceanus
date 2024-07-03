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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * The TransactionTag Bucket class.
 */
public final class MoneyWiseXAnalysisTransTagBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTransTagBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTransTagBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTransTagBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSTAG, MoneyWiseXAnalysisTransTagBucket::getTransTag);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSACTION.getListId(), MoneyWiseXAnalysisTransTagBucket::getHashMap);
    }

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * TransactionTag.
     */
    private final MoneyWiseTransTag theTransTag;

    /**
     * HashMap.
     */
    private final Map<Integer, MoneyWiseXAnalysisEvent> theHashMap;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTransTag the tag
     */
    private MoneyWiseXAnalysisTransTagBucket(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseTransTag pTransTag) {
        /* Store the parameters */
        theTransTag = pTransTag;
        theAnalysis = pAnalysis;

        /* Allocate the hashMap */
        theHashMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseXAnalysisTransTagBucket(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisTransTagBucket pBase,
                                             final TethysDate pDate) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<MoneyWiseXAnalysisEvent> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myIterator.next();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myEvent.getDate()) < 0) {
                break;
            }

            /* Process the event */
            processEvent(myEvent);
        }
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private MoneyWiseXAnalysisTransTagBucket(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisTransTagBucket pBase,
                                             final TethysDateRange pRange) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<MoneyWiseXAnalysisEvent> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myIterator.next();

            /* Check the range */
            final int iRange = pRange.compareToDate(myEvent.getDate());
            if (iRange < 0) {
                break;
            } else if (iRange == 0) {
                /* Process the event */
                processEvent(myEvent);
            }
        }
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisTransTagBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Integer getIndexedId() {
        return theTransTag.getIndexedId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTransTag.getName();
    }

    /**
     * Obtain transactionTag.
     * @return the tag
     */
    public MoneyWiseTransTag getTransTag() {
        return theTransTag;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain map Iterator.
     * @return the iterator
     */
    private Map<Integer, MoneyWiseXAnalysisEvent> getHashMap() {
        return theHashMap;
    }

    /**
     * Obtain map Iterator.
     * @return the iterator
     */
    private Iterator<MoneyWiseXAnalysisEvent> iterator() {
        return theHashMap.values().iterator();
    }

    /**
     * is the bucket idle.
     * @return true/false
     */
    boolean isIdle() {
        return theHashMap.isEmpty();
    }

    /**
     * Process the event.
     * @param pEvent the event
     */
    private void processEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Add to the map */
        theHashMap.put(pEvent.getIndexedId(), pEvent);
    }

    /**
     * Is this tag marked by the event.
     * @param pEvent the event
     * @return true/false
     */
    public boolean hasEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Note whether the event is mapped */
        return theHashMap.get(pEvent.getIndexedId()) != null;
    }

    /**
     * TransactionTagBucketList class.
     */
    public static final class MoneyWiseXAnalysisTransTagBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisTransTagBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisTransTagBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTransTagBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTransTagBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisTransTagBucket> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisTransTagBucketList(final MoneyWiseXAnalysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator(Comparator.comparing(MoneyWiseXAnalysisTransTagBucket::getTransTag));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisTransTagBucketList(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisTransTagBucketList pBase,
                                             final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTransTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTransTagBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisTransTagBucket myBucket = new MoneyWiseXAnalysisTransTagBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is active */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        MoneyWiseXAnalysisTransTagBucketList(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisTransTagBucketList pBase,
                                             final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTransTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTransTagBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisTransTagBucket myBucket = new MoneyWiseXAnalysisTransTagBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is active */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisTransTagBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisTransTagBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisTransTagBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the TransTagBucket for a given transaction tag.
         * @param pTag the transaction tag
         * @return the bucket
         */
        private MoneyWiseXAnalysisTransTagBucket getBucket(final MoneyWiseTransTag pTag) {
            /* Locate the bucket in the list */
            MoneyWiseXAnalysisTransTagBucket myItem = findItemById(pTag.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisTransTagBucket(theAnalysis, pTag);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the matching TagBucket.
         * @param pTag the tag
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisTransTagBucket getMatchingTag(final MoneyWiseTransTag pTag) {
            /* Return the matching tag if it exists else an orphan bucket */
            final MoneyWiseXAnalysisTransTagBucket myTag = findItemById(pTag.getIndexedId());
            return myTag != null
                    ? myTag
                    : new MoneyWiseXAnalysisTransTagBucket(theAnalysis, pTag);
        }

        /**
         * Obtain the default TagBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisTransTagBucket getDefaultTag() {
            /* Return the first payee in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * SortBuckets.
         */
        void sortBuckets() {
            theList.sortList();
        }

        /**
         * Process transaction tags.
         * @param pEvent the event
         * @param pIterator the transaction tag iterator
         */
        public void processEvent(final MoneyWiseXAnalysisEvent pEvent,
                                 final Iterator<MoneyWiseTransTag> pIterator) {
            /* Loop through the tags */
            while (pIterator.hasNext()) {
                final MoneyWiseTransTag myTag = pIterator.next();

                /* Obtain the bucket and process the transaction */
                final MoneyWiseXAnalysisTransTagBucket myBucket = getBucket(myTag);
                myBucket.processEvent(pEvent);
            }
        }
    }
}
