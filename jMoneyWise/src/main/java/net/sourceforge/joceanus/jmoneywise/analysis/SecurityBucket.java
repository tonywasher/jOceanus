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

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.analysis.InvestmentAnalysis.InvestmentAnalysisList;
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
     * Investment Analysis Field Id.
     */
    private static final JDataField FIELD_INVEST = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataDetail"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

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
     * The base.
     */
    private final SecurityBucket theBase;

    /**
     * InvestmentAnalyses list.
     */
    private InvestmentAnalysisList theInvestmentAnalyses = null;

    /**
     * Attribute Map.
     */
    private final Map<SecurityAttribute, Object> theAttributes;

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
        if (FIELD_INVEST.equals(pField)) {
            return (theInvestmentAnalyses != null)
                    ? theInvestmentAnalyses
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase != null)
                    ? theBase
                    : JDataFieldValue.SkipField;
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
     * Obtain the investmentAnalysisList.
     * @return the investmentAnalysisList
     */
    public InvestmentAnalysisList getInvestmentAnalyses() {
        return theInvestmentAnalyses;
    }

    /**
     * Obtain the base.
     * @return the base
     */
    public SecurityBucket getBase() {
        return theBase;
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
     * Obtain the attribute map.
     * @return the attribute map
     */
    protected Map<SecurityAttribute, Object> getAttributes() {
        return theAttributes;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final SecurityAttribute pAttr,
                                final Object pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
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
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X> X getAttribute(final SecurityAttribute pAttr,
                               final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JPrice getPriceAttribute(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JPrice.class);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getMoneyAttribute(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain a units attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JUnits getUnitsAttribute(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JUnits.class);
    }

    /**
     * Obtain an attribute value from the base.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X extends JDecimal> X getBaseAttribute(final SecurityAttribute pAttr,
                                                    final Class<X> pClass) {
        /* Obtain the attribute */
        return (theBase == null)
                ? null
                : theBase.getAttribute(pAttr, pClass);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getBaseMoneyAttribute(final SecurityAttribute pAttr) {
        /* Obtain the attribute */
        return getBaseAttribute(pAttr, JMoney.class);
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
        theBase = null;

        /* Obtain category, allowing for autoExpense */
        theCategory = theSecurity.getAccountCategory();

        /* Create the attribute map */
        theAttributes = new EnumMap<SecurityAttribute, Object>(SecurityAttribute.class);

        /* Initialise units to zero */
        setAttribute(SecurityAttribute.Units, new JUnits());

        /* Initialise money values */
        setAttribute(SecurityAttribute.Cost, new JMoney());
        setAttribute(SecurityAttribute.Invested, new JMoney());
        setAttribute(SecurityAttribute.Gains, new JMoney());
        setAttribute(SecurityAttribute.Gained, new JMoney());
        setAttribute(SecurityAttribute.Dividend, new JMoney());

        /* allocate the InvestmentAnalysis list */
        theInvestmentAnalyses = new InvestmentAnalysisList(theData, theSecurity);
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
     * Adjust security for debit.
     * @param pEvent the event causing the debit
     */
    protected void adjustForDebit(final Event pEvent) {
        JUnits myDelta = pEvent.getDebitUnits();
        if (myDelta != null) {
            JUnits myUnits = getUnitsAttribute(SecurityAttribute.Units);
            myUnits.subtractUnits(myDelta);
        }
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForCredit(final Event pEvent) {
        JUnits myDelta = pEvent.getCreditUnits();
        if (myDelta != null) {
            JUnits myUnits = getUnitsAttribute(SecurityAttribute.Units);
            myUnits.addUnits(myDelta);
        }
    }

    /**
     * Copy a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    protected void copyMap(final Map<SecurityAttribute, Object> pSource,
                           final Map<SecurityAttribute, Object> pTarget) {
        /* Clear the target map */
        pTarget.clear();

        /* For each entry in the source map */
        for (Map.Entry<SecurityAttribute, Object> myEntry : pSource.entrySet()) {
            /* Access key and object */
            SecurityAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Create copy of object in map */
            if (myObject instanceof JPrice) {
                pTarget.put(myAttr, new JPrice((JPrice) myObject));
            } else if (myObject instanceof JMoney) {
                pTarget.put(myAttr, new JMoney((JMoney) myObject));
            } else if (myObject instanceof JUnits) {
                pTarget.put(myAttr, new JUnits((JUnits) myObject));
            }
        }
    }

    /**
     * Clone a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    private void cloneMap(final Map<SecurityAttribute, Object> pSource,
                          final Map<SecurityAttribute, Object> pTarget) {
        /* For each entry in the source map */
        for (Map.Entry<SecurityAttribute, Object> myEntry : pSource.entrySet()) {
            /* Access key and object */
            SecurityAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Switch on the Attribute */
            switch (myAttr) {
                case Valuation:
                case Cost:
                case Gained:
                    pTarget.put(myAttr, new JMoney((JMoney) myObject));
                    break;
                case Gains:
                case Invested:
                case Dividend:
                    pTarget.put(myAttr, new JMoney());
                    break;
                case Units:
                    pTarget.put(myAttr, new JUnits((JUnits) myObject));
                    break;
                case Price:
                    pTarget.put(myAttr, myObject);
                    break;
                default:
                    break;
            }
        }
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
        JUnits myUnits = getUnitsAttribute(SecurityAttribute.Units);
        JPrice myPrice;

        /* If we found a price */
        if (myActPrice != null) {
            /* Store the price */
            myPrice = myActPrice.getPrice();

            /* else assume zero price */
        } else {
            myPrice = new JPrice();
        }

        /* Calculate the value */
        setAttribute(SecurityAttribute.Price, myPrice);
        setAttribute(SecurityAttribute.Valuation, myUnits.valueAtPrice(myPrice));
    }

    /**
     * calculate the profit for a priced asset.
     */
    protected void calculateProfit() {
        /* Calculate the profit */
        JMoney myValuation = getMoneyAttribute(SecurityAttribute.Valuation);
        JMoney myProfit = new JMoney(myValuation);
        myProfit.subtractAmount(getMoneyAttribute(SecurityAttribute.Cost));
        myProfit.addAmount(getMoneyAttribute(SecurityAttribute.Gained));

        /* Set the attribute */
        setAttribute(SecurityAttribute.Profit, myProfit);
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = new JMoney(getMoneyAttribute(SecurityAttribute.Valuation));

        /* Subtract any base value */
        if (theBase != null) {
            myValue.subtractAmount(getBaseMoneyAttribute(SecurityAttribute.Valuation));
        }

        /* Set the delta */
        setAttribute(SecurityAttribute.ValueDelta, myValue);
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
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        JUnits myUnits = getUnitsAttribute(SecurityAttribute.Units);
        return (myUnits != null)
               && (myUnits.isNonZero());
    }

    /**
     * Is the bucket relevant? That is to say is either this bucket or it's base active?
     * @return true/false
     */
    public boolean isRelevant() {
        /* Relevant if this value or the previous value is non-zero */
        if (isActive()) {
            return true;
        }
        return (theBase != null)
               && (theBase.isActive());
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
         * Gained.
         */
        Gained,

        /**
         * Invested.
         */
        Invested,

        /**
         * Dividend.
         */
        Dividend,

        /**
         * Gains.
         */
        Gains,

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
