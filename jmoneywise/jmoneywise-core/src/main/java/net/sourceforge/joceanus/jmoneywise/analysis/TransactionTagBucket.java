/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * The TransactionTag Bucket class.
 */
public final class TransactionTagBucket
        implements MetisDataContents, Comparable<TransactionTagBucket>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TRANSTAG_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * TransactionTag Field Id.
     */
    private static final MetisField FIELD_TRANSTAG = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSTAG.getItemName());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

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
        Iterator<Transaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            Transaction myTrans = myIterator.next();

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
        Iterator<Transaction> myIterator = pBase.iterator();
        while (myIterator.hasNext()) {
            Transaction myTrans = myIterator.next();

            /* Check the range */
            int iRange = pRange.compareTo(myTrans.getDate());
            if (iRange < 0) {
                break;
            } else if (iRange == 0) {
                /* Process the transaction */
                processTransaction(myTrans);
            }
        }
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_TRANSTAG.equals(pField)) {
            return theTransTag;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHashMap;
        }
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Integer getOrderedId() {
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
    private boolean isIdle() {
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
        TransactionTagBucket myThat = (TransactionTagBucket) pThat;
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
            extends MetisOrderedIdList<Integer, TransactionTagBucket>
            implements MetisDataContents {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TRANSTAG_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TransactionTagBucketList(final Analysis pAnalysis) {
            super(TransactionTagBucket.class);
            theAnalysis = pAnalysis;
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
            super(TransactionTagBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<TransactionTagBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TransactionTagBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                TransactionTagBucket myBucket = new TransactionTagBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    add(myBucket);
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
            super(TransactionTagBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<TransactionTagBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TransactionTagBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                TransactionTagBucket myBucket = new TransactionTagBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add the bucket */
                    add(myBucket);
                }
            }
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return MetisFieldValue.UNKNOWN;
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
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain an orphan TagBucket for a given tag.
         * @param pTag the tag
         * @return the bucket
         */
        public TransactionTagBucket getOrphanBucket(final TransactionTag pTag) {
            /* Allocate an orphan bucket */
            return new TransactionTagBucket(theAnalysis, pTag);
        }

        /**
         * Process transaction tags.
         * @param pTrans the transaction
         * @param pIterator the transaction tag iterator
         */
        protected void processTransaction(final Transaction pTrans,
                                          final Iterator<TransactionInfo> pIterator) {
            /* Loop through the tags */
            while (pIterator.hasNext()) {
                TransactionInfo myInfo = pIterator.next();

                /* if the item is not deleted */
                if (!myInfo.isDeleted()) {
                    /* Access details */
                    TransactionTag myTag = myInfo.getTransactionTag();

                    /* Obtain the bucket and process the transaction */
                    TransactionTagBucket myBucket = getBucket(myTag);
                    myBucket.processTransaction(pTrans);
                }
            }
        }
    }
}
