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

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Portfolio Bucket.
 */
public final class MoneyWiseXAnalysisPortfolioBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisPortfolioBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisPortfolioBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO, MoneyWiseXAnalysisPortfolioBucket::getPortfolio);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisPortfolioBucket::getPortfolioCash);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASH, MoneyWiseXAnalysisPortfolioBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.SECURITY.getListId(), MoneyWiseXAnalysisPortfolioBucket::getSecurities);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisAccountAttr.class, MoneyWiseXAnalysisPortfolioBucket::getValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS;

    /**
     * The portfolio.
     */
    private final MoneyWisePortfolio thePortfolio;

    /**
     * The reporting currency.
     */
    private final MoneyWiseCurrency theCurrency;

    /**
     * The cash bucket.
     */
    private final MoneyWiseXAnalysisPortfolioCashBucket theCash;

    /**
     * The security bucket list.
     */
    private final MoneyWiseXAnalysisSecurityBucketList theSecurities;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisAccountValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisAccountValues theBaseValues;

    /**
     * Is this a foreign currency?
     */
    private final Boolean isForeignCurrency;

    /**
     * Does this portfolio have foreign currency values?
     */
    private boolean hasForeignCurrency;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPortfolio the portfolio account
     */
    private MoneyWiseXAnalysisPortfolioBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWisePortfolio pPortfolio) {
        /* Store the portfolio */
        thePortfolio = pPortfolio;
        theCurrency = pAnalysis.getCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseXAnalysisPortfolioCashBucket(pAnalysis, pPortfolio);

        /* Create the securities list */
        theSecurities = pPortfolio != null
                ? new MoneyWiseXAnalysisSecurityBucketList(pAnalysis)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : theCurrency.getCurrency();
        theValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        theBaseValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        //initValues();

        /* Determine whether the portfolio is a foreign currency */
        isForeignCurrency = !MetisDataDifference.isEqual(pAnalysis.getCurrency(), theCurrency);
        hasForeignCurrency = isForeignCurrency;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseXAnalysisPortfolioBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisPortfolioBucket pBase,
                                              final TethysDate pDate) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseXAnalysisPortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pDate);

        /* Create the securities list */
        theSecurities = thePortfolio != null
                ? new MoneyWiseXAnalysisSecurityBucketList(pAnalysis, pBase.getSecurities(), pDate)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        theBaseValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        //initValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the date range for the bucket
     */
    private MoneyWiseXAnalysisPortfolioBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisPortfolioBucket pBase,
                                              final TethysDateRange pRange) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseXAnalysisPortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pRange);

        /* Create the securities list */
        theSecurities = thePortfolio != null
                ? new MoneyWiseXAnalysisSecurityBucketList(pAnalysis, pBase.getSecurities(), pRange)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        theBaseValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        //initValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisPortfolioBucket> getDataFieldSet() {
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

    /**
     * Is this a foreign currency?
     * @return true/false
     */
    public Boolean isForeignCurrency() {
        return isForeignCurrency;
    }

    /**
     * Does this portfolio hold foreign currency accounts/securities?
     * @return true/false
     */
    public Boolean hasForeignCurrency() {
        return hasForeignCurrency;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return thePortfolio == null
                ? NAME_TOTALS.getId()
                : thePortfolio.getName();
    }

    @Override
    public Integer getIndexedId() {
        return thePortfolio.getIndexedId();
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public MoneyWisePortfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public MoneyWiseCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the portfolio cash bucket.
     * @return the bucket
     */
    public MoneyWiseXAnalysisPortfolioCashBucket getPortfolioCash() {
        return theCash;
    }

    /**
     * Obtain the security buckets.
     * @return the buckets
     */
    public MoneyWiseXAnalysisSecurityBucketList getSecurities() {
        return theSecurities;
    }

    /**
     * Obtain the security bucket iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseXAnalysisSecurityBucket> securityIterator() {
        return theSecurities.iterator();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseXAnalysisAccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public MoneyWiseXAnalysisAccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseXAnalysisAccountAttr pAttr,
                  final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
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
    private Object getValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security holding.
     * @param pHolding the security holding
     * @return the bucket
     */
    public MoneyWiseXAnalysisSecurityBucket getSecurityBucket(final MoneyWiseSecurityHolding pHolding) {
        /* Return the security bucket for the portfolio's list */
        return theSecurities.getBucket(pHolding);
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security.
     * @param pSecurity the security
     * @return the bucket
     */
    public MoneyWiseXAnalysisSecurityBucket findSecurityBucket(final MoneyWiseSecurity pSecurity) {
        /* Return the security bucket for the portfolio's list */
        return theSecurities.findItemById(pSecurity.getIndexedId());
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
        if (!(pThat instanceof MoneyWiseXAnalysisPortfolioBucket)) {
            return false;
        }

        /* Compare the Portfolios */
        final MoneyWiseXAnalysisPortfolioBucket myThat = (MoneyWiseXAnalysisPortfolioBucket) pThat;
        return getPortfolio().equals(myThat.getPortfolio());
    }

    @Override
    public int hashCode() {
        return getPortfolio().hashCode();
    }

    /**
     * Calculate delta.
     */
    void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        final TethysMoney myBase = theBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA, myValue);
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    void addValues(final MoneyWiseXAnalysisSecurityBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());

        /* Adjust foreign currency indication */
        hasForeignCurrency |= pBucket.isForeignCurrency();
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    void addValues(final MoneyWiseXAnalysisPortfolioCashBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseXAnalysisAccountValues pTotals,
                                  final MoneyWiseXAnalysisAccountValues pSource) {
        /* Add valuation values */
        final TethysMoney myValue = pTotals.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseXAnalysisAccountValues pTotals,
                                  final MoneyWiseXAnalysisSecurityValues pSource) {
        /* Add valuation values */
        final TethysMoney myValue = pTotals.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Is the portfolio bucket active?
     * @return true/false
     */
    public boolean isActive() {
        /* Look for active cash */
        if (theCash.isActive()) {
            return true;
        }

        /* Loop through securities */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket mySecurity = myIterator.next();

            /* Look for active security */
            if (mySecurity.isActive()) {
                return true;
            }
        }

        /* Inactive */
        return false;
    }

    /**
     * Is the portfolio bucket idle?
     * @return true/false
     */
    public boolean isIdle() {
        /* Look for non-idle cash */
        if (!theCash.isIdle()) {
            return false;
        }

        /* Loop through securities */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket mySecurity = myIterator.next();

            /* Look for active security */
            if (!mySecurity.isIdle()) {
                return false;
            }
        }

        /* Idle */
        return true;
    }

    /**
     * Obtain cash valuation.
     * @param pBase get base valuation - true/false
     * @return the valuation minus the cash value
     */
    public TethysMoney getCashValue(final boolean pBase) {
        /* Obtain the cash valuation */
        final MoneyWiseXAnalysisAccountValues myCashValues = pBase
                ? theCash.getBaseValues()
                : theCash.getValues();
        return new TethysMoney(myCashValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
    }

    /**
     * Obtain non-cash valuation.
     * @param pBase get base valuation - true/false
     * @return the valuation minus the cash value
     */
    public TethysMoney getNonCashValue(final boolean pBase) {
        /* Handle valuation by subtracting the cash valuation */
        final MoneyWiseXAnalysisAccountValues myValues = pBase
                ? theBaseValues
                : theValues;
        final TethysMoney myValue = new TethysMoney(myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        myValue.subtractAmount(getCashValue(pBase));
        return myValue;
    }

    /**
     * PortfolioBucket list class.
     */
    public static final class MoneyWiseXAnalysisPortfolioBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisPortfolioBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisPortfolioBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisPortfolioBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisPortfolioBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisPortfolioBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisPortfolioBucket> theList;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisPortfolioBucket theTotals;

        /**
         * Do we have a foreign portfolio account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Do we have active securities?
         */
        private Boolean haveActiveSecurities = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisPortfolioBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getPortfolio().compareTo(r.getPortfolio()));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisPortfolioBucketList(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisPortfolioBucketList pBase,
                                              final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisPortfolioBucket myBucket = new MoneyWiseXAnalysisPortfolioBucket(pAnalysis, myCurr, pDate);

                /* Ignore if portfolio is idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
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
        MoneyWiseXAnalysisPortfolioBucketList(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisPortfolioBucketList pBase,
                                              final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisPortfolioBucket myBucket = new MoneyWiseXAnalysisPortfolioBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisPortfolioBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisPortfolioBucket> getUnderlyingList() {
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
        MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseXAnalysisPortfolioBucket getTotals() {
            return theTotals;
        }

        /**
         * Do we have a foreign currency?
         * @return true/false
         */
        public Boolean haveForeignCurrency() {
            return haveForeignCurrency;
        }

        /**
         * Do we have active securities?
         * @return true/false
         */
        public Boolean haveActiveSecurities() {
            return haveActiveSecurities;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisPortfolioBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the PortfolioBucket for a given portfolio.
         * @param pPortfolio the portfolio
         * @return the bucket
         */
        public MoneyWiseXAnalysisPortfolioBucket getBucket(final MoneyWisePortfolio pPortfolio) {
            /* Locate the bucket in the list */
            MoneyWiseXAnalysisPortfolioBucket myItem = findItemById(pPortfolio.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisPortfolioBucket(theAnalysis, pPortfolio);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the PortfolioBucket for a given portfolio.
         * @param pPortfolio the portfolio
         * @return the bucket
         */
        public MoneyWiseXAnalysisPortfolioCashBucket getCashBucket(final MoneyWisePortfolio pPortfolio) {
            /* Locate the bucket in the list */
            final MoneyWiseXAnalysisPortfolioBucket myItem = getBucket(pPortfolio);

            /* Return the bucket */
            return myItem.getPortfolioCash();
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the holding
         * @return the bucket
         */
        public MoneyWiseXAnalysisSecurityBucket getBucket(final MoneyWiseSecurityHolding pHolding) {
            /* Locate the portfolio bucket in the list */
            final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
            final MoneyWiseXAnalysisPortfolioBucket myBucket = getBucket(myPortfolio);

            /* Return the security bucket for the portfolio's list */
            return myBucket.getSecurityBucket(pHolding);
        }

        /**
         * Obtain the matching PortfolioBucket.
         * @param pPortfolio the portfolio
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisPortfolioBucket getMatchingPortfolio(final MoneyWisePortfolio pPortfolio) {
            /* Return the matching portfolio if it exists else an orphan bucket */
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = findItemById(pPortfolio.getIndexedId());
            return myPortfolio != null
                    ? myPortfolio
                    : new MoneyWiseXAnalysisPortfolioBucket(theAnalysis, pPortfolio);
        }

        /**
         * Obtain the matching SecurityBucket.
         * @param pSecurity the security
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisSecurityBucket getMatchingSecurityHolding(final MoneyWiseSecurityHolding pSecurity) {
            /* Find the portfolio and holding */
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = findItemById(pSecurity.getPortfolio().getIndexedId());
            final MoneyWiseXAnalysisSecurityBucket mySecurity = myPortfolio == null
                    ? null
                    : myPortfolio.findSecurityBucket(pSecurity.getSecurity());
            return mySecurity != null
                    ? mySecurity
                    : new MoneyWiseXAnalysisSecurityBucket(theAnalysis, pSecurity);
        }

        /**
         * Obtain the default PortfolioBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisPortfolioBucket getDefaultPortfolio() {
            /* Return the first portfolio in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Obtain the default SecurityBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisSecurityBucket getDefaultSecurityHolding() {
            /* Loop through the portfolio buckets */
            final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPortfolioBucket myPortfolio = myIterator.next();

                /* Loop through the security buckets */
                final Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = myPortfolio.securityIterator();
                if (mySecIterator.hasNext()) {
                    /* Access bucket and category */
                    return mySecIterator.next();
                }
            }

            /* No security bucket found */
            return null;
        }

        /**
         * Allocate the Totals PortfolioBucket.
         * @return the bucket
         */
        private MoneyWiseXAnalysisPortfolioBucket allocateTotalsBucket() {
            /* Obtain the totals portfolio */
            return new MoneyWiseXAnalysisPortfolioBucket(theAnalysis, (MoneyWisePortfolio) null);
        }

        /**
         * Analyse securities.
          */
        void analyseSecurities() {
            /* Access details */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final MoneyWiseXAnalysisPortfolioCashBucket myCashTotals = theTotals.getPortfolioCash();

            /* Loop through the portfolio buckets */
            final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPortfolioBucket myPortfolio = myIterator.next();

                /* Access the cash bucket */
                final MoneyWiseXAnalysisPortfolioCashBucket myCash = myPortfolio.getPortfolioCash();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCash.isForeignCurrency())) {
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCash.calculateDelta();
                myPortfolio.addValues(myCash);
                theTotals.addValues(myCash);
                myCashTotals.addValues(myCash);

                /* Loop through the buckets */
                final Iterator<MoneyWiseXAnalysisSecurityBucket> mySecIterator = myPortfolio.securityIterator();
                while (mySecIterator.hasNext()) {
                    /* Access bucket and category */
                    final MoneyWiseXAnalysisSecurityBucket myCurr = mySecIterator.next();

                    /* Analyse the security bucket */
                    //myCurr.analyseBucket(myRange);

                    /* Add to the portfolio bucket and add values */
                    myPortfolio.addValues(myCurr);
                    theTotals.addValues(myCurr);

                    /* Note active security */
                    haveActiveSecurities = Boolean.TRUE;

                    /* Handle foreign asset */
                    if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                        haveForeignCurrency = Boolean.TRUE;
                    }
                }

                /* Sort the list */
                myPortfolio.getSecurities().sortBuckets();

                /* Calculate delta for the portfolio */
                myPortfolio.calculateDelta();
            }

            /* Sort the list */
            theList.sortList();

            /* Calculate delta for the totals */
            theTotals.calculateDelta();
        }

        /**
         * Mark active securities.
         * @throws OceanusException on error
         */
        public void markActiveSecurities() throws OceanusException {
            /* Loop through the portfolio buckets */
            final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Mark active securities */
                final MoneyWiseXAnalysisSecurityBucketList mySecurities = myCurr.getSecurities();
                if (mySecurities.markActiveSecurities()) {
                    /* Check closed state */
                    final MoneyWisePortfolio myPortfolio = myCurr.getPortfolio();
                    if (Boolean.TRUE.equals(myPortfolio.isClosed())
                            && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new MoneyWiseDataException(myCurr, "Illegally closed portfolio");
                    }

                    /* Note that the portfolio is relevant as it has relevant securities */
                    myPortfolio.setRelevant();
                }
            }
        }
    }
}
