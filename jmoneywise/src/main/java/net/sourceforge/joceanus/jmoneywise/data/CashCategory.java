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
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Cash Category class.
 */
public class CashCategory
        extends CategoryBase<CashCategory, CashCategoryType, CashCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CASHCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CASHCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CASHTYPE.getItemName());

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
    public CashCategoryType getCategoryType() {
        return getCashCategoryType(getValueSet());
    }

    @Override
    public CashCategoryClass getCategoryTypeClass() {
        CashCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getCashClass();
    }

    @Override
    public CashCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain CashCategoryType.
     * @param pValueSet the valueSet
     * @return the CashCategoryType
     */
    public static CashCategoryType getCashCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, CashCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static CashCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, CashCategory.class);
    }

    /**
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final CashCategoryType pValue) {
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

    /**
     * Is this cash category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final CashCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public CashCategory getBase() {
        return (CashCategory) super.getBase();
    }

    @Override
    public CashCategoryList getList() {
        return (CashCategoryList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected CashCategory(final CashCategoryList pList,
                           final CashCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private CashCategory(final CashCategoryList pList,
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
    public CashCategory(final CashCategoryList pList) {
        super(pList);
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    public void setDefaults(final CashCategory pParent) throws JOceanusException {
        /* Set values */
        CashCategoryTypeList myTypes = getDataSet().getCashCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                                                               ? CashCategoryClass.PARENT
                                                               : CashCategoryClass.CASH));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getCashCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws JOceanusException {
        /* Resolve parent within list */
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void setCategoryType(final CashCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        CashCategoryType myCatType = getCategoryType();
        CashCategory myParent = getParentCategory();

        /* CashCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            CashCategoryClass myClass = myCatType.getCashClass();

            /* CashCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* Switch on the account class */
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
                    } else if (!myParent.isCategoryClass(CashCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    } else {
                        String myName = getName();

                        /* Check validity of parent */
                        CashCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != CashCategoryClass.PARENT) {
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
        /* Can only update from a cash category */
        if (!(pCategory instanceof CashCategory)) {
            return false;
        }
        CashCategory myCategory = (CashCategory) pCategory;

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
     * The Cash Category List class.
     */
    public static class CashCategoryList
            extends CategoryBaseList<CashCategory, CashCategoryType, CashCategoryClass> {
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
            return CashCategory.FIELD_DEFS;
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected CashCategoryList(final MoneyWiseData pData) {
            super(pData, CashCategory.class, MoneyWiseDataType.CASHCATEGORY);
        }

        @Override
        protected CashCategoryList getEmptyList(final ListStyle pStyle) {
            CashCategoryList myList = new CashCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected CashCategoryList(final CashCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public CashCategoryList deriveEditList() {
            /* Build an empty List */
            CashCategoryList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the categories */
            Iterator<CashCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                CashCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash category and add it to the list */
                CashCategory myCategory = new CashCategory(myList, myCurr);
                myList.append(myCategory);

                /* Adjust the map */
                myCategory.adjustMapForItem();
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
        public CashCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone a CashCategory */
            if (!(pCategory instanceof CashCategory)) {
                throw new UnsupportedOperationException();
            }

            CashCategory myCategory = new CashCategory(this, (CashCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public CashCategory addNewItem() {
            CashCategory myCategory = new CashCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public CashCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the category */
            CashCategory myCategory = new CashCategory(this, pValues);

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
         * Obtain default category for new cash account.
         * @return the default category
         */
        public CashCategory getDefaultCategory() {
            /* loop through the categories */
            Iterator<CashCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                CashCategory myCategory = myIterator.next();

                /* Ignore deleted categories */
                if (myCategory.isDeleted()) {
                    continue;
                }

                /* If the category is not a parent */
                if (!myCategory.isCategoryClass(CashCategoryClass.PARENT)) {
                    return myCategory;
                }
            }

            /* Return no category */
            return null;
        }
    }
}
