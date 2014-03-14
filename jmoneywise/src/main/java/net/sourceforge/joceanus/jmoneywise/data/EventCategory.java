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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Event Category class.
 */
public final class EventCategory
        extends CategoryBase<EventCategory, EventCategoryType, EventCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.EVENTCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.EVENTCATEGORY.getListName();

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventCategory.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.EVENTTYPE.getItemName());

    /**
     * Different Parent Error.
     */
    private static final String ERROR_DIFFPARENT = NLS_BUNDLE.getString("ErrorDiffParent");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return super.isActive() || isHidden();
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_CATTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public EventCategoryType getCategoryType() {
        return getEventCategoryType(getValueSet());
    }

    @Override
    public EventCategoryClass getCategoryTypeClass() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getCategoryClass();
    }

    @Override
    public EventCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain EventCategoryType.
     * @param pValueSet the valueSet
     * @return the EventCategoryType
     */
    public static EventCategoryType getEventCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, EventCategoryType.class);
    }

    /**
     * Obtain Parent EventCategory.
     * @param pValueSet the valueSet
     * @return the Parent AccountCategory
     */
    public static EventCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, EventCategory.class);
    }

    /**
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final EventCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set category type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set category type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    @Override
    public EventCategory getBase() {
        return (EventCategory) super.getBase();
    }

    @Override
    public EventCategoryList getList() {
        return (EventCategoryList) super.getList();
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final EventCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    /**
     * Is this event category a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        /* Check for match */
        EventCategoryClass myClass = getCategoryTypeClass();
        return (myClass == null)
                                ? false
                                : myClass.isTransfer();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected EventCategory(final EventCategoryList pList,
                            final EventCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private EventCategory(final EventCategoryList pList,
                          final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        Object myValue = pValues.getValue(FIELD_CATTYPE);
        if (myValue instanceof Integer) {
            setValueType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueType((String) myValue);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public EventCategory(final EventCategoryList pList) {
        super(pList);
    }

    @Override
    public int compareTo(final EventCategory pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the hidden attribute */
        boolean isHidden = isHidden();
        if (isHidden != pThat.isHidden()) {
            return (isHidden)
                             ? 1
                             : -1;
        }

        /* Compare the underlying id */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getEventCategoryTypes());
    }

    @Override
    public void setCategoryType(final EventCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        EventCategoryList myList = getList();
        EventCategoryType myCatType = getCategoryType();
        EventCategory myParent = getParentCategory();
        String myName = getName();

        /* EventCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            EventCategoryClass myClass = myCatType.getCategoryClass();

            /* EventCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* If the CategoryType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                int myCount = myList.countInstances(myClass);
                if (myCount > 1) {
                    addError(ERROR_MULT, FIELD_CATTYPE);
                }
            }

            /* Switch on the category class */
            switch (myClass) {
                case TOTALS:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, FIELD_PARENT);
                    }
                    break;
                case INCOMETOTALS:
                case EXPENSETOTALS:
                case STOCKPARENT:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(EventCategoryClass.TOTALS)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent requirement */
                    boolean isTransfer = myClass == EventCategoryClass.TRANSFER;
                    boolean hasParent = myParent != null;
                    if (hasParent == isTransfer) {
                        if (isTransfer) {
                            addError(ERROR_EXIST, FIELD_PARENT);
                        } else {
                            addError(ERROR_MISSING, FIELD_PARENT);
                        }
                    } else if (hasParent) {
                        /* Check validity of parent */
                        EventCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (!myParentClass.canParentCategory()) {
                            addError(ERROR_BADPARENT, FIELD_PARENT);
                        }
                        if ((myParentClass.isIncome() != myClass.isIncome()) || (myParentClass.isStockTransfer() != myClass.isStockTransfer())) {
                            addError(ERROR_DIFFPARENT, FIELD_PARENT);
                        }

                        /* Check that name reflects parent */
                        if ((myName != null) && !myName.startsWith(myParent.getName() + STR_SEP)) {
                            addError(ERROR_MATCHPARENT, FIELD_PARENT);
                        }
                    }
                    break;
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base category from an edited category.
     * @param pCategory the edited category
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pCategory) {
        /* Can only update from an event category */
        if (!(pCategory instanceof EventCategory)) {
            return false;
        }
        EventCategory myCategory = (EventCategory) pCategory;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myCategory);

        /* Update the category type if required */
        if (!Difference.isEqual(getCategoryType(), myCategory.getCategoryType())) {
            setValueType(myCategory.getCategoryType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Is the category hidden?
     * @return true/false
     */
    public boolean isHidden() {
        EventCategoryClass myClass = this.getCategoryTypeClass();
        return (myClass == null)
                                ? false
                                : myClass.isHiddenType();
    }

    /**
     * The Event Category List class.
     */
    public static class EventCategoryList
            extends CategoryBaseList<EventCategory, EventCategoryType, EventCategoryClass> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, CategoryBase.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return EventCategory.FIELD_DEFS;
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        public EventCategoryList(final MoneyWiseData pData) {
            super(pData, EventCategory.class, MoneyWiseDataType.EVENTCATEGORY);
        }

        @Override
        protected EventCategoryList getEmptyList(final ListStyle pStyle) {
            EventCategoryList myList = new EventCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventCategoryList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventCategoryList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventCategoryList(final EventCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public EventCategoryList deriveEditList() {
            /* Build an empty List */
            EventCategoryList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the categories */
            Iterator<EventCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked event category and add it to the list */
                EventCategory myCategory = new EventCategory(myList, myCurr);
                myList.append(myCategory);
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pCategory item
         * @return the newly added item
         */
        @Override
        public EventCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone an EventCategory */
            if (!(pCategory instanceof EventCategory)) {
                throw new UnsupportedOperationException();
            }

            EventCategory myCategory = new EventCategory(this, (EventCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public EventCategory addNewItem() {
            EventCategory myCategory = new EventCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Count the instances of a class.
         * @param pClass the event category class
         * @return The # of instances of the class
         */
        protected int countInstances(final EventCategoryClass pClass) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
                if (pClass == myCurr.getCategoryTypeClass()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the first event category for the specified class.
         * @param pClass the event category class
         * @return the category
         */
        public EventCategory getSingularClass(final EventCategoryClass pClass) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
                if (myCurr.getCategoryTypeClass() == pClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Obtain singular category for EventInfoClass.
         * @param pInfoClass the Event info class
         * @return the corresponding category.
         */
        public EventCategory getEventInfoCategory(final EventInfoClass pInfoClass) {
            /* Switch on info class */
            switch (pInfoClass) {
                case TAXCREDIT:
                    return getSingularClass(EventCategoryClass.TAXCREDIT);
                case NATINSURANCE:
                    return getSingularClass(EventCategoryClass.NATINSURANCE);
                case DEEMEDBENEFIT:
                    return getSingularClass(EventCategoryClass.DEEMEDBENEFIT);
                case CHARITYDONATION:
                    return getSingularClass(EventCategoryClass.CHARITYDONATION);
                default:
                    return null;
            }
        }

        @Override
        public EventCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the category */
            EventCategory myCategory = new EventCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getId())) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCategory);

            /* Return it */
            return myCategory;
        }
    }
}
