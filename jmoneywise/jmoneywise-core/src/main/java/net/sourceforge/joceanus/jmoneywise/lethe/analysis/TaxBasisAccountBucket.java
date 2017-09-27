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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The TaxBasis Account Bucket class.
 */
public final class TaxBasisAccountBucket
        extends TaxBasisBucket {
    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(TaxBasisAccountBucket.class, TaxBasisBucket.getBaseFieldSet());

    /**
     * Parent Field Id.
     */
    private static final MetisDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.TRANSACTION_ACCOUNT.getValue());

    /**
     * Parent.
     */
    private final TaxBasisBucket theParent;

    /**
     * Account.
     */
    private final TransactionAsset theAccount;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pParent the parent bucket
     * @param pAccount the account
     */
    protected TaxBasisAccountBucket(final Analysis pAnalysis,
                                    final TaxBasisBucket pParent,
                                    final TransactionAsset pAccount) {
        /* Store the parameters */
        super(pAnalysis, pParent.getTaxBasis());
        theAccount = pAccount;
        theParent = pParent;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pParent the parent bucket
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private TaxBasisAccountBucket(final Analysis pAnalysis,
                                  final TaxBasisBucket pParent,
                                  final TaxBasisAccountBucket pBase,
                                  final TethysDate pDate) {
        /* Copy details from base */
        super(pAnalysis, pBase, pDate);
        theAccount = pBase.getAccount();
        theParent = pParent;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pParent the parent bucket
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private TaxBasisAccountBucket(final Analysis pAnalysis,
                                  final TaxBasisBucket pParent,
                                  final TaxBasisAccountBucket pBase,
                                  final TethysDateRange pRange) {
        /* Copy details from base */
        super(pAnalysis, pBase, pRange);
        theAccount = pBase.getAccount();
        theParent = pParent;
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public Integer getIndexedId() {
        return theAccount.getId();
    }

    /**
     * Obtain simple name.
     * @return the simple name
     */
    public String getSimpleName() {
        return theAccount.getName();
    }

    @Override
    public String getName() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(getTaxBasis().getName());
        myBuilder.append(':');
        myBuilder.append(getSimpleName());
        return myBuilder.toString();
    }

    /**
     * Obtain account.
     * @return the account
     */
    public TransactionAsset getAccount() {
        return theAccount;
    }

    /**
     * Obtain parent.
     * @return the parent
     */
    public TaxBasisBucket getParent() {
        return theParent;
    }

    @Override
    public int compareTo(final TaxBasisBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare types of buckets */
        int iDiff = super.compareTo(pThat);
        if (iDiff == 0
            && pThat instanceof TaxBasisAccountBucket) {
            /* Compare the Accounts */
            final TaxBasisAccountBucket myThat = (TaxBasisAccountBucket) pThat;
            iDiff = getAccount().compareTo(myThat.getAccount());
        }

        /* Return the result */
        return iDiff;
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
        if (!(pThat instanceof TaxBasisAccountBucket)) {
            return false;
        }

        /* Compare the Accounts */
        final TaxBasisAccountBucket myThat = (TaxBasisAccountBucket) pThat;
        if (!getAccount().equals(myThat.getAccount())) {
            return false;
        }

        /* Compare the tax bases */
        return super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * TaxBasisAccountBucketList class.
     */
    public static class TaxBasisAccountBucketList
            implements MetisDataFieldItem, MetisDataList<TaxBasisAccountBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(TaxBasisAccountBucketList.class);

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
        private final MetisIndexedList<TaxBasisAccountBucket> theList;

        /**
         * Parent.
         */
        private final TaxBasisBucket theParent;

        /**
         * Bucket map.
         */
        private final Map<Long, TaxBasisAccountBucket> theMap;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         * @param pParent the parent bucket
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasisBucket pParent) {
            theAnalysis = pAnalysis;
            theParent = pParent;
            theMap = new HashMap<>();
            theList = new MetisIndexedList<>();
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pParent the parent bucket
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasisBucket pParent,
                                            final TaxBasisAccountBucketList pBase,
                                            final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<TaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final TaxBasisAccountBucket myBucket = new TaxBasisAccountBucket(pAnalysis, theParent, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    theList.addToList(myBucket);
                    theMap.put(deriveAssetKey(myBucket), myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pParent the parent bucket
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasisBucket pParent,
                                            final TaxBasisAccountBucketList pBase,
                                            final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<TaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final TaxBasisAccountBucket myBucket = new TaxBasisAccountBucket(pAnalysis, theParent, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();

                    /* Add to list and to map */
                    theList.addToList(myBucket);
                    theMap.put(deriveAssetKey(myBucket), myBucket);
                }
            }
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<TaxBasisAccountBucket> getUnderlyingList() {
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
        public TaxBasisAccountBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Register delta transaction value.
         * @param pTrans the transaction helper
         * @param pGross the gross delta value
         * @param pNett the net delta value
         * @param pTax the tax delta value
         */
        protected void registerDeltaValues(final TransactionHelper pTrans,
                                           final TethysMoney pGross,
                                           final TethysMoney pNett,
                                           final TethysMoney pTax) {
            /* Determine required asset */
            final TransactionAsset myAsset = deriveAsset(pTrans);

            /* Access the relevant account bucket */
            final TaxBasisAccountBucket myBucket = getBucket(myAsset);

            /* register deltas */
            myBucket.registerDeltaValues(pTrans, pGross, pNett, pTax);
        }

        /**
         * Adjust value.
         * @param pTrans the transaction
         * @param pGross the gross delta value
         */
        protected void adjustValue(final TransactionHelper pTrans,
                                   final TethysMoney pGross) {
            /* Determine required asset */
            final TransactionAsset myAsset = deriveAsset(pTrans);

            /* Access the relevant account bucket */
            final TaxBasisAccountBucket myBucket = getBucket(myAsset);

            /* adjust value */
            myBucket.adjustValue(pTrans, pGross);
        }

        /**
         * Adjust value.
         * @param pTrans the transaction
         * @return the relevant asset
         */
        private static TransactionAsset deriveAsset(final TransactionHelper pTrans) {
            /* Determine required asset */
            TransactionAsset myAsset = pTrans.getPartner();
            if (!(myAsset instanceof Payee)) {
                myAsset = pTrans.getAccount();
            }

            /* return the asset */
            return myAsset;
        }

        /**
         * Obtain the TaxBasisAccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        private TaxBasisAccountBucket getBucket(final TransactionAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetKey(pAccount);
            TaxBasisAccountBucket myItem = theMap.get(myKey);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxBasisAccountBucket(theAnalysis, theParent, pAccount);

                /* Add to the list */
                theMap.put(myKey, myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Find the TaxBasisAccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket (or null)
         */
        protected TaxBasisAccountBucket findBucket(final TransactionAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetKey(pAccount);
            return theMap.get(myKey);
        }

        /**
         * derive asset key for a bucket.
         * @param pBucket the bucket
         * @return the asset key
         */
        private static long deriveAssetKey(final TaxBasisAccountBucket pBucket) {
            /* Calculate the key */
            return deriveAssetKey(pBucket.getAccount());
        }

        /**
         * derive asset key for an asset.
         * @param pAsset the asset
         * @return the asset key
         */
        private static long deriveAssetKey(final TransactionAsset pAsset) {
            /* Calculate the key */
            return (((long) pAsset.getAssetType().getId()) << Integer.SIZE)
                   + pAsset.getId();
        }
    }
}
