/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Portfolio Bucket.
 */
public final class PortfolioBucket
        implements JDataContents, Comparable<PortfolioBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.PORTFOLIO_NAME.getValue());

    /**
     * Portfolio Field Id.
     */
    private static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getItemName());

    /**
     * Currency Field Id.
     */
    private static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * CashBucket Field Id.
     */
    private static final JDataField FIELD_CASH = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASH.getItemName());

    /**
     * Securities Field Id.
     */
    private static final JDataField FIELD_SECURITIES = FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY.getListName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, AccountAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * The portfolio.
     */
    private final Portfolio thePortfolio;

    /**
     * The currency.
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
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPortfolio the portfolio account
     */
    private PortfolioBucket(final Analysis pAnalysis,
                            final Portfolio pPortfolio) {
        /* Store the portfolio */
        thePortfolio = pPortfolio;
        theCurrency = (pPortfolio == null)
                                          ? pAnalysis.getCurrency()
                                          : pPortfolio.getAssetCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pPortfolio);

        /* Create the securities list */
        theSecurities = (pPortfolio != null)
                                            ? new SecurityBucketList(pAnalysis)
                                            : null;

        /* Create the value maps and initialise them */
        Currency myCurrency = theCurrency == null
                                                 ? AccountBucket.DEFAULT_CURRENCY
                                                 : theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();
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

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash());

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                              ? new SecurityBucketList(pAnalysis, pBase.getSecurities())
                                              : null;

        /* Create the value maps and initialise them */
        Currency myCurrency = theCurrency.getCurrency();
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
                            final JDateDay pDate) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pDate);

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                              ? new SecurityBucketList(pAnalysis, pBase.getSecurities(), pDate)
                                              : null;

        /* Create the value maps and initialise them */
        Currency myCurrency = theCurrency.getCurrency();
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
                            final JDateDayRange pRange) {
        /* Copy details from base */
        thePortfolio = pBase.getPortfolio();
        theCurrency = pBase.getCurrency();

        /* Create the cash bucket */
        theCash = new PortfolioCashBucket(pAnalysis, pBase.getPortfolioCash(), pRange);

        /* Create the securities list */
        theSecurities = (thePortfolio != null)
                                              ? new SecurityBucketList(pAnalysis, pBase.getSecurities(), pRange)
                                              : null;

        /* Create the value maps and initialise them */
        Currency myCurrency = theCurrency.getCurrency();
        theValues = new SecurityValues(myCurrency);
        theBaseValues = new SecurityValues(myCurrency);
        initValues();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_PORTFOLIO.equals(pField)) {
            return thePortfolio;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return theCurrency;
        }
        if (FIELD_CASH.equals(pField)) {
            return theCash;
        }
        if (FIELD_SECURITIES.equals(pField)) {
            return theSecurities;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        AccountAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                                                       ? myValue
                                                       : JDataFieldValue.SKIP;
            }
            return myValue;
        }

        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return (thePortfolio == null)
                                     ? NAME_TOTALS
                                     : thePortfolio.getName();
    }

    @Override
    public Integer getOrderedId() {
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
                            final JMoney pValue) {
        /* Set the value into the list */
        theValues.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final AccountAttribute pAttr) {
        /* Access value of object */
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                ? myValue
                                : JDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static AccountAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return theValues.get(pAttr);
    }

    /**
     * InitialiseValues.
     */
    private void initValues() {
        /* Create valuation fields for the portfolio */
        theValues.setValue(SecurityAttribute.VALUATION, new JMoney());
        theBaseValues.setValue(SecurityAttribute.VALUATION, new JMoney());

        /* Create profit fields for the portfolio */
        theValues.setValue(SecurityAttribute.PROFIT, new JMoney());
        theBaseValues.setValue(SecurityAttribute.PROFIT, new JMoney());

        /* Create market fields for the portfolio */
        theValues.setValue(SecurityAttribute.MARKET, new JMoney());
        theValues.setValue(SecurityAttribute.MARKETPROFIT, new JMoney());
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
        return theSecurities.findItemById(pSecurity.getOrderedId());
    }

    @Override
    public int compareTo(final PortfolioBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Portfolios */
        return getPortfolio().compareTo(pThat.getPortfolio());
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
        PortfolioBucket myThat = (PortfolioBucket) pThat;
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
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        JMoney myBase = theBaseValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(SecurityAttribute.DELTA, myValue);
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
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    private void addValues(final PortfolioCashBucket pBucket) {
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
        JMoney myValue = pTotals.getMoneyValue(SecurityAttribute.VALUATION);
        JMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
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
        JMoney myValue = pTotals.getMoneyValue(SecurityAttribute.VALUATION);
        JMoney mySrcValue = pSource.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.addAmount(mySrcValue);

        /* Add invested values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.INVESTED);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.INVESTED);
        myValue.addAmount(mySrcValue);

        /* Add cost values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.COST);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.COST);
        myValue.addAmount(mySrcValue);

        /* Add gains values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.GAINS);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.GAINS);
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
        myValue = pTotals.getMoneyValue(SecurityAttribute.MARKET);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.MARKET);
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
        Iterator<SecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            SecurityBucket mySecurity = myIterator.next();

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
        Iterator<SecurityBucket> myIterator = securityIterator();
        while (myIterator.hasNext()) {
            SecurityBucket mySecurity = myIterator.next();

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
    public JMoney getCashValue(final boolean pBase) {
        /* Obtain the cash valuation */
        AccountValues myCashValues = pBase
                                          ? theCash.getBaseValues()
                                          : theCash.getValues();
        return new JMoney(myCashValues.getMoneyValue(AccountAttribute.VALUATION));
    }

    /**
     * Obtain non-cash valuation.
     * @param pBase get base valuation - true/false
     * @return the valuation minus the cash value
     */
    public JMoney getNonCashValue(final boolean pBase) {
        /* Handle valuation by subtracting the cash valuation */
        SecurityValues myValues = pBase
                                       ? theBaseValues
                                       : theValues;
        JMoney myValue = new JMoney(myValues.getMoneyValue(SecurityAttribute.VALUATION));
        myValue.subtractAmount(getCashValue(pBase));
        return myValue;
    }

    /**
     * PortfolioBucket list class.
     */
    public static final class PortfolioBucketList
            extends OrderedIdList<Integer, PortfolioBucket>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.PORTFOLIO_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The totals.
         */
        private final PortfolioBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected PortfolioBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(PortfolioBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected PortfolioBucketList(final Analysis pAnalysis,
                                      final PortfolioBucketList pBase) {
            /* Initialise class */
            super(PortfolioBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket */
                PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr);

                /* Ignore if portfolio is idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
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
                                      final JDateDay pDate) {
            /* Initialise class */
            super(PortfolioBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr, pDate);

                /* Ignore if portfolio is idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
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
                                      final JDateDayRange pRange) {
            /* Initialise class */
            super(PortfolioBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<PortfolioBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                PortfolioBucket myBucket = new PortfolioBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public PortfolioBucket getTotals() {
            return theTotals;
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
                add(myItem);
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
            PortfolioBucket myItem = getBucket(pPortfolio);

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
            Portfolio myPortfolio = pHolding.getPortfolio();
            PortfolioBucket myBucket = getBucket(myPortfolio);

            /* Return the security bucket for the portfolio's list */
            return myBucket.getSecurityBucket(pHolding);
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
         */
        protected void analyseSecurities() {
            /* Market Analysis */
            MarketAnalysis myMarket = new MarketAnalysis();
            JDateDayRange myRange = theAnalysis.getDateRange();
            PortfolioCashBucket myCashTotals = theTotals.getPortfolioCash();

            /* Loop through the portfolio buckets */
            Iterator<PortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myPortfolio = myIterator.next();

                /* Add the cash bucket */
                PortfolioCashBucket myCash = myPortfolio.getPortfolioCash();
                myCash.calculateDelta();
                myPortfolio.addValues(myCash);
                theTotals.addValues(myCash);
                myCashTotals.addValues(myCash);

                /* Loop through the buckets */
                Iterator<SecurityBucket> mySecIterator = myPortfolio.securityIterator();
                while (mySecIterator.hasNext()) {
                    /* Access bucket and category */
                    SecurityBucket myCurr = mySecIterator.next();

                    /* Analyse the security bucket */
                    myCurr.analyseBucket(myRange);

                    /* Process market movements */
                    myMarket.processSecurity(myCurr);

                    /* Add to the portfolio bucket and add values */
                    myPortfolio.addValues(myCurr);
                    theTotals.addValues(myCurr);
                }

                /* Calculate delta for the portfolio */
                myPortfolio.calculateDelta();
            }

            /* Calculate delta for the totals */
            theTotals.calculateDelta();

            /* Propagate totals */
            myMarket.propagateTotals(theAnalysis);
        }

        /**
         * Mark active securities.
         * @throws JOceanusException on error
         */
        protected void markActiveSecurities() throws JOceanusException {
            /* Loop through the portfolio buckets */
            Iterator<PortfolioBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myCurr = myIterator.next();

                /* Mark active securities */
                SecurityBucketList mySecurities = myCurr.getSecurities();
                if (mySecurities.markActiveSecurities()) {
                    /* Check closed state */
                    Portfolio myPortfolio = myCurr.getPortfolio();
                    if (myPortfolio.isClosed() && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new JMoneyWiseDataException(myCurr, "Illegally closed portfolio");
                    }

                    /* Note that the portfolio is relevant as it has relevant securities */
                    myPortfolio.setRelevant();
                }
            }
        }
    }
}