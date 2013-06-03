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
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Event Category Bucket.
 */
public final class EventCategoryBucket
        implements JDataContents, Comparable<EventCategoryBucket>, OrderedIdItem<Integer> {

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryBucket.class.getSimpleName());

    /**
     * Event Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(EventCategory.class.getSimpleName());

    /**
     * Event Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(EventCategoryType.class.getSimpleName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, EventAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, EventAttribute.class);

    /**
     * The event category.
     */
    private final EventCategory theCategory;

    /**
     * The event category type.
     */
    private final EventCategoryType theType;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

    /**
     * The base.
     */
    private final EventCategoryBucket theBase;

    /**
     * Attribute Map.
     */
    private final Map<EventAttribute, JMoney> theAttributes;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_TYPE.equals(pField)) {
            return theType;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase != null)
                    ? theBase
                    : JDataFieldValue.SkipField;
        }

        /* Handle Attribute fields */
        EventAttribute myClass = getClassForField(pField);
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
        return theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
    }

    /**
     * Obtain the event category.
     * @return the event category
     */
    public EventCategory getEventCategory() {
        return theCategory;
    }

    /**
     * Obtain the event category type.
     * @return the event category type
     */
    private EventCategoryType getCategoryType() {
        return theType;
    }

    /**
     * Obtain the base.
     * @return the base
     */
    public EventCategoryBucket getBase() {
        return theBase;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    private FinanceData getDataSet() {
        return theData;
    }

    /**
     * Obtain the attribute map.
     * @return the attribute map
     */
    protected Map<EventAttribute, JMoney> getAttributes() {
        return theAttributes;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final EventAttribute pAttr,
                                final JMoney pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final EventAttribute pAttr) {
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
    private static EventAttribute getClassForField(final JDataField pField) {
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
    private <X extends JMoney> X getAttribute(final EventAttribute pAttr,
                                              final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final EventAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    protected JMoney getMoneyAttribute(final EventAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain an attribute value from the base.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X extends JMoney> X getBaseAttribute(final EventAttribute pAttr,
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
    protected JMoney getBaseMoneyAttribute(final EventAttribute pAttr) {
        /* Obtain the attribute */
        return getBaseAttribute(pAttr, JMoney.class);
    }

    /**
     * Constructor.
     * @param pData the dataSet
     * @param pCategory the event category
     */
    private EventCategoryBucket(final FinanceData pData,
                                final EventCategory pCategory) {
        /* Store the category */
        theCategory = pCategory;
        theType = pCategory.getCategoryType();
        theData = pData;
        theBase = null;

        /* Create the attribute map */
        theAttributes = new EnumMap<EventAttribute, JMoney>(EventAttribute.class);

        /* Create all possible values */
        setAttribute(EventAttribute.Income, new JMoney());
        setAttribute(EventAttribute.Expense, new JMoney());
        setAttribute(EventAttribute.TaxCredit, new JMoney());
        setAttribute(EventAttribute.NatInsurance, new JMoney());
        setAttribute(EventAttribute.Benefit, new JMoney());
        setAttribute(EventAttribute.Donation, new JMoney());
    }

    /**
     * Constructor.
     * @param pBase the underlying bucket
     */
    private EventCategoryBucket(final EventCategoryBucket pBase) {
        /* Copy details from base */
        theCategory = pBase.getEventCategory();
        theType = pBase.getCategoryType();
        theData = pBase.getDataSet();
        theBase = pBase;

        /* Create a new attribute map */
        theAttributes = new EnumMap<EventAttribute, JMoney>(EventAttribute.class);

        /* Clone the underlying map */
        cloneMap(theBase.getAttributes(), theAttributes);
    }

    @Override
    public int compareTo(final EventCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the EventCategories */
        return getEventCategory().compareTo(pThat.getEventCategory());
    }

    /**
     * Clone a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    private void cloneMap(final Map<EventAttribute, JMoney> pSource,
                          final Map<EventAttribute, JMoney> pTarget) {
        /* For each entry in the source map */
        for (Map.Entry<EventAttribute, JMoney> myEntry : pSource.entrySet()) {
            /* Access key and object */
            EventAttribute myAttr = myEntry.getKey();

            /* Switch on the Attribute */
            switch (myAttr) {
                case Income:
                case Expense:
                case TaxCredit:
                case NatInsurance:
                case Benefit:
                case Donation:
                    pTarget.put(myAttr, new JMoney());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final JMoney pValue) {
        JMoney myIncome = getAttribute(EventAttribute.Income, JMoney.class);
        myIncome.addAmount(pValue);
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final JMoney pValue) {
        JMoney myExpense = getAttribute(EventAttribute.Income, JMoney.class);
        myExpense.subtractAmount(pValue);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    protected void addExpense(final JMoney pValue) {
        JMoney myIncome = getAttribute(EventAttribute.Expense, JMoney.class);
        myIncome.addAmount(pValue);
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final JMoney pValue) {
        JMoney myExpense = getAttribute(EventAttribute.Expense, JMoney.class);
        myExpense.subtractAmount(pValue);
    }

    /**
     * Add event to totals.
     * @param pEvent the event
     */
    protected void adjustValues(final Event pEvent) {
        /* Switch on the category */
        switch (theCategory.getCategoryTypeClass()) {
            case TaxedIncome:
            case RentalIncome:
            case Interest:
            case TaxFreeInterest:
            case Dividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
            case OtherIncome:
            case Inherited:
            case LoanInterest:
                /* Adjust income */
                JMoney myMoney = getAttribute(EventAttribute.Income, JMoney.class);
                myMoney.addAmount(pEvent.getAmount());

                /* Access subValues */
                JMoney myTaxCredit = pEvent.getTaxCredit();
                JMoney myNatIns = pEvent.getNatInsurance();
                JMoney myBenefit = pEvent.getBenefit();
                JMoney myDonation = pEvent.getDonation();

                /* If there is a tax credit */
                if (myTaxCredit != null) {
                    myMoney = getAttribute(EventAttribute.TaxCredit, JMoney.class);
                    myMoney.addAmount(myTaxCredit);
                }

                /* If there is national insurance */
                if (myNatIns != null) {
                    myMoney = getAttribute(EventAttribute.NatInsurance, JMoney.class);
                    myMoney.addAmount(myNatIns);
                }

                /* If there is a benefit */
                if (myBenefit != null) {
                    myMoney = getAttribute(EventAttribute.Benefit, JMoney.class);
                    myMoney.addAmount(myBenefit);
                }

                /* If there is a donation */
                if (myDonation != null) {
                    myMoney = getAttribute(EventAttribute.Donation, JMoney.class);
                    myMoney.addAmount(myDonation);
                }
                break;
            default:
                /* Adjust expense */
                myMoney = getAttribute(EventAttribute.Expense, JMoney.class);
                myMoney.addAmount(pEvent.getAmount());
                break;
        }
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void adjustValues(final EventCategoryBucket pBucket) {
        /* Add underlying attributes */
        addValues(pBucket.getAttributes());
    }

    /**
     * Add bucket to totals.
     * @param pAttributes the underlying attributes
     */
    private void addValues(final Map<EventAttribute, JMoney> pAttributes) {
        /* For each entry in the source map */
        for (Map.Entry<EventAttribute, JMoney> myEntry : pAttributes.entrySet()) {
            /* Access key and object */
            EventAttribute myAttr = myEntry.getKey();
            JMoney myObject = myEntry.getValue();

            /* Switch on the Attribute */
            switch (myAttr) {
                case Income:
                    JMoney myIncome = getAttribute(myAttr, JMoney.class);
                    myIncome.addAmount(myObject);
                    break;
                case Expense:
                    JMoney myExpense = getAttribute(myAttr, JMoney.class);
                    myExpense.addAmount(myObject);
                    break;
                case TaxCredit:
                case NatInsurance:
                case Benefit:
                case Donation:
                default:
                    break;
            }
        }
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        /* Check for non-zero amount */
        JMoney myIncome = getAttribute(EventAttribute.Income, JMoney.class);
        JMoney myExpense = getAttribute(EventAttribute.Expense, JMoney.class);
        return myIncome.isNonZero()
               || myExpense.isNonZero();
    }

    /**
     * Is the bucket relevant? That is to say is either this bucket or it's base active?
     * @return true/false
     */
    protected boolean isRelevant() {
        /* Relevant if this value is non-zero or if this is the totals */
        if (isActive()
            || (theType.getCategoryClass() == EventCategoryClass.Totals)) {
            return true;
        }

        /* Relevant if the previous value is non-zero */
        return (theBase != null)
               && (theBase.isActive());
    }

    /**
     * EventCategoryBucket list class.
     */
    public static class EventCategoryBucketList
            extends OrderedIdList<Integer, EventCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryBucketList.class.getSimpleName());

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
         * The totals.
         */
        private final EventCategoryBucket theTotals;

        /**
         * Obtain the Totals EventCategoryBucket.
         * @return the bucket
         */
        protected EventCategoryBucket getTotalsBucket() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public EventCategoryBucketList(final Analysis pAnalysis) {
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
        }

        /**
         * Construct a secondary List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        public EventCategoryBucketList(final Analysis pAnalysis,
                                       final EventCategoryBucketList pBase) {
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();

            /* Access the iterator */
            Iterator<EventCategoryBucket> myIterator = pBase.listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* If the bucket is active */
                if (myCurr.isActive()) {
                    /* Add a derived bucket to the list */
                    EventCategoryBucket myBucket = new EventCategoryBucket(myCurr);
                    add(myBucket);
                }
            }

            /* Access the totals bucket */
            theTotals = allocateTotalsBucket();
        }

        /**
         * Obtain the EventCategoryBucket for a given event category.
         * @param pCategory the event category
         * @return the bucket
         */
        protected EventCategoryBucket getBucket(final EventCategory pCategory) {
            /* Locate the bucket in the list */
            EventCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new EventCategoryBucket(theData, pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the EventCategoryBucket for a given event category class.
         * @param pClass the event category class
         * @return the bucket
         */
        protected EventCategoryBucket getBucket(final EventCategoryClass pClass) {
            /* Locate the bucket in the list */
            EventCategory myCategory = theData.getEventCategories().getSingularClass(pClass);
            EventCategoryBucket myItem = findItemById(myCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new EventCategoryBucket(theData, myCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Totals EventCategoryBucket.
         * @return the bucket
         */
        private EventCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            EventCategory myTotals = theData.getEventCategories().getSingularClass(EventCategoryClass.Totals);
            return getBucket(myTotals);
        }

        /**
         * Produce totals for event categories.
         * @param pMetaAnalysis the meta analysis
         */
        protected void produceTotals(final MetaAnalysis pMetaAnalysis) {
            /* Create a list of new buckets */
            OrderedIdList<Integer, EventCategoryBucket> myTotals = new OrderedIdList<Integer, EventCategoryBucket>(EventCategoryBucket.class);

            /* Obtain the secondary buckets */
            EventCategoryBucket myTaxCredit = getBucket(EventCategoryClass.TaxCredit);
            EventCategoryBucket myNatInsurance = getBucket(EventCategoryClass.NatInsurance);
            EventCategoryBucket myBenefit = getBucket(EventCategoryClass.Benefit);
            EventCategoryBucket myDonation = getBucket(EventCategoryClass.CharityDonation);

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Copy amounts down */
                myTaxCredit.addExpense(myCurr.getAttribute(EventAttribute.TaxCredit, JMoney.class));
                myNatInsurance.addExpense(myCurr.getAttribute(EventAttribute.NatInsurance, JMoney.class));
                myBenefit.addExpense(myCurr.getAttribute(EventAttribute.Benefit, JMoney.class));
                myDonation.addExpense(myCurr.getAttribute(EventAttribute.Donation, JMoney.class));

                /* Obtain category and parent category */
                EventCategory myCategory = myCurr.getEventCategory();
                EventCategory myParent = myCategory.getParentCategory();

                /* If we have a parent category */
                if (myParent != null) {
                    /* Access parent bucket */
                    EventCategoryBucket myTotal = findItemById(myParent.getId());

                    /* If the bucket does not exist */
                    if (myTotal == null) {
                        /* Look for bucket in the new list */
                        myTotal = myTotals.findItemById(myParent.getId());

                        /* If the bucket is completely new */
                        if (myTotal == null) {
                            /* Create the new bucket and add to new list */
                            myTotal = new EventCategoryBucket(theData, myParent);
                            myTotals.add(myTotal);
                        }
                    }

                    /* Add the bucket to the totals */
                    myTotal.adjustValues(myCurr);
                }

                /* Prime the tax buckets */
                pMetaAnalysis.primeTaxCategory(myCurr);

                /* Remove the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    myIterator.remove();
                }
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Ignore the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    continue;
                }

                /* Obtain category and parent category */
                EventCategory myCategory = myCurr.getEventCategory();
                EventCategory myParent = myCategory.getParentCategory();

                /* If we have a parent category */
                if (myParent != null) {
                    /* Add the bucket to the totals */
                    findItemById(myParent.getId()).adjustValues(myCurr);
                }

                /* Add it to the list */
                add(myCurr);
            }
        }
    }

    /**
     * EventAttribute enumeration.
     */
    public enum EventAttribute {
        /**
         * Income.
         */
        Income,

        /**
         * Expense.
         */
        Expense,

        /**
         * IncomeDelta.
         */
        IncomeDelta,

        /**
         * TaxCredit.
         */
        TaxCredit,

        /**
         * NatInsurance.
         */
        NatInsurance,

        /**
         * Benefit.
         */
        Benefit,

        /**
         * Donation.
         */
        Donation;
    }
}