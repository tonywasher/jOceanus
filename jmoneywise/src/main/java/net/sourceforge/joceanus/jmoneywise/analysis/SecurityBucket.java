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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * The Security Bucket class.
 */
public final class SecurityBucket
        implements JDataContents, Comparable<SecurityBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.SECURITY_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * SecurityHolding Field Id.
     */
    private static final JDataField FIELD_HOLDING = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.ASSETTYPE_SECURITYHOLDING.getValue());

    /**
     * Security Type Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITYTYPE.getItemName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, SecurityAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, SecurityAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The security holding.
     */
    private final SecurityHolding theHolding;

    /**
     * The security.
     */
    private final Security theSecurity;

    /**
     * The portfolio.
     */
    private final Portfolio thePortfolio;

    /**
     * The security type.
     */
    private final SecurityType theCategory;

    /**
     * The dataSet.
     */
    private final MoneyWiseData theData;

    /**
     * Values.
     */
    private final SecurityValues theValues;

    /**
     * The base values.
     */
    private final SecurityValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<SecurityValues, SecurityAttribute> theHistory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_HOLDING.equals(pField)) {
            return theHolding;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        SecurityAttribute myClass = getClassForField(pField);
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
        return theSecurity.getName();
    }

    /**
     * Obtain the decorated name.
     * @return the decorated name
     */
    public String getDecoratedName() {
        return theHolding.getName();
    }

    /**
     * Obtain the holding.
     * @return the holding
     */
    public SecurityHolding getSecurityHolding() {
        return theHolding;
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public Security getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    @Override
    public Integer getOrderedId() {
        return theSecurity.getId();
    }

    /**
     * Obtain the security type.
     * @return the security type
     */
    public SecurityType getSecurityType() {
        return theCategory;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    protected MoneyWiseData getDataSet() {
        return theData;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public JDateDayRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public SecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public SecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public SecurityValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JDecimal getDeltaForTransaction(final Transaction pTrans,
                                           final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JMoney getMoneyDeltaForTransaction(final Transaction pTrans,
                                              final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pTrans, pAttr);
    }

    /**
     * Obtain units delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JUnits getUnitsDeltaForTransaction(final Transaction pTrans,
                                              final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaUnitsValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<SecurityValues, SecurityAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final SecurityAttribute pAttr,
                            final Object pValue) {
        /* Set the value into the list */
        theValues.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final SecurityAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

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
    private static SecurityAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final SecurityAttribute pAttr) {
        /* Obtain the value */
        return theValues.get(pAttr);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pHolding the security holding
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityHolding pHolding) {
        /* Store the details */
        theHolding = pHolding;
        theSecurity = pHolding.getSecurity();
        thePortfolio = pHolding.getPortfolio();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Obtain category */
        theCategory = theSecurity.getSecurityType();

        /* Create the history map */
        theHistory = new BucketHistory<SecurityValues, SecurityAttribute>(new SecurityValues());

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
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityBucket pBase,
                           final JDateDay pDate) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<SecurityValues, SecurityAttribute>(pBase.getHistoryMap(), pDate);

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
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityBucket pBase,
                           final JDateDayRange pRange) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<SecurityValues, SecurityAttribute>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final SecurityBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the portfolio */
        int iDiff = Difference.compareObject(getPortfolio(), pThat.getPortfolio());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the Securities */
        return getSecurity().compareTo(pThat.getSecurity());
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
        if (!(pThat instanceof SecurityBucket)) {
            return false;
        }

        /* Compare the Portfolios */
        SecurityBucket myThat = (SecurityBucket) pThat;
        if (!getPortfolio().equals(myThat.getPortfolio())) {
            return false;
        }

        /* Compare the Securities */
        if (!getSecurity().equals(myThat.getSecurity())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getSecurity().hashCode();
    }

    /**
     * Adjust security units.
     * @param pDelta the delta
     */
    protected void adjustUnits(final JUnits pDelta) {
        JUnits myUnits = theValues.getUnitsValue(SecurityAttribute.UNITS);
        myUnits = new JUnits(myUnits);
        myUnits.addUnits(pDelta);
        setValue(SecurityAttribute.UNITS, myUnits);
    }

    /**
     * Adjust security cost.
     * @param pDelta the delta
     */
    protected void adjustCost(final JMoney pDelta) {
        JMoney myCost = theValues.getMoneyValue(SecurityAttribute.COST);
        myCost = new JMoney(myCost);
        myCost.addAmount(pDelta);
        setValue(SecurityAttribute.COST, myCost);
    }

    /**
     * Adjust security invested.
     * @param pDelta the delta
     */
    protected void adjustInvested(final JMoney pDelta) {
        JMoney myInvested = theValues.getMoneyValue(SecurityAttribute.INVESTED);
        myInvested = new JMoney(myInvested);
        myInvested.addAmount(pDelta);
        setValue(SecurityAttribute.INVESTED, myInvested);
    }

    /**
     * Adjust security gains.
     * @param pDelta the delta
     */
    protected void adjustGains(final JMoney pDelta) {
        JMoney myGains = theValues.getMoneyValue(SecurityAttribute.GAINS);
        myGains = new JMoney(myGains);
        myGains.addAmount(pDelta);
        setValue(SecurityAttribute.GAINS, myGains);
    }

    /**
     * Adjust security dividends.
     * @param pDelta the delta
     */
    protected void adjustDividend(final JMoney pDelta) {
        JMoney myDividend = theValues.getMoneyValue(SecurityAttribute.DIVIDEND);
        myDividend = new JMoney(myDividend);
        myDividend.addAmount(pDelta);
        setValue(SecurityAttribute.DIVIDEND, myDividend);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction
     * @return the registered values
     */
    protected SecurityValues registerTransaction(final Transaction pTrans) {
        /* Register the event in the history */
        return theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * value the asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueAsset(final JDateDayRange pRange) {
        /* Obtain the appropriate price */
        MoneyWiseData myData = theAnalysis.getData();
        SecurityPriceDataMap<SecurityPrice> myPriceMap = myData.getSecurityPriceDataMap();
        JPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);

        /* Access base units */
        JUnits myUnits = theBaseValues.getUnitsValue(SecurityAttribute.UNITS);
        JPrice myPrice = myPrices[0];

        /* Calculate the value */
        theBaseValues.setValue(SecurityAttribute.PRICE, myPrice);
        theBaseValues.setValue(SecurityAttribute.VALUATION, myUnits.valueAtPrice(myPrice));

        /* Access units */
        myUnits = theValues.getUnitsValue(SecurityAttribute.UNITS);
        myPrice = myPrices[1];

        /* Calculate the value */
        setValue(SecurityAttribute.PRICE, myPrice);
        setValue(SecurityAttribute.VALUATION, myUnits.valueAtPrice(myPrice));
    }

    /**
     * calculate the profit for a priced asset.
     */
    protected void calculateProfit() {
        /* Calculate the profit */
        JMoney myValuation = theValues.getMoneyValue(SecurityAttribute.DELTA);
        JMoney myProfit = new JMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(SecurityAttribute.INVESTED));
        myProfit.addAmount(theValues.getMoneyValue(SecurityAttribute.DIVIDEND));

        /* Set the attribute */
        setValue(SecurityAttribute.PROFIT, myProfit);
    }

    /**
     * Analyse the bucket.
     * @param pRange the range of valuation
     */
    protected void analyseBucket(final JDateDayRange pRange) {
        /* Value the asset over the range */
        valueAsset(pRange);

        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(SecurityAttribute.VALUATION));

        /* Set the delta */
        setValue(SecurityAttribute.DELTA, myValue);

        /* Calculate the profit */
        calculateProfit();

        /* Calculate the market movement */
        calculateMarket();
    }

    /**
     * Adjust to base.
     */
    private void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Calculate market movement.
     */
    private void calculateMarket() {
        /* Obtain the delta value */
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.DELTA);
        myValue = new JMoney(myValue);

        /* Subtract the investment */
        myValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.INVESTED));

        /* Set the delta */
        setValue(SecurityAttribute.MARKET, myValue);
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * SecurityValues class.
     */
    public static class SecurityValues
            extends BucketValues<SecurityValues, SecurityAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 661272708599335410L;

        /**
         * Constructor.
         */
        protected SecurityValues() {
            /* Initialise class */
            super(SecurityAttribute.class);

            /* Initialise units etc. to zero */
            put(SecurityAttribute.UNITS, new JUnits());
            put(SecurityAttribute.COST, new JMoney());
            put(SecurityAttribute.INVESTED, new JMoney());
            put(SecurityAttribute.GAINS, new JMoney());
            put(SecurityAttribute.DIVIDEND, new JMoney());
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private SecurityValues(final SecurityValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected SecurityValues getSnapShot() {
            return new SecurityValues(this);
        }

        @Override
        protected void adjustToBaseValues(final SecurityValues pBase) {
            /* Adjust invested/gains values */
            adjustMoneyToBase(pBase, SecurityAttribute.INVESTED);
            adjustMoneyToBase(pBase, SecurityAttribute.GAINS);
            adjustMoneyToBase(pBase, SecurityAttribute.DIVIDEND);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset Invested, Gains and Dividend values */
            put(SecurityAttribute.INVESTED, new JMoney());
            put(SecurityAttribute.GAINS, new JMoney());
            put(SecurityAttribute.DIVIDEND, new JMoney());
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            JUnits myUnits = getUnitsValue(SecurityAttribute.UNITS);
            return (myUnits != null) && (myUnits.isNonZero());
        }
    }

    /**
     * SecurityBucket list class.
     */
    public static class SecurityBucketList
            extends OrderedIdList<Integer, SecurityBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.SECURITY_LIST.getValue());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected SecurityBucketList(final Analysis pAnalysis) {
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected SecurityBucketList(final Analysis pAnalysis,
                                     final SecurityBucketList pBase,
                                     final JDateDay pDate) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pDate);

                /*
                 * Ignore idle securities. Note that we must include securities that have been closed in order to adjust Market Growth.
                 */
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
        protected SecurityBucketList(final Analysis pAnalysis,
                                     final SecurityBucketList pBase,
                                     final JDateDayRange pRange) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Adjust to base and add to the list */
                    myBucket.adjustToBase();
                    append(myBucket);
                }
            }
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the security holding
         * @return the bucket
         */
        public SecurityBucket getBucket(final SecurityHolding pHolding) {
            /* Locate the bucket in the list */
            Security mySecurity = pHolding.getSecurity();
            SecurityBucket myItem = findItemById(mySecurity.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new SecurityBucket(theAnalysis, pHolding);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Mark active securities.
         * @return true/false are there active securities?
         * @throws JOceanusException on error
         */
        protected boolean markActiveSecurities() throws JOceanusException {
            /* Loop through the buckets */
            boolean areActive = false;
            Iterator<SecurityBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();
                Security mySecurity = myCurr.getSecurity();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the security as relevant */
                    mySecurity.setRelevant();
                    areActive = true;
                }

                /* If we are closed */
                if (mySecurity.isClosed()) {
                    /* Ensure that we have correct closed dates */
                    mySecurity.adjustClosed();

                    /* If we are Relevant */
                    if (mySecurity.isRelevant() && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new JMoneyWiseDataException(myCurr, "Illegally closed security");
                    }
                }
            }

            /* Return active indication */
            return areActive;
        }
    }
}
