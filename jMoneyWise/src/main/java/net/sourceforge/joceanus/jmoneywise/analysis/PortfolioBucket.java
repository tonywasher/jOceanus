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
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * Portfolio Bucket.
 */
public final class PortfolioBucket
        implements JDataContents, Comparable<PortfolioBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PortfolioBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Portfolio Field Id.
     */
    private static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPortfolio"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, AccountAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = NLS_BUNDLE.getString("NameTotals");

    /**
     * The portfolio.
     */
    private final Account thePortfolio;

    /**
     * Values.
     */
    private final SecurityValues theValues;

    /**
     * The base values.
     */
    private final SecurityValues theBaseValues;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_PORTFOLIO.equals(pField)) {
            return thePortfolio;
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
    public Account getPortfolio() {
        return thePortfolio;
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
     * Constructor.
     * @param pPortfolio the portfolio account
     */
    private PortfolioBucket(final Account pPortfolio) {
        /* Store the category */
        thePortfolio = pPortfolio;

        /* Create the value maps */
        theValues = new SecurityValues();
        theBaseValues = new SecurityValues();

        /* Create valuation fields for the portfolio */
        theValues.setValue(SecurityAttribute.VALUATION, new JMoney());
        theBaseValues.setValue(SecurityAttribute.VALUATION, new JMoney());

        /* Create profit fields for the portfolio */
        theValues.setValue(SecurityAttribute.PROFIT, new JMoney());
        theBaseValues.setValue(SecurityAttribute.PROFIT, new JMoney());

        /* Create market fields for the portfolio */
        theValues.setValue(SecurityAttribute.MARKET, new JMoney());
    }

    @Override
    public int compareTo(final PortfolioBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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

        /* Compare the Account Categories */
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

        /* Add profit values */
        myValue = pTotals.getMoneyValue(SecurityAttribute.PROFIT);
        mySrcValue = pSource.getMoneyValue(SecurityAttribute.PROFIT);
        if (mySrcValue != null) {
            myValue.addAmount(mySrcValue);
        }
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

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataTotals"));

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
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The totals.
         */
        private final PortfolioBucket theTotals;

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public PortfolioBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public PortfolioBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(PortfolioBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();
        }

        /**
         * Obtain the PortfolioBucket for a given portfolio.
         * @param pPortfolio the portfolio
         * @return the bucket
         */
        protected PortfolioBucket getBucket(final Account pPortfolio) {
            /* Locate the bucket in the list */
            PortfolioBucket myItem = findItemById(pPortfolio.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PortfolioBucket(pPortfolio);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Allocate the Totals PortfolioBucket.
         * @return the bucket
         */
        private PortfolioBucket allocateTotalsBucket() {
            /* Obtain the totals portfolio */
            return new PortfolioBucket(null);
        }

        /**
         * Analyse securities.
         * @param pSecurities the security buckets
         */
        protected void analyseSecurities(final SecurityBucketList pSecurities) {
            /* Market Analysis */
            MarketAnalysis myMarket = new MarketAnalysis();
            JDateDayRange myRange = theAnalysis.getDateRange();

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pSecurities.listIterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                SecurityBucket myCurr = myIterator.next();
                Account myPortfolio = myCurr.getPortfolio();

                /* Analyse the security bucket */
                myCurr.analyseBucket(myRange);

                /* Process market movements */
                myMarket.processSecurity(myCurr);

                /* Access security bucket and add values */
                PortfolioBucket myBucket = getBucket(myPortfolio);
                myBucket.addValues(myCurr);
                theTotals.addValues(myCurr);
            }

            /* Propagate totals */
            myMarket.propagateTotals(theAnalysis);
        }
    }
}
