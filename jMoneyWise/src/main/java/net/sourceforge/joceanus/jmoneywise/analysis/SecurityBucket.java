/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.SecurityPriceMap;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * The Security Bucket class.
 */
public final class SecurityBucket
        implements JDataContents, Comparable<SecurityBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SecurityBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Account (Security) Field Id.
     */
    private static final JDataField FIELD_SECURITY = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataSecurity"));

    /**
     * Portfolio Field Id.
     */
    private static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPortfolio"));

    /**
     * Security Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHistory"));

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, SecurityAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, SecurityAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The security.
     */
    private final Account theSecurity;

    /**
     * The portfolio.
     */
    private final Account thePortfolio;

    /**
     * The security category.
     */
    private final AccountCategory theCategory;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

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
    private final BucketHistory<SecurityValues> theHistory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_SECURITY.equals(pField)) {
            return theSecurity;
        }
        if (FIELD_PORTFOLIO.equals(pField)) {
            return thePortfolio;
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
     * Obtain the security.
     * @return the account
     */
    public Account getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public Account getPortfolio() {
        return thePortfolio;
    }

    @Override
    public Integer getOrderedId() {
        return theSecurity.getId();
    }

    /**
     * Obtain the security category.
     * @return the security category
     */
    public AccountCategory getAccountCategory() {
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
    protected FinanceData getDataSet() {
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
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public SecurityValues getValuesForEvent(final Event pEvent) {
        /* Obtain values for event */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain delta values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public SecurityValues getDeltaForEvent(final Event pEvent) {
        /* Obtain values for event */
        return theHistory.getDeltaForEvent(pEvent);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<SecurityValues> getHistoryMap() {
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
     * @param pSecurity the security
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final Account pSecurity) {
        /* Store the details */
        theSecurity = pSecurity;
        thePortfolio = pSecurity.getPortfolio();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Obtain category, allowing for autoExpense */
        theCategory = theSecurity.getAccountCategory();

        /* Create the history map */
        theHistory = new BucketHistory<SecurityValues>(new SecurityValues());

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
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getAccountCategory();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<SecurityValues>(pBase.getHistoryMap(), pDate);

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
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getAccountCategory();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<SecurityValues>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final SecurityBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
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

        /* Compare the Securities */
        SecurityBucket myThat = (SecurityBucket) pThat;
        return getSecurity().equals(myThat.getSecurity());
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
        JUnits myUnits = theValues.getUnitsValue(SecurityAttribute.Units);
        myUnits = new JUnits(myUnits);
        myUnits.addUnits(pDelta);
        setValue(SecurityAttribute.Units, myUnits);
    }

    /**
     * Adjust security cost.
     * @param pDelta the delta
     */
    protected void adjustCost(final JMoney pDelta) {
        JMoney myCost = theValues.getMoneyValue(SecurityAttribute.Cost);
        myCost = new JMoney(myCost);
        myCost.addAmount(pDelta);
        setValue(SecurityAttribute.Cost, myCost);
    }

    /**
     * Adjust security invested.
     * @param pDelta the delta
     */
    protected void adjustInvested(final JMoney pDelta) {
        JMoney myInvested = theValues.getMoneyValue(SecurityAttribute.Invested);
        myInvested = new JMoney(myInvested);
        myInvested.addAmount(pDelta);
        setValue(SecurityAttribute.Invested, myInvested);
    }

    /**
     * Adjust security gains.
     * @param pDelta the delta
     */
    protected void adjustGains(final JMoney pDelta) {
        JMoney myGains = theValues.getMoneyValue(SecurityAttribute.Gains);
        myGains = new JMoney(myGains);
        myGains.addAmount(pDelta);
        setValue(SecurityAttribute.Gains, myGains);
    }

    /**
     * Adjust security dividends.
     * @param pDelta the delta
     */
    protected void adjustDividend(final JMoney pDelta) {
        JMoney myDividend = theValues.getMoneyValue(SecurityAttribute.Dividend);
        myDividend = new JMoney(myDividend);
        myDividend.addAmount(pDelta);
        setValue(SecurityAttribute.Dividend, myDividend);
    }

    /**
     * Register the event.
     * @param pEvent the event
     * @return the registered values
     */
    protected SecurityValues registerEvent(final Event pEvent) {
        /* Register the event in the history */
        return theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * value the asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueAsset(final JDateDayRange pRange) {
        /* Obtain the appropriate price */
        SecurityPriceMap myPriceMap = theAnalysis.getPrices();
        JPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);

        /* Access base units */
        JUnits myUnits = theBaseValues.getUnitsValue(SecurityAttribute.Units);
        JPrice myPrice = myPrices[0];

        /* Calculate the value */
        theBaseValues.setValue(SecurityAttribute.Price, myPrice);
        theBaseValues.setValue(SecurityAttribute.Valuation, myUnits.valueAtPrice(myPrice));

        /* Access units */
        myUnits = theValues.getUnitsValue(SecurityAttribute.Units);
        myPrice = myPrices[1];

        /* Calculate the value */
        setValue(SecurityAttribute.Price, myPrices[1]);
        setValue(SecurityAttribute.Valuation, myUnits.valueAtPrice(myPrice));
    }

    /**
     * calculate the profit for a priced asset.
     */
    protected void calculateProfit() {
        /* Calculate the profit */
        JMoney myValuation = theValues.getMoneyValue(SecurityAttribute.Delta);
        JMoney myProfit = new JMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(SecurityAttribute.Invested));
        myProfit.addAmount(theValues.getMoneyValue(SecurityAttribute.Dividend));

        /* Set the attribute */
        setValue(SecurityAttribute.Profit, myProfit);
    }

    /**
     * Analyse the bucket.
     * @param pRange the range of valuation
     */
    protected void analyseBucket(final JDateDayRange pRange) {
        /* Value the asset over the range */
        valueAsset(pRange);

        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.Valuation);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(SecurityAttribute.Valuation));

        /* Set the delta */
        setValue(SecurityAttribute.Delta, myValue);

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
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.Delta);
        myValue = new JMoney(myValue);

        /* Subtract the investment and gains */
        myValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.Invested));
        // myValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.Gains));

        /* Set the delta */
        setValue(SecurityAttribute.Market, myValue);
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
        private static final long serialVersionUID = -7405283325286707968L;

        /**
         * Constructor.
         */
        protected SecurityValues() {
            /* Initialise class */
            super(SecurityAttribute.class);

            /* Initialise units etc. to zero */
            put(SecurityAttribute.Units, new JUnits());
            put(SecurityAttribute.Cost, new JMoney());
            put(SecurityAttribute.Invested, new JMoney());
            put(SecurityAttribute.Gains, new JMoney());
            put(SecurityAttribute.Dividend, new JMoney());
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
            adjustMoneyToBase(pBase, SecurityAttribute.Invested);
            adjustMoneyToBase(pBase, SecurityAttribute.Gains);
            adjustMoneyToBase(pBase, SecurityAttribute.Dividend);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset Invested, Gains and Dividend values */
            put(SecurityAttribute.Invested, new JMoney());
            put(SecurityAttribute.Gains, new JMoney());
            put(SecurityAttribute.Dividend, new JMoney());
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            JUnits myUnits = getUnitsValue(SecurityAttribute.Units);
            return (myUnits != null)
                   && (myUnits.isNonZero());
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
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"));

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

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
        public SecurityBucketList(final Analysis pAnalysis) {
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        public SecurityBucketList(final Analysis pAnalysis,
                                  final SecurityBucketList pBase,
                                  final JDateDay pDate) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pDate);

                /* Add to the list */
                append(myBucket);
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        public SecurityBucketList(final Analysis pAnalysis,
                                  final SecurityBucketList pBase,
                                  final JDateDayRange pRange) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive()
                    || !myBucket.isIdle()) {
                    /* Adjust to base and add to the list */
                    myBucket.adjustToBase();
                    append(myBucket);
                }
            }
        }

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        public SecurityBucket getBucket(final Account pAccount) {
            /* Locate the bucket in the list */
            SecurityBucket myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new SecurityBucket(theAnalysis, pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }
    }

    /**
     * SecurityAttribute enumeration.
     */
    public enum SecurityAttribute {
        /**
         * Valuation.
         */
        Valuation,

        /**
         * Valuation Delta.
         */
        Delta,

        /**
         * Units.
         */
        Units,

        /**
         * Cost.
         */
        Cost,

        /**
         * Gains.
         */
        Gains,

        /**
         * Invested.
         */
        Invested,

        /**
         * Dividend.
         */
        Dividend,

        /**
         * Market.
         */
        Market,

        /**
         * Profit.
         */
        Profit,

        /**
         * Price.
         */
        Price;
    }
}
