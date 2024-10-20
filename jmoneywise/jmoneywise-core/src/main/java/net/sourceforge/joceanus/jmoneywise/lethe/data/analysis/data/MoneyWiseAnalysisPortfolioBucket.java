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
package net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data;

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
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
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
public final class MoneyWiseAnalysisPortfolioBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisPortfolioBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisPortfolioBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO, MoneyWiseAnalysisPortfolioBucket::getPortfolio);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisPortfolioBucket::getPortfolioCash);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASH, MoneyWiseAnalysisPortfolioBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.SECURITY.getListId(), MoneyWiseAnalysisPortfolioBucket::getSecurities);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisSecurityAttr.class, MoneyWiseAnalysisPortfolioBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS;

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
    private final MoneyWiseAnalysisPortfolioCashBucket theCash;

    /**
     * The security bucket list.
     */
    private final MoneyWiseAnalysisSecurityBucketList theSecurities;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisSecurityValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisSecurityValues theBaseValues;

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
    private MoneyWiseAnalysisPortfolioBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWisePortfolio pPortfolio) {
        /* Store the portfolio */
        thePortfolio = pPortfolio;
        theCurrency = pAnalysis.getCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseAnalysisPortfolioCashBucket(pAnalysis, pPortfolio);

        /* Create the securities list */
        theSecurities = pPortfolio != null
                ? new MoneyWiseAnalysisSecurityBucketList(pAnalysis)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : theCurrency.getCurrency();
        theValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        theBaseValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
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
    private MoneyWiseAnalysisPortfolioBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucket pBase) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();
        hasForeignCurrency = pBase.hasForeignCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseAnalysisPortfolioCashBucket(pAnalysis, pBase.getPortfolioCash());

        /* Create the securities list */
        theSecurities = thePortfolio != null
                ? new MoneyWiseAnalysisSecurityBucketList(pAnalysis, pBase.getSecurities())
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        theBaseValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        initValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseAnalysisPortfolioBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucket pBase,
                                             final TethysDate pDate) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseAnalysisPortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pDate);

        /* Create the securities list */
        theSecurities = thePortfolio != null
                ? new MoneyWiseAnalysisSecurityBucketList(pAnalysis, pBase.getSecurities(), pDate)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        theBaseValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        initValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the date range for the bucket
     */
    private MoneyWiseAnalysisPortfolioBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucket pBase,
                                             final TethysDateRange pRange) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Create the cash bucket */
        theCash = new MoneyWiseAnalysisPortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pRange);

        /* Create the securities list */
        theSecurities = thePortfolio != null
                ? new MoneyWiseAnalysisSecurityBucketList(pAnalysis, pBase.getSecurities(), pRange)
                : null;

        /* Create the value maps and initialise them */
        final Currency myCurrency = theCurrency.getCurrency();
        theValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        theBaseValues = new MoneyWiseAnalysisSecurityValues(myCurrency);
        initValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisPortfolioBucket> getDataFieldSet() {
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
    public MoneyWiseAnalysisPortfolioCashBucket getPortfolioCash() {
        return theCash;
    }

    /**
     * Obtain the security buckets.
     * @return the buckets
     */
    public MoneyWiseAnalysisSecurityBucketList getSecurities() {
        return theSecurities;
    }

    /**
     * Obtain the security bucket iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseAnalysisSecurityBucket> securityIterator() {
        return theSecurities.iterator();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseAnalysisSecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public MoneyWiseAnalysisSecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseAnalysisSecurityAttr pAttr,
                  final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisSecurityAttr pAttr) {
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
    private Object getValue(final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * InitialiseValues.
     */
    private void initValues() {
        /* Determine currency */
        final Currency myCurrency = theCurrency == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : theCurrency.getCurrency();

        /* Create valuation fields for the portfolio */
        theValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, new TethysMoney(myCurrency));
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, new TethysMoney(myCurrency));

        /* Create profit fields for the portfolio */
        theValues.setValue(MoneyWiseAnalysisSecurityAttr.PROFIT, new TethysMoney(myCurrency));
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.PROFIT, new TethysMoney(myCurrency));

        /* Create market fields for the portfolio */
        theValues.setValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH, new TethysMoney(myCurrency));
        theValues.setValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT, new TethysMoney(myCurrency));
        theValues.setValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT, new TethysMoney(myCurrency));
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security holding.
     * @param pHolding the security holding
     * @return the bucket
     */
    public MoneyWiseAnalysisSecurityBucket getSecurityBucket(final MoneyWiseSecurityHolding pHolding) {
        /* Return the security bucket for the portfolio's list */
        return theSecurities.getBucket(pHolding);
    }

    /**
     * Obtain the SecurityBucket from this portfolio for a security.
     * @param pSecurity the security
     * @return the bucket
     */
    public MoneyWiseAnalysisSecurityBucket findSecurityBucket(final MoneyWiseSecurity pSecurity) {
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
        if (!(pThat instanceof MoneyWiseAnalysisPortfolioBucket)) {
            return false;
        }

        /* Compare the Portfolios */
        final MoneyWiseAnalysisPortfolioBucket myThat = (MoneyWiseAnalysisPortfolioBucket) pThat;
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
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        final TethysMoney myBase = theBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA, myValue);
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    void addValues(final MoneyWiseAnalysisSecurityBucket pBucket) {
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
    void addValues(final MoneyWiseAnalysisPortfolioCashBucket pBucket) {
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
    private static void addValues(final MoneyWiseAnalysisSecurityValues pTotals,
                                  final MoneyWiseAnalysisAccountValues pSource) {
        /* Add valuation values */
        final TethysMoney myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseAnalysisSecurityValues pTotals,
                                  final MoneyWiseAnalysisSecurityValues pSource) {
        /* Add valuation values */
        TethysMoney myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        TethysMoney mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        myValue.addAmount(mySrcValue);

        /* Add invested values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
        myValue.addAmount(mySrcValue);

        /* Add cost values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        myValue.addAmount(mySrcValue);

        /* Add gains values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        myValue.addAmount(mySrcValue);

        /* Add profit adjustment values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST);
        myValue.addAmount(mySrcValue);

        /* Add dividends values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND);
        myValue.addAmount(mySrcValue);

        /* Add market values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add currency values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add market profit values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }

        /* Add profit values */
        myValue = pTotals.getMoneyValue(MoneyWiseAnalysisSecurityAttr.PROFIT);
        mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.PROFIT);
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
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket mySecurity = myIterator.next();

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
        if (Boolean.FALSE.equals(theCash.isIdle())) {
            return false;
        }

        /* Loop through securities */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket mySecurity = myIterator.next();

            /* Look for active security */
            if (Boolean.FALSE.equals(mySecurity.isIdle())) {
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
        final MoneyWiseAnalysisAccountValues myCashValues = pBase
                ? theCash.getBaseValues()
                : theCash.getValues();
        return new TethysMoney(myCashValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
    }

    /**
     * Obtain non-cash valuation.
     * @param pBase get base valuation - true/false
     * @return the valuation minus the cash value
     */
    public TethysMoney getNonCashValue(final boolean pBase) {
        /* Handle valuation by subtracting the cash valuation */
        final MoneyWiseAnalysisSecurityValues myValues = pBase
                ? theBaseValues
                : theValues;
        final TethysMoney myValue = new TethysMoney(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
        myValue.subtractAmount(getCashValue(pBase));
        return myValue;
    }

    /**
     * PortfolioBucket list class.
     */
    public static final class MoneyWiseAnalysisPortfolioBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisPortfolioBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisPortfolioBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisPortfolioBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisPortfolioBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisPortfolioBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisPortfolioBucket> theList;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisPortfolioBucket theTotals;

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
        MoneyWiseAnalysisPortfolioBucketList(final MoneyWiseAnalysis pAnalysis) {
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
        MoneyWiseAnalysisPortfolioBucketList(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucketList pBase) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Access the bucket */
                final MoneyWiseAnalysisPortfolioBucket myBucket = new MoneyWiseAnalysisPortfolioBucket(pAnalysis, myCurr);

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
        MoneyWiseAnalysisPortfolioBucketList(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucketList pBase,
                                             final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisPortfolioBucket myBucket = new MoneyWiseAnalysisPortfolioBucket(pAnalysis, myCurr, pDate);

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
        MoneyWiseAnalysisPortfolioBucketList(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisPortfolioBucketList pBase,
                                             final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisPortfolioBucket myBucket = new MoneyWiseAnalysisPortfolioBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisPortfolioBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisPortfolioBucket> getUnderlyingList() {
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
        MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseAnalysisPortfolioBucket getTotals() {
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
        public MoneyWiseAnalysisPortfolioBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the PortfolioBucket for a given portfolio.
         * @param pPortfolio the portfolio
         * @return the bucket
         */
        public MoneyWiseAnalysisPortfolioBucket getBucket(final MoneyWisePortfolio pPortfolio) {
            /* Locate the bucket in the list */
            MoneyWiseAnalysisPortfolioBucket myItem = findItemById(pPortfolio.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisPortfolioBucket(theAnalysis, pPortfolio);

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
        public MoneyWiseAnalysisPortfolioCashBucket getCashBucket(final MoneyWisePortfolio pPortfolio) {
            /* Locate the bucket in the list */
            final MoneyWiseAnalysisPortfolioBucket myItem = getBucket(pPortfolio);

            /* Return the bucket */
            return myItem.getPortfolioCash();
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the holding
         * @return the bucket
         */
        public MoneyWiseAnalysisSecurityBucket getBucket(final MoneyWiseSecurityHolding pHolding) {
            /* Locate the portfolio bucket in the list */
            final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
            final MoneyWiseAnalysisPortfolioBucket myBucket = getBucket(myPortfolio);

            /* Return the security bucket for the portfolio's list */
            return myBucket.getSecurityBucket(pHolding);
        }

        /**
         * Obtain the matching PortfolioBucket.
         * @param pPortfolio the portfolio
         * @return the matching bucket
         */
        public MoneyWiseAnalysisPortfolioBucket getMatchingPortfolio(final MoneyWisePortfolio pPortfolio) {
            /* Return the matching portfolio if it exists else an orphan bucket */
            final MoneyWiseAnalysisPortfolioBucket myPortfolio = findItemById(pPortfolio.getIndexedId());
            return myPortfolio != null
                    ? myPortfolio
                    : new MoneyWiseAnalysisPortfolioBucket(theAnalysis, pPortfolio);
        }

        /**
         * Obtain the matching SecurityBucket.
         * @param pSecurity the security
         * @return the matching bucket
         */
        public MoneyWiseAnalysisSecurityBucket getMatchingSecurityHolding(final MoneyWiseSecurityHolding pSecurity) {
            /* Find the portfolio and holding */
            final MoneyWiseAnalysisPortfolioBucket myPortfolio = findItemById(pSecurity.getPortfolio().getIndexedId());
            final MoneyWiseAnalysisSecurityBucket mySecurity = myPortfolio == null
                    ? null
                    : myPortfolio.findSecurityBucket(pSecurity.getSecurity());
            return mySecurity != null
                    ? mySecurity
                    : new MoneyWiseAnalysisSecurityBucket(theAnalysis, pSecurity);
        }

        /**
         * Obtain the default PortfolioBucket.
         * @return the default bucket
         */
        public MoneyWiseAnalysisPortfolioBucket getDefaultPortfolio() {
            /* Return the first portfolio in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Obtain the default SecurityBucket.
         * @return the default bucket
         */
        public MoneyWiseAnalysisSecurityBucket getDefaultSecurityHolding() {
            /* Loop through the portfolio buckets */
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myPortfolio = myIterator.next();

                /* Loop through the security buckets */
                final Iterator<MoneyWiseAnalysisSecurityBucket> mySecIterator = myPortfolio.securityIterator();
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
        private MoneyWiseAnalysisPortfolioBucket allocateTotalsBucket() {
            /* Obtain the totals portfolio */
            return new MoneyWiseAnalysisPortfolioBucket(theAnalysis, (MoneyWisePortfolio) null);
        }

        /**
         * Analyse securities.
         * @param pMarket the market analysis
         */
        void analyseSecurities(final MoneyWiseAnalysisMarket pMarket) {
            /* Access details */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final MoneyWiseAnalysisPortfolioCashBucket myCashTotals = theTotals.getPortfolioCash();

            /* Loop through the portfolio buckets */
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myPortfolio = myIterator.next();

                /* Access the cash bucket */
                final MoneyWiseAnalysisPortfolioCashBucket myCash = myPortfolio.getPortfolioCash();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCash.isForeignCurrency())) {
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
                final Iterator<MoneyWiseAnalysisSecurityBucket> mySecIterator = myPortfolio.securityIterator();
                while (mySecIterator.hasNext()) {
                    /* Access bucket and category */
                    final MoneyWiseAnalysisSecurityBucket myCurr = mySecIterator.next();

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
            final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPortfolioBucket myCurr = myIterator.next();

                /* Mark active securities */
                final MoneyWiseAnalysisSecurityBucketList mySecurities = myCurr.getSecurities();
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
