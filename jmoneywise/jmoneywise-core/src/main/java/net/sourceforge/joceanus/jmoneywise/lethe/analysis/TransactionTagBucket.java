/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * The TransactionTag Bucket class.
 */
public final class TransactionTagBucket
        implements MetisDataFieldItem, Comparable<TransactionTagBucket>, MetisIndexedItem {
    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(TransactionTagBucket.class);

    /**
     * Analysis Field Id.
     */
    private static final MetisDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * TransactionTag Field Id.
     */
    private static final MetisDataField FIELD_TRANSTAG = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSTAG.getItemName());

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

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
            final int iRange = pRange.compareTo(myTrans.getDate());
            if (iRange < 0) {
                break;
            } else if (iRange == 0) {
                /* Process the transaction */
                processTransaction(myTrans);
            }
        }
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_TRANSTAG.equals(pField)) {
            return theTransTag;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHashMap;
        }
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
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

    @Override
    public int compareTo(final TransactionTagBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Tags */
        return getTransTag().compareTo(pThat.getTransTag());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof TransactionTagBucket)) {
            return false;
        }

        /* Compare the Tags */
        final TransactionTagBucket myThat = (TransactionTagBucket) pThat;
        if (!getTransTag().equals(myThat.getTransTag())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getTransTag().hashCode();
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
            implements MetisDataFieldItem, MetisDataList<TransactionTagBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(TransactionTagBucketList.class);

        /**
         * Analysis field Id.
         */
        private static final MetisDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisIndexedList<TransactionTagBucket> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TransactionTagBucketList(final Analysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisIndexedList<>();
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
                    theList.addToList(myBucket);
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
                    theList.addToList(myBucket);
                }
            }
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<TransactionTagBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return MetisDataFieldValue.UNKNOWN;
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
                theList.addToList(myItem);
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
            final TransactionTagBucket myTag = findItemById(pTag.getOrderedId());
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
