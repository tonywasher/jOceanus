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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The TaxBasis Account Bucket class.
 */
public final class MoneyWiseXAnalysisTaxBasisAccountBucket
        extends MoneyWiseXAnalysisTaxBasisBucket {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisAccountBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisAccountBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, MoneyWiseXAnalysisTaxBasisAccountBucket::getAccount);
    }

    /**
     * AssetId.
     */
    private final Long theAssetId;

    /**
     * Parent.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theParent;

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
    MoneyWiseXAnalysisTaxBasisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisTaxBasisBucket pParent,
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
    private MoneyWiseXAnalysisTaxBasisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                                    final MoneyWiseXAnalysisTaxBasisBucket pParent,
                                                    final MoneyWiseXAnalysisTaxBasisAccountBucket pBase,
                                                    final OceanusDate pDate) {
        /* Copy details from base */
        super(pAnalysis, pBase, pDate);
        theAssetId = pBase.getBucketId();
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
    private MoneyWiseXAnalysisTaxBasisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                                    final MoneyWiseXAnalysisTaxBasisBucket pParent,
                                                    final MoneyWiseXAnalysisTaxBasisAccountBucket pBase,
                                                    final OceanusDateRange pRange) {
        /* Copy details from base */
        super(pAnalysis, pBase, pRange);
        theAssetId = pBase.getBucketId();
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
        return MoneyWiseAssetType.createAlternateExternalId(MoneyWiseAssetType.TAXBASISACCOUNT, pAsset.getExternalId());
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisTaxBasisAccountBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Integer getIndexedId() {
        return theAccount.getExternalId().intValue();
    }

    @Override
    public Long getBucketId() {
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
        return getTaxBasis().getName()
                + ':'
                + getSimpleName();
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
    public MoneyWiseXAnalysisTaxBasisBucket getParent() {
        return theParent;
    }

    /**
     * TaxBasisAccountBucketList class.
     */
    public static class MoneyWiseXAnalysisTaxBasisAccountBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisTaxBasisAccountBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisAccountBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisAccountBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTaxBasisAccountBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final List<MoneyWiseXAnalysisTaxBasisAccountBucket> theList;

        /**
         * Parent.
         */
        private final MoneyWiseXAnalysisTaxBasisBucket theParent;

        /**
         * Bucket map.
         */
        private final Map<Long, MoneyWiseXAnalysisTaxBasisAccountBucket> theMap;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         * @param pParent   the parent bucket
         */
        protected MoneyWiseXAnalysisTaxBasisAccountBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                              final MoneyWiseXAnalysisTaxBasisBucket pParent) {
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
        protected MoneyWiseXAnalysisTaxBasisAccountBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                              final MoneyWiseXAnalysisTaxBasisBucket pParent,
                                                              final MoneyWiseXAnalysisTaxBasisAccountBucketList pBase,
                                                              final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myBucket = new MoneyWiseXAnalysisTaxBasisAccountBucket(pAnalysis, theParent, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    theList.add(myBucket);
                    theMap.put(myBucket.getBucketId(), myBucket);
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
        protected MoneyWiseXAnalysisTaxBasisAccountBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                              final MoneyWiseXAnalysisTaxBasisBucket pParent,
                                                              final MoneyWiseXAnalysisTaxBasisAccountBucketList pBase,
                                                              final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, pParent);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisAccountBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myBucket = new MoneyWiseXAnalysisTaxBasisAccountBucket(pAnalysis, theParent, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();

                    /* Add to list and to map */
                    theList.add(myBucket);
                    theMap.put(myBucket.getBucketId(), myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisTaxBasisAccountBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisTaxBasisAccountBucket> getUnderlyingList() {
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
        protected MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Adjust value.
         *
         * @param pAccount the relevant account
         * @param pValue   the value
         * @param pAdjust  adjustment control
         * @return the adjusted taxBasisAccountBucket (or null)
         */
        MoneyWiseXAnalysisTaxBasisAccountBucket adjustValue(final MoneyWiseTransAsset pAccount,
                                                            final OceanusMoney pValue,
                                                            final MoneyWiseXTaxBasisAdjust pAdjust) {
            /* Access the relevant account bucket */
            final MoneyWiseXAnalysisTaxBasisAccountBucket myBucket = getBucket(pAccount);

            /* register deltas */
            return myBucket.adjustValue(pAccount, pValue, pAdjust);
        }

        /**
         * Obtain the TaxBasisAccountBucket for a given account.
         *
         * @param pAccount the account
         * @return the bucket
         */
        private MoneyWiseXAnalysisTaxBasisAccountBucket getBucket(final MoneyWiseTransAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetId(pAccount);
            return theMap.computeIfAbsent(myKey, m -> {
                /* Create the new bucket */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myNew = new MoneyWiseXAnalysisTaxBasisAccountBucket(theAnalysis, theParent, pAccount);

                /* Add to the list */
                theList.add(myNew);
                return myNew;
            });
        }

        /**
         * SortBuckets.
         */
        protected void sortBuckets() {
            theList.sort((l, r) -> l.getBucketId().compareTo(r.getBucketId()));
        }

        /**
         * Find the TaxBasisAccountBucket for a given account.
         *
         * @param pAccount the account
         * @return the bucket (or null)
         */
        protected MoneyWiseXAnalysisTaxBasisAccountBucket findBucket(final MoneyWiseTransAsset pAccount) {
            /* Locate the bucket in the list */
            final Long myKey = deriveAssetId(pAccount);
            return theMap.get(myKey);
        }
    }
}
