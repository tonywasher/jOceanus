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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategorySection;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventAttribute;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * The Tax Bucket class.
 */
public final class TaxCategoryBucket
        implements JDataContents, Comparable<TaxCategoryBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TaxCategoryBucket.class.getSimpleName());

    /**
     * Tax Category Field Id.
     */
    private static final JDataField FIELD_TAXCAT = FIELD_DEFS.declareEqualityField(TaxCategory.class.getSimpleName());

    /**
     * Tax Section Field Id.
     */
    private static final JDataField FIELD_TAXSECT = FIELD_DEFS.declareEqualityField(TaxCategorySection.class.getSimpleName());

    /**
     * Parent Field Id.
     */
    private static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField("Parent");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TaxAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TaxAttribute.class);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_TAXCAT.equals(pField)) {
            return theTaxCategory;
        }
        if (FIELD_TAXSECT.equals(pField)) {
            return theTaxSection;
        }
        if (FIELD_PARENT.equals(pField)) {
            return (theParent != null)
                    ? theParent
                    : JDataFieldValue.SkipField;
        }

        /* Handle Attribute fields */
        TaxAttribute myClass = getClassForField(pField);
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

    /**
     * Tax Category.
     */
    private final TaxCategory theTaxCategory;

    /**
     * Tax Section.
     */
    private final TaxCategorySection theTaxSection;

    /**
     * Attribute Map.
     */
    private final Map<TaxAttribute, JDecimal> theAttributes;

    /**
     * The parent.
     */
    private TaxCategoryBucket theParent = null;

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public Integer getOrderedId() {
        return theTaxCategory.getId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTaxCategory.getName();
    }

    /**
     * Obtain tax category.
     * @return the category
     */
    public TaxCategory getTaxCategory() {
        return theTaxCategory;
    }

    /**
     * Obtain the tax section.
     * @return the tax section
     */
    protected TaxCategorySection getCategorySection() {
        return theTaxSection;
    }

    /**
     * Obtain the parent.
     * @return the parent
     */
    public TaxCategoryBucket getParent() {
        return theParent;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final TaxAttribute pAttr,
                                final JDecimal pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TaxAttribute pAttr) {
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
    private static TaxAttribute getClassForField(final JDataField pField) {
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
    private <X extends JDecimal> X getAttribute(final TaxAttribute pAttr,
                                                final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    protected JMoney getMoneyAttribute(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    protected JRate getRateAttribute(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JRate.class);
    }

    /**
     * Constructor.
     * @param pTaxCategory the category
     */
    private TaxCategoryBucket(final TaxCategory pTaxCategory) {
        /* Store the tax category */
        theTaxCategory = pTaxCategory;

        /* Determine the tax section */
        theTaxSection = theTaxCategory.getTaxClass().getClassSection();

        /* Create the attribute map */
        theAttributes = new EnumMap<TaxAttribute, JDecimal>(TaxAttribute.class);

        /* Create all possible values */
        setAttribute(TaxAttribute.Amount, new JMoney());
    }

    @Override
    public int compareTo(final TaxCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the TaxCategories */
        return getTaxCategory().compareTo(pThat.getTaxCategory());
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Is the bucket relevant?
     * @return true/false
     */
    protected boolean isRelevant() {
        /* Check for non-zero amount */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        JMoney myTax = getAttribute(TaxAttribute.Taxation, JMoney.class);
        return myAmount.isNonZero()
               || ((myTax != null) && (myTax.isNonZero()));
    }

    /**
     * Set a taxation amount and calculate the tax on it.
     * @param pAmount Amount to set
     * @return the taxation on this bucket
     */
    protected JMoney setAmount(final JMoney pAmount) {
        /* Access the rate */
        JRate myRate = getAttribute(TaxAttribute.Rate, JRate.class);

        /* Set the value */
        JMoney myAmount = new JMoney(pAmount);
        setAttribute(TaxAttribute.Amount, myAmount);

        /* Calculate the tax if we have a rate */
        JMoney myTaxation = (myRate != null)
                ? myAmount.valueAtRate(myRate)
                : new JMoney();

        /* Return the taxation amount */
        setAttribute(TaxAttribute.Taxation, myTaxation);
        return myTaxation;
    }

    /**
     * Set explicit taxation value.
     * @param pAmount Amount to set
     */
    protected void setTaxation(final JMoney pAmount) {
        /* Set the value */
        setAttribute(TaxAttribute.Taxation, new JMoney(pAmount));
    }

    /**
     * Set parent bucket for reporting purposes.
     * @param pParent the parent bucket
     */
    protected void setParent(final TaxCategoryBucket pParent) {
        /* Set the value */
        theParent = pParent;
    }

    /**
     * Set a tax rate.
     * @param pRate Amount to set
     */
    protected void setRate(final JRate pRate) {
        /* Set the value */
        setAttribute(TaxAttribute.Rate, new JRate(pRate));
    }

    /**
     * Add income.
     * @param pBucket event category bucket
     */
    protected void addIncome(final EventCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.Income));
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.TaxCredit));
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.NatInsurance));
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.Benefit));
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.Donation));
    }

    /**
     * Subtract income.
     * @param pBucket event category bucket
     */
    protected void subtractIncome(final EventCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.subtractAmount(pBucket.getMoneyAttribute(EventAttribute.Income));
    }

    /**
     * Add expense.
     * @param pBucket event category bucket
     */
    protected void addExpense(final EventCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.addAmount(pBucket.getMoneyAttribute(EventAttribute.Expense));
    }

    /**
     * Subtract expense.
     * @param pBucket event category bucket
     */
    protected void subtractExpense(final EventCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.subtractAmount(pBucket.getMoneyAttribute(EventAttribute.Expense));
    }

    /**
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final TaxCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.addAmount(pBucket.getMoneyAttribute(TaxAttribute.Amount));
    }

    /**
     * subtract values.
     * @param pBucket tax category bucket
     */
    protected void subtractValues(final TaxCategoryBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = getAttribute(TaxAttribute.Amount, JMoney.class);
        myAmount.subtractAmount(pBucket.getMoneyAttribute(TaxAttribute.Amount));
    }

    /**
     * TaxCategoryBucketList class.
     */
    public static class TaxCategoryBucketList
            extends OrderedIdList<Integer, TaxCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxCategoryBucketList.class.getSimpleName());

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
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Analysis field Id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

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
         * The data.
         */
        private final FinanceData theData;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public TaxCategoryBucketList(final Analysis pAnalysis) {
            super(TaxCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
        }

        /**
         * Obtain the EventCategoryBucket for a given event category class.
         * @param pClass the event category class
         * @return the bucket
         */
        protected TaxCategoryBucket getBucket(final TaxCategoryClass pClass) {
            /* Locate the bucket in the list */
            TaxCategory myCategory = theData.getTaxCategories().findItemByClass(pClass);
            TaxCategoryBucket myItem = findItemById(myCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxCategoryBucket(myCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Access the iterator */
            Iterator<TaxCategoryBucket> myIterator = listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                TaxCategoryBucket myCurr = myIterator.next();

                /* Remove the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    myIterator.remove();
                }
            }
        }
    }

    /**
     * EventAttribute enumeration.
     */
    public enum TaxAttribute {
        /**
         * Amount.
         */
        Amount,

        /**
         * Rate.
         */
        Rate,

        /**
         * Taxation.
         */
        Taxation;
    }
}
