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

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisAccountBucket.MoneyWiseXAnalysisTaxBasisAccountBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseChargeableGainSlice.MoneyWiseChargeableGainSliceList;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * The TaxBasis Bucket class.
 */
public class MoneyWiseXAnalysisTaxBasisBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTaxBasisBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseXAnalysisTaxBasisBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisTaxBasisBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_HISTORY, MoneyWiseXAnalysisTaxBasisBucket::getHistoryMap);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.TAXBASIS_ACCOUNTLIST, MoneyWiseXAnalysisTaxBasisBucket::getAccounts);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisTaxBasisAttr.class, MoneyWiseXAnalysisTaxBasisBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * Tax Basis.
     */
    private final MoneyWiseTaxBasis theTaxBasis;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisTaxBasisValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisTaxBasisValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTaxBasisValues, MoneyWiseXAnalysisTaxBasisAttr> theHistory;

    /**
     * Do we have accounts?
     */
    private final boolean hasAccounts;

    /**
     * Are we an expense bucket?
     */
    private final boolean isExpense;

    /**
     * AccountBucketList.
     */
    private final MoneyWiseXAnalysisTaxBasisAccountBucketList theAccounts;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    protected MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysis pAnalysis,
                                               final MoneyWiseTaxBasis pTaxBasis) {
        /* Store the parameters */
        theTaxBasis = pTaxBasis;
        theAnalysis = pAnalysis;
        isExpense = theTaxBasis != null
                && theTaxBasis.getTaxClass().isExpense();

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseXAnalysisTaxBasisValues myValues = new MoneyWiseXAnalysisTaxBasisValues(myCurrency);
        theHistory = new MoneyWiseXAnalysisHistory<>(myValues);

        /* Create the account list */
        hasAccounts = theTaxBasis != null
                && !(this instanceof MoneyWiseXAnalysisTaxBasisAccountBucket)
                && theTaxBasis.getTaxClass().analyseAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(theAnalysis, this)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    protected MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysis pAnalysis,
                                               final MoneyWiseXAnalysisTaxBasisBucket pBase,
                                               final TethysDate pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pDate);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pDate)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    protected MoneyWiseXAnalysisTaxBasisBucket(final MoneyWiseXAnalysis pAnalysis,
                                               final MoneyWiseXAnalysisTaxBasisBucket pBase,
                                               final TethysDateRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseXAnalysisTaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pRange)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseXAnalysisTaxBasisBucket> getDataFieldSet() {
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
        return theTaxBasis.getIndexedId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTaxBasis == null
                ? NAME_TOTALS.getId()
                : theTaxBasis.getName();
    }

    /**
     * Obtain tax basis.
     * @return the basis
     */
    public MoneyWiseTaxBasis getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Do we have accounts.
     * @return true/false
     */
    public boolean hasAccounts() {
        return hasAccounts;
    }

    /**
     * Is this an expense bucket.
     * @return true/false
     */
    public boolean isExpense() {
        return isExpense;
    }

    /**
     * Obtain account list.
     * @return the account list
     */
    private MoneyWiseXAnalysisTaxBasisAccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain account list iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseXAnalysisTaxBasisAccountBucket> accountIterator() {
        return hasAccounts
                ? theAccounts.iterator()
                : null;
    }

    /**
     * find an account bucket.
     * @param pAccount the account
     * @return the bucket
     */
    public MoneyWiseXAnalysisTaxBasisAccountBucket findAccountBucket(final MoneyWiseTransAsset pAccount) {
        return hasAccounts
                ? theAccounts.findBucket(pAccount)
                : null;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseXAnalysisTaxBasisValues getValues() {
        return theValues;
    }

    /**
     * Obtain the value for a particular attribute.
     * @param pAttr the attribute
     * @return the value
     */
    public TethysMoney getMoneyValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        return theValues.getMoneyValue(pAttr);
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseXAnalysisTaxBasisValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTaxBasisValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Obtain values for event */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTaxBasisValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                          final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTaxBasisValues, MoneyWiseXAnalysisTaxBasisAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected MoneyWiseXAnalysis getAnalysis() {
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
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr,
                            final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust value.
     * @param pValue the value
     * @param pAdjust adjustment control
     */
    protected void adjustValue(final TethysMoney pValue,
                               final MoneyWiseXTaxBasisAdjust pAdjust) {
        /* If we are adjusting Gross */
        if (pAdjust.adjustGross()) {
            /* Access the existing value */
            TethysMoney myGross = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
            myGross = new TethysMoney(myGross);

            /* Subtract or add the value depending as to whether we are an expense bucket */
            if (isExpense) {
                myGross.subtractAmount(pValue);
            } else {
                myGross.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS, myGross);
        }

        /* If we are adjusting Nett */
        if (pAdjust.adjustNett()) {
            /* Access the existing value */
            TethysMoney myNett = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);
            myNett = new TethysMoney(myNett);

            /* Subtract or add the value if we are an expense/income bucket */
            if (isExpense) {
                myNett.subtractAmount(pValue);
            } else {
                myNett.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseXAnalysisTaxBasisAttr.NETT, myNett);
        }
    }

    /**
     * Register the event.
     * @param pEvent the event
     */
    protected void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Register the transaction in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final MoneyWiseXAnalysisTaxBasisBucket pBucket) {
        /* Add the values */
        TethysMoney myAmount = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS));
        myAmount = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT));
    }

    /**
     * Adjust to base.
     */
    protected void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * Value adjust Modes.
     */
    protected enum MoneyWiseXTaxBasisAdjust {
        /**
         * Adjust both Gross and Nett.
         */
        STANDARD,

        /**
         * Only adjust Nett figure.
         */
        NETT,

        /**
         * Only adjust Gross figure.
         */
        GROSS;

        /**
         * should we adjust Gross?
         * @return true/false
         */
        private boolean adjustGross() {
            return this != NETT;
        }

        /**
         * should we adjust Nett?
         * @return true/false
         */
        private boolean adjustNett() {
            return this != GROSS;
        }
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
        private final MoneyWiseXAnalysis theAnalysis;

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
         * @param pAnalysis the analysis
         * @param pGains the new Gains list
         */
        private MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                     final MoneyWiseChargeableGainSliceList pGains) {
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theCharges = pGains;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getTaxBasis().compareTo(r.getTaxBasis()));
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysis pAnalysis) {
            this(pAnalysis, new MoneyWiseChargeableGainSliceList());
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                       final MoneyWiseXAnalysisTaxBasisBucketList pBase,
                                                       final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = new MoneyWiseXAnalysisTaxBasisBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Calculate the delta and add to the list */
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
        protected MoneyWiseXAnalysisTaxBasisBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                       final MoneyWiseXAnalysisTaxBasisBucketList pBase,
                                                       final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = new MoneyWiseXAnalysisTaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
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
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
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
         * @return the totals bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         * @return the bucket
         */
        private MoneyWiseXAnalysisTaxBasisBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisTaxBasisBucket(theAnalysis, null);
        }

        /**
         * Obtain the TaxBasisBucket for a given taxBasis.
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
         * @param pTaxBasis the taxBasis
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getMatchingBasis(final MoneyWiseXAnalysisTaxBasisBucket pTaxBasis) {
            /* Access the matching taxBasis bucket */
            MoneyWiseXAnalysisTaxBasisBucket myBasis = findItemById(pTaxBasis.getTaxBasis().getIndexedId());
            if (myBasis == null) {
                myBasis = new MoneyWiseXAnalysisTaxBasisBucket(theAnalysis, pTaxBasis.getTaxBasis());
            }

            /* If we are matching a TaxBasisAccount Bucket */
            if (pTaxBasis instanceof MoneyWiseXAnalysisTaxBasisAccountBucket) {
                /* Look up the asset bucket */
                final MoneyWiseTransAsset myAsset = ((MoneyWiseXAnalysisTaxBasisAccountBucket) pTaxBasis).getAccount();
                MoneyWiseXAnalysisTaxBasisAccountBucket myAccountBucket = myBasis.findAccountBucket(myAsset);

                /* If there is no such bucket in the analysis */
                if (myAccountBucket == null) {
                    /* Allocate an orphan bucket */
                    myAccountBucket = new MoneyWiseXAnalysisTaxBasisAccountBucket(theAnalysis, myBasis, myAsset);
                }

                /* Set bucket as the account bucket */
                myBasis = myAccountBucket;
            }

            /* Return the basis */
            return myBasis;
        }

        /**
         * Obtain the default BasisBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisTaxBasisBucket getDefaultBasis() {
            /* Return the first basis in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Adjust basis buckets.
         * @param pTrans the transaction helper
         * @param pCategory primary category
         */
        protected void adjustBasis(final MoneyWiseTransaction pTrans,
                                   final MoneyWiseTransCategory pCategory) {
            /* Switch on the category type */
            switch (pCategory.getCategoryTypeClass()) {
                case TAXEDINCOME:
                case GROSSINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.SALARY);
                    break;
                case OTHERINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.OTHERINCOME);
                    break;
                case INTEREST:
                case TAXEDINTEREST:
                case TAXEDLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXEDINTEREST);
                    break;
                case GROSSINTEREST:
                case GROSSLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.UNTAXEDINTEREST);
                    break;
                case PEER2PEERINTEREST:
                    addIncome(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case DIVIDEND:
                case SHAREDIVIDEND:
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
                case TAXFREEINTEREST:
                case TAXFREEDIVIDEND:
                case LOANINTERESTEARNED:
                case INHERITED:
                case CASHBACK:
                case LOYALTYBONUS:
                case TAXFREELOYALTYBONUS:
                case GIFTEDINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                    break;
                case PENSIONCONTRIB:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                    break;
                case BADDEBTCAPITAL:
                    addExpense(pTrans, MoneyWiseTaxClass.CAPITALGAINS);
                    break;
                case BADDEBTINTEREST:
                    addExpense(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case EXPENSE:
                case LOCALTAXES:
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                case TAXRELIEF:
                case RECOVEREDEXPENSES:
                    addExpense(pTrans, MoneyWiseTaxClass.EXPENSE);
                    break;
                case RENTALEXPENSE:
                    addExpense(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                    break;
                case UNITSADJUST:
                case SECURITYREPLACE:
                case STOCKTAKEOVER:
                case STOCKSPLIT:
                case STOCKDEMERGER:
                case STOCKRIGHTSISSUE:
                case PORTFOLIOXFER:
                case TRANSFER:
                default:
                    break;
            }
        }

        /**
         * Adjust basis for income.
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addIncome(final MoneyWiseTransaction pTrans,
                               final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            //myBucket.addIncomeTransaction(pTrans);
        }

        /**
         * Adjust basis for expense.
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addExpense(final MoneyWiseTransaction pTrans,
                                final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            //myBucket.addExpenseTransaction(pTrans);
        }

        /**
         * Adjust basis buckets.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustValue(final MoneyWiseTransaction pTrans,
                                   final MoneyWiseTaxClass pClass,
                                   final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            //myBucket.adjustValue(pTrans, pIncome, MoneyWiseXTaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust basis buckets for Gross only.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustGrossValue(final MoneyWiseTransaction pTrans,
                                        final MoneyWiseTaxClass pClass,
                                        final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            //myBucket.adjustValue(pTrans, pIncome, MoneyWiseXTaxBasisAdjust.GROSS);
        }

        /**
         * Adjust basis buckets for Nett only.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustNettValue(final MoneyWiseTransaction pTrans,
                                       final MoneyWiseTaxClass pClass,
                                       final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            //myBucket.adjustValue(pTrans, pIncome, MoneyWiseXTaxBasisAdjust.NETT);
        }

        /**
         * Adjust autoExpense.
         * @param pTrans the transaction
         * @param isExpense true/false
         */
        //public void adjustAutoExpense(final MoneyWiseTransaction pTrans,
        //                              final boolean isExpense) {
            /* Determine value */
            //TethysMoney myAmount = pTrans.getLocalAmount();
            //if (!isExpense) {
            //    myAmount = new TethysMoney(myAmount);
            //    myAmount.negate();
            //}

            /* Access the bucket and adjust it */
            //final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.EXPENSE);
            //myBucket.adjustValue(pTrans, myAmount, MoneyWiseXTaxBasisAdjust.STANDARD);
        //}

        /**
         * Adjust for market growth.
         * @param pIncome the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final TethysMoney pIncome,
                                    final TethysMoney pExpense) {
            /* Calculate the delta */
            final TethysMoney myDelta = new TethysMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.MARKET);
            myBucket.adjustValue(myDelta, MoneyWiseXTaxBasisAdjust.STANDARD);
        }

        /**
         * record ChargeableGain.
         * @param pTrans the transaction
         * @param pGain the gain
         */
        public void recordChargeableGain(final MoneyWiseTransaction pTrans,
                                         final TethysMoney pGain) {
            /* record the chargeable gain */
            theCharges.addTransaction(pTrans, pGain);
        }

        /**
         * produce Totals.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myBucket = myIterator.next();

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
            final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }

        @Override
        public TethysMoney getAmountForTaxBasis(final MoneyWiseTaxClass pBasis) {
            /* Access the bucket */
            final MoneyWiseXAnalysisTaxBasisBucket myItem = findItemById(pBasis.getClassId());

            /* If the bucket is not found */
            if (myItem == null) {
                final MoneyWiseCurrency myAssetCurrency = theAnalysis.getCurrency();
                final Currency myCurrency = myAssetCurrency == null
                        ? Currency.getInstance(Locale.getDefault())
                        : myAssetCurrency.getCurrency();
                return new TethysMoney(myCurrency);
            }

            return myItem.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        }
    }
}
