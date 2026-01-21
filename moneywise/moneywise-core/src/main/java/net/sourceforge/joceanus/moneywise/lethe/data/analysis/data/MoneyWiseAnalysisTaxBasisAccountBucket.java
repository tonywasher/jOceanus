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
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The TaxBasis Account Bucket class.
 */
public final class MoneyWiseAnalysisTaxBasisAccountBucket
        extends MoneyWiseAnalysisTaxBasisBucket {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisAccountBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisAccountBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, MoneyWiseAnalysisTaxBasisAccountBucket::getAccount);
    }

    /**
     * AssetId.
     */
    private final Long theAssetId;

    /**
     * Parent.
     */
    private final MoneyWiseAnalysisTaxBasisBucket theParent;

    /**
     * Account.
     */
    private final MoneyWiseTransAsset theAccount;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pParent   the parent bucket
     * @param pAccount  the account
     */
    MoneyWiseAnalysisTaxBasisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisTaxBasisBucket pParent,
                                           final MoneyWiseTransAsset pAccount) {
        /* Store the parameters */
        super(pAnalysis, pParent.getTaxBasis());
        theAssetId = deriveAssetId(pAccount);
        theAccount = pAccount;
        theParent = pParent;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pParent   the parent bucket
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    private MoneyWiseAnalysisTaxBasisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                                   final MoneyWiseAnalysisTaxBasisBucket pParent,
                                                   final MoneyWiseAnalysisTaxBasisAccountBucket pBase,
                                                   final OceanusDate pDate) {
        /* Copy details from base */
        super(pAnalysis, pBase, pDate);
        theAssetId = pBase.getAssetId();
        theAccount = pBase.getAccount();
        theParent = pParent;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pParent   the parent bucket
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    private MoneyWiseAnalysisTaxBasisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                                   final MoneyWiseAnalysisTaxBasisBucket pParent,
                                                   final MoneyWiseAnalysisTaxBasisAccountBucket pBase,
                                                   final OceanusDateRange pRange) {
        /* Copy details from base */
        super(pAnalysis, pBase, pRange);
        theAssetId = pBase.getAssetId();
        theAccount = pBase.getAccount();
        theParent = pParent;
    }

    /**
     * derive asset key for an asset.
     *
     * @param pAsset the asset
     * @return the asset key
     */
    private static Long deriveAssetId(final MoneyWiseTransAsset pAsset) {
        /* Calculate the key */
        return pAsset.getExternalId();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisTaxBasisAccountBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Integer getIndexedId() {
        return theAccount.getExternalId().intValue();
    }

    /**
     * Obtain assetId.
     *
     * @return the assetId
     */
    public Long getAssetId() {
        return theAssetId;
    }

    /**
     * Obtain simple name.
     *
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
     *
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return theAccount;
    }

    /**
     * Obtain parent.
     *
     * @return the parent
     */
    public MoneyWiseAnalysisTaxBasisBucket getParent() {
        return theParent;
    }

    /**
     * TaxBasisAccountBucketList class.
     */
    public static class MoneyWiseAnalysisTaxBasisAccountBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisTaxBasisAccountBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisAccountBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisAccountBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTaxBasisAccountBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final List<MoneyWiseAnalysisTaxBasisAccountBucket> theList;

        /**
         * Parent.
         */
        private final MoneyWiseAnalysisTaxBasisBucket theParent;

        /**
         * Bucket map.
         */
        private final Map<Long, MoneyWiseAnalysisTaxBasisAccountBucket> theMap;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         * @param pParent   the parent bucket
         */
        protected MoneyWiseAnalysisTaxBasisAccountBucketList(final MoneyWiseAnalysis pAnalysis,
                                                             final MoneyWiseAnalysisTaxBasisBucket pParent) {
            theAnalysis = pAnalysis;
            theParent = pParent;
            theMap = new HashMap<>();
            theList = new ArrayList<>();
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pParent   the parent bucket
         * @param pBase     the base list
         * @param pDate     the Date
         */
        protected MoneyWiseAnalysisTaxBasisAccountBucketList(final MoneyWiseAnalysis pAnalysis,
                                                             final MoneyWiseAnalysisTaxBasisBucket pParent,
                                                             final MoneyWiseAnalysisTaxBasisAccountBucketList pBase,
                                                             final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = new MoneyWiseAnalysisTaxBasisAccountBucket(pAnalysis, theParent, myCurr, pDate);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Calculate the delta and add to the list */
                    theList.add(myBucket);
                    theMap.put(myBucket.getAssetId(), myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         *
         * @param pAnalysis the analysis
         * @param pParent   the parent bucket
         * @param pBase     the base list
         * @param pRange    the Date Range
         */
        protected MoneyWiseAnalysisTaxBasisAccountBucketList(final MoneyWiseAnalysis pAnalysis,
                                                             final MoneyWiseAnalysisTaxBasisBucket pParent,
                                                             final MoneyWiseAnalysisTaxBasisAccountBucketList pBase,
                                                             final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = new MoneyWiseAnalysisTaxBasisAccountBucket(pAnalysis, theParent, myCurr, pRange);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();

                    /* Add to list and to map */
                    theList.add(myBucket);
                    theMap.put(myBucket.getAssetId(), myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisTaxBasisAccountBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisTaxBasisAccountBucket> getUnderlyingList() {
            return theList;
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
        protected MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Register delta transaction value.
         *
         * @param pTrans the transaction helper
         * @param pGross the gross delta value
         * @param pNett  the net delta value
         * @param pTax   the tax delta value
         */
        protected void registerDeltaValues(final MoneyWiseAnalysisTransactionHelper pTrans,
                                           final OceanusMoney pGross,
                                           final OceanusMoney pNett,
                                           final OceanusMoney pTax) {
            /* Determine required asset */
            final MoneyWiseTransAsset myAsset = deriveAsset(pTrans);

            /* Access the relevant account bucket */
            final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = getBucket(myAsset);

            /* register deltas */
            myBucket.registerDeltaValues(pTrans, pGross, pNett, pTax);
        }

        /**
         * Adjust value.
         *
         * @param pTrans  the transaction
         * @param pGross  the gross delta value
         * @param pAdjust adjustment control
         */
        protected void adjustValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                   final OceanusMoney pGross,
                                   final MoneyWiseTaxBasisAdjust pAdjust) {
            /* Determine required asset */
            final MoneyWiseTransAsset myAsset = deriveAsset(pTrans);

            /* Access the relevant account bucket */
            final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = getBucket(myAsset);

            /* adjust value */
            myBucket.adjustValue(pTrans, pGross, pAdjust);
        }

        /**
         * Adjust value.
         *
         * @param pTrans the transaction
         * @return the relevant asset
         */
        private static MoneyWiseTransAsset deriveAsset(final MoneyWiseAnalysisTransactionHelper pTrans) {
            /* Determine required asset */
            MoneyWiseTransAsset myAsset = pTrans.getPartner();
            if (!(myAsset instanceof MoneyWisePayee)) {
                myAsset = pTrans.getAccount();
            }

            /* return the asset */
            return myAsset;
        }

        /**
         * Obtain the TaxBasisAccountBucket for a given account.
         *
         * @param pAccount the account
         * @return the bucket
         */
        private MoneyWiseAnalysisTaxBasisAccountBucket getBucket(final MoneyWiseTransAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetId(pAccount);
            MoneyWiseAnalysisTaxBasisAccountBucket myItem = theMap.get(myKey);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisTaxBasisAccountBucket(theAnalysis, theParent, pAccount);

                /* Add to the list */
                theList.add(myItem);
                theMap.put(myKey, myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * SortBuckets.
         */
        protected void sortBuckets() {
            theList.sort((l, r) -> l.getAssetId().compareTo(r.getAssetId()));
        }

        /**
         * Find the TaxBasisAccountBucket for a given account.
         *
         * @param pAccount the account
         * @return the bucket (or null)
         */
        protected MoneyWiseAnalysisTaxBasisAccountBucket findBucket(final MoneyWiseTransAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetId(pAccount);
            return theMap.get(myKey);
        }
    }
}
