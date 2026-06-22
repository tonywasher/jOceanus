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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListIndexed;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketRegister;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisAccountBucket.MoneyWiseXAnalysisTaxBasisAccountBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseChargeableGainSlice.MoneyWiseChargeableGainSliceList;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxSource;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

/**
 * The TaxBasis Bucket class.
 */
public class MoneyWiseXAnalysisTaxBasisBucket
        extends MoneyWiseXAnalysisTaxBasisBaseBucket
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketRegister {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.TAXBASIS_ACCOUNTLIST, MoneyWiseXAnalysisTaxBasisBucket::getAccounts);
    }

    /**
     * Do we have accounts?
     */
    private final boolean hasAccounts;

    /**
     * AccountBucketList.
     */
    private final MoneyWiseXAnalysisTaxBasisAccountBucketList theAccounts;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                     final MoneyWiseTaxBasis pTaxBasis) {
        /* Initialise underlying class */
        super(pAnalysis, pTaxBasis);

        /* Create the account list */
        hasAccounts = pTaxBasis != null
                && pTaxBasis.getTaxClass().analyseAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(pAnalysis, pTaxBasis)
                : null;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                     final MoneyWiseXAnalysisTaxBasisBucket pBase,
                                     final OceanusDate pDate) {
        /* Initialise underlying class */
        super(pAnalysis, pBase, pDate);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(pAnalysis, getTaxBasis(), pBase.getAccounts(), pDate)
                : null;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                     final MoneyWiseXAnalysisTaxBasisBucket pBase,
                                     final OceanusDateRange pRange) {
        /* Initialise underlying class */
        super(pAnalysis, pBase, pRange);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(pAnalysis, getTaxBasis(), pBase.getAccounts(), pRange)
                : null;
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseXAnalysisTaxBasisBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    /**
     * Do we have accounts.
     *
     * @return true/false
     */
    public boolean hasAccounts() {
        return hasAccounts;
    }

    /**
     * Obtain account list.
     *
     * @return the account list
     */
    private MoneyWiseXAnalysisTaxBasisAccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain account list iterator.
     *
     * @return the iterator
     */
    public Iterator<MoneyWiseXAnalysisTaxBasisAccountBucket> accountIterator() {
        return hasAccounts
                ? theAccounts.iterator()
                : null;
    }

    /**
     * find an account bucket.
     *
     * @param pAccount the account
     * @return the bucket
     */
    public MoneyWiseXAnalysisTaxBasisAccountBucket findAccountBucket(final MoneyWiseTransAsset pAccount) {
        return hasAccounts
                ? theAccounts.findBucket(pAccount)
                : null;
    }

    @Override
    MoneyWiseXAnalysisTaxBasisBaseBucket adjustAccountValue(final MoneyWiseTransAsset pAccount,
                                                            final OceanusMoney pValue,
                                                            final MoneyWiseXTaxBasisAdjust pAdjust) {
        /* If we have accounts and are passed an account, adjust value for account and return the bucket */
        return hasAccounts && pAccount != null
                ? theAccounts.adjustValue(pAccount, pValue, pAdjust)
                : null;
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class MoneyWiseXAnalysisTaxBasisBucketList
            implements MetisFieldItem, MoneyWiseTaxSource, MetisDataList<MoneyWiseXAnalysisTaxBasisBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTaxBasisBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_CHARGES, MoneyWiseXAnalysisTaxBasisBucketList::getGainSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisTaxBasisBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysisHolder theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisTaxBasisBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The chargeableGains.
         */
        private final MoneyWiseChargeableGainSliceList theCharges;

        /**
         * The tax basis.
         */
        private final MoneyWiseXAnalysisTaxBasisBucket theTotals;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         * @param pGains    the new Gains list
         */
        private MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysisHolder pAnalysis,
                                                     final MoneyWiseChargeableGainSliceList pGains) {
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theCharges = pGains;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator(Comparator.comparing(MoneyWiseXAnalysisTaxBasisBucket::getTaxBasis));
        }

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         */
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysisHolder pAnalysis) {
            this(pAnalysis, new MoneyWiseChargeableGainSliceList());
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pDate     the Date
         */
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysisHolder pAnalysis,
                                                       final MoneyWiseXAnalysisTaxBasisBucketList pBase,
                                                       final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = new MoneyWiseXAnalysisTaxBasisBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
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
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysisHolder pAnalysis,
                                                       final MoneyWiseXAnalysisTaxBasisBucketList pBase,
                                                       final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = new MoneyWiseXAnalysisTaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisTaxBasisBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisTaxBasisBucket> getUnderlyingList() {
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
        protected MoneyWiseXAnalysisHolder getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         *
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisTaxBasisBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        @Override
        public MoneyWiseChargeableGainSliceList getGainSlices() {
            return theCharges;
        }

        /**
         * Obtain the Totals.
         *
         * @return the totals bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         *
         * @return the bucket
         */
        private MoneyWiseXAnalysisTaxBasisBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisTaxBasisBucket(theAnalysis, null);
        }

        /**
         * Obtain the TaxBasisBucket for a given taxBasis.
         *
         * @param pClass the taxBasis
         * @return the bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getBucket(final MoneyWiseTaxClass pClass) {
            /* Locate the bucket in the list */
            final MoneyWiseTaxBasis myBasis = theEditSet.getDataList(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseTaxBasisList.class).findItemByClass(pClass);
            MoneyWiseXAnalysisTaxBasisBucket myItem = findItemById(myBasis.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisTaxBasisBucket(theAnalysis, myBasis);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the matching BasisBucket.
         *
         * @param pTaxBasis the taxBasis
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisTaxBasisBaseBucket getMatchingBasis(final MoneyWiseXAnalysisTaxBasisBaseBucket pTaxBasis) {
            /* Access the matching taxBasis bucket */
            MoneyWiseXAnalysisTaxBasisBaseBucket myBasis = findItemById(pTaxBasis.getTaxBasis().getIndexedId());
            if (myBasis == null) {
                myBasis = new MoneyWiseXAnalysisTaxBasisBucket(theAnalysis, pTaxBasis.getTaxBasis());
            }

            /* If we are matching a TaxBasisAccount Bucket */
            if (pTaxBasis instanceof MoneyWiseXAnalysisTaxBasisAccountBucket myBucket) {
                /* Look up the asset bucket */
                final MoneyWiseTransAsset myAsset = myBucket.getAccount();
                MoneyWiseXAnalysisTaxBasisAccountBucket myAccountBucket =
                        ((MoneyWiseXAnalysisTaxBasisBucket) myBasis).findAccountBucket(myAsset);

                /* If there is no such bucket in the analysis */
                if (myAccountBucket == null) {
                    /* Allocate an orphan bucket */
                    myAccountBucket = new MoneyWiseXAnalysisTaxBasisAccountBucket(theAnalysis, myBasis.getTaxBasis(), myAsset);
                }

                /* Set bucket as the account bucket */
                myBasis = myAccountBucket;
            }

            /* Return the basis */
            return myBasis;
        }

        /**
         * Obtain the default BasisBucket.
         *
         * @return the default bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getDefaultBasis() {
            /* Return the first basis in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().getFirst();
        }

        /**
         * record ChargeableGain.
         *
         * @param pTrans the transaction
         * @param pGain  the gain
         * @param pSlice the slice
         * @param pYears the years
         */
        public void recordChargeableGain(final MoneyWiseTransaction pTrans,
                                         final OceanusMoney pGain,
                                         final OceanusMoney pSlice,
                                         final Integer pYears) {
            /* record the chargeable gain */
            theCharges.addTransaction(pTrans, pGain, pSlice, pYears);
        }

        /**
         * produce Totals.
         */
        public void produceTotals() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = myIterator.next();

                /* Remove idle items */
                if (myBucket.isIdle()) {
                    myIterator.remove();
                    continue;
                }

                /* Sort the accounts */
                if (myBucket.hasAccounts()) {
                    myBucket.getAccounts().sortBuckets();
                }

                /* Adjust the Total Profit buckets */
                theTotals.addValues(myBucket);
            }

            /* Sort the bases */
            theList.sortList();
        }

        @Override
        public OceanusMoney getAmountForTaxBasis(final MoneyWiseTaxClass pBasis) {
            /* Access the bucket */
            final MoneyWiseXAnalysisTaxBasisBucket myItem = findItemById(pBasis.getClassId());

            /* If the bucket is not found */
            if (myItem == null) {
                final MoneyWiseCurrency myAssetCurrency = theAnalysis.getCurrency();
                final Currency myCurrency = myAssetCurrency == null
                        ? OceanusMoney.getDefaultCurrency()
                        : myAssetCurrency.getCurrency();
                return new OceanusMoney(myCurrency);
            }

            return myItem.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        }
    }
}
