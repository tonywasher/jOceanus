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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Portfolio Bucket.
 */
public final class PortfolioBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<PortfolioBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO, PortfolioBucket::getPortfolio);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES, PortfolioBucket::getPortfolioCash);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASH, PortfolioBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY.getListId(), PortfolioBucket::getSecurities);
        FIELD_DEFS.declareLocalFieldsForEnum(SecurityAttribute.class, PortfolioBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS;

    /**
     * The portfolio.
     */
    private final Portfolio thePortfolio;

    /**
     * The reporting currency.
     */
    private final AssetCurrency theCurrency;

    /**
     * The cash bucket.
     */
    private final PortfolioCashBucket theCash;

    /**
     * The security bucket list.
     */
    private final SecurityBucketList theSecurities;

    /**
     * Values.
     */
    private final SecurityValues theValues;

    /**
     * The base values.
     */
    private final SecurityValues theBaseValues;

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
    private PortfolioBucket(final Analysis pAnalysis,
                            final Portfolio pPortfolio) {
        /* Store the portfolio */
        thePortfolio = pPortfolio;
        theCurrency = pAnalysis.getCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pPortfolio);

        /* Create the securities list */
        theSecurities = (pPortfolio != null)
                                             ? new SecurityBucketList(pAnalysis)
                                             : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency == null
                                                        ? AccountBucket.DEFAULT_CURRENCY
                                                        : theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();

        /* Determine whether the portfolio is a foreign currency */
        isForeignCurrency = !MetisDataDifference.isEqual(pAnalysis.getCurrency(), theCurrency);
        hasForeignCurrency = isForeignCurrency;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private PortfolioBucket(final Analysis pAnalysis,
                            final PortfolioBucket pBase) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();
        hasForeignCurrency = pBase.hasForeignCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash());

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                               ? new SecurityBucketList(pAnalysis, pBase.getSecurities())
                                               : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private PortfolioBucket(final Analysis pAnalysis,
                            final PortfolioBucket pBase,
                            final TethysDate pDate) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pDate);

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                               ? new SecurityBucketList(pAnalysis, pBase.getSecurities(), pDate)
                                               : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the date range for the bucket
     */
    private PortfolioBucket(final Analysis pAnalysis,
                            final PortfolioBucket pBase,
                            final TethysDateRange pRange) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pRange);

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                               ? new SecurityBucketList(pAnalysis, pBase.getSecurities(), pRange)
                                               : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();
    }

    @Override
    public MetisFieldSet<PortfolioBucket> getDataFieldSet() {
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
        return thePortfolio.getId();
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public AssetCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the portfolio cash bucket.
     * @return the bucket
     */
    public PortfolioCashBucket getPortfolioCash() {
        return theCash;
    }

    /**
     * Obtain the security buckets.
     * @return the buckets
     */
    public SecurityBucketList getSecurities() {
        return theSecurities;
    }

    /**
     * Obtain the security bucket iterator.
     * @return the iterator
     */
    public Iterator<SecurityBucket> securityIterator() {
        return theSecurities.iterator();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public SecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public SecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final SecurityAttribute pAttr,
                            final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final SecurityAttribute pAttr) {
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
    private Object getValue(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * InitialiseValues.
     */
    private void initValues() {
        /* Determine currency */
        final Currency myCurrency = theCurrency == null
                                                        ? AccountBucket.DEFAULT_CURRENCY
                                                        : theCurrency.getCurrency();

        /* Create valuation fields for the portfolio */
        theValues.setValue(SecurityAttribute.VALUATION, new TethysMoney(myCurrency));
        theBaseValues.setValue(SecurityAttribute.VALUATION, new TethysMoney(myCurrency));

        /* Create profit fields for the portfolio */
        theValues.setValue(SecurityAttribute.PROFIT, new TethysMoney(myCurrency));
        theBaseValues.setValue(SecurityAttribute.PROFIT, new TethysMoney(myCurrency));

        /* Create market fields for the portfolio */
        theValues.setValue(SecurityAttribute.MARKETGROWTH, new TethysMoney(myCurrency));
        theValues.setValue(SecurityAttribute.CURRENCYFLUCT, new TethysMoney(myCurrency));
        theValues.setValue(SecurityAttribute.MARKETPROFIT, new TethysMoney(myCurrency));
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security holding.
     * @param pHolding the security holding
     * @return the bucket
     */
    protected SecurityBucket getSecurityBucket(final SecurityHolding pHolding) {
        /* Return the security bucket for the portfolio's list */
        return theSecurities.getBucket(pHolding);
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security.
     * @param pSecurity the security
     * @return the bucket
     */
    public SecurityBucket findSecurityBucket(final Security pSecurity) {
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
        if (!(pThat instanceof PortfolioBucket)) {
            return false;
        }

        /* Compare the Portfolios */
        final PortfolioBucket myThat = (PortfolioBucket) pThat;
        return getPortfolio().equals(myThat.getPortfolio());
    }

    @Override
    public int hashCode() {
        return getPortfolio().hashCode();
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myValue = theValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        final TethysMoney myBase = theBaseValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(SecurityAttribute.VALUEDELTA, myValue);
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final SecurityBucket pBucket) {
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
    protected void addValues(final PortfolioCashBucket pBucket) {
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
    private static void addValues(final SecurityValues pTotals,
                                  final AccountValues pSource) {
        /* Add valuation values */
        final TethysMoney myValue = pTotals.getMoneyValue(SecurityAttribute.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final SecurityValues pTotals,
                                  final SecurityValues pSource) {
        /* Add valuation values */
        TethysMoney myValue = pTotals.getMoneyValue(SecurityAttribute.VALUATION);
        TethysMoney mySrcValue = pSource.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.addAmount(mySrcValue);

        /* Add invested values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.INVESTED);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.INVESTED);
        myValue.addAmount(mySrcValue);

        /* Add cost values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        myValue.addAmount(mySrcValue);

        /* Add gains values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.REALISEDGAINS);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.REALISEDGAINS);
        myValue.addAmount(mySrcValue);

        /* Add profit adjustment values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.GROWTHADJUST);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.GROWTHADJUST);
        myValue.addAmount(mySrcValue);

        /* Add dividends values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.DIVIDEND);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.DIVIDEND);
        myValue.addAmount(mySrcValue);

        /* Add market values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.MARKETGROWTH);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.MARKETGROWTH);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add currency values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.CURRENCYFLUCT);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.CURRENCYFLUCT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add market profit values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.MARKETPROFIT);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.MARKETPROFIT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add profit values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.PROFIT);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.PROFIT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }
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
        final Iterator<SecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final SecurityBucket mySecurity = myIterator.next();

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
        final Iterator<SecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final SecurityBucket mySecurity = myIterator.next();

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
        final AccountValues myCashValues = pBase
                                                 ? theCash.getBaseValues()
                                                 : theCash.getValues();
        return new TethysMoney(myCashValues.getMoneyValue(AccountAttribute.VALUATION));
    }

    /**
     * Obtain non-cash valuation.
     * @param pBase get base valuation - true/false
     * @return the valuation minus the cash value
     */
    public TethysMoney getNonCashValue(final boolean pBase) {
        /* Handle valuation by subtracting the cash valuation */
        final SecurityValues myValues = pBase
                                              ? theBaseValues
                                              : theValues;
        final TethysMoney myValue = new TethysMoney(myValues.getMoneyValue(SecurityAttribute.VALUATION));
        myValue.subtractAmount(getCashValue(pBase));
        return myValue;
    }

    /**
     * PortfolioBucket list class.
     */
    public static final class PortfolioBucketList
            implements MetisFieldItem, MetisDataList<PortfolioBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<PortfolioBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, PortfolioBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS, PortfolioBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<PortfolioBucket> theList;

        /**
         * The totals.
         */
        private final PortfolioBucket theTotals;

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
        protected PortfolioBucketList(final Analysis pAnalysis) {
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
         */
        protected PortfolioBucketList(final Analysis pAnalysis,
                                      final PortfolioBucketList pBase) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket */
                final PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr);

                /* Ignore if portfolio is idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected PortfolioBucketList(final Analysis pAnalysis,
                                      final PortfolioBucketList pBase,
                                      final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr, pDate);

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
        protected PortfolioBucketList(final Analysis pAnalysis,
                                      final PortfolioBucketList pBase,
                                      final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<PortfolioBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<PortfolioBucket> getUnderlyingList() {
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
         * Obtain the Totals.
         * @return the totals
         */
        public PortfolioBucket getTotals() {
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
        public PortfolioBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the PortfolioBucket for a given portfolio.
         * @param pPortfolio the portfolio
         * @return the bucket
         */
        public PortfolioBucket getBucket(final Portfolio pPortfolio) {
            /* Locate the bucket in the list */
            PortfolioBucket myItem = findItemById(pPortfolio.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PortfolioBucket(theAnalysis, pPortfolio);

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
        public PortfolioCashBucket getCashBucket(final Portfolio pPortfolio) {
            /* Locate the bucket in the list */
            final PortfolioBucket myItem = getBucket(pPortfolio);

            /* Return the bucket */
            return myItem.getPortfolioCash();
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the holding
         * @return the bucket
         */
        public SecurityBucket getBucket(final SecurityHolding pHolding) {
            /* Locate the portfolio bucket in the list */
            final Portfolio myPortfolio = pHolding.getPortfolio();
            final PortfolioBucket myBucket = getBucket(myPortfolio);

            /* Return the security bucket for the portfolio's list */
            return myBucket.getSecurityBucket(pHolding);
        }

        /**
         * Obtain the matching PortfolioBucket.
         * @param pPortfolio the portfolio
         * @return the matching bucket
         */
        public PortfolioBucket getMatchingPortfolio(final Portfolio pPortfolio) {
            /* Return the matching portfolio if it exists else an orphan bucket */
            final PortfolioBucket myPortfolio = findItemById(pPortfolio.getIndexedId());
            return myPortfolio != null
                                       ? myPortfolio
                                       : new PortfolioBucket(theAnalysis, pPortfolio);
        }

        /**
         * Obtain the matching SecurityBucket.
         * @param pSecurity the security
         * @return the matching bucket
         */
        public SecurityBucket getMatchingSecurityHolding(final SecurityHolding pSecurity) {
            /* Find the portfolio and holding */
            final PortfolioBucket myPortfolio = findItemById(pSecurity.getPortfolio().getIndexedId());
            final SecurityBucket mySecurity = myPortfolio == null
                                                                  ? null
                                                                  : myPortfolio.findSecurityBucket(pSecurity.getSecurity());
            return mySecurity != null
                                      ? mySecurity
                                      : new SecurityBucket(theAnalysis, pSecurity);
        }

        /**
         * Obtain the default PortfolioBucket.
         * @return the default bucket
         */
        public PortfolioBucket getDefaultPortfolio() {
            /* Return the first portfolio in the list if it exists */
            return isEmpty()
                             ? null
                             : theList.getUnderlyingList().get(0);
        }

        /**
         * Obtain the default SecurityBucket.
         * @return the default bucket
         */
        public SecurityBucket getDefaultSecurityHolding() {
            /* Loop through the portfolio buckets */
            final Iterator<PortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myPortfolio = myIterator.next();

                /* Loop through the security buckets */
                final Iterator<SecurityBucket> mySecIterator = myPortfolio.securityIterator();
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
        private PortfolioBucket allocateTotalsBucket() {
            /* Obtain the totals portfolio */
            return new PortfolioBucket(theAnalysis, (Portfolio) null);
        }

        /**
         * Analyse securities.
         * @param pMarket the market analysis
         */
        protected void analyseSecurities(final MarketAnalysis pMarket) {
            /* Access details */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final PortfolioCashBucket myCashTotals = theTotals.getPortfolioCash();

            /* Loop through the portfolio buckets */
            final Iterator<PortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myPortfolio = myIterator.next();

                /* Access the cash bucket */
                final PortfolioCashBucket myCash = myPortfolio.getPortfolioCash();

                /* Handle foreign asset */
                if (myCash.isForeignCurrency()) {
                    myCash.calculateFluctuations(myRange);
                    pMarket.processAccount(myCash);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCash.calculateDelta();
                myPortfolio.addValues(myCash);
                theTotals.addValues(myCash);
                myCashTotals.addValues(myCash);

                /* Loop through the buckets */
                final Iterator<SecurityBucket> mySecIterator = myPortfolio.securityIterator();
                while (mySecIterator.hasNext()) {
                    /* Access bucket and category */
                    final SecurityBucket myCurr = mySecIterator.next();

                    /* Analyse the security bucket */
                    myCurr.analyseBucket(myRange);

                    /* Process market movements */
                    pMarket.processSecurity(myCurr);

                    /* Add to the portfolio bucket and add values */
                    myPortfolio.addValues(myCurr);
                    theTotals.addValues(myCurr);

                    /* Note active security */
                    haveActiveSecurities = Boolean.TRUE;

                    /* Handle foreign asset */
                    if (myCurr.isForeignCurrency()) {
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
        protected void markActiveSecurities() throws OceanusException {
            /* Loop through the portfolio buckets */
            final Iterator<PortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PortfolioBucket myCurr = myIterator.next();

                /* Mark active securities */
                final SecurityBucketList mySecurities = myCurr.getSecurities();
                if (mySecurities.markActiveSecurities()) {
                    /* Check closed state */
                    final Portfolio myPortfolio = myCurr.getPortfolio();
                    if (myPortfolio.isClosed()
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
