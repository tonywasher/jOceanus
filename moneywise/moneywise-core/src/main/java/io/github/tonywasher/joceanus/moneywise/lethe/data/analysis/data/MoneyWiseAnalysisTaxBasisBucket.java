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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListIndexed;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisAccountBucket.MoneyWiseAnalysisTaxBasisAccountBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
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
import java.util.Objects;

/**
 * The TaxBasis Bucket class.
 */
public class MoneyWiseAnalysisTaxBasisBucket
        extends MoneyWiseAnalysisTaxBasisBaseBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.TAXBASIS_ACCOUNTLIST, MoneyWiseAnalysisTaxBasisBucket::getAccounts);
    }

    /**
     * Do we have accounts?
     */
    private final boolean hasAccounts;

    /**
     * AccountBucketList.
     */
    private final MoneyWiseAnalysisTaxBasisAccountBucketList theAccounts;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysisControl pAnalysis,
                                    final MoneyWiseTaxBasis pTaxBasis) {
        /* Initialise underlying class */
        super(pAnalysis, pTaxBasis);

        /* Create the account list */
        hasAccounts = pTaxBasis != null
                && pTaxBasis.getTaxClass().analyseAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(pAnalysis, pTaxBasis)
                : null;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysisControl pAnalysis,
                                    final MoneyWiseAnalysisTaxBasisBucket pBase,
                                    final OceanusDate pDate) {
        /* Initialise underlying class */
        super(pAnalysis, pBase, pDate);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(pAnalysis, getTaxBasis(), pBase.getAccounts(), pDate)
                : null;
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysisControl pAnalysis,
                                    final MoneyWiseAnalysisTaxBasisBucket pBase,
                                    final OceanusDateRange pRange) {
        /* Initialise underlying class */
        super(pAnalysis, pBase, pRange);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(pAnalysis, getTaxBasis(), pBase.getAccounts(), pRange)
                : null;
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseAnalysisTaxBasisBucket> getDataFieldSet() {
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
    private MoneyWiseAnalysisTaxBasisAccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain account list iterator.
     *
     * @return the iterator
     */
    public Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> accountIterator() {
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
    public MoneyWiseAnalysisTaxBasisAccountBucket findAccountBucket(final MoneyWiseTransAsset pAccount) {
        return hasAccounts
                ? theAccounts.findBucket(pAccount)
                : null;
    }

    @Override
    void adjustAccountsIncome(final MoneyWiseAnalysisTransactionHelper pTrans,
                              final OceanusMoney pGross,
                              final OceanusMoney pNett,
                              final OceanusMoney pTax) {
        /* If we have accounts */
        if (hasAccounts) {
            /* register the changes against the accounts */
            theAccounts.registerDeltaValues(pTrans, pGross, pNett, pTax);
        }
    }

    @Override
    void adjustAccountsExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                               final OceanusMoney pGross,
                               final OceanusMoney pNett,
                               final OceanusMoney pTax) {
        /* If we have accounts */
        if (hasAccounts) {
            /* register the changes against the accounts */
            theAccounts.registerDeltaValues(pTrans, pGross, pNett, pTax);
        }
    }


    @Override
    void adjustAccountsValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                             final OceanusMoney pValue,
                             final MoneyWiseTaxBasisAdjust pAdjust) {
        /* If we have accounts */
        if (hasAccounts) {
            /* register the adjustment against the accounts */
            theAccounts.adjustValue(pTrans, pValue, pAdjust);
        }
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class MoneyWiseAnalysisTaxBasisBucketList
            implements MetisFieldItem, MoneyWiseTaxSource, MetisDataList<MoneyWiseAnalysisTaxBasisBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTaxBasisBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_CHARGES, MoneyWiseAnalysisTaxBasisBucketList::getGainSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisTaxBasisBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysisControl theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisTaxBasisBucket> theList;

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
        private final MoneyWiseAnalysisTaxBasisBucket theTotals;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         * @param pGains    the new Gains list
         */
        private MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysisControl pAnalysis,
                                                    final MoneyWiseChargeableGainSliceList pGains) {
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theCharges = pGains;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator(Comparator.comparing(MoneyWiseAnalysisTaxBasisBucket::getTaxBasis));
        }

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         */
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysisControl pAnalysis) {
            this(pAnalysis, new MoneyWiseChargeableGainSliceList());
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pDate     the Date
         */
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysisControl pAnalysis,
                                                      final MoneyWiseAnalysisTaxBasisBucketList pBase,
                                                      final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisTaxBasisBucket myBucket = new MoneyWiseAnalysisTaxBasisBucket(pAnalysis, myCurr, pDate);

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
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysisControl pAnalysis,
                                                      final MoneyWiseAnalysisTaxBasisBucketList pBase,
                                                      final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisTaxBasisBucket myBucket = new MoneyWiseAnalysisTaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisTaxBasisBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisTaxBasisBucket> getUnderlyingList() {
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
        protected MoneyWiseAnalysisControl getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         *
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisTaxBasisBucket findItemById(final Integer pId) {
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
        public MoneyWiseAnalysisTaxBasisBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         *
         * @return the bucket
         */
        private MoneyWiseAnalysisTaxBasisBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, null);
        }

        /**
         * Obtain the TaxBasisBucket for a given taxBasis.
         *
         * @param pClass the taxBasis
         * @return the bucket
         */
        public MoneyWiseAnalysisTaxBasisBucket getBucket(final MoneyWiseTaxClass pClass) {
            /* Locate the bucket in the list */
            final MoneyWiseTaxBasis myBasis = theEditSet.getDataList(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseTaxBasisList.class).findItemByClass(pClass);
            MoneyWiseAnalysisTaxBasisBucket myItem = findItemById(myBasis.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, myBasis);

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
        public MoneyWiseAnalysisTaxBasisBaseBucket getMatchingBasis(final MoneyWiseAnalysisTaxBasisBaseBucket pTaxBasis) {
            /* Access the matching taxBasis bucket */
            MoneyWiseAnalysisTaxBasisBaseBucket myBasis = findItemById(pTaxBasis.getTaxBasis().getIndexedId());
            if (myBasis == null) {
                myBasis = new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, pTaxBasis.getTaxBasis());
            }

            /* If we are matching a TaxBasisAccount Bucket */
            if (pTaxBasis instanceof MoneyWiseAnalysisTaxBasisAccountBucket myBucket) {
                /* Look up the asset bucket */
                final MoneyWiseTransAsset myAsset = myBucket.getAccount();
                MoneyWiseAnalysisTaxBasisAccountBucket myAccountBucket
                        = ((MoneyWiseAnalysisTaxBasisBucket) myBasis).findAccountBucket(myAsset);

                /* If there is no such bucket in the analysis */
                if (myAccountBucket == null) {
                    /* Allocate an orphan bucket */
                    myAccountBucket = new MoneyWiseAnalysisTaxBasisAccountBucket(theAnalysis, myBasis.getTaxBasis(), myAsset);
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
        public MoneyWiseAnalysisTaxBasisBucket getDefaultBasis() {
            /* Return the first basis in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().getFirst();
        }

        /**
         * Adjust basis buckets.
         *
         * @param pTrans    the transaction helper
         * @param pCategory primary category
         */
        protected void adjustBasis(final MoneyWiseAnalysisTransactionHelper pTrans,
                                   final MoneyWiseTransCategory pCategory) {
            /* Switch on the category type */
            switch (Objects.requireNonNull(pCategory.getCategoryTypeClass())) {
                case TAXEDINCOME, GROSSINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.SALARY);
                    break;
                case OTHERINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.OTHERINCOME);
                    break;
                case INTEREST, TAXEDINTEREST, TAXEDLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXEDINTEREST);
                    break;
                case GROSSINTEREST, GROSSLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.UNTAXEDINTEREST);
                    break;
                case PEER2PEERINTEREST:
                    addIncome(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case DIVIDEND, SHAREDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.DIVIDEND);
                    break;
                case UNITTRUSTDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.UNITTRUSTDIVIDEND);
                    break;
                case FOREIGNDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.FOREIGNDIVIDEND);
                    break;
                case RENTALINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                    break;
                case ROOMRENTALINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.ROOMRENTAL);
                    break;
                case INCOMETAX:
                    addExpense(pTrans, MoneyWiseTaxClass.TAXPAID);
                    break;
                case TAXFREEINTEREST, TAXFREEDIVIDEND, LOANINTERESTEARNED, INHERITED,
                     CASHBACK, LOYALTYBONUS, TAXFREELOYALTYBONUS, GIFTEDINCOME, PENSIONCONTRIB:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                    break;
                case BADDEBTCAPITAL:
                    addExpense(pTrans, MoneyWiseTaxClass.CAPITALGAINS);
                    break;
                case BADDEBTINTEREST:
                    addExpense(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case EXPENSE, LOCALTAXES, WRITEOFF, LOANINTERESTCHARGED, TAXRELIEF, RECOVEREDEXPENSES:
                    addExpense(pTrans, MoneyWiseTaxClass.EXPENSE);
                    break;
                case RENTALEXPENSE:
                    addExpense(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                    break;
                default:
                    break;
            }
        }

        /**
         * Adjust basis for income.
         *
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addIncome(final MoneyWiseAnalysisTransactionHelper pTrans,
                               final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addIncomeTransaction(pTrans);
        }

        /**
         * Adjust basis for expense.
         *
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addExpenseTransaction(pTrans);
        }

        /**
         * Adjust basis buckets.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                   final MoneyWiseTaxClass pClass,
                                   final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust basis buckets for Gross only.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustGrossValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                        final MoneyWiseTaxClass pClass,
                                        final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.GROSS);
        }

        /**
         * Adjust basis buckets for Nett only.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustNettValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                       final MoneyWiseTaxClass pClass,
                                       final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.NETT);
        }

        /**
         * Adjust autoExpense.
         *
         * @param pTrans    the transaction
         * @param isExpense true/false
         */
        public void adjustAutoExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                      final boolean isExpense) {
            /* Determine value */
            OceanusMoney myAmount = pTrans.getLocalAmount();
            if (!isExpense) {
                myAmount = new OceanusMoney(myAmount);
                myAmount.negate();
            }

            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.EXPENSE);
            myBucket.adjustValue(pTrans, myAmount, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust for market growth.
         *
         * @param pIncome  the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final OceanusMoney pIncome,
                                    final OceanusMoney pExpense) {
            /* Calculate the delta */
            final OceanusMoney myDelta = new OceanusMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.MARKET);
            myBucket.adjustValue(myDelta, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * record ChargeableGain.
         *
         * @param pTrans the transaction
         * @param pGain  the gain
         */
        public void recordChargeableGain(final MoneyWiseTransaction pTrans,
                                         final OceanusMoney pGain) {
            /* record the chargeable gain */
            theCharges.addTransaction(pTrans, pGain);
        }

        /**
         * produce Totals.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myBucket = myIterator.next();

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

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }

        @Override
        public OceanusMoney getAmountForTaxBasis(final MoneyWiseTaxClass pBasis) {
            /* Access the bucket */
            final MoneyWiseAnalysisTaxBasisBucket myItem = findItemById(pBasis.getClassId());

            /* If the bucket is not found */
            if (myItem == null) {
                final MoneyWiseCurrency myAssetCurrency = theAnalysis.getCurrency();
                final Currency myCurrency = myAssetCurrency == null
                        ? OceanusMoney.getDefaultCurrency()
                        : myAssetCurrency.getCurrency();
                return new OceanusMoney(myCurrency);
            }

            return myItem.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        }
    }
}
