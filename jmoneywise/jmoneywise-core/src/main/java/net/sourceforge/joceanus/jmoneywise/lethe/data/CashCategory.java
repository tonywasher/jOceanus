/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final MetisField FIELD_CATTYPE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.CASHTYPE.getItemName(), MetisDataType.LINK);

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
     * @throws OceanusException on error
     */
    private CashCategory(final CashCategoryList pList,
                         final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(FIELD_CATTYPE);
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

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
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
        final CashCategoryType myType = getCategoryType();
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
    public static CashCategoryType getCashCategoryType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, CashCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static CashCategory getParentCategory(final MetisValueSet pValueSet) {
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
    public CashCategoryList getList() {
        return (CashCategoryList) super.getList();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final CashCategory pParent) throws OceanusException {
        /* Set values */
        final CashCategoryTypeList myTypes = getDataSet().getCashCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                                                                ? CashCategoryClass.PARENT
                                                                : CashCategoryClass.CASH));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getCashCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws OceanusException {
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
        final CashCategoryType myCatType = getCategoryType();
        final CashCategory myParent = getParentCategory();

        /* CashCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            final CashCategoryClass myClass = myCatType.getCashClass();

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
                        final String myName = getName();

                        /* Check validity of parent */
                        final CashCategoryClass myParentClass = myParent.getCategoryTypeClass();
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
        final CashCategory myCategory = (CashCategory) pCategory;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myCategory);

        /* Update the category type if required */
        if (!MetisDataDifference.isEqual(getCategoryType(), myCategory.getCategoryType())) {
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
         * Report fields.
         */
        private static final MetisFieldSet<CashCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(CashCategoryList.class);

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected CashCategoryList(final MoneyWiseData pData) {
            super(pData, CashCategory.class, MoneyWiseDataType.CASHCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected CashCategoryList(final CashCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<CashCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return CashCategory.FIELD_DEFS;
        }

        @Override
        protected CashCategoryList getEmptyList(final ListStyle pStyle) {
            final CashCategoryList myList = new CashCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public CashCategoryList deriveEditList() {
            /* Build an empty List */
            final CashCategoryList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the categories */
            final Iterator<CashCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final CashCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash category and add it to the list */
                final CashCategory myCategory = new CashCategory(myList, myCurr);
                myList.add(myCategory);

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

            final CashCategory myCategory = new CashCategory(this, (CashCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public CashCategory addNewItem() {
            final CashCategory myCategory = new CashCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public CashCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the category */
            final CashCategory myCategory = new CashCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getId())) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCategory);

            /* Return it */
            return myCategory;
        }
    }
}
