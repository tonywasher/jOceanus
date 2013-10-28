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

import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
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
     * Is the bucket idle.
     */
    private final Boolean isIdle;

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
                        : JDataFieldValue.SkipField;
            }
            return myValue;
        }

        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return getName();
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
     * Is this bucket idle.
     * @return true/false
     */
    public Boolean isIdle() {
        return isIdle;
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
    protected SecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    protected SecurityValues getBaseValues() {
        return theBaseValues;
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
                : JDataFieldValue.SkipField;
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

        /* Create the value maps */
        theValues = new SecurityValues();
        theBaseValues = new SecurityValues();
        isIdle = false;

        /* Create the history map */
        theHistory = new BucketHistory<SecurityValues>();
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

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Copy base values from source */
        theBaseValues = pBase.getBaseValues().getSnapShot();

        /* Obtain values for date */
        SecurityValues myValues = theHistory.getValuesForDate(pDate);

        /* Determine values */
        isIdle = (myValues == null);
        theValues = (isIdle)
                ? theBaseValues.getSnapShot()
                : myValues;
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

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Obtain values for range */
        SecurityValues[] myArray = theHistory.getValuesForRange(pRange);

        /* If no activity took place up to this date */
        if (myArray == null) {
            /* Use base values and note idleness */
            theValues = pBase.getBaseValues().getSnapShot();
            theBaseValues = theValues.getSnapShot();
            isIdle = true;

            /* else we have values */
        } else {
            /* Determine base values */
            SecurityValues myFirst = myArray[0];
            theBaseValues = (myFirst == null)
                    ? pBase.getBaseValues().getSnapShot()
                    : myFirst;

            /* Determine values */
            SecurityValues myValues = myArray[1];
            isIdle = (myValues == null);
            theValues = (isIdle)
                    ? theBaseValues.getSnapShot()
                    : myValues;
        }

        /* Adjust to base values */
        adjustToBaseValues();
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
    }

    /**
     * Adjust security cost.
     * @param pDelta the delta
     */
    protected void adjustCost(final JMoney pDelta) {
        JMoney myCost = theValues.getMoneyValue(SecurityAttribute.Cost);
        myCost = new JMoney(myCost);
        myCost.addAmount(pDelta);
    }

    /**
     * Adjust security invested.
     * @param pDelta the delta
     */
    protected void adjustInvested(final JMoney pDelta) {
        JMoney myInvested = theValues.getMoneyValue(SecurityAttribute.Invested);
        myInvested = new JMoney(myInvested);
        myInvested.addAmount(pDelta);
    }

    /**
     * Adjust security gains.
     * @param pDelta the delta
     */
    protected void adjustGains(final JMoney pDelta) {
        JMoney myGains = theValues.getMoneyValue(SecurityAttribute.Gains);
        myGains = new JMoney(myGains);
        myGains.addAmount(pDelta);
    }

    /**
     * Adjust security dividends.
     * @param pDelta the delta
     */
    protected void adjustDividend(final JMoney pDelta) {
        JMoney myDividend = theValues.getMoneyValue(SecurityAttribute.Dividend);
        myDividend = new JMoney(myDividend);
        myDividend.addAmount(pDelta);
    }

    /**
     * Register the event.
     * @param pEvent the event
     */
    protected void registerEvent(final Event pEvent) {
        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * value the asset at a particular date.
     * @param pDate the date of valuation
     */
    protected void valueAsset(final JDateDay pDate) {
        /* Obtain the appropriate price record */
        AccountPriceList myPrices = theData.getPrices();
        AccountPrice myActPrice = myPrices.getLatestPrice(getSecurity(), pDate);

        /* Access units */
        JUnits myUnits = theValues.getUnitsValue(SecurityAttribute.Units);
        JPrice myPrice = (myActPrice != null)
                ? myActPrice.getPrice()
                : new JPrice();

        /* Calculate the value */
        setValue(SecurityAttribute.Price, myPrice);
        setValue(SecurityAttribute.Valuation, myUnits.valueAtPrice(myPrice));
    }

    /**
     * calculate the profit for a priced asset.
     */
    protected void calculateProfit() {
        /* Calculate the profit */
        JMoney myValuation = theValues.getMoneyValue(SecurityAttribute.Valuation);
        JMoney myProfit = new JMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(SecurityAttribute.Invested));

        /* Set the attribute */
        setValue(SecurityAttribute.Profit, myProfit);
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.Valuation);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(SecurityAttribute.Valuation));

        /* Set the delta */
        setValue(SecurityAttribute.ValueDelta, myValue);
    }

    /**
     * analyse bucket.
     * @param pDate the date for analysis
     */
    protected void analyseBucket(final JDateDay pDate) {
        valueAsset(pDate);
        calculateDelta();
    }

    /**
     * Adjust to Base values.
     */
    private void adjustToBaseValues() {
        /* Adjust invested values */
        JMoney myValue = theValues.getMoneyValue(SecurityAttribute.Invested);
        JMoney myBaseValue = theBaseValues.getMoneyValue(SecurityAttribute.Invested);
        myValue.subtractAmount(myBaseValue);
        theBaseValues.put(SecurityAttribute.Invested, null);

        /* Adjust dividend values */
        myValue = theValues.getMoneyValue(SecurityAttribute.Dividend);
        myBaseValue = theBaseValues.getMoneyValue(SecurityAttribute.Dividend);
        myValue.subtractAmount(myBaseValue);
        theBaseValues.put(SecurityAttribute.Dividend, null);
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
        protected SecurityValues[] getSnapShotArray() {
            /* Allocate the array and return it */
            return new SecurityValues[2];
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
            return JDataFieldValue.UnknownField;
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
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected SecurityBucket getBucket(final Account pAccount) {
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
        ValueDelta,

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
         * Profit.
         */
        Profit,

        /**
         * Price.
         */
        Price;
    }
}
