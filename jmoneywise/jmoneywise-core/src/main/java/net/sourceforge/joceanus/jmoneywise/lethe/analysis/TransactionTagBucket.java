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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * The TransactionTag Bucket class.
 */
public final class TransactionTagBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<TransactionTagBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionTagBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TransactionTagBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSTAG, TransactionTagBucket::getTransTag);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSACTION.getListId(), TransactionTagBucket::getHashMap);
    }

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * TransactionTag.
     */
    private final TransactionTag theTransTag;

    /**
     * HashMap.
     */
    private final Map<Integer, Transaction> theHashMap;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTransTag the tag
     */
    private TransactionTagBucket(final Analysis pAnalysis,
                                 final TransactionTag pTransTag) {
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
    private TransactionTagBucket(final Analysis pAnalysis,
                                 final TransactionTagBucket pBase,
                                 final TethysDate pDate) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<Transaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final Transaction myTrans = myIterator.next();

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
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private TransactionTagBucket(final Analysis pAnalysis,
                                 final TransactionTagBucket pBase,
                                 final TethysDateRange pRange) {
        /* Copy details from base */
        this(pAnalysis, pBase.getTransTag());

        /* Loop through the map */
        final Iterator<Transaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            final Transaction myTrans = myIterator.next();

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
    public MetisFieldSet<TransactionTagBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Integer getIndexedId() {
        return theTransTag.getId();
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
    public TransactionTag getTransTag() {
        return theTransTag;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
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
    private Map<Integer, Transaction> getHashMap() {
        return theHashMap;
    }

    /**
     * Obtain map Iterator.
     * @return the iterator
     */
    private Iterator<Transaction> iterator() {
        return theHashMap.values().iterator();
    }

    /**
     * is the bucket idle.
     * @return true/false
     */
    protected boolean isIdle() {
        return theHashMap.isEmpty();
    }

    /**
     * Process the transaction.
     * @param pTrans the transaction
     */
    private void processTransaction(final Transaction pTrans) {
        /* Add to the map */
        theHashMap.put(pTrans.getId(), pTrans);
    }

    /**
     * Is this tag marked by the transaction.
     * @param pTrans the transaction
     * @return true/false
     */
    public boolean hasTransaction(final Transaction pTrans) {
        /* Note whether the transaction is mapped */
        return theHashMap.get(pTrans.getId()) != null;
    }

    /**
     * TransactionTagBucketList class.
     */
    public static class TransactionTagBucketList
            implements MetisFieldItem, MetisDataList<TransactionTagBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<TransactionTagBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionTagBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TransactionTagBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<TransactionTagBucket> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TransactionTagBucketList(final Analysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getTransTag().compareTo(r.getTransTag()));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TransactionTagBucketList(final Analysis pAnalysis,
                                           final TransactionTagBucketList pBase,
                                           final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<TransactionTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TransactionTagBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final TransactionTagBucket myBucket = new TransactionTagBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
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
        protected TransactionTagBucketList(final Analysis pAnalysis,
                                           final TransactionTagBucketList pBase,
                                           final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<TransactionTagBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TransactionTagBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final TransactionTagBucket myBucket = new TransactionTagBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<TransactionTagBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<TransactionTagBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public TransactionTagBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the TransTagBucket for a given transaction tag.
         * @param pTag the transaction tag
         * @return the bucket
         */
        private TransactionTagBucket getBucket(final TransactionTag pTag) {
            /* Locate the bucket in the list */
            TransactionTagBucket myItem = findItemById(pTag.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TransactionTagBucket(theAnalysis, pTag);

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
        public TransactionTagBucket getMatchingTag(final TransactionTag pTag) {
            /* Return the matching tag if it exists else an orphan bucket */
            final TransactionTagBucket myTag = findItemById(pTag.getIndexedId());
            return myTag != null
                                 ? myTag
                                 : new TransactionTagBucket(theAnalysis, pTag);
        }

        /**
         * Obtain the default TagBucket.
         * @return the default bucket
         */
        public TransactionTagBucket getDefaultTag() {
            /* Return the first payee in the list if it exists */
            return isEmpty()
                             ? null
                             : theList.getUnderlyingList().get(0);
        }

        /**
         * SortBuckets.
         */
        protected void sortBuckets() {
            theList.sortList();
        }

        /**
         * Process transaction tags.
         * @param pTrans the transaction
         * @param pIterator the transaction tag iterator
         */
        protected void processTransaction(final Transaction pTrans,
                                          final Iterator<TransactionTag> pIterator) {
            /* Loop through the tags */
            while (pIterator.hasNext()) {
                final TransactionTag myTag = pIterator.next();

                /* Obtain the bucket and process the transaction */
                final TransactionTagBucket myBucket = getBucket(myTag);
                myBucket.processTransaction(pTrans);
            }
        }
    }
}
