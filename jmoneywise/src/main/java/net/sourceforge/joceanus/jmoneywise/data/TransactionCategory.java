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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * Transaction Category class.
 */
public final class TransactionCategory
        extends CategoryBase<TransactionCategory, TransactionCategoryType, TransactionCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.TRANSTYPE.getItemName());

    /**
     * Different Parent Error.
     */
    private static final String ERROR_DIFFPARENT = ResourceMgr.getString(MoneyWiseDataResource.TRANSCATEGORY_ERROR_DIFFPARENT);

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
    public TransactionCategoryType getCategoryType() {
        return getTransCategoryType(getValueSet());
    }

    @Override
    public TransactionCategoryClass getCategoryTypeClass() {
        TransactionCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getCategoryClass();
    }

    @Override
    public TransactionCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain CategoryType.
     * @param pValueSet the valueSet
     * @return the CategoryType
     */
    public static TransactionCategoryType getTransCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, TransactionCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static TransactionCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, TransactionCategory.class);
    }

    /**
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final TransactionCategoryType pValue) {
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
    public TransactionCategory getBase() {
        return (TransactionCategory) super.getBase();
    }

    @Override
    public TransactionCategoryList getList() {
        return (TransactionCategoryList) super.getList();
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final TransactionCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    /**
     * Is this event category a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        /* Check for match */
        TransactionCategoryClass myClass = getCategoryTypeClass();
        return (myClass == null)
                                ? false
                                : myClass.isTransfer();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected TransactionCategory(final TransactionCategoryList pList,
                                  final TransactionCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private TransactionCategory(final TransactionCategoryList pList,
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
    public TransactionCategory(final TransactionCategoryList pList) {
        super(pList);
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    public void setDefaults(final TransactionCategory pParent) throws JOceanusException {
        /* Set values */
        TransactionCategoryTypeList myTypes = getDataSet().getTransCategoryTypes();
        TransactionCategoryClass myParentClass = pParent.getCategoryTypeClass();
        TransactionCategoryClass myNewClass;
        if (myParentClass.isTotals()) {
            myNewClass = TransactionCategoryClass.EXPENSETOTALS;
        } else if (myParentClass.isIncome()) {
            myNewClass = TransactionCategoryClass.OTHERINCOME;
        } else if (myParentClass.isTransfer()) {
            myNewClass = TransactionCategoryClass.STOCKSPLIT;
        } else {
            myNewClass = TransactionCategoryClass.EXPENSE;
        }
        setCategoryType(myTypes.findItemByClass(myNewClass));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public int compareTo(final TransactionCategory pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
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
        resolveDataLink(FIELD_CATTYPE, myData.getTransCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws JOceanusException {
        /* Resolve parent within list */
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void setCategoryType(final TransactionCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        TransactionCategoryList myList = getList();
        TransactionCategoryType myCatType = getCategoryType();
        TransactionCategory myParent = getParentCategory();
        String myName = getName();

        /* EventCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            TransactionCategoryClass myClass = myCatType.getCategoryClass();

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
                    } else if (!myParent.isCategoryClass(TransactionCategoryClass.TOTALS)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent requirement */
                    boolean isTransfer = myClass == TransactionCategoryClass.TRANSFER;
                    boolean hasParent = myParent != null;
                    if (hasParent == isTransfer) {
                        if (isTransfer) {
                            addError(ERROR_EXIST, FIELD_PARENT);
                        } else {
                            addError(ERROR_MISSING, FIELD_PARENT);
                        }
                    } else if (hasParent) {
                        /* Check validity of parent */
                        TransactionCategoryClass myParentClass = myParent.getCategoryTypeClass();
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
        /* Can only update from a transaction category */
        if (!(pCategory instanceof TransactionCategory)) {
            return false;
        }
        TransactionCategory myCategory = (TransactionCategory) pCategory;

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
        TransactionCategoryClass myClass = this.getCategoryTypeClass();
        return (myClass == null)
                                ? false
                                : myClass.isHiddenType();
    }

    /**
     * The Transaction Category List class.
     */
    public static class TransactionCategoryList
            extends CategoryBaseList<TransactionCategory, TransactionCategoryType, TransactionCategoryClass> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, CategoryBaseList.FIELD_DEFS);

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
            return TransactionCategory.FIELD_DEFS;
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        public TransactionCategoryList(final MoneyWiseData pData) {
            super(pData, TransactionCategory.class, MoneyWiseDataType.TRANSCATEGORY);
        }

        @Override
        protected TransactionCategoryList getEmptyList(final ListStyle pStyle) {
            TransactionCategoryList myList = new TransactionCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TransactionCategoryList(final TransactionCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public TransactionCategoryList deriveEditList() {
            /* Build an empty List */
            TransactionCategoryList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the categories */
            Iterator<TransactionCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                TransactionCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                TransactionCategory myCategory = new TransactionCategory(myList, myCurr);
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
        public TransactionCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone a TransactionCategory */
            if (!(pCategory instanceof TransactionCategory)) {
                throw new UnsupportedOperationException();
            }

            TransactionCategory myCategory = new TransactionCategory(this, (TransactionCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public TransactionCategory addNewItem() {
            TransactionCategory myCategory = new TransactionCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Count the instances of a class.
         * @param pClass the event category class
         * @return The # of instances of the class
         */
        protected int countInstances(final TransactionCategoryClass pClass) {
            /* Access the iterator */
            Iterator<TransactionCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                TransactionCategory myCurr = myIterator.next();
                if (pClass == myCurr.getCategoryTypeClass()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the first category for the specified class.
         * @param pClass the category class
         * @return the category
         */
        public TransactionCategory getSingularClass(final TransactionCategoryClass pClass) {
            /* Access the iterator */
            Iterator<TransactionCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                TransactionCategory myCurr = myIterator.next();
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
        public TransactionCategory getEventInfoCategory(final TransactionInfoClass pInfoClass) {
            /* Switch on info class */
            switch (pInfoClass) {
                case TAXCREDIT:
                    return getSingularClass(TransactionCategoryClass.TAXCREDIT);
                case NATINSURANCE:
                    return getSingularClass(TransactionCategoryClass.NATINSURANCE);
                case DEEMEDBENEFIT:
                    return getSingularClass(TransactionCategoryClass.DEEMEDBENEFIT);
                case CHARITYDONATION:
                    return getSingularClass(TransactionCategoryClass.CHARITYDONATION);
                default:
                    return null;
            }
        }

        @Override
        public TransactionCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the category */
            TransactionCategory myCategory = new TransactionCategory(this, pValues);

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

        /**
         * Obtain default expense for autoExpense cash.
         * @return the default expense
         */
        public TransactionCategory getDefaultAutoExpense() {
            /* loop through the categories */
            Iterator<TransactionCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                TransactionCategory myCategory = myIterator.next();

                /* Ignore deleted categories */
                if (myCategory.isDeleted()) {
                    continue;
                }

                /* Ignore categories that are the wrong class */
                TransactionCategoryClass myCatClass = myCategory.getCategoryTypeClass();
                if (myCatClass.isExpense() && !myCatClass.canParentCategory()) {
                    return myCategory;
                }
            }

            /* Return no category */
            return null;
        }
    }
}
