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
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Loan Category class.
 */
public class LoanCategory
        extends CategoryBase<LoanCategory, LoanCategoryType, LoanCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.LOANCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.LOANCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.LOANTYPE.getItemName());

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
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
    public LoanCategoryType getCategoryType() {
        return getLoanCategoryType(getValueSet());
    }

    @Override
    public LoanCategoryClass getCategoryTypeClass() {
        LoanCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getLoanClass();
    }

    @Override
    public LoanCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain LoanCategoryType.
     * @param pValueSet the valueSet
     * @return the LoanCategoryType
     */
    public static LoanCategoryType getLoanCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, LoanCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static LoanCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, LoanCategory.class);
    }

    /**
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final LoanCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set account type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Is this loan category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final LoanCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public LoanCategory getBase() {
        return (LoanCategory) super.getBase();
    }

    @Override
    public LoanCategoryList getList() {
        return (LoanCategoryList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected LoanCategory(final LoanCategoryList pList,
                           final LoanCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private LoanCategory(final LoanCategoryList pList,
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
    public LoanCategory(final LoanCategoryList pList) {
        super(pList);
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    public void setDefaults(final LoanCategory pParent) throws JOceanusException {
        /* Set values */
        LoanCategoryTypeList myTypes = getDataSet().getLoanCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                                                               ? LoanCategoryClass.PARENT
                                                               : LoanCategoryClass.LOAN));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getLoanCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws JOceanusException {
        /* Resolve parent within list */
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void setCategoryType(final LoanCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        LoanCategoryType myCatType = getCategoryType();
        LoanCategory myParent = getParentCategory();

        /* LoanCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            LoanCategoryClass myClass = myCatType.getLoanClass();

            /* AccountCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* Switch on the loan class */
            switch (myClass) {
                case PARENT:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(LoanCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    } else {
                        String myName = getName();

                        /* Check validity of parent */
                        LoanCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != LoanCategoryClass.PARENT) {
                            addError(ERROR_BADPARENT, FIELD_PARENT);
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
        /* Can only update from a loan category */
        if (!(pCategory instanceof LoanCategory)) {
            return false;
        }
        LoanCategory myCategory = (LoanCategory) pCategory;

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
     * The Loan Category List class.
     */
    public static class LoanCategoryList
            extends CategoryBaseList<LoanCategory, LoanCategoryType, LoanCategoryClass> {
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
            return LoanCategory.FIELD_DEFS;
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected LoanCategoryList(final MoneyWiseData pData) {
            super(pData, LoanCategory.class, MoneyWiseDataType.LOANCATEGORY);
        }

        @Override
        protected LoanCategoryList getEmptyList(final ListStyle pStyle) {
            LoanCategoryList myList = new LoanCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected LoanCategoryList(final LoanCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public LoanCategoryList deriveEditList() {
            /* Build an empty List */
            LoanCategoryList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the categories */
            Iterator<LoanCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                LoanCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                LoanCategory myCategory = new LoanCategory(myList, myCurr);
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
        public LoanCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone a LoanCategory */
            if (!(pCategory instanceof LoanCategory)) {
                throw new UnsupportedOperationException();
            }

            LoanCategory myCategory = new LoanCategory(this, (LoanCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public LoanCategory addNewItem() {
            LoanCategory myCategory = new LoanCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public LoanCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the category */
            LoanCategory myCategory = new LoanCategory(this, pValues);

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
         * Obtain default category for new loan account.
         * @return the default category
         */
        public LoanCategory getDefaultCategory() {
            /* loop through the categories */
            Iterator<LoanCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                LoanCategory myCategory = myIterator.next();

                /* Ignore deleted categories */
                if (myCategory.isDeleted()) {
                    continue;
                }

                /* If the category is not a parent */
                if (!myCategory.isCategoryClass(LoanCategoryClass.PARENT)) {
                    return myCategory;
                }
            }

            /* Return no category */
            return null;
        }
    }
}
