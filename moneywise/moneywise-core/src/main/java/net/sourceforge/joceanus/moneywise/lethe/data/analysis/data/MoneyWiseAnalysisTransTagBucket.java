/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The TransactionTag Bucket class.
 */
public final class MoneyWiseAnalysisTransTagBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTransTagBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTransTagBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTransTagBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSTAG, MoneyWiseAnalysisTransTagBucket::getTransTag);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSACTION.getListId(), MoneyWiseAnalysisTransTagBucket::getHashMap);
    }

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * TransactionTag.
     */
    private final MoneyWiseTransTag theTransTag;

    /**
     * HashMap.
     */
    private final Map<Integer, MoneyWiseTransaction> theHashMap;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pTransTag the tag
     */
    private MoneyWiseAnalysisTransTagBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseTransTag pTransTag) {
        /* Store the parameters */
        theTransTag = pTransTag;
        theAnalysis = pAnalysis;

        /* Allocate the hashMap */
        theHashMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    private MoneyWiseAnalysisTransTagBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisTransTagBucket pBase,
                                            final OceanusDate pDate) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<MoneyWiseTransaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myTrans = myIterator.next();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myTrans.getDate()) < 0) {
                break;
            }

            /* Process the transaction */
            processTransaction(myTrans);
        }
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    private MoneyWiseAnalysisTransTagBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisTransTagBucket pBase,
                                            final OceanusDateRange pRange) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<MoneyWiseTransaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myTrans = myIterator.next();

            /* Check the range */
            final int iRange = pRange.compareToDate(myTrans.getDate());
            if (iRange < 0) {
                break;
            } else if (iRange == 0) {
                /* Process the transaction */
                processTransaction(myTrans);
            }
        }
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisTransTagBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
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
     *
     * @return the name
     */
    public String getName() {
        return theTransTag.getName();
    }

    /**
     * Obtain transactionTag.
     *
     * @return the tag
     */
    public MoneyWiseTransTag getTransTag() {
        return theTransTag;
    }

    /**
     * Obtain the analysis.
     *
     * @return the analysis
     */
    MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     *
     * @return the range
     */
    public OceanusDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain map Iterator.
     *
     * @return the iterator
     */
    private Map<Integer, MoneyWiseTransaction> getHashMap() {
        return theHashMap;
    }

    /**
     * Obtain map Iterator.
     *
     * @return the iterator
     */
    private Iterator<MoneyWiseTransaction> iterator() {
        return theHashMap.values().iterator();
    }

    /**
     * is the bucket idle.
     *
     * @return true/false
     */
    boolean isIdle() {
        return theHashMap.isEmpty();
    }

    /**
     * Process the transaction.
     *
     * @param pTrans the transaction
     */
    private void processTransaction(final MoneyWiseTransaction pTrans) {
        /* Add to the map */
        theHashMap.put(pTrans.getIndexedId(), pTrans);
    }

    /**
     * Is this tag marked by the transaction.
     *
     * @param pTrans the transaction
     * @return true/false
     */
    public boolean hasTransaction(final MoneyWiseTransaction pTrans) {
        /* Note whether the transaction is mapped */
        return theHashMap.get(pTrans.getIndexedId()) != null;
    }

    /**
     * TransactionTagBucketList class.
     */
    public static final class MoneyWiseAnalysisTransTagBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisTransTagBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisTransTagBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTransTagBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTransTagBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisTransTagBucket> theList;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisTransTagBucketList(final MoneyWiseAnalysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getTransTag().compareTo(r.getTransTag()));
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pDate     the Date
         */
        MoneyWiseAnalysisTransTagBucketList(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisTransTagBucketList pBase,
                                            final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTransTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTransTagBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisTransTagBucket myBucket = new MoneyWiseAnalysisTransTagBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pRange    the Date Range
         */
        MoneyWiseAnalysisTransTagBucketList(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisTransTagBucketList pBase,
                                            final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTransTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTransTagBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisTransTagBucket myBucket = new MoneyWiseAnalysisTransTagBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisTransTagBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisTransTagBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         *
         * @return the analysis
         */
        MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         *
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisTransTagBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the TransTagBucket for a given transaction tag.
         *
         * @param pTag the transaction tag
         * @return the bucket
         */
        private MoneyWiseAnalysisTransTagBucket getBucket(final MoneyWiseTransTag pTag) {
            /* Locate the bucket in the list */
            MoneyWiseAnalysisTransTagBucket myItem = findItemById(pTag.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisTransTagBucket(theAnalysis, pTag);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the matching TagBucket.
         *
         * @param pTag the tag
         * @return the matching bucket
         */
        public MoneyWiseAnalysisTransTagBucket getMatchingTag(final MoneyWiseTransTag pTag) {
            /* Return the matching tag if it exists else an orphan bucket */
            final MoneyWiseAnalysisTransTagBucket myTag = findItemById(pTag.getIndexedId());
            return myTag != null
                    ? myTag
                    : new MoneyWiseAnalysisTransTagBucket(theAnalysis, pTag);
        }

        /**
         * Obtain the default TagBucket.
         *
         * @return the default bucket
         */
        public MoneyWiseAnalysisTransTagBucket getDefaultTag() {
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
         *
         * @param pTrans    the transaction
         * @param pIterator the transaction tag iterator
         */
        public void processTransaction(final MoneyWiseTransaction pTrans,
                                       final Iterator<MoneyWiseTransTag> pIterator) {
            /* Loop through the tags */
            while (pIterator.hasNext()) {
                final MoneyWiseTransTag myTag = pIterator.next();

                /* Obtain the bucket and process the transaction */
                final MoneyWiseAnalysisTransTagBucket myBucket = getBucket(myTag);
                myBucket.processTransaction(pTrans);
            }
        }
    }
}
