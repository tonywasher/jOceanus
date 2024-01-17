/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseDepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseDepositCategoryType.MoneyWiseDepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Deposit Category class.
 */
public final class MoneyWiseDepositCategory
        extends MoneyWiseCategoryBase
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.DEPOSITCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.DEPOSITCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseDepositCategory> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseDepositCategory.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseStaticDataType.DEPOSITTYPE);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    MoneyWiseDepositCategory(final MoneyWiseDepositCategoryList pList,
                             final MoneyWiseDepositCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseDepositCategory(final MoneyWiseDepositCategoryList pList,
                                     final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(MoneyWiseStaticDataType.DEPOSITTYPE);
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
    public MoneyWiseDepositCategory(final MoneyWiseDepositCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticDataType.DEPOSITTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public MoneyWiseDepositCategoryType getCategoryType() {
        return getValues().getValue(MoneyWiseStaticDataType.DEPOSITTYPE, MoneyWiseDepositCategoryType.class);
    }

    @Override
    public MoneyWiseDepositCategoryClass getCategoryTypeClass() {
        final MoneyWiseDepositCategoryType myType = getCategoryType();
        return myType == null
                ? null
                : myType.getDepositClass();
    }

    @Override
    public MoneyWiseDepositCategory getParentCategory() {
        return getValues().getValue(PrometheusDataResource.DATAGROUP_PARENT, MoneyWiseDepositCategory.class);
    }

    /**
     * Set deposit type value.
     * @param pValue the value
     */
    private void setValueType(final MoneyWiseDepositCategoryType pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.DEPOSITTYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.DEPOSITTYPE, pValue);
    }

    /**
     * Set account type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.DEPOSITTYPE, pValue);
    }

    /**
     * Is this deposit category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseDepositCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public MoneyWiseDepositCategoryList getList() {
        return (MoneyWiseDepositCategoryList) super.getList();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final MoneyWiseDepositCategory pParent) throws OceanusException {
        /* Set values */
        final MoneyWiseDepositCategoryTypeList myTypes = getDataSet().getDepositCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseDepositCategoryClass.PARENT
                : MoneyWiseDepositCategoryClass.SAVINGS));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseStaticDataType.DEPOSITTYPE, myData.getDepositCategoryTypes());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());

        /* Resolve StaticType if required */
        final PrometheusEditSet myEditSet = getList().getEditSet();
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.DEPOSITTYPE)) {
            resolveDataLink(MoneyWiseStaticDataType.DEPOSITTYPE, myEditSet.getDataList(MoneyWiseStaticDataType.DEPOSITTYPE, MoneyWiseDepositCategoryTypeList.class));
        }
    }

    @Override
    public void setCategoryType(final PrometheusStaticDataItem pType) {
        setValueType((MoneyWiseDepositCategoryType) pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        final MoneyWiseDepositCategoryType myCatType = getCategoryType();
        final MoneyWiseDepositCategory myParent = getParentCategory();

        /* DepositCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, MoneyWiseStaticDataType.DEPOSITTYPE);
        } else {
            /* Access the class */
            final MoneyWiseDepositCategoryClass myClass = myCatType.getDepositClass();

            /* DepositCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, MoneyWiseStaticDataType.DEPOSITTYPE);
            }

            /* Switch on the account class */
            switch (myClass) {
                case PARENT:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    break;
                default:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                    } else if (!myParent.isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    } else {
                        final String myName = getName();

                        /* Check validity of parent */
                        final MoneyWiseDepositCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != MoneyWiseDepositCategoryClass.PARENT) {
                            addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                        }
                        /* Check that name reflects parent */
                        if ((myName != null) && !myName.startsWith(myParent.getName() + STR_SEP)) {
                            addError(ERROR_MATCHPARENT, PrometheusDataResource.DATAGROUP_PARENT);
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
    public boolean applyChanges(final PrometheusDataItem pCategory) {
        /* Can only update from a deposit category */
        if (!(pCategory instanceof MoneyWiseDepositCategory)) {
            return false;
        }
        final MoneyWiseDepositCategory myCategory = (MoneyWiseDepositCategory) pCategory;

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
    public static class MoneyWiseDepositCategoryList
            extends MoneyWiseCategoryBaseList<MoneyWiseDepositCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositCategoryList.class);

        /**
         * The EditSet.
         */
        private PrometheusEditSet theEditSet;

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseDepositCategoryList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseDepositCategory.class, MoneyWiseBasicDataType.DEPOSITCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseDepositCategoryList(final MoneyWiseDepositCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseDepositCategory.FIELD_DEFS;
        }

        /**
         * Obtain editSet.
         * @return the editSet
         */
        public PrometheusEditSet getEditSet() {
            return theEditSet;
        }

        @Override
        protected MoneyWiseDepositCategoryList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseDepositCategoryList myList = new MoneyWiseDepositCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseDepositCategoryList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseDepositCategoryList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.DEPOSITCATEGORY, myList);

            /* Store the editSet */
            myList.theEditSet = pEditSet;

            /* Loop through the categories */
            final Iterator<MoneyWiseDepositCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseDepositCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                final MoneyWiseDepositCategory myCategory = new MoneyWiseDepositCategory(myList, myCurr);
                myList.add(myCategory);
                myCategory.resolveEditSetLinks();

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
        public MoneyWiseDepositCategory addCopyItem(final PrometheusDataItem pCategory) {
            /* Can only clone a DepositCategory */
            if (!(pCategory instanceof MoneyWiseDepositCategory)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseDepositCategory myCategory = new MoneyWiseDepositCategory(this, (MoneyWiseDepositCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseDepositCategory addNewItem() {
            final MoneyWiseDepositCategory myCategory = new MoneyWiseDepositCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public MoneyWiseDepositCategory addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the category */
            final MoneyWiseDepositCategory myCategory = new MoneyWiseDepositCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getIndexedId())) {
                myCategory.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCategory);

            /* Return it */
            return myCategory;
        }
    }
}
