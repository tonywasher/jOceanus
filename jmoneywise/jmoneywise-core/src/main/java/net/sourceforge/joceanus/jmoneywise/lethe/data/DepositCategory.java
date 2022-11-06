/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Deposit Category class.
 */
public class DepositCategory
        extends CategoryBase<DepositCategory, DepositCategoryType, DepositCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSITCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSITCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final MetisLetheField FIELD_CATTYPE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.DEPOSITTYPE.getItemName(), MetisDataType.LINK);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected DepositCategory(final DepositCategoryList pList,
                              final DepositCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private DepositCategory(final DepositCategoryList pList,
                            final DataValues pValues) throws OceanusException {
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
    public DepositCategory(final DepositCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_CATTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public DepositCategoryType getCategoryType() {
        return getDepositCategoryType(getValueSet());
    }

    @Override
    public DepositCategoryClass getCategoryTypeClass() {
        final DepositCategoryType myType = getCategoryType();
        return (myType == null)
                                ? null
                                : myType.getDepositClass();
    }

    @Override
    public DepositCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain DepositCategoryType.
     * @param pValueSet the valueSet
     * @return the DepositCategoryType
     */
    public static DepositCategoryType getDepositCategoryType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, DepositCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static DepositCategory getParentCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, DepositCategory.class);
    }

    /**
     * Set deposit type value.
     * @param pValue the value
     */
    private void setValueType(final DepositCategoryType pValue) {
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
     * Is this deposit category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final DepositCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public DepositCategoryList getList() {
        return (DepositCategoryList) super.getList();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final DepositCategory pParent) throws OceanusException {
        /* Set values */
        final DepositCategoryTypeList myTypes = getDataSet().getDepositCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                                                                ? DepositCategoryClass.PARENT
                                                                : DepositCategoryClass.SAVINGS));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getDepositCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void setCategoryType(final DepositCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        final DepositCategoryType myCatType = getCategoryType();
        final DepositCategory myParent = getParentCategory();

        /* DepositCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            final DepositCategoryClass myClass = myCatType.getDepositClass();

            /* DepositCategoryType must be enabled */
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
                    } else if (!myParent.isCategoryClass(DepositCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    } else {
                        final String myName = getName();

                        /* Check validity of parent */
                        final DepositCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != DepositCategoryClass.PARENT) {
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
    public boolean applyChanges(final DataItem pCategory) {
        /* Can only update from a deposit category */
        if (!(pCategory instanceof DepositCategory)) {
            return false;
        }
        final DepositCategory myCategory = (DepositCategory) pCategory;

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
     * The Deposit Category List class.
     */
    public static class DepositCategoryList
            extends CategoryBaseList<DepositCategory, DepositCategoryType, DepositCategoryClass> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositCategoryList.class);

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected DepositCategoryList(final MoneyWiseData pData) {
            super(pData, DepositCategory.class, MoneyWiseDataType.DEPOSITCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DepositCategoryList(final DepositCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<DepositCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return DepositCategory.FIELD_DEFS;
        }

        @Override
        protected DepositCategoryList getEmptyList(final ListStyle pStyle) {
            final DepositCategoryList myList = new DepositCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public DepositCategoryList deriveEditList() {
            /* Build an empty List */
            final DepositCategoryList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the categories */
            final Iterator<DepositCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final DepositCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                final DepositCategory myCategory = new DepositCategory(myList, myCurr);
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
        public DepositCategory addCopyItem(final DataItem pCategory) {
            /* Can only clone a DepositCategory */
            if (!(pCategory instanceof DepositCategory)) {
                throw new UnsupportedOperationException();
            }

            final DepositCategory myCategory = new DepositCategory(this, (DepositCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public DepositCategory addNewItem() {
            final DepositCategory myCategory = new DepositCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public DepositCategory addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the category */
            final DepositCategory myCategory = new DepositCategory(this, pValues);

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
