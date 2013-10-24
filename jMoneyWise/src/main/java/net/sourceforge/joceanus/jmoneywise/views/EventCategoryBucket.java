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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.AccountType;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

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
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(Analysis.class.getSimpleName());

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
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The event category.
     */
    private final EventCategory theCategory;

    /**
     * The event category type.
     */
    private final EventCategoryType theType;

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
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
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
    public EventCategoryType getEventCategoryType() {
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
    public JMoney getMoneyAttribute(final EventAttribute pAttr) {
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
    public JMoney getBaseMoneyAttribute(final EventAttribute pAttr) {
        /* Obtain the attribute */
        return getBaseAttribute(pAttr, JMoney.class);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the event category
     */
    private EventCategoryBucket(final Analysis pAnalysis,
                                final EventCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = pCategory.getCategoryType();
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
        setAttribute(EventAttribute.IncomeDelta, new JMoney());
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

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof EventCategoryBucket)) {
            return false;
        }

        /* Compare the Event Categories */
        EventCategoryBucket myThat = (EventCategoryBucket) pThat;
        return getEventCategory().equals(myThat.getEventCategory());
    }

    @Override
    public int hashCode() {
        return getEventCategory().hashCode();
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final JMoney pValue) {
        JMoney myIncome = getMoneyAttribute(EventAttribute.Income);
        myIncome.addAmount(pValue);
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final JMoney pValue) {
        JMoney myExpense = getMoneyAttribute(EventAttribute.Income);
        myExpense.subtractAmount(pValue);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    protected void addExpense(final JMoney pValue) {
        JMoney myIncome = getMoneyAttribute(EventAttribute.Expense);
        myIncome.addAmount(pValue);
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final JMoney pValue) {
        JMoney myExpense = getMoneyAttribute(EventAttribute.Expense);
        myExpense.subtractAmount(pValue);
    }

    /**
     * Add event to totals.
     * @param pEvent the event
     */
    protected void adjustValues(final Event pEvent) {
        /* Analyse the event */
        AccountType myDebitType = AccountType.deriveType(pEvent.getDebit());
        AccountType myCreditType = AccountType.deriveType(pEvent.getCredit());
        TransactionType myCatTran = TransactionType.deriveType(pEvent.getCategory());
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);
        JMoney myExpense = getMoneyAttribute(EventAttribute.Expense);
        JMoney myIncome = getMoneyAttribute(EventAttribute.Income);

        /* If this is an expense */
        if (myCatTran.isExpense()) {
            /* If this is a recovered expense */
            if (myActTran.isIncome()) {
                myIncome.addAmount(pEvent.getAmount());
            } else {
                myExpense.addAmount(pEvent.getAmount());
            }
        } else {
            /* If this is a returned income */
            if (myActTran.isExpense()) {
                myExpense.addAmount(pEvent.getAmount());
            } else {
                myIncome.addAmount(pEvent.getAmount());
            }

            /* Access subValues */
            JMoney myTaxCredit = pEvent.getTaxCredit();
            JMoney myNatIns = pEvent.getNatInsurance();
            JMoney myBenefit = pEvent.getDeemedBenefit();
            JMoney myDonation = pEvent.getCharityDonation();

            /* If there is a tax credit */
            if (myTaxCredit != null) {
                myIncome = getMoneyAttribute(EventAttribute.TaxCredit);
                myIncome.addAmount(myTaxCredit);
            }

            /* If there is national insurance */
            if (myNatIns != null) {
                myIncome = getMoneyAttribute(EventAttribute.NatInsurance);
                myIncome.addAmount(myNatIns);
            }

            /* If there is a benefit */
            if (myBenefit != null) {
                myIncome = getMoneyAttribute(EventAttribute.Benefit);
                myIncome.addAmount(myBenefit);
            }

            /* If there is a donation */
            if (myDonation != null) {
                myIncome = getMoneyAttribute(EventAttribute.Donation);
                myIncome.addAmount(myDonation);
            }
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
     * Calculate Income delta.
     */
    protected void calculateDelta() {
        /* Add underlying attributes */
        JMoney myMoney = new JMoney(getMoneyAttribute(EventAttribute.Income));
        myMoney.subtractAmount(getMoneyAttribute(EventAttribute.Expense));
        setAttribute(EventAttribute.IncomeDelta, myMoney);
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
                    JMoney myIncome = getMoneyAttribute(myAttr);
                    myIncome.addAmount(myObject);
                    break;
                case Expense:
                    JMoney myExpense = getMoneyAttribute(myAttr);
                    myExpense.addAmount(myObject);
                    break;
                case IncomeDelta:
                    JMoney myDelta = getMoneyAttribute(myAttr);
                    myDelta.addAmount(myObject);
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
        JMoney myIncome = getMoneyAttribute(EventAttribute.Income);
        JMoney myExpense = getMoneyAttribute(EventAttribute.Expense);
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
        public EventCategoryBucket getTotalsBucket() {
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
                myItem = new EventCategoryBucket(theAnalysis, pCategory);

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
            /* Determine required category */
            EventCategory myCategory = theData.getEventCategories().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Obtain the Totals EventCategoryBucket.
         * @return the bucket
         */
        private EventCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return getBucket(EventCategoryClass.Totals);
        }

        /**
         * Produce totals for event categories.
         * @param pMetaAnalysis the meta analysis
         */
        protected void produceTotals(final MetaAnalysis pMetaAnalysis) {
            /* Create a list of new buckets */
            OrderedIdList<Integer, EventCategoryBucket> myTotals = new OrderedIdList<Integer, EventCategoryBucket>(EventCategoryBucket.class);

            /* Obtain the secondary buckets */
            EventCategoryList myList = theData.getEventCategories();
            EventCategoryBucket myTaxCredit = getBucket(myList.getEventInfoCategory(EventInfoClass.TaxCredit));
            EventCategoryBucket myNatInsurance = getBucket(myList.getEventInfoCategory(EventInfoClass.NatInsurance));
            EventCategoryBucket myBenefit = getBucket(myList.getEventInfoCategory(EventInfoClass.DeemedBenefit));
            EventCategoryBucket myDonation = getBucket(myList.getEventInfoCategory(EventInfoClass.CharityDonation));

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Apply tax credit correctly */
                JMoney myMoney = myCurr.getMoneyAttribute(EventAttribute.TaxCredit);
                myCurr.addIncome(myMoney);
                myTaxCredit.addExpense(myMoney);

                /* Apply NatInsurance correctly */
                myMoney = myCurr.getMoneyAttribute(EventAttribute.NatInsurance);
                myCurr.addIncome(myMoney);
                myNatInsurance.addExpense(myMoney);

                /* Apply benefit correctly */
                myMoney = myCurr.getMoneyAttribute(EventAttribute.Benefit);
                myCurr.addIncome(myMoney);
                myBenefit.addExpense(myMoney);

                /* Apply donation correctly */
                myMoney = myCurr.getMoneyAttribute(EventAttribute.Donation);
                myCurr.addIncome(myMoney);
                myDonation.addExpense(myMoney);
            }

            /* Loop through the buckets again */
            myIterator = iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

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
                            myTotal = new EventCategoryBucket(theAnalysis, myParent);
                            myTotals.add(myTotal);
                        }
                    }

                    /* If this category is a base category */
                    if (!myCurr.getEventCategory().getCategoryTypeClass().canParentCategory()) {
                        /* Calculate the delta */
                        myCurr.calculateDelta();
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
